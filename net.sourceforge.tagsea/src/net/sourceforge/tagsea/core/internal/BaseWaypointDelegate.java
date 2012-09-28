package net.sourceforge.tagsea.core.internal;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.WaypointDelta;

/**
 * Base delegate for all others. Does nothing.
 * @author Del Myers
 */
public class BaseWaypointDelegate extends AbstractWaypointDelegate {

	public BaseWaypointDelegate() {
	}

	@Override
	public void navigate(IWaypoint waypoint) {
	}

	public void tagsChanged(TagDelta delta) {
	}

	public void waypointsChanged(WaypointDelta delta) {
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.AbstractWaypointDelegate#load()
	 */
	@Override
	public void load() {
	}

}
