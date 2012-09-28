/**
 * <copyright>
 * </copyright>
 *
 * $Id: WaypointTagNameChangeEvent.java,v 1.1 2007/06/15 19:12:07 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Waypoint Tag Name Change Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent#getNewTagName <em>New Tag Name</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent#getOldTagName <em>Old Tag Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointTagNameChangeEvent()
 * @model
 * @generated
 */
public interface WaypointTagNameChangeEvent extends WaypointEvent {
	/**
	 * Returns the value of the '<em><b>New Tag Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>New Tag Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>New Tag Name</em>' attribute.
	 * @see #setNewTagName(String)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointTagNameChangeEvent_NewTagName()
	 * @model required="true"
	 * @generated
	 */
	String getNewTagName();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent#getNewTagName <em>New Tag Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>New Tag Name</em>' attribute.
	 * @see #getNewTagName()
	 * @generated
	 */
	void setNewTagName(String value);

	/**
	 * Returns the value of the '<em><b>Old Tag Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Old Tag Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Old Tag Name</em>' attribute.
	 * @see #setOldTagName(String)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getWaypointTagNameChangeEvent_OldTagName()
	 * @model required="true"
	 * @generated
	 */
	String getOldTagName();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent#getOldTagName <em>Old Tag Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Old Tag Name</em>' attribute.
	 * @see #getOldTagName()
	 * @generated
	 */
	void setOldTagName(String value);

} // WaypointTagNameChangeEvent