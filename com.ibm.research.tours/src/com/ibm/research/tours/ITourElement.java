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
package com.ibm.research.tours;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;

/**
 * The interface for an element in a tour
 * All tour element implementations must support a default constructor
 * @author mdesmond
 */
public interface ITourElement
{	
	public static final int START_ON_CLICK = 1;
	public static final int START_WITH_PREVIOUS = 2;
	public static final int START_AFTER_PREVIOUS = 3;
	
	/**
	 * Set the transition
	 * @param transition
	 */
	public void setTransition(int transition);
	
	/*
	 * Get the transition
	 */
	public int getTransition();
	
	/**
	 * Set the time limite on this element
	 */
	public void setTimeLimit(ITimeLimit limit);
	
	/**
	 * Set the time limit on this element
	 */
	public ITimeLimit getTimeLimit();
	
	/**
	 * Get the notes associated of this element
	 * @return
	 */
	public String getNotes();
	
	/**
	 * Set the description of this element
	 * @return
	 */
	public void setNotes(String notes);

	/**
	 * Get the ui text
	 * @return
	 */
	public String getText();
	
	/**
	 * Get the short runtime ui text
	 * @return
	 */
	public String getShortText();
	
	/**
	 * Get the image
	 * @return
	 */
	public Image getImage();
	
	/**
	 * 
	 * @return
	 */
	public Color getBackground();
	
	/**
	 * 
	 * @return
	 */
	public Color getForeground();
	
	/**
	 * 
	 * @return
	 */
	public Font getFont();
	
	/**
	 * 
	 * @param listener
	 */
	public void addTourElementListener(ITourElementListener listener);
	
	/**
	 * 
	 * @param listener
	 */
	public void removeTourElementListener(ITourElementListener listener);
	
	/**
	 * Start this tour element
	 */
	public void start();
	
	/**
	 * Run this tour element
	 */
	public void transition();
	
	/**
	 * Stop this tour element
	 */
	public void stop();
	
	/**
	 * Save state to the given memento
	 * @param memento
	 */
	public void save(IMemento memento);
	
	/**
	 * Load state from the given memento
	 * @param memento
	 */
	public void load(IMemento memento);
	
	/**
	 * Load state from the given memento
	 * @param memento
	 */
	public ITourElement createClone();
}
