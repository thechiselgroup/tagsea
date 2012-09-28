/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tours.content.dropadapter;

import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.ResourceTransfer;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourElementDropAdapter;

public class ResourceDropAdapter implements ITourElementDropAdapter {

	public ITourElement[] convertDropData(Object data) 
	{
		IResource[] resources = (IResource[])data;
		Vector<ITourElement> elements = new Vector<ITourElement>();
	
		
		return elements.toArray(new ITourElement[0]);
	}

	public Transfer getTransfer() 
	{
		return ResourceTransfer.getInstance();
	}

}
