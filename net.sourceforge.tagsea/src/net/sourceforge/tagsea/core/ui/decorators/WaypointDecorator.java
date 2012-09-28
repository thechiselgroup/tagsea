/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui.decorators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.tagsea.TagSEAPlugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

/**
 * An example showing how to control when an element is decorated. This example
 * decorates only elements that are instances of IResource and whose attribute
 * is 'Read-only'.
 * 
 * @see ILightweightLabelDecorator
 */
public class WaypointDecorator implements ILightweightLabelDecorator {
	/**
	 * String constants for the various icon placement options from the template
	 * wizard.
	 */
	public static final String TOP_RIGHT = "TOP_RIGHT";

	public static final String TOP_LEFT = "TOP_LEFT";

	public static final String BOTTOM_RIGHT = "BOTTOM_RIGHT";

	public static final String BOTTOM_LEFT = "BOTTOM_LEFT";

	public static final String UNDERLAY = "UNDERLAY";

	/** The integer value representing the placement options */
	private int quadrant;

	/** The icon image location in the project folder */
	private String iconPath = "icons/smalltag.gif"; //NON-NLS-1
	private String parentIconPath = "icons/smalltagdisabled.gif"; //NON-NLS-1

	/**
	 * The image description used in
	 * <code>addOverlay(ImageDescriptor, int)</code>
	 */
	private ImageDescriptor descriptor;

	private HashMap<ILabelProviderListener, MarkerChangeListener> listeners;

	private ImageDescriptor parentDescriptor;
	
	private class MarkerChangeListener implements IResourceChangeListener {
		private class ResourceVisitor implements IResourceDeltaVisitor {
			private Set<IResource> changes;
			/**
			 * 
			 */
			public ResourceVisitor() {
				changes = new HashSet<IResource>();
			}
			/* (non-Javadoc)
			 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
			 */
			public boolean visit(IResourceDelta delta) throws CoreException {
				IMarkerDelta[] markerDeltas= delta.getMarkerDeltas();
				for (IMarkerDelta mdelta : markerDeltas) {
					if(mdelta.isSubtypeOf(TagSEAPlugin.MARKER_ID)) {
						changes.add(mdelta.getResource());
						addParents(mdelta.getResource());
					}
				}
				return true;
			}
			/**
			 * @param resource
			 */
			private void addParents(IResource resource) {
				IResource parent = resource.getParent();
				while (parent != null && !(parent instanceof IWorkspaceRoot)) {
					changes.add(parent);
					parent = parent.getParent();
				}
			}
			
		}
		private ILabelProviderListener labelListener;
		
		/**
		 * 
		 */
		public MarkerChangeListener(ILabelProviderListener listener) {
			this.labelListener = listener;
		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			ResourceVisitor visitor = new ResourceVisitor();
			try {
				if (event.getDelta() == null) return;
				event.getDelta().accept(visitor);
				if (visitor.changes.size() > 0) {
					labelListener.labelProviderChanged(new LabelProviderChangedEvent(WaypointDecorator.this, visitor.changes.toArray()));
				}
			} catch (CoreException e) {
				TagSEAPlugin.getDefault().log(e);
			}			
		}
	}
	
	
	/**
	 * 
	 */
	public WaypointDecorator() {
		this.listeners = new HashMap<ILabelProviderListener, MarkerChangeListener>();
		descriptor = TagSEAPlugin.getImageDescriptor(iconPath);
		parentDescriptor = TagSEAPlugin.getImageDescriptor(parentIconPath);
		quadrant = IDecoration.TOP_LEFT;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {
		/**
		 * Checks that the element is an IResource with the 'Read-only' attribute
		 * and adds the decorator based on the specified image description and the
		 * integer representation of the placement option.
		 */
		IResource resource = (IResource) element;
		try {
			IMarker[] markers = resource.findMarkers(TagSEAPlugin.MARKER_ID, true, IResource.DEPTH_ZERO);
			if (markers.length > 0){
				decoration.addOverlay(descriptor,quadrant);
			} else {
				markers = resource.findMarkers(TagSEAPlugin.MARKER_ID, true, IResource.DEPTH_INFINITE);
				if (markers.length > 0) {
					decoration.addOverlay(parentDescriptor,quadrant);
				}
			}
		} catch (CoreException e) {
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		MarkerChangeListener markerListener = new MarkerChangeListener(listener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(markerListener);
		listeners.put(listener, new MarkerChangeListener(listener));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		for (MarkerChangeListener markerListener : listeners.values()) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(markerListener);
		}
		listeners.clear();
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
		MarkerChangeListener markerListener = listeners.get(listener);
		if (markerListener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(markerListener);
		}
		listeners.remove(listener);
	}
}