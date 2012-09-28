/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tagging.java;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.java.extractor.WaypointDefinitionExtractor;
import com.ibm.research.tagging.java.markers.WaypointMarkerParser;
import com.ibm.research.tagging.java.markers.WaypointMarkerValidator;
import com.ibm.research.tagging.java.parser.IJavaWaypointInfo;
import com.ibm.research.tagging.java.parser.IJavaWaypointParser;
import com.ibm.research.tagging.java.parser.JavaWaypointParserFactory;
import com.ibm.research.tagging.java.parser.JavaWaypointParserUtilities;
import com.ibm.research.tagging.java.refactoring.DocumentReadSession;

public class ModifiedFileSynchronizer 
{

	public void lockAndSynchronizeWithModel(IFile file)
	{
		//Lock the document for the duration
		DocumentReadSession session = new DocumentReadSession(file);
		session.begin();

		try
		{
			synchronizeWithModel(file, session.getDocument());
		}
		finally
		{
			session.end();
		}
	}

	public void synchronizeWithModel(IFile file, IDocument document)
	{
		List<IRegion> newWaypointRegions = new ArrayList<IRegion>();

		// Get markers
		IMarker markerArray[] = getMarkers(file);	

		//Get invalidated markers
		List<IMarker> invalidatedMarkers = getInvalidatedMarkers(file,markerArray);

		// Get the waypoint regions
		IRegion[] waypointRegions = WaypointDefinitionExtractor.getWaypointRegions(document);

		// Match the waypoint regions with the existing waypoint markers
		for(IRegion region : waypointRegions)
		{
			boolean matched = false;

			for(IMarker marker : markerArray)
			{
				int offset = MarkerUtilities.getCharStart(marker);
				int length = MarkerUtilities.getCharEnd(marker) - offset;

				if(region.getOffset() == offset)
				{
					matched = true;
					break;
				}
			}

			if(!matched)
				newWaypointRegions.add(region);
		}

		removeDeletedWaypoints(file);

		List<IMarker> deletedMarkers = new ArrayList<IMarker>();

		// Process the invalidated markers, an invalidated marker was either deleted or modified
		for(IMarker marker : invalidatedMarkers)
		{
			boolean markerMatched = false;

			int offset = MarkerUtilities.getCharStart(marker);

			// look for a matching region for this invalid waypoint
			for(IRegion region : waypointRegions)
			{
				// We ASSUME that this is the matching region
				if(region.getOffset() == offset)
				{
					markerMatched = true;
					updateWaypointWithRegion(region,document,marker);
				}
			}

			// We can assume that this waypoint was deleted :( But the 
			// eclipse marker stuff is too dumb to realise
			if(!markerMatched)
			{
				deletedMarkers.add(marker);
			}
		}

		// Remove the deleted markers and waypoints
		for(IMarker marker : deletedMarkers)
		{
			// Get the waypoint
			JavaWaypoint waypoint = (JavaWaypoint)TagCorePlugin.getDefault().getTagCore().getWaypointModel().getWaypoint(Long.toString(marker.getId()));
			JavaWaypointPlugin.getDefault().removeWaypoint(waypoint);

			try 
			{
				marker.delete();
			}
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}


		// Create the new waypoints 
		for(IRegion region : newWaypointRegions)
		{
			IMarker marker = WaypointMarkerFactory.createMarker(file, document, region);

			if(marker != null)
			{
				WaypointMarkerParser parser = new WaypointMarkerParser();
				IJavaWaypointInfo info;

				try 
				{
					info = parser.parse(marker);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					continue;
				}

				String author = JavaWaypointParserUtilities.getAuthor(info);
				Date date = JavaWaypointParserUtilities.getDate(info);
				JavaWaypoint waypoint = new JavaWaypoint(marker,info.getDescription(),author,date);

				// Always add java waypoints via the proxy
				JavaWaypointPlugin.getDefault().addWaypoint(waypoint);

				for(String tagName : info.getTags())
				{
					ITag tag = TagCorePlugin.getDefault().getTagCore().getTagModel().addTag(tagName);
					waypoint.addTag(tag);
				}
			}
		}

	}

	private void updateWaypointWithRegion(IRegion region,IDocument document, IMarker marker) 
	{
		String waypointDefinition = null;

		try 
		{
			waypointDefinition = document.get(region.getOffset(), region.getLength());
		} 
		catch (BadLocationException e) 
		{
			e.printStackTrace();
		}

		try 
		{
			// Update the marker fields
			marker.setAttribute(IMarker.MESSAGE, waypointDefinition);
			marker.setAttribute(IMarker.CHAR_START, region.getOffset());
			marker.setAttribute(IMarker.CHAR_END, region.getOffset() + region.getLength());
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}

		// Get the waypoint
		JavaWaypoint waypoint = (JavaWaypoint)TagCorePlugin.getDefault().getTagCore().getWaypointModel().getWaypoint(Long.toString(marker.getId()));

		// parser the waypoint and update
		IJavaWaypointParser parser = JavaWaypointParserFactory.createParser();

		IJavaWaypointInfo info = null;

		try 
		{
			info = parser.parse(waypointDefinition);
		}
		catch (Exception e) 
		{
			// whoops looks like this waypoint was actually deleted!
			JavaWaypointPlugin.getDefault().removeWaypoint(waypoint);
			e.printStackTrace();
			return;
		}

		if(!waypoint.getDescription().equals(info.getDescription()))
			waypoint.setDescription(info.getDescription());

		String author = JavaWaypointParserUtilities.getAuthor(info);

		if(!waypoint.getAuthor().equals(author))
			waypoint.setAuthor(author);

		Date date = JavaWaypointParserUtilities.getDate(info);
		waypoint.setDate(date);

		ITag[] tags = waypoint.getTags();
		String[] tagNames = info.getTags();

		for(String tagName : tagNames)
		{
			boolean matched = false;

			for(ITag tag : tags)
			{
				if(tagName.equals(tag.getName()))
					matched = true;
			}

			if(!matched)
				waypoint.addTag(TagCorePlugin.getDefault().getTagCore().getTagModel().addTag(tagName));
		}

		for(ITag tag : tags)
		{
			boolean matched = false;

			for(String tagName : tagNames)
			{
				if(tagName.equals(tag.getName()))
					matched = true;
			}

			if(!matched)
				waypoint.removeTag(tag);
		}
	}

	private void removeDeletedWaypoints(IFile file) 
	{
		// Remove any deleted waypoints
		List<JavaWaypoint> existingWaypoints = JavaWaypointPlugin.getDefault().get(file);
		List<JavaWaypoint> deletedWaypoints = new ArrayList<JavaWaypoint>();
		
		if(existingWaypoints != null)
		{
			for(JavaWaypoint jw : existingWaypoints)
			{
				if(!jw.exists())
					deletedWaypoints.add(jw);
			}
		}
		
		
		for(JavaWaypoint w : deletedWaypoints)
		{
			JavaWaypointPlugin.getDefault().removeWaypoint(w);
		}
	}

	private IMarker[] getMarkers(IFile file) 
	{
		try 
		{
			return file.findMarkers(JavaWaypoint.MARKER_ID, false, IResource.DEPTH_ZERO);
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}

		return new IMarker[0];
	}

	private List<IMarker> getInvalidatedMarkers(IFile file, IMarker[] markerArray) 
	{
		List<IMarker> invalidatedMarkers = new ArrayList<IMarker>();
		WaypointMarkerValidator validator = new WaypointMarkerValidator(file);

		for(IMarker marker : markerArray)
		{
			if(!validator.isValid(marker))
				invalidatedMarkers.add(marker);
		}

		return invalidatedMarkers;
	}
}
