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
package com.ibm.research.tours;


public interface ITourListener 
{
	public void elementsAdded(ITour tour, ITourElement[] elements);
	public void elementsRemoved(ITour tour,ITourElement[] elements);
	// TODO add tour properties keys so that specific changes can be singled out
	public void tourChanged(ITour tour);
}
