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
package com.ibm.research.tours.content.url.delegates;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.cue.tourist.internal.win32.ppt.IPowerpointApplicationListener;
import com.ibm.research.cue.tourist.internal.win32.ppt.PowerpointApplication;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ToursPlugin;
import com.ibm.research.tours.content.SlideRange;
import com.ibm.research.tours.content.ToursContentPlugin;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;
import com.ibm.research.tours.content.url.IURL;
import com.ibm.research.tours.content.url.PowerPointURL;
import com.ibm.research.tours.runtime.DefaultTourRuntime;
import com.ibm.research.tours.runtime.TourRunner;

public class PowerPointURLTourElementDelegate implements IURLTourElementDelegate, IPowerpointApplicationListener
{
	private IFile fFile;
	private SlideRange fSlideRange;

	Shell fShell;
	PowerpointApplication fppt;
	private ResourceURLTourElement fTourElement;

	public PowerPointURLTourElementDelegate(ResourceURLTourElement tourElement,IResource resource)
	{
		fFile = (IFile)resource;
		fTourElement = tourElement;
	}

	public IFile getPowerpointFile()
	{
		return fFile;
	}
	
//	public SlideRange getSlideRange()
//	{
//		return fRange;
//	}

	public int getNumberOfSlides()
	{
		try 
		{
			String filePath = getPowerpointFile().getRawLocation().toOSString();
			SlideShow ppt = new SlideShow(new HSLFSlideShow(filePath));

			if(ppt.getSlides()!=null)
				return ppt.getSlides().length;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return 0;
		
			
//		Display.getDefault().syncExec(new Runnable() 
//		{
//			public void run() 
//			{
//				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//				Shell tempShell = new Shell(shell.getDisplay());
//				PowerpointApplication powerpoint = new PowerpointApplication(tempShell,SWT.NONE);
//				powerpoint.open(fFile.getLocation().toString());
//				fNumSlides = powerpoint.getCount();
//				powerpoint.quit();
//			}
//		});
//
//		return fNumSlides;
	}

//	public void setSlideRange(SlideRange range)
//	{
//		if(fRange!=null)
//		{
//			if(!fRange.equals(range))
//			{
//				fRange = range;
//				fTourElement.fireElementChangedEvent();
//			}
//		}
//		else if(range!=null)
//		{
//			fRange = range;
//			fTourElement.fireElementChangedEvent();
//		}
//	}

	public ITourElement createClone() 
	{
		return new ResourceURLTourElement(fFile);
	}

	public Image getImage() 
	{
		return ToursContentPlugin.getImage(fFile);
	}

	public SlideRange getSlideRange()
	{
		return fSlideRange;
	}
	
	public void setSlideRange(SlideRange range)
	{
		fSlideRange = range;
		fTourElement.fireElementChangedEvent();
	}
	
	public String details()
	{
		StringBuffer buffer = new StringBuffer();
		
		if(getSlideRange()!=null)
		{
			int start = getSlideRange().getStart();
			int end = getSlideRange().getEnd();
			
			if(start != end)
			{
				buffer.append("Slides ");
				buffer.append(start + " to " + end);
			}
			else
				buffer.append("Slide " + start);
		}
		else
			buffer.append("All slides");
		
		return buffer.toString();
	}
	
	public String getShortText() 
	{
		return fFile.getName() + " - " + details();
	}

	public String getText() 
	{
		return fFile.getFullPath().toString()  + " - " +  details();
	}

	public void start() 
	{
	}

	public void stop() 
	{
		if(fppt!=null)
		{
			fppt.endSlideShow();
			fppt.quit();
			fppt = null;
		}
	}

	public void transition() 
	{
		// always show the resource - whether it is a file or whatever, in the package explorer for context
		PackageExplorerPart view = PackageExplorerPart.openInActivePerspective();
		ToursContentPlugin.log("debug : view = " + view + " " + Arrays.toString(view.getClass().getMethods()));
		view.tryToReveal(fFile);

		// for now, pop up powerpoint
		fShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		fppt = new PowerpointApplication(fShell,SWT.BAR);

		String filename = fFile.getLocation().toOSString();
		
		if ( !fppt.isActive(filename) )
			fppt.open(filename);

		fppt.addPowerpointApplicationListener(this);
		
		try 
		{
			Display.getDefault().asyncExec(new Runnable() 
			{
				public void run() 
				{
					if(getSlideRange()!=null)
					{ 						
						fppt.runSlideShow(getSlideRange().getStart(),getSlideRange().getEnd());
						// fppt.gotoEditorSlide(range.getStart());   // @tag powerpoint tours bug : generates npe
					}
					else
					{
						fppt.runSlideShow();
					}
				}
			});
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public IURL getUrl() 
	{
		PowerPointURL url = new PowerPointURL(fFile);
		
		if(getSlideRange()!=null)
			url.setSlideRange(getSlideRange());
		
		return url;
	}

	public void slideShowEnded() 
	{
		Display.getDefault().asyncExec(new Runnable() 
		{
			public void run() {
				// if ( fppt!=null )   // fppt!=null important to ensure this only gets called when user finishes slide show properly - not when tour runner is advanced over the slide show
				{
					// @tag powerpoint tours hack : advance slide if slideshow is terminated properly - bypassses interface API
					TourRunner runner = ((DefaultTourRuntime) ToursPlugin.getDefault().getTourRuntime()).getTourRunner();
					runner.next();
					fppt = null;
				}
			}
		});
	}

	public void endSlideShowCalled() {
	}
}
