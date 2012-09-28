package net.sourceforge.tagsea.mylyn.task;

import net.sourceforge.tagsea.mylyn.core.BuildMapsJob;
import net.sourceforge.tagsea.mylyn.core.LocationDescriptor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskList;

public class BuildTaskMaps extends BuildMapsJob {

	private IMarker[] markers;

	public BuildTaskMaps(){
		this("Building Eclipse task map");
	}
	public BuildTaskMaps(String name) {
		super(name);
	}

	// @tag tagsea.mylyn.refactor : Refactor with run method from
	// Waypoints.BuildMaps
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		super.run(monitor);
		
		TaskList taskList = taskManager.getTaskList();
		if(taskList == null){
			return Status.CANCEL_STATUS;
		}

		try {
			markers = ResourcesPlugin.getWorkspace().getRoot()
					.findMarkers(IMarker.TASK, true, IResource.DEPTH_INFINITE);
			for (AbstractTask task : taskList.getAllTasks()) {
				String notes = task.getNotes();
				String[] lines = notes.split("\n");
				for (String line : lines) {
					// Handle case where there is no information
					if (line.isEmpty())
						continue;

					IMarker marker = markerFromText(line);
					if(marker != null){
						System.out.println("Adding --> " + marker.toString());
						TaskMylynPlugin.getDefault().addMarker(marker, task);
					}
				}
			}

		} catch (CoreException e) {
			return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}

	private IMarker markerFromText(String line) {
		LocationDescriptor descriptor = LocationDescriptor.createFromText(TaskHyperlink.LINK_TAG, "", line);
		for (IMarker marker : markers) {
			String desc = TaskUtils.getDescription(marker);
			String location = TaskUtils.getLocation(marker);
			if(desc.equals(descriptor.getDescription()) && location.equals(descriptor.getLocation())){
				return marker;
			}
		}
		return null;
	}

}
