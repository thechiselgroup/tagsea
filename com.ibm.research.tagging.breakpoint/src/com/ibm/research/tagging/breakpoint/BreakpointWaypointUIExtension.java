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
package com.ibm.research.tagging.breakpoint;

import java.text.SimpleDateFormat;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.IWaypointUIExtension;

public class BreakpointWaypointUIExtension implements IWaypointUIExtension
{
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getImage(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(BreakpointWaypoint.TYPE))
		{
			BreakpointWaypoint breakWaypoint = (BreakpointWaypoint)waypoint;
			return DebugUITools.newDebugModelPresentation().getImage(breakWaypoint.getBreakpoint());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getLabel(com.ibm.research.tagging.core.IWaypoint)
	 */
	public String getLabel(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(BreakpointWaypoint.TYPE))
		{
			BreakpointWaypoint breakWaypoint = (BreakpointWaypoint)waypoint;
			return DebugUITools.newDebugModelPresentation().getText(breakWaypoint.getBreakpoint());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getSmallImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getSmallImage(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(BreakpointWaypoint.TYPE))
		{
			BreakpointWaypoint breakWaypoint = (BreakpointWaypoint)waypoint;
			Image image = DebugUITools.newDebugModelPresentation().getImage(breakWaypoint.getBreakpoint());
			return image;
		}
		return null;
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
		if(waypoint.getType().equals(BreakpointWaypoint.TYPE))
		{
			BreakpointWaypoint breakpoint = (BreakpointWaypoint)waypoint;
			
			FormText text = new FormText(parent,SWT.WRAP);

			StringBuffer buf = new StringBuffer();

			buf.append("<form>");

			buf.append("<li style=\"image\" value=\"debug\">");

			if(waypoint.getDescription()!=null && waypoint.getDescription().trim().length() > 0)
				buf.append(waypoint.getDescription().trim());
			else
				buf.append("This waypoint needs a description.");

			buf.append("</li>");

			buf.append("<li style=\"image\" value=\"state\">");
			
			try 
			{
				if(breakpoint.getBreakpoint().isEnabled())
					buf.append("This breakpoint is enabled");
				else
					buf.append("This breakpoint is disabled");
			} 
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
			
			buf.append("</li>");
			
			IResource resource = breakpoint.getBreakpoint().getMarker().getResource();
			if ( resource!=null )
			{
				buf.append("<li>");
				buf.append("resource: ");
				buf.append(resource.toString().substring(1).trim());
				buf.append("</li>");
			}
			
			buf.append("<p>");

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

			buf.append("</p>");

			buf.append("</form>");

			text.setImage("debug",BreakpointPlugin.getDefault(). getImageRegistry().get(BreakpointPlugin.IMG_DEBUG));
			text.setImage("state", DebugUITools.newDebugModelPresentation().getImage(breakpoint.getBreakpoint()));
			text.setText(buf.toString(), true, true);

			return text;
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
		
		if ( waypoint.getType().equals(BreakpointWaypoint.TYPE) )
		{
			BreakpointWaypoint breakWaypoint = (BreakpointWaypoint)waypoint;
			
			if ( column==0 )
			{
				return DebugUITools.newDebugModelPresentation().getText(breakWaypoint.getBreakpoint());
			}
			else if ( column==1 )
			{
				return breakWaypoint.getDescription();
			}
		}
		
		return null;
	}
	
	
	
}
