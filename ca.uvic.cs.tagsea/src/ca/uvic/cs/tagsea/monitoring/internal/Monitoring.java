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
package ca.uvic.cs.tagsea.monitoring.internal;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.monitoring.ITagSEAMonitor;
import ca.uvic.cs.tagsea.monitoring.ITagSEAMonitoringEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEAActionEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEAJobEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEANavigationEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEAPartEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEARefactorEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEARoutingEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEASelectionEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEATagEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEAWaypointEvent;
import ca.uvic.cs.tagsea.monitoring.internal.ui.MonitorAcceptanceWizard;
import ca.uvic.cs.tagsea.monitoring.internal.ui.MonitorAcceptanceWizardPage;
import ca.uvic.cs.tagsea.ui.views.RoutesView;
import ca.uvic.cs.tagsea.ui.views.TagsView;

/**
 * Central class for monitoring TagSEA actions.
 * @author Del Myers
 *
 */
public class Monitoring implements ISelectionListener{
	private static Monitoring instance;
	private IViewPart tagsView;
	private IViewPart routesView;
	private ArrayList<ITagSEAMonitor> monitors;
	private ArrayList<ITagSEAMonitor> allMonitors;
	private ArrayList<ITagSEAMonitor> activated;
	private static final String PREF_KEY = "monitoring.keys:";
	private MonitorThread publisher;
	private static final Object LOCK = new Object();
	
	private class MonitorThread extends Thread {
		
		private LinkedList<ITagSEAMonitoringEvent> eventQueue;
		/**
		 * 
		 */
		public MonitorThread() {
			this.eventQueue = new LinkedList<ITagSEAMonitoringEvent>();
		}
		
		public void run() {
			synchronized(eventQueue) {
				while (true) {
					try {
						while (eventQueue.size() <= 0) {
							eventQueue.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
					while (eventQueue.size() > 0) {
						ITagSEAMonitoringEvent evt = eventQueue.removeFirst();
						ITagSEAMonitor[] monitorsArray = new ITagSEAMonitor[monitors.size()]; 
						synchronized (monitors) {
							monitors.toArray(monitorsArray);
						}
						for (ITagSEAMonitor monitor: monitorsArray) {
							try {
								switch (evt.getKind()) {
									case Job:
										monitor.jobEventOccurred((TagSEAJobEvent) evt); break;
									case Navigation:
										monitor.navigationOccurred((TagSEANavigationEvent) evt); break;
									case Refactoring:
										monitor.refactorOccurred((TagSEARefactorEvent) evt); break;
									case Selection:
										TagSEASelectionEvent selevt = (TagSEASelectionEvent)evt;
										monitor.selectionOccurred(selevt.getPart(), selevt.getSelection());
										break;
									case Routing:
										monitor.routingOccurred((TagSEARoutingEvent) evt); break;
									case Tagging:
										monitor.tagOccurred((TagSEATagEvent) evt); break;
									case Waypointing:
										monitor.waypointOccurred((TagSEAWaypointEvent) evt); break;
									case Part:
										monitor.partEventOccurred((TagSEAPartEvent)evt); break;
									case Action:
										monitor.actionEventOccurred((TagSEAActionEvent)evt); break;
									default: break;
								} 
							} catch (Exception e) {
								//don't kill the thread because of an exception on one monitor.
								TagSEAPlugin.log("", e);
							}
						}
					}
				}
			}
		}
		public void queueEvent(ITagSEAMonitoringEvent evt) {
			synchronized (eventQueue) {
				synchronized (monitors) {
					//only do it if there are monitors to send to.
					if (monitors.size() <= 0) return;	
				}
				eventQueue.add(evt);
				eventQueue.notifyAll();
			}
		}
	}
	
	class WindowListener implements IWindowListener {
		IWorkbenchWindow window;
		IPageListener listener;
		{
			listener = new PageListener();
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowActivated(IWorkbenchWindow window) {
			if (window != null) {
				window.removePageListener(listener);
			}
			window.addPageListener(listener);
			listener.pageActivated(window.getActivePage());
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowClosed(IWorkbenchWindow window) {				
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowDeactivated(IWorkbenchWindow window) {
			if (window != null) {
				window.removePageListener(listener);
			}
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowOpened(IWorkbenchWindow window) {
		}
	}
	
	class PageListener implements IPageListener {
		IWorkbenchPage page;
		IPartListener partListener;
		ISelectionListener selectionListener;
		{
			partListener = new PartListener();
			selectionListener = new SelectionListener();
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPageListener#pageActivated(org.eclipse.ui.IWorkbenchPage)
		 */
		public void pageActivated(IWorkbenchPage page) {
			if (this.page != null) {
				this.page.removePartListener(partListener);
				this.page.removeSelectionListener(selectionListener);
			}
			this.page = page;
			page.addPartListener(partListener);
			partListener.partActivated(page.getActivePart());
			page.addSelectionListener(selectionListener);
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPageListener#pageClosed(org.eclipse.ui.IWorkbenchPage)
		 */
		public void pageClosed(IWorkbenchPage page) {
			if (this.page != null) {
				this.page.removePartListener(partListener);
				this.page.removeSelectionListener(selectionListener);
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPageListener#pageOpened(org.eclipse.ui.IWorkbenchPage)
		 */
		public void pageOpened(IWorkbenchPage page) {
		}
	}
	
	class PartListener implements IPartListener {
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof TagsView) {
				fireEvent(new TagSEAPartEvent(TagSEAPartEvent.Type.ViewActivated, (TagsView)part));
			} else if (part instanceof RoutesView) {
				fireEvent(new TagSEAPartEvent(TagSEAPartEvent.Type.ViewActivated, (RoutesView)part));
			}
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partBroughtToTop(IWorkbenchPart part) {
			
			
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partClosed(IWorkbenchPart part) {
			if (part instanceof TagsView) {
				fireEvent(new TagSEAPartEvent(TagSEAPartEvent.Type.ViewClosed, (TagsView)part));
			} else if (part instanceof RoutesView) {
				fireEvent(new TagSEAPartEvent(TagSEAPartEvent.Type.ViewClosed, (RoutesView)part));
			}
			
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partDeactivated(IWorkbenchPart part) {
			if (part instanceof TagsView) {
				fireEvent(new TagSEAPartEvent(TagSEAPartEvent.Type.ViewDeactivated, (TagsView)part));
			} else if (part instanceof RoutesView) {
				fireEvent(new TagSEAPartEvent(TagSEAPartEvent.Type.ViewDeactivated, (RoutesView)part));
			}
			
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partOpened(IWorkbenchPart part) {
			if (part instanceof TagsView) {
				fireEvent(new TagSEAPartEvent(TagSEAPartEvent.Type.ViewOpened, (TagsView)part));
			} else if (part instanceof RoutesView) {
				fireEvent(new TagSEAPartEvent(TagSEAPartEvent.Type.ViewOpened, (RoutesView)part));
			}
		}
		
	}
	
	class SelectionListener implements ISelectionListener {

		/* (non-Javadoc)
		 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
		 */
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (part instanceof RoutesView || part instanceof TagsView) {
				fireEvent(new TagSEASelectionEvent(part, selection));
			}
		}
	}
	
	private Monitoring() {
		//Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
		monitors = new ArrayList<ITagSEAMonitor>();
		allMonitors = new ArrayList<ITagSEAMonitor>();
		publisher = new MonitorThread();
		activated = new ArrayList<ITagSEAMonitor>();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (this.tagsView == null) this.tagsView = part.getSite().getPage().findView(TagsView.ID);
		if (this.routesView == null) this.routesView = part.getSite().getPage().findView(RoutesView.ID);
		if (part != tagsView || part != routesView) return;
		selectionOccurred(part, selection);
	}
	
	/**
	 * @param part
	 * @param selection
	 */
	private void selectionOccurred(IWorkbenchPart part, ISelection selection) {
		publisher.queueEvent(new TagSEASelectionEvent(part, selection));	
	}
	
	public void fireEvent(ITagSEAMonitoringEvent evt) {
		publisher.queueEvent(evt);
	}
	
	public static Monitoring getDefault() {
		final ArrayList<ITagSEAMonitor> newMonitors = new ArrayList<ITagSEAMonitor>();
		synchronized (LOCK) {
			if (instance != null) return instance; 

			instance = new Monitoring();
			IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor("ca.uvic.cs.tagsea.monitor");
			for (IConfigurationElement element : elements) {
				try {
					ITagSEAMonitor monitor = (ITagSEAMonitor) element.createExecutableExtension("class");
					if (monitor != null) {
						instance.allMonitors.add(monitor);
						if (isNewMonitor(monitor)) {
							newMonitors.add(monitor);
						} 
					}
				} catch (CoreException e) {
					TagSEAPlugin.log("Error Loading Plugin", e);
				}
			}

		}

		final Wizard wizard = new MonitorAcceptanceWizard();
		TagSEAPlugin.getDefault().getWorkbench().getDisplay().syncExec(new Runnable(){
			public void run() {
				synchronized (LOCK) {
					Shell shell = TagSEAPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
					instance.hookWorkbenchListeners();
					if (newMonitors.size() > 0) {
						for (ITagSEAMonitor monitor : newMonitors) {
							wizard.addPage(new MonitorAcceptanceWizardPage(monitor.getName(), monitor));
						}
						final WizardDialog d = new WizardDialog(shell, wizard);
						d.setTitle("Monitor Acceptance");
						//d.setMessage("In order to help with the research of TagSEA, some monitors....");
						d.open();
					}
				}
			}
		});
		synchronized (LOCK) {
			instance.hookPreferences();
			instance.updatePreferences();
			instance.publisher.start();
			return instance;
		}

	}

	
	/**
	 * @param monitor
	 */
	private void activate(ITagSEAMonitor monitor) {
		synchronized (LOCK) {
			if (!activated.contains(monitor)) {
				monitor.activate();
				activated.add(monitor);
			}
		}
	}
	private static final boolean isNewMonitor(ITagSEAMonitor monitor) {
		IPreferenceStore prefs = TagSEAPlugin.getDefault().getPreferenceStore();
		String key = getPreferenceKey(monitor);
		return "".equals(prefs.getString(key));
	}
	
	/**
	 * 
	 */
	protected void hookWorkbenchListeners() {
		IWorkbench workbench = TagSEAPlugin.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWindowListener listener = new WindowListener();
		listener.windowActivated(window);
		workbench.addWindowListener(listener);

	}
	/**
	 * 
	 */
	private void updatePreferences() {
		IPreferenceStore prefs = TagSEAPlugin.getDefault().getPreferenceStore();
		synchronized (monitors) {
			for (ITagSEAMonitor monitor : allMonitors) {
				if (prefs.getBoolean(getPreferenceKey(monitor))) {
					activate(monitor);
				}
			}
			for (ITagSEAMonitor monitor : allMonitors) {
				if (prefs.getBoolean(getPreferenceKey(monitor))) {
					monitors.add(monitor);
				}
			}
		}
	}
	/**
	 * @param monitors2
	 */
	private void hookPreferences() {
		IPreferenceStore prefs = TagSEAPlugin.getDefault().getPreferenceStore();
		prefs.addPropertyChangeListener(new IPropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event) {
				String prop = event.getProperty();
				boolean active = false;
				try {
					active = Boolean.parseBoolean(event.getNewValue().toString());
				} catch (Exception e) {
					return;
				}
				ITagSEAMonitor monitor = getMonitorForPreference(prop);
				if (monitor != null) {
					synchronized (monitors) {
						if (active) {
							activate(monitor);
							monitors.add(monitor);
						} else {
							monitors.remove(monitor);
						}
					}
				}
			}
		});
	}
	
	
	public String[] getPreferenceKeys() {
		String[] keys = new String[allMonitors.size()];
		int i = 0;
		for (ITagSEAMonitor monitor : allMonitors) {
			keys[i] = getPreferenceKey(monitor);
			i++;
		}
		return keys;
	}
	
	public ITagSEAMonitor getMonitorForPreference(String key) {
		if (key.startsWith(PREF_KEY)) {
			String[] split = key.split(":");
			if (split.length == 2) {
				String clazz = split[1];
				for (ITagSEAMonitor monitor : allMonitors) {
					if (monitor.getClass().getName().equals(clazz)) {
						return monitor;
					}
				}
			}
		}
		return null;
	}
	
	
	public static String getPreferenceKey(ITagSEAMonitor monitor) {
		return PREF_KEY + monitor.getClass().getName();
	}

}
