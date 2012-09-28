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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ibm.research.tagging.core.ui.fieldassist.TagAssistField;
import com.ibm.research.tagging.core.ui.validation.InvalidNameValidator;

/**
 * 
 * @author mdesmond
 *
 */
public abstract class AbstractWizardPage extends WizardPage 
{	
	protected final static String DESCRIPTION_LABEL_TEXT = "Description:";
	protected final static String AUTHOR_LABEL_TEXT = "Author:";
	protected final static String TAGS_LABEL_TEXT = "Enter tags";
	protected final static String TAG_FEEDACK_LABEL_TEXT = "Separate tags with a space.";
	protected final static String TAG_DELIMETER_REGEX = "\\s+";
	protected final static String TAGS_FEEDBACK = " tags entered.";
	protected final static String TAG_INVALID = "The tag contains invalid characters.";
	
	protected InvalidNameValidator fValidator;
	
	protected AbstractWizardPage(String title, String description, ImageDescriptor image) 
	{
		super(title,title,image);
		setDescription(description);
		fValidator = new InvalidNameValidator()
		{
			@Override
			protected String getInvalidNameError() 
			{
				return TAG_INVALID;
			}
		};
	}

	public void createControl(Composite parent) 
	{
	    Composite composite = new Composite(parent, SWT.NONE);
	    GridLayout layout = new GridLayout(2, false);
	    layout.verticalSpacing = 6;
	    
	    composite.setLayout(layout);
	    createPageContents(composite);
	    setControl(composite);
	}
	
	/**
	 * override to set your custom layout of fields and controls.  call createCustomText, createDescriptionText, createAuthorText, and createTagsText
	 * @param parent
	 */
	protected abstract void createPageContents(Composite parent);

	/**
	 * use this to create a customizable text field
	 * @param composite
	 * @param labelText
	 * @return Text
	 */
	protected Text createCustomText(Composite composite)
	{
	    Text text = new Text(composite,SWT.BORDER);
	    GridData data = new GridData(GridData.FILL_HORIZONTAL);
	    data.horizontalIndent = TagAssistField.FIELD_ASSIST_INDENT;
	    text.setLayoutData(data);
	    return text;
	}
	
	/**
	 * use this to create the standard waypoint description field
	 * @param composite
	 * @return Text
	 */
	protected Text createDescriptionText(Composite composite)
	{   
	    Text descriptionText = new Text(composite,SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
	    GridData descriptionData = new GridData(GridData.FILL_HORIZONTAL);
	    descriptionData.horizontalIndent = TagAssistField.FIELD_ASSIST_INDENT;
	    descriptionData.heightHint = 48;
	    descriptionText.setLayoutData(descriptionData);
	    
	    // allow user to tab out of the description text box
	    descriptionText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if ( event.character=='\t' )
				{
					event.doit=false;
					((Text)event.widget).traverse(SWT.TRAVERSE_TAB_NEXT);
				}
			}
	    });
	    
	    return descriptionText;
	}
	
	protected Label createSeperator(Composite parent)	
	{
		Label seperator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData seperatorData = new GridData(GridData.FILL_HORIZONTAL);
		seperatorData.horizontalSpan = 2;
    	seperator.setLayoutData(seperatorData);
    	return seperator;
	}
	
	protected Label createLabel(Composite parent, int style, String text)
	{
		Label label = new Label(parent, style);
		label.setText(text);
		return label;
	}
	
	protected Label createDescriptionLabel(Composite parent)
	{
		return createLabel(parent, SWT.LEFT, DESCRIPTION_LABEL_TEXT);
	}
		
	protected Label createAuthorLabel(Composite parent)
	{
		return createLabel(parent, SWT.LEFT, AUTHOR_LABEL_TEXT);
	}
	
	/**
	 * use this to create the standard waypoint author field
	 * @param composite
	 * @return Text
	 */
	protected Text createAuthorText(Composite composite)
	{
	    Text authorText = new Text(composite,SWT.BORDER);
	    GridData authorData = new GridData(GridData.FILL_HORIZONTAL);
	    authorData.horizontalIndent = TagAssistField.FIELD_ASSIST_INDENT;
	    authorText.setLayoutData(authorData);
	    authorText.setText(System.getProperty("user.name")); 
	    return authorText;
	}
	
	protected Label createTagsLabel(Composite parent)
	{
	    Label tagsLabel = new Label(parent, SWT.LEFT);
	    tagsLabel.setText(TAGS_LABEL_TEXT);
	    return tagsLabel;
	}
	
	protected Text createTagsText(Composite parent)
	{
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
	    TagAssistField tagAssist = new TagAssistField(parent,SWT.BORDER,data);
	    Text tagText = tagAssist.getTextControl();
	    return tagText;
	}
	
	/**
	 * use this to create the standard waypoint tags field
	 * @param composite
	 * @return Text
	 */
	protected Text createTagsTextArea(Composite parent)
	{
		Label tagsLabel = createTagsLabel(parent);
		GridData tagLabelData = new GridData(GridData.FILL_HORIZONTAL);
		tagLabelData.horizontalSpan = 2;
		tagsLabel.setLayoutData(tagLabelData);
		Text tagsText = createTagsText(parent);
		createTagsFeedbackLabel(parent,tagsText);
		return tagsText;
	}

	protected Label createTagsFeedbackLabel(Composite parent, Text tagsText) 
	{
		final Label feedbackLabel = new Label(parent,SWT.NONE);
		feedbackLabel.setText(TAG_FEEDACK_LABEL_TEXT);
		GridData feedbackLabelData = new GridData(GridData.FILL_HORIZONTAL);
		feedbackLabelData.horizontalSpan = 2;
		feedbackLabel.setLayoutData(feedbackLabelData);
		tagsText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				String text = ((Text)e.getSource()).getText();
				String error = fValidator.isValid(text);
				
				if(error != null)
				{
					feedbackLabel.setText(error);
					return;
				}
				
				if(text.trim().length() > 0)
				{
					String[] tags = text.trim().split(TAG_DELIMETER_REGEX);
					
					if(tags.length > 0)
					{
						feedbackLabel.setText(tags.length  + TAGS_FEEDBACK);
						return;
					}
				}
				
				feedbackLabel.setText(TAG_FEEDACK_LABEL_TEXT);
			}
		});
		
		return feedbackLabel;
	}
}