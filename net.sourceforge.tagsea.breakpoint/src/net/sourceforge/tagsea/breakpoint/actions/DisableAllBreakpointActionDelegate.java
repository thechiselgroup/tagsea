package net.sourceforge.tagsea.breakpoint.actions;

import java.util.List;

import net.sourceforge.tagsea.breakpoint.waypoints.BreakpointUtil;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class DisableAllBreakpointActionDelegate implements
		IObjectActionDelegate {

	IWorkbenchPart fPart;
	IStructuredSelection fSelection;
	IWaypoint[] fWaypoints;

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
				IBreakpoint point = BreakpointUtil.findBreakpoint(waypoint);

				if(point!=null)
				{
					try 
					{
						if(point.isEnabled())
							point.setEnabled(false);
					} 
					catch (CoreException e)
					{

						e.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = (IStructuredSelection)selection;

		if(fSelection!=null && !fSelection.isEmpty())
		{
			List<IWaypoint> waypointList = fSelection.toList();
			fWaypoints = waypointList.toArray(new IWaypoint[0]);

			boolean allEnabled = true;

			for(IWaypoint waypoint : fWaypoints)
			{
				if(!waypoint.getBooleanValue(BreakpointUtil.ENABLMENT_ATTR, true))
				{
					allEnabled = false;
					break;
				}
			}

			if(!allEnabled)
				action.setEnabled(false);
		}
	}

}
