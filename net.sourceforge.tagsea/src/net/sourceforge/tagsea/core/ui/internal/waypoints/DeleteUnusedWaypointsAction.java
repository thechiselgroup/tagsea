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
package net.sourceforge.tagsea.core.ui.internal.waypoints;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Removes waypoints that only have the "default" tag associated with them.
 * @author Del Myers
 */

public class DeleteUnusedWaypointsAction extends Action {
	
	private static class UnusedWaypointsContentProvider implements ITreeContentProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			return new Object[0];
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[])
				return (Object[])inputElement;
			return new Object[0];
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
		}
		
	}
	private Shell shell;
	
	/**
	 * 
	 * @param shell the shell that related ui elements will display on.
	 */
	public DeleteUnusedWaypointsAction(Shell shell) {
		this.shell = shell;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		
		CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(shell, new WorkbenchLabelProvider(), new UnusedWaypointsContentProvider());
		dialog.setTitle("Cleanup Untagged Locations");
		dialog.setHelpAvailable(false);
		dialog.setMessage("The following TagSEA locations do not have any tags other than the default tag. Select the locations that you would like to delete.");
		Object[] input = getUnusedWaypoints();
		dialog.setInput(input);
		dialog.setInitialSelections(input);
		int result = dialog.open();
		if (result == Dialog.OK) {
			final Object[] waypoints = dialog.getResult();
			TagSEAPlugin.run(new TagSEAOperation("Deleting Waypoints..."){
				@Override
				public IStatus run(IProgressMonitor monitor) throws  InvocationTargetException {
					MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
					monitor.beginTask("Deleting Waypoints...", waypoints.length);
					for (Object wp : waypoints) {
						if (wp instanceof IWaypoint) {
							status.merge(((IWaypoint)wp).delete().getStatus());
						}
						monitor.worked(1);
					}
					monitor.done();
					return status;
				}
			}, true);
		}
	}
	/**
	 * @return
	 */
	private Object[] getUnusedWaypoints() {
		HashSet<IWaypoint> waypoints = new HashSet<IWaypoint>();
		for (IWaypoint wp : TagSEAPlugin.getWaypointsModel().getAllWaypoints()) {
			if (wp.getTags().length == 1) {
				if (wp.getTags()[0].getName().equals(ITag.DEFAULT))
					waypoints.add(wp);
			}
		}
		return waypoints.toArray();
	}
}
