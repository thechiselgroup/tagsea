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
package com.ibm.research.tagging.java;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.resource.decorators.IDecoratableResourceContributor;

public class JavaResourceContributor implements IDecoratableResourceContributor {

	public IResource getResource(IWaypoint waypoint) {
		if ( waypoint instanceof JavaWaypoint )
		{
			JavaWaypoint wp = (JavaWaypoint) waypoint;
			IMarker marker = wp.getMarker();
			
			if ( marker!=null )
			{
				return marker.getResource();
			}
		}
		return null;
	}

}
