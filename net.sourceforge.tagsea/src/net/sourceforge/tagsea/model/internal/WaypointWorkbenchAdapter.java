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
package net.sourceforge.tagsea.model.internal;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Adapter for waypoints in the workbench.
 * @author Del Myers
 */

public class WaypointWorkbenchAdapter implements IWorkbenchAdapter {
	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object o) {
		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		Image image = getUI((IWaypoint)object).getImage((IWaypoint)object);
		if (image != null) {
			return ImageDescriptor.createFromImage(image);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object o) {
		return getUI((IWaypoint)o).getLabel((IWaypoint)o);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object o) {
		return null;
	}
	
	private IWaypointUIExtension getUI(IWaypoint waypoint) {
		return TagSEAPlugin.getDefault().getUI().getWaypointUI(waypoint.getType());
	}


}
