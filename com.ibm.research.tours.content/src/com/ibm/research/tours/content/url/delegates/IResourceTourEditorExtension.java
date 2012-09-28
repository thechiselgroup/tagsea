/**
 * 
 */
package com.ibm.research.tours.content.url.delegates;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;

/**
 * A class that enables the client to extend the functionality
 * of an editor after it has been opened to reveal a tour element.
 * @author Del Myers
 *
 */
public interface IResourceTourEditorExtension {
	
	/**
	 * Runs after the given editor has been opened to view the given
	 * file.
	 * @param part
	 * @param file
	 */
	public void editorOpened(IEditorPart part, IFile file);
	
	/**
	 * Called when the tour element responsible for this extension has closed.
	 * @param part
	 * @param file
	 */
	public void finish(IEditorPart part, IFile file);
	
	/**
	 * Saves state information about this editor extension.
	 * @param memento the memento to save into.
	 */
	public void save(IMemento memento);
	
	/**
	 * Loads state information pertinant to this editor extension.
	 * @param memento the memento to load from.
	 */
	public void load(IMemento memento);

}
