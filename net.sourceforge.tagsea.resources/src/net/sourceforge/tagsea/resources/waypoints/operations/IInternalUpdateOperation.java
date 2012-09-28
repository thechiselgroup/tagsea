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
package net.sourceforge.tagsea.resources.waypoints.operations;

/**
 * A simple interface used for type checking to indicate that a resource waypoint internal operation is running
 * so that the operation can be allowed to update revisions, stamps, etc.
 * @author Del Myers
 *
 */
public interface IInternalUpdateOperation {

}
