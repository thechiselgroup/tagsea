package net.sourceforge.tagsea.parsed.ui.internal.preferences;

import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointPreferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = ParsedWaypointPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.LINKED_TO_RESOURCE_WAYPOINT_FILTERS, true);
		store.setDefault(ResourceWaypointPreferences.FILTER_PREFERENCE_KEY, ResourceWaypointPreferences.FILTER_PROJECT);
		store.setDefault(PreferenceConstants.FILTERED_KINDS, "");
	}

}
