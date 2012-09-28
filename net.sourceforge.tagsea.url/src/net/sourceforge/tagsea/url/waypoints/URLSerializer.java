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
package net.sourceforge.tagsea.url.waypoints;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.url.URLWaypointPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * @author mdesmond
 */
public class URLSerializer 
{	
	private static final String URL_FILE = "url.repository.xml";
	private static final String DOC_ROOT = "url-repository";

	private static final String NAME_ATTRIBUTE = "name";
	private static final String TAG = "tag";

	private static final String URL_WAYPOINT = "url-waypoint";
	private static final String URL_ATTRIBUTE = "url";
	private static final String DESCRIPTION_ATTRIBUTE = "description";
	private static final String AUTHOR_ATTRIBUTE = "author";
	private static final String DATE_ATTRIBUTE = "date";

	public static void serialize()
	{
		XMLMemento memento = XMLMemento.createWriteRoot(DOC_ROOT);
		writeMemento(memento);
		writeMementoToDisk(memento);
	}

	private static void writeMemento(IMemento memento)
	{
		IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(URLWaypointPlugin.WAYPOINT_ID);

		for(IWaypoint waypoint : waypoints)
		{
			IMemento waypointMemento = memento.createChild(URL_WAYPOINT);

			waypointMemento.putString(URL_ATTRIBUTE, waypoint.getStringValue(URLWaypointUtil.URL_ATTR,""));
			waypointMemento.putString(AUTHOR_ATTRIBUTE, waypoint.getAuthor());

			if(waypoint.getDate()!=null )
			{
				SimpleDateFormat format = new SimpleDateFormat();
				String dateString = format.format(waypoint.getDate());
				waypointMemento.putString(DATE_ATTRIBUTE, dateString);
			}

			waypointMemento.putString(DESCRIPTION_ATTRIBUTE, waypoint.getText());

			ITag[] tags = waypoint.getTags();

			for(ITag tag : tags)
			{
				IMemento tagMemento = waypointMemento.createChild(TAG);
				tagMemento.putString(NAME_ATTRIBUTE, tag.getName());
			}
		} 
	}

	private static void writeMementoToDisk(XMLMemento memento) 
	{
		IPath path = URLWaypointPlugin.getDefault().getStateLocation().append(URL_FILE);
		File mementoFile = path.toFile();
		mementoFile.getParentFile().mkdirs();

		try
		{		
			FileOutputStream stream = new FileOutputStream(mementoFile);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
			memento.save(writer);
			writer.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public static void deSerialize()
	{
		IPath path = URLWaypointPlugin.getDefault().getStateLocation().append(URL_FILE);		
		File mementoFile = path.toFile();

		if(mementoFile.exists())
		{
			try 
			{
				FileInputStream input = new FileInputStream(mementoFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8")); //$NON-NLS-1$
				IMemento memento = XMLMemento.createReadRoot(reader);			
				restoreStateFromMemento(memento);
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			} 
			catch (WorkbenchException e) 
			{
				e.printStackTrace();
			}
		}
	}

	private static void restoreStateFromMemento(IMemento memento) 
	{	
		IMemento[] urlMementos = memento.getChildren(URL_WAYPOINT);

		for (IMemento urlMemento : urlMementos) 
		{
			String urlString = urlMemento.getString(URL_ATTRIBUTE);

			String author = urlMemento.getString(AUTHOR_ATTRIBUTE);
			String dateString = urlMemento.getString(DATE_ATTRIBUTE);
			String description = urlMemento.getString(DESCRIPTION_ATTRIBUTE);

			String[] tagNames = getTagNames(urlMemento);

			Date date = null;

			if(dateString!=null)
			{
				try 
				{
					date = SimpleDateFormat.getInstance().parse(dateString);
				} 
				catch (ParseException e) 
				{
					e.printStackTrace();
				}
			}

			IWaypoint waypoint= TagSEAPlugin.getWaypointsModel().createWaypoint(URLWaypointPlugin.WAYPOINT_ID, new String[0]);

			for(String tagName : tagNames)
			{
				waypoint.addTag(tagName);
			}

			waypoint.setAuthor(author);
			waypoint.setDate(date);
			waypoint.setText(description);
			waypoint.setStringValue(URLWaypointUtil.URL_ATTR, urlString);
		}
	}

	private static String[] getTagNames(IMemento urlMemento) 
	{
		IMemento[] tagMementos = urlMemento.getChildren(TAG);
		List<String> tags = new ArrayList<String>();

		for(IMemento tagMemento : tagMementos)
		{
			String tagName = tagMemento.getString(NAME_ATTRIBUTE);

			if(tagName!=null)
				tags.add(tagName);
		}

		String[] array = new String[0];
		return tags.toArray(array);
	}
}
