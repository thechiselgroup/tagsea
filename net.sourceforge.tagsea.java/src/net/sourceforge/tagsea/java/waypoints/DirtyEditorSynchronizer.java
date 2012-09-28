/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.java.waypoints;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.TagSEAModelException;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.java.IJavaWaypointsConstants;
import net.sourceforge.tagsea.java.JavaTagsPlugin;
import net.sourceforge.tagsea.java.JavaWaypointUtils;
import net.sourceforge.tagsea.java.resources.internal.FileWaypointRefreshJob;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * A class that saves dirty editors and synchronizes stale waypoints before a refactor
 * operation.
 * @author Del Myers
 */

//note this class is assumed to be run internally by the java waypoint delegate, so it does it's own updating of waypoints, etc.
class DirtyEditorSynchronizer implements IRunnableWithProgress {

	private WaypointDelta delta;
	//a list of waypoints that could not be synchronized.
	private List<IWaypoint> badWaypoints;
	private List<IWaypointChangeEvent> newEvents;

	public DirtyEditorSynchronizer(WaypointDelta delta) {
		this.delta = delta;
		badWaypoints = new ArrayList<IWaypoint>();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(final IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask("Saving Editors...", 310);
		List<ITextFileBuffer> dirtyBuffers = new ArrayList<ITextFileBuffer>();
		for (IWaypointChangeEvent event : delta.changes) {
			if ((IJavaWaypointsConstants.JAVA_WAYPOINT.equals(event.getWaypoint().getType()))) {
				IFile file = JavaWaypointUtils.getFile(event.getWaypoint());
				if (file != null && file.exists()) {
					ITextFileBuffer buffer = 
						FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
					if (buffer != null && buffer.isDirty() && !dirtyBuffers.contains(buffer)) {
						dirtyBuffers.add(buffer);
					}
				}
			}
		}
		
		//get the relevant dirty editors.
		IEditorPart[] editors = getDirtyEditors();
		List<IEditorPart> editorsToSave = new ArrayList<IEditorPart>();
		for (ITextFileBuffer buffer : dirtyBuffers) {
			for (IEditorPart editor : editors) {
				if (editor.getEditorInput() instanceof IFileEditorInput) {
					IPath location = ((IFileEditorInput)editor.getEditorInput()).getFile().getFullPath();
					if (buffer.getLocation().equals(location)) {
						if (!editorsToSave.contains(editor)) {
							editorsToSave.add(editor);
						}
					}
				}
			}
		}
		monitor.worked(10);
		if (editorsToSave.size() == 0) {
			newEvents = new ArrayList<IWaypointChangeEvent>(Arrays.asList(delta.getChanges()));
			monitor.done();
			return;
		}
		openSaveDialog(editorsToSave.toArray(new IEditorPart[editorsToSave.size()]));
		final int work = 100/editorsToSave.size();
		LinkedList<IFile> filesToRefresh = new LinkedList<IFile>();
		for (final IEditorPart part : editorsToSave) {
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					part.doSave(new SubProgressMonitor(monitor, work));
				}
			});
			
			//update the file.
			IFile file = ((IFileEditorInput)part.getEditorInput()).getFile();
			filesToRefresh.add(file);
			
		}
		if (filesToRefresh.size() > 0) {
			FileWaypointRefreshJob refresher = new FileWaypointRefreshJob(filesToRefresh);
			//refresh the file. This is necessary so that the waypoints can be synchronized.
			TagSEAPlugin.syncRun(refresher, new SubProgressMonitor(monitor, work));
		}
		synchronizeWaypoints(new SubProgressMonitor(monitor, 100, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
		
		
		monitor.done();
	}

	/**
	 * Synchronizes the previous waypoints in the delta with the ones that currently exist.
	 * @param monitor
	 */
	private void synchronizeWaypoints(SubProgressMonitor monitor) {
		List<IWaypointChangeEvent> events = new LinkedList<IWaypointChangeEvent>(Arrays.asList(delta.changes));
		HashMap<IWaypoint, IWaypoint> oldNewMap = new HashMap<IWaypoint, IWaypoint>();
		monitor.beginTask("Synchronizing waypoints...", events.size());
		for (int i = 0; i < events.size(); i++) {
			IWaypointChangeEvent event = events.get(i);
			if ((IJavaWaypointsConstants.JAVA_WAYPOINT.equals(event.getType()))) {
				if (!event.getWaypoint().exists()) {
					IFile file = JavaWaypointUtils.getFile(event.getWaypoint());
					IWaypoint newWaypoint = oldNewMap.get(event.getWaypoint());
					if (!badWaypoints.contains(event.getWaypoint())) {
						if (newWaypoint == null && file != null && file.exists()) {
							try {
								newWaypoint = findNewWaypoint(event.getWaypoint(), file);
								oldNewMap.put(event.getWaypoint(), newWaypoint);
							} catch (TagSEAModelException e) {
								//do nothing, leave as null.
							}
						}
						if (newWaypoint == null) {
							//if it is still null, flag the waypoint for removal;
							badWaypoints.add(event.getWaypoint());
						} else {
//							replace the event in the list.
							events.remove(i);
							events.add(i, new ReplacedWaypointEvent(event, newWaypoint));
						}
					}
				}
			}
			monitor.worked(1);
		}
		//run through the list and remove the bad waypoints.
		for (int i = 0; i < events.size(); i++) {
			IWaypointChangeEvent event = events.get(i);
			if (badWaypoints.contains(event.getWaypoint())) {
				events.remove(i);
				i--;
			}
		}
		this.newEvents = events;
		monitor.done();
	}
	
	/**
	 * Gets the waypoint changes that are applicable after the saving is complete.
	 * @return the waypoint changes that are applicable after the saving is complete.
	 */
	public IWaypointChangeEvent[] getWaypointChanges() {
		return newEvents.toArray(new IWaypointChangeEvent[newEvents.size()]);
	}

	/**
	 * Finds a waypoint with the exact same attributes as the oldWaypoint and
	 * returns that as a new waypoint, or null if an equal waypoint could not
	 * be found.
	 * @param waypoint
	 * @param file
	 * @return
	 * @throws TagSEAModelException 
	 */
	private IWaypoint findNewWaypoint(IWaypoint oldWaypoint, IFile file) throws TagSEAModelException {
		IWaypoint[] waypoints = JavaTagsPlugin.getJavaWaypointsForFile(file);
		for (IWaypoint waypoint : waypoints) {
			String[] attributes = waypoint.getAttributes();
			boolean equal = true;
			for (String key : attributes) {
				Object oldv = oldWaypoint.getValue(key);
				Object newv = waypoint.getValue(key);
				equal &= (oldv != null) && (newv != null);
				if (equal) {
					equal &= oldv.equals(newv);
				}
				if (!equal) {
					break;
				}
			}
			if (equal) {
				return waypoint;
			}
		}
		return null;
	}

	/**
	 * @param parts
	 */
	private void openSaveDialog(final IEditorPart[] parts) {
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				ListDialog dialog = new ListDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				dialog.setTitle("Save Editors"); 
				dialog.setAddCancelButton(false);
				dialog.setLabelProvider(createDialogLabelProvider());
				dialog.setMessage("The following resources will be saved before continuing."); 
				dialog.setContentProvider(new ArrayContentProvider());
				dialog.setInput(parts);
				dialog.open();
			}
		});
	}
	
	private ILabelProvider createDialogLabelProvider() {
		return new LabelProvider() {
			public Image getImage(Object element) {
				return ((IEditorPart) element).getTitleImage();
			}
			public String getText(Object element) {
				return ((IEditorPart) element).getTitle();
			}
		};
	}	
	
	IEditorPart[] getDirtyEditors() {
		final Set<IEditorInput> inputs= new HashSet<IEditorInput>();
		final List<IEditorPart> result= new ArrayList<IEditorPart>(0);
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				IWorkbench workbench= PlatformUI.getWorkbench();
				IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
				for (int i= 0; i < windows.length; i++) {
					IWorkbenchPage[] pages= windows[i].getPages();
					for (int x= 0; x < pages.length; x++) {
						IEditorPart[] editors= pages[x].getDirtyEditors();
						for (int z= 0; z < editors.length; z++) {
							IEditorPart ep= editors[z];
							IEditorInput input= ep.getEditorInput();
							if (!inputs.contains(input)) {
								inputs.add(input);
								result.add(ep);
							}
						}
					}
				}
			}
			
		});
		return (IEditorPart[])result.toArray(new IEditorPart[result.size()]);
	}

}
