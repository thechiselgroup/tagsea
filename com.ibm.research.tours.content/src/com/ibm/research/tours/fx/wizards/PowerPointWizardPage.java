///*******************************************************************************
// * Copyright (c) 2006-2007 IBM Corporation and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     IBM Corporation - initial API and implementation
// *     IBM Research
// *******************************************************************************/
//package com.ibm.research.tours.fx.wizards;
//
//import org.eclipse.jface.wizard.WizardPage;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Composite;
//
//import com.ibm.research.tours.content.SlideRange;
//import com.ibm.research.tours.content.controls.PowerpointRangeGroup;
//
//public class PowerPointWizardPage extends WizardPage
//{
//	private PowerpointRangeGroup fGroup;
//	private SlideRange fRange;
//	private int fSlides;
//	
//	protected PowerPointWizardPage(String pageName) 
//	{
//		super(pageName);
//		setTitle("Powerpoint preferences");
//		setDescription("Select the preferences for this slide show");
//	}
//
//	public void init(int slides,SlideRange range)
//	{
//		fSlides = slides;
//		fRange = range;
//	}
//
//	public void createControl(Composite parent) 
//	{
//		Composite composite = new Composite(parent, SWT.NONE);
//		GridLayout layout = new GridLayout();
//		layout.verticalSpacing = 6;
//		composite.setLayout(layout);
//
//		fGroup = new PowerpointRangeGroup(fSlides,fRange);
//		fGroup.createComposite(composite);
//		setControl(composite);
//	}
//
//	public SlideRange getSlideRange() 
//	{
//		return fGroup.getSlideRange();
//	}
//
//}
