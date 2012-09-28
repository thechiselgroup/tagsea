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

import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IMemento;

import com.ibm.research.tours.content.BoxTextEffect;
import com.ibm.research.tours.content.DashedBoxTextEffect;
import com.ibm.research.tours.content.HighlightedTextEffect;
import com.ibm.research.tours.content.ITextEffect;
import com.ibm.research.tours.content.NativeProblemUnderlineTextEffect;
import com.ibm.research.tours.content.SquigglesTextEffect;
import com.ibm.research.tours.content.UnderlinedTextEffect;
import com.ibm.research.tours.content.VerticalBarTextEffect;
import com.ibm.research.tours.fx.elements.MaximizableTourElement;

/**
 * @author mdesmond
 *
 */
public abstract class TextPresentationTourElement extends MaximizableTourElement 
{
	private static final String TEXT_PRESENTATION_ELEMENT = "text-presentation";
	//private static final String SHOW_IN_TEXT_ELEMENT = "showInText";
	private static final String SHOW_IN_OVERVIEW_ELEMENT = "showInOverview";
	private static final String SHOW_IN_VERTICAL_ELEMENT = "showInVertical";
	private static final String COLOR_ELEMENT = "color";
	private static final String EFFECT_ELEMENT = "effect";
	private static final String TRUE = "true";

	private boolean fTextHighlighting;
	private boolean fShowInOverview;
	private boolean fShowInVertical;
	private RGB fTextPresentationColor;
	private ITextEffect fSelectedTextEffect;

	public boolean getTextHighlighting() {
		return fTextHighlighting;
	}

	public void setTextHighlighting(boolean showInText)
	{
		if(fTextHighlighting != showInText)
		{
			fTextHighlighting = showInText;
			fireElementChangedEvent();
		}
	}

	public boolean getShowInOverview() {
		return fShowInOverview;
	}

	public void setShowInOverview(boolean showInOverview) 
	{
		if(fShowInOverview != showInOverview)
		{
			fShowInOverview = showInOverview;
			fireElementChangedEvent();
		}
	}

	public boolean getShowInVertical() 
	{
		return fShowInVertical;
	}

	public void setShowInVertical(boolean showInVertical) 
	{

		if(fShowInVertical != showInVertical)
		{
			fShowInVertical = showInVertical;
			fireElementChangedEvent();
		}
	}

	public RGB getTextPresentationColor() 
	{
//		if(fTextPresentationColor == null)
//			fTextPresentationColor = new RGB(0,0,0);

		return fTextPresentationColor;
	}

	public void setTextPresentationColor(RGB textPresentationColor) 
	{
		if(getTextPresentationColor()==null || !getTextPresentationColor().equals(textPresentationColor))
		{
			fTextPresentationColor = textPresentationColor;
			fireElementChangedEvent();			
		}
	}

	public ITextEffect[] getSupportedTextEffects()
	{
		return new ITextEffect[]{
				HighlightedTextEffect.getInstance(),
				SquigglesTextEffect.getInstance(),
				UnderlinedTextEffect.getInstance(),
				BoxTextEffect.getInstance(),
				VerticalBarTextEffect.getInstance(),
				DashedBoxTextEffect.getInstance(),
				NativeProblemUnderlineTextEffect.getInstance()
		};
	}

	public ITextEffect getSelectedTextEffect()
	{
//		if(fSelectedTextEffect == null)
//			fSelectedTextEffect =  HighlightedTextEffect.getInstance();

		return fSelectedTextEffect;	
	}

	public void setSelectedTextEffect(ITextEffect effect)
	{
		if(getSelectedTextEffect()!=null || getSelectedTextEffect() != effect)
		{
			fSelectedTextEffect = effect;
			fireElementChangedEvent();
		}
	}

	@Override
	public void save(IMemento memento) 
	{
		super.save(memento);

		if(getTextHighlighting())
		{
			IMemento root = memento.createChild(TEXT_PRESENTATION_ELEMENT);

			IMemento effectMemento = root.createChild(EFFECT_ELEMENT);
			effectMemento.putTextData(getSelectedTextEffect().getText());

			if(fShowInOverview)
			{
				IMemento m = root.createChild(SHOW_IN_OVERVIEW_ELEMENT);
				m.putTextData(TRUE);
			}

			if(fShowInVertical)
			{
				IMemento m = root.createChild(SHOW_IN_VERTICAL_ELEMENT);
				m.putTextData(TRUE);
			}

			IMemento colorMemento = root.createChild(COLOR_ELEMENT);
			RGB color = getTextPresentationColor();
			colorMemento.putTextData(color.red + "," + color.green + "," + color.blue);
		}
	}

	@Override
	public void load(IMemento memento) 
	{
		super.load(memento);

		IMemento root = memento.getChild(TEXT_PRESENTATION_ELEMENT);

		if(root == null)
		{
			setTextHighlighting(false);
			return;
		}
		
		setTextHighlighting(true);
		
		IMemento effectMemento = root.getChild(EFFECT_ELEMENT);

		if(effectMemento!=null)
		{
			String effectString = effectMemento.getTextData();

			if(effectString!=null)
			{
				for(ITextEffect effect : getSupportedTextEffects())
				{
					if(effect.getText().equalsIgnoreCase(effectString))
					{
						fSelectedTextEffect = effect;
						break;
					}
				}
			}
		}

		IMemento overviewMemento = root.getChild(SHOW_IN_OVERVIEW_ELEMENT);

		if(overviewMemento!=null)
		{
			String showInOverviewValue = overviewMemento.getTextData();

			if(showInOverviewValue!=null && showInOverviewValue.equalsIgnoreCase(TRUE))
				fShowInOverview = true;
		}

		IMemento verticalMemento = root.getChild(SHOW_IN_VERTICAL_ELEMENT);

		if(verticalMemento!=null)
		{
			String showInVerticalValue = verticalMemento.getTextData();

			if(showInVerticalValue!=null && showInVerticalValue.equalsIgnoreCase(TRUE))
				fShowInVertical = true;
		}

		IMemento colorMemento = root.getChild(COLOR_ELEMENT);

		if(colorMemento != null)
		{
			String text = colorMemento.getTextData();

			if(text!=null)
			{
				String[] rgb = text.split(",");

				if(rgb.length == 3)
				{
					try 
					{
						int red = Integer.parseInt(rgb[0]);
						int green = Integer.parseInt(rgb[1]);
						int blue = Integer.parseInt(rgb[2]);

						fTextPresentationColor = new RGB(red,green,blue);

					}
					catch (NumberFormatException e) 
					{
						e.printStackTrace();
					}

				}
			}
		}
	}
}
