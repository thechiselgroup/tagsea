package net.sourceforge.tagsea.model.internal;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class WaypointAdapterFactory implements IAdapterFactory {
	public static final Class<?>[] ADAPTABLE_TYPES = new Class[] {
		IWaypoint.class,
		String.class,
		ImageDescriptor.class,
		IWorkbenchAdapter.class,
		IActionFilter.class
	};
	
	private static final IWorkbenchAdapter Adapter = new WaypointWorkbenchAdapter();
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof IWaypoint) {
			if (IWorkbenchAdapter.class.equals(adapterType)) {
				return Adapter;
			} else if (ImageDescriptor.class.equals(adapterType)) {
				return Adapter.getImageDescriptor(adaptableObject);
			} else if (String.class.equals(adapterType)) {
				return Adapter.getLabel(adaptableObject);
			} else if (IActionFilter.class.equals(adapterType)) {
				return new WaypointActionFilter();
			} else if (IWaypoint.class.equals(adapterType)) {
				return ((IWaypoint)adaptableObject);
			}
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
		return ADAPTABLE_TYPES;
	}

}
