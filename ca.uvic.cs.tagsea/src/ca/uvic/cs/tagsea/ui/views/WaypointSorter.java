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

import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_AUTHOR;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_COMMENT;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_DATE;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_JAVA_ELEMENT;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_LINE_NUMBER;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_RESOURCE;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ca.uvic.cs.tagsea.core.Waypoint;

/**
 * Sorts a table column. If the same column is clicked, the items are inverted.
 * 
 * There are two ways to sort a table: 1) Work directly on the text for each column in every TableItem 2) Work with the
 * JFace TableViewer data objects (e.g. Waypoints)
 * 
 * We decided it was simpler to work directly on the TableItems because every column just displays text, so it is easier
 * than trying to compare java elements, line numbers etc.
 * 
 * @tag WaypointsTableSorter : sorts the waypoints use string comparison
 * 
 * @author Chris Callendar, Chris Bennett
 */
public class WaypointSorter extends ViewerSorter implements SelectionListener {

	private TableViewer tableViewer;
	private int lastColumn = -1;
	private boolean ascending = true;

	public WaypointSorter(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		TableColumn column = (TableColumn) e.widget;
		Table table = tableViewer.getTable();
		int columnIndex = table.indexOf(column);

		// check if the same column was clicked, if so invert the items
		if (columnIndex == lastColumn) {
			ascending = !ascending;
		}
		lastColumn = columnIndex;
		table.setSortColumn(column);
		
		// this will cause the table to be sorted
		tableViewer.refresh();		
	}
	
	/**
	 * Compares the Waypoint objects based on which column is selected.
	 * Defaults to the waypoint name.
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int compareVal = 0;
		if (e1 instanceof Waypoint && e2 instanceof Waypoint) {
			Waypoint wp1, wp2;
			
			if (ascending) {
				wp1 = (Waypoint) e1;
				wp2 = (Waypoint) e2;
			} else {
				wp1 = (Waypoint) e2;
				wp2 = (Waypoint) e1;				
			}
			        
			switch (lastColumn) {
				case INDEX_JAVA_ELEMENT :
					compareVal = wp1.getJavaElementName().compareTo(wp2.getJavaElementName());
					break;
				case INDEX_RESOURCE :
					IMarker marker1 = wp1.getMarker();
					IMarker marker2 = wp2.getMarker();
					if (marker1 != null && marker2 != null) {
						compareVal = marker1.getResource().getName().compareTo(marker2.getResource().getName());
					}
					break;
				case INDEX_COMMENT :
					compareVal = wp1.getComment().compareTo(wp2.getComment());
					break;
				case INDEX_AUTHOR :
					compareVal = wp1.getAuthor().compareTo(wp2.getAuthor());
					break;
				case INDEX_DATE :
					compareVal = wp1.getDate().compareTo(wp2.getDate());
					break;
				case INDEX_LINE_NUMBER :
					compareVal = wp1.getLineNumber() - wp2.getLineNumber();
					break;
				case WaypointsComposite.INDEX_TAG_NAME :
					// fall through
				default :
					// sort by keyword
					compareVal = wp1.getKeyword().compareTo(wp2.getKeyword());
			}
			
		}
		
		return compareVal;
	}

}
