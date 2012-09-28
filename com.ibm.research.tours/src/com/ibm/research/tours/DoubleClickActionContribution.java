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

class DoubleClickActionContribution implements IDoubleClickActionContribution {

	private IDoubleClickActionDelegate fDelegate;
	private String fObjectContribution;
	
	public DoubleClickActionContribution(IDoubleClickActionDelegate delegate,String objectContribution) 
	{
		fDelegate = delegate;
		fObjectContribution = objectContribution;
	}
	
	public IDoubleClickActionDelegate getDoubleClickActionDelegate() 
	{	
		return fDelegate;
	}

	public String getObjectContribution() 
	{
		return fObjectContribution;
	}

}
