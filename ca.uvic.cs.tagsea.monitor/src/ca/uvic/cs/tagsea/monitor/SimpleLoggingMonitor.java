package ca.uvic.cs.tagsea.monitor;

import java.io.IOException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

import ca.uvic.cs.tagsea.core.Route;
import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.core.Waypoint;
import ca.uvic.cs.tagsea.monitor.jobs.UploadJob;
import ca.uvic.cs.tagsea.monitoring.ITagSEAMonitor;
import ca.uvic.cs.tagsea.monitoring.TagSEAActionEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEAJobEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEANavigationEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEAPartEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEARefactorEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEARoutingEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEATagEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEAWaypointEvent;

public class SimpleLoggingMonitor implements ITagSEAMonitor {
	private static final String STARTSTRING = "TagSEA Event: ";
	
	public void activate() {
		new UploadJob("Uploading TagSEA logs...").schedule();
	}
	
	
	public String getAgreementString() {
		return TagSEAMonitorPlugin.getDefault().getResourceString("monitor.agreement");
	}
	
	public void jobEventOccurred(TagSEAJobEvent evt) {
		String s = "Job: ";
		switch (evt.getType()) {
			case StartLoadingTags: s += "Start loading tags."; break;
			case EndLoadingTags: s += "End loading tags."; break;
			case StartUpdatingTags: s+= "Start updating tags."; break;
			case EndUpdatingTags: s+= "End updating tags."; break;
			case StartLoadingRoutes: s+= "Start loading routes"; break;
			case EndLoadingRoutes: s += "End loading routes"; break;
		}
		try {
			SimpleDayLog mon = TagSEAMonitorPlugin.getDefault().getMonitorLog();
			mon.logLineWithTime(STARTSTRING + s);
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
		}
	}
 
	public void navigationOccurred(TagSEANavigationEvent evt) {
		String s = STARTSTRING + "Navigating: " + 
			"to " + getWaypointString(evt.getTo());
		try {
			TagSEAMonitorPlugin.getDefault().getMonitorLog().logLineWithTime(s);
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
		}
	}

	public void refactorOccurred(TagSEARefactorEvent evt) {
		String rString = "Refactor: ";
		String tagString = "<Tag " + evt.getTag().getId() + ">";
		Object oldValue = evt.getOldValue();
		String oldString = "";
		if (oldValue instanceof Tag) {
			oldString = getTagString((Tag)oldValue);
		} else if (oldValue instanceof String) {
			oldString = oldValue.toString();
		}
		switch (evt.getRefactor()) {
			case Delete: rString += "Delete " + tagString + " from " + oldString; break;
			case Move: rString += "Move: " + tagString + " from " + oldString; break;
			case Rename: rString += "Rename: " + tagString + " from " + oldString; break;
		}
		String s = STARTSTRING + rString;
		try {
			TagSEAMonitorPlugin.getDefault().getMonitorLog().logLineWithTime(s);
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
		}
	}

	public void routingOccurred(TagSEARoutingEvent evt) {
		Route route = evt.getRoute();
		Waypoint waypoint = evt.getWaypoint();
		String rString = getRouteString(route);
		String wString = "null";
		if (waypoint != null) {
			wString = getWaypointString(waypoint);
		}
		String s = "Routing: ";
		switch (evt.getRouting()) {
			case Delete: s += "Delete: " + rString; break;
			case New: s += "Create: " + rString; break;
			case NewWaypoint: s += "Add: " + rString + ": " + wString; break;
			case DeleteWaypoint: s += "Remove: " + rString + ": " + wString; break;
			case MoveWaypoint: s += "Move: " + rString + ": " + wString; break;
			case Rename: s += "Rename: " + rString + ": old name: " + evt.getOldName();
		}
		try {
			TagSEAMonitorPlugin.getDefault().getMonitorLog().logLineWithTime(STARTSTRING + s);
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
		}

	}

	public void selectionOccurred(IWorkbenchPart part, ISelection selection) {
		String sString = "null";
		if (selection instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection)selection).getFirstElement();
			if (o instanceof Tag) {
				sString = getTagString((Tag)o);
			} else if (o instanceof Waypoint) {
				sString = getWaypointString((Waypoint)o);
			} else if (o instanceof Route) {
				sString = getRouteString((Route)o);
			}
		}
		try {
			TagSEAMonitorPlugin.getDefault().getMonitorLog().logLineWithTime(STARTSTRING + "Selected: " + part.getTitle() + ": " + sString);
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
		}
	}

	public void tagOccurred(TagSEATagEvent evt) {
		String tString = getTagString(evt.getTag());
		String s = "Tag: ";
		switch (evt.getTagging()) {
			case New: s+= "Created: "; break;
			case Removed: s += "Deleted: "; break;
		}
		try {
			TagSEAMonitorPlugin.getDefault().getMonitorLog().logLineWithTime(STARTSTRING + s + tString);
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
		}
	}

	public void waypointOccurred(TagSEAWaypointEvent evt) {
		String wString = getWaypointString(evt.getWaypoint());
		String s = "Waypoint: ";
		switch (evt.getWaypointing()) {
			case Edited: s += "Edited: "; break;
			case New: s+= "Created: "; break;
			case Removed: s+= "Deleted: "; break;
		}
		try {
			TagSEAMonitorPlugin.getDefault().getMonitorLog().logLineWithTime(STARTSTRING + s + wString);
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
		}
	}
	
	private String getTagString(Tag tag) {
		return "<Tag='" + tag.getId() + "'>";
	}
	
	
	private String getWaypointString(Waypoint waypoint) {
		if (waypoint == null) {
			return "<Waypoint null>";
		}
		IMarker marker = waypoint.getMarker();
		char syntax = '(';
		try {
			String text = (String)marker.getAttribute(IMarker.MESSAGE);
			if (text != null) {
				syntax = findSyntax(text);
			}
		} catch (CoreException e) {
		}
		String string = "<Waypoint='" + waypoint.getKeyword() + "' line='" + waypoint.getLineNumber() +
			"' javeElement=' " + getJavaElementName(waypoint.getJavaElement()) + "'" +
			" syntax='"+syntax+"'>";
		return string;
	}
	
	/**
	 * @param text
	 * @return
	 */
	private char findSyntax(String text) {
		//strip off the @tag
		int index = text.indexOf("@tag");
		if (index < 0) return '(';
		text = text.substring(index + "@tag".length() - 1).trim();
		if (text.indexOf('.') > 0) return '.';
		if (text.indexOf('(') > 0) return '(';
		return '(';
	}


	private String getRouteString(Route route) {
		return "<Route " + route.getName() + ">";
	}
	
	public String getName() {
		return TagSEAMonitorPlugin.getDefault().getResourceString("monitor.name");
	}

	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitor#partEventOccurred(ca.uvic.cs.tagsea.monitoring.TagSEAPartEvent)
	 */
	public void partEventOccurred(TagSEAPartEvent evt) {
		String s = "Part: ";
		String part = evt.getView().getClass().getSimpleName();
		switch (evt.getType()) {
			case ViewActivated: s += "Activated: "; break;
			case ViewClosed: s+= "Closed: "; break;
			case ViewDeactivated: s += "Deactivated: "; break;
			case ViewOpened: s+= "Opened: "; break;
		}
		try {
			TagSEAMonitorPlugin.getDefault().getMonitorLog().logLineWithTime(STARTSTRING + s + part);
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
		}
	}

	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitor#actionEventOccurred(ca.uvic.cs.tagsea.monitoring.TagSEAActionEvent)
	 */
	public void actionEventOccurred(TagSEAActionEvent evt) {
		String s = "Action: ";
		switch (evt.getType()) {
			case AutoComplete: s += "Auto-complete: "; break;
			case Filter: s += "Filter: "; break;
			case Refresh: s += "Refresh: "; break;
		}
		s += evt.getData();
		try {
			TagSEAMonitorPlugin.getDefault().getMonitorLog().logLineWithTime(s);
		} catch (IOException e) {
			TagSEAMonitorPlugin.getDefault().log(e);
		}
		
	}
	
	private String getJavaElementName(IJavaElement element) {
		switch (element.getElementType()) {
			case IJavaElement.TYPE:
				return "Java Type";
			case IJavaElement.METHOD:
				return "Java Method";
			case IJavaElement.FIELD:
				return "Java Field";
			case IJavaElement.LOCAL_VARIABLE:
				return "Java Local Variable";
			case IJavaElement.PACKAGE_DECLARATION:
				return "Java Package Declaration";
			case IJavaElement.IMPORT_DECLARATION:
				return "Java Import Declaration";
			case IJavaElement.INITIALIZER:
				return "Java Initializer";
			case IJavaElement.TYPE_PARAMETER:
				return "Java Type Parameter";
		}
		return "Java Element";
	}

}
