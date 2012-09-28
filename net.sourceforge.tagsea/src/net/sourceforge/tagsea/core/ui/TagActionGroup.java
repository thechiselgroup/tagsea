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
package net.sourceforge.tagsea.core.ui;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ui.internal.actions.ContextAction;
import net.sourceforge.tagsea.core.ui.internal.actions.GeneralizeAction;
import net.sourceforge.tagsea.core.ui.internal.actions.TagDeleteAction;
import net.sourceforge.tagsea.core.ui.internal.actions.TagRenameAction;
import net.sourceforge.tagsea.core.ui.internal.actions.TagRenamer;
import net.sourceforge.tagsea.core.ui.internal.actions.TagToggleTreeAction;
import net.sourceforge.tagsea.core.ui.internal.actions.TagToggleTreeNamingAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;

/**
 * Action Group for the tags viewer.
 * @author Del Myers
 */

class TagActionGroup extends ActionGroup implements IPropertyChangeListener {
	
	
	private TagRenameAction renameAction;
	private ContextAction generalizeAction;
	private TagDeleteAction deleteAction;
	private Action tagCloudToggleAction;

	private TagToggleTreeNamingAction toggleTreeNamingAction;
	private TagToggleTreeAction toggleTreeAction;
	private TagSEAView tagSEAView;
	

	public TagActionGroup(TagSEAView view) {
		this.tagSEAView = view;
		renameAction = new TagRenameAction(view.getTagsTreeViewer(), new TagRenamer(), (String[])view.getTagsTreeViewer().getColumnProperties());
		renameAction.setImageDescriptor(TagSEAPlugin.getImageDescriptor("/icons/rename.gif"));
		renameAction.setText("Rename Tag");
		renameAction.setToolTipText("Rename Tag");
		generalizeAction = new GeneralizeAction();
		generalizeAction.setImageDescriptor(TagSEAPlugin.getImageDescriptor("/icons/taggeneralize.gif"));
		generalizeAction.setText("Push Tag Into Parent");
		generalizeAction.setToolTipText("Push Tag Into Parent");
		deleteAction = new TagDeleteAction();
		deleteAction.setText("Delete Tag");
		deleteAction.setToolTipText("Removes references to this tag and its children from all tagged locations");
		deleteAction.setImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_DELETE));
		deleteAction.setDisabledImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_DELETE_DISABLED));
		toggleTreeNamingAction = new TagToggleTreeNamingAction();
		toggleTreeNamingAction.setText("Keep Hierarchical Names");
		toggleTreeNamingAction.setDescription("Toggles whether renaming will keep the tree hierarchy.");
		toggleTreeAction  = new TagToggleTreeAction(view);
		toggleTreeAction.setChecked(view.isViewAsHierarchy());
		toggleTreeAction.setText("View Tags as Hierarchy");
		toggleTreeAction.setDescription("Toggles the display mode of tags.");
		toggleTreeAction.setImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_TAG_HIERARCHY));
		
		tagCloudToggleAction = new Action("View Tags in Cloud", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				tagSEAView.setViewAsCloud(isChecked());
				updateActionBars();
			}
		};
		tagCloudToggleAction.setImageDescriptor(TagSEAPlugin.getImageDescriptor("icons/cloud.gif"));
		tagCloudToggleAction.setToolTipText("View Tags in Cloud");
		refreshActions();
		IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(this);
	}
	
	
	public void runDefaultAction(ISelection selection) {
		
	}
	
	public void runDefaultAction(Event event) {
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#setContext(org.eclipse.ui.actions.ActionContext)
	 */
	@Override
	public void setContext(ActionContext context) {
		renameAction.setContext(context);
		generalizeAction.setContext(context);
		deleteAction.setContext(context);
		updateActionBars();
		super.setContext(context);
	}
	
	@Override
	public void updateActionBars() {
		if (tagSEAView.isViewAsCloud()) {
			renameAction.setEnabled(false);
			//deleteAction.setEnabled(false);
			generalizeAction.setEnabled(false);
		}
		super.updateActionBars();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		if (!tagSEAView.isViewAsCloud()) {
			menu.add(renameAction);
			menu.add(generalizeAction);
			menu.add(new Separator());
			menu.add(deleteAction);
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		}
		super.fillContextMenu(menu);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		//MenuManager tagsMenu = new MenuManager("Tags");
		
//		actionBars.getToolBarManager().add(new Separator());
		actionBars.getToolBarManager().add(toggleTreeAction);
		actionBars.getToolBarManager().add(tagCloudToggleAction);

//		actionBars.getMenuManager().add(new Separator());
//		actionBars.getMenuManager().add(deleteAction);
//		actionBars.getMenuManager().add(new Separator());
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
	
	IAction getDeleteAction() {
		return deleteAction;
	}
	
	/**
	 * Runs the action used to edit tag names.
	 */
	public void runEditAction() {
		renameAction.run();
	}
}
