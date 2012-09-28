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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.content.BoxTextEffect;
import com.ibm.research.tours.content.DashedBoxTextEffect;
import com.ibm.research.tours.content.HighlightedTextEffect;
import com.ibm.research.tours.content.ITextEffect;
import com.ibm.research.tours.content.NativeProblemUnderlineTextEffect;
import com.ibm.research.tours.content.SquigglesTextEffect;
import com.ibm.research.tours.content.ToursContentPlugin;
import com.ibm.research.tours.content.UnderlinedTextEffect;
import com.ibm.research.tours.content.VerticalBarTextEffect;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;
import com.ibm.research.tours.content.url.IURL;
import com.ibm.research.tours.content.url.TextRegionURL;
import com.ibm.research.tours.content.util.DocumentReadSession;
import com.ibm.research.tours.fx.AlphaFx;
import com.ibm.research.tours.fx.DefaultHighlightEffect;
import com.ibm.research.tours.fx.EclipseFx;
import com.ibm.research.tours.fx.GlobalHighlightEffect;
import com.ibm.research.tours.fx.LetterboxEffect;
import com.ibm.research.tours.fx.UnhighlightEffect;

public class TextRegionURLTourElementDelegate implements IURLTourElementDelegate
{
	private MarkerAnnotationPreferences fMarkerAnnotationPreferences;
	private AnnotationPreference fAnnotationPreference;
	private IFile fFile;
	private IRegion fTextRegion;
	private IRegion fLineRegion;
	private IMarker fMarker;
	private String fSnippet;
	private  AlphaFx fAlphaFX;
	private WorkbenchJob fJob;
	private ResourceURLTourElement fTourElement;
	private IPreferenceStore fStore;

	public TextRegionURLTourElementDelegate(ResourceURLTourElement tourElement,IFile file, IRegion textRegion)
	{
		fTourElement = tourElement;
		fFile = file;
		fTextRegion = textRegion;
		initTextRegion();
	}

	private void initTextRegion() 
	{
		DocumentReadSession session = new DocumentReadSession(fFile);

		try
		{	
			session.begin();

			try 
			{
				fSnippet = session.getDocument().get(fTextRegion.getOffset(), fTextRegion.getLength());

				int lineStart = session.getDocument().getLineOfOffset(fTextRegion.getOffset()) + 1;
				int lineEnd = session.getDocument().getLineOfOffset(fTextRegion.getOffset()+ fTextRegion.getLength()) + 1;

				fLineRegion = new Region(lineStart,lineEnd - lineStart);
			} 
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
		finally
		{
			session.end();
		}
	}

	public ITourElement createClone() 
	{ 
		return new ResourceURLTourElement(fFile,fTextRegion);
	}

	public Image getImage() 
	{
		return ToursContentPlugin.getImage(fFile);
	}

	private String getLineRegionString() 
	{
		if(fLineRegion.getLength() == 0)
			return " (Line #" + fLineRegion.getOffset() + ")";
		else
			return " (Lines #" + fLineRegion.getOffset() + "-" + (fLineRegion.getOffset() + fLineRegion.getLength()+")");
	}

	public String getShortText() 
	{
		return fFile.getName() + getLineRegionString();
	}

	public String getText() 
	{
		return fFile.getFullPath() + getLineRegionString();
	}

	public void start() 
	{
		if(fTourElement.getTextHighlighting())
		{
			fMarkerAnnotationPreferences = EditorsPlugin.getDefault().getMarkerAnnotationPreferences();
			List annotations = fMarkerAnnotationPreferences.getAnnotationPreferences();

			for(Object o : annotations) 
			{
				AnnotationPreference info = (AnnotationPreference) o;
				String type = (String)info.getAnnotationType();

				if(type.equals("com.ibm.research.tours.content.tourtextannotation"))
					fAnnotationPreference = info;
			}

			fStore = EditorsPlugin.getDefault().getPreferenceStore();
			fStore.setValue(fAnnotationPreference.getHighlightPreferenceKey(), fTourElement.getTextHighlighting()?"true":"false");
			fStore.setValue(fAnnotationPreference.getOverviewRulerPreferenceKey(), fTourElement.getShowInOverview()?"true":"false");
			fStore.setValue(fAnnotationPreference.getVerticalRulerPreferenceKey(), fTourElement.getShowInVertical()?"true":"false");

			fStore.setValue(fAnnotationPreference.getHighlightPreferenceKey(),"false");
			fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"false");

			ITextEffect effect = fTourElement.getSelectedTextEffect();

			if(effect instanceof HighlightedTextEffect)
			{
				fStore.setValue(fAnnotationPreference.getHighlightPreferenceKey(),"true");
				fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"false");
			}
			else if(effect instanceof SquigglesTextEffect)
			{
				fStore.setValue(fAnnotationPreference.getTextStylePreferenceKey(),AnnotationPreference.STYLE_SQUIGGLES);
				fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"true");
			}
			else if(effect instanceof UnderlinedTextEffect)
			{
				fStore.setValue(fAnnotationPreference.getTextStylePreferenceKey(),AnnotationPreference.STYLE_UNDERLINE);
				fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"true");
			}
			else if(effect instanceof BoxTextEffect)
			{
				fStore.setValue(fAnnotationPreference.getTextStylePreferenceKey(),AnnotationPreference.STYLE_BOX);
				fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"true");
			}
			else if(effect instanceof VerticalBarTextEffect)
			{
				fStore.setValue(fAnnotationPreference.getTextStylePreferenceKey(),AnnotationPreference.STYLE_IBEAM);
				fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"true");
			}
			else if(effect instanceof DashedBoxTextEffect)
			{
				fStore.setValue(fAnnotationPreference.getTextStylePreferenceKey(),AnnotationPreference.STYLE_DASHED_BOX);
				fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"true");
			}
			else if(effect instanceof NativeProblemUnderlineTextEffect)
			{
				fStore.setValue(fAnnotationPreference.getTextStylePreferenceKey(),AnnotationPreference.STYLE_PROBLEM_UNDERLINE);
				fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"true");
			}
			else
				fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"false");

			if(fTourElement.getTextPresentationColor() != null)
			{
				String color = fTourElement.getTextPresentationColor().red + "," + fTourElement.getTextPresentationColor().green + "," + fTourElement.getTextPresentationColor().blue;
				fStore.setValue(fAnnotationPreference.getColorPreferenceKey(), color);
			}
		}

		try 
		{
			fMarker = fFile.createMarker("com.ibm.research.tours.content.tourtextmarker");
			fMarker.setAttribute(IMarker.CHAR_START, fTextRegion.getOffset());
			fMarker.setAttribute(IMarker.CHAR_END, fTextRegion.getOffset() + fTextRegion.getLength());
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	public void stop() 
	{
		if(fAnnotationPreference!=null && fStore!=null)
		{
			fStore.setValue(fAnnotationPreference.getHighlightPreferenceKey(),"false");
			fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"false");
		}

		try 
		{
			if(fMarker!=null)
				fMarker.delete();
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}

		if(fJob!=null)
			fJob.cancel();

		if(fAlphaFX !=null && !fAlphaFX.getAlphaShell().getShell().isDisposed())
			fAlphaFX.dispose();
	}

	public void transition() 
	{
		// always show the resource - whether it is a file or whatever, in the package explorer for context
		PackageExplorerPart view = PackageExplorerPart.openInActivePerspective();
		view.tryToReveal(fFile);

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		try 
		{
			final IEditorPart part = IDE.openEditor(page, fMarker, true);

			if(part instanceof ITextEditor)
			{
				ITextEditor editor = (ITextEditor)part;
				editor.setHighlightRange(MarkerUtilities.getCharStart(fMarker),0,true);
			}
			else
			{
				if(part instanceof IAdaptable)
				{
					IAdaptable adaptable = 	(IAdaptable)part;
					ITextEditor editor = (ITextEditor)adaptable.getAdapter(ITextEditor.class);

					if(editor!=null)
						editor.setHighlightRange(MarkerUtilities.getCharStart(fMarker),0,true);
				}
			}

			if(fTourElement.getMaximixedHint())
				EclipseFx.maximizeActiveEditor();

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

	public IURL getUrl() 
	{
		return new TextRegionURL(fFile,fTextRegion);
	}
}
