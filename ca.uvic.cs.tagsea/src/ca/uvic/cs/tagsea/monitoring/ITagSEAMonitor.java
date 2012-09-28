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
 * A monitor for tagSEA events.
 * @author Del Myers
 *
 */
public interface ITagSEAMonitor {
	
	/**
	 * Returns a non-null, non-empty string that will be used to prompt tagSEA
	 * users for thier consent in using this monitor before it gets hooked into
	 * the system.
	 * @return a non-null, non-empty string that will be used to prompt tagSEA
	 * users for thier consent in using this monitor before it gets hooked into
	 * the system.
	 */
	public String getAgreementString();
	
	/**
	 * Called just before the monitor is started for the first time this session.
	 *
	 */
	void activate();
	void selectionOccurred(IWorkbenchPart part, ISelection selection);
	void refactorOccurred(TagSEARefactorEvent evt);
	void tagOccurred(TagSEATagEvent evt);
	void waypointOccurred(TagSEAWaypointEvent evt);
	void navigationOccurred(TagSEANavigationEvent evt);
	void routingOccurred(TagSEARoutingEvent evt);
	void jobEventOccurred(TagSEAJobEvent evt);
	void partEventOccurred(TagSEAPartEvent evt);
	void actionEventOccurred(TagSEAActionEvent evt);

	/**
	 * @return the name of the monitor
	 */
	public String getName();
}
