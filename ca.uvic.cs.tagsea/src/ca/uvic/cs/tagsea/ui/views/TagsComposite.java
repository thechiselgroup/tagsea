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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import ca.uvic.cs.tagsea.ITagseaImages;
import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.actions.RefreshTagsAction;
import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.core.TagCollection;
import ca.uvic.cs.tagsea.editing.TagNameValidator;
import ca.uvic.cs.tagsea.editing.TagTreeItemListener;
import ca.uvic.cs.tagsea.editing.TreeItemWorker;
import ca.uvic.cs.tagsea.navigation.TagsTreeFilter;

/**
 * The composite which contains the tags TreeViewer, header label and filter composite.
 * 
 * @author Chris Callendar, Chris Bennett, mdesmond
 */
public class TagsComposite extends BaseTagsViewComposite {

	private static final String KEY_COLLAPSE = "tagsea_collapse";
	private static final String KEY_COLLAPSE_DIS = "tagsea_collapse_disabled";
	private static final String KEY_EXPAND = "tagsea_expand";
	private static final String KEY_EXPAND_DIS = "tagsea_expand_disabled";

	private TreeViewer tagsTreeViewer;
	private Action scanForTagsAction;
	private Action selectAllTagsAction;
	private Action collapseTagsAction;
	private Action expandTagsAction;
	private Action deleteAction;
	private Action renameAction;
	private Action generalizeAction;
	
	private TreeItemWorker tagsWorker;
	private TagTreeItemListener tagsListener;
	private TagsView fView;
	

	public TagsComposite(Composite parent, int style, TagsView view) {
		super(parent, style);
		loadImageDescriptors();
		fView = view;
		GridLayout layout = new GridLayout(1, true);
		this.setLayout(layout);

		GridData data = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
		this.setLayoutData(data);

		Composite header = new Composite(this, SWT.NONE);		
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		header.setLayoutData(data);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 3;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		header.setLayout(gridLayout);
		
		// creating a toolbar button instead of a label
		createScanToolBar(header);
		createToolBarMenu(header);
		// this composite has the filter composite and the toolbar menu
		FilterComposite filterComposite = new FilterComposite(header, SWT.NONE);
		filterComposite.setTreeFilter(new TagsTreeFilter());
		initTreeViewer();
		filterComposite.createTextFilterControl(tagsTreeViewer);
		filterComposite.getTextControl().setForeground(getHeaderLabelColor());
		makeActions();
	}

	
	private void createScanToolBar(Composite parent) 
	{
		ToolBar toolbar = new ToolBar(parent,SWT.FLAT | SWT.LEFT);
		GridData data = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		toolbar.setLayoutData(data);
		
		ToolBarManager manager = new ToolBarManager(toolbar);
		
		scanForTagsAction = new Action("Refresh tag database...",TagSEAPlugin.getImageDescriptor("/icons/synch.gif")) 
		{
			public void run() 
			{
				new RefreshTagsAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow()).run();
			}
		};

		manager.add(scanForTagsAction);
		manager.update(false);
		toolbar.setToolTipText("Refresh tag database...");
	}
	
	public TreeViewer getTagsTreeViewer() {
		return tagsTreeViewer;
	}
	
	private void loadImageDescriptors() {
		ImageRegistry images = TagSEAPlugin.getDefault().getImageRegistry();
		images.put(KEY_EXPAND, TagSEAPlugin.getImageDescriptor("/icons/expandall.gif"));
		images.put(KEY_EXPAND_DIS, TagSEAPlugin.getImageDescriptor("/icons/expandall_disabled.gif"));
		images.put(KEY_COLLAPSE, TagSEAPlugin.getImageDescriptor("/icons/collapseall.gif"));
		images.put(KEY_COLLAPSE_DIS, TagSEAPlugin.getImageDescriptor("/icons/collapseall_disabled.gif"));
	}

	private void makeActions() {
	
		selectAllTagsAction = new Action("Select &All\tCtrl+A") {
			public void run() {
				TagCollection tagCollection = TagSEAPlugin.getDefault().getTagCollection();
				Tag[] allTags = tagCollection.getAllTags();
				tagsTreeViewer.setSelection(new StructuredSelection(allTags), false);
				tagsTreeViewer.getControl().setFocus();
			}
		};
		selectAllTagsAction.setAccelerator(SWT.CTRL + 'A');

		ImageRegistry images = TagSEAPlugin.getDefault().getImageRegistry();
		ImageDescriptor collapseDesc = images.getDescriptor(KEY_COLLAPSE);
		ImageDescriptor collapseDescDis = images.getDescriptor(KEY_COLLAPSE_DIS);
		ImageDescriptor expandDesc = images.getDescriptor(KEY_EXPAND);
		ImageDescriptor expandDescDis = images.getDescriptor(KEY_EXPAND_DIS);
		collapseTagsAction = new Action("Collapse All", collapseDesc) {
			public void run() {
				tagsTreeViewer.collapseAll();
			}
		};
		collapseTagsAction.setDisabledImageDescriptor(collapseDescDis);
		expandTagsAction = new Action("Expand All", expandDesc) {
			public void run() {
				tagsTreeViewer.expandAll();
			}
		};
		expandTagsAction.setDisabledImageDescriptor(expandDescDis);
		
		final ISharedImages workbenchImages = PlatformUI.getWorkbench().getSharedImages();
		
		ImageDescriptor deleteDescriptor = workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE);
		ImageDescriptor deleteDescriptorDis = workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED);
		deleteAction = new Action("Delete", deleteDescriptor) {
			public void run() {
				TreeItem[] selection = tagsTreeViewer.getTree().getSelection();
				if (selection.length == 1) {
					tagsWorker.deleteTreeItem(selection[0], true);
				}
			}
		};
		deleteAction.setToolTipText("Delete this tag");
		deleteAction.setDisabledImageDescriptor(deleteDescriptorDis);

		renameAction = new Action("Rename") {
			public void run() {
				TreeItem[] selection = tagsTreeViewer.getTree().getSelection();
				if (selection.length == 1) {
					tagsWorker.renameTreeItem(selection[0]);
				}
			}
		};
		renameAction.setToolTipText("Rename the selected tag.");
		
		generalizeAction = new Action("Generalize") {
			public void run() {
				// @tag Editing(Generalize): need to implement
			}
		};
		generalizeAction.setEnabled(false);
	}

	private void createToolBarMenu(Composite parent) {
		final ToolBar toolbar = new ToolBar(parent, SWT.FLAT |SWT.LEFT | SWT.HORIZONTAL);
		GridData data = new GridData(SWT.LEFT, SWT.FILL, false, true);
//		data.minimumWidth = 40;
//		data.widthHint = 48;
//		data.heightHint = 24;
		toolbar.setLayoutData(data);
		
		MenuManager menuMgr = new MenuManager();
		final Menu menu = menuMgr.createContextMenu(this);
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TagsComposite.this.fillToolBarMenuTags(manager);
			}
		});
		// create the drop down menu toolbar item, the menu will show when the 
		// dropdown arrow is clicked AND when the button is clicked
		Image img = TagSEAPlugin.getDefault().getTagseaImages().getImage(ITagseaImages.IMG_WAYPOINT);
		createDropDownToolBarMenu(toolbar, "", img, menu, true);
		toolbar.setToolTipText("Click to open the Tags menu");
	}

	/**
	 * @param manager
	 */
	protected void fillToolBarMenuTags(IMenuManager manager) {
		boolean enabled = tagsTreeViewer.getTree().getItemCount() > 0;
		collapseTagsAction.setEnabled(enabled);
		expandTagsAction.setEnabled(enabled);
		
		manager.add(expandTagsAction);
		manager.add(collapseTagsAction);
		manager.add(selectAllTagsAction);

		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

	}

	/**
	 * Initializes the tree viewer, setting the content and label providers.
	 */
	private void initTreeViewer() 
	{
		tagsTreeViewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		TagCollection provider = TagSEAPlugin.getDefault().getTagCollection();

		tagsWorker = new TreeItemWorker(tagsTreeViewer, false);
		tagsListener = new TagTreeItemListener(fView);
		tagsWorker.addListener(tagsListener);
		tagsWorker.setRenameValidator(new TagNameValidator());
		
		tagsTreeViewer.setContentProvider(provider);
		tagsTreeViewer.setLabelProvider(provider);
		tagsTreeViewer.setSorter(new ViewerSorter());

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 3;
		tagsTreeViewer.getControl().setLayoutData(data);
	}


	public MenuManager createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				TagsComposite.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tagsTreeViewer.getControl());
		tagsTreeViewer.getControl().setMenu(menu);
		return menuMgr;
	}

	private void fillContextMenu(IMenuManager manager) {
		boolean enabled = (tagsTreeViewer.getTree().getSelection().length == 1);
		deleteAction.setEnabled(enabled);
		renameAction.setEnabled(enabled);
		
		enabled = tagsTreeViewer.getTree().getItemCount() > 0;
		collapseTagsAction.setEnabled(enabled);
		expandTagsAction.setEnabled(enabled);
		
		manager.add(deleteAction);
		manager.add(renameAction);
		manager.add(generalizeAction);
		manager.add(new Separator());
		manager.add(expandTagsAction);
		manager.add(collapseTagsAction);
		manager.add(selectAllTagsAction);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

	}

	/**
	 * Refreshes the tags tree viewer.
	 */
	public void refreshTagsViewer(Object element) {
		// get selected and expanded id's so we can maintain the view state
		String[] selectedIdList = getSelectedList();
		String[] expandedIdList = getExpandedList();
		
		if (element == null) {
			tagsTreeViewer.refresh();
		} else {
			tagsTreeViewer.refresh(element);
		}
		
		// reselect the tags that were selected prior to the refresh
		Tag[] tags = TagSEAPlugin.getDefault().getTagCollection().getTags(expandedIdList);
		tagsTreeViewer.setExpandedElements(tags);
		tagsTreeViewer.setSelection(new StructuredSelection(TagSEAPlugin.getDefault().getTagCollection().getTags(selectedIdList)));
		//tagsTreeViewer.getControl().setFocus();		
	}
	
	/**
	 * Gets the selected list of tag ids from the view.
	 * @return An array of string's representing selected tag ids.
	 */
	protected String[] getSelectedList() {
		ISelection selection = tagsTreeViewer.getSelection();
		Object[] currentSelection = ((IStructuredSelection) selection).toArray();		
		
		String[] idList = new String[currentSelection.length];
		int i = 0;
		for ( Object o : currentSelection ) {
			idList[i++] = ((Tag)o).getId();
		}
		
		return idList;
	}
	
	/**
	 * Gets the selected list of tag ids from the view.
	 * @return An array of string's representing selected tag ids.
	 */
	protected List getSelectedTags() 
	{
		ISelection selection = tagsTreeViewer.getSelection();
		Object[] currentSelection = ((IStructuredSelection) selection).toArray();		
		
		List<Tag> tags = new ArrayList<Tag>();
		
		for ( Object o : currentSelection ) 
		{
			tags.add((Tag)o);
		}
		return tags;
	}
	
	/**
	 * Gets the expanded list of tag ids from the view.
	 * @return An array of string's representing expanded tag ids.
	 */
	private String[] getExpandedList() {
		Object[] expandedElements = tagsTreeViewer.getExpandedElements();
		String[] expandedIdList = new String[expandedElements.length];
		int i = 0;
		for ( Object o : expandedElements ) {
			expandedIdList[i++] = ((Tag)o).getId();
		}
		
		return expandedIdList;
	}
	
	/**
	 * 
	 */
	public void loadPreferences() {
	}

	/**
	 * 
	 */
	public void savePreferences() {
	}
}