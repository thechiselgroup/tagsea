package net.sourceforge.tagsea.c.waypoints;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.c.CWaypointUtils;
import net.sourceforge.tagsea.c.CWaypointsPlugin;
import net.sourceforge.tagsea.c.ICWaypointAttributes;
import net.sourceforge.tagsea.c.ICWaypointsConstants;
import net.sourceforge.tagsea.c.resources.internal.CResourceListener;
import net.sourceforge.tagsea.c.resources.internal.FileWaypointRefreshJob;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITagSEAOperationStateListener;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.WaypointDelta;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

public class CWaypointDelegate extends AbstractWaypointDelegate {
	/**
	 * Map of waypoints to markers.
	 */
	private HashMap<IWaypoint, IMarker> waypointMarkerMap;
	private HashMap<IMarker, IWaypoint> markerWaypointMap;
	/**
	 * List of buffers to listen for changes on.
	 */
	private HashMap<IDocument, ITextFileBuffer> documentBufferMap;
	private JavaDocumentChangedListener documentChangedListener;
	/**
	 * Flag to be set if refactoring events should be listened for.
	 */
	//private boolean shouldRefactor;
	
	/**
	 * Flag to be set if document changes should be listened for.
	 */
//	private boolean listenToDocuments;
	private IResourceChangeListener resourceListener;
	
	/**
	 * An enumeration indicating the current state of the delegate: whether it is
	 * handling internal changes (and therefore expects changes to waypoints), is
	 * idle, or is changing waypoints itself.
	 * @author Del Myers
	 */
	private enum JavaWaypointState {
		/**
		 * The delegate is idle.
		 */
		IDLE,
		/**
		 * The delegate is updating waypoints, and expects changes.
		 */
		UPDATING,
		/**
		 * The delegate is refactoring, and doesn't expect changes.
		 */
		REFACTORING
	}
	
	private JavaWaypointState state;
	
	/**
	 * A stack of previous states so that they can be restored.
	 */
	private LinkedList<JavaWaypointState> stateStack;

	public CWaypointDelegate() {
		waypointMarkerMap = new HashMap<IWaypoint, IMarker>();
		markerWaypointMap = new HashMap<IMarker, IWaypoint>();
		documentBufferMap = new HashMap<IDocument, ITextFileBuffer>();
		documentChangedListener = new JavaDocumentChangedListener();
		//shouldRefactor = true;
//		listenToDocuments = true;
		this.state = JavaWaypointState.IDLE;
		this.stateStack = new LinkedList<JavaWaypointState>();
	}

	private class JavaDocumentChangedListener implements IDocumentListener {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentAboutToBeChanged(DocumentEvent event) {
		}
		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentChanged(DocumentEvent event) {
//			if (!listenToDocuments) return;
			//we are refactoring, don't listen for document changes.
			if (state == JavaWaypointState.REFACTORING) return;
			//note: refactor operations run inside the ui thread, so document changes can't happen at the same time.
			internalRun(new DocumentChangeOperation(event, documentBufferMap.get(event.fDocument)), false);
		}
	}
	/**
	 * Object used to synchronize refactorings.
	 */
	private static final Object REFACTORLOCK = new Object();
	/**
	 * A runs refactoring operations inside the UI thread.
	 * @author Del Myers
	 */
	private class RefactoringOperation implements Runnable {
		
		private WaypointDelta delta;
		
		public RefactoringOperation(WaypointDelta delta) {
			this.delta = delta;
		}
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			//remove the resource listener.
			synchronized (REFACTORLOCK) {
				ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
				Shell shell = 
					CWaypointsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
//				boolean oldDocumentValue;
//				boolean oldRefactorValue;

//				oldDocumentValue = listenToDocuments;
//				listenToDocuments = false;
//				oldRefactorValue = shouldRefactor;
//				shouldRefactor = false;
				if (state != JavaWaypointState.IDLE) {
					CWaypointsPlugin.getDefault().getLog().log(new Status(
						IStatus.WARNING,
						CWaypointsPlugin.PLUGIN_ID,
						IStatus.WARNING,
						"Refactoring occurred while delegate in " + state.toString(),
						null
					));
				}
				pushState(JavaWaypointState.REFACTORING);


				try {
					dialog.run(true, false, getDialogRunner());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				popState();

				//add the listener back again.
				ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener);
			}
		}
		
		/**
		 * Returns the operation that will be run within the dialog.
		 * @return
		 */
		private IRunnableWithProgress getDialogRunner() {
			return new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Refactoring... ", 150);
//					check for synchronization
					DirtyEditorSynchronizer synchronizer = new DirtyEditorSynchronizer(delta);
					synchronizer.run(new SubProgressMonitor(monitor, 50, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
					ModelChangeOperation refacteror = new ModelChangeOperation(synchronizer.getWaypointChanges());
					refacteror.run(new SubProgressMonitor(monitor, 100, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
				}
			};
		}




		public void start() {
			synchronized (REFACTORLOCK) {
				Display.getDefault().asyncExec(this);
			}
		}

	}
	
	
	/**
	 * An internal operation that is run to ensure that refactoring doesn't occur when the Java
	 * plugin makes changes to the model.
	 * @author Del Myers
	 */
	private class InternalOperation extends TagSEAOperation {

		private TagSEAOperation proxy;
		/**
		 * 
		 */
		public InternalOperation(TagSEAOperation proxy) {
			super(proxy.getName());
			this.proxy = proxy;
		}
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.ITagSEAOperation#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public IStatus run(IProgressMonitor monitor) throws  InvocationTargetException {
			return proxy.run(monitor);
		}
		
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.TagSEAOperation#operationDone()
		 */
		@Override
		public void operationDone() {
			super.operationDone();
			proxy.operationDone();
			popState();
		}
		
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.TagSEAOperation#operationQueued()
		 */
		@Override
		public void operationQueued() {
			super.operationQueued();
			proxy.operationQueued();
		}
		
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.TagSEAOperation#operationStarted()
		 */
		@Override
		public void operationStarted() {
			super.operationStarted();
			proxy.operationStarted();
			pushState(JavaWaypointState.UPDATING);
		}
		
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.TagSEAOperation#operationWaiting()
		 */
		@Override
		public void operationWaiting() {
			super.operationWaiting();
			proxy.operationWaiting();
		}
		
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.TagSEAOperation#operationQuiting()
		 */
		@Override
		public void operationQuiting() {
			super.operationQuiting();
			proxy.operationQuiting();
		}
		
	}
	
	
	
	
	@Override
	public void load() {
		final LoadWaypointsOperation op = new LoadWaypointsOperation();
		//LoadWaypointsJob job = new LoadWaypointsJob();
		resourceListener = new CResourceListener();
		TagSEAPlugin.addOperationStateListener(new ITagSEAOperationStateListener() {
			public void stateChanged(TagSEAOperation operation) {
				if (operation instanceof InternalOperation) {
					if (((InternalOperation)operation).proxy == op) {
						if (operation.getState() == TagSEAOperation.OperationState.DONE) {
							TagSEAPlugin.removeOperationStateListener(this);
							ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener);
						}
					}
				}
			}
		});
		internalRun(op, false);
//		job.addJobChangeListener(new IJobChangeListener(){
//			public void aboutToRun(IJobChangeEvent event) {
//			}
//			public void awake(IJobChangeEvent event) {
//			}
//			public void done(IJobChangeEvent event) {
//				//add the resource listener when the job is finished.
//				ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener);
//				
//			}
//			public void running(IJobChangeEvent event) {
//			}
//			public void scheduled(IJobChangeEvent event) {
//			}
//			public void sleeping(IJobChangeEvent event) {
//			}
//		});
//		job.schedule();
		FileBuffers.getTextFileBufferManager().addFileBufferListener(new CFileBufferListener());
	}

	@Override
	public void navigate(final IWaypoint waypoint) {
		Display.getDefault().asyncExec(new Runnable(){
			public void run() {
				IFile file = CWaypointUtils.getFile(waypoint);
				
				if (file != null && file.exists()) {
					IWorkbenchPage page = 
						CWaypointsPlugin.
							getDefault().
							getWorkbench().
							getActiveWorkbenchWindow().
							getActivePage();
					try {
						if (page == null) return;
						IEditorPart part = IDE.openEditor(page, file);
						if (part instanceof ITextEditor) {
							((ITextEditor)part).selectAndReveal(CWaypointUtils.getOffset(waypoint), CWaypointUtils.getLength(waypoint));
						}
					} catch (PartInitException e) {
					}
				}
			}
		});
	}

	public void tagsChanged(TagDelta delta) {

	}

	public void waypointsChanged(WaypointDelta delta) {
		//can't do anything if we are closing
		if (PlatformUI.getWorkbench().isClosing()) return;
		for (IWaypointChangeEvent event : delta.changes) {
			switch (event.getType()) {
			case IWaypointChangeEvent.NEW:
				try {
						handleNew(event.getWaypoint());
					} catch (CoreException e1) {
						CWaypointsPlugin.getDefault().log(e1);
					}
				break;
			case IWaypointChangeEvent.DELETE:
				handleDelete(event.getWaypoint());
				break;
			case IWaypointChangeEvent.CHANGE:
				try {
						handleChange(event);
					} catch (CoreException e) {
						CWaypointsPlugin.getDefault().log(e);
					}
				break;
			}
		}
		
		if (state == JavaWaypointState.IDLE) {
			new RefactoringOperation(delta).start();
		}
		
	}

	/**
	 * @param event
	 * @throws CoreException 
	 */
	@SuppressWarnings({"unchecked"}) //$NON-NLS-1$
	private void handleChange(IWaypointChangeEvent event) throws CoreException {
		if (!event.getWaypoint().exists()) return;
		//do simple changes with the markers.
		String[] attributes = event.getChangedAttributes();
		Map newMarkerAttributes = new HashMap();
		int newEnd = event.getWaypoint().getIntValue(ICWaypointAttributes.ATTR_CHAR_END, 0);
		//int newStart = event.getWaypoint().getIntValue(ICWaypointAttributes.ATTR_CHAR_START, 1);
		boolean locationChanged = false;
		for (String attribute : attributes) {
			if (ICWaypointAttributes.ATTR_CHAR_START.equals(attribute)) {
				newMarkerAttributes.put(IMarker.CHAR_START, event.getNewValue(attribute));
				//newStart = (Integer)event.getNewValue(attribute);
				locationChanged = true;
			} else if (ICWaypointAttributes.ATTR_CHAR_END.equals(attribute)) {
				newEnd = (Integer)event.getNewValue(attribute);
				locationChanged = true;
			} else if (IWaypoint.ATTR_MESSAGE.equals(attribute)) {
				newMarkerAttributes.put(IMarker.MESSAGE, event.getNewValue(attribute));
			} else if (ICWaypointAttributes.ATTR_RESOURCE.equals(attribute)) {
				if (waypointMarkerMap.get(event.getWaypoint()) == null) {
					createNewMarker(event.getWaypoint());
				}
			} else if (IWaypoint.ATTR_DATE.equals(attribute)) {
				Date date = event.getWaypoint().getDate();
				if (date != null) {
					Locale loc = Locale.getDefault();
					String s = loc.getLanguage()+loc.getCountry()+":"+DateFormat.getDateInstance().format(date); //$NON-NLS-1$
					newMarkerAttributes.put(IWaypoint.ATTR_DATE, s);
				}
			}
		}
		ITag[] tags = event.getWaypoint().getTags();
		String tagString = ""; //$NON-NLS-1$
		for (ITag tag : tags) {
			tagString += tag.getName() + " "; //$NON-NLS-1$
		}
		newMarkerAttributes.put("tags", tagString); //$NON-NLS-1$
		if (locationChanged) {
			newMarkerAttributes.put(IMarker.CHAR_END, new Integer(newEnd));
		}
		IMarker marker = waypointMarkerMap.get(event.getWaypoint());
		Map markerMap = marker.getAttributes();
		for (Object key : newMarkerAttributes.keySet()) {
			markerMap.put(key, newMarkerAttributes.get(key));
		}
		marker.setAttributes(markerMap);
		
	}

	/**
	 * @param waypoint
	 */
	@SuppressWarnings({"unchecked"})  //$NON-NLS-1$
	private void createNewMarker(IWaypoint waypoint) throws CoreException {
		String fileName = waypoint.getStringValue(ICWaypointAttributes.ATTR_RESOURCE, ""); //$NON-NLS-1$
		if (!"".equals(fileName)) { //$NON-NLS-1$
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
			//can't create a marker while the tree is locked.
			if (file.getWorkspace().isTreeLocked()) {
				throw new CoreException(new Status(
					Status.WARNING,
					CWaypointsPlugin.PLUGIN_ID,
					Status.WARNING,
					"Could not create marker for Java Waypoint",
					null
				));
			}
			HashMap attrs = new HashMap();
			int start = waypoint.getIntValue(ICWaypointAttributes.ATTR_CHAR_START, 1);
			int end = waypoint.getIntValue(ICWaypointAttributes.ATTR_CHAR_END,1);
			String message = waypoint.getText();
			attrs.put(IMarker.CHAR_START, start);
			attrs.put(IMarker.MESSAGE, message);
			attrs.put(IMarker.CHAR_END, end);
			attrs.put("waypointType", ICWaypointsConstants.C_WAYPOINT);
			ITag[] tags = waypoint.getTags();
			String tagString = ""; //$NON-NLS-1$
			for (ITag tag : tags) {
				tagString += tag.getName() + " "; //$NON-NLS-1$
			}
			attrs.put("tags", tagString); //$NON-NLS-1$
			Date date = waypoint.getDate();
			if (date != null) {
				Locale loc = Locale.getDefault();
				String s = loc.getLanguage()+loc.getCountry()+":"+DateFormat.getDateInstance().format(date); //$NON-NLS-1$
				attrs.put(IWaypoint.ATTR_DATE, s);
			}
			
			if (file != null && file.exists()) {
				if (PlatformUI.getWorkbench().isClosing()) return;
				IMarker marker = file.createMarker(ICWaypointsConstants.WAYPOINT_MARKER);
				if (marker != null && marker.exists()) {
					marker.setAttributes(attrs);
					waypointMarkerMap.put(waypoint, marker);
					markerWaypointMap.put(marker, waypoint);
				}
			}
		}
	}

	/**
	 * @param waypoint
	 */
	private void handleDelete(IWaypoint waypoint) {
		//remove the marker for the given waypoint.
		IMarker marker = waypointMarkerMap.get(waypoint);
		waypointMarkerMap.remove(waypoint);
		markerWaypointMap.remove(marker);
		if (marker != null && marker.exists()) {
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param waypoint
	 * @throws CoreException 
	 */
	private void handleNew(IWaypoint waypoint) throws CoreException {
		createNewMarker(waypoint);
	}
	
	protected void addFileBuffer(ITextFileBuffer buffer) {
		documentBufferMap.put(buffer.getDocument(), buffer);
		buffer.getDocument().addDocumentListener(documentChangedListener);
		
	}
	
	protected void removeFileBuffer(ITextFileBuffer buffer) {
		documentBufferMap.put(buffer.getDocument(), buffer);
		buffer.getDocument().removeDocumentListener(documentChangedListener);
		if (buffer.isDirty()) {
			IFile file = FileBuffers.getWorkspaceFileAtLocation(buffer.getLocation());
			LinkedList<IFile> files = new LinkedList<IFile>();
			files.add(file);
			internalRun(new FileWaypointRefreshJob(files), false);
		}
	}

	/**
	 * Runs the given operation internally so that refactor events won't occur due to changes
	 * in the model.
	 * @param op
	 */
	public void internalRun(TagSEAOperation op, boolean wait) {
		TagSEAPlugin.run(new InternalOperation(op), wait);
	}
	
	
	/**
	 * Checks for equality between all of the attributes of the waypoints except the length
	 * of it.
	 */
	@Override
	public boolean waypointsEqual(IWaypoint wp0, IWaypoint wp1) {
		boolean equal = true;
		for (String name : wp0.getAttributes()) {
			if (name.equals(ICWaypointAttributes.ATTR_CHAR_END)) continue;
			Object value1 = wp0.getValue(name);
			Object value2 = wp1.getValue(name);
			equal &= (value1.equals(value2));
			if (!equal)
				break;
		}
		return equal;
	}

		
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.AbstractWaypointDelegate#processChange(net.sourceforge.tagsea.model.internal.WaypointChangeEvent)
	 */
	@Override
	public TagSEAChangeStatus processChange(IWaypointChangeEvent event) {
		if (this.state == JavaWaypointState.REFACTORING) {
			return new TagSEAChangeStatus(CWaypointsPlugin.PLUGIN_ID, false, CWaypointsPlugin.CONCURRENT_CHANGE_ERROR, "Could not perform waypoint change due to concurrent changes in the java waypoint platform.");
		}
		if (this.state == JavaWaypointState.IDLE) {
			List<String> attributes = Arrays.asList(event.getChangedAttributes());
			if (attributes.contains(ICWaypointAttributes.ATTR_CHAR_START) ||
				attributes.contains(ICWaypointAttributes.ATTR_RESOURCE) ||
				attributes.contains(ICWaypointAttributes.ATTR_CHAR_END)) {
				//System.err.println("Big Mistake!");
				return new TagSEAChangeStatus(CWaypointsPlugin.PLUGIN_ID, false, CWaypointsPlugin.ILLEGAL_CHANGE_ERROR, "Attribute cannot be changed outside the Java Waypoints platform.");
			}
				
		}
		return super.processChange(event);
	}
	
	/**
	 * Sets the current state to the given state, and saves the last one on the stack.
	 * @param state
	 */
	private synchronized void pushState(JavaWaypointState state) {
		stateStack.add(this.state);
		this.state = state;
	}
	
	/**
	 * Resets the state of tehdelegate to the previous state.
	 */
	private synchronized void popState() {
		this.state = stateStack.removeLast();
	}
	
	public IWaypoint getWaypointForMarker(IMarker marker) {
		return markerWaypointMap.get(marker);
	}
	
	public IMarker getMarkerForWaypoint(IWaypoint waypoint) {
		return waypointMarkerMap.get(waypoint);
	}

}
