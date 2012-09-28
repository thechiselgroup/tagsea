/**
 * <copyright>
 * </copyright>
 *
 * $Id: NavigateEvent.java,v 1.1 2007/06/15 19:12:07 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Navigate Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.NavigateEvent#getWaypoint <em>Waypoint</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getNavigateEvent()
 * @model
 * @generated
 */
public interface NavigateEvent extends UIEvent {
	/**
	 * Returns the value of the '<em><b>Waypoint</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Waypoint</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Waypoint</em>' containment reference.
	 * @see #setWaypoint(WaypointState)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getNavigateEvent_Waypoint()
	 * @model containment="true" required="true"
	 * @generated
	 */
	WaypointState getWaypoint();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.NavigateEvent#getWaypoint <em>Waypoint</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Waypoint</em>' containment reference.
	 * @see #getWaypoint()
	 * @generated
	 */
	void setWaypoint(WaypointState value);

} // NavigateEvent