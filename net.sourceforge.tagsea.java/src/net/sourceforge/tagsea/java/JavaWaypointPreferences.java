package net.sourceforge.tagsea.java;

import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointPreferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class JavaWaypointPreferences extends AbstractPreferenceInitializer {
	/**
	 * Represents the filter types for the resource waypoints custom waypoint filter.
	 * @author Del Myers
	 *
	 */
	
	public static final String FILTER_PREFERENCE_KEY = ResourceWaypointPreferences.FILTER_PREFERENCE_KEY;
	public static final String LINK_TO_RESOURCES_KEY = "linkeToResources";
	public static final String FILTER_ANY = ResourceWaypointPreferences.FILTER_ANY;
	public static final String FILTER_SELECTED = ResourceWaypointPreferences.FILTER_SELECTED;
	public static final String FILTER_PROJECT = ResourceWaypointPreferences.FILTER_PROJECT;
	public static final String FILTER_CHILDREN = ResourceWaypointPreferences.FILTER_CHILDREN;
	
	
	public JavaWaypointPreferences() {
	}

	@Override
	public void initializeDefaultPreferences() {
		JavaTagsPlugin.getDefault().getPreferenceStore().setDefault(FILTER_PREFERENCE_KEY, FILTER_ANY);
		JavaTagsPlugin.getDefault().getPreferenceStore().setDefault(LINK_TO_RESOURCES_KEY, true);
	}
	
	public static String getCurrentFilterType() {
		if (!isFilterLinkedToResources()) {
			return JavaTagsPlugin.getDefault().getPreferenceStore().getString(FILTER_PREFERENCE_KEY);
		}
		return ResourceWaypointPlugin.getDefault().getPreferenceStore().getString(FILTER_PREFERENCE_KEY);
	}
	
	public static boolean isFilterLinkedToResources() {
		return JavaTagsPlugin.getDefault().getPreferenceStore().getBoolean(LINK_TO_RESOURCES_KEY);
	}

}
