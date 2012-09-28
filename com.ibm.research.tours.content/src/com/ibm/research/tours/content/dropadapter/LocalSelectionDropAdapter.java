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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourElementDropAdapter;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;

public class LocalSelectionDropAdapter implements ITourElementDropAdapter {

	public ITourElement[] convertDropData(Object data) 
	{
		IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getInstance().getSelection();
		Vector<ITourElement> elements = new Vector<ITourElement>();
		
		for(Object o : selection.toArray())
		{
			if(o instanceof IJavaElement)
			{
				IJavaElement element = (IJavaElement)o;
				int type = element.getElementType();
				
				switch(type)
				{
					case IJavaElement.TYPE : // ok
					case IJavaElement.METHOD : // ok
					case IJavaElement.FIELD : // ok
					//case IJavaElement.IMPORT_DECLARATION : // not intresting
					//case IJavaElement.IMPORT_CONTAINER : // not intresting
					//case IJavaElement.PACKAGE_DECLARATION : // not intresting
					//case IJavaElement.PACKAGE_FRAGMENT : // as resource
					//case IJavaElement.JAVA_PROJECT : // as resource
					//case IJavaElement.CLASS_FILE : // as resource
						elements.add(new ResourceURLTourElement(element));
						continue;
					default:
						break;
				}
			}

			// This will be processed as a resource
			IResource resource = null;
			
			if (o instanceof IJavaElement) 
			{
				// don't use IAdaptable as for members only the top level type adapts
				resource= ((IJavaElement) o).getResource();
			} 
			else if (o instanceof IAdaptable) 
			{
				resource= (IResource) ((IAdaptable) o).getAdapter(IResource.class);
			}
			
			if (resource != null)
				elements.add(new ResourceURLTourElement(resource));
			else
			{
				MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				box.setText("Unsupported drop operation");
				box.setMessage(o.getClass().getName());
				box.open();
			}
		}
		
		return elements.toArray(new ITourElement[0]);
	}

	public Transfer getTransfer() 
	{
		return LocalSelectionTransfer.getInstance();
	}
}
