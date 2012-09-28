/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui.internal.tags;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.core.ui.tags.TagsView;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;

/**
 * Action Group for the tags viewer.
 * @author Del Myers
 */
@Deprecated
public class TagActionGroup extends ActionGroup implements IPropertyChangeListener {
	
	
	private TagRenameAction renameAction;
	private ContextAction generalizeAction;
	private TagDeleteAction deleteAction;

	private TagToggleTreeNamingAction toggleTreeNamingAction;
	//private OpenWaypointViewAction openWaypointViewAction;

	public TagActionGroup(TagsView view) {
		renameAction = new TagRenameAction(view.getTreeViewer(), new TagRenamer(), (String[])view.getTreeViewer().getColumnProperties());
		renameAction.setImageDescriptor(TagSEAPlugin.getImageDescriptor("/icons/rename.gif"));
		renameAction.setText("Rename");
		renameAction.setToolTipText("Rename");
		generalizeAction = new GeneralizeAction();
		generalizeAction.setImageDescriptor(TagSEAPlugin.getImageDescriptor("/icons/taggeneralize.gif"));
		generalizeAction.setText("Push Up Tags/Waypoints");
		generalizeAction.setToolTipText("Moves tags and waypoints into the parent");
		deleteAction = new TagDeleteAction();
		deleteAction.setText("Delete");
		deleteAction.setToolTipText("Removes references to this tag and its children from all waypoints");
		deleteAction.setImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_DELETE));
		deleteAction.setDisabledImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_DELETE_DISABLED));
		view.getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);
		toggleTreeNamingAction = new TagToggleTreeNamingAction();
		toggleTreeNamingAction.setText("Keep Hierarchical Names");
		toggleTreeNamingAction.setDescription("Toggles whether renaming will keep the tree hierarchy.");
//		openWaypointViewAction = new OpenWaypointViewAction();
//		openWaypointViewAction.setText("Open Waypoint View");
//		openWaypointViewAction.setToolTipText("Opens the waypoint view");
//		openWaypointViewAction.setImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_WAYPOINT));
		refreshActions();
		IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(this);
	}
	
	
	public void runDefaultAction(ISelection selection) {
//		openWaypointViewAction.run();
	}
	
	public void runDefaultAction(Event event) {
//		openWaypointViewAction.run();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#setContext(org.eclipse.ui.actions.ActionContext)
	 */
	@Override
	public void setContext(ActionContext context) {
		renameAction.setContext(context);
		generalizeAction.setContext(context);
		deleteAction.setContext(context);
		super.setContext(context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		menu.add(renameAction);
		menu.add(generalizeAction);
		menu.add(new Separator());
		menu.add(deleteAction);
		super.fillContextMenu(menu);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		fillContributionManager(actionBars.getToolBarManager());
		fillContributionManager(actionBars.getMenuManager());
	}
	
	private void fillContributionManager(IContributionManager manager) {
		manager.add(renameAction);
		manager.add(generalizeAction);
		manager.add(new Separator());
		manager.add(deleteAction);
		manager.add(new Separator());
//		manager.add(openWaypointViewAction);
		
		//MenuManager viewMenu = new MenuManager("View", "view");
		
		//viewMenu.add(toggleTreeNamingAction);
		//manager.add(viewMenu);
	}
	
	private void refreshActions() {
		//reset the context to update other actions.
		setContext(getContext());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
		store.removePropertyChangeListener(this);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		refreshActions();
	}
	
	/**
	 * Runs the action used to edit tag names.
	 */
	public void runEditAction() {
		renameAction.run();
	}
}
