/**
 * <copyright>
 * </copyright>
 *
 * $Id: WaypointState.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Waypoint State</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.WaypointState#getWaypointType <em>Waypoint Type</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.WaypointState#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.WaypointState#getTagNames <em>Tag Names</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointState()
 * @model
 * @generated
 */
public interface WaypointState extends EObject {
	/**
	 * Returns the value of the '<em><b>Waypoint Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Waypoint Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Waypoint Type</em>' attribute.
	 * @see #setWaypointType(String)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointState_WaypointType()
	 * @model required="true"
	 * @generated
	 */
	String getWaypointType();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.WaypointState#getWaypointType <em>Waypoint Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Waypoint Type</em>' attribute.
	 * @see #getWaypointType()
	 * @generated
	 */
	void setWaypointType(String value);

	/**
	 * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
	 * The list contents are of type {@link net.sourceforge.tagsea.logging.WaypointAttribute}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attributes</em>' containment reference list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointState_Attributes()
	 * @model type="net.sourceforge.tagsea.logging.WaypointAttribute" containment="true" required="true"
	 * @generated
	 */
	EList getAttributes();

	/**
	 * Returns the value of the '<em><b>Tag Names</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Tag Names</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tag Names</em>' attribute list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointState_TagNames()
	 * @model
	 * @generated
	 */
	EList getTagNames();

} // WaypointState