/**
 * <copyright>
 * </copyright>
 *
 * $Id: LogFileImpl.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import java.util.Collection;

import net.sourceforge.tagsea.logging.CurrentWaypoints;
import net.sourceforge.tagsea.logging.LogFile;
import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.ModelLog;
import net.sourceforge.tagsea.logging.UILog;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Log File</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.LogFileImpl#getModelLog <em>Model Log</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.LogFileImpl#getUiLog <em>Ui Log</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.impl.LogFileImpl#getCurrentWaypoints <em>Current Waypoints</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LogFileImpl extends EObjectImpl implements LogFile {
	/**
	 * The cached value of the '{@link #getModelLog() <em>Model Log</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModelLog()
	 * @generated
	 * @ordered
	 */
	protected ModelLog modelLog;

	/**
	 * The cached value of the '{@link #getUiLog() <em>Ui Log</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUiLog()
	 * @generated
	 * @ordered
	 */
	protected EList uiLog;

	/**
	 * The cached value of the '{@link #getCurrentWaypoints() <em>Current Waypoints</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrentWaypoints()
	 * @generated
	 * @ordered
	 */
	protected CurrentWaypoints currentWaypoints;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LogFileImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return LoggingPackage.Literals.LOG_FILE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModelLog getModelLog() {
		return modelLog;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetModelLog(ModelLog newModelLog, NotificationChain msgs) {
		ModelLog oldModelLog = modelLog;
		modelLog = newModelLog;
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setModelLog(ModelLog newModelLog) {
		if (newModelLog != modelLog) {
			NotificationChain msgs = null;
			if (modelLog != null)
				msgs = ((InternalEObject)modelLog).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LoggingPackage.LOG_FILE__MODEL_LOG, null, msgs);
			if (newModelLog != null)
				msgs = ((InternalEObject)newModelLog).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - LoggingPackage.LOG_FILE__MODEL_LOG, null, msgs);
			msgs = basicSetModelLog(newModelLog, msgs);
			if (msgs != null) msgs.dispatch();
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getUiLog() {
		if (uiLog == null) {
			uiLog = new BasicInternalEList(UILog.class);
		}
		return uiLog;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CurrentWaypoints getCurrentWaypoints() {
		return currentWaypoints;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCurrentWaypoints(CurrentWaypoints newCurrentWaypoints, NotificationChain msgs) {
		CurrentWaypoints oldCurrentWaypoints = currentWaypoints;
		currentWaypoints = newCurrentWaypoints;
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCurrentWaypoints(CurrentWaypoints newCurrentWaypoints) {
		if (newCurrentWaypoints != currentWaypoints) {
			NotificationChain msgs = null;
			if (currentWaypoints != null)
				msgs = ((InternalEObject)currentWaypoints).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LoggingPackage.LOG_FILE__CURRENT_WAYPOINTS, null, msgs);
			if (newCurrentWaypoints != null)
				msgs = ((InternalEObject)newCurrentWaypoints).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - LoggingPackage.LOG_FILE__CURRENT_WAYPOINTS, null, msgs);
			msgs = basicSetCurrentWaypoints(newCurrentWaypoints, msgs);
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
			case LoggingPackage.LOG_FILE__MODEL_LOG:
				return basicSetModelLog(null, msgs);
			case LoggingPackage.LOG_FILE__UI_LOG:
				return ((InternalEList)getUiLog()).basicRemove(otherEnd, msgs);
			case LoggingPackage.LOG_FILE__CURRENT_WAYPOINTS:
				return basicSetCurrentWaypoints(null, msgs);
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
			case LoggingPackage.LOG_FILE__MODEL_LOG:
				return getModelLog();
			case LoggingPackage.LOG_FILE__UI_LOG:
				return getUiLog();
			case LoggingPackage.LOG_FILE__CURRENT_WAYPOINTS:
				return getCurrentWaypoints();
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
			case LoggingPackage.LOG_FILE__MODEL_LOG:
				setModelLog((ModelLog)newValue);
				return;
			case LoggingPackage.LOG_FILE__UI_LOG:
				getUiLog().clear();
				getUiLog().addAll((Collection)newValue);
				return;
			case LoggingPackage.LOG_FILE__CURRENT_WAYPOINTS:
				setCurrentWaypoints((CurrentWaypoints)newValue);
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
			case LoggingPackage.LOG_FILE__MODEL_LOG:
				setModelLog((ModelLog)null);
				return;
			case LoggingPackage.LOG_FILE__UI_LOG:
				getUiLog().clear();
				return;
			case LoggingPackage.LOG_FILE__CURRENT_WAYPOINTS:
				setCurrentWaypoints((CurrentWaypoints)null);
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
			case LoggingPackage.LOG_FILE__MODEL_LOG:
				return modelLog != null;
			case LoggingPackage.LOG_FILE__UI_LOG:
				return uiLog != null && !uiLog.isEmpty();
			case LoggingPackage.LOG_FILE__CURRENT_WAYPOINTS:
				return currentWaypoints != null;
		}
		return super.eIsSet(featureID);
	}

} //LogFileImpl