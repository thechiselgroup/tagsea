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
package net.sourceforge.tagsea.core.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * UI used to apply settings to an IWaypointFilter. There is exactly one IWaypointFilterUI
 * per waypoint filter, and one per waypoint type. The IWaypointFilterUI must be contributed
 * in the same plugin as defines the waypoint type.
 * 
 * Contributed via the <code>net.sourceforge.tagsea.filters</code> extension.
 * 
 * @author Del Myers
 */
public interface IWaypointFilterUI {

	/**
	 * Initialize this ui according to the settings for the given filter. The filter is
	 * guaranteed to be initialized before this method is called. It is recommended that
	 * implementors keep a reference to the filter. 
	 * @param filter the filter that this UI will apply settings to.
	 */
	void initialize(IWaypointFilter filter);
	
	/**
	 * Creates the control for this UI element.
	 * @param parent the parent of the control.
	 * @return the new control.
	 */
	Control createControl(Composite parent);
	
	/**
	 * Called when the UI is about to close, and the user has selected to apply changes to the filter.
	 *
	 */
	void applyToFilter();
}
