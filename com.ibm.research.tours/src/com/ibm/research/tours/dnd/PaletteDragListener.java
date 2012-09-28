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
package com.ibm.research.tours.dnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.ibm.research.tours.IPaletteEntry;

public class PaletteDragListener implements DragSourceListener {

	private List<IPaletteEntry> fEntries;
	private TableViewer fViewer;
	
	public PaletteDragListener(TableViewer viewer)
	{
		fViewer = viewer;
	}
	
	public void dragFinished(DragSourceEvent event) 
	{
		fEntries = null;
	}

	public void dragSetData(DragSourceEvent event) 
	{
		if (fEntries != null) 
			event.data = fEntries.toArray(new IPaletteEntry[0]);
	}

	public void dragStart(DragSourceEvent event) 
	{
		fEntries = new ArrayList<IPaletteEntry>();
		
		IStructuredSelection selection = (IStructuredSelection)fViewer.getSelection();

		if (!selection.isEmpty()) 
		{
			for(Object o : selection.toArray())
				if(o instanceof IPaletteEntry)
					fEntries.add((IPaletteEntry)o);
		} 
		else 
		{
			event.doit = false;
		}
	}
}
