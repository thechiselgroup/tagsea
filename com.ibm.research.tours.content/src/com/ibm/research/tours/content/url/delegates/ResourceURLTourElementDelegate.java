/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tours.content.url.delegates;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.texteditor.ITextEditor;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.content.ToursContentPlugin;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;
import com.ibm.research.tours.content.url.IURL;
import com.ibm.research.tours.content.url.ResourceURL;
import com.ibm.research.tours.fx.AlphaFx;
import com.ibm.research.tours.fx.DefaultHighlightEffect;
import com.ibm.research.tours.fx.EclipseFx;
import com.ibm.research.tours.fx.GlobalHighlightEffect;
import com.ibm.research.tours.fx.LetterboxEffect;
import com.ibm.research.tours.fx.UnhighlightEffect;

public class ResourceURLTourElementDelegate implements IURLTourElementDelegate
{
	private IResource fResource;
	protected AlphaFx fAlphaFX;
	private WorkbenchJob fJob;
	private ResourceURLTourElement fTourElement;
	private HashMap<PartEditorKey, IResourceTourEditorExtension> extensions;
	private HashMap<PartEditorKey, IResourceTourUIExtension> uiExtensions;
	private static final String RESOURCE_EXTENSION_MEMENTO = "resource.extension";
	
	private class PartEditorKey {
		private String key;

		public PartEditorKey(String editorID, String extension) {
			if (extension == null) extension = "";
			extension = extension.toLowerCase();
			this.key = editorID + ":" + extension;
		}
		
		public int hashCode() {
			return key.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !getClass().equals(obj.getClass())) return false;
			return key.equals(((PartEditorKey)obj).key);
		}
	}

	public ResourceURLTourElementDelegate(ResourceURLTourElement tourElement,IResource resource)
	{
		fResource = resource;
		fTourElement = tourElement;
	}

	public ITourElement createClone() 
	{
		return new ResourceURLTourElement(fResource);
	}

	public Image getImage() 
	{
		return ToursContentPlugin.getImage(fResource);
	}

	public String getShortText() 
	{
		return fResource.getName();
	}

	public String getText() 
	{
		return fResource.getFullPath().toString();
	}

	public void start() 
	{

	}

	public void stop() 
	{
		if(fJob!=null)
			fJob.cancel();

		if (fResource instanceof IFile) {
			IFile file = (IFile) fResource;
			FileEditorInput input = new FileEditorInput(file);
			IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(input);
			if (part != null) {
				IResourceTourEditorExtension extension = getEditorExtension(part.getSite().getId(), file.getFileExtension());
				if (extension != null) {
					extension.finish(part, file);
				}
			}
			
		}
		if(fAlphaFX !=null && !fAlphaFX.getAlphaShell().getShell().isDisposed())
			fAlphaFX.dispose();
	}

	public void transition() 
	{
		// always show the resource - whether it is a file or whatever, in the package explorer for context
		PackageExplorerPart view = PackageExplorerPart.openInActivePerspective();
		view.tryToReveal(fResource);

		if(fResource instanceof IFile)
		{
			IFile file = (IFile)fResource;

			try 
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				final IEditorPart part = IDE.openEditor(page, file);
				processExtension(part, file);
				
				if(fTourElement.getMaximixedHint())
					EclipseFx.maximizeActiveEditor();

				if(part instanceof ITextEditor)
				{
					ITextEditor editor = (ITextEditor)part;
					editor.setHighlightRange(0,0,true);
				}

				if(fTourElement.getHighlightEffect()!=null)
				{
					fJob = new WorkbenchJob("") 
					{
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor) 
						{
							if(fTourElement.getHighlightEffect() instanceof DefaultHighlightEffect)
							{
								GlobalHighlightEffect.reset();
								fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
								Rectangle bounds = EclipseFx.getBounds(part);
								fAlphaFX.focus(bounds, true);
								fAlphaFX.fadeTo(128);
							}
							else if(fTourElement.getHighlightEffect() instanceof LetterboxEffect)
							{
								GlobalHighlightEffect.reset();
								fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
								Rectangle bounds = EclipseFx.getBounds(part);
								fAlphaFX.letterbox(bounds.y, bounds.height, true);
								fAlphaFX.fadeTo(128);
							}
							else if(fTourElement.getHighlightEffect() instanceof UnhighlightEffect)
							{
								if (fAlphaFX!=null)
								{
									Rectangle bounds = EclipseFx.getBounds(part);
									fAlphaFX.unfocus(bounds, true);
								}
								else
								{
									AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
									if ( alphaFX!=null )
									{
										Rectangle bounds = EclipseFx.getBounds(part);
										alphaFX.unfocus(bounds, true);
									}
								}
							}
							else if(fTourElement.getHighlightEffect() instanceof GlobalHighlightEffect)
							{
								AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
								Rectangle bounds = EclipseFx.getBounds(part);
								alphaFX.focus(bounds, true);
								alphaFX.fadeTo(128);
							}


							return Status.OK_STATUS;
						}
					};
					fJob.schedule();
				}
			}
			catch (PartInitException e) 
			{
				e.printStackTrace();
			}
		}
	}

	private void processExtension(IEditorPart part, IFile file) {
		String fileExtension = file.getFileExtension();
		String editorID = part.getSite().getId();
		IResourceTourEditorExtension extension = getEditorExtension(editorID, fileExtension);
		if (extension != null) {
			try {
				extension.editorOpened(part, file);
			} catch (Exception e) {
				ToursContentPlugin.log("Error executing extension", e);
			}
		}
		
	}

	private synchronized void loadExtensions() {
		if (extensions != null) return;
		extensions = new HashMap<PartEditorKey, IResourceTourEditorExtension>();
		uiExtensions = new HashMap<PartEditorKey, IResourceTourUIExtension>();
		IConfigurationElement[] extensionPointElements = Platform.getExtensionRegistry().getConfigurationElementsFor("com.ibm.research.tours.content.tourEditorExtension");
		for (IConfigurationElement editorExtension : extensionPointElements) {
			String editorID = editorExtension.getAttribute("editorID");
			String fileExtension = editorExtension.getAttribute("fileExtension");
			PartEditorKey key = new PartEditorKey(editorID, fileExtension);
			if (!extensions.containsKey(key)) {
				try {
					IResourceTourEditorExtension extension = (IResourceTourEditorExtension) editorExtension.createExecutableExtension("class");
					extensions.put(key, extension);
					if (editorExtension.getAttribute("ui") != null) {
						IResourceTourUIExtension uiExtension = (IResourceTourUIExtension)editorExtension.createExecutableExtension("ui");
						uiExtensions.put(key, uiExtension);
					}
				} catch (CoreException e) {
					ToursContentPlugin.getDefault().getLog().log(e.getStatus());
				}
			}

		}
		
	}

	public IURL getUrl() 
	{
		return new ResourceURL(fResource);
	}
	
	public void load(IMemento memento) {
		ResourceURL url = (ResourceURL) getUrl();
		IResource resource = url.getResource();
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			IEditorDescriptor editor = IDE.getDefaultEditor(file);
			IResourceTourEditorExtension extension = getEditorExtension(editor.getId(), file.getFileExtension());
			if (extension != null) {
				IMemento extensionMemento = memento.getChild(RESOURCE_EXTENSION_MEMENTO);
				if (extensionMemento != null) {
					extension.load(memento);
				}
			}
		}
	}
	
	public void save(IMemento memento) {
		ResourceURL url = (ResourceURL) getUrl();
		IResource resource = url.getResource();
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			IEditorDescriptor editor = IDE.getDefaultEditor(file);
			if (editor != null) {
				IResourceTourEditorExtension extension = getEditorExtension(editor.getId(), file.getFileExtension());
				if (extension != null) {
					IMemento extensionMemento = memento.getChild(RESOURCE_EXTENSION_MEMENTO);
					if (extensionMemento == null) {
						extensionMemento = memento.createChild(RESOURCE_EXTENSION_MEMENTO);
					}
					extension.save(extensionMemento);
				}
			}
		}
	}

	public IResourceTourEditorExtension getEditorExtension(String id,
			String fileExtension) {
		loadExtensions();
		return extensions.get(new PartEditorKey(id, fileExtension));
	}

	public IResourceTourUIExtension getUIExtension(String id,
			String fileExtension) {
		loadExtensions();
		return uiExtensions.get(new PartEditorKey(id, fileExtension));
	}
}
