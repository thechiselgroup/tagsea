/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.ui.internal.waypoints.DeleteUnusedWaypointsAction;
import net.sourceforge.tagsea.core.ui.internal.waypoints.WaypointFilteringDialog;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;

/**
 * Contains the actions for waypoints in the TagSEAView
 * @author Del Myers
 *
 */
public class WaypointActionGroup extends ActionGroup {
	
	private Action deleteAction;
	private DeleteUnusedWaypointsAction deleteUnusedAction;
	private Action openFiltersAction;
	private TagSEAView view;

	public WaypointActionGroup(TagSEAView view) {
		this.view = view;
		deleteAction = new Action() 
		{
			@Override
			public void run()
			{
				ISelection selection = getContext().getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection)selection;
					final LinkedList<IWaypoint> waypoints = new LinkedList<IWaypoint>();
					for (Object o : ss.toList()) {
						
						IWaypoint wp = (IWaypoint) ((IAdaptable)o).getAdapter(IWaypoint.class);
						IWaypointUIExtension extension = TagSEAPlugin.getDefault().getUI().getWaypointUI(wp.getType());
						if (extension.canUIDelete(wp)) {
							waypoints.add(wp);
						}
					}

					TagSEAPlugin.run(new TagSEAOperation("Deleting Waypoints..."){
						public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
							monitor.beginTask("Deleting Waypoints", waypoints.size());
							MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
							for (IWaypoint wp : waypoints) {
								status.merge(TagSEAPlugin.getWaypointsModel().removeWaypoint(wp).getStatus());
								monitor.worked(1);
								if (monitor.isCanceled())
									return Status.CANCEL_STATUS;
							}
							return status;
						}
					}, false);
					
				}
			}
		};
		deleteAction.setText("Delete Tagged Location");
		deleteAction.setToolTipText("Delete selected");
		deleteAction.setImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_DELETE));
		//getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);
		deleteUnusedAction = new DeleteUnusedWaypointsAction(view.getSite().getShell());
		deleteUnusedAction.setImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_DELETE_UNUSED));
		deleteUnusedAction.setText("Cleanup Untagged Locations...");
		deleteUnusedAction.setToolTipText("Cleanup TagSEA locations that do not have tags");
		openFiltersAction = new Action() {
			@Override
			public void run() {
				WaypointFilteringDialog dialog = new WaypointFilteringDialog(WaypointActionGroup.this.view.getSite().getShell());
				dialog.open();
				WaypointActionGroup.this.view.scheduleRefresh();
			}
		};
		openFiltersAction.setImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_FILTER));
		openFiltersAction.setText("Filters...");
	}
	
	@Override
	public void fillActionBars(IActionBars actionBars) {
//		MenuManager waypointsMenu = new MenuManager("Waypoints");
//		
		actionBars.getToolBarManager().add(openFiltersAction);
		actionBars.getToolBarManager().add(new Separator());
		actionBars.getToolBarManager().add(deleteUnusedAction);
		actionBars.getToolBarManager().add(new Separator());
		
//		actionBars.getMenuManager().add(deleteAction);

	}
	
	IAction getDeleteAction() {
		return deleteAction;
	}
	
	@Override
	public void setContext(ActionContext context) {
		if (context.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) context.getSelection();
			if (ss.getFirstElement() instanceof IAdaptable) {
				IWaypoint wp = (IWaypoint) ((IAdaptable)ss.getFirstElement()).getAdapter(IWaypoint.class);
				if (wp != null) {
					deleteAction.setEnabled(TagSEAPlugin.getDefault().getUI().canUIDelete(wp));
				}
			}
		}
		super.setContext(context);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		menu.add(deleteAction);
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		super.fillContextMenu(menu);
	}
	
	
}
