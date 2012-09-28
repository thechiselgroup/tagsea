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
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.logging.CurrentWaypoints;
import net.sourceforge.tagsea.logging.Event;
import net.sourceforge.tagsea.logging.Log;
import net.sourceforge.tagsea.logging.LoggingFactory;
import net.sourceforge.tagsea.logging.LoggingPackage;
import net.sourceforge.tagsea.logging.ModelEvent;
import net.sourceforge.tagsea.logging.ModelLog;
import net.sourceforge.tagsea.logging.UIEvent;
import net.sourceforge.tagsea.logging.UILog;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * A class that acts as a sink for log events.
 * @author Del Myers
 *
 */
//@tag tagsea.bug.178.fix tagsea.bug.177.fix
class LogSink {
	
	private class SinkThread extends Thread {
		private LinkedList<Event> sink;
		public SinkThread() {
			super("TagSEA Event Logging");
			sink = new LinkedList<Event>();
		}
		
		@Override
		public void run() {
			while (true) {
				synchronized (sink) {
					while (sink.size() == 0) {
						try {
							sink.wait();
						} catch (InterruptedException e) {
							interrupt();
						}
					}
					Event event = sink.removeFirst();
					try {
						internalLogEvent(event);
					} catch (IOException e) {
						TagSEAInstrumentationPlugin.getDefault().log(e);
					}
				}
			}
		}
		
		public void consume(Event event) {
			synchronized(sink) {
				sink.add(event);
				sink.notify();
			}
		}
		
		public void flush() {
			synchronized(sink) {
				while (sink.size() > 0) {
					try {
						internalLogEvent(sink.removeFirst());
					} catch (IOException e) {
						TagSEAInstrumentationPlugin.getDefault().log(e);
					}
				}
			}
		}
	}
	
	
	private SinkThread thread;
	private final int MAX_EVENTS = 2048; //the maximum number of log events before starting a new session.
	private int modelEventCount;
	private int uiEventCount;
	private final HashSet<Log> DIRTY_LOGS;
	private final HashMap<String, ModelLog> LOG_MAP;
	private final HashMap<String, UILog> UI_LOG_MAP;
	private final HashMap<String, Object> SAVE_OPTIONS;
	private final ResourceSet OUTPUT_RESOURCE_SET;
		
	
	
	public LogSink() {
		DIRTY_LOGS = new HashSet<Log>();
		LOG_MAP = new HashMap<String, ModelLog>();
		UI_LOG_MAP = new HashMap<String, UILog>();
		SAVE_OPTIONS = new HashMap<String, Object>();
		SAVE_OPTIONS.put(XMLResource.OPTION_FLUSH_THRESHOLD, (Integer)2048);
		SAVE_OPTIONS.put(XMLResource.OPTION_USE_FILE_BUFFER, Boolean.TRUE);
		OUTPUT_RESOURCE_SET = new ResourceSetImpl();
		OUTPUT_RESOURCE_SET.getPackageRegistry().put(LoggingPackage.eNS_PREFIX, LoggingPackage.eINSTANCE);
		OUTPUT_RESOURCE_SET.getResourceFactoryRegistry().getExtensionToFactoryMap().put("modellog", new XMIResourceFactoryImpl());
		OUTPUT_RESOURCE_SET.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uilog", new XMIResourceFactoryImpl());
		OUTPUT_RESOURCE_SET.getResourceFactoryRegistry().getExtensionToFactoryMap().put("waypoints", new XMIResourceFactoryImpl());
		thread = new SinkThread();
		thread.start();
	}
	
	
	@SuppressWarnings("unchecked") ResourceSet getOutputResourceSet() {
		return OUTPUT_RESOURCE_SET;
	}
	
	
	
	public void consumeEvent(Event event) {
		if (event != null) {
			thread.consume(event);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private synchronized void internalLogEvent(Event event) throws IOException {
		
		if (event instanceof UIEvent) {
			if (uiEventCount >= MAX_EVENTS) {
				uiEventCount = 0;
				InstrumentationPreferences.incrementUISession();
			}
			uiEventCount++;
			UILog log = loadUILog(DateUtils.today(), InstrumentationPreferences.getUISessionNumber());
			log.getEvents().add(event);
			DIRTY_LOGS.add(log);
		} else if (event instanceof ModelEvent) {
			if (modelEventCount >= MAX_EVENTS) {
				modelEventCount = 0;
				InstrumentationPreferences.incrementModelSession();
			}
			modelEventCount++;
			ModelLog log = loadModelLog(DateUtils.today(), InstrumentationPreferences.getModelSessionNumber());
			log.getEvents().add(event);
			DIRTY_LOGS.add(log);
		}
	}

	
	
	
	
	@SuppressWarnings("unchecked")
	private synchronized ModelLog loadModelLog(Date date, int session) throws IOException {
		String fileNameString = getDateFileString(date) + "." + session + ".modellog";
		File file = new File(fileNameString);
		ModelLog log = null;

		if (!LOG_MAP.containsKey(fileNameString)) {
			if (file.exists()) {
				ResourceSet inputResourceSet = new ResourceSetImpl();
				inputResourceSet.getPackageRegistry().put(LoggingPackage.eNS_PREFIX, LoggingPackage.eINSTANCE);
				inputResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("modellog", new XMIResourceFactoryImpl());
				Resource inputResource = inputResourceSet.getResource(URI.createFileURI(file.getCanonicalFile().toString()), true);
				inputResource.load(null);
				if (inputResource.getContents().size() > 0) {
					log = (ModelLog) inputResource.getContents().get(0);
				} 
			}
			if (log == null) {
				log =	LoggingFactory.eINSTANCE.createModelLog();
				Locale locale = Locale.getDefault();
				log.setCountry(locale.getCountry());
				log.setLanguage(locale.getLanguage());
				log.setUid(TagSEAInstrumentationPlugin.getDefault().getUserId());
				log.setUname(TagSEAInstrumentationPlugin.getDefault().getUserName());
				log.setSession(session);
				log.setDate(DateUtils.toNearestDay(date));
			}

			LOG_MAP.put(fileNameString, log);
		}
		return (LOG_MAP.get(fileNameString));
		
	}
	
	
	@SuppressWarnings("unchecked")
	private synchronized UILog loadUILog(Date date, int session) throws IOException {
		IPath stateLocation = TagSEAInstrumentationPlugin.getDefault().getStateLocation();
		DateFormat formatter = DateUtils.getDateFormat();
		String dateString = formatter.format(date);
		String fileNameString = dateString.replace('/', '-') + "." + session +".uilog";
		IPath pathLocation = stateLocation.append(fileNameString);
		File file = pathLocation.toFile();
		UILog log = null;

		if (!UI_LOG_MAP.containsKey(fileNameString)) {
			if (file.exists()) {
				ResourceSet inputResourceSet = new ResourceSetImpl();
				inputResourceSet.getPackageRegistry().put(LoggingPackage.eNS_PREFIX, LoggingPackage.eINSTANCE);
				inputResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uilog", new XMIResourceFactoryImpl());
				Resource inputResource = inputResourceSet.createResource(URI.createFileURI(file.getCanonicalFile().toString()));
				inputResource.load(null);
				log = (UILog) inputResource.getContents().get(0);
			} else {
				log =	LoggingFactory.eINSTANCE.createUILog();
				Locale locale = Locale.getDefault();
				log.setCountry(locale.getCountry());
				log.setLanguage(locale.getLanguage());
				log.setUid(TagSEAInstrumentationPlugin.getDefault().getUserId());
				log.setUname(TagSEAInstrumentationPlugin.getDefault().getUserName());
				log.setSession(session);
				try {
					log.setDate(formatter.parse(dateString));
				} catch (ParseException e) {
					TagSEAInstrumentationPlugin.getDefault().log(e);
				}
			}
			UI_LOG_MAP.put(fileNameString, log);
		}
		return (UI_LOG_MAP.get(fileNameString));
		
	}
	
	
	String getDateFileString(Date date) {
		String fileNameString = DateUtils.toString(date).replace('/', '-');
		IPath stateLocation = TagSEAInstrumentationPlugin.getDefault().getStateLocation();
		IPath pathLocation = stateLocation.append(fileNameString);
		pathLocation.removeTrailingSeparator();
		return pathLocation.toPortableString();
	}
	
	private File getLogFile(Log log) {
		String fileString = getDateFileString(log.getDate()) + "." + log.getSession();
		if (log instanceof ModelLog) {
			fileString += ".modellog";
		} if (log instanceof UILog) {
			fileString += ".uilog";
		}
		return new File(fileString);
	}
	
	
	/**
	 * @throws IOException 
	 * 
	 */
	@SuppressWarnings("unchecked")
	synchronized void saveLogs() throws IOException {
		//System.err.println("Save logs");
		thread.flush();
		ResourceSet outputResourceSet = getOutputResourceSet();
		for (Log log : DIRTY_LOGS) {
			File file = getLogFile(log);
			try {
				Resource outputResource = outputResourceSet.createResource(URI.createFileURI(file.getCanonicalFile().toString()));
				outputResource.getContents().add(log);
				outputResource.save(SAVE_OPTIONS);
				if (!DateUtils.today().equals(log.getDate())) {
					//remove the log from the map.
					if (log instanceof UILog && log.getSession() != InstrumentationPreferences.getUISessionNumber()) {
						UI_LOG_MAP.values().remove(log);
					} else if (log instanceof ModelLog  && log.getSession() != InstrumentationPreferences.getModelSessionNumber()){
						LOG_MAP.values().remove(log);
					}
				}
			} catch (NullPointerException e) {
				TagSEAInstrumentationPlugin.getDefault().getLog().log(new Status(Status.INFO, TagSEAInstrumentationPlugin.PLUGIN_ID, "error saving file " +  file.getName() + " log = " + log));
			}
		}
		DIRTY_LOGS.clear();
	}
	
	
	@SuppressWarnings("unchecked")
	synchronized void saveToday() throws IOException {
		thread.flush();
		//System.err.println("Save today");
		ResourceSet outputResourceSet = getOutputResourceSet();
		HashSet<Log> savedLogs = new HashSet<Log>();
		for (Log log : DIRTY_LOGS) {
			if (DateUtils.today().equals(log.getDate())) {
				File file = getLogFile(log);
				try {

					Resource outputResource = outputResourceSet.createResource(URI.createFileURI(file.getAbsolutePath()));
					outputResource.getContents().add(log);
					outputResource.save(SAVE_OPTIONS);
					savedLogs.add(log);
					if (log instanceof UILog) {
						if (log.getSession() != InstrumentationPreferences.getUISessionNumber()) {
							UI_LOG_MAP.values().remove(log);
						}
					} else {
						if (log.getSession() != InstrumentationPreferences.getModelSessionNumber()) {
							LOG_MAP.values().remove(log);
						}
					}
				} catch (NullPointerException e) {
					TagSEAInstrumentationPlugin.getDefault().getLog().log(new Status(Status.INFO, TagSEAInstrumentationPlugin.PLUGIN_ID, "error saving file " +  file.getName() + " log = " + log, e));
				}
			}
		}
		DIRTY_LOGS.remove(savedLogs);
	}
	
	
	
	@SuppressWarnings("unchecked")
	void saveWaypoints() throws IOException {
		Date today = DateUtils.today();
		String fileString = getDateFileString(today) + ".waypoints";
		CurrentWaypoints waypointsState = LoggingFactory.eINSTANCE.createCurrentWaypoints();
		waypointsState.setSession(InstrumentationPreferences.getModelSessionNumber());
		Locale l = Locale.getDefault();
		waypointsState.setCountry(l.getCountry());
		waypointsState.setLanguage(l.getLanguage());
		waypointsState.setDate(today);
		waypointsState.setUid(TagSEAInstrumentationPlugin.getDefault().getUserId());
		waypointsState.setUname(TagSEAInstrumentationPlugin.getDefault().getUserName());
		for (IWaypoint wp : TagSEAPlugin.getWaypointsModel().getAllWaypoints()) {
			waypointsState.getWaypoint().add(TagSEAInstrumentationPlugin.createWaypointState(wp));
		}
		ResourceSet outputResourceSet = getOutputResourceSet();
		Resource outputResource = outputResourceSet.createResource(URI.createFileURI(fileString));
		outputResource.getContents().add(waypointsState);
		outputResource.save(null);
	}
	
}
