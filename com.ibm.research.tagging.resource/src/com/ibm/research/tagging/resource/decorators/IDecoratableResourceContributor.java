/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/

package com.ibm.research.tagging.resource.decorators;

import org.eclipse.core.resources.IResource;

import com.ibm.research.tagging.core.IWaypoint;

public interface IDecoratableResourceContributor {
	/**
	 * for a given waypoint, return the associated IResource
	 * @param waypoint
	 * @return IResource or null if cannot find associated resource
	 */
	public IResource getResource(IWaypoint waypoint);
}
