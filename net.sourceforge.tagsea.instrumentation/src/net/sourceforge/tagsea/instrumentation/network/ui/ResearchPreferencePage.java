package net.sourceforge.tagsea.instrumentation.network.ui;

import net.sourceforge.tagsea.instrumentation.InstrumentationPreferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class ResearchPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private Button monitorButton;
	private Combo uploadCombo;
	private Button registerButton;
	private RegisterComposite mainComposite;
	private int uploadInterval;
	private boolean monitoring;
	public ResearchPreferencePage() {
	}

	public ResearchPreferencePage(String title) {
		super(title);
	}

	public ResearchPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		page.setLayout(layout);
		Label label = new Label(page, SWT.NONE);
		label.setText("This version of TagSEA registered to:");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		mainComposite = new RegisterComposite(page, SWT.NONE);
		mainComposite.setEnabled(false);
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 2;
		mainComposite.setLayoutData(data);
		monitorButton = new Button(page, SWT.CHECK);
		monitorButton.setText("Monitor TagSEA Usage");
		monitorButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				monitoring = monitorButton.getSelection();
			}
		});
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = 2;
		monitorButton.setLayoutData(data);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(monitorButton, "net.sourceforge.tagsea.instrumentation.monitor");
		Label l = new Label(page, SWT.NONE);
		l.setText("Automatic uploads");
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		l.setLayoutData(data);
		uploadCombo = new Combo(page, SWT.SINGLE);
		uploadCombo.setItems(new String[] {"Never", "Daily", "Weekly", "Every two weeks"});
		uploadCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				uploadInterval = uploadCombo.getSelectionIndex();
			}
		});
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		uploadCombo.setLayoutData(data);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(uploadCombo, "net.sourceforge.tagsea.instrumentation.consent");
		registerButton = new Button(page, SWT.PUSH);
		registerButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				WizardDialog wizard = new WizardDialog(getShell(), new RegisterWizard());
				wizard.setTitle("Register TagSEA");
				int oldId = InstrumentationPreferences.getUID();
				if (InstrumentationPreferences.isRegistered()) {
					//unregister
					InstrumentationPreferences.setUID(-1);
				}
				int code = wizard.open();
				if (code == WizardDialog.CANCEL) {
					InstrumentationPreferences.setUID(oldId);
				}
				InstrumentationPreferences.setAskForRegistration(false);
				refresh();
			}
		});
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		registerButton.setLayoutData(data);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(registerButton, "net.sourceforge.tagsea.instrumentation.register");
		PlatformUI.getWorkbench().getHelpSystem().setHelp(page, "net.sourceforge.tagsea.instrumentation.consent");
		refresh();
		return page;
	}

	/**
	 * 
	 */
	protected void refresh() {
		uploadInterval = InstrumentationPreferences.getUploadInterval();
		uploadCombo.setText(uploadCombo.getItem(uploadInterval));
		monitoring = InstrumentationPreferences.isMonitoringEnabled();
		monitorButton.setSelection(monitoring);
		boolean registered = InstrumentationPreferences.isRegistered();
		registerButton.setText((registered) ? "Reregister TagSEA" : "Register TagSEA");
		uploadCombo.setEnabled(registered);
		monitorButton.setEnabled(registered);
		mainComposite.refreshFromPreferences();
	}

	@Override
	protected void performApply() {
		InstrumentationPreferences.setUploadInterval(uploadInterval);
		InstrumentationPreferences.setMonitoringEnabled(monitoring);
		super.performApply();
	}
	
	public void init(IWorkbench workbench) {
	}

}
