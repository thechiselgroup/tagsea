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

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;

import ca.uvic.cs.tagsea.ITagseaImages;
import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.actions.GoToTagAction;
import ca.uvic.cs.tagsea.core.Waypoint;
import ca.uvic.cs.tagsea.core.WaypointProvider;
import ca.uvic.cs.tagsea.preferences.TagSEAPreferences;



/**
 * The composite which contains the Waypoints TableViewer and header label.
 * 
 * @author Chris Callendar, Chris Bennett, mdesmond
 */
public class WaypointsComposite extends BaseTagsViewComposite {

	private static final int WIDTH = 800;
	
	public static final int WIDTH_TAG_NAME = 100;
	public static final int WIDTH_JAVA_ELEMENT = 125;
	public static final int WIDTH_RESOURCE = 250;
	public static final int WIDTH_COMMENT = 200;
	public static final int WIDTH_AUTHOR = 75;
	public static final int WIDTH_DATE = 60;
	public static final int WIDTH_LINE_NUMBER = 50;
	
	public static final int INDEX_TAG_NAME = 0;
	public static final int INDEX_JAVA_ELEMENT = 1;
	public static final int INDEX_RESOURCE = 2;
	public static final int INDEX_COMMENT = 3;
	public static final int INDEX_AUTHOR = 4;
	public static final int INDEX_DATE = 5;
	public static final int INDEX_LINE_NUMBER = 6;
	
	private TableViewer waypointsTableViewer;
	private WaypointProvider waypointProvider;
	private WaypointSorter waypointSorter;
	private ToggleComposite toggleRoutesComposite;
	
	private GoToTagAction fToolBarGotoTagAction;
	private ToolBar toolBar;
	private ToolBarManager barManager;
	private static Action backAction;
	private static Action forwardAction;
	private ImageDescriptor imgDescriptor;	
	private final int UP = -1;
	private final int DOWN = 1;

	private Action selectAllAction;
	
	public WaypointsComposite(Composite parent, int style) {
		super(parent, style);
		
		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		this.setLayout(layout);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = WIDTH;
		this.setLayoutData(data);
		
		Label header = new Label(this, SWT.LEFT);
		header.setText("Waypoints");
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		header.setLayoutData(data);	
		
		//toolbar
		toolBar = new ToolBar(this, SWT.FLAT | SWT.LEFT);
		GridData toolBarData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		toolBar.setData(toolBarData);
		barManager = new ToolBarManager(toolBar);
		fillToolbar();
		
		//TOGGLER
		//createToggleRoutesButton();

		initTableViewer();
		makeActions();
	}

	private void fillToolbar() 
	{
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
		
		barManager.add(backAction);
		barManager.add(forwardAction);
		barManager.update(false);
	}

	/**
	 * Returns the waypointsTableViewer
	 * @return WaypointsComposite
	 */
	public TableViewer getWaypointsTableViewer() {
		return waypointsTableViewer;
	}
	
	
	/**
	 * Returns the waypoint content/label provider.
	 * @return WaypointProvider
	 */
	public WaypointProvider getWaypointProvider() {
		return waypointProvider;
	}
	
	
	/** Creates a vertical button that toggles the visibility of the routes tree. */
//	private void createToggleRoutesButton() {
//		toggleRoutesComposite = new ToggleComposite(this, SWT.VERTICAL);
//		
//		GridData data = new GridData(SWT.RIGHT, SWT.FILL, false, true);
//		data.verticalSpan = 2;
//		data.widthHint = 12;
//		data.minimumWidth = 12;
//		toggleRoutesComposite.setLayoutData(data);
//		toggleRoutesComposite.setTooltips("Show routes", "Hide routes");
//
//		
//		final SashForm sash = (SashForm)this.getParent();
//		toggleRoutesComposite.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				if (e.detail == SWT.RIGHT) {
//					sash.setMaximizedControl(WaypointsComposite.this);
//				} else if (e.detail == SWT.LEFT) {
//					sash.setMaximizedControl(null);
//				}
//			}
//		});			
//		
//	}	
	
	/**
	 * Initializes the table viewer - sets the content and label providers.
	 */
	private void initTableViewer() {
		
		final Table table = createWaypointTable(this);
		waypointsTableViewer = new TableViewer(table);
		waypointSorter = new WaypointSorter(waypointsTableViewer);
		
		//waypointsTableViewer.addSelectionChangedListener(new TagSelectionListener());
		waypointProvider = new WaypointProvider(waypointsTableViewer);
		waypointsTableViewer.setContentProvider(waypointProvider);
		waypointsTableViewer.setLabelProvider(waypointProvider);
		waypointsTableViewer.setSorter(waypointSorter);
		//waypointsTableViewer.addFilter(new WaypointFilter());
		waypointsTableViewer.setInput(new Object());
		
		// add the table column selection listeners
		for (TableColumn column : waypointsTableViewer.getTable().getColumns()) {
			column.addSelectionListener(waypointSorter);
		}
		 
//		int ops = DND.DROP_COPY | DND.DROP_MOVE;
//		Transfer[] transfers = new Transfer[] { WaypointTransfer.getInstance()};
//		waypointsTableViewer.addDragSupport(ops, transfers, new WaypointDragListener(waypointsTableViewer));
	}
	
	/**
	 * Convenience method for creating a TableColumn.
	 * Make sure waypointSorter has been initialized.
	 * @param table the table to add the column to
	 * @param index the column index 
	 * @param text the column header
	 * @param width the column width
	 * @return TableColumn
	 */
	protected TableColumn createTableColumn(Table table, int index, String text, int width) {
		TableColumn column = new TableColumn(table, SWT.LEFT, index);
		column.setText(text);
		column.setWidth(width);
		return column;
	}
	
	/**
	 * Creates a table for the waypoints, initializing the table columns.
	 * @param parent the parent composite to add the table to
	 * @return Table
	 */
	private Table createWaypointTable(Composite parent) {		
		Table table = new Table(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
		
		table.setLayoutData(data);

		createTableColumn(table, INDEX_TAG_NAME, "Tag", WIDTH_TAG_NAME);
		createTableColumn(table, INDEX_JAVA_ELEMENT, "Java Element", WIDTH_JAVA_ELEMENT);
		createTableColumn(table, INDEX_RESOURCE, "Resource", WIDTH_RESOURCE);
		createTableColumn(table, INDEX_COMMENT, "Comment", WIDTH_COMMENT);
		createTableColumn(table, INDEX_AUTHOR, "Author", WIDTH_AUTHOR);
		createTableColumn(table, INDEX_DATE, "Date", WIDTH_DATE);
		createTableColumn(table, INDEX_LINE_NUMBER, "Line #", WIDTH_LINE_NUMBER);
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		return table;
	}

	private void makeActions() 
	{
		selectAllAction = new Action("Select All") {
			public void run() {
				Object[] elements = waypointProvider.getElements(null);
				waypointsTableViewer.setSelection(new StructuredSelection(elements));
				waypointsTableViewer.getControl().setFocus();
			}
		};

	}
	
	/**
	 * @author mdesmond
	 * @param direction
	 */
	private void goToWaypoint(int direction) 
	{
		int selected = waypointsTableViewer.getTable().getSelectionIndex();

		// nothing selected
		if(selected == -1)
			waypointsTableViewer.getTable().setSelection(0);
		else
		{
			switch(direction)
			{
				case UP:
					
					if((selected -1) >= 0 )
						waypointsTableViewer.getTable().setSelection(--selected);
					else
						waypointsTableViewer.getTable().setSelection(waypointsTableViewer.getTable().getItemCount()-1);
				break;

			case DOWN:
				
					if((selected + 1) <= waypointsTableViewer.getTable().getItemCount()-1)
						waypointsTableViewer.getTable().setSelection(++selected);
					else
						waypointsTableViewer.getTable().setSelection(0);
				break;

			default:
				break;
			}
		}
		
		if(fToolBarGotoTagAction != null)
			fToolBarGotoTagAction.run();
	}
	
	public MenuManager createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				WaypointsComposite.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(waypointsTableViewer.getControl());
		waypointsTableViewer.getControl().setMenu(menu);
		return menuMgr;
	}

	private void fillContextMenu(IMenuManager manager) {
		boolean enabled = (waypointsTableViewer.getTable().getItemCount() > 0);
		selectAllAction.setEnabled(enabled);
		
		manager.add(selectAllAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}	


	/**
	 * Adds a double click listener to the waypoints table viewer which will open an editor
	 * showing the double clicked waypoint.
	 * @param page the active page
	 */
	public void hookDoubleClickAction(IWorkbenchPage page) {
		waypointsTableViewer.addDoubleClickListener(new GoToTagAction(waypointsTableViewer, page));		
		fToolBarGotoTagAction = new GoToTagAction(waypointsTableViewer, page);
	}

	/**
	 * Loads the preferences - checks if the routes should be hidden.
	 * Also adjusts the column widths.
	 */
	public void loadPreferences() {
//		// hide the routes if the preferences say so
//		if (!TagSEAPreferences.isShowRoutes()) {
//			toggleRoutesComposite.toggle();
//		}
		
		// load the preferred column widths
		Table table = waypointsTableViewer.getTable();
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(i).setWidth(TagSEAPreferences.getColumnWidth(i));
		}		
	}

	/**
	 * Saves the column widths.
	 */
	public void savePreferences() {
		Table table = waypointsTableViewer.getTable();
		// save the column widths
		int[] widths = new int[table.getColumnCount()];
		for (int i = 0; i < widths.length; i++) {
			widths[i] = table.getColumn(i).getWidth();
		}
		TagSEAPreferences.setColumnWidths(widths);
	}


	/**
	 * Gets the selected waypoints or returns empty array.
	 * @return Waypoint[] the selected waypoints or an empty array
	 */
	public Waypoint[] getSelectedWaypoints() {
		Waypoint[] wps = new Waypoint[0];
		IStructuredSelection sel = (IStructuredSelection) waypointsTableViewer.getSelection();
		if (!sel.isEmpty()) {
			Object[] array = sel.toArray();
			ArrayList<Waypoint> wpList = new ArrayList<Waypoint>(array.length);
			for (Object obj : array) {
				if (obj instanceof Waypoint) {
					wpList.add((Waypoint)obj);
				}
			}
			wps = new Waypoint[wpList.size()];
			wps = (Waypoint[]) wpList.toArray(wps);
		}
		return wps;
	}	

}
