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
package ca.uvic.cs.tagsea.research;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * TODO comment
 * @author Del Myers
 *
 */
public class GetIDWithProgress implements IRunnableWithProgress {

	String uidScript = "http://stgild.cs.uvic.ca/cgi-bin/tagseauid.cgi";
	
	private NameValuePair first;
	private NameValuePair last;
	private NameValuePair email;
	private NameValuePair job;
	private NameValuePair size;
	private NameValuePair buisness;
	private NameValuePair anon;
	private int status;
	private String resp;
	private boolean failed;
	private int uid;

	
	/**
	 * 
	 */
	public GetIDWithProgress(String firstName, String lastName, String emailAddress, boolean anonymous, String jobFunction,
			String companySize, String companyFunction) {
		this.first = new NameValuePair("firstName", firstName);
		this.last = new NameValuePair("lastName", lastName);
		this.email = new NameValuePair("email", emailAddress);
		this.job = new NameValuePair("jobFunction", jobFunction);
		this.size = new NameValuePair("companySize", companySize);
		this.buisness = new NameValuePair("companyBuisness", companyFunction);
		this.anon = null;
		if (anonymous) {
			anon = new NameValuePair("anonymous", "true");
		} else {
			anon = new NameValuePair("anonymous", "false");
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		GetMethod getUidMethod = new GetMethod(uidScript);
		getUidMethod.setQueryString(new NameValuePair[] { first, last, email, job, size, buisness, anon });
		monitor.beginTask("Get User Id", 1);
		HttpClient client = new HttpClient();
		try {
			status = client.executeMethod(getUidMethod);

			resp = getData(getUidMethod.getResponseBodyAsStream());

			// release the connection to the server
			getUidMethod.releaseConnection();
		} catch (Exception e) {
			// there was a problem with the file upload so throw up
			// an error
			// dialog to inform the user and log the exception
			failed = true;
			throw new InvocationTargetException(e);
		}
		
		if (status != 200) {
			// there was a problem with the file upload so throw up an error
			// dialog to inform the user

			failed = true;

			// there was a problem with the file upload so throw up an error
			// dialog to inform the user
			MessageDialog.openError(null, "Error Getting User ID", "There was an error getting a user id: \n"
					+ "HTTP Response Code " + status + "\n" + "Please try again later");
		} else {
			resp = resp.substring(resp.indexOf(":") + 1).trim();
			uid = Integer.parseInt(resp);
		}
		
		monitor.worked(1);
		monitor.done();
	}

	private String getData(InputStream i) {
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

	public int getUID() {
		return uid;
	}
	
	public boolean success() {
		return !failed;
	}
}
