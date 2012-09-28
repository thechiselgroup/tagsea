/**
 * <copyright>
 * </copyright>
 *
 * $Id: ViewEvent.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>View Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.ViewEvent#getType <em>Type</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.ViewEvent#getViewid <em>Viewid</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.ViewEvent#getFilterString <em>Filter String</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getViewEvent()
 * @model
 * @generated
 */
public interface ViewEvent extends UIEvent {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * The literals are from the enumeration {@link net.sourceforge.tagsea.logging.ViewEventType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see net.sourceforge.tagsea.logging.ViewEventType
	 * @see #setType(ViewEventType)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getViewEvent_Type()
	 * @model default="" required="true"
	 * @generated
	 */
	ViewEventType getType();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.ViewEvent#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see net.sourceforge.tagsea.logging.ViewEventType
	 * @see #getType()
	 * @generated
	 */
	void setType(ViewEventType value);

	/**
	 * Returns the value of the '<em><b>Viewid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Viewid</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Viewid</em>' attribute.
	 * @see #setViewid(String)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getViewEvent_Viewid()
	 * @model required="true"
	 * @generated
	 */
	String getViewid();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.ViewEvent#getViewid <em>Viewid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Viewid</em>' attribute.
	 * @see #getViewid()
	 * @generated
	 */
	void setViewid(String value);

	/**
	 * Returns the value of the '<em><b>Filter String</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Filter String</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Filter String</em>' attribute.
	 * @see #setFilterString(String)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getViewEvent_FilterString()
	 * @model default=""
	 * @generated
	 */
	String getFilterString();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.ViewEvent#getFilterString <em>Filter String</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Filter String</em>' attribute.
	 * @see #getFilterString()
	 * @generated
	 */
	void setFilterString(String value);

} // ViewEvent