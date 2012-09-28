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
package ca.uvic.cs.tagsea.statistics.svn.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.core.TeamException;
import org.eclipse.ui.PlatformUI;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.ISVNRemoteResource;
import org.tigris.subversion.subclipse.core.ISVNRepositoryLocation;
import org.tigris.subversion.subclipse.core.SVNException;
import org.tigris.subversion.subclipse.core.SVNProviderPlugin;
import org.tigris.subversion.subclipse.core.client.OperationManager;
import org.tigris.subversion.subclipse.core.client.OperationProgressNotifyListener;
import org.tigris.subversion.subclipse.core.commands.GetRemoteResourceCommand;
import org.tigris.subversion.subclipse.core.history.ILogEntry;
import org.tigris.subversion.subclipse.core.history.LogEntry;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.subclipse.ui.authentication.PasswordPromptDialog;
import org.tigris.subversion.svnclientadapter.ISVNAnnotations;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.SVNRevision.Number;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.statistics.svn.SVNStatistics;
import ca.uvic.cs.tagsea.statistics.svn.jobs.SimpleJavaCodeScanner.Stats;

/**
 * A job that searches SVN projects and their different revisions to 
 * extrapolate evolution of tags and comments.
 * @author Del Myers
 *
 */
public class SVNCommentScanningJob extends Job {
	private HashMap fileEntryMap;

	class UserPasswordRunnable implements Runnable {
		String pass;
		String username;
		private String label;
		UserPasswordRunnable(String label) {
			pass = null;
			username = null;
			this.label = label;
		}
		public void run() {
			Shell shell  = PlatformUI.
				getWorkbench().
				getActiveWorkbenchWindow().
				getShell();
			final PasswordPromptDialog  dialog = new PasswordPromptDialog(shell, label, "", false);
			dialog.open();
			pass = dialog.getPassword();
			username = dialog.getUsername();
		}	
	}
	/**
	 * @param name
	 */
	public SVNCommentScanningJob() {
		super("Scanning SVN Java files for comments...");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */

	protected IStatus run(IProgressMonitor monitor) {
		IProject[] projects  =
			ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ISVNClientAdapter client;
		List svnResources = new LinkedList();
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			ISVNLocalResource r = SVNWorkspaceRoot.getSVNResourceFor(project);
			try {
				if (r != null && r.isManaged()) {
					svnResources.add(r);
				}
			} catch (SVNException e) {
				//do nothing, continue t the next
			}
		}
		monitor.beginTask("Scanning subversion projects...", svnResources.size()*1000000);
		IPath state = SVNStatistics.getDefault().getStateLocation();
		File tempdir = state.append("temp").toFile();
		if (!tempdir.isDirectory()) {
			if (tempdir.exists()) {
				tempdir.delete();
			}
			tempdir.mkdir();
		}
		deleteTemps(tempdir);
		
		for (Iterator it = svnResources.iterator(); it.hasNext();) {
			ISVNLocalResource svnProject = (ISVNLocalResource) it.next();
			//used to make sure that we don't repeat old comments.
			HashMap fileCommentsMap = new HashMap();
			fileEntryMap = new HashMap();
			monitor.subTask("Getting project information for " + svnProject.getName());
			//create a temp file for each project. They will be uploaded
			//to the server.
			String projectName = svnProject.getName(); //names are guaranteed unique
						
			try {
				ISVNRemoteResource remote=null;
				for (int tries = 0; remote==null && tries < 10; tries++) {
					try {
						remote = svnProject.getLatestRemoteResource();
					} catch (Exception e) {};
					if (remote == null) {
						SVNStatistics.getDefault().getLog().log(new Status(IStatus.WARNING, SVNStatistics.PLUGIN_ID, IStatus.WARNING, "could not get remote resource for " + svnProject.getName() + "... trying again.", null));
						try {
//							@tag tagsea.bug.subclipse : it seems that sublcipse has a synchronization problem. Wait a little while and try again.
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							return new Status(IStatus.ERROR, SVNProviderPlugin.ID, IStatus.ERROR, "Could not communicate with remote resource.", null);
						}
					}
				}
				if (remote == null) {
					SVNStatistics.getDefault().getLog().log(new Status(IStatus.ERROR, SVNStatistics.PLUGIN_ID, 0, "Could not get a remote resource", null));
					monitor.worked(1000000);
					continue;
				}
				ISVNRepositoryLocation repository = remote.getRepository();
				
				
				client = repository.getSVNClient();
//				@tag tagsea.statistics.enhance : It seems best to use this password callback because that way, the passwords can be saved with the workspace. But, it might be confusing to see for the first time.
				client.addPasswordCallback(SVNProviderPlugin.getPlugin().getSvnPromptUserPassword());
				SVNRevision.Number revision = remote.getLastChangedRevision();
				
				long revNum = revision.getNumber();
				int revisionWork = 1000000;
				ILogEntry[] entries;
				try {
					entries = remote.getLogEntries(new NullProgressMonitor());
				} catch (TeamException e1) {
					monitor.worked(revisionWork);
					e1.printStackTrace();
					continue;
				}
				if (revNum > 0) {
					revisionWork = 1000000/(int)revNum;
				}
				for (int ei = 0; ei < entries.length; ei++) {
					ILogEntry entry = entries[ei];
					revision = entry.getRevision();
					File tempFile = state.append(projectName +"." + getDateString()+"."+revision.getNumber()+ ".comments.txt").toFile();
					if (tempFile.exists()) {
						tempFile.delete();
					}
					try {
						tempFile.createNewFile();
					} catch (IOException e) {
						//skip to the next one.
						continue;
					}
					PrintStream out;
					try {
						out = new PrintStream(tempFile);
					}  catch (IOException e) {
						continue;
					}
					
					out.println(remote.getUrl() + " Revision:" + revision.getNumber());
					monitor.subTask("Finding java resources: " + svnProject.getName() + "...");
					SubProgressMonitor revMonitor = new SubProgressMonitor(monitor, revisionWork);
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					monitor.subTask("temporarily checking out " + svnProject.getName()+"...");
					SubProgressMonitor subPm = new SubProgressMonitor(revMonitor, 10);
					try {
						OperationManager.getInstance().beginOperation(client, new OperationProgressNotifyListener(subPm));
						client.checkout(remote.getUrl(), new File(tempdir, svnProject.getName()), revision, true);
					} catch (SVNClientException e) {
						//I wish that there were a better way to do this, but it seem that we
						//have to just keep decrementing it.
						revMonitor.done();
						revNum--;
						revision = new SVNRevision.Number(revNum);
						continue;
					} finally {
						OperationManager.getInstance().endOperation();
						subPm.done();
					}
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					List files = findJavaFiles(tempdir);
					int work = 0;
					if (files.size() > 0) {
						work = (revisionWork-20)/files.size();
						for (Iterator fit = files.iterator(); fit.hasNext();) {
							File file = (File) fit.next();
						
							monitor.subTask("Scanning java file....");
							TreeSet commentSet = (TreeSet) fileCommentsMap.get(file.getAbsolutePath());
							if (commentSet == null) {
								commentSet = new TreeSet();
								fileCommentsMap.put(file.getAbsolutePath(), commentSet);
							}
							FileReader reader = new FileReader(file);
							StringBuilder builder = new StringBuilder();
							char[] buffer = new char[1024];
							int read = 0;
							while ((read = reader.read(buffer)) >= 0) {
								builder.append(buffer, 0, read);
							}
							reader.close();
							ISVNAnnotations ann = null;
							try {
								//get blame information.
								List fileLogs = getLogEntries(file, client, repository);
								//don't do extra work if this file doesn't have a log for this revision.
								if (!checkRevision(fileLogs, revision)) {
									monitor.worked(work);
									//System.out.println("Skipped " + file.getAbsolutePath() + " revision " + revision.getNumber());
									continue;
								}
								ann = client.annotate(file, revision, revision);
							} catch (SVNClientException e) {
							} catch (TeamException e) {
							}
							if (monitor.isCanceled()) {
								return Status.CANCEL_STATUS;
							}
							SubProgressMonitor scanMonitor = new SubProgressMonitor(revMonitor, work);
							Stats s = SimpleJavaCodeScanner.scan(builder.toString(), scanMonitor);
							if (monitor.isCanceled()) {
								return Status.CANCEL_STATUS;
							}
							monitor.worked(work);
							out.println("New/Changed Tags:");
							for (int ci = 0; ci < s.TAGS.length; ci++) {
								Comment c = s.TAGS[ci];
								if (!commentSet.contains(c)) {
									commentSet.add(c);
									String author = getAuthor(c, ann);
									out.println(c.toString()+"\tauthor="+author);
								}
							}
							out.println("New/Changed Tasks:");
							for (int ci = 0; ci < s.TASKS.length; ci++) {
								Comment c = s.TASKS[ci];
								if (!commentSet.contains(c)) {
									commentSet.add(c);
									String author = getAuthor(c, ann);
									out.println(c.toString()+"\tauthor="+author);
								}
							}
							out.println("New/Changed Other:");
							for (int ci = 0; ci < s.NONTAGS.length; ci++) {
								Comment c = s.NONTAGS[ci];
								if (!commentSet.contains(c)) {
									commentSet.add(c);
									String author = getAuthor(c, ann);
									out.println(c.toString()+"\tauthor="+author);
								}
							}
							
							if (monitor.isCanceled()) {
								return Status.CANCEL_STATUS;
							}
						}
					}
					if (work == 0) {
						revMonitor.worked(revisionWork-10);
					}
					monitor.subTask("Sending and Deleting temporary files...");
					out.close();
					sendFile(tempFile);
					deleteTemps(tempdir);
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					revMonitor.done();
					monitor.worked(revisionWork-20);
					
				}
			} catch (SVNException e) {
				return new Status(IStatus.ERROR, SVNStatistics.PLUGIN_ID, 0, e.getMessage(), e);
			}  catch (IOException e) {
				return new Status(IStatus.ERROR, SVNStatistics.PLUGIN_ID, 0, e.getMessage(), e);
			}
		}
		return Status.OK_STATUS;
	}
	
	/**
	 * @param fileLogs
	 * @param revision
	 * @return
	 */
	private boolean checkRevision(List fileLogs, Number revision) {
		for (Iterator i = fileLogs.iterator(); i.hasNext();) {
			ILogEntry entry = (ILogEntry) i.next(); 
			if (entry.getRevision().equals(revision)) {
				return true;
			}
		}
		return false;
	}

	List getLogEntries(File file, ISVNClientAdapter client, ISVNRepositoryLocation repository) throws SVNClientException, TeamException {
		List logEntries = (List) fileEntryMap.get(file.getAbsolutePath());
		if (logEntries == null) {
			ISVNInfo info = client.getInfo(file);
			SVNUrl url = info.getUrl();
			GetRemoteResourceCommand command = new GetRemoteResourceCommand(repository, url, info.getRevision());
			command.run(new NullProgressMonitor());
			ISVNRemoteResource resource = command.getRemoteResource();
			ILogEntry[] entries = resource.getLogEntries(new NullProgressMonitor());
			logEntries = new ArrayList(Arrays.asList(entries));
			fileEntryMap.put(file.getAbsolutePath(), logEntries);
		}

		return logEntries;
	}
	
	/**
	 * @param files
	 */
	private IStatus sendFile(File file) {
		String uploadScript = "http://stgild.cs.uvic.ca/cgi-bin/tagSEAUpload.cgi";
		int id = TagSEAPlugin.getDefault().getUserID();
		if (id < 0) {
			id = askForID();
			if (id == 0) {
				//just do nothing.
				return Status.OK_STATUS;
			}
		}
		HttpClient client = new HttpClient();
		IStatus s = Status.OK_STATUS;
		String message = "";

		try {
			PostMethod post = new PostMethod(uploadScript);
			Part[] parts = { new StringPart("KIND", "subversion"), new FilePart("MYLAR" + id, file.getName(), file) };
			post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
			int status = client.executeMethod(post);
			String resp = getData(post.getResponseBodyAsStream());
			if (status != 200) {
				message += resp + ";";
				s = new Status(IStatus.ERROR, SVNStatistics.PLUGIN_ID, status, message, null);
			}
		} catch (IOException e) {
			message += file.getAbsolutePath() + " could not be sent;";
			s = new Status(IStatus.ERROR, SVNStatistics.PLUGIN_ID, 0, message, e);
		}

		return s;
	}
	
	/**
	 * @return
	 */
	private int askForID() {
		Display disp = Display.getDefault();
		if (SVNStatistics.getDefault().getPreferenceStore().getBoolean("register.asked")) {
			return -1;
		}
		disp.syncExec(new Runnable(){
			public void run() {
				Shell shell = 
					SVNStatistics.
						getDefault().
						getWorkbench().
						getActiveWorkbenchWindow().
						getShell();
				MessageDialogWithToggle d = MessageDialogWithToggle.openYesNoQuestion(
					shell, "Register TagSEA", 
					"TagSEA is not registered.\n\n" +
					"You have installed the TagSEA Subversion plugin. By doing so, you have" +
					" agreed that you would like to send the developers of TagSEA some information" +
					" about how you comment your code. This can be done, however, you must first" +
					" register TagSEA.\n\n" +
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
					SVNStatistics.getDefault().getPreferenceStore().setValue("register.asked", true);
				}
			}
		});
		return TagSEAPlugin.getDefault().getUserID();
	}

	private String getData(InputStream i) {
		String s = "";
		String data = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(i));
		try {
			while ((s = br.readLine()) != null)
				data += s;
		} catch (IOException e) {
			SVNStatistics.getDefault().getLog().log(new Status(IStatus.ERROR, SVNStatistics.PLUGIN_ID, 0, e.getMessage(), e));
		}
		return data;
	}

	/**
	 * @param c
	 * @param ann
	 * @return
	 */
	private String getAuthor(Comment c, ISVNAnnotations ann) {
		String author = "unknown";
		if (ann != null) {
			TreeSet authors = new TreeSet();
			int i = c.START_LINE;
			for (i=c.START_LINE; i <= c.END_LINE;i++){
				String a = ann.getAuthor(i);
				if (a == null) a = "";
				authors.add(a);
			}
			author = "";
			for (Iterator it = authors.iterator(); it.hasNext();){ 
				String a = (String) it.next();
				author += a+",";
			}
		}
		return author;
	}

	List findJavaFiles(File dir) {
		LinkedList files = new LinkedList();
		if (!dir.isDirectory()) {
			return files;
		}
		String[] subs = dir.list();
		for (int i = 0; i < subs.length; i++) {
			String next = subs[i];
			File nextFile = new File(dir, next);
			if (nextFile.isDirectory()) {
				files.addAll(findJavaFiles(nextFile));
			} else {
				if (next.endsWith(".java")) {
					files.add(nextFile);
				}
			}
		}
		return files;
	}
	//deletes all sub files and folders to the given directory.
	private final void deleteTemps(File dir) {
		String[] subs = dir.list();
		for (int i = 0; i < subs.length; i++) {
			String next = subs[i];
			File nextFile = new File(dir, next);
			if (nextFile.isDirectory()) {
				deleteTemps(nextFile);
			}
			if (!nextFile.delete()) {
				System.err.println("Could not delete");
			}
		}
	}
	
	private String getDateString() {
		Date d = new Date(System.currentTimeMillis());
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
		String key = format.format(d);
		key = key.replace('/', '-');
		key = key.replace('\\', '\\');
		key = key.replace(' ', '_');
		key = key.replace(':', '_');
		key = key.replace(';', '.');
		return key;
	}

}
