/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.editing;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Route;
import ca.uvic.cs.tagsea.core.RouteCollection;
import ca.uvic.cs.tagsea.core.Waypoint;
import ca.uvic.cs.tagsea.editing.events.ItemDragEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemCopyEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemDeleteEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemListener;
import ca.uvic.cs.tagsea.editing.events.TreeItemMoveEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemRenameEvent;

/**
 * Listener for delete, move, and rename events on the routes TreeViewer.
 * 
 * @@author Chris Callendar
 */
public class RoutesTreeItemListener implements TreeItemListener {

	private TreeViewer routesTreeViewer;

	public RoutesTreeItemListener(TreeViewer routesTreeViewer) {
		this.routesTreeViewer = routesTreeViewer;
	}

	/** Only allow moving waypoints (not routes). */
	public void beforeMove(TreeItemMoveEvent event) {
		event.doit = (event.data instanceof Waypoint);
		if (event.doit) {
			Route route = (Route) event.item.getParentItem().getData();
			route.removeWaypoint(event.index);
		}
	}

	/**
	 * Moves a waypoint
	 */
	public void afterMove(TreeItemMoveEvent event) {
		if (event.data instanceof Waypoint) {
			Route route = (Route) event.parent.getData();
			Waypoint waypoint = (Waypoint) event.data;
			route.addWaypoint(event.index, waypoint);			
			// if (tooltip != null) {
			// tooltip.setToolTip(event.item, item.getTag().getToolTip());
			// }			
		}
	}

	/** Only allow renaming routes (not waypoints). */
	public void beforeRename(TreeItemRenameEvent event) {
		event.doit = event.data instanceof Route;
	}

	public void afterRename(TreeItemRenameEvent event) {
		if (event.data instanceof Route) {
			Route route = (Route) event.data;
			route.setName(event.newName);
		}
	}

	public void beforeDelete(TreeItemDeleteEvent event) {
		if (event.data instanceof Route) {
			Route route = (Route) event.data;
			// prompt the user if the route has waypoints
			if (route.getWaypoints().size() > 0) {
				String msg = "Are you sure you want to delete "
						+ route.getName() + "?";
				event.doit = MessageDialog.openQuestion(event.tree.getShell(),
						"Delete?", msg);
			}
		}
	}

	public void afterDelete(TreeItemDeleteEvent event) {
		RouteCollection routes = TagSEAPlugin.getDefault().getRouteCollection();
		if (event.data instanceof Route) {
			Route route = (Route) event.data;
			routes.removeRoute(route);
		} else if (event.data instanceof Waypoint) {
			int index = event.index;
			TreeItem parent = event.parent;
			Route parentItem = (Route) parent.getData();
			parentItem.removeWaypoint(index);
		}		
	}

	public void dragStart(ItemDragEvent event) {
		// can only drag RouteItems (in the Routes TreeViewer) or Tags
		// (Waypoints TableViewer)
		for (Item item : event.source) {
			Object data = item.getData();
			event.doit = data instanceof Waypoint;
			if (event.doit == false) {
				break;
			}
		}
	}

	public void dragOver(ItemDragEvent event) {
		// can only drop onto a Route
		event.doit = ((event.target != null) && (event.target.getData() instanceof Route));
	}

	public void drop(ItemDragEvent event) {
	}

	public void afterCopy(TreeItemCopyEvent event) {
		TreeItem item = event.item;
		TreeItem parent = item.getParentItem();
		Object data = event.data;
		if ((data instanceof Waypoint) && (parent != null)
				&& (parent.getData() instanceof Route)) {
			Waypoint waypoint = (Waypoint) data;
			Route route = (Route) parent.getData();
			route.addWaypoint(event.index, waypoint);
			item.setData(waypoint);

			// if ((item.getImage() != null) && !item.getImage().isDisposed()) {
			// item.getImage().dispose();
			// }
			// Image newImage = labelProvider.getImage(waypoint);
			// item.setImage(newImage);

			// @tag Routes(TreeItemTooltip)
			// if (tooltip != null) {
			// tooltip.setToolTip(item, (tag).getToolTip());
			// }
		}
	}
	
	public void finishedDelete() {
		refreshViewer(null);
	}
	
	public void finishedMove(Item[] movedItems) {
		if (movedItems == null) {
			movedItems = new Item[0];
		}
		Object[] data = new Object[movedItems.length];
		for (int i = 0; i < movedItems.length; i++) 
		{	if(!movedItems[i].isDisposed())
				data[i] = movedItems[i].getData();
		}
		refreshViewer(data);
	}

	/**
	 * Refreshes the tree viewer.
	 * 
	 * @@param selection
	 *            the selection object - can be null
	 */
	private void refreshViewer(Object selection) 
	{		
		//String[] expandedIdList = routesComposite.getExpandedRoutesList();		
		
		//@tag jie(buggy): need to find a better way
		// try saving all expanded routes
		Object[] expanded = routesTreeViewer.getExpandedElements();
		
		// refresh the view (recreates the TreeItems)
		routesTreeViewer.refresh();

		// now expand the previously expanded routes
		routesTreeViewer.setExpandedElements(expanded);
		
		//routesTreeViewer.setExpandedElements(Activator.getDefault().getRouteCollection().getRoutes(expandedIdList));
		StructuredSelection sel = new StructuredSelection();
		if (selection != null) {
			sel = new StructuredSelection(selection);
		}
		routesTreeViewer.setSelection(sel, true);
		
//		RouteXMLUtil.recordRoutes(Activator.getDefault().getRouteCollection());
		
	}

	public void copyData(TreeItemCopyEvent event) {
		// @tag addkeyword
	}
}