/**
 * <copyright>
 * </copyright>
 *
 * $Id: LoggingSwitch.java,v 1.3 2007/07/30 21:12:11 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.util;

import java.util.List;

import net.sourceforge.tagsea.logging.CurrentWaypoints;
import net.sourceforge.tagsea.logging.DeletedTagEvent;
import net.sourceforge.tagsea.logging.DeletedWaypointEvent;
import net.sourceforge.tagsea.logging.Event;
import net.sourceforge.tagsea.logging.FiltersChangedEvent;
import net.sourceforge.tagsea.logging.JobEvent;
import net.sourceforge.tagsea.logging.Log;
import net.sourceforge.tagsea.logging.LogFile;
import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.ModelEvent;
import net.sourceforge.tagsea.logging.ModelLog;
import net.sourceforge.tagsea.logging.ModelRecordStartEvent;
import net.sourceforge.tagsea.logging.NavigateEvent;
import net.sourceforge.tagsea.logging.NewTagEvent;
import net.sourceforge.tagsea.logging.NewWaypointEvent;
import net.sourceforge.tagsea.logging.TagEvent;
import net.sourceforge.tagsea.logging.TagNameChangeEvent;
import net.sourceforge.tagsea.logging.TagWaypointsChangedEvent;
import net.sourceforge.tagsea.logging.TaskNavigateEvent;
import net.sourceforge.tagsea.logging.UIEvent;
import net.sourceforge.tagsea.logging.UILog;
import net.sourceforge.tagsea.logging.UIRecordStartEvent;
import net.sourceforge.tagsea.logging.ViewEvent;
import net.sourceforge.tagsea.logging.WaypointAttribute;
import net.sourceforge.tagsea.logging.WaypointChangeEvent;
import net.sourceforge.tagsea.logging.WaypointEvent;
import net.sourceforge.tagsea.logging.WaypointState;
import net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent;
import net.sourceforge.tagsea.logging.WaypointTagsChangedEvent;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see net.sourceforge.tagsea.logging.LoggingPackage
 * @generated
 */
public class LoggingSwitch {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static LoggingPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LoggingSwitch() {
		if (modelPackage == null) {
			modelPackage = LoggingPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public Object doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch((EClass)eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case LoggingPackage.MODEL_LOG: {
				ModelLog modelLog = (ModelLog)theEObject;
				Object result = caseModelLog(modelLog);
				if (result == null) result = caseLog(modelLog);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.EVENT: {
				Event event = (Event)theEObject;
				Object result = caseEvent(event);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.TAG_EVENT: {
				TagEvent tagEvent = (TagEvent)theEObject;
				Object result = caseTagEvent(tagEvent);
				if (result == null) result = caseModelEvent(tagEvent);
				if (result == null) result = caseEvent(tagEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.WAYPOINT_EVENT: {
				WaypointEvent waypointEvent = (WaypointEvent)theEObject;
				Object result = caseWaypointEvent(waypointEvent);
				if (result == null) result = caseModelEvent(waypointEvent);
				if (result == null) result = caseEvent(waypointEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.LOG_FILE: {
				LogFile logFile = (LogFile)theEObject;
				Object result = caseLogFile(logFile);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.WAYPOINT_STATE: {
				WaypointState waypointState = (WaypointState)theEObject;
				Object result = caseWaypointState(waypointState);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.UI_LOG: {
				UILog uiLog = (UILog)theEObject;
				Object result = caseUILog(uiLog);
				if (result == null) result = caseLog(uiLog);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.CURRENT_WAYPOINTS: {
				CurrentWaypoints currentWaypoints = (CurrentWaypoints)theEObject;
				Object result = caseCurrentWaypoints(currentWaypoints);
				if (result == null) result = caseLog(currentWaypoints);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.WAYPOINT_ATTRIBUTE: {
				WaypointAttribute waypointAttribute = (WaypointAttribute)theEObject;
				Object result = caseWaypointAttribute(waypointAttribute);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.NEW_WAYPOINT_EVENT: {
				NewWaypointEvent newWaypointEvent = (NewWaypointEvent)theEObject;
				Object result = caseNewWaypointEvent(newWaypointEvent);
				if (result == null) result = caseWaypointEvent(newWaypointEvent);
				if (result == null) result = caseModelEvent(newWaypointEvent);
				if (result == null) result = caseEvent(newWaypointEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.DELETED_WAYPOINT_EVENT: {
				DeletedWaypointEvent deletedWaypointEvent = (DeletedWaypointEvent)theEObject;
				Object result = caseDeletedWaypointEvent(deletedWaypointEvent);
				if (result == null) result = caseWaypointEvent(deletedWaypointEvent);
				if (result == null) result = caseModelEvent(deletedWaypointEvent);
				if (result == null) result = caseEvent(deletedWaypointEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.WAYPOINT_CHANGE_EVENT: {
				WaypointChangeEvent waypointChangeEvent = (WaypointChangeEvent)theEObject;
				Object result = caseWaypointChangeEvent(waypointChangeEvent);
				if (result == null) result = caseWaypointEvent(waypointChangeEvent);
				if (result == null) result = caseModelEvent(waypointChangeEvent);
				if (result == null) result = caseEvent(waypointChangeEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.TAG_NAME_CHANGE_EVENT: {
				TagNameChangeEvent tagNameChangeEvent = (TagNameChangeEvent)theEObject;
				Object result = caseTagNameChangeEvent(tagNameChangeEvent);
				if (result == null) result = caseTagEvent(tagNameChangeEvent);
				if (result == null) result = caseModelEvent(tagNameChangeEvent);
				if (result == null) result = caseEvent(tagNameChangeEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.WAYPOINT_TAG_NAME_CHANGE_EVENT: {
				WaypointTagNameChangeEvent waypointTagNameChangeEvent = (WaypointTagNameChangeEvent)theEObject;
				Object result = caseWaypointTagNameChangeEvent(waypointTagNameChangeEvent);
				if (result == null) result = caseWaypointEvent(waypointTagNameChangeEvent);
				if (result == null) result = caseModelEvent(waypointTagNameChangeEvent);
				if (result == null) result = caseEvent(waypointTagNameChangeEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.WAYPOINT_TAGS_CHANGED_EVENT: {
				WaypointTagsChangedEvent waypointTagsChangedEvent = (WaypointTagsChangedEvent)theEObject;
				Object result = caseWaypointTagsChangedEvent(waypointTagsChangedEvent);
				if (result == null) result = caseWaypointEvent(waypointTagsChangedEvent);
				if (result == null) result = caseModelEvent(waypointTagsChangedEvent);
				if (result == null) result = caseEvent(waypointTagsChangedEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT: {
				TagWaypointsChangedEvent tagWaypointsChangedEvent = (TagWaypointsChangedEvent)theEObject;
				Object result = caseTagWaypointsChangedEvent(tagWaypointsChangedEvent);
				if (result == null) result = caseTagEvent(tagWaypointsChangedEvent);
				if (result == null) result = caseModelEvent(tagWaypointsChangedEvent);
				if (result == null) result = caseEvent(tagWaypointsChangedEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.NEW_TAG_EVENT: {
				NewTagEvent newTagEvent = (NewTagEvent)theEObject;
				Object result = caseNewTagEvent(newTagEvent);
				if (result == null) result = caseTagEvent(newTagEvent);
				if (result == null) result = caseModelEvent(newTagEvent);
				if (result == null) result = caseEvent(newTagEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.DELETED_TAG_EVENT: {
				DeletedTagEvent deletedTagEvent = (DeletedTagEvent)theEObject;
				Object result = caseDeletedTagEvent(deletedTagEvent);
				if (result == null) result = caseTagEvent(deletedTagEvent);
				if (result == null) result = caseModelEvent(deletedTagEvent);
				if (result == null) result = caseEvent(deletedTagEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.JOB_EVENT: {
				JobEvent jobEvent = (JobEvent)theEObject;
				Object result = caseJobEvent(jobEvent);
				if (result == null) result = caseModelEvent(jobEvent);
				if (result == null) result = caseEvent(jobEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.LOG: {
				Log log = (Log)theEObject;
				Object result = caseLog(log);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.NAVIGATE_EVENT: {
				NavigateEvent navigateEvent = (NavigateEvent)theEObject;
				Object result = caseNavigateEvent(navigateEvent);
				if (result == null) result = caseUIEvent(navigateEvent);
				if (result == null) result = caseEvent(navigateEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.VIEW_EVENT: {
				ViewEvent viewEvent = (ViewEvent)theEObject;
				Object result = caseViewEvent(viewEvent);
				if (result == null) result = caseUIEvent(viewEvent);
				if (result == null) result = caseEvent(viewEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.FILTERS_CHANGED_EVENT: {
				FiltersChangedEvent filtersChangedEvent = (FiltersChangedEvent)theEObject;
				Object result = caseFiltersChangedEvent(filtersChangedEvent);
				if (result == null) result = caseUIEvent(filtersChangedEvent);
				if (result == null) result = caseEvent(filtersChangedEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.UI_EVENT: {
				UIEvent uiEvent = (UIEvent)theEObject;
				Object result = caseUIEvent(uiEvent);
				if (result == null) result = caseEvent(uiEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.MODEL_EVENT: {
				ModelEvent modelEvent = (ModelEvent)theEObject;
				Object result = caseModelEvent(modelEvent);
				if (result == null) result = caseEvent(modelEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.MODEL_RECORD_START_EVENT: {
				ModelRecordStartEvent modelRecordStartEvent = (ModelRecordStartEvent)theEObject;
				Object result = caseModelRecordStartEvent(modelRecordStartEvent);
				if (result == null) result = caseModelEvent(modelRecordStartEvent);
				if (result == null) result = caseEvent(modelRecordStartEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.UI_RECORD_START_EVENT: {
				UIRecordStartEvent uiRecordStartEvent = (UIRecordStartEvent)theEObject;
				Object result = caseUIRecordStartEvent(uiRecordStartEvent);
				if (result == null) result = caseUIEvent(uiRecordStartEvent);
				if (result == null) result = caseEvent(uiRecordStartEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case LoggingPackage.TASK_NAVIGATE_EVENT: {
				TaskNavigateEvent taskNavigateEvent = (TaskNavigateEvent)theEObject;
				Object result = caseTaskNavigateEvent(taskNavigateEvent);
				if (result == null) result = caseUIEvent(taskNavigateEvent);
				if (result == null) result = caseEvent(taskNavigateEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model Log</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model Log</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseModelLog(ModelLog object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseEvent(Event object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Tag Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Tag Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseTagEvent(TagEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Waypoint Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Waypoint Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseWaypointEvent(WaypointEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Log File</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Log File</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseLogFile(LogFile object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Waypoint State</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Waypoint State</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseWaypointState(WaypointState object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>UI Log</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>UI Log</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseUILog(UILog object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Current Waypoints</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Current Waypoints</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseCurrentWaypoints(CurrentWaypoints object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Waypoint Attribute</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Waypoint Attribute</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseWaypointAttribute(WaypointAttribute object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>New Waypoint Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>New Waypoint Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseNewWaypointEvent(NewWaypointEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Deleted Waypoint Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Deleted Waypoint Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseDeletedWaypointEvent(DeletedWaypointEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Waypoint Change Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Waypoint Change Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseWaypointChangeEvent(WaypointChangeEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Tag Name Change Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Tag Name Change Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseTagNameChangeEvent(TagNameChangeEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Waypoint Tag Name Change Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Waypoint Tag Name Change Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseWaypointTagNameChangeEvent(WaypointTagNameChangeEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Tag Waypoints Changed Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Tag Waypoints Changed Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseTagWaypointsChangedEvent(TagWaypointsChangedEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>New Tag Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>New Tag Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseNewTagEvent(NewTagEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Deleted Tag Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Deleted Tag Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseDeletedTagEvent(DeletedTagEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Job Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Job Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseJobEvent(JobEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Log</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Log</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseLog(Log object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Navigate Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Navigate Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseNavigateEvent(NavigateEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>View Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>View Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseViewEvent(ViewEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Filters Changed Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Filters Changed Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseFiltersChangedEvent(FiltersChangedEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>UI Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>UI Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseUIEvent(UIEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseModelEvent(ModelEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Waypoint Tags Changed Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Waypoint Tags Changed Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseWaypointTagsChangedEvent(WaypointTagsChangedEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model Record Start Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model Record Start Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseModelRecordStartEvent(ModelRecordStartEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>UI Record Start Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>UI Record Start Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseUIRecordStartEvent(UIRecordStartEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Task Navigate Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Task Navigate Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseTaskNavigateEvent(TaskNavigateEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public Object defaultCase(EObject object) {
		return null;
	}

} //LoggingSwitch
