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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class WaypointMarkerFactory {

	public static IMarker createMarker(IFile file, IDocument document, IRegion region)
	{
		String waypointDefinition;
		
		try 
		{
			waypointDefinition = document.get(region.getOffset(),region.getLength());
		} 
		catch (BadLocationException e) 
		{
			e.printStackTrace();
			return null;
		}

		IMarker marker;
		try 
		{
			marker = file.createMarker(JavaWaypoint.MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, waypointDefinition);
			marker.setAttribute(IMarker.CHAR_START, region.getOffset());
			marker.setAttribute(IMarker.CHAR_END, region.getOffset() + region.getLength());
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
			return null;
		}
		
		return marker;
	}
}
