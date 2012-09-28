/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.model.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAModelException;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITagChangeEvent;
import net.sourceforge.tagsea.core.ITagChangeListener;
import net.sourceforge.tagsea.core.ITagSEAOperationStateListener;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.IWaypointChangeListener;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.WaypointDelta;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;

/**
 * A class that supports all the changes that occur in the waypoint and 
 * @author Del Myers
 */

public class TagSEAChangeSupport {
	
		
	public static final ISchedulingRule TAGSEA_SCHEDULING = new TagSEAChangeRule();
	
	/**
	 * The number of nested operations or locks currently held due to a run or a synched run.
	 */
	private int blockingCount = 0;
	
	private static class TagSEAChangeRule implements ISchedulingRule {
		public boolean contains(ISchedulingRule rule) {
			if (rule instanceof TagSEAChangeRule)
				return true;
			if (rule instanceof MultiRule) {
				MultiRule multi = (MultiRule) rule;
				ISchedulingRule[] children = multi.getChildren();
				for (int i = 0; i < children.length; i++)
					if (!contains(children[i]))
						return false;
				return true;
			}
			return false;
//			//we don't care what runs inside a TagSEAChangeRule, just that it doesn't run beside
//			//another TagSEAChangeRule
//			return true;
		}
		public boolean isConflicting(ISchedulingRule rule) {
			return (rule instanceof TagSEAChangeRule);
		}
	}
	
	/**
	 * 
	 * @author Del Myers
	 */
	
	private final class OperationRunner  extends Job{
		/**
		 * 
		 */
		private final TagSEAOperation op;
		//private Object waiter = new Object();
		private boolean wait;
		//private boolean isWaiting;

		/**
		 * @param op
		 */
		private OperationRunner(TagSEAOperation op) {
			super(op.getName());
			this.op = op;
		}

		public IStatus run(IProgressMonitor monitor)  {
			monitor.beginTask(Messages.getString("TagSEAChangeSupport.opName"), 1101); //$NON-NLS-1$
			SubProgressMonitor operationMonitor = new SubProgressMonitor(monitor, 1000);
			SubProgressMonitor postChangeMonitor = new SubProgressMonitor(monitor, 100);
			monitor.subTask(Messages.getString("TagSEAChangeSupport.waiting")); //$NON-NLS-1$
			op.operationWaiting();
			postOperationStateChanged(op);
			IStatus result = Status.OK_STATUS;
			synchronized (fThreadDeltaMap) {
				blockingCount++;
				monitor.worked(1);
				monitor.subTask(Messages.getString("TagSEAChangeSupport.running")); //$NON-NLS-1$
//				boolean subOp = fThreadDeltaMap.containsKey(Thread.currentThread());
//				if (!subOp)
					fThreadDeltaMap.put(Thread.currentThread(), new TagSEADeltaInfo(operationMonitor));
				MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
				try {
					op.operationStarted();
					postOperationStateChanged(op);
					status.merge(op.run(operationMonitor));
					TagSEADeltaInfo deltaInfo = fThreadDeltaMap.get(Thread.currentThread());
					status.merge(deltaInfo.getSubOpStatus());
				} catch (InvocationTargetException e) {
					if (e.getCause() instanceof CoreException) {
						return (((CoreException)e.getCause()).getStatus());
					}
					e.printStackTrace();
				} finally {
					//must make sure to post all changes.
					op.operationQuiting();
					postOperationStateChanged(op);
					operationMonitor.done();
					postChangeMonitor.beginTask(Messages.getString("TagSEAChangeSupport.posting"), 100); //$NON-NLS-1$
					TagSEADeltaInfo deltaInfo = fThreadDeltaMap.get(Thread.currentThread());

					ITagChangeEvent[] tagChanges = deltaInfo.getCompressedTagChanges();
					IStatus tagStatus = Status.OK_STATUS;
					if (tagChanges.length > 0) {
						tagStatus = fireTagChanges(deltaInfo.getCompressedTagChanges());
					}
					IWaypointChangeEvent[] waypointChanges = deltaInfo.getCompressedWaypointChanges();
					IStatus waypointStatus = Status.OK_STATUS;
					if (waypointChanges.length > 0) {
						waypointStatus = fireWaypointChanges(deltaInfo.getCompressedWaypointChanges());
					}
					status.merge(tagStatus);
					status.merge(waypointStatus);
					if (status.getSeverity() != IStatus.OK) {
						result = status;
					}
					fThreadDeltaMap.remove(Thread.currentThread());	
					postChangeMonitor.done();

					monitor.done();

					op.operationDone();
					postOperationStateChanged(op);
				}
				blockingCount--;
			}
			return result;
		}
		
		private void runAsSubOperation() {
			TagSEADeltaInfo info = fThreadDeltaMap.get(Thread.currentThread());
			Job parent = getJobManager().currentJob();
			
			if (parent != null) {
				ISchedulingRule parentRule = parent.getRule();
				if (op.getRule() != null) {
					ISchedulingRule thisRule = op.getRule();
					if (parent instanceof OperationRunner) {
						//should always be true.
						parentRule = ((OperationRunner)parent).op.getRule();
					}
					if (parentRule != null) {
						if (parent.getRule().contains(thisRule)) {
							//we have to die because a deadlock could occur
							info.getSubOpStatus().merge(new Status(Status.ERROR, TagSEAPlugin.PLUGIN_ID, op.getName() + " could not run because of scheduling conflicts."));
							return;
						}
					} 
				}
			}
			IProgressMonitor monitor = info.getMonitor();
			SubProgressMonitor operationMonitor = new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
			op.operationWaiting();
			postOperationStateChanged(op);
			synchronized (fThreadDeltaMap) {
				blockingCount++;
				operationMonitor.worked(1);
				operationMonitor.subTask(Messages.getString("TagSEAChangeSupport.runOp") + getName() + "..."); //$NON-NLS-1$ //$NON-NLS-2$
				MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
				try {
					op.operationStarted();
					postOperationStateChanged(op);
					status.merge(op.run(operationMonitor));
					TagSEADeltaInfo deltaInfo = fThreadDeltaMap.get(Thread.currentThread());
					deltaInfo.getSubOpStatus().merge(status);
				} catch (InvocationTargetException e) {
					TagSEAPlugin.getDefault().log(e);
				} finally {
					//must make sure to post all changes.
					op.operationQuiting();
					postOperationStateChanged(op);
					operationMonitor.done();
					op.operationDone();
					postOperationStateChanged(op);
				}
				blockingCount--;
			}			
		}
		
		/**
		 * Schedules the job and blocks until it is finished.
		 *
		 */
		void start(boolean wait) {
				if (fThreadDeltaMap.size() > 1) {
					throw new IllegalStateException(Messages.getString("TagSEAChangeSupport.error.tooManyThreads")); //$NON-NLS-1$
				}
				this.wait = wait;
				//this.isWaiting = false;
				op.operationQueued();
				postOperationStateChanged(op);
				if (fThreadDeltaMap.containsKey(Thread.currentThread())) {
					if (wait == true) {
						runAsSubOperation();
						return;
					}
				}
			if (op.getRule() != null) {
				setRule(new MultiRule(new ISchedulingRule[]{op.getRule(), TAGSEA_SCHEDULING}));
//				setRule(op.getRule());
			} else {
				setRule(TAGSEA_SCHEDULING);
			}
			schedule();
			if (this.wait) {
				try {
					join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		
		@Override
		public boolean belongsTo(Object family) {
			return (family == TagSEAPlugin.getDefault());
		}
	}

	private class TagSEADeltaInfo {
		private LinkedList<IWaypointChangeEvent> waypointChanges;
		private LinkedList<ITagChangeEvent> tagChanges;
		private boolean compressed;
		private MultiStatus subOpStatus;
		private IProgressMonitor operationMonitor;
		/**
		 * 
		 * @param operationMonitor the monitor that is used to run the operation. Made available so that
		 *  sub operations can be run in the same monitor, and status can be shown about them.
		 */
		public TagSEADeltaInfo(IProgressMonitor operationMonitor) {
			waypointChanges = new LinkedList<IWaypointChangeEvent>();
			tagChanges = new LinkedList<ITagChangeEvent>();
			compressed = true;
			subOpStatus = new MultiStatus(TagSEAPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
			this.operationMonitor = operationMonitor;
		}
		
		/**
		 * @return
		 */
		public IProgressMonitor getMonitor() {
			return operationMonitor;
		}

		public void addWaypointChange(IWaypointChangeEvent event) {
			waypointChanges.addLast(event);
			compressed = false;
		}
		
		public void addTagChange(ITagChangeEvent event) {
			tagChanges.addLast(event);
			compressed = false;
		}
		
		
		private void compress() {
			compressWaypoints();
			compressTags();
			compressed = true;
		}
		
		public IWaypointChangeEvent[] getCompressedWaypointChanges() {
			if (!compressed) {
				compress();
			}
			return waypointChanges.toArray(new IWaypointChangeEvent[waypointChanges.size()]);
		}
		
		public ITagChangeEvent[] getCompressedTagChanges() {
			if (!compressed) {
				compress();
			}
			return tagChanges.toArray(new ITagChangeEvent[tagChanges.size()]);
		}
		
		public MultiStatus getSubOpStatus() {
			return subOpStatus;
		}

		private void compressTags() {
			//first iterate backwards through the list to remove any references
			//to tags that are later deleted. Also remove delete events that 
			//occur after a create event in the same operation.
			LinkedList<ITagChangeEvent> toBeRemoved = new LinkedList<ITagChangeEvent>();
			LinkedList<ITag> deleted = new LinkedList<ITag>();
			LinkedList<ITagChangeEvent> deletedEvents = new LinkedList<ITagChangeEvent>();
			for (int i = tagChanges.size()-1; i >= 0; i--) {
				ITagChangeEvent currentEvent = tagChanges.get(i);
				if (currentEvent.getType() == ITagChangeEvent.DELETED) {
					deleted.add(currentEvent.getTag());
					deletedEvents.add(currentEvent);
				} else if (deleted.contains(currentEvent.getTag())) {
					//remove the event
					toBeRemoved.add(currentEvent);
					if (currentEvent.getType() == ITagChangeEvent.NEW) {
						//remove the later delte event that comes from this new event.
						ITagChangeEvent toDelete = null;
						for (ITagChangeEvent delete : deletedEvents) {
							if (delete.getTag() == currentEvent.getTag()) {
								toDelete = delete;
							}
						}
						if (toDelete != null) {
							deletedEvents.remove(toDelete);
							toBeRemoved.add(toDelete);
						}
					}
				}
			}
			
			tagChanges.removeAll(toBeRemoved);
			toBeRemoved.clear();
			deleted.clear();
			deletedEvents.clear();
			//consolodate all the tag name changes into single changes on a tag.
			HashMap<ITag, String> oldNameMap = new HashMap<ITag, String>();
			for (ITagChangeEvent currentEvent : tagChanges) {
				if (currentEvent.getType() == ITagChangeEvent.NAME) {
					String old = oldNameMap.get(currentEvent.getTag());
					if (old == null) {
						oldNameMap.put(currentEvent.getTag(), currentEvent.getOldName());
					} else {
						toBeRemoved.add(currentEvent);
					}
				}
			}
			tagChanges.removeAll(toBeRemoved);
			for (ITag currentTag : oldNameMap.keySet()) {
				//create the new event
				String oldName = oldNameMap.get(currentTag);
				if (!oldName.equals(currentTag.getName())) {
					tagChanges.add(TagChangeEvent.createNameEvent(currentTag, oldName));
				}
			}
			toBeRemoved.clear();
			//finally: consolodate all of the waypoint changes
			HashMap<ITag, IWaypoint[]> oldWaypoints = new HashMap<ITag, IWaypoint[]>();
			for (ITagChangeEvent currentEvent : tagChanges) {
				if (currentEvent.getType() == ITagChangeEvent.WAYPOINTS) {
					if (oldWaypoints.containsKey(currentEvent.getTag())) {
						toBeRemoved.add(currentEvent);
					} else {
						oldWaypoints.put(currentEvent.getTag(), currentEvent.getOldWaypoints());
					}
				}
			}
			tagChanges.removeAll(toBeRemoved);
			for (ITag currentTag : oldWaypoints.keySet()) {
				IWaypoint[] old = oldWaypoints.get(currentTag);
				tagChanges.add(TagChangeEvent.createWaypointEvent(currentTag, old));
			}
		}
		
		/**
		 * Compresses the waypoint changes into more succinct information.
		 */
		private void compressWaypoints() {
			//first iterate backwards through the list to remove any references
			//to waypoints that are later deleted. It won't be necessary to publish
			//them because they'll just be made obsolete later.
			LinkedList<IWaypointChangeEvent> toBeRemoved = new LinkedList<IWaypointChangeEvent>();
			LinkedList<IWaypoint> deleted = new LinkedList<IWaypoint>();
			LinkedList<IWaypointChangeEvent> deletedEvents = new LinkedList<IWaypointChangeEvent>();
			for (int i = waypointChanges.size()-1; i >= 0; i--) {
				IWaypointChangeEvent currentEvent = waypointChanges.get(i);
				if (currentEvent.getType() == IWaypointChangeEvent.DELETE) {
					deleted.add(currentEvent.getWaypoint());
					deletedEvents.add(currentEvent);
				} else if (deleted.contains(currentEvent.getWaypoint())) {
					toBeRemoved.add(currentEvent);
					if (currentEvent.getType() == IWaypointChangeEvent.NEW) {
						//there is no need to post a delete event when it was deleted
						//from a new one created in the same operation.
						IWaypointChangeEvent toDelete = null;
						for (IWaypointChangeEvent delete : deletedEvents) {
							if (delete.getWaypoint() == currentEvent.getWaypoint()) {
								toDelete = delete;
								break;
							}
						}
						if (toDelete != null) {
							deletedEvents.remove(toDelete);
							toBeRemoved.add(toDelete);
						}
					}
				}
			}
			//remove all the obsolete changes.
			waypointChanges.removeAll(toBeRemoved);
			toBeRemoved.clear();
			deleted.clear();
			
			//now iterate forward, accumulating and condensing all changes on waypoints.
			
			//a map containing all of the oldest values for the waypoint. The waypoint itself
			//will contain the newest values.
			HashMap<IWaypoint, Map<String, Object>> changeMap = 
				new HashMap<IWaypoint, Map<String, Object>>();
			for (IWaypointChangeEvent event : waypointChanges) {
				if (event.getType() == IWaypointChangeEvent.CHANGE) {
					Map<String, Object> changes = changeMap.get(event.getWaypoint());
					if (changes == null) {
						changes = new HashMap<String, Object>();
						changeMap.put(event.getWaypoint(), changes);
					}
					String[] attrs = event.getChangedAttributes();
					for (String attr : attrs) {
						if (!changes.containsKey(attr)) {
							changes.put(attr, event.getOldValue(attr));
						}
					}
					toBeRemoved.add(event);
				}
			}
			//remove all the stale events
			waypointChanges.removeAll(toBeRemoved);
			//create new change events for the changes
			for (IWaypoint waypoint : changeMap.keySet()) {
				Map<String, Object> oldValues = changeMap.get(waypoint);
				//create a new event
				waypointChanges.add(WaypointChangeEvent.createChangeEvent(waypoint, oldValues));
			}
			
			toBeRemoved.clear();
			changeMap.clear();
			HashMap<IWaypoint, String[]> oldTagMap = new HashMap<IWaypoint, String[]>();
			//finally compress all the tag change information into single events.
			for (IWaypointChangeEvent event : waypointChanges) {
				if (event.getType() == IWaypointChangeEvent.TAGS_CHANGED) {
					if (!oldTagMap.containsKey(event.getWaypoint())) {
						oldTagMap.put(event.getWaypoint(), event.getOldTags());
					}
					toBeRemoved.add(event);
				} else if (event.getType() == IWaypointChangeEvent.TAG_NAME_CHANGED) {
					//make sure that the tag changes have the right names.
					String[] oldTags = oldTagMap.get(event.getWaypoint());
					if (oldTags != null) {
						String oldName = event.getOldTagName();
						for (int i = 0; i < oldTags.length; i++) {
							if (oldTags[i].equals(oldName)) {
								oldTags[i] = event.getNewTagName();
							}
						}
					}
					
				}
			}
			waypointChanges.removeAll(toBeRemoved);
			for (IWaypoint waypoint : oldTagMap.keySet()) {
				String[] oldTags = oldTagMap.get(waypoint);
				waypointChanges.add(WaypointChangeEvent.createTagChangeEvent(waypoint, oldTags));
			}
			
		}
		
	};
	/**
	 * A map of threads to delta info. This is used so that within
	 * a single ITagSEAOperation, changes can be accumulated and only
	 * the minumum number of changes will be posted.
	 */
	private Map<Thread, TagSEADeltaInfo> fThreadDeltaMap;
	
	private LinkedList<IWaypointChangeListener> waypointListeners;
	
	private TreeMap<String, LinkedList<IWaypointChangeListener>> typedListeners;
	
	private LinkedList<ITagChangeListener> tagListeners;
	private LinkedList<ITagSEAOperationStateListener> stateListeners;
	
	/**
	 * There is only one global instance for the whole platform.
	 */
	public static final TagSEAChangeSupport INSTANCE = new TagSEAChangeSupport();
	
	private TagSEAChangeSupport() {
		fThreadDeltaMap = new HashMap<Thread, TagSEADeltaInfo>();
		waypointListeners = new LinkedList<IWaypointChangeListener>();
		tagListeners = new LinkedList<ITagChangeListener>();
		typedListeners = new TreeMap<String, LinkedList<IWaypointChangeListener>>();
		stateListeners = new LinkedList<ITagSEAOperationStateListener>();
	}
	
	public void addWaypointChangeListener(IWaypointChangeListener listener) {
		synchronized (waypointListeners) {
			if (waypointListeners.contains(listener)) return;
			waypointListeners.add(listener);
		}
	}
	
	public void addWaypointChangeListener(IWaypointChangeListener listener, String type) {
		synchronized (typedListeners) {
			LinkedList<IWaypointChangeListener> list = typedListeners.get(type);
			if (list == null) {
				list = new LinkedList<IWaypointChangeListener>();
				typedListeners.put(type, list);
			}
			if (!list.contains(listener))
				list.add(listener);
		}
	}
	
	public void removeWaypointChangeListener(IWaypointChangeListener listener) {
		synchronized (waypointListeners) {
			waypointListeners.remove(listener);
		}
		synchronized (typedListeners) {
			LinkedList<String> toRemove = new LinkedList<String>();
			for (String type : typedListeners.keySet()) {
				LinkedList<IWaypointChangeListener> list = typedListeners.get(type);
				list.remove(listener);
				if (list.size() == 0) {
					toRemove.add(type);
				}
			}
			for (String type : toRemove) {
				typedListeners.remove(type);
			}
		}
	}
	
	public void addTagChangeListener(ITagChangeListener listener) {
		synchronized (tagListeners) {
			if (tagListeners.contains(listener)) return;
			tagListeners.add(listener);
		}
	}
	
	public void removeTagChangeListener(ITagChangeListener listener) {
		synchronized (tagListeners) {
			tagListeners.remove(listener);
		}
	} 
	
	private IStatus fireWaypointChange(IWaypointChangeEvent event) {
		IWaypointChangeListener[] listeners;
		LinkedList<IWaypointChangeListener> newListeners = new LinkedList<IWaypointChangeListener>();
		MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		synchronized (typedListeners) {
			LinkedList<IWaypointChangeListener> tls = typedListeners.get(event.getWaypoint().getType());
			if (tls != null) {
				newListeners.addAll(tls);
			}
		}
		synchronized (waypointListeners) {
			newListeners.addAll(waypointListeners);
			
		}
		listeners = newListeners.toArray(new IWaypointChangeListener[newListeners.size()]);
		for (IWaypointChangeListener listener : listeners) {
			try {
			listener.waypointsChanged(new WaypointDelta(new IWaypointChangeEvent[]{event}));
			} catch (Exception e) {
				//catch al exceptions
				if (e instanceof CoreException) {
					status.merge(((CoreException)e).getStatus());
				} else {
					String message = e.getMessage();
					if (message == null) {
						message = ""; //$NON-NLS-1$
					}
					status.merge(new Status(IStatus.ERROR, TagSEAPlugin.PLUGIN_ID, IStatus.ERROR, message, e));
				}
			}
		}
		return status;
	}
	
	private IStatus fireWaypointChanges(IWaypointChangeEvent[] events) {
		IWaypointChangeListener[] listeners;
		MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		synchronized (waypointListeners) {
			listeners = waypointListeners.toArray(new IWaypointChangeListener[waypointListeners.size()]);
		}
		WaypointDelta delta = new WaypointDelta(events);
		for (IWaypointChangeListener listener : listeners) {
			try {
				listener.waypointsChanged(delta);
			} catch (Exception e) {
				//catch al exceptions
				if (e instanceof CoreException) {
					status.merge(((CoreException)e).getStatus());
				} else {
					status.merge(new Status(IStatus.ERROR, TagSEAPlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
				}
			}
		}
		synchronized (typedListeners) {
			if (typedListeners.size() == 0) return status;
		}
		TreeMap<String, LinkedList<IWaypointChangeEvent>> typedEvents = 
			new TreeMap<String, LinkedList<IWaypointChangeEvent>>();
		for (IWaypointChangeEvent event : events) {
			LinkedList<IWaypointChangeEvent> list = typedEvents.get(event.getWaypoint().getType());
			if (list == null) {
				list = new LinkedList<IWaypointChangeEvent>();
				typedEvents.put(event.getWaypoint().getType(), list);
			}
			list.add(event);
		}
		for (String type : typedEvents.keySet()) {
			listeners = new IWaypointChangeListener[0];
			synchronized (typedListeners) {
				LinkedList<IWaypointChangeListener> list = typedListeners.get(type);
				if (list != null) {
					listeners = list.toArray(new IWaypointChangeListener[list.size()]);
				}
			}
			if (listeners.length > 0) {
				IWaypointChangeEvent[] eventArray = typedEvents.get(type).toArray(new IWaypointChangeEvent[0]);
				delta = new WaypointDelta(eventArray);
				for (IWaypointChangeListener listener : listeners) {
					try {
						listener.waypointsChanged(delta);
					} catch (Exception e) {
						//catch al exceptions
						if (e instanceof CoreException) {
							status.merge(((CoreException)e).getStatus());
						} else {
							String message = e.getMessage();
							if (message == null) message = ""; //$NON-NLS-1$
							status.merge(new Status(IStatus.ERROR, TagSEAPlugin.PLUGIN_ID, IStatus.ERROR, "", e)); //$NON-NLS-1$
						}
					}
				}
			}
		}
		return status;
	}
	
	private IStatus fireTagChange(ITagChangeEvent event) {
		ITagChangeListener[] listeners;
		MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		synchronized (tagListeners) {
			listeners = tagListeners.toArray(new ITagChangeListener[tagListeners.size()]);
		}
		for (ITagChangeListener listener : listeners) {
			try {
				listener.tagsChanged(new TagDelta(new ITagChangeEvent[]{event}));
			} catch (Exception e) {
				//catch al exceptions
				if (e instanceof CoreException) {
					status.merge(((CoreException)e).getStatus());
				} else {
					status.merge(new Status(IStatus.ERROR, TagSEAPlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
				}
			}
		}
		return status;
	}
	
	private IStatus fireTagChanges(ITagChangeEvent[] events) {
		ITagChangeListener[] listeners;
		MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		synchronized (tagListeners) {
			listeners = tagListeners.toArray(new ITagChangeListener[tagListeners.size()]);
		}
		TagDelta delta = new TagDelta(events);
		for (ITagChangeListener listener : listeners) {
			try {
				listener.tagsChanged(delta);
			} catch (Exception e) {
				//catch al exceptions
				if (e instanceof CoreException) {
					status.merge(((CoreException)e).getStatus());
				} else {
					status.merge(new Status(IStatus.ERROR, TagSEAPlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
				}
			}
		}
		return status;
	}
	
	/**
	 * Posts a waypoint change. If a ITagSEAOperation is running in the current thread, the
	 * change will be cached for processing and firing later. The change may not be fired at all
	 * if other changes render this change obsolete. Changes that will render this one obsolete
	 * are as follows:
	 * 
	 * -The waypoint is later deleted, making this change stale and unimporant.
	 * -The waypoint has a value changed for the same key in a property. The changes will be 
	 *  compressed into one single change which records only the newest value for the change.
	 * -event is a delete event that is a result of a previous new event in the same operation.
	 * -event is a tags changed event in the middle of other tags changed events. Only the oldest
	 *  and the newest information will be recorded.
	 *  
	 * If there is an ITagSEAOperation running in another thread, this method will block until
	 * that thread is completed.
	 * 
	 * @param event
	 */
	public void postWaypointChange(IWaypointChangeEvent event) {
		Thread currentThread;
		TagSEADeltaInfo deltaInf;
		synchronized (fThreadDeltaMap) {
			currentThread = Thread.currentThread();
			deltaInf = fThreadDeltaMap.get(currentThread);
			if (deltaInf != null) {
				deltaInf.addWaypointChange(event);
			} else {
				IStatus status = fireWaypointChange(event);
				if (status.getCode() != IStatus.OK) {
					TagSEAPlugin.getDefault().getLog().log(status);
				}
			}
		}
	}
	
	public void postTagChange(ITagChangeEvent event) {
		Thread currentThread;
		TagSEADeltaInfo deltaInf;
		LinkedList<IWaypointChangeEvent> waypointChanges = new LinkedList<IWaypointChangeEvent>();
		synchronized (fThreadDeltaMap) {
			if (ITagChangeEvent.NAME==(event.getType())) {
				IWaypoint[] waypoints = event.getTag().getWaypoints();
				//create name change events for the waypoints.
				for (int i = 0; i < waypoints.length; i++) {
					IWaypoint waypoint = waypoints[i];
					IWaypointChangeEvent waypointEvent =
						WaypointChangeEvent.createTagNameChangeEvent(waypoint, event.getOldName(), event.getNewName());
					AbstractWaypointDelegate delegate = TagSEAPlugin.getDefault().getWaypointDelegate(waypoint.getType());
					TagSEAChangeStatus changeStatus = delegate.processChange(waypointEvent);
					if (changeStatus.changePerformed) {
						waypointChanges.add(waypointEvent);
					} else {
						try {
							((Waypoint)waypoint).internalRemoveTag(event.getTag());
							ITag newTag = TagsModel.INSTANCE.createOrGetTag(event.getOldName());
							((Waypoint)waypoint).internalAddTag(newTag);
							((Tag)event.getTag()).removeWaypoint(waypoint);
							((Tag)newTag).addWaypoint(waypoint);
						} catch (TagSEAModelException e) {
							TagSEAPlugin.getDefault().log(e);
						}
					}
					
				}
			}
			currentThread = Thread.currentThread();
			deltaInf = fThreadDeltaMap.get(currentThread);
			if (deltaInf != null) {
				deltaInf.addTagChange(event);
			} else {
				IStatus status = fireTagChange(event);
				if (status.getCode() != IStatus.OK) {
					TagSEAPlugin.getDefault().getLog().log(status);
				}
			}
			for (IWaypointChangeEvent we : waypointChanges) {
				postWaypointChange(we);
			}
		}
	}
	
	/**
	 * Returns the object used to block operations on the tags and waypoints model. Use this
	 * object for synchronization of changes.
	 * @return the object used to block operations on the tags and waypoints model.
	 */
	Object getOperationBlocker() {
		return fThreadDeltaMap;
	}
	
	/**
	 * Runs the given operation this thread will block if wait is set to true. Normally, that will
	 * be the preferred way of calling this method. Setting wait to false is useful for long running
	 * operations that begin in the UI thread. Note: all other operations on the model will block
	 * until this operation is complete. Even single calls to the model to change fine-grained elements
	 * will block if another operation is currently running.
	 * @param op the operation to run
	 */
	public void run(TagSEAOperation op, boolean wait) {
		new OperationRunner(op).start(wait);
	}
	
	/**
	 * Runs the given operation in the current thread. This differs from {@link #run(TagSEAOperation, boolean)}
	 * in that {@link #run(TagSEAOperation, boolean)} will always spawn a separate job to run the code found
	 * in the given operation. This method will run the code within this thread, using the progress monitor
	 * provided. This method is still thread-safe in that it will suspend the current thread until other
	 * running TagSEAOperations have been completed, or any current model changes have finished. It does
	 * not guarantee ordering of TagSEAOperations either. If several {@link TagSEAOperation} have been
	 * scheduled using {@link #run(TagSEAOperation, boolean)}, this method may insert itself between 
	 * operations waiting to run, depending on scheduling. Changes to the TagSEA model will not
	 * be posted until after the operation (and possible nested operations) has completed. 
	 * @param op the operation to run.
	 * @param monitor the monitor to use while running the operation.
	 * @return the status of the changes 
	 */
	public IStatus syncRun(TagSEAOperation op, IProgressMonitor monitor) {
		MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "Sync Running TagSEA Operation", null);
		monitor.beginTask("Sync Running TagSEA operation " + op.getName(), 102);
		IProgressMonitor schedulingMonitor = new SubProgressMonitor(monitor, 1);
		IProgressMonitor runningMonitor = new SubProgressMonitor(monitor, 100, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		IProgressMonitor changeMonitor = new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		op.operationQueued();
		postOperationStateChanged(op);
		op.operationWaiting();
		postOperationStateChanged(op);
		ISchedulingRule rule;
		if (op.getRule() != null) {
			rule = new MultiRule(new ISchedulingRule[]{op.getRule(), TAGSEA_SCHEDULING});
//			rule = op.getRule();
		} else {
			rule = TAGSEA_SCHEDULING;
//			rule = null;
		}
		
		try {
			Job.getJobManager().beginRule(rule, schedulingMonitor);
			synchronized(fThreadDeltaMap) {
				blockingCount++;
				Thread thread = Thread.currentThread();
				boolean asSubOp = false;
				if (!fThreadDeltaMap.containsKey(thread)) {
					fThreadDeltaMap.put(thread, new TagSEADeltaInfo(new NullProgressMonitor()));
				} else {
					asSubOp = true;
				}
				op.operationStarted();
				postOperationStateChanged(op);
				try {
					status.merge(op.run(runningMonitor));
					runningMonitor.done();
				} catch (InvocationTargetException ex) {
					String message = ex.getMessage();
					if (message == null) {
						message = "";
					}
					Status s = new Status(
						Status.ERROR,
						TagSEAPlugin.PLUGIN_ID,
						Status.ERROR,
						message,
						ex
					);
					status.merge(s);
				}
				op.operationQuiting();
				postOperationStateChanged(op);
				if (!asSubOp) {
					changeMonitor.beginTask("Posting TagSEA changes", 1);
					TagSEADeltaInfo delta = fThreadDeltaMap.get(thread);
					status.merge(fireTagChanges(delta.getCompressedTagChanges()));
					status.merge(fireWaypointChanges(delta.getCompressedWaypointChanges()));
					changeMonitor.done();
					//remove from the map
					fThreadDeltaMap.remove(thread);
				}
				op.operationDone();
				postOperationStateChanged(op);
				monitor.done();
				blockingCount--;
			}
		} catch (Exception e){
			if (e instanceof CoreException) {
				status.merge(((CoreException)e).getStatus());
			} else {
				String message = e.getMessage();
				if (message == null) {
					message = "";
				}
				status.merge(new Status(IStatus.ERROR, TagSEAPlugin.PLUGIN_ID, message, e));
			}
		} finally {
			Job.getJobManager().endRule(rule);
		}
		if (status.getSeverity() == IStatus.OK) {
			//just return a simple status.
			return Status.OK_STATUS;
		}
		return status;
	}

	public void addOperationStateListener(ITagSEAOperationStateListener listener) {
		if (!stateListeners.contains(listener))
			stateListeners.add(listener);
	}

	public void removeOperationStateListener(ITagSEAOperationStateListener listener) {
		int i = stateListeners.indexOf(listener);
		if (i >= 0)
			stateListeners.remove(i);	
	}
	
	private void postOperationStateChanged(TagSEAOperation op) {
		ITagSEAOperationStateListener[] listeners = stateListeners.toArray(new ITagSEAOperationStateListener[stateListeners.size()]);
		for (ITagSEAOperationStateListener listener : listeners) {
			listener.stateChanged(op);
		}
	}
	
	public boolean isBlocked() {
		return blockingCount!=0;
	}
}

