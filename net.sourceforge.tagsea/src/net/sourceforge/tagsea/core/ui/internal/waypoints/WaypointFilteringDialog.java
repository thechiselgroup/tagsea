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
package net.sourceforge.tagsea.core.ui.internal.waypoints;

import java.util.HashMap;
import java.util.TreeSet;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.internal.ITagSEAPreferences;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.core.ui.IWaypointFilter;
import net.sourceforge.tagsea.core.ui.IWaypointFilterUI;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUI;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

/**
 * Dialog for displaying waypoint filtering information.
 * @author Del Myers
 */

public class WaypointFilteringDialog extends Dialog {

	private CheckboxTableViewer typeViewer;
	private Label noSelectionControl;
	private HashMap<String, Control> filterControlMap;
	private Label defaultControl;
	protected AbstractWaypointDelegate selectedDelegate;
	private Composite advancedArea;
	private TreeSet<String> dirtyTypeSet;

	/**
	 * @param parentShell
	 */
	public WaypointFilteringDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		page.setLayout(new GridLayout(2, false));
		createTableArea(page);
		createAdvancedArea(page);
		return page;
	}

	/**
	 * @param page
	 */
	private void createAdvancedArea(Composite page) {
		Group advanced = new Group(page, SWT.FLAT);
		advanced.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		advanced.setLayout(new GridLayout());
		advanced.setText("Filter Options");
		advancedArea = new Composite(advanced, SWT.FLAT);
		advancedArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		advancedArea.setLayout(new StackLayout());
		dirtyTypeSet = new TreeSet<String>();
		noSelectionControl = new Label(advancedArea, SWT.NONE);
		noSelectionControl.setText("Select a type to see advanced filtering options");
		defaultControl = new Label(advancedArea, SWT.NONE);
		defaultControl.setText("No additional filtering options available");
		filterControlMap = new HashMap<String, Control>();
		for (AbstractWaypointDelegate delegate : TagSEAPlugin.getDefault().getWaypointDelegates()) {
			IWaypointFilter filter = ((TagSEAUI)TagSEAPlugin.getDefault().getUI()).getFilter(delegate.getType());
			if (filter != null) {
				createControlForFilter(advancedArea, filter, delegate.getType());
			}
		}
		Button applyButton = new Button(advanced, SWT.PUSH);
		applyButton.setText("Apply");
		applyButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedDelegate != null) {
					IWaypointFilterUI ui = ((TagSEAUI)TagSEAPlugin.getDefault().getUI()).getFilterUI(selectedDelegate.getType());
					if (ui != null) {
						ui.applyToFilter();
						dirtyTypeSet.remove(selectedDelegate.getType());
					}
				}
			}
		});
		GridData data = new GridData(SWT.END, SWT.FILL, false, false);
		applyButton.setLayoutData(data);
		((StackLayout)advancedArea.getLayout()).topControl = noSelectionControl;
		advancedArea.layout();
		
	}
	

	/**
	 * @param area 
	 * @param filter
	 * @param type 
	 */
	private void createControlForFilter(Composite parent, IWaypointFilter filter, String type) {
		IWaypointFilterUI filterUI = ((TagSEAUI)TagSEAPlugin.getDefault().getUI()).getFilterUI(type);
		if (filterUI == null) return;
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout());
		filterUI.createControl(page);
		filterControlMap.put(type, page);
		filterUI.initialize(filter);
		
	}

	/**
	 * @param page
	 */
	private void createTableArea(Composite page) {
		Group area = new Group(page, SWT.FLAT);
		area.setText("Visible Types");
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		area.setLayout(new GridLayout(2, false));
		typeViewer = CheckboxTableViewer.newCheckList(area, SWT.FLAT | SWT.BORDER);
		typeViewer.setContentProvider(new ArrayContentProvider());
		typeViewer.setLabelProvider(new ITableLabelProvider(){
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof AbstractWaypointDelegate) {
					return ((AbstractWaypointDelegate)element).getName();
				}
				return "";
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return true;
			}

			public void removeListener(ILabelProviderListener listener) {
			}
			
		});
		typeViewer.setInput(TagSEAPlugin.getDefault().getWaypointDelegates());
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) event.getSelection();
					if (ss.size() == 1) {
						selectedDelegate = (AbstractWaypointDelegate)ss.getFirstElement();
						dirtyTypeSet.add(selectedDelegate.getType());
						Control filterControl = filterControlMap.get(selectedDelegate.getType());
						if (filterControl != null) {
							((StackLayout)advancedArea.getLayout()).topControl = filterControl;	
						} else {
							((StackLayout)advancedArea.getLayout()).topControl = defaultControl;
						}
					} else {
						((StackLayout)advancedArea.getLayout()).topControl = noSelectionControl;
					}
				}
				advancedArea.layout();
			}
			
		});
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
		typeViewer.getControl().setLayoutData(data);
		Button selectAllButton = new Button(area, SWT.PUSH);
		selectAllButton.setText("Check All");
		selectAllButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				typeViewer.setAllChecked(true);
			}
		});
		data = new GridData(SWT.END, SWT.END, false, false);
		data.horizontalAlignment = SWT.END;
		selectAllButton.setLayoutData(data);
		
		Button selectNoneButton = new Button(area, SWT.PUSH);
		data = new GridData(SWT.END, SWT.END, false, false);
		data.horizontalAlignment = SWT.END;
		selectNoneButton.setLayoutData(data);
		selectNoneButton.setText("Check None");
		selectNoneButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				typeViewer.setAllChecked(false);
			}
		});
		initializeTable();
		
	}

	/**
	 * Initialize the check-state in the table viewer according to the preferences.
	 */
	private void initializeTable() {
		typeViewer.setAllChecked(true);
		IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
		String filtered = store.getString(ITagSEAPreferences.FILTERED_WAYPOINT_TYPES);
		String[] types = filtered.split("[,]");
		for (String type : types) {
			AbstractWaypointDelegate delegate = TagSEAPlugin.getDefault().getWaypointDelegate(type);
			if (delegate != null) {
				typeViewer.setChecked(delegate, false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		for (String type : dirtyTypeSet) {
			IWaypointFilterUI filterUI = ((TagSEAUI)TagSEAPlugin.getDefault().getUI()).getFilterUI(type);
			if (filterUI != null) {
				filterUI.applyToFilter();
			}
		}
		dirtyTypeSet.clear();
		String preferenceString = "";
		for (TableItem item :typeViewer.getTable().getItems()) {
			if (!item.getChecked()) {
				AbstractWaypointDelegate delegate = (AbstractWaypointDelegate) item.getData();
				preferenceString += delegate.getType() + ",";
			}
		}
		TagSEAPlugin.getDefault().getPreferenceStore().setValue(ITagSEAPreferences.FILTERED_WAYPOINT_TYPES, preferenceString);
		super.okPressed();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setImage(TagSEAPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_FILTER));
		newShell.setText("TagSEA Filters");
	}

}
