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
package com.ibm.research.tagging.core.ui.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.ITagSelectionListener;
import com.ibm.research.tagging.core.ui.ITagUI;
import com.ibm.research.tagging.core.ui.ITagViewListener;
import com.ibm.research.tagging.core.ui.IWaypointUIExtension;
import com.ibm.research.tagging.core.ui.IWaypointViewListener;
import com.ibm.research.tagging.core.ui.TagUIPlugin;

/**
 * 
 * @author mdesmond
 *
 */
public class TagUI implements ITagUI 
{
	private static final int MAX_LABEL_LENGTH = 100;
	
	private List<IWaypointUIExtension> fExtensions;
	private List<ITagSelectionListener> fTagSelectionListeners;
	private List<ITagViewListener> fTagViewListeners;
	private List<IWaypointViewListener> fWaypointViewListeners;
	private FormText fDefaultFormText;
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.ITagUI#addWaypointUIExtension(com.ibm.research.tagging.core.ui.IWaypointUIExtension)
	 */
	public void addWaypointUIExtension(IWaypointUIExtension extension) 
	{
		if(!getExtensions().contains(extension))
			getExtensions().add(extension);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.ITagUI#removeWaypointUIExtension(com.ibm.research.tagging.core.ui.IWaypointUIExtension)
	 */
	public void removeWaypointUIExtension(IWaypointUIExtension extension) 
	{
		getExtensions().remove(extension);
	}
	
	/**
	 * Get the extension list
	 * @return
	 */
	protected List<IWaypointUIExtension> getExtensions() 
	{
		if(fExtensions == null)
			fExtensions = new ArrayList<IWaypointUIExtension>();
		return fExtensions;
	}
	
	/**
	 * Get the extension list
	 * @return
	 */
	protected List<ITagSelectionListener> getTagSelectionListeners() 
	{
		if(fTagSelectionListeners == null)
			fTagSelectionListeners = new ArrayList<ITagSelectionListener>();
		
		return fTagSelectionListeners;
	}
	
	/**
	 * Get the extension list
	 * @return
	 */
	protected List<ITagViewListener> getTagViewListeners() 
	{
		if(fTagViewListeners == null)
			fTagViewListeners = new ArrayList<ITagViewListener>();
		
		return fTagViewListeners;
	}
	
	/**
	 * Get the extension list
	 * @return
	 */
	protected List<IWaypointViewListener> getWaypointViewListeners() 
	{
		if(fWaypointViewListeners == null)
			fWaypointViewListeners = new ArrayList<IWaypointViewListener>();
		
		return fWaypointViewListeners;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagUI#getImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getImage(IWaypoint waypoint) 
	{
		for(IWaypointUIExtension extension : getExtensions())
		{
			Image image = null;
			image = extension.getImage(waypoint);
			
			if(image !=null)
				return image;
		}
		
		return TagCorePlugin.getDefault().getImageRegistry().get(TagUIPlugin.IMG_WAYPOINT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagUI#getSmallImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getSmallImage(IWaypoint waypoint) 
	{
		for(IWaypointUIExtension extension : getExtensions())
		{
			Image image = null;
			image = extension.getSmallImage(waypoint);
			
			if(image !=null)
				return image;
		}
		
		return TagCorePlugin.getDefault().getImageRegistry().get(TagUIPlugin.IMG_WAYPOINT);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagUI#getLabel(com.ibm.research.tagging.core.IWaypoint)
	 */
	public String getLabel(IWaypoint waypoint) 
	{
		for(IWaypointUIExtension extension : getExtensions())
		{
			String label = null;
			label = extension.getLabel(waypoint);
			
			if(label !=null)
			{
				// apply ellipsis for long labels
				if ( label.length()>MAX_LABEL_LENGTH )
				{
					return label.substring(0,MAX_LABEL_LENGTH-3) + "...";
				}
				
				return label;
			}
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagUI#getLabel(com.ibm.research.tagging.core.IWaypoint,int)
	 */
	public String getLabel(IWaypoint waypoint, int column) 
	{
		for(IWaypointUIExtension extension : getExtensions())
		{
			String label = extension.getLabel(waypoint,column);
			if ( label!=null )
				return label;
		}
		
		return null;
	}
	
	@Deprecated
	public void addTagSelectionListener(ITagSelectionListener listener) 
	{
		if(!getTagSelectionListeners().contains(listener))
			getTagSelectionListeners().add(listener);
	}

	@Deprecated
	public void removeTagSelectionListener(ITagSelectionListener listener) 
	{
		getTagSelectionListeners().remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.ITagUI#tagSelectionChanged(com.ibm.research.tagging.core.ITag[])
	 */
	public void tagSelectionChanged(final ITag[] tags) 
	{
		TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() {
		
			public void run() 
			{
				for(ITagSelectionListener listener : getTagSelectionListeners())
					listener.tagsSelected(tags);
			}
		});

	}

	public FormText getProperties(Composite parent, IWaypoint waypoint) 
	{
		for(IWaypointUIExtension extension : getExtensions())
		{
			FormText text = null;
			text = extension.getProperties(parent,waypoint);
			
			if(text !=null)
				return text;
		}
		
		return createDefaultProperties(parent, waypoint);
	}
	
	// Create a default form text
	private FormText createDefaultProperties(Composite parent, IWaypoint waypoint)
	{
		fDefaultFormText = new FormText(parent,SWT.WRAP);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<form>");
		
			buf.append("<li style=\"image\" value=\"waypoint\">");
				
					if(waypoint.getDescription()!=null && waypoint.getDescription().trim().length() > 0)
						buf.append(waypoint.getDescription().trim());
					else
						buf.append("This waypoint needs a description.");
					
			buf.append("</li>");

			buf.append("<li>");
			
			buf.append("Created by <b>");
			
			if(waypoint.getAuthor()!=null && waypoint.getAuthor().trim().length() > 0)
				buf.append(waypoint.getAuthor().trim());
				else
				buf.append("Anonymous");
			
			buf.append("</b>");
			
			if(waypoint.getDate()!=null)
			{
				String dateString = SimpleDateFormat.getInstance().format(waypoint.getDate());
				buf.append(" on ");
				buf.append("<b>" + dateString + "</b>");
			}
			
			buf.append("</li>");
			
		buf.append("</form>");
	    
	    fDefaultFormText.setImage("waypoint", TagUIPlugin.getDefault(). getImageRegistry().get(TagUIPlugin.IMG_WAYPOINT));
	    fDefaultFormText.setText(buf.toString(), true, true);
	    
	    return fDefaultFormText;
	}

	public void addTagViewListener(ITagViewListener listener) 
	{
		if(!getTagViewListeners().contains(listener))
			getTagViewListeners().add(listener);
	}

	public void removeTagViewListener(ITagViewListener listener) 
	{
		getTagViewListeners().remove(listener);
	}
	
	public void addWaypointViewListener(IWaypointViewListener listener) 
	{
		if(!getWaypointViewListeners().contains(listener))
			getWaypointViewListeners().add(listener);
	}
	
	public void removeWaypointViewListener(IWaypointViewListener listener) 
	{
		getWaypointViewListeners().remove(listener);
	}

	public boolean editProperties(IWaypoint waypoint) {
		for(IWaypointUIExtension extension : getExtensions())
		{
			if ( extension.editProperties(waypoint) )
				return true;
		}
		
		return false;
	}
}
