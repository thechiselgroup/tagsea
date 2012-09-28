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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * An event for selection.
 * @author Del Myers
 *
 */
public class TagSEASelectionEvent implements ITagSEAMonitoringEvent {
	private ISelection selection;
	private IWorkbenchPart part;
	/**
	 * 
	 */
	public TagSEASelectionEvent(IWorkbenchPart part, ISelection selection) {
		this.part = part;
		this.selection = selection;
	}
	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitoringEvent#getKind()
	 */
	public EventKind getKind() {
		return EventKind.Selection;
	}
	
	/**
	 * @return the part
	 */
	public IWorkbenchPart getPart() {
		return part;
	}
	
	/**
	 * @return the selection
	 */
	public ISelection getSelection() {
		return selection;
	}

}
