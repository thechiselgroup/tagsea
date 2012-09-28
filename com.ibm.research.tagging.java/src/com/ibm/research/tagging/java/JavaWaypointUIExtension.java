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

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.IWaypointUIExtension;
import com.ibm.research.tagging.resource.ResourceWaypointUIExtension;

public class JavaWaypointUIExtension implements IWaypointUIExtension
{
	private JavaElementLabelProvider fJavaLabelProvider;
	private JavaElementLabelProvider fSmallJavaLabelProvider;

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getImage(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(JavaWaypoint.TYPE))
		{
			try 
			{
				JavaWaypoint javaFileWaypoint = (JavaWaypoint)waypoint;
				Image image = getJavaLabelProvider().getImage(javaFileWaypoint.getJavaElement());

				if(image == null)
					image = JavaWaypointPlugin.getDefault().getImageRegistry().get(JavaWaypointPlugin.IMG_WAYPOINT);

				return image;
			} 
			catch (JavaModelException e) 
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getLabel(com.ibm.research.tagging.core.IWaypoint)
	 */
	public String getLabel(IWaypoint waypoint)
	{
		if(waypoint.getType().equals(JavaWaypoint.TYPE))
		{
			try 
			{
				JavaWaypoint wp = (JavaWaypoint) waypoint;
				String description = wp.getDescription()==null ? "" :" (" + wp.getDescription() + ")";
				String text = "";

				if(((JavaWaypoint)waypoint).getJavaElement() == null)
					text = "Java File Waypoint" + description;
				else
					text =  getJavaLabelProvider().getText(((JavaWaypoint)waypoint).getJavaElement()) + description;

				return text;
			} 
			catch (JavaModelException e) 
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getSmallImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getSmallImage(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(JavaWaypoint.TYPE))
		{
			try 
			{
				Image image = getSmallJavaLabelProvider().getImage(((JavaWaypoint)waypoint).getJavaElement());

				if(image == null)
					image = JavaWaypointPlugin.getDefault().getImageRegistry().get(JavaWaypointPlugin.IMG_WAYPOINT);

				return image;
			} 
			catch (JavaModelException e) 
			{
				e.printStackTrace();
			}
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
	 * @see com.ibm.research.tagging.core.ui.ITagUIExtension#editProperties(com.ibm.research.tagging.core.IWaypoint)
	 */
	public boolean editProperties(IWaypoint waypoint) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getProperties(org.eclipse.swt.widgets.Composite, com.ibm.research.tagging.core.IWaypoint)
	 */
	public FormText getProperties(Composite parent, IWaypoint waypoint)
	{
		if ( waypoint instanceof JavaWaypoint )
		{
			JavaWaypoint wp = (JavaWaypoint) waypoint;
			return ResourceWaypointUIExtension.getDefaultResourceProperties(parent, wp, wp.getMarker());
		}
		return null;	
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getToolTipText(com.ibm.research.tagging.core.IWaypoint)
	 */
	public FormText getToolTipText(IWaypoint waypoint) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getLabel(com.ibm.research.tagging.core.IWaypoint, int)
	 */
	public String getLabel(IWaypoint waypoint, int column) {
		if(waypoint.getType().equals(JavaWaypoint.TYPE))
		{
			try 
			{
				JavaWaypoint wp = (JavaWaypoint) waypoint;
				
				if ( column==0 )
				{
					if(((JavaWaypoint)waypoint).getJavaElement() == null)
						return "Java File Waypoint";
					else
						return getJavaLabelProvider().getText(((JavaWaypoint)waypoint).getJavaElement());
				}
				else if ( column==1 )
				{
					return wp.getDescription();
				}
			} 
			catch (JavaModelException e) 
			{
				JavaWaypointPlugin.log("error processing getLabel, waypoint=" + waypoint + " column=" + column, e);
			}
		}
		
		return null;
	}

}
