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
package net.sourceforge.tagsea.instrumentation.tasks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import net.sourceforge.tagsea.core.ui.internal.TagSEAUIEvent;
import net.sourceforge.tagsea.instrumentation.TagSEAInstrumentationPlugin;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.markers.internal.TableView;
import org.eclipse.ui.views.markers.internal.TaskMarker;
import org.eclipse.ui.views.markers.internal.TaskView;

/**
 * A class that contains several listeners for monitoring tasks in the task list.
 * @author Del Myers
 *
 */
public class TaskListMonitoring {
	
	private class WindowListener implements IWindowListener {
		IWorkbenchWindow activeWindow;
		PageListener pageListener = new PageListener();
		
		public void windowActivated(IWorkbenchWindow window) {
				disconnect();
				this.activeWindow = window;
				window.addPageListener(pageListener);
				pageListener.pageActivated(window.getActivePage());
		}

		public void windowClosed(IWorkbenchWindow window) {
		}

		public void windowDeactivated(IWorkbenchWindow window) {
		}

		public void windowOpened(IWorkbenchWindow window) {
		}
		
		void disconnect() {
			pageListener.disconnect();
			if (activeWindow != null) {
				activeWindow.removePageListener(pageListener);
				activeWindow = null;
			}
		}
	}
	
	private class PageListener implements IPageListener {
		private PartListener partListener = new PartListener();
		private IWorkbenchPage activePage; 

		public void pageActivated(IWorkbenchPage page) {
			disconnect();
			activePage = page;
			activePage.addPartListener(partListener);
			IViewReference reference = page.findViewReference(TASK_LIST_ID);
			if (reference != null) {
				partListener.partOpened(reference);
			}
		}

		public void pageClosed(IWorkbenchPage page) {
			if (page == activePage) {
				disconnect();
			}
		}

		public void pageOpened(IWorkbenchPage page) {
		}
		
		void disconnect() {
			partListener.disconnect();
			if (activePage != null) {
				activePage.removePartListener(partListener);
				this.activePage = null;
			}
		}
		
	}
	
	private class PartListener implements IPartListener2 {
		IOpenListener openListener = new TaskOpenListener();
		TreeViewer taskTableViewer;

		public void partActivated(IWorkbenchPartReference partRef) {}
		public void partBroughtToTop(IWorkbenchPartReference partRef) {}
		@SuppressWarnings("restriction")
		public void partClosed(IWorkbenchPartReference partRef) {
			if (TASK_LIST_ID.equals(partRef.getId())) {
				IWorkbenchPart part = partRef.getPart(false);
				if (part != null) {
					TagSEAInstrumentationPlugin.getDefault().logUIEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_CLOSED, TASK_LIST_ID, ""));
					TaskView taskView = (TaskView)part;
					TreeViewer viewer;
					try {
						viewer = getTaskListTableViewer(taskView);
						viewer.removeOpenListener(openListener);
						if (viewer == taskTableViewer) {
							taskTableViewer = null;
						}
					} catch (Exception e) {
						TagSEAInstrumentationPlugin.getDefault().getLog().log(new Status(
							Status.ERROR,
							TagSEAInstrumentationPlugin.PLUGIN_ID,
							"Could not invoke listener on Task List",
							e
						));
						TagSEAInstrumentationPlugin.getDefault().logUIEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_CLOSED, TASK_LIST_ID, "Error: Could not access Tasks View using reflection"));
						TaskListMonitoring.this.disconnect();
						return;
					}
					
				}
			}
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {}

		public void partHidden(IWorkbenchPartReference partRef) {}

		public void partInputChanged(IWorkbenchPartReference partRef) {}

		@SuppressWarnings("restriction")
		public void partOpened(IWorkbenchPartReference partRef) {
			if (TASK_LIST_ID.equals(partRef.getId())) {
				IWorkbenchPart part = partRef.getPart(false);
				TagSEAInstrumentationPlugin.getDefault().logUIEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_OPENED, TASK_LIST_ID, ""));
				if (part != null) {
					try {
						TaskView taskView = (TaskView) part;
						TreeViewer viewer = getTaskListTableViewer(taskView);
						if (viewer != null) {
							if (taskTableViewer != null) {
								taskTableViewer.removeOpenListener(openListener);
							}
							taskTableViewer = viewer;
							taskTableViewer.addOpenListener(openListener);

						}
					} catch (Exception e) {
						TagSEAInstrumentationPlugin.getDefault().getLog().log(new Status(
								Status.ERROR,
								TagSEAInstrumentationPlugin.PLUGIN_ID,
								"Could not invoke listener on Tasks View",
								e
						));
						TagSEAInstrumentationPlugin.getDefault().logUIEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_OPENED, TASK_LIST_ID, "Error: Could not access Tasks View using reflection"));
						TaskListMonitoring.this.disconnect();
						return;
					}
				}
			}
			
		}
		
		@SuppressWarnings({ "unchecked", "restriction" })
		TreeViewer getTaskListTableViewer(TaskView taskView) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			Class taskClass = TableView.class;
			Method tableViewerMethod = taskClass.getDeclaredMethod("getViewer", new Class[0]);
			if (tableViewerMethod != null) {
				tableViewerMethod.setAccessible(true);
				Object result = tableViewerMethod.invoke(taskView, new Object[0]);
				return (TreeViewer) result;
			}
			return null;
		}

		void disconnect() {
			if (taskTableViewer != null) {
				taskTableViewer.removeOpenListener(openListener);
				taskTableViewer = null;
			}
		}
		
		public void partVisible(IWorkbenchPartReference partRef) {}
	}
	
	private class TaskOpenListener implements IOpenListener {

		public void open(OpenEvent event) {
			if (event.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				for (Iterator i = ss.iterator(); i.hasNext();) {
					Object o = i.next();
					if (o instanceof TaskMarker) {
						TaskMarker marker = (TaskMarker) o;
						TagSEAInstrumentationPlugin.getDefault().logTaskEvent(marker);
					}
				}
			}
		}
		
	}
	
	
	private final String TASK_LIST_ID = "org.eclipse.ui.views.TaskList";
	
	private WindowListener windowListener = new WindowListener();
	private boolean connected = false;
	public void connect() {
		if (connected) {
			return;
		}
		connected = true;
		PlatformUI.getWorkbench().addWindowListener(windowListener);
		windowListener.windowActivated(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
	}
	
	public void disconnect() {
		if (connected) {
			windowListener.disconnect();
			PlatformUI.getWorkbench().removeWindowListener(windowListener);
			connected = false;
		}
	}

}
