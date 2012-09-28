/*******************************************************************************
 * 
 *   Copyright 2007, 2008, CHISEL Group, University of Victoria, Victoria, BC, Canada. 
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *       The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.mylyn.task.actions;

import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.mylyn.task.TaskMylynPlugin;
import net.sourceforge.tagsea.mylyn.task.TaskUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class TaskMylynSubmenuContributor implements IDynamicSubMenuContributor {

	private static final String TASKS_LABEL = "Eclipse Tasks";

	// @tag tagsea.mylyn -author="John" -date="enCA:31/10/07" : borrowed from
	// org.eclipse.mylyn.internal.tasks.ui.ScheduleTaskMenuContributor
	public MenuManager getSubMenuManager(
			List<AbstractTaskContainer> selectedElements) {

		final TaskListManager tasklistManager = TasksUiPlugin
				.getTaskListManager();

		final MenuManager subMenuManager = new MenuManager(TASKS_LABEL);

		subMenuManager.setVisible(selectedElements.size() > 0);

		for (AbstractTaskContainer selection : selectedElements) {
			if (selection instanceof AbstractTask) {
				final AbstractTask task = tasklistManager.getTaskForElement(
						selection, false);
				Set<IMarker> todos = TaskMylynPlugin.getDefault().getMarkers(task);

				if (todos != null) {
					for (IMarker todo : todos) {

						Action action = new NavigateToTask(todo);
						action.setText(TaskUtils.getDescription(todo) + "("
								+ TaskUtils.getLocation(todo) + ")");
						action.setEnabled(true);
						subMenuManager.add(action);
					}
				}else{
					System.err.println("Couldn't find TODOs for task.");
				}
			}
		}
		return subMenuManager;
	}

	private class NavigateToTask extends Action {

		private final IMarker todo;

		NavigateToTask(IMarker todo) {
			this.todo = todo;
		}

		@Override
		public void run() {

			// @tag tagsea.mylyn.task.navigate : Refactor into the utils? 
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(page, todo);
				return;
			} catch (PartInitException e) {
				e.printStackTrace();
			}

		}
	}

}
