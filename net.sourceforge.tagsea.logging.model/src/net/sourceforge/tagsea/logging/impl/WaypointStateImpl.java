/**
 * <copyright>
 * </copyright>
 *
 * $Id: WaypointStateImpl.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import java.util.Collection;

import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.WaypointAttribute;
import net.sourceforge.tagsea.logging.WaypointState;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Waypoint State</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.WaypointStateImpl#getWaypointType <em>Waypoint Type</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.WaypointStateImpl#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.WaypointStateImpl#getTagNames <em>Tag Names</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WaypointStateImpl extends EObjectImpl implements WaypointState {
	/**
	 * The default value of the '{@link #getWaypointType() <em>Waypoint Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWaypointType()
	 * @generated
	 * @ordered
	 */
	protected static final String WAYPOINT_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWaypointType() <em>Waypoint Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWaypointType()
	 * @generated
	 * @ordered
	 */
	protected String waypointType = WAYPOINT_TYPE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttributes()
	 * @generated
	 * @ordered
	 */
	protected EList attributes;

	/**
	 * The cached value of the '{@link #getTagNames() <em>Tag Names</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTagNames()
	 * @generated
	 * @ordered
	 */
	protected EList tagNames;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WaypointStateImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.WAYPOINT_STATE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getWaypointType() {
		return waypointType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWaypointType(String newWaypointType) {
		waypointType = newWaypointType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getAttributes() {
		if (attributes == null) {
			attributes = new BasicInternalEList(WaypointAttribute.class);
		}
		return attributes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getTagNames() {
		if (tagNames == null) {
			tagNames = new BasicInternalEList(String.class);
		}
		return tagNames;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LoggingPackage.WAYPOINT_STATE__ATTRIBUTES:
				return ((InternalEList)getAttributes()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LoggingPackage.WAYPOINT_STATE__WAYPOINT_TYPE:
				return getWaypointType();
			case LoggingPackage.WAYPOINT_STATE__ATTRIBUTES:
				return getAttributes();
			case LoggingPackage.WAYPOINT_STATE__TAG_NAMES:
				return getTagNames();
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
			case LoggingPackage.WAYPOINT_STATE__WAYPOINT_TYPE:
				setWaypointType((String)newValue);
				return;
			case LoggingPackage.WAYPOINT_STATE__ATTRIBUTES:
				getAttributes().clear();
				getAttributes().addAll((Collection)newValue);
				return;
			case LoggingPackage.WAYPOINT_STATE__TAG_NAMES:
				getTagNames().clear();
				getTagNames().addAll((Collection)newValue);
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
			case LoggingPackage.WAYPOINT_STATE__WAYPOINT_TYPE:
				setWaypointType(WAYPOINT_TYPE_EDEFAULT);
				return;
			case LoggingPackage.WAYPOINT_STATE__ATTRIBUTES:
				getAttributes().clear();
				return;
			case LoggingPackage.WAYPOINT_STATE__TAG_NAMES:
				getTagNames().clear();
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
			case LoggingPackage.WAYPOINT_STATE__WAYPOINT_TYPE:
				return WAYPOINT_TYPE_EDEFAULT == null ? waypointType != null : !WAYPOINT_TYPE_EDEFAULT.equals(waypointType);
			case LoggingPackage.WAYPOINT_STATE__ATTRIBUTES:
				return attributes != null && !attributes.isEmpty();
			case LoggingPackage.WAYPOINT_STATE__TAG_NAMES:
				return tagNames != null && !tagNames.isEmpty();
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
		result.append(" (waypointType: ");
		result.append(waypointType);
		result.append(", tagNames: ");
		result.append(tagNames);
		result.append(')');
		return result.toString();
	}

} //WaypointStateImpl