/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tagging.core.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.controls.ExpressionFilteredTable;
import com.ibm.research.tagging.core.ui.controls.ExpressionPatternFilter;
import com.ibm.research.tagging.core.ui.dnd.WaypointTableDragListener;
import com.ibm.research.tagging.core.ui.dnd.WaypointTransfer;
import com.ibm.research.tagging.core.ui.waypoints.WaypointTableContentProvider;
import com.ibm.research.tagging.core.ui.waypoints.WaypointTableDoubleClickListener;
import com.ibm.research.tagging.core.ui.waypoints.WaypointTableLabelProvider;
import com.ibm.research.tagging.core.ui.waypoints.WaypointTableSelectionChangedListener;
import com.ibm.research.tagging.core.ui.waypoints.WaypointTableSorter;

/**
 * dialog similar to "Open Type" and "Open Resource" - but works on waypoints.
 * uses the same filtered table in the waypoints view.  supports "@<tag>" to show waypoints by tag.
 * hot-key configured to "ctrl-shift-@" (i.e. ctrl-shift-2)
 * 
 * @tag todo openwaypointdialog : very basic implementation - may want to provide some contextual info (e.g. tag info) via tooltips
 * 
 * @author Li-Te Cheng, CUE, IBM Research
 */

public class OpenWaypointDialog extends Dialog implements ISelectionChangedListener {

	private IWaypoint[] selectedWaypoints;
	
	public OpenWaypointDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Open Waypoint");
		
		Composite composite = (Composite) super.createDialogArea(parent);
		createWaypointTableViewer(parent);
		
		selectedWaypoints = null;
		
		return composite;
	}
	
	public IWaypoint[] getWaypoints() {
		return selectedWaypoints;
	}

	private void createWaypointTableViewer(Composite parent) 
	{			
		Composite composite = new Composite(parent,SWT.NONE);
		GridData compositeLayout = new GridData(GridData.FILL_BOTH);
		compositeLayout.heightHint = 240;
		composite.setLayoutData(compositeLayout); 
		composite.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 3;
		layout.marginHeight = 3;
		layout.verticalSpacing = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		
		ExpressionFilteredTable fFilteredTable = new ExpressionFilteredTable(composite,SWT.MULTI | SWT.V_SCROLL, new ExpressionPatternFilter());
		fFilteredTable.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		TableViewer fWaypointViewer = fFilteredTable.getViewer();
		fWaypointViewer.setContentProvider(new WaypointTableContentProvider());
		fWaypointViewer.setLabelProvider(new WaypointTableLabelProvider());
		fWaypointViewer.setSorter(new WaypointTableSorter());
		fWaypointViewer.addDragSupport(DND.DROP_MOVE|DND.DROP_COPY, new Transfer[]{WaypointTransfer.getInstance()/*, PluginTransfer.getInstance()*/}, new WaypointTableDragListener(fWaypointViewer));
		fWaypointViewer.addSelectionChangedListener(new WaypointTableSelectionChangedListener());
		fWaypointViewer.addSelectionChangedListener(this);
		fWaypointViewer.addDoubleClickListener(new WaypointTableDoubleClickListener());
		
		Table table = fWaypointViewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		
		final TableColumn fNameColumn = new TableColumn(table,SWT.LEFT,0);
		fNameColumn.setMoveable(false);
		fNameColumn.setWidth(240);
		
		final TableColumn fDescriptionColumn = new TableColumn(table,SWT.LEFT,1);
		fDescriptionColumn.setMoveable(false);
		fDescriptionColumn.setWidth(640);
		
		// auto-resize columns on any change to the table
		table.addPaintListener(new PaintListener() 
		{
			public void paintControl(PaintEvent e) 
			{
				Table table = (Table) e.widget;
				Rectangle clientSize = table.getClientArea();
				int newWidth = clientSize.width - fNameColumn.getWidth();
				
				// this check prevents recursion
				if ( fDescriptionColumn.getWidth()!=newWidth )
					fDescriptionColumn.setWidth(newWidth);
			}
		});
		
		fWaypointViewer.setInput(new Object());
	}

	public void selectionChanged(SelectionChangedEvent event) {
		// @tag hack openwaypoint : using selectionChanged instead of getSelection... getSelection returning empty structured selection objects!
		
		IStructuredSelection structuredSelection = (IStructuredSelection)event.getSelection();
		Object[] selection = structuredSelection.toArray();
		
		if(selection.length > 0)
		{
			selectedWaypoints = new IWaypoint[selection.length];
				
			for(int i=0; i<selection.length; i++)
				selectedWaypoints[i] = (IWaypoint) selection[i];
		}
		else
		{
			selectedWaypoints = null;
		}
	}

}
