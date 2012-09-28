package ca.uvic.cs.tagsea.statistics.svn;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.ui.IStartup;

import ca.uvic.cs.tagsea.statistics.svn.jobs.SVNCommentScanningJob;

public class Starter implements IStartup {

	public void earlyStartup() {
		Timer t = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				SVNCommentScanningJob job = new SVNCommentScanningJob();
				job.schedule();
			}
		};
		//once a day.
		t.schedule(task, 0, 1000*60*60*24);
	}

}
