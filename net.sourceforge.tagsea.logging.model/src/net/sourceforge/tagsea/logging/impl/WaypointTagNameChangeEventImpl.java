/**
 * <copyright>
 * </copyright>
 *
 * $Id: WaypointTagNameChangeEventImpl.java,v 1.1 2007/06/15 19:12:09 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Waypoint Tag Name Change Event</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.WaypointTagNameChangeEventImpl#getNewTagName <em>New Tag Name</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.WaypointTagNameChangeEventImpl#getOldTagName <em>Old Tag Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WaypointTagNameChangeEventImpl extends WaypointEventImpl implements WaypointTagNameChangeEvent {
	/**
	 * The default value of the '{@link #getNewTagName() <em>New Tag Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNewTagName()
	 * @generated
	 * @ordered
	 */
	protected static final String NEW_TAG_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNewTagName() <em>New Tag Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNewTagName()
	 * @generated
	 * @ordered
	 */
	protected String newTagName = NEW_TAG_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getOldTagName() <em>Old Tag Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOldTagName()
	 * @generated
	 * @ordered
	 */
	protected static final String OLD_TAG_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOldTagName() <em>Old Tag Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOldTagName()
	 * @generated
	 * @ordered
	 */
	protected String oldTagName = OLD_TAG_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WaypointTagNameChangeEventImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.WAYPOINT_TAG_NAME_CHANGE_EVENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getNewTagName() {
		return newTagName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNewTagName(String newNewTagName) {
		newTagName = newNewTagName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOldTagName() {
		return oldTagName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOldTagName(String newOldTagName) {
		oldTagName = newOldTagName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LoggingPackage.WAYPOINT_TAG_NAME_CHANGE_EVENT__NEW_TAG_NAME:
				return getNewTagName();
			case LoggingPackage.WAYPOINT_TAG_NAME_CHANGE_EVENT__OLD_TAG_NAME:
				return getOldTagName();
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
			case LoggingPackage.WAYPOINT_TAG_NAME_CHANGE_EVENT__NEW_TAG_NAME:
				setNewTagName((String)newValue);
				return;
			case LoggingPackage.WAYPOINT_TAG_NAME_CHANGE_EVENT__OLD_TAG_NAME:
				setOldTagName((String)newValue);
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
			case LoggingPackage.WAYPOINT_TAG_NAME_CHANGE_EVENT__NEW_TAG_NAME:
				setNewTagName(NEW_TAG_NAME_EDEFAULT);
				return;
			case LoggingPackage.WAYPOINT_TAG_NAME_CHANGE_EVENT__OLD_TAG_NAME:
				setOldTagName(OLD_TAG_NAME_EDEFAULT);
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
			case LoggingPackage.WAYPOINT_TAG_NAME_CHANGE_EVENT__NEW_TAG_NAME:
				return NEW_TAG_NAME_EDEFAULT == null ? newTagName != null : !NEW_TAG_NAME_EDEFAULT.equals(newTagName);
			case LoggingPackage.WAYPOINT_TAG_NAME_CHANGE_EVENT__OLD_TAG_NAME:
				return OLD_TAG_NAME_EDEFAULT == null ? oldTagName != null : !OLD_TAG_NAME_EDEFAULT.equals(oldTagName);
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
		result.append(" (newTagName: ");
		result.append(newTagName);
		result.append(", oldTagName: ");
		result.append(oldTagName);
		result.append(')');
		return result.toString();
	}

} //WaypointTagNameChangeEventImpl