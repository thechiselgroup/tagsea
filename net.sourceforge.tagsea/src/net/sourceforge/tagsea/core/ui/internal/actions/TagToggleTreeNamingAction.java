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

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.internal.ITagSEAPreferences;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Action to toggle whether or not refactorings should keep hierarchical naming
 * even when the view is not set as a tree.
 * @author Del Myers
 */

public class TagToggleTreeNamingAction extends Action {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
		boolean asHierarchy = store.getBoolean(ITagSEAPreferences.TAGS_VIEW_TREE_NAMING);
		store.setValue(ITagSEAPreferences.TAGS_VIEW_TREE_NAMING, !asHierarchy);
	}
}
