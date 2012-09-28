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
package com.ibm.research.tours.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ToursPlugin;
import com.ibm.research.tours.ITour;
import com.ibm.research.tours.serializer.XMLTourFactory;

public class NewTourWizard extends Wizard implements INewWizard 
{	
	private NewTourWizardPage1 page1;
	private NewTourWizardPage2 page2;
	private IStructuredSelection selection;
	private IWorkbench workbench;

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages()
	{
		page1 = new NewTourWizardPage1(workbench,selection);
		page2 = new NewTourWizardPage2(workbench,selection);
		addPage(page1);
		addPage(page2);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench aWorkbench,IStructuredSelection currentSelection) 
	{
		workbench = aWorkbench;
		selection = currentSelection;
		setWindowTitle("New Tour");
		setDefaultPageImageDescriptor(ToursPlugin.getDefault().getImageRegistry().getDescriptor(ToursPlugin.IMG_TOUR_WIZ));	
	}

	@Override
	public void createPageControls(Composite pageContainer) 
	{
		// Lazy create pages
	}
	
	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{	
		String fileName = page2.getFileName();
		
		if(!fileName.endsWith(".tour"))
			fileName = fileName + ".tour";
		
		IFile newFile = page2.createNewFile();

		if (newFile == null) 
			return false;  // ie.- creation was unsuccessful

		ITour tour = XMLTourFactory.createTour(page1.getTourTitle(), page1.getTourDescription(), page1.getAuthor());
		try 
		{
			tour.write(newFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
		
		// Since the file resource was created fine, open it for editing
		// iff requested by the user
		try 
		{
			IWorkbenchWindow dwindow = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = dwindow.getActivePage();
			
			if (page != null)
				IDE.openEditor(page, newFile, true); 
		}
		catch (PartInitException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
}