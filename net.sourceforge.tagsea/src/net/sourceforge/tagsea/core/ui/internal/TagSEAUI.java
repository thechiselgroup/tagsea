/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.internal.ITagSEAPreferences;
import net.sourceforge.tagsea.core.ui.ITagSEAUI;
import net.sourceforge.tagsea.core.ui.IWaypointFilter;
import net.sourceforge.tagsea.core.ui.IWaypointFilterUI;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;
import net.sourceforge.tagsea.core.ui.TagSEAView;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Access point for different UI elements in TagSEA. Useful for keeping user interface labels, images,
 * interactions, etc., consistant accross views.
 * @author Del Myers
 */

public class TagSEAUI implements ITagSEAUI {
	
	private final Map<String, IWaypointFilter> WAYPOINT_FILTERS = new HashMap<String, IWaypointFilter>();
	private final Map<IWaypointFilter, IWaypointFilterUI> WAYPOINT_FILTER_UIS = new HashMap<IWaypointFilter, IWaypointFilterUI>();
	private TreeSet<String> filteredTypes;
	private UIEventModel publisher = new UIEventModel();
	/**
	 * Used for when no filter has been defined for a given waypoint type so that 
	 * the platform doesn't have to constantly check extensions when no filter is
	 * available.
	 */
	private static final IWaypointFilter DUMMY_FILTER = new IWaypointFilter() {

		public void initialize() {
		}

		public boolean select(IWaypoint waypoint) {
			return false;
		}

		public boolean isImportantSelection(ISelection selection) {
			return false;
		}
		
	};
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#getTagsView()
	 */
	@Deprecated
	public net.sourceforge.tagsea.core.ui.tags.TagsView getTagsView() {
		try {
			return (net.sourceforge.tagsea.core.ui.tags.TagsView) 
			PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			findView(net.sourceforge.tagsea.core.ui.tags.TagsView.ID);
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#showTagsView()
	 */
	@Deprecated
	public net.sourceforge.tagsea.core.ui.tags.TagsView showTagsView() {
		try {
			return (net.sourceforge.tagsea.core.ui.tags.TagsView) 
			PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			showView(net.sourceforge.tagsea.core.ui.tags.TagsView.ID);
		} catch (Exception e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#getTagsView()
	 */
	public TagSEAView getTagSEAView() {
		try {
			return (TagSEAView) 
			PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			findView(TagSEAView.ID);
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#showTagsView()
	 */
	public TagSEAView showTagSEAView() {
		try {
			return (TagSEAView) 
			PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			showView(TagSEAView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
		} catch (Exception e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#getWaypointView()
	 *
	public WaypointView getWaypointView() {
		try {
			return (WaypointView) 
			PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			findView(WaypointView.ID);
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#showWaypointView()
	 *
	public WaypointView showWaypointView() {
		try {
			return (WaypointView) 
			PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			showView(WaypointView.ID);
		} catch (Exception e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#getImage(net.sourceforge.tagsea.core.IWaypoint)
	 */
	public Image getImage(IWaypoint waypoint) {
		return getWaypointUI(waypoint.getType()).getImage(waypoint);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#getLabel(net.sourceforge.tagsea.core.IWaypoint)
	 */
	public String getLabel(IWaypoint waypoint) {
		return getWaypointUI(waypoint.getType()).getLabel(waypoint);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#getTooltipText(net.sourceforge.tagsea.core.IWaypoint)
	 */
	public String getTooltipText(IWaypoint waypoint) {
		return getWaypointUI(waypoint.getType()).getToolTipText(waypoint);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#canUIChange(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	public boolean canUIChange(IWaypoint waypoint, String attribute) {
		return getWaypointUI(waypoint.getType()).canUIChange(waypoint, attribute);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#canUIMove(net.sourceforge.tagsea.core.IWaypoint)
	 */ 
	public boolean canUIMove(IWaypoint waypoint) {
		return getWaypointUI(waypoint.getType()).canUIMove(waypoint);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#canUIDelete(net.sourceforge.tagsea.core.IWaypoint)
	 */
	public boolean canUIDelete(IWaypoint waypoint) {
		return getWaypointUI(waypoint.getType()).canUIDelete(waypoint);
	}
	 
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.ITagSEAUI#getWaypointUI(java.lang.String)
	 */
	public IWaypointUIExtension getWaypointUI(String waypointType) {
		return TagSEAPlugin.getDefault().getWaypointUI(waypointType);
	}
	
	private void loadFilter(String type) {
		if (WAYPOINT_FILTERS.get(type) != null) return;
		//find the contributor for the waypoint of the given type to make sure that it matches with the filter contributor.
		IConfigurationElement[] elements = 
			Platform.getExtensionRegistry().getConfigurationElementsFor("net.sourceforge.tagsea.waypoint");
		IContributor contributor = null;
		for (IConfigurationElement element : elements) {
			String elementType = element.getAttribute("type");
			if ((element.getContributor().getName() + "." + elementType).equals(type)) {
				contributor = element.getContributor();
			}
		}
		if (contributor == null) {
			WAYPOINT_FILTERS.put(type, DUMMY_FILTER);
			return;
		}
		elements = Platform.getExtensionRegistry().getConfigurationElementsFor("net.sourceforge.tagsea.filters");
		for (IConfigurationElement element : elements) {
			String elementType = element.getAttribute("waypointType");
			if (elementType.equals(type)) {
				//make sure that the filter and the waypoint were defined by the same plugin
				if (!element.getContributor().equals(contributor)) {
					TagSEAPlugin.getDefault().getLog().log(new Status(
						IStatus.WARNING,
						TagSEAPlugin.PLUGIN_ID,
						IStatus.WARNING,
						"Waypoint filter must be declared in the same plugin as its waypoint type.",
						null
					));
					WAYPOINT_FILTERS.put(type, DUMMY_FILTER);
				} else {
					try {
						IWaypointFilter filter = (IWaypointFilter) element.createExecutableExtension("class");
						IWaypointFilterUI filterUI = (IWaypointFilterUI) element.createExecutableExtension("ui");
						filter.initialize();
						WAYPOINT_FILTERS.put(type, filter);
						WAYPOINT_FILTER_UIS.put(filter, filterUI);
					} catch (CoreException e) {
						TagSEAPlugin.getDefault().log(e);
						WAYPOINT_FILTERS.put(type, DUMMY_FILTER);
					}
				}
			}
		}
	}
	
	/**
	 * Returns the filter used for the given waypoint type if it is available, or null otherwise.
	 * @param waypointType the type of waypoint.
	 * @return the filter used for the given waypoint type if it is available, or null otherwise.
	 */
	public synchronized IWaypointFilter getFilter(String waypointType) {
		loadFilter(waypointType);
		IWaypointFilter filter = WAYPOINT_FILTERS.get(waypointType);
		if (DUMMY_FILTER.equals(filter)) return null;
		return filter;
	}
	  
	/**
	 * Return the filter ui used for the given waypoin type if it is available, or null otherwise.
	 * @param waypointType they type of waypoint.
	 * @return the filter ui used for the given waypoin type if it is available, or null otherwise.
	 */
	public synchronized IWaypointFilterUI getFilterUI(String waypointType) {
		IWaypointFilter filter = getFilter(waypointType);
		if (filter != null) {
			return WAYPOINT_FILTER_UIS.get(filter);
		}
		return null;
	}
	
	
	/**
	 * Returns true if the user has requested to filter the given waypoint type from the TagSEA views.
	 * @param waypointType the type to query.
	 */
	public boolean isFilteredOutType(String waypointType) {
		if (filteredTypes == null) {
			initializeFilteredTypes();
		}
		return filteredTypes.contains(waypointType);
	}

	/**
	 * 
	 */
	private void initializeFilteredTypes() {
		refreshFilters();
		IPreferenceStore prefs = TagSEAPlugin.getDefault().getPreferenceStore();
		prefs.addPropertyChangeListener(new IPropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(ITagSEAPreferences.FILTERED_WAYPOINT_TYPES))
					refreshFilters();
			}});
		
	}

	/**
	 * 
	 */
	private void refreshFilters() {
		filteredTypes = new TreeSet<String>();
		IPreferenceStore prefs = TagSEAPlugin.getDefault().getPreferenceStore();
		String types= prefs.getString(ITagSEAPreferences.FILTERED_WAYPOINT_TYPES);
		String[] filters = types.split("[,]");
		for (String filter : filters) {
			filteredTypes.add(filter);
		}
	}

	/**
	 * Internal method should not be called by clients.
	 */
	public void start() {
		stop();
		publisher.hookToUI();
	}
	
	public UIEventModel getUIEventModel() {
		return publisher;
	}

	/**
	 * Internal method should not be used by clients.
	 */
	public void stop() {
		if (publisher != null) {
			publisher.stop();
		}
	}
	
	/**
	 * Internal method should not be used by clients.
	 * @param listener
	 */
	public void addUIEventListener(ITagSEAUIListener listener) {
		if (publisher != null) {
			publisher.addListener(listener);
		}
	}
	
	/**
	 * Internal method should not be used by clients.
	 * @param listener
	 */
	public void removeUIEventListener(ITagSEAUIListener listener) {
		if (publisher != null) {
			publisher.removeListener(listener);
		}
	}
	
	
}
