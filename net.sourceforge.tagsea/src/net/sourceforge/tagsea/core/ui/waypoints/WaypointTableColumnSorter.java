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
package net.sourceforge.tagsea.core.ui.waypoints;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Sorts waypoints in a table viewer by one of Message, Author, or Date.
 * @author Del Myers
 *
 */
public class WaypointTableColumnSorter extends ViewerComparator {

	private int column;

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		IWaypoint w1 = getWaypoint(e1);
		IWaypoint w2 = getWaypoint(e2);
		if (w2 == null) {
			if (w1 == null) {
				return 0;
			}
			return 1;
		} else if (w1 == null) {
			if (w2 == null) {
				return 0;
			}
			return -1;
		}
		switch (getColumn()) {
		case WaypointTableColumnLabelProvider.MESSAGE_COLUMN:
			return compareWithNull(w1.getText(), w2.getText());
		case WaypointTableColumnLabelProvider.AUTHOR_COLUMN:
			return compareWithNull(w1.getAuthor(), w2.getAuthor());
		case WaypointTableColumnLabelProvider.DATE_COLUMN:
			return compareWithNull(w1.getDate(), w2.getDate());
		}
		
		return 0;
	}

	/**
	 * @return
	 */
	public int getColumn() {
		return this.column;
	}
	
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * @param text
	 * @param c2
	 * @return
	 */
	private <T extends Comparable<? super T>> int compareWithNull(T c1, T c2) {
		if (c2 == null) {
			if (c1 == null) {
				return 0;
			}
			return 1;
		} else if (c1 == null) {
			if (c2 == null) {
				return 0;
			}
			return -1;
		}
		return c1.compareTo(c2);
	}

	/**
	 * @param e1
	 * @return
	 */
	private IWaypoint getWaypoint(Object e1) {
		if (e1 instanceof IAdaptable) {
			return (IWaypoint)((IAdaptable)e1).getAdapter(IWaypoint.class);
		}
		return null;
	}
	
}
