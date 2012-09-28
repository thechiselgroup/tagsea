package net.sourceforge.tagsea.breakpoint.actions;

import net.sourceforge.tagsea.breakpoint.waypoints.BreakpointUtil;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class DisableBreakpointActionDelegate implements IObjectActionDelegate{

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
			IBreakpoint point = BreakpointUtil.findBreakpoint(fWaypoint);

			if(point!=null)
			{
				try 
				{
					point.setEnabled(false);
				} 
				catch (CoreException e)
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
		{
			fWaypoint = (IWaypoint)fSelection.getFirstElement();

			if(fWaypoint != null)
			{
				boolean enabled = fWaypoint.getBooleanValue(BreakpointUtil.ENABLMENT_ATTR, true);

				if(!enabled)
					action.setEnabled(false);
			}
		}
	}
}
