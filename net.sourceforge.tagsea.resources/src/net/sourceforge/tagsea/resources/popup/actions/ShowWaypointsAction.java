package net.sourceforge.tagsea.resources.popup.actions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.TagSEAView;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


/**
 * An action that selects the tags in the tags viewer, then selects the waypoints in the waypoints viewer for the
 * selected resource.
 */
public class ShowWaypointsAction implements IObjectActionDelegate,
		IActionDelegate2 {

	private LinkedList<IResource> resources;
	private LinkedList<IWaypoint> waypoints;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		
		TagSEAView tView = TagSEAPlugin.getDefault().getUI().showTagSEAView();
		if (tView != null) {
			//get all of the tags and show select them in the tags view
			TreeSet<ITag> tagSet = new TreeSet<ITag>();
			for (IWaypoint wp : this.waypoints) {
				tagSet.addAll(Arrays.asList(wp.getTags()));
			}
			tView.setSelectedTags(tagSet.toArray(new ITag[tagSet.size()]));
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.resources = new LinkedList<IResource>();
		if (selection instanceof IStructuredSelection) {
			Iterator i = ((IStructuredSelection)selection).iterator();
			while (i.hasNext()) {
				resources.add((IResource) i.next());
			}
		}
		refreshWaypoints();
		action.setEnabled(waypoints.size() > 0);
	}

	private void refreshWaypoints() {
		this.waypoints = new LinkedList<IWaypoint>();
		if (resources != null) {
			for (IResource resource : resources) {
				IWaypoint[] wps = ResourceWaypointUtils.getWaypointsForResource(resource, true);
				this.waypoints.addAll(Arrays.asList(wps));
			}
		}
	}

	public void dispose() {
	}

	public void init(IAction action) {
		
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

}
