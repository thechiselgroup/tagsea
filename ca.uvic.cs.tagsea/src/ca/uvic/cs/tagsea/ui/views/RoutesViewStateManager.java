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
package ca.uvic.cs.tagsea.ui.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Route;
import ca.uvic.cs.tagsea.core.Waypoint;

/**
 * @author mdesmond
 */
public class RoutesViewStateManager 
{
	public final static String MEMENTO_XML_FILE = ".metadata/.plugins/ca.uvic.cs.tagsea/routesViewMemento.xml";
	
	private static XMLMemento fMemento;
	
	public static void recordState(RoutesComposite routesComposite) 
	{		
		fMemento = XMLMemento.createWriteRoot("routesViewMemento");
		saveStateToMemento(routesComposite);
		saveMemento();
	}

	/**
	 * Save the routes to an XML file (This is the first step,
	 * where we save the routes to a memento)
	 * @param memento
	 */
	private static void saveStateToMemento(RoutesComposite routesComposite) 
	{			
		IMemento expandedRoutes = fMemento.createChild("expandedRoutes");
		IMemento selectedRoutes = fMemento.createChild("selectedRoutes");
		
		Object[] expandedRouteElements = routesComposite.getRoutesTreeViewer().getExpandedElements();
		ISelection selection = routesComposite.getRoutesTreeViewer().getSelection();
		Object[] selectedRouteElements = ((IStructuredSelection)selection).toArray();
		
		for(Object o : expandedRouteElements)
		{
			Route route = (Route)o;
			IMemento m = expandedRoutes.createChild("route");
			m.putString("name", route.getName());
		}
		
		for(Object o : selectedRouteElements)
		{
			if(o instanceof Route)
			{
				Route route = (Route)o;
				IMemento m = selectedRoutes.createChild("route");
				m.putString("name", route.getName());
			}
			else if(o instanceof Waypoint)
			{
				Waypoint waypoint = (Waypoint)o;
				IMemento m = selectedRoutes.createChild("waypoint");
				m.putString("id", waypoint.getKeyword() + waypoint.getLineNumber());
			}
		}
	}

	/**
	 * Save the routes to an XML file (This is the second step,
	 * where we save the memento to a file)
	 * @param fileName
	 * @return success
	 */
	private static boolean saveMemento() 
	{
		IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = workSpaceRoot.getLocation().append(MEMENTO_XML_FILE);		
		File mementoFile = path.toFile();
		mementoFile.getParentFile().mkdirs();
		
		if (mementoFile == null)
			return false;
		
		try
		{		
			FileOutputStream stream = new FileOutputStream(mementoFile);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
			fMemento.save(writer);
			writer.close();
		}
		catch (IOException e) 
		{
			TagSEAPlugin.log("Error: writing to routes file failed in RouteXMLUtil.saveRoutes()");
			return false;
		}
		// Success !
		return true;
	}
	
	public static void restoreState(RoutesComposite routesComposite)
	{	
		IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = workSpaceRoot.getLocation().append(MEMENTO_XML_FILE);		
		File mementoFile = path.toFile();
		
		if(mementoFile.exists())
		{
			try 
			{
				FileInputStream input = new FileInputStream(mementoFile);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input, "utf-8")); //$NON-NLS-1$
				
				IMemento memento = XMLMemento.createReadRoot(reader);			
				
				restoreStateFromMemento(routesComposite, memento);
			} 
			catch (FileNotFoundException e) 
			{
				TagSEAPlugin.log("Error: can't find the routes file in RouteXMLUtil.restoreRoutes()");
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

	@SuppressWarnings("unchecked")
	private static void restoreStateFromMemento(RoutesComposite routesComposite,IMemento memento) 
	{	
		IMemento expandedRoutesMemento = memento.getChild("expandedRoutes");
		IMemento selectedRoutesMemento = memento.getChild("selectedRoutes");
		
		if (expandedRoutesMemento != null) 
		{
			IMemento[] routeMementos = expandedRoutesMemento.getChildren("route");
			List<Route> routes = new ArrayList<Route>();
			
			for (IMemento m : routeMementos) 
			{
				String name = m.getString("name");
				
				if(name!=null)
				{
					Route r = TagSEAPlugin.getDefault().getRouteCollection().getRoute(name);
					
					if(r!=null)
						routes.add(r);
				}
			}
			
			if(routes.size() > 0)
			{
				Route[] array = new Route[routes.size()];
				array = routes.toArray(array);
				routesComposite.getRoutesTreeViewer().setExpandedElements(array);
			}
		}
		
		if (selectedRoutesMemento != null) 
		{
			IMemento[] routeMementos = selectedRoutesMemento.getChildren("route");
			IMemento[] waypointMementos = selectedRoutesMemento.getChildren("waypoint");
			
			List selection = new ArrayList();
			
			for (IMemento m : routeMementos) 
			{
				String name = m.getString("name");
				
				if(name!=null)
				{
					Route r = TagSEAPlugin.getDefault().getRouteCollection().getRoute(name);
					
					if(r!=null)
						selection.add(r);
				}
			}
			
			try 
			{
				for (IMemento m : waypointMementos) 
				{
					String id = m.getString("id");
					
					if(id!=null)
					{
						Waypoint w = TagSEAPlugin.getDefault().getTagCollection().getWaypointCollection().getWaypoint(id);
						
						if(w!=null)
							selection.add(w);
					}
				}
			}
			catch (RuntimeException e) 
			{
				e.printStackTrace();
			}
			
			if(selection.size() > 0)
				routesComposite.getRoutesTreeViewer().setSelection(new StructuredSelection(selection));
		}
	}
}
