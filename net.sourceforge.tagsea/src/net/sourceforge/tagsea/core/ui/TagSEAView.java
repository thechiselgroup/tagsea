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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITagChangeListener;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeListener;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.core.internal.TagSEADelegatingStyledCellLabelProvider;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUI;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUIEvent;
import net.sourceforge.tagsea.core.ui.internal.clouds.CloudTreeViewer;
import net.sourceforge.tagsea.core.ui.internal.controls.HorizontalHandleToggle;
import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItem;
import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItemTransfer;
import net.sourceforge.tagsea.core.ui.internal.tags.TagWaypointViewerFilter;
import net.sourceforge.tagsea.core.ui.internal.tags.TagsDragListener;
import net.sourceforge.tagsea.core.ui.internal.tags.TagsTree;
import net.sourceforge.tagsea.core.ui.internal.tags.TagsViewDropListener;
import net.sourceforge.tagsea.core.ui.internal.views.CachingTreePatternFilter;
import net.sourceforge.tagsea.core.ui.internal.views.FilteredTable;
import net.sourceforge.tagsea.core.ui.internal.views.PatternFilteredTree;
import net.sourceforge.tagsea.core.ui.internal.views.SimpleSubStringPatternFilter;
import net.sourceforge.tagsea.core.ui.internal.waypoints.WaypointExtensionsFilter;
import net.sourceforge.tagsea.core.ui.internal.waypoints.WaypointTableDoubleClickListener;
import net.sourceforge.tagsea.core.ui.internal.waypoints.WaypointTableDragListener;
import net.sourceforge.tagsea.core.ui.internal.waypoints.WaypointTableViewDropAdapter;
import net.sourceforge.tagsea.core.ui.tags.TagNameTransfer;
import net.sourceforge.tagsea.core.ui.tags.TagsViewSorter;
import net.sourceforge.tagsea.core.ui.tags.WaypointPropertiesDialog;
import net.sourceforge.tagsea.core.ui.waypoints.WaypointTableColumnLabelProvider;
import net.sourceforge.tagsea.core.ui.waypoints.WaypointTableColumnSorter;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;


/**
 * A combined view for Tags/Waypoints. 
 * @author Del Myers
 * @author Chris Callendar
 *
 */
public class TagSEAView extends ViewPart {
	public static final String ID = "net.sourceforge.tagsea.tagseaview";
	private static final String TAGSEA_VIEW_SETTINGS = "tagseaview";
	private static final String WAYPOINTS_SETTINGS = "waypointsection";
	private static final String WAYPOINT_SORT_COLUMN = "waypointsorter";
	private static final String WAYPOINT_COLUMN_ORDER = "waypointorder";
	private static final String WAYPOINT_COLUMN_SIZES = "waypointcolumnsizes";
	private static final String TAGS_SETTINGS = "tagssection";
	private static final String TAGS_FLAT = "tagsflat";
	
	IPartListener aboutToCloseListener = new IPartListener() {

		public void partActivated(IWorkbenchPart part) {}

		public void partBroughtToTop(IWorkbenchPart part) {}

		public void partClosed(IWorkbenchPart part) {
			if (part == TagSEAView.this)
				saveState();
		}

		public void partDeactivated(IWorkbenchPart part) {}

		public void partOpened(IWorkbenchPart part) {}
		
	};
	
	
	/**
	 * The page that contains the views.
	 */
	private SashForm page;
	private PatternFilteredTree tagTreeViewer;
	//private SashForm waypointTableSash;
	private Composite waypointTableSash;
	private TableViewer waypointTableViewer;
	private WaypointExtensionsFilter fWaypointFilter;
	private Timer refreshTimer;
	private TimerTask refreshTask;
	private TagSEAModelListener tagseaModelListener;
	private FormToolkit formToolKit;
	private FormText tagsFormText;
//	private Button tagEditButton;
	private TagActionGroup tagActions;
	private WaypointActionGroup waypointActions;
	private TagsTree tagsTree;
	private Group tagsGroup;
	private CloudTreeViewer cloudViewer;
	private TagTreeSelectionListener tagSelectionListener;
	private ISelectionListener localWorkbenchSelectionListener;
	private TagWaypointViewerFilter waypointTagFilter;
	private Composite fTagsArea;
	
	
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
	 * @tag tagsea.todo : Add actions
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
						tagActions.runEditAction();
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
					if (event.widget instanceof Tree) {
						if (thisSelection == lastSelection && lastSelection != null) {
							doubleClickTimer = new Timer();
							doubleClickTimer.schedule(new TimerTask(){
								@Override
								public void run() {
									display.asyncExec(new Runnable(){
										public void run() {
											if (!doubleClicked) {
												tagActions.runEditAction();
											}
										}
									});								
								}			
							}, display.getDoubleClickTime()+25);
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
				//actionGroup.runDefaultAction(event);
				
				break;
			}
			
		}
	}
	
	
	class TagSEAModelListener implements IWaypointChangeListener, ITagChangeListener {

		public void waypointsChanged(WaypointDelta delta) {
			scheduleRefresh();
		}

		public void tagsChanged(TagDelta delta) {
			scheduleRefresh();
		}
	}
	
	class WaypointTableSelectionListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			refreshWaypointTagsArea();
			waypointActions.setContext(new ActionContext(event.getSelection()));
		}
	}
	
	class TagTreeSelectionListener implements ISelectionChangedListener {

		public void selectionChanged(SelectionChangedEvent event) {
			tagActions.setContext(new ActionContext(event.getSelection()));
			waypointTableViewer.refresh();
		}
		
	}
	
	
	private class WaypointTableContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			ITag[] selectedTags = getSelectedTags();
			if (selectedTags.length == 0) {
				return TagSEAPlugin.getWaypointsModel().getAllWaypoints();
			} else {
				HashSet<IWaypoint> waypointSet = new HashSet<IWaypoint>();
				for (ITag tag : selectedTags) {
					waypointSet.addAll(Arrays.asList(tag.getWaypoints()));
				}
				return waypointSet.toArray();
			}
		}

		public void dispose() {			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
	}
	

	public TagSEAView() {
		refreshTimer = new Timer();
	}

	/**
	 * Sets a timer to do a refresh.
	 */
	public void scheduleRefresh() {
		if (this.refreshTask != null) {
			this.refreshTask.cancel();
		}
		this.refreshTask = new TimerTask() {
			@Override
			public void run() {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
					public void run() {
						//refreshing and disposal checking can't be done outside of the display thread.
						if (page.isDisposed()) {
							return;
						}
						waypointTableViewer.refresh();
						tagTreeViewer.refresh();
						ISelection cloudSelection = cloudViewer.getSelection();
						cloudViewer.refresh();
						cloudViewer.getViewer().setSelection(cloudSelection);
					}
				});			
			}
		};
		refreshTimer.schedule(refreshTask, 500);
	}

	@Override
	public void createPartControl(Composite parent) {
		this.waypointTagFilter =new TagWaypointViewerFilter();
		page = new SashForm(parent, SWT.HORIZONTAL);

		page.addControlListener(new ControlAdapter(){
			@Override
			public void controlResized(ControlEvent e) {
				Point size = page.getSize();
				//avoid divide by zero.
				if (size.y == 0) {
					return;
				}
				//it doesn't matter if we use a float or integers. Integer division will result in 0
				//if size.y > size.x, and we still get the right result.
				if (((size.x/size.y) < 1)) {
					if (page.getOrientation() != SWT.VERTICAL) {
						page.setOrientation(SWT.VERTICAL);
					}
				} else if (page.getOrientation() != SWT.HORIZONTAL) {
					page.setOrientation(SWT.HORIZONTAL);
				}
				super.controlResized(e);
			}
		});
		createTagsArea(page);
		createWaypointsArea(page);
		page.setWeights(new int[] {1, 4});
		this.tagseaModelListener = new TagSEAModelListener();
		TagSEAPlugin.addWaypointChangeListener(tagseaModelListener);
		IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) getSite().getAdapter(IWorkbenchSiteProgressService.class);
		if (service != null) {
			service.showBusyForFamily(TagSEAPlugin.getDefault());
		}
		
		getSite().getPage().addPartListener(aboutToCloseListener);
		restoreState();
		makeActions();
		fillActionBars();
		this.localWorkbenchSelectionListener = new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
					if (waypointTagFilter.isImportantSelection(selection)) {
						scheduleRefresh();
					}
			}
		};
		getViewSite().getWorkbenchWindow().getSelectionService().addSelectionListener(localWorkbenchSelectionListener);
		
	}

	/**
	 * 
	 */
	private void fillActionBars() {
		waypointActions.fillActionBars(getViewSite().getActionBars());
		tagActions.fillActionBars(getViewSite().getActionBars());
		getViewSite().getActionBars().getToolBarManager().add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * 
	 */
	private void makeActions() {
		tagActions = new TagActionGroup(this);
		waypointActions = new WaypointActionGroup(this);
	}

	/**
	 * @param page
	 */
	private void createWaypointsArea(SashForm form) 
	{
		Group parent = new Group(form, SWT.FLAT);
		parent.setLayout(new FillLayout());
		fWaypointFilter = new WaypointExtensionsFilter();
		
		//waypointTableSash = new SashForm(parent,SWT.VERTICAL);
		waypointTableSash = new Composite(parent,SWT.VERTICAL);
		
		if(waypointTableSash.getVerticalBar()!=null)
		{
			waypointTableSash.getVerticalBar().setEnabled(false);
			waypointTableSash.getVerticalBar().setVisible(false);
		}
		
		waypointTableSash.setLayoutData(new GridData(GridData.FILL_BOTH));
		//composite.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 3;
		layout.marginHeight = 3;
		layout.verticalSpacing = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.horizontalSpacing = 0;
		waypointTableSash.setLayout(layout);
		SimpleSubStringPatternFilter patternFilter = new SimpleSubStringPatternFilter();
		FilteredTable waypointFilteredTable = new FilteredTable(waypointTableSash, SWT.FULL_SELECTION  | SWT.MULTI | SWT.BORDER | SWT.FLAT, patternFilter);
		waypointTableViewer = waypointFilteredTable.getViewer();
		TableViewer tableViewer = waypointTableViewer;
		//fFilteredTable.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		tableViewer.addFilter(fWaypointFilter);
		tableViewer.setContentProvider(new WaypointTableContentProvider());
		tableViewer.setComparator(new WaypointTableColumnSorter());
		tableViewer.addDragSupport(DND.DROP_MOVE|DND.DROP_COPY, new Transfer[]{WaypointTransfer.getInstance()/*, PluginTransfer.getInstance()*/}, new WaypointTableDragListener(tableViewer));
		tableViewer.addDropSupport(DND.DROP_COPY, new Transfer[] {TagNameTransfer.getInstance()}, new WaypointTableViewDropAdapter(tableViewer));
		tableViewer.addDoubleClickListener(new WaypointTableDoubleClickListener());
		tableViewer.addSelectionChangedListener(new WaypointTableSelectionListener());
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setSortDirection(SWT.DOWN);
		for (int i = 0; i < WaypointTableColumnLabelProvider.COLUMN_LABELS.length; i++) {
			TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.FLAT);
			viewerColumn.setLabelProvider(new TagSEADelegatingStyledCellLabelProvider(new WaypointTableColumnLabelProvider(patternFilter, i)));
			TableColumn column = viewerColumn.getColumn();
			final int columnIndex = i;
			column.setMoveable(true);
			column.setText(WaypointTableColumnLabelProvider.COLUMN_LABELS[i]);
			column.setData(WaypointTableColumnLabelProvider.COLUMNS[i]);
			column.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					TableColumn column = (TableColumn) e.widget;
					waypointTableViewer.getTable().setSortColumn(column);
					((WaypointTableColumnSorter)waypointTableViewer.getComparator()).setColumn(columnIndex);
					waypointTableViewer.refresh();
				}
			});
		}
		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(25));
		tableLayout.addColumnData(new ColumnWeightData(25));
		tableLayout.addColumnData(new ColumnWeightData(25));
		tableLayout.addColumnData(new ColumnWeightData(25));
		table.setLayout(tableLayout);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setInput(new Object());
		
		createWaypointTagsArea(waypointTableSash);
		
		MenuManager contextMenu = new MenuManager();
		contextMenu.setRemoveAllWhenShown(true);
		Menu menu = contextMenu.createContextMenu(table);
		contextMenu.addMenuListener(new IMenuListener(){
			public void menuAboutToShow(IMenuManager manager) {
				waypointContextMenuAboutToShow(manager);				
			}
		});
		table.setMenu(menu);
		table.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), waypointActions.getDeleteAction());
				getViewSite().getActionBars().updateActionBars();
			}
			public void focusLost(FocusEvent e) {
			}
		});
		getViewSite().registerContextMenu(contextMenu, tableViewer);
		//waypointTableSash.setWeights(new int[] {10, 1});
	}

	/**
	 * @param manager
	 */
	protected void waypointContextMenuAboutToShow(IMenuManager manager) {
		waypointActions.fillContextMenu(manager);
		manager.add(new Action("Properties..."){
			@Override
			public void run() {
				IWaypoint waypoint = getSelectedWaypoint();
				if (waypoint != null) {
					new WaypointPropertiesDialog(getSite().getShell(), waypoint).open();
				}
			}
		});
		
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void createToggle(Composite parent) 
	{
		HorizontalHandleToggle fToggle = new HorizontalHandleToggle(parent,SWT.NONE);
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.heightHint = 10;
		data.minimumHeight = 10;
		fToggle.setLayoutData(data);
		fToggle.setTooltips("Show tags", "Hide tags");

		fToggle.addSelectionListener(new SelectionListener() 
		{	
			public void widgetSelected(SelectionEvent e) 
			{
				if(e.detail == SWT.CLOSE)
				{
					fTagsArea.setVisible(false);
					GridData data = (GridData)fTagsArea.getLayoutData();
					data.exclude = true;
					fTagsArea.setLayoutData(data);
					waypointTableSash.layout(true,true);
				}
				else if(e.detail == SWT.OPEN)
				{
					fTagsArea.setVisible(true);
					GridData data = (GridData)fTagsArea.getLayoutData();
					data.exclude = false;
					fTagsArea.setLayoutData(data);
					waypointTableSash.layout(true,true);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
	}
	
	/**
	 * @param waypointTableComposite2
	 */
	private void createWaypointTagsArea(Composite parent) 
	{
		// Create the toggle
		createToggle(parent);
		
		// Add a seperator line, it looks odd without this
		Label seperator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		seperator.setLayoutData(data);
		
		// Set up the tags view
		formToolKit = new FormToolkit(parent.getDisplay());
		fTagsArea = new Composite(parent, SWT.FLAT);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 24;
		fTagsArea.setLayoutData(data);
		fTagsArea.setLayout(new GridLayout(2, false));
		//fTagsArea.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		tagsFormText = formToolKit.createFormText(fTagsArea, true);
		data = new GridData(GridData.FILL_BOTH);
		tagsFormText.setLayoutData(data);
		
		tagsFormText.setBackground(parent.getBackground());
		tagsFormText.addHyperlinkListener(new IHyperlinkListener(){
			public void linkActivated(HyperlinkEvent e) {
				String tagName = (String) e.getHref();
				ITag tag = TagSEAPlugin.getTagsModel().getTag(tagName);
				if (tag != null) {
					setSelectedTags(new ITag[] {tag});
				}
			}
			public void linkEntered(HyperlinkEvent e) {}

			public void linkExited(HyperlinkEvent e) {}
			
		});
	
		
//		tagEditButton = new Button(tagsArea, SWT.FLAT | SWT.PUSH);
//		tagEditButton.setText("Edit...");
//		tagEditButton.addSelectionListener(new SelectionListener(){
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//			public void widgetSelected(SelectionEvent e) {
//				TagEditDialog dialog = new TagEditDialog(getViewSite().getShell());
//				if (getSelectedWaypoint() != null) {
//					dialog.setInitialTags(getSelectedWaypoint().getTags());
//					final IWaypoint selectedWaypoint = getSelectedWaypoint();
//					int result = dialog.open();
//					if (result == Dialog.OK) {
//						final String[] tagNames = dialog.getTagNames();
//						TagSEAPlugin.run(new TagSEAOperation("Checking Tag Names..."){
//							@Override
//							public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
//								TreeSet<String> oldTagNames = new TreeSet<String>();
//								TreeSet<String> newTagNames = new TreeSet<String>(Arrays.asList(tagNames));
//								for (ITag tag : selectedWaypoint.getTags()) {
//									oldTagNames.add(tag.getName());
//								}
//								MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
//								for (String oldName : oldTagNames) {
//									if (!newTagNames.contains(oldName)) {
//										ITag tag = TagSEAPlugin.getTagsModel().getTag(oldName);
//										if (tag != null) {
//											status.merge(selectedWaypoint.removeTag(tag).getStatus());
//										}
//									}
//								}
//								for (String newName : newTagNames) {
//									if (!oldTagNames.contains(newName)) {
//										ITag tag = selectedWaypoint.addTag(newName);
//										if (tag == null) {
//											status.merge(new Status(
//												IStatus.WARNING,
//												TagSEAPlugin.PLUGIN_ID,
//												IStatus.WARNING,
//												"Could not add tag " + newName,
//												null
//											));
//										}
//									}
//								}
//								return status;
//							}}, false);
//					}
//				}
//			}
//		});
//		tagEditButton.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		
	}

	/**
	 * @param tags
	 */
	public void setSelectedTags(ITag[] tags) {
		BaseWorkbenchContentProvider contentProvider = (BaseWorkbenchContentProvider) this.tagTreeViewer.getViewer().getContentProvider();
		Object[] elements = contentProvider.getElements(this.tagTreeViewer.getViewer().getInput());
		LinkedList<Object> elementList = new LinkedList<Object>();
		elementList.addAll(Arrays.asList(elements));
		HashSet<ITag> tagsLeft = new HashSet<ITag>();
		tagsLeft.addAll(Arrays.asList(tags));
		List<Object> selectionList = new LinkedList<Object>();
		while (elementList.size() > 0 && tagsLeft.size() > 0) {
			Object element = elementList.removeFirst();
			if (element instanceof TagTreeItem) {
				TagTreeItem tti = (TagTreeItem) element;
				ITag foundTag = null;
				for (ITag tag : tagsLeft) {
					if (tag.equals(tti.getTag())) {
						foundTag = tag;
						break;
					}
				}
				if (foundTag != null) {
					tagsLeft.remove(foundTag);
					selectionList.add(element);
				}
				elementList.addAll(Arrays.asList(tti.getChildren()));
			}
		}
		StructuredSelection selection = new StructuredSelection(selectionList);
		tagTreeViewer.getViewer().setSelection(selection, true);
		cloudViewer.getViewer().setSelection(selection, true);
	}

	/**
	 * @param page2
	 */
	private void createTagsArea(SashForm parent) {
		this.tagSelectionListener = new TagTreeSelectionListener();
		this.tagsTree = new TagsTree(false);
		tagsGroup = new Group(parent, SWT.FLAT);
		tagsGroup.setText("Tags");
		tagsGroup.setLayout(new StackLayout());
		createTagsTree(tagsGroup);
		createTagsCloud(tagsGroup);
		((StackLayout)tagsGroup.getLayout()).topControl = tagTreeViewer;
		tagsGroup.layout();
		tagTreeViewer.getViewer().addSelectionChangedListener(tagSelectionListener);
	}
	/**
	 * @param tagsGroup2
	 */
	private void createTagsCloud(Composite tagsGroup) {
		this.cloudViewer = new CloudTreeViewer(tagsGroup, SWT.FLAT);
		cloudViewer.getViewer().addFilter(waypointTagFilter);
		cloudViewer.setInput(tagsTree);
		cloudViewer.getViewer().getControl().addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), tagActions.getDeleteAction());
				getViewSite().getActionBars().updateActionBars();
			}

			public void focusLost(FocusEvent e) {}
			
		});
	}

	private void createTagsTree(Composite tagsGroup) {
		CachingTreePatternFilter patternFilter = new CachingTreePatternFilter();
		this.tagTreeViewer = new PatternFilteredTree(tagsGroup, SWT.FLAT | SWT.BORDER | SWT.MULTI, patternFilter);
		this.tagTreeViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.tagTreeViewer.getViewer().setContentProvider(new BaseWorkbenchContentProvider());
		this.tagTreeViewer.getViewer().setSorter(new TagsViewSorter());
		tagTreeViewer.getViewer().addFilter(waypointTagFilter);
		tagTreeViewer.getViewer().setInput(tagsTree);
		tagTreeViewer.getFilterControl().addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				if (e.widget instanceof Text) {
					((TagSEAUI)TagSEAPlugin.getDefault().getUI()).getUIEventModel().fireEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_FILTERED, ID, ((Text)e.widget).getText()));
				}
			}
		});
		
		//set up columns
		Tree tree = tagTreeViewer.getViewer().getTree();
		TreeViewerColumn nameViewerColumn = new TreeViewerColumn(tagTreeViewer.getViewer(), SWT.FLAT | SWT.LEFT);
		nameViewerColumn.setLabelProvider(new TagSEADelegatingStyledCellLabelProvider(new TagsViewLabelProvider(patternFilter, 0)));
		TreeViewerColumn sizeViewerColumn = new TreeViewerColumn(tagTreeViewer.getViewer(), SWT.FLAT | SWT.LEFT);
		sizeViewerColumn.setLabelProvider(new TagSEADelegatingStyledCellLabelProvider(new TagsViewLabelProvider(patternFilter, 1)));
		final TreeColumn nameColumn = nameViewerColumn.getColumn();
		final TreeColumn sizeColumn = sizeViewerColumn.getColumn();
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
		tagTreeViewer.getViewer().setColumnProperties(new String[] {"name", "waypointCount"});
		tagTreeViewer.getViewer().addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] {TagTreeItemTransfer.getInstance(), TagNameTransfer.getInstance()}, new TagsDragListener(tagTreeViewer.getViewer()));
		tagTreeViewer.getViewer().addDropSupport(DND.DROP_MOVE, new Transfer[] {TagTreeItemTransfer.getInstance()}, new TagsViewDropListener());
		tagTreeViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		MenuManager contextMenu = new MenuManager("Tags");
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new IMenuListener(){
			public void menuAboutToShow(IMenuManager manager) {
				tagsTreeContextMenuAboutToShow(manager);
			}
		});
		Menu menu = contextMenu.createContextMenu(tagTreeViewer.getViewer().getTree());
		tagTreeViewer.getViewer().getTree().setMenu(menu);
		tree.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), tagActions.getDeleteAction());
				getViewSite().getActionBars().updateActionBars();
			}
			public void focusLost(FocusEvent e) {}
		});
		getViewSite().registerContextMenu(contextMenu, getTagsTreeViewer());
	}
	
	/**
	 * 
	 */
	protected void tagsTreeContextMenuAboutToShow(IMenuManager manager) {
		tagActions.fillContextMenu(manager);
	}

	private void refreshWaypointTagsArea() {
		IWaypoint selected = getSelectedWaypoint();
		//ITagSEAUI ui = TagSEAPlugin.getDefault().getUI();
//		tagEditButton.setEnabled(selected != null && ui.canUIMove(selected));
		if (selected == null) {
			tagsFormText.setText("<form><p>Nothing Selected</p></form>", true, false);
		} else {
			//@tag tagsea.todo : update this to parse the tag names to look for less-than, etc.
			String text = "<form><p>Tags: ";
			for (ITag tag : selected.getTags()) {
				text += "<a href=\"" + tag.getName() +"\">" + tag.getName() + "</a> ";
			}
			text += "</p></form>";
			tagsFormText.setText(text, true, false);
		}
		tagsFormText.redraw();
	}

	@Override
	public void setFocus() {

	}
	
	
	@Override
	public void dispose() {
		getSite().getPage().removePartListener(aboutToCloseListener);
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(localWorkbenchSelectionListener);
		TagSEAPlugin.removeWaypointChangeListener(tagseaModelListener);
		refreshTimer.cancel();
		formToolKit.dispose();
		super.dispose();
	}
	
	public IWaypoint getSelectedWaypoint() {
		ISelection selection = waypointTableViewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.size() > 0) {
				Object selected = ss.getFirstElement();
				if (selected instanceof IAdaptable) {
					IWaypoint wp = (IWaypoint)((IAdaptable)selected).getAdapter(IWaypoint.class);
					return wp;
				}
			}
		}
		return null;
	}
	
	public ITag[] getSelectedTags() {
		ISelection selection = new StructuredSelection();
		if (isViewAsCloud()) {
			selection = cloudViewer.getSelection();
		} else {
			selection = tagTreeViewer.getViewer().getSelection();
		}
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			List<?> selectionList = ss.toList();
			HashSet<ITag> tags = new HashSet<ITag>();
			for (Object o : selectionList) {
				if (o instanceof ITag) {
					tags.add((ITag)o);
				} else if (o instanceof TagTreeItem) {
					TagTreeItem tti = (TagTreeItem) o;
					if (tti.getTag() != null) {
						tags.add(tti.getTag());
					}
					tags.addAll(tti.getChildTags());
				}
			}
			return tags.toArray(new ITag[tags.size()]);
		}
		return new ITag[0];
	}
	
	private void saveState() {
		DialogSettings settings = new DialogSettings(TAGSEA_VIEW_SETTINGS);
		IDialogSettings waypointsSection = settings.addNewSection(WAYPOINTS_SETTINGS);
		
		//waypointsSection.put(WAYPOINT_SORT_COLUMN, ((WaypointTableColumnSorter)waypointTableViewer.getComparator()).getColumn());
		TableColumn sortColumn = waypointTableViewer.getTable().getSortColumn();
		waypointsSection.put(WAYPOINT_SORT_COLUMN, 0);
		if (sortColumn != null) {
			for (int i = 0; i < waypointTableViewer.getTable().getColumnCount(); i++) {
				if (waypointTableViewer.getTable().getColumn(i) == sortColumn) {
					waypointsSection.put(WAYPOINT_SORT_COLUMN, i);
					break;
				}
			}
		}
		int[] columnOrder = waypointTableViewer.getTable().getColumnOrder();
		String[] columnOrderSettings = new String[columnOrder.length];
		for (int i = 0; i < columnOrderSettings.length; i++) {
			columnOrderSettings[i] = "" + columnOrder[i];
		}
		waypointsSection.put(WAYPOINT_COLUMN_ORDER, columnOrderSettings);
		TableColumn[] columns = waypointTableViewer.getTable().getColumns();
		String[] columnSizes = new String[columns.length];
		for (int i = 0; i < columnSizes.length; i++) {
			columnSizes[i] = columns[i].getWidth()+"";
		}
		waypointsSection.put(WAYPOINT_COLUMN_SIZES, columnSizes);
		IDialogSettings tagsSection = settings.addNewSection(TAGS_SETTINGS);
		TagsTree tagsInput = (TagsTree) tagTreeViewer.getViewer().getInput();
		tagsSection.put(TAGS_FLAT, tagsInput.isFlat());
		
		TagSEAPlugin.getDefault().getDialogSettings().addSection(settings);
	}
	
	private void restoreState() {
		IDialogSettings settings = TagSEAPlugin.getDefault().getDialogSettings().getSection(TAGSEA_VIEW_SETTINGS);
		if (settings == null) {
			return;
		}
		IDialogSettings waypointsSection = settings.getSection(WAYPOINTS_SETTINGS);
		int column = waypointsSection.getInt(WAYPOINT_SORT_COLUMN);
		String[] columnOrderStrings = waypointsSection.getArray(WAYPOINT_COLUMN_ORDER);
		int[] columnOrder = new int[columnOrderStrings.length];
		for (int i = 0; i < columnOrder.length; i++) {
			columnOrder[i] = Integer.parseInt(columnOrderStrings[i]);
		}
		String[] columnSizesStrings = waypointsSection.getArray(WAYPOINT_COLUMN_SIZES);
		int[] columnSizes = new int[columnSizesStrings.length];
		for (int i = 0; i < columnSizesStrings.length; i++) {
			columnSizes[i] = Integer.parseInt(columnSizesStrings[i]);
		}
		try {
			TableColumn sortColumn = waypointTableViewer.getTable().getColumn(column);
			waypointTableViewer.getTable().setSortColumn(sortColumn);
		} catch (IllegalArgumentException e) {}
		waypointTableViewer.getTable().setColumnOrder(columnOrder);
		TableColumn[] columns = waypointTableViewer.getTable().getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].setWidth(columnSizes[i]);
		}
		
		IDialogSettings tagsSection = settings.getSection(TAGS_SETTINGS);
		
		boolean flat = tagsSection.getBoolean(TAGS_FLAT);
		tagsTree.setFlat(flat);
	}
	
	TreeViewer getTagsTreeViewer() {
		return tagTreeViewer.getViewer();
	}
	
	TableViewer getWaypointsTableViewer() {
		return waypointTableViewer;
	}
	
	public void setViewAsHierachy(boolean hierarchy) {
		if (hierarchy == this.tagsTree.isFlat()) {
			this.tagsTree.setFlat(!hierarchy);
			this.tagTreeViewer.getViewer().refresh();
			this.cloudViewer.refresh();
		}
	}

	/**
	 * @return
	 */
	public boolean isViewAsHierarchy() {
		return !this.tagsTree.isFlat();
	}

	/**
	 * @param checked
	 */
	void setViewAsCloud(boolean asCloud) {
		StackLayout stackLayout = (StackLayout) tagsGroup.getLayout();
		if (asCloud) {		
			if (stackLayout.topControl != cloudViewer) {
				stackLayout.topControl = cloudViewer;
				IStructuredSelection selection = (IStructuredSelection) tagTreeViewer.getViewer().getSelection();
				TagTreeItem newSelection = null;
				tagTreeViewer.getViewer().removeSelectionChangedListener(tagSelectionListener);
				cloudViewer.getViewer().addSelectionChangedListener(tagSelectionListener);
				tagSelectionListener.selectionChanged(new SelectionChangedEvent(cloudViewer.getViewer(), cloudViewer.getSelection()));
				if (selection.size() > 0) {
					newSelection = (TagTreeItem) selection.getFirstElement();
					if (newSelection.getParent() != null) {
						cloudViewer.getViewer().setInput(newSelection.getParent());
					} else {
						cloudViewer.getViewer().setInput(tagsTree);
					}
					cloudViewer.refresh();
					cloudViewer.getViewer().setSelection(selection);
					
				}
			}
		} else {
			if (stackLayout.topControl != tagTreeViewer) {
				stackLayout.topControl = tagTreeViewer;
				TagTreeItem newSelection = (TagTreeItem) cloudViewer.getViewer().getInput();
				tagTreeViewer.getViewer().addSelectionChangedListener(tagSelectionListener);
				cloudViewer.getViewer().removeSelectionChangedListener(tagSelectionListener);
				tagSelectionListener.selectionChanged(new SelectionChangedEvent(tagTreeViewer.getViewer(), tagTreeViewer.getViewer().getSelection()));
				if (newSelection != null) {
					tagTreeViewer.getViewer().reveal(newSelection);
				}
			}
		}
		tagsGroup.layout();
	}

	/**
	 * @return
	 */
	public boolean isViewAsCloud() {
		return ((StackLayout)tagsGroup.getLayout()).topControl == cloudViewer;
	}

}
