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
package ca.uvic.cs.tagsea.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.ui.views.WaypointsComposite;

/**
 * Class used to initialize default preference values.
 * 
 * @author Chris Callendar
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@SuppressWarnings("restriction")
	public void initializeDefaultPreferences() {
		IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
		
		//store.setDefault(TagSEAPreferences.KEY_ADD_TASK_TAG, true);
		store.setDefault(TagSEAPreferences.KEY_ADD_AUTHOR, false);
		store.setDefault(TagSEAPreferences.KEY_ADD_DATE, false);
		store.setDefault(TagSEAPreferences.KEY_SHOW_ROUTES, true);
		store.setDefault(TagSEAPreferences.KEY_AUTHOR_NAME, System.getProperty("user.name", "unknown"));
		
		// set the default column widths for the waypoints table
		store.setDefault(TagSEAPreferences.KEY_COLUMN_WIDTH + WaypointsComposite.INDEX_TAG_NAME, WaypointsComposite.WIDTH_TAG_NAME);
		store.setDefault(TagSEAPreferences.KEY_COLUMN_WIDTH + WaypointsComposite.INDEX_JAVA_ELEMENT, WaypointsComposite.WIDTH_JAVA_ELEMENT);
		store.setDefault(TagSEAPreferences.KEY_COLUMN_WIDTH + WaypointsComposite.INDEX_RESOURCE, WaypointsComposite.WIDTH_RESOURCE);
		store.setDefault(TagSEAPreferences.KEY_COLUMN_WIDTH + WaypointsComposite.INDEX_COMMENT, WaypointsComposite.WIDTH_COMMENT);
		store.setDefault(TagSEAPreferences.KEY_COLUMN_WIDTH + WaypointsComposite.INDEX_AUTHOR, WaypointsComposite.WIDTH_AUTHOR);
		store.setDefault(TagSEAPreferences.KEY_COLUMN_WIDTH + WaypointsComposite.INDEX_DATE, WaypointsComposite.WIDTH_DATE);
		store.setDefault(TagSEAPreferences.KEY_COLUMN_WIDTH + WaypointsComposite.INDEX_LINE_NUMBER, WaypointsComposite.WIDTH_LINE_NUMBER);
		
		//tagsea registration
		store.setDefault(TagSEAPreferences.KEY_USER_ID, -1);
		store.setDefault(TagSEAPreferences.KEY_ASKED_FOR_ID, false);
	}

}
