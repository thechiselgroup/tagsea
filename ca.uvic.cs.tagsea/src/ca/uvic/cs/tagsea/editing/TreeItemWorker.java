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
package ca.uvic.cs.tagsea.editing;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeyLookupFactory;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.editing.events.ItemDragEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemCopyEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemDeleteEvent;
import ca.uvic.cs.tagsea.editing.events.TreeItemListener;
import ca.uvic.cs.tagsea.editing.events.TreeItemMoveEvent;

/**
 * Sets up drag and dropping of nodes within an SWT Tree.
 * Also handles renaming and deleting TreeItems.
 * 
 * TODO should this class be changed to work with TreeViewers instead of Trees?
 * 
 * @author Chris Callendar
 */
public class TreeItemWorker {

	private TreeViewer treeViewer = null;
	private Tree tree;
	private static Item[] draggedItems = new Item[0];
	
	private int dropDetail = DND.DROP_MOVE;
	
	private InlineTreeItemRenamer renamer;

	private ArrayList<TreeItemListener> listeners;
	
	private boolean childOrderImportant = true;

	/**
	 * Initializes the worker to handle drag and drop, inline rename, and deletion.
	 * Sets up the drag source and the drag target as the tree.
	 * This constructor takes a tree viewer which is used to update selections.
	 * @param treeViewer the tree viewer - its tree will be monitored for drags, renames, and deletions
	 * @param childOrderImportant if the index of a Tree or a TreeItem's children matters.  
	 * If this is false then moving a TreeItem to the same parent (different position) won't do anything.
	 */
	public TreeItemWorker(TreeViewer treeViewer, boolean childOrderImportant) {
		this(treeViewer.getTree(), childOrderImportant);
		this.treeViewer = treeViewer;
	}
	
	/**
	 * Initializes the drag operation.
	 * Sets up the drag source and the drag target as the tree.
	 * @param tree the tree which will be monitored for drags, renames, and deletions
	 * @param childOrderImportant if the index of a Tree or a TreeItem's children matters.  
	 * If this is false then moving a TreeItem to the same parent (different position) won't do anything.
	 */
	public TreeItemWorker(Tree tree, boolean childOrderImportant) {
		this.tree = tree;
		this.listeners = new ArrayList<TreeItemListener>();
		this.renamer = new InlineTreeItemRenamer(tree);
		this.childOrderImportant = childOrderImportant;
		createDragSource();
		createDragTarget();
		addKeyListener();
	}	
	
	/** Adds a listener to the list. */
	public void addListener(TreeItemListener listener) {
		listeners.add(listener);
		renamer.addListener(listener);
	}

	/** Removes a listener from the list. */
	public void removeListener(TreeItemListener listener) {
		listeners.remove(listener);
		renamer.addListener(listener);
	}
	
	public boolean isChildOrderImportant() {
		return childOrderImportant;
	}
	
	/**
	 * Sets the validator which determines if a rename is valid.
	 * @param validator
	 */
	public void setRenameValidator(IInputValidator validator) {
		renamer.setValidator(validator);
	}
	
	/**
	 * Selects the given item and shows the inline tree editor.
	 * @param item the item to rename
	 */
	public void renameTreeItem(TreeItem item) {
		boolean doit = RefactorHelp.requestSave(new Tag[] {(Tag)item.getData()});
		if (!doit) return;
		renamer.renameTreeItem(item);
	}
	
	/**
	 * Creates a new TreeItem from the existing item.
	 * @param item the item to copy
	 * @param parent the parent (can be null)
	 * @param index the index to insert into the parent
	 * @return TreeItem
	 */
	public TreeItem copyItem(Item item, TreeItem parent, int index) {
		if (item.getData() instanceof Tag) {
			boolean doit = RefactorHelp.requestSave(new Tag[] {(Tag)item.getData()});
			if (!doit) return null;
		}
		
		TreeItem newItem = copy(item, parent, index);
		tree.showItem(newItem);
		TreeItemCopyEvent copyEvent = new TreeItemCopyEvent(item, newItem);
		fireAfterCopyEvent(copyEvent);
		return newItem;
	}
	
	/**
	 * Deletes the given tree item.  An event is fired before and after the deletion, and then the 
	 * finishedDelete() method is called.  This way the listeners have the choice of cancelling 
	 * the event. The tree item can be disposed afterwards.
	 * @param item the item to delete
	 * @param dispose if the tree items should be disposed
	 */
	public void deleteTreeItem(TreeItem item, boolean dispose) {
		deleteTreeItems(new TreeItem[] { item }, dispose);
	}
	
	/**
	 * Deletes the given tree items.  An event is fired before and after each deletion, and after
	 * all have been deleted then finishedDelete() method is called.  This way the listeners have 
	 * the choice of cancelling the event. The tree item can be disposed afterwards.
	 * @param items the items to delete
	 * @param dispose if the tree items should be disposed
	 */
	public void deleteTreeItems(TreeItem[] items, boolean dispose) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		for (TreeItem item : items) {
			if (item.getData() instanceof Tag)
				tags.add((Tag)item.getData());
		}
		Tag[] tagsArray = new Tag[tags.size()];
		tags.toArray(tagsArray);
		if (tagsArray.length > 0) {
			boolean doit = RefactorHelp.requestSave(tagsArray);
			if (!doit) return;
		}
		
		for (TreeItem item : items) {
			deleteTreeItemInternal(item, dispose);
		}
		setSelection(new TreeItem[0]);
		fireFinishedDelete();
	}
	
	/**
	 * Doesn't fire the finishedDelete() event or set the selection.
	 * @param item the item to delete
	 * @param dispose
	 */
	private void deleteTreeItemInternal(TreeItem item, boolean dispose) {
		if ((item != null) && !item.isDisposed()) {
			TreeItemDeleteEvent event = new TreeItemDeleteEvent(item);
			fireBeforeDeleteEvent(event);
			if (event.doit) {
				if (dispose) {
					/*
					if (item.getImage() != null) {
						item.getImage().dispose();
					}
					*/					
					item.dispose();
				}
				fireAfterDeleteEvent(event);
			}
		}
	}

	/**
	 * Moves a tree item to be a root item (directly under the Tree)
	 * It the item is already a root item it is returned.
	 * @param item		the item to move (not disposed)
	 * @param disposeItem if the old item should be disposed after the move (if a new item is created)
	 * @return TreeItem the new item
	 */
	public TreeItem moveTreeItemToRoot(TreeItem item, boolean disposeItem) {
		TreeItem newItem = item;
		if (item != null && !item.isDisposed()) {
			if (item.getParentItem() != null) {
				newItem = moveTreeItem(null, tree.getItemCount(), item, disposeItem);
			} else {
				//System.out.println("Already a root node");
			}
		}
		return newItem;
	}
	
	/**
	 * Moves a tree item to the end of the list of items of the parent.
	 * If the parent is null then it is added to the Tree (root).
	 * @param parent	The parent tree item, or null to use the Tree as the parent
	 * @param item		The old item which is being moved
	 * @param disposeItem if the old item should be disposed after the move (a new item is created)
	 * @return TreeItem the new item
	 */
	public TreeItem moveTreeItem(TreeItem parent, TreeItem item, boolean disposeItem) {
		TreeItem newItem = item;
		if (item != null) {
			if (parent != null) {
				newItem = moveTreeItem(parent, parent.getItemCount(), item, disposeItem);
			} else {
				newItem = moveTreeItem(null, tree.getItemCount(), item, disposeItem);
			}		
		}
		return newItem;
	}
		
	/**
	 * Moves a tree item.  Its parent will be the given parent TreeItem if not null, 
	 * otherwise its parent will be the Tree.
	 * The text, image, and data are copied into the new item from the old item.
	 * The children of the old item are also created.
	 * @param parent	The parent tree item, or null to use the Tree as the parent
	 * @param index		The position to insert
	 * @param item		The old item which is being moved
	 * @param disposeItem if the old item should be disposed after the move (a new item is created)
	 * @return TreeITem the new item
	 */
	public TreeItem moveTreeItem(TreeItem parent, int index, TreeItem item, boolean disposeItem) {
		if ((item == null) || item.isDisposed()) 
			return item;
		
		// check if we need to move
		TreeItem oldParent = item.getParentItem();
		
		// parent is a child of the item (or parent is the item)?
		// The above drag'n'drop code shouldn't allow this, but anyone can call this method
		TreeItem check = parent;
		while (check != null) {
			if (check == item) {
				//System.out.println("Parent is a child of the item");
				return item;
			}
			check = check.getParentItem();
		}
		
		// check for same parent, then if the order matters check the position/index
		if (/*(oldParent != null) && (parent != null) &&*/ (oldParent == parent)) {
			if (!childOrderImportant) {
				// same parent - no need to move
				return item;
			} else {
				// check if position has changed
				int pos = (item.getParentItem() == null ? tree.indexOf(item) : item.getParentItem().indexOf(item));
				if ((index == pos) || (index == (pos + 1))) {
					return item;
				}
			}
		}
		// already a root node?
		if ((oldParent == null) && (parent == null)) {
			//System.out.println("No need to move - already a root");
			return item;
		}
		
		// ok, proceed with the move (really a create/delete)
		
		// create the move event for the old item
		TreeItemMoveEvent event = new TreeItemMoveEvent(item);
		if (event.data instanceof Tag) {
			Tag tag = (Tag) event.data;
			event.doit = RefactorHelp.requestSave(new Tag[] {tag});
		}
		
		if (!event.doit) return item;
		fireBeforeMovedEvent(event);
		
		// if the event was cancelled then we don't create a new TreeItem
		if (!event.doit) 
			return item;
		
		// okay the event wasn't cancelled, so create the new item
		TreeItem newItem = copy(item, parent, index);

		// create the move event for the new item
		event = new TreeItemMoveEvent(newItem);
		
		// recursively add the children
		addChildren(newItem, item.getItems());

		// now ensure this new item is visible and maybe expanded
		newItem.setExpanded(item.getExpanded());
		tree.showItem(newItem);
		setSelection(new TreeItem[] { newItem });
		
		// notify listeners of move event
		fireAfterMovedEvent(event);
		
		if (disposeItem) {
			item.dispose();
		}
		
		return newItem;
	}
	
	/**
	 * Creates a new TreeItem from the given item, copying over the text,
	 * image, and data.  Then a TreeItemCopyEvent is fired giving any listeners
	 * the opportunity to copy over keyed data.
	 * @param item the item to copy
	 * @param parent the parent for the new item
	 * @param index the index for the new item in the parent's list of children
	 * @return TreeItem the created item
	 */
	private TreeItem copy(Item item, TreeItem parent, int index) {
		TreeItem newItem;
		if (parent == null) {
			newItem = new TreeItem(tree, SWT.NONE, index);
		} else {
			newItem = new TreeItem(parent, SWT.NONE, index);
		}
		newItem.setText(item.getText());
		newItem.setImage(copyImage16(item));
		// can't copy keyed data!
		newItem.setData(item.getData());
		// send copy event to let the listener copy over any keyed data
		TreeItemCopyEvent event = new TreeItemCopyEvent(item, newItem);
		fireCopyDataEvent(event);

		return newItem;
	}
	
	/**
	 * Returns the 16x16 image from the item (if not null or disposed).
	 * TreeItem objects can only have 16x16 images.
	 * @param item the item whose image will be returned.
	 * @return Image (16x16)
	 */
	private Image copyImage16(Item item) {
		Image img = null;
		if ((item.getImage() != null) && !item.getImage().isDisposed()) {
			img = item.getImage();
			Image img16 = new Image(img.getDevice(), 16, 16);
			GC gc = new GC(img16);
			gc.drawImage(img, 0, 0, 16, 16, 0, 0, 16, 16);
			img = img16;
			gc.dispose();
		}
		return img;
	}
	
	/**
	 * Recursively adds all the children items.
	 * @param item		the new item to add children to
	 * @param children	the children to add
	 */
	private void addChildren(TreeItem item, TreeItem[] children) {
		for (int i = 0; i < children.length; i++) {
			TreeItem oldChild = children[i];
			TreeItem child = new TreeItem(item, SWT.NONE);
			child.setText(oldChild.getText());
			child.setImage(oldChild.getImage());
			child.setData(oldChild.getData());

			addChildren(child, oldChild.getItems());
			
			// have to set expanded after the children are added
			child.setExpanded(oldChild.getExpanded());
		}
	}	
	
	protected void fireBeforeMovedEvent(TreeItemMoveEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().beforeMove(event);
			if (!event.doit)
				break;
		}
	}
	
	protected void fireCopyDataEvent(TreeItemCopyEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().copyData(event);
			if (!event.doit)
				break;
		}
	}	

	protected void fireAfterCopyEvent(TreeItemCopyEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().afterCopy(event);
		}
	}
	
	protected void fireAfterMovedEvent(TreeItemMoveEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().afterMove(event);
		}
	}
		
	protected void fireBeforeDeleteEvent(TreeItemDeleteEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().beforeDelete(event);
			if (!event.doit)
				break;
		}
	}

	protected void fireAfterDeleteEvent(TreeItemDeleteEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().afterDelete(event);
		}
	}

	protected void fireDragStartEvent(ItemDragEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().dragStart(event);
		}
	}
	
	protected void fireDragOverEvent(ItemDragEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().dragOver(event);
		}
	}
	
	protected void fireDragDropEvent(ItemDragEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().drop(event);
		}
	}
	
	protected void fireFinishedMove(Item[] movedData) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().finishedMove(movedData);
		}
	}

	protected void fireFinishedDelete() {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().finishedDelete();
		}
	}
	
	/**
	 * Adds a drag source and listener to the Table.
	 * This allows you to drag TableItems onto the tree.
	 */
	public void addTableDragSource(final Table table) 
	{
		//@tag bug(1522066) : temporary fix until the model is updated.
		if (table.getData("DragSource") != null) return; //$NON-NLS$
		DragSource source = new DragSource(table, DND.DROP_COPY);
		source.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		source.addDragListener(new DragSourceListener() {
			
			/** Starts the drag - sets the draggedItem. */
			public void dragStart(DragSourceEvent event) {
				TableItem[] selection = table.getSelection();
				if (selection.length > 0) {
					draggedItems = selection;
					dropDetail = DND.DROP_COPY;
					ItemDragEvent dragEvent = new ItemDragEvent(draggedItems);
					dragEvent.dragType = dropDetail;
					fireDragStartEvent(dragEvent);
					event.doit = dragEvent.doit;
					if (!dragEvent.doit) {
						draggedItems = new Item[0];
					}
				} else {
					event.doit = false;
				}
			}
			
			public void dragSetData(DragSourceEvent event) {
				if (draggedItems.length > 0) {
					event.data = draggedItems[0].getText();
				}
			}
			
			public void dragFinished(DragSourceEvent event) {
				draggedItems = new Item[0];
			}
		});		
	}
	
	/**
	 * Creates the drag source and listener for the Tree.
	 */
	private void createDragSource() 
	{
		DragSource source = new DragSource(tree, DND.DROP_MOVE);
		source.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		source.addDragListener(new DragSourceListener() {
			
			/** Starts the drag - sets the draggedItem. */
			public void dragStart(DragSourceEvent event) 
			{
				renamer.stopEditting(); // stop any inline editing
				TreeItem[] selection = tree.getSelection();
				
				if (selection.length > 0) 
				{
					draggedItems = selection;
					dropDetail = DND.DROP_MOVE;
					ItemDragEvent dragEvent = new ItemDragEvent(draggedItems);
					dragEvent.dragType = dropDetail;
					fireDragStartEvent(dragEvent);
					event.doit = dragEvent.doit;
					
					if (!dragEvent.doit) 
					{
						draggedItems = new Item[0];
					}
				} 
				else 
				{
					event.doit = false;
				}
			}
			
			public void dragSetData(DragSourceEvent event) {
				if (draggedItems.length > 0) {
					event.data = draggedItems[0].getText();
				}
			}
			
			public void dragFinished(DragSourceEvent event) {
				draggedItems = new Item[0];
			}
		});		
	}
	
	/**
	 * Creates the drop target and the drop listener.
	 */
	private void createDragTarget() 
	{
		DropTarget target = new DropTarget(tree, DND.DROP_MOVE | DND.DROP_COPY);
		target.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		target.addDropListener(new DropTargetAdapter() {
			
			/** Sets up the drop feedback. */
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				if (event.item != null) {
					TreeItem item = (TreeItem)event.item;
					// check if the target item is a child (or the same) of the draggedItems
					TreeItem check = item;
					while (check != null) {
						for (Item draggedItem : draggedItems) {
							if (check == draggedItem) {
								event.detail = DND.DROP_NONE;
								return;
							}
						}
						check = check.getParentItem();
					}
					
					TreeItem target = item.getParentItem(); 
					Point pt = Display.getDefault().map(null, tree, event.x, event.y);
					Rectangle bounds = item.getBounds();
					int feedback;
					if (pt.y < bounds.y + bounds.height/3) {
						feedback = DND.FEEDBACK_INSERT_BEFORE;
					} else if (pt.y > bounds.y + 2*bounds.height/3) {
						feedback = DND.FEEDBACK_INSERT_AFTER;
					} else {
						target = item;
						feedback = DND.FEEDBACK_SELECT;
					}
					// fire the dragOver event
					ItemDragEvent dragEvent = new ItemDragEvent(draggedItems, target);
					dragEvent.dragType = dropDetail;
					fireDragOverEvent(dragEvent);
					if (dragEvent.doit) {
						event.detail = dropDetail;	// dropDetail set in drag start (copy/move)
						event.feedback |= feedback;
					} else {
						event.detail = DND.DROP_NONE;
					}
				} else {
					event.detail = DND.DROP_NONE;
				}
			}

			/** Handles the drop event. */
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				
				ItemDragEvent dragEvent = new ItemDragEvent(draggedItems, (TreeItem)event.item);
				dragEvent.dragType = dropDetail;
				fireDragDropEvent(dragEvent);
				if (!dragEvent.doit) {
					event.detail = DND.DROP_NONE;
					return;
				}
				
				if (event.item == null) {
					for (Item draggedItem : draggedItems) {
						if (event.detail == DND.DROP_MOVE) {
							moveTreeItem(null, (TreeItem)draggedItem, true);
						} else if (event.detail == DND.DROP_COPY) {
							copyItem(draggedItem, null, tree.getItemCount());
						}
					}
					fireFinishedMove(draggedItems);					
				} else {
					TreeItem item = (TreeItem)event.item;
					Point pt = Display.getDefault().map(null, tree, event.x, event.y);
					Rectangle bounds = item.getBounds();
					
					// check if the position is above or below the item
					boolean insertBefore = (pt.y < bounds.y + bounds.height/3);
					boolean insertAfter = (pt.y > bounds.y + 2*bounds.height/3);
					
					TreeItem parent = item.getParentItem();
					TreeItem[] items = (parent != null ? parent.getItems() : tree.getItems());
					int index = items.length;
					for (int i = 0; i < items.length; i++) {
						if (items[i] == item) {
							index = i;
							break;
						}
					}
					// update the index and parent vars
					if (insertBefore) {
					} else if (insertAfter) {
						index = index + 1;
					} else {
						parent = item;
						index = parent.getItemCount();
					}
					
					for (Item draggedItem : draggedItems) {
						if (event.detail == DND.DROP_MOVE) {
							moveTreeItem(parent, index, (TreeItem)draggedItem, true);
						} else if (event.detail == DND.DROP_COPY) {
							copyItem(draggedItem, parent, index);
						}
						index++;
					}
					fireFinishedMove(draggedItems);
				}
			}
		});
	}

	/**
	 * Adds a key listener to watch for delete events.
	 */
	private void addKeyListener() {
		final IKeyLookup lookup = KeyLookupFactory.getSWTKeyLookup();
		final int DEL = lookup.formalKeyLookup(IKeyLookup.DEL_NAME);
		final int DEL2 = lookup.formalKeyLookup(IKeyLookup.DELETE_NAME);
		
		tree.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int code = e.keyCode;
				if (code == DEL || code == DEL2) {
					TreeItem[] selection = tree.getSelection();
					deleteTreeItems(selection, true);
				}
			}
		});
	}

	/**
	 * Sets the selection on the tree.  
	 * Also updates the TreeViewer selection if it is not null.
	 * The tree viewer's selection will be the data Object from each TreeItem.
	 * @param selectedItems the tree items that are to be selected
	 */
	protected void setSelection(TreeItem[] selectedItems) {
		tree.setSelection(selectedItems);
		
		if (treeViewer != null) {
			ArrayList<Object> selectedData = new ArrayList<Object>(selectedItems.length);
			for (TreeItem item : selectedItems) {
				if ((item != null) && !item.isDisposed()) {
					Object data = item.getData();
					if (data != null) {
						selectedData.add(data);
					}
				}
			}
			treeViewer.setSelection(new StructuredSelection(selectedData));
		}
	}
	
}

