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
 *  *******************************************************************************/

package net.sourceforge.tagsea.mylyn.core.actions;


import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

public abstract class MylynLocalTask extends MylynTask {

	@Override
	protected AbstractTask createTask(String taskInfo) {
		AbstractTask task = TasksUiPlugin.getTaskListManager()
				.createNewLocalTask(null);
		if (task != null) {
			task.setNotes(taskInfo);
			TasksUiUtil.openEditor(task, true);
		}
		return task;
	}
}
