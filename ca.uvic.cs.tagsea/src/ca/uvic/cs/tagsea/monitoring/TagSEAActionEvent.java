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

/**
 * monitoring miscelatious actions.
 * @author Del Myers
 *
 */
public class TagSEAActionEvent implements ITagSEAMonitoringEvent {
	public enum Type {
		Filter,
		Refresh,
		AutoComplete
	}
	
	private String data;
	private Type type;
	
	public TagSEAActionEvent(Type type, String data) {
		this.type = type;
		this.data = data;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	
	
	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitoringEvent#getKind()
	 */
	public EventKind getKind() {
		return EventKind.Action;
	}

}
