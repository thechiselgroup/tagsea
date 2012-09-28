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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Wizard page for merging conflicting waypoints.
 * @author Del Myers
 *
 */
public class ResourceWaypointImportWizardPage2 extends
		AbstractResourceWaypointImportWizardPage {


	private CheckboxTreeViewer treeViewer;
	private HashSet<IResourceWaypointDescriptor> selectedWaypoints;
	private IResourceWaypointDescriptor[] descriptors;
	//map of descriptors 
	private HashMap<IResourceWaypointDescriptor, Set<String>> checkedMap;
	//set of descriptors that have their tags checked.
	private HashSet<IResourceWaypointDescriptor> tagCheckedSet;
	private CheckboxTableViewer tableViewer;
	private Label unselectLabel;
	private StackLayout tableStack;
	private Composite tableComposite;
	
	private static final String[] VISIBLE_ATTRIBUTES = {
		IResourceWaypointAttributes.ATTR_AUTHOR,
		IResourceWaypointAttributes.ATTR_DATE,
		IResourceWaypointAttributes.ATTR_MESSAGE,
		IResourceWaypointAttributes.ATTR_LINE,
		IResourceWaypointAttributes.ATTR_CHAR_START,
		IResourceWaypointAttributes.ATTR_CHAR_END,
		IResourceWaypointAttributes.ATTR_RESOURCE,
	};

	private static final String[] COLUMNS = new String[] {"Property", "New Value", "Old Value"};
	
	private class WaypointCompareContentProvider implements IStructuredContentProvider {
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof IResourceWaypointDescriptor) {
				//@tag tagsea.import.content : return strings for attributes.
				Object[] result = new Object[VISIBLE_ATTRIBUTES.length + 1];
				System.arraycopy(VISIBLE_ATTRIBUTES, 0, result, 0, VISIBLE_ATTRIBUTES.length);
				result[VISIBLE_ATTRIBUTES.length] = ((IResourceWaypointDescriptor)inputElement).getTags();
				return result;
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
		}
		
	}
	
	private class WaypointCompareLabelProvider implements ITableLabelProvider, IColorProvider {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			IResourceWaypointDescriptor selectedDescriptor = getSelectedDescriptor();
			IWaypointUIExtension ui = TagSEAPlugin.getDefault().getWaypointUI(ResourceWaypointPlugin.WAYPOINT_ID);
			if (selectedDescriptor == null) return "";
			switch (columnIndex) {
			case 0:
				if (element instanceof String) return ui.getAttributeLabel(element.toString());
				else return "Tags";
			case 1:
				if (element instanceof String) {
					return selectedDescriptor.getValue(element.toString()).toString();
				} else {
					return element.toString();
				}
			case 2:
				IWaypoint wp = getWaypointForId(new ResourceWaypointIdentifier(selectedDescriptor));
				if (wp == null) {
					return "";
				}
				if (element instanceof String) {
					return wp.getValue(element.toString()).toString();
				} else {
					TreeSet<String> tags = new TreeSet<String>();
					for (ITag t : wp.getTags()) {
						tags.add(t.getName());
					}
					return tags.toString();
				}
			}
			return "";
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
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
		 */
		public Color getBackground(Object element) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
		 */
		public Color getForeground(Object element) {
			IResourceWaypointDescriptor selected = getSelectedDescriptor();
			if (selected == null) return null;
			IWaypoint wp = getWaypointForId(new ResourceWaypointIdentifier(selected));
			if (wp == null) return null;
			if (element instanceof String) {
				Object wpValue = wp.getValue(element.toString());
				Object dValue = selected.getValue(element.toString());
				if (wpValue == null || dValue == null) {
					return null;
				}
				if (!wpValue.equals(dValue)) {
					return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				}
			} else {
				TreeSet<String> wpTags = new TreeSet<String>();
				for (ITag t : wp.getTags()) {
					wpTags.add(t.getName());
				}
				if (!wpTags.equals(selected.getTags())) {
					return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				}
			}
			return null;
		}
		
	}
	
	protected ResourceWaypointImportWizardPage2() {
		super("Merge Waypoints", "Import Resource Waypoints",ResourceWaypointPlugin.getImageDescriptor("icons/bigicon.png"));
		this.selectedWaypoints = new HashSet<IResourceWaypointDescriptor>();
		checkedMap = new HashMap<IResourceWaypointDescriptor, Set<String>>();
		tagCheckedSet = new HashSet<IResourceWaypointDescriptor>();
		this.descriptors = new IResourceWaypointDescriptor[0];
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.sharing.ui.AbstractResourceWaypointImportWizardPage#calculateMergedDescriptors()
	 */
	@Override
	protected IResourceWaypointDescriptor[] calculateMergedDescriptors() {
		IResourceWaypointDescriptor[] descriptors = getDescriptors();
		IResourceWaypointDescriptor[] result = new IResourceWaypointDescriptor[descriptors.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = mergeDescriptor(descriptors[i], checkedMap.get(descriptors[i]), tagCheckedSet.contains(descriptors[i]));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.sharing.ui.AbstractResourceWaypointImportWizardPage#getDescriptors()
	 */
	@Override
	protected IResourceWaypointDescriptor[] getDescriptors() {
		return selectedWaypoints.toArray(new IResourceWaypointDescriptor[selectedWaypoints.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout());
		this.treeViewer = new CheckboxTreeViewer(page, SWT.BORDER | SWT.SINGLE);
		treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		treeViewer.setContentProvider(new ResourceWaypointTreeContentProvider());
		treeViewer.setLabelProvider(new DecoratingLabelProvider(
				new ResourceWaypointTreeLabelProvider(), new MultiLabelDecorator(
						new ILabelDecorator[] { 
							new ResourceWaypointLabelDecorator(),
							new MissingResourceLabelDecorator(),
							new ConflictWaypointLabelDecorator(false, true)
						}
					)
				)
		);
		treeViewer.setInput(
			new ResourceWaypointTree(descriptors)
		);
		treeViewer.expandAll();
		treeViewer.addCheckStateListener(new ICheckStateListener(){
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateTreeCheckState((CheckboxTreeViewer) event.getCheckable(), event.getChecked(), event.getElement());
				selectedWaypoints.clear();
				for (Object o : treeViewer.getCheckedElements()) {
					if (o instanceof IResourceWaypointDescriptor) {
						selectedWaypoints.add((IResourceWaypointDescriptor) o);
					}
				}
				tableViewer.refresh();
				updateWarnings();
			}

			private void updateTreeCheckState(CheckboxTreeViewer viewer, boolean checked, Object element) {
//				sometimes content providers don't follow the API for ITreeContentProvider, and
				//neglect to supply parents because they are too hard to calculate. So, use
				//the elements in the tree instead.
				Tree tree = viewer.getTree();
				TreeItem[] children = tree.getItems();
				//breadth-first search for the element.
				LinkedList<TreeItem> items = new LinkedList<TreeItem>();
				TreeItem item = null;
				for (TreeItem child : children) {
					items.add(child);
				}
				while (items.size() > 0) {
					TreeItem current = items.removeFirst();
					if (current.getData() == element) {
						item = current;
						break;
					}
					TreeItem[] childItems = current.getItems();
					for (TreeItem child : childItems) {
						items.add(child);
					}
				}
				if (item == null) return;
				//get all of the leafs for the item.
				items.clear();
				LinkedList<Object> leafElements = new LinkedList<Object>();
				items.add(item);
				while (items.size() > 0) {
					TreeItem current = items.removeFirst();
					TreeItem[] childItems = current.getItems();
					if (childItems.length == 0) {
						leafElements.add(current.getData());
					} else {
						for (TreeItem child : childItems) {
							items.add(child);
						}
					}
				}
				updateTreeGrayedState(viewer, leafElements, checked);
			}
		});
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection s = (IStructuredSelection) event.getSelection();
				IResourceWaypointDescriptor d = null;
				if (s.size() > 0) {
					Object o = s.getFirstElement();
					if (o instanceof IAdaptable) {
						d = (IResourceWaypointDescriptor) ((IAdaptable)o).getAdapter(IResourceWaypointDescriptor.class);
					}
				}
				if (d != null) {
					tableStack.topControl = tableViewer.getControl();
					tableViewer.setInput(d);
					((Composite)getControl()).layout();
					List<Object> checks = new ArrayList<Object>(checkedMap.get(d));
					if (tagCheckedSet.contains(d)) {
						checks.add(d.getTags());
					}
					tableViewer.setCheckedElements(checks.toArray());
					//set the enabled state based on whether or not the waypoint
					//exists in the workspace.
					IWaypoint wp = getWaypointForId(new ResourceWaypointIdentifier(d));
					tableViewer.getControl().setEnabled(wp != null);
				} else {
					tableViewer.setInput(null);
					tableStack.topControl = unselectLabel;
					((Composite)getControl()).layout();
				}
			}
		});
		
		tableComposite = new Composite(page, SWT.NONE);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=false;
		data.horizontalAlignment=SWT.FILL;
		data.verticalAlignment=SWT.FILL;
		tableComposite.setLayoutData(data);
		tableStack = new StackLayout();
		tableComposite.setLayout(tableStack);
		
		unselectLabel = new Label(tableComposite, SWT.NONE);
		unselectLabel.setText("Please select a waypoint");		
		Table table = new Table(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE  | SWT.CHECK);
		TableLayout tableLayout = new TableLayout();
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
		tableViewer = new CheckboxTableViewer(table);
		tableViewer.setColumnProperties(COLUMNS);
		tableViewer.setContentProvider(new WaypointCompareContentProvider());
		tableViewer.setLabelProvider(new WaypointCompareLabelProvider());
		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				//add the checked line to the list of checks.
				boolean checked = event.getChecked();
				Object element = event.getElement();
				IResourceWaypointDescriptor selected = getSelectedDescriptor();
				Set<String> attrs = checkedMap.get(selected);
				if (selected != null) {
//					@tag tagsea.import.content : content provider returns strings for attributes.
					if (element instanceof String) {
						if (attrs == null) {
							attrs = new TreeSet<String>();
							checkedMap.put(selected, attrs);
						}
						if (checked) {
							attrs.add(element.toString());
						} else {
							attrs.remove(element.toString());
						}
					} else {
						//otherwise, the checked content are the tags.
						if (checked) {
							tagCheckedSet.add(selected);
						} else {
							tagCheckedSet.remove(selected);
						}
					}
				}
				//update the checked state in the tree.
				boolean elementChecked = tagCheckedSet.contains(selected) || checkedMap.size() > 0;
				treeViewer.setChecked(selected, elementChecked);
				LinkedList<Object> list = new LinkedList<Object>();
				list.add(selected);
				updateTreeGrayedState(treeViewer, list, elementChecked);
				updateWarnings();
			}
		});
		tableStack.topControl = unselectLabel;
		tableComposite.layout();
		
		setControl(page);
	}
	
	/**
	 * @param leaves
	 */
	protected void updateTreeGrayedState(CheckboxTreeViewer treeViewer, List<?> leaves, boolean checked) {
		for (Object leaf : leaves) {
			if (checked) {
				if (leaf instanceof IResourceWaypointDescriptor) {
					IResourceWaypointDescriptor d = (IResourceWaypointDescriptor) leaf;
					Set<String> checkedAttributes = checkedMap.get(d);
					int checkCount = checkedAttributes.size() +
						(tagCheckedSet.contains(d) ? 1 : 0);
					int expectedCount = VISIBLE_ATTRIBUTES.length + 1;
					if (checkedAttributes != null) {
						if (checkCount == 0) {
							treeViewer.setChecked(leaf, false);
							treeViewer.setGrayed(leaf, false);
						} else if (checkCount == expectedCount) {
							treeViewer.setChecked(leaf, true);
							treeViewer.setGrayed(leaf, false);
						} else if (checkCount < expectedCount) {
							treeViewer.setChecked(leaf, true);
							treeViewer.setGrayed(leaf, true);
						}
					}
				}
			}  else {
				treeViewer.setChecked(leaf, false);
				treeViewer.setGrayed(leaf, false);
			}
		}
		
		//@tag tagsea.performance : this could be made faster if we didn't refresh the whole tree.
		CheckboxTreeViewerGreyStateUpdater.initializeGrayedState(treeViewer);
	}

	public void setDescriptors(IResourceWaypointDescriptor[] descriptors) {
		this.descriptors = descriptors;
		selectedWaypoints.addAll(Arrays.asList(descriptors));
		calculateConflicts();
		refreshViewers();
	}
	
	/**
	 * 
	 */
	private void updateWarnings() {
		//warn if at least one checked waypoint is on a resource that doesn't exist.
		for (IResourceWaypointDescriptor d : selectedWaypoints) {
			IResource r = getResourceForDescriptor(d);
			if (r == null) {
				setMessage("Resource does not exist: " + d.getResource(), WARNING);
				return;
			}
		}
		setMessage(null);
	}
	

	/**
	 * 
	 */
	private void refreshViewers() {
		treeViewer.setInput(new ResourceWaypointTree(descriptors));
		updateTreeGrayedState(treeViewer, Arrays.asList(descriptors), true);
	}
	
	




	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			IWizardPage previous = getWizard().getPreviousPage(this);
			if (previous instanceof AbstractResourceWaypointImportWizardPage) {
				setDescriptors(((AbstractResourceWaypointImportWizardPage)previous).getDescriptors());
			}
		}
		super.setVisible(visible);
	}

	/**
	 * 
	 */
	private void calculateConflicts() {
		for (IResourceWaypointDescriptor d : descriptors) {
			Set<String> atts = findConflictingAttributes(d, true, true);
			if (atts == null) {
				atts = new TreeSet<String>(Arrays.asList(VISIBLE_ATTRIBUTES));
				tagCheckedSet.add(d);
				selectedWaypoints.remove(d);
			}
			checkedMap.put(d, atts);
			if (doTagsConflict(d)) {
				tagCheckedSet.add(d);
			}
		}
	}
	
	/**
	 * @return
	 */
	private IResourceWaypointDescriptor getSelectedDescriptor() {
		IStructuredSelection s = (IStructuredSelection) treeViewer.getSelection();
		if (s.size() > 0) {
			Object o = s.getFirstElement();
			if (o instanceof IAdaptable) {
				return	(IResourceWaypointDescriptor) ((IAdaptable)o).getAdapter(IResourceWaypointDescriptor.class);
			}
		}
		return null;
	}

}
