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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public abstract class AbstractPatternFilter extends ViewerFilter
{
	public abstract boolean isLeafMatch(Viewer tableViewer, Object data);
	public abstract boolean isElementSelectable(Object data);
	public abstract void setPattern(String text);
}
