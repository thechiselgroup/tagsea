/**
 * <copyright>
 * </copyright>
 *
 * $Id: LoggingFactory.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see net.sourceforge.tagsea.logging.LoggingPackage
 * @generated
 */
public interface LoggingFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	LoggingFactory eINSTANCE = net.sourceforge.tagsea.logging.impl.LoggingFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Model Log</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Model Log</em>'.
	 * @generated
	 */
	ModelLog createModelLog();

	/**
	 * Returns a new object of class '<em>Log File</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Log File</em>'.
	 * @generated
	 */
	LogFile createLogFile();

	/**
	 * Returns a new object of class '<em>Waypoint State</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Waypoint State</em>'.
	 * @generated
	 */
	WaypointState createWaypointState();

	/**
	 * Returns a new object of class '<em>UI Log</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>UI Log</em>'.
	 * @generated
	 */
	UILog createUILog();

	/**
	 * Returns a new object of class '<em>Current Waypoints</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Current Waypoints</em>'.
	 * @generated
	 */
	CurrentWaypoints createCurrentWaypoints();

	/**
	 * Returns a new object of class '<em>Waypoint Attribute</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Waypoint Attribute</em>'.
	 * @generated
	 */
	WaypointAttribute createWaypointAttribute();

	/**
	 * Returns a new object of class '<em>New Waypoint Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>New Waypoint Event</em>'.
	 * @generated
	 */
	NewWaypointEvent createNewWaypointEvent();

	/**
	 * Returns a new object of class '<em>Deleted Waypoint Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Deleted Waypoint Event</em>'.
	 * @generated
	 */
	DeletedWaypointEvent createDeletedWaypointEvent();

	/**
	 * Returns a new object of class '<em>Waypoint Change Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Waypoint Change Event</em>'.
	 * @generated
	 */
	WaypointChangeEvent createWaypointChangeEvent();

	/**
	 * Returns a new object of class '<em>Tag Name Change Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Tag Name Change Event</em>'.
	 * @generated
	 */
	TagNameChangeEvent createTagNameChangeEvent();

	/**
	 * Returns a new object of class '<em>Waypoint Tag Name Change Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Waypoint Tag Name Change Event</em>'.
	 * @generated
	 */
	WaypointTagNameChangeEvent createWaypointTagNameChangeEvent();

	/**
	 * Returns a new object of class '<em>Tag Waypoints Changed Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Tag Waypoints Changed Event</em>'.
	 * @generated
	 */
	TagWaypointsChangedEvent createTagWaypointsChangedEvent();

	/**
	 * Returns a new object of class '<em>New Tag Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>New Tag Event</em>'.
	 * @generated
	 */
	NewTagEvent createNewTagEvent();

	/**
	 * Returns a new object of class '<em>Deleted Tag Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Deleted Tag Event</em>'.
	 * @generated
	 */
	DeletedTagEvent createDeletedTagEvent();

	/**
	 * Returns a new object of class '<em>Job Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Job Event</em>'.
	 * @generated
	 */
	JobEvent createJobEvent();

	/**
	 * Returns a new object of class '<em>Navigate Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Navigate Event</em>'.
	 * @generated
	 */
	NavigateEvent createNavigateEvent();

	/**
	 * Returns a new object of class '<em>View Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>View Event</em>'.
	 * @generated
	 */
	ViewEvent createViewEvent();

	/**
	 * Returns a new object of class '<em>Filters Changed Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Filters Changed Event</em>'.
	 * @generated
	 */
	FiltersChangedEvent createFiltersChangedEvent();

	/**
	 * Returns a new object of class '<em>Waypoint Tags Changed Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Waypoint Tags Changed Event</em>'.
	 * @generated
	 */
	WaypointTagsChangedEvent createWaypointTagsChangedEvent();

	/**
	 * Returns a new object of class '<em>Model Record Start Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Model Record Start Event</em>'.
	 * @generated
	 */
	ModelRecordStartEvent createModelRecordStartEvent();

	/**
	 * Returns a new object of class '<em>UI Record Start Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>UI Record Start Event</em>'.
	 * @generated
	 */
	UIRecordStartEvent createUIRecordStartEvent();

	/**
	 * Returns a new object of class '<em>Task Navigate Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Task Navigate Event</em>'.
	 * @generated
	 */
	TaskNavigateEvent createTaskNavigateEvent();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	LoggingPackage getLoggingPackage();

} //LoggingFactory
