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
package net.sourceforge.tagsea.resources.sharing.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.tagsea.resources.sharing.ui.ResourceWaypointTree.TreeNode;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A content provider that expects a ResourceWaypointTree as input. Returns TreeNodes and waypoint
 * descriptors as elements. It is expected that there is a one-to-one relationship between this 
 * content provider and its viewer.
 * @author Del Myers
 *
 */
public class ResourceWaypointTreeContentProvider implements
		ITreeContentProvider {

	private HashMap<IResourceWaypointDescriptor, Object> waypointParents;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof ResourceWaypointTree) {
			ResourceWaypointTree tree = (ResourceWaypointTree) parentElement;
			TreeNode[] children = tree.getChildren();
			return children;
		} else if (parentElement instanceof TreeNode) {
			TreeNode node = (TreeNode)parentElement;
			List<Object> children = new ArrayList<Object>();
			TreeNode[] childNodes = node.getChildren();
			for (TreeNode child : childNodes) {
				children.add(child);
			}
			for (IResourceWaypointDescriptor desc : node.getWaypoints()) {
				children.add(desc);
			}
			return children.toArray();
		}
		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof TreeNode) {
			return ((TreeNode)element).getParent();
		} else if (element instanceof IResourceWaypointDescriptor) {
			//return a psuedo tree node.
			return waypointParents.get(element);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length != 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//initialize the parents.
		this.waypointParents = new HashMap<IResourceWaypointDescriptor, Object>();
		Object[] elements = getElements(newInput);
		List<Object> objects = new ArrayList<Object>(Arrays.asList(elements));
		for (int i = 0; i < objects.size(); i++) {
			Object parent = objects.get(i);
			Object[] children = getChildren(parent);
			for (Object child : children) {
				if (child instanceof IResourceWaypointDescriptor) {
					waypointParents.put((IResourceWaypointDescriptor) child, parent);
				}
			}
			objects.addAll(Arrays.asList(children));
		}
	}

}
