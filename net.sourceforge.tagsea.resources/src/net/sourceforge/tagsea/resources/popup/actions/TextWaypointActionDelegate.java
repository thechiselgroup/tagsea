package net.sourceforge.tagsea.resources.popup.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ui.TextWaypointCreateDialog;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class TextWaypointActionDelegate implements  IEditorActionDelegate  
{
	private IEditorPart part;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		part = targetEditor;
	}

	@SuppressWarnings("restriction")
	public void run(IAction action) 
	{
		if(part instanceof ITextEditor)
		{
			ITextEditor editor = (ITextEditor)part;
			ISelection selection = editor.getSelectionProvider().getSelection();

			if(selection instanceof ITextSelection)
			{
				ITextSelection textSelection = (ITextSelection)selection;

				final int offset = textSelection.getOffset();
				final int length = textSelection.getLength();
				String text = textSelection.getText();

				IResource _resource = null;
				String fileName = null;
				
				// For class files
				String _handleIdentifier = null;
				
				IEditorInput input =  editor.getEditorInput();

				if(input instanceof IClassFileEditorInput)
				{
					IClassFileEditorInput classFileEditorInput = (IClassFileEditorInput)input;
					IClassFile classFile = classFileEditorInput.getClassFile();
					fileName = classFile.getElementName();
					_resource = classFile.getJavaProject().getResource();
					_handleIdentifier = classFile.getHandleIdentifier();
					
				}
				else if(input instanceof IFileEditorInput)
				{
					IFileEditorInput fEditorInput = (IFileEditorInput)input;
					_resource = fEditorInput.getFile();
					fileName = _resource.getName();
				}
				
				final IResource resource = _resource;
				final String handleIdentifier = _handleIdentifier;
				
				TextWaypointCreateDialog dialog = new TextWaypointCreateDialog(part.getSite().getShell());
				dialog.setFileName(fileName);
				dialog.setRegion(new Region(offset,length));
				dialog.setText(text);

				int result = dialog.open();
				if (result != Dialog.OK) return;
				final String[] tagNames = dialog.getTagNames();
				final String message = dialog.getMessage();
				final Date date = dialog.getDate();
				final String author = dialog.getAuthor();

				TagSEAPlugin.run(
						new TagSEAOperation("Creating Resource Waypoint..."){
							@Override
							public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
								MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, 0, "", null);
								monitor.beginTask("Creating Resource Waypoint...", 1);
								IWaypoint wp = TagSEAPlugin.getWaypointsModel().createWaypoint(ResourceWaypointPlugin.WAYPOINT_ID, tagNames);
								if (wp != null) 
								{
									status.merge(wp.setText(message).getStatus());
									status.merge(wp.setDate(date).getStatus());
									status.merge(wp.setAuthor(author).getStatus());
									status.merge(wp.setStringValue(IResourceWaypointAttributes.ATTR_RESOURCE, resource.getFullPath().toPortableString()).getStatus());
									status.merge(wp.setIntValue(IResourceWaypointAttributes.ATTR_CHAR_START, offset).getStatus());
									status.merge(wp.setIntValue(IResourceWaypointAttributes.ATTR_CHAR_END, offset + length).getStatus());
									
									if(handleIdentifier!=null)
										status.merge(wp.setStringValue(IResourceWaypointAttributes.ATTR_HANDLE_IDENTIFIER,handleIdentifier).getStatus());
								}
								monitor.done();
								return status;
							}
						}, 
						false
				);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
	}
}
