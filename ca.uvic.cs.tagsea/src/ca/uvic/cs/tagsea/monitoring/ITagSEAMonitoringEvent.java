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
 * A generic event type for monitoring
 * @author Del Myers
 *
 */
public interface ITagSEAMonitoringEvent {
	public enum EventKind {
		/** Event can be casted to a TagSEATagEvent */
		Tagging,
		/** Event can be casted to a TagSEAWaypointEvent */
		Waypointing,
		/** Event can be casted to a TagSEARoutingEvent */
		Routing,
		/** Event can be casted to a TagSEARefactorEvent */
		Refactoring,
		/** Event can be casted to a TagSEASelectionEvent */
		Selection,
		/** Event can be casted to a TagSEAJobEvent */
		Job,
		/** Event can be casted to a TagSEANavigationEvent */
		Navigation,
		/** Event can be casted to a TagSEAPartEvent */
		Part,
		/** Event can be casted to a TagSEAActionEvent */
		Action
	}

	public EventKind getKind();
}
