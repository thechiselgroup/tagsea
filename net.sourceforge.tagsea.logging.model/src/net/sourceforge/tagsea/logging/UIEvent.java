/**
 * <copyright>
 * </copyright>
 *
 * $Id: UIEvent.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>UI Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.UIEvent#isHierarchyOn <em>Hierarchy On</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getUIEvent()
 * @model abstract="true"
 * @generated
 */
public interface UIEvent extends Event {
	/**
	 * Returns the value of the '<em><b>Hierarchy On</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Hierarchy On</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hierarchy On</em>' attribute.
	 * @see #setHierarchyOn(boolean)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getUIEvent_HierarchyOn()
	 * @model default="false"
	 * @generated
	 */
	boolean isHierarchyOn();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.UIEvent#isHierarchyOn <em>Hierarchy On</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Hierarchy On</em>' attribute.
	 * @see #isHierarchyOn()
	 * @generated
	 */
	void setHierarchyOn(boolean value);

} // UIEvent