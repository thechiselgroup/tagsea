/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.core;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * Class containing waypoint data.  A waypoint consists of a marker, a java element
 * (e.g. a method or class) and metadata (author, comment, date, etc).
 * A waypoint has a reference to a {@link ca.uvic.cs.tagsea.core.Tag} and also has a
 * keyword.  The keyword is a representation of the tag hierarchy like root(tag1(child)).
 * 
 * @author many
 */
public class Waypoint {
	
	private WaypointMetaData waypointMetaData;
	private String keyword;	
	private IJavaElement javaElement;
	private IMarker marker;
	private int fLineNumber;
	private Tag tag = null;

	public Waypoint(IMarker marker, Tag tag, IJavaElement javaElement, WaypointMetaData waypointMetaData) {
		init(marker, tag, javaElement, waypointMetaData);
	}

	/**
	 * Initializes the waypoint with a null marker and java element.  The keyword is calculated from
	 * the tag hierarchy.  The metadata fields are all blank.
	 * TESTING ONLY. 
	 * @tag WaypointConstructor(testing) : for testing only 
	 */
	public Waypoint (Tag tag) {
		WaypointMetaData waypointMetaData = new WaypointMetaData();
		waypointMetaData.setAuthor("");
		waypointMetaData.setComment("");
		waypointMetaData.setDate("");
		init(null, tag, null, waypointMetaData);
	}

	private void init(IMarker marker, Tag tag, IJavaElement javaElement, WaypointMetaData waypointMetaData) {
		this.marker = marker;
		this.tag = tag;
		this.waypointMetaData = waypointMetaData;
		setJavaElement(javaElement);

		// generate the keyword from the tag hierarchy
		setKeywordFromTag();
		
		if (marker != null) {
			try {
				Object obj = marker.getAttribute(IMarker.LINE_NUMBER);
				if (obj instanceof Integer) {
					fLineNumber = (Integer)obj;
				}
			} catch (CoreException e) {
				TagSEAPlugin.log("Couldn't find the line number for " + getKeyword(), e);
				//e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets the tag associated with this waypoint
	 * @return The tag
	 */
	public Tag getTag() {
		return this.tag;
	}

	/**
	 * Sets the keyword based on the tag hierarchy.
	 */
	public void setKeywordFromTag() {
		this.keyword = generateKeyword(this);
	}
	
	public String getKeyword() {
		return keyword;
	}
		
	private void setJavaElement(IJavaElement javaElement) {
		this.javaElement = javaElement;
	}
	
	/**
	 * @return IJavaElement or null
	 */
	public IJavaElement getJavaElement() {
		return javaElement;
	}

	/**
	 * Gets the java element name.  If the element is null then an empty string is returned.
	 * @return String the java element name (won't be null)
	 */
	public String getJavaElementName() {
		return (javaElement == null ? "" : javaElement.getElementName());
	}
	
	public String getAuthor() {
		return waypointMetaData.getAuthor();
	}

	public void setAuthor(String author) {
		waypointMetaData.setAuthor(author);
	}

	public String getDate() {
		return waypointMetaData.getDate();
	}

	public void setDate(String date) {
		waypointMetaData.setDate(date);
	}

	public void setComment(String comment) {
		waypointMetaData.setComment(comment);
	}

	public String getComment() {
		return waypointMetaData.getComment();
	}

	public WaypointMetaData getMetadata() {
		return waypointMetaData;
	}

	/**
	 * Gets the line number from the marker. Returns 0 if a parsing error occurs.
	 * @tag Refactor(Marker(LineNumber)) : needs to be done! 
	 * @return int the line number
	 */
	public int getLineNumber() 
	{
//		int line = 0;
//		if (marker != null) {
//			try {
//				Object obj = marker.getAttribute(IMarker.LINE_NUMBER);
//				if (obj instanceof Integer) {
//					line = (Integer)obj;
//				}
//			} catch (CoreException e) {
//				Activator.log("Couldn't find the line number for " + getKeyword(), e);
//				//e.printStackTrace();
//			}
//		}
		return fLineNumber;
	}

	public void setMarker(IMarker marker) {
		this.marker = marker;
	}

	public IMarker getMarker() {
		return marker;
	}

	public boolean equals(Object o) 
	{
		if (o instanceof Waypoint) 
		{
			Waypoint w = (Waypoint) o;
			
			if (marker == null) {
				return keyword.equals(w.getKeyword());
			} else {
				return (keyword.equals(w.getKeyword()) && marker.equals(w.getMarker()));
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Waypoint " + getKeyword();
	}

	/**
	 * Generates the keyword for the given waypoint by iterating up the Tag hierarchy.
	 * @param waypoint
	 * @return String like root(tag1(child)), won't be null
	 * @see Tag#generateKeyword(Tag)
	 */
	public static String generateKeyword(Waypoint waypoint) {
		return Tag.generateKeyword(waypoint.getTag());
	}

	/**
	 * Check if a waypoint is stale
	 * @return
	 */
	public boolean isStale()
	{
		if(marker != null)
		{
			// big up yo-self!
			return !marker.exists();
		}
		
		return true;
	}
	
}