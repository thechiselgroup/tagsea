package net.sourceforge.tagsea.breakpoint.waypoints;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.breakpoint.BreakpointWaypointPlugin;
import net.sourceforge.tagsea.core.ITagChangeEvent;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.WaypointDelta;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

public class BreakpointWaypointDelegate extends AbstractWaypointDelegate 
{
	private class SaveParticipant implements ISaveParticipant {
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context) {
		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException {

		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context) {
			//can't rollback saves

		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
		 */
		public void saving(ISaveContext context) throws CoreException 
		{
			//only care about full saves.
			if (context.getKind() != ISaveContext.FULL_SAVE) return;
			BreakpointUtil.saveAll();
		}
	}

	public BreakpointWaypointDelegate() 
	{

	}

	@Override
	protected void load() 
	{
//		//
//		TagSEAPlugin.run(new TagSEAOperation("Loading Breakpoint Waypoints..."){
//
//			@Override
//			public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
				IBreakpoint[] breakpoints = BreakpointUtil.getBreakpoints();
				//monitor.beginTask("Loading Breakpoint Waypoints", breakpoints.length);
				for(IBreakpoint breakpoint : breakpoints)
					BreakpointUtil.createWaypointFromBreakpoint(breakpoint, false);

				IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
				manager.addBreakpointListener(new BreakpointListener(BreakpointWaypointDelegate.this));
//				return Status.OK_STATUS;
//			}
//			
//		}, false);
		
		try 
		{
			ResourcesPlugin.getWorkspace().addSaveParticipant(BreakpointWaypointPlugin.getDefault(), new SaveParticipant());
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void navigate(IWaypoint waypoint) 
	{
		IBreakpoint breakpoint = BreakpointUtil.findBreakpoint(waypoint);
		
//		if (breakpoint == null) return;
//		
//		try 
//		{
//			String handleIdentifier = (String)breakpoint.getMarker().getAttribute("org.eclipse.jdt.debug.ui.JAVA_ELEMENT_HANDLE_ID");
//		
//			if(handleIdentifier != null && handleIdentifier.trim().length()>0)
//			{
//				IJavaElement element = JavaCore.create(handleIdentifier);
//				int line = waypoint.getIntValue(BreakpointUtil.LINE_ATTR, 0);
//					
//				try 
//				{
//					IEditorPart part = EditorUtility.openInEditor(element);
//
//					if(part instanceof ITextEditor)
//					{
//						ITextEditor editor = (ITextEditor)part;
//						IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
//						
//						if(doc !=null)
//						{
//							IRegion region  = doc.getLineInformation(line - 1);
//							editor.getSelectionProvider().setSelection(new TextSelection(region.getOffset(),region.getLength()));
//						}
//					}
//				} 
//				catch (PartInitException e) 
//				{
//					e.printStackTrace();
//				}
//				catch (BadLocationException e)
//				{
//					e.printStackTrace();
//				}
//				catch (CoreException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		} 
//		catch (CoreException e) 
//		{
//			e.printStackTrace();
//		}
		
		if(breakpoint!=null)
		{
			try 
			{
				IDE.openEditor(BreakpointWaypointPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), breakpoint.getMarker());
			} 
			catch (PartInitException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void tagsChanged(TagDelta delta) 
	{
		ITagChangeEvent[] events = delta.getEvents();

		for(ITagChangeEvent event : events)
		{
			IWaypoint[] waypoints = event.getNewWaypoints();

			for(IWaypoint waypoint:waypoints)
			{
				if(waypoint.getType().equals(BreakpointWaypointPlugin.WAYPOINT_ID))
					BreakpointUtil.save(waypoint);
			}

			waypoints = event.getOldWaypoints();

			for(IWaypoint waypoint:waypoints)
			{
				if(waypoint.getType().equals(BreakpointWaypointPlugin.WAYPOINT_ID))
					BreakpointUtil.save(waypoint);
			}
		}

	}


	@Override
	protected void waypointsChanged(WaypointDelta delta)
	{
		IWaypointChangeEvent[] events = delta.getChanges();

		for(IWaypointChangeEvent event : events)
		{
			if(event.getType() == IWaypointChangeEvent.CHANGE)
			{
				IWaypoint waypoint = event.getWaypoint();

				if(waypoint.getType().equals(BreakpointWaypointPlugin.WAYPOINT_ID))
				{
					BreakpointUtil.save(waypoint);
				}
			}
			
			if(event.getType() == IWaypointChangeEvent.DELETE)
			{
				IWaypoint waypoint = event.getWaypoint();

				if(waypoint.getType().equals(BreakpointWaypointPlugin.WAYPOINT_ID))
				{
					IBreakpoint breakpoint = BreakpointUtil.findBreakpoint(waypoint);
					if (breakpoint != null)	BreakpointUtil.delete(breakpoint);
				}
				
			}
		}
	}

	public void updateWaypoints(IBreakpoint[] breakpoints) 
	{
		BreakpointUtil.updateWaypoints(breakpoints);
	}
}
