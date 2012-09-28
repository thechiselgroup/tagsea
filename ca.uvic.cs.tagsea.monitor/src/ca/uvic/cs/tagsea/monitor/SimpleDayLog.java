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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

/**
 * A simple logging mechanism for saving lines of text.
 * @author Del Myers
 *
 */
public class SimpleDayLog {
	StringBuffer log;
	StringBuffer buffer;
	Date date;
	private static final DateFormat FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);;
	private static final HashMap<String, SimpleDayLog> LOG_CACHE = new HashMap<String, SimpleDayLog>();
	private LogSaver saver;
	private ArrayList<ISimpleLogListener> listeners;
	private class LogSaver extends TimerTask implements IWorkbenchListener {

		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			try {
				save();
			} catch (IOException e) {
				TagSEAMonitorPlugin.getDefault().log(e);
			}			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWorkbenchListener#postShutdown(org.eclipse.ui.IWorkbench)
		 */
		public void postShutdown(IWorkbench workbench) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWorkbenchListener#preShutdown(org.eclipse.ui.IWorkbench, boolean)
		 */
		public boolean preShutdown(IWorkbench workbench, boolean forced) {
			try {
				cancel();
				save();
			} catch (IOException e) {
				TagSEAMonitorPlugin.getDefault().log(e);
			}
			return true;
		}
		
	}
	
	private SimpleDayLog(Date date) throws IOException {
		this.date = date;
		saver = new LogSaver();
		Timer t = new Timer();
		this.listeners = new ArrayList<ISimpleLogListener>();
		load();
		//save every 5 minutes
		t.schedule(saver, 300000, 300000);
		PlatformUI.getWorkbench().addWorkbenchListener(saver);
	}
	
	public void addListener(ISimpleLogListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}
	
	public void removeListener(ISimpleLogListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	private void load() throws IOException {
		log = new StringBuffer();
		buffer = new StringBuffer();
		synchronized (log) {
			if (!getFile().exists()) {
				createLog();
			}
			FileReader reader = new FileReader(getFile());
			BufferedReader in = new BufferedReader(reader);
			String s;
			s = in.readLine();
			while ((s = in.readLine()) != null) {
				log.append(s + eol());
			}
			reader.close();
		}
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	private void createLog() throws IOException {
		buffer.append(FORMAT.format(getDate()) + eol());
		save();
	}

	/**
	 * @return
	 */
	private String eol() {
		return System.getProperty("line.separator");
	}

	private void save() throws IOException {
		synchronized (log) {
			if (log == null) load();
			if (buffer.length() == 0) return; //nothing to save
			FileWriter writer = new FileWriter(getFile(), true);
			BufferedReader reader = new BufferedReader(new StringReader(buffer.toString()));
			String s;
			while ((s = reader.readLine()) != null) {
				log.append(s + eol());
				writer.append(s + eol());
			}
			buffer = new StringBuffer();
			writer.close();
			reader.close();
		}
	}
	
	public String getText() {
		String result = null;
		synchronized (log) {
			if (log == null) {
				try {
					load();
				} catch (IOException e) {
					TagSEAMonitorPlugin.getDefault().log(e);
					return null;
				}
			}
			result = log.toString() + buffer.toString();
		}
		return result;
	}
	
	/**
	 * logs the given string, with all newlines replaced by '\' characters,
	 * and the current time-stamp prepended to it.
	 * @param s
	 */
	protected void logLineWithTime(String s) {
		s.replaceAll(eol(), "\\");
		synchronized (log) {
			long time = System.currentTimeMillis();
			String timeString = 
				DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(time));
			s = "<Time:"+timeString+">" + s + eol();
			buffer.append(s);
			fireLineAdded(s);
		}
	}

	/**
	 * @param s
	 */
	private void fireLineAdded(String s) {
		ISimpleLogListener[] array;
		synchronized (listeners) {
			array = listeners.toArray(new ISimpleLogListener[listeners.size()]);
		}
		for (ISimpleLogListener listener : array) {
			listener.lineAdded(s);
		}
	}

	protected File getFile() {
		File location = TagSEAMonitorPlugin.getDefault().getStateLocation().toFile();
		if (location.isDirectory()) {
			File file = new File(location, getDateKey() + ".monitor.log");
			return file;
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public String getDateString() {
		String key = FORMAT.format(getDate());
		return key;
	}
	
	private String getDateKey() {
		String key = getDateString();
		key = key.replace('/', '-');
		key = key.replace('\\', '\\');
		key = key.replace(' ', '_');
		key = key.replace(':', '_');
		key = key.replace(';', '.');
		return key;
	}
	
	public Date getDate() {
		return date;
	}

	protected static SimpleDayLog loadLogForDay(Date day) throws IOException {
		SimpleDayLog result = null;
		result = createOrLoadLog(day);
		return result;
	}

	/**
	 * @param key
	 * @return
	 * @throws IOException 
	 */
	private static SimpleDayLog createOrLoadLog(Date day) throws IOException {
		synchronized(LOG_CACHE) {
			String key = FORMAT.format(day);
			SimpleDayLog result = LOG_CACHE.get(key);
			if (result == null) {
				result = new SimpleDayLog(day);
				LOG_CACHE.put(key, result);
			}
			return result;
		}
		
	}
	
	protected static File findLogFileForDay(Date day) throws IOException {
		SimpleDayLog log = loadLogForDay(day);
		return log.getFile();
	}
	

}
