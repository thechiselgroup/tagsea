/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * A runnable operation that represents a collection of operations that must be done on
 * the waypoint or tags models. Notification of model updates will be suppressed until the
 * end of the running of the operation, and will be concatinated in order to give the least
 * ammount of notifications possible. For example, clients will not be notified of both
 * an "about to be deleted" event and a "deleted" event on a tag or waypoint because
 * the element will have been deleted by the end of the operation.
 * 
 * The order of the states in a TagSEAOperation always follow this order:
 * 
 * CREATED, QUEUED, WAITING, RUNNING, QUITING, DONE.
 * 
 * Clients can register an {@link ITagSEAOperationStateListener} with the platform in order
 * to listen to the change of state of operations that have been registered with the platform.
 * Since listeners aren't notified about changes in state until after the operation has been
 * sent to the platform (i.e. after it has been queued), only the states after Queued will be
 * posted.
 * 
 * NOTE: it is important that all extenders of this method call <code>super</code> for all
 * non-abstract methods on this class in order that the state can be properly updated.
 * 
 * @author Del Myers
 */

public abstract class TagSEAOperation {
	/**
	 * The state of the operation.
	 * @author Del Myers
	 */
	public enum OperationState {
		/**
		 * Simple state that this operation has been created.
		 */
		CREATED,
		/**
		 * The operation has been added to the queue of operations to run.
		 */
		QUEUED,
		/**
		 * The operation has been created and is waiting to be run.
		 */
		WAITING,
		/**
		 * The operation has started to run.
		 */
		RUNNING,
		/**
		 * The operation is finished running, but is waiting for all events to be fired.
		 */
		QUITING,
		/**
		 * The operation is finished and all events have been fired.
		 */
		DONE
	}
	
	private OperationState state;
	private String name;
	
	
	/**
	 * Default constructor for a TagSEA operation. Gives the operation the name "Updating Waypoints".
	 *
	 */
	public TagSEAOperation() {
		this("Updating Waypoints...");
	}
	
	/**
	 * Constructor that gives a name to this operation. The name is used as feedback when the operation
	 * runs. The name cannot be null.
	 * @param name
	 */
	public TagSEAOperation(String name) {
		state = OperationState.CREATED;
		this.name = name;
	}
	
	/**
	 * Sets the name of this operation. If called, must be called before the operation is run. 
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Runs the operation in the current thread.
	 * @param moitor
	 */
	public abstract IStatus run(IProgressMonitor monitor) throws InvocationTargetException;
	
	/**
	 * Signal to tell the operation that it has started. Extenders can override, but must call super
	 * first.
	 *
	 */
	public void operationStarted() {
		this.state = OperationState.RUNNING;
	}
	
	/**
	 * Signal to tell the operation that it has finished, and all events have been fired.
	 * Extenders may override, but must call super first.
	 *
	 */
	public void operationDone() {
		this.state = OperationState.DONE;
	}
	
	/**
	 * The operation has entered the front of the queue, but is waiting for model changes to finish.
	 * Extenders may override, but must call super first.
	 */
	public void operationWaiting() {
		this.state = OperationState.WAITING;
	}
	
	/**
	 * The operation has finished running, but is waiting for all the events to be fired.
	 * Extenders may override, but must call super first.
	 *
	 */
	public void operationQuiting() {
		this.state = OperationState.QUITING;
	}
	
	/**
	 * The operation has been queued. Extenders may override, but must call super first.
	 *
	 */
	public void operationQueued() {
		this.state = OperationState.QUEUED;
	}
	
	
	
	/**
	 * Returns the current state of the operation.
	 * @return the current state of the operation.
	 */
	public final OperationState getState() {
		return state;
	}
	
	/**
	 * Returns a rule that will be used to schedule this operation. For example, if this operation needs
	 * a lock on a resource in the workspace, then this method should return the appropriate rule. Note
	 * that it is important that if an operation runs sub-operations, and waits for their completion that
	 * the parent operation knows about all of the rules of its children. If conflicting rules occur, then
	 * the sub operation will not be run in order to avoid deadlock.
	 * 
	 * Returns null by default. Extenders may override.
	 * 
	 * @return the rule used by operation to schedule its running.
	 */
	public ISchedulingRule getRule() {
		return null;
	}
	
	
}
