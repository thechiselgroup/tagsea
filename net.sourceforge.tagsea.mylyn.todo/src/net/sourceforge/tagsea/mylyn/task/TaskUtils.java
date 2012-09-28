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
package net.sourceforge.tagsea.mylyn.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.mylyn.core.LocationDescriptor;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask;

public class TaskUtils {

	// @tag tagsea.mylyn.seeAlso -author="John" :
	// #net.sourceforge.tagsea.tasks.WaypointUtils.getTagString
	public static String getTaskString(IMarker marker, boolean tag) {

		try {
			if (marker.isSubtypeOf(IMarker.TASK)) {
				String message = getDescription(marker);
				String location = getLocation(marker);
				String taskInfo = message
						+ LocationDescriptor.LOCATON_DELIMINTER + location;
				if (tag) {
					return TaskHyperlink.LINK_TAG + " " + taskInfo;
				} else {
					return taskInfo;
				}

			}
		} catch (CoreException e) {
			// TODO Better logging technique
			System.err
					.println("Core exception occurred while creating the task string.");
		}
		return null;
	}

	// @tag tagsea.mylyn.refactor.location : Check if this can be refactored
	// with getLocation from the WaypointUtils
	public static String getLocation(IMarker marker) {
		String location = marker.getAttribute(IMarker.LOCATION, null);
		if (location == null) {
			IResource resource = marker.getResource();
			IPath path = resource.getLocation();
			location = path.lastSegment();

			String line = null;
			try {
				line = marker.getAttribute(IMarker.LINE_NUMBER).toString();
			} catch (CoreException e) {
				location = "";
			}
			if (line != null)
				location += " line " + line;
		}
		return location;
	}

	public static String getDescription(IMarker marker) {
		String message = marker.getAttribute(IMarker.MESSAGE, "");
		return message;
	}

	public static String getTaskInfo(List<IMarker> todos) {

		StringBuffer taskInfo = new StringBuffer();
		for (IMarker todo : (List<IMarker>) todos) {
			taskInfo.append(TaskUtils.getTaskString(todo, true) + "\n");
		}

		return taskInfo.toString();
	}

	public static List<IMarker> getTasks(StructuredSelection selection) {
		List<IMarker> tasks = new ArrayList<IMarker>();
		try {
			for (IMarker marker : (List<IMarker>) selection.toList()) {
				if (marker.isSubtypeOf(IMarker.TASK)) {
					tasks.add(marker);
				}
			}
		} catch (CoreException e) {
			// TODO Better logging technique
			System.err
					.println("Core exception occurred while creating new task.");
		}
		return tasks;
	}

	public static void recordTask(List<IMarker> markers, AbstractTask task) {
		for (IMarker marker : markers) {
			TaskMylynPlugin.getDefault().addMarker(marker, task);
		}
	}

	public static List<String> getHandles(List<IMarker> taskMarkers) {
		List<String> handles = new ArrayList<String>();
		for (IMarker marker : taskMarkers) {
			handles.add(getHandle(marker));
		}
		return handles;
	}

	// @tag tagsea.mylyn.refactor.handles : can this be refactored to avoind
	// syncronization?
	// @tag tagsea.mylyn.clone -author=John -date="enCA:20/03/08" : Synchronize
	// changes with WaypointUtils.getHandle(IWaypoint)
	private static String getHandle(IMarker todoMarker) {
		try {
			IResource resource = todoMarker.getResource();
			IJavaElement element = JavaCore.create(resource);
			if (element instanceof ICompilationUnit) {
				ICompilationUnit unit = (ICompilationUnit) element;
				int lineNum = Integer.parseInt(todoMarker.getAttribute(
						IMarker.CHAR_START).toString());
				IJavaElement unitElement = unit.getElementAt(lineNum);
				if (unitElement != null) {
					// unitElement is a method or field
					return unitElement.getHandleIdentifier();
				} else { // use the enclosing class
					return unit.getHandleIdentifier();
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static IMarker markerFromText(String taskText) {

		LocationDescriptor descriptor = LocationDescriptor.createFromText(
				TaskHyperlink.LINK_TAG, "", taskText);

		Set<IMarker> markers = TaskMylynPlugin.getDefault().getAllMarkers();
		for (IMarker marker : markers) {
			String markerLocation = getLocation(marker);
			if (markerLocation.equals(descriptor.getLocation())) {
				return marker;
			}
		}
		return null;
	}

	public static List<IMarker> getNewTodos(AbstractTask task,
			List<IMarker> taskMarkers) {
		List<IMarker> newTodos = new ArrayList<IMarker>(taskMarkers);
		Set<IMarker> existingTodos = TaskMylynPlugin.getDefault().getMarkers(
				task);
		if (existingTodos == null) {
			return newTodos;
		}

		for (IMarker todo : taskMarkers) {
			if (existingTodos.contains(todo)) {
				newTodos.remove(todo);
			}
		}
		return newTodos;
	}
}
