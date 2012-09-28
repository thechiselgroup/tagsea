/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.parsed.java;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.IParsedWaypointImageConstants;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointPresentation;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * UI presentation for java waypoints.
 * @author Del Myers
 *
 */
public class JavaWaypointPresentation implements IParsedWaypointPresentation {
	private JavaElementLabelProvider provider;
	/**
	 * 
	 */
	public JavaWaypointPresentation() {
		// TODO Auto-generated constructor stub
	}


	public Image getDomainImage(IWaypoint waypoint, String domainObjectString) {
		IJavaElement element = getJavaElement(domainObjectString);
		return getImage(element);
	}

	/**
	 * Uses the JavaUI shared images so that we don't have to worry about disposing.
	 * @param element
	 * @return
	 */
	private Image getImage(IJavaElement element) {
		if (element == null) return null;
		return getJavaLabelProvider().getImage(element);
	}
	
	private JavaElementLabelProvider getJavaLabelProvider() {
		if (provider == null) {
			provider = new JavaElementLabelProvider();
			Display.getCurrent().addListener(SWT.Dispose, new Listener(){
				public void handleEvent(Event event) {
					provider.dispose();
				}
			});
		}
		return provider;
	}

	public String getDomainLabel(IWaypoint waypoint, String domainObjectString) {
		IJavaElement element = getJavaElement(domainObjectString);
		if (element != null) {
			return getJavaLabelProvider().getText(element);
		}
		return "";
	}
	
	private IJavaElement getJavaElement(IWaypoint waypoint) {
		if (!waypoint.exists()) return null;
		String string = waypoint.getStringValue(IParsedWaypointAttributes.ATTR_DOMAIN, "");
		return getJavaElement(string);
	}
	
	private IJavaElement getJavaElement(String handleIdentifier) {
		if (handleIdentifier != null && !"".equals(handleIdentifier)) {
			IJavaElement element = JavaCore.create(handleIdentifier);
			return element;
		}
		return null;
	}

	public String getDomainObjectName(IWaypoint waypoint) {
		return "Java Element";
	}

	public Image getImage(IWaypoint waypoint) {
		
		IFile waypointFile = ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getFileForWaypoint(waypoint);
		if (waypointFile == null) return null;
		return getImage();
	}
	

	public String getLabel(IWaypoint waypoint) {
		IJavaElement element = getJavaElement(waypoint);
		String postFix = "";
		if (element != null) {
			postFix = " (" + getJavaLabelProvider().getText(element) + ")";
		}
		return waypoint.getText() + postFix;
	}


	public String getLocationString(IWaypoint waypoint) {
		//just use the default location string;
		return null;
	}


	public Image getImage() {
		ImageRegistry registry = ParsedJavaWaypointsPlugin.getDefault().getImageRegistry();
		Image image = registry.get("javaImage");
		if (image == null) {
			//construct the image from various adornments.
			Image base = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CUNIT);
			ImageDescriptor overlay = TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_WAYPOINT_OVERLAY);
			ImageDescriptor overlay2 = ParsedWaypointPlugin.getDefault().getImageRegistry().getDescriptor(IParsedWaypointImageConstants.PARSE_OVERLAY);
			DecorationOverlayIcon icon = new DecorationOverlayIcon(base, new ImageDescriptor[]{overlay,  null, null, overlay2, null});
			registry.put("javaImage", icon);
			image = registry.get("javaImage");
		}
		return image;
	}
	
	

}
