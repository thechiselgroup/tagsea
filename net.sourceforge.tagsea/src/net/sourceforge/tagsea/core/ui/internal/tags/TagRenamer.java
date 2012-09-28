package net.sourceforge.tagsea.core.ui.internal.tags;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.TagSEAOperation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ICellModifier;

/**
 * Simple cell modifier to change tag names in the tree viewer.
 * @author Del Myers
 */

public class TagRenamer implements ICellModifier {
	public boolean canModify(Object element, String property) {
		if (!(element instanceof TagTreeItem)) return false;
		TagTreeItem item = (TagTreeItem) element;
		if ("default".equals(item.getName())) return false;
		return ("name".equals(property));
	}

	public Object getValue(Object element, String property) {
		if ((element instanceof TagTreeItem)) {
			return ((TagTreeItem)element).getText();
		}
		return "";
	}

	public void modify(Object element, String property, Object value) {
		if (!"name".equals(property)) return;
		TagTreeItem item = (TagTreeItem) element;
		if (item.getText().equals(value)) return;
		if (!item.isFlat()) {
			modifyInTree(item, value.toString());
		} else {
			modifyInTable(item, value.toString());
		}
	}

	/**
	 * Modifies the item inside a table-like structure, preserving
	 * the tree-naming if requested in the preferences (updates prefixes).
	 * @param item the item to change.
	 * @param string the string to change to.
	 */
	private void modifyInTable(final TagTreeItem item, final String newValue) {
		TagSEAPlugin.syncRun(new TagSEAOperation("Renaming Tags..."){
			@Override
			public IStatus run(IProgressMonitor monitor) throws  InvocationTargetException {
				ITag tag = item.getTag();
				tag.setName(newValue);
				return Status.OK_STATUS;
			}
		}, new NullProgressMonitor());
	}

	/**
	 * Modifies this item in a tree structure.
	 * @param item
	 * @param string
	 */
	private void modifyInTree(final TagTreeItem item, final String newValue) {
		TagSEAPlugin.syncRun(new TagSEAOperation("Renaming Tags..."){
			@Override
			public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
				int dot = item.getName().lastIndexOf('.');
				int end = item.getText().length() + dot + 1;
				List<ITag> childTags = gatherChildrenOfItem(item);
				for (ITag tag : childTags) {
					StringBuilder nameBuilder = new StringBuilder(tag.getName());
					nameBuilder.replace(dot+1, end, newValue);
					tag.setName(nameBuilder.toString());
				}
				return Status.OK_STATUS;
			}
		}, new NullProgressMonitor());		
	}
	
	/*package*/ List<ITag> gatherChildrenOfItem(TagTreeItem item) {
		List<ITag> tags = new LinkedList<ITag>();
		ITag tag = item.getTag();
		if (tag != null) {
			tags.add(tag);
		}
		if (item.hasChildren()) {
			for (TagTreeItem child : item.getChildren()) {
				tags.addAll(gatherChildrenOfItem(child));
			}
		}
		return tags;
	}
}