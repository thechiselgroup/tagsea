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
import com.ibm.research.tagging.resource.ResourceWaypoint;

public class ResourceWaypointContributor implements
		IDecoratableResourceContributor {

	public IResource getResource(IWaypoint waypoint) {
		
		if ( waypoint instanceof ResourceWaypoint )
		{
			return ((ResourceWaypoint) waypoint).getMarker().getResource();
		}
		
		return null;
	}

}
