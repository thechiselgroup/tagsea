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
import java.util.Vector;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.TreeItem;

import com.ibm.research.tours.IPaletteEntry;
import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourElementDropAdapter;
import com.ibm.research.tours.ITourElementDropAdapterExtension;
import com.ibm.research.tours.ToursPlugin;
import com.ibm.research.tours.editors.TourEditor;
import com.ibm.research.tours.editors.TourElements;

public class TourDragNDropAdapter extends ViewerDropAdapter implements DragSourceListener
{
	private ITourElementDropAdapter fAcceptingAdapter;
	private TreeItem fDropTarget;
	private boolean isTourElementTransfer;
	private boolean isPaletteTransfer;
	private static boolean externalMove;
	private TourEditor fEditor;
	private Vector<ITourElement> fElements;

	/**
	 * @param viewer
	 */
	public TourDragNDropAdapter(TourEditor editor) 
	{
		super(editor.getTourViewer());
		fEditor = editor;
	}

	@Override
	public boolean performDrop(Object data) 
	{			
		externalMove = false;

		// Get the current drop target
		Object currentTarget = getCurrentTarget();
		ITour tour = fEditor.getTour();
		int dropIndex = 0;

		// If the drop target is null then default to the last element 
		if(currentTarget == null)
		{
			ITourElement[] elements = fEditor.getTour().getElements();

			if(elements.length == 0)
				currentTarget = fEditor.getTourTreeAdapter().getTourElements();
			else
			{
				currentTarget = elements[fEditor.getTour().getElementCount() - 1];
				dropIndex = fEditor.getTour().getElementCount();
			}
		}

		if(!(currentTarget instanceof ITourElement) && !(currentTarget instanceof TourElements))
			return false;

		// We are droping onto an existing tour element or the root tour elements container
		if(currentTarget instanceof ITourElement)
		{
			if(fDropTarget != null)
			{
				TreeItem parentItem = fDropTarget.getParentItem();
				int location = getCurrentLocation();

				for(int i = 0 ; i < parentItem.getItemCount(); i++)
				{
					if(fDropTarget == parentItem.getItem(i))
					{
						if(location == LOCATION_BEFORE)
							dropIndex = i;
						else if(location == LOCATION_ON)
							dropIndex = i+1;
						else if(location == LOCATION_AFTER)
							dropIndex = i+1;

						break;
					}
				}
			}
		}

		// At this point we have the target tour and the drop index
		// Now handle the elements
		ITourElement[] elements = null;

		// This transfer is coming from the 
		if(isPaletteTransfer)
		{
			IPaletteEntry[] entries = (IPaletteEntry[])data;
			List<ITourElement> elementList = new ArrayList<ITourElement>();

			for(IPaletteEntry entry : entries)
			{
				ITourElement[] elementArray = entry.getProvider().createElements();

				if(elementArray != null)
				{
					for(ITourElement element : elementArray)
						elementList.add(element);
				}
			}

			elements = elementList.toArray(new ITourElement[0]);
		}
		else if(isTourElementTransfer)
			elements = (ITourElement[])data;
		else if(fAcceptingAdapter != null) {
			if (fAcceptingAdapter instanceof ITourElementDropAdapterExtension) {
				elements = ((ITourElementDropAdapterExtension)fAcceptingAdapter).convertDropData(data, tour);
			}
			else {
				elements = fAcceptingAdapter.convertDropData(data);
			}
		}	

		if(elements == null || (elements !=null && elements.length <= 0))
			return false;

		if(isTourElementTransfer)
		{
			if(getCurrentOperation() == DND.DROP_MOVE)				
			{
				// Check if this is an internal drop, if so the elements must be removed more carefully
				// For external drops removal is handled in the drag listener
				if(fEditor.getTour().contins(elements[0]))
				{
					int earliestDragIndex = fEditor.getTour().getIndex(elements[0]);

					if(earliestDragIndex < dropIndex)
						// Awww here it goes! We need to correct the drop index based on the 
						// removal of dragged elements
					{
						for(ITourElement e : elements)
						{
							int index = fEditor.getTour().getIndex(e);

							if(index < dropIndex)
								dropIndex --;
						}	
					}
					fEditor.getTour().removeElements(elements);
				}
				else
					externalMove = true;
			}
			else if(getCurrentOperation() == DND.DROP_COPY)
			{
				Vector<ITourElement> clones = new Vector<ITourElement>();

				// For copies we replace the dragged elements with clones
				for(ITourElement e : elements)
				{
					clones.add(e.createClone());
				}

				elements = clones.toArray(new ITourElement[0]);
			}
		}

		tour.addElements(dropIndex,elements);

		TreeViewer viewer = (TreeViewer)getViewer();
		viewer.setExpandedState(fEditor.getTourTreeAdapter().getTourElements(), true);
		viewer.setSelection(new StructuredSelection(elements));

		fAcceptingAdapter = null;
		isTourElementTransfer = false;
		isPaletteTransfer = false;
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) 
	{
		if((!(target instanceof ITourElement) && !(target instanceof TourElements)))
			// We can drag onto null
			if(target != null)
				return false;

		// The adapter is set in the drag enter event handler
		// When the adapter is set we are dragging from an external source
		if(fAcceptingAdapter == null)
		{
			if(TourElementTransfer.getInstance().isSupportedType(transferType))
			{
				isTourElementTransfer = true;
				fAcceptingAdapter = null;
				isPaletteTransfer = false;
				return true;
			}
			else if(PaletteEntryTransfer.getInstance().isSupportedType(transferType))
			{
				isPaletteTransfer = true;
				isTourElementTransfer = false;
				fAcceptingAdapter = null;
				return true;
			}
		}
		else
		{
			isTourElementTransfer = false;
			isPaletteTransfer = false;
			return true;
		}

		isTourElementTransfer = false;
		isPaletteTransfer = false;
		fAcceptingAdapter = null;
		return false;
	}

	@Override
	protected Object determineTarget(DropTargetEvent event) 
	{
		fDropTarget = (TreeItem)event.item;
		return super.determineTarget(event);
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) 
	{
		if(fAcceptingAdapter!=null)
		{
			if(event.detail == DND.DROP_MOVE)
				event.detail = DND.DROP_COPY;
		}

		super.dragOperationChanged(event);
	}

	@Override
	public void dragEnter(DropTargetEvent event) 
	{
		fAcceptingAdapter = null;
		
		for(ITourElementDropAdapter adapter : ToursPlugin.getDefault().getDropAdapters())
			if(adapter.getTransfer().isSupportedType(event.currentDataType) == true)
			{
				fAcceptingAdapter = adapter;
				break;
			}

		// For adapter drops we only allow copy, if we allow move then the dragger may delete the dragged items
		// after the drop
		if(fAcceptingAdapter!=null)
		{
			if(event.detail == DND.DROP_MOVE)
				event.detail = DND.DROP_COPY;
		}

		super.dragEnter(event);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event) 
	{
		TreeItem[] selection = fEditor.getTourViewer().getTree().getSelection();

		if (selection.length > 0) 
		{
			fElements = new Vector<ITourElement>();

			for(TreeItem item : selection)
			{
				Object data = item.getData(); 

				if(data instanceof ITourElement)
					fElements.add((ITourElement)data);
			}

			if(fElements.size() == 0)
				event.doit = false;

			return;
		} 

		event.doit = false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event)
	{
		if (fElements != null) 
			event.data = fElements.toArray(new ITourElement[0]);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragFinished(DragSourceEvent event) 
	{
		// For external move operations wir mussen deleten der dragged elements
		if(fElements != null && fElements.size() > 0)
			if(externalMove)
				if(event.detail == DND.DROP_MOVE)				
					fEditor.getTour().removeElements(fElements.toArray(new ITourElement[0]));

		externalMove = false;
		fElements = null;
	}
}
