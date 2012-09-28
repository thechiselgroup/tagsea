/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Victoria - initial API and implementation
 *     University of Victoria
 *******************************************************************************/

package com.ibm.research.tours;

/**
 * Extension to the tour element drop adapter that adds context to the drop function.
 * 
 * @author Chris Bennett, Del Myers
 *
 */
public interface ITourElementDropAdapterExtension extends
		ITourElementDropAdapter {

	/**
	 * A version of convertDropData that specifies context in the form of a tour 
	 * @param data
	 * @param tour
	 * @return
	 */
	public ITourElement[] convertDropData(Object data, ITour tour);
	
}
