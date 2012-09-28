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

package com.ibm.research.tagging.resource.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.wizards.WaypointWizard;
import com.ibm.research.tagging.resource.ResourceWaypoint;
import com.ibm.research.tagging.resource.ResourceWaypointPlugin;
import com.ibm.research.tagging.resource.ResourceWaypointUtil;

public class NewResourceWaypointWizard extends WaypointWizard 
{
	private static final Pattern BUNDLE_NAME_PATTERN = Pattern.compile("Bundle-Name: (.+)");
	
	private final static String WINDOW_TITLE = "New Resource Waypoint";

	public NewResourceWaypointWizard() 
	{
		super(WINDOW_TITLE, new NewResourceWaypointPage());
	}
	
	private IResource getResource(Object o)
	{
		if (o instanceof IAdaptable )
		{
			IResource resource = (IResource) ((IAdaptable)o).getAdapter(IResource.class);
			
			// some java elements do not give IResource via getAdapter
			if ( resource==null && (o instanceof IJavaElement) )
			{
				resource = ((IJavaElement) o).getResource();
				
				if ( resource==null )
				{
					// this is an external resource, can't handle these for now...
					// IPath path = ((IJavaElement) o).getPath(); 
				}
			}
	
			if ( !resource.exists() )
				return null;
			
			return resource;
		}
		
		return null;
	}
	
	private String readManifestBundleName(IProject project)
	{
		// see if there is a MANIFEST.MF - use the bundle name as a default description
		IResource manifest = project.findMember(new Path("META-INF/MANIFEST.MF"));
		if ( manifest instanceof IFile )
		{
			try {
				InputStream inputStream = ((IFile) manifest).getContents();
				Reader reader = new InputStreamReader(inputStream);
				
	            int size = 0;
	            char[] b = new char[65536];
	            StringBuilder builder = new StringBuilder();
				do	{
					size = reader.read(b, 0, b.length);
					if(size > 0) 
						builder.append(b, 0, size);
				} while(size > 0);
				
				reader.close();
				inputStream.close();
				
				String content = builder.toString();
				if ( content!=null && content.length()>0 )
				{
					Matcher matcher = BUNDLE_NAME_PATTERN.matcher(content);
					if ( matcher.find() )
						return matcher.group(1);
				}
			} catch (CoreException e) {
				ResourceWaypointPlugin.log("error while reading " + manifest, e);
			} catch (IOException e) {
				ResourceWaypointPlugin.log("error while reading " + manifest, e);
			}
		}
		
		return null;
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		
		// set defaults if we selected one resource (not multiple)
		if ( selection!=null )
		{
			Object[] o = selection.toArray();
			
			if ( o.length==1 && o[0] instanceof IAdaptable )
			{
				IResource resource = getResource(o[0]);
				
				if ( resource!=null )
				{
					// look for an existing resource marker
					IMarker marker = ResourceWaypointUtil.getFirstMarker(ResourceWaypoint.MARKER_ID, resource);

					ResourceWaypoint waypoint = null;
					
					// if an existing marker exists, get the waypoint - if any - and use its data to prepopulate the wizard fields
					if ( marker != null )
					{
						IWaypoint w = ResourceWaypointUtil.getWaypointFromModel(Long.toString(marker.getId()));

						if ( w!=null )
							waypoint = (ResourceWaypoint)w;
					}
					
					if ( waypoint!=null )
					{
						getPage().setDefaultAuthorText(waypoint.getAuthor());
						getPage().setDefaultDescriptionText(waypoint.getDescription());
						getPage().setDefaultTagsText(waypoint.getTags());
					}
					// if no existing waypoint, and if this is a project, get the project's manifest info for the waypoint description
					else if ( resource instanceof IProject )
					{
						String bundleName = readManifestBundleName((IProject) resource);
						if ( bundleName!=null )
							getPage().setDefaultDescriptionText(bundleName);
					}
					
				}
			}
		}
	}
	
	// have to override, because resource waypoint wizard can handle multiple waypoints, not just one
	public boolean performFinish() 
	{	
		String[] tagNames = getPage().getTags();
		String     author = getPage().getAuthorText(),
	               desc   = getPage().getDescriptionText();
	
		
		IStructuredSelection selection = getSelection();
		
		if ( selection==null )
		{
			MessageDialog.openError(getShell(), "error", "no resources selected to create waypoints with");
			return false;
		}
		
		for(Object o : selection.toArray())
		{
			IResource resource = getResource(o);
				
			if ( resource!=null )
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
						waypoint = new ResourceWaypoint(resource,desc,author,new Date());
					else
					{
						// update the description and author at least
						waypoint.setAuthor(author);
						waypoint.setDescription(desc);
					}

					TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypoint(waypoint);
					ResourceWaypointUtil.tag(waypoint,tagNames);

					waypoint.save();
				} 
				catch (CoreException e) 
				{
					ResourceWaypointPlugin.log("exception while trying to waypoint resource=" + resource, e);
				}
			}
		}
		
		return true;
	}

	protected IWaypoint getWaypoint() {
		// unused, because we're customizing performFinish()
		return null;
	}
}
