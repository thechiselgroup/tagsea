package net.sourceforge.tagsea.url.actions;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.url.waypoints.URLWaypointUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class CopyURLActionDelegate implements IObjectActionDelegate{
	
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
			String url = fWaypoint.getStringValue(URLWaypointUtil.URL_ATTR, "");
			Clipboard cb = new Clipboard(Display.getDefault());
			
			String textData = url;
			if (textData.length() > 0) 
			{
				TextTransfer textTransfer = TextTransfer.getInstance();
				cb.setContents(new Object[]{textData}, new Transfer[]{textTransfer});
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
