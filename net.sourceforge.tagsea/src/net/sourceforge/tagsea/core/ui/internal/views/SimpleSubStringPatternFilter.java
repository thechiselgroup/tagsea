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
package net.sourceforge.tagsea.core.ui.internal.views;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * A pattern filter that does simple substring matching.
 * @author Del Myers
 *
 */
public class SimpleSubStringPatternFilter extends AbstractPatternFilter
		implements IStringFinder {

	private String[] pattern = new String[0];

	@Override
	public boolean isElementSelectable(Object data) {
		return true;
	}

	@Override
	public boolean isLeafMatch(Viewer tableViewer, Object element) {
		if (pattern == null || pattern.length == 0) {
			return true;
		}
		IBaseLabelProvider labelProvider = ((StructuredViewer)tableViewer).getLabelProvider();
		ArrayList<String> textToMatch = new ArrayList<String>();
		if (tableViewer instanceof TableViewer) {
			int cols = ((TableViewer)tableViewer).getTable().getColumnCount();
			for (int i = 0; i < cols; i++) {
				String text = "";
				CellLabelProvider cProvider = (CellLabelProvider) ((TableViewer)tableViewer).getLabelProvider(i);
				if (cProvider instanceof ILabelProvider) {
					text = ((ILabelProvider)cProvider).getText(element);
				}
				String[] split = text.split("\\s+");
				textToMatch.addAll(Arrays.asList(split));
			}
		}
		if (textToMatch.size() == 0) {
			if (labelProvider instanceof ILabelProvider) {
				textToMatch.add((((ILabelProvider)labelProvider).getText(element)));
			}
		}
		for (int i = 0; i < textToMatch.size(); i++) {
			String word = textToMatch.get(i).toLowerCase();
			if (word == null || word.length() == 0) {
				continue;
			}
			for (int j = 0; j < pattern.length; j++) {
				if (word.contains(pattern[j])) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void setPattern(String text) {
		if (text == null) {
			this.pattern = new String[0];
			return;
		}
		String[] split = text.split("\\s+");
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < split.length; i++) {
			String s = split[i].toLowerCase();
			if (s.length() > 0) {
				result.add(s);
			}
		}
		this.pattern = result.toArray(new String[result.size()]);
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return isLeafMatch(viewer, element);
	}

	public IRegion[] findRegions(String text) {
		String textToMatch = text.toLowerCase();
		ArrayList<IRegion> regions = new ArrayList<IRegion>();
		//find matching substring regions.
		for (int i = 0; i < pattern.length; i++) {
			String p = pattern[i];
			int searchLength = textToMatch.length()-p.length();
			for (int j = 0; j <= searchLength; j++) {
				if (textToMatch.substring(j, j+p.length()).equals(p)) {
					IRegion newRegion = new Region(j, p.length());
					boolean overlapped = false;
					for (int ri = 0; ri < regions.size(); ri++) {
						IRegion oldRegion = regions.get(ri);
						if (TextUtilities.overlaps(oldRegion, newRegion)) {
							// union the regions
							overlapped = true;
							int newOffset = Math.min(oldRegion.getOffset(), newRegion.getOffset());
							int newEnd = Math.max(newOffset + oldRegion.getLength(), newOffset + newRegion.getLength());
							newRegion = new Region(
									newOffset,
									newEnd-newOffset
							);
							regions.set(ri, newRegion);
						}
					}
					if (!overlapped) {
						regions.add(newRegion);
					}
				}
			}
			
		}
		return regions.toArray(new IRegion[regions.size()]);
	}

}
