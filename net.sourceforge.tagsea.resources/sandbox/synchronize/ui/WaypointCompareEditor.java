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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.synchronize.WaypointSynchronizeObject;
import net.sourceforge.tagsea.resources.synchronize.WaypointSynchronizeObject.ValueComparison;
import net.sourceforge.tagsea.resources.ui.ISharedImages;
import net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import sun.security.action.GetLongAction;

/**
 * An editor to compare two waypoints.
 * @author Del Myers
 *
 */
public class WaypointCompareEditor extends EditorPart implements IReusableEditor {

	
	public static final String ID = "net.sourceforge.tagsea.resources.compareEditor";  //$NON-NLS-1$
	private TableViewer viewer;
	private Action copyLeftAction;
	private Action copyRightAction;
	private Action copyLeftAllAction;
	private Action copyRightAllAction;
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			refreshActions();
		}
	};
	private boolean dirty;
	private static final String[] COLUMNS = new String[] {"Property", "Local Value", "Remote Value"};
	
	
	
	
	private class WaypointCompareContentProvider implements IStructuredContentProvider {

		private WaypointSynchronizeEditorInput input;
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			
				if (inputElement instanceof WaypointSynchronizeEditorInput) {
					this.input = (WaypointSynchronizeEditorInput) inputElement;
					WaypointSynchronizeObject obj = input.getSync();
					return new WaypointSynchronizeObject.ValueComparison[] {
						obj.getValueComparison(IResourceWaypointAttributes.ATTR_MESSAGE),
						obj.getValueComparison(IResourceWaypointAttributes.ATTR_AUTHOR),
						obj.getValueComparison(IResourceWaypointAttributes.ATTR_DATE),
						obj.getValueComparison(IResourceWaypointAttributes.ATTR_LINE),
						obj.getValueComparison(IResourceWaypointAttributes.ATTR_CHAR_START),
						obj.getValueComparison(IResourceWaypointAttributes.ATTR_CHAR_END),
						
						obj.getTagsComparison()
					};
					
				}
				
			
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
			this.input = (WaypointSynchronizeEditorInput) newInput;			
		}
		
	}

	private class WaypointCompareLableProvider implements ITableLabelProvider, ITableColorProvider {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			WaypointSynchronizeObject.ValueComparison row = (WaypointSynchronizeObject.ValueComparison) element;
			if (columnIndex == 0) {
				if (!(row.getLocalValue() == null || row.getRemoteValue() == null)) {
					if (row.getLocalValue().equals(row.getRemoteValue()))
						return null;
				} else if (row.getLocalValue() == null && row.getRemoteValue() == null){
					//shouldn't happen
					return null;
				}

				String imageKey = null;
				switch (row.getSync().getKind()) {
				case WaypointSynchronizeObject.NEW_IN:
					imageKey = ISharedImages.IMG_NEW_IN;
					break;
				case WaypointSynchronizeObject.NEW_OUT:
					imageKey = ISharedImages.IMG_NEW_OUT;
					break;
				case WaypointSynchronizeObject.REMOVE_IN:
					imageKey = ISharedImages.IMG_REMOVE_IN;
					break;
				case WaypointSynchronizeObject.REMOVE_OUT:
					imageKey = ISharedImages.IMG_REMOVE_OUT;
					break;
				case WaypointSynchronizeObject.SYNCH_IN:
					imageKey = ISharedImages.IMG_SYNCH_IN;
					break;
				case WaypointSynchronizeObject.SYNCH_OUT:
					imageKey = ISharedImages.IMG_SYNCH_OUT;
					break;
				case WaypointSynchronizeObject.CONFLICT:
					imageKey = ISharedImages.IMG_CONFLICT;
					break;
				}
				if (imageKey != null) {
					return ResourceWaypointPlugin.getDefault().getImageRegistry().get(imageKey);
				}
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			WaypointSynchronizeObject.ValueComparison row = (WaypointSynchronizeObject.ValueComparison) element;
			String result = "";
			switch (columnIndex) {
			case 0:
				result = row.getAttribute().toString();
				break;
			case 1:
				Object val = row.getLocalValue();
				if (val != null) {
					result = val.toString();
				}
				break;
			case 2:
				val = row.getRemoteValue();
				if (val != null) {
					result = val.toString();
				}
				break;
			}
			return result;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
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
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
		 */
		public Color getBackground(Object element, int columnIndex) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
		 */
		public Color getForeground(Object element, int columnIndex) {
			WaypointSynchronizeObject.ValueComparison row = (WaypointSynchronizeObject.ValueComparison) element;
			if (!(row.getLocalValue() == null || row.getRemoteValue() == null)) {
				if (row.getLocalValue().equals(row.getRemoteValue()))
					return null;
			} else if (row.getLocalValue() == null && row.getRemoteValue() == null){
				//shouldn't happen
				return null;
			}
			Color c = null;
			switch (row.getSync().getKind()) {
			case WaypointSynchronizeObject.NEW_IN:
			case WaypointSynchronizeObject.REMOVE_IN:
			case WaypointSynchronizeObject.SYNCH_IN:
				c = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
				break;
			case WaypointSynchronizeObject.NEW_OUT:
			case WaypointSynchronizeObject.REMOVE_OUT:
			case WaypointSynchronizeObject.SYNCH_OUT:
				c = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
				break;
			case WaypointSynchronizeObject.CONFLICT:
				c = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				break;
			}
			return c;
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void setInput(IEditorInput input) {
		IEditorInput old = getEditorInput();
		super.setInput(input);
		firePropertyChange(PROP_INPUT);
		if (old == null || !old.equals(input))
			refresh();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (getEditorInput() instanceof WaypointSynchronizeEditorInput) {
			IStatus status = 
				((WaypointSynchronizeEditorInput)getEditorInput()).getSync().commit();
			if (status.isOK()) {
				setDirty(false);
			}
			
		}
	}

	/**
	 * @param b
	 */
	private void setDirty(boolean b) {
		if (b != isDirty()) {
			this.dirty = b;
			firePropertyChange(PROP_DIRTY);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setInput(input);
		setSite(site);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite page = new Composite(parent, SWT.BORDER);
		page.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		GridLayout layout = new GridLayout();
		page.setLayout(layout);
		ToolBar bar = new ToolBar(page, SWT.RIGHT);
		bar.setLayoutData(new GridData(SWT.TRAIL, SWT.FILL, true, false));
		ToolBarManager manager = new ToolBarManager(bar);
		Table table = new Table(page, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		TableLayout tableLayout = new TableLayout();
		GridData data = new GridData();
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=false;
		data.horizontalAlignment=SWT.FILL;
		data.verticalAlignment=SWT.CENTER;
		table.setLayoutData(data);
		tableLayout.addColumnData(new ColumnWeightData(10));
		tableLayout.addColumnData(new ColumnWeightData(45));
		tableLayout.addColumnData(new ColumnWeightData(45));
		TableColumn c = new TableColumn(table, SWT.NONE);
		c.setText(COLUMNS[0]);
		c = new TableColumn(table, SWT.NONE);
		c.setText(COLUMNS[1]);
		c = new TableColumn(table, SWT.NONE);
		c.setText(COLUMNS[2]);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayout(tableLayout);
		TableViewer viewer = new TableViewer(table);
		viewer.setColumnProperties(COLUMNS);
		viewer.setContentProvider(new WaypointCompareContentProvider());
		viewer.setLabelProvider(new WaypointCompareLableProvider());
		this.viewer = viewer;
		this.viewer.addSelectionChangedListener(selectionListener);
		makeActions();
		initializeToolbar(manager);
		manager.update(false);
		
		refresh();
		
	}

	/**
	 * 
	 */
	private void makeActions() {
		this.copyLeftAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.size() > 0) {
					WaypointSynchronizeObject.ValueComparison row = (WaypointSynchronizeObject.ValueComparison)ss.getFirstElement();
					row.copyLeft();
					setDirty(true);
					viewer.update(row, COLUMNS);
				}
			}
		};
		copyLeftAction.setImageDescriptor(ResourceWaypointPlugin.getImageDescriptor("icons/checkout_action.gif"));
		copyLeftAction.setText("Copy Value Left");
		this.copyRightAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.size() > 0) {
					WaypointSynchronizeObject.ValueComparison row = (WaypointSynchronizeObject.ValueComparison) ss.getFirstElement();
					row.copyRight();
					setDirty(true);
					viewer.update(row, COLUMNS);
				}
			}
		};
		copyRightAction.setImageDescriptor(ResourceWaypointPlugin.getImageDescriptor("icons/checkin_action.gif"));
		copyRightAction.setText("Copy Value Right");
		this.copyLeftAllAction = new Action() {
			@Override
			public void run() {
				WaypointSynchronizeEditorInput input = (WaypointSynchronizeEditorInput) getEditorInput();
				WaypointSynchronizeObject sync = input.getSync();
				sync.checkout();
				setDirty(true);
				viewer.refresh();
			}
		};
		copyLeftAllAction.setImageDescriptor(ResourceWaypointPlugin.getImageDescriptor("icons/copy_in_all.gif"));
		copyLeftAllAction.setText("Copy All Changes Left");
		this.copyRightAllAction = new Action() {
			@Override
			public void run() {
				WaypointSynchronizeEditorInput input = (WaypointSynchronizeEditorInput) getEditorInput();
				WaypointSynchronizeObject sync = input.getSync();
				sync.checkIn();
				setDirty(true);
				viewer.refresh();
			}
		};
		copyRightAllAction.setImageDescriptor(ResourceWaypointPlugin.getImageDescriptor("icons/copy_out_all.gif"));
		copyRightAllAction.setText("Copy All Changes Left");
	}
	
	private void initializeToolbar(IToolBarManager manager) {
		manager.add(copyLeftAllAction);
		manager.add(copyRightAllAction);
		manager.add(copyLeftAction);
		manager.add(copyRightAction);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}
	
	private void refresh() {
		if (viewer != null && getEditorInput() != null && !viewer.getTable().isDisposed()) {
			viewer.setInput(getEditorInput());
			viewer.getTable().getParent().layout();
			refreshActions();
		}
	}

	/**
	 * Refreshes the active state of the actions.
	 */
	private void refreshActions() {
		if (getEditorInput() instanceof WaypointSynchronizeEditorInput) {
			WaypointSynchronizeEditorInput input = (WaypointSynchronizeEditorInput) getEditorInput();
			switch (input.getSync().getKind()) {
			case WaypointSynchronizeObject.EQUAL:
			case WaypointSynchronizeObject.NEW_IN:
			case WaypointSynchronizeObject.NEW_OUT:
			case WaypointSynchronizeObject.REMOVE_IN:
			case WaypointSynchronizeObject.REMOVE_OUT:
				this.copyLeftAction.setEnabled(false);
				this.copyRightAction.setEnabled(false);
				break;
			default:
				this.copyLeftAction.setEnabled(true);
				this.copyRightAction.setEnabled(true);
			}
		}
		if (copyLeftAction.isEnabled() || copyRightAction.isEnabled()) {
			IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
			Object left = null;
			Object right = null;
			if (!ss.isEmpty()) {
				ValueComparison comparison = (ValueComparison) ss.getFirstElement();
				left = comparison.getLocalValue();
				right = comparison.getRemoteValue();
			}
			if (left != null && right != null && !left.equals(right)) {
				this.copyLeftAction.setEnabled(true);
				this.copyRightAction.setEnabled(true);
			} else {
				this.copyLeftAction.setEnabled(false);
				this.copyRightAction.setEnabled(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		viewer.removeSelectionChangedListener(selectionListener);
	}

	

}
