/**
 * <copyright>
 * </copyright>
 *
 * $Id: FiltersChangedEvent.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Filters Changed Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.FiltersChangedEvent#getVisible <em>Visible</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.FiltersChangedEvent#getHidden <em>Hidden</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getFiltersChangedEvent()
 * @model
 * @generated
 */
public interface FiltersChangedEvent extends UIEvent {
	/**
	 * Returns the value of the '<em><b>Visible</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Visible</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Visible</em>' attribute list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getFiltersChangedEvent_Visible()
	 * @model
	 * @generated
	 */
	EList getVisible();

	/**
	 * Returns the value of the '<em><b>Hidden</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Hidden</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hidden</em>' attribute list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getFiltersChangedEvent_Hidden()
	 * @model
	 * @generated
	 */
	EList getHidden();

} // FiltersChangedEvent