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
package net.sourceforge.tagsea.core.ui.internal.tags;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionContext;

/**
 * An action that can be updated using an action context.
 * @author Del Myers
 */

public class ContextAction extends Action {
	private ActionContext context = null;
	
	/**
	 * @param context the context to set
	 */
	public void setContext(ActionContext context) {
		this.context = context;
		if (context == null) { 
			setEnabled(false);
			return;
		}
		setEnabled(true);
	}
	
	/**
	 * @return the context
	 */
	public ActionContext getContext() {
		return context;
	}

}
