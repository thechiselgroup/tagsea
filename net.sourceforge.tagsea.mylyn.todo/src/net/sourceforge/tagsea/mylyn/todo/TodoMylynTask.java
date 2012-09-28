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
package net.sourceforge.tagsea.mylyn.todo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask;

import net.sourceforge.tagsea.mylyn.core.actions.MylynTaskGoto;

public class TodoMylynTask extends MylynTaskGoto {

	public TodoMylynTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List<AbstractTask> getTasks(StructuredSelection selection) {
		List<AbstractTask> tasks = new ArrayList<AbstractTask>();
		List<IMarker> todos = TodoUtils.getTodos(selection);
		for (IMarker todo : todos) {
			List<AbstractTask> t = TodoMylynPlugin.getDefault().getTasks(todo);
			if(t != null){
				tasks.addAll(t);
			}
		}
		return tasks;
	}

}
