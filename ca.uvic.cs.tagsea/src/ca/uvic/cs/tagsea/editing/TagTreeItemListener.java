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
package ca.uvic.cs.tagsea.editing;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.MessageBox;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.core.Waypoint;
import ca.uvic.cs.tagsea.core.resource.ResourceUtil;
import ca.uvic.cs.tagsea.editing.events.ItemDragEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemCopyEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemDeleteEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemListener;
import ca.uvic.cs.tagsea.editing.events.TreeItemMoveEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemRenameEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEARefactorEvent;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;
import ca.uvic.cs.tagsea.ui.views.TagsView;
import ca.uvic.cs.tagsea.util.DescendingWaypointComparator;


/**
 * This class gets notified when a UI refactor event occurs: move,
 * copy, rename, or delete.  When an event occurs this class will update 
 * the underlying tags model.dra
 * 
 * @author Chris Callendar
 */
public class TagTreeItemListener implements TreeItemListener {

	private enum RefactorAction {
		Delete,
		Rename,
		Move,
	}
	
	private TagsView view;
	public TagTreeItemListener(TagsView tagsView)
	{
		this.view = view;
	}
	
	/**
	 * Does nothing.
	 */
	public void beforeMove(TreeItemMoveEvent event) {
		if (event.data instanceof Tag) {
			//Tag tag = (Tag) event.data;
		}
	}
	
	/**
	 * If any keyed data gets stored in the tag TreeItems, then
	 * here is where you copy it to the new item. 
	 */
	public void copyData(TreeItemCopyEvent event) {
		/* e.g.
		TreeItem oldItem = event.item;
		TreeItem newItem = event.newItem;
		newItem.setData("KEY", oldItem.getData("KEY"))
		*/
	}
	
	/** Does nothing. */
	public void afterCopy(TreeItemCopyEvent event) {
	}

	public void afterMove(TreeItemMoveEvent event) {
		if (event.data instanceof Tag) {
			Tag tag = (Tag) event.data;
			Tag parentTag = null;
			if (event.parent != null && (event.parent.getData() instanceof Tag)) {
				parentTag = (Tag) event.parent.getData();
			}
			moveTag(tag, parentTag);
		}
	}

	/** Does nothing. */
	public void beforeRename(TreeItemRenameEvent event) {
		// don't need to do anything - no rename has happened yet
	}

	public void afterRename(TreeItemRenameEvent event) 
	{
		if (event.data instanceof Tag) 
		{
			renameTag((Tag)event.data, event.newName);
		}
	}

	public void beforeDelete(TreeItemDeleteEvent event) {
		if (event.data instanceof Tag) {
			Tag tag = (Tag) event.data;
			Waypoint[] wps = tag.getAllWaypoints();
			int wpCount = wps.length;
			// if multiple waypoints are being deleted, prompt the user
			if (wpCount > 1) {
				MessageBox delParent = new MessageBox(event.tree.getShell(), SWT.YES | SWT.NO);
				delParent.setMessage("Deleting " +  wpCount + " waypoints.  Do you want to continue?");
				delParent.setText("Delete Waypoints?");
				int dialogReturn = delParent.open();
				if (dialogReturn == SWT.NO) {
					// cancel the deletion
					event.doit = false;
					return;
				}
			}			
		}
	}

	public void afterDelete(TreeItemDeleteEvent event) {
		if (event.data instanceof Tag) {
			deleteTag((Tag)event.data);
		} else if (event.data instanceof Waypoint) {
			deleteTag(((Waypoint)event.data).getTag());
		}
	}
	
	/** Does nothing. */
	public void dragStart(ItemDragEvent event) {}
	/** Does nothing. */
	public void dragOver(ItemDragEvent event) {}
	/** Does nothing. */
	public void drop(ItemDragEvent event) {}

	/** Does nothing */
	public void finishedMove(Item[] movedData) {
	}
	
	/** Does nothing */
	public void finishedDelete() {
	}
	
	/**
	 * Moves the given tag to the new parent (can be null).
	 * @param tag the tag to move
	 * @param newParent the new parent for the tag - can be null to make tag a root
	 */
	public void moveTag(Tag tag, Tag newParent) {
		if (tag.getParent() != newParent) {
			refactor(RefactorAction.Move, tag, null, newParent);
		}
	}
	
	/**
	 * Deletes the given tag.
	 * @param tag the tag to delete
	 */
	public void deleteTag(Tag tag) {
		refactor(RefactorAction.Delete, tag, null, null);
	}
	
	/**
	 * Renames the given tag.
	 * @param tag the tag to rename
	 * @param newName the new name - can't be null
	 */
	public void renameTag(Tag tag, String newName) {
		if ((newName != null) && !newName.equals(tag.getName())) {
			refactor(RefactorAction.Rename, tag, newName, null);
		}
	}
	
	/**
	 * Does the refactoring - deletes, renames, or moves a tag.
	 * @param action the action to perform - delete, rename or move
	 * @param tag the tag being acted upon
	 * @param newName the new name (rename action only, null otherwise)
	 * @param newParent the new parent (move action only, null otherwise).  Can be null when moving.
	 * If the newParent is null, then the tag will have a parent Tag with a blank name.
	 */
	private void refactor(RefactorAction action, Tag tag, String newName, Tag newParent) {
		Waypoint[] allWaypoints = tag.getAllWaypoints();
		if (allWaypoints.length > 1) {
			// sort the waypoints based on resource and line number - descending order
			// this must be done so that the marker's positions don't get out of whack
			Arrays.sort(allWaypoints, new DescendingWaypointComparator());
		}
		
		// now go through each file and change the tag name
		final TagSEAPlugin plugin = TagSEAPlugin.getDefault();

		// first save any changes to the editors
		if (plugin != null) {	// this check is only for testing purposes
			
			
		}
		
		// store the resource and offset keys so that waypoints on the same line aren't done twice
		HashSet<String> completedResources = new HashSet<String>();
		IFile resource = null;
		for (Waypoint wp : allWaypoints) {
			Tag wpTag = wp.getTag();
			IMarker marker = wp.getMarker();
			if ((marker != null) && marker.exists()) {
				resource = (IFile)marker.getResource();
				IDocument doc = ResourceUtil.getDocument(resource);
				int offset = marker.getAttribute(IMarker.CHAR_START, 0);
				int length = marker.getAttribute(IMarker.CHAR_END, offset) - offset;
				
				// check if this resource and offset (same line) has already been refactored
				String key = resource.getFullPath().toString() + "_" + offset;
				if (!completedResources.contains(key)) {
					boolean okay = false;
					try {
						Object oldValue;
						switch (action) {
							case Delete :
								oldValue = tag.getParent();
								okay = TagRefactoring.deleteTagInDocument(tag, doc, offset, length);
								if (wpTag.getParent() != null) {
									wpTag.getParent().removeWaypoint(wp);
								}
								marker.delete();
								if (okay) {
									Monitoring.getDefault().fireEvent(new TagSEARefactorEvent(TagSEARefactorEvent.Refactoring.Delete, tag, oldValue));
								}
								break;
							case Rename :
								oldValue = tag.getName();
								okay =TagRefactoring.renameTagInDocument(tag, newName, doc, offset, length);
								
								if (okay) {
									Monitoring.getDefault().fireEvent(new TagSEARefactorEvent(TagSEARefactorEvent.Refactoring.Rename, tag, oldValue));
								}
								break;
							case Move :
								oldValue = tag.getParent();
								// note we want to use the tag variable, not the wpTag because they could be different
								// what has moved is tag, wpTag could be a child of tag.
								okay = TagRefactoring.moveTagInDocument(tag, newParent, doc, offset, length);
								if (okay) {
									Monitoring.getDefault().fireEvent(new TagSEARefactorEvent(TagSEARefactorEvent.Refactoring.Move, tag, oldValue));
								}
								break;
						}
						completedResources.add(key);
					} catch (Exception e) {
						TagSEAPlugin.log("Error refactoring (" + action.name() + ")", e);
						e.printStackTrace();
					}
				} else {
					//System.out.println("Already done " + key);
				}
				
			}
		}
		completedResources.clear();
		
		// now update the Tags model
		switch (action) {
			case Delete :
				// remove the tag from its parent
				Tag parent = tag.getParent();
				if (parent != null) {
					parent.removeChild(tag);
					tag.setParent(null);
				}
				break;
			case Rename :
				// update the tag name, ids (children too), and waypoint keywords
				tag.rename(newName);
				break;
			case Move :
				// update the tags parent
				if (tag.getParent() != null) {
					tag.getParent().removeChild(tag);
				}
				if (newParent == null) {
					if (plugin != null) {	// for testing purposes
						tag = plugin.getTagCollection().addRootTag(tag);
					} else {
						tag.setParent(new Tag());
					}
				} else {
					tag = newParent.addChild(tag);
				}				
				break;
		}
		
		// now save all our changes
		if (plugin != null && resource != null) {
			//@tag bug(1524158) : must save in the actual file, not in the editor. 
			ResourceUtil.commit(resource);
			//plugin.getWorkbench().saveAllEditors(false);
		}	
		
		// and refresh the views
//		if(fTagsView!=null)
//		{
//			fTagsView.getTagsComposite().refreshTagsViewer(null);
//			fTagsView.getWaypointsComposite().getWaypointsTableViewer().refresh(true);
//		}
	}

	
}
