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
package com.ibm.research.tagging.resource.actions;

import java.util.Date;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.dialogs.QuickTagDialog;
import com.ibm.research.tagging.resource.ResourceWaypoint;
import com.ibm.research.tagging.resource.ResourceWaypointUtil;

public class QuickTagActionDelegate implements IObjectActionDelegate 
{
	private static final String DIALOG_TITLE = "Tag selected resources";

	private ISelection fSelection;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		if(fSelection != null)
		{
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			QuickTagDialog dialog = new QuickTagDialog(shell,DIALOG_TITLE,null);
			
			if(dialog.open() == Window.OK)
			{
				String[] tags = dialog.getTags();
				
				if(tags.length > 0)
				{
					IStructuredSelection structuredSelection = (IStructuredSelection)fSelection;

					for(Object o : structuredSelection.toArray())
					{
						IResource resource = null;
						
						if(o instanceof IResource)
						{
							resource = (IResource)o;
						}
						else if (o instanceof IAdaptable)
						{
							IAdaptable adaptable = (IAdaptable)o;
							resource = (IResource)adaptable.getAdapter(IResource.class);
						}
							
						if(resource !=null)
						{
							if(resource.exists())
							{
								try 
								{
									// look for an existing resource marker
									IMarker marker = ResourceWaypointUtil.getFirstMarker(ResourceWaypoint.MARKER_ID, resource);
									
									ResourceWaypoint waypoint = null;
									
									// an existing maker exists
									if(marker != null)
									{
										IWaypoint w = ResourceWaypointUtil.getWaypointFromModel(Long.toString(marker.getId()));
										
										if(w!=null)
											waypoint = (ResourceWaypoint)w;
									}
									
									if(waypoint == null)
										waypoint = new ResourceWaypoint(resource,null,System.getProperty("user.name"),new Date());

									TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypoint(waypoint);
									ResourceWaypointUtil.tag(waypoint,tags);
									
									waypoint.save();
								} 
								catch (CoreException e) 
								{
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = selection;
	}
}
