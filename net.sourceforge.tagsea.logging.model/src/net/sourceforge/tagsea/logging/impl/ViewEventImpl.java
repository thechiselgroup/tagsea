/**
 * <copyright>
 * </copyright>
 *
 * $Id: ViewEventImpl.java,v 1.1 2007/06/15 19:12:09 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.ViewEvent;
import net.sourceforge.tagsea.logging.ViewEventType;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>View Event</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.ViewEventImpl#getType <em>Type</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.ViewEventImpl#getViewid <em>Viewid</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.ViewEventImpl#getFilterString <em>Filter String</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ViewEventImpl extends UIEventImpl implements ViewEvent {
	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final ViewEventType TYPE_EDEFAULT = ViewEventType.OPENED_LITERAL;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected ViewEventType type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getViewid() <em>Viewid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getViewid()
	 * @generated
	 * @ordered
	 */
	protected static final String VIEWID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getViewid() <em>Viewid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getViewid()
	 * @generated
	 * @ordered
	 */
	protected String viewid = VIEWID_EDEFAULT;

	/**
	 * The default value of the '{@link #getFilterString() <em>Filter String</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFilterString()
	 * @generated
	 * @ordered
	 */
	protected static final String FILTER_STRING_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getFilterString() <em>Filter String</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFilterString()
	 * @generated
	 * @ordered
	 */
	protected String filterString = FILTER_STRING_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ViewEventImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.VIEW_EVENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ViewEventType getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(ViewEventType newType) {
		type = newType == null ? TYPE_EDEFAULT : newType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getViewid() {
		return viewid;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setViewid(String newViewid) {
		viewid = newViewid;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFilterString() {
		return filterString;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFilterString(String newFilterString) {
		filterString = newFilterString;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LoggingPackage.VIEW_EVENT__TYPE:
				return getType();
			case LoggingPackage.VIEW_EVENT__VIEWID:
				return getViewid();
			case LoggingPackage.VIEW_EVENT__FILTER_STRING:
				return getFilterString();
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
			case LoggingPackage.VIEW_EVENT__TYPE:
				setType((ViewEventType)newValue);
				return;
			case LoggingPackage.VIEW_EVENT__VIEWID:
				setViewid((String)newValue);
				return;
			case LoggingPackage.VIEW_EVENT__FILTER_STRING:
				setFilterString((String)newValue);
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
			case LoggingPackage.VIEW_EVENT__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case LoggingPackage.VIEW_EVENT__VIEWID:
				setViewid(VIEWID_EDEFAULT);
				return;
			case LoggingPackage.VIEW_EVENT__FILTER_STRING:
				setFilterString(FILTER_STRING_EDEFAULT);
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
			case LoggingPackage.VIEW_EVENT__TYPE:
				return type != TYPE_EDEFAULT;
			case LoggingPackage.VIEW_EVENT__VIEWID:
				return VIEWID_EDEFAULT == null ? viewid != null : !VIEWID_EDEFAULT.equals(viewid);
			case LoggingPackage.VIEW_EVENT__FILTER_STRING:
				return FILTER_STRING_EDEFAULT == null ? filterString != null : !FILTER_STRING_EDEFAULT.equals(filterString);
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
		result.append(" (type: ");
		result.append(type);
		result.append(", viewid: ");
		result.append(viewid);
		result.append(", filterString: ");
		result.append(filterString);
		result.append(')');
		return result.toString();
	}

} //ViewEventImpl