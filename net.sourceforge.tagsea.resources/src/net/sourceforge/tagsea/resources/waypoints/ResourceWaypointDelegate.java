package net.sourceforge.tagsea.resources.waypoints;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITagSEAOperationStateListener;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.IWaypointLocator;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;
import net.sourceforge.tagsea.resources.waypoints.operations.IInternalUpdateOperation;
import net.sourceforge.tagsea.resources.waypoints.operations.LoadProjectWaypointsOperation;
import net.sourceforge.tagsea.resources.waypoints.operations.WaypointProjectXMLWriteOperation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.navigator.IResourceNavigator;

public class ResourceWaypointDelegate extends AbstractWaypointDelegate {
	/**
	 * A runnable to open up the given waypoint. Tries to find the best editor
	 * for the resource that the waypoint is on, and opens it.
	 * @author Del Myers
	 */
	private class WaypointNavigator implements Runnable {
		private IWaypoint waypoint;
		WaypointNavigator(IWaypoint waypoint) {
			this.waypoint = waypoint;
		}
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() 
		{
			int charStart = waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_START, -1);
			int charEnd = waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_END, -1);
			String handleIdentifier = waypoint.getStringValue(IResourceWaypointAttributes.ATTR_HANDLE_IDENTIFIER, null);

			// This is a text waypoint?
			if(charStart > 0 && charEnd > 0)
			{
				// A class file text waypoint
				if(handleIdentifier != null && handleIdentifier.trim().length()>0)
				{
					IJavaElement element = JavaCore.create(handleIdentifier);
					try 
					{
						IEditorPart part = EditorUtility.openInEditor(element);

						if(part instanceof ITextEditor)
						{
							ITextEditor editor = (ITextEditor)part;
							editor.getSelectionProvider().setSelection(new TextSelection(charStart,charEnd - charStart));
						}
					} 
					catch (PartInitException e) 
					{
						e.printStackTrace();
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				else
				{
					// Regular text waypoint
					IMarker marker = ResourceWaypointMarkerHelp.getMarker(waypoint);

					if(marker!=null)
					{
						IWorkbenchPage page = 
							ResourceWaypointPlugin.
							getDefault().
							getWorkbench().
							getActiveWorkbenchWindow().
							getActivePage();

						try 
						{
							IDE.openEditor(page, marker);
						} 
						catch (PartInitException e) 
						{
							e.printStackTrace();
						}
					}
				}
				return;
			}

			IPath resourcePath = ResourceWaypointUtils.getResourcePath(waypoint);
			if (resourcePath == null) return;
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(resourcePath);
			IWorkbenchPage page = 
				ResourceWaypointPlugin.
				getDefault().
				getWorkbench().
				getActiveWorkbenchWindow().
				getActivePage();
			if (resource instanceof IFile) {
				try {
					IEditorPart editor = IDE.openEditor(page, ((IFile)resource));
					if (editor instanceof ITextEditor) {
						ITextEditor textEditor = (ITextEditor) editor;
						IDocument doc = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
						if (doc != null) {
							int line = ResourceWaypointUtils.getLine(waypoint) - 1;
							if (line > 0) {
								try {
									int offset = doc.getLineOffset(line);
									int length = doc.getLineLength(line);
									textEditor.selectAndReveal(offset, length);
								} catch (BadLocationException e) {}
							}
						}
					}
				} catch (PartInitException e) {
					ResourceWaypointPlugin.getDefault().log(e);
				}
			} else {
				try {
					IViewPart navigator = page.showView(IPageLayout.ID_RES_NAV);
					if (navigator instanceof IResourceNavigator) {
						page.activate(navigator);
						((IResourceNavigator)navigator).getViewer().setSelection(new StructuredSelection(resource));
					}
				} catch (PartInitException e) {
					ResourceWaypointPlugin.getDefault().log(e);
				}
			}
		}

	}

	private class SaveParticipant implements ISaveParticipant {
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context) {
		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException {

		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context) {
			//can't rollback saves

		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
		 */
		public void saving(ISaveContext context) throws CoreException {
			if (context.getKind() == ISaveContext.PROJECT_SAVE) {
				IProject project = context.getProject();
				if (project != null) {
					ProjectWaypointSaveScheduler saver = projectSavers.get(project);
					if (saver != null) {
						saver.cancel();
					}
					performSave(project);
				}
				return;
			}
			//only care about full saves.
			if (context.getKind() != ISaveContext.FULL_SAVE) return;

			//cancel current jobs and perform a save
			performSave();
		}

	}

	
	//
	private class InternalOperationListener implements ITagSEAOperationStateListener {

		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.ITagSEAOperationStateListener#stateChanged(net.sourceforge.tagsea.core.TagSEAOperation)
		 */
		public void stateChanged(TagSEAOperation operation) {
			if (operation instanceof LoadProjectWaypointsOperation) {
				switch(operation.getState()) {
				case RUNNING: loadingWaypoints = true; break;
				case DONE: loadingWaypoints = false;
				}
			}
			if (operation instanceof IInternalUpdateOperation) {
				switch (operation.getState()) {
				case RUNNING: updateOperation = operation; break;
				case DONE: updateOperation = null;
				}
			}
		}
		
	}

	
	

	/**
	 * Simple scheduler for saving waypoints associated with projects.
	 * @author Del Myers
	 *
	 */
	private final Timer timer = new Timer("Project waypoint save scheduler");
	private class ProjectWaypointSaveScheduler {
		private IProject project;
		
		private TimerTask saveTask;
		private TagSEAOperation op;
		public ProjectWaypointSaveScheduler(IProject project) {
			this.project = project;	
			op = new WaypointProjectXMLWriteOperation(this.project);
		}
		
		public IProject getProject() {
			return project;
		}
		
		public void reschedule() {
			if (saveTask != null)
				saveTask.cancel();
			saveTask = createTask();
			timer.schedule(saveTask, 10000);
		}
		
		private TimerTask createTask() {
			return new TimerTask() {
				public void run() {
					TagSEAPlugin.run(op, false);
				}
			};
		}
		
		public void cancel() {
			if (saveTask != null)
				saveTask.cancel();
		}
	}
	
	
	private HashMap<IProject, ProjectWaypointSaveScheduler> projectSavers;
	private TagSEAOperation updateOperation;
	//flag set for loading waypoints. We don't have to schedule saves if the waypoint changes occurr as a
	//result of a load operation.
	private boolean loadingWaypoints;
	//private boolean updatingRevision;
	
	//the last time that a stamp was calculated.
	private long lastStampTime;
	//used to incremement stamps that are created in the same millisecond.
	private long stampCount;
	private ResourceWaypointLocator locator;
	public ResourceWaypointDelegate() {
		projectSavers = new HashMap<IProject, ProjectWaypointSaveScheduler>();
		lastStampTime = 0;
		stampCount = 0;
	}



	@Override
	protected void load() {
		IProject[] projects = 
			ResourcesPlugin.getWorkspace().getRoot().getProjects();
		TagSEAPlugin.addOperationStateListener(new InternalOperationListener());
		for (IProject p : projects) {
			TagSEAPlugin.run(new LoadProjectWaypointsOperation(p), true);
			ResourceWaypointMarkerHelp.listenToMarkers(true);
		}
		//WaypointSynchronizerHelp.INSTANCE.start();
		try {
			ResourcesPlugin.getWorkspace().addSaveParticipant(ResourceWaypointPlugin.getDefault(), new SaveParticipant());
		} catch (CoreException e) {
			ResourceWaypointPlugin.getDefault().log(e);
		}
	}

	@Override
	public void navigate(IWaypoint waypoint) {
		Display.getDefault().asyncExec(new WaypointNavigator(waypoint));
	}

	@Override
	protected void tagsChanged(TagDelta delta) {
		//scheduleSave();
	}

	@Override
	protected void waypointsChanged(WaypointDelta delta) {
		ResourceWaypointMarkerHelp.synchronize(delta);
		scheduleSave();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.AbstractWaypointDelegate#processChange(net.sourceforge.tagsea.core.IWaypointChangeEvent)
	 */
	@Override
	public TagSEAChangeStatus processChange(IWaypointChangeEvent event) {
//		if (updatingRevision) 
//			return TagSEAChangeStatus.SUCCESS_STATUS;
		String[] attributes = event.getChangedAttributes();
		for (String attr : attributes) {
			if (attr.equals(IResourceWaypointAttributes.ATTR_STAMP)) {
				if (updateOperation == null) {
					return new TagSEAChangeStatus(ResourceWaypointPlugin.PLUGIN_ID, false, 0, "Attribute 'stamp' is for internal use only");
				}
			} else if (attr.equals(IResourceWaypointAttributes.ATTR_REVISION)) {
//				if (updateOperation == null && !updatingRevision) {
//					return new TagSEAChangeStatus(ResourceWaypointPlugin.PLUGIN_ID, false, 0, "Attribute 'revisionStamp' is for internal use only");
//				}
			}
		}
		TagSEAChangeStatus status = super.processChange(event);
//		if (status.changePerformed && updateOperation == null) {
//			updatingRevision = true;
//			event.getWaypoint().setStringValue(IResourceWaypointAttributes.ATTR_REVISION, getDefaultValue(IResourceWaypointAttributes.ATTR_REVISION).toString());
//			updatingRevision = false;
//		}
		return status;
	}

	/**
	 * 
	 */
	private synchronized void scheduleSave(IProject project) {
		if (!loadingWaypoints) {
			ProjectWaypointSaveScheduler saver = projectSavers.get(project);
			if (saver == null) {
				saver = new ProjectWaypointSaveScheduler(project);
				projectSavers.put(project, saver);
			}
			saver.reschedule();
		}
	}
	
	private void scheduleSave() {
		for (IProject project :ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			scheduleSave(project);
		}
	}
	
	public void performSave(IProject project) {
		ProjectWaypointSaveScheduler saver = projectSavers.get(project);
		if (saver == null) {
			saver = new ProjectWaypointSaveScheduler(project);
			projectSavers.put(project, saver);
		}
		saver.cancel();
		TagSEAPlugin.run(new WaypointProjectXMLWriteOperation(project), true);
	}
	
	public void performSave() {
		for (IProject project :ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			performSave(project);
		}
	}
	
	/**
	 * Calculates a unique stamp for the given waypont if it doesn't already have one. The stamp is not
	 * applied to the waypoint, it is simply returned.
	 * @param waypoint the waypoin to calculate the stamp for.
	 * @return the stamp.
	 */
	public final String calculateStampForWaypoint(IWaypoint waypoint) {
		Object defaultStamp = getDefaultValue(IResourceWaypointAttributes.ATTR_STAMP);
		String stamp = waypoint.getStringValue(IResourceWaypointAttributes.ATTR_STAMP, defaultStamp.toString());
		if (!stamp.equals(defaultStamp)) {
			return stamp;
		}
		
		String user = System.getProperty("user.name"); 
		if (user == null) {
			user = "" + Math.random();
		}
		long time = System.currentTimeMillis();
		if (lastStampTime != time) {
			lastStampTime = time;
			stampCount = 0;
		} else {
			stampCount++;
		}
		return user.hashCode() + ":" + time + ":" + stampCount;
	}
	
	
	@Override
	public IWaypointLocator getLocator() {
		if (this.locator == null) {
			this.locator = new ResourceWaypointLocator();
		}
		return this.locator;
	}
	

}