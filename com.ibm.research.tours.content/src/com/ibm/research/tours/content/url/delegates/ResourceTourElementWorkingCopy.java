package com.ibm.research.tours.content.url.delegates;

import org.eclipse.ui.IMemento;

/**
 * A working copy of a resource tour element. This is used for saving and loading extensions
 * to resource tour elements.
 * @author Del Myers
 *
 */
public class ResourceTourElementWorkingCopy {
	private IMemento memento;
	
	protected ResourceTourElementWorkingCopy(IMemento memento) {
		this.memento = memento;
	}
	
	public IMemento getMemento() {
		return memento;
	}
}
