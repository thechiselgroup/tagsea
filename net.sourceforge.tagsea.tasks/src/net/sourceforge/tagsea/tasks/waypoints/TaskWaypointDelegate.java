/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.tasks.waypoints;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITagSEAOperationStateListener;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.tasks.ITaskWaypointAttributes;
import net.sourceforge.tagsea.tasks.TaskWaypointPlugin;
import net.sourceforge.tagsea.tasks.TaskWaypointUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Delegate for finding tasks in Java code. Finds the Java TODOs, etc. based on the tasks in a resource.
 * @author Del Myers
 *
 */
public class TaskWaypointDelegate extends AbstractWaypointDelegate implements ITagSEAOperationStateListener {
	private final Object LOCK = new Object();
	private boolean allowChange = false;
	
	private class ResourceListener implements IResourceChangeListener {
		private LinkedList<IMarker> interesting = new LinkedList<IMarker>();
		private UpdateWaypointOperation updater;
		private class DeltaVistitor implements IResourceDeltaVisitor {
			/* (non-Javadoc)
			 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
			 */
			public boolean visit(IResourceDelta delta) throws CoreException {
				if ((delta.getFlags() & IResourceDelta.MARKERS) != 0) {
					IMarkerDelta[] mdeltas = delta.getMarkerDeltas();
					if (mdeltas.length > 0) {
						for (IMarkerDelta mdelta : mdeltas) {
							if (TaskWaypointUtils.getWaypointForTask(mdelta.getMarker()) != null ||
									isInterestingDelta(mdelta)) {
								interesting.add(mdelta.getMarker());
							}
						}
					} 
				}
				return true;
			}

			/**
			 * @param mdelta
			 * @return
			 */
			private boolean isInterestingDelta(IMarkerDelta mdelta) {
				if (!mdelta.getMarker().exists()) {
					if (mdelta.isSubtypeOf(IMarker.TASK)) {
						String message = mdelta.getAttribute(IMarker.MESSAGE, null);
						int offset = mdelta.getAttribute(IMarker.CHAR_START, -1);
						int end = mdelta.getAttribute(IMarker.CHAR_END, -1);
						if (message != null && offset != -1 && end != -1) {
							String[] words = message.split("\\s+");
							for (String tag : TaskWaypointUtils.getJavaTaskTags()) {
								if (words[0].equals(tag)) return true;
							}
						}
					}
					return false;
				}
				return TaskWaypointUtils.isInterestingMarker(mdelta.getMarker());
			}
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			
				DeltaVistitor visitor = new DeltaVistitor();
				interesting.clear();
				try {
					if (event.getDelta() == null) return;
					event.getDelta().accept(visitor);
				} catch (CoreException e) {
					return;
				}
				if (interesting.size() > 0) {
					if (updater == null || !updater.join(interesting)) {
						updater = new UpdateWaypointOperation(interesting);
//						must run later because this thread starts due to a resource change, and it may result in a resource change.
						TagSEAPlugin.run(updater, false);
					}
				}
				
				
		}
	}
	
	/**
	 * 
	 */
	public TaskWaypointDelegate() {
		TagSEAPlugin.addOperationStateListener(this);
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.AbstractWaypointDelegate#load()
	 */
	@Override
	protected void load() {
		TagSEAPlugin.run(new TaskLoadOperation(), true);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceListener());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.AbstractWaypointDelegate#navigate(net.sourceforge.tagsea.core.IWaypoint)
	 */
	@Override
	public void navigate(final IWaypoint waypoint) {
		Display.getDefault().asyncExec(new Runnable(){

			public void run() {
				IMarker task = TaskWaypointUtils.getTaskForWaypoint(waypoint);
				if (task == null || !task.exists()) return;
				IWorkbenchPage page = 
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, task);
				} catch (PartInitException e) {
				}
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.AbstractWaypointDelegate#tagsChanged(net.sourceforge.tagsea.core.TagDelta)
	 */
	@Override
	protected void tagsChanged(TagDelta delta) {
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.AbstractWaypointDelegate#waypointsChanged(net.sourceforge.tagsea.core.WaypointDelta)
	 */
	@Override
	protected void waypointsChanged(WaypointDelta delta) {
		//don't think that we can do anything here.
//		for (IWaypointChangeEvent event : delta.changes) {
//			IMarker marker = TaskWaypointUtils.getTaskForWaypoint(event.getWaypoint());
//			if (marker == null) {
//				//mark for delete?
//				createNewMarker(event.getWaypoint());
//			} else {
//				if (event.getType() == IWaypointChangeEvent.DELETE) {
//					try {
//						marker.delete();
//					} catch (CoreException e) {
//						e.printStackTrace();
//					}
//				} else if (event.getType() == IWaypointChangeEvent.CHANGE){
//					List<String> changes = Arrays.asList(event.getChangedAttributes());
//					IMarker task = TaskWaypointUtils.getTaskForWaypoint(event.getWaypoint());
//					if (task != null) {
//						if (changes.contains(IWaypoint.ATTR_AUTHOR)) {
//							try {
//								task.setAttribute(IWaypoint.ATTR_AUTHOR, event.getNewValue(IWaypoint.ATTR_AUTHOR));
//							} catch (CoreException e) {
//							}
//						}
//					}
//				}
//			}
//		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.AbstractWaypointDelegate#processChange(net.sourceforge.tagsea.core.IWaypointChangeEvent)
	 */
	@Override
	public TagSEAChangeStatus processChange(IWaypointChangeEvent event) {
		
		if (allowChange) {
			return TagSEAChangeStatus.SUCCESS_STATUS;
		}
		List<String> changes = Arrays.asList(event.getChangedAttributes());
		if (changes.size() == 1) {
			if (changes.contains(ITaskWaypointAttributes.ATTR_AUTHOR)) {
				return TagSEAChangeStatus.SUCCESS_STATUS;
			}
		}
		return new TagSEAChangeStatus(TaskWaypointPlugin.PLUGIN_ID, false, -1, "Illegal delegate state.");
	}
	
//	/**
//	 * @param waypoint
//	 */
//	private void createNewMarker(final IWaypoint waypoint) {
//		IWorkspaceRunnable r= new IWorkspaceRunnable() {
//			public void run(IProgressMonitor monitor) throws CoreException {
//				IResource resource = ResourceWaypointUtils.getResource(waypoint);
//				if (resource != null) {
//					IMarker marker= resource.createMarker(TaskWaypointPlugin.MARKER_ID);
//					marker.setAttribute(IMarker.MESSAGE + "Wht bdfdf", waypoint.getText());
//					marker.setAttribute("waypointType", TaskWaypointPlugin.WAYPOINT_ID);
//					marker.setAttribute(IMarker.CHAR_START, TaskWaypointUtils.getOffset(waypoint));
//					marker.setAttribute(IMarker.CHAR_END, TaskWaypointUtils.getEnd(waypoint));
//					marker.setAttribute("taskReference", ""+TaskWaypointUtils.getTaskID(waypoint));
//				}
//				
//			}
//		};
//		try {
//			ResourcesPlugin.getWorkspace().run(r, new NullProgressMonitor());
//		} catch (CoreException e) {
//		}
//	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ITagSEAOperationStateListener#stateChanged(net.sourceforge.tagsea.core.TagSEAOperation)
	 */
	public void stateChanged(TagSEAOperation operation) {
		if (operation instanceof IInternalWaypointOperation) {
			synchronized (LOCK) {
				switch (operation.getState()) {
				case QUEUED:
				case RUNNING:
					allowChange = true;
					break;
				default:
					allowChange = false;
				}
			}
		}
	}



}
