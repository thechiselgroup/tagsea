/**
 * <copyright>
 * </copyright>
 *
 * $Id: UIEventImpl.java,v 1.1 2007/06/15 19:12:09 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.UIEvent;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>UI Event</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.UIEventImpl#isHierarchyOn <em>Hierarchy On</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class UIEventImpl extends EventImpl implements UIEvent {
	/**
	 * The default value of the '{@link #isHierarchyOn() <em>Hierarchy On</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isHierarchyOn()
	 * @generated
	 * @ordered
	 */
	protected static final boolean HIERARCHY_ON_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isHierarchyOn() <em>Hierarchy On</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isHierarchyOn()
	 * @generated
	 * @ordered
	 */
	protected boolean hierarchyOn = HIERARCHY_ON_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UIEventImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.UI_EVENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isHierarchyOn() {
		return hierarchyOn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHierarchyOn(boolean newHierarchyOn) {
		hierarchyOn = newHierarchyOn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LoggingPackage.UI_EVENT__HIERARCHY_ON:
				return isHierarchyOn() ? Boolean.TRUE : Boolean.FALSE;
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
			case LoggingPackage.UI_EVENT__HIERARCHY_ON:
				setHierarchyOn(((Boolean)newValue).booleanValue());
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
			case LoggingPackage.UI_EVENT__HIERARCHY_ON:
				setHierarchyOn(HIERARCHY_ON_EDEFAULT);
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
			case LoggingPackage.UI_EVENT__HIERARCHY_ON:
				return hierarchyOn != HIERARCHY_ON_EDEFAULT;
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
		result.append(" (hierarchyOn: ");
		result.append(hierarchyOn);
		result.append(')');
		return result.toString();
	}

} //UIEventImpl