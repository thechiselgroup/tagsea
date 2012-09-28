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

import java.util.Date;

import org.eclipse.core.resources.IFile;

public interface ITour 
{
	public String getTitle();
	public void setTitle(String name);
	
	public String getDescription();
	public void setDescription(String description);
	
	public ITimeLimit getTimeLimit();
	public void setTimeLimit(ITimeLimit limit);
	
	public String getAuthor();
	public void setAuthor(String author);
	
	public Date getDate();
	public void setDate(Date date);
	
	public void addElements(int index,ITourElement[] element);
	public void addElements(ITourElement[] element);
	
	public void removeElements(ITourElement[] element);
	public ITourElement removeElement(int index);
	
	public ITourElement getElement(int index);
	public ITourElement[] getElements();
	public int getElementCount();
	
	public boolean contins(ITourElement element);
	public void clear();
	
	public int getIndex(ITourElement element);
	public ITourElement setElement(int index,ITourElement element);
	
	public void addTourListener(ITourListener listener, boolean getCatchUpEvent);
	public void removeTourListener(ITourListener listener);
	
	public void write(IFile file) throws Exception;
	public void read(IFile file) throws Exception;
	public IFile getFile();
	public String getID();
}
