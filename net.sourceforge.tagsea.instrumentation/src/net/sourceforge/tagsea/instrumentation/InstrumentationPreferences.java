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
package net.sourceforge.tagsea.instrumentation;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Stores the preferences for the instrumenation plugin.
 * @author Del Myers
 *
 */
public class InstrumentationPreferences {
	public static final String FIRST_NAME = ".firstname";
	public static final String LAST_NAME = ".lastname";
	public static final String EMAIL = ".email";
	public static final String UID = ".uid";
	public static final String JOB = ".job";
	public static final String COMPANY = ".company";
	public static final String ASK = ".ask";
	public static final String COMPANY_SIZE = ".companysize";
	public static final String COMPANY_WORK = ".companywork";
	public static final String ANONYMOUS = ".anonymous";
	public static final String MONITORING = ".monitoring";
	public static final String SEND_DATE = ".senddate";
	public static final String SEND_INTERVAL =".sendInterval";
	private static final String REGISTRATION_DATE = ".registrationDate";
	private static final String MODEL_SESSION_NUMBER = ".modelsessionNumber.";
	private static final String UI_SESSION_NUMBER = ".uisessionNumber";
	
	//@tag tagsea.instrumentation.2008.january : set the prefix to be in january 2008 to make sure that there is no name collision
	private static final String PREFIX = "net.sourceforge.tagsea.instrumentation.prefs.january2008";
	private static boolean modelSanityChecked = false;
	private static boolean uiSanityChecked = false;
	
	public static boolean isRegistered() {
		return getUserPreferences().getInt(UID, -1) != -1;
	}
	
	public static int getUID() {
		return getUserPreferences().getInt(UID, -1);
	}
	
	public static void setUID(int id) {
		Preferences prefs = getUserPreferences();
		prefs.putInt(UID, id);
		String registration = prefs.get(REGISTRATION_DATE, null);
		if (registration == null) {
			//set the registration date to today.
			Date today = DateUtils.today();
			prefs.put(REGISTRATION_DATE, DateUtils.toString(today));
		}
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}
	
	public static String getFirstName() {
		return getUserPreferences().get(FIRST_NAME, "");
	}
	
	public static void setFirstName(String name) {
		getUserPreferences().put(FIRST_NAME, name);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}
	
	public static String getLastName() {
		return getUserPreferences().get(LAST_NAME, "");
	}
	
	public static void setLastName(String name) {
		getUserPreferences().put(LAST_NAME, name);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}
	
	public static String getEmail() {
		return getUserPreferences().get(EMAIL, "");
	}
	
	public static void setEmail(String email) {
		getUserPreferences().put(EMAIL, email);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}
	
	public static String getJob() {
		return getUserPreferences().get(JOB, "");
	}
	
	public static void setJob(String job) {
		getUserPreferences().put(JOB, job);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}
	
	public static String getCompany() {
		return getUserPreferences().get(COMPANY, "");
	}
	
	public static void setCompany(String company) {
		getUserPreferences().put(COMPANY, company);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}
	
	public static String getCompanySize() {
		return getUserPreferences().get(COMPANY_SIZE, "");
	}
	
	public static void setCompanySize(String size) {
		getUserPreferences().put(COMPANY_SIZE, size);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}
	
	public static String getFieldOfWork() {
		return getUserPreferences().get(COMPANY_WORK, "");
	}
	
	public static void setFieldOfWork(String field) {
		getUserPreferences().put(COMPANY_WORK, field);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}
	
	public static boolean getAskForRegistration() {
		return getUserPreferences().getBoolean(ASK, true);
	}
	
	public static void setAskForRegistration(boolean ask) {
		getUserPreferences().putBoolean(ASK, ask);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}
	
	public static boolean isAnonymous() {
		return getUserPreferences().getBoolean(ANONYMOUS, false);
	}
	
	public static void setAnonymous(boolean anonymous) {
		getUserPreferences().putBoolean(ANONYMOUS, anonymous);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}
	
	private static Preferences getPreferenceNode() {
		
		IPreferencesService service = Platform.getPreferencesService();
		//@tag tagsea preference.location tagsea.debugging : the location of the preferences is in .metadata/.plugins/org.eclipse.pde.core/<workspace dir>
		Preferences instrumentationNode = service.getRootNode().node(ConfigurationScope.SCOPE).node(PREFIX);
		try {
			
			instrumentationNode.flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
		return instrumentationNode;
	}
	
	private static Preferences getUserPreferences() {
		String user = System.getProperties().getProperty("user.name");
		return getPreferenceNode().node(user);
	}

	/**
	 * @param selection
	 */
	public static void setMonitoringEnabled(boolean monitoring) {
		getUserPreferences().putBoolean(MONITORING, monitoring);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}

	/**
	 * @return
	 */
	public static boolean isUploadEnabled() {
		return getUploadInterval() > 0;
	}
	
	public static Date getLastSendDate() {
		String dateString = getUserPreferences().get(SEND_DATE, null);
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
		if (dateString != null)	{	
			try {
				return format.parse(dateString);
			} catch (ParseException e) {
			}
		}
		return new Date(0);
	}
	
	public static Date getNextSendDate() {
		Calendar c = Calendar.getInstance();
		String registrationDateString = getUserPreferences().get(REGISTRATION_DATE, null);
		Date registration = DateUtils.fromString(registrationDateString);
		if (registrationDateString == null) {
			if (getUID() != -1) {
				//@tag tagsea.bug.154.fix.update : make sure that there is a registration date
				//set the date to yesterday.
				registration = new Date((DateUtils.today().getTime()-1000*60*60*24));
				Preferences prefs = getUserPreferences();
				prefs.put(REGISTRATION_DATE, DateUtils.toString(registration));
				try {
					prefs.flush();
				} catch (BackingStoreException e) {
				}
			}
		}
		//@tag tagsea.bug.154.fix : make sure that the logs get sent after the registration date.
		if (registration == null || DateUtils.today().equals(registration)) {
			c.setTime(DateUtils.today());
		} else {
			c.setTime(getLastSendDate());
		}
		//TagSEAInstrumentationPlugin.getDefault().getLog().log(new Status(Status.INFO, TagSEAInstrumentationPlugin.PLUGIN_ID, "Last send date " + c.getTime().toString()));
		switch (getUploadInterval()) {
			case 0:
				return null;
			case 1:
				c.add(Calendar.DAY_OF_YEAR, 1);
				break;
			case 2:
				c.add(Calendar.WEEK_OF_YEAR, 1);
//				update to last week on Friday.
				while (c.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
					c.add(Calendar.DAY_OF_YEAR, -1);
				}
				break;
			case 3:
				while (c.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
					c.add(Calendar.DAY_OF_YEAR, -1);
				}
				c.add(Calendar.WEEK_OF_YEAR, 2);
				break;
			default:
				return null;
		}
		
		return c.getTime();
	}
	
	
	/**
	 * @return
	 */
	public static int getUploadInterval() {
		return getUserPreferences().getInt(SEND_INTERVAL, 0);
	}
	
	public static void setUploadInterval(int interval) {
		getUserPreferences().putInt(SEND_INTERVAL, interval);
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}

	public static void tagSendDate() {
		Date today = DateUtils.today();
		Calendar sendDate = Calendar.getInstance();
		sendDate.setTime(today);
		switch(getUploadInterval()) {
		case 0:
		case 1:
			break;
		case 2:
		case 3:
			//update to last week on Friday.
			while (sendDate.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
				sendDate.add(Calendar.DAY_OF_YEAR, -1);
			}
			break;
		}
		getUserPreferences().put(SEND_DATE, DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).format(sendDate.getTime()));
		try {
			getUserPreferences().flush();
		} catch (BackingStoreException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
	}

	/**
	 * @return
	 */
	public static boolean isMonitoringEnabled() {
		return getUserPreferences().getBoolean(MONITORING, false);
	}
	
	public static void addPreferenceChangeListener(IPreferenceChangeListener listener) {
		((IEclipsePreferences)getUserPreferences()).addPreferenceChangeListener(listener);
	}
	
	public static void removePreferenceChangeListener(IPreferenceChangeListener listener) {
		((IEclipsePreferences)getUserPreferences()).removePreferenceChangeListener(listener);
	}
	
	/**
	 * 
	 * @return the current session number for today.
	 */
	public static int getModelSessionNumber() {
		Date today = DateUtils.today();
		//use the workspace preferences because the logs are saved on a per-workspace
		//basis.
		int session = TagSEAInstrumentationPlugin.getDefault().getPreferenceStore().getInt(MODEL_SESSION_NUMBER+DateUtils.toString(today));
		if (!modelSanityChecked) {
			int readSession = checkSessionNumber(".modellog", today);
			if (readSession > session) {
				//make the session to the new session number.
				TagSEAInstrumentationPlugin.getDefault().getLog().log(new Status(
						Status.INFO,
						TagSEAInstrumentationPlugin.PLUGIN_ID,
						"Possible workbench crash detected. Updating TagSEA instrumentation session number."
				));
				session = readSession;
				TagSEAInstrumentationPlugin.getDefault().getPreferenceStore().setValue(MODEL_SESSION_NUMBER+DateUtils.toString(today), session);
			}
			modelSanityChecked  = true;
		}
		return session;
	}
	
	/**
	 * 
	 * @return the current session number for today.
	 */
	public static int getUISessionNumber() {
		Date today = DateUtils.today();
		//use the workspace preferences because the logs are saved on a per-workspace
		//basis.
		int session = TagSEAInstrumentationPlugin.getDefault().getPreferenceStore().getInt(UI_SESSION_NUMBER+DateUtils.toString(today));
		

		if (!uiSanityChecked) {
			int readSession = checkSessionNumber(".uilog", today);

			if (readSession > session) {
				//make the session to the new session number.
				TagSEAInstrumentationPlugin.getDefault().getLog().log(new Status(
						Status.INFO,
						TagSEAInstrumentationPlugin.PLUGIN_ID,
						"Possible workbench crash detected. Updating TagSEA instrumentation session number."
				));
				session = readSession;
				TagSEAInstrumentationPlugin.getDefault().getPreferenceStore().setValue(UI_SESSION_NUMBER+DateUtils.toString(today), session);
			}

			uiSanityChecked  = true;
		}
		return session;
	}
	
	/**
	 * @param string
	 * @return
	 */
	private static int checkSessionNumber(final String extension, Date day) {
		//sanity check : make sure that it is larger than the largest number already saved.
		IPath stateLocation = TagSEAInstrumentationPlugin.getDefault().getStateLocation();
		File stateFolder = stateLocation.toFile();
		final String dayString = DateUtils.toString(day).replace('/', '-');
		int readSession = -1;
		if (stateFolder.exists() && stateFolder.isDirectory()) {
			File[] children = stateFolder.listFiles(new FileFilter(){
				public boolean accept(File pathname) {
					String fileName = pathname.getName();
					return fileName.startsWith(dayString) &&
					(fileName.endsWith(extension));
				}});
			for (File child : children) {
				String name = child.getName();
				if (name.endsWith(extension)) {
					int dot = name.lastIndexOf('.');
					int i = dot-1;
					while (i >= 0) {
						char c = name.charAt(i);
						if (c == '.') {
							break;
						}
						i--;
					}
					if (i >= 0 && name.charAt(i) == '.') {
						String numberString = name.substring(i+1, dot);
						try {
							int s = Integer.parseInt(numberString);
							if (s > readSession) {
								readSession = s;
							}
						} catch (NumberFormatException e) {}
					}
				}
			}
		}
		return readSession;
	}

	public static void incrementModelSession() {
		Date today = DateUtils.today();
		int session = getModelSessionNumber()+1;
		TagSEAInstrumentationPlugin.getDefault().getPreferenceStore().setValue(MODEL_SESSION_NUMBER+DateUtils.toString(today), session);
	}
	
	public static void incrementUISession()  {
		Date today = DateUtils.today();
		int session = getUISessionNumber()+1;
		TagSEAInstrumentationPlugin.getDefault().getPreferenceStore().setValue(UI_SESSION_NUMBER+DateUtils.toString(today), session);
	}

}
