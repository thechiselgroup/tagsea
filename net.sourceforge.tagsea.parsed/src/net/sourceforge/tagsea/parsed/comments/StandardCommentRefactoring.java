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
package net.sourceforge.tagsea.parsed.comments;

import java.io.IOException;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.comments.StandardMutableCommentWaypointDescriptor.TagStyle;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;
import net.sourceforge.tagsea.parsed.parser.IMutableParsedWaypointDescriptor;
import net.sourceforge.tagsea.parsed.parser.IWaypointParseProblemCollector;
import net.sourceforge.tagsea.parsed.parser.IWaypointParser;
import net.sourceforge.tagsea.parsed.parser.IWaypointRefactoring;
import net.sourceforge.tagsea.parsed.parser.WaypointParseError;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;

/**
 * Refactoring methods for standard comments.
 * @author Del Myers
 *
 */
public class StandardCommentRefactoring implements IWaypointRefactoring {

	
	public boolean canAddTags(IWaypoint waypoint) {
		return true;
	}

	public boolean canDelete(IWaypoint waypoint) {
		return true;
	}

	public boolean canRemoveTags(IWaypoint waypoint) {
		return true;
	}

	public boolean canReplaceTags(IWaypoint waypoint) {
		return true;
	}

	public boolean canSetAuthor(IWaypoint waypoint) {
		return true;
	}

	public boolean canSetDate(IWaypoint waypoint) {
		return true;
	}

	public boolean canSetMessage(IWaypoint waypoint) {
		return true;
	}

	public TextEdit delete(IWaypoint waypoint, IDocument document)
			throws UnsupportedOperationException {
		int offset = waypoint.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_START, -1);
		int end = waypoint.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_END, -1);
		IParsedWaypointDefinition def = ParsedWaypointUtils.getWaypointDefinition(waypoint);
		char[] metaCharacters = new char[] {
				'$', '^', '(', ')', '.', '*', '+', '?', '[', ']', '\\'
		};
		if (def instanceof StandardCommentWaypointDefinition) {
			IWaypointParser p = def.getParser();
			if (p instanceof StandardCommentParser) {
				StandardCommentParser parser = (StandardCommentParser) p;
				int type = parser.getCommentTypeFor(waypoint);
				if (type == StandardCommentParser.SINGLELINE) {
					//generate a method to delete the line.
					//get the whole line from the document.
					try {
						int line = document.getLineOfOffset(offset);
						int lineLength = document.getLineLength(line);
						int lineStart = document.getLineOffset(line);
						String lineString = document.get(lineStart, lineLength);
						lineString = lineString.trim();
						for (String startString : parser.getSingleLineIndicators()) {
							for (char mc : metaCharacters) {
								startString = startString.replace(""+mc, "\\"+mc);
							}
							String regEx = "^"+startString+"(["+startString+"])*\\s*@tag((\\s+.*)|$)";
							if (lineString.matches(regEx)) {
								return new DeleteEdit(lineStart, lineLength);
							}
							
						}
					} catch (BadLocationException e) {
						//do nothing here, just return a regular delete later.
					}
				}
			}
		}
		if (offset < 0 || end < 0) {
			return null;
		}
		return new DeleteEdit(offset, end-offset);
		
	}

	public IMutableParsedWaypointDescriptor getMutable(IWaypoint waypoint,
			IRegion waypointRegion, IDocument document, IWaypointParseProblemCollector collector) {
		try {
			int line = document.getLineOfOffset(waypointRegion.getOffset());
			StandardCommentWaypointDescriptor descriptor = StandardCommentTextParser.parse(document.get(waypointRegion.getOffset(), waypointRegion.getLength()), waypointRegion.getOffset(), line);
			return new StandardMutableCommentWaypointDescriptor(descriptor, TagStyle.DOT);
		} catch (MalformedWaypointException e) {
			collector.accept(new WaypointParseError(e.getMessage(), e.getOffset() + waypointRegion.getOffset(), e.getLength(), document));
		} catch (IOException e) {
			collector.accept(new WaypointParseError("Error reading waypoint", waypointRegion.getOffset(), waypointRegion.getLength(), document));
		} catch (BadLocationException e) {
			//can't do anything
		}
		return null;
	}

}
