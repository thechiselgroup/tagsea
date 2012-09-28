/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.parsed.core.internal.resources;

import java.util.HashMap;

import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IDocumentRegistry;
import net.sourceforge.tagsea.parsed.parser.IWaypointParseProblemCollector;
import net.sourceforge.tagsea.parsed.parser.WaypointParseProblem;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * Listens to the creation/deletion of file buffers and registers listeners to adapt to document changes.
 * Invokes a parse on a buffer created unless it was created during a ParseFileOperation.
 * @author Del Myers
 *
 */
public class DocumentRegistry extends FileBufferAdapter implements IWaypointParseProblemCollector, IDocumentRegistry {
	public static final DocumentRegistry INSTANCE = new DocumentRegistry();

	private HashMap<IDocument, IFile> documentFileMap;
	private HashMap<IDocument, ITextFileBuffer> documentBufferMap;
	
	private IDocumentListener documentListener;
		
	private boolean connected;
	
	private ProblemAnnotator annotator;
	
	private ListenerList documentListeners;

	/**
	 * 
	 */
	private DocumentRegistry() {
		this.documentFileMap = new HashMap<IDocument,IFile>();
		documentListener = new DocumentWaypointUpdater(this);
		documentBufferMap = new HashMap<IDocument, ITextFileBuffer>();
		annotator = new ProblemAnnotator();
		connected = false;
		this.documentListeners = new ListenerList(ListenerList.EQUALITY);
	}
	
	public void bufferCreated(IFileBuffer buffer) {
		internalBufferCreated(buffer);
	}
	
	private void internalBufferCreated(IFileBuffer buffer) {
		if (buffer instanceof ITextFileBuffer) {
			ITextFileBuffer textBuffer = (ITextFileBuffer) buffer;
			IDocument document = textBuffer.getDocument();
			IPath location = textBuffer.getLocation();
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(location);
			if (resource != null && resource.isAccessible() && resource instanceof IFile) {
				documentFileMap.put(document, (IFile)resource);
				documentBufferMap.put(document, textBuffer);
				document.addDocumentListener(documentListener);
				for (Object o : documentListeners.getListeners()) {
					((IDocumentLifecycleListener)o).documentCreated(textBuffer);
				}
			}
		}
	}
	
	public void ignoreDocument(IDocument document) {
		document.removeDocumentListener(documentListener);
	}
	
	public void watchDocument(IDocument document) {
		document.addDocumentListener(documentListener);
	}

	public synchronized void bufferDisposed(IFileBuffer buffer) {
		internalBufferDisposed(buffer);
	}
	
	private void internalBufferDisposed(IFileBuffer buffer) {
		if (buffer instanceof ITextFileBuffer) {
			ITextFileBuffer textBuffer = (ITextFileBuffer) buffer;
			IDocument document = textBuffer.getDocument();
			document.removeDocumentListener(documentListener);
			IFile file = documentFileMap.remove(document);
			documentBufferMap.remove(document);
			annotator.cleanDocument(document, (IFile)ResourcesPlugin.getWorkspace().getRoot().findMember(textBuffer.getLocation()));
			if (file != null) {
				for (Object o : documentListeners.getListeners()) {
					((IDocumentLifecycleListener)o).documentDisposed(textBuffer);
				}
			}
		}
	}
	
	public synchronized IFile getFileForDocument(IDocument document) {
		return documentFileMap.get(document);
	}
	
	public synchronized ITextFileBuffer getBufferForDocument(IDocument document) {
		return documentBufferMap.get(document);
	}
	
	/**
	 * Convenience method for connecting this registry to the buffer manager. Also initializes all documents.
	 */
	public synchronized void connect() {
		if (connected) return;
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				IWorkbenchWindow[] windows = ParsedWaypointPlugin.getDefault().getWorkbench().getWorkbenchWindows();
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

								IFileBuffer buffer  = FileBuffers.getTextFileBufferManager().getTextFileBuffer(filePath, LocationKind.IFILE);
								if (buffer != null) {
									internalBufferCreated(buffer);
								}
								
							}
						}
					}
				}
			}
		});
		FileBuffers.getTextFileBufferManager().addFileBufferListener(this);
		connected = true;
	}

	/**
	 * @param marker
	 * @return
	 */
	public WaypointParseProblem getProblemForMarker(IMarker marker) {
		return annotator.getProblemForMarker(marker);
	}

	public void accept(WaypointParseProblem problem) {
		IDocument document = problem.getDocument();
		IFile file = getFileForDocument(document);
		if (file == null) return;
		annotator.createAnnotation(problem, file);
	}

	public WaypointParseProblem[] problems() {
		return null;
	}

	/**
	 * @param document
	 * @param r
	 */
	public void clearProblems(IDocument document, IRegion r) {
		IFile file = getFileForDocument(document);
		if (file == null) return;
		annotator.cleanDocument(document, file, r);
	}
	
	public void clearProblems(IDocument document) {
		IFile file = getFileForDocument(document);
		if (file == null) return;
		annotator.cleanDocument(document, file);
	}

	public void addDocumentLifecycleListener(IDocumentLifecycleListener listener) {
		documentListeners.add(listener);		
	}

	public void removeDocumentLifecycleListener(
			IDocumentLifecycleListener listener) {
		documentListeners.remove(listener);		
	}

}
