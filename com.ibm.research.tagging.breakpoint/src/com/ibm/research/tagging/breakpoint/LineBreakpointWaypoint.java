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

import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.ILineBreakpoint;

public class LineBreakpointWaypoint extends BreakpointWaypoint
{
	public LineBreakpointWaypoint(ILineBreakpoint breakpoint, String description, String author, Date date) 
	{
		super(breakpoint);
	}
	
	public LineBreakpointWaypoint(ILineBreakpoint breakpoint) 
	{
		super(breakpoint);
	}

	public int getLine() throws CoreException
	{
		return ((ILineBreakpoint)getBreakpoint()).getLineNumber();
	}
	
	public int getCharStart() throws CoreException
	{
		return ((ILineBreakpoint)getBreakpoint()).getCharStart();
	}
	
	public int getCharEnd() throws CoreException
	{
		return ((ILineBreakpoint)getBreakpoint()).getCharEnd();
	}
}
