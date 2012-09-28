/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.instrumentation.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.instrumentation.DateUtils;
import net.sourceforge.tagsea.instrumentation.InstrumentationPreferences;
import net.sourceforge.tagsea.instrumentation.TagSEAInstrumentationPlugin;
import net.sourceforge.tagsea.instrumentation.network.ui.UploadWizard;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Del Myers
 *
 */
public class NetworkSendJob extends TagSEAOperation {

	/**
	 * @param name
	 */
	public NetworkSendJob() {
		super("Sending TagSEA log info");
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
//		Date lastSend = InstrumentationPreferences.getLastSendDate();
//		if (!TagSEAInstrumentationPlugin.getDefault().today().after(lastSend)) {
//			InstrumentationPreferences.tagSendDate();
//			return Status.OK_STATUS;
//		}
		
		
		monitor.beginTask("Sending TagSEA log info", 10);
		monitor.subTask("Saving log state");
		//force a save.
		try {
			TagSEAInstrumentationPlugin.getDefault().saving(new ISaveContext(){
				public IPath[] getFiles() {return new IPath[0];}
				public int getKind() {return ISaveContext.FULL_SAVE;}
				public int getPreviousSaveNumber() {return 0;}
				public IProject getProject() {return null;}
				public int getSaveNumber() {return 0;}
				public IPath lookup(IPath file) {return null;}
				public void map(IPath file, IPath location) {}
				public void needDelta() {}
				public void needSaveNumber() {}
			});
		} catch (Exception e) {
			String message = "Error sending TagSEA logs" + ((e.getMessage() != null) ? ": " + e.getMessage() : "");
			InstrumentationPreferences.tagSendDate();
			return new Status(
				Status.ERROR,
				TagSEAInstrumentationPlugin.PLUGIN_ID,
				Status.ERROR,
				message,
				e
			);
		}
		monitor.worked(1);
		final File[] files = gatherFiles();
		if (files.length == 0) {
			InstrumentationPreferences.tagSendDate();
			return Status.OK_STATUS;
		}
		final Object[] result = new Object[1];
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				UploadWizard wizard = new UploadWizard(files);
				wizard.setWindowTitle("TagSEA Logs Upload");
				WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
				dialog.open();
				result[0] = wizard.getResult();
			}
		});
		File[] compressFiles = (File[]) result[0];
		if (compressFiles == null || compressFiles.length == 0) {
			InstrumentationPreferences.tagSendDate();
			return Status.OK_STATUS;
		}
	
		Exception caught = null;
		//package the files.
		File file = null;
		try {
			monitor.subTask("Packaging files");
			file = compressLogs(compressFiles);
			monitor.worked(4);
			if (file != null) {
				monitor.subTask("Sending files");
				PostMethod post = new PostMethod(NetworkUtilities.UPLOAD_SCRIPT);
				int id = InstrumentationPreferences.getUID();
				Part[] parts = {new FilePart("TAGSEA" + id, file.getName(), file)};
				post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
				HttpClient client = new HttpClient();
				int status = client.executeMethod(post);
				String resp = NetworkUtilities.readInputAsString(post.getResponseBodyAsStream());
				if (status != 200) {
					IOException ex = new IOException(resp);
					throw (ex);
				}
			} else {
				monitor.done();
			}
		} catch (FileNotFoundException e) {
			caught = e;
		} catch (HttpException e) {
			caught = e;
		} catch (IOException e) {
			caught = e;
		} finally {
			InstrumentationPreferences.tagSendDate();
			monitor.done();
			if (file != null && file.exists()) file.delete();
		}
		if (caught != null) {
			String message = "Error sending TagSEA logs";
			return new Status(
				Status.ERROR,
				TagSEAInstrumentationPlugin.PLUGIN_ID,
				Status.ERROR,
				message,
				caught
			);
		}
		return Status.OK_STATUS;
	}
	
	private File[] gatherFiles() {
		Date lastSendDate = InstrumentationPreferences.getLastSendDate();
		Date today = DateUtils.today();
		IPath stateLocation = TagSEAInstrumentationPlugin.getDefault().getStateLocation();
		DateFormat format = DateUtils.getDateFormat();
		File statePath = stateLocation.toFile();
		String[] files = statePath.list();
		ArrayList<File> filesList = new ArrayList<File>();
		
		for (String fileName : files) {
			File file = new File(statePath, fileName);
			if (file.exists() && !file.isDirectory() && !fileName.endsWith(".zip")) {
				int firstDot = fileName.indexOf('.');
				if (firstDot != -1) {
					String dateString = fileName.substring(0, firstDot);
					dateString = dateString.replace('-', '/');
					try {
						Date fileDate = format.parse(dateString);
						//@tag tagsea.bug.175.fix -author="Del Myers" -date="enCA:31/07/07" : send files that are equal to the last send date as well.
						if (fileDate.before(today) && (fileDate.after(lastSendDate) || fileDate.equals(lastSendDate))) {
							filesList.add(file);
						}
					} catch (ParseException e) {
					}
				}
			}
		}
		return filesList.toArray(new File[filesList.size()]);
	}
	
	private File compressLogs(File[] filesToCompress) throws IOException {
		IPath stateLocation = TagSEAInstrumentationPlugin.getDefault().getStateLocation();
		DateFormat format = DateUtils.getDateFormat();
		String sendName = format.format(new Date()).replace('/', '-') + "-workspace" + stateLocation.toPortableString().hashCode() +".zip";
		File sendFile = stateLocation.append(sendName).toFile();
		if (!sendFile.exists()) {
			sendFile.createNewFile();
		}
		if (filesToCompress.length == 0) {
			sendFile.delete();
			return null;
		}
		ZipOutputStream zipStream;
		zipStream = new ZipOutputStream(new FileOutputStream(sendFile));
		
		for (File file : filesToCompress) {
			FileInputStream inputStream = null;
			try {
				ZipEntry entry = new ZipEntry(file.getName());
				zipStream.putNextEntry(entry);
				inputStream = new FileInputStream(file);
				byte[] buffer = new byte[1024];
				int read = -1;
				while ((read = inputStream.read(buffer)) != -1) {
					zipStream.write(buffer, 0, read);
				}
				
				zipStream.closeEntry();
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
		zipStream.close();
		return sendFile;
	}

}
