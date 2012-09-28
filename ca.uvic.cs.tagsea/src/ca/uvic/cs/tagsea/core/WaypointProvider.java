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
package ca.uvic.cs.tagsea.core;

import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_AUTHOR;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_COMMENT;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_DATE;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_JAVA_ELEMENT;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_LINE_NUMBER;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_RESOURCE;
import static ca.uvic.cs.tagsea.ui.views.WaypointsComposite.INDEX_TAG_NAME;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import ca.uvic.cs.tagsea.ITagseaImages;
import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * The content and label provider for the waypoint table viewer. Also is a selection listener for the Tags tree viewer.
 * When the tags selection changes, the waypoints table is updated with the waypoints for the selected tag.
 * 
 * @author Chris Callendar
 */
public class WaypointProvider implements IStructuredContentProvider, ITableLabelProvider, ISelectionChangedListener {

	private TableViewer tableViewer;
	private ArrayList<Tag> selectedTags;
	
	private JavaElementLabelProvider javaLabelProvider;
	

	public WaypointProvider(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
		selectedTags = new ArrayList<Tag>();
	}
	
	/**
	 * Lazy initialization.  The JavaElementLabelProvider constructor requires the 
	 * eclipse workbench to be running.  So for test cases we don't want to initialize
	 * the JavaElementLabelProvider in our constructor.
	 * @return JavaElementLabelProvider
	 */
	private ILabelProvider getJavaLabelProvider() {
		if (javaLabelProvider == null) {
			javaLabelProvider = new JavaElementLabelProvider();
		}
		return javaLabelProvider;
	}

	// IStructuredContentProvider methods

	/**
	 * Gets the all waypoints for the selected tags.
	 * @param inputElement not used 
	 * @return Object[] the Waypoint[]
	 */
	public Object[] getElements(Object inputElement) {
		if ((selectedTags != null) && (selectedTags.size() > 0)) {
			HashSet<Waypoint> allWaypoints = new HashSet<Waypoint>();
			for (Tag tag : selectedTags) {
				Waypoint[] wps = tag.getAllWaypoints();
				for (Waypoint wp : wps) {
					allWaypoints.add(wp);
				}
			}
			return allWaypoints.toArray(new Waypoint[allWaypoints.size()]);
		}
		return new Object[0];
	}

	/** Does nothing. */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	// ITableLabelProvider methods

	/** Does nothing. */
	public void addListener(ILabelProviderListener listener) {
	}

	/** Does nothing. */
	public void dispose() {
	}

	/**
	 * Gets the java element image for the java element column.  
	 * Returns null for the other columns.
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof Waypoint) {
			Waypoint waypoint = (Waypoint) element;
			switch (columnIndex) {
				case INDEX_TAG_NAME :
					// JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_ANNOTATION);
					return TagSEAPlugin.getDefault().getTagseaImages().getImage(ITagseaImages.IMG_WAYPOINT24); 
				case INDEX_JAVA_ELEMENT:
					return getJavaLabelProvider().getImage(waypoint.getJavaElement());
				case INDEX_RESOURCE :
					IMarker marker = waypoint.getMarker();
					if (marker != null) {
						// also tried WorkbenchLabelProvider - but it only provides 16x16 images
						return getJavaLabelProvider().getImage(marker.getResource());
					}
			}
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String str = "";
		if (element instanceof Waypoint) {
			Waypoint waypoint = (Waypoint) element;

			switch (columnIndex) {
				case INDEX_TAG_NAME:
					str = waypoint.getKeyword();
					break;
				case INDEX_JAVA_ELEMENT:
					str = waypoint.getJavaElementName();
					break;
				case INDEX_RESOURCE:
					IMarker marker = waypoint.getMarker();
					if (marker != null) {
						str = marker.getResource().getFullPath().toString();
					}
					break;
				case INDEX_COMMENT:
					str = waypoint.getComment();
					break;
				case INDEX_AUTHOR:
					str = waypoint.getAuthor();
					break;
				case INDEX_DATE:
					if (waypoint.getDate() != null) {
						str = waypoint.getDate().toString();
					}
					break;
				case INDEX_LINE_NUMBER:
					str = Integer.toString(waypoint.getLineNumber());
					break;
			}
		}
		return str;
	}

	/** Returns false. */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/** Does nothing. */
	public void removeListener(ILabelProviderListener listener) {
	}

	// ISelectionChangedListener methods

	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		selectedTags.clear();
		if (!selection.isEmpty()) {
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (obj instanceof Tag) {
					selectedTags.add((Tag) obj);
				}
			}
		}
		tableViewer.refresh();
	}

}
