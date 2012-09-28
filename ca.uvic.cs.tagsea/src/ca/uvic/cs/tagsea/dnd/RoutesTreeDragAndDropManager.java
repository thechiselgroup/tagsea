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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.TreeItem;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Route;
import ca.uvic.cs.tagsea.core.Waypoint;

public class RoutesTreeDragAndDropManager extends ViewerDropAdapter implements DragSourceListener 
{
	private TreeItem fItem;
	private Vector<Waypoint> fWayPoints = new Vector<Waypoint>();
	private TreeViewer fViewer;
	private TreeItem[] fSelection;
	
	public RoutesTreeDragAndDropManager(TreeViewer viewer)
	{
		super(viewer);
		fViewer = viewer;
	    int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transfers = new Transfer[] { WaypointTransfer.getInstance()};
		viewer.addDropSupport(ops, transfers, this);
		viewer.addDragSupport(ops, transfers, this);
	}
	
	public void dragStart(DragSourceEvent event) 
	{
		fSelection = fViewer.getTree().getSelection();
		fWayPoints = new Vector<Waypoint>();

		if (fSelection.length > 0) 
		{
			for(TreeItem item : fSelection)
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
		
		if(fWayPoints.size() == 0)
			event.doit = false;
	}

	public void dragSetData(DragSourceEvent event)
	{
		if (fWayPoints.size() > 0) 
		{
			event.data = fWayPoints.toArray();
		}
	}

	public void dragFinished(DragSourceEvent event) 
	{	
		fSelection = null;
		fWayPoints = null;
	}
	
	@Override
	public boolean performDrop(Object data) 
	{
	    Object target = getCurrentTarget();
	     
	     if (target == null)
	        target = getViewer().getInput();
	     
	     String[] toDrop = (String[])data;
	     Vector<Waypoint> waypoints = new Vector<Waypoint>();
	     
	     for(String wpid : toDrop)
	     {
	    	 Waypoint w = TagSEAPlugin.getDefault().getTagCollection().getWaypointCollection().getWaypoint(wpid);
	    	 
	    	 if(w != null)
	    		 waypoints.add(w); 
	     }
	     
	     TreeViewer viewer = (TreeViewer)getViewer();
	     Route route = null;
	     int index = 0;
	     
	     if(target instanceof Route)
	     {
	    	 index = 0;
	    	 route = (Route)target;
	     }
	     else if(target instanceof Waypoint)
	     {
	    	 route = (Route)fItem.getParentItem().getData();

	    	 for(TreeItem item : fItem.getParentItem().getItems())
	    	 {
	    		 if(item == fItem)
	    			 break;

	    		 index ++;
	    	 }
	    	 
			 int location = getCurrentLocation();
			 
			 if(location != ViewerDropAdapter.LOCATION_BEFORE)
					 index += 1;
	     }
	     else
	    	 return false; 
	     
	     if(fSelection!=null)
	     {
	    	 // we have an internal dnd
	    	 // we will handle this in the drop operation as a special case
	    	 if(getCurrentOperation() == DND.DROP_MOVE)
	    	 {
			     for(int i = waypoints.size() -1; i >= 0; i--)
			    	 route.getWaypoints().add(index,waypoints.get(i));   
	    	 }
	     }
	     else
	     {
		     for(int i = waypoints.size() -1; i >= 0; i--)
		    	 route.getWaypoints().add(index,waypoints.get(i));
		    
	     }
    	 
	     viewer.reveal(fItem);
	     viewer.refresh();
    	 fItem = null;
	     return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) 
	{
		return WaypointTransfer.getInstance().isSupportedType(transferType);
	}
	
	@Override
	public void drop(DropTargetEvent event) 
	{
		// This is the drop target, i.e. the item under the cursor when the drop is executed
		fItem = (TreeItem) event.item;
		super.drop(event);
	}
}
