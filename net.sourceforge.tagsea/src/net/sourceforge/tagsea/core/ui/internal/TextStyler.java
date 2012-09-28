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
package net.sourceforge.tagsea.core.ui.internal;

import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;

/**
 * A simple text styler that uses a font, an underline and a background colour for styling strings.
 * @author Del Myers
 *
 */
public class TextStyler extends Styler {

	private Font font;
	private Color fg;
	private Color bg;

	/**
	 * Creates a new text styler
	 * @param font the font to use for the styler
	 * @param underline the underline colour
	 * @param background the background colour
	 */
	public TextStyler(Font font, Color underline, Color background) {
		this.font = font;
		this.fg = underline;
		this.bg = background;
	}
	
	@Override
	public void applyStyles(TextStyle textStyle) {
		textStyle.background = bg;
		//textStyle.foreground = fg;
		textStyle.font = font;
		textStyle.underline=true;
		textStyle.underlineColor=fg;
		textStyle.underlineStyle = SWT.UNDERLINE_DOUBLE;
	}
	
}