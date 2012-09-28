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
package ca.uvic.cs.tagsea.ui.views;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import ca.uvic.cs.tagsea.ITagseaImages;
import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.actions.GoToTagAction;
import ca.uvic.cs.tagsea.core.Route;
import ca.uvic.cs.tagsea.core.RouteCollection;
import ca.uvic.cs.tagsea.core.Waypoint;
import ca.uvic.cs.tagsea.editing.RoutesTreeItemListener;
import ca.uvic.cs.tagsea.editing.TreeItemWorker;
import ca.uvic.cs.tagsea.util.RouteNameValidator;

/**
 * The composite which contains the routes TreeViewer, header label and navigation composite.
 * 
 * @author Suzanne Thompson, Jie Zhang
 */
public class RoutesComposite extends BaseTagsViewComposite {
	private ToolBar toolBar;
	private ToolBarManager barManager;
	private ImageDescriptor imgDescriptor;	
	private TreeItemWorker treeWorker;
	private RoutesTreeItemListener routesTreeListener;
	private static Action backAction;
	private static Action forwardAction;
	private Action newRouteAction;
	private Action deleteAction;
	private Action removeStaleWaypointsAction;
	private TreeViewer routesTreeViewer;
	private final int WIDTH = 200;
	private final int UP = -1;
	private final int DOWN = 1;
	
	public RoutesComposite(Composite parent, int style) {
		super(parent, style);		
		
		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		this.setLayout(layout);
		
		GridData data = new GridData(SWT.END, SWT.FILL, false, true);
		data.widthHint = WIDTH;
		this.setLayoutData(data);
		
		Label header = new Label(this, SWT.LEFT);
		header.setFont(getHeaderLabelFont());
		//@tag bug(177) : use default font size
		//header.setForeground(getHeaderLabelColor());
		header.setText("Routes");
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		//@tag bug(177) : use default font size
		//data.heightHint = 24;
		header.setLayoutData(data);
		
		toolBar = new ToolBar(this, SWT.FLAT | SWT.LEFT);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.heightHint = 24;
		toolBar.setLayoutData(data);
		barManager = new ToolBarManager(toolBar);
		
		fillToolBar();		
		initTreeViewer();
		createContextMenu();
	}

	private void fillToolBar() {
		ITagseaImages images = TagSEAPlugin.getDefault().getTagseaImages();
		//backAction
		imgDescriptor = images.getDescriptor(ITagseaImages.IMG_UP_ARROW);		
		backAction = new Action("Previous waypoint", imgDescriptor) {
			public void run() {				
				goToWaypoint(UP);
			}
		};
		backAction.setToolTipText("Previous waypoint");
		imgDescriptor = images.getDescriptor(ITagseaImages.IMG_UP_ARROW_DISABLED);	
		backAction.setDisabledImageDescriptor(imgDescriptor);
		
		//forwardAction
		imgDescriptor = images.getDescriptor(ITagseaImages.IMG_DOWN_ARROW);
		forwardAction = new Action("Next waypoint", imgDescriptor) {
			public void run() {				
				goToWaypoint(DOWN);				
			}
		};
		forwardAction.setToolTipText("Next waypoint");
		imgDescriptor = images.getDescriptor(ITagseaImages.IMG_DOWN_ARROW_DISABLED);	
		forwardAction.setDisabledImageDescriptor(imgDescriptor);
		
		//newRouteAction
		final ImageDescriptor routeImgDescriptor = images.getDescriptor(ITagseaImages.IMG_ROUTE);		
		newRouteAction = new Action("&Add A New Route", routeImgDescriptor) {
			public void run() {				
				addNewRoute();
//				RouteXMLUtil.recordRoutes(Activator.getDefault().getRouteCollection());
			}			
		};
		newRouteAction.setToolTipText("Add a new route");
		ImageDescriptor routeImgDescriptorDis = images.getDescriptor(ITagseaImages.IMG_ROUTE_DISABLED);
		newRouteAction.setDisabledImageDescriptor(routeImgDescriptorDis);
		
		//delete Route/Waypoint Action
		ImageDescriptor deleteDescriptor = images.getDescriptor(ITagseaImages.IMG_TOOL_DELETE);
		ImageDescriptor deleteDescriptorDis = images.getDescriptor(ITagseaImages.IMG_TOOL_DELETE_DISABLED);
		deleteAction = new Action("&Delete Route/Waypoint", deleteDescriptor) {
			public void run() {		
				deleteRouteWaypoint();
				refreshRoutesViewer();
//				RouteXMLUtil.recordRoutes(Activator.getDefault().getRouteCollection());
			}			
		};
		deleteAction.setToolTipText("Delete selected waypoints/routes");
		deleteAction.setDisabledImageDescriptor(deleteDescriptorDis);
		
		//delete Route/Waypoint Action
		ImageDescriptor removeStaleWaypointsDescriptor = images.getDescriptor(ITagseaImages.IMG_CLEAR);
		
		removeStaleWaypointsAction = new Action("&Clear stale waypoints", removeStaleWaypointsDescriptor) 
		{
			public void run() 
			{	
				// Delete the stale waypoints
				TreeItem[] selections = routesTreeViewer.getTree().getSelection();
				
				for(TreeItem item : selections)
				{					
					if(item.getData() instanceof Route)
					{
						Route r = (Route)item.getData();
						
						Vector waypoints = r.getWaypoints();
						
						for(int i = waypoints.size() -1 ; i >= 0; i--)
						{
							Waypoint w = (Waypoint)waypoints.elementAt(i);
							
							if(w.isStale())
								waypoints.removeElementAt(i);
						}
					}
				}
				
				TagSEAPlugin.getDefault().getRouteCollection().updateView();
			}			
		};
		
		removeStaleWaypointsAction.setToolTipText("Remove stale waypoints");
	
		removeStaleWaypointsAction.setEnabled(false);
		deleteAction.setEnabled(false);
		backAction.setEnabled(false);
		forwardAction.setEnabled(false);
		
		barManager.add(newRouteAction);
		barManager.add(deleteAction);
		barManager.add(new Separator());
		barManager.add(removeStaleWaypointsAction);
		barManager.add(new Separator());
		barManager.add(backAction);
		barManager.add(forwardAction);
		
		barManager.update(false);
	}
	
	protected void addNewRoute() {
		RouteCollection routeCollection = TagSEAPlugin.getDefault().getRouteCollection();
        routeCollection = TagSEAPlugin.getDefault().getRouteCollection();
		RouteNameValidator validator = new RouteNameValidator(routeCollection);
		InputDialog dlg = new InputDialog(routesTreeViewer.getTree().getShell(), 
				"New Route Name", "Enter a name for the new route:", "", validator);
		if (dlg.open() == InputDialog.OK) {
			Route route = routeCollection.addRoute(dlg.getValue());						
			refreshRoutesViewer();
			routesTreeViewer.setSelection(new StructuredSelection(route), true);
			routesTreeViewer.getControl().setFocus();			
		}
		
	}
	
	@SuppressWarnings("restriction")
	protected void deleteRouteWaypoint() {        
		RouteCollection routes = TagSEAPlugin.getDefault().getRouteCollection();
		
		TreeItem[] selections = routesTreeViewer.getTree().getSelection();
		if(selections.length>0) {
			HashMap<Route, int[]> deleteMap = new HashMap<Route, int[]>();
			for (int i = 0; i < selections.length; i++) {
				Object item = selections[i].getData();
				if (item instanceof Route) {
					routes.removeRoute((Route)item);		
				}else if(item instanceof Waypoint){
					TreeItem routeItem = selections[i].getParentItem();
					int index = routeItem.indexOf(selections[i]);
					Route route = (Route)routeItem.getData();
					// for each route, save the indeces to delete
					int[] indeces;
					if (deleteMap.containsKey(route)) {
						int[] oldIndeces = (int[])deleteMap.get(route);
						indeces = new int[oldIndeces.length + 1];
						System.arraycopy(oldIndeces, 0, indeces, 1, oldIndeces.length);
						indeces[0] = index;
					} else {
						indeces = new int[] { index };
					}
					deleteMap.put(route, indeces);
				}
			}
			
			// now delete the waypoints for each route
			for (Route route : deleteMap.keySet()) {
				int[] indeces = (int[]) deleteMap.get(route);
				// sort the indeces to remove
				Arrays.sort(indeces);	
				// must do this in reverse order so that the index stays valid
				for (int i = indeces.length - 1; i >= 0; i--) 
				{
					route.removeWaypoint(indeces[i]);
					//System.out.println("Deleting " + indeces[i] + " ok? " + ok);
				}
			}
				
		}		
	}

	/**
	 * Initializes the tree viewer, setting the content and label providers.
	 */
	private void initTreeViewer() 
	{
		RouteCollection rc = TagSEAPlugin.getDefault().getRouteCollection();
		routesTreeViewer = new TreeViewer(this, SWT.MULTI| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		routesTreeViewer.setContentProvider(rc);
		routesTreeViewer.setLabelProvider(rc);	
		routesTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() 
		{
            public void selectionChanged(SelectionChangedEvent event) {
                RoutesComposite.this.selectionChanged(event);
            }
        });
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
		routesTreeViewer.getControl().setLayoutData(data);
		treeWorker = new TreeItemWorker(routesTreeViewer.getTree(), true);
		routesTreeListener = new RoutesTreeItemListener(routesTreeViewer);
		treeWorker.addListener(routesTreeListener);
		treeWorker.setRenameValidator(new RouteNameValidator(rc));
		
	    //new RoutesTreeDragAndDropManager(routesTreeViewer);
	}
	
	public TreeViewer getRoutesTreeViewer() {
		return routesTreeViewer;
	}
	
	public TreeItemWorker getRoutesTreeItemWorker() {
		return treeWorker;
	}
	
	public MenuManager createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				RoutesComposite.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(routesTreeViewer.getControl());
		routesTreeViewer.getControl().setMenu(menu);
		return menuMgr;
	}
	
	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(newRouteAction);
		manager.add(deleteAction);
		manager.add(new Separator());
		manager.add(removeStaleWaypointsAction);
		manager.add(new Separator());
		manager.add(backAction);
		manager.add(forwardAction);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
	}
	
	/**
	 * Adds a double click listener to the waypoints table viewer which will open an editor
	 * showing the double clicked waypoint.
	 * @param page the active page
	 */
	public void hookDoubleClickAction(IWorkbenchPage page) {
		routesTreeViewer.addDoubleClickListener(new GoToTagAction(routesTreeViewer, page));		
	}
	
	public void goToWaypoint(int direction) {
		Waypoint wp = null;
		TreeItem routeItem = null;
		int index = -1;
		int waypointsInRoute;
		
		//determine the index of previous/next waypoints in the route	
		TreeItem[] selections = routesTreeViewer.getTree().getSelection();
		if(selections.length>0){
			TreeItem selection = selections[0];
			if (selection.getData() instanceof Route) {
				routeItem = selection;
				waypointsInRoute = routeItem.getItemCount();
				if(waypointsInRoute>0){
					if(direction == -1){
						index = waypointsInRoute-1;		
					}else{
						index = 0;
					}
				}
			} else if (selection.getData() instanceof Waypoint) {
				routeItem = selection.getParentItem();				
				index = routeItem.indexOf(selection);
				waypointsInRoute = routeItem.getItemCount();
				if(direction == -1){
					index = (index+waypointsInRoute-1)%waypointsInRoute;		
				}else{
					index = (index+1)%waypointsInRoute;
				}
			}
		}		
		
		if(index!=-1){
			//get the waypoint in the route
			TreeItem wpItem = routeItem.getItem(index);
			wp = (Waypoint)wpItem.getData();
			
			//go to the tag in resource
			RoutesView routesView = TagSEAPlugin.getDefault().getRoutesView();
			
			IMarker marker = wp.getMarker();
	        if ((marker != null) && marker.exists()) {
		        IResource resource = marker.getResource();
		        if (marker.exists() && resource instanceof IFile) {
		            try {
		                IDE.openEditor(routesView.getSite().getPage(), marker, OpenStrategy.activateOnOpen());
		            } catch (PartInitException e) {
		            	TagSEAPlugin.log("Couldn't open editor to show the tag", e);
		            }
		        }
	    
	        }
	        
	        //highlight the waypoint in routeTreeViewer
	        //routesTreeViewer.setSelection(new StructuredSelection(wpItem.getData()), true);
	        routesTreeViewer.getTree().setSelection(wpItem);
			routesTreeViewer.getControl().setFocus();
		}        
	}
	
	/**
	 * Gets the expanded list of route ids from the view.
	 * @return An array of string's representing expanded tag ids.
	 */
	public String[] getExpandedRoutesList() {
		Object[] expandedElements = routesTreeViewer.getExpandedElements();
		String[] expandedIdList = new String[expandedElements.length];
		int i = 0;
		for ( Object o : expandedElements ) {
			expandedIdList[i++] = ((Route)o).getName();
		}		
		return expandedIdList;
	}
	
	/**
	 * Returns the selected Route.  If a waypoint is selected then its parent route is returned.
	 * If nothing is selected, then the first route is returned.  If no routes exist then null is returned.
	 * @return Route or null if none selected.
	 */
	public Route getSelectedRoute() {
		Route route = null;
		Tree tree = routesTreeViewer.getTree();
		TreeItem[] sel = tree.getSelection();
		if (sel.length > 0) {
			TreeItem item = sel[0];
			Object obj = item.getData();
			if ((obj instanceof Waypoint) && (item.getParentItem() != null)) {
				obj = item.getParentItem().getData();
			}
			if (obj instanceof Route) {
				route = (Route) obj;
			}
		} else if (tree.getItemCount() > 0) {
			// get the first item
			TreeItem first = tree.getItem(0);
			Object obj = first.getData();
			if (obj instanceof Route) {
				route = (Route) obj;
			}
		}
		return route;
	}
	
	/**
	 * Refreshes the routes tree viewer
	 */
	public void refreshRoutesViewer(){		
		refreshRoutesViewer(null);
	}
	
	/**
	 * Refreshes just the given element.
	 * @param element the Route or Waypoint to refresh
	 */
	public void refreshRoutesViewer(Object element) {
		// get expanded id's so we can maintain the view state			
		String[] expandedIdList = getExpandedRoutesList();
		
		if (element == null) {
			routesTreeViewer.refresh();
		} else {
			routesTreeViewer.refresh(element);
		}
		
		// reselect the tags that were selected prior to the refresh
		routesTreeViewer.setExpandedElements(TagSEAPlugin.getDefault().getRouteCollection().getRoutes(expandedIdList));		
	}
	
	//enable and disable the actions according to the selction
	protected void selectionChanged(SelectionChangedEvent event) {
		if(event.getSelection().isEmpty())
		{
			enableAllActions(false);
		}else{
			enableAllActions(true);
			IStructuredSelection selections = (IStructuredSelection)event.getSelection();
			
			if (selections.size()==1 && selections.getFirstElement()instanceof Route) 
			{
				Route route = (Route) selections.getFirstElement();
				
				if (route.getWaypoints().size()==0) 
				{
					backAction.setEnabled(false);
					forwardAction.setEnabled(false);
				}
			}			
		}	
		
		boolean routesSelected = false;
		
		TreeItem[] selections = routesTreeViewer.getTree().getSelection();
		
		for(TreeItem item : selections)
		{	
			if(item.getData() instanceof Route)
				routesSelected = true;
		}
		
		if(routesSelected)
			removeStaleWaypointsAction.setEnabled(true);
		else
			removeStaleWaypointsAction.setEnabled(false);
	}
	
	private void enableAllActions(boolean enable){
		deleteAction.setEnabled(enable);
		backAction.setEnabled(enable);
		forwardAction.setEnabled(enable);
	}

}

