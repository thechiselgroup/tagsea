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
package ca.uvic.cs.tagsea.core.resource;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * Utility class for global resource related functions.
 * @author Peter C. Rigby
 * @author Sean Falconer
 * @author mdesmond
 */
public abstract class ResourceUtil 
{
	/**
	 * Get the document associated with an IFile instance
	 * @param file
	 * @author mdesmond
	 * @return the document associated with the given IFile instance
	 */
	public static IDocument getDocument(IFile file) 
	{
		if(file == null)
			return null;
		
		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		IPath path= file.getFullPath();
		try {
			manager.connect(path, new NullProgressMonitor());
			ITextFileBuffer buffer = manager.getTextFileBuffer(path);
			if (buffer != null) {
				return buffer.getDocument();
			}
		} catch (CoreException e) {
			TagSEAPlugin.log(IStatus.ERROR, "" + file.getFullPath().toOSString() + "'.");
		}
		return null; 
	}
	
	
	/**
	 * Commits changes (if any) to the given file.
	 * @param file the file to commit.
	 * @tag bug(1524158) : add saving functionality to the file itself.
	 */
	public static void commit(IFile file) {
		if (file == null) return;
		ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
		try {
			try {
				ITextFileBuffer buffer = manager.getTextFileBuffer(file.getFullPath());
				if (buffer != null && buffer.isDirty()) {
					buffer.commit(new NullProgressMonitor(), true);
				}
			}  finally {
				manager.disconnect(file.getFullPath(), new NullProgressMonitor());
			}
		} catch (CoreException e) {
			TagSEAPlugin.log(IStatus.ERROR, "" + file.getFullPath().toOSString() + "'." );
		}
	}
    /**
     * Returns the workspace.
     * @author mdesmond
     */
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }
}
