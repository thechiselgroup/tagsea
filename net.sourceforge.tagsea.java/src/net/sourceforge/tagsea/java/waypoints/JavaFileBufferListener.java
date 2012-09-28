/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.java.waypoints;

import net.sourceforge.tagsea.java.JavaTagsPlugin;
import net.sourceforge.tagsea.java.documents.internal.FileBufferAdapter;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * Listens for when file buffers are created or destroyed on java text files.
 * @author Del Myers
 */

public class JavaFileBufferListener extends FileBufferAdapter {
		
	/**
	 * 
	 */
	public JavaFileBufferListener() {
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				IWorkbenchWindow[] windows = JavaTagsPlugin.getDefault().getWorkbench().getWorkbenchWindows();
				for (IWorkbenchWindow window : windows) {
					for (IWorkbenchPage page : window.getPages()) {
						for (IEditorReference editor : page.getEditorReferences()) {
							IEditorInput input;
							try {
								input = editor.getEditorInput();
							} catch (PartInitException e) {
								continue;
							}
							if (input instanceof IFileEditorInput) {
								IFile file = ((IFileEditorInput)input).getFile();
								IPath filePath = file.getFullPath();
								if ("java".equalsIgnoreCase(filePath.getFileExtension())) { //$NON-NLS-1$
									IFileBuffer buffer  = FileBuffers.getTextFileBufferManager().getTextFileBuffer(filePath, LocationKind.IFILE);
									if (buffer != null) {
										bufferCreated(buffer);
									}
								}
							}
						}
					}
				}
			}
		});
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.documents.internal.FileBufferAdapter#bufferCreated(org.eclipse.core.filebuffers.IFileBuffer)
	 */
	@Override
	public void bufferCreated(IFileBuffer buffer) {
		if (buffer instanceof ITextFileBuffer) {
			ITextFileBuffer tfb = (ITextFileBuffer) buffer;
			String extension = tfb.getLocation().getFileExtension();
			if ("java".equalsIgnoreCase(extension)) { //$NON-NLS-1$
				((JavaWaypointDelegate)JavaTagsPlugin.getJavaWaypointDelegate()).addFileBuffer(tfb);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.documents.internal.FileBufferAdapter#bufferDisposed(org.eclipse.core.filebuffers.IFileBuffer)
	 */
	@Override
	public void bufferDisposed(IFileBuffer buffer) {
		if (buffer instanceof ITextFileBuffer) {
			ITextFileBuffer tfb = (ITextFileBuffer) buffer;
			String extension = tfb.getLocation().getFileExtension();
			if ("java".equalsIgnoreCase(extension)) { //$NON-NLS-1$
				((JavaWaypointDelegate)JavaTagsPlugin.getJavaWaypointDelegate()).removeFileBuffer(tfb);
			}
		}
	}
}
