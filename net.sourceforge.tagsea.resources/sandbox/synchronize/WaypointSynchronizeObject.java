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
package net.sourceforge.tagsea.resources.synchronize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointDelegate;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointProxyDescriptor;
import net.sourceforge.tagsea.resources.waypoints.operations.CreateWaypointOperation;
import net.sourceforge.tagsea.resources.waypoints.xml.ResourceWaypointDescriptor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.xml.sax.SAXException;

public class WaypointSynchronizeObject {
	private IResourceWaypointDescriptor local;
	private IResourceWaypointDescriptor remote;
	private IProject project;
	private long latestRevision;
	
	public final static int NEW_IN = 0;
	public final static int NEW_OUT = 1;
	public final static int REMOVE_IN = 2;
	public final static int REMOVE_OUT = 3;
	public final static int SYNCH_IN = 4;
	public final static int SYNCH_OUT = 5;
	public final static int CONFLICT = 6;
	public final static int EQUAL = 7;
	
	public class ValueComparison {
		public static final String TAGS_ATTR = "net.sourceforge.tagsea.resources.synchronize.tags.attribute";
		private String attribute;

		public ValueComparison(String attribute) {
			this.attribute = attribute;
		}
		
		public WaypointSynchronizeObject getSync() {
			return WaypointSynchronizeObject.this;
		}
		
		public Object getLocalValue() {
			if (getLocal() == null) {
				return null;
			}
			return getValue(getLocal());
		}
		
		public Object getRemoteValue() {
			if (getRemote() == null) {
				return null;
			}
			return getValue(getRemote());
		}
		
		public void copyLeft() {
			if (local == null) {
				IMutableResourceWaypointDescriptor desc = new ResourceWaypointDescriptor();
				desc.setRevision(latestRevision+"");
				local = desc;
			}
			if (local instanceof IMutableResourceWaypointDescriptor) {
				IMutableResourceWaypointDescriptor desc = (IMutableResourceWaypointDescriptor) local;
				copyValue(getRemote(), desc);
			}
		}
		
		public void copyRight() {
			if (remote == null) {
				IMutableResourceWaypointDescriptor desc = new ResourceWaypointDescriptor();
				desc.setRevision(latestRevision+"");
				remote = desc;
			}
			if (remote instanceof IMutableResourceWaypointDescriptor) {
				IMutableResourceWaypointDescriptor desc = (IMutableResourceWaypointDescriptor) remote;
				copyValue(getLocal(), desc);
			}
		}

		/**
		 * @param remote
		 * @param desc
		 */
		private void copyValue(IResourceWaypointDescriptor source, IMutableResourceWaypointDescriptor dest) {
			if (IResourceWaypointAttributes.ATTR_AUTHOR.equals(attribute)) {
				dest.setAuthor(source.getAuthor());
			} else if (IResourceWaypointAttributes.ATTR_CHAR_END.equals(attribute)) {
				dest.setCharEnd(source.getCharEnd());
			} else if (IResourceWaypointAttributes.ATTR_CHAR_START.equals(attribute)) {
				dest.setCharStart(source.getCharStart());
			} else if (IResourceWaypointAttributes.ATTR_DATE.equals(attribute)) {
				dest.setDate(source.getDate());
			} else if (IResourceWaypointAttributes.ATTR_LINE.equals(attribute)) {
				dest.setLine(source.getLine());
			} else if (IResourceWaypointAttributes.ATTR_MESSAGE.equals(attribute)) {
				dest.setText(source.getText());
			} else if (IResourceWaypointAttributes.ATTR_RESOURCE.equals(attribute)) {
				dest.setResource(source.getResource());
			} else if (IResourceWaypointAttributes.ATTR_STAMP.equals(attribute)) {
				dest.setStamp(source.getStamp());
			} else if (TAGS_ATTR.equals(attribute)) {
				dest.setTags(source.getTags());
			}
			//update the revision of the destination to the latest revision value of the two.
			Date sourceDate = new Date(Long.parseLong(source.getRevision()));
			Date destDate = new Date(Long.parseLong(dest.getRevision()));
			if (destDate.before(sourceDate)) {
				dest.setRevision(source.getRevision());
			}
		}

		/**
		 * @param local
		 * @return
		 */
		private Object getValue(IResourceWaypointDescriptor desc) {
			if (IResourceWaypointAttributes.ATTR_AUTHOR.equals(attribute)) {
				return desc.getAuthor();
			} else if (IResourceWaypointAttributes.ATTR_CHAR_END.equals(attribute)) {
				return desc.getCharEnd();
			} else if (IResourceWaypointAttributes.ATTR_CHAR_START.equals(attribute)) {
				return desc.getCharStart();
			} else if (IResourceWaypointAttributes.ATTR_DATE.equals(attribute)) {
				return desc.getDate();
			} else if (IResourceWaypointAttributes.ATTR_LINE.equals(attribute)) {
				return desc.getLine();
			} else if (IResourceWaypointAttributes.ATTR_MESSAGE.equals(attribute)) {
				return desc.getText();
			} else if (IResourceWaypointAttributes.ATTR_RESOURCE.equals(attribute)) {
				return desc.getResource();
			} else if (IResourceWaypointAttributes.ATTR_REVISION.equals(attribute)) {
				return desc.getRevision();
			} else if (IResourceWaypointAttributes.ATTR_STAMP.equals(attribute)) {
				return desc.getStamp();
			} else if (TAGS_ATTR.equals(attribute)) {
				return desc.getTags();
			}
			return null;
		}
		
		/**
		 * Returns the attribute that is being compared.
		 * @return the attribute
		 */
		public String getAttribute() {
			return attribute;
		}
		
		/**
		 * Returns true iff this comparison object compares tags.
		 * @return
		 */
		public boolean comparesTags() {
			return TAGS_ATTR.equals(getAttribute());
		}
		
	}
	
	
	
	public WaypointSynchronizeObject(IProject project, long workspaceRevision, IResourceWaypointDescriptor local, IResourceWaypointDescriptor remote) {
		this.project = project;
		this.local = local;
		this.remote = remote;
		this.latestRevision = workspaceRevision;
	}
	
	/**
	 * @return the local
	 */
	public IResourceWaypointDescriptor getLocal() {
		return local;
	}
	
	/**
	 * @return the remote
	 */
	public IResourceWaypointDescriptor getRemote() {
		return remote;
	}
	
	/**
	 * @return the project
	 */
	public IProject getProject() {
		return project;
	}
	
	public long getWorkspaceRevision() {
		return latestRevision;
	}
	
	public int getKind() {
		Date localDate = null;
		Date remoteDate = null;
		Date workspaceDate = new Date(latestRevision);
		if (getLocal() != null) {
			long lr = Long.parseLong(getLocal().getRevision());
			localDate = new Date(lr);
		}
		if (getRemote() != null) {
			long rr = Long.parseLong(getRemote().getRevision());
			remoteDate = new Date(rr);
		}
		if (remoteDate == null) {
			if (localDate.before(workspaceDate)) {
				return REMOVE_IN;
			}
			return NEW_OUT;
		} else if (localDate == null) {
			if (remoteDate.before(workspaceDate)) {
				return REMOVE_OUT;
			}
			return NEW_IN;
		} else {
			if (remoteDate.before(localDate)) {
				return SYNCH_OUT;
			} else if (localDate.before(remoteDate)) {
				return SYNCH_IN;
			} else {
				boolean same = getLocal().getAuthor().equals(getRemote().getAuthor());
				same &= getLocal().getCharEnd()==(getRemote().getCharEnd());
				same &= getLocal().getCharStart()==getRemote().getCharStart();
				same &= getLocal().getDate().equals(getRemote().getDate());
				same &= getLocal().getLine()==getRemote().getLine();
				same &= getLocal().getResource().equals(getRemote().getResource());
				same &= getLocal().getText().equals(getRemote().getText());
				same &= getLocal().getTags().equals(getRemote().getTags());
				if (same) {
					return EQUAL;
				}
			}
		}
		return CONFLICT;
	}

	/**
	 * Copies all the values from the remote object to the local one.
	 */
	public void checkout() {
		if (local == null) {
			IMutableResourceWaypointDescriptor desc = new ResourceWaypointDescriptor();
			desc.setRevision(latestRevision+"");
			local = desc;
		}
		copyValues(getRemote(), (IMutableResourceWaypointDescriptor) getLocal());
	}
	
	/**
	 * Copies all the values from the local object to the remote one.
	 *
	 */
	public void checkIn() {
		if (remote == null) {
			IMutableResourceWaypointDescriptor desc = new ResourceWaypointDescriptor();
			desc.setRevision(latestRevision+"");
			remote = desc;
		}
		copyValues(getLocal(), (IMutableResourceWaypointDescriptor) getRemote());
	}
	
	/**
	 * Commits the changes.
	 *
	 */
	public IStatus commit() {
		if (getRemote() == null || getLocal() == null) {
			return new Status(IStatus.ERROR, ResourceWaypointPlugin.PLUGIN_ID, IStatus.ERROR, "Null value", null);
		}
		if (getLocal() instanceof ResourceWaypointProxyDescriptor) {
			((ResourceWaypointProxyDescriptor)getLocal()).commit();
		} else {
			CreateWaypointOperation createOp = new CreateWaypointOperation(getLocal());
			//create a new waypoint
			TagSEAPlugin.run(createOp, true);
			if (!createOp.getStatus().isOK()) {
				return createOp.getStatus();
			}
		}
		List<IResourceWaypointDescriptor> descriptors  = new ArrayList<IResourceWaypointDescriptor>(1);
		descriptors.add(getRemote());
		Exception ex = null;
		try {
			WaypointSynchronizerHelp.INSTANCE.updateSynchronizeInfo(project, descriptors, new NullProgressMonitor());
		} catch (IOException e) {
			ResourceWaypointPlugin.getDefault().log(e);
			ex = e;
		} catch (SAXException e) {
			ResourceWaypointPlugin.getDefault().log(e);
			ex = e;
		}
		if (ex != null) {
			String message = ex.getMessage();
			if (message == null) {
				message = "";
			}
			return new Status(
				IStatus.ERROR, 
				ResourceWaypointPlugin.PLUGIN_ID,
				IStatus.ERROR,
				message,
				ex
			);
		}
		return Status.OK_STATUS;
	}
	
	/**
	 * Copies all of the values from the source to the destination.
	 * @param source
	 * @param dest
	 */
	private void copyValues(IResourceWaypointDescriptor source, IMutableResourceWaypointDescriptor dest) {
		dest.setText(source.getText());
		dest.setAuthor(source.getAuthor());
		dest.setCharEnd(source.getCharEnd());
		dest.setCharStart(source.getCharStart());
		dest.setDate(source.getDate());
		dest.setLine(source.getLine());
		dest.setResource(source.getResource());
		dest.setStamp(source.getStamp());
		dest.setTags(source.getTags());
//		update the revision of the destination to the latest revision value of the two.
		Date sourceDate = new Date(Long.parseLong(source.getRevision()));
		Date destDate = new Date(Long.parseLong(dest.getRevision()));
		if (destDate.before(sourceDate)) {
			dest.setRevision(source.getRevision());
		}
	}
	
	/**
	 * Returns a comparison object for the specified attribute. See IResourceWaypointAttributes for the
	 * available attributes.
	 * @param attribute
	 * @return
	 */
	public ValueComparison getValueComparison(String attribute) {
		return new ValueComparison(attribute);
	}
	
	/**
	 * Returns a comparison for the tags in the local and remote copies of the waypoint.
	 * @return a comparison for the tags in the local and remote copies of the waypoint.
	 */
	public ValueComparison getTagsComparison() {
		return new ValueComparison(ValueComparison.TAGS_ATTR);
	}
}