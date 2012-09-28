/**
 * <copyright>
 * </copyright>
 *
 * $Id: TaskNavigateEvent.java,v 1.1 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Task Navigate Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.TaskNavigateEvent#getDescription <em>Description</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.TaskNavigateEvent#getLine <em>Line</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.TaskNavigateEvent#getResource <em>Resource</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getTaskNavigateEvent()
 * @model
 * @generated
 */
public interface TaskNavigateEvent extends UIEvent {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getTaskNavigateEvent_Description()
	 * @model default="" required="true"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.TaskNavigateEvent#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Line</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Line</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Line</em>' attribute.
	 * @see #setLine(int)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getTaskNavigateEvent_Line()
	 * @model default="0" required="true"
	 * @generated
	 */
	int getLine();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.TaskNavigateEvent#getLine <em>Line</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Line</em>' attribute.
	 * @see #getLine()
	 * @generated
	 */
	void setLine(int value);

	/**
	 * Returns the value of the '<em><b>Resource</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resource</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resource</em>' attribute.
	 * @see #setResource(String)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getTaskNavigateEvent_Resource()
	 * @model default="" required="true"
	 * @generated
	 */
	String getResource();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.TaskNavigateEvent#getResource <em>Resource</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Resource</em>' attribute.
	 * @see #getResource()
	 * @generated
	 */
	void setResource(String value);

} // TaskNavigateEvent
