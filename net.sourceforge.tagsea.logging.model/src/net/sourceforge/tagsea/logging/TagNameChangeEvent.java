/**
 * <copyright>
 * </copyright>
 *
 * $Id: TagNameChangeEvent.java,v 1.1 2007/06/15 19:12:08 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tag Name Change Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.TagNameChangeEvent#getOldName <em>Old Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getTagNameChangeEvent()
 * @model
 * @generated
 */
public interface TagNameChangeEvent extends TagEvent {
	/**
	 * Returns the value of the '<em><b>Old Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Old Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Old Name</em>' attribute.
	 * @see #setOldName(String)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getTagNameChangeEvent_OldName()
	 * @model
	 * @generated
	 */
	String getOldName();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.TagNameChangeEvent#getOldName <em>Old Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Old Name</em>' attribute.
	 * @see #getOldName()
	 * @generated
	 */
	void setOldName(String value);

} // TagNameChangeEvent