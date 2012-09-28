package com.ibm.research.tours.fx.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.research.tours.fx.ToursFxPlugin;

public class FXPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage, IToursPreferences {

	private Text fDelayText;
	
	public FXPreferencesPage() {
	}

	public FXPreferencesPage(String title) {
		super(title);
	}

	public FXPreferencesPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent) 
	{
		Composite root = new Composite(parent,SWT.NONE);
		root.setLayout(new GridLayout(2,false));
		
		new Label(root, SWT.NONE).setText("Aplha FX delay");
		fDelayText = new Text(root, SWT.BORDER);
		
		int fxDelay = getPreferenceStore().getInt(FX_DELAY);
		fDelayText.setText(""+fxDelay);
		GridData data = new GridData();
		data.widthHint = 50;
		fDelayText.setLayoutData(data);
		
		return root;
	}

	public void init(IWorkbench workbench) 
	{
		setPreferenceStore(ToursFxPlugin.getDefault().getPreferenceStore());
	}
	
	@Override
	public boolean performOk() 
	{
		try 
		{
			int delay = Integer.parseInt(fDelayText.getText());
			getPreferenceStore().setValue(FX_DELAY, delay);
		}
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		}
		
		return super.performOk();
	}
	
	@Override
	protected void performDefaults() 
	{
		fDelayText.setText(""+DEFAULT_FX_DELAY);
	}
}
