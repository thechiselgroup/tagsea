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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IMemento;

import com.ibm.research.tours.AbstractTourElement;
import com.ibm.research.tours.fx.IHighlightEffect;
import com.ibm.research.tours.fx.IHighlightable;
import com.ibm.research.tours.fx.ToursFxPlugin;

public abstract class HighlightableTourElement extends AbstractTourElement implements IHighlightable
{
	public static final String HIGHLIGHT_EFFECT_ELEMENT = "highlight";
	
	private IHighlightEffect fHighlightEffect;
	private IHighlightEffect[] instances;
	
	public IHighlightEffect getHighlightEffect() 
	{
		return fHighlightEffect;
	}

	public void setHighlightEffect(IHighlightEffect highlightEffect) 
	{
		if(!(fHighlightEffect == highlightEffect))
		{
			fHighlightEffect = highlightEffect;
			fireElementChangedEvent();
		}
	}

	public IHighlightEffect[] getSupportedHighlightEffects() 
	{
		if ( instances==null )
		{
			IExtensionPoint extPt = Platform.getExtensionRegistry().getExtensionPoint(ToursFxPlugin.PLUGIN_ID + ".highlightEffect");
			IExtension[] extensions = extPt.getExtensions();
			
			if ( extensions==null )
				return new IHighlightEffect[0];
			
			List<IHighlightEffect> effects = new ArrayList<IHighlightEffect>();
			for (IExtension ext : extensions)
			{
				IConfigurationElement[] elements = ext.getConfigurationElements();
				for (IConfigurationElement elem : elements)
				{
					try
					{
						IHighlightEffect effect = (IHighlightEffect) elem.createExecutableExtension("class");
						effects.add(effect);
					}
					catch (CoreException e)
					{
						ToursFxPlugin.log("error processing configuration element " + elem, e);
					}
				}
			}
			
			instances = (IHighlightEffect[]) effects.toArray(new IHighlightEffect[effects.size()]);
		}
		
		return instances; // new IHighlightEffect[]{DefaultHighlightEffect.getInstance(),MismarHighlightEffect.getInstance(),LetterboxEffect.getInstance(),UnhighlightEffect.getInstance(),GlobalHighlightEffect.getInstance()};
	}
	
	@Override
	public void load(IMemento memento) 
	{
		super.load(memento);
		
		IMemento highlightMemento = memento.getChild(HIGHLIGHT_EFFECT_ELEMENT);
		
		if(highlightMemento !=null)
		{
			String highlightString = highlightMemento.getTextData();
			
			if(highlightString!=null)
			{
				IHighlightEffect[] effects = getSupportedHighlightEffects();
				
				for(IHighlightEffect effect : effects)
					if(effect.getText().equals(highlightString))
						setHighlightEffect(effect);
			}
		}
	}
	
	@Override
	public void save(IMemento memento) 
	{
		super.save(memento);
		
		if(getHighlightEffect()!=null)
		{
			IMemento highlightMemento = memento.createChild(HIGHLIGHT_EFFECT_ELEMENT);
			highlightMemento.putTextData(getHighlightEffect().getText());
		}
	}
}
