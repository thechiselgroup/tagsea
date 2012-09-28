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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.xml.WaypointShareUtilities;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.ide.IDE;

/**
 * Export wizard for resource waypoints.
 * @author Del Myers
 *
 */
public class ResourceWaypointExportWizard extends Wizard implements
		IExportWizard {

	private ResourceWaypointExportWizardPage page;

	/**
	 * 
	 */
	public ResourceWaypointExportWizard() {
		this.page = new ResourceWaypointExportWizardPage("Resource Waypoints");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		final List<IResourceWaypointDescriptor> selected = page.getSelectedDescriptorList();
		try {
			dialog.run(true, false, new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Exporting waypoints", 1);
					String pathString = page.getPath();
					IPath path = new Path(pathString);
					IPath parents = path.removeLastSegments(1);
					if (parents.segmentCount() > 0) {
						File dirs = parents.toFile();
						if (!dirs.exists()) {
							if (!dirs.mkdirs()) {
								throw new InvocationTargetException(new IOException("Failed to create directories."));
							}
						}
					}
					File file = path.toFile();
					try {
						if (!file.exists()) {
							if (!file.createNewFile()) {
								throw new InvocationTargetException(new IOException("Failed to create directories."));
							}
						}
						XMLMemento memento = XMLMemento.createWriteRoot("resource-waypoints");
						WaypointShareUtilities.writeWaypoints(selected, memento);
						FileWriter writer = new FileWriter(file);
						memento.save(writer);
						monitor.done();
					} catch (IOException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.worked(1);
						monitor.done();
					}
				}
				
			});
		} catch (InvocationTargetException e) {
			ResourceWaypointPlugin.getDefault().log(e);
			MessageDialog.openError(getShell(), "Export Error", e.getCause().getMessage());
			return false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@SuppressWarnings("unchecked")
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Resource Waypoints");
		List resources = IDE.computeSelectedResources(selection);
		HashSet<IWaypoint> selectedWaypoints = new HashSet<IWaypoint>();
		for (int i = 0; i < resources.size(); i++) {
			IResource resource = (IResource) resources.get(i);
			IWaypoint[] wps = ResourceWaypointUtils.getWaypointsForResource(resource, false);
			selectedWaypoints.addAll(Arrays.asList(wps));
			if (resource instanceof IContainer) {
				try {
					IResource[] children = ((IContainer)resource).members();
					resources.addAll(Arrays.asList(children));
				} catch (CoreException e) {
				}
			}
		}
		if (resources.size() ==0) {
			//no resources selected, add all resource waypoints.
			page.setInitialChecks(TagSEAPlugin.getWaypointsModel().getWaypoints(ResourceWaypointPlugin.WAYPOINT_ID));
		} else {
			page.setInitialChecks(selectedWaypoints.toArray(new IWaypoint[selectedWaypoints.size()]));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(page);
	}

}
