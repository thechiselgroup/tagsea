/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui.internal.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItem;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Removes a selected set of tags from the tag model by removing them
 * on their waypoints.
 * @author Del Myers
 */

public class TagDeleteAction extends TagContextAction {

	
	//private Collection<ITag> tags;

	/**
	 * 
	 */
	public TagDeleteAction() {
	//.tags = new TreeSet<ITag>();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		final ISelection selection = getContext().getSelection();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(false, true, new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					if (selection instanceof IStructuredSelection) {
						List<?> selectionList = ((IStructuredSelection)selection).toList();
						monitor.beginTask("Deleting Tags...", selectionList.size()+10);
						IProgressMonitor modelMonitor = new SubProgressMonitor(monitor, selectionList.size());
						IProgressMonitor updateMonitor = new SubProgressMonitor(monitor, 10);
						IStatus status = TagSEAPlugin.syncRun(getOperation(selectionList), modelMonitor);
						if (!status.isOK()) {
							TagSEAPlugin.getDefault().getLog().log(status);
						}
						updateMonitor.worked(10);
						monitor.done();
					}
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private TagSEAOperation getOperation(final List<?> selectionList) {
		return new TagSEAOperation("Deleting Tags...") {
			@Override
			public IStatus run(IProgressMonitor monitor) throws  InvocationTargetException {
				MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
				monitor.beginTask("Updating tags model...", selectionList.size());
				for (Object o : selectionList) {
					while(Display.getCurrent().readAndDispatch());
					if (monitor.isCanceled()) {
						break;
					}
					if (o instanceof TagTreeItem) {
						TagTreeItem item = (TagTreeItem) o;
						item.getName();
						if (item.isFlat()) {
							status.merge(deleteFlat(item));
						} else {
							status.merge(delteChildren(item));
						}
					}
					monitor.worked(1);
				}
				while(Display.getCurrent().readAndDispatch());
				return status;
			}

			private IStatus delteChildren(TagTreeItem item) {
				MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
				int dot = item.getName().lastIndexOf('.');
				List<ITag> children = item.getChildTags();
				ITag parent = item.getTag();
				if (parent != null)
					children.add(parent);
				for (ITag child : children) {
					if (dot == -1) {
						for (IWaypoint wp: child.getWaypoints()) {
							status.merge(wp.removeTag(child).getStatus());
						}
					} else {
						String newName = item.getName().substring(0, dot);
						if (!child.setName(newName)) {
							ITag newTag = TagSEAPlugin.getTagsModel().getTag(newName);
							if (newTag != null) {
								for (IWaypoint wp: child.getWaypoints()) {
									TagSEAChangeStatus s = wp.removeTag(child);
									if (s.changePerformed) {
										wp.addTag(newName);
									} else {
										status.merge(s.getStatus());
									}
								}
							}
						}
					}
				}
				return status;
			}

			private IStatus deleteFlat(TagTreeItem item) {
				ITag tag = item.getTag();
				MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
				if (tag != null) {
					for (IWaypoint wp: tag.getWaypoints()) {
						status.merge(wp.removeTag(tag).getStatus());
					}
				}
				return status;
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.tags.ContextAction#setContext(org.eclipse.ui.actions.ActionContext)
	 */
//	@Override
//	public void setContext(ActionContext context) {
//		super.setContext(context);
//		if (context == null) return;
//		this.tags = getAllTags();
//		setEnabled(true);
//		if (this.tags.size() == 1) {
//			//check to see if it is the default tag that is selected
//			ITag tag = this.tags.iterator().next();
//			if (ITag.DEFAULT.equals(tag.getName())) {
//				setEnabled(false);
//			}
//		}
//	}
}
