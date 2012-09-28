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

package com.ibm.research.tagging.resource.wizards;

import com.ibm.research.tagging.core.ui.wizards.WaypointPage;
import com.ibm.research.tagging.resource.ResourceWaypointPlugin;

public class NewResourceWaypointPage extends WaypointPage 
{	
	private final static String WIZARD_ICON = "icons/newsrcfldr_wiz.gif";
	
	private final static String PAGE_TITLE = "Resource Waypoint";
	private final static String PAGE_DESCRIPTION = "Fill in the provided fields to create a new resource waypoint.";
	
	protected NewResourceWaypointPage() 
	{
		super(PAGE_TITLE,PAGE_DESCRIPTION,ResourceWaypointPlugin.getImageDescriptor(WIZARD_ICON));
	}
}