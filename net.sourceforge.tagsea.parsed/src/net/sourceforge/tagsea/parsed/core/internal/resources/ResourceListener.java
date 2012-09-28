/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.parsed.core.internal.resources;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.internal.operations.CleanFilesOperation;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * Listens for changes in the workspace to discover files that need to be reparsed.
 * @author Del Myers
 */

public class ResourceListener implements IResourceChangeListener {
	
 private class ChangeGatherer implements IResourceDeltaVisitor {
		List<IFile> changes;
		Map<IPath, IFile> moves;

		/**
		 * 
		 */
		public ChangeGatherer() {
			changes = new LinkedList<IFile>();
			moves = new HashMap<IPath, IFile>();
		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (delta.getResource() instanceof IContainer) {
				if (delta.getResource() instanceof IProject) {
					if (delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.OPEN) != 0) {
						//clean the project.
						if (delta.getResource().isAccessible()) {
							ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().clean(false);
							return false;
						}
					}
				}
				return true;
			}
			int kind = delta.getKind();
			if (kind != IResourceDelta.CHANGED && kind != IResourceDelta.ADDED && kind != IResourceDelta.REMOVED) return false;
			if (kind == IResourceDelta.CHANGED) {
				if (((delta.getFlags()&IResourceDelta.CONTENT) == 0)) return false; //don't care about marker changes.
			}
			if (delta.getResource() instanceof IFile) {
				IFile file = (IFile) delta.getResource();
				//check for matching definitions.
				IParsedWaypointDefinition[] defs = 
					ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getMatchingDefinitions(file);
				if (defs != null && defs.length > 0) { //$NON-NLS-1$
					if (kind == IResourceDelta.CHANGED) {
						changes.add(file);
					} else if (kind == IResourceDelta.ADDED) {
						if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
							moves.put(delta.getMovedFromPath(), file);
						} else {
							changes.add(file);
						}
					} else if (kind == IResourceDelta.REMOVED) {
						if ((delta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
							//process the deleted file.
							changes.add(file);
						}
					}
				}
			}
				
			return false;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		ChangeGatherer gatherer = new ChangeGatherer();
		try {
			if (event.getDelta() == null) return;
			event.getDelta().accept(gatherer);
		} catch (CoreException e) {
		}
		if (gatherer.changes.size() > 0)
			TagSEAPlugin.run(new CleanFilesOperation(gatherer.changes), false);		
 	}

}
