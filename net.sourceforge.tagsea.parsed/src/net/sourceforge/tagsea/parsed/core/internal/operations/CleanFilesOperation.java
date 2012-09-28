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
package net.sourceforge.tagsea.parsed.core.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;

/**
 * An operation for cleaning a list of files.
 * @author Del Myers
 *
 */
public class CleanFilesOperation extends AbstractWaypointUpdateOperation {

	private List<IFile> files;
	private MultiRule readRule;

	/**
	 * 
	 */
	public CleanFilesOperation(List<IFile> files) {
		super("Reparsing Files...");
		this.files = files;
		ISchedulingRule[] nested = new ISchedulingRule[files.size()];
		int i = 0;
		for (IFile file : files) {
			nested[i] = ResourcesPlugin.getWorkspace().getRuleFactory().modifyRule(file);
			i++;
		}
		this.readRule = new MultiRule(nested);
	}
	
	@Override
	public IStatus run(IProgressMonitor monitor)
			throws InvocationTargetException {
		MultiStatus status = new MultiStatus(ParsedWaypointPlugin.PLUGIN_ID, 0, "Parsing files...", null);
		monitor.beginTask("Parsing files...", files.size());
		for (IFile file : files) {
			IProgressMonitor subMonitor =
				new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
			status.merge(TagSEAPlugin.syncRun(new ParseFileOperation(file), subMonitor));
			if (monitor.isCanceled()) {
				return new Status(IStatus.WARNING, ParsedWaypointPlugin.PLUGIN_ID, "Process cancelled. Waypoints may be out-of-sync.");
			}
		}
		return status;
	}
	
	@Override
	public ISchedulingRule getRule() {
		return readRule;
	}

}
