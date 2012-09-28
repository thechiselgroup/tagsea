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
package net.sourceforge.tagsea.parsed.javabang;

import java.util.Collection;
import java.util.List;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.parser.DefaultWaypointRefactoring;
import net.sourceforge.tagsea.parsed.parser.IMutableParsedWaypointDescriptor;
import net.sourceforge.tagsea.parsed.parser.IParsedWaypointDescriptor;
import net.sourceforge.tagsea.parsed.parser.IWaypointParseProblemCollector;
import net.sourceforge.tagsea.parsed.parser.NullWaypointParseProblemCollector;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

/**
 * Generates the edits for refactoring Bang Waypoints
 * @author Del Myers
 *
 */
public class BangRefactoring extends DefaultWaypointRefactoring {

	
	@Override
	public boolean canReplaceTags(IWaypoint waypoint) {
		return true;
	}
	
	@Override
	public boolean canDelete(IWaypoint waypoint) {
		return true;
	}
	
	@Override
	public boolean canAddTags(IWaypoint waypoint) {
		return true;
	}
	
	@Override
	public boolean canRemoveTags(IWaypoint waypoint) {
		return true;
	}

	@Override
	public TextEdit delete(IWaypoint waypoint, IDocument document)
			throws UnsupportedOperationException {
		int start = waypoint.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_START, -1);
		int end = waypoint.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_END, -1);
		if (start == -1 || end == -1) return null;
		Region waypointRegion = new Region(start, end-start);
		BangCommentParser parser = (BangCommentParser) ParsedWaypointPlugin
				.getDefault().getParsedWaypointRegistry().getDefinition(
						JavaBangPlugin.WAYPOINT_KIND).getParser();
		List<IParsedWaypointDescriptor> descriptors = parser.doParseInComment(
				document, waypointRegion, new NullWaypointParseProblemCollector());
		if (descriptors.size() == 1) {
			MutableBangWaypointDescriptor descriptor = (MutableBangWaypointDescriptor) descriptors.get(0);
			Collection<IRegion> tagRegions = descriptor.getAllTagLocations();
			MultiTextEdit edit = new MultiTextEdit();
			for (IRegion region : tagRegions) {
				try {
					Region realRegion = new Region(region.getOffset()+start, region.getLength());
					String text = document.get(realRegion.getOffset(), realRegion.getLength());
					int textStart = 0;
					while (textStart < text.length() && text.charAt(textStart) == '`') {
						textStart ++;
					}
					if (textStart >= text.length()) {
						text = "";
					} else {
						text = text.substring(textStart);
					}
					edit.addChild(new ReplaceEdit(realRegion.getOffset(), realRegion.getLength(), text));
				} catch (MalformedTreeException e) {
				} catch (BadLocationException e) {
				}
			}
			return edit;
		}
		return null;
	}
	
	
	@Override
	public IMutableParsedWaypointDescriptor getMutable(IWaypoint waypoint,
			IRegion waypointRegion, IDocument document,
			IWaypointParseProblemCollector collector) {
		BangCommentParser parser = (BangCommentParser) ParsedWaypointPlugin
				.getDefault().getParsedWaypointRegistry().getDefinition(
						JavaBangPlugin.WAYPOINT_KIND).getParser();
		List<IParsedWaypointDescriptor> descriptors = 
			parser.doParseInComment(document, waypointRegion, collector);
		if (descriptors.size() == 1) {
			return (IMutableParsedWaypointDescriptor) descriptors.get(0);
		}
		return null;
	}
}
