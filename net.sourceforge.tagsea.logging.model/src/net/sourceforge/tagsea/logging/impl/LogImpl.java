/**
 * <copyright>
 * </copyright>
 *
 * $Id: LogImpl.java,v 1.2 2007/06/27 00:02:51 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import java.util.Date;

import net.sourceforge.tagsea.logging.Log;
import net.sourceforge.tagsea.logging.LoggingPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Log</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.LogImpl#getUid <em>Uid</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.LogImpl#getDate <em>Date</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.LogImpl#getUname <em>Uname</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.LogImpl#getCountry <em>Country</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.LogImpl#getLanguage <em>Language</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.LogImpl#getSession <em>Session</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class LogImpl extends EObjectImpl implements Log {
	/**
	 * The default value of the '{@link #getUid() <em>Uid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUid()
	 * @generated
	 * @ordered
	 */
	protected static final int UID_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getUid() <em>Uid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUid()
	 * @generated
	 * @ordered
	 */
	protected int uid = UID_EDEFAULT;

	/**
	 * The default value of the '{@link #getDate() <em>Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDate() <em>Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDate()
	 * @generated
	 * @ordered
	 */
	protected Date date = DATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getUname() <em>Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUname()
	 * @generated
	 * @ordered
	 */
	protected static final String UNAME_EDEFAULT = "anonymous";

	/**
	 * The cached value of the '{@link #getUname() <em>Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUname()
	 * @generated
	 * @ordered
	 */
	protected String uname = UNAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getCountry() <em>Country</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCountry()
	 * @generated
	 * @ordered
	 */
	protected static final String COUNTRY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCountry() <em>Country</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCountry()
	 * @generated
	 * @ordered
	 */
	protected String country = COUNTRY_EDEFAULT;

	/**
	 * The default value of the '{@link #getLanguage() <em>Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final String LANGUAGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLanguage() <em>Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLanguage()
	 * @generated
	 * @ordered
	 */
	protected String language = LANGUAGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getSession() <em>Session</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSession()
	 * @generated
	 * @ordered
	 */
	protected static final int SESSION_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getSession() <em>Session</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSession()
	 * @generated
	 * @ordered
	 */
	protected int session = SESSION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LogImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.LOG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getUid() {
		return uid;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUid(int newUid) {
		uid = newUid;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDate(Date newDate) {
		date = newDate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUname() {
		return uname;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUname(String newUname) {
		uname = newUname;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCountry(String newCountry) {
		country = newCountry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLanguage(String newLanguage) {
		language = newLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getSession() {
		return session;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSession(int newSession) {
		session = newSession;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LoggingPackage.LOG__UID:
				return new Integer(getUid());
			case LoggingPackage.LOG__DATE:
				return getDate();
			case LoggingPackage.LOG__UNAME:
				return getUname();
			case LoggingPackage.LOG__COUNTRY:
				return getCountry();
			case LoggingPackage.LOG__LANGUAGE:
				return getLanguage();
			case LoggingPackage.LOG__SESSION:
				return new Integer(getSession());
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
			case LoggingPackage.LOG__UID:
				setUid(((Integer)newValue).intValue());
				return;
			case LoggingPackage.LOG__DATE:
				setDate((Date)newValue);
				return;
			case LoggingPackage.LOG__UNAME:
				setUname((String)newValue);
				return;
			case LoggingPackage.LOG__COUNTRY:
				setCountry((String)newValue);
				return;
			case LoggingPackage.LOG__LANGUAGE:
				setLanguage((String)newValue);
				return;
			case LoggingPackage.LOG__SESSION:
				setSession(((Integer)newValue).intValue());
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
			case LoggingPackage.LOG__UID:
				setUid(UID_EDEFAULT);
				return;
			case LoggingPackage.LOG__DATE:
				setDate(DATE_EDEFAULT);
				return;
			case LoggingPackage.LOG__UNAME:
				setUname(UNAME_EDEFAULT);
				return;
			case LoggingPackage.LOG__COUNTRY:
				setCountry(COUNTRY_EDEFAULT);
				return;
			case LoggingPackage.LOG__LANGUAGE:
				setLanguage(LANGUAGE_EDEFAULT);
				return;
			case LoggingPackage.LOG__SESSION:
				setSession(SESSION_EDEFAULT);
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
			case LoggingPackage.LOG__UID:
				return uid != UID_EDEFAULT;
			case LoggingPackage.LOG__DATE:
				return DATE_EDEFAULT == null ? date != null : !DATE_EDEFAULT.equals(date);
			case LoggingPackage.LOG__UNAME:
				return UNAME_EDEFAULT == null ? uname != null : !UNAME_EDEFAULT.equals(uname);
			case LoggingPackage.LOG__COUNTRY:
				return COUNTRY_EDEFAULT == null ? country != null : !COUNTRY_EDEFAULT.equals(country);
			case LoggingPackage.LOG__LANGUAGE:
				return LANGUAGE_EDEFAULT == null ? language != null : !LANGUAGE_EDEFAULT.equals(language);
			case LoggingPackage.LOG__SESSION:
				return session != SESSION_EDEFAULT;
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
		result.append(" (uid: ");
		result.append(uid);
		result.append(", date: ");
		result.append(date);
		result.append(", uname: ");
		result.append(uname);
		result.append(", country: ");
		result.append(country);
		result.append(", language: ");
		result.append(language);
		result.append(", session: ");
		result.append(session);
		result.append(')');
		return result.toString();
	}

} //LogImpl