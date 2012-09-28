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
package com.ibm.research.tagging.core.ui.fieldassist;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.fieldassist.TextControlCreator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistField;

/**
 * helper class to create content-assist enabled text controls that provide
 * tag suggestions when control-space is used.  Note that this really creates
 * a widget (the layout control) that contains the text control. 
 * 
 * @tag content-assist command-complete auto-complete field-assist : example of using ContentAssistField
 * 
 * @author Li-Te Cheng
 *
 */
public class TagAssistField {

	public static final int FIELD_ASSIST_INDENT = 5;	// indent required to align other controls in a column with this field		
	
	private ContentAssistField tagAssistField;

	public TagAssistField(Composite parent, int style, Object layoutData)
	{
		tagAssistField = new ContentAssistField(
				parent,
				style,
				new TextControlCreator(),
				new TextContentAdapter(),
				new TagContentProposalProvider(),
				null, null
				);  
		
		Control layoutControl = tagAssistField.getLayoutControl();
	    layoutControl.setLayoutData(layoutData);
	}
	
	public ContentAssistField getContentAssistField() {
		return tagAssistField;
	}
	
	public Text getTextControl() {
		return (Text) tagAssistField.getControl();
	}
	
	public Control getLayoutControl() {
		return tagAssistField.getLayoutControl();
	}
}
