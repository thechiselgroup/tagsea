/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tours.content.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class FileAccessSession 
{
	private IFile fFile;
	private ISchedulingRule fRule;
	
	public FileAccessSession(IFile file)
	{
		fFile = file;
	}
	
	public IFile getFile()
	{
		return fFile;
	}
	
	public void begin()
	{
		fRule = createSchedulingRule();
		Platform.getJobManager().beginRule(fRule, null);
	}
	
	public void end()
	{
		Platform.getJobManager().endRule(fRule);
	}
	
	protected ISchedulingRule createSchedulingRule()
	{
		IResourceRuleFactory ruleFactory = 
			ResourcesPlugin.getWorkspace().getRuleFactory();

		ISchedulingRule rule =  ruleFactory.modifyRule(fFile);
		return rule;
	}
}
