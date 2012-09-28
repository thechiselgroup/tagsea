/**
 * <copyright>
 * </copyright>
 *
 * $Id: LogFile.java,v 1.1 2007/06/15 19:12:07 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Log File</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.LogFile#getModelLog <em>Model Log</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.LogFile#getUiLog <em>Ui Log</em>}</li>
 *   <li>{@link net.sourceforge.tagsea.logging.LogFile#getCurrentWaypoints <em>Current Waypoints</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getLogFile()
 * @model
 * @generated
 */
public interface LogFile extends EObject {
	/**
	 * Returns the value of the '<em><b>Model Log</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Model Log</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Model Log</em>' containment reference.
	 * @see #setModelLog(ModelLog)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getLogFile_ModelLog()
	 * @model containment="true"
	 * @generated
	 */
	ModelLog getModelLog();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.LogFile#getModelLog <em>Model Log</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Model Log</em>' containment reference.
	 * @see #getModelLog()
	 * @generated
	 */
	void setModelLog(ModelLog value);

	/**
	 * Returns the value of the '<em><b>Ui Log</b></em>' containment reference list.
	 * The list contents are of type {@link net.sourceforge.tagsea.logging.UILog}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ui Log</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ui Log</em>' containment reference list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getLogFile_UiLog()
	 * @model type="net.sourceforge.tagsea.logging.UILog" containment="true"
	 * @generated
	 */
	EList getUiLog();

	/**
	 * Returns the value of the '<em><b>Current Waypoints</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Current Waypoints</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Current Waypoints</em>' containment reference.
	 * @see #setCurrentWaypoints(CurrentWaypoints)
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getLogFile_CurrentWaypoints()
	 * @model containment="true"
	 * @generated
	 */
	CurrentWaypoints getCurrentWaypoints();

	/**
	 * Sets the value of the '{@link net.sourceforge.tagsea.logging.LogFile#getCurrentWaypoints <em>Current Waypoints</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Current Waypoints</em>' containment reference.
	 * @see #getCurrentWaypoints()
	 * @generated
	 */
	void setCurrentWaypoints(CurrentWaypoints value);

} // LogFile