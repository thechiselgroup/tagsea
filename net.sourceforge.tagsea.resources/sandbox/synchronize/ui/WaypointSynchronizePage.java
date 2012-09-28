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
package net.sourceforge.tagsea.resources.synchronize.ui;



import java.util.HashSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.IWaypointChangeListener;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;
import net.sourceforge.tagsea.resources.synchronize.IWaypointSynchronizeListener;
import net.sourceforge.tagsea.resources.synchronize.WaypointSynchronizeObject;
import net.sourceforge.tagsea.resources.synchronize.WaypointSynchronizerHelp;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;

/**
 * A page that displays the 
 * @author Del Myers
 *
 */
public class WaypointSynchronizePage extends Page implements IWaypointChangeListener, IWaypointSynchronizeListener {
	
	private Composite control;
	private TreeViewer viewer;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout());
		page.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TreeViewer viewer = new TreeViewer(page, SWT.SINGLE);
		viewer.setContentProvider(new ProjectWaypointContentProvider());
		viewer.setLabelProvider(new WaypointSynchronizeLabelProvider());
		viewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.getTree().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				openEditor();
			}
		});
		viewer.getTree().addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshEditor();
			}
		});
		this.control = page;
		this.viewer = viewer;
		TagSEAPlugin.addWaypointChangeListener(this);
		WaypointSynchronizerHelp.INSTANCE.addSynchronizeListener(this);
	}

	protected void refreshEditor() {
		IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
		if (!ss.isEmpty()) {
			Object o = ss.getFirstElement();
			if (o instanceof WaypointSynchronizeObject) {
				IEditorInput input = new WaypointSynchronizeEditorInput((WaypointSynchronizeObject) o);
				IEditorReference[] editors = 
					getSite().
					getPage().
					findEditors(
							input, 
							WaypointCompareEditor.ID,
							IWorkbenchPage.MATCH_ID
					);
				for (IEditorReference e : editors) {
					IEditorPart editor = e.getEditor(false);
					if (editor instanceof IReusableEditor) {
						((IReusableEditor)editor).setInput(input);
						break;
					}
				}
				
			}
		}
	}
	
	/**
	 * 
	 */
	protected void openEditor() {
		IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
		if (!ss.isEmpty()) {
			Object o = ss.getFirstElement();
			if (o instanceof WaypointSynchronizeObject) {
				try {
					IEditorInput input = new WaypointSynchronizeEditorInput((WaypointSynchronizeObject) o);
					IEditorPart editor = 
						getSite().
						getPage().
						openEditor(
							input, 
							WaypointCompareEditor.ID, true, 
							IWorkbenchPage.MATCH_ID
						);
					if (editor instanceof IReusableEditor) {
						((IReusableEditor)editor).setInput(input);
					}
				} catch (PartInitException e) { 
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		TagSEAPlugin.removeWaypointChangeListener(this);
		WaypointSynchronizerHelp.INSTANCE.removeSynchronizeListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		return control;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeListener#waypointsChanged(net.sourceforge.tagsea.core.WaypointDelta)
	 */
	public void waypointsChanged(WaypointDelta delta) {
		HashSet<IProject> projectsToRefresh = new HashSet<IProject>();
		for (IWaypointChangeEvent e :delta.getChanges()) {
			if (e.getWaypoint().getType().equals(ResourceWaypointPlugin.WAYPOINT_ID)) {
				IResource resource = ResourceWaypointUtils.getResource(e.getWaypoint());
				if (resource != null && resource.isAccessible()) {
					projectsToRefresh.add(resource.getProject());
				}
			}
		}
		for (IProject project : projectsToRefresh) {
			final IProject p = project;
			getSite().getShell().getDisplay().asyncExec(new Runnable(){
				public void run() {
					viewer.refresh(p, true);
				}
			});
			
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.synchronize.IWaypointSynchronizeListener#synchronizationChanged(org.eclipse.core.resources.IProject)
	 */
	public void synchronizationChanged(IProject project) {
		viewer.refresh(project);		
	}

}
