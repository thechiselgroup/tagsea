/**
 * <copyright>
 * </copyright>
 *
 * $Id: LoggingAdapterFactory.java,v 1.3 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.util;

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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see net.sourceforge.tagsea.logging.LoggingPackage
 * @generated
 */
public class LoggingAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static LoggingPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LoggingAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = LoggingPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch the delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LoggingSwitch modelSwitch =
		new LoggingSwitch() {
			public Object caseModelLog(ModelLog object) {
				return createModelLogAdapter();
			}
			public Object caseEvent(Event object) {
				return createEventAdapter();
			}
			public Object caseTagEvent(TagEvent object) {
				return createTagEventAdapter();
			}
			public Object caseWaypointEvent(WaypointEvent object) {
				return createWaypointEventAdapter();
			}
			public Object caseLogFile(LogFile object) {
				return createLogFileAdapter();
			}
			public Object caseWaypointState(WaypointState object) {
				return createWaypointStateAdapter();
			}
			public Object caseUILog(UILog object) {
				return createUILogAdapter();
			}
			public Object caseCurrentWaypoints(CurrentWaypoints object) {
				return createCurrentWaypointsAdapter();
			}
			public Object caseWaypointAttribute(WaypointAttribute object) {
				return createWaypointAttributeAdapter();
			}
			public Object caseNewWaypointEvent(NewWaypointEvent object) {
				return createNewWaypointEventAdapter();
			}
			public Object caseDeletedWaypointEvent(DeletedWaypointEvent object) {
				return createDeletedWaypointEventAdapter();
			}
			public Object caseWaypointChangeEvent(WaypointChangeEvent object) {
				return createWaypointChangeEventAdapter();
			}
			public Object caseTagNameChangeEvent(TagNameChangeEvent object) {
				return createTagNameChangeEventAdapter();
			}
			public Object caseWaypointTagNameChangeEvent(WaypointTagNameChangeEvent object) {
				return createWaypointTagNameChangeEventAdapter();
			}
			public Object caseWaypointTagsChangedEvent(WaypointTagsChangedEvent object) {
				return createWaypointTagsChangedEventAdapter();
			}
			public Object caseTagWaypointsChangedEvent(TagWaypointsChangedEvent object) {
				return createTagWaypointsChangedEventAdapter();
			}
			public Object caseNewTagEvent(NewTagEvent object) {
				return createNewTagEventAdapter();
			}
			public Object caseDeletedTagEvent(DeletedTagEvent object) {
				return createDeletedTagEventAdapter();
			}
			public Object caseJobEvent(JobEvent object) {
				return createJobEventAdapter();
			}
			public Object caseLog(Log object) {
				return createLogAdapter();
			}
			public Object caseNavigateEvent(NavigateEvent object) {
				return createNavigateEventAdapter();
			}
			public Object caseViewEvent(ViewEvent object) {
				return createViewEventAdapter();
			}
			public Object caseFiltersChangedEvent(FiltersChangedEvent object) {
				return createFiltersChangedEventAdapter();
			}
			public Object caseUIEvent(UIEvent object) {
				return createUIEventAdapter();
			}
			public Object caseModelEvent(ModelEvent object) {
				return createModelEventAdapter();
			}
			public Object caseModelRecordStartEvent(ModelRecordStartEvent object) {
				return createModelRecordStartEventAdapter();
			}
			public Object caseUIRecordStartEvent(UIRecordStartEvent object) {
				return createUIRecordStartEventAdapter();
			}
			public Object caseTaskNavigateEvent(TaskNavigateEvent object) {
				return createTaskNavigateEventAdapter();
			}
			public Object defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	public Adapter createAdapter(Notifier target) {
		return (Adapter)modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.ModelLog <em>Model Log</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.ModelLog
	 * @generated
	 */
	public Adapter createModelLogAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.Event <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.Event
	 * @generated
	 */
	public Adapter createEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.TagEvent <em>Tag Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.TagEvent
	 * @generated
	 */
	public Adapter createTagEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.WaypointEvent <em>Waypoint Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.WaypointEvent
	 * @generated
	 */
	public Adapter createWaypointEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.LogFile <em>Log File</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.LogFile
	 * @generated
	 */
	public Adapter createLogFileAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.WaypointState <em>Waypoint State</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.WaypointState
	 * @generated
	 */
	public Adapter createWaypointStateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.UILog <em>UI Log</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.UILog
	 * @generated
	 */
	public Adapter createUILogAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.CurrentWaypoints <em>Current Waypoints</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.CurrentWaypoints
	 * @generated
	 */
	public Adapter createCurrentWaypointsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.WaypointAttribute <em>Waypoint Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.WaypointAttribute
	 * @generated
	 */
	public Adapter createWaypointAttributeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.NewWaypointEvent <em>New Waypoint Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.NewWaypointEvent
	 * @generated
	 */
	public Adapter createNewWaypointEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.DeletedWaypointEvent <em>Deleted Waypoint Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.DeletedWaypointEvent
	 * @generated
	 */
	public Adapter createDeletedWaypointEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.WaypointChangeEvent <em>Waypoint Change Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.WaypointChangeEvent
	 * @generated
	 */
	public Adapter createWaypointChangeEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.TagNameChangeEvent <em>Tag Name Change Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.TagNameChangeEvent
	 * @generated
	 */
	public Adapter createTagNameChangeEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent <em>Waypoint Tag Name Change Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent
	 * @generated
	 */
	public Adapter createWaypointTagNameChangeEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.TagWaypointsChangedEvent <em>Tag Waypoints Changed Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.TagWaypointsChangedEvent
	 * @generated
	 */
	public Adapter createTagWaypointsChangedEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.NewTagEvent <em>New Tag Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.NewTagEvent
	 * @generated
	 */
	public Adapter createNewTagEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.DeletedTagEvent <em>Deleted Tag Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.DeletedTagEvent
	 * @generated
	 */
	public Adapter createDeletedTagEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.JobEvent <em>Job Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.JobEvent
	 * @generated
	 */
	public Adapter createJobEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.Log <em>Log</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.Log
	 * @generated
	 */
	public Adapter createLogAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.NavigateEvent <em>Navigate Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.NavigateEvent
	 * @generated
	 */
	public Adapter createNavigateEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.ViewEvent <em>View Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.ViewEvent
	 * @generated
	 */
	public Adapter createViewEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.FiltersChangedEvent <em>Filters Changed Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.FiltersChangedEvent
	 * @generated
	 */
	public Adapter createFiltersChangedEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.UIEvent <em>UI Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.UIEvent
	 * @generated
	 */
	public Adapter createUIEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.ModelEvent <em>Model Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.ModelEvent
	 * @generated
	 */
	public Adapter createModelEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.WaypointTagsChangedEvent <em>Waypoint Tags Changed Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.WaypointTagsChangedEvent
	 * @generated
	 */
	public Adapter createWaypointTagsChangedEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.ModelRecordStartEvent <em>Model Record Start Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.ModelRecordStartEvent
	 * @generated
	 */
	public Adapter createModelRecordStartEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.UIRecordStartEvent <em>UI Record Start Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.UIRecordStartEvent
	 * @generated
	 */
	public Adapter createUIRecordStartEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link net.sourceforge.tagsea.logging.TaskNavigateEvent <em>Task Navigate Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see net.sourceforge.tagsea.logging.TaskNavigateEvent
	 * @generated
	 */
	public Adapter createTaskNavigateEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //LoggingAdapterFactory
