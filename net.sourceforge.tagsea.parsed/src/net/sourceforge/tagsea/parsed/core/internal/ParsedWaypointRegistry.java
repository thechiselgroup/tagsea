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
package net.sourceforge.tagsea.parsed.core.internal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import net.sourceforge.tagsea.IWaypointType;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITagSEAOperationStateListener;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.comments.IDomainMethod;
import net.sourceforge.tagsea.parsed.comments.StandardCommentParser;
import net.sourceforge.tagsea.parsed.comments.StandardCommentWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointPresentation;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointRegistry;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;
import net.sourceforge.tagsea.parsed.core.internal.operations.AbstractWaypointUpdateOperation;
import net.sourceforge.tagsea.parsed.core.internal.operations.CleanOperation;
import net.sourceforge.tagsea.parsed.core.internal.operations.ParseFileOperation;
import net.sourceforge.tagsea.parsed.core.internal.persistence.WaypointSerializer;
import net.sourceforge.tagsea.parsed.core.internal.persistence.WaypointXMLPersistence;
import net.sourceforge.tagsea.parsed.core.internal.resources.IDocumentLifecycleListener;
import net.sourceforge.tagsea.parsed.ui.internal.preferences.PreferenceConstants;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IMemento;

/**
 * Concrete implementation of IParsedWaypointRegistry
 * @author Del Myers
 *
 */
public class ParsedWaypointRegistry implements IParsedWaypointRegistry, IDocumentLifecycleListener, ITagSEAOperationStateListener {
	
	/**
	 * A timer task that is used to prevent too many full cleans from being
	 * scheduled at once.
	 * @author Del Myers
	 *
	 */
	private class CleanTask extends TimerTask {
		boolean force;
		boolean done;
		boolean running;
		public CleanTask(boolean force) {
			this.force = force;
			this.done = false;
			this.running = false;
		}
		@Override
		public void run() {
			//get the projects in the workspace, and check their properties
			//to find each of the definitions.
			running = true;
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			List<IProject> projectsToClean = new LinkedList<IProject>();
			if (force) {
				projectsToClean.addAll(Arrays.asList(projects));
				//delete all the previous markers
				IMarker[] oldMarkers = new IMarker[0];
				try {
					oldMarkers = ResourcesPlugin.getWorkspace().getRoot().findMarkers(ParsedWaypointPlugin.MARKER_TYPE, false, IResource.DEPTH_INFINITE);
				} catch (CoreException e) {
					ParsedWaypointPlugin.getDefault().log(e);
				}
				for (IMarker m : oldMarkers) {
					try {
						m.delete();
					} catch (CoreException e) {
						ParsedWaypointPlugin.getDefault().log(e);
					}
				}
			} else {
				for (IProject project : projects) {
					if (project.isAccessible()) {
						try {
							String properties = project.getPersistentProperty(ParsedWaypointDelegate.WAYPOINT_DEFINITIONS);
							if (properties == null) {
								projectsToClean.add(project);
							} else {
								String[] projectDefinitions = properties.split("\\s+");
								TreeSet<String> projectDefinitionSet = new TreeSet<String>();
								projectDefinitionSet.addAll(Arrays.asList(projectDefinitions));
								if (!registered.equals(projectDefinitionSet)) {
									projectsToClean.add(project);
								}
							}

						} catch (CoreException e) {
							projectsToClean.add(project);
						}
					}
				}
				//restore waypoints
				TagSEAPlugin.run(new LoadPersistentWaypointsOperation(), false);
			}
			String property = "";
			for (String name : registered) {
				property += name + " ";
			}
			for (IProject project : projectsToClean) {
				try {
					if (project.isAccessible()) {
						clean(project, IResource.DEPTH_INFINITE);
						project.setPersistentProperty(ParsedWaypointDelegate.WAYPOINT_DEFINITIONS, property);
					}
				} catch (CoreException e) {
					ParsedWaypointPlugin.getDefault().log(e);
				}
			}
			running = false;
			done = true;
		}
		
	}
	
	public class LoadPersistentWaypointsOperation extends
	AbstractWaypointUpdateOperation {

		public LoadPersistentWaypointsOperation() {
			super("Loading persited parsed waypoints");
		}

		@Override
		public IStatus run(IProgressMonitor monitor)
		throws InvocationTargetException {
			try {
				IMemento[] mementos = persistence.loadMementos();

				IWaypointType waypointType = TagSEAPlugin.getDefault().getWaypointType(ParsedWaypointPlugin.WAYPOINT_TYPE);
				String[] keys = waypointType.getDeclaredAttributes();
				for (IMemento fileMemento : mementos) {
					String fileName = fileMemento.getString("fileName");
					IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(fileName));
					//take into account the fact that an editor may have already been loaded with the waypoints in it.
					//in this case, the file will have been reparsed, and waypoints will already exist.
					if (resource instanceof IFile) {
						IWaypoint[] currentWaypoints = 
							ParsedWaypointPlugin.
							getDefault().
							getParsedWaypointRegistry().
							getWaypointsForFile((IFile)resource);
						if (currentWaypoints.length > 0) {
							continue;
						}
					}
					IMemento[] waypointChildren = fileMemento.getChildren("waypoint");
					for (IMemento memento : waypointChildren) {
						IMemento[] tagChildren = memento.getChildren("tag");
						String[] tagNames = new String[tagChildren.length];
						for (int i = 0; i < tagChildren.length; i++) {
							tagNames[i] = tagChildren[i].getTextData();
						}
						IWaypoint wp = TagSEAPlugin.getWaypointsModel().createWaypoint(ParsedWaypointPlugin.WAYPOINT_TYPE, tagNames);
						for (String key : keys) {
							Class<?> clazz = waypointType.getAttributeType(key);
							String value = memento.getString(key);
							try {
								if (value != null) {
									if (clazz.equals(String.class)) {
										wp.setStringValue(key, value);
									} else if (clazz.equals(Integer.class)) {
										wp.setIntValue(key, Integer.parseInt(value));
									} else if (clazz.equals(Boolean.class)) {
										wp.setBooleanValue(key, Boolean.getBoolean(value));
									} else if (clazz.equals(Date.class)) {
										wp.setDateValue(key, WaypointSerializer.getDateFromString(value));
									}
								}
							} catch (Exception e) {
								ParsedWaypointPlugin.getDefault().log(e);
							}
						}
					}
				}
			} catch (IOException e) {
				ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().clean(true);
			}

			return Status.OK_STATUS;
		}

	}
	
	/**
	 * A set of files that are currently being parsed. Used so that if a 
	 * clean operation causes a document to be created, the registry won't begin
	 * a new parse, causing an infinite loop.
	 */
	private HashSet<IFile> parsingFiles;
	
	
	WaypointFinder waypointFinder;
	private HashMap<String, IParsedWaypointDefinition> registry;
	private TreeSet<String> registered;
	private Timer cleanTimer;
	private CleanTask currentTask;
	private WaypointXMLPersistence persistence;

	public ParsedWaypointRegistry() throws IOException {
		this.waypointFinder = new WaypointFinder();
		registry = new HashMap<String, IParsedWaypointDefinition>();
		parsingFiles = new HashSet<IFile>();
		registered = new TreeSet<String>();
		cleanTimer = new Timer();
		currentTask = null;
		persistence = new WaypointXMLPersistence();
	}
	
	public void clean(boolean force) {
		if (currentTask == null || currentTask.done || currentTask.running) {
			currentTask = new CleanTask(force);
			cleanTimer.schedule(currentTask, 500);
		} else {
			currentTask.cancel();
			currentTask = new CleanTask(currentTask.force || force);
			cleanTimer.schedule(currentTask, 500);
		}
	}

	public void clean(IResource resource, int depth) {
		TagSEAPlugin.run(new CleanOperation("Loading Parsed Waypoints...", resource, depth), false);
	}

	public IParsedWaypointDefinition getDefinition(String kind) {
		return registry.get(kind);
	}

	public IParsedWaypointDefinition[] getMatchingDefinitions(IFile resource) {
		WildcardMatcher matcher = new WildcardMatcher();
		HashSet<IParsedWaypointDefinition> result = new HashSet<IParsedWaypointDefinition>();
		for (String kind : registered) {
			IParsedWaypointDefinition def = registry.get(kind);
			String contentTypeString = def.getContentType();
			IContentType contentType = 
				(contentTypeString != null) ? 
						Platform.getContentTypeManager().getContentType(contentTypeString) :
						null;
			String[] nameMatch = def.getFileAssociations();
			boolean add = (contentType != null) ?
					contentType.isAssociatedWith(resource.getName()) :
					false;
			if (!add) {
				for (String testName : nameMatch) {
					switch (matcher.matches(resource.getName(), testName)) {
					case WildcardMatcher.WILDCARD:
					case WildcardMatcher.PERFECT:
						add=true;
					}
				}
			}
			if (add) {
				result.add(def);
			}
		}
		return result.toArray(new IParsedWaypointDefinition[result.size()]);
	}

	public void setRegistered(String[] definitionKinds) {
		if (definitionKinds == null) return;
		TreeSet<String> newRegistry = new TreeSet<String>(Arrays.asList(definitionKinds));
		if (!newRegistry.equals(registered)) {
			registered = newRegistry;
			storeRegistration();
			clean(false);
		}
		
	}
	
	public void register(String definition) {
		if (definition==null) return;
		if (registry.keySet().contains(definition) && !registered.contains(definition)) {
			registered.add(definition);
			storeRegistration();
			clean(false);
		}
		
	}

	public void register(String[] definitions) {
		if (definitions == null || definitions.length == 0) return;
		boolean clean = false;
		for (String definition : definitions) {
			if (registry.keySet().contains(definition) && !registered.contains(definition)) {
				registered.add(definition);
				clean = true;
			}
		}
		if (clean) {
			storeRegistration();
			clean(false);
		}
	}
	

	public void unregister(String definition) {
		if (registry.keySet().contains(definition) && registered.contains(definition)) {
			registered.remove(definition);
			storeRegistration();
			clean(false);
		}
	}

	public void unregister(String[] definitions) {
		if (definitions == null || definitions.length == 0) return;
		boolean clean = false;
		for (String definition : definitions) {
			if (registry.keySet().contains(definition) && registered.contains(definition)) {
				registered.add(definition);
				clean = true;
			}
		}
		if (clean) {
			storeRegistration();
			clean(false);
		}
	}

	/**
	 * Saves the registration information.
	 */
	private void storeRegistration() {
		String registrationString = "";
		for (String kind : registry.keySet()) {
			char indicator = '-';
			if (registered.contains(kind)) {
				indicator = '+';
			}
			registrationString = registrationString + indicator + kind + " ";
		}
		IPreferenceStore store = ParsedWaypointPlugin.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.REGISTERED_DEFINITIONS, registrationString);
	}
	
	public IWaypoint[] getWaypointsForFile(IFile resource) {
		return waypointFinder.getWaypointsForFile(resource);
	}
	
	public IWaypoint[] getWaypointsForFile(IFile resource, String kind) {
		return waypointFinder.getWaypointsForFile(resource, kind);
	}

	public IFile getFileForWaypoint(IWaypoint wp) {
		IResource resource = ResourceWaypointUtils.getResource(wp);
		if (resource instanceof IFile) {
			return (IFile)resource;
		}
		return null;
	}
	

	public IWaypoint[] findWaypoints(IResource resource, int depth) {
		return waypointFinder.findWaypoints(resource, depth);
	}

	public IParsedWaypointDefinition[] getContributedDefinitions() {
		Collection<IParsedWaypointDefinition> definitions =
			registry.values();
		return definitions.toArray(new IParsedWaypointDefinition[definitions.size()]);
	}
	
	
	/**
	 * Initializes the registry with the contributions from the extension points, and the preferences.
	 * @param forceClean true to force a full clean of the waypoints in the workspace.
	 * @throws CoreException 
	 */
	protected void initializeRegistry(boolean forceClean) throws CoreException {
		ResourcesPlugin.getWorkspace().addSaveParticipant(ParsedWaypointPlugin.getDefault(), persistence);
		TagSEAPlugin.addOperationStateListener(this);
		TagSEAPlugin.addWaypointChangeListener(persistence, ParsedWaypointPlugin.WAYPOINT_TYPE);
		ParsedWaypointPlugin.getDefault().getPlatformDocumentRegistry().addDocumentLifecycleListener(this);
		List<IParsedWaypointDefinition> contributions = loadRawContributions();
		contributions.addAll(loadCommentContributions());
		for (IParsedWaypointDefinition def : contributions) {
			registry.put(def.getKind(), def);
		}
		IPreferenceStore store = ParsedWaypointPlugin.getDefault().getPreferenceStore();
		String storedRegistryString = store.getString(PreferenceConstants.REGISTERED_DEFINITIONS);
		String[] storedRegistry = storedRegistryString.split("\\s");
		TreeSet<String> registered = new TreeSet<String>();
		TreeSet<String> unregistered = new TreeSet<String>();
		for (String r : storedRegistry) {
			if ("".equals(r)) continue;
			char indicator = r.charAt(0);
			String kind = r.substring(1);
			if (indicator == '-') {
				unregistered.add(kind);
			}
		}
		for (IParsedWaypointDefinition def : contributions) {
			if (!unregistered.contains(def.getKind())) {
				registered.add(def.getKind());
			}
		}
		String[] definitions = registered.toArray(new String[registered.size()]);
		if (!forceClean) {
			register(definitions);
		} else {
			if (definitions == null || definitions.length == 0) return;
			for (String definition : definitions) {
				if (registry.keySet().contains(definition)) {
					this.registered.add(definition);
				}
			}
			storeRegistration();
			clean(true);
		}
	}
	
	/**
	 * @return
	 */
	private List<IParsedWaypointDefinition> loadRawContributions() {
		IConfigurationElement[] elements = 
			Platform.getExtensionRegistry().getConfigurationElementsFor("net.sourceforge.tagsea.parsed.parsedWaypoint");
		LinkedList<IParsedWaypointDefinition> contributed = new LinkedList<IParsedWaypointDefinition>();
		for (IConfigurationElement el : elements) {
			if ("definition".equals(el.getName())) {
				try {
					IParsedWaypointDefinition def = (IParsedWaypointDefinition) el.createExecutableExtension("class");
					contributed.add(def);
				} catch (CoreException e) {
				}
			}
		}
		return contributed;
	}

	private List<IParsedWaypointDefinition> loadCommentContributions() {
		IConfigurationElement[] elements = 
			Platform.getExtensionRegistry().getConfigurationElementsFor("net.sourceforge.tagsea.parsed.standardComment");
		LinkedList<IParsedWaypointDefinition> contributed = new LinkedList<IParsedWaypointDefinition>();
		for (IConfigurationElement el : elements) {
			if ("definition".equals(el.getName())) {
				IConfigurationElement defel = el;
				String kind = defel.getAttribute("kind");
				kind = defel.getContributor().getName() + "." + kind;
				String name = defel.getAttribute("name");
				String contentType = defel.getAttribute("contentType");
				String associations = defel.getAttribute("fileAssociations");
				IParsedWaypointPresentation presentation = null;
				IDomainMethod domainMethod = null;
				try {
					presentation = 
						(IParsedWaypointPresentation) defel.createExecutableExtension("presentation");
				} catch (CoreException e) {}
				try {
					domainMethod =
						(IDomainMethod) defel.createExecutableExtension("domainMethod");
				} catch (CoreException e) {}
				if (contentType != null) {
					contentType = contentType.trim();
				}
				LinkedList<String> associationsList = new LinkedList<String>();
				Status status = null;
				if (associations != null) {
					String[] array = associations.split(",");
					for (String s : array) {
						s = s.trim();
						if (!"".equals(s)) {
							int star = s.indexOf('*');
							int lastStar = s.lastIndexOf('*');
							
							//make sure that it is legal:
							if (star != lastStar) {
								status = new Status(
									Status.ERROR, 
									ParsedWaypointPlugin.PLUGIN_ID, 
									"Illegal file association in extension " + kind
								);
							} else if (star != -1 && star != 0) {
								status = new Status(
									Status.ERROR, 
									ParsedWaypointPlugin.PLUGIN_ID, 
									"Illegal file association in extension " + kind
								);
							} else if (star == 0) {
								if (!s.startsWith("*.")) {
									status = new Status(
										Status.ERROR, 
										ParsedWaypointPlugin.PLUGIN_ID, 
										"Illegal file association in extension " + kind
									);
								}
							}
							associationsList.add(s);
						}
					}
				}
				if (associationsList.size() == 0 && (contentType == null || "".equals(contentType))) {
					status = new Status(
						Status.ERROR, 
						ParsedWaypointPlugin.PLUGIN_ID, 
						"Missing file association or content type in extension " + kind
					);
				}
				if (status != null) {
					ParsedWaypointPlugin.getDefault().getLog().log(status);
					break;
				}
				LinkedList<String> singleLine = new LinkedList<String>();
				LinkedList<String> multiLineOpen = new LinkedList<String>();
				LinkedList<String> multiLineClose = new LinkedList<String>();
				LinkedList<String> exclusionOpen = new LinkedList<String>();
				LinkedList<String> exclusionClose = new LinkedList<String>();
				IConfigurationElement[] childElements = defel.getChildren();
				for (IConfigurationElement child : childElements) {
					if ("singleline".equals(child.getName())) {
						String s = child.getAttribute("open");
						if (s != null) {
							s = s.trim();
							if (!"".equals(s)) {
								singleLine.add(s);
							}
						}
					} else if ("multiline".equals(child.getName())) {
						String o = child.getAttribute("open");
						String c = child.getAttribute("close");
						if (!(o == null || c == null)) {
							o = o.trim();
							c = c.trim();
							if (!("".equals(o) || "".equals(c))) {
								multiLineOpen.add(o);
								multiLineClose.add(c);
							}
						}
					} else if ("exclusion".equals(child.getName())) {
						String o = child.getAttribute("open");
						String c = child.getAttribute("close");
						if (!(o == null || c == null)) {
							o = o.trim();
							c = c.trim();
							if (!("".equals(o) || "".equals(c))) {
								exclusionOpen.add(o);
								exclusionClose.add(c);
							}
						}
					}
				}
				if (singleLine.size() != 0 || multiLineOpen.size() != 0) {
					try {
						StandardCommentParser parser = new StandardCommentParser(
								singleLine.toArray(new String[singleLine.size()]),
								multiLineOpen.toArray(new String[multiLineOpen.size()]),
								multiLineClose.toArray(new String[multiLineClose.size()]),
								exclusionOpen.toArray(new String[exclusionOpen.size()]),
								exclusionClose.toArray(new String[exclusionClose.size()]),
								kind
						);
						StandardCommentWaypointDefinition definition =
							new StandardCommentWaypointDefinition(kind, name,
									associationsList.toArray(new String[associationsList.size()]),
									contentType,
									parser, 
									false,
									false
							);
						if (presentation != null) {
							definition.setPresentation(presentation);
						}
						if (domainMethod != null) {
							((StandardCommentParser)definition.getParser()).setDomainMethod(domainMethod);
						}
						contributed.add(definition);
					} catch (IllegalArgumentException e) {
						status = new Status(
								Status.ERROR, 
								ParsedWaypointPlugin.PLUGIN_ID, 
								"Unable to contribute comment parser " + kind,
								e
							);
							ParsedWaypointPlugin.getDefault().getLog().log(status);
					}
				} else {
					status = new Status(
						Status.ERROR, 
						ParsedWaypointPlugin.PLUGIN_ID, 
						"Missing comment delimiters in extension " + kind
					);
					ParsedWaypointPlugin.getDefault().getLog().log(status);
				}
			}
		}
		return contributed;
	}

	public IParsedWaypointDefinition[] getRegisteredDefinitions() {
		HashSet<IParsedWaypointDefinition> registeredSet = new HashSet<IParsedWaypointDefinition>();
		for (String key : registered) {
			registeredSet.add(registry.get(key));
		}
		return registeredSet.toArray(new IParsedWaypointDefinition[registeredSet.size()]);
	}

	public boolean isRegistered(String kind) {
		return registered.contains(kind);
	}

	public IMarker getMarkerForWaypoint(IWaypoint wp) {
		IResource resource = ParsedWaypointUtils.getResource(wp);
		if (resource != null && resource.exists()) {
			IMarker[] markers;
			try {
				markers = resource.findMarkers(ParsedWaypointPlugin.MARKER_TYPE, false, IResource.DEPTH_ONE);
				for (IMarker marker : markers) {
					int charStart = marker.getAttribute(IMarker.CHAR_START, -1);
					if (charStart == ParsedWaypointUtils.getCharStart(wp)) {
						return marker;
					}
				}
			} catch (CoreException e) {
				ParsedWaypointPlugin.getDefault().log(e);
				return null;
			}
		}
		return null;
	}

	public IWaypoint getWaypointForMarker(IMarker marker) {
		if (marker.getResource().getType() != IResource.FILE) {
			return null;
		}
		IWaypoint[] wps = getWaypointsForFile((IFile) marker.getResource());
		for (IWaypoint wp : wps) {
			int charStart = wp.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_START, -1);
			if (marker.getAttribute(IMarker.CHAR_START, -1) == charStart) {
				return wp;
			}
		}
		return null;
	}

	/**
	 * Lets the registry know that the given file has been/is being parsed. This is necessary in case
	 * the parsing doesn't indicate a waypoint change, but the waypoints aren't in synch with the saved
	 * state (for example, due to a crash or an exception). This will tell the registry to mark the 
	 * file as dirty and in need of updating.
	 * @param file
	 */
	public void touch(IFile file) {
		persistence.markDirty(file);		
	}

	/**
	 * Backs-up the given set of waypoints for later restoration if necessary.
	 * @param the waypoints to backup
	 */
	public void backup(IWaypoint[] waypoints) {
		try {
			persistence.backup(waypoints);
		} catch (IOException e) {
			ParsedWaypointPlugin.getDefault().log(e);
		}
	}
	
	
	public synchronized void stateChanged(TagSEAOperation operation) {
		//avoid infinite loops by notifying that there is currently a
		//parse operation occurring on a file. If a document is created
		//for this file during the parse, ignore it. 
		synchronized (parsingFiles) {
			if (operation instanceof ParseFileOperation) {
				switch (operation.getState()) {
				case RUNNING: 
					parsingFiles.add(((ParseFileOperation)operation).getFile());
					break;
				case DONE:
					parsingFiles.remove(((ParseFileOperation)operation).getFile());
					break;
				}
			}
		}
	}

	public void documentCreated(ITextFileBuffer buffer) {
		synchronized (parsingFiles) {
			IPath location = buffer.getLocation();
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(location);
			if (resource instanceof IFile && 
					resource.isAccessible() &&
					!parsingFiles.contains(resource)) {
				TagSEAPlugin.run(
					new CleanOperation(
						"Refreshing Waypoints on File " + resource.getName(), 
						resource, 
						IResource.DEPTH_ZERO
					), 
					false
				);
			}
		}
	}

	public void documentDisposed(ITextFileBuffer buffer) {

		IPath location = buffer.getLocation();
		if (location != null) {
			final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(location);
			if (resource instanceof IFile) {
				try {
					final IMemento fileMemento = persistence.removeBackup((IFile) resource);
					if (buffer.isDirty()) {
						IStatus status = TagSEAPlugin.syncRun(new AbstractWaypointUpdateOperation("Unloading Waypoint Changes for " + resource.getName()){
							@Override
							public IStatus run(IProgressMonitor monitor)
							throws InvocationTargetException {
								IWaypointType waypointType = TagSEAPlugin.getDefault().getWaypointType(ParsedWaypointPlugin.WAYPOINT_TYPE);
								String[] keys = waypointType.getDeclaredAttributes();
								IWaypoint[] oldWaypoints = getWaypointsForFile((IFile)resource);
								for (IWaypoint wp : oldWaypoints) {
									wp.delete();
								}
								if (fileMemento == null) {
									return Status.OK_STATUS;
								}
								IMemento[] waypointChildren = fileMemento.getChildren("waypoint");
								MultiStatus status = new MultiStatus(ParsedWaypointPlugin.PLUGIN_ID, 0, "Parsed waypoint changes", null);
								for (IMemento memento : waypointChildren) {
									IMemento[] tagChildren = memento.getChildren("tag");
									String[] tagNames = new String[tagChildren.length];
									for (int i = 0; i < tagChildren.length; i++) {
										tagNames[i] = tagChildren[i].getTextData();
									}
									IWaypoint wp = TagSEAPlugin.getWaypointsModel().createWaypoint(ParsedWaypointPlugin.WAYPOINT_TYPE, tagNames);
									for (String key : keys) {
										Class<?> clazz = waypointType.getAttributeType(key);
										String value = memento.getString(key);
										try {
											if (value != null) {
												if (clazz.equals(String.class)) {
													wp.setStringValue(key, value);
												} else if (clazz.equals(Integer.class)) {
													wp.setIntValue(key, Integer.parseInt(value));
												} else if (clazz.equals(Boolean.class)) {
													wp.setBooleanValue(key, Boolean.getBoolean(value));
												} else if (clazz.equals(Date.class)) {
													wp.setDateValue(key, WaypointSerializer.getDateFromString(value));
												}
											}
										} catch (Exception e) {
											status.merge(ParsedWaypointPlugin.getDefault().createErrorStatus(e));
										}
									}
								}
								if (status.isOK()) {
									return Status.OK_STATUS;
								}
								return status;
							}
						}, new NullProgressMonitor());
						if (!status.isOK()) {
							ParsedWaypointPlugin.getDefault().getLog().log(status);
						}
					}
				} catch (IOException e) {
					ParsedWaypointPlugin.getDefault().log(e);
				}
			}
		}
	}

}
