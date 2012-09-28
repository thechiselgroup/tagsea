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
package ca.uvic.cs.tagsea.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.monitoring.TagSEARoutingEvent;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;
import ca.uvic.cs.tagsea.ui.views.RoutesView;
import ca.uvic.cs.tagsea.util.RouteXMLUtil;

/**
 * Holds the routes.
 * 
 * @author many
 */
public class RouteCollection implements ITreeContentProvider, ILabelProvider, IColorProvider{
	// list of route object's
	private List<Route> routes;
	
	private ImageDescriptor routesDescriptor;
	private static Image routesImage;
	private List <IRouteCollectionListener> fListeners;
	private boolean fLoaded = false;
	
	public RouteCollection() 
	{
		this.routes = new ArrayList<Route>();
		routesDescriptor = TagSEAPlugin.getImageDescriptor("/icons/route.gif");
	}

	private Image getRoutesImage() {
		if ((routesImage == null) || routesImage.isDisposed()) {
			routesImage = routesDescriptor.createImage();
		}
		return routesImage;
	}

	/**
	 * Adds the route if it doesn't already contain it, or one with the same name.
	 * @param route
	 */
	public void addRoute(Route route) {
		if ((route != null) && !routes.contains(route)) {
			for (Route r : routes) {
				if (r.equals(route) || r.getName().equals(route.getName())) {
					return;
				}
			}
			this.routes.add(route);
			Monitoring.getDefault().fireEvent(new TagSEARoutingEvent(TagSEARoutingEvent.Routing.New, route, null));
		}
	}
	
	public int getSize() {
		return routes.size();
	}
	
	/**
	 * Creates and adds a new route if one with the given name
	 * doesn't already exist.
	 * @param name
	 */
	public Route addRoute(String name) {
		if (name != null) {
			for (Route route : routes) {
				if (name.equals(route.getName()))
					return route;
			}
			Route route = new Route(name);
			routes.add(route);
			Monitoring.getDefault().fireEvent(new TagSEARoutingEvent(TagSEARoutingEvent.Routing.New, route, null));
			return route;
		}
		return null;
	}
	
	/**
	 * Removes the route from the list.
	 * @param route
	 */
	public boolean removeRoute(Route route) {
		if (route != null) {
			Monitoring.getDefault().fireEvent(new TagSEARoutingEvent(TagSEARoutingEvent.Routing.Delete, route, null));
			return routes.remove(route);
		}
		return false;
	}
	
	/**
	 * Returns all the routes.
	 * @return List of Route objects
	 */
	public List<Route> getRoutes() {
		return routes;
	}
	
	/**
	 * Returns the Route for the given name.
	 * @param name
	 * @return Route
	 */
	public Route getRoute(String name) {
		Route route = null;
		for (Route r : routes) {
			if (r.getName().equals(name)) {
				route = r;
				break;
			}
		}
		return route;
	}
	
	public String toString() {
		return "Routes [" + routes.size() + "]";
	}

	/**
	 * Gets the root Route objects.
	 */
	public Object[] getElements(Object parent) {
		return getRoutes().toArray();
	}
	
	/**
	 * Returns null.
	 */
	public Object getParent(Object child) {
		return null;
	}
	
	/**
	 * Get children or an empty array if there are no children or specified parent is not a tag.
	 * @param parent the Route
	 * @return Object[] the children Tag objects
	 */
	public Object[] getChildren(Object parent) {
		if (parent instanceof Route) {
			Route parentTag = (Route) parent;
			return parentTag.getWaypoints().toArray();
		} else {
			return new Object[0];
		}
	}

	/**
	 * Checks if the given parent (which should be a Tag) has children.
	 * @param parent the Tag object
	 * @return boolean if the parent has children
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof Route) {
			Route route = (Route) parent;
			return route.getWaypoints().size() > 0;
		} else {
			return false;
		}
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}
	
	/**
	 * Gets the text (the name) for a given Route object.
	 * @param obj the Tag
	 */
	public String getText(Object obj) 
	{
		if (obj instanceof Route) 
		{
			Route route = (Route) obj;
			return route.getName();
		} 
		else if (obj instanceof Waypoint) 
		{
			Waypoint waypoint = (Waypoint) obj;
			return waypoint.getJavaElementName() + " - " + waypoint.getKeyword();
		}
		return obj.toString();
	}

	/**
	 * Gets the image for the given Route object.
	 * TODO Returns null for now.
	 */
	public Image getImage(Object element) {
		
		Image img = null;
		if (element instanceof Route) 
		{
			img = getRoutesImage();
		} 
		else if (element instanceof Waypoint) 
		{	
			Waypoint waypoint = (Waypoint)element;

			// trees use different size images than tables!
			// we need to set the flag so that the label provider returns a Small (16x16) image
			// by default it will return a larger (24x16) image which is used in tables.
			// trees need 16x16 images
			
			int flags = JavaElementLabelProvider.SHOW_DEFAULT | JavaElementLabelProvider.SHOW_SMALL_ICONS;
			JavaElementLabelProvider jlabel = new JavaElementLabelProvider(flags);
			img = jlabel.getImage(waypoint.getJavaElement());
			if ((img != null) && img.isDisposed()) {
				img = null;
			}
		}
		return img;
	}	
	
	/** Does nothing. */
	public void addListener(ILabelProviderListener listener) {
	}
	
	/** Returns false. */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	
	/** Does nothing. */
	public void removeListener(ILabelProviderListener listener) {
	}
	
	public boolean hasWaypointsInRoutes(){
		boolean hasWpsInRoutes = false;
		if(getSize()>0){
			for (Iterator iter = routes.iterator(); iter.hasNext();) {
				Route element = (Route) iter.next();
				if(element.getWaypoints().size()>0){
					hasWpsInRoutes = true;
					break;
				}				
			}
		}
		return hasWpsInRoutes;
	}
	
	/**
	 * Gets the routes with the given name
	 * @param ids An array of route names
	 * @return An array of all tag objects that match the list of route names.
	 */
	public Route[] getRoutes(String[] ids) {
		LinkedList<Route> listOfRoutes = new LinkedList<Route>();
		if(ids.length > 0) {
			for ( Route route : routes ) {
				for(String id : ids) {
					if ( route.getName().equals(id) ) {
						listOfRoutes.add( route );
					}
				}
			}
		}
		return listOfRoutes.toArray(new Route[listOfRoutes.size()]);
	}
	
	/**
	 * Updates the routes view. 
	 */
	public void updateView()
	{
		// @tag reimpliment
		// The routes component is badly implemented, it should look for stale waypoints 
		// by listening to the tag collection, for the moment this hack will work but the 
		// underlying route and tag storage and managment model needs to be completly 
		// reimplemented...respect my authoratah!
		// Using this implementation the routes may potentially be out of sync
		
		List<Route> routes = getRoutes();
		
		for(Route r : routes)
		{
			Vector<Waypoint> waypoints = r.getWaypoints();
			List<Integer> indices = new ArrayList<Integer>();
			int i = 0;
			
			for(Waypoint w : waypoints)
			{
				if(w.isStale())
				{
					//@tag badDesign : the waypoint id should be stored in the waypoint
					String waypointId = w.getKeyword() + w.getLineNumber();
					Waypoint freshCopy = TagSEAPlugin.getDefault().getTagCollection().getWaypointCollection().getWaypoint(waypointId);
				
					if(freshCopy != null)
					{
						indices.add(i);
					}
				}
				
				i++;
			}
			
			// Cant modify the waypoints while interating under them, thats why this mess exists
			if(indices.size() > 0)
				for(i = (indices.size() -1); i >= 0; i--)
				{
					Waypoint w = waypoints.elementAt(indices.get(i));
					String waypointId = w.getKeyword() + w.getLineNumber();
					waypoints.removeElementAt(indices.get(i));
					Waypoint freshCopy = TagSEAPlugin.getDefault().getTagCollection().getWaypointCollection().getWaypoint(waypointId);
					waypoints.insertElementAt(freshCopy,indices.get(i));
				}
		}
		
		
		RoutesView view = TagSEAPlugin.getDefault().getRoutesView();
		
		if (view != null) 		
			view.getRoutesComposite().refreshRoutesViewer();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object element) 
	{
		return Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) 
	{
		if(element instanceof Waypoint)
		{
			Waypoint w = (Waypoint)element;
			 
			if(w.isStale())
				return Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
		}
		
		return Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	}

	public void save() 
	{
		RouteXMLUtil.recordRoutes(this);
	}
	
	public void load() 
	{
		RouteXMLUtil.restoreRoutes();
		fLoaded = true;
		fireLoaded();
	}
	
	public synchronized void addRoutesCollectionListener(IRouteCollectionListener listener)
	{
		getListeners().add(listener);
		fireLoaded();
	}
	
	private void fireLoaded() 
	{
		if(fLoaded)
			Display.getDefault().asyncExec(new Runnable() {

				public void run() 
				{
					for(IRouteCollectionListener listener : getListeners())
						listener.routesLoaded();
				}

			});
	}

	public synchronized void removeRoutesCollectionListener(IRouteCollectionListener listener)
	{
		getListeners().remove(listener);
	}
	
	List <IRouteCollectionListener> getListeners()
	{
		if(fListeners == null)
			fListeners = new ArrayList <IRouteCollectionListener>();
		
		return fListeners;
	}
	
}
