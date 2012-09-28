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
package net.sourceforge.tagsea.core;

import java.util.Date;

import net.sourceforge.tagsea.TagSEAModelException;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Waypoints represent "places" that have
 * been "tagged" as interesting. When a digital artifact (source code, file, url, etc.)
 * has been deemed as interesting, the user can define a waypoint on that artifact, and
 * supply a tag name for the waypoint. The artifact will then be associated with that tag
 * name. Tags cannot exist without waypoints.
 * 
 * Waypoints may be created or deleted at arbitrary times in the workbench, so implementors
 * should not expect referential integrity of waypoints.
 * 
 * Waypoints extend IAdaptable so that workbench label providers and content
 * providers can be used to display them.
 * 
 * Waypoints also adapt to IActionFilter so that they can trigger additions of actions to context
 * menus and toolbars. There are two "special" attribute names that are used for such cases:
 * 
 * TYPE_FILTER_ATTRIBUTE
 * SUBTYPE_FILTER_ATTRIBUTE
 * 
 * These are used for the org.eclipse.ui.popupMenus extension point for the inclusion or
 * exclusion of an action from a menu based on the waypoint type. For example:
 * 
 * <pre>
 * &lt;extension point="org.eclipse.ui.popupMenus"&gt; 
 *     &lt;objectContribution 
 *        id="com.abc.def" 
 *        objectClass="net.sourceforge.tagsea.IWaypoint"
 *        adaptable="true"&gt;
 *        &lt;visibility&gt;
 *           &lt;and&gt;
 *              &lt;objectState name="SUBTYPE_FILTER_ATTRIBUTE" value="com.abc.def.mywaypoint"/&gt;
 *           &lt;/and&gt;
 *        &lt;/visibility&gt;
 *        &lt;action
 *           id="com.abc.def.MyAction" 
 *           label="An Action"
 *           style="push"
 *           menubarPath="additions" 
 *           icon="icons/action.gif" 
 *           class="com.abc.def.MyActionDelegate"&gt; 
 *        &lt;/action&gt;
 *     &lt;/viewerContribution&gt; 
 *  &lt;/extension&gt; 
 *</pre>
 * 
 * will contribute the action MyActionDelegate to popup menus for selections that include objects that
 * adapt to waypoints that are of type, or a subtype of "com.abc.def.mywaypoint".
 * 
 * Because of this special use of these two names for filtering, it is recommended that waypoints do not
 * use these names for attributes, as it can cause unexpected results for popup menus.
 * @author Del Myers
 * @see ITag
 */

public interface IWaypoint extends IAdaptable {
	public static final String BASE_WAYPOINT = "net.sourceforge.tagsea.base";
	public static final String TEXT_WAYPOINT = "net.sourceforge.tagsea.text";
	
	/**
	 * Default author attribute. Found on all waypoints.
	 */
	public static final String ATTR_AUTHOR = "author"; //$NON-NLS-1$
	
	/**
	 * Default text attribute. Found on all waypoints.
	 */
	public static final String ATTR_MESSAGE = "message"; //$NON-NLS-1$
	
	/**
	 * Default date attribute. Found on all waypoints.
	 */
	public static final String ATTR_DATE = "date"; //$NON-NLS-1$

	/**
	 * Special attribute name for use when adapting to IActionFilters. It is recommended that 
	 * clients don't use this as a name for attributes on their custom wayponts.
	 */
	public static final String TYPE_FILTER_ATTRIBUTE = "TYPE_FILTER_ATTRIBUTE"; //$NON-NLS-1$
	
	/**
	 * Special attribute name for use when adapting to IActionFilters. It is recommended that 
	 * clients don't use this as a name for attributes on their custom wayponts.
	 */
	public static final String SUBTYPE_FILTER_ATTRIBUTE = "TYPE_FILTER_ATTRIBUTE"; //$NON-NLS-1$
	
	/**
	 * Adds the tag of the given tag name to the list of tags, if it doesn't already exist.
	 * The tag tag was added is returned for convenience.
	 * @param tagName the name of the tag to add.
	 * @return the tag added.
	 */
	ITag addTag(String tagName);
	
	/**
	 * Removes the given tag from the list of tags. Returns true if the tag existed in the
	 * list.
	 * @param tag the tag to remove from the list of tags.
	 * @return the status of the removal.
	 */
	TagSEAChangeStatus removeTag(ITag tag);
	
	/**
	 * Returns the author metadata on this waypoint, or the empty string if none. This
	 * is a convenience method for (String)getValue(IWaypoint.ATTR_AUTHOR).
	 * @return the author metadata on this waypoint.
	 */
	String getAuthor();
	
	/**
	 * Sets the bsic author attribute on this waypoint. This is a convenience method
	 * for (String)setString(IWaypoint.ATTR_AUTHOR, author).
	 * @param author the name of the author.
	 */
	TagSEAChangeStatus setAuthor(String author);
	
	/**
	 * Returns the basic text attribute on this waypoint. This is a convenience method
	 * for (String)getValue(IWaypoint.ATTR_TEXT).
	 * @return the text.
	 */
	String getText();
	
	/**
	 * Sets the basic text attribute on this waypoint. This is a convenience method
	 * for setStringValue(IWaypoint.ATTR_TEXT, text).
	 * @param text the text to set.
	 */
	TagSEAChangeStatus setText(String text);
	
	/**
	 * Returns the date that the waypoint was made, if applicable. Null otherwise.
	 * This method has special functionality in that if the date that is stored as
	 * an attribute is set to the default value, this method assumes that the date
	 * has not been properly set, and will return null.
	 * @return the date.
	 */
	Date getDate();
	
	/**
	 * Sets the basic date attribute on this waypoint. This is a convenience method
	 * for setDateValue(IWaypoint.ATTR_DATE, date).
	 * @param date the value of the date to set.
	 */
	TagSEAChangeStatus setDate(Date date);
	
	/**
	 * Returns the unique identifier for the type of this waypoint. The unique identifier
	 * consists of the plugin id of the declaring plugin for this type and the simple type
	 * name as set in the declaring plugin.
	 * @return the unique identifier for the type of this waypoint.
	 */
	String getType();
	
	/**
	 * Returns the value for the attribute of the given name, or null if no such attribute
	 * exists. The type returned is guaranteed to be one of String, Integer, Date, or Boolean.
	 * @param name the name of the attribute.
	 * @return the value for the attribute of the given name, or null.
	 */
	Object getValue(String name);
	
	/**
	 * Returns the attribute names declared for this type of waypoint. 
	 * @return the attributes names declared for this type of waypoint.
	 */
	String[] getAttributes();
	
	/**
	 * Returns the String value of the given attribute, or defaultValue if it doesn't exist.
	 * @param name the name of the attribute.
	 * @param defaultValue the default value to return if it doesn't exist.
	 * @return the String value of the given attribute, or defaultValue if it doesn't exist.
	 */
	String getStringValue(String name, String defaultValue);
	
	/**
	 * Returns the int value of the given attribute, or defaultValue if it doesn't exist.
	 * @param name the name of the attribute.
	 * @param defaultValue the default value to return if it doesn't exist.
	 * @return the String value of the given attribute, or defaultValue if it doesn't exist.
	 */
	int getIntValue(String name, int defaultValue);
	
	/**
	 * Returns the boolean value of the given attribute, or defaultValue if it doesn't exist.
	 * @param name the name of the attribute.
	 * @param defaultValue the default value to return if it doesn't exist.
	 * @return the String value of the given attribute, or defaultValue if it doesn't exist.
	 */
	boolean getBooleanValue(String name, boolean defaultValue);
	
	/**
	 * Returns the date value of the given attribute, or defaultValue if it doesn't exist.
	 * @param name the name of the attribute.
	 * @param defaultValue the default value to return if it doesn't exist.
	 * @return the Date value of the given attribute, or defaultValue if it doesn't exist.
	 */
	Date getDateValue(String name, Date defaultValue);
	
	
	/**
	 * Sets the attribute of the given name to the given String value. This method will block
	 * until all currently running ITagSEAOperations are finished running, at which point the
	 * change may be obsolete. If the change is obsolete and could not be completed, then
	 * this method will return false.
	 * @param name the attribute to set.
	 * @param value the value to set it to.
	 * @return the status indicating whether or not the change could occurr.
	 * @throws TagSEAModelException if the declared type for the given attribute is not a String.
	 * @see TagSEAOperation
	 */
	TagSEAChangeStatus setStringValue(String name, String value);
	
	/**
	 * Sets the attribute of the given name to the given boolean value. This method will block
	 * until all currently running ITagSEAOperations are finished running, at which point the
	 * change may be obsolete. If the change is obsolete and could not be completed, then
	 * this method will return false.
	 * @param name the attribute to set.
	 * @param value the value to set it to.
	 * @return the status indicating whether or not the change could occurr.
	 * @throws TagSEAModelException if the declared type for the given attribute is not a boolean.
	 * @see TagSEAOperation
	 */
	TagSEAChangeStatus setBooleanValue(String name, boolean value);
	
	/**
	 * Sets the attribute of the given name to the given int value. This method will block
	 * until all currently running ITagSEAOperations are finished running, at which point the
	 * change may be obsolete. If the change is obsolete and could not be completed, then
	 * this method will return false.
	 * @param name the attribute to set.
	 * @param value the value to set it to.
	 * @return the status indicating whether or not the change could occurr.
	 * @throws TagSEAModelException if the declared type for the given attribute is not an int.
	 * @see TagSEAOperation
	 */
	TagSEAChangeStatus setIntValue(String name, int value);
	
	/**
	 * Sets the attribute of the given name to the given Object value. This method will block
	 * until all currently running ITagSEAOperations are finished running, at which point the
	 * change may be obsolete. If the change is obsolete and could not be completed, then
	 * this method will return false.
	 * @param name the attribute to set.
	 * @param value the value to set it to.
	 * @return the status indicating whether or not the change could occurr.
	 * @throws TagSEAModelException if the declared type for the given attribute does not match the
	 * underlying type of the value Object.
	 * @see TagSEAOperation
	 */
	TagSEAChangeStatus setObjectValue(String name, Object value);
	
	/**
	 * Sets the attribute of the given name to the given Date value. This method will block
	 * until all currently running ITagSEAOperations are finished running, at which point the
	 * change may be obsolete. If the change is obsolete and could not be completed, then
	 * this method will return false.
	 * @param name the attribute to set.
	 * @param value the value to set it to.
	 * @return the status indicating whether or not the change could occurr.
	 * @throws TagSEAModelException if the declared type for the given attribute does not match the
	 * underlying type of the value Object.
	 * @see TagSEAOperation
	 */
	TagSEAChangeStatus setDateValue(String name, Date value);
	
	/**
	 * Returns the tags associated with this waypoint.
	 * @return the tags associated with this waypoint.
	 */
	ITag[] getTags();
	
	/**
	 * Returns true if this waypoint still exists and is accessable from the waypoints model;
	 * false if the reference is no longer valid.
	 * @return true if this waypoint still exists and is accessable from the waypoints model;
	 * false if the reference is no longer valid.
	 */
	boolean exists();
	
	/**
	 * Checks to see if the given waypoint type is a parent of this type.
	 * @param parentType the type to check. If this waypoint type is a subtype of the given type,
	 * it can be guaranteed that this type has all of the attributes of the parent.
	 * @return whether or not the given waypoint type is a parent of this type.
	 */
	boolean isSubtypeOf(String parentType);
	
	/**
	 * Removes this waypoint from the waypoints model if it exists there.
	 * @return the status of the delete.
	 *
	 */
	TagSEAChangeStatus delete();
	
	
}
