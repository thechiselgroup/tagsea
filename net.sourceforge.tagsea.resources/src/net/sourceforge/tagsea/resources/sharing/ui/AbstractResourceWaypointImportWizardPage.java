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
package net.sourceforge.tagsea.resources.sharing.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.WaypointMatch;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointProxyDescriptor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;

/**
 * Generic wizard page for importing resource waypoints has functionality for
 * discovering differences between a resource waypoint descriptor and a waypoint
 * in the workpsace.
 * @author Del Myers
 *
 */
public abstract class AbstractResourceWaypointImportWizardPage extends WizardPage {

	private HashMap<IWaypointIdentifier, Set<String>> attributeConflicts;
	private HashMap<IWaypointIdentifier, Boolean> conflictingTags;
	private HashSet<IWaypointIdentifier> checkedWaypoints;
	private HashMap<IWaypointIdentifier, IWaypoint> matchingWaypoints;
	
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected AbstractResourceWaypointImportWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		attributeConflicts = new HashMap<IWaypointIdentifier, Set<String>>();
		checkedWaypoints = new HashSet<IWaypointIdentifier>();
		matchingWaypoints = new HashMap<IWaypointIdentifier, IWaypoint>();
		conflictingTags = new HashMap<IWaypointIdentifier, Boolean>();
	}
	
	/**
	 * Looks to find a matching waypoint for the given descriptor in the workspace. If one doesn't exist,
	 * null is returned. If it does exist, then a set containing the names of the attributes that conflict
	 * are returned.
	 * @param descriptor the descriptor to check.
	 * @param ignoreTextData whether or not line numbers and character offsets should be ignored.
	 * @param ignoreRevision whether or not revisions should be ignored.
	 * @return
	 */
	protected Set<String> findConflictingAttributes(IResourceWaypointDescriptor descriptor, boolean ignoreTextData, boolean ignoreRevision) {
		ResourceWaypointIdentifier id = new ResourceWaypointIdentifier(descriptor);
		if (checkedWaypoints.contains(id)) {
			return attributeConflicts.get(id);
		}
		IWaypoint wp = getWaypointForId(id);
		checkedWaypoints.add(id);
		if (wp != null) {
			Set<String> conflicts = new TreeSet<String>();
			String[] atts = descriptor.getAttributes();
			for (String att : atts) {
				if (ignoreRevision && att.equals(IResourceWaypointAttributes.ATTR_REVISION)) {
					continue;
				}
				if (ignoreTextData && (
					att.equals(IResourceWaypointAttributes.ATTR_LINE) ||
					att.equals(IResourceWaypointAttributes.ATTR_CHAR_END) ||
					att.equals(IResourceWaypointAttributes.ATTR_CHAR_START)
					)
				){
					continue;
				}
				if (att.equals(IResourceWaypointAttributes.ATTR_HANDLE_IDENTIFIER)) continue;
				Object thisValue = descriptor.getValue(att);
				Object thatValue = wp.getValue(att);
				if (thisValue == null && thatValue == null) {
					continue;
				} else if ((thisValue == null && thatValue != null) || (thisValue !=null && thatValue==null)) {
					conflicts.add(att);
				} else if (!thisValue.equals(thatValue)) {
					conflicts.add(att);
				}
			}
			attributeConflicts.put(id, conflicts);
			return conflicts;
		}
		return null;
	}
	
	/**
	 * Returns true if a waypoint with the same id as the given descriptor exists in the workspace
	 * and it has different tags than the descriptor, or if a waypoint with . False otherwise.
	 * @param descriptor
	 * @return
	 */
	protected boolean doTagsConflict(IResourceWaypointDescriptor descriptor) {
		ResourceWaypointIdentifier id = new ResourceWaypointIdentifier(descriptor);
		Boolean conflict = conflictingTags.get(id);
		if (conflict == null) {
			IWaypoint wp = getWaypointForId(id);
			if (wp != null) {
				conflict = !descriptor.getTags().equals(new ResourceWaypointProxyDescriptor(wp).getTags());
			} else {
				conflict = false;
			}
			conflictingTags.put(id, conflict);
		}
		return conflict;
	}
	
	
	
	protected IWaypoint getWaypointForId(IWaypointIdentifier id) {
		if (checkedWaypoints.contains(id)) {
			return matchingWaypoints.get(id);
		}
		checkedWaypoints.add(id);
		WaypointMatch[] matches = TagSEAPlugin.getDefault().locate(ResourceWaypointPlugin.WAYPOINT_ID, id.getAttributes(), id.getTagNames());
		if (matches != null && matches.length > 0) {
			matchingWaypoints.put(id, matches[0].getWaypoint());
			return matches[0].getWaypoint();
		}
		return null;
	}
	
	/**
	 * Merges the given waypoint descriptor with the attributes and tags for the local workspace waypoint
	 * if it exists so that the source descriptor's attributes are copied over the local waypoint's. If
	 * A local waypoint doesn't exist, then the source is returned. If the waypoint exists, the returned
	 * descriptor will be an instanceof ResourceWaypointProxyDescriptor and can be committed directly.
	 * @param target
	 * @param attributesToCopy
	 * @param copyTags
	 * @return
	 */
	protected IResourceWaypointDescriptor mergeDescriptor(IResourceWaypointDescriptor source, Set<String> attributesToCopy, boolean copyTags) {
		IWaypoint wp = getWaypointForId(new ResourceWaypointIdentifier(source));
		IMutableResourceWaypointDescriptor result;
		if (wp == null) {
			return source;
		} else {
			result = new ResourceWaypointProxyDescriptor(wp);
			for (String attr : attributesToCopy) {
				result.setValue(attr, source.getValue(attr));
			}
			if (copyTags) {
				result.setTags(source.getTags());
			}
		}
		return result;
	}
	
	/**
	 * Returns true if the page has conflicts with the waypoints in the workspace.
	 * @return
	 */
	protected boolean pageHasConflicts(boolean ignoreTextData, boolean ignoreRevision) {
		boolean conflict = false;
		for (IResourceWaypointDescriptor d : getDescriptors()) {
			conflict |= doTagsConflict(d);
			if (!conflict) {
				Set<String> attrs =findConflictingAttributes(d, ignoreTextData, ignoreRevision);
				conflict |= (attrs != null && attrs.size() > 0);
			}
			if (conflict)
				break;
		}
		return conflict;
	}
	
	/**
	 * Calculates the merged descriptors for this page.
	 * @return
	 */
	protected abstract IResourceWaypointDescriptor[] calculateMergedDescriptors();
	
	/**
	 * Gets the unmerged descriptors for this page.
	 * @return
	 */
	protected abstract IResourceWaypointDescriptor[] getDescriptors();

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	@Override
	public void dispose() {
		attributeConflicts.clear();
		checkedWaypoints.clear();
		conflictingTags.clear();
		matchingWaypoints.clear();
		super.dispose();
	}
	
	/**
	 * Returns a resource for the given descriptor or null if it does not exist.
	 * @param descriptor
	 */
	protected IResource getResourceForDescriptor(IResourceWaypointDescriptor descriptor) {
		return ResourcesPlugin.getWorkspace().getRoot().findMember(descriptor.getResource());
	}
	
	

}
