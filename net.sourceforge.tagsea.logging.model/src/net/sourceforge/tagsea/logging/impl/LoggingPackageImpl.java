/**
 * <copyright>
 * </copyright>
 *
 * $Id: LoggingPackageImpl.java,v 1.3 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.impl;

import net.sourceforge.tagsea.logging.CurrentWaypoints;
import net.sourceforge.tagsea.logging.DeletedTagEvent;
import net.sourceforge.tagsea.logging.DeletedWaypointEvent;
import net.sourceforge.tagsea.logging.Event;
import net.sourceforge.tagsea.logging.FiltersChangedEvent;
import net.sourceforge.tagsea.logging.JobEvent;
import net.sourceforge.tagsea.logging.JobState;
import net.sourceforge.tagsea.logging.Log;
import net.sourceforge.tagsea.logging.LogFile;
import net.sourceforge.tagsea.logging.LoggingFactory;
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
import net.sourceforge.tagsea.logging.ViewEventType;
import net.sourceforge.tagsea.logging.WaypointAttribute;
import net.sourceforge.tagsea.logging.WaypointChangeEvent;
import net.sourceforge.tagsea.logging.WaypointEvent;
import net.sourceforge.tagsea.logging.WaypointState;
import net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent;
import net.sourceforge.tagsea.logging.WaypointTagsChangedEvent;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LoggingPackageImpl extends EPackageImpl implements LoggingPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass modelLogEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tagEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass waypointEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass logFileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass waypointStateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass uiLogEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass currentWaypointsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass waypointAttributeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass newWaypointEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deletedWaypointEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass waypointChangeEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tagNameChangeEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass waypointTagNameChangeEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tagWaypointsChangedEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass newTagEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deletedTagEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass jobEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass logEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass navigateEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass viewEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass filtersChangedEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass uiEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass modelEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass waypointTagsChangedEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass modelRecordStartEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass uiRecordStartEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass taskNavigateEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum jobStateEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum viewEventTypeEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private LoggingPackageImpl() {
		super(eNS_URI, LoggingFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static LoggingPackage init() {
		if (isInited) return (LoggingPackage)EPackage.Registry.INSTANCE.getEPackage(LoggingPackage.eNS_URI);

		// Obtain or create and register package
		LoggingPackageImpl theLoggingPackage = (LoggingPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof LoggingPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new LoggingPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theLoggingPackage.createPackageContents();

		// Initialize created meta-data
		theLoggingPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theLoggingPackage.freeze();

		return theLoggingPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getModelLog() {
		return modelLogEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getModelLog_Events() {
		return (EReference)modelLogEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEvent() {
		return eventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEvent_Time() {
		return (EAttribute)eventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTagEvent() {
		return tagEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTagEvent_TagName() {
		return (EAttribute)tagEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWaypointEvent() {
		return waypointEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWaypointEvent_Waypoint() {
		return (EReference)waypointEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLogFile() {
		return logFileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLogFile_ModelLog() {
		return (EReference)logFileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLogFile_UiLog() {
		return (EReference)logFileEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLogFile_CurrentWaypoints() {
		return (EReference)logFileEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWaypointState() {
		return waypointStateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWaypointState_WaypointType() {
		return (EAttribute)waypointStateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWaypointState_Attributes() {
		return (EReference)waypointStateEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWaypointState_TagNames() {
		return (EAttribute)waypointStateEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUILog() {
		return uiLogEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUILog_Events() {
		return (EReference)uiLogEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCurrentWaypoints() {
		return currentWaypointsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCurrentWaypoints_Waypoint() {
		return (EReference)currentWaypointsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWaypointAttribute() {
		return waypointAttributeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWaypointAttribute_Name() {
		return (EAttribute)waypointAttributeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWaypointAttribute_Value() {
		return (EAttribute)waypointAttributeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNewWaypointEvent() {
		return newWaypointEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeletedWaypointEvent() {
		return deletedWaypointEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWaypointChangeEvent() {
		return waypointChangeEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWaypointChangeEvent_NewAttributes() {
		return (EReference)waypointChangeEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWaypointChangeEvent_OldAttributes() {
		return (EReference)waypointChangeEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTagNameChangeEvent() {
		return tagNameChangeEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTagNameChangeEvent_OldName() {
		return (EAttribute)tagNameChangeEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWaypointTagNameChangeEvent() {
		return waypointTagNameChangeEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWaypointTagNameChangeEvent_NewTagName() {
		return (EAttribute)waypointTagNameChangeEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWaypointTagNameChangeEvent_OldTagName() {
		return (EAttribute)waypointTagNameChangeEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTagWaypointsChangedEvent() {
		return tagWaypointsChangedEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getTagWaypointsChangedEvent_OldWaypoint() {
		return (EReference)tagWaypointsChangedEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getTagWaypointsChangedEvent_NewWaypoint() {
		return (EReference)tagWaypointsChangedEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNewTagEvent() {
		return newTagEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeletedTagEvent() {
		return deletedTagEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getJobEvent() {
		return jobEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJobEvent_State() {
		return (EAttribute)jobEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJobEvent_Name() {
		return (EAttribute)jobEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLog() {
		return logEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLog_Uid() {
		return (EAttribute)logEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLog_Date() {
		return (EAttribute)logEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLog_Uname() {
		return (EAttribute)logEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLog_Country() {
		return (EAttribute)logEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLog_Language() {
		return (EAttribute)logEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLog_Session() {
		return (EAttribute)logEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNavigateEvent() {
		return navigateEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavigateEvent_Waypoint() {
		return (EReference)navigateEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getViewEvent() {
		return viewEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getViewEvent_Type() {
		return (EAttribute)viewEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getViewEvent_Viewid() {
		return (EAttribute)viewEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getViewEvent_FilterString() {
		return (EAttribute)viewEventEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getFiltersChangedEvent() {
		return filtersChangedEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getFiltersChangedEvent_Visible() {
		return (EAttribute)filtersChangedEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getFiltersChangedEvent_Hidden() {
		return (EAttribute)filtersChangedEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUIEvent() {
		return uiEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUIEvent_HierarchyOn() {
		return (EAttribute)uiEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getModelEvent() {
		return modelEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWaypointTagsChangedEvent() {
		return waypointTagsChangedEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWaypointTagsChangedEvent_OldTags() {
		return (EAttribute)waypointTagsChangedEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getModelRecordStartEvent() {
		return modelRecordStartEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUIRecordStartEvent() {
		return uiRecordStartEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTaskNavigateEvent() {
		return taskNavigateEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTaskNavigateEvent_Description() {
		return (EAttribute)taskNavigateEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTaskNavigateEvent_Line() {
		return (EAttribute)taskNavigateEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTaskNavigateEvent_Resource() {
		return (EAttribute)taskNavigateEventEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getJobState() {
		return jobStateEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getViewEventType() {
		return viewEventTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LoggingFactory getLoggingFactory() {
		return (LoggingFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		modelLogEClass = createEClass(MODEL_LOG);
		createEReference(modelLogEClass, MODEL_LOG__EVENTS);

		eventEClass = createEClass(EVENT);
		createEAttribute(eventEClass, EVENT__TIME);

		tagEventEClass = createEClass(TAG_EVENT);
		createEAttribute(tagEventEClass, TAG_EVENT__TAG_NAME);

		waypointEventEClass = createEClass(WAYPOINT_EVENT);
		createEReference(waypointEventEClass, WAYPOINT_EVENT__WAYPOINT);

		logFileEClass = createEClass(LOG_FILE);
		createEReference(logFileEClass, LOG_FILE__MODEL_LOG);
		createEReference(logFileEClass, LOG_FILE__UI_LOG);
		createEReference(logFileEClass, LOG_FILE__CURRENT_WAYPOINTS);

		waypointStateEClass = createEClass(WAYPOINT_STATE);
		createEAttribute(waypointStateEClass, WAYPOINT_STATE__WAYPOINT_TYPE);
		createEReference(waypointStateEClass, WAYPOINT_STATE__ATTRIBUTES);
		createEAttribute(waypointStateEClass, WAYPOINT_STATE__TAG_NAMES);

		uiLogEClass = createEClass(UI_LOG);
		createEReference(uiLogEClass, UI_LOG__EVENTS);

		currentWaypointsEClass = createEClass(CURRENT_WAYPOINTS);
		createEReference(currentWaypointsEClass, CURRENT_WAYPOINTS__WAYPOINT);

		waypointAttributeEClass = createEClass(WAYPOINT_ATTRIBUTE);
		createEAttribute(waypointAttributeEClass, WAYPOINT_ATTRIBUTE__NAME);
		createEAttribute(waypointAttributeEClass, WAYPOINT_ATTRIBUTE__VALUE);

		newWaypointEventEClass = createEClass(NEW_WAYPOINT_EVENT);

		deletedWaypointEventEClass = createEClass(DELETED_WAYPOINT_EVENT);

		waypointChangeEventEClass = createEClass(WAYPOINT_CHANGE_EVENT);
		createEReference(waypointChangeEventEClass, WAYPOINT_CHANGE_EVENT__NEW_ATTRIBUTES);
		createEReference(waypointChangeEventEClass, WAYPOINT_CHANGE_EVENT__OLD_ATTRIBUTES);

		tagNameChangeEventEClass = createEClass(TAG_NAME_CHANGE_EVENT);
		createEAttribute(tagNameChangeEventEClass, TAG_NAME_CHANGE_EVENT__OLD_NAME);

		waypointTagNameChangeEventEClass = createEClass(WAYPOINT_TAG_NAME_CHANGE_EVENT);
		createEAttribute(waypointTagNameChangeEventEClass, WAYPOINT_TAG_NAME_CHANGE_EVENT__NEW_TAG_NAME);
		createEAttribute(waypointTagNameChangeEventEClass, WAYPOINT_TAG_NAME_CHANGE_EVENT__OLD_TAG_NAME);

		waypointTagsChangedEventEClass = createEClass(WAYPOINT_TAGS_CHANGED_EVENT);
		createEAttribute(waypointTagsChangedEventEClass, WAYPOINT_TAGS_CHANGED_EVENT__OLD_TAGS);

		tagWaypointsChangedEventEClass = createEClass(TAG_WAYPOINTS_CHANGED_EVENT);
		createEReference(tagWaypointsChangedEventEClass, TAG_WAYPOINTS_CHANGED_EVENT__OLD_WAYPOINT);
		createEReference(tagWaypointsChangedEventEClass, TAG_WAYPOINTS_CHANGED_EVENT__NEW_WAYPOINT);

		newTagEventEClass = createEClass(NEW_TAG_EVENT);

		deletedTagEventEClass = createEClass(DELETED_TAG_EVENT);

		jobEventEClass = createEClass(JOB_EVENT);
		createEAttribute(jobEventEClass, JOB_EVENT__STATE);
		createEAttribute(jobEventEClass, JOB_EVENT__NAME);

		logEClass = createEClass(LOG);
		createEAttribute(logEClass, LOG__UID);
		createEAttribute(logEClass, LOG__DATE);
		createEAttribute(logEClass, LOG__UNAME);
		createEAttribute(logEClass, LOG__COUNTRY);
		createEAttribute(logEClass, LOG__LANGUAGE);
		createEAttribute(logEClass, LOG__SESSION);

		navigateEventEClass = createEClass(NAVIGATE_EVENT);
		createEReference(navigateEventEClass, NAVIGATE_EVENT__WAYPOINT);

		viewEventEClass = createEClass(VIEW_EVENT);
		createEAttribute(viewEventEClass, VIEW_EVENT__TYPE);
		createEAttribute(viewEventEClass, VIEW_EVENT__VIEWID);
		createEAttribute(viewEventEClass, VIEW_EVENT__FILTER_STRING);

		filtersChangedEventEClass = createEClass(FILTERS_CHANGED_EVENT);
		createEAttribute(filtersChangedEventEClass, FILTERS_CHANGED_EVENT__VISIBLE);
		createEAttribute(filtersChangedEventEClass, FILTERS_CHANGED_EVENT__HIDDEN);

		uiEventEClass = createEClass(UI_EVENT);
		createEAttribute(uiEventEClass, UI_EVENT__HIERARCHY_ON);

		modelEventEClass = createEClass(MODEL_EVENT);

		modelRecordStartEventEClass = createEClass(MODEL_RECORD_START_EVENT);

		uiRecordStartEventEClass = createEClass(UI_RECORD_START_EVENT);

		taskNavigateEventEClass = createEClass(TASK_NAVIGATE_EVENT);
		createEAttribute(taskNavigateEventEClass, TASK_NAVIGATE_EVENT__DESCRIPTION);
		createEAttribute(taskNavigateEventEClass, TASK_NAVIGATE_EVENT__LINE);
		createEAttribute(taskNavigateEventEClass, TASK_NAVIGATE_EVENT__RESOURCE);

		// Create enums
		jobStateEEnum = createEEnum(JOB_STATE);
		viewEventTypeEEnum = createEEnum(VIEW_EVENT_TYPE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Add supertypes to classes
		modelLogEClass.getESuperTypes().add(this.getLog());
		tagEventEClass.getESuperTypes().add(this.getModelEvent());
		waypointEventEClass.getESuperTypes().add(this.getModelEvent());
		uiLogEClass.getESuperTypes().add(this.getLog());
		currentWaypointsEClass.getESuperTypes().add(this.getLog());
		newWaypointEventEClass.getESuperTypes().add(this.getWaypointEvent());
		deletedWaypointEventEClass.getESuperTypes().add(this.getWaypointEvent());
		waypointChangeEventEClass.getESuperTypes().add(this.getWaypointEvent());
		tagNameChangeEventEClass.getESuperTypes().add(this.getTagEvent());
		waypointTagNameChangeEventEClass.getESuperTypes().add(this.getWaypointEvent());
		waypointTagsChangedEventEClass.getESuperTypes().add(this.getWaypointEvent());
		tagWaypointsChangedEventEClass.getESuperTypes().add(this.getTagEvent());
		newTagEventEClass.getESuperTypes().add(this.getTagEvent());
		deletedTagEventEClass.getESuperTypes().add(this.getTagEvent());
		jobEventEClass.getESuperTypes().add(this.getModelEvent());
		navigateEventEClass.getESuperTypes().add(this.getUIEvent());
		viewEventEClass.getESuperTypes().add(this.getUIEvent());
		filtersChangedEventEClass.getESuperTypes().add(this.getUIEvent());
		uiEventEClass.getESuperTypes().add(this.getEvent());
		modelEventEClass.getESuperTypes().add(this.getEvent());
		modelRecordStartEventEClass.getESuperTypes().add(this.getModelEvent());
		uiRecordStartEventEClass.getESuperTypes().add(this.getUIEvent());
		taskNavigateEventEClass.getESuperTypes().add(this.getUIEvent());

		// Initialize classes and features; add operations and parameters
		initEClass(modelLogEClass, ModelLog.class, "ModelLog", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getModelLog_Events(), this.getModelEvent(), null, "events", null, 0, -1, ModelLog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eventEClass, Event.class, "Event", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEvent_Time(), ecorePackage.getELong(), "time", null, 1, 1, Event.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(tagEventEClass, TagEvent.class, "TagEvent", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTagEvent_TagName(), ecorePackage.getEString(), "tagName", null, 0, 1, TagEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(waypointEventEClass, WaypointEvent.class, "WaypointEvent", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getWaypointEvent_Waypoint(), this.getWaypointState(), null, "waypoint", null, 1, 1, WaypointEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(logFileEClass, LogFile.class, "LogFile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getLogFile_ModelLog(), this.getModelLog(), null, "modelLog", null, 0, 1, LogFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLogFile_UiLog(), this.getUILog(), null, "uiLog", null, 0, -1, LogFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLogFile_CurrentWaypoints(), this.getCurrentWaypoints(), null, "currentWaypoints", null, 0, 1, LogFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(waypointStateEClass, WaypointState.class, "WaypointState", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWaypointState_WaypointType(), ecorePackage.getEString(), "waypointType", null, 1, 1, WaypointState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWaypointState_Attributes(), this.getWaypointAttribute(), null, "attributes", null, 1, -1, WaypointState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWaypointState_TagNames(), ecorePackage.getEString(), "tagNames", null, 0, -1, WaypointState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(uiLogEClass, UILog.class, "UILog", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getUILog_Events(), this.getUIEvent(), null, "events", null, 0, -1, UILog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(currentWaypointsEClass, CurrentWaypoints.class, "CurrentWaypoints", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCurrentWaypoints_Waypoint(), this.getWaypointState(), null, "waypoint", null, 0, -1, CurrentWaypoints.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(waypointAttributeEClass, WaypointAttribute.class, "WaypointAttribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWaypointAttribute_Name(), ecorePackage.getEString(), "name", null, 1, 1, WaypointAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWaypointAttribute_Value(), ecorePackage.getEString(), "value", null, 1, 1, WaypointAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(newWaypointEventEClass, NewWaypointEvent.class, "NewWaypointEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(deletedWaypointEventEClass, DeletedWaypointEvent.class, "DeletedWaypointEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(waypointChangeEventEClass, WaypointChangeEvent.class, "WaypointChangeEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getWaypointChangeEvent_NewAttributes(), this.getWaypointAttribute(), null, "newAttributes", null, 0, -1, WaypointChangeEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWaypointChangeEvent_OldAttributes(), this.getWaypointAttribute(), null, "oldAttributes", null, 0, -1, WaypointChangeEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(tagNameChangeEventEClass, TagNameChangeEvent.class, "TagNameChangeEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTagNameChangeEvent_OldName(), ecorePackage.getEString(), "oldName", null, 0, 1, TagNameChangeEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(waypointTagNameChangeEventEClass, WaypointTagNameChangeEvent.class, "WaypointTagNameChangeEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWaypointTagNameChangeEvent_NewTagName(), ecorePackage.getEString(), "newTagName", null, 1, 1, WaypointTagNameChangeEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWaypointTagNameChangeEvent_OldTagName(), ecorePackage.getEString(), "oldTagName", null, 1, 1, WaypointTagNameChangeEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(waypointTagsChangedEventEClass, WaypointTagsChangedEvent.class, "WaypointTagsChangedEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWaypointTagsChangedEvent_OldTags(), ecorePackage.getEString(), "oldTags", null, 0, -1, WaypointTagsChangedEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(tagWaypointsChangedEventEClass, TagWaypointsChangedEvent.class, "TagWaypointsChangedEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTagWaypointsChangedEvent_OldWaypoint(), this.getWaypointState(), null, "oldWaypoint", null, 0, -1, TagWaypointsChangedEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTagWaypointsChangedEvent_NewWaypoint(), this.getWaypointState(), null, "newWaypoint", null, 0, -1, TagWaypointsChangedEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(newTagEventEClass, NewTagEvent.class, "NewTagEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(deletedTagEventEClass, DeletedTagEvent.class, "DeletedTagEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(jobEventEClass, JobEvent.class, "JobEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getJobEvent_State(), this.getJobState(), "state", "created", 1, 1, JobEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJobEvent_Name(), ecorePackage.getEString(), "name", null, 1, 1, JobEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(logEClass, Log.class, "Log", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLog_Uid(), ecorePackage.getEInt(), "uid", "0", 1, 1, Log.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLog_Date(), ecorePackage.getEDate(), "date", null, 1, 1, Log.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLog_Uname(), ecorePackage.getEString(), "uname", "anonymous", 1, 1, Log.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLog_Country(), ecorePackage.getEString(), "country", null, 1, 1, Log.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLog_Language(), ecorePackage.getEString(), "language", null, 1, 1, Log.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLog_Session(), ecorePackage.getEInt(), "session", "0", 1, 1, Log.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(navigateEventEClass, NavigateEvent.class, "NavigateEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNavigateEvent_Waypoint(), this.getWaypointState(), null, "waypoint", null, 1, 1, NavigateEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(viewEventEClass, ViewEvent.class, "ViewEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getViewEvent_Type(), this.getViewEventType(), "type", "", 1, 1, ViewEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getViewEvent_Viewid(), ecorePackage.getEString(), "viewid", null, 1, 1, ViewEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getViewEvent_FilterString(), ecorePackage.getEString(), "filterString", "", 0, 1, ViewEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(filtersChangedEventEClass, FiltersChangedEvent.class, "FiltersChangedEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFiltersChangedEvent_Visible(), ecorePackage.getEString(), "visible", null, 0, -1, FiltersChangedEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFiltersChangedEvent_Hidden(), ecorePackage.getEString(), "hidden", null, 0, -1, FiltersChangedEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(uiEventEClass, UIEvent.class, "UIEvent", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getUIEvent_HierarchyOn(), ecorePackage.getEBoolean(), "hierarchyOn", "false", 0, 1, UIEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(modelEventEClass, ModelEvent.class, "ModelEvent", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(modelRecordStartEventEClass, ModelRecordStartEvent.class, "ModelRecordStartEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(uiRecordStartEventEClass, UIRecordStartEvent.class, "UIRecordStartEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(taskNavigateEventEClass, TaskNavigateEvent.class, "TaskNavigateEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTaskNavigateEvent_Description(), ecorePackage.getEString(), "description", "", 1, 1, TaskNavigateEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTaskNavigateEvent_Line(), ecorePackage.getEInt(), "line", "0", 1, 1, TaskNavigateEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTaskNavigateEvent_Resource(), ecorePackage.getEString(), "resource", "", 1, 1, TaskNavigateEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(jobStateEEnum, JobState.class, "JobState");
		addEEnumLiteral(jobStateEEnum, JobState.CREATED_LITERAL);
		addEEnumLiteral(jobStateEEnum, JobState.QUEUED_LITERAL);
		addEEnumLiteral(jobStateEEnum, JobState.WAITING_LITERAL);
		addEEnumLiteral(jobStateEEnum, JobState.RUNNING_LITERAL);
		addEEnumLiteral(jobStateEEnum, JobState.QUITING_LITERAL);
		addEEnumLiteral(jobStateEEnum, JobState.DONE_LITERAL);

		initEEnum(viewEventTypeEEnum, ViewEventType.class, "ViewEventType");
		addEEnumLiteral(viewEventTypeEEnum, ViewEventType.OPENED_LITERAL);
		addEEnumLiteral(viewEventTypeEEnum, ViewEventType.TOP_LITERAL);
		addEEnumLiteral(viewEventTypeEEnum, ViewEventType.ACTIVATED_LITERAL);
		addEEnumLiteral(viewEventTypeEEnum, ViewEventType.DEACTIVATED_LITERAL);
		addEEnumLiteral(viewEventTypeEEnum, ViewEventType.HIDDEN_LITERAL);
		addEEnumLiteral(viewEventTypeEEnum, ViewEventType.CLOSED_LITERAL);
		addEEnumLiteral(viewEventTypeEEnum, ViewEventType.FILTERED_LITERAL);
		addEEnumLiteral(viewEventTypeEEnum, ViewEventType.HIERARCHY_LITERAL);

		// Create resource
		createResource(eNS_URI);
	}

} //LoggingPackageImpl
