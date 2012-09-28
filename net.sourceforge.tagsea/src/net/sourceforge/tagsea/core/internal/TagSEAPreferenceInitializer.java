package net.sourceforge.tagsea.core.internal;

import net.sourceforge.tagsea.TagSEAPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class TagSEAPreferenceInitializer extends AbstractPreferenceInitializer {

	public TagSEAPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore prefs = TagSEAPlugin.getDefault().getPreferenceStore();
		prefs.setDefault(ITagSEAPreferences.WAYPOINT_VIEW_LINK_TO_TAGS_VIEW, true);
		prefs.setDefault(ITagSEAPreferences.TAGS_VIEW_TREE, true);
		prefs.setDefault(ITagSEAPreferences.TAGS_VIEW_TREE_NAMING, true);
		prefs.setDefault(ITagSEAPreferences.FILTERED_WAYPOINT_TYPES, "");
		prefs.setDefault(ITagSEAPreferences.FILTER_TAGS_TO_WAYPOINTS, false);
	}

}
