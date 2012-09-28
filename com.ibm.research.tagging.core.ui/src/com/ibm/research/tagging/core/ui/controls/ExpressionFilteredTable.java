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
package com.ibm.research.tagging.core.ui.controls;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.fieldassist.TextControlCreator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistField;

import com.ibm.research.tagging.core.ui.fieldassist.ExpressionProposalProvider;

/**
 * similar to FilteredTable except the filter text control supports content assistance
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class ExpressionFilteredTable extends FilteredTable {

	/**
     * constructor
     * 
     * @param parent
     *            parent <code>Composite</code>
     * @param treeStyle
     *            the style bits for the <code>Tree</code>
     * @param filter
     *            the filter to be used
     */
    public ExpressionFilteredTable(Composite parent, int treeStyle, 
    		AbstractPatternFilter filter) {
        super(parent, treeStyle, filter);
    }

    protected Text createFilterTextControl(Composite parent, int style, Object layoutData) {
    	
    	// @tag expressionfilteredtable content-assist filtertable : originally wanted to make this generic, and have the proposal provider a field, but this method gets called by the super constructor, so passing the provider as a constructor parameter would result in "null" providers being passed here (the additional parameter gets processed AFTER super constructor)
    	
    	ContentAssistField contentAssistField = new ContentAssistField(
				parent,
				style,
				new TextControlCreator(),
				new TextContentAdapter(),
				new ExpressionProposalProvider(),
				null, null
				);  
    	
    	contentAssistField.getContentAssistCommandAdapter().setPopupSize(new Point(200,60));
    	
    	contentAssistField.getLayoutControl().setLayoutData(layoutData);
    	return (Text) contentAssistField.getControl();
    }
}
