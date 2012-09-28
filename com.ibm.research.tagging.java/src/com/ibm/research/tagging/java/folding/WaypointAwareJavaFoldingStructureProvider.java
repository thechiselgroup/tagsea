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
package com.ibm.research.tagging.java.folding;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.text.folding.DefaultJavaFoldingStructureProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.ibm.research.tagging.java.extractor.WaypointDefinitionExtractor;

/**
 * @tag TagFolding : Tag folding implementation
 * @author mdesmond
 *
 */
public class WaypointAwareJavaFoldingStructureProvider extends DefaultJavaFoldingStructureProvider 
{
	protected ITextEditor fTagEditor;
	
	@Override
	public void install(ITextEditor editor, ProjectionViewer viewer) 
	{
		super.install(editor,viewer);
		
		/* We depend on jdt.ui anyway so what the hell */
		if (editor instanceof JavaEditor)
			fTagEditor= editor;
	}
	
	@Override
	protected void computeFoldingStructure(IJavaElement element, FoldingStructureComputationContext ctx) 
	{
		super.computeFoldingStructure(element, ctx);
		
		switch (element.getElementType())
		{
		case IJavaElement.METHOD:
			return;
			
		case IJavaElement.IMPORT_CONTAINER:
		case IJavaElement.TYPE:
		case IJavaElement.FIELD:
		case IJavaElement.INITIALIZER:
			break;
		default:
			return;
		}

		IRegion[] waypointRegions = WaypointDefinitionExtractor.getWaypointRegions(getDocument(),(ISourceReference)element);

		for(IRegion region : waypointRegions)
		{

			JavaProjectionAnnotation annotation = new JavaProjectionAnnotation(true,element,false);
			IRegion normalized= alignRegion(region, ctx);
			
			/* A null here indicates that we are attempting to fold a single line region */
			if(normalized != null)
			{
				Position position = createCommentPosition(normalized);
				ctx.addProjectionRange(annotation, position);
			}
		}

	}
	
	/**
	 * Get the document in the open editor instance
	 * @return the document instance
	 */
	private IDocument getDocument() 
	{
		IDocumentProvider provider= fTagEditor.getDocumentProvider();
		return provider.getDocument(fTagEditor.getEditorInput());
	}
	
}
