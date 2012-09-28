/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui.internal;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.internal.ITagSEAPreferences;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Tracks events that occurr in the TagSEAUI. This class is internal and not intended to be
 * used by clients.
 * @author Del Myers
 */
public class TagSEAUIEvent {
	/**
	 * A tagsea view state was changed. The viewId string will be set to the view that was opened; the waypoint
	 * will be null; and the kind will be one of VIEW_ACTIVATED, VIEW_TOP, VIEW_OPENED, VIEW_DEACTIVATED,
	 * VIEW_HIDDEN, VIEW_CLOSED, or VIEW_FILTERED. If the kind is VIEW_FILTERED, the detail will contain the
	 * string used in the filtering. If the kind is VIEW_HIERARCHY, the detail will contain a boolean "true"
	 * or false to state whether the user is viewing tags as a hierarchy.
	 */
	public static final int VIEW = 0;
	
	/**
	 * A waypoint was navigated to. The waypoint will be set to the waypont navigated to; the
	 * detail will be null; the view id will be null.
	 */
	public static final int NAVIGATE = 1;
	/**
	 * A change has ocurred in what filters are being applied to the views. This simply records when filters are
	 * turned on or off and cannot respond to when specific details about a filter change because how those
	 * changes are recorded is up to the plugins, and not to the TagSEA platform. The detail will contain a
	 * list of filters that are currently turned on.
	 */
	public static final int FILTER = 2;
	
	public static final int VIEW_ACTIVATED = 0;
	public static final int VIEW_TOP = 1;
	public static final int VIEW_OPENED = 2;
	public static final int VIEW_DEACTIVATED =3;
	public static final int VIEW_HIDDEN = 4;
	public static final int VIEW_CLOSED = 5;
	public static final int VIEW_FILTERED = 6;
	public static final int VIEW_HIERARCHY = 7;
	
//	public static final String ACTIVATED_DETAIL = "Activated";
//	public static final String TOP_DETAIL = "Top";
//	public static final String OPENED_DETAIL = "Opened";
//	public static final String DEACTIVATED_DETAIL = "Deactivated";
//	public static final String HIDDEN_DETAIL = "Hidden";
//	public static final String CLOSED_DETAIL = "Closed";
//	public static final String FILTERED_DETAIL = "Filtered";
	
	public final int type;
	public final String detail;
	public final long time;
	
	public final IWaypoint waypoint;
	public final String viewId;

	public final int kind;
	
	
	/**
	 * Constructs a new UIEvent related to views.
	 * @param type
	 * @param kind
	 * @param viewId
	 * @param detail
	 */
	public static TagSEAUIEvent createViewEvent(int kind, String viewId, String detail) {
		return new TagSEAUIEvent(VIEW, kind, detail, System.currentTimeMillis(), null, viewId);
	}
	
	/**
	 * Constructs a new navigation event.
	 * @param type
	 * @param detail
	 * @param waypoint
	 */
	public static TagSEAUIEvent createNavigationEvent(IWaypoint waypoint) {
		return new TagSEAUIEvent(NAVIGATE, -1, null, System.currentTimeMillis(), waypoint, null);
	}
	
	public static TagSEAUIEvent createFilterEvent() {
		IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
		String waypointTypes = store.getString(ITagSEAPreferences.FILTERED_WAYPOINT_TYPES);
		return new TagSEAUIEvent(FILTER, -1, waypointTypes, System.currentTimeMillis(), null, null);
	}
	
	
	private TagSEAUIEvent(int type, int kind, String detail, long time, IWaypoint waypoint, String viewId) {
		this.type = type;
		this.kind = kind;
		this.detail =detail;
		this.time = time;
		this.waypoint = waypoint;
		this.viewId = viewId;
	}
}
