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

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointProxyDescriptor;
import net.sourceforge.tagsea.resources.waypoints.operations.CreateWaypointOperation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Import wizard for resource waypoints
 * @author Del Myers
 *
 */
public class ResourceWaypointImportWizard extends Wizard implements
		IImportWizard {

	
	/**
	 * 
	 */
	public ResourceWaypointImportWizard() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			AbstractResourceWaypointImportWizardPage page = (AbstractResourceWaypointImportWizardPage) getContainer().getCurrentPage();
			if (page instanceof ResourceWaypointImportWizardPage1 && page.pageHasConflicts(true, true)) {
				boolean cont = MessageDialog.openConfirm(getShell(), "Overwrite Existing Waypoints", "The file you chose to import" +
						"contains waypoints that already exist in the workspace. These waypoints will be overwritten." +
						"If you choose to cancel, you can merge the waypoints manually using this wizard. Continue?");
				if (!cont) {
					return false;
				}
			}
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					AbstractResourceWaypointImportWizardPage page = (AbstractResourceWaypointImportWizardPage) getContainer().getCurrentPage();
					IResourceWaypointDescriptor[] mergedDescriptors = page.calculateMergedDescriptors();
					monitor.beginTask("Importing waypoints", mergedDescriptors.length);
					for (IResourceWaypointDescriptor d : mergedDescriptors) {
						if (monitor.isCanceled()) {
							throw new InterruptedException();
						}
						if (d instanceof ResourceWaypointProxyDescriptor) {
							((ResourceWaypointProxyDescriptor)d).commit();
						} else {
							//must create a new descriptor
							TagSEAPlugin.run(new CreateWaypointOperation(d), true);
						}
					}
				}
			});
		} catch (InvocationTargetException e) {
			ResourceWaypointPlugin.getDefault().log(e);
			MessageDialog.openError(getShell(), "Error", "An error occurred while merging waypoints. See error log for more details.");
			return false;
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Resource Waypoints");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(new ResourceWaypointImportWizardPage1());
		addPage(new ResourceWaypointImportWizardPage2());
	}

}
