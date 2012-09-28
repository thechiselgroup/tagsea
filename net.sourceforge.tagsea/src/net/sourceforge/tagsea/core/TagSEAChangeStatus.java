/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core;

import net.sourceforge.tagsea.TagSEAPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * A class that gives the status of a change requested in the tagsea model.
 * @author Del Myers
 */

public class TagSEAChangeStatus {
	
	
	/**
	 * Default code for a waypoint attribute can't be changed because the attribute has not been defined.
	 */
	public static final int NO_SUCH_ATTRIBUTE = 1;

	/**
	 * Default code for when the type for the given attribute in the waypoint doesn't match the value assigned.
	 */
	public static final int BAD_ATTRIBUTE_TYPE = 2;
	
	/**
	 * Default code for when a tag could not be removed from a waypoint because it doesn't exist on that waypoint.
	 */
	public static final int TAG_DOES_NOT_EXITS = 3;

	/**
	 * Default success status.
	 */
	public static final TagSEAChangeStatus SUCCESS_STATUS = new TagSEAChangeStatus(TagSEAPlugin.PLUGIN_ID, true, 0, null);

	/**
	 * Default code for when an illegal attempt to to change the default tag has occurred.
	 */
	public static final int DEFAULT_TAG_CHANGE_ERROR = 4;
	
	/**
	 * The id of the plugin that allowed or dissallowed the change.
	 */
	public final String pluginID;
	
	/**
	 * Whether or not the change could be performed in the platform.
	 */
	public final boolean changePerformed;
	
	/**
	 * The plugin specific code for the reason that the change could not be performed.
	 * In most cases, if changePerformed is true, this should be ignored. Plugins should
	 * publish thier reason codes in some easily-accessable area.
	 */
	public final int code;
	
	/**
	 * The reason that the change could not be performed. May be null.
	 */
	public final String reason;
	

	
	
	/**
	 * Creates a new instance.
	 * @param waypoint The waypoint created, deleted, or changed.
	 * @param pluginID The id of the plugin that allowed or dissallowed the change.
	 * @param performChange Whether or not the change can be performed in the platform.
	 * @param code The plugin specific code for the reason that the change could not be performed.
	 * @param reason The reason that the change could not be performed. May be null.
	 */
	public TagSEAChangeStatus(String pluginID, boolean performChange, int code, String reason) {
		this.pluginID = pluginID;
		this.changePerformed = performChange;
		this.code = code;
		this.reason = reason;
	}
	
	/**
	 * Converts the change status to a standard Core IStatus for logging. When changes can't
	 * be performed, the severity is set to IStatus.WARNING, not IStatus.ERROR.
	 * @return an IStatus object for logging.
	 */
	public IStatus getStatus() {
		String localReason = (reason != null) ? reason : "";
		return new Status(
			(changePerformed) ? IStatus.OK : IStatus.WARNING,
			pluginID,
			code,
			localReason,
			null
		);
	}
	

}
