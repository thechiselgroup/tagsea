package net.sourceforge.tagsea.resources;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class ResourceWaypointPreferences extends AbstractPreferenceInitializer {
	/**
	 * Represents the filter types for the resource waypoints custom waypoint filter.
	 * @author Del Myers
	 *
	 */
	
	public static final String FILTER_PREFERENCE_KEY = "resourceWaypointFilter"; //$NON-NLS-1$
	public static final String FILTER_ANY = "ANY"; //$NON-NLS-1$
	public static final String FILTER_SELECTED = "SELECTED"; //$NON-NLS-1$
	public static final String FILTER_PROJECT = "PROJECT"; //$NON-NLS-1$
	public static final String FILTER_CHILDREN = "CHILDREN"; //$NON-NLS-1$
//	@Deprecated
//	public static final String SHARE_WAYPOINTS_KEY = "SHARE_WAYPOINTS"; //$NON-NLS-1$
//	@Deprecated
//	public static final QualifiedName PROJECT_REVISION_KEY = new QualifiedName(ResourceWaypointPlugin.PLUGIN_ID, "PROJECT_WAYPOINT_REVISION"); //$NON-NLS-1$
//	@Deprecated
//	private static final QualifiedName PROJECT_SPECIFIC_KEY = new QualifiedName(ResourceWaypointPlugin.PLUGIN_ID, "PROJECT_SPECIFIC"); //$NON-NLS-1$
//	@Deprecated
//	private static final QualifiedName SHARE_WAYPOINTS_PROJECT_KEY = new QualifiedName(ResourceWaypointPlugin.PLUGIN_ID, SHARE_WAYPOINTS_KEY);
	
	public ResourceWaypointPreferences() {
	}

	@Override
	public void initializeDefaultPreferences() {
		ResourceWaypointPlugin.getDefault().getPreferenceStore().setDefault(FILTER_PREFERENCE_KEY, FILTER_ANY);
//		ResourceWaypointPlugin.getDefault().getPreferenceStore().setDefault(SHARE_WAYPOINTS_KEY, false);
	}
	
	public static String getCurrentFilterType() {
		String value = ResourceWaypointPlugin.getDefault().getPreferenceStore().getString(FILTER_PREFERENCE_KEY);
		return value;
	}
	/*
	@Deprecated
	public static boolean hasProjectSpecificSettings(IProject project) {
		try {
			return project.getPersistentProperty(PROJECT_SPECIFIC_KEY) != null;
		} catch (CoreException e) {
		}
		return false;
	}
	
	@Deprecated
	public static void setHasProjectSpecificSettings(IProject project, boolean hasProjectSpecificSettings) {
		String value = (hasProjectSpecificSettings) ? "true" : null;  //$NON-NLS-1$
		try {
			project.setPersistentProperty(PROJECT_SPECIFIC_KEY, value);
		} catch (CoreException e) {
		}
	}
	
	@Deprecated
	public static boolean doesShareWaypoints(IProject project) {
		if (hasProjectSpecificSettings(project)) {
			try {
				return project.getPersistentProperty(SHARE_WAYPOINTS_PROJECT_KEY) != null;
			} catch (CoreException e) {
			}
		}
		return ResourceWaypointPlugin.getDefault().getPreferenceStore().getBoolean(SHARE_WAYPOINTS_KEY);
	}
	
	@Deprecated
	public static void setShareWaypoints(IProject project, boolean share) {
		String value = (share) ? "true" : null;  //$NON-NLS-1$
		try {
			project.setPersistentProperty(SHARE_WAYPOINTS_PROJECT_KEY, value);
		} catch (CoreException e) {
		}
	}


	@Deprecated
	public static void setRevision(IProject project, long timestamp) {
		try {
			project.setPersistentProperty(PROJECT_REVISION_KEY, ""+timestamp);  //$NON-NLS-1$
		} catch (CoreException e) {
		}		
	}

	/**
	 * Returns the last revison saved for the given project, if the waypoints in that project are being shared.
	 * -1 otherwise.
	 * @param project the project to check.
	 * @return the last revison saved for the given project.
	 *
	@Deprecated
	public static long getRevision(IProject project) {
		long result = -1;
		if (!doesShareWaypoints(project)) return result;
		try {
			String value = project.getPersistentProperty(PROJECT_REVISION_KEY);
			if (value != null) {
				result = Long.parseLong(value);
			}
		} catch (NumberFormatException e) {
			ResourceWaypointPlugin.getDefault().log(e);
		} catch (CoreException e) {
			ResourceWaypointPlugin.getDefault().log(e);
		}
		return result;
	}
	*/
	
}
