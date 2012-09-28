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

import org.eclipse.ui.IViewPart;

import ca.uvic.cs.tagsea.ui.views.RoutesView;
import ca.uvic.cs.tagsea.ui.views.TagsView;

/**
 * 
 * @author Del Myers
 *
 */
public class TagSEAPartEvent implements ITagSEAMonitoringEvent {
	public enum Type {
		ViewOpened,
		ViewClosed,
		ViewActivated,
		ViewDeactivated
	}
	private Type type;
	private IViewPart view;
	
	public TagSEAPartEvent(Type type, RoutesView view) {
		this.view = view;
		this.type = type;
	}
	
	public TagSEAPartEvent(Type type, TagsView view) {
		this.view = view;
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitoringEvent#getKind()
	 */
	public EventKind getKind() {
		return EventKind.Part;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Guaranteed to be of type TagsView of of type RoutesView
	 * @return the view
	 */
	public IViewPart getView() {
		return view;
	}

}
