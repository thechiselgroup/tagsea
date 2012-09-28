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

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.tags.TagNameTransfer;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

/**
 * Drop adapter for the waypoint view. Accepts tag name transfers
 * @author Del Myers
 */

public class WaypointTableViewDropAdapter extends ViewerDropAdapter {

	/**
	 * @param viewer
	 */
	public WaypointTableViewDropAdapter(Viewer viewer) {
		super(viewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
	 */
	@Override
	public boolean performDrop(Object data) {
		IWaypoint waypoint = (IWaypoint) ((IAdaptable) getCurrentTarget()).getAdapter(IWaypoint.class);
		if (data instanceof String[]) {
			for (String name : ((String[])data)) {
				waypoint.addTag(name);
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragEnter(DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
		super.dragEnter(event);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		boolean okay = (TagNameTransfer.getInstance().isSupportedType(transferType));
		if (okay = (target instanceof IAdaptable)) {
			okay = ((IAdaptable)target).getAdapter(IWaypoint.class) != null;
		}
		return okay;
	}

}
