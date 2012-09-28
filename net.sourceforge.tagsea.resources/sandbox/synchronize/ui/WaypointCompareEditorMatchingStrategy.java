package net.sourceforge.tagsea.resources.synchronize.ui;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;

public class WaypointCompareEditorMatchingStrategy implements
		IEditorMatchingStrategy {

	public boolean matches(IEditorReference editorRef, IEditorInput input) {
		return editorRef.getId().equals(WaypointCompareEditor.ID) && input instanceof WaypointSynchronizeEditorInput; 
	}

}
