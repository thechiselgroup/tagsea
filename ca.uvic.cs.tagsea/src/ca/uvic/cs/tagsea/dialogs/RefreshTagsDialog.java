/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation
 *******************************************************************************/
package ca.uvic.cs.tagsea.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;

import ca.uvic.cs.tagsea.ITagseaImages;
import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.resource.TagDatabaseRefreshJob;
import ca.uvic.cs.tagsea.monitoring.TagSEAActionEvent;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;


public class RefreshTagsDialog extends MessageDialog 
{    
    private Button allButton, selectedButton;
    private CheckboxTableViewer projectNames;
    private Object[] selection;

    /**
     * Gets the text of the clean dialog, depending on whether the
     * workspace is currently in autobuild mode.
     * @return String the question the user will be asked.
     */
    private static String getQuestion() 
    {
       return "Scan workspace for @tags. This action will synchronize your tag database with all tagged resources.";
    }

    /**
     * Creates a new RefreshTagsDialog dialog.
     * 
     * @param window the window to create it in
     * @param selection the currently selected projects (may be empty)
     */
    public RefreshTagsDialog(IWorkbenchWindow window, IProject[] selection) 
    {
        super(
                window.getShell(),
                "Synchronize @tag database...", null, getQuestion(), QUESTION, new String[] {
                IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
        this.selection = selection;
        if (this.selection == null) {
			this.selection = new Object[0];
		}
        setShellStyle(SWT.RESIZE | getShellStyle());
        
        setDefaultImage(TagSEAPlugin.getDefault().getTagseaImages().getImage(ITagseaImages.IMG_WAYPOINT));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed(int buttonId) 
    {
    	boolean syncAll = allButton.getSelection();
    	
    	super.buttonPressed(buttonId);
    	
        if (buttonId == IDialogConstants.OK_ID)
        {
			doSynchronize(syncAll);
		}
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createCustomArea(Composite parent) 
    {
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = layout.marginHeight = 0;
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        area.setLayout(layout);
        area.setLayoutData(new GridData(GridData.FILL_BOTH));
        SelectionListener updateEnablement = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateEnablement();
            }
        };
        
        //first row
        allButton = new Button(area, SWT.RADIO);
        allButton.setText("Scan and synchronize all projects");
        allButton.setSelection(false);
        allButton.addSelectionListener(updateEnablement);
        selectedButton = new Button(area, SWT.RADIO);
        selectedButton.setText("Select projects to scan and synchronize");
        selectedButton.setSelection(true);
        selectedButton.addSelectionListener(updateEnablement);

        //second row
        createProjectSelectionTable(area);
        projectNames.getTable().setEnabled(true);
        return area;
    }

    private void createProjectSelectionTable(Composite radioGroup) 
    {
    	projectNames = CheckboxTableViewer.newCheckList(radioGroup, SWT.BORDER);
    	projectNames.setContentProvider(new WorkbenchContentProvider());
    	projectNames.setLabelProvider(new WorkbenchLabelProvider());
    	projectNames.setSorter(new ResourceSorter(ResourceSorter.NAME));
    	projectNames.addFilter(new ViewerFilter() 
    	{
    		private final IProject[] projectHolder = new IProject[1];
    		
			public boolean select(Viewer viewer, Object parentElement, Object element) 
			{
				if (!(element instanceof IProject)) {
					return false;
				}
				IProject project = (IProject) element;
				if (!project.isAccessible()) {
					return false;
				}
				projectHolder[0] = project;
				return true;
			}
		});
    	projectNames.setInput(ResourcesPlugin.getWorkspace().getRoot());
    	GridData data = new GridData(GridData.FILL_BOTH);
    	data.horizontalSpan = 2;
    	data.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
    	data.heightHint = IDialogConstants.ENTRY_FIELD_WIDTH;
    	projectNames.getTable().setLayoutData(data);
    	projectNames.setCheckedElements(selection);
    	//table is disabled to start because all button is selected
    	projectNames.getTable().setEnabled(selectedButton.getSelection());
    	projectNames.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				selection = projectNames.getCheckedElements();
				updateEnablement();
			}
		});
	}

    /**
     * Performs the actual synchronization operation.
     */
    protected void doSynchronize(boolean syncAll)
    {
    	TagDatabaseRefreshJob tagRefreshJob;

    	if(syncAll)
    		tagRefreshJob = new TagDatabaseRefreshJob();
    	else
    	{
    		List<IProject> projects = new ArrayList<IProject>();

    		for(Object o : selection)
    			projects.add((IProject)o);

    		tagRefreshJob = new TagDatabaseRefreshJob(projects);
    	}

    	tagRefreshJob.setUser(true);
    	tagRefreshJob.schedule();
    	Monitoring.getDefault().fireEvent(new TagSEAActionEvent(TagSEAActionEvent.Type.Refresh, ""));
    }

    /**
     * Updates the enablement of the dialog's ok button based
     * on the current choices in the dialog.
     */
    protected void updateEnablement() {
    	projectNames.getTable().setEnabled(selectedButton.getSelection());
        boolean enabled = allButton.getSelection() || selection.length > 0;
        getButton(OK).setEnabled(enabled);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#close()
     */
    public boolean close() 
    {
        return super.close();
    }
}
