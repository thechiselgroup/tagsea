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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class FontGroup 
{
	private Font fFont;
	private FontData fFontData;
	private Color fForegroundColor;
	
	public FontGroup(FontData data)
	{
		fFontData = data;
	}
	
	public FontData getFontData()
	{
		if(fFontData == null)
			fFontData = Display.getDefault().getSystemFont().getFontData()[0];

		return fFontData;
	}
	
	public Composite createComposite(Composite parent, Object layoutData)
	{
		Group fontGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		fontGroup.setLayoutData(layoutData);
		fontGroup.setLayout(new GridLayout(2,false));
		fontGroup.setText("Font");
		
		Button fontButton = new Button(fontGroup,SWT.PUSH);
		final Label selectedFontLabel = new Label(fontGroup, SWT.BORDER);
		selectedFontLabel.setText("Selected Font");
		
		GridData fontLabeldata = new GridData(GridData.FILL_BOTH);
		selectedFontLabel.setLayoutData(fontLabeldata);
		fontButton.setText("Choose font...");
		
		fFont = new Font(Display.getDefault(), getFontData());
		selectedFontLabel.setFont(fFont);
		
		fontButton.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				FontDialog dialog = new FontDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				dialog.setFontList(new FontData[]{getFontData()});
				
				if(dialog.open() != null)
				{
					if(fFont!=null && !fFont.isDisposed())
						fFont.dispose();
					
					fFont = new Font(Display.getDefault(), dialog.getFontList());
					fForegroundColor = new Color(Display.getDefault(),dialog.getRGB());
					
					fFontData = dialog.getFontList()[0];
					
					selectedFontLabel.setFont(fFont);
					selectedFontLabel.setForeground(fForegroundColor);
				}
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		return fontGroup;
	}

	
	public void dispose()
	{
		if(fFont!=null && !fFont.isDisposed())
			fFont.dispose();
		
		if(fForegroundColor!=null && !fForegroundColor.isDisposed())
			fForegroundColor.dispose();
	}
}
