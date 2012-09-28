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
package com.ibm.research.tagging.core;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ibm.research.tagging.core.impl.TagCore;
import com.ibm.research.tagging.core.system.IEventDispatcher;
import com.ibm.research.tagging.core.system.impl.DefaultEventDispatcher;

/**
 * The activator class controls the plug-in life cycle
 */
public class TagCorePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.research.tagging.core";
	public static final String MODEL_EXTENSION_ID = PLUGIN_ID + ".waypointModel";

	// The shared instance
	private static TagCorePlugin plugin;
	
	private	ITagCore fTagCore;	
	private	IEventDispatcher fEventDispatcher;	
	
	/**
	 * The constructor
	 */
	public TagCorePlugin()
	{
		plugin = this;
		fEventDispatcher = new DefaultEventDispatcher();
		fTagCore = new TagCore();
		
		IExtensionRegistry registry 	       = Platform.getExtensionRegistry();
		IExtensionPoint    waypointModelextensionPt = registry.getExtensionPoint(MODEL_EXTENSION_ID);
		IExtension[]	   waypointModelExtensions  = waypointModelextensionPt.getExtensions();
		if ( waypointModelExtensions!=null )
		{
			for (int i=0; i<waypointModelExtensions.length; i++)
			{
				IExtension extension = waypointModelExtensions[i];
				IConfigurationElement[] config = extension.getConfigurationElements();
				for (int j=0; j<config.length; j++)
				{
					try
					{
						IWaypointModelExtension model = (IWaypointModelExtension) config[j].createExecutableExtension("class");
						
						// @tag debug tagsea extension-point model : comment out when no longer need to verify extensions are loading correctly
						System.out.println("adding model extension : name=" + config[j].getName() + " id=" + config[j].getAttribute("id"));
						fTagCore.addWaypointModelExtension(model);
					}
					catch (RuntimeException e)
					{
						System.out.println("unable to instantiate tag model extension : " + config[j].getName() + " ... skipping");
						e.printStackTrace();
					} 
					catch (CoreException e) 
					{
						System.out.println("unable to instantiate tag model extension : " + config[j].getName() + " ... skipping");
						e.printStackTrace();
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		Job job = new Job("Starting tag model") 
		{
			@Override
			protected IStatus run(IProgressMonitor monitor) 
			{
				fTagCore.start();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

		// Hook into the workspace save operation to persist the tag model
		// This will prevent problems with workspace services being shutdown during model saving
		try 
		{
			ResourcesPlugin.getWorkspace().addSaveParticipant(this, new ISaveParticipant() 
			{
				/*
				 * (non-Javadoc)
				 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
				 */
				public void saving(ISaveContext context) throws CoreException 
				{
					switch (context.getKind()) 
					{
						case ISaveContext.FULL_SAVE:
							fTagCore.stop();
					}
				}

				/*
				 * (non-Javadoc)
				 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
				 */
				public void rollback(ISaveContext context) 
				{
				}

				/*
				 * (non-Javadoc)
				 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
				 */
				public void prepareToSave(ISaveContext context) throws CoreException 
				{
				}

				/*
				 * (non-Javadoc)
				 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
				 */
				public void doneSaving(ISaveContext context) 
				{
				}
			});
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception 
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TagCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * Gets the event dispatcher.
	 * @return
	 */
	public IEventDispatcher getEventDispatcher()
	{
		return fEventDispatcher;
	}
	
	/**
	 * Gets the core tagging model.
	 * @return
	 */
	public ITagCore getTagCore()
	{
		return fTagCore;
	}
}
