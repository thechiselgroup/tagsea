/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package com.ibm.research.tours.fx.dialogs;

import java.util.Vector;

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ibm.research.tours.content.SlideRange;
import com.ibm.research.tours.content.ToursContentPlugin;


public class TagPowerpointDialog extends StatusDialog {

	private FormToolkit fFormToolkit;
	private SlideRange fSlideRange;
	private Table fSlideTable;
	private Group fSlidesGroup = null;
	private SlideShow fppt;
	private CLabel fSlideStatus;

	private Vector<Integer> fIndices = new Vector<Integer>();
	private Button fRangeCheck;
//	private Button fIndividualCheck;
	private Button fShowCheck;
	
	private boolean fIsRangeSelection;
	private boolean fAllSlidesSelected;

	public TagPowerpointDialog(Shell shell)
	{
		super(shell);
	}

	public void setSlideShow(SlideShow ppt) 
	{
		fppt = ppt;
	}

	@Override
	public int open() 
	{
		// Ensure that we have a valid ppt file
		if(fppt == null)
			return Window.CANCEL;

		return super.open();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		page.setLayout(layout);
		page.setData(data);

		createSlidesGroup(parent);
		return page;
	}

	/**
	 * This method initializes addGroup	
	 *
	 */
	private void createSlidesGroup(Composite parent) 
	{	
		fSlidesGroup = new Group(parent, SWT.NONE);
		fSlidesGroup.setText("Select slides");

		GridLayout layout = new GridLayout();
		layout.marginTop = 8;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		fSlidesGroup.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.minimumWidth = 400;
		data.heightHint = 230;
		fSlidesGroup.setLayoutData(data);

		fSlideTable = new Table(fSlidesGroup, SWT.FLAT | SWT.CHECK | SWT.BORDER);
		data = new GridData(GridData.FILL_BOTH);
		fSlideTable.setLayoutData(data);

		Slide[] slides = fppt.getSlides();

		for(Slide slide : slides)
		{
			TableItem item = new TableItem(fSlideTable,SWT.NONE);
		
			char[] chars = slide.getTitle().toCharArray();
			
			for(int i =0 ;i<chars.length;i++)
				if(chars[i] == '') // Dont ask, im not quite sure either!
					chars[i] = ' ';
			
			item.setText(new String(chars));
		}

		Composite toolsArea = new Composite(fSlidesGroup,SWT.NONE);
		layout = new GridLayout(2,false);
		toolsArea.setLayout(layout);

		data = new GridData(GridData.FILL_HORIZONTAL);
		toolsArea.setLayoutData(data);

		/*
		 * ---------------------------------------------
		 */
		Composite radioComposite = new Composite(toolsArea, SWT.NONE);
		radioComposite.setLayout(new GridLayout());
		
//		fIndividualCheck = new Button(radioComposite, SWT.RADIO);
//		fIndividualCheck.setText("Individual slides");
//
//		data = new GridData(GridData.FILL_HORIZONTAL);
//		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
//		fIndividualCheck.setLayoutData(data);
		
		fRangeCheck = new Button(radioComposite, SWT.RADIO);
		fRangeCheck.setText("Slide range");

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		fRangeCheck.setLayoutData(data);
		
		fShowCheck = new Button(radioComposite, SWT.RADIO);
		fShowCheck.setText("All slides");

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		fShowCheck.setLayoutData(data);
		
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		radioComposite.setLayoutData(data);
		
//		fIndividualCheck.setSelection(true);
		
		/*
		 * ---------------------------------------------
		 */
		
		Composite buttonsComp = new Composite(toolsArea,SWT.NONE);
		layout = new GridLayout(2,false);
		layout.verticalSpacing = 15;
		buttonsComp.setLayout(layout);
		
		data = new GridData(GridData.FILL_BOTH);
		buttonsComp.setLayoutData(data);

		fFormToolkit = new FormToolkit(Display.getDefault());

		Button selectAll = fFormToolkit.createButton(buttonsComp, "Select All", SWT.PUSH);
		Button selectNone =  fFormToolkit.createButton(buttonsComp, "Select None", SWT.PUSH);

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalAlignment = SWT.RIGHT;
		selectAll.setLayoutData(data);
		
		fSlideStatus = new CLabel(buttonsComp,SWT.BORDER);
		fSlideStatus.setText("No slides selected");

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		fSlideStatus.setLayoutData(data);
		/*
		 * ---------------------------------------------
		 */

		fShowCheck.addSelectionListener(new SelectionListener() {
			
			
			public void widgetSelected(SelectionEvent e) 
			{
				checkAll();
				fSlideTable.setEnabled(false);
				refreshStatus();
			}
		
			
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		
		});
		
//		fIndividualCheck.addSelectionListener(new SelectionListener() {
//		
//			@Override
//			public void widgetSelected(SelectionEvent e) 
//			{
//				fSlideTable.setEnabled(true);
//				refreshStatus();
//			}
//		
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//		
//		});
		
		fRangeCheck.addSelectionListener(new SelectionListener() 
		{
			
			public void widgetSelected(SelectionEvent e)
			{
				fSlideTable.setEnabled(true);
				
				if(fRangeCheck.getSelection())
				{
					int checkedItems = getNumberOfCheckedItems();

					if(checkedItems > 0)
					{
						int low = getLowRangeIndex();
						int high = getHighRangeIndex();

						clearTable();
						check(low,high);
					}
				}
				
				refreshStatus();
			}

			
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		fSlideTable.addSelectionListener(new SelectionListener() 
		{
			
			public void widgetSelected(SelectionEvent e) 
			{
				if(fRangeCheck.getSelection())
				{
					int checkedItems = getNumberOfCheckedItems();
					
					if(checkedItems > 0)
					{
						int low = getLowRangeIndex();
						int high = getHighRangeIndex();
						
						TableItem checkedItem = (TableItem)e.item;
						int checked = getIndex(checkedItem);
						
						if(checked < high && checked > low)
							high = checked - 1;

						clearTable();
						check(low,high);
					}
				}
				
				refreshStatus();
			}

		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});

		selectNone.addSelectionListener(new SelectionListener() 
		{
		
			public void widgetSelected(SelectionEvent e) 
			{
				clearTable();
				refreshStatus();
			}


			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});

		selectAll.addSelectionListener(new SelectionListener() 
		{

			public void widgetSelected(SelectionEvent e) 
			{
				checkAll();
				refreshStatus();
			}


			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		if(fSlideRange!=null)
		{
			fRangeCheck.setSelection(true);
			clearTable();
			check(fSlideRange.getStart()-1,fSlideRange.getEnd()-1);
		}
		else
		{
			fShowCheck.setSelection(true);
			checkAll();
			fSlideTable.setEnabled(false);
		}
	}

	private int getIndex(TableItem tableItem) 
	{
		TableItem[]items = fSlideTable.getItems();

		for(int i = 0;i<items.length;i++)
		{
			if(items[i] == tableItem)
				return i; 
		}

		return -1;
	}

	private void refreshStatus() 
	{
		
		if(fShowCheck.getSelection())
		{
			fSlideStatus.setText("All slides selected");
			getButton(IDialogConstants.OK_ID).setEnabled(true);
			return;
		}
		
		int count = getNumberOfCheckedItems();

		if(count == 0)
		{
			fSlideStatus.setText("No slides selected");
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return;
		}
		else
		{
			if(fRangeCheck.getSelection())
			{
				int low = getLowRangeIndex();
				int high = getHighRangeIndex();

				if(high==low)
					fSlideStatus.setText("Slide " + (low+1) + " selected");
				else
					fSlideStatus.setText("Slides " + (low+1) + " to " + (high+1) + " selected");
				
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		}
	}

	private void check(int low, int high)
	{
		TableItem[]items = fSlideTable.getItems();

		for(int i=low;i<=high;i++)
			items[i].setChecked(true);
	}

	private void checkAll()
	{
		TableItem[]items = fSlideTable.getItems();

		for(TableItem item: items)
			item.setChecked(true);

	}

	private void clearTable()
	{
		TableItem[]items = fSlideTable.getItems();

		for(TableItem item: items)
			item.setChecked(false);
	}

	private int getNumberOfCheckedItems()
	{
		TableItem[]items = fSlideTable.getItems();

		int count = 0;

		for(TableItem item: items)
		{
			if(item.getChecked())
				count++;
		}

		return count;
	}

	private int getLowRangeIndex()
	{
		TableItem[]items = fSlideTable.getItems();

		int low = -1;

		for(int i = 0; i<items.length;i++)
			if(items[i].getChecked())
			{
				low = i;
				break;
			}

		return low;
	}

	private int getHighRangeIndex()
	{
		TableItem[]items = fSlideTable.getItems();

		int high = -1;

		for(int i = items.length-1; i>=0;i--)
			if(items[i].getChecked())
			{
				high = i;
				break;
			}

		return high;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) 
	{
		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite composite = new Composite(container, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		applyDialogFont(composite);
		// initialize the dialog units
		initializeDialogUnits(composite);
		// create the dialog area and button bar
		dialogArea = createDialogArea(composite);
		buttonBar = createButtonBar(composite);
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, "Ok",
				true);

		getButton(IDialogConstants.OK_ID).setEnabled(false);

		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		
		refreshStatus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(final Shell newShell) 
	{
		super.configureShell(newShell);
		newShell.setText("Select slides");
		newShell.setImage(ToursContentPlugin.getDefault().getImageRegistry().get(ToursContentPlugin.IMG_PPT));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() 
	{	
		fIndices.clear();

		TableItem[]items = fSlideTable.getItems();

		for(int i =0; i < items.length; i++)
		{
			if(items[i].getChecked())
				fIndices.add((i+1)); // no longer 0 based
		}

		fIsRangeSelection = fRangeCheck.getSelection();
		
		
		if(fIsRangeSelection)
		{
			int high = Integer.MIN_VALUE;
			int low = Integer.MAX_VALUE;

			for(int i : fIndices)
			{
				high = Math.max(high, i);
				low = Math.min(low, i);
			}
			
			fSlideRange= new SlideRange(low,high);
		}
		
		fAllSlidesSelected = fShowCheck.getSelection();
		super.okPressed();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#getShellStyle()
	 */
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.APPLICATION_MODAL;
	}

//	private int[] getSelectedSlideIndices() 
//	{
//		int[] indicesArray = new int[fIndices.size()];
//
//		for(int i=0;i<indicesArray.length;i++)
//			indicesArray[i] = fIndices.get(i);
//
//		return indicesArray;
//	}
	
	/**
	 * @return
	 */
	public boolean rangeSelection()
	{
		return fIsRangeSelection;
	}

	public boolean allSlidesSelection() 
	{
		return fAllSlidesSelected;
	}

	public void setSlideRange(SlideRange slideRange) 
	{
		fSlideRange = slideRange;
	}
	

	public SlideRange getSlideRange() 
	{
		return fSlideRange;
	}
}
