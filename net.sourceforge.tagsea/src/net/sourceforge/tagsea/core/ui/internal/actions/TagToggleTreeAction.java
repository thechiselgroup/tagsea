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
package net.sourceforge.tagsea.core.ui.internal.actions;

import net.sourceforge.tagsea.core.ui.TagSEAView;

import org.eclipse.jface.action.Action;

/**
 * A simple action that toggles whether or not the tags viewer should be viewed
 * as flat, or as a tree.
 * @author Del Myers
 */

public class TagToggleTreeAction extends Action {
	private TagSEAView view;

	/**
	 * @param view
	 */
	public TagToggleTreeAction(TagSEAView view) {
		this.view = view;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		view.setViewAsHierachy(isChecked());
		view.scheduleRefresh();
	}
}
