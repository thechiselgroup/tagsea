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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import net.sourceforge.tagsea.instrumentation.InstrumentationPreferences;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * A runnable that registers the user with the server.
 * @author Del Myers
 *
 */
public class RegisterWithProgress implements IRunnableWithProgress {

	private String firstName;
	private String lastName;
	private String email;
	private boolean anonymous;
	private String job;
	private String company;
	private String companySize;
	private String fieldOfWork;
	
	private int status;
	private String response;
	private int userID;

	/**
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param anonymous
	 * @param job
	 * @param company
	 * @param companySize
	 * @param fieldOfWork
	 */
	public RegisterWithProgress(String firstName, String lastName, String email, boolean anonymous, String job, String company, String companySize, String fieldOfWork) {
		this.firstName = notNull(firstName);
		this.lastName = notNull(lastName);
		this.email = notNull(email);
		this.anonymous = anonymous;
		this.job = notNull(job);
		this.company = notNull(company);
		this.companySize = notNull(companySize);
		this.fieldOfWork = notNull(fieldOfWork);
		this.status = 200;
		this.response = "";
		this.userID = -1;
	}

	/**
	 * @param firstName2
	 * @return
	 */
	private String notNull(String string) {
		return (string != null) ? string : "";
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		GetMethod getUidMethod = new GetMethod(NetworkUtilities.REGISTER_SCRIPT);
		getUidMethod.setQueryString(new NameValuePair[] { 
			new NameValuePair("firstName", firstName),
			new NameValuePair("lastName", lastName),
			new NameValuePair("email", email),
			new NameValuePair("jobFunction", job),
			new NameValuePair("company", company),
			new NameValuePair("companySize", companySize),
			new NameValuePair("companyBusiness", fieldOfWork),
			new NameValuePair("anonymous", Boolean.toString(anonymous))
		});
		monitor.beginTask("Get User Id", 1);
		HttpClient client = new HttpClient();
		
			try {
				status = client.executeMethod(getUidMethod);

				response = NetworkUtilities.readInputAsString(getUidMethod.getResponseBodyAsStream());
			} catch (HttpException e) {
				//there was a problem with the file upload so throw up
				// an error
				// dialog to inform the user and log the exception
				status = 500;
				response = e.getMessage();
				throw new InvocationTargetException(e, response);
			} catch (IOException e) {
				//there was a problem with the file upload so throw up
				// an error
				// dialog to inform the user and log the exception
				status = 500;
				response = e.getMessage();
				throw new InvocationTargetException(e, response);
			} finally {
//				 release the connection to the server
				getUidMethod.releaseConnection();
			}
		
		if (status != 200) {
		} else {
			String uidString = response.substring(response.indexOf(":") + 1).trim();
			userID = Integer.parseInt(uidString);
			InstrumentationPreferences.setAnonymous(anonymous);
			InstrumentationPreferences.setAskForRegistration(false);
			InstrumentationPreferences.setCompany(company);
			InstrumentationPreferences.setCompanySize(companySize);
			InstrumentationPreferences.setEmail(email);
			InstrumentationPreferences.setFieldOfWork(fieldOfWork);
			InstrumentationPreferences.setFirstName(firstName);
			InstrumentationPreferences.setJob(job);
			InstrumentationPreferences.setLastName(lastName);
			InstrumentationPreferences.setUID(userID);
			InstrumentationPreferences.setMonitoringEnabled(true);
			InstrumentationPreferences.setUploadInterval(1);
		}
		
		monitor.worked(1);
		monitor.done();
	}
	
	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}
	
	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}
	
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	
	public boolean success() {
		return status == 200;
	}

}
