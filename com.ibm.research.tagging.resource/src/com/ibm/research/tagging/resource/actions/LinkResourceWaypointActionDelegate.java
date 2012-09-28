/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/

package com.ibm.research.tagging.resource.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.ccvs.ui.ICVSUIConstants;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.progress.UIJob;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.waypoints.WaypointView;
import com.ibm.research.tagging.resource.ResourceWaypointPlugin;
import com.ibm.research.tagging.resource.decorators.IDecoratableResourceContributor;
import com.ibm.research.tagging.resource.decorators.ResourceWaypointDecorator;

public class LinkResourceWaypointActionDelegate implements IViewActionDelegate {

	private static final String DECORATOR_ID = "com.ibm.research.tagging.resource.decorator";
	private static final String EXTENSION_ID = ResourceWaypointPlugin.PLUGIN_ID + ".decoratableResource";
	
	private WaypointView fView;
	private IAction fAction;
	private List<IDecoratableResourceContributor> fContributors = new ArrayList<IDecoratableResourceContributor>();
	
	private boolean fLastChecked=false;
	private int	    fLastCount=0;
	private Boolean fCVSFontPref=null;

	public void init(IViewPart view) {
		fView = (WaypointView) view;
		fView.getWaypointTableViewer().getTable().addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if ( fAction!=null )
					updatePackageExplorer((Table) e.widget);
			}
		});
		
		fView.getWaypointTableViewer().getTable().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				
				// in case workbench gets shutdown and user never toggles off this button - need to restore the CVS font preference if it was changed by us
				if ( fCVSFontPref!=null )
				{
					// @tag hack cvs tagsea resource : restore CVS font decorator if it was enabled previously
					IPreferenceStore cvsUiPrefStore = CVSUIPlugin.getPlugin().getPreferenceStore();
					cvsUiPrefStore.setValue(ICVSUIConstants.PREF_USE_FONT_DECORATORS, fCVSFontPref.booleanValue());
				}
				
			}
		});
		
		// setup extensions
		setupContributors();
	}

	private void setupContributors() {
		IExtensionRegistry registry    = Platform.getExtensionRegistry();
		IExtensionPoint    extensionPt = registry.getExtensionPoint(EXTENSION_ID);
		IExtension[]	   extensions  = extensionPt.getExtensions();
		if ( extensions!=null )
		{
			for (int i=0; i<extensions.length; i++)
			{
				IExtension extension = extensions[i];
				IConfigurationElement[] config = extension.getConfigurationElements();
				for (int j=0; j<config.length; j++)
				{
					try
					{
						IDecoratableResourceContributor contributor = (IDecoratableResourceContributor) config[j].createExecutableExtension("class");
						
						// @tag debug tagsea extension-point model : comment out when no longer need to verify extensions are loading correctly
						System.out.println("adding decoratable resource extension : name=" + config[j].getName() + " id=" + config[j].getAttribute("id"));
						fContributors.add(contributor);
					}
					catch (RuntimeException e)
					{
						ResourceWaypointPlugin.log("unable to instantiate decoratable resource extension : " + config[j].getName() + " ... skipping",e);
					} 
					catch (CoreException e) 
					{
						ResourceWaypointPlugin.log("unable to instantiate decoratable resource extension : " + config[j].getName() + " ... skipping",e);
					}
				}
			}
		}
	}

	public void run(IAction action) {
		fAction = action;
		updatePackageExplorer(fView.getWaypointTableViewer().getTable());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fAction = action;
	}
	
	private Set<IAdaptable> findSelectedResources(PackageExplorerPart view)
	{
		Set<IAdaptable> resources = new HashSet<IAdaptable>();
		
		int count = fView.getWaypointTableViewer().getTable().getItemCount();
		for (int i=0; i<count; i++)
		{
			Object o = fView.getWaypointTableViewer().getElementAt(i);		
			if ( o instanceof IWaypoint )
			{
				IWaypoint waypoint = (IWaypoint) o;
				for (IDecoratableResourceContributor contributor : fContributors)
				{
					IResource resource = null;
					
					try {
						resource = contributor.getResource(waypoint);
					} catch (RuntimeException e) {
						ResourceWaypointPlugin.log("error processing IDecoratableResourceContributor " + contributor + " with waypoint " + waypoint,e);
					}
					
					if ( resource!=null )
					{
						resources.add(resource);
						
						// walk up parent hierarchy
						IContainer parent = resource.getParent();
						while ( parent!=null )
						{
							resources.add(parent);
							parent = parent.getParent();
						}
						
						IProject project = resource.getProject();
						
						if ( project!=null )
						{
							// look for associated working sets
							IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
							IWorkingSet[] workingSets = workingSetManager.getAllWorkingSets();
							if ( workingSets!=null )
							{
								for (IWorkingSet set : workingSets)
								{
									IAdaptable[] elements = set.getElements();
									for (IAdaptable element : elements)
									{
										IProject workingSetProject = null;
										if ( element instanceof IProject )
											workingSetProject = (IProject) element;
										else
											workingSetProject = (IProject) element.getAdapter(IProject.class);
										
										if ( project.equals(workingSetProject) )
											resources.add(set);
									}
								}
							}
						}
						
						continue;
					}
				}
			}
		}

		return resources;
	}

	private void updatePackageExplorer(Table table) {
		
		if ( !fAction.isChecked() && !fLastChecked )
			return;		// no longer need to update UI if this feature is turned off

		if ( fAction.isChecked()!=fLastChecked )  // only enable/disable via decoratorManager if something has changed
		{
			IDecoratorManager decoratorManager  = PlatformUI.getWorkbench().getDecoratorManager();
			
			if ( fAction.isChecked() )
			{
				// @tag hack cvs tagsea resource : this is a hack - disable CVS font decorator - because it overrides our decorator! this won't prevent any other coloring decorator from stopping us...
				IPreferenceStore cvsUiPrefStore = CVSUIPlugin.getPlugin().getPreferenceStore();
				fCVSFontPref = new Boolean(cvsUiPrefStore.getBoolean(ICVSUIConstants.PREF_USE_FONT_DECORATORS));   // save previous pref setting to restore later
				cvsUiPrefStore.setValue(ICVSUIConstants.PREF_USE_FONT_DECORATORS, false);
				
				// force enablement of decorator if button is selected
				try {
					decoratorManager.setEnabled(DECORATOR_ID, true);
				} catch (CoreException e) {
					ResourceWaypointPlugin.log("error while trying to set enablement on decorator " + DECORATOR_ID + " to " + fAction.isChecked(),e);
				}
			}
			else
			{
				// toggle off
				ResourceWaypointDecorator decorator = (ResourceWaypointDecorator) decoratorManager.getBaseLabelProvider(DECORATOR_ID);
				if ( decorator==null )
					ResourceWaypointPlugin.log("unable to retrieve decorator for id=" + DECORATOR_ID + " in order to toggle off resource waypoint decorator");
				decorator.setResources(null);
				decorator.refresh();
				
				// @tag hack cvs tagsea resource : restore CVS font decorator if it was enabled previously
				IPreferenceStore cvsUiPrefStore = CVSUIPlugin.getPlugin().getPreferenceStore();
				cvsUiPrefStore.setValue(ICVSUIConstants.PREF_USE_FONT_DECORATORS, fCVSFontPref.booleanValue());
			}
		}
		
		boolean toggledOn = fAction.isChecked() && !fLastChecked;   // keep track if the user flipped the toggle on (from off)
		
		fLastChecked = fAction.isChecked();
		
		// check count of items in the table - if it changed, then we need a refresh
		TableItem[] items = table.getItems();
		int count = 0;
		if ( items!=null )
			count = items.length;
		
		if ( count==fLastCount && !toggledOn )
			return;
		
		fLastCount = count;

		UIJob job = new UIJob("update decorators") {

			public IStatus runInUIThread(IProgressMonitor monitor) {
			
				IDecoratorManager decoratorManager  = PlatformUI.getWorkbench().getDecoratorManager();
				ResourceWaypointDecorator decorator = (ResourceWaypointDecorator) decoratorManager.getBaseLabelProvider(DECORATOR_ID);
				
				if ( decorator==null && fAction.isChecked() )
				{
					// decorator thread not completed yet - we'll wait a bit before giving up
					int nTries = 0;
					while ( decorator==null )
					{
						if ( monitor.isCanceled() )
							return Status.CANCEL_STATUS;
						
						decorator = (ResourceWaypointDecorator) decoratorManager.getBaseLabelProvider(DECORATOR_ID);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							return Status.CANCEL_STATUS;
						}
						
						nTries++;
						if ( nTries>10 || !fAction.isChecked() )
						{
							break;
						}
					}
				}
				
				if ( decorator==null )
				{
					ResourceWaypointPlugin.log("error - unable to retrieve decorator id=" + DECORATOR_ID + " from decoratorManager");
					return Status.OK_STATUS;
				}
				
				PackageExplorerPart view = PackageExplorerPart.getFromActivePerspective();
				if ( view==null )
					view = PackageExplorerPart.openInActivePerspective();
				
				if ( monitor.isCanceled() )
					return Status.CANCEL_STATUS;
				
				Set<IAdaptable> found = findSelectedResources(view);

				if ( monitor.isCanceled() )
					return Status.CANCEL_STATUS;
				
				if ( decorator!=null && found!=null )
				{
					decorator.setResources(found);
					decorator.refresh();
				}
				
				fView.setFocus();
				
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}
}
