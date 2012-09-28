package net.sourceforge.tagsea.model.internal;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IActionFilter;

/**
 * Used for adapting waypoints to IActionFilters so that waypoints that fit certain criteria can trigger
 * the addition of actions to menus and toolbars.
 * @author Del Myers
 * 
 */
class WaypointActionFilter implements IActionFilter {
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionFilter#testAttribute(java.lang.Object, java.lang.String, java.lang.String)
	 */
	public boolean testAttribute(Object target, String name, String value) {
		if (target instanceof IAdaptable) {
			//adapt to a waypoint
			IWaypoint waypoint = (IWaypoint)((IAdaptable)target).getAdapter(IWaypoint.class);
			if (waypoint != null) {
				if (IWaypoint.TYPE_FILTER_ATTRIBUTE.equals(name)) {
					return value.equals(waypoint.getType());
				} else if (IWaypoint.SUBTYPE_FILTER_ATTRIBUTE.equals(name)) {
					if (!value.equals(waypoint.getType())) {
						return waypoint.isSubtypeOf(value);
					}
					return true;
				} else {
					Object val = waypoint.getValue(name);
					if (val != null) {
						return val.toString().equals(value);
					}
				}
			}
		}
		return false;
	}
	
}