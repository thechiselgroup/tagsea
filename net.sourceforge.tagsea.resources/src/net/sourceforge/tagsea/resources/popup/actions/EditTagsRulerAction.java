package net.sourceforge.tagsea.resources.popup.actions;


import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ui.WaypointCreateDialog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class EditTagsRulerAction implements IEditorActionDelegate,  IActionDelegate2 {

	private IEditorPart part;
int line = -1;



	/**
	 * Constructor for Action1.
	 */
	public EditTagsRulerAction() {
		super();
	}




	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		WaypointCreateDialog dialog = new WaypointCreateDialog(part.getSite().getShell());
		if (part != null) {
			IVerticalRulerInfo ruler = (IVerticalRulerInfo)part.getAdapter(IVerticalRulerInfo.class);
			if (ruler != null) {
				this.line = ruler.getLineOfLastMouseButtonActivity();
			}
		}
		IFile file = null;
		if (part.getEditorInput() instanceof IFileEditorInput) {
			file = ((IFileEditorInput)part.getEditorInput()).getFile();
		} else {
			return;
		}
		
		dialog.setResources(new IResource[]{file});
		dialog.setLineNumber(line);
		int result = dialog.open();
		if (result != Dialog.OK) return;
		final String[] tagNames = dialog.getTagNames();
		final String message = dialog.getMessage();
		final Date date = dialog.getDate();
		final String author = dialog.getAuthor();
		final IResource resource = file;
		TagSEAPlugin.run(
				new TagSEAOperation("Creating Resource Waypoint..."){
					@Override
					public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
						MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, 0, "", null);
						monitor.beginTask("Creating Resource Waypoint...", 1);
						IWaypoint wp = TagSEAPlugin.getWaypointsModel().createWaypoint(ResourceWaypointPlugin.WAYPOINT_ID, tagNames);
						if (wp != null) {
							status.merge(wp.setText(message).getStatus());
							status.merge(wp.setDate(date).getStatus());
							status.merge(wp.setAuthor(author).getStatus());
							status.merge(wp.setStringValue(IResourceWaypointAttributes.ATTR_RESOURCE, resource.getFullPath().toPortableString()).getStatus());
							status.merge(wp.setIntValue(IResourceWaypointAttributes.ATTR_LINE, line).getStatus());
						}
						monitor.done();
						return status;
					}
				}, 
				false
		);

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
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

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.part = targetEditor;
		if (targetEditor == null) return;
		
		IEditorInput input = targetEditor.getEditorInput();
		if (!(input instanceof IFileEditorInput)) {
			action.setEnabled(false);
		} else {
			action.setEnabled(true);
		}
		
	}

}
