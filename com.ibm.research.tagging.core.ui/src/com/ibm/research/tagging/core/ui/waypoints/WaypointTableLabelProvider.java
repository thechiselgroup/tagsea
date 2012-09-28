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
package com.ibm.research.tagging.core.ui.waypoints;

import org.eclipse.swt.graphics.Image;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.TagUIPlugin;
import com.ibm.research.tagging.core.ui.adapters.TableLabelProviderAdapter;

/**
 * 
 * @author mdesmond
 *
 */
public class WaypointTableLabelProvider extends TableLabelProviderAdapter
{
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.adapters.TableLabelProviderAdapter#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) 
	{
		IWaypoint waypoint = (IWaypoint)element;
		if ( columnIndex==0 )
		{
			Image image = TagUIPlugin.getDefault().getTagUI().getImage(waypoint);
			
			// using Image.toString() as key to prevent repetition.  could use waypoint.getId(), but that would create an icon for every single waypoint...
			return TagUIPlugin.getDefault().getPaddedIcon(image.toString(), image);
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) 
	{
		IWaypoint waypoint = (IWaypoint)element;
		return TagUIPlugin.getDefault().getTagUI().getLabel(waypoint,columnIndex);
	}
}
