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
package com.ibm.research.tagging.core.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.dialogs.OpenWaypointDialog;

/**
 * opens OpenWaypointDialog via menu or hot-key command binding
 */
public class OpenWaypointAction extends Action implements IWorkbenchWindowActionDelegate, IHandler {
	private List<IHandlerListener> listeners = new ArrayList<IHandlerListener>();
	
	/**
	 * The constructor.
	 */
	public OpenWaypointAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		runAction();
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
	}

	public void addHandlerListener(IHandlerListener handlerListener) {
		listeners.add(handlerListener);
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		runAction();
		return null;
	}

	private void runAction() {
		OpenWaypointDialog dialog = new OpenWaypointDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		if ( dialog.open() == Window.OK )
		{
			IWaypoint[] waypoints = dialog.getWaypoints();
			if ( waypoints!=null )
			{
				for (IWaypoint wp : waypoints)
					wp.navigate();
			}
		}
	}

	public void removeHandlerListener(IHandlerListener handlerListener) {
		listeners.remove(handlerListener);
	}
}