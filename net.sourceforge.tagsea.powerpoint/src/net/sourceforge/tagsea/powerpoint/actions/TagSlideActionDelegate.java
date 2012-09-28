package net.sourceforge.tagsea.powerpoint.actions;

import java.io.IOException;
import java.util.Date;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.powerpoint.PowerpointWaypointPlugin;
import net.sourceforge.tagsea.powerpoint.waypoints.PowerpointWaypointDelegate;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class TagSlideActionDelegate implements IObjectActionDelegate
{
	private IWorkbenchPart fActivePart;
	private ISelection fSelection;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		fActivePart = targetPart;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		if(fSelection != null)
		{
			StructuredSelection selection = (StructuredSelection)fSelection;

			if(selection.toArray().length == 1)
			{
				Object o = selection.toArray()[0];

				if(o instanceof IFile)
				{
					IFile file = (IFile)o;

					if("ppt".equalsIgnoreCase(file.getFileExtension()))
					{
						try 
						{
							String filePath = file.getRawLocation().toOSString();
							SlideShow ppt = new SlideShow(new HSLFSlideShow(filePath));

							if(ppt.getSlides().length <= 0)
							{
								// Error
								return;
							}
							
							TagPowerpointDialog dialog = new TagPowerpointDialog(fActivePart.getSite().getShell());
							dialog.setSlideShow(ppt);

							int result = dialog.open();
							if (result != Dialog.OK) 
								return;

							String author = dialog.getAuthor();
							String description = dialog.getDescription();
							Date date = dialog.getDate();
							String[] tags = dialog.getTagNames();
							int[] indices = dialog.getSelectedSlideIndices();
							boolean isRangeSelection = dialog.isRangeSelection();
							boolean isShowSelection = dialog.isShowSelection();

							if(isShowSelection)
							{
								IWaypoint waypoint = createWypoint(tags,author,description,date);

								if(description==null || description.trim().length()==0)
									waypoint.setText(stripInsaneChar(ppt.getSlides()[0].getTitle()));
								
								try 
								{
									IMarker marker = createMarker(file);
									waypoint.setStringValue(PowerpointWaypointDelegate.MARKER_ID_ATTR, ""+marker.getId());
									PowerpointWaypointDelegate.flush(waypoint);

								} 
								catch (CoreException e) 
								{
									e.printStackTrace();
									waypoint.delete();
									return;
								}

							}
							else if(isRangeSelection)
							{
								int start = getStart(indices);
								int end = getEnd(indices);

								if(start > 0 && end > 0)
								{
									IWaypoint waypoint = createWypoint(tags,author,description,date);
									
									if(start!=end)
										waypoint.setStringValue(PowerpointWaypointDelegate.SLIDE_RANGE_ATTR, start + "-" + end);
									else
										waypoint.setIntValue(PowerpointWaypointDelegate.SLIDE_ATTR, start);
									
									if(description==null || description.trim().length()==0)
										waypoint.setText(stripInsaneChar(ppt.getSlides()[start - 1].getTitle()));
									
									try 
									{
										IMarker marker = createMarker(file);
										waypoint.setStringValue(PowerpointWaypointDelegate.MARKER_ID_ATTR, ""+marker.getId());
										PowerpointWaypointDelegate.flush(waypoint);

									} 
									catch (CoreException e) 
									{
										e.printStackTrace();
										waypoint.delete();
										return;
									}
								}
							}
							else
							{
								for(int i: indices)
								{
									IWaypoint waypoint = createWypoint(tags,author,description,date);
									waypoint.setIntValue(PowerpointWaypointDelegate.SLIDE_ATTR, i);

									if(description==null || description.trim().length()==0)
										waypoint.setText(stripInsaneChar(ppt.getSlides()[i - 1].getTitle()));
									
									try 
									{
										IMarker marker = createMarker(file);
										waypoint.setStringValue(PowerpointWaypointDelegate.MARKER_ID_ATTR, ""+marker.getId());
										PowerpointWaypointDelegate.flush(waypoint);

									} 
									catch (CoreException e) 
									{
										e.printStackTrace();
										waypoint.delete();
										return;
									}
								}
							}
						}
						catch (IOException e) 
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private String stripInsaneChar(String title)
	{
		char[] chars = title.toCharArray();
		
		for(int i =0 ;i<chars.length;i++)
			if(chars[i] == '') // Dont ask, im not quite sure either!
				chars[i] = ' ';
		
		return new String(chars);
	}

	private IMarker createMarker(IFile file) throws CoreException
	{
		// Create the marker
		IMarker marker = null;
		marker = file.createMarker(PowerpointWaypointPlugin.MARKER_ID);
		return marker;
	}

	private IWaypoint createWypoint(String[] tags, String author,String description, Date date) 
	{
		IWaypoint waypoint = TagSEAPlugin.getWaypointsModel().createWaypoint(PowerpointWaypointPlugin.WAYPOINT_ID, tags);

		if(author!=null && author.trim().length()>0)
			waypoint.setAuthor(author);

		if(description!=null && description.trim().length()>0)
			waypoint.setText(description);

		if(date!=null)
			waypoint.setDate(date);

		return waypoint;
	}

	private int getStart(int[] indices) 
	{
		int start = Integer.MAX_VALUE;

		for(int i = 0; i<indices.length; i++)
			start = Math.min(start, indices[i]);

		return start;		
	}

	private int getEnd(int[] indices) 
	{
		int end = 0;

		for(int i = 0; i<indices.length; i++)
			end = Math.max(end, indices[i]);

		return end;		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = selection;
	}
}
