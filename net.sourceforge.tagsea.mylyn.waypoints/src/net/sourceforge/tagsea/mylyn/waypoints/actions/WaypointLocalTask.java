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
package net.sourceforge.tagsea.mylyn.waypoints.actions;

import java.util.List;

import net.sourceforge.tagsea.core.ITextWaypointAttributes;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.mylyn.core.actions.MylynLocalTask;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointsUtils;
import net.sourceforge.tagsea.resources.IResourceInterfaceAttributes;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.InteractionContextElement;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.tasks.core.AbstractTask;

public class WaypointLocalTask extends MylynLocalTask {

	private List<IWaypoint> waypoints;

	@Override
	protected String getTaskInfo() {
		return WaypointsUtils.getWaypointInformation(this.waypoints);
	}

	@Override
	protected void recordTask(AbstractTask task) {
		WaypointsUtils.recordTask(this.waypoints, task);
	}

	@Override
	protected void gatherData(StructuredSelection selection) {
		this.waypoints = WaypointsUtils.getWaypoints(selection);
	}

	@Override
	protected List<String> getHandles() {
		return WaypointsUtils.getHandles(this.waypoints);
	}

	@Override
	protected void postOps(AbstractTask task) {
		WaypointsUtils.addTaskTag(task, waypoints);
		//addToContext(task, this.waypoints);
	}

	public static void addToContext(AbstractTask task, List<IWaypoint> waypoints) {

		String taskHandle = task.getHandleIdentifier();

		if (ContextCorePlugin.getContextManager() != null) {
			InteractionContextManager interactionManager = ContextCorePlugin
					.getContextManager();
			interactionManager.activateContext(taskHandle);
			if (interactionManager.isContextActive()) {
				IInteractionContext context = ContextCorePlugin
						.getContextManager().getActiveContext();
				for (IWaypoint waypoint : waypoints) {

					String resourcePath = waypoint.getStringValue(
							IResourceInterfaceAttributes.ATTR_RESOURCE, "");
					IResource resource = ResourcesPlugin.getWorkspace()
							.getRoot().findMember(resourcePath);
					IJavaElement jElement = JavaCore.create(resource);
					IJavaElement unitElement = null;
					if (jElement instanceof ICompilationUnit) {
						ICompilationUnit unit = (ICompilationUnit) jElement;
						Integer charStart = (Integer) waypoint
								.getValue(ITextWaypointAttributes.ATTR_CHAR_START);
						try {
							unitElement = unit.getElementAt(charStart
									.intValue());
						} catch (JavaModelException e) {
						}
					}

					AbstractContextStructureBridge bridge;
					String elemHandle;
					if (unitElement != null) {
						bridge = ContextCorePlugin.getDefault()
								.getStructureBridge(unitElement);
						elemHandle = bridge.getHandleIdentifier(unitElement);
					} else {
						bridge = ContextCorePlugin.getDefault()
								.getStructureBridge(jElement);
						elemHandle = bridge.getHandleIdentifier(jElement);
					}

					IInteractionElement element = ContextCorePlugin
							.getContextManager().getElement(elemHandle);

					boolean manipulated = interactionManager
							.manipulateInterestForElement(element, true, false,
									false, "java", context);
					if (manipulated)
						System.err.println("manipulated: " + elemHandle);
					else
						System.err.println("NOT MANIPULATED: " + elemHandle);
				}
				/*
				 * IInteractionContext context = interactionManager
				 * .getActiveContext(); InteractionContextScaling scaling =
				 * context.getScaling(); if (context != null) { for (String
				 * handle : handles) { // Example: kind: selection
				 * structureKind: java handle: =net.sourceforge.tagsea/src<net.sourceforge.tagsea{TagSEAPlugin.java
				 * originId: org.eclipse.ui.navigator.ProjectExplorer interest:
				 * 1.0 InteractionEvent event = new
				 * InteractionEvent(InteractionEvent.Kind.SELECTION, KIND,
				 * handle, ORIGIN, scaling.getForcedLandmark()); event. // Call
				 * the right method if (context instanceof
				 * CompositeInteractionContext) { ((CompositeInteractionContext)
				 * context) .addEvent(event); } if (context instanceof
				 * InteractionContext) { ((InteractionContext)
				 * context).parseEvent(event); } } }
				 */
			}
			System.err.println("Deactivating context...");
			interactionManager.deactivateContext(taskHandle);
			System.err.println("Context deactivated.");
		}
	}
}
