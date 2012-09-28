/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.tagsea.core.ui.internal.TextStyler;
import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItem;
import net.sourceforge.tagsea.core.ui.internal.views.IStringFinder;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Tags view label provider supports a second column that displays the number of children of a tag.
 * @author Del Myers
 */

class TagsViewLabelProvider extends WorkbenchLabelProvider implements IStyledLabelProvider {

	private IStringFinder finder;
	private Map<Font, Font> boldFonts;
	private int columnIndex;
	
	public TagsViewLabelProvider(IStringFinder finder, int index) {
		this.finder = finder;
		this.boldFonts = new HashMap<Font, Font>();
		this.columnIndex = index;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex != 1) {
			return getImage(element);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex != 1) {
			return getText(element);
		}
		if (element instanceof TagTreeItem) {
			return ((TagTreeItem)element).getWaypointCount() +"";
		}
		return "";
	}

	public StyledString getStyledText(Object element) {
		Font font = getFont(element);
		if (font == null) {
			font = PlatformUI.getWorkbench().getDisplay().getSystemFont();
		}
		Color bg = getBackground(element);
		Font boldFont = getBoldFont(font);
		Styler matchedStyler = new TextStyler(boldFont, font.getDevice().getSystemColor(SWT.COLOR_MAGENTA), bg);
		String text = getColumnText(element, columnIndex);
		IRegion[] regions = finder.findRegions(text);
		StyledString string = new StyledString();
		int start = 0;
		int end = text.length();
		for (IRegion region : regions) {
			if (start < region.getOffset()) {
				//append the portion for this unmatched region
				string.append(text.substring(start, region.getOffset()));
			}
			start = region.getOffset() + region.getLength();
			string.append(
				text.substring(region.getOffset(), start), 
				matchedStyler
			);
		}
		if (start < end) {
			string.append(text.substring(start, end));
		}
		return string;
	}

	/**
	 * @param font
	 * @return
	 */
	private Font getBoldFont(Font font) {
		
		FontData fd = font.getFontData()[0];
		if ((fd.getStyle() & SWT.BOLD) != 0) {
			return font;
		}
		Font bold = boldFonts.get(font);
		if (bold == null) {
			fd.setStyle(fd.getStyle() | SWT.BOLD);
			bold = new Font(font.getDevice(), fd);
			boldFonts.put(font, bold);
		}
		return bold;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		for (Font f : boldFonts.values()) {
			if (!f.isDisposed()) {
				f.dispose();
			}
		}
		boldFonts.clear();
	}

}
