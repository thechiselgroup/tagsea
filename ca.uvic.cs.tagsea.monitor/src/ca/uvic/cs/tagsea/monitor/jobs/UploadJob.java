/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.monitor.jobs;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.monitor.MonitorPreferences;
import ca.uvic.cs.tagsea.monitor.NetworkLogging;
import ca.uvic.cs.tagsea.monitor.TagSEAMonitorPlugin;

public class UploadJob extends Job {
	IStatus status;
	/**
	 * @param name
	 */
	public UploadJob(String name) {
		super(name);
		this.status = Status.OK_STATUS;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected synchronized IStatus run(IProgressMonitor monitor) {
		//get the logs that have to be uploaded.
		monitor.beginTask(getName(), 101);
		IPreferenceStore prefs = TagSEAMonitorPlugin.getDefault().getPreferenceStore();
		String date = prefs.getString(MonitorPreferences.LAST_DATE_PREF);
		DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT);
		Date today = new Date(System.currentTimeMillis());
		NetworkLogging logging = new NetworkLogging();
		int id = TagSEAPlugin.getDefault().getUserID();
		//ask to register if we haven't already.
		if (id < 0 && !MonitorPreferences.hasAskedToRegister()) {
			Display disp = TagSEAMonitorPlugin.getDefault().getWorkbench().getDisplay();
			disp.syncExec(new Runnable(){
				public void run() {
					Shell shell = 
						TagSEAMonitorPlugin.
							getDefault().
							getWorkbench().
							getActiveWorkbenchWindow().
							getShell();
					MessageDialogWithToggle d = MessageDialogWithToggle.openYesNoQuestion(
						shell, "Register TagSEA", 
						"TagSEA is not registered.\n\n" +
						"You have requested that a log of your interactions with TagSEA be" +
						" uploaded to the University of Victoria for research purposes, but" +
						" you have not yet registered TagSEA with the University of Victoria." +
						" You must register before these logs can be uploaded.\n\n " +
						"Selecting 'Yes' will prompt you to register TagSEA. Selecting 'No'" +
						" will cancel the current upload.\n\n" +
						" You may manually upload your logging data, by selecting the 'Upload'" +
						" action in the TagSEA log view (Window>Show Views>Other>TagSEA>TagSEA Log)" +
						" You may also register at any time using the TagSEA preference page.\n\n" +
						"Would you like to register now?",
						" Don't ask again.",
						false,
						null,
						null
					);
					int result = 0;
					int code = d.getReturnCode();
					if (code == IDialogConstants.YES_ID) {
						result = TagSEAPlugin.getDefault().askForUserID();
					}
					if (d.getToggleState() && result != -2) {
						MonitorPreferences.setAskedToRegister(true);
					}
				}
			});
		}
		id = TagSEAPlugin.getDefault().getUserID();
		if (id < 0) {
			//id is still 0. cancel
			monitor.done();
			this.status = Status.CANCEL_STATUS;
			return Status.CANCEL_STATUS;
		}
		try {
			today = f.parse(f.format(today));
			ArrayList<Date> newDates = new ArrayList<Date>();
			if (!"".equals(date)) {
				Date last = f.parse(date);
				Date[] ds = TagSEAMonitorPlugin.getDefault().getLogDates();
				Arrays.sort(ds);
				for (Date current : ds) {
					if (current.after(last) && current.before(today)) {
						newDates.add(current);
					} else if (current.after(today)) {
						//shouldn't happen
						break;
					}
				}
			} else {
				Date[] ds = TagSEAMonitorPlugin.getDefault().getLogDates();
				for (Date current :ds) {
					if (current.before(today)) {
						newDates.add(current);
					}
				}
			}
			monitor.worked(1);
			int work = (int)Math.floor(100.0/newDates.size());
			for (Date current : newDates) {
				monitor.subTask(DateFormat.getDateInstance().format(current));
				if (!logging.uploadForDate(current)) {
					monitor.done();
					this.status = new Status(Status.ERROR, TagSEAMonitorPlugin.PLUGIN_ID, 0, "Unable to upload log.", null);
					return this.status;
				}
				monitor.worked(work);
			}
		} catch (ParseException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
			return new Status(Status.ERROR,TagSEAMonitorPlugin.PLUGIN_ID, 0, e.getLocalizedMessage(), e);
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
			return new Status(Status.ERROR,TagSEAMonitorPlugin.PLUGIN_ID, 0, e.getLocalizedMessage(), e);
		} finally {
			monitor.done();
		}
		this.status = Status.OK_STATUS;
		return Status.OK_STATUS;
	}
	
	public IStatus getStatus() {
		return status;
	}
	
}