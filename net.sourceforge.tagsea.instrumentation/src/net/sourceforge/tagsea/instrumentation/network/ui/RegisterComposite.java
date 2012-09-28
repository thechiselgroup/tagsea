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
package net.sourceforge.tagsea.instrumentation.network.ui;

import net.sourceforge.tagsea.instrumentation.InstrumentationPreferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * TODO comment
 * @author Del Myers
 *
 */
public class RegisterComposite extends Composite {
	
	public static interface IValidator {
		public String validateInput(RegisterComposite composite);
	}
	
	private ModifyListener validationListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			if (validator != null) {
				validator.validateInput(RegisterComposite.this);
			}
		}
	};

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
	private Combo sizeCombo = null;
	private Button anonymousButton = null;
	private Label idLabel = null;
	private Label idValue = null;
	private IValidator validator;

	private Label fieldLabel;

	private Text fieldText;
	public RegisterComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
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
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 0;
		this.setLayout(gridLayout);
		createFormComposite();
		this.setSize(new Point(400, 376));
		initializeFields();
	}

	/**
	 * This method initializes formComposite	
	 *
	 */
	private void createFormComposite() {
		GridData gridData13 = new GridData();
		gridData13.horizontalAlignment = GridData.FILL;
		gridData13.grabExcessHorizontalSpace = true;
		gridData13.horizontalSpan = 4;
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
		gridData8.horizontalSpan = 4;
		gridData8.horizontalAlignment = GridData.FILL;
		GridData gridData7 = new GridData();
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
		firstNameText.addModifyListener(validationListener);
		firstNameText.setLayoutData(gridData2);
		lastNameLabel = new Label(formComposite, SWT.NONE);
		lastNameLabel.setText("*Last Name:");
		lastNameText = new Text(formComposite, SWT.BORDER);
		lastNameText.setLayoutData(gridData4);
		lastNameText.addModifyListener(validationListener);
		emailLabel = new Label(formComposite, SWT.NONE);
		emailLabel.setText("*Email:");
		emailText = new Text(formComposite, SWT.BORDER);
		emailText.setLayoutData(gridData5);
		emailText.addModifyListener(validationListener);
		jobLabel = new Label(formComposite, SWT.NONE);
		jobLabel.setText("Job Title:");
		jobText = new Text(formComposite, SWT.BORDER);
		jobText.setLayoutData(gridData6);
		fieldLabel = new Label(formComposite, SWT.NONE);
		fieldLabel.setText("Job Description:");
		fieldText = new Text(formComposite, SWT.BORDER);
		fieldText.setLayoutData(gridData13);
		companyLabel = new Label(formComposite, SWT.NONE);
		companyLabel.setText("Company:");
		companyLabel.setLayoutData(gridData7);
		companyText = new Text(formComposite, SWT.BORDER);
		companyText.setLayoutData(gridData8);
		sizeLabel = new Label(formComposite, SWT.NONE);
		sizeLabel.setText("Company Size:");
		sizeLabel.setLayoutData(gridData9);
		sizeCombo = new Combo(formComposite, SWT.BORDER);
		sizeCombo.setItems(new String[]{"", "1-20", "21-50", "51-100", "101-1000", ">1000"});
		sizeCombo.setLayoutData(gridData10);
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
				setEnabled(!anonymousButton.getSelection());
				anonymousButton.setEnabled(true);
			}
		});
		idLabel = new Label(formComposite, SWT.NONE);
		idLabel.setText("User ID:");
		idValue = new Label(formComposite, SWT.NONE);
		idValue.setText("      ");
		idValue.setLayoutData(gridData12);
		firstNameText.setFocus();
	}
	
	private void initializeFields() {

		if (InstrumentationPreferences.isAnonymous()) {
			
			anonymousButton.setSelection(true);
			firstNameText.setText("");
			lastNameText.setText("");
			emailText.setText("");
			companyText.setText("");
			sizeCombo.setText("");
			jobText.setText("");
			fieldText.setText("");
			if (anonymousButton.isEnabled()) {
				firstNameText.setEnabled(false);
				lastNameText.setEnabled(false);
				emailText.setEnabled(false);
				companyText.setEnabled(false);
				sizeCombo.setEnabled(false);
				jobText.setEnabled(false);
				fieldText.setEnabled(false);
			}
		} else {
			if (anonymousButton.isEnabled()) {
				setEnabled(true);
			}
			anonymousButton.setSelection(false);
			firstNameText.setText(notNull(InstrumentationPreferences.getFirstName()));
			lastNameText.setText(notNull(InstrumentationPreferences.getLastName()));
			emailText.setText(notNull(InstrumentationPreferences.getEmail()));
			companyText.setText(notNull(InstrumentationPreferences.getCompany()));
			sizeCombo.setText(notNull(InstrumentationPreferences.getCompanySize()));
			jobText.setText(notNull(InstrumentationPreferences.getJob()));
			fieldText.setText(notNull(InstrumentationPreferences.getFieldOfWork()));
		}
		int uid = InstrumentationPreferences.getUID();
		idValue.setText((uid != -1) ? "" + uid : "unregistered");

	}
	
	private String notNull(String string) {
		return (string == null) ? "" : string;
	}
	
	public void setEnabled(boolean enabled) {
		anonymousButton.setEnabled(enabled);
		firstNameText.setEnabled(enabled);
		lastNameText.setEnabled(enabled);
		emailText.setEnabled(enabled);
		companyText.setEnabled(enabled);
		sizeCombo.setEnabled(enabled);
		jobText.setEnabled(enabled);
		fieldText.setEnabled(enabled);
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
	
	public String getFieldOfWork() {
		if (isAnonymous()) {
			return "anonymous";
		}
		return fieldText.getText();
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
		return sizeCombo.getText();
	}
	
	public boolean isAnonymous() {
		return anonymousButton.getSelection();
	}
	
	public void setIDText(int id) {
		idValue.setText(id + "");
	}
	
	public void setValidator(IValidator validator) {
		this.validator = validator;
	}

	/**
	 * 
	 */
	public void refreshFromPreferences() {
		initializeFields();
	}

}  //  @jve:decl-index=0:visual-constraint="0,0"
