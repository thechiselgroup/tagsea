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
 * An event for when refactoring occurs. Used for monitoring.
 * @author Del Myers
 *
 */
public class TagSEARefactorEvent implements ITagSEAMonitoringEvent {
	public enum Refactoring {
		Delete,
		Rename,
		Move
	}
	
	private Tag tag;
	private Refactoring refactor;
	private Object oldValue;
	
	/**
	 * 
	 * @param refactor
	 * @param tag
	 * @param oldValue the value before the change. For Delete and move, this will be the old 
	 * parent, for Rename, this will be the old name, for 
	 */
	public TagSEARefactorEvent(Refactoring refactor, Tag tag, Object oldValue) {
		this.refactor = refactor;
		this.tag = tag;
		this.oldValue = oldValue;
	}
	
	/**
	 * @return the tag
	 */
	public Tag getTag() {
		return tag;
	}
	
	/**
	 * @return the refactor
	 */
	public Refactoring getRefactor() {
		return refactor;
	}

	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitoringEvent#getKind()
	 */
	public EventKind getKind() {
		return EventKind.Refactoring;
	}
	
	/**
	 * @return the oldValue
	 */
	public Object getOldValue() {
		return oldValue;
	}
	
	
}
