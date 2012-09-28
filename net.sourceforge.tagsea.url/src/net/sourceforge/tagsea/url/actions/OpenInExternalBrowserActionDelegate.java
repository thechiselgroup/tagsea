package net.sourceforge.tagsea.url.actions;

import java.io.IOException;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.url.waypoints.URLWaypointUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class OpenInExternalBrowserActionDelegate implements IObjectActionDelegate{

	IWorkbenchPart fPart;
	IStructuredSelection fSelection;
	IWaypoint fWaypoint;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		fPart = targetPart;
	}

	public void run(IAction action) 
	{
		if(fWaypoint!=null)
		{
			String urlString = fWaypoint.getStringValue(URLWaypointUtil.URL_ATTR, "");
			
			// Check for protocol preamble
			int index = urlString.indexOf("://");

			// No preamble, use default http://
			if(index < 0)
				urlString = "http://" + urlString;
			
			String[] cmd = new String[5];
			String OS = System.getProperty("os.name").toLowerCase();

			if ( (OS.indexOf("nt") > -1)
					|| (OS.indexOf("windows 2000") > -1 )
					|| (OS.indexOf("windows xp") > -1) ) {

				cmd[0] = "cmd.exe";
				cmd[1] = "/C";
				cmd[2] = "rundll32";
				cmd[3] = "url.dll,FileProtocolHandler";
				cmd[4] = urlString;

				try 
				{
					Process process = Runtime.getRuntime().exec( cmd );
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = (IStructuredSelection)selection;

		if(fSelection!=null)
			fWaypoint = (IWaypoint)fSelection.getFirstElement();
	}
}