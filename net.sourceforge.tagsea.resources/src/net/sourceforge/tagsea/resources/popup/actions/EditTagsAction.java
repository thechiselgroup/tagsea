package net.sourceforge.tagsea.resources.popup.actions;


import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ui.WaypointCreateDialog;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class EditTagsAction implements IObjectActionDelegate, IActionDelegate2 {

	private IWorkbenchPart part;
	private LinkedList<IResource> resources;
	private Control control;
	int line = -1;
	private LocalMenuListener menuListener;
	private boolean menuShown;
	
	private class LocalMenuListener implements MenuListener {
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.MenuListener#menuHidden(org.eclipse.swt.events.MenuEvent)
		 */
		public void menuHidden(MenuEvent e) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.MenuListener#menuShown(org.eclipse.swt.events.MenuEvent)
		 */
		public void menuShown(MenuEvent e) {
			menuShown = true;
			updateLine();
			menuShown = false;
		}
		
	}

	/**
	 * Constructor for Action1.
	 */
	public EditTagsAction() {
		super();
	}

	private void updateLine() {
		line = -1;
		IVerticalRulerInfo rulerInfo = (IVerticalRulerInfo) part.getAdapter(IVerticalRulerInfo.class);
		
		
		if (control instanceof StyledText) {
			StyledText text = (StyledText) control;
			try {
				Point pos;
				if (menuShown) {
					pos = Display.getCurrent().getCursorLocation();
					pos  = control.toControl(pos);
				} else {
					Caret caret = text.getCaret();
					pos = caret.getLocation();
				}
				if (rulerInfo != null) {
					line = rulerInfo.toDocumentLineNumber(pos.y)+1;
				} else {
					line = text.getLineIndex(pos.y) + 1;
				}
			} catch (RuntimeException e) {
			}
		} else if (control instanceof Text) {
			Text text = (Text) control;
			line = text.getCaretLineNumber();
		}
	}
	
	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (this.part == targetPart) return;
		this.part = targetPart;
		this.menuListener = new LocalMenuListener();
		Control newControl = (Control) part.getAdapter(Control.class);
		hookControl(newControl);
	}

	/**
	 * @param newControl
	 */
	private void hookControl(Control newControl) {
		if (this.control != null) {
			if (control.getMenu() != null) {
				control.getMenu().removeMenuListener(menuListener);
			}
		}
		this.control = newControl;
		line = -1;
		if (control != null) {
			if (control.getMenu() != null)
				control.getMenu().addMenuListener(menuListener);
		}
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		WaypointCreateDialog dialog = new WaypointCreateDialog(part.getSite().getShell());
		if (resources != null && resources.size() > 0) {
			dialog.setResources(resources.toArray(new IResource[resources.size()]));
			dialog.setLineNumber(line);
			int result = dialog.open();
			if (result != Dialog.OK) return;
			final String[] tagNames = dialog.getTagNames();
			final String message = dialog.getMessage();
			final Date date = dialog.getDate();
			final String author = dialog.getAuthor();
			TagSEAPlugin.run(
				new TagSEAOperation("Creating Resource Waypoints..."){
					@Override
					public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
						MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, 0, "", null);
						monitor.beginTask("Creating waypoints...", resources.size());
						for (IResource resource : resources) {
							if (!resource.isAccessible()) continue;
							IWaypoint wp = TagSEAPlugin.getWaypointsModel().createWaypoint(ResourceWaypointPlugin.WAYPOINT_ID, tagNames);
							if (wp != null) {
								status.merge(wp.setText(message).getStatus());
								status.merge(wp.setDate(date).getStatus());
								status.merge(wp.setAuthor(author).getStatus());
								status.merge(wp.setStringValue(IResourceWaypointAttributes.ATTR_RESOURCE, resource.getFullPath().toPortableString()).getStatus());
								status.merge(wp.setIntValue(IResourceWaypointAttributes.ATTR_LINE, line).getStatus());
							}
							monitor.worked(1);
						}
						monitor.done();
						return status;
					}
				}, 
				false
			);
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.resources = new LinkedList<IResource>();
		if (selection instanceof IStructuredSelection) {
			Iterator i = ((IStructuredSelection)selection).iterator();
			while (i.hasNext()) {
				resources.add((IResource) i.next());
			}
		}
		//if the resources are not accessable, disable the action.
		action.setEnabled(false);
		for (IResource resource :resources) {
			if (resource.isAccessible()) {
				action.setEnabled(true);
				return;
			}
		}

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#dispose()
	 */
	public void dispose() {
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
	 */
	public void init(IAction action) {

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}


}
