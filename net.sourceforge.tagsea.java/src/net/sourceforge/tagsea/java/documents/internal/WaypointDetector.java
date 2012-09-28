/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *     IBM Corporation
 *******************************************************************************/
package net.sourceforge.tagsea.java.documents.internal;

import org.eclipse.jface.text.rules.IWordDetector;
@Deprecated
public class WaypointDetector implements IWordDetector
{
	public boolean isWordStart(char c) 
	{	
		if(c == WaypointDefinitionExtractor.TAG_START_CHAR)
			return true;
		
		return false;
	}

	public boolean isWordPart(char c) 
	{		
		if((c == 't') || (c == 'a') ||(c == 'g') )
			return true;
		
		return false;
	}
}
