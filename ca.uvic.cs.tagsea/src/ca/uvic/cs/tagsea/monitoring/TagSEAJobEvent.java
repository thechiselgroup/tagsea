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
 * Marks the beginning or end of a tagSEA job (e.g. Refreshing or loading tags/routes).
 * @author Del Myers
 *
 */
public class TagSEAJobEvent implements ITagSEAMonitoringEvent {
	public enum JobType {
		StartLoadingTags,
		EndLoadingTags,
		StartUpdatingTags,
		EndUpdatingTags,
		StartLoadingRoutes,
		EndLoadingRoutes
	}
	private JobType type;
	public TagSEAJobEvent(JobType type) {
		this.type = type;
	}
	
	/**
	 * @return the type
	 */
	public JobType getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitoringEvent#getKind()
	 */
	public EventKind getKind() {
		return EventKind.Job;
	}

}
