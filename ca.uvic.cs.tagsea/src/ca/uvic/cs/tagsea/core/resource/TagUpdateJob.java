/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *     IBM Corporation 
 *******************************************************************************/
package ca.uvic.cs.tagsea.core.resource;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.TagCollection;
import ca.uvic.cs.tagsea.extraction.RawTagStripper;
import ca.uvic.cs.tagsea.extraction.TagExtractor;
import ca.uvic.cs.tagsea.monitoring.TagSEAJobEvent;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;

/**
 * @author mdesmond
 */
public class TagUpdateJob extends Job 
{
	private static final long UI_REFRESH_DELAY = 200;
	private static final long UI_REFRESH_PERIOD = 300;
	
	List<IResourceDelta> fResouceDeltas;

	public TagUpdateJob(List<IResourceDelta> resouceDeltas) 
	{
		super("Updating @tags on resource change");
		fResouceDeltas = resouceDeltas;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		Timer t = new Timer();
		t.schedule(new TimerTask() 
		{
			@Override
			public void run() 
			{
				refreshTagUI();
			}				
		}, UI_REFRESH_DELAY, UI_REFRESH_PERIOD);
		Monitoring.getDefault().fireEvent(new TagSEAJobEvent(TagSEAJobEvent.JobType.StartUpdatingTags));
		for (IResourceDelta delta : fResouceDeltas) 
		{
			IResource resource = delta.getResource();

			if (((delta.getKind() == IResourceDelta.ADDED)) || (delta.getKind() == IResourceDelta.CHANGED)) 
			{
				IFile file = (IFile)resource;

				// Remove waypoints				
				TagSEAPlugin.getDefault().getTagCollection().removeWaypoints(file);

				// remove all pre-existing markers
				try 
				{
					IMarker[] markers = file.findMarkers(TagSEAPlugin.MARKER_ID,true,IResource.DEPTH_INFINITE);

					for (IMarker marker : markers) 
						marker.delete();
				} 
				catch (CoreException ce) 
				{
					ce.printStackTrace();
				}

				try 
				{
					IDocument document = ResourceUtil.getDocument(file);

					if (document == null)
						return Status.CANCEL_STATUS;

					TagCollection tagCollection = TagSEAPlugin.getDefault().getTagCollection();
					IRegion[] regions = TagExtractor.getTagRegions(document);

					for (IRegion tagRegion : regions) 
					{
						String rawTag = document.get(tagRegion.getOffset(), tagRegion.getLength());
						String strippedTag = RawTagStripper.stripRawTag(rawTag);

						if(strippedTag != null)
						{
							IMarker marker = file.createMarker(TagSEAPlugin.MARKER_ID);
							int lineNumber = document.getLineOfOffset(tagRegion.getOffset()) + 1;

							marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
							marker.setAttribute(IMarker.CHAR_START, tagRegion.getOffset());
							marker.setAttribute(IMarker.CHAR_END, tagRegion.getOffset() + tagRegion.getLength());
							marker.setAttribute(IMarker.MESSAGE, strippedTag);				

							// find the java element for this marker, need null checks here
							ICompilationUnit cu = (ICompilationUnit) JavaCore.create(marker.getResource());
							IJavaElement javaElement = cu.getElementAt(document.getLineOffset(lineNumber));
							tagCollection.add(strippedTag, marker, javaElement);
						}
					}
				}
				catch (Exception e) 
				{
					TagSEAPlugin.log(e.getMessage(), e);
				}
			} 
			else if ((delta.getKind() == IResourceDelta.REMOVED)) 
			{
				IFile file = (IFile)resource;
				TagSEAPlugin.getDefault().getTagCollection().removeWaypoints(file);
			}
		}

		t.cancel();
		refreshTagUI();
		refreshRouteUI();
		Monitoring.getDefault().fireEvent(new TagSEAJobEvent(TagSEAJobEvent.JobType.EndUpdatingTags));
		return Status.OK_STATUS;
	}

    /**
     * Refresh the tag UI part
     */
    private void refreshRouteUI() 
    {
        Display.getDefault().asyncExec(new Runnable()
        {
			public void run()
			{
	           	TagSEAPlugin.getDefault().getRouteCollection().updateView();
			}
		});
    }
    
    /**
     * Refresh the tag UI part
     */
    private void refreshTagUI() 
    {
        Display.getDefault().asyncExec(new Runnable()
        {
			public void run()
			{
            	TagSEAPlugin.getDefault().getTagCollection().updateView();
			}
		});
    }
}
