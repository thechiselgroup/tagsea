package com.ibm.research.tours.content.url.delegates;

import org.eclipse.swt.widgets.Composite;


public interface IResourceTourUIExtension {
	
	/**
	 * Creates the contents, and initializes the ui from the given element. The element
	 * should be cast to the appropriate sub-type to initialize the values.
	 * @param parent
	 * @param element
	 * @return
	 */
	public Composite createContents(Composite parent, IResourceTourEditorExtension element);
	
	/**
	 * Applies the changes to the given element
	 * @param element
	 */
	public void applyChanges(IResourceTourEditorExtension element);

	/**
	 * Returns a short descriptive name for this extension, as it will appear in
	 * the wizard dialog. Must not be null.
	 * @return a short descriptive name for this extension, as it will appear in
	 * the wizard dialog. Must not be null.
	 */
	public String getText();

}
