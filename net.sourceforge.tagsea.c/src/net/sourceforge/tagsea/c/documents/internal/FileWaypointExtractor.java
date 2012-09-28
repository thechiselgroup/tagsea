/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.c.documents.internal;

import java.util.LinkedList;

import net.sourceforge.tagsea.c.waypoints.parser.CWaypointParserFactory;
import net.sourceforge.tagsea.c.waypoints.parser.ICWaypointParser;
import net.sourceforge.tagsea.c.waypoints.parser.IParsedCWaypointInfo;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * @author Del Myers
 */

public class FileWaypointExtractor {
	/**
	 * Finds the waypoints on a given file, and returns the info for them.
	 * @param file
	 */
	public static IParsedCWaypointInfo[] findWaypoints(IFile file) {
		DocumentReadSession session = new DocumentReadSession(file);
		IDocument document = session.getDocument();
		LinkedList<IParsedCWaypointInfo> infos = new LinkedList<IParsedCWaypointInfo>();
		try {
			session.begin();

			IRegion[] regions = NewWaypointDefinitionExtractor.getWaypointRegions(document);

			for (IRegion region : regions) {
				ICWaypointParser parser = CWaypointParserFactory.createParser();
				IParsedCWaypointInfo info = null;
				try {
					info = parser.parse(document.get(region.getOffset(), region.getLength()), region.getOffset());
					infos.add(info);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		} finally {
			session.end();
		}
		return infos.toArray(new IParsedCWaypointInfo[infos.size()]);
	}
}
