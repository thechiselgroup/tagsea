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

package com.ibm.research.tagging.java.markers;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.ibm.research.tagging.java.refactoring.DocumentReadSession;

public class WaypointMarkerValidator 
{
	private IFile fFile;

	public WaypointMarkerValidator(IFile file)
	{
		fFile = file;
	}

	public boolean isValid(IMarker marker)
	{
		DocumentReadSession session = new DocumentReadSession(fFile);
		session.begin();
		IDocument document = session.getDocument();

		int offset = MarkerUtilities.getCharStart(marker);
		int length = MarkerUtilities.getCharEnd(marker) - offset;

		try
		{
			if(offset != -1)
			{
				String waypointDefinition = null;

				try 
				{
					waypointDefinition = document.get(offset, length);
				} 
				catch (BadLocationException e) 
				{
					e.printStackTrace();
					return false;
				}

				String storedWaypointDefinition = MarkerUtilities.getMessage(marker);

				// The stored waypoint does not match the current waypoint
				if(!waypointDefinition.equals(storedWaypointDefinition))
				{
					System.out.println("Cached waypoint does not match actual");
					System.out.println("[");
					System.out.println(storedWaypointDefinition);
					System.out.println(waypointDefinition);
					System.out.println("]");
					
					return false;
				}
			}
		}
		finally
		{
			session.end();
		}

		return true;
	}

	public boolean isValid(List<IMarker> markers)
	{
		for(IMarker marker : markers)
			if(!isValid(marker))
				return false;

		return true;
	}

}
