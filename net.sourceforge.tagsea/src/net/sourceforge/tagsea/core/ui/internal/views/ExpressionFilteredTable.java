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
package net.sourceforge.tagsea.core.ui.internal.views;

import net.sourceforge.tagsea.core.ui.internal.fieldassist.ExpressionProposalProvider;
import net.sourceforge.tagsea.core.ui.tags.TagProposalProvider;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;


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
    	Text text = new Text(parent, style);
		
		ContentAssistCommandAdapter field = new ContentAssistCommandAdapter(
			text,
			new TextContentAdapter(),
			new ExpressionProposalProvider(),
			null,
			null,
			true
		);

		text.setLayoutData(layoutData);
    	return text;
    }
}
