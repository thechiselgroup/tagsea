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

import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.IFileBufferListener;
import org.eclipse.core.runtime.IPath;

/**
 * A listener for creation/deletion of file buffers.
 * @author Del Myers
 *
 */
abstract class FileBufferAdapter implements IFileBufferListener {
	
		

	public void bufferContentAboutToBeReplaced(IFileBuffer buffer) {
		//taken care of by the resource listeners
	}

	public void bufferContentReplaced(IFileBuffer buffer) {
		//taken care of by the resource listeners
	}

	public void dirtyStateChanged(IFileBuffer buffer, boolean isDirty) {
		//not interested
	}

	public void stateChangeFailed(IFileBuffer buffer) {
		//not interested
	}

	public void stateChanging(IFileBuffer buffer) {
		//not interested
	}

	public void stateValidationChanged(IFileBuffer buffer,
			boolean isStateValidated) {
	}

	public void underlyingFileDeleted(IFileBuffer buffer) {
		//this is taken care of by the resource listeners
	}

	public void underlyingFileMoved(IFileBuffer buffer, IPath path) {
		//this is taken care of by the resource listeners
	}

}
