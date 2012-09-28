/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/

package com.ibm.research.tagging.resource.decorators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkingSet;

/**
 * decorates a list of resources in the workspace
 * 
 * @tag todo optimize resources package-explorer decorator : this is a fairly simplistic approach to decorating - run through a list every time and redecorate.  this needs to be optimized if working with large workspaces
 *  
 * @author Li-Te Cheng
 * CUE, IBM Resesearch 2006
 */

public class ResourceWaypointDecorator implements ILightweightLabelDecorator {

	private Color fColor;
	private List<ILabelProviderListener> listeners = new ArrayList<ILabelProviderListener>();
	private Set<IAdaptable> fResources, fOldResources;
	
	public void decorate(final Object element, final IDecoration decoration) {
		
		if ( fColor==null )
		{
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					fColor = new Color(Display.getDefault(),95,238,255);   // same color as the waypoint annotation marker
					applyDecoration(element,decoration);
				}
			});
		}
		else
			applyDecoration(element,decoration);
	}
	
	private void applyDecoration(Object element, IDecoration decoration) 
	{
		if ( fResources==null )
			decoration.setBackgroundColor(null);
		else if ( isWaypointedObject(element) )
			decoration.setBackgroundColor(fColor);
		else
			decoration.setBackgroundColor(null);
	}
	
	private boolean isWaypointedObject(Object o)
	{
		if ( o instanceof IJavaElement )
		{
			o = ((IJavaElement) o).getResource();
		}
		
		if ( fResources.contains(o) )
			return true;
		
		if ( o instanceof IAdaptable )
		{
			IAdaptable adaptable = (IAdaptable)  o;
			IContainer container = (IContainer)  adaptable.getAdapter(IContainer.class),
			           project   = (IProject)    adaptable.getAdapter(IProject.class),
			           folder    = (IFolder)     adaptable.getAdapter(IFolder.class);
			IWorkingSet set		 = (IWorkingSet) adaptable.getAdapter(IWorkingSet.class);
			
			if ( project!=null )
				container = (IContainer) project;
			else if ( folder!=null )
				container = (IContainer) folder;
			else if ( set!=null )
				return fResources.contains(set);
			
			return fResources.contains(container);
		}
		
		return false;
	}
	
	public void setResources(Set<IAdaptable> resources)
	{
		fOldResources = fResources;
		fResources = resources;
	}

	public void addListener(ILabelProviderListener listener) {
		listeners .add(listener);
	}

	public void dispose() {
		if ( fColor!=null && !fColor.isDisposed() )
			fColor.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		listeners.remove(listener);
	}
	
	public void fireLabelProviderChanged(LabelProviderChangedEvent event) {
		for (ILabelProviderListener listener : listeners)
			listener.labelProviderChanged(event);
	}

	// must be called in UI thread
	public void refresh()
	{
		Set<IAdaptable> union = new HashSet<IAdaptable>();
		
		if ( fOldResources!=null )
			union.addAll(fOldResources);
		if ( fResources!=null )
			union.addAll(fResources);
		
		if ( union.size()>0 )
			fireLabelProviderChanged(new LabelProviderChangedEvent(this,union.toArray()));
	}
}
