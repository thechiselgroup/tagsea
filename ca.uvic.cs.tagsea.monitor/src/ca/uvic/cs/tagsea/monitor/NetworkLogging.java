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
package ca.uvic.cs.tagsea.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import ca.uvic.cs.tagsea.TagSEAPlugin;


/**
 * Uploads a log file to the UVic server.
 * @author Del Myers
 *
 */
public class NetworkLogging {
	boolean failed;
	int status;
	
	
	
	
	String resp;
	String uploadScript = "http://stgild.cs.uvic.ca/cgi-bin/tagSEAUpload.cgi";
	
	public synchronized boolean uploadForDate(Date d) throws IOException {
		File f = SimpleDayLog.findLogFileForDay(d);
		if (f.length() == 0) return true;
	
		PostMethod post = new PostMethod(uploadScript);
		Display disp = TagSEAMonitorPlugin.getDefault().getWorkbench().getDisplay(); 
		int id = getUID();
		if (id < 0) {
			//error getting the user id. Quit.
			return false;
		}
		Part[] parts = { new StringPart("KIND", "log"), new FilePart("MYLAR" + id, f.getName(), f) };
		post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
		HttpClient client = new HttpClient();
		int status = client.executeMethod(post);
		resp = getData(post.getResponseBodyAsStream());
		if (status != 200) {
			disp.syncExec( new Runnable(){
				public void run() {
					MessageDialog.openError(null, "Error Sending File", resp);
				}
			});
			return false;
			
		}
		MonitorPreferences.setLastSendDate(d);
		return true;
		
	}

	private String getData(InputStream i) {
		String s = "";
		String data = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(i));
		try {
			while ((s = br.readLine()) != null)
				data += s;
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
		}
		return data;
	}
	/**
	 * @return
	 */
	private int getUID() {
		return TagSEAPlugin.getDefault().getUserID();
	}
}
