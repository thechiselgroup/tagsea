/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.ui.views.overview;

import org.eclipse.swt.graphics.Rectangle;

public class LineInfo 
{
	/* start offset */
	int start;
	/* end offset */
	int length;
	/* cached bounds for hit testing */
	Rectangle bounds;
}
