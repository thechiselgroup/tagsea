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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointProxyDescriptor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Simple class that organizes resource waypoints into a tree structure. The tree
 * has three levels: projects, resources, waypoints. A project may have a waypoint
 * on it as well.
 * @author Del Myers
 *
 */
public class ResourceWaypointTree {
	
	public static class TreeNode implements IAdaptable {
		private String name;
		private HashMap<String, TreeNode> children;
		private Set<IResourceWaypointDescriptor> waypoints;
		private TreeNode parent;
		public TreeNode(TreeNode parent, String name) {
			this.parent = parent;
			this.name = name;
			children = new HashMap<String, TreeNode>();
			waypoints = new HashSet<IResourceWaypointDescriptor>();
		}
		
		/**
		 * Adds a child. Two children cannot have the same name.
		 * @param node
		 */
		public void addChild(TreeNode node) {
			children.put(node.getName(), node);
		}
		
		/**
		 * Returns the child with the given name. Null if it doesn't exist.
		 * @param name the name of the child.
		 * @return the child with the given name.
		 */
		public TreeNode getChild(String name) {
			return children.get(name);
		}
		
		public TreeNode[] getChildren() {
			Collection<TreeNode> childNodes = children.values();
			return childNodes.toArray(new TreeNode[childNodes.size()]);
		}
		
		public IResourceWaypointDescriptor[] getWaypoints() {
			return waypoints.toArray(new IResourceWaypointDescriptor[waypoints.size()]);
		}
		
		public void addWaypoint(IResourceWaypointDescriptor waypoint) {
			waypoints.add(waypoint);
		}
		
		public String toString() {
			return name;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return getName().hashCode();
		}
		
		public String getName() {
			return name;
		}
		
		/**
		 * @return the parent
		 */
		public TreeNode getParent() {
			return parent;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!obj.getClass().isAssignableFrom(this.getClass())) return false;
			return ((TreeNode)obj).getName().equals(getName());
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
		@SuppressWarnings("unchecked")
		public Object getAdapter(Class adapter) {
			if (adapter.isAssignableFrom(TreeNode.class)) {
				return this;
			} else if (adapter.equals(IResource.class)) {
				Path path = new Path(getName());
				if (path.segmentCount() == 1) {
					return ResourcesPlugin.getWorkspace().getRoot().getProject(getName());
				} else {
					IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
					if (resource == null) {
						//make a guess that it should be a file so that we don't return null
						resource = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
					}
					return resource;
				}
			} else if (adapter.isAssignableFrom(IResourceWaypointDescriptor[].class)) {
				List<IResourceWaypointDescriptor> descriptors = new LinkedList<IResourceWaypointDescriptor>();
				descriptors.addAll(waypoints);
				List<TreeNode> children = new ArrayList<TreeNode>();
				children.addAll(this.children.values());
				for (int i = 0; i < children.size(); i++) {
					List<TreeNode> nextChildren = Arrays.asList(children.get(i).getChildren());
					descriptors.addAll(Arrays.asList(children.get(i).getWaypoints()));
					children.addAll(nextChildren);
				}
				return descriptors.toArray(new IResourceWaypointDescriptor[descriptors.size()]);
			}
			return null;
		}
	}
	

	
	private HashMap<String, TreeNode> roots;
	
	public ResourceWaypointTree(IWaypoint[] waypoints) {
		
		IResourceWaypointDescriptor[] descriptors = new IResourceWaypointDescriptor[waypoints.length];
		for (int i = 0; i < waypoints.length; i++) {
			descriptors[i] = new ResourceWaypointProxyDescriptor(waypoints[i]);
		}
		organizeDescriptors(descriptors);
	}
	
	public ResourceWaypointTree(IResourceWaypointDescriptor[] descriptors) {
		organizeDescriptors(descriptors);
	}

	/**
	 * Organizes the descriptors into a tree.
	 * @param descriptors
	 */
	private void organizeDescriptors(IResourceWaypointDescriptor[] descriptors) {
		roots = new HashMap<String, TreeNode>();
		for (IResourceWaypointDescriptor d : descriptors) {
			String resource = d.getResource();
			IPath path = new Path(resource);
			if (path.segmentCount() > 0) {
				String name = path.segment(0);
				IPath resourcePath = path.removeFirstSegments(1);
				TreeNode node = roots.get(name);
				if (node == null) {
					node = new TreeNode(null, name);
					roots.put(name, node);
				}
				if (resourcePath.segmentCount() == 0) {
					node.addWaypoint(d);
				} else {
					TreeNode resourceNode = node.getChild(path.toPortableString());
					if (resourceNode == null) {
						resourceNode = new TreeNode(node, path.toPortableString());
						node.addChild(resourceNode);
					}
					resourceNode.addWaypoint(d);
				}
			}
		}
	}
	
	public TreeNode[] getChildren() {
		Collection<TreeNode> children = roots.values();
		return children.toArray(new TreeNode[children.size()]);
	}

}
