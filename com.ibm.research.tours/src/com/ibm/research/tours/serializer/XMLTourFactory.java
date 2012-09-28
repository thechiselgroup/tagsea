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
package com.ibm.research.tours.serializer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import com.ibm.research.tours.ITour;

public class XMLTourFactory {

	/**
	 * Creates a tour from an XML file
	 * @param tourAuthor 
	 * @param tourDescription 
	 * @param file 
	 * @return
	 * @throws Exception 
	 */
	public static ITour createTour(String title, String description, String author) {
		
		XMLTour tour = new XMLTour(title, description, author);
		return tour;
	}
	
	/**
	 * Creates a tour from an XML file
	 * @param file 
	 * @return
	 * @throws Exception 
	 */
	public static ITour createTour(IFile file) throws CoreException	{
		XMLTour tour = new XMLTour("","","");
		tour.read(file);
		return tour;
	}
	
}
