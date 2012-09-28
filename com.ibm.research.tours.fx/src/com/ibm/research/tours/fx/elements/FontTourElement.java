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
package com.ibm.research.tours.fx.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;

import com.ibm.research.tours.AbstractTourElement;

public abstract class FontTourElement extends AbstractTourElement
{
	public static final String FONT_ELEMENT        = "font";
	public static final String NAME_ELEMENT        = "name";
	public static final String HEIGHT_ELEMENT      = "height";
	public static final String BOLD_ELEMENT        = "bold";
	public static final String ITALIC_ELEMENT      = "italic";
	public static final String STRIKEOUT_ELEMENT   = "strikeout";
	public static final String UNDERLINE_ELEMENT   = "underline";
	public static final String TRUE                = "true";

	private FontData fFontData;

	public FontData getFontData() 
	{
		if(fFontData == null)
			fFontData = Display.getDefault().getSystemFont().getFontData()[0];
		
		return fFontData;
	}

	public void setFontData(FontData data) 
	{
		if(fFontData!=null)
		{
			if(!fFontData.equals(data))
			{
				fFontData = data;
				fireElementChangedEvent();
			}
		}
		else
		{
			if(data !=null)
			{
				fFontData = data;
				fireElementChangedEvent();
			}
		}
	}

	protected String getTextAnnotations()
	{
		StringBuffer annotationBuffer = new StringBuffer();
		
		if(getTimeLimit()!=null)
			annotationBuffer.append(" [" + getTimeLimit().toString() + "]");
		
		if(getFontData()!=null)
		{
			annotationBuffer.append(" (" + getFontData().getName() + "|" + 
										   getFontData().getHeight() + 
										   ((getFontData().getStyle() & SWT.BOLD)!=0?"|bold":"") +
										   ((getFontData().getStyle() & SWT.ITALIC)!=0?"|italic":"") + 
										   (getFontData().data.lfStrikeOut !=0?"|strikeout":"") + 
										   (getFontData().data.lfUnderline !=0?"|underline":"") + ")");
		}
		
		return annotationBuffer.toString();
	}
	
	@Override
	public void load(IMemento memento) 
	{
		super.load(memento);

		IMemento font = memento.getChild(FONT_ELEMENT);
		
		if(font!=null)
		{
			IMemento nameMemento = font.getChild(NAME_ELEMENT);
			IMemento heightMemento = font.getChild(HEIGHT_ELEMENT);
			IMemento boldMemento = font.getChild(BOLD_ELEMENT);
			IMemento italicMemento = font.getChild(ITALIC_ELEMENT);
			IMemento strikeoutMemento = font.getChild(STRIKEOUT_ELEMENT);
			IMemento underlineMemento = font.getChild(UNDERLINE_ELEMENT);
			
			String name = null;
			String height = null;
			String bold = null;
			String italic = null;
			String strikeout = null;
			String underline = null;
			
			if(nameMemento!=null)
				name = nameMemento.getTextData();
			
			if(heightMemento!=null)
				height = heightMemento.getTextData();
			
			if(boldMemento!=null)
				bold = boldMemento.getTextData();
			
			if(italicMemento!=null)
				italic = italicMemento.getTextData();

			if(strikeoutMemento!=null)
				strikeout = strikeoutMemento.getTextData();
			
			if(underlineMemento!=null)
				underline = underlineMemento.getTextData();
			
			if(name!=null && height!=null)
			{
				int style = 0;
				
				if(bold!=null && bold.trim().equals(TRUE))
					style |= SWT.BOLD;
				
				if(italic!=null && italic.trim().equals(TRUE))
					style |= SWT.ITALIC;
				
				int fontHeight = Display.getDefault().getSystemFont().getFontData()[0].getHeight();
				
				try 
				{
					fontHeight = Integer.parseInt(height);
				} 
				catch (NumberFormatException e) 
				{
					e.printStackTrace();
				}
				
				FontData data = new FontData(name.trim(),fontHeight,style);
				
				if(strikeout!=null && strikeout.trim().equals(TRUE))
					data.data.lfStrikeOut = 1;
				
				if(underline!=null && underline.trim().equals(TRUE))
					data.data.lfUnderline = 1;
				
				setFontData(data);
			}
		}
	}

	@Override
	public void save(IMemento memento) 
	{
		super.save(memento);

		if(fFontData != null)
		{
			IMemento font = memento.createChild(FONT_ELEMENT);
			IMemento name = font.createChild(NAME_ELEMENT);
			// We are win32 specific
			name.putTextData(fFontData.getName());

			IMemento height = font.createChild(HEIGHT_ELEMENT);
			height.putTextData(Integer.toString(fFontData.getHeight()));

			if((fFontData.getStyle() & SWT.BOLD)!=0)
			{
				IMemento bold = font.createChild(BOLD_ELEMENT);
				bold.putTextData(TRUE);
			}

			if((fFontData.getStyle() & SWT.ITALIC)!=0)
			{
				IMemento italic = font.createChild(ITALIC_ELEMENT);
				italic.putTextData(TRUE);
			}
			
			if(fFontData.data.lfStrikeOut != 0)
			{
				IMemento strikeout = font.createChild(STRIKEOUT_ELEMENT);
				strikeout.putTextData(TRUE);
			}
			
			if(fFontData.data.lfUnderline != 0)
			{
				IMemento underline = font.createChild(UNDERLINE_ELEMENT);
				underline.putTextData(TRUE);
			}
		}
	}
}	


