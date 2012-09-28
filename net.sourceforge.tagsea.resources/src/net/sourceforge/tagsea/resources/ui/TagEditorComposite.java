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
package net.sourceforge.tagsea.resources.ui;

import java.text.DateFormat;
import java.util.Date;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ui.tags.TagProposalProvider;
import net.sourceforge.tagsea.core.ui.tags.TagSelectionDialog;
import net.sourceforge.tagsea.core.ui.waypoints.DateDialog;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.fieldassist.TextControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.ui.fieldassist.ContentAssistField;

/**
 * A composite for editing resource waypoints.
 * @author Del Myers
 */

public class TagEditorComposite extends Composite {

	private Group addGroup = null;
	private Button selectButton = null;
	private Text tagsText = null;
	private Group detailsGroup = null;
	private Label resourceLabel = null;
	private Label lineLabel = null;
	private Label resourceValueLabel = null;
	private Label lineValueLabel = null;
	private final IResource[] resources;
	private final int line;
	private Label authorLabel = null;
	private Text authorText = null;
	private Label dateLabel = null;
	private Composite dateComposite = null;
	private Label dateValueLabel = null;
	private Button dateButton = null;
	private Label commentLabel = null;
	private Text commentText = null;
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
	
	
	public TagEditorComposite(Composite parent, int style, IResource[] resources, int line) {
		super(parent, style);
		this.resources = resources;
		this.line = line;
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
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		addGroup = new Group(this, SWT.NONE);
		addGroup.setText("Tags");
		addGroup.setLayoutData(gridData2);
		addGroup.setLayout(gridLayout1);
		tagsText = createFilterTextControl(addGroup, SWT.BORDER, gridData1);
		//tagsText.setLayoutData(gridData1);
		selectButton = new Button(addGroup, SWT.NONE);
		selectButton.setText("Select...");
		selectButton.addSelectionListener(new OpenTagSelectionDialogAction());
	}

	/**
	 * This method initializes detailsGroup	
	 *
	 */
	private void createDetailsGroup() {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.BEGINNING;
		gridData3.verticalAlignment = GridData.BEGINNING;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		GridData gridData9 = new GridData();
		gridData9.grabExcessHorizontalSpace = true;
		gridData9.verticalAlignment = GridData.CENTER;
		gridData9.horizontalAlignment = GridData.FILL;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.grabExcessVerticalSpace = false;
		gridData6.verticalAlignment = GridData.FILL;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		detailsGroup = new Group(this, SWT.NONE);
		detailsGroup.setLayout(gridLayout2);
		detailsGroup.setLayoutData(gridData);
		detailsGroup.setText("Details");
		resourceLabel = new Label(detailsGroup, SWT.NONE);
		resourceLabel.setText("Resource");
		resourceValueLabel = new Label(detailsGroup, SWT.NONE);
		if (resources.length == 1) {
			resourceValueLabel.setText(resources[0].getFullPath().toOSString());
		} else {
			resourceValueLabel.setText("");
		}
		lineLabel = new Label(detailsGroup, SWT.NONE);
		lineLabel.setText("Line");
		lineValueLabel = new Label(detailsGroup, SWT.NONE);
		lineValueLabel.setText("" + line);
		authorLabel = new Label(detailsGroup, SWT.NONE);
		authorLabel.setText("Author");
		authorText = new Text(detailsGroup, SWT.BORDER);
		authorText.setLayoutData(gridData6);
		String author = System.getProperty("user.name");
		authorText.setText(author);
		commentLabel = new Label(detailsGroup, SWT.NONE);
		commentLabel.setText("Message");
		commentText = new Text(detailsGroup, SWT.BORDER);
		commentText.setLayoutData(gridData9);
		dateLabel = new Label(detailsGroup, SWT.NONE);
		dateLabel.setText("Date");
		dateLabel.setLayoutData(gridData3);
		createDateComposite();
	}

	/**
	 * This method initializes dateComposite	
	 *
	 */
	private void createDateComposite() {
		
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
		dateComposite = new Composite(detailsGroup, SWT.NONE);
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

		// @tag expressionfilteredtable content-assist filtertable tour.TourTest2.1194628865421.0 : originally wanted to make this generic, and have the proposal provider a field, but this method gets called by the super constructor, so passing the provider as a constructor parameter would result in "null" providers being passed here (the additional parameter gets processed AFTER super constructor)

		ContentAssistField contentAssistField = new ContentAssistField(
				parent,
				style,
				new TextControlCreator(),
				new TextContentAdapter(),
				new TagProposalProvider(parent),
				null, null
		);  

		contentAssistField.getContentAssistCommandAdapter().setPopupSize(new Point(200,60));

		contentAssistField.getLayoutControl().setLayoutData(layoutData);
		return (Text) contentAssistField.getControl();
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
	
	public IResource[] getResources() {
		return resources;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getMessage() {
		return commentText.getText();
	}

	/**
	 * @return
	 */
	public String getAuthor() {
		return authorText.getText().trim();
	}
}  //  @jve:decl-index=0:visual-constraint="11,-6"
