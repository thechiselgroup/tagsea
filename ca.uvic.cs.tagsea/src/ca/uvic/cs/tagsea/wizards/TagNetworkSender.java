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
package ca.uvic.cs.tagsea.wizards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.eclipse.core.runtime.IProgressMonitor;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * Sends a string of text to the UVic server. Warning: not to be exposed outside of TagSEA.
 * @author Del Myers
 *
 */
class TagNetworkSender {
	
	public static synchronized void send(String xml, IProgressMonitor sendMonitor, int id) throws InvocationTargetException {
		sendMonitor.beginTask("Uploading Tags", 100);
		sendMonitor.subTask("Saving Temporary file...");
		File location = TagSEAPlugin.getDefault().getStateLocation().toFile();
		File temp = new File(location, "tagsea.temp.file.txt");
		if (temp.exists()) {
			String message = "Unable to send tags. Unable to create temporary file.";
			IOException ex = new IOException(message);
			throw (new InvocationTargetException(ex, message));
		}
		try {
			FileWriter writer = new FileWriter(temp);
			writer.write(xml);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new InvocationTargetException(e);
		}
		sendMonitor.worked(5);
		sendMonitor.subTask("Uploading Tags...");
		String uploadScript = "http://stgild.cs.uvic.ca/cgi-bin/tagSEAUpload.cgi";
		PostMethod post = new PostMethod(uploadScript);
		
		
		String fileName = getToday() + ".txt";
		try {
			Part[] parts = { new StringPart("KIND", "tag"), new FilePart("MYLAR" + id, fileName, temp) };
			post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
			HttpClient client = new HttpClient();
			int status = client.executeMethod(post);
			String resp = getData(post.getResponseBodyAsStream());
			if (status != 200) {
				IOException ex = new IOException(resp);
				throw (ex);
			}
		} catch (IOException e) {
			throw new InvocationTargetException(e, e.getLocalizedMessage());
		} finally {
			sendMonitor.worked(90);
			sendMonitor.subTask("Deleting Temporary File");
			temp.delete();
			sendMonitor.done();
		}
		
	}
	
	private static String getData(InputStream i) {
		String s = "";
		String data = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(i));
		try {
			while ((s = br.readLine()) != null)
				data += s;
		} catch (IOException e) {
			TagSEAPlugin.log("",e);
		}
		return data;
	}
	
	private static String getToday() {
		Date today = new Date(System.currentTimeMillis());
		String todayString = DateFormat.getDateInstance(DateFormat.SHORT).format(today);
		todayString = todayString.replace('/','_');
		todayString = todayString.replace('\\', '-');
		todayString = todayString.replace('.', '_');
		todayString = todayString.replace(':', '_');
		todayString = todayString.replace(';', '_');
		return todayString;
	}

}
