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

package com.ibm.research.tagging.resource;

import java.text.SimpleDateFormat;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormText;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.IWaypointUIExtension;
import com.ibm.research.tagging.core.ui.TagUIPlugin;
import com.ibm.research.tagging.resource.wizards.NewResourceWaypointWizard;

public class ResourceWaypointUIExtension implements IWaypointUIExtension
{
	private JavaElementLabelProvider fJavaLabelProvider;
	private JavaElementLabelProvider fSmallJavaLabelProvider;
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagUIExtension#getImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getImage(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(ResourceWaypoint.TYPE))
		{
			ResourceWaypoint rw = (ResourceWaypoint)waypoint;
			return getJavaLabelProvider().getImage(rw.getMarker().getResource());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagUIExtension#getLabel(com.ibm.research.tagging.core.IWaypoint)
	 */
	public String getLabel(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(ResourceWaypoint.TYPE))
		{
			ResourceWaypoint rw = (ResourceWaypoint)waypoint;
			return getJavaLabelProvider().getText(rw.getMarker().getResource()) + (rw.getDescription()!=null && rw.getDescription().trim().length() > 0 ?" (" + rw.getDescription() + ")":"");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagUIExtension#getSmallImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getSmallImage(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(ResourceWaypoint.TYPE))
		{
			ResourceWaypoint rw = (ResourceWaypoint)waypoint;
			return getSmallJavaLabelProvider().getImage(rw.getMarker().getResource());
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
		if ( waypoint instanceof ResourceWaypoint && waypoint.getType().equals(ResourceWaypoint.TYPE) )
		{
			ResourceWaypoint wp = (ResourceWaypoint) waypoint;
			IMarker marker 		= wp.getMarker();
			IResource resource 	= marker.getResource();

			// the resource waypoint wizard automatically checks for waypoints and prepopulates the fields appropriately
			IStructuredSelection structuredSelection = new StructuredSelection(new Object[] {resource});
			NewResourceWaypointWizard wizard= new NewResourceWaypointWizard();
			wizard.init(PlatformUI.getWorkbench(),structuredSelection);
			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
			dialog.create();
			dialog.open();
			
			return true;
		}
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getProperties(org.eclipse.swt.widgets.Composite, com.ibm.research.tagging.core.IWaypoint)
	 */
	public FormText getProperties(Composite parent, IWaypoint waypoint) 
	{
		if ( waypoint instanceof ResourceWaypoint && waypoint.getType().equals(ResourceWaypoint.TYPE))
		{
			ResourceWaypoint wp = (ResourceWaypoint) waypoint;
			return getDefaultResourceProperties(parent, wp, wp.getMarker());
		}
		
		return null;
	}
	
	/**
	 * general utility routine to generate properties for a waypoint and associated IMarker information
	 * @param parent
	 * @param waypoint
	 * @param resource
	 * @return Formtext
	 */
	public static FormText getDefaultResourceProperties(Composite parent, IWaypoint waypoint, IMarker marker)
	{
		FormText formText = new FormText(parent,SWT.WRAP);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<form>");
		
			buf.append("<li style=\"image\" value=\"waypoint\">");
				
					if(waypoint.getDescription()!=null && waypoint.getDescription().trim().length() > 0)
						buf.append(waypoint.getDescription().trim());
					else
						buf.append("This waypoint needs a description.");
					
			buf.append("</li>");

			IResource resource = marker.getResource();
			if ( resource!=null )
			{
				buf.append("<li>");
				buf.append("resource: ");
				buf.append(resource.toString().substring(1).trim());
				buf.append("</li>");
			}
			
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
	    
		formText.setImage("waypoint", TagUIPlugin.getDefault(). getImageRegistry().get(TagUIPlugin.IMG_WAYPOINT));
		formText.setText(buf.toString(), true, true);
	    
	    return formText;
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
		if(waypoint.getType().equals(ResourceWaypoint.TYPE))
		{
			ResourceWaypoint rw = (ResourceWaypoint)waypoint;
			if ( column==0 )
			{
				return getJavaLabelProvider().getText(rw.getMarker().getResource());
			}
			else if ( column==1 )
			{
				return rw.getDescription();
			}
		}
		return null;
	}
}