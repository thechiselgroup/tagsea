/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tagging.core.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ibm.research.tagging.core.ITag;

/**
 * 
 * @author mdesmond
 *
 */
public class WaypointPage extends AbstractWizardPage 
{
	protected Text fDescriptionText;
	protected Text fAuthorText;
	protected Text fTagsText;
	
	private String fDefaultDescriptionText,
				   fDefaultAuthorText,
				   fDefaultTagsText;
	
	protected WaypointPage(String title, String description, ImageDescriptor image) 
	{
		super(title, description, image);
	}

	@Override
	protected void createPageContents(Composite parent) 
	{
		createDescriptionLabel(parent);
		fDescriptionText = createDescriptionText(parent);
		createAuthorLabel(parent);
		fAuthorText = createAuthorText(parent);
		createSeperator(parent);
		fTagsText = createTagsTextArea(parent);
		
		if ( fDefaultDescriptionText!=null )
			fDescriptionText.setText(fDefaultDescriptionText);
		if ( fDefaultAuthorText!=null )
			fAuthorText.setText(fDefaultAuthorText);
		if ( fDefaultTagsText!=null )
			fTagsText.setText(fDefaultTagsText);
	}
	
	public void setDefaultDescriptionText(String text)
	{
		fDefaultDescriptionText = text;
		if ( fDescriptionText!=null )
			fDescriptionText.setText(fDefaultDescriptionText);
	}
	
	public void setDefaultAuthorText(String text)
	{
		fDefaultAuthorText = text;
		if ( fAuthorText!=null )
			fAuthorText.setText(fDefaultAuthorText);
	}
	
	public void setDefaultTagsText(String text)
	{
		fDefaultTagsText = text;
		if ( fTagsText!=null )
			fTagsText.setText(fDefaultTagsText);
	}
	
	public void setDefaultTagsText(ITag[] tags)
	{
		if ( tags!=null && tags.length>0 )
		{
			String tagStr = "";
			for (ITag tag : tags)
				tagStr += tag.getName() + " ";
			setDefaultTagsText(tagStr.trim());
		}
	}
	
	public String getDescriptionText()
	{
		return fDescriptionText!=null?fDescriptionText.getText():"";
	}
	
	public String getAuthorText()
	{
		return fAuthorText!=null?fAuthorText.getText():"";
	}
	
	public String getTagsText()
	{
		return fTagsText!=null?fTagsText.getText():"";
	}
	
	public String[] getTags()
	{
		List<String> tagsList = new ArrayList<String>();

		if(fTagsText != null)
		{
			String text = getTagsText().trim();
			
			if(text.length() > 0)
			{
				String[] tagNames = text.split(TAG_DELIMETER_REGEX);

				for(String tagName : tagNames)
					if(tagName.trim().length() > 0)
						tagsList.add(tagName.trim());
			}
		}
		String[] array = new String[0];
		return tagsList.toArray(array);
	}
}
