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
package com.ibm.research.tagging.core.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ibm.research.tagging.core.ui.fieldassist.TagAssistField;
import com.ibm.research.tagging.core.ui.validation.InvalidNameValidator;

/**
 * 
 * @author mdesmond
 *
 */
public class QuickTagDialog extends Dialog {
    
	protected final static String TAGS_LABEL_TEXT = "Enter tags";
	protected final static String TAG_FEEDACK_LABEL_TEXT = "Separate tags with a space.";
	protected final static String TAG_DELIMETER_REGEX = "\\s+";
	protected final static String TAGS_FEEDBACK = " tags entered.";
	protected final static String TAG_INVALID = "The tag contains invlaid characters.";
	protected InvalidNameValidator fValidator;
	protected String[] fTags;
	/**
     * The title of the dialog.
     */
    private String title;

    /**
     * The message to display, or <code>null</code> if none.
     */
    private String fMessage;

    /**
     * Ok button widget.
     */
    private Button okButton;

    /**
     * Input tags text widget.
     */
    private TagAssistField fTagsText;

    /**
     * Input tags text widget.
     */
    private Label fTagsFeedbackLabel;
    
    /**
     * Quick tag dialog for getting a list of tags quickly from the user
     * @param parentShell
     * @param dialogTitle
     * @param dialogMessage
     */
    public QuickTagDialog(Shell parentShell, String dialogTitle, String dialogMessage) 
    {
        super(parentShell);
        this.title = dialogTitle;
        if(dialogMessage !=null)
        	fMessage = dialogMessage;
        else
        	fMessage = TAGS_LABEL_TEXT;
        
        fValidator = new InvalidNameValidator();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) 
    {
        super.configureShell(shell);
        
        if (title != null) 
			shell.setText(title);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) 
    {
        // create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
        //do this here because setting the text will set enablement on the ok button
        fTagsText.getTextControl().setFocus();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) 
    {
    	Composite composite = (Composite) super.createDialogArea(parent);
    	// create message

    	Label label = new Label(composite, SWT.WRAP);
    	label.setText(fMessage);
    	GridData data = new GridData(GridData.GRAB_HORIZONTAL
    			| GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
    			| GridData.VERTICAL_ALIGN_CENTER);
    	data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
    	label.setLayoutData(data);
    	label.setFont(parent.getFont());

    	GridData tagData = new GridData(GridData.GRAB_HORIZONTAL
    			| GridData.HORIZONTAL_ALIGN_FILL);
    	fTagsText = new TagAssistField(composite, SWT.SINGLE | SWT.BORDER,tagData);

    	fTagsText.getTextControl().addModifyListener(new ModifyListener() {
    		public void modifyText(ModifyEvent e) {
    			validateInput();
    		}
    	});
    	fTagsFeedbackLabel = new Label(composite, SWT.NONE);
    	fTagsFeedbackLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
    			| GridData.HORIZONTAL_ALIGN_FILL));
    	fTagsFeedbackLabel.setBackground(fTagsText.getTextControl().getDisplay()
    			.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

    	applyDialogFont(composite);
    	return composite;
    }

	protected Label createTagsLabel(Composite parent)
	{
	    Label tagsLabel = new Label(parent, SWT.WRAP);
	    tagsLabel.setText(fMessage);
	    
        GridData data = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_CENTER);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        
        tagsLabel.setLayoutData(data);
        tagsLabel.setFont(parent.getFont());
        
	    return tagsLabel;
	}
	
	protected Text createTagsText(Composite parent)
	{
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
	    TagAssistField tagAssist = new TagAssistField(parent,SWT.SINGLE | SWT.BORDER,data);
	    Text tagText = tagAssist.getTextControl();
	    return tagText;
	}
	
	protected Label createTagsFeedbackLabel(Composite parent, Text tagsText) 
	{
		final Label feedbackLabel = new Label(parent,SWT.NONE);
		feedbackLabel.setText(TAG_FEEDACK_LABEL_TEXT);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		feedbackLabel.setLayoutData(data);
		return feedbackLabel;
	}
    
    /**
     * Returns the ok button.
     * 
     * @return the ok button
     */
    protected Button getOkButton() {
        return okButton;
    }

    /**
     * Returns the text area.
     * 
     * @return the text area
     */
    protected Text getTagsText() {
        return fTagsText.getTextControl();
    }

    
    protected void validateInput() 
    {
    	String text = fTagsText.getTextControl().getText().trim();
		String error = fValidator.isValid(text);
		
		if(error != null)
		{
			fTagsFeedbackLabel.setText(error);
			disableOK();
			return;
		}
		
		if(text.length() > 0)
		{
			String[] tags = text.split(TAG_DELIMETER_REGEX);
			
			if(tags.length > 0)
			{
				fTagsFeedbackLabel.setText(tags.length  + TAGS_FEEDBACK);
				enableOK();
				return;
			}
		}
		
		fTagsFeedbackLabel.setText(TAG_FEEDACK_LABEL_TEXT);
		enableOK();
    }

    private void disableOK()
    {
    	Control button = getButton(IDialogConstants.OK_ID);
    	
  		if (button != null) 
  		{
  			button.setEnabled(false);
  		}
    }
    
    private void enableOK()
    {
    	Control button = getButton(IDialogConstants.OK_ID);
    	
  		if (button != null) 
  		{
  			button.setEnabled(true);
  		}
    }
    
    @Override
    protected void okPressed() 
    {
		List<String> tagsList = new ArrayList<String>();

		if(getTagsText() != null)
		{
			String text = getTagsText().getText().trim();
			
			if(text.length() > 0)
			{
				String[] tagNames = text.split(TAG_DELIMETER_REGEX);

				for(String tagName : tagNames)
					if(tagName.trim().length() > 0)
						tagsList.add(tagName.trim());
			}
		}
		String[] array = new String[0];
		fTags = tagsList.toArray(array);

    	super.okPressed();
    }
    
	public String[] getTags()
	{
		return fTags;
	}
}
