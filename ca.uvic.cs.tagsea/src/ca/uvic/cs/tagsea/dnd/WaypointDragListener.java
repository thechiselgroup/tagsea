/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation
 *******************************************************************************/
package ca.uvic.cs.tagsea.dnd;

import java.util.Vector;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.TableItem;

import ca.uvic.cs.tagsea.core.Waypoint;

public class WaypointDragListener implements DragSourceListener 
{
	private Vector<Waypoint> fWayPoints = new Vector<Waypoint>();
	private TableViewer fViewer;
	
	public WaypointDragListener(TableViewer viewer)
	{
		fViewer = viewer;
	}
	
	public void dragFinished(DragSourceEvent event) 
	{
		fWayPoints = new Vector<Waypoint>();
	}

	public void dragSetData(DragSourceEvent event)
	{
		if (fWayPoints.size() > 0) 
		{
			event.data = fWayPoints.toArray();
		}
	}

	public void dragStart(DragSourceEvent event) 
	{
		TableItem[] selection = fViewer.getTable().getSelection();

		if (selection.length > 0) 
		{
			for(TableItem item : selection)
			{
				Object data = item.getData(); 
				
				if(data instanceof Waypoint)
					fWayPoints.add((Waypoint)data);
			}
		} 
		else 
		{
			event.doit = false;
		}
	}
}
