/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.internal;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;

/**
 * An adapter for delegating styled cell label providers which will also provide text when
 * requested.
 * @author Del Myers
 *
 */
public class TagSEADelegatingStyledCellLabelProvider extends
		DelegatingStyledCellLabelProvider implements ILabelProvider {

	public TagSEADelegatingStyledCellLabelProvider(
			IStyledLabelProvider labelProvider) {
		super(labelProvider);
	}

	public String getText(Object element) {
		if (getStyledStringProvider() instanceof ILabelProvider) {
			return ((ILabelProvider)getStyledStringProvider()).getText(element);
		}
		StyledString styledString = getStyledText(element);
		return (styledString != null) ? styledString.getString() : null;
	}
}
