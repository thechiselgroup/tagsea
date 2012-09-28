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

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.fx.FontFx;
import com.ibm.research.tours.fx.ToursFxPlugin;

public class SetPreferenceFontTourElement extends ResettingFontTourElement {
	
	public static final String TEXT = "Set preference-controlled font",
							   PREF_ID = "preferenceId",
							   LABEL = "label";
	
	private String prefId, label;
	
	public SetPreferenceFontTourElement() {
	}
	
	public void setPreferenceId(String id) {
		prefId = id;
		fireElementChangedEvent();
	}
	
	public String getPreferenceId() {
		return prefId;
	}
	
	public void setLabel(String label) {
		this.label = label;
		fireElementChangedEvent();
	}
	
	public String getLabel() {
		return label;
	}

	public ITourElement createClone() {
		return new SetPreferenceFontTourElement();
	}

	public Image getImage() {
		return ToursFxPlugin.getDefault().getImageRegistry().get(ToursFxPlugin.IMG_FONT);
	}

	public String getShortText() {
		return TEXT;
	}

	public String getText() {
		if ( label!=null )
			return "Set " + label + getTextAnnotations();
		
		return TEXT + getTextAnnotations();
	}
	
	public void start() {
	}

	public void stop() {
	}

	public void transition() {
		if ( getFontData()!=null && prefId!=null )
			FontFx.setFont(prefId, getFontData());
	}

	@Override
	public void save(IMemento memento) {
		super.save(memento);
		if ( prefId!=null )
		{
			IMemento pref = memento.createChild(PREF_ID);
			pref.putTextData(prefId);
		}
		if ( label!=null )
		{
			IMemento lbl = memento.createChild(LABEL);
			lbl.putTextData(label);
		}
	}
	
	@Override
	public void load(IMemento memento) {
		super.load(memento);
		IMemento pref = memento.getChild(PREF_ID);
		if ( pref!=null )
		{
			prefId = pref.getTextData();
		}
		IMemento lbl = memento.getChild(LABEL);
		if ( lbl!=null )
		{
			label = lbl.getTextData();
		}
	}
}
