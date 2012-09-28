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
package net.sourceforge.tagsea.core.ui.tags;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITagChangeListener;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.internal.ITagSEAPreferences;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUI;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUIEvent;
import net.sourceforge.tagsea.core.ui.internal.tags.TagActionGroup;
import net.sourceforge.tagsea.core.ui.internal.tags.TagToggleTreeAction;
import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItem;
import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItemTransfer;
import net.sourceforge.tagsea.core.ui.internal.tags.TagWaypointViewerFilter;
import net.sourceforge.tagsea.core.ui.internal.tags.TagsDragListener;
import net.sourceforge.tagsea.core.ui.internal.tags.TagsTree;
import net.sourceforge.tagsea.core.ui.internal.tags.TagsViewDropListener;
import net.sourceforge.tagsea.core.ui.internal.views.CachingTreePatternFilter;
import net.sourceforge.tagsea.core.ui.internal.views.PatternFilteredTree;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * The Tags view.
 * @author Del Myers
 */
@Deprecated
public class TagsView extends ViewPart implements ISelectionProvider {
	
	public static final String ID = "net.sourceforge.tagsea.ui.TagsView";
	private PatternFilteredTree treeViewer;
	private TagsChangedListener tagsListener;
	private boolean refreshScheduled;
	private List<ISelectionChangedListener> selectionListeners;
	private ISelection currentSelection;
	private TreeSelectionChangedListener treeSelectionListener;
	private TagActionGroup actionGroup;
	private TagsPreferenceListener preferenceListener;
	private Action fFilterToWaypointsAction;
	private TagToggleTreeAction toggleTreeAction;
	private TagWaypointViewerFilter fWaypointViewerFilter;
	private ISelectionListener fWorkbenchSelectionListener;


	/**
	 * Listener used in the tree viewer that opens an item for editing or opens the waypoint view. The
	 * editor is opened when one of the following occurs:
	 * 1) F2 is pressed & released
	 * 2) "space" is pressed & released
	 * 3) An item is selected for the second time.
	 * 
	 * The waypoints view is opened when an item is double-clicked.
	 * @author Del Myers
	 *
	 */
	private final class ItemEditListener implements Listener {
		boolean doubleClicked = false;
		//timer to make sure that a double-click isn't pressed.
		private Timer doubleClickTimer = null;

		private Display display;

		private Item thisSelection;

		private Item lastSelection;

		public void handleEvent(Event event) {
			this.display = event.widget.getDisplay();
			switch (event.type) {
			case SWT.Dispose:
				if (doubleClickTimer != null) {
					doubleClickTimer.cancel();
					doubleClickTimer = null;
				}
				break;
			case SWT.KeyUp:
				if (event.detail == 0) {
					if (event.keyCode == SWT.F2 || event.character == ' ') {
						actionGroup.runEditAction();
					}
				}
				break;
			case SWT.Selection:
				lastSelection = thisSelection;
				thisSelection = (Item) event.item;
				if (this.doubleClickTimer != null) {
					this.doubleClickTimer.cancel();
					this.doubleClickTimer = null;
				}
				if (doubleClicked) {
					doubleClicked = false;
				} else {
					if (true) {
						if (event.widget instanceof Tree) {
							if (thisSelection == lastSelection && lastSelection != null) {
								doubleClickTimer = new Timer();
								doubleClickTimer.schedule(new TimerTask(){
									@Override
									public void run() {
										display.asyncExec(new Runnable(){
											public void run() {
												if (!doubleClicked) {
													actionGroup.runEditAction();
												}
											}
										});								
									}			
								}, display.getDoubleClickTime()+25);
							}
						}
					}
				}
				break;
			case SWT.MouseDoubleClick:
				doubleClicked = true;
				if (this.doubleClickTimer != null) {
					this.doubleClickTimer.cancel();
					this.doubleClickTimer = null;
				}
				actionGroup.runDefaultAction(event);
				
				break;
			}
			
		}
	}


	/**
	 * Listener for the internal tree viewer to translate the viewer model
	 * to tags.
	 * @author Del Myers
	 */
	private class TreeSelectionChangedListener implements ISelectionChangedListener {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			
			ISelection selection = event.getSelection();
			actionGroup.setContext(new ActionContext(selection));
			updateSelection(selection);
		}
	}
	
	
	private class TagsChangedListener implements ITagChangeListener {
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.ITagChangeListener#tagsChanged(net.sourceforge.tagsea.core.TagDelta)
		 */
		public void tagsChanged(TagDelta delta) {
			if (treeViewer.isDisposed()) return;
			scheduleViewerRefresh();
		}
	}
	
	private class TagsPreferenceListener implements IPropertyChangeListener {
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.Preferences.IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			refreshActions();
			if (ITagSEAPreferences.TAGS_VIEW_TREE.equals(event.getProperty())) {
				boolean newValue = ((Boolean)event.getNewValue());
				treeViewer.getViewer().setInput(new TagsTree(!newValue));
				scheduleViewerRefresh();
			}
		}
	}
	
	
	public TagsView() {
		selectionListeners = new LinkedList<ISelectionChangedListener>();
		currentSelection = StructuredSelection.EMPTY;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite page = new Composite(parent, SWT.FLAT);
		page.setLayout(new GridLayout());
		page.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createTreeViewer(page);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		preferenceListener = new TagsPreferenceListener();
		TagSEAPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(preferenceListener);
		IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) getSite().getAdapter(IWorkbenchSiteProgressService.class);
		if (service != null) {
			service.showBusyForFamily(TagSEAPlugin.getDefault());
		}
		this.fWorkbenchSelectionListener = new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				updateForSelection(part, selection);				
			}
		};
		this.fWaypointViewerFilter = new TagWaypointViewerFilter();
		updateForWaypointsFilter();
		
		
	}

	/**
	 * 
	 */
	public void scheduleViewerRefresh() {
		if (!refreshScheduled) {
			this.refreshScheduled = true;
			TimerTask refresher = new TimerTask() {
				@Override
				public void run() {
					Display.getDefault().asyncExec(new Runnable(){
						public void run() {
							if (!refreshScheduled) return;
							if (treeViewer.getViewer().getControl().isDisposed()) return;
							treeViewer.refresh();
							refreshScheduled = false;
							IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) getSite().getAdapter(IWorkbenchSiteProgressService.class);
							if (service != null) {
								service.warnOfContentChange();
							}
							updateSelection(treeViewer.getViewer().getSelection());
						}
						
					});
				}
			};
			new Timer().schedule(refresher, 1000);
		}
		
	}

	/**
	 * Updates this view's selection according to the given selection (from the tree viewer).
	 * @param selection
	 */
	protected void updateSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			List<ITag> tagList = new LinkedList<ITag>();
			Iterator<?> it = ss.iterator();
			while (it.hasNext()) {
				TagTreeItem item = (TagTreeItem) it.next();
				LinkedList<TagTreeItem> children = new LinkedList<TagTreeItem>();
				children.add(item);
				while (children.size() > 0) {
					item = children.removeFirst();
					ITag tag = item.getTag();
					if (tag != null) {
						tagList.add(tag);
					}
					for (TagTreeItem child : item.getChildren()) {
						children.add(child);
					}
				}
			}
			IStructuredSelection newSelection = new StructuredSelection(tagList);
			//	check the selection.
			if (!currentSelection.equals(newSelection)) {
				currentSelection = newSelection;
				fireSelectionChanged(newSelection);
			}
		}
	}
	
	/**      
	 * 
	 */
	private void contributeToActionBars() {
		actionGroup.fillActionBars(getViewSite().getActionBars());
//		IContributionItem item = getViewSite().getActionBars().getMenuManager().find("view");
//		if (item instanceof IMenuManager) {
//			((IMenuManager)item).add(fFilterToWaypointsAction);
//		}
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
		manager.add(new Separator());
		manager.add(toggleTreeAction);  
		manager.add(fFilterToWaypointsAction);
	}

	/**
	 * 
	 */
	private void hookContextMenu() {
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener(){
			public void menuAboutToShow(IMenuManager manager) {
				actionGroup.fillContextMenu(manager);
			}
		});
		Menu menu = manager.createContextMenu(treeViewer.getViewer().getTree());
		treeViewer.getViewer().getControl().setMenu(menu);
		//getSite().registerContextMenu(manager, treeViewer.getViewer());
	}

	/**
	 * 
	 */
	private void makeActions() {
		this.actionGroup = new TagActionGroup(this);
		this.fFilterToWaypointsAction = new Action("Link to Waypoint Filters",Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				boolean filter = TagSEAPlugin.getDefault().getPluginPreferences().getBoolean(ITagSEAPreferences.FILTER_TAGS_TO_WAYPOINTS);
				TagSEAPlugin.getDefault().getPluginPreferences().setValue(ITagSEAPreferences.FILTER_TAGS_TO_WAYPOINTS, !filter);
				updateForWaypointsFilter();
			}
		};
		this.fFilterToWaypointsAction.setImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_FILTER_WAYPOINT));
		toggleTreeAction  = new TagToggleTreeAction();
		toggleTreeAction.setText("View As Hierarchy");
		toggleTreeAction.setDescription("Toggles the display mode of tags.");
		toggleTreeAction.setImageDescriptor(TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_TAG_HIERARCHY));
		refreshActions();
	}
	
	/**
	 * 
	 */
	private void refreshActions() {
		IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
		boolean asTree = store.getBoolean(ITagSEAPreferences.TAGS_VIEW_TREE);
		toggleTreeAction.setChecked(asTree);
		if (asTree) {
			toggleTreeAction.setToolTipText("Uncheck to view tags with flat names");
		} else {
			toggleTreeAction.setToolTipText("Check to view dot-separated tag names as a hierarchy");
		}
		
	}
	private void updateForWaypointsFilter() {
		boolean filter = TagSEAPlugin.getDefault().getPluginPreferences().getBoolean(ITagSEAPreferences.FILTER_TAGS_TO_WAYPOINTS);
		fFilterToWaypointsAction.setChecked(filter);
		if (filter) {
			treeViewer.getViewer().addFilter(fWaypointViewerFilter);
			getSite().getPage().addSelectionListener(fWorkbenchSelectionListener);
		} else {
			treeViewer.getViewer().removeFilter(fWaypointViewerFilter);
			getSite().getPage().removeSelectionListener(fWorkbenchSelectionListener);
		}
		scheduleViewerRefresh();
	}
	/**
	 * @param part
	 * @param selection
	 */
	protected void updateForSelection(IWorkbenchPart part, ISelection selection) {
		if (fWaypointViewerFilter.isImportantSelection(selection)) {
			scheduleViewerRefresh();
		}
	}
	/**
	 * 
	 */
	private void createTreeViewer(Composite parent) {
		this.treeViewer = new PatternFilteredTree(parent, SWT.FLAT | SWT.BORDER | SWT.MULTI, new CachingTreePatternFilter());
		this.treeViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.treeViewer.getViewer().setContentProvider(new BaseWorkbenchContentProvider());
		this.treeViewer.getViewer().setLabelProvider(new TagsViewLabelProvider());
		this.treeViewer.getViewer().setSorter(new TagsViewSorter());
		boolean asTree = TagSEAPlugin.getDefault().getPreferenceStore().getBoolean(ITagSEAPreferences.TAGS_VIEW_TREE);
		treeViewer.getViewer().setInput(new TagsTree(!asTree));
		treeViewer.getFilterControl().addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				if (e.widget instanceof Text) {
					((TagSEAUI)TagSEAPlugin.getDefault().getUI()).getUIEventModel().fireEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_FILTERED, ID, ((Text)e.widget).getText()));
				}
			}
		});
		
		//set up columns
		Tree tree = treeViewer.getViewer().getTree();
		final TreeColumn nameColumn = new TreeColumn(tree, SWT.FLAT | SWT.LEFT);
		final TreeColumn sizeColumn = new TreeColumn(tree, SWT.FLAT | SWT.LEFT);
		tree.addControlListener(new ControlListener(){
			public void controlMoved(ControlEvent e) {
				//auto resize the columns
				Tree tree = (Tree) e.widget;
				int width = tree.getClientArea().width;
				sizeColumn.setWidth(30);
				nameColumn.setWidth(width-30);
			}

			public void controlResized(ControlEvent e) {
				controlMoved(e);
			}
		});
		
		Listener openListener = new ItemEditListener();
		tree.addListener(SWT.KeyUp, openListener);
		tree.addListener(SWT.MouseDoubleClick, openListener);
		tree.addListener(SWT.Selection, openListener);
		tree.addListener(SWT.Dispose, openListener);
		treeViewer.getViewer().setColumnProperties(new String[] {"name", "waypointCount"});
		tagsListener = new TagsChangedListener();
		this.treeSelectionListener = new TreeSelectionChangedListener();
		treeViewer.getViewer().addSelectionChangedListener(treeSelectionListener);
		treeViewer.getViewer().addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] {TagTreeItemTransfer.getInstance(), TagNameTransfer.getInstance()}, new TagsDragListener(treeViewer.getViewer()));
		treeViewer.getViewer().addDropSupport(DND.DROP_MOVE, new Transfer[] {TagTreeItemTransfer.getInstance()}, new TagsViewDropListener());
		TagSEAPlugin.addTagChangeListener(tagsListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public TreeViewer getTreeViewer() {
		return this.treeViewer.getViewer();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		getSite().getPage().removeSelectionListener(fWorkbenchSelectionListener);
		super.dispose();
		refreshScheduled = false;
		TagSEAPlugin.removeTagChangeListener(tagsListener);
		TagSEAPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(preferenceListener);
		treeViewer.getViewer().removeSelectionChangedListener(treeSelectionListener);
		actionGroup.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (selectionListeners.contains(listener)) return;
		selectionListeners.add(listener);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		treeViewer.getViewer().getSelection();
		return currentSelection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		this.currentSelection = selection;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss =
				(IStructuredSelection) selection;
			
			ITreeSelection treeSelection = createTreeSelection(ss);
			treeViewer.getViewer().setSelection(treeSelection, true);
		}
		fireSelectionChanged(selection);
	}
	
	/**
	 * @param ss
	 * @return
	 */
	private ITreeSelection createTreeSelection(IStructuredSelection ss) {
		List<TreePath> treeItems = new LinkedList<TreePath>();
		Iterator<?> it = ss.iterator();
		
		while (it.hasNext()) {
			Object next = it.next();
			if (next instanceof ITag) {
				ITag tag = (ITag) next;
				//make a dummy item to select.
				TreePath path = createTreePath(tag);
				treeItems.add(path);
			}
		}
		TreeSelection selection = new TreeSelection(treeItems.toArray(new TreePath[treeItems.size()]));
		return selection;
	}
	/**
	 * @param tag
	 * @return
	 */
	private TreePath createTreePath(ITag tag) {
		String[] segments = tag.getName().split("\\.");
		boolean flat = !TagSEAPlugin.getDefault().getPreferenceStore().getBoolean(ITagSEAPreferences.TAGS_VIEW_TREE);
		TagTreeItem parent = null;
		String parentName = "";
		TreePath path = TreePath.EMPTY;
		for (String segment : segments) {
			TagTreeItem item = new TagTreeItem(parentName + segment, parent, flat);
			parentName = item.getName() + ".";
			path = path.createChildPath(item);
			parent = item;
		}
		return path;
	}
	private void fireSelectionChanged(ISelection selection) {
		ISelectionChangedListener[] localListeners =
			selectionListeners.
			toArray(new ISelectionChangedListener[selectionListeners.size()]);
		SelectionChangedEvent event =
			new SelectionChangedEvent(this, selection);
		for (ISelectionChangedListener listener : localListeners) {
			listener.selectionChanged(event);
		}
	}

}
