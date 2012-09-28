package net.sourceforge.tagsea.breakpoint.waypoints;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.TagSEAOperation;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.model.IBreakpoint;

public class BreakpointListener implements IBreakpointsListener
{
	BreakpointWaypointDelegate fDelegate;
	
	public BreakpointListener(BreakpointWaypointDelegate delegate) 
	{
		fDelegate = delegate;
	}
	/**
	 * Notifies this listener that the given breakpoints have been added
	 * to the breakpoint manager.
	 *
	 * @param breakpoints the added breakpoints
	 */
	public void breakpointsAdded(final IBreakpoint[] breakpoints)
	{
		TagSEAPlugin.run(new TagSEAOperation("Adding Breakpoints..."){
			@Override
			public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
				for(IBreakpoint breakpoint : breakpoints)
					BreakpointUtil.createWaypointFromBreakpoint(breakpoint, true);
				return Status.OK_STATUS;
			}
			
		}, false);
		
	}
	/**
	 * Notifies this listener that the given breakpoints have been removed
	 * from the breakpoint manager.
	 * If a breakpoint has been removed because it has been deleted,
	 * the associated marker delta is also provided.
	 *
	 * @param breakpoints the removed breakpoints
	 * @param deltas the associated marker deltas. Entries may be
	 *  <code>null</code> when a breakpoint is removed from the breakpoint
	 *  manager without being deleted
	 *
	 * @see org.eclipse.core.resources.IMarkerDelta
	 */
	public void breakpointsRemoved(final IBreakpoint[] breakpoints, final IMarkerDelta[] deltas)
	{
		
		TagSEAPlugin.run(new TagSEAOperation("Removing Breakpoints...") {
			@Override
			public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
				for(IMarkerDelta delta : deltas)
					if(delta!=null)
						BreakpointUtil.removeWaypoint(delta.getId());
				for(IBreakpoint breakpoint : breakpoints)
					BreakpointUtil.removeWaypoint(breakpoint);	
				return Status.OK_STATUS;
			}
			
		}, false);
		
	}

	/**
	 * Notifies this listener that the given breakpoints have
	 * changed, as described by the corresponding deltas.
	 *
	 * @param breakpoints the changed breakpoints
	 * @param deltas the marker deltas that describe the changes
	 *  with the markers associated with the given breakpoints. Entries
	 *  may be <code>null</code> when a breakpoint change does not generate
	 *  a marker delta
	 *
	 * @see org.eclipse.core.resources.IMarkerDelta
	 */
	public void breakpointsChanged(IBreakpoint[] breakpoints, IMarkerDelta[] deltas)
	{
		fDelegate.updateWaypoints(breakpoints);	
	}
}
