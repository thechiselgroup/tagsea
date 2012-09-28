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
package net.sourceforge.tagsea.mylyn.todo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask;

public class TodoUtils {

	// @tag tagsea.mylyn.seeAlso -author="John" : #net.sourceforge.tagsea.tasks.WaypointUtils.getTagString
	public static String getTaskString(IMarker marker) {

		try {
			if (marker.isSubtypeOf(IMarker.TASK)) {
				String message = getDescription(marker);
				String location = getLocation(marker);

				return message + " " + location;

			}
		} catch (CoreException e) {
			// TODO Better logging technique
			System.err
					.println("Core exception occurred while creating the task string.");
		}
		return null;
	}

	public static String getLocation(IMarker marker) {
		String location = marker.getAttribute(IMarker.LOCATION, null);
		if (location == null) {
			IResource resource = marker.getResource();
			location = resource.getFullPath().toPortableString();
			String line = null;
			try {
				line = marker.getAttribute(IMarker.LINE_NUMBER).toString();
			} catch (CoreException e) {
				// TODO Better logging of problem
				e.printStackTrace();
			}
			if (line != null)
				location += ":" + line;
		}
		return location;
	}

	public static String getDescription(IMarker marker) {
		String message = marker.getAttribute(IMarker.MESSAGE, "");
		return message;
	}

	public static String getTodoInfo(List<IMarker> todos) {

		StringBuffer taskInfo = new StringBuffer();
		for (IMarker todo : (List<IMarker>) todos) {
			taskInfo.append(TodoUtils.getTaskString(todo) + "\n");
		}

		return taskInfo.toString();
	}

	public static List<IMarker> getTodos(StructuredSelection selection) {
		List<IMarker> todos = new ArrayList<IMarker>();
		try {
			for (IMarker marker : (List<IMarker>) selection.toList()) {
				if (marker.isSubtypeOf(IMarker.TASK)) {
					todos.add(marker);
				}
			}
		} catch (CoreException e) {
			// TODO Better logging technique
			System.err
					.println("Core exception occurred while creating new task.");
		}
		return todos;
	}

	public static void recordTask(List<IMarker> todos, AbstractTask task) {
		for (IMarker todo : todos) {
			TodoMylynPlugin.getDefault().addTodo(todo, task);
		}
	}
	
	public static List<String> getHandles(List<IMarker> taskMarkers){
		List<String> handles = new ArrayList<String>();
		for(IMarker marker : taskMarkers){
			handles.add(getHandle(marker));
		}
		return handles;
	}
	
	// @tag tagsea.mylyn.clone -author=John -date="enCA:20/03/08" : Synchronize changes with WaypointUtils.getHandle(IWaypoint)
	private static String getHandle(IMarker todoMarker){
		try {
			IResource resource = todoMarker.getResource();
			IJavaElement element = JavaCore.create(resource);
			if (element instanceof ICompilationUnit) {
				ICompilationUnit unit = (ICompilationUnit)element;
				int lineNum = Integer.parseInt(todoMarker.getAttribute(IMarker.CHAR_START).toString());
				IJavaElement unitElement = unit.getElementAt(lineNum);
				if(unitElement != null){
					// unitElement is a method or field
					return unitElement.getHandleIdentifier();
				}else{ // use the enclosing class
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
}
