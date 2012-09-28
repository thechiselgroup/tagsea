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
package com.ibm.research.tagging.core.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.ITagModel;
import com.ibm.research.tagging.core.TagCorePlugin;

/**
 * @author mdesmond
 */
public class TagSerializer 
{	
	private static final String TAGS_FILE = "tags.repository.xml";
	private static final String DOC_ROOT = "tag-repository";
	private static final String TAG = "tag";
	private static final String NAME_ATTRIBUTE = "name";

	public void serialize(ITagModel collection)
	{
		XMLMemento memento = XMLMemento.createWriteRoot(DOC_ROOT);
		writeMemento(collection, memento);
		writeMementoToDisk(memento);
	}

	private void writeMemento(ITagModel collection, IMemento memento)
	{
		ITag[] tags = collection.getTags();

		for(ITag tag : tags)
		{
			IMemento tagMemento = memento.createChild(TAG);
			tagMemento.putString(NAME_ATTRIBUTE, tag.getName());	
		}
	}

	private void writeMementoToDisk(XMLMemento memento) 
	{
		IPath path = TagCorePlugin.getDefault().getStateLocation();	
		path = path.append(TAGS_FILE);
		File mementoFile = path.toFile();
		mementoFile.getParentFile().mkdirs();

		try
		{		
			FileOutputStream stream = new FileOutputStream(mementoFile);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
			memento.save(writer);
			writer.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void deSerialize(ITagModel collection)
	{
		IPath path = TagCorePlugin.getDefault().getStateLocation();	
		path = path.append(TAGS_FILE);		
		File mementoFile = path.toFile();

		if(mementoFile.exists())
		{
			try 
			{
				FileInputStream input = new FileInputStream(mementoFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8")); //$NON-NLS-1$
				IMemento memento = XMLMemento.createReadRoot(reader);			
				restoreStateFromMemento(collection,memento);
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			} 
			catch (WorkbenchException e) 
			{
				e.printStackTrace();
			}
		}
	}

	private static void restoreStateFromMemento(ITagModel collection, IMemento memento) 
	{	
		IMemento[] tagMementos = memento.getChildren(TAG);

		for (IMemento tagMemento : tagMementos) 
		{
			String name = tagMemento.getString(NAME_ATTRIBUTE);

			if(name != null)
			{
				collection.addTag(name);
			}
		}
	}
}
