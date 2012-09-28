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
package ca.uvic.cs.tagsea.ui.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Tag;

/**
 * @author mdesmond
 */
public class TagsViewStateManager 
{
	public final static String MEMENTO_XML_FILE = ".metadata/.plugins/ca.uvic.cs.tagsea/tagsViewMemento.xml";
	
	private static XMLMemento fMemento;
	
	public static void recordState(TagsComposite tagsComposite) 
	{		
		fMemento = XMLMemento.createWriteRoot("tagsViewMemento");
		saveStateToMemento(tagsComposite);
		saveMemento();
	}

	/**
	 * Save the routes to an XML file (This is the first step,
	 * where we save the routes to a memento)
	 * @param memento
	 */
	private static void saveStateToMemento(TagsComposite tagsComposite) 
	{	
		IMemento selected = fMemento.createChild("selectedTags");
		String[] tagIds = tagsComposite.getSelectedList();
		
		for (String tagId : tagIds)
		{
			IMemento t = selected.createChild("tag");
			t.putString("tag-id", tagId);
		}
		
		IMemento expanded = fMemento.createChild("expandedTags");
		Object[] expandedElements = tagsComposite.getTagsTreeViewer().getExpandedElements();
		
		for(Object o : expandedElements)
		{
			Tag tag = (Tag)o;
			IMemento t = expanded.createChild("tag");
			t.putString("tag-id", tag.getId());
		}
	}

	/**
	 * Save the routes to an XML file (This is the second step,
	 * where we save the memento to a file)
	 * @param fileName
	 * @return success
	 */
	private static boolean saveMemento() 
	{
		IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = workSpaceRoot.getLocation().append(MEMENTO_XML_FILE);		
		File mementoFile = path.toFile();
		mementoFile.getParentFile().mkdirs();
		
		if (mementoFile == null)
			return false;
		
		try
		{		
			FileOutputStream stream = new FileOutputStream(mementoFile);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
			fMemento.save(writer);
			writer.close();
		}
		catch (IOException e) 
		{
			TagSEAPlugin.log("Error: writing to routes file failed in RouteXMLUtil.saveRoutes()");
			return false;
		}
		// Success !
		return true;
	}
	
	public static void restoreState(TagsComposite tagsComposite)
	{	
		IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = workSpaceRoot.getLocation().append(MEMENTO_XML_FILE);		
		File mementoFile = path.toFile();
		
		if(mementoFile.exists())
		{
			try 
			{
				FileInputStream input = new FileInputStream(mementoFile);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input, "utf-8")); //$NON-NLS-1$
				
				IMemento memento = XMLMemento.createReadRoot(reader);			
				
				restoreStateFromMemento(tagsComposite,memento);
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

	private static void restoreStateFromMemento(TagsComposite tagsComposite, IMemento memento) 
	{	
		IMemento selectedTagsMemento = memento.getChild("selectedTags");
		IMemento expandedTagsMemento = memento.getChild("expandedTags");
		
		if (expandedTagsMemento != null) 
		{
			IMemento[] tagMementos = expandedTagsMemento.getChildren("tag");
			List<String> tagIds = new ArrayList<String>();
			
			for (IMemento tagMemento : tagMementos) 
			{
				String tagId = tagMemento.getString("tag-id");
				
				if(tagId!=null)
					tagIds.add(tagId);
			}
			
			if(tagIds.size() > 0)
			{
				String[] array = new String[tagIds.size()];
				array = tagIds.toArray(array);
				Tag[] expandedTags = TagSEAPlugin.getDefault().getTagCollection().getTags(array);
				
				if(expandedTags.length > 0)
					tagsComposite.getTagsTreeViewer().setExpandedElements(expandedTags);
			}		
		}
		
		if (selectedTagsMemento != null) 
		{
			IMemento[] tagMementos = selectedTagsMemento.getChildren("tag");
			List<String> tagIds = new ArrayList<String>();
			
			for (IMemento tagMemento : tagMementos) 
			{
				String tagId = tagMemento.getString("tag-id");
				
				if(tagId!=null)
					tagIds.add(tagId);
			}
			
			if(tagIds.size() > 0)
			{
				String[] array = new String[tagIds.size()];
				array = tagIds.toArray(array);
				Tag[] selectedTags = TagSEAPlugin.getDefault().getTagCollection().getTags(array);
				if(selectedTags.length > 0)
					tagsComposite.getTagsTreeViewer().setSelection(new StructuredSelection(selectedTags));
			}		
		}
	}
}
