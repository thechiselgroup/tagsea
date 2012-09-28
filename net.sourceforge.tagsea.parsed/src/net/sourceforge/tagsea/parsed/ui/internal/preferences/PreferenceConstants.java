package net.sourceforge.tagsea.parsed.ui.internal.preferences;


/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	/**
	 * space-separated list of registered definitions, by kind id. If the returned value
	 * starts with a '+', it has been registered. If it starts with '-', it has been unregistered.
	 * Kind id's that don't appear in the preferences should be considered to be newly
	 * contributed and should be registered.
	 */
	public static final String REGISTERED_DEFINITIONS = "REGISTERED_DEFINITIONS";
	public static final String LINKED_TO_RESOURCE_WAYPOINT_FILTERS = "LINKED-T0-RWF";
	public static final String FILTERED_KINDS = "FILTERED-KINDS";	
}
