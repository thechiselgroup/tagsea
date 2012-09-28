package ca.uvic.cs.tagsea.editing;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.part.FileEditorInput;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.core.Waypoint;

public class RefactorHelp {
	
	protected static boolean requestSave(Tag[] tags) {
		HashSet<Waypoint> waypoints = new HashSet<Waypoint>();
		for (Tag tag : tags) {
			waypoints.addAll(Arrays.asList(tag.getAllWaypoints()));
		}
		TagSEAPlugin plugin = TagSEAPlugin.getDefault();
		if (plugin == null) return false;
		IEditorPart[] editors = 
			plugin.
				getWorkbench().
				getActiveWorkbenchWindow().
				getActivePage().
				getDirtyEditors();
		final HashSet<IEditorPart> editorsToSave = new HashSet<IEditorPart>();
		for (Waypoint waypoint : waypoints) {
			IMarker marker = waypoint.getMarker();
			if (marker != null) {
				IResource resource = marker.getResource();
				for (IEditorPart editor : editors) {
					IEditorInput input = editor.getEditorInput();
					if (input instanceof FileEditorInput) {
						if (((FileEditorInput)input).getFile().equals(resource)) {
							editorsToSave.add(editor);
						}
					}
				}
			}
		}
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		if (editorsToSave.size() > 0) {
			ListDialog dialog = new ListDialog(shell);
			dialog.setAddCancelButton(true);
			dialog.setTitle("Must Save Editors");
			dialog.setMessage(
					"The following editors must be saved before the refactoring can continue. " +
					"Please save them and try again. " +
					"Select 'OK' to save, or 'Cancel' to cancel."
				);
			dialog.setLabelProvider(new LabelProvider(){
				public Image getImage(Object element) {
					return ((IEditorPart)element).getTitleImage();
				}
				@Override
				public String getText(Object element) {
					return ((IEditorPart)element).getTitle();
				}
			});
			dialog.setContentProvider(new ArrayContentProvider());
			dialog.setInput(editorsToSave);
			int result = dialog.open();
			if (result == ListDialog.CANCEL) return false;
			ProgressMonitorDialog saveDialog = new ProgressMonitorDialog(shell);
			try {
				saveDialog.run(false, false, new IRunnableWithProgress(){
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						for (IEditorPart editor : editorsToSave) {
							editor.doSave(new NullProgressMonitor());
						}
					}});
			} catch (InvocationTargetException e) {
				TagSEAPlugin.log(e.getLocalizedMessage(), e);
			} catch (InterruptedException e) {
			}
			return false;
		}
		return true;
	}
}
