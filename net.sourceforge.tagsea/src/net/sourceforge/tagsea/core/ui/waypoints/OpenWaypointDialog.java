/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *     IBM Research, Cambridge, MA
 *******************************************************************************/

package net.sourceforge.tagsea.core.ui.waypoints;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.internal.TagSEADelegatingStyledCellLabelProvider;
import net.sourceforge.tagsea.core.ui.WaypointTransfer;
import net.sourceforge.tagsea.core.ui.adapters.StructuredContentProviderAdapter;
import net.sourceforge.tagsea.core.ui.internal.views.ExpressionFilter;
import net.sourceforge.tagsea.core.ui.internal.views.ExpressionFilteredTable;
import net.sourceforge.tagsea.core.ui.internal.views.ExpressionPatternFilter;
import net.sourceforge.tagsea.core.ui.internal.views.TagExpressionFilter;
import net.sourceforge.tagsea.core.ui.internal.waypoints.WaypointExtensionsFilter;
import net.sourceforge.tagsea.core.ui.internal.waypoints.WaypointTableDoubleClickListener;
import net.sourceforge.tagsea.core.ui.internal.waypoints.WaypointTableDragListener;
import net.sourceforge.tagsea.core.ui.internal.waypoints.WaypointTableViewDropAdapter;
import net.sourceforge.tagsea.core.ui.tags.TagNameTransfer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * dialog similar to "Open Type" and "Open Resource" - but works on waypoints.
 * uses the same filtered table in the waypoints view.  supports "@<tag>" to show waypoints by tag.
 * hot-key configured to "ctrl-shift-@" (i.e. ctrl-shift-2)
 * 
 * 
 * 
 * @author Li-Te Cheng, CUE, IBM Research
 */

public class OpenWaypointDialog extends Dialog implements ISelectionChangedListener {

	private IWaypoint[] selectedWaypoints;
	
	class WaypointDialogContentProvider extends StructuredContentProviderAdapter
	{
		public Object[] getElements(Object inputElement) {
			int i = 0;
			IWaypoint[] waypoints = getAllWaypoints();
			WaypointWrapper[] wrapperArray = new WaypointWrapper[waypoints.length];
			for (IWaypoint waypoint : waypoints) {
				wrapperArray[i++] = new WaypointWrapper(waypoint);
			}
			return wrapperArray;
		}
		
		private IWaypoint[] getAllWaypoints()
		{
			return TagSEAPlugin.getWaypointsModel().getAllWaypoints();
		}
	};
	
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
		composite.setLayout(layout);
		ExpressionPatternFilter patternFilter = new ExpressionPatternFilter();
		patternFilter.addExpressionFilter(new ExpressionFilter());
		patternFilter.addExpressionFilter(new TagExpressionFilter());
		ExpressionFilteredTable fFilteredTable = new ExpressionFilteredTable(composite,SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER, patternFilter);
		
		TableViewer fWaypointViewer = fFilteredTable.getViewer();
		WaypointExtensionsFilter fWaypointFilter = new WaypointExtensionsFilter();
		fWaypointViewer.addFilter(fWaypointFilter);
		fWaypointViewer.setContentProvider(new WaypointDialogContentProvider());
		fWaypointViewer.setLabelProvider(new TagSEADelegatingStyledCellLabelProvider(new WaypointTableColumnLabelProvider(patternFilter)));
		fWaypointViewer.setSorter(new WaypointTableSorter());
		fWaypointViewer.addDragSupport(DND.DROP_MOVE|DND.DROP_COPY, new Transfer[]{WaypointTransfer.getInstance()/*, PluginTransfer.getInstance()*/}, new WaypointTableDragListener(fWaypointViewer));
		fWaypointViewer.addDropSupport(DND.DROP_COPY, new Transfer[] {TagNameTransfer.getInstance()}, new WaypointTableViewDropAdapter(fWaypointViewer));
		//fWaypointViewer.addSelectionChangedListener(new WaypointTableSelectionChangedListener());
		fWaypointViewer.addDoubleClickListener(new WaypointTableDoubleClickListener());
		fWaypointViewer.setSorter(new ViewerSorter());
		
		
		Table table = fWaypointViewer.getTable();
		table.setLinesVisible(true);
		fWaypointViewer.setInput(new Object());
		fWaypointViewer.addSelectionChangedListener(this);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		
		IStructuredSelection structuredSelection = (IStructuredSelection)event.getSelection();
		Object[] selection = structuredSelection.toArray();

		if(selection.length > 0)
		{
			selectedWaypoints = new IWaypoint[selection.length];
			
			for(int i=0; i<selection.length; i++)
			{
				if ( selection[i] instanceof IWaypoint )
					selectedWaypoints[i] = (IWaypoint) selection[i];
				else if ( selection[i] instanceof WaypointWrapper )
				{
					selectedWaypoints[i] = (IWaypoint) ((WaypointWrapper) (selection[i])).getAdapter(IWaypoint.class);
				}
			}
		}
		else
		{
			selectedWaypoints = null;
		}
	}

}
