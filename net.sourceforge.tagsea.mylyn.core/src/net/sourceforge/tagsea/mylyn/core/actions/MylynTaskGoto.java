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
package net.sourceforge.tagsea.mylyn.core.actions;

import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public abstract class MylynTaskGoto implements IViewActionDelegate {

	private StructuredSelection selection;

	@Override
	public void run(IAction action) {
		Set<AbstractTask> tasks = getTasks(this.selection);
		
		for (AbstractTask task : tasks) {
			TasksUiUtil.openEditor(task, true);
		}
	}

	protected abstract Set<AbstractTask> getTasks(StructuredSelection selection);
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
		 this.selection = (StructuredSelection)selection;	
		}
	}

	@Override
	public void init(IViewPart view) {
		// Not used
	}

}
