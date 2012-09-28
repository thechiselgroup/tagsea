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
//import org.eclipse.jface.wizard.Wizard;
//
//import com.ibm.research.tours.content.url.delegates.PowerPointURLTourElementDelegate;
//
//public class PowerPointWizard extends Wizard
//{
//	PowerPointURLTourElementDelegate fDelegate;
//	PowerPointWizardPage fPage;
//	
//	public PowerPointWizard(PowerPointURLTourElementDelegate delegate) 
//	{
//		fDelegate = delegate;
//		fPage = new PowerPointWizardPage("PowerPointWizardPage");
//		fPage.init(fDelegate.getNumberOfSlides(),fDelegate.getSlideRange());
//		addPage(fPage);
//		setWindowTitle("Powerpoint Preferences");
//	}
//	
//	@Override
//	public boolean performFinish() 
//	{
//		fDelegate.setSlideRange(fPage.getSlideRange());
//		return true;
//	}
//}
