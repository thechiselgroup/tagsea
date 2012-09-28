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
package net.sourceforge.tagsea.core.ui.waypoints;

import java.text.DateFormat;
import java.util.Date;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;
import net.sourceforge.tagsea.core.ui.internal.views.IStringFinder;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;




public class WaypointTableColumnLabelProvider extends StyledCellLabelProvider
{
	
	public static final int MESSAGE_COLUMN = 0;
	public static final int AUTHOR_COLUMN = 2;
	public static final int DATE_COLUMN = 3;
	public static final int LOCATION_COLUMN = 1;
	
	public static final String[] COLUMNS = {"MESSAGE", "LOCATION", "AUTHOR", "DATE"};
	public static final String[] COLUMN_LABELS = {"Message", "Location", "Author", "Date"};
	//private HashMap<IWaypoint, Image> imageMap;
	private int columnIndex;
	
	/**
	 * 
	 */
	public WaypointTableColumnLabelProvider(IStringFinder finder) {
		this(finder, 0);
		//imageMap = new HashMap<IWaypoint, Image>();
		//TagSEAPlugin.addWaypointChangeListener(this)
	}
	/**
	 * @param patternFilter
	 * @param i
	 */
	public WaypointTableColumnLabelProvider(
			IStringFinder finder, int i) {
		super(finder);
		this.columnIndex = i;
	}
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.adapters.TableLabelProviderAdapter#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) 
	{
		IWaypoint waypoint = (IWaypoint)((IAdaptable)element).getAdapter(IWaypoint.class);
		if ( columnIndex==0 )
		{
			IWaypointUIExtension extension = TagSEAPlugin.getDefault().getWaypointUI(waypoint.getType());
			return extension.getImage(waypoint);
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) 
	{
		if (element instanceof String) {
			return (String)element;
		}
		IWaypoint waypoint = (IWaypoint)((IAdaptable)element).getAdapter(IWaypoint.class);
		String string = "";
		switch (columnIndex) {
		case MESSAGE_COLUMN :
			string = waypoint.getText();
			break;
		case AUTHOR_COLUMN :
			string = waypoint.getAuthor();
			break;
		case DATE_COLUMN :
			Date date = waypoint.getDate();
			if (date != null) {
				string = DateFormat.getDateInstance().format(date);
			}
			break;
		case LOCATION_COLUMN:
			IWaypointUIExtension ui = TagSEAPlugin.getDefault().getWaypointUI(waypoint.getType());
			if (ui != null) {
				string = ui.getLocationString(waypoint);
			}
			break;
		default:
			IWaypointUIExtension extension = TagSEAPlugin.getDefault().getWaypointUI(waypoint.getType());
			string = extension.getLabel(waypoint);
		}
		return (string != null) ? string : "";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.adapters.TableLabelProviderAdapter#dispose()
	 */

	public void dispose() {
//		for (Image i : imageMap.values()) {
//			i.dispose();
//		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		return getColumnImage(element, columnIndex);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		return getColumnText(element, columnIndex);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeListener#waypointsChanged(net.sourceforge.tagsea.core.WaypointDelta)
	 */
//	public void waypointsChanged(WaypointDelta delta) {
//		for (IWaypointChangeEvent event : delta.changes) {
//			if (event.getType() == IWaypointChangeEvent.DELETE) {
//				Image image = imageMap.get(event.getWaypoint());
//				imageMap.remove(event.getWaypoint());
//			}
//		}
//	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object element) {
		
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) {

		IWaypoint wp = (IWaypoint)((IAdaptable)element).getAdapter(IWaypoint.class);
		if (wp != null) {
			if (wp.getTags().length == 1) {
				if (wp.getTags()[0].getName().equals(ITag.DEFAULT))
					return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
			}
		}
		
		return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	}
	public void addListener(ILabelProviderListener listener) {
	}
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}
	public void removeListener(ILabelProviderListener listener) {
	}
}
