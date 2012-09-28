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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
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

/**
 * @author mdesmond
 */
public class TagDatabaseRefreshJob extends Job 
{
	private static final long UI_REFRESH_DELAY = 200;
	private static final long UI_REFRESH_PERIOD = 300;
	private List<IProject> fProjects;

	public TagDatabaseRefreshJob() 
	{
		this(null);
	}

	public TagDatabaseRefreshJob(List<IProject> projects) 
	{
		super("Synchronizing @tag database with selected workspace resources");
		fProjects = projects;
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

		
		List<IResource> resourcesOfIntrest = new ArrayList<IResource>();
		
		try {
			if(fProjects == null)
			{
				monitor.beginTask("Scanning files...", ResourcesPlugin.getWorkspace().getRoot().getProjects().length);
				JavaFileResourceVisitor visitor = new JavaFileResourceVisitor(monitor);
				ResourcesPlugin.getWorkspace().getRoot().accept(visitor);
				resourcesOfIntrest.addAll(visitor.getResources());
			}
			else
			{
				monitor.beginTask("Scanning files...", fProjects.size());

				for(IProject p : fProjects)
				{
					JavaFileResourceVisitor visitor = new JavaFileResourceVisitor(null);
					p.accept(visitor);
					resourcesOfIntrest.addAll(visitor.getResources());
					monitor.worked(1);
				}
			}
		}
		catch (CoreException ce) 
		{
			ce.printStackTrace();
		}
		
		monitor.beginTask("Scanning for @tags...", resourcesOfIntrest.size());
		
		for (IResource r : resourcesOfIntrest) 
		{
			IFile file = (IFile)r;

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
			
			monitor.worked(1);
		} 

		t.cancel();
		refreshTagUI();
		refreshRouteUI();
		monitor.done();
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
