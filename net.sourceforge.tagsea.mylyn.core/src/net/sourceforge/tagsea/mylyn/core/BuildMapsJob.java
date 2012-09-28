package net.sourceforge.tagsea.mylyn.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

public class BuildMapsJob extends Job {

	private static final long JOB_DELAY = 100;
	protected TaskListManager taskManager;

	public BuildMapsJob(String name) {
		super(name);
		taskManager = TasksUiPlugin.getTaskListManager();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if(taskManager.isTaskListInitialized() == false){
			System.err.println("Reschedling job");
			this.schedule(JOB_DELAY);
		}
		return Status.OK_STATUS;
	}
	
	
}
