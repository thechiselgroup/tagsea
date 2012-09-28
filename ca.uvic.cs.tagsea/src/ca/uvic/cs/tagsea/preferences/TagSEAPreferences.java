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

import org.eclipse.jface.preference.IPreferenceStore;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * Constant definitions for the TagSEA plug-in preferences.
 * 
 * @see PreferenceInitializer
 * @see TagSEAPreferencePage
 * @author Chris Callendar
 */
public class TagSEAPreferences {
	
	//public static final String KEY_ADD_TASK_TAG	= "tagsea.add.tasktag";
	public static final String KEY_ADD_AUTHOR 	= "tagsea.add.author";
	public static final String KEY_ADD_DATE 	= "tagsea.add.date";
	public static final String KEY_SHOW_ROUTES 	= "tagsea.show.routes";
	public static final String KEY_AUTHOR_NAME	= "tagsea.author.name";
	
	public static final String KEY_SASH_WEIGHT_TAGS			= "tagsea.sash.weight.tags";
	public static final String KEY_SASH_WEIGHT_RIGHT		= "tagsea.sash.weight.rightsash";
	public static final String KEY_SASH_WEIGHT_WAYPOINTS	= "tagsea.sash.weight.waypoints";
	public static final String KEY_SASH_WEIGHT_ROUTES		= "tagsea.sash.weight.routes";
	public static final String KEY_COLUMN_WIDTH				= "tagsea.columnwidths.";

	public static final int DEFAULT_COLUMN_WIDTH = 100;
	public static final int MINIMUM_COLUMN_WIDTH = 4;
	
	public static final String KEY_USER_ID = "tagsea.user.id";
	public static final String KEY_ASKED_FOR_ID = "tagsea.user.id.asked";
	
	public static IPreferenceStore getPreferenceStore() {
		return TagSEAPlugin.getDefault().getPreferenceStore();
	}
	
	/**
	 * Checks the preferences to see if the (@)tag compiler task tag
	 * should be registered automatically on plug-in startup.
	 * @return boolean
	 */
//	public static boolean isRegisterTaskTag() {
//		return getPreferenceStore().getBoolean(KEY_ADD_TASK_TAG);
//	}
	
	/**
	 * Checks the preferences to see if the add author tag preference is set.
	 * @return boolean
	 */
	public static boolean isAddAuthor() {
		return getPreferenceStore().getBoolean(KEY_ADD_AUTHOR);
	}
	
	public static boolean isAddDate() {
		return getPreferenceStore().getBoolean(KEY_ADD_DATE);
	}
	
	@SuppressWarnings("restriction")
	public static String getAuthor() {
		String author = getPreferenceStore().getString(KEY_AUTHOR_NAME);
		if (author == null) {
			author = getPreferenceStore().getDefaultString(KEY_AUTHOR_NAME);
		}
		if (author == null) {
			author = System.getProperty("user.name");
		}
		return author;
	}

//	public static boolean isShowRoutes() {
//		return getPreferenceStore().getBoolean(KEY_SHOW_ROUTES);
//	}

	/**
	 * Sets the preference for whether the routes tree is shown.
	 * @param showRoutes if the routes tree should be shown
	 */
//	public static void setShowRoutes(boolean showRoutes) {
//		getPreferenceStore().setValue(KEY_SHOW_ROUTES, showRoutes);
//	}	
	
	//////////////////////////////
	// COLUMN WIDTH ACCESSORS
	//////////////////////////////
	
	/**
	 * Save the column widths.
	 * Note that if the minimum width for any column is <b>4</b>.  This is done so that
	 * when the table columns are created, instead of being hidden (width = 0)
	 * the will now just be visible and can be resized. 
	 */
	public static void setColumnWidths(int[] columnWidths) {
		int i = 0;
		for (int width : columnWidths) {
			String key = KEY_COLUMN_WIDTH + i;
			setColumnWidth(key, width);
			i++;
		}
	}
	
	private static void setColumnWidth(String key, int width) {
		/* 
		 * Use a minimum width of 4, this way when the view is 
		 * created again the columns which had a width of 0 will be
		 * shown to have a width of 4 and can easily be expanded.
		 */		
		width = Math.max(MINIMUM_COLUMN_WIDTH, width);
		getPreferenceStore().setValue(key, width);
	}
	
	/**
	 * Gets the width for the key.  If the value isn't set in the preferences
	 * then the default value is returned.  
	 * @param key
	 * @return int the width
	 */
	private static int getWidth(String key) {
		IPreferenceStore store = getPreferenceStore();
		if (store.contains(key)) {
			return store.getInt(key);
		} else {
			return store.getDefaultInt(key);
		}
	}
	
	/**
	 * Gets the width for the given column number (starting at 0).
	 * @param columnNumber the column index starting at 0
	 * @return int the width, will always be at least 4
	 */
	public static int getColumnWidth(int columnNumber) {
		int width = getWidth(KEY_COLUMN_WIDTH + columnNumber);
		if (width < MINIMUM_COLUMN_WIDTH) {
			width = DEFAULT_COLUMN_WIDTH;
		}
		return width;		
	}

	/**
	 * Sets the Tags SashForm weights.
	 * @param weights the sashform weights
	 */
	public static void setTagsSashWeights(int[] weights) {
		getPreferenceStore().setValue(KEY_SASH_WEIGHT_TAGS, weights[0]);
		getPreferenceStore().setValue(KEY_SASH_WEIGHT_RIGHT, weights[1]);
	}
	
	/**
	 * Gets the SashForm weights for the Tags viewer
	 * @return int[2] the weights or null if not defined
	 */
	public static int[] getTagsSashWeights() {
		int[] weights = null;
		if (getPreferenceStore().contains(KEY_SASH_WEIGHT_TAGS) &&
			getPreferenceStore().contains(KEY_SASH_WEIGHT_RIGHT)) {
			weights = new int[2];
			weights[0] = getPreferenceStore().getInt(KEY_SASH_WEIGHT_TAGS);
			weights[1] = getPreferenceStore().getInt(KEY_SASH_WEIGHT_RIGHT);
		}
		return weights;
	}


	/**
	 * Sets the Tags SashForm weights.
	 * @param weights the sashform weights
	 */
	public static void setWaypointsSashWeights(int[] weights) 
	{
		getPreferenceStore().setValue(KEY_SASH_WEIGHT_WAYPOINTS, weights[0]);
	}
	
	/**
	 * Gets the SashForm weights for the Waypoints and Routes viewers
	 * @return int[2] the weights or null if not defined
	 */
	public static int[] getWaypointsSashWeights()
	{
		int[] weights = null;
		
		if (getPreferenceStore().contains(KEY_SASH_WEIGHT_WAYPOINTS))
		{
			weights = new int[1];
			weights[0] = getPreferenceStore().getInt(KEY_SASH_WEIGHT_WAYPOINTS);
		}
		return weights;
	}
	
	public static int getUserID() {
		int id = getPreferenceStore().getInt(KEY_USER_ID);
		return id;
	}
	
	public static void setUserID(int id) {
		getPreferenceStore().setValue(KEY_USER_ID, id);
	}

	/**
	 * @param b
	 */
	public static void setAskedForID(boolean b) {
		getPreferenceStore().setValue(KEY_ASKED_FOR_ID, b);
	}
	
	public static boolean askedForID() {
		return getPreferenceStore().getBoolean(KEY_ASKED_FOR_ID);
	}
}
