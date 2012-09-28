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
package com.ibm.research.tours.content.elements;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.content.ToursContentPlugin;
import com.ibm.research.tours.content.url.HttpURL;
import com.ibm.research.tours.content.url.IURL;
import com.ibm.research.tours.content.url.JavaURL;
import com.ibm.research.tours.content.url.PowerPointURL;
import com.ibm.research.tours.content.url.ResourceURL;
import com.ibm.research.tours.content.url.TextRegionURL;
import com.ibm.research.tours.content.url.URLTranslator;
import com.ibm.research.tours.content.url.delegates.HttpURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.IURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.JavaURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.PowerPointURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.ResourceURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.TextRegionURLTourElementDelegate;

public class ResourceURLTourElement extends TextPresentationTourElement 
{
	private static final String URL_ELEMENT = "url";
	private IURLTourElementDelegate fDelegate;
	private String fUrlString;

	public ResourceURLTourElement() 
	{
		// Do not edit signature, this method is required by the framework
	}

	public ResourceURLTourElement(IResource resource)
	{
		String fileExtension = resource.getFileExtension();

		if(fileExtension!=null && fileExtension.endsWith("ppt"))
			fDelegate = new PowerPointURLTourElementDelegate(this,resource);
		else
			fDelegate = new ResourceURLTourElementDelegate(this,resource);
	}

	public ResourceURLTourElement(IFile file, IRegion textRegion)
	{
		fDelegate = new TextRegionURLTourElementDelegate(this,file,textRegion);
	}

	public ResourceURLTourElement(String urlString)
	{
		// Translate the string to the url object if possible
		fUrlString = urlString;
		IURL url = URLTranslator.getURL(urlString);

		if(url != null)
		{
			if(url instanceof HttpURL)
			{
				HttpURL httpUrl = (HttpURL)url;
				fDelegate = new HttpURLTourElementDelegate(this,httpUrl.toPortableString());
			}
			if(url instanceof JavaURL)
			{
				JavaURL javaUrl = (JavaURL)url;
				fDelegate = new JavaURLTourElementDelegate(this,javaUrl.getJavaElement());
			}
			else if(url instanceof TextRegionURL)
			{
				TextRegionURL textRegionUrl = (TextRegionURL)url;
				fDelegate = new TextRegionURLTourElementDelegate(this,(IFile)textRegionUrl.getResource(),textRegionUrl.getTextRegion());	
			}
			else if(url instanceof ResourceURL)
			{
				ResourceURL resourceUrl = (ResourceURL)url;
				fDelegate = new ResourceURLTourElementDelegate(this,resourceUrl.getResource());
			}
			else if(url instanceof PowerPointURL)
			{
				PowerPointURL ppUrl = (PowerPointURL)url;

				PowerPointURLTourElementDelegate delegate = new PowerPointURLTourElementDelegate(this,ppUrl.getPowerPointFile());
				delegate.setSlideRange(ppUrl.getSlideRange());
				fDelegate = delegate;
			}
		}
	}

	public ResourceURLTourElement(IJavaElement element)
	{
		fDelegate = new JavaURLTourElementDelegate(this,element);
	}

	public ResourceURLTourElement(IClassFile file)
	{
		// todo
		// fDelegate = new ClassFileURLTourElementDelegate(file);
	}

	public ResourceURLTourElement(IClassFile file, IRegion textRegion)
	{
		// todo
	}

	public ITourElement createClone() 
	{
		if(fDelegate!=null)
			return fDelegate.createClone();
		else
			return null;
	}

	public Image getImage() 
	{
		if(fDelegate!=null)
			return fDelegate.getImage();
		else
			return ToursContentPlugin.getDefault().getImageRegistry().get(ToursContentPlugin.IMG_ERROR);
	}

	public String getShortText() 
	{
		if(fDelegate!=null)
			return fDelegate.getShortText();
		else
			return "Invalid URL";
	}

	public String getText() 
	{	
		if(fDelegate!=null)
			return fDelegate.getText() + getTextAnnotations();

		if(fUrlString !=null)
			return "Invalid URL: " + fUrlString;
		else
			return "No URL specified";
	}

	private String getTextAnnotations()
	{
		StringBuffer annotationBuffer = new StringBuffer();

		if(getTimeLimit()!=null)
			annotationBuffer.append(" [" + getTimeLimit().toString() + "]");

		if(getMaximixedHint())
			annotationBuffer.append(" (" + "maximized" + ")");

		if(getHighlightEffect()!=null)
			annotationBuffer.append(" (" + getHighlightEffect().getText() + ")");

		if(getTextHighlighting())
			if(getSelectedTextEffect()!=null)
				annotationBuffer.append(" (" + getSelectedTextEffect().getText() + ")");
		
		return annotationBuffer.toString();
	}
	
	public void start() 
	{
		if(fDelegate!=null)
			fDelegate.start();
	}

	public void stop() 
	{
		if(fDelegate!=null)
			fDelegate.stop();
	}

	public void transition() 
	{
		if(fDelegate!=null)
			fDelegate.transition();
	}

	@Override
	public void save(IMemento memento) 
	{
		super.save(memento);
		
		if(fDelegate!=null)
		{
			IMemento urlMemento = memento.createChild(URL_ELEMENT);
			IURL url = fDelegate.getUrl();
			urlMemento.putTextData(url.toPortableString());
			if (fDelegate instanceof ResourceURLTourElementDelegate) {
				((ResourceURLTourElementDelegate)fDelegate).save(memento);
			}
		}
	}

	@Override
	public void load(IMemento memento) 
	{
		super.load(memento);
		IMemento urlMemento = memento.getChild(URL_ELEMENT);
		fUrlString = urlMemento.getTextData();

		if(fUrlString != null)
		{
			// Translate the string to the url object if possible
			IURL url = URLTranslator.getURL(fUrlString);

			if(url != null)
			{
				if(url instanceof HttpURL)
				{
					HttpURL httpUrl = (HttpURL)url;
					fDelegate = new HttpURLTourElementDelegate(this,httpUrl.toPortableString());
				}
				if(url instanceof JavaURL)
				{
					JavaURL javaUrl = (JavaURL)url;
					fDelegate = new JavaURLTourElementDelegate(this,javaUrl.getJavaElement());
				}
				else if(url instanceof TextRegionURL)
				{
					TextRegionURL textRegionUrl = (TextRegionURL)url;
					fDelegate = new TextRegionURLTourElementDelegate(this,(IFile)textRegionUrl.getResource(),textRegionUrl.getTextRegion());	
				}
				else if(url instanceof ResourceURL)
				{
					ResourceURL resourceUrl = (ResourceURL)url;
					fDelegate = new ResourceURLTourElementDelegate(this,resourceUrl.getResource());
					((ResourceURLTourElementDelegate)fDelegate).load(memento);
				}
				else if(url instanceof PowerPointURL)
				{
					PowerPointURL ppUrl = (PowerPointURL)url;

					PowerPointURLTourElementDelegate delegate = new PowerPointURLTourElementDelegate(this,ppUrl.getPowerPointFile());
					delegate.setSlideRange(ppUrl.getSlideRange());
					fDelegate = delegate;
				}
			}
		}
	}

	public IURLTourElementDelegate getDelegate() 
	{
		return fDelegate;
	}

	@Override
	public void fireElementChangedEvent() 
	{
		// made public for the delegates to fire events
		super.fireElementChangedEvent();
	}
}
