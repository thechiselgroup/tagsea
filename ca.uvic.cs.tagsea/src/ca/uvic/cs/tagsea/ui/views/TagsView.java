/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *     IBM Corporation
 *******************************************************************************/
package ca.uvic.cs.tagsea.ui.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.ViewPart;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.actions.SendTagsAction;
import ca.uvic.cs.tagsea.core.ITagCollectionListener;
import ca.uvic.cs.tagsea.preferences.TagSEAPreferences;

/**
 * This is the overall view part for TagSEA.  It shows the tags tree viewer,
 * the waypoints table viewer, and the routes tree viewer.   All three are inside 
 * of split panes.
 * 
 * @author Chris Callendar, Chris Bennett, mdesmond
 */

public class TagsView extends ViewPart implements IPerspectiveListener, IPerspectiveListener2
{

	public static final String ID = "ca.uvic.cs.tagsea.views.TagsView";
	
    /** default help email address */
    private static final String HELP_EMAIL = "chisel-support@cs.uvic.ca, tagsea-user-help@lists.sourceforge.net";
	
	private TagsComposite tagsComposite;
	private WaypointsComposite waypointsComposite;
	private SashForm leftSash;
	private SashForm rightSash;
	private Action emailAction;
	private Action sendTagsAction;
	private IPartListener partListener;
	private ITagCollectionListener tagListener;
	
	private class ViewSelectionProvider implements ISelectionProvider {
		private List<ISelectionChangedListener> listeners;
		private ISelection selection;
		/**
		 * 
		 */
		public ViewSelectionProvider() {
			this.listeners = new ArrayList<ISelectionChangedListener>();
			this.selection = StructuredSelection.EMPTY;
		}
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			if (!listeners.contains(listener)) listeners.add(listener);
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
		 */
		public ISelection getSelection() {
			return this.selection;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.remove(listener);
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
		 */
		public void setSelection(ISelection selection) {
			this.selection = selection;
			fireSelectionChanged();
		}
		
		private void fireSelectionChanged() {
			SelectionChangedEvent evt = new SelectionChangedEvent(this, getSelection());
			for (ISelectionChangedListener listener : listeners) {
				listener.selectionChanged(evt);
			}
		}
		
	}

	/**
	 * Should be called after the part control has been created.
	 */
	private void loadPreferences() {
		// load the preferred sash weights
		int[] leftWeights = TagSEAPreferences.getTagsSashWeights();
		
		if (leftWeights == null) {
			leftWeights = new int[] {10, 25};	// default weights
		}
		
		leftSash.setWeights(leftWeights);
		
		int[] rightWeights = TagSEAPreferences.getWaypointsSashWeights();
		
		if (rightWeights == null) {
			rightWeights = new int[] {100};	// default weights
		}
		
		rightSash.setWeights(rightWeights);
		tagsComposite.loadPreferences();
		waypointsComposite.loadPreferences();
		getSite().getWorkbenchWindow().addPerspectiveListener(this);
		connectDnd();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveListener2#perspectiveChanged(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor, org.eclipse.ui.IWorkbenchPartReference, java.lang.String)
	 */
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) 
	{	
		if (partRef instanceof IViewReference && changeId.equals(IWorkbenchPage.CHANGE_VIEW_HIDE))
		{
			String id = ((IViewReference) partRef).getId();
			
			if (id.equals(getViewSite().getId())) 
			{
				savePreferences();
				saveViewerState();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveListener#perspectiveActivated(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor)
	 */
	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) 
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveListener#perspectiveChanged(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor, java.lang.String)
	 */
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) 
	{
	}
	
	/**
	 * Saves the TagSEA preferences.
	 */
	private void savePreferences()
	{
		try {
//			boolean showRoutes = (rightSash.getMaximizedControl() == null);
//			TagSEAPreferences.setShowRoutes(showRoutes);

			int[] weights = leftSash.getWeights();
			TagSEAPreferences.setTagsSashWeights(weights);
			weights = rightSash.getWeights();
			TagSEAPreferences.setWaypointsSashWeights(weights);
			
			tagsComposite.savePreferences();
			waypointsComposite.savePreferences();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(final Composite parent) {
		//add the selection provider
		getSite().setSelectionProvider(new ViewSelectionProvider());
		
		leftSash = new SashForm(parent, SWT.HORIZONTAL);
		tagsComposite = new TagsComposite(leftSash, SWT.NONE, this);
		
		rightSash = new SashForm(leftSash, SWT.HORIZONTAL);		
		waypointsComposite = new WaypointsComposite(rightSash, SWT.NONE);
		
		tagsComposite.getTagsTreeViewer().setInput(getViewSite());
		tagsComposite.getTagsTreeViewer().addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				getSite().getSelectionProvider().setSelection(event.getSelection());
			}
		});
		waypointsComposite.getWaypointsTableViewer().setInput(getViewSite());
		waypointsComposite.getWaypointsTableViewer().addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				getSite().getSelectionProvider().setSelection(event.getSelection());
			}
		});
		// when a tag is selected, show all the waypoints
		tagsComposite.getTagsTreeViewer().addSelectionChangedListener(waypointsComposite.getWaypointProvider());

		makeActions();
		hookContextMenus();
		hookDoubleClickActions();
		contributeToActionBars();
		loadPreferences();
		hookListeners();
	}
	private void hookListeners() {
		partListener = new IPartListener() 
		{
			public void partClosed(IWorkbenchPart part) 
			{		
				saveViewerState();
				getSite().getWorkbenchWindow().getPartService().removePartListener(this);
			}
			public void partActivated(IWorkbenchPart part) {}
			public void partBroughtToTop(IWorkbenchPart part) {}
			public void partDeactivated(IWorkbenchPart part) {}
			public void partOpened(IWorkbenchPart part) 
			{
				// attempt to connect dnd to the tags view
				connectDnd();
			}
		};
		getSite().getWorkbenchWindow().getPartService().addPartListener(partListener);
		
		tagListener = new ITagCollectionListener() {
		
			public void tagsLoaded() 
			{
				getTagsComposite().getTagsTreeViewer().refresh();
				loadViewerState();
			}
		
		};
		TagSEAPlugin.getDefault().getTagCollection().addTagCollectionListener(tagListener);
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
		TagSEAPlugin.getDefault().getTagCollection().removeTagCollectionListener(tagListener);
		super.dispose();
	}
		
	@Override
	public void saveState(IMemento memento)
	{
		savePreferences();
		saveViewerState();
		super.saveState(memento);
	}
	
	private void saveViewerState() 
	{
		TagsViewStateManager.recordState(tagsComposite);
	}
	
	private void loadViewerState() 
	{
		TagsViewStateManager.restoreState(tagsComposite);
	}
	
	
	private void hookContextMenus() {
		MenuManager menuMgr = tagsComposite.createContextMenu();
		getSite().registerContextMenu(menuMgr, tagsComposite.getTagsTreeViewer());

		menuMgr = waypointsComposite.createContextMenu();
		getSite().registerContextMenu(menuMgr, waypointsComposite.getWaypointsTableViewer());
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TagsView.this.fillWaypointsContextMenu(manager);
			}
		});
		
		// @tag TagsView(ContextMenu) : register additional context menus here
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillWaypointsContextMenu(IMenuManager manager) 
	{
//		Route route = routesComposite.getSelectedRoute();
//		final Waypoint[] selectedWaypoints = waypointsComposite.getSelectedWaypoints();
//		boolean enabled = (route != null) && (selectedWaypoints.length > 0);
//		addWaypointsToRouteAction.setEnabled(enabled);
		
		// update the action text to show the route name
//		boolean multiple = (selectedWaypoints.length >= 2);
//		String text = (multiple ? ADD_WAYPOINTS_TEXT  : ADD_WAYPOINT_TEXT);
//		if (route != null) {
//			text += " (" + route.getName() + ")";
//		}
//		addWaypointsToRouteAction.setText(text);
		
		//manager.add(addWaypointsToRouteAction);
//		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalPullDown(IMenuManager manager) 
	{
		manager.add(emailAction);
		manager.add(sendTagsAction);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(emailAction);
		manager.add(sendTagsAction);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() 
	{		
		emailAction = new Action() {
			public void run() {
				try {
					String href = "mailto:" + HELP_EMAIL + "?subject=TagSEA Feedback";
					IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
					IWebBrowser browser = support.getExternalBrowser();
					browser.openURL(new URL(href));
				} catch (Throwable t) {
					TagSEAPlugin.log("Failed to open email client.", t);
				}
			}
		};
		emailAction.setText("Send Feedback");
		emailAction.setToolTipText("Email us with your feedback");
		emailAction.setImageDescriptor(TagSEAPlugin.getImageDescriptor("/icons/email.gif"));
		
		sendTagsAction = new SendTagsAction(getViewSite());
		sendTagsAction.setText("Send Tags");
		sendTagsAction.setToolTipText("Send us your tags");
		sendTagsAction.setImageDescriptor(TagSEAPlugin.getImageDescriptor("/icons/send_tags.gif"));
		
//		addWaypointsToRouteAction = new Action(ADD_WAYPOINT_TEXT) {
//			public void run() {
//				Route route = routesComposite.getSelectedRoute();
//				if (route != null) {
//					Waypoint[] wps = waypointsComposite.getSelectedWaypoints();
//					if (wps.length > 0) {
//						for (Waypoint wp : wps) {
//							route.addWaypoint(wp);
//						}
//						routesComposite.refreshRoutesViewer(route);
//						// expand the route and select the waypoints
//						routesComposite.getRoutesTreeViewer().expandToLevel(route, 1);
//						routesComposite.getRoutesTreeViewer().setSelection(new StructuredSelection(wps), true);
//					}
//				}
//			}
//		};
	}

	private void hookDoubleClickActions() {
		IWorkbenchPage page = getSite().getPage();
		waypointsComposite.hookDoubleClickAction(page);
//		routesComposite.hookDoubleClickAction(page);		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		tagsComposite.getTagsTreeViewer().getControl().setFocus();
	}

	/**
	 * Refreshes the tags tree viewer.
	 */
	public void refreshTagsViewer() {
		tagsComposite.refreshTagsViewer(null);
	}

	public TagsComposite getTagsComposite() {
		return tagsComposite;
	}
	
	public WaypointsComposite getWaypointsComposite() {
		return waypointsComposite;
	}
	
	/**
	 * This is a hack to temporarily support the drag and drop support
	 */
	private void connectDnd()
	{
		RoutesView view = TagSEAPlugin.getDefault().getRoutesView();

		try 
		{
			if(view != null)
				view.getRoutesComposite().getRoutesTreeItemWorker().addTableDragSource(waypointsComposite.getWaypointsTableViewer().getTable());
		} 
		catch (SWTError e) 
		{
			e.printStackTrace();
		}
	}

}