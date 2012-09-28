/**
 * <copyright>
 * </copyright>
 *
 * $Id: WaypointChangeEvent.java,v 1.1 2007/06/15 19:12:07 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Waypoint Change Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.WaypointChangeEvent#getNewAttributes <em>New Attributes</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.WaypointChangeEvent#getOldAttributes <em>Old Attributes</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointChangeEvent()
 * @model
 * @generated
 */
public interface WaypointChangeEvent extends WaypointEvent {
	/**
	 * Returns the value of the '<em><b>New Attributes</b></em>' containment reference list.
	 * The list contents are of type {@link net.sourceforge.tagsea.logging.WaypointAttribute}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>New Attributes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>New Attributes</em>' containment reference list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointChangeEvent_NewAttributes()
	 * @model type="net.sourceforge.tagsea.logging.WaypointAttribute" containment="true"
	 * @generated
	 */
	EList getNewAttributes();

	/**
	 * Returns the value of the '<em><b>Old Attributes</b></em>' containment reference list.
	 * The list contents are of type {@link net.sourceforge.tagsea.logging.WaypointAttribute}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Old Attributes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Old Attributes</em>' containment reference list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointChangeEvent_OldAttributes()
	 * @model type="net.sourceforge.tagsea.logging.WaypointAttribute" containment="true"
	 * @generated
	 */
	EList getOldAttributes();

} // WaypointChangeEvent