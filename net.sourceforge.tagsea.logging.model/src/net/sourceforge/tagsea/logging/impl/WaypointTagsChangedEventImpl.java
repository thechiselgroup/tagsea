/**
 * <copyright>
 * </copyright>
 *
 * $Id: WaypointTagsChangedEventImpl.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import java.util.Collection;

import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.WaypointTagsChangedEvent;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.BasicInternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Waypoint Tags Changed Event</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.WaypointTagsChangedEventImpl#getOldTags <em>Old Tags</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WaypointTagsChangedEventImpl extends WaypointEventImpl implements WaypointTagsChangedEvent {
	/**
	 * The cached value of the '{@link #getOldTags() <em>Old Tags</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOldTags()
	 * @generated
	 * @ordered
	 */
	protected EList oldTags;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WaypointTagsChangedEventImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.WAYPOINT_TAGS_CHANGED_EVENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getOldTags() {
		if (oldTags == null) {
			oldTags = new BasicInternalEList(String.class);
		}
		return oldTags;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LoggingPackage.WAYPOINT_TAGS_CHANGED_EVENT__OLD_TAGS:
				return getOldTags();
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
			case LoggingPackage.WAYPOINT_TAGS_CHANGED_EVENT__OLD_TAGS:
				getOldTags().clear();
				getOldTags().addAll((Collection)newValue);
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
			case LoggingPackage.WAYPOINT_TAGS_CHANGED_EVENT__OLD_TAGS:
				getOldTags().clear();
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
			case LoggingPackage.WAYPOINT_TAGS_CHANGED_EVENT__OLD_TAGS:
				return oldTags != null && !oldTags.isEmpty();
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
		result.append(" (oldTags: ");
		result.append(oldTags);
		result.append(')');
		return result.toString();
	}

} //WaypointTagsChangedEventImpl