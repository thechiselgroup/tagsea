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
package com.ibm.research.tours.serializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

import com.ibm.research.tours.ITimeLimit;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.TimeLimit;
import com.ibm.research.tours.ToursPlugin;

public class TourSerializer 
{
	public static final String TOUR_ROOT   = "tour";
	public static final String TITLE       = "title";
	public static final String DESCRIPTION = "description";
	public static final String AUTHOR      = "author";
	public static final String ELEMENTS    = "tour-elements";
	public static final String ELEMENT     = "element";
	public static final String TYPE        = "type";
	
	public static final String TIME_LIMIT_ELEMENT  = "time-limit";
	public static final String MINUTES_ELEMENT     = "minutes";
	public static final String SECONDS_ELEMENT     = "seconds";
	private static final String TOUR_ID = "id";
	
	/**
	 * Create a tour memento based on the given tour
	 * @param tour
	 * @return
	 */
	private static XMLMemento createTourMemento(XMLTour tour)
	{
		XMLMemento memento = XMLMemento.createWriteRoot(TOUR_ROOT);
		memento.putString(TOUR_ID, tour.getID());
		
		/**
		 * Load the base elements
		 */
		IMemento title = memento.createChild(TITLE);
		title.putTextData(tour.getTitle());
		IMemento description = memento.createChild(DESCRIPTION);
		description.putTextData(tour.getDescription());
		IMemento author = memento.createChild(AUTHOR);
		author.putTextData(tour.getAuthor());
		
		if(tour.getTimeLimit() != null)
		{
			IMemento timeLimitMemento = memento.createChild(TIME_LIMIT_ELEMENT);
			IMemento minutesMemento = timeLimitMemento.createChild(MINUTES_ELEMENT);
			minutesMemento.putTextData(Integer.toString(tour.getTimeLimit().getMinutes()));
			IMemento secondsMemento = timeLimitMemento.createChild(SECONDS_ELEMENT);
			secondsMemento.putTextData(Integer.toString(tour.getTimeLimit().getSeconds()));
		}
		
		IMemento elementsMemento = memento.createChild(ELEMENTS);
		
		if(tour.getElementCount() > 0)
		{
			for(ITourElement element : tour.getElements())
			{
				// Create an element memento 
				IMemento elementMemento = elementsMemento.createChild(ELEMENT);
				String className = element.getClass().getName();
				ClassLoader elementClassLoader = element.getClass().getClassLoader();
				if (elementClassLoader instanceof BundleReference) {
					Bundle bundle = ((BundleReference)elementClassLoader).getBundle();
					String bundleName = bundle.getSymbolicName();
					className = bundleName + ":" + className;
				}
				// Write the type
				elementMemento.putString(TYPE, className);
				// Let the element do its thing
				element.save(elementMemento);
			}
		}
		
		return memento;
	}

	private static void writeMemento(IFile file, XMLMemento memento) throws IOException, CoreException
	{
		if(file !=null)
		{
			// Write to a string
			StringWriter writer = new StringWriter();
			memento.save(writer);
			// Create the input stream
			ByteArrayInputStream bais = new ByteArrayInputStream(writer.getBuffer().toString().getBytes());
			file.setContents(bais, true, false, null);
			writer.close();
		}
	}

	// TODO progress monitor and error handling
	public static void write(IFile file, XMLTour tour) throws Exception
	{
		XMLMemento memento = createTourMemento(tour);
		writeMemento(file,memento);

	}
	
	// TODO progress monitor and error handling
	public static void read(IFile file, XMLTour tour) throws CoreException 
	{
			InputStream input = file.getContents();
			IMemento memento;
			memento = XMLMemento.createReadRoot(new InputStreamReader(input));
			// Populate the tour from the memento
			readMemento(memento,tour);
	}

	private static void readMemento(IMemento memento, XMLTour tour) 
	{
		tour.setID(memento.getString(TOUR_ID)); // overrides default ID
		// Load the base elements
		IMemento titleMemento = memento.getChild(TITLE);
		String title = titleMemento.getTextData();
		if(title != null)
			tour.setTitle(title);
		
		IMemento descriptionMemento = memento.getChild(DESCRIPTION);
		String description = descriptionMemento.getTextData();
		if(description != null)
			tour.setDescription(description);
		
		IMemento authorMemento = memento.getChild(AUTHOR);
		String author = authorMemento.getTextData();
		if(author != null)
			tour.setAuthor(author);
		
		IMemento timeLimitMemento = memento.getChild(TIME_LIMIT_ELEMENT);

		if(timeLimitMemento!=null)
		{
			IMemento minutesMemento = timeLimitMemento.getChild(MINUTES_ELEMENT);
			IMemento secondsMemento = timeLimitMemento.getChild(SECONDS_ELEMENT);

			int minutes = -1;
			int seconds = -1;

			if(minutesMemento!=null)
			{
				String minuteString = minutesMemento.getTextData();

				if(minuteString !=null)
				{
					try 
					{
						minutes = Integer.parseInt(minuteString);
					} 
					catch (NumberFormatException e) 
					{
						e.printStackTrace();
					}
				}
			}

			if(secondsMemento!=null)
			{
				String secondString = secondsMemento.getTextData();

				if(secondString !=null)
				{
					try 
					{
						seconds = Integer.parseInt(secondString);
					} 
					catch (NumberFormatException e) 
					{
						e.printStackTrace();
					}
				}
			}
			
			if(minutes>0 || seconds > 0)
			{
				ITimeLimit limit = new TimeLimit();
				
				if(minutes > 0)
					limit.setMinutes(minutes);
				
				if(seconds > 0)
					limit.setSeconds(minutes);
				
				tour.setTimeLimit(limit);
			}
		}

		
		IMemento elementsMemento = memento.getChild(ELEMENTS);
		
		if(elementsMemento != null)
		{
			IMemento[] elementMementos = elementsMemento.getChildren(ELEMENT);
			Vector<ITourElement> newElements = new Vector<ITourElement>();
			
			for(IMemento elementMemento : elementMementos)
			{
				String type = elementMemento.getString(TYPE);
				ITourElement element = null;
				
				if(type != null)
				{
					try 
					{
						Bundle bundle = ToursPlugin.getDefault().getBundle();
						int bundleSeparator = type.indexOf(':');
						if (bundleSeparator > 0) {
							String bundleName = type.substring(0, bundleSeparator);
							type = type.substring(bundleSeparator + 1);

							Bundle typeBundle = Platform.getBundle(bundleName);

							if (typeBundle != null) {

								bundle = typeBundle;
							}
						}
						
						Class<?> clazz = bundle.loadClass(type);						
						element = (ITourElement)clazz.newInstance();
					} 
					catch (ClassNotFoundException e) 
					{
						e.printStackTrace();
						continue;
					} 
					catch (InstantiationException e) 
					{
						e.printStackTrace();
						continue;
					} 
					catch (IllegalAccessException e) 
					{
						e.printStackTrace();
						continue;
					}
				
					if(element != null)
					{
						// Load and add the element
						element.load(elementMemento);
						newElements.add(element);
					}
				}
			}
			
			if(newElements != null)
				tour.addElements(newElements.toArray(new ITourElement[0]));
		}
	}
}
