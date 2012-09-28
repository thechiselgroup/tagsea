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
package net.sourceforge.tagsea.parsed.core.internal;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.BaseWaypointUI;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.IParsedWaypointImageConstants;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointPresentation;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;
import net.sourceforge.tagsea.parsed.parser.IWaypointRefactoring;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;

/**
 * The waypoint ui for parsed waypoints.
 * @author Del Myers
 *
 */
public class ParsedWaypointUI extends BaseWaypointUI {
	
	private final String[] VISIBLE_ATTRIBUTES = {
		IParsedWaypointAttributes.ATTR_MESSAGE,
		IParsedWaypointAttributes.ATTR_AUTHOR,
		IParsedWaypointAttributes.ATTR_DATE,
		IParsedWaypointAttributes.ATTR_RESOURCE,
		IParsedWaypointAttributes.ATTR_DOMAIN,
		IParsedWaypointAttributes.ATTR_CHAR_START,
		IParsedWaypointAttributes.ATTR_CHAR_END
	};
	

	public boolean canUIChange(IWaypoint waypoint, String attribute) {
		IParsedWaypointDefinition d = getDefinition(waypoint);
		if (d != null) {
			if (IParsedWaypointAttributes.ATTR_AUTHOR.equals(attribute)) {
				return d.getRefactoringMethod().canSetAuthor(waypoint);
			} else if (IParsedWaypointAttributes.ATTR_DATE.equals(attribute)) {
				return d.getRefactoringMethod().canSetDate(waypoint);
			} if (IParsedWaypointAttributes.ATTR_MESSAGE.equals(attribute)) {
				return d.getRefactoringMethod().canSetMessage(waypoint);
			}
		}
		return false;
	}

	/**
	 * @param waypoint
	 * @return
	 */
	private IParsedWaypointDefinition getDefinition(IWaypoint waypoint) {
		String kind = waypoint.getStringValue(IParsedWaypointAttributes.ATTR_KIND, null);
		if (kind != null) {
			IParsedWaypointDefinition def = ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getDefinition(kind);
			if (def != null) {
				return def;
			}
		}
		return null;
	}

	public boolean canUIDelete(IWaypoint waypoint) {
		IParsedWaypointDefinition d = getDefinition(waypoint);
		if (d == null) {
			return super.canUIDelete(waypoint);
		}
		IWaypointRefactoring r = d.getRefactoringMethod();
		return r.canDelete(waypoint);
	}

	public boolean canUIMove(IWaypoint waypoint) {
		IParsedWaypointDefinition d = getDefinition(waypoint);
		if (d == null) {
			return super.canUIMove(waypoint);
		}
		IWaypointRefactoring r = d.getRefactoringMethod();
		return (r.canReplaceTags(waypoint));
	}
	
	@Override
	public boolean canUIAddTag(IWaypoint waypoint) {
		IParsedWaypointDefinition d = getDefinition(waypoint);
		if (d == null) {
			return super.canUIMove(waypoint);
		}
		IWaypointRefactoring r = d.getRefactoringMethod();
		return r.canAddTags(waypoint);
	}
	
	@Override
	public boolean canUIDeleteTag(IWaypoint waypoint) {
		IParsedWaypointDefinition d = getDefinition(waypoint);
		if (d == null) {
			return super.canUIMove(waypoint);
		}
		IWaypointRefactoring r = d.getRefactoringMethod();
		return r.canRemoveTags(waypoint);
	}

	public String getAttributeLabel(IWaypoint waypoint, String attribute) {
		IParsedWaypointPresentation p = getPresentation((waypoint));
		if (p == null) {
			return super.getAttributeLabel(waypoint, attribute);
		}
		if (IParsedWaypointAttributes.ATTR_DOMAIN.equals(attribute)) {
			return p.getDomainObjectName(waypoint);
		}
		return super.getAttributeLabel(waypoint, attribute);
	}

	public String getAttributeLabel(String attribute) {
		return super.getAttributeLabel(attribute);
	}

	public Image getImage(IWaypoint waypoint) {
		IParsedWaypointPresentation p = getPresentation((waypoint));
		if (p == null) {
			return ParsedWaypointPlugin.getDefault().getImageRegistry().get(IParsedWaypointImageConstants.PARSED_WAYPOINT);
		}
		return p.getImage(waypoint);
	}

	public String getLabel(IWaypoint waypoint) {
		IParsedWaypointPresentation p = getPresentation((waypoint));
		if (p == null) {
			return super.getLabel(waypoint);
		}
		return p.getLabel(waypoint);
	}


	public String getType() {
		return ParsedWaypointPlugin.WAYPOINT_TYPE;
	}

	public Image getValueImage(IWaypoint waypoint, String attribute) {
		IParsedWaypointPresentation p = getPresentation((waypoint));
		if (p == null) {
			return super.getValueImage(waypoint, attribute);
		}
		if (IParsedWaypointAttributes.ATTR_DOMAIN.equals(attribute)) {
			String domainObjectString = waypoint.getStringValue(IParsedWaypointAttributes.ATTR_DOMAIN, null);
			return p.getDomainImage(waypoint, domainObjectString);
		}
		return super.getValueImage(waypoint, attribute);
	}

	public String getValueLabel(IWaypoint waypoint, String attribute, Object value) {
		IParsedWaypointPresentation p = getPresentation((waypoint));
		if (p == null) {
			return super.getValueLabel(waypoint, attribute, value);
		}
		if (IParsedWaypointAttributes.ATTR_DOMAIN.equals(attribute)) {
			String domainObjectString = waypoint.getStringValue(IParsedWaypointAttributes.ATTR_DOMAIN, null);
			return p.getDomainLabel(waypoint, domainObjectString);
		}
		return super.getValueLabel(waypoint, attribute, value);
	}
	
	

	public String[] getVisibleAttributes() {
		return VISIBLE_ATTRIBUTES;
	}
	
	private IParsedWaypointPresentation getPresentation(IWaypoint waypoint) {
		String kind = waypoint.getStringValue(IParsedWaypointAttributes.ATTR_KIND, null);
		if (kind != null) {
			IParsedWaypointDefinition def = ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getDefinition(kind);
			if (def != null) {
				return def.getPresentation();
			}
		}
		return null;
	}
	
	
	public String getLocationString(IWaypoint waypoint) {
		IParsedWaypointPresentation presentation = getPresentation(waypoint);
		String location = null;
		
		if (presentation != null) {
			location = presentation.getLocationString(waypoint);
		}
		if (location == null) {
			String domainObjectString = waypoint.getStringValue(IParsedWaypointAttributes.ATTR_DOMAIN, "");
			String domain = domainObjectString;
			if (presentation != null) {
				domain = presentation.getDomainLabel(waypoint, domainObjectString);
				if (domain == null) {
					domain = domainObjectString;
				}
			}
			String resourceString = waypoint.getStringValue(IParsedWaypointAttributes.ATTR_RESOURCE, "");
			IResource resource = ParsedWaypointUtils.getResource(waypoint);
			if (resource != null && resource.exists()) {
				resourceString = resource.getName();
			}
			int lineNumber = waypoint.getIntValue(IParsedWaypointAttributes.ATTR_LINE, -1);
			
			int charStart = ParsedWaypointUtils.getCharStart(waypoint);
			int charEnd = ParsedWaypointUtils.getCharEnd(waypoint);
			location = resourceString + " " + domain + ((lineNumber != -1) ? " line " + lineNumber : "[" + charStart +"-" + charEnd +"]");
		}
		return location;
	}

}
