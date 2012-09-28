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
package net.sourceforge.tagsea.core.ui.internal;

import java.util.HashSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.internal.ITagSEAPreferences;
import net.sourceforge.tagsea.core.ui.TagSEAView;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Class responsible for publishing various UI events including opening and closing of views, various
 * changes to the filtering mechanisms, etc.
 * @author Del Myers
 *
 */
public class UIEventModel {
	
	private HashSet<ITagSEAUIListener> listeners;
	private WindowListener windowListener;
	private IPropertyChangeListener preferenceListener;
	
	private class WindowListener implements IWindowListener {
		private IWorkbenchWindow currentWindow;
		private PageListener pageListener = new PageListener(); 
		public void windowActivated(IWorkbenchWindow window) {
			if (currentWindow != null) {
				currentWindow.removePageListener(pageListener);
			}
			this.currentWindow = window;
			if (window.getActivePage() != null) pageListener.pageActivated(window.getActivePage());
			window.addPageListener(pageListener);
		}
		public void windowClosed(IWorkbenchWindow window) {}
		public void windowDeactivated(IWorkbenchWindow window) {
			if (window == currentWindow) {
				currentWindow.removePageListener(pageListener);
				currentWindow = null;
			}
		}
		public void windowOpened(IWorkbenchWindow window) {}
		/**
		 * 
		 */
		public void stop() {
			if (currentWindow != null) {
				currentWindow.removePageListener(pageListener);
				currentWindow = null;
				pageListener.stop();
			}
		}
	}
	
	private class PageListener implements IPageListener {
		IWorkbenchPart part;
		IWorkbenchPage currentPage;
		IPartListener2 partListener = new PartListener();
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPageListener#pageActivated(org.eclipse.ui.IWorkbenchPage)
		 */
		public void pageActivated(IWorkbenchPage page) {
			if (currentPage != null) {
				currentPage.removePartListener(partListener);
			}
			this.currentPage = page;
			if (page.getActivePartReference() != null) partListener.partActivated(page.getActivePartReference());
			page.addPartListener(partListener);
		}
		public void pageClosed(IWorkbenchPage page) {
			if (page == currentPage) {
				currentPage.removePartListener(partListener);
				currentPage = null;
			}
		}
		public void pageOpened(IWorkbenchPage page) {}
		
		public void stop() {
			if (currentPage != null) {
				currentPage.removePartListener(partListener);
				currentPage = null;
			}
		}
		
	}
	
	private class PartListener implements IPartListener2 {

		private boolean isInterestingView(IWorkbenchPartReference partRef) {
			//return TagsView.ID.equals(partRef.getId()) || WaypointView.ID.equals(partRef.getId());
			return TagSEAView.ID.equals(partRef.getId());
		}
		public void partActivated(IWorkbenchPartReference partRef) {
			if (isInterestingView(partRef)) {
				fireEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_ACTIVATED, partRef.getId(), null));
			}
			
		}
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			if (isInterestingView(partRef)) {
				fireEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_TOP, partRef.getId(), null));
			}
		}
		public void partClosed(IWorkbenchPartReference partRef) {
			if (isInterestingView(partRef)) {
				fireEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_CLOSED, partRef.getId(), null));
			}
		}
		public void partDeactivated(IWorkbenchPartReference partRef) {
			if (isInterestingView(partRef)) {
				fireEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_DEACTIVATED, partRef.getId(), null));
			}
		}
		public void partHidden(IWorkbenchPartReference partRef) {
			if (isInterestingView(partRef)) {
				fireEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_HIDDEN, partRef.getId(), null));
			}
		}
		public void partInputChanged(IWorkbenchPartReference partRef) {}
		public void partOpened(IWorkbenchPartReference partRef) {
			if (isInterestingView(partRef)) {
				fireEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_OPENED, partRef.getId(), null));
			}
		}
		public void partVisible(IWorkbenchPartReference partRef) {}
	}
	
	private class PreferenceListener implements IPropertyChangeListener {

		public void propertyChange(PropertyChangeEvent event) {
			if (ITagSEAPreferences.FILTERED_WAYPOINT_TYPES.equals(event.getProperty())) {
				fireEvent(TagSEAUIEvent.createFilterEvent());
			} else if (ITagSEAPreferences.TAGS_VIEW_TREE.equals(event.getProperty())) {
				fireEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_HIERARCHY, TagSEAView.ID, event.getNewValue().toString()));
			}
		}
		
	}
	
	
	public UIEventModel() {
		listeners = new HashSet<ITagSEAUIListener>();
		windowListener = new WindowListener();
		preferenceListener = new PreferenceListener();
	}
	
	
	public void addListener(ITagSEAUIListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ITagSEAUIListener listener) {
		listeners.remove(listener);
	}
	
	public void fireEvent(TagSEAUIEvent event) {
		ITagSEAUIListener[] array = listeners.toArray(new ITagSEAUIListener[listeners.size()]);
		for (ITagSEAUIListener listener : array) {
			listener.eventPerformed(event);
		}
	}
	
	
	/**
	 * Hooks this publisher to the platform ui to listen for view opening/closing and specific preference
	 * changes for filtering.
	 *
	 */
	public void hookToUI() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			windowListener.windowActivated(window);
		}
		PlatformUI.getWorkbench().addWindowListener(windowListener);
		IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
		Object tree = store.getString(ITagSEAPreferences.TAGS_VIEW_TREE);
		fireEvent(TagSEAUIEvent.createFilterEvent());
		fireEvent(TagSEAUIEvent.createViewEvent(TagSEAUIEvent.VIEW_HIERARCHY, TagSEAView.ID, tree.toString()));
		store.addPropertyChangeListener(preferenceListener);
		
	}


	/**
	 * Releases all listeners and stops the publishing of events.
	 */
	public void stop() {
		PlatformUI.getWorkbench().removeWindowListener(windowListener);
		windowListener.stop();
		TagSEAPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(preferenceListener);		
	}

}
