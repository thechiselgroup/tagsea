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
package net.sourceforge.tagsea.url.actions;

import java.text.DateFormat;
import java.util.Date;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ui.tags.TagProposalProvider;
import net.sourceforge.tagsea.core.ui.tags.TagSelectionDialog;
import net.sourceforge.tagsea.core.ui.waypoints.DateDialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

/**
 * A composite for editing resource waypoints.
 * @author Del Myers
 */

public class TagEditorComposite extends Composite {

	private Group urlGroup = null;
	private Button selectButton = null;
	private Text tagsText = null;
	private Text urlValueText = null;
	private Text authorText = null;
	private Label dateLabel = null;
	private Composite dateComposite = null;
	private Label dateValueLabel = null;
	private Button dateButton = null;
	private Text descriptionText = null;
	private Date date;
	
	private class OpenTagSelectionDialogAction extends Action implements SelectionListener {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
		 */
		@Override
		public void runWithEvent(Event event) {
			Shell shell = event.widget.getDisplay().getActiveShell();
			String[] tagNames = getTagNames();
			TreeSet<ITag> initialChecks = new TreeSet<ITag>();
			for (String name : tagNames) {
				ITag tag = TagSEAPlugin.getTagsModel().getTag(name);
				if (tag != null) {
					initialChecks.add(tag);
				}
			}
			TagSelectionDialog dialog = new TagSelectionDialog(shell);
			dialog.setInitialChecks(initialChecks.toArray(new ITag[initialChecks.size()]));
			int result = dialog.open();
			if (result != Dialog.OK) return;
			ITag[] checked = dialog.getSelectedTags();
			String text = "";
			for (ITag tag : checked) {
				text += tag.getName() + " ";
			}
			tagsText.setText(text);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			Event event = new Event();
			event.widget = e.widget;
			event.item = e.item;
			event.detail = e.detail;
			event.time = e.time;
			event.doit = e.doit;
			event.display = e.display;
			event.data = e.data;
			event.stateMask = e.stateMask;
			event.time = e.time;
			event.text = e.text;
			event.type = SWT.Selection;
			runWithEvent(event);
		}
	}
	
	
	public TagEditorComposite(Composite parent, int style) 
	{
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout11 = new GridLayout();
		gridLayout11.horizontalSpacing = 0;
		gridLayout11.marginWidth = 0;
		gridLayout11.marginHeight = 0;
		gridLayout11.verticalSpacing = 0;
		createAddGroup();
		this.setLayout(gridLayout11);
		createDetailsGroup();
		setSize(new Point(160, 192));
	}

	/**
	 * This method initializes addGroup	
	 *
	 */
	private void createAddGroup() {
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.verticalAlignment = GridData.FILL;
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalAlignment = GridData.FILL;
		
		GridLayout urlGroupLayout = new GridLayout();
		urlGroupLayout.numColumns = 2;
		
		urlGroup = new Group(this, SWT.NONE);
		urlGroup.setText("URL");
		urlGroup.setLayoutData(gridData2);
		urlGroup.setLayout(urlGroupLayout);
		
		//urlLabel = new Label(urlGroup, SWT.NONE);
		//urlLabel.setText("URL");
		
		GridData urlData = new GridData(GridData.FILL_HORIZONTAL);
		urlData.widthHint = 350; //This will expand the dialog
		urlValueText = new Text(urlGroup, SWT.BORDER);
		urlValueText.setLayoutData(urlData);
		
	}

	/**
	 * This method initializes detailsGroup	
	 *
	 */
	private void createDetailsGroup()
	{
		Group tagsComposite = new Group(this,SWT.NONE);
		tagsComposite.setText("Tags");
		GridData tagsCompData = new GridData();
		tagsCompData.grabExcessHorizontalSpace = true;
		tagsCompData.verticalAlignment = GridData.CENTER;
		tagsCompData.horizontalAlignment = GridData.FILL;
		tagsCompData.horizontalSpan =2;
		tagsComposite.setLayoutData(tagsCompData);
		GridLayout tagsCompositeLayout = new GridLayout(2,false);
		//tagsCompositeLayout.marginWidth = 0;
		//tagsCompositeLayout.marginHeight = 0;
		tagsComposite.setLayout(tagsCompositeLayout);
		
		GridData tagsTextData = new GridData();
		tagsTextData.grabExcessHorizontalSpace = true;
		tagsTextData.verticalAlignment = GridData.CENTER;
		tagsTextData.horizontalAlignment = GridData.FILL;
		
		tagsText = createFilterTextControl(tagsComposite, SWT.BORDER, tagsTextData);
		//tagsText.setLayoutData(gridData1);
		selectButton = new Button(tagsComposite, SWT.NONE);
		selectButton.setText("Select...");
		selectButton.addSelectionListener(new OpenTagSelectionDialogAction());
		 
		Group descriptionGroup = new Group(this, SWT.NONE);
		descriptionGroup.setLayout(new GridLayout(2,false));
		descriptionGroup.setText("Desciption");
		
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.FILL;
		descriptionGroup.setLayoutData(data);
		
		//descriptionLabel = new Label(descriptionGroup, SWT.NONE);
		//descriptionLabel.setText("Description");
		descriptionText = new Text(descriptionGroup, SWT.BORDER|SWT.MULTI);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalSpan = 2;
		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = 40;
		descriptionText.setLayoutData(data);
		
		Group authorGroup = new Group(this, SWT.NONE);
		authorGroup.setLayout(new GridLayout(2,false));
		authorGroup.setText("Author");

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.FILL;
		authorGroup.setLayoutData(data);

		authorText = new Text(authorGroup, SWT.BORDER);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;

		authorText.setLayoutData(data);

		String author = System.getProperty("user.name");
		authorText.setText(author);

		Group dateGroup = new Group(this, SWT.NONE);
		dateGroup.setLayout(new GridLayout(2,false));
		dateGroup.setText("Date");

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.FILL;
		dateGroup.setLayoutData(data);
		
		createDateComposite(dateGroup);
	}

	/**
	 * This method initializes dateComposite	
	 *
	 */
	private void createDateComposite(Composite parent) {
		
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.CENTER;
		gridData4.verticalAlignment = GridData.BEGINNING;
		GridData gridData8 = new GridData();
		gridData8.grabExcessHorizontalSpace = true;
		gridData8.verticalAlignment = GridData.BEGINNING;
		gridData8.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.numColumns = 2;
		gridLayout4.verticalSpacing = 0;
		gridLayout4.marginWidth = 0;
		gridLayout4.marginHeight = 0;
		gridLayout4.horizontalSpacing = 0;
		GridData gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.FILL;
		gridData7.grabExcessHorizontalSpace = true;
		gridData7.grabExcessVerticalSpace = false;
		gridData7.verticalAlignment = GridData.BEGINNING;
		dateComposite = new Composite(parent, SWT.NONE);
		dateComposite.setLayoutData(gridData7);
		dateComposite.setLayout(gridLayout4);
		
		dateValueLabel = new Label(dateComposite, SWT.NONE);
		date = new Date(System.currentTimeMillis());
		DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
		dateValueLabel.setText(format.format(date));
		dateValueLabel.setLayoutData(gridData8);
		dateButton = new Button(dateComposite, SWT.NONE);
		dateButton.setText("Set Date...");
		dateButton.setLayoutData(gridData4);
		dateButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				DateDialog dialog = new DateDialog(getShell());
				dialog.setDate(date);
				int result = dialog.open();
				if (result == Dialog.OK) {
					TagEditorComposite.this.date = dialog.getDate();
					DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
					dateValueLabel.setText(format.format(date));
				}
			}
		});
	}
	
	protected Text createFilterTextControl(Composite parent, int style, Object layoutData) {

		// @tag expressionfilteredtable content-assist filtertable : originally wanted to make this generic, and have the proposal provider a field, but this method gets called by the super constructor, so passing the provider as a constructor parameter would result in "null" providers being passed here (the additional parameter gets processed AFTER super constructor)
		Text textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
		textField.setLayoutData(layoutData);
		new ContentAssistCommandAdapter(
				textField,
				new TextContentAdapter(),
				new TagProposalProvider(parent),
				null,
				null,
				true
		);
		return textField;
	}
	
	/**
	 * Adds a verify listener to verify the text in the url field of this composite.
	 * @param listener
	 */
	public void addVerifyListener(VerifyListener listener) {
		urlValueText.addVerifyListener(listener);
	}
	
	/**
	 * Removes the verify listener from the url field of this composite.
	 * @param listener
	 */
	public void removeVerifyListener(VerifyListener listener) {
		urlValueText.removeVerifyListener(listener);
	}
	
	public String[] getTagNames() {
		String text = tagsText.getText();
		String[] tagNames = text.split("\\s+");
		TreeSet<String> result = new TreeSet<String>();
		for (String name : tagNames) {
			if (!"".equals(name)) {
				result.add(name);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	public Date getDate() {
		return date;
	}
	
	public String getDescription() {
		return descriptionText.getText();
	}
	
	public String getURL() {
		return urlValueText.getText();
	}

	/**
	 * @return
	 */
	public String getAuthor() {
		return authorText.getText().trim();
	}
}  //  @jve:decl-index=0:visual-constraint="11,-6"
