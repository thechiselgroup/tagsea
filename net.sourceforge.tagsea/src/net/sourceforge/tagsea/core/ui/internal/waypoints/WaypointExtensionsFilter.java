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

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.IWaypointFilter;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUI;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters on waypoints according to what types are available according to the preferences, and according to
 * filters that have been contributed for waypoint types.
 * @author Del Myers
 */

public class WaypointExtensionsFilter extends ViewerFilter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IAdaptable) {
			IWaypoint wp = (IWaypoint)((IAdaptable)element).getAdapter(IWaypoint.class);
			if (wp != null) {
				String type = wp.getType();
				if (((TagSEAUI)TagSEAPlugin.getDefault().getUI()).isFilteredOutType(type)) return false;
				IWaypointFilter filter =
					((TagSEAUI)TagSEAPlugin.getDefault().getUI()).getFilter(type);
				return (filter == null || filter.select(wp));
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean isImportantSelection(ISelection selection) {
		String[] types = TagSEAPlugin.getDefault().getWaypointTypes();
		TagSEAUI ui = (TagSEAUI) TagSEAPlugin.getDefault().getUI();
		for (String type : types) {
			if (!ui.isFilteredOutType(type)) {
				IWaypointFilter filter = ui.getFilter(type);
				if (filter != null && filter.isImportantSelection(selection)) return true;
			}
		}
		return false;
	}

}
