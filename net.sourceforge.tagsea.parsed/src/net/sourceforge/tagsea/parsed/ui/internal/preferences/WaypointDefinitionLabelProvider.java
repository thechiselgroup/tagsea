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
package net.sourceforge.tagsea.parsed.ui.internal.preferences;

import net.sourceforge.tagsea.parsed.IParsedWaypointImageConstants;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointPresentation;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class WaypointDefinitionLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		return ((IParsedWaypointDefinition)element).getName();
	}
	
	@Override
	public Image getImage(Object element) {
		IParsedWaypointPresentation presentation  = 
			((IParsedWaypointDefinition)element).getPresentation();
		if (presentation != null) {
			return presentation.getImage();
		}
		return ParsedWaypointPlugin.getDefault().getImageRegistry().get(IParsedWaypointImageConstants.PARSED_WAYPOINT);
	}
	
}