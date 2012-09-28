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
package com.ibm.research.tours.runtime;

import com.ibm.research.tours.ITour;

public interface ITourRuntime 
{
	/**
	 * Run the given tour in the tour runtime system
	 * @param tour
	 */
	public void run(ITour tour);
	public void moveAbove();
	
	public void addTourRuntimeListener(ITourRuntimeListener listener);
	public void removeTourRuntimeListener(ITourRuntimeListener listener);
}
