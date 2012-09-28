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
package ca.uvic.cs.tagsea.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.MarkerUtilities;

import ca.uvic.cs.tagsea.ITagseaImages;
import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.resource.ResourceUtil;
import ca.uvic.cs.tagsea.extraction.RawTagStripper;
import ca.uvic.cs.tagsea.parser.ParseNode;
import ca.uvic.cs.tagsea.parser.ParseTree;
import ca.uvic.cs.tagsea.parser.TagParser;
import ca.uvic.cs.tagsea.ui.views.TagsView;
import ca.uvic.cs.tagsea.ui.views.cloudsee.CloudSeeView;

public class TagCollection implements ITreeContentProvider, ILabelProvider {

	private List <ITagCollectionListener> fListeners;
	private boolean fLoaded = false;
	
	private Tag root;
	
	public TagCollection() {
		this.root = new Tag();
	}

	public Tag[] getRootTags() {
		return root.getChildren();
	}
	
	/**
	 * Gets the tags with the given ids
	 * @param ids An array of tag ids
	 * @return An array of all tag objects that match the list of tag ids.
	 */
	public Tag[] getTags(String[] ids) {
		LinkedList<Tag> listOfTags = new LinkedList<Tag>();
		if(ids.length > 0) {
			for ( Tag tag : getAllTags() ) {
				for(String id : ids) {
					if ( tag.getId().equals(id) ) {
						listOfTags.add( tag );
					}
				}
			}
		}
		return listOfTags.toArray(new Tag[listOfTags.size()]);
	}
	
	/**
	 * Gets an array of all tag objects in the tag tree.
	 * @return An array of tag objects
	 */
	public Tag[] getAllTags() {
		LinkedList<Tag> listOfTags = new LinkedList<Tag>();
		populateTags(root, listOfTags);
		return (Tag[])listOfTags.toArray( new Tag[listOfTags.size()] );	
	}
	
	/**
	 * Populates the list of tags with the children tags of root.
	 * @param parentTag The current parent tag in the recursion
	 * @param listOfTags A linked list of tag objects
	 */
	private void populateTags(Tag parentTag, LinkedList<Tag> listOfTags) {
		for (Tag tag : parentTag.getChildren()) {
			listOfTags.add(tag);
			populateTags(tag, listOfTags);
		}
	}
	
	/**
	 * Removes all the waypoints from the given resource.
	 * @param resource The resource we're removing waypoints from.
	 */
	public void removeWaypoints(IResource resource) {
		LinkedList<Waypoint> waypoints = TagSEAPlugin.getDefault().getResourceWaypointMap().remove(resource);
		for (Waypoint waypoint : waypoints) {
			waypoint.getTag().removeWaypoint(waypoint);
		}	
	}
	
	/**
	 * Gets all the waypoints as a WaypointCollection object
	 * @return A WaypointCollection object containing all waypoints in the system.
	 */
	public WaypointCollection getWaypointCollection() {
		Waypoint[] waypoints = root.getAllWaypoints();
		WaypointCollection waypointCollection = new WaypointCollection();
		for (int i = 0; i < waypoints.length; i++) {
			String wpID = waypoints[i].getKeyword() + waypoints[i].getLineNumber();
			waypointCollection.addWaypoint(wpID, waypoints[i]);
		}
		return waypointCollection;
	}
	
	/**
	 * Returns an array of all the waypoints.
	 * @return Waypoint[]
	 */
	public Waypoint[] getAllWaypoints() {
		return root.getAllWaypoints();
	}
	
	/**
	 * Uses the parser to parse the tag text and then adds the new tags to the collection.
	 * @param tagText The text extracted to be parsed.
	 * @param marker The marker created for this tag.
	 * @param javaElement The java element associated with this tag.
	 */
	public void add(String tagText, IMarker marker, IJavaElement javaElement) {
		TagParser parser = new TagParser();
		ParseTree parseTree = parser.parse(tagText);
		
		//String keyword = parseTree.getKeywords();
		WaypointMetaData waypointMetaData = new WaypointMetaData(parseTree.getMetaDataKeys(), parseTree.getMetaDataValues());
		
		addToCollection(root, parseTree.getNodeCollection(), marker, javaElement, waypointMetaData);
	}

	/**
	 * Adds a root tag.
	 * @param tagName the name of the tag to make into a root tag
	 * @return Tag the tag that was added, or an existing Tag with the same name 
	 */
	public Tag addRootTag(Tag tag) {
		if (tag == null)
			throw new NullPointerException("The tag can't be null");
		
		Tag result = root.addChild(tag);
		return result;
	}
	
	/**
	 * Recurses through the parse nodes returned by the parser and adds them to the tag collection.
	 * @param parenTag The parent tag we are adding childrent to.
	 * @param nodes The array of parsed nodes to add to the tag tree.
	 * @param marker The marker location for the waypoint.
	 * @param javaElement The java element for this waypoint.
	 * @param waypointMetaData The waypoint metadata for this tag.
	 */
	private void addToCollection(Tag parentTag, ParseNode[] nodes, IMarker marker, IJavaElement javaElement, WaypointMetaData waypointMetaData) {
		for(int i = 0; i < nodes.length; i++) {
			Tag newTag = parentTag.addChild(nodes[i].getName());
			if(nodes[i].getChildren().length > 0) {
				// recurse on children
				addToCollection(newTag, nodes[i].getChildren(), marker, javaElement, waypointMetaData);
			}
			else {
				// leaf tag, add waypoint
				Waypoint waypoint = new Waypoint(marker, newTag, javaElement, waypointMetaData);
				
				newTag.addWaypoint(waypoint);
				TagSEAPlugin.getDefault().getResourceWaypointMap().add(marker.getResource(), waypoint);
			}
		}
	}


	/**
	 * Clears all the tags.
	 */
	public void clearAll() {
		root = new Tag();
	}	

	/**
	 * Updates the tags view. 
	 */
	public void updateView() {
		try {
			IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (activeWindow == null)
				return;
			
			IWorkbenchPage activePage = activeWindow.getActivePage();
			if (activePage == null)
				return;
			
			TagsView view = (TagsView)activePage.findView(TagsView.ID);
			if (view != null) { 
				view.refreshTagsViewer();				
			}
			CloudSeeView cloudSeeView = (CloudSeeView)activePage.findView(CloudSeeView.ID);
			if (cloudSeeView != null) { 
				cloudSeeView.refresh();				
			}
		} catch (Exception ex) {
			TagSEAPlugin.log("Error - couldn't update the Tags View", ex);
		}
	}
	
	// Content provider methods
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}
	
	/**
	 * Gets the root Tag objects.
	 */
	public Object[] getElements(Object parent) {
		return getRootTags();
	}

	/**
	 * Returns null.
	 */
	public Object getParent(Object child) {
		return null;
	}

	/**
	 * Get children or an empty array if there are no children or specified parent is not a tag.
	 * @param parent the Tag
	 * @return Object[] the children Tag objects
	 */
	public Object[] getChildren(Object parent) {
		if (parent instanceof Tag) {
			Tag parentTag = (Tag) parent;
			return parentTag.getChildren();
		} else {
			return new Object[0];
		}
	}

	/**
	 * Checks if the given parent (which should be a Tag) has children.
	 * @param parent the Tag object
	 * @return boolean if the parent has children
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof Tag) {
			Tag parentTag = (Tag) parent;
			return parentTag.getChildrenCount() > 0;
		} else {
			return false;
		}
	}
	
	
	// LabelProvider methods
	
	/**
	 * Gets the text (the keyword) for a given Tag object.
	 * @param obj the Tag
	 */
	public String getText(Object obj) {
		if (obj instanceof Tag) {
			Tag tag = (Tag) obj;
			return tag.getName();
		}
		return obj.toString();
	}

	/**
	 * Gets the image for the given Tag object.
	 */
	public Image getImage(Object obj) {
		// JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_ANNOTATION);
		return TagSEAPlugin.getDefault().getTagseaImages().getImage(ITagseaImages.IMG_WAYPOINT);
	}	
	
	/** Does nothing. */
	public void addListener(ILabelProviderListener listener) {
	}
	
	/** Returns false. */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	
	/** Does nothing. */
	public void removeListener(ILabelProviderListener listener) {
	}
	
	public void save(IProgressMonitor monitor) 
	{
		// no need :)
	}
	
	public void load() 
	{
        try 
        {
            IResource root = ResourceUtil.getWorkspace().getRoot();
            IMarker[] markers = root.findMarkers(TagSEAPlugin.MARKER_ID,true,IResource.DEPTH_INFINITE);
            
            // Add the tag assiciated with each marker to the tag model
            for (IMarker marker : markers) 
            {
            	IFile file = (IFile)marker.getResource();
            	IDocument doc = ResourceUtil.getDocument(file);
            	
            	// print error
            	if(doc == null)
					continue;
            	
            	
            	int offset = MarkerUtilities.getCharStart(marker);
            	int length = MarkerUtilities.getCharEnd(marker) - offset;
            	int lineNumber =  MarkerUtilities.getLineNumber(marker) - 1;
            	
            	String tagString = null;
            	IJavaElement javaElement = null;
            	
            	try
            	{
            		tagString = doc.get(offset, length);
            		//@tag bug(1522652) : strip the tag for multiline comments
            		tagString = RawTagStripper.stripRawTag(tagString);
    				// find the java element for this marker
            		ICompilationUnit cu = (ICompilationUnit) JavaCore.create(marker.getResource());
    				javaElement = cu.getElementAt(doc.getLineOffset(lineNumber));
    				
				} 
            	catch (BadLocationException e) 
            	{
					continue;
            	}
            	
            	add(tagString, marker, javaElement);
            	
                Display.getDefault().asyncExec(new Runnable()
                {
        			public void run()
        			{
        				updateView();
        			}
        		});
            }
        } 
        catch (CoreException e) 
        {
            // shouldn't happen
    	}
        
		fLoaded = true;
		fireLoaded();
	}
	
	public synchronized void addTagCollectionListener(ITagCollectionListener listener)
	{
		getListeners().add(listener);
		fireLoaded();
	}
	
	private void fireLoaded() 
	{
		if(fLoaded)
			Display.getDefault().asyncExec(new Runnable() {

				public void run() 
				{
					for(ITagCollectionListener listener : getListeners())
						listener.tagsLoaded();
				}

			});
	}

	public synchronized void removeTagCollectionListener(ITagCollectionListener listener)
	{
		getListeners().remove(listener);
	}
	
	private List <ITagCollectionListener> getListeners()
	{
		if(fListeners == null)
			fListeners = new ArrayList <ITagCollectionListener>();
		
		return fListeners;
	}
	
}