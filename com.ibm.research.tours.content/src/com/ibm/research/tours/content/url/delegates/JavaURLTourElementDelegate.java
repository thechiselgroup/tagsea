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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;

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
import com.ibm.research.tours.content.url.JavaURL;
import com.ibm.research.tours.fx.AlphaFx;
import com.ibm.research.tours.fx.EclipseFx;
import com.ibm.research.tours.fx.IHighlightEffect;

public class JavaURLTourElementDelegate implements IURLTourElementDelegate
{
	private MarkerAnnotationPreferences fMarkerAnnotationPreferences;
	private AnnotationPreference fAnnotationPreference;
	private JavaElementLabelProvider fJavaElementLabelProvider;
	private JavaElementLabelProvider fRuntimeJavaElementLabelProvider;
	private IJavaElement fElement;
	private ResourceURLTourElement fTourElement;
	private IMarker fMarker;
	protected AlphaFx fAlphaFX;
	private WorkbenchJob fJob;
	private IPreferenceStore fStore;
	private ISourceRange fRange;

	public JavaURLTourElementDelegate(ResourceURLTourElement tourElement,IJavaElement element)
	{
		fElement = element;
		fTourElement = tourElement;
	}

	private JavaElementLabelProvider getJavaElementLabelProvider()
	{
		if(fJavaElementLabelProvider == null)
			fJavaElementLabelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_QUALIFIED|
					JavaElementLabelProvider.SHOW_SMALL_ICONS|
					JavaElementLabelProvider.SHOW_PARAMETERS);
		return fJavaElementLabelProvider;
	}

	private JavaElementLabelProvider getRuntimeJavaElementLabelProvider()
	{
		if(fRuntimeJavaElementLabelProvider == null)
			fRuntimeJavaElementLabelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_SMALL_ICONS|
					JavaElementLabelProvider.SHOW_PARAMETERS);
		return fRuntimeJavaElementLabelProvider;
	}

	public ITourElement createClone() 
	{
		return new ResourceURLTourElement(fElement);
	}

	public Image getImage() 
	{
		return ToursContentPlugin.getImage(fElement);
	}

	public String getShortText() 
	{
		return getRuntimeJavaElementLabelProvider().getText(fElement);
	}

	public String getText() 
	{
		return getJavaElementLabelProvider().getText(fElement);
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

			// This code is `duplicated!!! 
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
			else if(effect instanceof NativeProblemUnderlineTextEffect)
			{
				fStore.setValue(fAnnotationPreference.getTextStylePreferenceKey(),AnnotationPreference.STYLE_PROBLEM_UNDERLINE);
				fStore.setValue(fAnnotationPreference.getTextPreferenceKey(),"true");
			}
			else if(effect instanceof DashedBoxTextEffect)
			{
				fStore.setValue(fAnnotationPreference.getTextStylePreferenceKey(),AnnotationPreference.STYLE_DASHED_BOX);
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
			fRange = null;

			if(fElement.getElementType() == IJavaElement.TYPE)
			{
				IType type = (IType)fElement;
				fRange = type.getSourceRange();
			}
			else if(fElement.getElementType() == IJavaElement.METHOD)
			{
				IMethod method = (IMethod)fElement;
				fRange = method.getSourceRange();
			}
			else if(fElement.getElementType() == IJavaElement.FIELD)
			{
				IField field = (IField)fElement;
				fRange = field.getSourceRange();
			}

			if(fRange !=null)
			{
				fMarker = fElement.getResource().createMarker("com.ibm.research.tours.content.tourtextmarker");
				fMarker.setAttribute(IMarker.CHAR_START, fRange.getOffset());
				fMarker.setAttribute(IMarker.CHAR_END, fRange.getOffset() + fRange.getLength());
			}
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
		view.tryToReveal(fElement);

		try 
		{
			final IEditorPart part = JavaUI.openInEditor(fElement);

			if(fTourElement.getMaximixedHint())
				EclipseFx.maximizeActiveEditor();

			if(part instanceof ITextEditor)
			{
				if(fRange!=null)
				{
					ITextEditor editor = (ITextEditor)part;
					editor.setHighlightRange(fRange.getOffset(),0,true);
				}
			}

			if(fTourElement.getHighlightEffect()!=null)
			{
				fJob = new WorkbenchJob("") 
				{
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) 
					{
						IHighlightEffect effect = fTourElement.getHighlightEffect();
						if ( effect!=null )
							fAlphaFX = effect.apply(fAlphaFX, part);

						//						if(fTourElement.getHighlightEffect() instanceof DefaultHighlightEffect)
						//						{
						//							GlobalHighlightEffect.reset();
						//							fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
						//							Rectangle bounds = EclipseFx.getBounds(part);
						//							fAlphaFX.focus(bounds, true);
						//							fAlphaFX.fadeTo(128);
						//						}
						//						else if (fTourElement.getHighlightEffect() instanceof MismarHighlightEffect) {
						//							GlobalHighlightEffect.reset();
						//							fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
						//							Rectangle bounds = EclipseFx.getBounds(part);
						//							IViewPart mismarView = getMismarView();
						//							
						//							if (mismarView != null) {
						//								Rectangle bounds2 = EclipseFx.getBounds(mismarView);
						//								fAlphaFX.focus(bounds,bounds2, true);
						//							} else {
						//								fAlphaFX.focus(bounds, true);
						//							}
						//							
						//							fAlphaFX.fadeTo(128);
						//						}
						//						else if(fTourElement.getHighlightEffect() instanceof LetterboxEffect)
						//						{
						//							GlobalHighlightEffect.reset();
						//							fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
						//							Rectangle bounds = EclipseFx.getBounds(part);
						//							fAlphaFX.letterbox(bounds.y, bounds.height, true);
						//							fAlphaFX.fadeTo(128);
						//						}
						//						else if(fTourElement.getHighlightEffect() instanceof UnhighlightEffect)
						//						{
						//							if (fAlphaFX!=null)
						//							{
						//								Rectangle bounds = EclipseFx.getBounds(part);
						//								fAlphaFX.unfocus(bounds, true);
						//							}
						//							else
						//							{
						//								AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
						//								if ( alphaFX!=null )
						//								{
						//									Rectangle bounds = EclipseFx.getBounds(part);
						//									alphaFX.unfocus(bounds, true);
						//								}
						//							}
						//						}
						//						else if(fTourElement.getHighlightEffect() instanceof GlobalHighlightEffect)
						//						{
						//							AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
						//							Rectangle bounds = EclipseFx.getBounds(part);
						//							alphaFX.focus(bounds, true);
						//							alphaFX.fadeTo(128);
						//						}

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
		catch (JavaModelException e) 
		{
			e.printStackTrace();
		}
	}

	public IURL getUrl() 
	{
		return new JavaURL(fElement);
	}
}
