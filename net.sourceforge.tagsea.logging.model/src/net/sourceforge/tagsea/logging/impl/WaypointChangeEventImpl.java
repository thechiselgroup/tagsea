/**
 * <copyright>
 * </copyright>
 *
 * $Id: WaypointChangeEventImpl.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import java.util.Collection;

import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.WaypointAttribute;
import net.sourceforge.tagsea.logging.WaypointChangeEvent;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Waypoint Change Event</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.WaypointChangeEventImpl#getNewAttributes <em>New Attributes</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.WaypointChangeEventImpl#getOldAttributes <em>Old Attributes</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WaypointChangeEventImpl extends WaypointEventImpl implements WaypointChangeEvent {
	/**
	 * The cached value of the '{@link #getNewAttributes() <em>New Attributes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNewAttributes()
	 * @generated
	 * @ordered
	 */
	protected EList newAttributes;

	/**
	 * The cached value of the '{@link #getOldAttributes() <em>Old Attributes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOldAttributes()
	 * @generated
	 * @ordered
	 */
	protected EList oldAttributes;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WaypointChangeEventImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.WAYPOINT_CHANGE_EVENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getNewAttributes() {
		if (newAttributes == null) {
			newAttributes = new BasicInternalEList(WaypointAttribute.class);
		}
		return newAttributes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getOldAttributes() {
		if (oldAttributes == null) {
			oldAttributes = new BasicInternalEList(WaypointAttribute.class);
		}
		return oldAttributes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LoggingPackage.WAYPOINT_CHANGE_EVENT__NEW_ATTRIBUTES:
				return ((InternalEList)getNewAttributes()).basicRemove(otherEnd, msgs);
			case LoggingPackage.WAYPOINT_CHANGE_EVENT__OLD_ATTRIBUTES:
				return ((InternalEList)getOldAttributes()).basicRemove(otherEnd, msgs);
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
			case LoggingPackage.WAYPOINT_CHANGE_EVENT__NEW_ATTRIBUTES:
				return getNewAttributes();
			case LoggingPackage.WAYPOINT_CHANGE_EVENT__OLD_ATTRIBUTES:
				return getOldAttributes();
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
			case LoggingPackage.WAYPOINT_CHANGE_EVENT__NEW_ATTRIBUTES:
				getNewAttributes().clear();
				getNewAttributes().addAll((Collection)newValue);
				return;
			case LoggingPackage.WAYPOINT_CHANGE_EVENT__OLD_ATTRIBUTES:
				getOldAttributes().clear();
				getOldAttributes().addAll((Collection)newValue);
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
			case LoggingPackage.WAYPOINT_CHANGE_EVENT__NEW_ATTRIBUTES:
				getNewAttributes().clear();
				return;
			case LoggingPackage.WAYPOINT_CHANGE_EVENT__OLD_ATTRIBUTES:
				getOldAttributes().clear();
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
			case LoggingPackage.WAYPOINT_CHANGE_EVENT__NEW_ATTRIBUTES:
				return newAttributes != null && !newAttributes.isEmpty();
			case LoggingPackage.WAYPOINT_CHANGE_EVENT__OLD_ATTRIBUTES:
				return oldAttributes != null && !oldAttributes.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //WaypointChangeEventImpl