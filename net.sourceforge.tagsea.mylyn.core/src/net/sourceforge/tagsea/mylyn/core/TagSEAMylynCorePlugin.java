/*******************************************************************************
 * 
 *   Copyright 2007, 2008, CHISEL Group, University of Victoria, Victoria, BC, Canada. 
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *       The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.mylyn.core;

import java.util.List;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class TagSEAMylynCorePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.mylyn.core";

	// The shared instance
	private static TagSEAMylynCorePlugin plugin;

	// Values for affecting task context
	protected static final String ORIGIN = "net.sourceforge.tagsea.mylyn";
	protected static final String KIND = "java";
	private static final int INTERACTION_WEIGHT = 100; // High enough to make a

	// landmark
	// (landmark=30?)

	/**
	 * The constructor
	 */
	public TagSEAMylynCorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static TagSEAMylynCorePlugin getDefault() {
		return plugin;
	}

	public static void addToContext(AbstractTask task, List<String> handles) {

		String taskHandle = task.getHandleIdentifier();

		if (ContextCorePlugin.getContextManager() != null) {
			InteractionContextManager interactionManager = ContextCorePlugin
					.getContextManager();
			interactionManager.activateContext(taskHandle);
			if (interactionManager.isContextActive()) {
				IInteractionContext context = ContextCorePlugin
						.getContextManager().getActiveContext();

				InteractionContextScaling scaling = context.getScaling();
				if (context != null) {
					for (String handle : handles) {
						// Example: kind: selection structureKind: java handle:
						// =net.sourceforge.tagsea/src<net.sourceforge.tagsea{TagSEAPlugin.java
						// originId: org.eclipse.ui.navigator.ProjectExplorer
						// interest: 1.0
						InteractionEvent event = new InteractionEvent(
								InteractionEvent.Kind.SELECTION, KIND, handle,
								ORIGIN, scaling.getForcedLandmark());
						// Call the right method
						if (context instanceof CompositeInteractionContext) {
							((CompositeInteractionContext) context)
									.addEvent(event);
						}
						if (context instanceof InteractionContext) {
							((InteractionContext) context).parseEvent(event);
						}

						IInteractionElement element = ContextCorePlugin
								.getContextManager().getElement(handle);
						boolean manipulated = interactionManager
								.manipulateInterestForElement(element, true,
										false, false, ORIGIN, context);
					}
				}
			}
			interactionManager.deactivateContext(taskHandle);
			
			testAddingContext(task, handles);
		}
	}

	private static void testAddingContext(AbstractTask task,
			List<String> handles) {
		String taskHandle = task.getHandleIdentifier();
		InteractionContextManager interactionManager = ContextCorePlugin
				.getContextManager();
		interactionManager.activateContext(taskHandle);
		IInteractionContext context = ContextCorePlugin.getContextManager()
				.getActiveContext();

		if(context.getLandmarks().size() == handles.size()){
			System.out.println("Landmarks OK");
		}else{
			System.err.println("Landmarks not OK : expected: " + handles.size() + " actual: " + context.getLandmarks().size());
		}
		
		if(context.getInteresting().size() == handles.size()){
			System.out.println("Interesting OK");
		}else{
			System.err.println("Intersting not OK: expected: " + handles.size() + " actual: " + context.getInteresting().size());
		}
		
		
		for( IInteractionElement element : context.getInteresting()){
			System.out.println(element.getHandleIdentifier() + " -->  " + element.getInterest());
		}
		
		interactionManager.deactivateContext(taskHandle);
	}
}
