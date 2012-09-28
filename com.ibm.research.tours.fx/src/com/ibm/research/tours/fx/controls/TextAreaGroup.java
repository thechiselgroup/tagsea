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
package com.ibm.research.tours.fx.controls;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tours.fx.location.ILocation;
import com.ibm.research.tours.fx.location.ILocationProvider;

public class TextAreaGroup 
{		
	private Composite fTextComposite;
	private ColorSelector backgroundSelector;
	private ColorSelector foregroundSelector;
	private Font fFont;
	private FontData fFontData;
	private RGB fForeground;
	private RGB fBackground;
	private String fText;
	private ILocationProvider fLocationProvider;
	private ILocation fSelectedLocation;

	public TextAreaGroup(String text,
					   	 ILocationProvider provider, 
					   	 ILocation selectedLocation, 
					   	 RGB foreground, 
					   	 RGB background, 
					   	 FontData fontData)
	{
		fText = text;
		fLocationProvider = provider;
		fSelectedLocation = selectedLocation;
		fForeground = foreground;
		fBackground = background;
		fFontData = fontData;
	}

	public Composite createComposite(Composite parent, Object layoutData)
	{
		fTextComposite = new Composite(parent,SWT.NONE);
		fTextComposite.setLayoutData(layoutData);
		fTextComposite.setLayout(new GridLayout(2,false));

		Label textLabel = new Label(fTextComposite,SWT.NONE);
		textLabel.setText("Text:");
		
		final Text text = new Text(fTextComposite,SWT.BORDER | SWT.MULTI);
		text.setText(getText());
		text.addModifyListener(new ModifyListener() {
		
			public void modifyText(ModifyEvent e) 
			{
				setText(text.getText());
			}
		});
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 100;
		gd.widthHint = 250;
		text.setLayoutData(gd);
		
		new Label(fTextComposite,SWT.NONE).setText("Location:");

		final Combo combo = new Combo(fTextComposite,SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		for(ILocation location : getLocationProvider().getSupportedLocations())
			combo.add(location.getText());
		
		if(getSelectedLocation() != null )
			combo.setText(getSelectedLocation().getText());

		combo.addSelectionListener(new SelectionListener() {
		
			public void widgetSelected(SelectionEvent e) 
			{
				String text = combo.getText();
				
				for(ILocation location : getLocationProvider().getSupportedLocations())
					if(text.equals(location.getText()))
							setSelectedLocation(location);
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		
		});
		
		Group colorGroup = new Group(fTextComposite, SWT.SHADOW_ETCHED_IN);
		GridData colorGroupData = new GridData(GridData.FILL_HORIZONTAL);
		colorGroupData.horizontalSpan = 2;
		colorGroup.setLayoutData(colorGroupData);
		colorGroup.setLayout(new GridLayout(3,false));
		colorGroup.setText("Colors");
		
		new Label(colorGroup,SWT.NONE).setText("Background color:");
		
		backgroundSelector = new ColorSelector(colorGroup);
		backgroundSelector.setColorValue(getBackground());
		backgroundSelector.getButton().addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				setBackground(backgroundSelector.getColorValue());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		
		});
		GridData backgroundData = new GridData();
		backgroundData.horizontalSpan = 2;
		backgroundSelector.getButton().setLayoutData(backgroundData);
		
		new Label(colorGroup,SWT.NONE).setText("Text color:");
		
		foregroundSelector = new ColorSelector(colorGroup);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		foregroundSelector.getButton().setLayoutData(data);
		foregroundSelector.setColorValue(getForeground());
		
		foregroundSelector.getButton().addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) 
			{
				setForeground(foregroundSelector.getColorValue());
			}
		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		
		});
		
		Group fontGroup = new Group(fTextComposite, SWT.SHADOW_ETCHED_IN);
		GridData fontGroupData = new GridData(GridData.FILL_HORIZONTAL);
		fontGroupData.horizontalSpan = 2;
		fontGroupData.heightHint = 60;
		fontGroupData.widthHint = 300;
		fontGroup.setLayoutData(fontGroupData);
		fontGroup.setLayout(new GridLayout(2,false));
		fontGroup.setText("Font");
		
		Button fontButton = new Button(fontGroup,SWT.PUSH);
		
		final Label selectedFontLabel = new Label(fontGroup,SWT.BORDER);
		selectedFontLabel.setText("Selected Font");
		
		GridData fontLabeldata = new GridData(GridData.FILL_BOTH);
		selectedFontLabel.setLayoutData(fontLabeldata);
		fontButton.setText("Choose font...");
		
		fFont = new Font(getShell().getDisplay(), getFontData());
		selectedFontLabel.setFont(fFont);
		
		fontButton.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				FontDialog dialog = new FontDialog(getShell());
				dialog.setFontList(new FontData[]{getFontData()});
				
				if(dialog.open() != null)
				{
					if(fFont!=null && !fFont.isDisposed())
						fFont.dispose();
					
					fFont = new Font(getShell().getDisplay(), dialog.getFontList());
					setFontData(dialog.getFontList()[0]);
					
					selectedFontLabel.setFont(fFont);
				}
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		return fTextComposite;
	}

	private void setFontData(FontData fontData) 
	{
		fFontData = fontData;
	}

	private Shell getShell() 
	{
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	public FontData getFontData() 
	{
		if(fFontData == null)
			fFontData = Display.getDefault().getSystemFont().getFontData()[0];
		
		return fFontData;
	}

	private void setForeground(RGB colorValue) 
	{
		fForeground = colorValue;
	}

	public RGB getForeground() 
	{
		if(fForeground == null)
			fForeground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK).getRGB();
		return fForeground;
	}

	public RGB getBackground() 
	{
		if(fBackground == null)
			fBackground = Display.getDefault().getSystemColor(SWT.COLOR_WHITE).getRGB();
		
		return fBackground;
	}

	private void setBackground(RGB colorValue) 
	{
		fBackground = colorValue;
	}
	
	public String getText() 
	{
		if(fText == null)
			fText = new String();
		return fText;
	}
	
	private void setText(String text) 
	{
		fText = text;
	}

	private ILocationProvider getLocationProvider() 
	{
		if(fLocationProvider == null)
		{
			fLocationProvider = new ILocationProvider() 
			{
				public ILocation[] getSupportedLocations() 
				{
					return new ILocation[0];
				}
			};
		}
		
		return fLocationProvider;
	}


	public ILocation getSelectedLocation() 
	{
		return fSelectedLocation;
	}

	private void setSelectedLocation(ILocation selectedLocation) 
	{
		fSelectedLocation = selectedLocation;
	}
}
