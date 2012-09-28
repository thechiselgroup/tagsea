/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tours.fx.wizards;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.themes.FontDefinition;
import org.eclipse.ui.internal.themes.IThemeRegistry;

import com.ibm.research.tours.adapters.StructuredContentProviderAdapter;
import com.ibm.research.tours.adapters.TableLabelProviderAdapter;
import com.ibm.research.tours.controls.FilteredTable;
import com.ibm.research.tours.controls.NQueryTablePatternFilter;

public class ResetFontPreferenceWizardPage extends WizardPage {

	private String id, fLabel;
	private FilteredTable fFontPrefRegistry;

	protected ResetFontPreferenceWizardPage(String pageName) 
	{
		super(pageName);
		setTitle("Font preferences");
		setDescription("Select the font preferences for the element.");
	}
	
	public void createControl(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 6;
		composite.setLayout(layout);

		Label label = new Label(composite,SWT.WRAP);
		label.setText("Configure font for this element");
		GridData data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		label.setData(data);
		

		ITableLabelProvider labelProvider = new TableLabelProviderAdapter(){
			public String getColumnText(Object element, int columnIndex) {
				if ( element instanceof FontDefinition )
				{
					FontDefinition defn = (FontDefinition) element;
					if ( columnIndex==0 )
						return defn.getName();
					else if ( columnIndex==1 )
						return defn.getDescription();
				}
				return null;
			}
		};
		
		IContentProvider contentProvider = new StructuredContentProviderAdapter() {
			public Object[] getElements(Object inputElement) {
				IThemeRegistry registry = WorkbenchPlugin.getDefault().getThemeRegistry();
				FontDefinition[] fontDefinitions = registry.getFonts();
				return fontDefinitions;
			}
		};
		
		fFontPrefRegistry = new FilteredTable(composite,SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL,new NQueryTablePatternFilter());
		fFontPrefRegistry.getViewer().setContentProvider(contentProvider);
		fFontPrefRegistry.getViewer().setSorter(new ViewerSorter());
		fFontPrefRegistry.getViewer().setLabelProvider(labelProvider);
		fFontPrefRegistry.getViewer().setInput(new Object());
		fFontPrefRegistry.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selected = (IStructuredSelection) event.getSelection();
				if ( selected.isEmpty() )
				{
					ResetFontPreferenceWizardPage.this.setPageComplete(false);
				}
				else
				{
					FontDefinition defn = (FontDefinition) selected.getFirstElement();
					ResetFontPreferenceWizardPage.this.setId(defn.getId());
					ResetFontPreferenceWizardPage.this.setLabel(defn.getName());
					ResetFontPreferenceWizardPage.this.setPageComplete(id!=null);
				}
			}
		});
		
		Table table = fFontPrefRegistry.getViewer().getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		
		TableColumn nameColumn = new TableColumn(table,SWT.LEFT,0);
		nameColumn.setWidth(240);
		
		TableColumn descColumn = new TableColumn(table,SWT.LEFT,1);
		descColumn.setWidth(640);		
		
		GridData registryListData = new GridData(GridData.FILL_HORIZONTAL);
		registryListData.heightHint = 120;
		registryListData.horizontalSpan = 2;
		fFontPrefRegistry.setLayoutData(registryListData);
		
		if ( id!=null )
		{
			IThemeRegistry registry = WorkbenchPlugin.getDefault().getThemeRegistry();
			FontDefinition[] fontDefinitions = registry.getFonts();
			for (FontDefinition defn : fontDefinitions )
			{
				if ( id.equals(defn.getId()) )
				{
					IStructuredSelection selection = new StructuredSelection(defn);
					fFontPrefRegistry.getViewer().setSelection(selection, true);
					break;
				}
			}
		}
		
		setPageComplete(id!=null);
		
		setControl(composite);
	}

	@Override
	public void dispose() 
	{
		super.dispose();
	}

	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setLabel(String label)
	{
		fLabel = label;
	}
	
	public String getLabel()
	{
		return fLabel;
	}

	public void init(String preferenceId, String label) {
		id = preferenceId;
		fLabel = label;
	}
}
