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
package net.sourceforge.tagsea.core.ui.tags;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.TagSEAUtils;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;
import net.sourceforge.tagsea.core.ui.internal.waypoints.DateCellEditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

/**
 * 
 * @author Del Myers
 * @author Jody Ryall
 *
 */
public class WaypointPropertiesDialog extends StatusDialog {

	private IWaypoint waypoint;
	private CellEditor[] editors;
	private HashMap<Object, CellEditor> propertyEditorMap;
	private TableViewer propertiesViewer;
	private final String[] COLUMN_PROPERTIES = {"key", "value"};
	private HashMap<String, Object> propertiesMap;
	
	private Button addButton;
	private Text tagText;
	private TreeSet<String> tagNames;
	private ListViewer tagNameListViewer;

	private class PropertiesContentProvider implements IStructuredContentProvider {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof HashMap) {
				IWaypointUIExtension extension = 
					TagSEAPlugin.getDefault().getUI().getWaypointUI(waypoint.getType());
				return extension.getVisibleAttributes();
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
	
	private class PropertiesLabelProvider implements ITableLabelProvider {
	
		/**
		 * 
		 */
		public PropertiesLabelProvider() {
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			if (waypoint == null) return null;
			IWaypointUIExtension extension = TagSEAPlugin.getDefault().getWaypointUI(waypoint.getType());
			switch (columnIndex) {
			case 1:
				return extension.getValueImage(waypoint, element.toString());
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			String text = null;
			if (waypoint == null) return "";
			IWaypointUIExtension extension = TagSEAPlugin.getDefault().getWaypointUI(waypoint.getType());
			switch (columnIndex) {
			case 0:
				text = extension.getAttributeLabel(waypoint, element.toString());
				break;
			case 1:
				Object value = propertiesMap.get(element);
				if (value != null)
				text = extension.getValueLabel(waypoint, element.toString(), value);
				break;
			}
			return (text != null) ? text : "";
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
		
		
		
	}
	
	/**
	 * Modifier for the properties portion of the waypoint editor.
	 * @author Del Myers
	 */

	public class WaypointCellModifier implements ICellModifier {


		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
		 */
		public boolean canModify(Object element, String property) {
			if (!"value".equals(property)) return false;
			IWaypointUIExtension ext = TagSEAPlugin.getDefault().getWaypointUI(waypoint.getType());
			boolean modify = ext.canUIChange(waypoint, element.toString());
			if (modify) {
				editors[1] = createEditorForElement(element);
			}
			return modify;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
		 */
		public Object getValue(Object element, String property) {
			if ("key".equals(property)) return element;
			Object value = waypoint.getValue(element.toString());
			if (!(value instanceof Date)) return value.toString();
			return value;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
		 */
		public void modify(Object element, String property, Object value) {
			if (element instanceof Item) {
				//this is due to a bug in JFace, we get the item instead of the model element.
				element = ((Item)element).getData();
			}
			if ("key".equals(property)) return;
			AbstractWaypointDelegate delegate = TagSEAPlugin.getDefault().getWaypointDelegate(waypoint.getType());
			Class<?> type = delegate.getAttributeType(element.toString());
			if (Boolean.class.equals(type)) {
				value = Boolean.getBoolean(value.toString());
			} else if (Integer.class.equals(type)) {
				value = Integer.parseInt(value.toString());
			}
			propertiesMap.put(element.toString(), value);
			propertiesViewer.update(element, COLUMN_PROPERTIES);
		}
		

		private CellEditor createEditorForElement(Object element) {
			String key = element.toString();
			AbstractWaypointDelegate delegate = TagSEAPlugin.getDefault().getWaypointDelegate(waypoint.getType());
			IWaypointUIExtension ext = TagSEAPlugin.getDefault().getWaypointUI(waypoint.getType());
			if (!ext.canUIChange(waypoint, key)) return null;
			Class<?> type = delegate.getAttributeType(key);
			if (type == null) return null;
			if (String.class.equals(type)) {
				return new TextCellEditor(propertiesViewer.getTable());
			} else if (Integer.class.equals(type)) {
				return new TextCellEditor(propertiesViewer.getTable());
			} else if (Date.class.equals(type)) {
				return new DateCellEditor(propertiesViewer.getTable());
			}
			return null;
		}

	}
	
	
	private class CellEditorAdapter extends MouseAdapter {
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.MouseAdapter#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
		 */
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			if (e.widget != propertiesViewer.getTable()) return;
			Table table = propertiesViewer.getTable();
			TableItem item = table.getItem(new Point(e.x, e.y));
			openEditorFor(item);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt.events.MouseEvent)
		 */
		@Override
		public void mouseDown(MouseEvent e) {
		}
	}
	
	/**
	 * @param parentShell
	 */
	public WaypointPropertiesDialog(Shell parentShell, IWaypoint waypoint) {
		super(parentShell);
		this.waypoint = waypoint;
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		this.propertiesMap = new HashMap<String, Object>();
		String[] visibleAttributes = 
			TagSEAPlugin.getDefault().getUI().getWaypointUI(waypoint.getType()).getVisibleAttributes();
		for (String property : visibleAttributes) {
			Object value = waypoint.getValue(property);
			if (value != null) {
				propertiesMap.put(property, waypoint.getValue(property));
			}
		}
		
		this.tagNames = new TreeSet<String>();
		
		if (waypoint != null) {
			setInitialTags(waypoint.getTags());
			final IWaypoint selectedWaypoint = waypoint;
			final String[] tagNames = getTagNames();
			TagSEAPlugin.run(new TagSEAOperation("Checking Tag Names..."){
				@Override
				public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
					TreeSet<String> oldTagNames = new TreeSet<String>();
					TreeSet<String> newTagNames = new TreeSet<String>(Arrays.asList(tagNames));
					for (ITag tag : selectedWaypoint.getTags()) {
						oldTagNames.add(tag.getName());
					}
					MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
					for (String oldName : oldTagNames) {
						if (!newTagNames.contains(oldName)) {
							ITag tag = TagSEAPlugin.getTagsModel().getTag(oldName);
							if (tag != null) {
								status.merge(selectedWaypoint.removeTag(tag).getStatus());
							}
						}
					}
					for (String newName : newTagNames) {
						if (!oldTagNames.contains(newName)) {
							ITag tag = selectedWaypoint.addTag(newName);
							if (tag == null) {
								status.merge(new Status(
									IStatus.WARNING,
									TagSEAPlugin.PLUGIN_ID,
									IStatus.WARNING,
									"Could not add tag " + newName,
									null
								));
							}
						}
					}
					return status;
				}}, false);
//			}
		}
	}
	
	public void setInitialNames(String[] tagNames) {
		this.tagNames.clear();
		this.tagNames.addAll(Arrays.asList(tagNames));
		this.tagNames.remove(ITag.DEFAULT);
	}
	
	public void setInitialTags(ITag[] tags) {
		this.tagNames.clear();
		for (ITag tag : tags) {
			if (!ITag.DEFAULT.equals(tag.getName()))
				this.tagNames.add(tag.getName());
		}
	}
	
	public String[] getTagNames() {
		return tagNames.toArray(new String[tagNames.size()]);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite propertiesComposite = (Composite) super.createDialogArea(parent);
		propertiesComposite.setLayout(new GridLayout(2, false));
		
		//Tag area
		Group area = new Group(propertiesComposite, SWT.FLAT);
		area.setText("Tags");
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		area.setLayout(new GridLayout(1, false));
		
		// List of tags
		Composite listComposite = new Composite(area, SWT.FLAT);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		listComposite.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint=100;
		data.widthHint=200;
		listComposite.setLayoutData(data);
		List tagNameList = new List(listComposite, SWT.FLAT | SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		tagNameListViewer = new ListViewer(tagNameList);
		tagNameListViewer.setContentProvider(new ArrayContentProvider());
		tagNameList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tagNameListViewer.setInput(tagNames);
		
		// Area for buttons
//		Group area2 = new Group(area, SWT.FLAT);
//		area2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		area2.setLayout(new GridLayout(3, false));
		
		Composite textComposite = new Composite(listComposite, SWT.FLAT);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		textComposite.setLayout(layout);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		textComposite.setLayoutData(data);
		ContentAssistCommandAdapter tagAssist = createTagText(textComposite, SWT.BORDER);
		tagText = (Text) tagAssist.getControl();
		tagText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				String text = tagText.getText();
				text = text.trim();
				if ("".equals(text)) {
					updateStatus(Status.OK_STATUS);
					addButton.setEnabled(false);
					return;
				}
				int valid = TagSEAUtils.isValidTagName(text);
				switch (valid) {
				case TagSEAUtils.TAG_NAME_BAD_CHARACTER:
					updateStatus(
						new Status(
							IStatus.ERROR, 
							TagSEAPlugin.PLUGIN_ID, 
							IStatus.ERROR, 
							"Tag name contains an invalid character.", 
							null
						)
					);
					break;
				case TagSEAUtils.TAG_NAME_SYNTAX_ERROR:
					updateStatus(
						new Status(
							IStatus.ERROR, 
							TagSEAPlugin.PLUGIN_ID, 
							IStatus.ERROR, 
							"Tag name has bad syntax.", 
							null
						)
					);
					break;
				case TagSEAUtils.TAG_NAME_VALID:
					updateStatus(Status.OK_STATUS);
					break;
				}
				addButton.setEnabled(getStatus().isOK());
			}
		});
		tagText.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				getShell().setDefaultButton(addButton);
			}

			public void focusLost(FocusEvent e) {
				getShell().setDefaultButton(getButton(Dialog.OK));
			}
		});
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalIndent = 15;
		tagText.setLayoutData(gd);
		addButton = new Button(textComposite, SWT.PUSH);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		addButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				updateTagsForText();
				tagText.setText("");
			}});
		addButton.setText("Add");
		tagText.setText("");
		
		// Delete tag button
		Button deleteTagButton = new Button(area, SWT.PUSH);
		deleteTagButton.setText("Delete");
//		deleteTagButton.setEnabled(false);
		deleteTagButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tagNameListViewer.getSelection();
				for (Object o : selection.toList()) {
					tagNames.remove(o);
				}
				tagNameListViewer.refresh();
			}
		});
		data = new GridData(SWT.END, SWT.END, false, false);
		data.horizontalAlignment = SWT.END;
		deleteTagButton.setLayoutData(data);

		//create a table to display the properties
		Table table = new Table(propertiesComposite, SWT.FLAT | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableGridData.widthHint = 350;
		table.setLayoutData(tableGridData);
		this.propertyEditorMap = new HashMap<Object, CellEditor>();
		this.propertiesViewer = new TableViewer(table);
		table.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(40));
		TableColumn column = new TableColumn(table, SWT.FLAT);
		column.setText("Property");
		column.setMoveable(true);
		tableLayout.addColumnData(new ColumnWeightData(100));
		column = new TableColumn(table, SWT.FLAT);
		column.setMoveable(true);
		column.setText("Value");
		table.setLinesVisible(true);
		table.setLayout(tableLayout);
		propertiesViewer.setContentProvider(new PropertiesContentProvider());
		propertiesViewer.setLabelProvider(new PropertiesLabelProvider());
		this.editors = new CellEditor[] {
			new TextCellEditor(),
			new TextCellEditor()
		};
		table.setHeaderVisible(true);
		propertiesViewer.setCellEditors(editors);
		propertiesViewer.setColumnProperties(COLUMN_PROPERTIES);
		propertiesViewer.setCellModifier(new WaypointCellModifier());
		propertiesViewer.setInput(propertiesMap);
		table.addMouseListener(new CellEditorAdapter());
		return propertiesComposite;
	}

	/**
	 * Creates a text area that can have content assist on it.
	 * @param textComposite
	 * @param border
	 * @return
	 */
	private ContentAssistCommandAdapter createTagText(Composite parent, int style) {
		Text text = new Text(parent, style);
		
		ContentAssistCommandAdapter field = new ContentAssistCommandAdapter(
			text,
			new TextContentAdapter(),
			new TagProposalProvider(parent),
			null,
			null,
			true
		);		
		return field;
	}
	
	void updateTagsForText() {
		String text = tagText.getText();
		text = text.trim();
		if (!"".equals(text)) {
			if (!tagNames.contains(text)) {
				tagNames.add(text);
				tagNameListViewer.refresh();
			}
		}
	}
	
	
	/**
	 * 
	 */
	private void openEditorFor(TableItem item) {
		Table table = propertiesViewer.getTable();
		CellEditor cellEditor = propertyEditorMap.get(item.getData());
		if (cellEditor != null) {
			TableEditor editor = new TableEditor(table);
			editor.grabHorizontal = true;
			editor.grabVertical = true;
			editor.setColumn(1);
			editor.setEditor(cellEditor.getControl(), item, 1);
			cellEditor.getControl().setVisible(true);
		}
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setImage(TagSEAPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_WAYPOINT));
		newShell.setText("Properties");
	}
	
	@Override
	protected void okPressed() {
		//apply the changes to the waypoint.
		for (String key : propertiesMap.keySet()) {
			Object localValue = propertiesMap.get(key);
			Object waypointValue = waypoint.getValue(key);
			if (localValue != null && waypointValue != null && !waypointValue.equals(localValue)) {
				waypoint.setObjectValue(key, localValue);
			}
		}
		if (getStatus().isOK()) {
			TreeSet<String> newSet = new TreeSet<String>();
			for (int i = 0; true ; i++) {
				Object element = tagNameListViewer.getElementAt(i);
				if (element == null) {
					break;
				}
				if (element instanceof String) {
					newSet.add(element.toString());
				}
			}
			for (ITag tag : waypoint.getTags()) {
				if (!newSet.contains(tag.getName())) {
					waypoint.removeTag(tag);
				}
			}
			for (String tagName : newSet) {
				waypoint.addTag(tagName);
			}
		}
		super.okPressed();
	}
}
