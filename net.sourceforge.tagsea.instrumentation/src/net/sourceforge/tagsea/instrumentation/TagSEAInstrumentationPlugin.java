package net.sourceforge.tagsea.instrumentation;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITagChangeEvent;
import net.sourceforge.tagsea.core.ITagChangeListener;
import net.sourceforge.tagsea.core.ITagSEAOperationStateListener;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.IWaypointChangeListener;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.core.ui.internal.ITagSEAUIListener;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUI;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUIEvent;
import net.sourceforge.tagsea.instrumentation.network.NetworkSendJob;
import net.sourceforge.tagsea.logging.DeletedTagEvent;
import net.sourceforge.tagsea.logging.DeletedWaypointEvent;
import net.sourceforge.tagsea.logging.FiltersChangedEvent;
import net.sourceforge.tagsea.logging.JobEvent;
import net.sourceforge.tagsea.logging.JobState;
import net.sourceforge.tagsea.logging.LoggingFactory;
import net.sourceforge.tagsea.logging.ModelEvent;
import net.sourceforge.tagsea.logging.NavigateEvent;
import net.sourceforge.tagsea.logging.NewTagEvent;
import net.sourceforge.tagsea.logging.NewWaypointEvent;
import net.sourceforge.tagsea.logging.TagNameChangeEvent;
import net.sourceforge.tagsea.logging.TagWaypointsChangedEvent;
import net.sourceforge.tagsea.logging.TaskNavigateEvent;
import net.sourceforge.tagsea.logging.UIEvent;
import net.sourceforge.tagsea.logging.ViewEvent;
import net.sourceforge.tagsea.logging.ViewEventType;
import net.sourceforge.tagsea.logging.WaypointAttribute;
import net.sourceforge.tagsea.logging.WaypointChangeEvent;
import net.sourceforge.tagsea.logging.WaypointState;
import net.sourceforge.tagsea.logging.WaypointTagNameChangeEvent;
import net.sourceforge.tagsea.logging.WaypointTagsChangedEvent;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.markers.internal.TaskMarker;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TagSEAInstrumentationPlugin extends AbstractUIPlugin implements ISaveParticipant {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.instrumentation";
	private TagSEAModelListener modelListener;
	private TagSEAUIListener uiListener;
	private Timer uploadTimer;
	private boolean logging;


	
	
	// The shared instance
	private static TagSEAInstrumentationPlugin plugin;
	private LogSink sink;
	
	
	/**
	 * Periodic task for uploading TagSEA logs.
	 * @author Del Myers
	 *
	 */
	private final class UploadTask extends TimerTask {
		@Override
		public void run() {
			TagSEAPlugin.run(new NetworkSendJob(), true);
			Date date = InstrumentationPreferences.getNextSendDate();
			//getLog().log(new Status(Status.INFO, PLUGIN_ID, "Scheduling send for " + date.toString()));
			if (date != null) {
				uploadTimer.schedule(new UploadTask(), date);
			}
		}
	}

	private static class TagSEAModelListener implements IWaypointChangeListener, ITagChangeListener, ITagSEAOperationStateListener {
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.IWaypointChangeListener#waypointsChanged(net.sourceforge.tagsea.core.WaypointDelta)
		 */
		public void waypointsChanged(WaypointDelta delta) {
			for (IWaypointChangeEvent event : delta.getChanges()) {
				TagSEAInstrumentationPlugin.getDefault().logModelEvent(event);
			}
		}
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.ITagChangeListener#tagsChanged(net.sourceforge.tagsea.core.TagDelta)
		 */
		public void tagsChanged(TagDelta delta) {
			for (ITagChangeEvent event : delta.getEvents()) {
				TagSEAInstrumentationPlugin.getDefault().logModelEvent(event);
			}
		}
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.ITagSEAOperationStateListener#stateChanged(net.sourceforge.tagsea.core.TagSEAOperation)
		 */
		public void stateChanged(TagSEAOperation operation) {
			TagSEAInstrumentationPlugin.getDefault().logOperationState(operation);			
		}
	}
	
	private static class TagSEAUIListener implements ITagSEAUIListener {
		public void eventPerformed(TagSEAUIEvent event) {
			TagSEAInstrumentationPlugin.getDefault().logUIEvent(event);
		}
	}
	
	/**
	 * Listens to preferences to start/stop listening to model events and logging.
	 * @author Del Myers
	 *
	 */
	private class PreferenceListener implements IPreferenceChangeListener {
		public void preferenceChange(PreferenceChangeEvent event) {
			if (InstrumentationPreferences.MONITORING.equals(event.getKey())) {
				if (InstrumentationPreferences.isMonitoringEnabled()) {
					initializeLogging();
				} else {
					quitLogging();
				}
			} else if (InstrumentationPreferences.SEND_INTERVAL.equals(event.getKey())) {
				if (InstrumentationPreferences.isUploadEnabled()) {
					startUploadTimer();
				} else {
					stopUploadTimer();
				}
			}
		}
	}
	
	
	/**
	 * The constructor
	 */
	public TagSEAInstrumentationPlugin() {
		plugin = this;
		this.sink = new LogSink();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Date today = DateUtils.today();
		
		Date tomorrow = new Date(today.getTime() + DateUtils.ONE_DAY);
		//save waypoints once a day.
		Timer timer = new Timer("Logging Waypoint State");
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				try {
					sink.saveWaypoints();
				} catch (IOException e) {
					log(e);
				}
			}}, tomorrow, DateUtils.ONE_DAY);
	}


	/**
	 * Saves a log of the current state of the waypoints in the user's workspace.
	 *
	 */
	@SuppressWarnings("unchecked")



	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TagSEAInstrumentationPlugin getDefault() {
		return plugin;
	}
	
	public void log(Exception e) {
		if (e instanceof CoreException) {
			getLog().log(((CoreException)e).getStatus());
		} else {
			String message = e.getMessage();
			if (message == null) {
				message = "";
			}
			Status status = new Status(
				Status.ERROR,
				PLUGIN_ID,
				Status.ERROR,
				message,
				e
			);
			getLog().log(status);
		}
	}

	
	/**
	 * @param marker
	 */
	@SuppressWarnings("restriction")
	public void logTaskEvent(TaskMarker marker) {
		TaskNavigateEvent event = LoggingFactory.eINSTANCE.createTaskNavigateEvent();
		event.setTime(System.currentTimeMillis());
		event.setLine(marker.getLine());
		event.setResource(marker.getResource().getFullPath().toPortableString());
		event.setDescription(marker.getDescription());
		sink.consumeEvent(event);
	}

	/**
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void logUIEvent(TagSEAUIEvent event) {
		//System.err.print("U");
		UIEvent uievent = null;
		switch (event.type) {
		case TagSEAUIEvent.NAVIGATE:
			NavigateEvent ne = LoggingFactory.eINSTANCE.createNavigateEvent();
			ne.setTime(event.time);
			ne.setWaypoint(createWaypointState(event.waypoint));
			uievent = ne;
			break;
		case TagSEAUIEvent.FILTER:
			FiltersChangedEvent fe = LoggingFactory.eINSTANCE.createFiltersChangedEvent();
			fe.setTime(event.time);
			String detail = event.detail;
			if (detail != null) {
				String[] names = detail.split(",");
				String[] waypointTypes = TagSEAPlugin.getDefault().getWaypointTypes();
				TreeSet<String> hiddenTypes = new TreeSet<String>();
				for (String name : names) {
					String value = name.trim();
					fe.getHidden().add(value);
					hiddenTypes.add(value);
				}
				for (String type : waypointTypes) {
					if (!hiddenTypes.contains(type)) {
						fe.getVisible().add(type);
					}
				}
			}
			uievent = fe;
			break;
		case TagSEAUIEvent.VIEW:
			ViewEvent ve = LoggingFactory.eINSTANCE.createViewEvent();
			ve.setFilterString("");
			ve.setTime(event.time);
			if (event.viewId != null) {
				ve.setViewid(event.viewId);
			}
			switch (event.kind) {
			case TagSEAUIEvent.VIEW_ACTIVATED:
				ve.setType(ViewEventType.ACTIVATED_LITERAL);
				break;
			case TagSEAUIEvent.VIEW_DEACTIVATED:
				ve.setType(ViewEventType.DEACTIVATED_LITERAL);
				break;
			case TagSEAUIEvent.VIEW_CLOSED:
				ve.setType(ViewEventType.CLOSED_LITERAL);
				break;
			case TagSEAUIEvent.VIEW_OPENED:
				ve.setType(ViewEventType.OPENED_LITERAL);
				break;
			case TagSEAUIEvent.VIEW_HIDDEN:
				ve.setType(ViewEventType.HIDDEN_LITERAL);
				break;
			case TagSEAUIEvent.VIEW_TOP:
				ve.setType(ViewEventType.TOP_LITERAL);
				break;
			case TagSEAUIEvent.VIEW_FILTERED:
				ve.setType(ViewEventType.FILTERED_LITERAL);
				ve.setFilterString(event.detail);
				break;
			case TagSEAUIEvent.VIEW_HIERARCHY:
				ve.setType(ViewEventType.HIERARCHY_LITERAL);
				ve.setHierarchyOn(Boolean.parseBoolean(event.detail));
			}
			if (event.detail != null) {
				ve.setFilterString(event.detail);
			}
			uievent = ve;
			break;
		}
		if (uievent != null) {
			sink.consumeEvent(uievent);
		}
	}
	
	
	

	

	

	
	
	
	
	@SuppressWarnings("unchecked")
	public void logModelEvent(IWaypointChangeEvent event) {
		//System.err.print("W");
		ModelEvent ev = null;
		switch (event.getType()) {
		case IWaypointChangeEvent.CHANGE: 
			WaypointChangeEvent wce = LoggingFactory.eINSTANCE.createWaypointChangeEvent();
			wce.setTime(System.currentTimeMillis());
			wce.setWaypoint(createWaypointState(event.getWaypoint()));
			for (String attr : event.getChangedAttributes()) {
				WaypointAttribute na = LoggingFactory.eINSTANCE.createWaypointAttribute();
				WaypointAttribute oa = LoggingFactory.eINSTANCE.createWaypointAttribute();
				na.setName(attr);
				oa.setName(attr);
				na.setValue(getValueAsString(event.getNewValue(attr)));
				oa.setValue(getValueAsString(event.getOldValue(attr)));
			}
			ev = wce;
			break;
		case IWaypointChangeEvent.NEW: 
			NewWaypointEvent ne = LoggingFactory.eINSTANCE.createNewWaypointEvent();
			ne.setTime(System.currentTimeMillis());
			ne.setWaypoint(createWaypointState(event.getWaypoint()));
			ev = ne;
			break;
		case IWaypointChangeEvent.DELETE: 
			DeletedWaypointEvent de  = LoggingFactory.eINSTANCE.createDeletedWaypointEvent();
			de.setTime(System.currentTimeMillis());
			de.setWaypoint(createWaypointState(event.getWaypoint()));
			ev = de;
			break;
		case IWaypointChangeEvent.TAG_NAME_CHANGED: 
			WaypointTagNameChangeEvent tne = LoggingFactory.eINSTANCE.createWaypointTagNameChangeEvent();
			tne.setTime(System.currentTimeMillis());
			tne.setWaypoint(createWaypointState(event.getWaypoint()));
			tne.setNewTagName(tne.getNewTagName());
			tne.setOldTagName(event.getOldTagName());
			ev = tne;
			break;
		case IWaypointChangeEvent.TAGS_CHANGED: 
			WaypointTagsChangedEvent tce = LoggingFactory.eINSTANCE.createWaypointTagsChangedEvent();
			tce.setTime(System.currentTimeMillis());
			tce.setWaypoint(createWaypointState(event.getWaypoint()));
			for (String t : event.getOldTags()) {
				tce.getOldTags().add(t);
			}
			ev = tce;
			break;
		}
		if (ev != null) {
			sink.consumeEvent(ev);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void logModelEvent(ITagChangeEvent event) {
		//System.err.print("T");
		ModelEvent me = null;
		switch (event.getType()) {
		case ITagChangeEvent.NEW:
			NewTagEvent nte = LoggingFactory.eINSTANCE.createNewTagEvent();
			nte.setTime(System.currentTimeMillis());
			nte.setTagName(event.getTag().getName());
			me = nte;
			break;
		case ITagChangeEvent.DELETED:
			DeletedTagEvent dte = LoggingFactory.eINSTANCE.createDeletedTagEvent();
			dte.setTime(System.currentTimeMillis());
			dte.setTagName(event.getTag().getName());
			me = dte;
			break;
		case ITagChangeEvent.NAME:
			TagNameChangeEvent tne = LoggingFactory.eINSTANCE.createTagNameChangeEvent();
			tne.setTime(System.currentTimeMillis());
			tne.setOldName(event.getOldName());
			tne.setTagName(event.getNewName());
			me = tne;
			break;
		case ITagChangeEvent.WAYPOINTS:
			TagWaypointsChangedEvent twe = LoggingFactory.eINSTANCE.createTagWaypointsChangedEvent();
			for (IWaypoint wp : event.getOldWaypoints()) {
				twe.getOldWaypoint().add(createWaypointState(wp));
			}
			for (IWaypoint wp : event.getNewWaypoints()) {
				twe.getNewWaypoint().add(createWaypointState(wp));
			}
			twe.setTime(System.currentTimeMillis());
			twe.setTagName(event.getTag().getName());
			me = twe;
			break;
		}
		if (me != null) {
			sink.consumeEvent(me);
		}
	}
	
	/**
	 * Logs the state of the given operation.
	 * @param op
	 */
	@SuppressWarnings("unchecked")
	public void logOperationState(TagSEAOperation op) {
		//System.err.print("O");
		JobEvent je = LoggingFactory.eINSTANCE.createJobEvent();
		je.setName(op.getName());
		je.setTime(System.currentTimeMillis());
		switch (op.getState()) {
		case CREATED:
			je.setState(JobState.CREATED_LITERAL);
			break;
		case QUEUED:
			je.setState(JobState.QUEUED_LITERAL);
			break;
		case WAITING:
			je.setState(JobState.QUEUED_LITERAL);
			break;
		case QUITING:
			je.setState(JobState.QUITING_LITERAL);
			break;
		case RUNNING:
			je.setState(JobState.RUNNING_LITERAL);
			break;
		case DONE:
			je.setState(JobState.DONE_LITERAL);
			break;
		}
		sink.consumeEvent(je);
	}
	
	@SuppressWarnings("unchecked")
	static WaypointState createWaypointState(IWaypoint waypoint) {
		WaypointState state = LoggingFactory.eINSTANCE.createWaypointState();
		state.setWaypointType(waypoint.getType());
		for (String attr : waypoint.getAttributes()) {
			WaypointAttribute attribute = LoggingFactory.eINSTANCE.createWaypointAttribute();
			Object value = waypoint.getValue(attr);
			attribute.setName(attr);
			attribute.setValue(getValueAsString(value));
			state.getAttributes().add(attribute);
		}
		for (ITag tag : waypoint.getTags()) {
			state.getTagNames().add(tag.getName());
		}
		return state;
	}
	
	private static String getValueAsString(Object value) {
		if (value instanceof Date) {
			DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
			return format.format((Date)value);
		} else {
			return value.toString();
		}
	}
	
	
	
//	@SuppressWarnings("unchecked")
//	public CurrentWaypoints getWaypoints(Date date) throws IOException {
//		IPath stateLocation = TagSEAInstrumentationPlugin.getDefault().getStateLocation();
//		DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
//		String dateString = formatter.format(date);
//		String fileNameString = dateString.replace('/', '-') + ".waypointlog";
//		IPath pathLocation = stateLocation.append(fileNameString);
//		File file = pathLocation.toFile();
//		CurrentWaypoints log = null;
//		if (file.exists()) {
//			ResourceSet inputResourceSet = new ResourceSetImpl();
//			inputResourceSet.getPackageRegistry().put(LoggingPackage.eNS_PREFIX, LoggingPackage.eINSTANCE);
//			inputResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("waypointlog", new XMIResourceFactoryImpl());
//			Resource inputResource = inputResourceSet.createResource(URI.createFileURI(pathLocation.toPortableString()));
//			inputResource.load(null);
//			log = (CurrentWaypoints) inputResource.getContents().get(0);
//		} else {
//			log =	LoggingFactory.eINSTANCE.createCurrentWaypoints();
//			Locale locale = Locale.getDefault();
//			log.setCountry(locale.getCountry());
//			log.setLanguage(locale.getLanguage());
//			log.setUid(getUserId());
//			log.setUname(getUserName());
//			try {
//				log.setDate(formatter.parse(dateString));
//			} catch (ParseException e) {
//				log(e);
//			}
//		}
//		return log;
//	}

	@SuppressWarnings("unchecked")
	void startUILog() {
		InstrumentationPreferences.incrementUISession();
		sink.consumeEvent(LoggingFactory.eINSTANCE.createUIRecordStartEvent());
		//add an event stating that we are able to listen for the task view
		TaskNavigateEvent tne = LoggingFactory.eINSTANCE.createTaskNavigateEvent();
		tne.setLine(-1);
		tne.setDescription("Logging Task Navigation Events");
		tne.setResource("$$&&(Logging Task Navigation Events)&&$$");
		tne.setTime(System.currentTimeMillis());
		sink.consumeEvent(tne);
	}
	
	@SuppressWarnings("unchecked")
	void startModelLog() {
		InstrumentationPreferences.incrementModelSession();
		sink.consumeEvent(LoggingFactory.eINSTANCE.createModelRecordStartEvent());
	}
	
	void startUploadTimer() {
		stopUploadTimer();
		uploadTimer = new Timer();
		//wait a couple of minutes, and then send to try to ensure that we get an updated
		//state of the waypoints.
		Date date = InstrumentationPreferences.getNextSendDate();
		//getLog().log(new Status(Status.INFO, PLUGIN_ID, "Scheduling send for " + date.toString()));
		if (date != null) {
			uploadTimer.schedule(new UploadTask(), 
			date
			);
		}
	}
	
	
	void stopUploadTimer() {
		if (uploadTimer != null) {
			uploadTimer.cancel();
			uploadTimer = null;
		}
	}
	
	/**
	 * @return
	 */
	public String getUserName() {
		return InstrumentationPreferences.getFirstName() + " " + InstrumentationPreferences.getLastName();
	}

	/**
	 * @return
	 */
	public int getUserId() {
		return InstrumentationPreferences.getUID();
	}

	
	synchronized void initializeLogging() {
		if (logging) return;
		if (modelListener == null) modelListener = new TagSEAModelListener();
		//@tag tagsea.instrumentation.2008.january : not needed in jauary 2008 study
		//if (uiListener == null) uiListener = new TagSEAUIListener();
		if (!InstrumentationPreferences.isRegistered()) {
			return;
		}
		if (InstrumentationPreferences.isMonitoringEnabled()) {
			startModelLog();
			//@tag tagsea.instrumentation.2008.january : The UI isn't logged for the study in January 2008.
			//startUILog();
			TagSEAPlugin.addOperationStateListener(modelListener);
			TagSEAPlugin.addTagChangeListener(modelListener);
			TagSEAPlugin.addWaypointChangeListener(modelListener);
			final TagSEAUI ui = ((TagSEAUI)TagSEAPlugin.getDefault().getUI());
			//@tag tagsea.bug.159.fix tagsea.instrumentation.2008.january : log what views are open at start up. Not needed in January 2008. 
			//Display.getDefault().syncExec(new Runnable(){
			//	public void run() {
			//		IViewPart view = ui.getTagsView();
			//		int state = TagSEAUIEvent.VIEW_CLOSED;
			//		if (view == null) {
			//			state = TagSEAUIEvent.VIEW_CLOSED;
			//		} else {
			//			IWorkbenchPart active = view.getSite().getPage().getActivePart();
			//			if (active == view) {
			//				state = TagSEAUIEvent.VIEW_ACTIVATED;
			//			} else {
			//				IViewPart[] stack = view.getSite().getPage().getViewStack(view);
			//				if (stack == null) {
			//					state = TagSEAUIEvent.VIEW_CLOSED;
			//				} if (stack[0]  != view) {
			//					state = TagSEAUIEvent.VIEW_HIDDEN;
			//				} else {
			//					state = TagSEAUIEvent.VIEW_TOP;
			//				}
			//			}
			//		}
//					TagSEAUIEvent event = TagSEAUIEvent.createViewEvent(state, TagsView.ID, null);
//					logUIEvent(event);
//					view = ui.getWaypointView();
//					state = TagSEAUIEvent.VIEW_CLOSED;
//					if (view == null) {
//						state = TagSEAUIEvent.VIEW_CLOSED;
//					} else {
//						IWorkbenchPart active = view.getSite().getPage().getActivePart();
//						if (active == view) {
//							state = TagSEAUIEvent.VIEW_ACTIVATED;
//						} else {
//							IViewPart[] stack = view.getSite().getPage().getViewStack(view);
//							if (stack == null) {
//								state = TagSEAUIEvent.VIEW_CLOSED;
//							} if (stack[0]  != view) {
//								state = TagSEAUIEvent.VIEW_HIDDEN;
//							} else {
//								state = TagSEAUIEvent.VIEW_TOP;
//							}
//						}
//					}
//					event = TagSEAUIEvent.createViewEvent(state, WaypointView.ID, null);
//					logUIEvent(event);
					
//				}
//			});
			//ui.addUIEventListener(uiListener);
			logging = true;
		}
	}
	
	synchronized void quitLogging() {
		if (!logging) return;
		if (modelListener != null) {
			TagSEAPlugin.removeWaypointChangeListener(modelListener);
			TagSEAPlugin.removeTagChangeListener(modelListener);
			TagSEAPlugin.removeOperationStateListener(modelListener);
		}
		//@tag tagsea.instrumentation.2008.january : not needed in Jauary 2008 study.
//		if (uiListener != null) {
//			TagSEAUI ui = ((TagSEAUI)TagSEAPlugin.getDefault().getUI());
//			ui.removeUIEventListener(uiListener);
//		}
		this.logging = false;
	}
	
	public void doneSaving(ISaveContext context) {
	}

	public void prepareToSave(ISaveContext context) throws CoreException {
	}

	public void rollback(ISaveContext context) {
	}

	public void saving(ISaveContext context) throws CoreException {
		try {
			switch (context.getKind()) {
			case ISaveContext.FULL_SAVE:
				sink.saveLogs();
				sink.saveWaypoints();
				break;
			case ISaveContext.SNAPSHOT:
				sink.saveToday();
				sink.saveWaypoints();
				break;
			}
		} catch (IOException e) {
			log(e);
		}
	}
	
	void startLogging() {
		if (InstrumentationPreferences.isUploadEnabled()) {
			//TagSEAInstrumentationPlugin.getDefault().getLog().log(new Status(Status.INFO, TagSEAInstrumentationPlugin.PLUGIN_ID, "Started upload timer."));
			startUploadTimer();
		}
		InstrumentationPreferences.addPreferenceChangeListener(new PreferenceListener());
		try {
			ResourcesPlugin.getWorkspace().addSaveParticipant(this, this);
		} catch (CoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}

	

}
