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
package com.ibm.research.tagging.core.ui;

import org.eclipse.jface.viewers.IStructuredSelection;

import com.ibm.research.tagging.core.ITag;

/**
 * 
 * @author mdesmond
 *
 */
public interface ITagViewListener 
{
	public void tagCreated(ITag tag);
	public void tagsDeleted(ITag[] tag);
	public void tagRenamed(ITag[] tag);
	public void selectionChanged(IStructuredSelection selection);
}
