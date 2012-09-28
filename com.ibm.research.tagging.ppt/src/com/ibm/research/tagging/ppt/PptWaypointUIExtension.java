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

package com.ibm.research.tagging.ppt;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.IWaypointUIExtension;
import com.ibm.research.tagging.resource.ResourceWaypointUIExtension;

// @tag to-fix powerpoint : for now, use java label provider functionality to find powerpoint icon - assumes files are really powerpoint files 

public class PptWaypointUIExtension implements IWaypointUIExtension
{	
	private JavaElementLabelProvider fJavaLabelProvider;
	private JavaElementLabelProvider fSmallJavaLabelProvider;
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getImage(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(PptWaypoint.TYPE))
		{
			PptWaypoint wp = (PptWaypoint) waypoint;
			return getJavaLabelProvider().getImage(wp.getMarker().getResource());			
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getLabel(com.ibm.research.tagging.core.IWaypoint)
	 */
	public String getLabel(IWaypoint waypoint)
	{
		if(waypoint.getType().equals(PptWaypoint.TYPE))
		{
			PptWaypoint wp = (PptWaypoint)waypoint;
			String label = wp.getMarker().getResource().getName() + " "; 
			
			String desc =  wp.getDescription();
			if ( desc!=null )
				label += "- " + desc;
			
			int start = wp.getStartSlide(),
			    end   = wp.getEndSlide();
			
			if ( start>=1 && start==end )
			   label += " (slide #" + start + ")";
			else if ( start>=1 && start!=end )
			   label += " (slide #" + start + "-" + end +")";
			
			return label;
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getSmallImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getSmallImage(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(PptWaypoint.TYPE))
		{
			PptWaypoint wp = (PptWaypoint) waypoint;
			return getSmallJavaLabelProvider().getImage(wp.getMarker().getResource());
		}
		
		return null;
	}
	
	
	/**
	 * Get the java label provider
	 * @return
	 */
	private ILabelProvider getJavaLabelProvider() 
	{
		if (fJavaLabelProvider == null) 
		{
			fJavaLabelProvider = new JavaElementLabelProvider();
		}
		return fJavaLabelProvider;
	}
	
	/**
	 * Get the small java label provider
	 * @return
	 */
	private ILabelProvider getSmallJavaLabelProvider() 
	{
		if (fSmallJavaLabelProvider == null) 
		{
			int flags = JavaElementLabelProvider.SHOW_DEFAULT | JavaElementLabelProvider.SHOW_SMALL_ICONS;
			fSmallJavaLabelProvider = new JavaElementLabelProvider(flags);
		}
		
		return fSmallJavaLabelProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#editProperties(com.ibm.research.tagging.core.IWaypoint)
	 */
	public boolean editProperties(IWaypoint waypoint) 
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getProperties(org.eclipse.swt.widgets.Composite, com.ibm.research.tagging.core.IWaypoint)
	 */
	public FormText getProperties(Composite parent, IWaypoint waypoint) 
	{
		if ( waypoint instanceof PptWaypoint )
		{
			PptWaypoint wp = (PptWaypoint) waypoint;
			return ResourceWaypointUIExtension.getDefaultResourceProperties(parent, wp, wp.getMarker());
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getToolTipText(com.ibm.research.tagging.core.IWaypoint)
	 */
	public FormText getToolTipText(IWaypoint waypoint) 
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getLabel(com.ibm.research.tagging.core.IWaypoint, int)
	 */
	public String getLabel(IWaypoint waypoint, int column) {
		
		if(waypoint.getType().equals(PptWaypoint.TYPE))
		{
			PptWaypoint wp = (PptWaypoint)waypoint;
			
			if ( column==0 )
			{
				return wp.getMarker().getResource().getName();
			}
			else if ( column==1 )
			{
				String label = ""; 
				
				String desc =  wp.getDescription();
				if ( desc!=null )
					label += desc;
				
				int start = wp.getStartSlide(),
				    end   = wp.getEndSlide();
				
				if ( start>=1 && start==end )
				   label += " (slide #" + start + ")";
				else if ( start>=1 && start!=end )
				   label += " (slide #" + start + "-" + end +")";
				
				return label;
			}
		}
		
		return null;
	}
	
}
