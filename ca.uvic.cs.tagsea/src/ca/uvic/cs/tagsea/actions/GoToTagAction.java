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
package ca.uvic.cs.tagsea.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Waypoint;
import ca.uvic.cs.tagsea.monitoring.TagSEANavigationEvent;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;

/**
 * Opens an editor and shows the selected waypoint.
 * 
 * @author Chris Callendar
 */
public class GoToTagAction extends Action implements IDoubleClickListener {
	private StructuredViewer viewer;
	private IWorkbenchPage page;
	
	public GoToTagAction(StructuredViewer viewer, IWorkbenchPage page) {
		this.viewer = viewer;
		this.page = page;
	}

	public void doubleClick(DoubleClickEvent event) {
		run();
	}
	
	/**
	 * Grabs the currently selected waypoint from the viewer and navigates to
	 * that waypoint's marker position in the editor.
	 */
	public void run() {
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if (!selection.isEmpty()) {
			Object obj = selection.getFirstElement();
			IMarker marker = null;
	        if (obj instanceof Waypoint) {
	        	// Waypoints TableViewer
	        	marker = ((Waypoint)obj).getMarker(); 
			}
	        if ((marker != null) && marker.exists()) {
		        IResource resource = marker.getResource();
		        if (marker.exists() && resource instanceof IFile) {
		            try {
		                IDE.openEditor(page, marker, OpenStrategy.activateOnOpen());
		                Monitoring.getDefault().fireEvent(new TagSEANavigationEvent((Waypoint)obj));
		            } catch (PartInitException e) {
		            	TagSEAPlugin.log("Couldn't open editor to show the tag", e);
		            }
		        }
	        }
		}
	}
}
