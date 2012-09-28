/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/

package net.sourceforge.tagsea.java.documents.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;

public class FileAccessSession 
{
	private IFile fFile;
	private ISchedulingRule fRule;
	private boolean started = false;
	
	public FileAccessSession(IFile file)
	{
		fFile = file;
	}
	
	public IFile getFile()
	{
		return fFile;
	}
	
	public void begin() throws IllegalStateException
	{
		if (started) return;
		fRule = createSchedulingRule();
		//@tag tagsea.bug.78.fix : check for workbench close. Throw an exception.
		if (PlatformUI.getWorkbench().isClosing()) 
			throw new IllegalStateException("Workbench is closing");
		Job.getJobManager().beginRule(fRule, null);
		started = true;
	}
	
	public void end()
	{
		if (!started) return;
		Job.getJobManager().endRule(fRule);
		started = false;
	}
	
	protected ISchedulingRule createSchedulingRule()
	{
		IResourceRuleFactory ruleFactory = 
			ResourcesPlugin.getWorkspace().getRuleFactory();

		ISchedulingRule rule =  ruleFactory.modifyRule(fFile);
		return rule;
	}
}
