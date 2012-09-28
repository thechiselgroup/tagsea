/**
 * <copyright>
 * </copyright>
 *
 * $Id: WaypointEventImpl.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.WaypointEvent;
import net.sourceforge.tagsea.logging.WaypointState;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Waypoint Event</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.WaypointEventImpl#getWaypoint <em>Waypoint</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class WaypointEventImpl extends ModelEventImpl implements WaypointEvent {
	/**
	 * The cached value of the '{@link #getWaypoint() <em>Waypoint</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWaypoint()
	 * @generated
	 * @ordered
	 */
	protected WaypointState waypoint;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WaypointEventImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.WAYPOINT_EVENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WaypointState getWaypoint() {
		return waypoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetWaypoint(WaypointState newWaypoint, NotificationChain msgs) {
		WaypointState oldWaypoint = waypoint;
		waypoint = newWaypoint;
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWaypoint(WaypointState newWaypoint) {
		if (newWaypoint != waypoint) {
			NotificationChain msgs = null;
			if (waypoint != null)
				msgs = ((InternalEObject)waypoint).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LoggingPackage.WAYPOINT_EVENT__WAYPOINT, null, msgs);
			if (newWaypoint != null)
				msgs = ((InternalEObject)newWaypoint).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - LoggingPackage.WAYPOINT_EVENT__WAYPOINT, null, msgs);
			msgs = basicSetWaypoint(newWaypoint, msgs);
			if (msgs != null) msgs.dispatch();
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LoggingPackage.WAYPOINT_EVENT__WAYPOINT:
				return basicSetWaypoint(null, msgs);
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
			case LoggingPackage.WAYPOINT_EVENT__WAYPOINT:
				return getWaypoint();
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
			case LoggingPackage.WAYPOINT_EVENT__WAYPOINT:
				setWaypoint((WaypointState)newValue);
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
			case LoggingPackage.WAYPOINT_EVENT__WAYPOINT:
				setWaypoint((WaypointState)null);
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
			case LoggingPackage.WAYPOINT_EVENT__WAYPOINT:
				return waypoint != null;
		}
		return super.eIsSet(featureID);
	}

} //WaypointEventImpl