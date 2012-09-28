/**
 * <copyright>
 * </copyright>
 *
 * $Id: FiltersChangedEventImpl.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import java.util.Collection;

import net.sourceforge.tagsea.logging.FiltersChangedEvent;
import net.sourceforge.tagsea.logging.LoggingPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.BasicInternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Filters Changed Event</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.FiltersChangedEventImpl#getVisible <em>Visible</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.FiltersChangedEventImpl#getHidden <em>Hidden</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FiltersChangedEventImpl extends UIEventImpl implements FiltersChangedEvent {
	/**
	 * The cached value of the '{@link #getVisible() <em>Visible</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVisible()
	 * @generated
	 * @ordered
	 */
	protected EList visible;

	/**
	 * The cached value of the '{@link #getHidden() <em>Hidden</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHidden()
	 * @generated
	 * @ordered
	 */
	protected EList hidden;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FiltersChangedEventImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.FILTERS_CHANGED_EVENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getVisible() {
		if (visible == null) {
			visible = new BasicInternalEList(String.class);
		}
		return visible;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getHidden() {
		if (hidden == null) {
			hidden = new BasicInternalEList(String.class);
		}
		return hidden;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LoggingPackage.FILTERS_CHANGED_EVENT__VISIBLE:
				return getVisible();
			case LoggingPackage.FILTERS_CHANGED_EVENT__HIDDEN:
				return getHidden();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case LoggingPackage.FILTERS_CHANGED_EVENT__VISIBLE:
				getVisible().clear();
				getVisible().addAll((Collection)newValue);
				return;
			case LoggingPackage.FILTERS_CHANGED_EVENT__HIDDEN:
				getHidden().clear();
				getHidden().addAll((Collection)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
			case LoggingPackage.FILTERS_CHANGED_EVENT__VISIBLE:
				getVisible().clear();
				return;
			case LoggingPackage.FILTERS_CHANGED_EVENT__HIDDEN:
				getHidden().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case LoggingPackage.FILTERS_CHANGED_EVENT__VISIBLE:
				return visible != null && !visible.isEmpty();
			case LoggingPackage.FILTERS_CHANGED_EVENT__HIDDEN:
				return hidden != null && !hidden.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (visible: ");
		result.append(visible);
		result.append(", hidden: ");
		result.append(hidden);
		result.append(')');
		return result.toString();
	}

} //FiltersChangedEventImpl