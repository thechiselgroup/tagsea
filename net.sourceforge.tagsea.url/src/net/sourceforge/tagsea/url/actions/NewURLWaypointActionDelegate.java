package net.sourceforge.tagsea.url.actions;

import java.util.Date;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.url.URLWaypointPlugin;
import net.sourceforge.tagsea.url.waypoints.URLWaypointUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class NewURLWaypointActionDelegate implements IViewActionDelegate {

	private IViewPart part;

	public void init(IViewPart view) 
	{
		part = view;
	}

	public void run(IAction action) 
	{
		URLWaypointCreateDialog dialog = new URLWaypointCreateDialog(part.getSite().getShell());
		int result = dialog.open();
		if (result != Dialog.OK) return;

		String[] tagNames = dialog.getTagNames();
		String desc = dialog.getDescription();
		Date date = dialog.getDate();
		String author = dialog.getAuthor();
		String url = dialog.getURL();

		IWaypoint wp = TagSEAPlugin.getWaypointsModel().createWaypoint(URLWaypointPlugin.WAYPOINT_ID, tagNames);
		if (wp != null) 
		{
			if(desc != null && desc.trim().length() !=0)
				wp.setText(desc);
			else 
				wp.setText(url);
			
			wp.setDate(date);
			wp.setAuthor(author);
			wp.setStringValue(URLWaypointUtil.URL_ATTR,url);
		}
	}

	public void selectionChanged(IAction action, ISelection selection)
	{


	}

}
