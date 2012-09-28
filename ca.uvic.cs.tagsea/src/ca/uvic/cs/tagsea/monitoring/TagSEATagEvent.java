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
package ca.uvic.cs.tagsea.monitoring;

import ca.uvic.cs.tagsea.core.Tag;

/**
 * An event for monitoring tags.
 * @author Del Myers
 *
 */
public class TagSEATagEvent implements ITagSEAMonitoringEvent {
	public enum Tagging {
		New,
		Removed
	}
	
	private Tag tag;
	private Tagging tagging;
	
	/**
	 * 
	 */
	public TagSEATagEvent(Tagging tagging, Tag tag) {
		this.tagging = tagging;
		this.tag = tag;
	}

	/**
	 * @return the tag
	 */
	public Tag getTag() {
		return tag;
	}
	
	/**
	 * @return the tagging
	 */
	public Tagging getTagging() {
		return tagging;
	}

	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitoringEvent#getKind()
	 */
	public EventKind getKind() {
		return EventKind.Tagging;
	}
	
	
}
