/**
 * <copyright>
 * </copyright>
 *
 * $Id: LoggingPackage.java,v 1.3 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see net.sourceforge.tagsea.logging.LoggingFactory
 * @model kind="package"
 * @generated
 */
public interface LoggingPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "logging";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "net.sourceforge.tagsea.logging";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "net.sourceforge.tagsea.logging";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	LoggingPackage eINSTANCE = net.sourceforge.tagsea.logging.impl.LoggingPackageImpl.init();

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.LogImpl <em>Log</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.LogImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getLog()
	 * @generated
	 */
	int LOG = 19;

	/**
	 * The feature id for the '<em><b>Uid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG__UID = 0;

	/**
	 * The feature id for the '<em><b>Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG__DATE = 1;

	/**
	 * The feature id for the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG__UNAME = 2;

	/**
	 * The feature id for the '<em><b>Country</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG__COUNTRY = 3;

	/**
	 * The feature id for the '<em><b>Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG__LANGUAGE = 4;

	/**
	 * The feature id for the '<em><b>Session</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG__SESSION = 5;

	/**
	 * The number of structural features of the '<em>Log</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.ModelLogImpl <em>Model Log</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.ModelLogImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getModelLog()
	 * @generated
	 */
	int MODEL_LOG = 0;

	/**
	 * The feature id for the '<em><b>Uid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_LOG__UID = LOG__UID;

	/**
	 * The feature id for the '<em><b>Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_LOG__DATE = LOG__DATE;

	/**
	 * The feature id for the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_LOG__UNAME = LOG__UNAME;

	/**
	 * The feature id for the '<em><b>Country</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_LOG__COUNTRY = LOG__COUNTRY;

	/**
	 * The feature id for the '<em><b>Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_LOG__LANGUAGE = LOG__LANGUAGE;

	/**
	 * The feature id for the '<em><b>Session</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_LOG__SESSION = LOG__SESSION;

	/**
	 * The feature id for the '<em><b>Events</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_LOG__EVENTS = LOG_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Model Log</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_LOG_FEATURE_COUNT = LOG_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.EventImpl <em>Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.EventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getEvent()
	 * @generated
	 */
	int EVENT = 1;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__TIME = 0;

	/**
	 * The number of structural features of the '<em>Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.ModelEventImpl <em>Model Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.ModelEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getModelEvent()
	 * @generated
	 */
	int MODEL_EVENT = 24;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_EVENT__TIME = EVENT__TIME;

	/**
	 * The number of structural features of the '<em>Model Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_EVENT_FEATURE_COUNT = EVENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.TagEventImpl <em>Tag Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.TagEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getTagEvent()
	 * @generated
	 */
	int TAG_EVENT = 2;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_EVENT__TIME = MODEL_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Tag Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_EVENT__TAG_NAME = MODEL_EVENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Tag Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_EVENT_FEATURE_COUNT = MODEL_EVENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.WaypointEventImpl <em>Waypoint Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.WaypointEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointEvent()
	 * @generated
	 */
	int WAYPOINT_EVENT = 3;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_EVENT__TIME = MODEL_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Waypoint</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_EVENT__WAYPOINT = MODEL_EVENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Waypoint Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_EVENT_FEATURE_COUNT = MODEL_EVENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.LogFileImpl <em>Log File</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.LogFileImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getLogFile()
	 * @generated
	 */
	int LOG_FILE = 4;

	/**
	 * The feature id for the '<em><b>Model Log</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_FILE__MODEL_LOG = 0;

	/**
	 * The feature id for the '<em><b>Ui Log</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_FILE__UI_LOG = 1;

	/**
	 * The feature id for the '<em><b>Current Waypoints</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_FILE__CURRENT_WAYPOINTS = 2;

	/**
	 * The number of structural features of the '<em>Log File</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_FILE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.WaypointStateImpl <em>Waypoint State</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.WaypointStateImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointState()
	 * @generated
	 */
	int WAYPOINT_STATE = 5;

	/**
	 * The feature id for the '<em><b>Waypoint Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_STATE__WAYPOINT_TYPE = 0;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_STATE__ATTRIBUTES = 1;

	/**
	 * The feature id for the '<em><b>Tag Names</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_STATE__TAG_NAMES = 2;

	/**
	 * The number of structural features of the '<em>Waypoint State</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_STATE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.UILogImpl <em>UI Log</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.UILogImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getUILog()
	 * @generated
	 */
	int UI_LOG = 6;

	/**
	 * The feature id for the '<em><b>Uid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_LOG__UID = LOG__UID;

	/**
	 * The feature id for the '<em><b>Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_LOG__DATE = LOG__DATE;

	/**
	 * The feature id for the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_LOG__UNAME = LOG__UNAME;

	/**
	 * The feature id for the '<em><b>Country</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_LOG__COUNTRY = LOG__COUNTRY;

	/**
	 * The feature id for the '<em><b>Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_LOG__LANGUAGE = LOG__LANGUAGE;

	/**
	 * The feature id for the '<em><b>Session</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_LOG__SESSION = LOG__SESSION;

	/**
	 * The feature id for the '<em><b>Events</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_LOG__EVENTS = LOG_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>UI Log</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_LOG_FEATURE_COUNT = LOG_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.CurrentWaypointsImpl <em>Current Waypoints</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.CurrentWaypointsImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getCurrentWaypoints()
	 * @generated
	 */
	int CURRENT_WAYPOINTS = 7;

	/**
	 * The feature id for the '<em><b>Uid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURRENT_WAYPOINTS__UID = LOG__UID;

	/**
	 * The feature id for the '<em><b>Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURRENT_WAYPOINTS__DATE = LOG__DATE;

	/**
	 * The feature id for the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURRENT_WAYPOINTS__UNAME = LOG__UNAME;

	/**
	 * The feature id for the '<em><b>Country</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURRENT_WAYPOINTS__COUNTRY = LOG__COUNTRY;

	/**
	 * The feature id for the '<em><b>Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURRENT_WAYPOINTS__LANGUAGE = LOG__LANGUAGE;

	/**
	 * The feature id for the '<em><b>Session</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURRENT_WAYPOINTS__SESSION = LOG__SESSION;

	/**
	 * The feature id for the '<em><b>Waypoint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURRENT_WAYPOINTS__WAYPOINT = LOG_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Current Waypoints</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURRENT_WAYPOINTS_FEATURE_COUNT = LOG_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.WaypointAttributeImpl <em>Waypoint Attribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.WaypointAttributeImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointAttribute()
	 * @generated
	 */
	int WAYPOINT_ATTRIBUTE = 8;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_ATTRIBUTE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_ATTRIBUTE__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Waypoint Attribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_ATTRIBUTE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.NewWaypointEventImpl <em>New Waypoint Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.NewWaypointEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getNewWaypointEvent()
	 * @generated
	 */
	int NEW_WAYPOINT_EVENT = 9;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEW_WAYPOINT_EVENT__TIME = WAYPOINT_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Waypoint</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEW_WAYPOINT_EVENT__WAYPOINT = WAYPOINT_EVENT__WAYPOINT;

	/**
	 * The number of structural features of the '<em>New Waypoint Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEW_WAYPOINT_EVENT_FEATURE_COUNT = WAYPOINT_EVENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.DeletedWaypointEventImpl <em>Deleted Waypoint Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.DeletedWaypointEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getDeletedWaypointEvent()
	 * @generated
	 */
	int DELETED_WAYPOINT_EVENT = 10;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETED_WAYPOINT_EVENT__TIME = WAYPOINT_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Waypoint</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETED_WAYPOINT_EVENT__WAYPOINT = WAYPOINT_EVENT__WAYPOINT;

	/**
	 * The number of structural features of the '<em>Deleted Waypoint Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETED_WAYPOINT_EVENT_FEATURE_COUNT = WAYPOINT_EVENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.WaypointChangeEventImpl <em>Waypoint Change Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.WaypointChangeEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointChangeEvent()
	 * @generated
	 */
	int WAYPOINT_CHANGE_EVENT = 11;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_CHANGE_EVENT__TIME = WAYPOINT_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Waypoint</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_CHANGE_EVENT__WAYPOINT = WAYPOINT_EVENT__WAYPOINT;

	/**
	 * The feature id for the '<em><b>New Attributes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_CHANGE_EVENT__NEW_ATTRIBUTES = WAYPOINT_EVENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Old Attributes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_CHANGE_EVENT__OLD_ATTRIBUTES = WAYPOINT_EVENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Waypoint Change Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_CHANGE_EVENT_FEATURE_COUNT = WAYPOINT_EVENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.TagNameChangeEventImpl <em>Tag Name Change Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.TagNameChangeEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getTagNameChangeEvent()
	 * @generated
	 */
	int TAG_NAME_CHANGE_EVENT = 12;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_NAME_CHANGE_EVENT__TIME = TAG_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Tag Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_NAME_CHANGE_EVENT__TAG_NAME = TAG_EVENT__TAG_NAME;

	/**
	 * The feature id for the '<em><b>Old Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_NAME_CHANGE_EVENT__OLD_NAME = TAG_EVENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Tag Name Change Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_NAME_CHANGE_EVENT_FEATURE_COUNT = TAG_EVENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.WaypointTagNameChangeEventImpl <em>Waypoint Tag Name Change Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.WaypointTagNameChangeEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointTagNameChangeEvent()
	 * @generated
	 */
	int WAYPOINT_TAG_NAME_CHANGE_EVENT = 13;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_TAG_NAME_CHANGE_EVENT__TIME = WAYPOINT_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Waypoint</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_TAG_NAME_CHANGE_EVENT__WAYPOINT = WAYPOINT_EVENT__WAYPOINT;

	/**
	 * The feature id for the '<em><b>New Tag Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_TAG_NAME_CHANGE_EVENT__NEW_TAG_NAME = WAYPOINT_EVENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Old Tag Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_TAG_NAME_CHANGE_EVENT__OLD_TAG_NAME = WAYPOINT_EVENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Waypoint Tag Name Change Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_TAG_NAME_CHANGE_EVENT_FEATURE_COUNT = WAYPOINT_EVENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.TagWaypointsChangedEventImpl <em>Tag Waypoints Changed Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.TagWaypointsChangedEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getTagWaypointsChangedEvent()
	 * @generated
	 */
	int TAG_WAYPOINTS_CHANGED_EVENT = 15;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.NewTagEventImpl <em>New Tag Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.NewTagEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getNewTagEvent()
	 * @generated
	 */
	int NEW_TAG_EVENT = 16;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.DeletedTagEventImpl <em>Deleted Tag Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.DeletedTagEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getDeletedTagEvent()
	 * @generated
	 */
	int DELETED_TAG_EVENT = 17;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.JobEventImpl <em>Job Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.JobEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getJobEvent()
	 * @generated
	 */
	int JOB_EVENT = 18;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.UIEventImpl <em>UI Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.UIEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getUIEvent()
	 * @generated
	 */
	int UI_EVENT = 23;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.NavigateEventImpl <em>Navigate Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.NavigateEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getNavigateEvent()
	 * @generated
	 */
	int NAVIGATE_EVENT = 20;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.ViewEventImpl <em>View Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.ViewEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getViewEvent()
	 * @generated
	 */
	int VIEW_EVENT = 21;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.FiltersChangedEventImpl <em>Filters Changed Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.FiltersChangedEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getFiltersChangedEvent()
	 * @generated
	 */
	int FILTERS_CHANGED_EVENT = 22;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.WaypointTagsChangedEventImpl <em>Waypoint Tags Changed Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.WaypointTagsChangedEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointTagsChangedEvent()
	 * @generated
	 */
	int WAYPOINT_TAGS_CHANGED_EVENT = 14;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_TAGS_CHANGED_EVENT__TIME = WAYPOINT_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Waypoint</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_TAGS_CHANGED_EVENT__WAYPOINT = WAYPOINT_EVENT__WAYPOINT;

	/**
	 * The feature id for the '<em><b>Old Tags</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_TAGS_CHANGED_EVENT__OLD_TAGS = WAYPOINT_EVENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Waypoint Tags Changed Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAYPOINT_TAGS_CHANGED_EVENT_FEATURE_COUNT = WAYPOINT_EVENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_WAYPOINTS_CHANGED_EVENT__TIME = TAG_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Tag Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_WAYPOINTS_CHANGED_EVENT__TAG_NAME = TAG_EVENT__TAG_NAME;

	/**
	 * The feature id for the '<em><b>Old Waypoint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_WAYPOINTS_CHANGED_EVENT__OLD_WAYPOINT = TAG_EVENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>New Waypoint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_WAYPOINTS_CHANGED_EVENT__NEW_WAYPOINT = TAG_EVENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Tag Waypoints Changed Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_WAYPOINTS_CHANGED_EVENT_FEATURE_COUNT = TAG_EVENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEW_TAG_EVENT__TIME = TAG_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Tag Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEW_TAG_EVENT__TAG_NAME = TAG_EVENT__TAG_NAME;

	/**
	 * The number of structural features of the '<em>New Tag Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEW_TAG_EVENT_FEATURE_COUNT = TAG_EVENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETED_TAG_EVENT__TIME = TAG_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Tag Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETED_TAG_EVENT__TAG_NAME = TAG_EVENT__TAG_NAME;

	/**
	 * The number of structural features of the '<em>Deleted Tag Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETED_TAG_EVENT_FEATURE_COUNT = TAG_EVENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOB_EVENT__TIME = MODEL_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOB_EVENT__STATE = MODEL_EVENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOB_EVENT__NAME = MODEL_EVENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Job Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOB_EVENT_FEATURE_COUNT = MODEL_EVENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_EVENT__TIME = EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Hierarchy On</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_EVENT__HIERARCHY_ON = EVENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>UI Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_EVENT_FEATURE_COUNT = EVENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAVIGATE_EVENT__TIME = UI_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Hierarchy On</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAVIGATE_EVENT__HIERARCHY_ON = UI_EVENT__HIERARCHY_ON;

	/**
	 * The feature id for the '<em><b>Waypoint</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAVIGATE_EVENT__WAYPOINT = UI_EVENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Navigate Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAVIGATE_EVENT_FEATURE_COUNT = UI_EVENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_EVENT__TIME = UI_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Hierarchy On</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_EVENT__HIERARCHY_ON = UI_EVENT__HIERARCHY_ON;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_EVENT__TYPE = UI_EVENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Viewid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_EVENT__VIEWID = UI_EVENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Filter String</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_EVENT__FILTER_STRING = UI_EVENT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>View Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_EVENT_FEATURE_COUNT = UI_EVENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTERS_CHANGED_EVENT__TIME = UI_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Hierarchy On</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTERS_CHANGED_EVENT__HIERARCHY_ON = UI_EVENT__HIERARCHY_ON;

	/**
	 * The feature id for the '<em><b>Visible</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTERS_CHANGED_EVENT__VISIBLE = UI_EVENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Hidden</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTERS_CHANGED_EVENT__HIDDEN = UI_EVENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Filters Changed Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTERS_CHANGED_EVENT_FEATURE_COUNT = UI_EVENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.ModelRecordStartEventImpl <em>Model Record Start Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.ModelRecordStartEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getModelRecordStartEvent()
	 * @generated
	 */
	int MODEL_RECORD_START_EVENT = 25;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_RECORD_START_EVENT__TIME = MODEL_EVENT__TIME;

	/**
	 * The number of structural features of the '<em>Model Record Start Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_RECORD_START_EVENT_FEATURE_COUNT = MODEL_EVENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.UIRecordStartEventImpl <em>UI Record Start Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.UIRecordStartEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getUIRecordStartEvent()
	 * @generated
	 */
	int UI_RECORD_START_EVENT = 26;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_RECORD_START_EVENT__TIME = UI_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Hierarchy On</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_RECORD_START_EVENT__HIERARCHY_ON = UI_EVENT__HIERARCHY_ON;

	/**
	 * The number of structural features of the '<em>UI Record Start Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UI_RECORD_START_EVENT_FEATURE_COUNT = UI_EVENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.impl.TaskNavigateEventImpl <em>Task Navigate Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.impl.TaskNavigateEventImpl
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getTaskNavigateEvent()
	 * @generated
	 */
	int TASK_NAVIGATE_EVENT = 27;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TASK_NAVIGATE_EVENT__TIME = UI_EVENT__TIME;

	/**
	 * The feature id for the '<em><b>Hierarchy On</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TASK_NAVIGATE_EVENT__HIERARCHY_ON = UI_EVENT__HIERARCHY_ON;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TASK_NAVIGATE_EVENT__DESCRIPTION = UI_EVENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Line</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TASK_NAVIGATE_EVENT__LINE = UI_EVENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Resource</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TASK_NAVIGATE_EVENT__RESOURCE = UI_EVENT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Task Navigate Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TASK_NAVIGATE_EVENT_FEATURE_COUNT = UI_EVENT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.JobState <em>Job State</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.JobState
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getJobState()
	 * @generated
	 */
	int JOB_STATE = 28;

	/**
	 * The meta object id for the '{@link net.sourceforge.tagsea.logging.ViewEventType <em>View Event Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see net.sourceforge.tagsea.logging.ViewEventType
	 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getViewEventType()
	 * @generated
	 */
	int VIEW_EVENT_TYPE = 29;


	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.ModelLog <em>Model Log</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model Log</em>'.
	 * @see net.sourceforge.tagsea.logging.ModelLog
	 * @generated
	 */
	EClass getModelLog();

	/**
	 * Returns the meta object for the containment reference list '{@link net.sourceforge.tagsea.logging.ModelLog#getEvents <em>Events</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Events</em>'.
	 * @see net.sourceforge.tagsea.logging.ModelLog#getEvents()
	 * @see #getModelLog()
	 * @generated
	 */
	EReference getModelLog_Events();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.Event <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event</em>'.
	 * @see net.sourceforge.tagsea.logging.Event
	 * @generated
	 */
	EClass getEvent();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.Event#getTime <em>Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time</em>'.
	 * @see net.sourceforge.tagsea.logging.Event#getTime()
	 * @see #getEvent()
	 * @generated
	 */
	EAttribute getEvent_Time();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.TagEvent <em>Tag Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tag Event</em>'.
	 * @see net.sourceforge.tagsea.logging.TagEvent
	 * @generated
	 */
	EClass getTagEvent();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.TagEvent#getTagName <em>Tag Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Tag Name</em>'.
	 * @see net.sourceforge.tagsea.logging.TagEvent#getTagName()
	 * @see #getTagEvent()
	 * @generated
	 */
	EAttribute getTagEvent_TagName();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.WaypointEvent <em>Waypoint Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Waypoint Event</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointEvent
	 * @generated
	 */
	EClass getWaypointEvent();

	/**
	 * Returns the meta object for the containment reference '{@link net.sourceforge.tagsea.logging.WaypointEvent#getWaypoint <em>Waypoint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Waypoint</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointEvent#getWaypoint()
	 * @see #getWaypointEvent()
	 * @generated
	 */
	EReference getWaypointEvent_Waypoint();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.LogFile <em>Log File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Log File</em>'.
	 * @see net.sourceforge.tagsea.logging.LogFile
	 * @generated
	 */
	EClass getLogFile();

	/**
	 * Returns the meta object for the containment reference '{@link net.sourceforge.tagsea.logging.LogFile#getModelLog <em>Model Log</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Model Log</em>'.
	 * @see net.sourceforge.tagsea.logging.LogFile#getModelLog()
	 * @see #getLogFile()
	 * @generated
	 */
	EReference getLogFile_ModelLog();

	/**
	 * Returns the meta object for the containment reference list '{@link net.sourceforge.tagsea.logging.LogFile#getUiLog <em>Ui Log</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Ui Log</em>'.
	 * @see net.sourceforge.tagsea.logging.LogFile#getUiLog()
	 * @see #getLogFile()
	 * @generated
	 */
	EReference getLogFile_UiLog();

	/**
	 * Returns the meta object for the containment reference '{@link net.sourceforge.tagsea.logging.LogFile#getCurrentWaypoints <em>Current Waypoints</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Current Waypoints</em>'.
	 * @see net.sourceforge.tagsea.logging.LogFile#getCurrentWaypoints()
	 * @see #getLogFile()
	 * @generated
	 */
	EReference getLogFile_CurrentWaypoints();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.WaypointState <em>Waypoint State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Waypoint State</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointState
	 * @generated
	 */
	EClass getWaypointState();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.WaypointState#getWaypointType <em>Waypoint Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Waypoint Type</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointState#getWaypointType()
	 * @see #getWaypointState()
	 * @generated
	 */
	EAttribute getWaypointState_WaypointType();

	/**
	 * Returns the meta object for the containment reference list '{@link net.sourceforge.tagsea.logging.WaypointState#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attributes</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointState#getAttributes()
	 * @see #getWaypointState()
	 * @generated
	 */
	EReference getWaypointState_Attributes();

	/**
	 * Returns the meta object for the attribute list '{@link net.sourceforge.tagsea.logging.WaypointState#getTagNames <em>Tag Names</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Tag Names</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointState#getTagNames()
	 * @see #getWaypointState()
	 * @generated
	 */
	EAttribute getWaypointState_TagNames();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.UILog <em>UI Log</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>UI Log</em>'.
	 * @see net.sourceforge.tagsea.logging.UILog
	 * @generated
	 */
	EClass getUILog();

	/**
	 * Returns the meta object for the containment reference list '{@link net.sourceforge.tagsea.logging.UILog#getEvents <em>Events</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Events</em>'.
	 * @see net.sourceforge.tagsea.logging.UILog#getEvents()
	 * @see #getUILog()
	 * @generated
	 */
	EReference getUILog_Events();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.CurrentWaypoints <em>Current Waypoints</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Current Waypoints</em>'.
	 * @see net.sourceforge.tagsea.logging.CurrentWaypoints
	 * @generated
	 */
	EClass getCurrentWaypoints();

	/**
	 * Returns the meta object for the containment reference list '{@link net.sourceforge.tagsea.logging.CurrentWaypoints#getWaypoint <em>Waypoint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Waypoint</em>'.
	 * @see net.sourceforge.tagsea.logging.CurrentWaypoints#getWaypoint()
	 * @see #getCurrentWaypoints()
	 * @generated
	 */
	EReference getCurrentWaypoints_Waypoint();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.WaypointAttribute <em>Waypoint Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Waypoint Attribute</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointAttribute
	 * @generated
	 */
	EClass getWaypointAttribute();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.WaypointAttribute#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointAttribute#getName()
	 * @see #getWaypointAttribute()
	 * @generated
	 */
	EAttribute getWaypointAttribute_Name();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.WaypointAttribute#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointAttribute#getValue()
	 * @see #getWaypointAttribute()
	 * @generated
	 */
	EAttribute getWaypointAttribute_Value();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.NewWaypointEvent <em>New Waypoint Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>New Waypoint Event</em>'.
	 * @see net.sourceforge.tagsea.logging.NewWaypointEvent
	 * @generated
	 */
	EClass getNewWaypointEvent();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.DeletedWaypointEvent <em>Deleted Waypoint Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Deleted Waypoint Event</em>'.
	 * @see net.sourceforge.tagsea.logging.DeletedWaypointEvent
	 * @generated
	 */
	EClass getDeletedWaypointEvent();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.WaypointChangeEvent <em>Waypoint Change Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Waypoint Change Event</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointChangeEvent
	 * @generated
	 */
	EClass getWaypointChangeEvent();

	/**
	 * Returns the meta object for the containment reference list '{@link net.sourceforge.tagsea.logging.WaypointChangeEvent#getNewAttributes <em>New Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>New Attributes</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointChangeEvent#getNewAttributes()
	 * @see #getWaypointChangeEvent()
	 * @generated
	 */
	EReference getWaypointChangeEvent_NewAttributes();

	/**
	 * Returns the meta object for the containment reference list '{@link net.sourceforge.tagsea.logging.WaypointChangeEvent#getOldAttributes <em>Old Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Old Attributes</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointChangeEvent#getOldAttributes()
	 * @see #getWaypointChangeEvent()
	 * @generated
	 */
	EReference getWaypointChangeEvent_OldAttributes();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.TagNameChangeEvent <em>Tag Name Change Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tag Name Change Event</em>'.
	 * @see net.sourceforge.tagsea.logging.TagNameChangeEvent
	 * @generated
	 */
	EClass getTagNameChangeEvent();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.TagNameChangeEvent#getOldName <em>Old Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Old Name</em>'.
	 * @see net.sourceforge.tagsea.logging.TagNameChangeEvent#getOldName()
	 * @see #getTagNameChangeEvent()
	 * @generated
	 */
	EAttribute getTagNameChangeEvent_OldName();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent <em>Waypoint Tag Name Change Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Waypoint Tag Name Change Event</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent
	 * @generated
	 */
	EClass getWaypointTagNameChangeEvent();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent#getNewTagName <em>New Tag Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>New Tag Name</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent#getNewTagName()
	 * @see #getWaypointTagNameChangeEvent()
	 * @generated
	 */
	EAttribute getWaypointTagNameChangeEvent_NewTagName();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent#getOldTagName <em>Old Tag Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Old Tag Name</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent#getOldTagName()
	 * @see #getWaypointTagNameChangeEvent()
	 * @generated
	 */
	EAttribute getWaypointTagNameChangeEvent_OldTagName();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.TagWaypointsChangedEvent <em>Tag Waypoints Changed Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tag Waypoints Changed Event</em>'.
	 * @see net.sourceforge.tagsea.logging.TagWaypointsChangedEvent
	 * @generated
	 */
	EClass getTagWaypointsChangedEvent();

	/**
	 * Returns the meta object for the containment reference list '{@link net.sourceforge.tagsea.logging.TagWaypointsChangedEvent#getOldWaypoint <em>Old Waypoint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Old Waypoint</em>'.
	 * @see net.sourceforge.tagsea.logging.TagWaypointsChangedEvent#getOldWaypoint()
	 * @see #getTagWaypointsChangedEvent()
	 * @generated
	 */
	EReference getTagWaypointsChangedEvent_OldWaypoint();

	/**
	 * Returns the meta object for the containment reference list '{@link net.sourceforge.tagsea.logging.TagWaypointsChangedEvent#getNewWaypoint <em>New Waypoint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>New Waypoint</em>'.
	 * @see net.sourceforge.tagsea.logging.TagWaypointsChangedEvent#getNewWaypoint()
	 * @see #getTagWaypointsChangedEvent()
	 * @generated
	 */
	EReference getTagWaypointsChangedEvent_NewWaypoint();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.NewTagEvent <em>New Tag Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>New Tag Event</em>'.
	 * @see net.sourceforge.tagsea.logging.NewTagEvent
	 * @generated
	 */
	EClass getNewTagEvent();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.DeletedTagEvent <em>Deleted Tag Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Deleted Tag Event</em>'.
	 * @see net.sourceforge.tagsea.logging.DeletedTagEvent
	 * @generated
	 */
	EClass getDeletedTagEvent();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.JobEvent <em>Job Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Job Event</em>'.
	 * @see net.sourceforge.tagsea.logging.JobEvent
	 * @generated
	 */
	EClass getJobEvent();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.JobEvent#getState <em>State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>State</em>'.
	 * @see net.sourceforge.tagsea.logging.JobEvent#getState()
	 * @see #getJobEvent()
	 * @generated
	 */
	EAttribute getJobEvent_State();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.JobEvent#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see net.sourceforge.tagsea.logging.JobEvent#getName()
	 * @see #getJobEvent()
	 * @generated
	 */
	EAttribute getJobEvent_Name();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.Log <em>Log</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Log</em>'.
	 * @see net.sourceforge.tagsea.logging.Log
	 * @generated
	 */
	EClass getLog();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.Log#getUid <em>Uid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uid</em>'.
	 * @see net.sourceforge.tagsea.logging.Log#getUid()
	 * @see #getLog()
	 * @generated
	 */
	EAttribute getLog_Uid();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.Log#getDate <em>Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Date</em>'.
	 * @see net.sourceforge.tagsea.logging.Log#getDate()
	 * @see #getLog()
	 * @generated
	 */
	EAttribute getLog_Date();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.Log#getUname <em>Uname</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uname</em>'.
	 * @see net.sourceforge.tagsea.logging.Log#getUname()
	 * @see #getLog()
	 * @generated
	 */
	EAttribute getLog_Uname();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.Log#getCountry <em>Country</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Country</em>'.
	 * @see net.sourceforge.tagsea.logging.Log#getCountry()
	 * @see #getLog()
	 * @generated
	 */
	EAttribute getLog_Country();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.Log#getLanguage <em>Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Language</em>'.
	 * @see net.sourceforge.tagsea.logging.Log#getLanguage()
	 * @see #getLog()
	 * @generated
	 */
	EAttribute getLog_Language();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.Log#getSession <em>Session</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Session</em>'.
	 * @see net.sourceforge.tagsea.logging.Log#getSession()
	 * @see #getLog()
	 * @generated
	 */
	EAttribute getLog_Session();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.NavigateEvent <em>Navigate Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Navigate Event</em>'.
	 * @see net.sourceforge.tagsea.logging.NavigateEvent
	 * @generated
	 */
	EClass getNavigateEvent();

	/**
	 * Returns the meta object for the containment reference '{@link net.sourceforge.tagsea.logging.NavigateEvent#getWaypoint <em>Waypoint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Waypoint</em>'.
	 * @see net.sourceforge.tagsea.logging.NavigateEvent#getWaypoint()
	 * @see #getNavigateEvent()
	 * @generated
	 */
	EReference getNavigateEvent_Waypoint();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.ViewEvent <em>View Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>View Event</em>'.
	 * @see net.sourceforge.tagsea.logging.ViewEvent
	 * @generated
	 */
	EClass getViewEvent();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.ViewEvent#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see net.sourceforge.tagsea.logging.ViewEvent#getType()
	 * @see #getViewEvent()
	 * @generated
	 */
	EAttribute getViewEvent_Type();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.ViewEvent#getViewid <em>Viewid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Viewid</em>'.
	 * @see net.sourceforge.tagsea.logging.ViewEvent#getViewid()
	 * @see #getViewEvent()
	 * @generated
	 */
	EAttribute getViewEvent_Viewid();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.ViewEvent#getFilterString <em>Filter String</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Filter String</em>'.
	 * @see net.sourceforge.tagsea.logging.ViewEvent#getFilterString()
	 * @see #getViewEvent()
	 * @generated
	 */
	EAttribute getViewEvent_FilterString();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.FiltersChangedEvent <em>Filters Changed Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Filters Changed Event</em>'.
	 * @see net.sourceforge.tagsea.logging.FiltersChangedEvent
	 * @generated
	 */
	EClass getFiltersChangedEvent();

	/**
	 * Returns the meta object for the attribute list '{@link net.sourceforge.tagsea.logging.FiltersChangedEvent#getVisible <em>Visible</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Visible</em>'.
	 * @see net.sourceforge.tagsea.logging.FiltersChangedEvent#getVisible()
	 * @see #getFiltersChangedEvent()
	 * @generated
	 */
	EAttribute getFiltersChangedEvent_Visible();

	/**
	 * Returns the meta object for the attribute list '{@link net.sourceforge.tagsea.logging.FiltersChangedEvent#getHidden <em>Hidden</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Hidden</em>'.
	 * @see net.sourceforge.tagsea.logging.FiltersChangedEvent#getHidden()
	 * @see #getFiltersChangedEvent()
	 * @generated
	 */
	EAttribute getFiltersChangedEvent_Hidden();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.UIEvent <em>UI Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>UI Event</em>'.
	 * @see net.sourceforge.tagsea.logging.UIEvent
	 * @generated
	 */
	EClass getUIEvent();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.UIEvent#isHierarchyOn <em>Hierarchy On</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Hierarchy On</em>'.
	 * @see net.sourceforge.tagsea.logging.UIEvent#isHierarchyOn()
	 * @see #getUIEvent()
	 * @generated
	 */
	EAttribute getUIEvent_HierarchyOn();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.ModelEvent <em>Model Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model Event</em>'.
	 * @see net.sourceforge.tagsea.logging.ModelEvent
	 * @generated
	 */
	EClass getModelEvent();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.WaypointTagsChangedEvent <em>Waypoint Tags Changed Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Waypoint Tags Changed Event</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointTagsChangedEvent
	 * @generated
	 */
	EClass getWaypointTagsChangedEvent();

	/**
	 * Returns the meta object for the attribute list '{@link net.sourceforge.tagsea.logging.WaypointTagsChangedEvent#getOldTags <em>Old Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Old Tags</em>'.
	 * @see net.sourceforge.tagsea.logging.WaypointTagsChangedEvent#getOldTags()
	 * @see #getWaypointTagsChangedEvent()
	 * @generated
	 */
	EAttribute getWaypointTagsChangedEvent_OldTags();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.ModelRecordStartEvent <em>Model Record Start Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model Record Start Event</em>'.
	 * @see net.sourceforge.tagsea.logging.ModelRecordStartEvent
	 * @generated
	 */
	EClass getModelRecordStartEvent();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.UIRecordStartEvent <em>UI Record Start Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>UI Record Start Event</em>'.
	 * @see net.sourceforge.tagsea.logging.UIRecordStartEvent
	 * @generated
	 */
	EClass getUIRecordStartEvent();

	/**
	 * Returns the meta object for class '{@link net.sourceforge.tagsea.logging.TaskNavigateEvent <em>Task Navigate Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Task Navigate Event</em>'.
	 * @see net.sourceforge.tagsea.logging.TaskNavigateEvent
	 * @generated
	 */
	EClass getTaskNavigateEvent();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.TaskNavigateEvent#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see net.sourceforge.tagsea.logging.TaskNavigateEvent#getDescription()
	 * @see #getTaskNavigateEvent()
	 * @generated
	 */
	EAttribute getTaskNavigateEvent_Description();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.TaskNavigateEvent#getLine <em>Line</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Line</em>'.
	 * @see net.sourceforge.tagsea.logging.TaskNavigateEvent#getLine()
	 * @see #getTaskNavigateEvent()
	 * @generated
	 */
	EAttribute getTaskNavigateEvent_Line();

	/**
	 * Returns the meta object for the attribute '{@link net.sourceforge.tagsea.logging.TaskNavigateEvent#getResource <em>Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Resource</em>'.
	 * @see net.sourceforge.tagsea.logging.TaskNavigateEvent#getResource()
	 * @see #getTaskNavigateEvent()
	 * @generated
	 */
	EAttribute getTaskNavigateEvent_Resource();

	/**
	 * Returns the meta object for enum '{@link net.sourceforge.tagsea.logging.JobState <em>Job State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Job State</em>'.
	 * @see net.sourceforge.tagsea.logging.JobState
	 * @generated
	 */
	EEnum getJobState();

	/**
	 * Returns the meta object for enum '{@link net.sourceforge.tagsea.logging.ViewEventType <em>View Event Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>View Event Type</em>'.
	 * @see net.sourceforge.tagsea.logging.ViewEventType
	 * @generated
	 */
	EEnum getViewEventType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	LoggingFactory getLoggingFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals  {
		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.ModelLogImpl <em>Model Log</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.ModelLogImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getModelLog()
		 * @generated
		 */
		EClass MODEL_LOG = eINSTANCE.getModelLog();

		/**
		 * The meta object literal for the '<em><b>Events</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL_LOG__EVENTS = eINSTANCE.getModelLog_Events();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.EventImpl <em>Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.EventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getEvent()
		 * @generated
		 */
		EClass EVENT = eINSTANCE.getEvent();

		/**
		 * The meta object literal for the '<em><b>Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT__TIME = eINSTANCE.getEvent_Time();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.TagEventImpl <em>Tag Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.TagEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getTagEvent()
		 * @generated
		 */
		EClass TAG_EVENT = eINSTANCE.getTagEvent();

		/**
		 * The meta object literal for the '<em><b>Tag Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TAG_EVENT__TAG_NAME = eINSTANCE.getTagEvent_TagName();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.WaypointEventImpl <em>Waypoint Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.WaypointEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointEvent()
		 * @generated
		 */
		EClass WAYPOINT_EVENT = eINSTANCE.getWaypointEvent();

		/**
		 * The meta object literal for the '<em><b>Waypoint</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WAYPOINT_EVENT__WAYPOINT = eINSTANCE.getWaypointEvent_Waypoint();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.LogFileImpl <em>Log File</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.LogFileImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getLogFile()
		 * @generated
		 */
		EClass LOG_FILE = eINSTANCE.getLogFile();

		/**
		 * The meta object literal for the '<em><b>Model Log</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOG_FILE__MODEL_LOG = eINSTANCE.getLogFile_ModelLog();

		/**
		 * The meta object literal for the '<em><b>Ui Log</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOG_FILE__UI_LOG = eINSTANCE.getLogFile_UiLog();

		/**
		 * The meta object literal for the '<em><b>Current Waypoints</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOG_FILE__CURRENT_WAYPOINTS = eINSTANCE.getLogFile_CurrentWaypoints();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.WaypointStateImpl <em>Waypoint State</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.WaypointStateImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointState()
		 * @generated
		 */
		EClass WAYPOINT_STATE = eINSTANCE.getWaypointState();

		/**
		 * The meta object literal for the '<em><b>Waypoint Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WAYPOINT_STATE__WAYPOINT_TYPE = eINSTANCE.getWaypointState_WaypointType();

		/**
		 * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WAYPOINT_STATE__ATTRIBUTES = eINSTANCE.getWaypointState_Attributes();

		/**
		 * The meta object literal for the '<em><b>Tag Names</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WAYPOINT_STATE__TAG_NAMES = eINSTANCE.getWaypointState_TagNames();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.UILogImpl <em>UI Log</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.UILogImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getUILog()
		 * @generated
		 */
		EClass UI_LOG = eINSTANCE.getUILog();

		/**
		 * The meta object literal for the '<em><b>Events</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UI_LOG__EVENTS = eINSTANCE.getUILog_Events();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.CurrentWaypointsImpl <em>Current Waypoints</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.CurrentWaypointsImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getCurrentWaypoints()
		 * @generated
		 */
		EClass CURRENT_WAYPOINTS = eINSTANCE.getCurrentWaypoints();

		/**
		 * The meta object literal for the '<em><b>Waypoint</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CURRENT_WAYPOINTS__WAYPOINT = eINSTANCE.getCurrentWaypoints_Waypoint();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.WaypointAttributeImpl <em>Waypoint Attribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.WaypointAttributeImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointAttribute()
		 * @generated
		 */
		EClass WAYPOINT_ATTRIBUTE = eINSTANCE.getWaypointAttribute();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WAYPOINT_ATTRIBUTE__NAME = eINSTANCE.getWaypointAttribute_Name();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WAYPOINT_ATTRIBUTE__VALUE = eINSTANCE.getWaypointAttribute_Value();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.NewWaypointEventImpl <em>New Waypoint Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.NewWaypointEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getNewWaypointEvent()
		 * @generated
		 */
		EClass NEW_WAYPOINT_EVENT = eINSTANCE.getNewWaypointEvent();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.DeletedWaypointEventImpl <em>Deleted Waypoint Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.DeletedWaypointEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getDeletedWaypointEvent()
		 * @generated
		 */
		EClass DELETED_WAYPOINT_EVENT = eINSTANCE.getDeletedWaypointEvent();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.WaypointChangeEventImpl <em>Waypoint Change Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.WaypointChangeEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointChangeEvent()
		 * @generated
		 */
		EClass WAYPOINT_CHANGE_EVENT = eINSTANCE.getWaypointChangeEvent();

		/**
		 * The meta object literal for the '<em><b>New Attributes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WAYPOINT_CHANGE_EVENT__NEW_ATTRIBUTES = eINSTANCE.getWaypointChangeEvent_NewAttributes();

		/**
		 * The meta object literal for the '<em><b>Old Attributes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WAYPOINT_CHANGE_EVENT__OLD_ATTRIBUTES = eINSTANCE.getWaypointChangeEvent_OldAttributes();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.TagNameChangeEventImpl <em>Tag Name Change Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.TagNameChangeEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getTagNameChangeEvent()
		 * @generated
		 */
		EClass TAG_NAME_CHANGE_EVENT = eINSTANCE.getTagNameChangeEvent();

		/**
		 * The meta object literal for the '<em><b>Old Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TAG_NAME_CHANGE_EVENT__OLD_NAME = eINSTANCE.getTagNameChangeEvent_OldName();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.WaypointTagNameChangeEventImpl <em>Waypoint Tag Name Change Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.WaypointTagNameChangeEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointTagNameChangeEvent()
		 * @generated
		 */
		EClass WAYPOINT_TAG_NAME_CHANGE_EVENT = eINSTANCE.getWaypointTagNameChangeEvent();

		/**
		 * The meta object literal for the '<em><b>New Tag Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WAYPOINT_TAG_NAME_CHANGE_EVENT__NEW_TAG_NAME = eINSTANCE.getWaypointTagNameChangeEvent_NewTagName();

		/**
		 * The meta object literal for the '<em><b>Old Tag Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WAYPOINT_TAG_NAME_CHANGE_EVENT__OLD_TAG_NAME = eINSTANCE.getWaypointTagNameChangeEvent_OldTagName();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.TagWaypointsChangedEventImpl <em>Tag Waypoints Changed Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.TagWaypointsChangedEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getTagWaypointsChangedEvent()
		 * @generated
		 */
		EClass TAG_WAYPOINTS_CHANGED_EVENT = eINSTANCE.getTagWaypointsChangedEvent();

		/**
		 * The meta object literal for the '<em><b>Old Waypoint</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TAG_WAYPOINTS_CHANGED_EVENT__OLD_WAYPOINT = eINSTANCE.getTagWaypointsChangedEvent_OldWaypoint();

		/**
		 * The meta object literal for the '<em><b>New Waypoint</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TAG_WAYPOINTS_CHANGED_EVENT__NEW_WAYPOINT = eINSTANCE.getTagWaypointsChangedEvent_NewWaypoint();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.NewTagEventImpl <em>New Tag Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.NewTagEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getNewTagEvent()
		 * @generated
		 */
		EClass NEW_TAG_EVENT = eINSTANCE.getNewTagEvent();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.DeletedTagEventImpl <em>Deleted Tag Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.DeletedTagEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getDeletedTagEvent()
		 * @generated
		 */
		EClass DELETED_TAG_EVENT = eINSTANCE.getDeletedTagEvent();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.JobEventImpl <em>Job Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.JobEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getJobEvent()
		 * @generated
		 */
		EClass JOB_EVENT = eINSTANCE.getJobEvent();

		/**
		 * The meta object literal for the '<em><b>State</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JOB_EVENT__STATE = eINSTANCE.getJobEvent_State();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JOB_EVENT__NAME = eINSTANCE.getJobEvent_Name();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.LogImpl <em>Log</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.LogImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getLog()
		 * @generated
		 */
		EClass LOG = eINSTANCE.getLog();

		/**
		 * The meta object literal for the '<em><b>Uid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOG__UID = eINSTANCE.getLog_Uid();

		/**
		 * The meta object literal for the '<em><b>Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOG__DATE = eINSTANCE.getLog_Date();

		/**
		 * The meta object literal for the '<em><b>Uname</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOG__UNAME = eINSTANCE.getLog_Uname();

		/**
		 * The meta object literal for the '<em><b>Country</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOG__COUNTRY = eINSTANCE.getLog_Country();

		/**
		 * The meta object literal for the '<em><b>Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOG__LANGUAGE = eINSTANCE.getLog_Language();

		/**
		 * The meta object literal for the '<em><b>Session</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOG__SESSION = eINSTANCE.getLog_Session();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.NavigateEventImpl <em>Navigate Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.NavigateEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getNavigateEvent()
		 * @generated
		 */
		EClass NAVIGATE_EVENT = eINSTANCE.getNavigateEvent();

		/**
		 * The meta object literal for the '<em><b>Waypoint</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAVIGATE_EVENT__WAYPOINT = eINSTANCE.getNavigateEvent_Waypoint();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.ViewEventImpl <em>View Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.ViewEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getViewEvent()
		 * @generated
		 */
		EClass VIEW_EVENT = eINSTANCE.getViewEvent();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VIEW_EVENT__TYPE = eINSTANCE.getViewEvent_Type();

		/**
		 * The meta object literal for the '<em><b>Viewid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VIEW_EVENT__VIEWID = eINSTANCE.getViewEvent_Viewid();

		/**
		 * The meta object literal for the '<em><b>Filter String</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VIEW_EVENT__FILTER_STRING = eINSTANCE.getViewEvent_FilterString();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.FiltersChangedEventImpl <em>Filters Changed Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.FiltersChangedEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getFiltersChangedEvent()
		 * @generated
		 */
		EClass FILTERS_CHANGED_EVENT = eINSTANCE.getFiltersChangedEvent();

		/**
		 * The meta object literal for the '<em><b>Visible</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILTERS_CHANGED_EVENT__VISIBLE = eINSTANCE.getFiltersChangedEvent_Visible();

		/**
		 * The meta object literal for the '<em><b>Hidden</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILTERS_CHANGED_EVENT__HIDDEN = eINSTANCE.getFiltersChangedEvent_Hidden();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.UIEventImpl <em>UI Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.UIEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getUIEvent()
		 * @generated
		 */
		EClass UI_EVENT = eINSTANCE.getUIEvent();

		/**
		 * The meta object literal for the '<em><b>Hierarchy On</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UI_EVENT__HIERARCHY_ON = eINSTANCE.getUIEvent_HierarchyOn();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.ModelEventImpl <em>Model Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.ModelEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getModelEvent()
		 * @generated
		 */
		EClass MODEL_EVENT = eINSTANCE.getModelEvent();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.WaypointTagsChangedEventImpl <em>Waypoint Tags Changed Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.WaypointTagsChangedEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getWaypointTagsChangedEvent()
		 * @generated
		 */
		EClass WAYPOINT_TAGS_CHANGED_EVENT = eINSTANCE.getWaypointTagsChangedEvent();

		/**
		 * The meta object literal for the '<em><b>Old Tags</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WAYPOINT_TAGS_CHANGED_EVENT__OLD_TAGS = eINSTANCE.getWaypointTagsChangedEvent_OldTags();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.ModelRecordStartEventImpl <em>Model Record Start Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.ModelRecordStartEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getModelRecordStartEvent()
		 * @generated
		 */
		EClass MODEL_RECORD_START_EVENT = eINSTANCE.getModelRecordStartEvent();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.UIRecordStartEventImpl <em>UI Record Start Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.UIRecordStartEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getUIRecordStartEvent()
		 * @generated
		 */
		EClass UI_RECORD_START_EVENT = eINSTANCE.getUIRecordStartEvent();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.impl.TaskNavigateEventImpl <em>Task Navigate Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.impl.TaskNavigateEventImpl
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getTaskNavigateEvent()
		 * @generated
		 */
		EClass TASK_NAVIGATE_EVENT = eINSTANCE.getTaskNavigateEvent();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TASK_NAVIGATE_EVENT__DESCRIPTION = eINSTANCE.getTaskNavigateEvent_Description();

		/**
		 * The meta object literal for the '<em><b>Line</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TASK_NAVIGATE_EVENT__LINE = eINSTANCE.getTaskNavigateEvent_Line();

		/**
		 * The meta object literal for the '<em><b>Resource</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TASK_NAVIGATE_EVENT__RESOURCE = eINSTANCE.getTaskNavigateEvent_Resource();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.JobState <em>Job State</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.JobState
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getJobState()
		 * @generated
		 */
		EEnum JOB_STATE = eINSTANCE.getJobState();

		/**
		 * The meta object literal for the '{@link net.sourceforge.tagsea.logging.ViewEventType <em>View Event Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see net.sourceforge.tagsea.logging.ViewEventType
		 * @see net.sourceforge.tagsea.logging.impl.LoggingPackageImpl#getViewEventType()
		 * @generated
		 */
		EEnum VIEW_EVENT_TYPE = eINSTANCE.getViewEventType();

	}

} //LoggingPackage
