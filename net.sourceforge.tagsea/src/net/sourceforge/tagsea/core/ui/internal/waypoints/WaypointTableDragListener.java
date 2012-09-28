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
package net.sourceforge.tagsea.core.ui.internal.waypoints;

import java.util.Vector;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.TableItem;


/**
 * 
 * @author mdesmond
 *
 */
public class WaypointTableDragListener implements DragSourceListener 
{
	private Vector<IWaypoint> fWayPoints = new Vector<IWaypoint>();
	private TableViewer fViewer;
	
	public WaypointTableDragListener(TableViewer viewer)
	{
		fViewer = viewer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event) 
	{
		TableItem[] selection = fViewer.getTable().getSelection();
		fWayPoints.clear();
		if (selection.length > 0) 
		{
			for(TableItem item : selection)
			{
				Object data = item.getData(); 
				
				if(data instanceof IWaypoint)
					fWayPoints.add((IWaypoint)data);
				else if (data instanceof IAdaptable) {
					IWaypoint wp =
						(IWaypoint)((IAdaptable)data).getAdapter(IWaypoint.class);
					if (wp != null) {
						fWayPoints.add(wp);
					}
				}
			}
			event.doit = true;
		} 
		else 
		{
			event.doit = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event)
	{
		if (fWayPoints.size() > 0) 
			event.data = fWayPoints.toArray(new IWaypoint[fWayPoints.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragFinished(DragSourceEvent event) 
	{
		fWayPoints = new Vector<IWaypoint>();
	}
}
