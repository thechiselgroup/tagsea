/**
 * <copyright>
 * </copyright>
 *
 * $Id: Event.java,v 1.1 2007/06/15 19:12:07 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.Event#getTime <em>Time</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getEvent()
 * @model abstract="true"
 * @generated
 */
public interface Event extends EObject {
	/**
	 * Returns the value of the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Time</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time</em>' attribute.
	 * @see #setTime(long)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getEvent_Time()
	 * @model required="true"
	 * @generated
	 */
	long getTime();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.Event#getTime <em>Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Time</em>' attribute.
	 * @see #getTime()
	 * @generated
	 */
	void setTime(long value);

} // Event