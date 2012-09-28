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
package net.sourceforge.tagsea.tours.waypoints;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tours.AbstractTourElement;
import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.editors.TourEditor;

/**
 * Simple tour element for waypoints.
 * @author Del Myers
 *
 */
public class WaypointTourElement extends AbstractTourElement {
	private static final String STEP_ID_KEY = "STEP_ID_KEY";
	public static final String TOUR_TAG_PREFIX = "tour";
	public static final String START_ID = "";
	
	private IWaypoint waypoint;
	/**
	 * The step id is the same as the tag used in the waypoint to
	 * identify it in a tour = "tour.&lt;tourID&gt;.&lt;elementID&gt;"
	 */
	private String stepID;
	/**
	 * A reference is stored for this memento on load and on save in order
	 * to facilitate refreshing of the tour element when tags are created at
	 * start-up. Note that if you pass an instance of IMemento into a method
	 * make sure to update this reference.
	 */
	private IMemento memento;

	public WaypointTourElement(IWaypoint waypoint, String id) {
		this.waypoint = waypoint;
		this.stepID = id;
	}
	
	public WaypointTourElement() {
		this(null, "");
	}
	
	
	public HashMap<String, Object> getAttributes() {
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		IWaypoint waypoint = getWaypoint();
		if (waypoint != null) {
			for (String attr : waypoint.getAttributes()) {
				attrs.put(attr, waypoint.getValue(attr));
			}
		}
		return attrs;
	}
	
	public SortedSet<String> getTags() {
		TreeSet<String> tags = new TreeSet<String>();
		IWaypoint waypoint = getWaypoint();
		if (waypoint != null) {
			for (ITag tag : waypoint.getTags()) {
				tags.add(tag.getName());
			}
		}
		return tags;
	}
	
	public IWaypoint getWaypoint() {
		if (waypoint != null && !waypoint.exists()) {
			//the waypoint is stale because of some major change that may have deleted it and re-created it.
			//Try and re-load the waypoint.
			ITag tag = TagSEAPlugin.getTagsModel().getTag(stepID);
			if (tag != null) {
				if (tag.getWaypointCount() > 1) {
				WaypointsToursPlugin.getDefault().getLog().log(new Status(
						Status.WARNING,
						WaypointsToursPlugin.PLUGIN_ID,
						"More than one waypoint for tour element"
					));
				}
				this.waypoint = tag.getWaypoints()[0];
			} else {
				this.waypoint = null;
			}
		}
		return waypoint;
	}

	public ITourElement createClone() {
		return new WaypointTourElement(getWaypoint(), getStepID());
	}

	public Image getImage() {
		if (getWaypoint() != null) {
			return TagSEAPlugin.getDefault().getUI().getImage(getWaypoint());
		} else {
			return null;
		}
	}

	public String getShortText() {
		if (getWaypoint() != null) {
			String text = TagSEAPlugin.getDefault().getWaypointType(getWaypoint().getType()).getName();
			return text;
		} else {
			return "Waypoint not found";
		}
	}

	public String getText() {
		if (getWaypoint() != null) {
			String text = TagSEAPlugin.getDefault().getUI().getLabel(getWaypoint());
			return text;
		} else {
			return "Waypoint not found";
		}
	}

	public void start() {
		if (getWaypoint() != null) {
			TagSEAPlugin.getDefault().navigate(getWaypoint());
		}
	}

	public void stop() {
	}

	public void transition() {
		if (getWaypoint() != null) {
			TagSEAPlugin.getDefault().navigate(getWaypoint());
		}
	}
	
	@Override
	public void load(IMemento memento) {
		this.memento = memento;
		super.load(memento);
		stepID = memento.getString(STEP_ID_KEY);
		ITag tag = TagSEAPlugin.getTagsModel().getTag(stepID);
		if (tag == null || tag.getWaypointCount() == 0) {
			WaypointsToursPlugin.getDefault().getLog().log(new Status(
				Status.ERROR,
				WaypointsToursPlugin.PLUGIN_ID,
				"Error getting waypoint for tour element"
			));
			return;
		} else if (tag.getWaypointCount() > 1) {
			WaypointsToursPlugin.getDefault().getLog().log(new Status(
				Status.WARNING,
				WaypointsToursPlugin.PLUGIN_ID,
				"More than one waypoint for tour element"
			));
		} 
		this.waypoint = tag.getWaypoints()[0];
	}
	
	public void reload() {
		if (this.memento != null) {
			load(this.memento);
			fireElementChangedEvent();
		}
	}
	
	@Override
	public void save(IMemento memento) {
		super.save(memento);
		memento.putString(STEP_ID_KEY, getStepID());
		this.memento = memento;
	}

	public int getElementID() {
		int lastDot = getStepID().lastIndexOf('.');
		if (lastDot >= 0) {
			String elementIDString = getStepID().substring(lastDot+1);
			return Integer.parseInt(elementIDString);
		}
		return -1;
	}
	
	public String getStepID() {
		return stepID;
	}

	/**
	 * Build a tag name from tag ID and step
	 * @param tour
	 * @param tourElement
	 * @return
	 */
	public static String constructTagName(ITour tour, int stepNumber) {
		return TOUR_TAG_PREFIX + "." + tour.getID() + "." + stepNumber; 
	}

	/**
	 * If the specified tag is a tour tag, queue up a reload task for any editor that
	 * contains a tour that uses this tag.
	 * @param name
	 */
	public static void reloadForTag(final String name) {
		if (name.startsWith(TOUR_TAG_PREFIX+ ".")) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					for (IWorkbenchPage page : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()) {
						for (IEditorReference editorReference : page.getEditorReferences()) {
							IEditorPart editor = editorReference.getEditor(false);
							if (editor instanceof TourEditor) {
								ITour tour = ((TourEditor)editor).getTour();
								if (tour != null) {
									for (ITourElement element : tour.getElements()) {
										if (element instanceof WaypointTourElement) {
											WaypointTourElement wpElement = (WaypointTourElement) element;
											if (wpElement.getStepID().equals(name)) {
												wpElement.reload();
											}
										}
									}
								}
							}
						}
					}
				}
			});
		}
	}
	
}
