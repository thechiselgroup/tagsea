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
package com.ibm.research.tours.content.actions;

import java.io.IOException;
import java.util.Vector;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tours.IDoubleClickActionDelegate;
import com.ibm.research.tours.content.SlideRange;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;
import com.ibm.research.tours.content.url.delegates.HttpURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.IURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.JavaURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.PowerPointURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.ResourceURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.TextRegionURLTourElementDelegate;
import com.ibm.research.tours.fx.dialogs.TagPowerpointDialog;
import com.ibm.research.tours.fx.wizards.ResourceWizard;
import com.ibm.research.tours.fx.wizards.TextRegionWizard;

public class DoubleClickActionDelegate implements IDoubleClickActionDelegate
{
	private IWorkbenchPart fTargetPart;
	private IStructuredSelection fSelection;

	public void run() 
	{   // Should be an extension
		ResourceURLTourElement element = (ResourceURLTourElement)getSelection()[0];

		IURLTourElementDelegate delegate = element.getDelegate();

		if(delegate instanceof PowerPointURLTourElementDelegate)
		{
			PowerPointURLTourElementDelegate pptDelegate = (PowerPointURLTourElementDelegate)delegate;
			IFile file = pptDelegate.getPowerpointFile();

			try 
			{
				String filePath = file.getRawLocation().toOSString();
				SlideShow ppt = new SlideShow(new HSLFSlideShow(filePath));

				if(ppt.getSlides().length <= 0)
				{
					// there be dragons!
					return;
				}

				TagPowerpointDialog dialog = new TagPowerpointDialog(Display.getDefault().getActiveShell());
				dialog.setSlideShow(ppt);
				
				if(pptDelegate.getSlideRange()!=null)
				{
					int start = pptDelegate.getSlideRange().getStart();
					int end = pptDelegate.getSlideRange().getEnd();
					dialog.setSlideRange(new SlideRange(start,end));
				}
				
				int result = dialog.open();
				if (result != Dialog.OK) 
					return;

				boolean isRangeSelection = dialog.rangeSelection();

				if(isRangeSelection)
					pptDelegate.setSlideRange(dialog.getSlideRange());
				else
					pptDelegate.setSlideRange(null);
					
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}

			//			PowerPointWizard wizard = new PowerPointWizard(pptDelegate);
			//			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
			//			dialog.create();
			//			dialog.open();
		}
		else if(delegate instanceof JavaURLTourElementDelegate)
		{
			TextRegionWizard wizard = new TextRegionWizard(element,"Presentation","Java Element Presentation","Configure the presentation of the java element");
			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
			dialog.create();
			dialog.open();
		}
		else if(delegate instanceof TextRegionURLTourElementDelegate)
		{
			TextRegionWizard wizard = new TextRegionWizard(element,"Presentation","Text Presentation","Configure the presentation of the text region");
			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
			dialog.create();
			dialog.open();
		}
		else if(delegate instanceof ResourceURLTourElementDelegate || delegate instanceof HttpURLTourElementDelegate)
		{
			ResourceWizard wizard = new ResourceWizard(element,"Presentation","Resource Presentation","Configure the presentation of the resource");
			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
			dialog.create();
			dialog.open();
		}
	}

	public void selectionChanged(ISelection selection) 
	{
		if(selection instanceof IStructuredSelection)
			fSelection = (IStructuredSelection)selection;
	}

	public void setActivePart(IWorkbenchPart targetPart) 
	{
		fTargetPart = targetPart;
	}

	public IWorkbenchPart getTargetPart()
	{
		return fTargetPart;
	}

	public Object[] getSelection()
	{
		if(fSelection !=null && fSelection.size() > 0)
			return fSelection.toArray();

		return new Object[0];
	}
}
