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
package com.ibm.research.tagging.java.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

public class MarkerUpdatingDocumentWriteSession extends DocumentWriteSession
{
	private ResourceMarkerAnnotationModel fModel;

	public MarkerUpdatingDocumentWriteSession(IFile file) 
	{
		super(file);
		fModel = new ResourceMarkerAnnotationModel(file);
	}
	
	@Override
	public void begin() 
	{
		super.begin();
		fModel.connect(getDocument());
	}
	
	@Override
	public void end()
	{
		try 
		{
			fModel.commit(getDocument());
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
		
		fModel.disconnect(getDocument());
		super.end();
	}
}
