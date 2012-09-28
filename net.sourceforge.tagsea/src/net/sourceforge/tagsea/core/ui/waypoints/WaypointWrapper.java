package net.sourceforge.tagsea.core.ui.waypoints;

import java.util.ArrayList;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;

/**
 * A simple class that checks equality of the waypoints by the delegate's 
 * idea of what equality is.
 * @author Del Myers
 */
@SuppressWarnings("unchecked")
class WaypointWrapper implements IAdaptable {
	
	private IWaypoint waypoint;
	
	private static class AdapterFactory implements IAdapterFactory {

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			return ((WaypointWrapper)adaptableObject).waypoint.getAdapter(adapterType);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			IAdapterManager adapterManager = Platform.getAdapterManager();
			String[] types = adapterManager.computeAdapterTypes(IWaypoint.class);
			ArrayList<Class> result = new ArrayList<Class>();

			for (String type : types) {
				try {
					result.add(Class.forName(type));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			if (!result.contains(IWaypoint.class)) {
				result.add(IWaypoint.class);
			}

			
			return result.toArray(new Class[result.size()]);
		}
		
	}
	
	static {
		Platform.getAdapterManager().registerAdapters(new AdapterFactory(), WaypointWrapper.class);
	}
	
	public WaypointWrapper(IWaypoint waypoint) {
		this.waypoint = waypoint;
	}
	
	
	public boolean equals(Object other) {
		if (!(other instanceof WaypointWrapper)) return false;
		WaypointWrapper that = (WaypointWrapper) other;
		return TagSEAPlugin.getDefault().waypointsEqual(this.waypoint, that.waypoint);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == this.getClass()) return this;
		return waypoint.getAdapter(adapter);
	}
	
}