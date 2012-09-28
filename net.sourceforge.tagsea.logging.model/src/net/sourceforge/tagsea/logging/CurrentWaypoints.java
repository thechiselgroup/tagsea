/**
 * <copyright>
 * </copyright>
 *
 * $Id: CurrentWaypoints.java,v 1.1 2007/06/15 19:12:07 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Current Waypoints</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.CurrentWaypoints#getWaypoint <em>Waypoint</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getCurrentWaypoints()
 * @model
 * @generated
 */
public interface CurrentWaypoints extends Log {
	/**
	 * Returns the value of the '<em><b>Waypoint</b></em>' containment reference list.
	 * The list contents are of type {@link net.sourceforge.tagsea.logging.WaypointState}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Waypoint</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Waypoint</em>' containment reference list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getCurrentWaypoints_Waypoint()
	 * @model type="net.sourceforge.tagsea.logging.WaypointState" containment="true"
	 * @generated
	 */
	EList getWaypoint();

} // CurrentWaypoints