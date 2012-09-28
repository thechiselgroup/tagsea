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
package ca.uvic.cs.tagsea.research;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * TODO comment
 * @author Del Myers
 *
 */
public class DialogComposite extends Composite {

	private Text info = null;
	private Composite formComposite = null;
	private Text firstNameText = null;
	private Label firstNameLabel = null;
	private Label lastNameLabel = null;
	private Text lastNameText = null;
	private Label emailLabel = null;
	private Text emailText = null;
	private Label jobLabel = null;
	private Text jobText = null;
	private Label companyLabel = null;
	private Text companyText = null;
	private Label sizeLabel = null;
	private Text sizeText = null;
	private Button anonymousButton = null;
	private Label idLabel = null;
	private Label idValue = null;
	private Composite composite = null;
	public DialogComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridData gridData21 = new GridData();
		gridData21.horizontalAlignment = GridData.CENTER;
		gridData21.grabExcessHorizontalSpace = true;
		gridData21.grabExcessVerticalSpace = false;
		gridData21.verticalAlignment = GridData.FILL;
		GridData gridData13 = new GridData();
		gridData13.horizontalAlignment = GridData.FILL;
		gridData13.grabExcessHorizontalSpace = true;
		gridData13.verticalAlignment = GridData.FILL;
		gridData13.widthHint = 400;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = GridData.FILL;
		Composite container = new Composite(this, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(gridData13);
		info = new Text(container, SWT.BORDER |SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		info.setText("Please register TagSEA with us at the University of Victoria. This will" +
				" help us to understand the kinds of users that are using TagSEA. It will also" +
				" allow you to send us information about how you are using TagSEA-information" +
				" such as the tags that you have created. This information is important to us" +
				" and the development of TagSEA and other tagging utilities.\n\n" +
				" You may choose to fill out the information below, or to remain anonymous." +
				" Simply select 'Remain Anonymous' below. Otherwise, fields marked with a '*'" +
				" are required.\n\n" +
				" You will only be asked to do this once. If you would like to register later, " +
				" simply select 'Cancel' below. You can register TagSEA later via the 'Register" +
				" TagSEA' button on the TagSEA preference page."
		);
		info.setLayoutData(gridData21);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 0;
		this.setLayout(gridLayout);
		createFormComposite();
		this.setSize(new Point(400, 376));
	}

	/**
	 * This method initializes formComposite	
	 *
	 */
	private void createFormComposite() {
		GridData gridData12 = new GridData();
		gridData12.horizontalSpan = 4;
		gridData12.horizontalAlignment = GridData.FILL;
		gridData12.verticalAlignment = GridData.CENTER;
		gridData12.grabExcessHorizontalSpace = true;
		GridData gridData11 = new GridData();
		gridData11.horizontalSpan = 5;
		GridData gridData10 = new GridData();
		gridData10.horizontalSpan = 2;
		GridData gridData9 = new GridData();
		GridData gridData8 = new GridData();
		gridData8.grabExcessHorizontalSpace = true;
		gridData8.verticalAlignment = GridData.CENTER;
		gridData8.horizontalSpan = 3;
		gridData8.horizontalAlignment = GridData.FILL;
		GridData gridData7 = new GridData();
		gridData7.horizontalSpan = 2;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.horizontalSpan = 4;
		gridData6.verticalAlignment = GridData.CENTER;
		GridData gridData5 = new GridData();
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.verticalAlignment = GridData.CENTER;
		gridData5.grabExcessVerticalSpace = false;
		gridData5.horizontalSpan = 4;
		gridData5.horizontalAlignment = GridData.FILL;
		GridData gridData4 = new GridData();
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = GridData.CENTER;
		gridData4.horizontalAlignment = GridData.FILL;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.BEGINNING;
		gridData3.verticalAlignment = GridData.CENTER;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalSpan = 2;
		gridData2.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 5;
		gridLayout1.makeColumnsEqualWidth = false;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.horizontalSpan = 4;
		gridData1.verticalSpan = 3;
		gridData1.verticalAlignment = GridData.FILL;
		formComposite = new Composite(this, SWT.NONE);
		formComposite.setLayoutData(gridData1);
		formComposite.setLayout(gridLayout1);
		firstNameLabel = new Label(formComposite, SWT.NONE);
		firstNameLabel.setText("*First Name:");
		firstNameLabel.setLayoutData(gridData3);
		firstNameText = new Text(formComposite, SWT.BORDER);
		firstNameText.setLayoutData(gridData2);
		lastNameLabel = new Label(formComposite, SWT.NONE);
		lastNameLabel.setText("*Last Name:");
		lastNameText = new Text(formComposite, SWT.BORDER);
		lastNameText.setLayoutData(gridData4);
		emailLabel = new Label(formComposite, SWT.NONE);
		emailLabel.setText("*Email:");
		emailText = new Text(formComposite, SWT.BORDER);
		emailText.setLayoutData(gridData5);
		jobLabel = new Label(formComposite, SWT.NONE);
		jobLabel.setText("Job Title:");
		jobText = new Text(formComposite, SWT.BORDER);
		jobText.setLayoutData(gridData6);
		companyLabel = new Label(formComposite, SWT.NONE);
		companyLabel.setText("Company Description:");
		companyLabel.setLayoutData(gridData7);
		companyText = new Text(formComposite, SWT.BORDER);
		companyText.setLayoutData(gridData8);
		sizeLabel = new Label(formComposite, SWT.NONE);
		sizeLabel.setText("Company Size:");
		sizeLabel.setLayoutData(gridData9);
		sizeText = new Text(formComposite, SWT.BORDER);
		sizeText.setLayoutData(gridData10);
		Label filler40 = new Label(formComposite, SWT.NONE);
		Label filler41 = new Label(formComposite, SWT.NONE);
		anonymousButton = new Button(formComposite, SWT.CHECK);
		anonymousButton.setText("Remain Anonymous");
		anonymousButton.setLayoutData(gridData11);
		anonymousButton.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setEnablement(!anonymousButton.getSelection());
			}
			private void setEnablement(boolean b) {
				firstNameText.setEnabled(b);
				lastNameText.setEnabled(b);
				emailText.setEnabled(b);
				jobText.setEnabled(b);
				companyText.setEnabled(b);
				sizeText.setEnabled(b);
			}
		});
		idLabel = new Label(formComposite, SWT.NONE);
		idLabel.setText("User ID:");
		idValue = new Label(formComposite, SWT.NONE);
		idValue.setText("      ");
		idValue.setLayoutData(gridData12);
	}
	
	public String getFirstName() {
		if (isAnonymous()) {
			return "anonymous";
		}
		return firstNameText.getText();
	}
	
	public String getLastName() {
		if (isAnonymous()) {
			return "anonymous";
		}
		return lastNameText.getText();
	}
	
	public String getEmail() {
		if (isAnonymous()) {
			return "anonymous";
		}
		return emailText.getText();
	}
	
	public String getJob() {
		if (isAnonymous()) {
			return "anonymous";
		}
		return jobText.getText();
	}
	
	public String getCompany() {
		if (isAnonymous()) {
			return "anonymous";
		}
		return companyText.getText();
	}
	
	public String getCompanySize() {
		if (isAnonymous()) {
			return "anonymous";
		}
		return sizeText.getText();
	}
	
	public boolean isAnonymous() {
		return anonymousButton.getSelection();
	}
	
	public void setIDText(int id) {
		idValue.setText(id + "");
	}

}  //  @jve:decl-index=0:visual-constraint="0,0"
