package net.sourceforge.tagsea.tours.waypoints;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;
import net.sourceforge.tagsea.core.ui.WaypointTransfer;

import org.eclipse.swt.dnd.Transfer;

import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourElementDropAdapterExtension;

public class WaypointsTourElementDropAdapter implements ITourElementDropAdapterExtension {

	/**
	 * Not supported
	 */
	public ITourElement[] convertDropData(Object data) {
		return new ITourElement[0];
	}

	/**
	 * Builds a list of tour elements for all changeable waypoints 
	 */
	public ITourElement[] convertDropData(Object data, ITour tour) {
		IWaypoint[] waypoints = ((IWaypoint[])data);
		List<ITourElement> tourElements = new LinkedList<ITourElement>();
		int nextStep = getNextStep(tour); 
		for (int i = 0; i < waypoints.length; i++) {
			IWaypointUIExtension uix = TagSEAPlugin.getDefault().getWaypointUI(waypoints[i].getType());
			if (uix.canUIAddTag(waypoints[i]) && uix.canUIDeleteTag(waypoints[i])) {
				String stepID = WaypointTourElement.constructTagName(tour, nextStep);
				WaypointTourElement tourElement = new WaypointTourElement(waypoints[i], stepID);
				tourElements.add(tourElement );
				waypoints[i].addTag(stepID);
			}	
		}
		return tourElements.toArray(new ITourElement[tourElements.size()]);
	}

	/**
	 * Get the next waypoint step ID
	 * @param tour
	 * @return
	 */
	private int getNextStep(ITour tour) {
		int elementID = -1;
		for (ITourElement element : tour.getElements()) {
			if (element instanceof WaypointTourElement) {
				WaypointTourElement wpElement = (WaypointTourElement) element;
				if (wpElement.getElementID() > elementID) {
					elementID = wpElement.getElementID();
				}
			}
		}
		return elementID+1;
	}

	public Transfer getTransfer() {
		return WaypointTransfer.getInstance();
	}

}
