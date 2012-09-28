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
package com.ibm.research.tours.fx.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ide.IDE;

import com.ibm.research.tours.content.controls.TextPresentationGroup;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;
import com.ibm.research.tours.content.url.ResourceURL;
import com.ibm.research.tours.content.url.delegates.IResourceTourEditorExtension;
import com.ibm.research.tours.content.url.delegates.IResourceTourUIExtension;
import com.ibm.research.tours.content.url.delegates.IURLTourElementDelegate;
import com.ibm.research.tours.content.url.delegates.ResourceURLTourElementDelegate;
import com.ibm.research.tours.fx.IHighlightEffect;
import com.ibm.research.tours.fx.controls.HighlightGroup;
import com.ibm.research.tours.fx.controls.MaximizeGroup;

public class ResourceWizardPage extends WizardPage
{	
	private ResourceURLTourElement fElement;
	private TextPresentationGroup fTextGroup;
	private MaximizeGroup fMaxGroup;
	private HighlightGroup fHighlightGroup;
	
	protected ResourceWizardPage(String pageName,String pagetTitle,String description) 
	{
		super(pageName);
		setTitle(pagetTitle);
		setDescription(description);
	}

	public void init(ResourceURLTourElement element)
	{
		fElement = element;
	}

	public void createControl(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 6;
		composite.setLayout(layout);
		
		fMaxGroup = new MaximizeGroup(fElement.getMaximixedHint());
		fMaxGroup.createComposite(composite,"View state","Maximize view when opened");
		
		boolean highlighted = fElement.getHighlightEffect()!=null;
		IHighlightEffect[] supported = fElement.getSupportedHighlightEffects();
		IHighlightEffect selected = fElement.getHighlightEffect(); // null defaults to the first element in the supported array
		
		fHighlightGroup = new HighlightGroup(highlighted,supported,selected);
		fHighlightGroup.createComposite(composite);
		addExtension(composite);
		setControl(composite);
	}

	/**
	 * Searches for an extension to the given resource tour element, and adds the info to the
	 * composite.
	 * @param composite
	 */
	private void addExtension(Composite parent) {
		IURLTourElementDelegate delegate = fElement.getDelegate();
		if (delegate instanceof ResourceURLTourElementDelegate) {
			ResourceURLTourElementDelegate resourceDelegate = (ResourceURLTourElementDelegate) delegate;
			ResourceURL url = (ResourceURL) resourceDelegate.getUrl();
			IResource resource = url.getResource();
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				IEditorDescriptor editor = IDE.getDefaultEditor(file);
				String fileExtension = file.getFileExtension();
				IResourceTourEditorExtension extension = resourceDelegate.getEditorExtension(editor.getId(), fileExtension);
				IResourceTourUIExtension uiExtension = resourceDelegate.getUIExtension(editor.getId(), fileExtension);
				if (extension != null && uiExtension != null) {
					Group uiGroup = new Group(parent, SWT.NONE);
					uiGroup.setLayout(new GridLayout());
					uiGroup.setText(uiExtension.getText());
					Composite composite = uiExtension.createContents(uiGroup, extension);
					composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				}
			}
		}
		
	}

	public TextPresentationGroup getTextPresentationGroup() {
		return fTextGroup;
	}

	public MaximizeGroup getMaximizeGroup() {
		return fMaxGroup;
	}


	public HighlightGroup getHighlightGroup() {
		return fHighlightGroup;
	}

}
