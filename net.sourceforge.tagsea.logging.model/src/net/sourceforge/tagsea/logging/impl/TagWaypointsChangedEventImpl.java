/**
 * <copyright>
 * </copyright>
 *
 * $Id: TagWaypointsChangedEventImpl.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import java.util.Collection;

import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.TagWaypointsChangedEvent;
import net.sourceforge.tagsea.logging.WaypointState;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Tag Waypoints Changed Event</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.TagWaypointsChangedEventImpl#getOldWaypoint <em>Old Waypoint</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.TagWaypointsChangedEventImpl#getNewWaypoint <em>New Waypoint</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TagWaypointsChangedEventImpl extends TagEventImpl implements TagWaypointsChangedEvent {
	/**
	 * The cached value of the '{@link #getOldWaypoint() <em>Old Waypoint</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOldWaypoint()
	 * @generated
	 * @ordered
	 */
	protected EList oldWaypoint;

	/**
	 * The cached value of the '{@link #getNewWaypoint() <em>New Waypoint</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNewWaypoint()
	 * @generated
	 * @ordered
	 */
	protected EList newWaypoint;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TagWaypointsChangedEventImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.TAG_WAYPOINTS_CHANGED_EVENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getOldWaypoint() {
		if (oldWaypoint == null) {
			oldWaypoint = new BasicInternalEList(WaypointState.class);
		}
		return oldWaypoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getNewWaypoint() {
		if (newWaypoint == null) {
			newWaypoint = new BasicInternalEList(WaypointState.class);
		}
		return newWaypoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT__OLD_WAYPOINT:
				return ((InternalEList)getOldWaypoint()).basicRemove(otherEnd, msgs);
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT__NEW_WAYPOINT:
				return ((InternalEList)getNewWaypoint()).basicRemove(otherEnd, msgs);
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
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT__OLD_WAYPOINT:
				return getOldWaypoint();
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT__NEW_WAYPOINT:
				return getNewWaypoint();
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
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT__OLD_WAYPOINT:
				getOldWaypoint().clear();
				getOldWaypoint().addAll((Collection)newValue);
				return;
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT__NEW_WAYPOINT:
				getNewWaypoint().clear();
				getNewWaypoint().addAll((Collection)newValue);
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
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT__OLD_WAYPOINT:
				getOldWaypoint().clear();
				return;
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT__NEW_WAYPOINT:
				getNewWaypoint().clear();
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
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT__OLD_WAYPOINT:
				return oldWaypoint != null && !oldWaypoint.isEmpty();
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT__NEW_WAYPOINT:
				return newWaypoint != null && !newWaypoint.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //TagWaypointsChangedEventImpl