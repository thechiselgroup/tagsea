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

import net.sourceforge.tagsea.resources.sharing.ui.ResourceWaypointTree.TreeNode;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Label provider for ResourceWaypointTrees.
 * @author Del Myers
 *
 */
public class ResourceWaypointTreeLabelProvider implements ILabelProvider {
	WorkbenchLabelProvider labelProvider;

	
	
	public ResourceWaypointTreeLabelProvider() {
		labelProvider = new WorkbenchLabelProvider();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof IResourceWaypointDescriptor)
		{
			IResourceWaypointDescriptor waypoint = (IResourceWaypointDescriptor) element;
			IResource resource = getResource(waypoint.getResource());
			if (resource != null) {
				Image providerImage = labelProvider.getImage(resource);
				if (providerImage != null) {
					return providerImage;
				}
			}
			return PlatformUI.
			getWorkbench().
			getSharedImages().
			getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);
		} else if (element instanceof TreeNode) {
			TreeNode node = (TreeNode) element;
			IResource resource = getResource(node.getName());
			if (resource != null) {
				return labelProvider.getImage(resource);
			} else {
				Path path = new Path(node.getName());
				if (path.segmentCount() > 1) {
					return PlatformUI.
						getWorkbench().
						getSharedImages().
						getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);
				} else {
					return PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
				}
			}
		}
		
		return null;
	}

	/**
	 * @param resource
	 * @return
	 */
	private IResource getResource(String resource) {
		return ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(resource));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof TreeNode) {
			Path p = new Path(((TreeNode)element).getName());
			if (p.segmentCount() > 1) {
				return p.removeFirstSegments(1).toPortableString();
			} else {
				return p.toPortableString();
			}
		} else if (element instanceof IResourceWaypointDescriptor) {
			return ((IResourceWaypointDescriptor)element).getText();
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		labelProvider.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

}
