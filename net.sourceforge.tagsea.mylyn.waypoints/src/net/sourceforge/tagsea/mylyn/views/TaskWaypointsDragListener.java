/*******************************************************************************
 * 
 *   Copyright 2007, CHISEL Group, University of Victoria, Victoria, BC, Canada. 
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *       The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.mylyn.views;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.WaypointTransfer;
import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItemTransfer;
import net.sourceforge.tagsea.core.ui.tags.TagNameTransfer;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.PluginTransferData;

public class TaskWaypointsDragListener implements DragSourceListener {

	private final StructuredViewer viewer;

	public TaskWaypointsDragListener(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		System.err.println("Drag finished.");
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		System.err.println("--> dragSetData");
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (WaypointTransfer.getInstance().isSupportedType(event.dataType)) {
			System.err.println("Waypoint transfer");
			IWaypoint[] waypoints = (IWaypoint[])selection.toList().toArray(new IWaypoint[selection.size()]);
			event.data = waypoints;
		} else if (TagTreeItemTransfer.getInstance().isSupportedType(
				event.dataType)) {
			System.err.println("Tag Tree transfer");
			ITag[] tags = (ITag[])selection.toList().toArray(new ITag[selection.size()]);
			event.data = tags;
		} else if (TagNameTransfer.getInstance()
				.isSupportedType(event.dataType)) {
			System.err.println("Tag name transfer");
			String[] tagNames = (String[])selection.toList().toArray(new String[selection.size()]);
			event.data = tagNames;
		} else if (PluginTransfer.getInstance().isSupportedType(event.dataType)) {
			System.err.println("Plug-in transfer");
			System.err.println("Event data type: " + event.dataType);
			byte[] data = "Test".getBytes();
			event.data = new PluginTransferData("net.sourceforge.tagsea.mylyn.drop", data);
			//byte[] data = GadgetTransfer.getInstance().toByteArray(gadgets);
	         //event.data = new PluginTransferData("org.eclipse.ui.examples.gdt.gadgetDrop", data);
		}
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		System.err.println("Drag started...");

	}

}
