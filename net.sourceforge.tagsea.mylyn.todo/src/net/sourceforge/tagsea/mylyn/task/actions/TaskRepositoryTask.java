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

import net.sourceforge.tagsea.mylyn.core.actions.MylynRepositoryTask;
import net.sourceforge.tagsea.mylyn.task.TaskUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask;

public class TaskRepositoryTask extends MylynRepositoryTask {

private List<IMarker> todos;
	
	@Override
	protected String getTaskInfo() {
		return TaskUtils.getTaskInfo(this.todos);
	}

	@Override
	protected void gatherData(StructuredSelection selection) {
		this.todos = TaskUtils.getTasks(selection);
	}

	@Override
	protected void recordTask(AbstractTask task) {
		TaskUtils.recordTask(this.todos, task);
	}

	@Override
	protected List<String> getHandles() {
		return TaskUtils.getHandles(todos);
	}

	@Override
	protected void postOps(AbstractTask task) {
		// TODO Auto-generated method stub
		
	}

	


}
