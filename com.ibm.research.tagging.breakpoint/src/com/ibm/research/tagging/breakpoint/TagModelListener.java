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
package com.ibm.research.tagging.breakpoint;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.ITagModelListener;
import com.ibm.research.tagging.core.IWaypoint;

public class TagModelListener implements ITagModelListener {

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModelListener#tagAdded(com.ibm.research.tagging.core.ITag)
	 */
	public void tagAdded(ITag tag) 
	{

	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModelListener#tagRemoved(com.ibm.research.tagging.core.ITag, com.ibm.research.tagging.core.IWaypoint[])
	 */
	public void tagRemoved(ITag tag, IWaypoint[] waypoints) 
	{

	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModelListener#tagRenamed(com.ibm.research.tagging.core.ITag, java.lang.String)
	 */
	public void tagRenamed(ITag tag, String oldName) 
	{

	}

}
