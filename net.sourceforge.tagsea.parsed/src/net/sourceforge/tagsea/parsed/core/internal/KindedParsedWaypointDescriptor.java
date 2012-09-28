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
package net.sourceforge.tagsea.parsed.core.internal;

import java.util.Date;

import net.sourceforge.tagsea.parsed.parser.IParsedWaypointDescriptor;

/**
 * A proxy to a parsed waypoint descriptor that carries with it the kind of waypoint that it describes.
 * @author Del Myers
 *
 */
public class KindedParsedWaypointDescriptor implements
		IParsedWaypointDescriptor {

	private IParsedWaypointDescriptor real;
	private String kind;

	/**
	 * 
	 * @param realDescriptor the real descriptor that this waypoint descriptor describes.
	 * @param kind the kind of waypoint that the descriptor describes.
	 */
	public KindedParsedWaypointDescriptor(IParsedWaypointDescriptor realDescriptor, String kind) {
		this.real = realDescriptor;
		this.kind = kind;
	}
	
	public String getKind() {
		return kind;
	}
	
	public String getAuthor() {
		return real.getAuthor();
	}

	public int getCharEnd() {
		return real.getCharEnd();
	}

	public int getCharStart() {
		return real.getCharStart();
	}

	public Date getDate() {
		return real.getDate();
	}

	public String getDetail() {
		return real.getDetail();
	}

	public String getMessage() {
		return real.getMessage();
	}

	public String[] getTags() {
		return real.getTags();
	}

	public int getLine() {
		return real.getLine();
	}

}
