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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.mylyn.core.TagSEAMylynCorePlugin;
import net.sourceforge.tagsea.mylyn.task.TaskMylynPlugin;
import net.sourceforge.tagsea.mylyn.task.TaskUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskSelectionDialog;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

public class TaskExistingTask implements IViewActionDelegate {

	private TaskSelectionDialog dialog;
	private List<IMarker> taskMarkers;

	public TaskExistingTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IViewPart view) {
System.err.println("Adding todo to existing task inited");
	}

	@Override
	public void run(IAction action) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		if (shell == null) {
			System.err.println("NO SHELL!");
		}

		this.dialog = new TaskSelectionDialog(shell);

		dialog.setTitle("Select Task");
		dialog
				.setMessage("&Select a task to add Eclipse tasks to (? = any character, * = any String):");
		dialog.setShowExtendedOpeningOptions(true);
		if (dialog.open() != Window.OK) {
			System.err.println("Not OK opening window");
			return;
		}

		Object result = dialog.getFirstResult();
		if (result instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) result;

			// Add task references to task description
			List<IMarker> todos = TaskUtils.getNewTodos(task, taskMarkers);
			String todoInfo = TaskUtils
					.getTaskInfo(todos);

			if (task.isLocal()) {
				String existingNotes = task.getNotes();
				if (existingNotes.trim().length() == 0) {
					task.setNotes(todoInfo);
				} else {
					task.setNotes(existingNotes + todoInfo);
				}
			} else {
				//System.out.println("Exisitng repository task");
				TaskDataManager manager = TasksUiPlugin.getTaskDataManager();
				RepositoryTaskData taskData = manager.getNewTaskData(task
						.getRepositoryUrl(), task.getTaskId());
				taskData.setNewComment(todoInfo);
				Set<RepositoryTaskAttribute> changed = new HashSet<RepositoryTaskAttribute>();
				changed.add(taskData
						.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));
				TasksUiPlugin.getTaskDataManager().saveEdits(
						task.getRepositoryUrl(), task.getTaskId(), changed);

				TasksUiUtil.refreshAndOpenTaskListElement(task);
			}

			for (IMarker todo : todos) {
				TaskMylynPlugin.getDefault().addMarker(todo, task);
			}

			// Add todos to task context
			List<String> handles = TaskUtils.getHandles(todos);
			TagSEAMylynCorePlugin.addToContext(task, handles);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection sel = (StructuredSelection) selection;
			this.taskMarkers = TaskUtils.getTasks(sel);
		}

	}

}
