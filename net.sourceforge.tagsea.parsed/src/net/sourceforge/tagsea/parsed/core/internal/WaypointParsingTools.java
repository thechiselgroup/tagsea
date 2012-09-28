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
package net.sourceforge.tagsea.parsed.core.internal;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;
import net.sourceforge.tagsea.parsed.parser.IParsedWaypointDescriptor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * 
 * @author Del Myers
 *
 */
public class WaypointParsingTools {
	

	private static class RegionSorter<T> implements Comparable<RegionSorter<T>> {
		private IRegion region;
		private int objectId;
		private T object;
		private boolean isEnd;
		
		RegionSorter(IRegion region, int objectId, T object, boolean isEnd) {
			this.region = region;
			this.objectId = objectId;
			this.object = object;
			this.isEnd = isEnd;
		}
		
		public static final Comparator<RegionSorter<?>> COMPARATOR = new Comparator<RegionSorter<?>>() {
				public int compare(RegionSorter<?> o1, RegionSorter<?> o2) {
					if (!o1.isEnd && o2.isEnd) {
						int diff = o1.region.getOffset()-(o2.region.getOffset()+o2.region.getLength());
						if (diff == 0) {
							diff = o1.objectId-o2.objectId;
							if (diff == 0) {
								diff = 1;
							}
						}
						return diff;
					} else if (o1.isEnd && !o2.isEnd) {
						int diff = (o1.region.getOffset()+o1.region.getLength())-o2.region.getOffset();
						if (diff == 0) {
							diff = o1.objectId-o2.objectId;
							if (diff == 0) {
								diff = -1;
							}
						}
						return diff;
					} else if (!(o1.isEnd || o2.isEnd)) {
						int diff = o1.region.getOffset()-o2.region.getOffset();
						if (diff == 0) {
							return o1.objectId-o2.objectId;
						}
						return diff;
					} else if (o1.isEnd && o2.isEnd) {
						int diff = (o1.region.getOffset()+o1.region.getLength())-
							(o2.region.getOffset()+o2.region.getLength());
						if (diff == 0) {
							return o1.objectId-o2.objectId;
						}
						return diff;
					}
					return 0;
				}
			};
			public int compareTo(RegionSorter<T> o2) {
				return RegionSorter.COMPARATOR.compare(this, o2);
			}
		}
	
	/**
	 * A region node represents an object in a graph of regions in text. The graph can be
	 * used to discover overlapping regions. If a region node has an edge to another region
	 * node, then the two regions overlap.
	 * @author Del Myers
	 *
	 */
	public static class RegionNode<T> {
		
		private IRegion region;
		private T object;
		private int objectId;
		private List<RegionNode<T>> adjacencies;

		/**
		 * Creates a new regionNode
		 * @param region
		 * @param object
		 * @param objectId
		 */
		protected RegionNode(IRegion region, T object, int objectId) {
			this.region = region;
			this.object = object;
			this.objectId = objectId;
			adjacencies = new LinkedList<RegionNode<T>>();
		}
		
		public List<RegionNode<T>> getAdjacencies() {
			return adjacencies;
		}

		/**
		 * @return
		 */
		public T getObject() {
			return object;
		}
		
		public IRegion getRegion() {
			return region;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static class NodeComparator implements Comparator<RegionNode> {
		public int compare(RegionNode o1, RegionNode o2) {
			int diff = o1.getAdjacencies().size() - o2.getAdjacencies().size();
			if (diff == 0) {
				return o1.objectId - o2.objectId;
			}
			return diff;
		}
		
	}
	
	/**
	 * Represents a graph of overlapping regions. Ordered by the number of regions that one
	 * region overlaps with. 
	 * @author Del Myers
	 *
	 */
	public static class RegionGraph<T> {
		HashMap<T, RegionNode<T>> objectNodeMap;
		private ArrayList<RegionNode<T>> N;
		private T[] initialObjects;
		
		
		
		public RegionGraph(IRegion[] regions, T[] objects) {
			this.initialObjects = objects;
			if (regions.length != objects.length) {
				throw new IllegalArgumentException("regions must have matching objects");
			}
			TreeSet<RegionSorter<RegionNode<T>>> sorter = new TreeSet<RegionSorter<RegionNode<T>>>();
			for (int i = 0; i < objects.length; i++) {
				RegionNode<T> node = new RegionNode<T>(regions[i], objects[i], i);
				sorter.add(new RegionSorter<RegionNode<T>>(regions[i], i, node, false));
				sorter.add(new RegionSorter<RegionNode<T>>(regions[i], i, node, true));
			}
			objectNodeMap = new HashMap<T, RegionNode<T>>();
			//build the graph
			N = new ArrayList<RegionNode<T>>();
			LinkedList<RegionNode<T>> heap = new LinkedList<RegionNode<T>>();
			for (RegionSorter<RegionNode<T>> s : sorter) {
				RegionNode<T> currentNode = s.object;
				if (!s.isEnd) {
					//connect to all other open nodes
					for (RegionNode<T> n : heap) {
						n.getAdjacencies().add(currentNode);
						currentNode.getAdjacencies().add(n);
					}
					N.add(currentNode);
					objectNodeMap.put(currentNode.object, currentNode);
					heap.add(currentNode);
				} else {
					heap.remove(currentNode);
				}
			}
			Collections.sort(N, new NodeComparator());
		}
		
		@SuppressWarnings("unchecked")
		public T[] getOverlappingNodes(T object) {
			RegionNode<T> node = objectNodeMap.get(object);
			if (node != null) {

				T[] result = (T[]) new Object[node.getAdjacencies().size()];
				int i = 0;
				for (RegionNode<T> adjacent : node.getAdjacencies()) {
					result[i] = adjacent.object;
					i++;
				}
				return result;

			}
			return null;
		}
		
		public int getOverlapCount(T object) {
			RegionNode<T> node = objectNodeMap.get(object);
			if (node != null) {
				return node.adjacencies.size();
			}
			return -1;
		}
		
		public IRegion getRegion(T object) {
			RegionNode<T> node = objectNodeMap.get(object);
			if (node != null) {
				return node.region;
			}
			return null;
		}
		
		public void remove(T object) {
			RegionNode<T> node = objectNodeMap.get(object);
			if (node != null) {
				int index = Collections.binarySearch(N, node, new NodeComparator());
				if (index > 0) {
					RegionNode<T> found = N.get(index);
					if (found == node) {
						for (RegionNode<T> adjacent : node.getAdjacencies()) {
							adjacent.getAdjacencies().remove(node);
						}
						objectNodeMap.remove(object);
						N.remove(index);
						Collections.sort(N, new NodeComparator());
					}
				}
			}
		}
		
		public RegionNode<T> pop() {
			if (N.size() > 0) {
				RegionNode<T> top = N.remove(0);
				for (RegionNode<T> adjacent : top.getAdjacencies()) {
					adjacent.getAdjacencies().remove(top);
				}
				objectNodeMap.remove(top.object);
				Collections.sort(N, new NodeComparator());
				return top;
			}
			return null;
		}
		
		public T popObject() {
			RegionNode<T> node = pop();
			if (node != null) {
				return node.getObject();
			}
			return null;
		}
		
		public RegionNode<T> peek() {
			if (N.size() > 0) {
				return N.get(0);
			}
			return null;
		}
		
		public T peekObject() {
			RegionNode<T> node = peek();
			if (node != null) {
				return node.getObject();
			}
			return null;
		}
		
		@SuppressWarnings("unchecked")
		public T[] getNodeObjects() {
			
			T[] result = (T[])Array.newInstance(initialObjects.getClass().getComponentType(), N.size());
			int i = 0;
			for (RegionNode<T> node : N) {
				result[i] = node.getObject();
				i++;
			}
			return (T[])result;
		}
		
	}

	/**
	 * Describes a matching between a waypoint and a waypoint descriptor. If both
	 * <code>waypoint</code> and <code>descriptor</code> have non-null values, then the
	 * two describe the same region in the text and the descriptor can safely be copied into
	 * the waypoint. If <code>waypoint</code> is non-null, and <code>descriptor</code> is
	 * null, then there is no matching waypoint for the descriptor. The converse is true
	 * as well.
	 * 
	 * {@link WaypointParsingTools#organizeWaypoints(IFile, IWaypoint[], IParsedWaypointDescriptor[], String)}
	 * @author Del Myers
	 *
	 */
	public static class WaypointDescriptorMatch {
		public final IWaypoint waypoint;
		public final IParsedWaypointDescriptor descriptor;
		private WaypointDescriptorMatch(IWaypoint waypoint, IParsedWaypointDescriptor descriptor) {
			this.waypoint = waypoint;
			this.descriptor = descriptor;
		}
	}
	
	
	public static final boolean checkForOverlap(IParsedWaypointDescriptor[] descriptors) {
		TreeSet<RegionSorter<IParsedWaypointDescriptor>> overLapChecker =
			new TreeSet<RegionSorter<IParsedWaypointDescriptor>>();
		for (int i = 0; i < descriptors.length; i++) {
			int start = descriptors[i].getCharStart();
			int length = descriptors[i].getCharEnd() - start;
			Region r = new Region(start, length);
			RegionSorter<IParsedWaypointDescriptor> ss = new RegionSorter<IParsedWaypointDescriptor>(r, i, descriptors[i], false);
			RegionSorter<IParsedWaypointDescriptor> se = new RegionSorter<IParsedWaypointDescriptor>(r, i, descriptors[i], true);
			overLapChecker.add(ss);
			overLapChecker.add(se);
		}
		//pull off of the heap, ensuring that the start and end are well-formed
		RegionSorter<IParsedWaypointDescriptor> last = null;
		for (RegionSorter<IParsedWaypointDescriptor> s : overLapChecker) {
			if (last == null) {
				if (s.isEnd) return false;
				else last = s;
			} else {
				//check against the last one.
				if (!last.isEnd) {
					//make sure that we have the right object.
					if (!s.isEnd || last.objectId != s.objectId) {
						return false;
					}
					//make sure that when we have the same object, the 
					//and end aren't the same.
					if (s.region.getLength() == 0) return false;
				} else {
					if (s.isEnd || last.objectId == s.objectId) {
						return false;
					}
					//make sure that when we have different objects,
					//the start isn't the same.
					if (s.region.getOffset()==last.region.getOffset()) return false;
				}
			}
		}
		return true;
	}

	/**
	 * Creates a graph of overlaps between parsed waypoint descriptors. Assumes that all descriptors are in
	 * the same file.
	 * @param allDescriptors
	 * @return
	 */
	public static final RegionGraph<IParsedWaypointDescriptor> calculateOverlap(List<? extends IParsedWaypointDescriptor> allDescriptors) {
		IParsedWaypointDescriptor[] descriptorArray = new IParsedWaypointDescriptor[allDescriptors.size()];
		IRegion[] regionArray = new IRegion[allDescriptors.size()];
		for (int i = 0; i < allDescriptors.size(); i++) {
			IParsedWaypointDescriptor desc = allDescriptors.get(i);
			descriptorArray[i] = desc;
			regionArray[i] = new Region(desc.getCharStart(), desc.getCharEnd()-desc.getCharStart());
		}
		return new RegionGraph<IParsedWaypointDescriptor>(regionArray, descriptorArray);
	}
	
	/**
	 * Creates a graph of overlaps between parsed waypoints. Assumes that all waypoints are in
	 * the same file.
	 * @param allDescriptors
	 * @return
	 */
	public static final RegionGraph<IWaypoint> calculateOverlappingWaypoints(List<? extends IWaypoint> allDescriptors) {
		IWaypoint[] descriptorArray = new IWaypoint[allDescriptors.size()];
		IRegion[] regionArray = new IRegion[allDescriptors.size()];
		for (int i = 0; i < allDescriptors.size(); i++) {
			IWaypoint desc = allDescriptors.get(i);
			descriptorArray[i] = desc;
			int start = ParsedWaypointUtils.getCharStart(desc);
			int end = ParsedWaypointUtils.getCharEnd(desc);
			regionArray[i] = new Region(start, end-start);
		}
		return new RegionGraph<IWaypoint>(regionArray, descriptorArray);
	}

	/**
	 * looks at the waypoints currently in the given file, and the waypoint descriptors provided
	 * to synchronize the new descriptors with the old waypoints. Matches by region only in
	 * order to improve efficiency.
	 * 
	 * @param file
	 * @param oldWaypoints
	 * @param descriptors
	 * @param kind
	 */
	public static List<WaypointDescriptorMatch> organizeWaypoints(IFile file, IWaypoint[] oldWaypoints,
			IParsedWaypointDescriptor[] descriptors, String kind) {
		LinkedList<IParsedWaypointDescriptor> descriptorList = new LinkedList<IParsedWaypointDescriptor>();
		descriptorList.addAll(Arrays.asList(descriptors));
		LinkedList<WaypointDescriptorMatch> matches = new LinkedList<WaypointDescriptorMatch>();
		for (int i = 0; i < oldWaypoints.length; i++) {
			IWaypoint wp = oldWaypoints[i];
			IFile wpFile = 
				ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getFileForWaypoint(wp);
			if (!file.equals(wpFile))
				continue;
			if (!wp.exists())
				continue;
			IParsedWaypointDescriptor match = null;
			int wpStart = wp.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_START, -1);
			int wpEnd = wp.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_END, -1);
			if (wpStart == -1 || wpEnd == -1) continue;
			for (Iterator<IParsedWaypointDescriptor> it = descriptorList.iterator(); it.hasNext();) {
				IParsedWaypointDescriptor desc = it.next();
				int descStart = desc.getCharStart();
				int descEnd = desc.getCharEnd();
				if (descStart == wpStart && descEnd == wpEnd) {
					//match them.
					match = desc;
					it.remove();
					break;
				}
			}
			matches.add(new WaypointDescriptorMatch(wp, match));
		}
		//add all the unmatched descriptors
		for (IParsedWaypointDescriptor desc : descriptorList) {
			matches.add(new WaypointDescriptorMatch(null, desc));
		}
		return matches;
	}


	/**
	 * Looks at the list of matchings and either copies the matchings, deletes unmatched waypoints,
	 * or generates new waypoints based on the descriptors. Note: it is expected that this
	 * method is called within the context of an AbstractWaypointUpdateOperation.
	 * @param matchings
	 * @return a list of waypoints that were created/updated by this call. Excludes deleted waypoints.
	 */
	public static List<IWaypoint> generateWaypoints(List<WaypointDescriptorMatch> matchings, IFile file, String kind) {
		List<IWaypoint> waypoints = new LinkedList<IWaypoint>();
		for (WaypointDescriptorMatch match : matchings) {
			if (match.waypoint != null && match.descriptor == null) {
				deleteWaypoint(match.waypoint);
			} else if (match.waypoint == null && match.descriptor != null) {
				waypoints.add(createWaypoint(match.descriptor, file, kind));
			} else if (match.waypoint != null && match.descriptor != null) {
				copyDescriptor(match.waypoint, match.descriptor);
				waypoints.add(match.waypoint);
			}
		}
		return waypoints;
	}


	/**
	 * @param waypoint
	 * @param descriptor
	 */
	private static void copyDescriptor(final IWaypoint wp,
			IParsedWaypointDescriptor descriptor) {
		final ArrayList<String> attributes = new ArrayList<String>();
		final ArrayList<Object> values = new ArrayList<Object>();
		if (descriptor.getAuthor() != null) {
			wp.setAuthor(descriptor.getAuthor());
			attributes.add(IWaypoint.ATTR_AUTHOR);
			values.add(descriptor.getAuthor());
		}
		if (descriptor.getDate() != null) {
			wp.setDate(descriptor.getDate());
			attributes.add(IWaypoint.ATTR_DATE);
			DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
			values.add(format.format(descriptor.getDate()));
		}
		if (descriptor.getMessage() != null) {
			wp.setText(descriptor.getMessage());
			attributes.add(IWaypoint.ATTR_MESSAGE);
			values.add(descriptor.getMessage());
		}
		if (descriptor.getDetail() != null) {
			wp.setStringValue(IParsedWaypointAttributes.ATTR_DOMAIN, descriptor.getDetail());
			attributes.add(IParsedWaypointAttributes.ATTR_DOMAIN);
			values.add(descriptor.getDetail());
		}
		wp.setIntValue(IParsedWaypointAttributes.ATTR_LINE, descriptor.getLine());
		attributes.add(IParsedWaypointAttributes.ATTR_LINE);
		values.add(descriptor.getLine());
		//copy the tags
		TreeSet<String> newTags = new TreeSet<String>();
		TreeSet<ITag> oldTags = new TreeSet<ITag>();
		
		for (ITag tag : wp.getTags()) {
			oldTags.add(tag);
		}
		for (String tag : descriptor.getTags()) {
			newTags.add(tag);
		}
		//remove orphaned tags
		for (ITag tag : oldTags) {
			if (!newTags.contains(tag.getName())) {
				wp.removeTag(tag);
			}
		}
		//add new ones
		for (String tag : newTags) {
			wp.addTag(tag);
		}
		String tagNames = "";
		for (ITag tag : wp.getTags()) {
			tagNames = tagNames + tag.getName() + " ";
		}
		tagNames.trim();
		attributes.add("tags");
		values.add(tagNames);
		attributes.add(IParsedWaypointAttributes.ATTR_CHAR_START);
		values.add(descriptor.getCharStart());
		attributes.add(IParsedWaypointAttributes.ATTR_CHAR_START);
		values.add(descriptor.getCharEnd());
		attributes.add(IParsedWaypointAttributes.ATTR_KIND);
		values.add(ParsedWaypointUtils.getKind(wp));	
	}


	/**
	 * @param descriptor
	 * @param file
	 * @param kind
	 */
	private static IWaypoint createWaypoint(final IParsedWaypointDescriptor descriptor,
			final IFile file, final String kind) {
		final IWaypoint wp = TagSEAPlugin.getWaypointsModel().createWaypoint(ParsedWaypointPlugin.WAYPOINT_TYPE, descriptor.getTags());
		if (wp != null) {
			final String author = descriptor.getAuthor();
			final Date date = descriptor.getDate();
			final String message = descriptor.getMessage();
			final String detail = descriptor.getDetail();
			if (author != null) {
				wp.setAuthor(author);
			}
			if (date != null) {
				wp.setDate(date);
			}
			if (message != null) {
				wp.setText(message);
			}
			if (detail != null) {
				wp.setStringValue(IParsedWaypointAttributes.ATTR_DOMAIN, detail);
			}
			final int start = descriptor.getCharStart();
			final int end = descriptor.getCharEnd();
			wp.setIntValue(IParsedWaypointAttributes.ATTR_CHAR_START, start);
			wp.setIntValue(IParsedWaypointAttributes.ATTR_CHAR_END, end);
			wp.setStringValue(IParsedWaypointAttributes.ATTR_RESOURCE, file.getFullPath().toPortableString());
			wp.setStringValue(IParsedWaypointAttributes.ATTR_KIND, kind);
			wp.setIntValue(IParsedWaypointAttributes.ATTR_LINE, descriptor.getLine());
		}
		
		return wp;
	}


	/**
	 * @param waypoint
	 */
	public static void deleteWaypoint(final IWaypoint waypoint) {
		if (waypoint.exists()) {
			waypoint.delete();
		}		
	}

}
