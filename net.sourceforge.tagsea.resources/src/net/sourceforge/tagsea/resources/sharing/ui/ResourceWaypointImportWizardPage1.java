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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.xml.WaypointShareUtilities;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * First page for importing resource waypoints. Presents the user with a way to select a file to load.
 * @author Del Myers
 *
 */
public class ResourceWaypointImportWizardPage1 extends
		AbstractResourceWaypointImportWizardPage {


	private Text fileText;
	private String path;

	protected ResourceWaypointImportWizardPage1() {
		super("Load Resource Waypoints", "Import Resource Waypoints",ResourceWaypointPlugin.getImageDescriptor("icons/bigicon.png"));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.sharing.ui.AbstractResourceWaypointImportWizardPage#calculateMergedDescriptors()
	 */
	@Override
	protected IResourceWaypointDescriptor[] calculateMergedDescriptors() {
		IResourceWaypointDescriptor[] descriptors = getDescriptors();
		ArrayList<IResourceWaypointDescriptor> result = new ArrayList<IResourceWaypointDescriptor>();
		for (int i = 0; i < descriptors.length; i++) {
			//only add the ones that have resources belonging to them
			if (getResourceForDescriptor(descriptors[i]) != null) {
				result.add(mergeDescriptor(descriptors[i], findConflictingAttributes(descriptors[i], true, true), true));
			}
		}
		return result.toArray(new IResourceWaypointDescriptor[result.size()]);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.sharing.ui.AbstractResourceWaypointImportWizardPage#getDescriptors()
	 */
	@Override
	protected IResourceWaypointDescriptor[] getDescriptors() {
		if (!isPageComplete()) return null;
		File f = new File(path);
		if (!f.exists()) return null;
		try {
			FileReader reader = new FileReader(f);
			XMLMemento memento = XMLMemento.createReadRoot(reader);
			List<IResourceWaypointDescriptor> descriptors = WaypointShareUtilities.readWaypoints(memento);
			//only return the ones with important differences.
			for (Iterator iter = descriptors.iterator(); iter.hasNext();) {
				IResourceWaypointDescriptor d = (IResourceWaypointDescriptor) iter.next();
				Set<String> atts = findConflictingAttributes(d, true, true);
				if (atts != null) {
					if (atts.size() == 0 && !doTagsConflict(d)) {
						iter.remove();
					}
				}
			}
			return descriptors.toArray(new IResourceWaypointDescriptor[descriptors.size()]);
		} catch (FileNotFoundException e) {
			MessageDialog.openError(getShell(), "File Not Found", e.getMessage());
		} catch (WorkbenchException e) {
			MessageDialog.openError(getShell(), "Workbench Error", "An error occurred while reading the file. Check the log for more details.");
			ResourceWaypointPlugin.getDefault().log(e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite browseComposite = new Composite(parent, SWT.NONE);
		browseComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		browseComposite.setLayout(new GridLayout(3, false));
		Label l = new Label(browseComposite, SWT.NONE);
		l.setText("File:");
		l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		fileText = new Text(browseComposite, SWT.BORDER | SWT.FLAT);
		fileText.setText("");
		fileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fileText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				setPath(((Text)e.widget).getText());
			}});
		Button browseButton = new Button(browseComposite, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		browseButton.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[] {"*.xml"});
				String text = dialog.open();
				text = (text != null) ? text : "";
				fileText.setText(text);
			}
		});
		updateErrorMessages();
		setControl(browseComposite);
	}
	
	/**
	 * @param text
	 */
	public void setPath(String text) {
		this.path = text;
		updateErrorMessages();
		
	}
	
	private void updateErrorMessages() {
		if (this.path == null || "".equals(path)) {
			setErrorMessage("Must set a target path");
			setPageComplete(false);
			return;
		}
		setErrorMessage(null);
		setPageComplete(true);
	}

}
