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
import java.util.HashSet;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointProxyDescriptor;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.wizard.WizardPage;
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

/**
 * Export wizard page for resource waypoints.
 * @author Del Myers
 *
 */
public class ResourceWaypointExportWizardPage extends WizardPage {

	private CheckboxTreeViewer viewer;
	private String path;
	private Text fileText;
	private IWaypoint[] initialChecks;

	/**
	 * @param pageName
	 */
	protected ResourceWaypointExportWizardPage(String pageName) {
		super(pageName, "Export Resource Waypoints", ResourceWaypointPlugin.getImageDescriptor("icons/bigicon.png"));
		this.path = "";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout());
		this.viewer = new CheckboxTreeViewer(page, SWT.BORDER);
		viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setContentProvider(new ResourceWaypointTreeContentProvider());
		viewer.setLabelProvider(new DecoratingLabelProvider(
				new ResourceWaypointTreeLabelProvider(), new MultiLabelDecorator(
						new ILabelDecorator[] { 
							new ResourceWaypointLabelDecorator(),
							new MissingResourceLabelDecorator()
						}
					)
				)
		);
		viewer.setInput(
			new ResourceWaypointTree(
				TagSEAPlugin.
					getWaypointsModel().
					getWaypoints(ResourceWaypointPlugin.WAYPOINT_ID)
			)
		);
		viewer.addCheckStateListener(new CheckboxTreeViewerGreyStateUpdater());
		viewer.expandAll();
		Composite browseComposite = new Composite(page, SWT.NONE);
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
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				dialog.setFilterExtensions(new String[] {"*.xml"});
				String text = dialog.open();
				text = (text != null) ? text : "";
				fileText.setText(text);
			}
		});
		if (initialChecks != null) {
			initializeCheckedState();
		}
		updateErrorMessages();
		setControl(page);
	}
	
	/**
	 * 
	 */
	private void initializeCheckedState() {
		IResourceWaypointDescriptor[] descriptors = new IResourceWaypointDescriptor[initialChecks.length];
		ITreeContentProvider provider = (ITreeContentProvider) viewer.getContentProvider();
		HashSet<Object> parents = new HashSet<Object>();
		for (int i = 0; i < initialChecks.length; i++) {
			descriptors[i] = new ResourceWaypointProxyDescriptor(initialChecks[i]);
			Object parent = provider.getParent(descriptors[i]);
			if (parent != null)	{
				parents.add(parent);
			}
		}
		viewer.setCheckedElements(descriptors);
		CheckboxTreeViewerGreyStateUpdater.initializeGrayedState(viewer);
	}

	

	/**
	 * @param text
	 */
	public void setPath(String text) {
		this.path = text;
		updateErrorMessages();
		
	}
	
	/**
	 * 
	 */
	private void updateErrorMessages() {
		if (this.path == null || "".equals(path)) {
			setErrorMessage("Must set a target path");
			setPageComplete(false);
			return;
		}
		if (viewer.getCheckedElements().length == 0) {
			setErrorMessage("Must select at least one waypoint");
			setPageComplete(false);
			return;
		}
		setErrorMessage(null);
		setPageComplete(true);
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	public IWaypoint[] getSelectedWaypoints() {
		List<IResourceWaypointDescriptor> list = getSelectedDescriptorList();
		IWaypoint[] result = new IWaypoint[list.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = ((ResourceWaypointProxyDescriptor)list.get(i)).getWaypoint();
		}
		return result;
	}
	
	public IResourceWaypointDescriptor[] getSelectedDescriptors() {
		List<IResourceWaypointDescriptor> list = getSelectedDescriptorList();
		return list.toArray(new IResourceWaypointDescriptor[list.size()]);
	}
	
	public List<IResourceWaypointDescriptor> getSelectedDescriptorList() {
		List<IResourceWaypointDescriptor> list = new ArrayList<IResourceWaypointDescriptor>();
		for (Object o : viewer.getCheckedElements()) {
			if (o instanceof IResourceWaypointDescriptor) {
				list.add((IResourceWaypointDescriptor) o);
			}
		}
		return list;
	}
	
	
	
	/**
	 * Sets the initial waypoints that are checked in the page.
	 * @param checks the initial waypoints to check.
	 */
	public void setInitialChecks(IWaypoint[] checks) {
		this.initialChecks = checks;
	}
}
