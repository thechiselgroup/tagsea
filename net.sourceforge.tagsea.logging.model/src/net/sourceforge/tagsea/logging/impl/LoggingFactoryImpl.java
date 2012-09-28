/**
 * <copyright>
 * </copyright>
 *
 * $Id: LoggingFactoryImpl.java,v 1.3 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import net.sourceforge.tagsea.logging.CurrentWaypoints;
import net.sourceforge.tagsea.logging.DeletedTagEvent;
import net.sourceforge.tagsea.logging.DeletedWaypointEvent;
import net.sourceforge.tagsea.logging.FiltersChangedEvent;
import net.sourceforge.tagsea.logging.JobEvent;
import net.sourceforge.tagsea.logging.JobState;
import net.sourceforge.tagsea.logging.LogFile;
import net.sourceforge.tagsea.logging.LoggingFactory;
import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.ModelLog;
import net.sourceforge.tagsea.logging.ModelRecordStartEvent;
import net.sourceforge.tagsea.logging.NavigateEvent;
import net.sourceforge.tagsea.logging.NewTagEvent;
import net.sourceforge.tagsea.logging.NewWaypointEvent;
import net.sourceforge.tagsea.logging.TagNameChangeEvent;
import net.sourceforge.tagsea.logging.TagWaypointsChangedEvent;
import net.sourceforge.tagsea.logging.TaskNavigateEvent;
import net.sourceforge.tagsea.logging.UILog;
import net.sourceforge.tagsea.logging.UIRecordStartEvent;
import net.sourceforge.tagsea.logging.ViewEvent;
import net.sourceforge.tagsea.logging.ViewEventType;
import net.sourceforge.tagsea.logging.WaypointAttribute;
import net.sourceforge.tagsea.logging.WaypointChangeEvent;
import net.sourceforge.tagsea.logging.WaypointState;
import net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent;
import net.sourceforge.tagsea.logging.WaypointTagsChangedEvent;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LoggingFactoryImpl extends EFactoryImpl implements LoggingFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static LoggingFactory init() {
		try {
			LoggingFactory theLoggingFactory = (LoggingFactory)EPackage.Registry.INSTANCE.getEFactory("net.sourceforge.tagsea.logging"); 
			if (theLoggingFactory != null) {
				return theLoggingFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new LoggingFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LoggingFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case LoggingPackage.MODEL_LOG: return createModelLog();
			case LoggingPackage.LOG_FILE: return createLogFile();
			case LoggingPackage.WAYPOINT_STATE: return createWaypointState();
			case LoggingPackage.UI_LOG: return createUILog();
			case LoggingPackage.CURRENT_WAYPOINTS: return createCurrentWaypoints();
			case LoggingPackage.WAYPOINT_ATTRIBUTE: return createWaypointAttribute();
			case LoggingPackage.NEW_WAYPOINT_EVENT: return createNewWaypointEvent();
			case LoggingPackage.DELETED_WAYPOINT_EVENT: return createDeletedWaypointEvent();
			case LoggingPackage.WAYPOINT_CHANGE_EVENT: return createWaypointChangeEvent();
			case LoggingPackage.TAG_NAME_CHANGE_EVENT: return createTagNameChangeEvent();
			case LoggingPackage.WAYPOINT_TAG_NAME_CHANGE_EVENT: return createWaypointTagNameChangeEvent();
			case LoggingPackage.WAYPOINT_TAGS_CHANGED_EVENT: return createWaypointTagsChangedEvent();
			case LoggingPackage.TAG_WAYPOINTS_CHANGED_EVENT: return createTagWaypointsChangedEvent();
			case LoggingPackage.NEW_TAG_EVENT: return createNewTagEvent();
			case LoggingPackage.DELETED_TAG_EVENT: return createDeletedTagEvent();
			case LoggingPackage.JOB_EVENT: return createJobEvent();
			case LoggingPackage.NAVIGATE_EVENT: return createNavigateEvent();
			case LoggingPackage.VIEW_EVENT: return createViewEvent();
			case LoggingPackage.FILTERS_CHANGED_EVENT: return createFiltersChangedEvent();
			case LoggingPackage.MODEL_RECORD_START_EVENT: return createModelRecordStartEvent();
			case LoggingPackage.UI_RECORD_START_EVENT: return createUIRecordStartEvent();
			case LoggingPackage.TASK_NAVIGATE_EVENT: return createTaskNavigateEvent();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case LoggingPackage.JOB_STATE:
				return createJobStateFromString(eDataType, initialValue);
			case LoggingPackage.VIEW_EVENT_TYPE:
				return createViewEventTypeFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case LoggingPackage.JOB_STATE:
				return convertJobStateToString(eDataType, instanceValue);
			case LoggingPackage.VIEW_EVENT_TYPE:
				return convertViewEventTypeToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModelLog createModelLog() {
		ModelLogImpl modelLog = new ModelLogImpl();
		return modelLog;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LogFile createLogFile() {
		LogFileImpl logFile = new LogFileImpl();
		return logFile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WaypointState createWaypointState() {
		WaypointStateImpl waypointState = new WaypointStateImpl();
		return waypointState;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UILog createUILog() {
		UILogImpl uiLog = new UILogImpl();
		return uiLog;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CurrentWaypoints createCurrentWaypoints() {
		CurrentWaypointsImpl currentWaypoints = new CurrentWaypointsImpl();
		return currentWaypoints;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WaypointAttribute createWaypointAttribute() {
		WaypointAttributeImpl waypointAttribute = new WaypointAttributeImpl();
		return waypointAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NewWaypointEvent createNewWaypointEvent() {
		NewWaypointEventImpl newWaypointEvent = new NewWaypointEventImpl();
		return newWaypointEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeletedWaypointEvent createDeletedWaypointEvent() {
		DeletedWaypointEventImpl deletedWaypointEvent = new DeletedWaypointEventImpl();
		return deletedWaypointEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WaypointChangeEvent createWaypointChangeEvent() {
		WaypointChangeEventImpl waypointChangeEvent = new WaypointChangeEventImpl();
		return waypointChangeEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TagNameChangeEvent createTagNameChangeEvent() {
		TagNameChangeEventImpl tagNameChangeEvent = new TagNameChangeEventImpl();
		return tagNameChangeEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WaypointTagNameChangeEvent createWaypointTagNameChangeEvent() {
		WaypointTagNameChangeEventImpl waypointTagNameChangeEvent = new WaypointTagNameChangeEventImpl();
		return waypointTagNameChangeEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TagWaypointsChangedEvent createTagWaypointsChangedEvent() {
		TagWaypointsChangedEventImpl tagWaypointsChangedEvent = new TagWaypointsChangedEventImpl();
		return tagWaypointsChangedEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NewTagEvent createNewTagEvent() {
		NewTagEventImpl newTagEvent = new NewTagEventImpl();
		return newTagEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeletedTagEvent createDeletedTagEvent() {
		DeletedTagEventImpl deletedTagEvent = new DeletedTagEventImpl();
		return deletedTagEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public JobEvent createJobEvent() {
		JobEventImpl jobEvent = new JobEventImpl();
		return jobEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NavigateEvent createNavigateEvent() {
		NavigateEventImpl navigateEvent = new NavigateEventImpl();
		return navigateEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ViewEvent createViewEvent() {
		ViewEventImpl viewEvent = new ViewEventImpl();
		return viewEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FiltersChangedEvent createFiltersChangedEvent() {
		FiltersChangedEventImpl filtersChangedEvent = new FiltersChangedEventImpl();
		return filtersChangedEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WaypointTagsChangedEvent createWaypointTagsChangedEvent() {
		WaypointTagsChangedEventImpl waypointTagsChangedEvent = new WaypointTagsChangedEventImpl();
		return waypointTagsChangedEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModelRecordStartEvent createModelRecordStartEvent() {
		ModelRecordStartEventImpl modelRecordStartEvent = new ModelRecordStartEventImpl();
		return modelRecordStartEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UIRecordStartEvent createUIRecordStartEvent() {
		UIRecordStartEventImpl uiRecordStartEvent = new UIRecordStartEventImpl();
		return uiRecordStartEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TaskNavigateEvent createTaskNavigateEvent() {
		TaskNavigateEventImpl taskNavigateEvent = new TaskNavigateEventImpl();
		return taskNavigateEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public JobState createJobStateFromString(EDataType eDataType, String initialValue) {
		JobState result = JobState.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertJobStateToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ViewEventType createViewEventTypeFromString(EDataType eDataType, String initialValue) {
		ViewEventType result = ViewEventType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertViewEventTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LoggingPackage getLoggingPackage() {
		return (LoggingPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	public static LoggingPackage getPackage() {
		return LoggingPackage.eINSTANCE;
	}

} //LoggingFactoryImpl
