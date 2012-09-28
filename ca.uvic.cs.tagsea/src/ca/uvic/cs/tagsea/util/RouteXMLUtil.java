/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Route;
import ca.uvic.cs.tagsea.core.RouteCollection;
import ca.uvic.cs.tagsea.core.Waypoint;
import ca.uvic.cs.tagsea.core.WaypointCollection;

/**
 * The RouteXMLUtil is used to save route information from the time that Eclipse
 * has been closed until it is next opened.  We use the IMemento to facilitate this.
 * 
 * @author Jie Zhang and Suzanne Thompson, mdesmond
 *
 */

public class RouteXMLUtil 
{
	public final static String WAYPOINT = "WAYPOINT";
	public final static String WAYPOINT_ID = "WAYPOINT_ID";
	public final static String ROUTE = "ROUTE";
	public final static String ROUTE_ID = "ROUTE_ID";
	public final static String ROUTES = "ROUTES";
	public final static String ROUTE_XML_FILE = ".metadata/.plugins/ca.uvic.cs.tagsea/routesMemento.xml";
	
	private static XMLMemento memento;
	
	public static void recordRoutes(RouteCollection routes) {
			
		memento = XMLMemento
					.createWriteRoot("ROUTE_REPOSITORY");
			saveRouteToMemento(memento, routes );
			saveRoutes();
	}

	/**
	 * Save the routes to an XML file (This is the first step,
	 * where we save the routes to a memento)
	 * @param memento
	 * 
	 */
	private static void saveRouteToMemento(IMemento routesMemento, RouteCollection routes ) {
		
		int routeCount = routes.getRoutes().size();
		if ( routeCount > 0) {
			
			IMemento routesMem = routesMemento.createChild(ROUTES);
			//register all the routes
			for (int i= 0; i < routeCount; i++) {
				IMemento routeMem= routesMem.createChild(ROUTE);				
				Route route = routes.getRoutes().get(i);
				routeMem.putString(ROUTE_ID, route.getName());
				List<Waypoint> waypoints = route.getWaypoints();
				
				//register all the waypoints in the route
				for (Iterator iter = waypoints.iterator(); iter.hasNext();) {
					Waypoint element = (Waypoint) iter.next();
					IMemento wpMem= routeMem.createChild(WAYPOINT);
					wpMem.putString(WAYPOINT_ID, element.getKeyword()+element.getLineNumber());
				}
			}
		}		
	}

	/**
	 * Save the routes to an XML file (This is the second step,
	 * where we save the memento to a file)
	 * @param fileName
	 * @return success
	 */
	private static boolean saveRoutes() {
		IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = workSpaceRoot.getLocation().append(ROUTE_XML_FILE);		
		File routeFile = path.toFile();
		routeFile.getParentFile().mkdirs();
		if (routeFile == null)
			return false;
		
		try {		
			FileOutputStream stream = new FileOutputStream(routeFile);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
			memento.save(writer);
			writer.close();
		} catch (IOException e) {
			TagSEAPlugin.log("Error: writing to routes file failed in RouteXMLUtil.saveRoutes()");
			return false;
		}
		// Success !
		return true;
	}
	
	public static void restoreRoutes(){		
		
		IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = workSpaceRoot.getLocation().append(ROUTE_XML_FILE);		
		File routeFile = path.toFile();
		if(routeFile.exists()){
			try {
				FileInputStream input = new FileInputStream(routeFile);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input, "utf-8")); //$NON-NLS-1$
				
				IMemento memento = XMLMemento.createReadRoot(reader);			
						
				restoreRoutesFromMemento( memento);
			} catch (FileNotFoundException e) {
				TagSEAPlugin.log("Error: can't find the routes file in RouteXMLUtil.restoreRoutes()");
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WorkbenchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void restoreRoutesFromMemento(IMemento salvagedRouteMem) {
		
		IMemento rootMem= salvagedRouteMem.getChild(ROUTES);
		
		if (rootMem != null) 
		{
			IMemento[] routesMem = rootMem.getChildren(ROUTE);
			
			for (int i= 0; i < routesMem.length; i++)
			{
				String routeName = routesMem[i].getString(ROUTE_ID);
				IMemento[] waypointsMem = routesMem[i].getChildren(WAYPOINT);
				
				Route route = new Route(routeName);
				WaypointCollection waypoints = TagSEAPlugin.getDefault().getTagCollection().getWaypointCollection();
				
				for(int j = 0; j < waypointsMem.length; j++) 
				{
					String waypointID = waypointsMem[j].getString(WAYPOINT_ID);
					
					Waypoint waypointInRepository = waypoints.getWaypoint(waypointID);
					
					//if the waypoint can't be found in the repository drop it (?)
					if(waypointInRepository != null) 
					{
						route.addWaypoint(waypointInRepository);
					}
					
					//@tag futureWork(routes): what if we can't find a waypoint?
					//right now if we can't find a waypoint, we don't include it in the route
				}
				
				if(route != null) 
				{
					TagSEAPlugin.getDefault().getRouteCollection().addRoute(route);
				} 
				else 
				{
					TagSEAPlugin.log("Error: In RouteXMLUtil, salvaged route was null");
				}
				
			}
		}
		
	}
}
