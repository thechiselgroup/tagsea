package net.sourceforge.tagsea.parsed.ui.internal.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.IWaypointFilter;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;
import net.sourceforge.tagsea.parsed.ui.internal.preferences.PreferenceConstants;
import net.sourceforge.tagsea.resources.ResourceWaypointPreferences;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class WaypointFilter implements IWaypointFilter {
	private List<IResource> resources;
	
	private final int filterAny = 0;
	private final int filterChildren = 1;
	private final int filterProject = 2;
	private final int filterResource = 3;
	
	@SuppressWarnings("unchecked")
	public void initialize() {
		resources = Collections.EMPTY_LIST;
	}

	
	public boolean select(IWaypoint waypoint) {
		
		String kind = ParsedWaypointUtils.getKind(waypoint);
		if (kind == null) return false;
		IPreferenceStore store = ParsedWaypointPlugin.getDefault().getPreferenceStore();
		String filtered = store.getString(PreferenceConstants.FILTERED_KINDS);
		String[] kinds = filtered.split("\\s+");
		for (String filterkind : kinds) {
			if (kind.equals(filterkind)) {
				return false;
			}
		}

		int type = getFilterType();
		if (type == filterAny) {
			return true;
		}
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			LinkedList<IResource> localResources = new LinkedList<IResource>();
			for (Iterator<?> i = ss.iterator(); i.hasNext();) {
				Object next = i.next();
				if (next instanceof IAdaptable) {
					IResource resource = (IResource)((IAdaptable)next).getAdapter(IResource.class);
					if (resource != null) {
						localResources.add(resource);
					}
				}
			}
			if (localResources != null) {
				resources = localResources;
			}
		} else {
			IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
			if (part instanceof IEditorPart) {
				IEditorInput input = ((IEditorPart)part).getEditorInput();
				if (input instanceof IFileEditorInput) {
					IResource file = ((IFileEditorInput)input).getFile();
					if (file != null) {
						resources = new ArrayList<IResource>();
						resources.add(file);
					}
				}
			}
		}
		switch(type) {
		case filterProject:
			return isSelectedProject(waypoint);
		case filterChildren:
			return isSelectedChild(waypoint);
		case filterResource:
			return isSelected(waypoint);
		}
		return true;
	}

	/**
	 * @param waypoint
	 * @return
	 */
	private boolean isSelectedProject(IWaypoint waypoint) {
		IResource resource = ResourceWaypointUtils.getResource(waypoint);
		if (resource != null) {
			for (IResource r : resources) {
				if (r.getProject().equals(resource.getProject()))
					return true;
			}
		}
		return false;
	}

	/**
	 * @param waypoint
	 * @return
	 */
	private boolean isSelectedChild(IWaypoint waypoint) {
		IResource resource = ResourceWaypointUtils.getResource(waypoint);
		if (resource != null) {
			for (IResource r : resources) {
				if (r.equals(resource)) return true;
				if (r instanceof IContainer) {
					IPath parent = r.getFullPath();
					IPath child = resource.getFullPath();
					if (parent.isPrefixOf(child)) return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param waypoint
	 * @return
	 */
	private boolean isSelected(IWaypoint waypoint) {
		IResource resource = ResourceWaypointUtils.getResource(waypoint);
		if (resource != null) {
			for (IResource r : resources) {
				if (r.equals(resource)) 
					return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointFilter#isImportantSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public boolean isImportantSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			for (Iterator<?> i = ss.iterator(); i.hasNext();) {
				Object next = i.next();
				if (next instanceof IAdaptable) {
					IResource resource = (IResource)((IAdaptable)next).getAdapter(IResource.class);
					if (resource != null) {
						return true;
					}
				}
			}
		} else if (selection instanceof ITextSelection) {
			return true;
		}
		return false;
	}
	
	private boolean isLinkedToResourceWaypoints() {
		return ParsedWaypointPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.LINKED_TO_RESOURCE_WAYPOINT_FILTERS);
	}
	
	private int getFilterType() {
		String filter = "";
		if (isLinkedToResourceWaypoints()) {
			filter = ResourceWaypointPreferences.getCurrentFilterType();
		} else {
			filter = ParsedWaypointPlugin.getDefault().getPreferenceStore().getString(ResourceWaypointPreferences.FILTER_PREFERENCE_KEY);
		}
		if (ResourceWaypointPreferences.FILTER_ANY.equals(filter)) {
			return filterAny;
		} else if (ResourceWaypointPreferences.FILTER_SELECTED.equals(filter)) {
			return filterResource;
		} else if (ResourceWaypointPreferences.FILTER_CHILDREN.equals(filter)) {
			return filterChildren;
		} else if (ResourceWaypointPreferences.FILTER_PROJECT.equals(filter)) {
			return filterProject;
		}
		return filterProject;
	}

}
