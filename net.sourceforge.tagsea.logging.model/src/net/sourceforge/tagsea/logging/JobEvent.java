/**
 * <copyright>
 * </copyright>
 *
 * $Id: JobEvent.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Job Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.JobEvent#getState <em>State</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.JobEvent#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getJobEvent()
 * @model
 * @generated
 */
public interface JobEvent extends ModelEvent {
	/**
	 * Returns the value of the '<em><b>State</b></em>' attribute.
	 * The default value is <code>"created"</code>.
	 * The literals are from the enumeration {@link net.sourceforge.tagsea.logging.JobState}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>State</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>State</em>' attribute.
	 * @see net.sourceforge.tagsea.logging.JobState
	 * @see #setState(JobState)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getJobEvent_State()
	 * @model default="created" required="true"
	 * @generated
	 */
	JobState getState();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.JobEvent#getState <em>State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>State</em>' attribute.
	 * @see net.sourceforge.tagsea.logging.JobState
	 * @see #getState()
	 * @generated
	 */
	void setState(JobState value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getJobEvent_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.JobEvent#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // JobEvent