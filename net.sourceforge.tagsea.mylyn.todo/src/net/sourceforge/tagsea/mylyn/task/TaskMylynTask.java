/*******************************************************************************
 * 
 *   Copyright 2007, CHISEL Group, University of Victoria, Victoria, BC, Canada. 
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *       The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.mylyn.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask;

import net.sourceforge.tagsea.mylyn.core.actions.MylynTaskGoto;

public class TaskMylynTask extends MylynTaskGoto {

	public TaskMylynTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Set<AbstractTask> getTasks(StructuredSelection selection) {
		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
		List<IMarker> markers = TaskUtils.getTasks(selection);
		for (IMarker task : markers) {
			Set<AbstractTask> t = TaskMylynPlugin.getDefault().getTasks(task);
			if(t != null){
				tasks.addAll(t);
			}
		}
		return tasks;
	}

}
