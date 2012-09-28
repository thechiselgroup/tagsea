/**
 * <copyright>
 * </copyright>
 *
 * $Id: WaypointTagsChangedEvent.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Waypoint Tags Changed Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.WaypointTagsChangedEvent#getOldTags <em>Old Tags</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointTagsChangedEvent()
 * @model
 * @generated
 */
public interface WaypointTagsChangedEvent extends WaypointEvent {
	/**
	 * Returns the value of the '<em><b>Old Tags</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Old Tags</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Old Tags</em>' attribute list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointTagsChangedEvent_OldTags()
	 * @model
	 * @generated
	 */
	EList getOldTags();

} // WaypointTagsChangedEvent