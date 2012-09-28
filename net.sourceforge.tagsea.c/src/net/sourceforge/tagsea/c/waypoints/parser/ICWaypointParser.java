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

package net.sourceforge.tagsea.c.waypoints.parser;


public interface ICWaypointParser 
{
	public static final String TAG_SEQUENCE = "@tag"; //$NON-NLS-1$
	public IParsedCWaypointInfo parse(String waypointDefinition, int offset);
}
