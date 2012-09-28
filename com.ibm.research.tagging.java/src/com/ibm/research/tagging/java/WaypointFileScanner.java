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
package com.ibm.research.tagging.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import com.ibm.research.tagging.java.extractor.WaypointDefinitionExtractor;
import com.ibm.research.tagging.java.refactoring.DocumentReadSession;

public class WaypointFileScanner 
{
	private IFile fFile;
	private DocumentReadSession fDocumentReadSession;
	private IDocument fDocument;
	
	public WaypointFileScanner(IFile file)
	{
		fFile = file;
		fDocumentReadSession = new DocumentReadSession(fFile);
	}

	public DocumentReadSession getDocumentReadSession()
	{
		return fDocumentReadSession;
	}
	
	public IFile getFile() 
	{
		return fFile;
	}
	
	public IDocument getDocument() 
	{
		if(fDocument == null)
			fDocument = getDocumentReadSession().getDocument();
		
		return fDocument;
	}
	
	public IMarker[] scan()
	{
		getDocumentReadSession().begin();

		try
		{
			// Get the waypoint regions
			IRegion[] waypointRegions = WaypointDefinitionExtractor.getWaypointRegions(getDocument());
			List<IMarker> markers = new ArrayList<IMarker>();

			for(IRegion region : waypointRegions)
			{
				IMarker marker = WaypointMarkerFactory.createMarker(getFile(), getDocument(), region);
				
				if(marker != null)
					markers.add(marker);
			}
			
			return markers.toArray(new IMarker[0]);
		}
		finally
		{
			getDocumentReadSession().end();
		}
	}
}
