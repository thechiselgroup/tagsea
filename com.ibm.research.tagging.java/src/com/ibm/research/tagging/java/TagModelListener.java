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
package com.ibm.research.tagging.java;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.ITagModelListener;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.java.jobs.TagRemoveJob;
import com.ibm.research.tagging.java.jobs.TagRenameJob;

public class TagModelListener implements ITagModelListener
{
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagCollectionListener#tagAdded(com.ibm.research.tagging.core.ITag)
	 */
	public void tagAdded(ITag tag)
	{
		// not intrested
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModelListener#tagRemoved(com.ibm.research.tagging.core.ITag, com.ibm.research.tagging.core.IWaypoint[])
	 */
	public void tagRemoved(final ITag tag, final IWaypoint[] waypoints)
	{
		TagRemoveJob job = new TagRemoveJob(tag,waypoints);
		job.setSystem(true);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModelListener#tagRenamed(com.ibm.research.tagging.core.ITag, java.lang.String)
	 */
	public void tagRenamed(ITag tag, String oldName)
	{
		TagRenameJob job = new TagRenameJob(tag,oldName);
		job.setSystem(true);
		job.schedule();
	}
}
