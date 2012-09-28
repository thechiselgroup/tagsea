/**
 * <copyright>
 * </copyright>
 *
 * $Id: TagWaypointsChangedEvent.java,v 1.1 2007/06/15 19:12:07 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tag Waypoints Changed Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.TagWaypointsChangedEvent#getOldWaypoint <em>Old Waypoint</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.TagWaypointsChangedEvent#getNewWaypoint <em>New Waypoint</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getTagWaypointsChangedEvent()
 * @model
 * @generated
 */
public interface TagWaypointsChangedEvent extends TagEvent {
	/**
	 * Returns the value of the '<em><b>Old Waypoint</b></em>' containment reference list.
	 * The list contents are of type {@link net.sourceforge.tagsea.logging.WaypointState}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Old Waypoint</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Old Waypoint</em>' containment reference list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getTagWaypointsChangedEvent_OldWaypoint()
	 * @model type="net.sourceforge.tagsea.logging.WaypointState" containment="true"
	 * @generated
	 */
	EList getOldWaypoint();

	/**
	 * Returns the value of the '<em><b>New Waypoint</b></em>' containment reference list.
	 * The list contents are of type {@link net.sourceforge.tagsea.logging.WaypointState}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>New Waypoint</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>New Waypoint</em>' containment reference list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getTagWaypointsChangedEvent_NewWaypoint()
	 * @model type="net.sourceforge.tagsea.logging.WaypointState" containment="true"
	 * @generated
	 */
	EList getNewWaypoint();

} // TagWaypointsChangedEvent