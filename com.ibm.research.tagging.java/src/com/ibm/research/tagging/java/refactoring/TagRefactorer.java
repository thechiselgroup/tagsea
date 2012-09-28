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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.ibm.research.tagging.java.JavaWaypoint;
import com.ibm.research.tagging.java.ModifiedFileSynchronizer;
import com.ibm.research.tagging.java.util.SortingUtilities;

public abstract class TagRefactorer extends AbstractRefactorer
{
	private List<JavaWaypoint> fWaypoints;
	
	public TagRefactorer(IFile file, List<JavaWaypoint> waypoints)
	{
		super(file);
		fWaypoints = waypoints;
	}
	
	@Override
	public void performRefactoring() 
	{
		SortingUtilities.sortWaypointsByDescendingOffset(getWaypoints());

		for(JavaWaypoint waypoint : getWaypoints())
		{					
			IMarker marker = waypoint.getMarker();

			int offset = MarkerUtilities.getCharStart(marker);
			int length = MarkerUtilities.getCharEnd(marker) - offset;

			// Snip off the keyword
			offset = offset + "@tag".length();
			length = length - "@tag".length();

			String waypointDefinition = null;

			try 
			{
				waypointDefinition = getSession().getDocument().get(offset, length);
			} 
			catch (BadLocationException e) 
			{
				e.printStackTrace();
				continue;
			}

			String tagDefintion = null;

			int tagDefintionLength = waypointDefinition.indexOf(":");

			if(tagDefintionLength == -1)
			{
				// keep the newline
				tagDefintionLength = length - 1;
				tagDefintion = waypointDefinition;
			}
			else
			{
				tagDefintion = waypointDefinition.substring(0, tagDefintionLength);
			}

			String newTagDefinition = refactorTag(tagDefintion);
			
			if(newTagDefinition == null)
				return;
			
			final int _offset = offset;
			final int _tagDefinitionLength = tagDefintionLength;
			final String _newTagDefinition = newTagDefinition;

			Display.getDefault().syncExec(new Runnable() {

				public void run()
				{
					try 
					{
						getSession().getDocument().replace(_offset, _tagDefinitionLength, _newTagDefinition);
					}
					catch (BadLocationException e) 
					{
						e.printStackTrace();
					}
				}
			});
		}

		ModifiedFileSynchronizer synchronizer = new ModifiedFileSynchronizer();
		synchronizer.synchronizeWithModel(getFile(), getSession().getDocument());
	}

	public abstract String refactorTag(String tagDefintion);
	

	public List<JavaWaypoint> getWaypoints() 
	{
		return fWaypoints;
	}
}
