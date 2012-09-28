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
package net.sourceforge.tagsea.mylyn.core.actions;

import java.util.List;

import net.sourceforge.tagsea.mylyn.core.TagSEAMylynCorePlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public abstract class MylynTask implements IViewActionDelegate {

	@Override
	public void init(IViewPart view) {
		// No initialization needed (yet)
	}

	protected StructuredSelection selection;

	protected abstract AbstractTask createTask(String taskInfo);

	protected abstract String getTaskInfo();

	protected abstract void recordTask(AbstractTask task);

	protected abstract void gatherData(StructuredSelection selection);

	protected abstract List<String> getHandles();
	
	protected abstract void postOps(AbstractTask task);
	
	@Override
	public void run(IAction action) {
		gatherData(this.selection);
		String taskInfo = getTaskInfo();
		AbstractTask task = createTask(taskInfo);
		TagSEAMylynCorePlugin.addToContext(task, getHandles());
		recordTask(task);
		postOps(task);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			this.selection = (StructuredSelection) selection;
		}
	}

	

	

}
