package net.sourceforge.tagsea.delicious.actions;

import java.util.Date;
import java.util.List;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.delicious.DeliciousWaypointPlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import del.icio.us.Delicious;

public class PostURLActionDelegate implements IObjectActionDelegate{
	
	IWorkbenchPart fPart;
	IStructuredSelection fSelection;
	IWaypoint[] fWaypoints;
	//private Delicious fDelicious;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		fPart = targetPart;
	}
	
	public void run(IAction action) 
	{
		if(fWaypoints!=null)
		{
			for(IWaypoint waypoint : fWaypoints)
			{
				String url = waypoint.getStringValue("URL","");
				
				if(url!=null)
				{
					Delicious delicious = DeliciousWaypointPlugin.getDefault().getDeliciousInstance();
					
					String description = waypoint.getText();
					ITag[] tags = waypoint.getTags();
					Date date = waypoint.getDate();
					StringBuffer tagBuffer = new StringBuffer();
					
					for(ITag t : tags)
					{
						if(!t.getName().trim().equalsIgnoreCase("Default"))
						{
							tagBuffer.append(" " + t.getName().trim());
						}
					}
					
					delicious.addPost(url, description, null, tagBuffer.toString(), date);
					
					// Force a ui update
					DeliciousWaypointPlugin.getDefault().getPreferenceStore().firePropertyChangeEvent("update", "update", "update");
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = (IStructuredSelection)selection;

		if(fSelection!=null && !fSelection.isEmpty())
		{
			List<?> waypointList = fSelection.toList();
			fWaypoints = waypointList.toArray(new IWaypoint[0]);
			action.setEnabled(true);
		}
	}
}
