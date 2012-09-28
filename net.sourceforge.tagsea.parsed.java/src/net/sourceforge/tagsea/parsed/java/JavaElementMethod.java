package net.sourceforge.tagsea.parsed.java;

import net.sourceforge.tagsea.parsed.comments.IDomainMethod;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class JavaElementMethod implements IDomainMethod {
	
	/**
	 * Used to try to speed up look-ups that occurr in the same document.
	 */
	private IDocument lastDocument;
	private ICompilationUnit lastUnit;

	public JavaElementMethod() {
	}

	public String getDomainObject(IDocument document, IRegion region) {
		//find the file for the document
		if (document != lastDocument) {
			lastDocument = document;
			ITextFileBuffer buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(document);
			if (buffer != null) {
				IPath location = buffer.getLocation();
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(location);
				if (resource != null) {
					IJavaElement element = JavaCore.create(resource);
					if (element instanceof ICompilationUnit) {
						lastUnit = (ICompilationUnit)element;
					}
				}
			}
		}
		if (lastUnit != null) {
			//get the element in the middle of the region
			try {
				IJavaElement element = lastUnit.getElementAt(region.getOffset());
				if (element != null) {
					return element.getHandleIdentifier();
				}
				return lastUnit.getHandleIdentifier();
			} catch (JavaModelException e) {
			}
		}
		return null;
	}

}
