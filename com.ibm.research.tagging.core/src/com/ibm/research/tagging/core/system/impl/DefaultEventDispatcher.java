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
package com.ibm.research.tagging.core.system.impl;

import com.ibm.research.tagging.core.system.IEventDispatcher;

/**
 * 
 * @author mdesmond
 *
 */
public class DefaultEventDispatcher implements IEventDispatcher
{
	public void dispatch(Runnable r)
	{
		r.run();
		//Display.getDefault().syncExec(r);
	}
}
