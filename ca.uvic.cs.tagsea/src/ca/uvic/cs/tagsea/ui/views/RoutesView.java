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
package ca.uvic.cs.tagsea.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.IRouteCollectionListener;

/**
 * @author mdesmond
 */

public class RoutesView extends ViewPart implements IPerspectiveListener, IPerspectiveListener2, IRouteCollectionListener
{
	public static final String ID = "ca.uvic.cs.tagsea.views.RoutesView";
	private RoutesComposite routesComposite;
	
	/**
	 * @author mdesmond
	 */
	public void createPartControl(final Composite parent) 
	{	
		routesComposite = new RoutesComposite(parent, SWT.NONE);
		routesComposite.getRoutesTreeViewer().setInput(getViewSite());		
		connectDnd();
		
		// Enable double click
		hookDoubleClickActions();
		getSite().getWorkbenchWindow().addPerspectiveListener(this);
		
		/*
		 * Restore view state
		 */
		TagSEAPlugin.getDefault().getRouteCollection().addRoutesCollectionListener(this); 
	
		
		getSite().getWorkbenchWindow().getPartService().addPartListener(new IPartListener() 
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
		});
	}
	
	public void dispose() {
		super.dispose();
		TagSEAPlugin.getDefault().getRouteCollection().removeRoutesCollectionListener(this); 
	}
	
	@Override
	public void saveState(IMemento memento) 
	{
		saveViewerState();
		super.saveState(memento);
	}
	
	private void saveViewerState() 
	{
		RoutesViewStateManager.recordState(routesComposite);
	}

	private void loadViewerState() 
	{
		getRoutesComposite().getRoutesTreeViewer().refresh();
		RoutesViewStateManager.restoreState(routesComposite);
	}
	
	private void hookDoubleClickActions() 
	{
		IWorkbenchPage page = getSite().getPage();
		routesComposite.hookDoubleClickAction(page);		
	}

	public RoutesComposite getRoutesComposite() 
	{
		return routesComposite;
	}

	@Override
	public void setFocus()
	{
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
				saveViewerState();
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
	 * This is a hack to temporarily support the drag and drop support
	 */
	private void connectDnd()
	{
		TagsView view = TagSEAPlugin.getDefault().getTagsView();

		try 
		{
			if(view != null)
				routesComposite.getRoutesTreeItemWorker().addTableDragSource(view.getWaypointsComposite().getWaypointsTableViewer().getTable());
		} 
		catch (SWTError e) 
		{
			e.printStackTrace();
		}
	}

	public void routesLoaded() {
		loadViewerState();
	}
}