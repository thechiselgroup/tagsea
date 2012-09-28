/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package net.sourceforge.tagsea.tours.waypoint.dropadapter;

import java.util.Vector;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.WaypointTransfer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourElementDropAdapter;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;

/**
 * drop adapter for TagSEA 6.0 waypoints - currently only supports delicious, url, java, and resources
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2007
 */

public class TagSeaWaypointDropAdapter  implements ITourElementDropAdapter {

	private static final String WAYPOINT_EXTENSION_POINT = "net.sourceforge.tagsea.waypoint";
	
	private String deliciousWaypointId;
	private String javaWaypointId;
	private String resourceWaypointId;

	private String urlWaypointId;
	
	public TagSeaWaypointDropAdapter()
	{
		// query all waypoint extensions 
		// - very simple approach to finding the waypoints - just string matching, doesn't require dependencies on plugins
		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(WAYPOINT_EXTENSION_POINT).getExtensions();
		if ( extensions!=null )
		{
			for ( IExtension ext : extensions )
			{
				String pluginId = ext.getContributor().getName();
				
				IConfigurationElement[] elements = ext.getConfigurationElements();
				for ( IConfigurationElement element : elements )
				{
					if ( element.getName().equals("waypoint") )
					{
						String type = element.getAttribute("type");
						String waypointId = pluginId + "." + type; 
						
						if ( type!=null )
						{
							if ( type.indexOf("delicious")>=0 )
								deliciousWaypointId = waypointId;
							else if ( type.indexOf("java")>=0 )
								javaWaypointId = waypointId;
							else if ( type.indexOf("resource")>=0 )
								resourceWaypointId = waypointId;
							else if ( type.indexOf("url")>=0 )
								urlWaypointId = waypointId;
						}
					}
				}
			}
		}
	}
	
	public ITourElement[] convertDropData(Object data) 
	{
		IWaypoint[] waypoints =  (IWaypoint[])data;
		Vector<ITourElement> elements = new Vector<ITourElement>();

		System.out.println(">> types : " + deliciousWaypointId + " " + javaWaypointId + " " + resourceWaypointId + " " + urlWaypointId);
		
		for(IWaypoint waypoint : waypoints)
		{
			System.out.println(">> type : " + waypoint.getType() );
			if(waypoint.getType().equals(deliciousWaypointId) || waypoint.getType().equals(urlWaypointId))
			{
				ResourceURLTourElement urlElement = new ResourceURLTourElement(waypoint.getStringValue("URL",null));
				urlElement.setNotes(waypoint.getText());
				elements.add(urlElement);
			}
			else if(waypoint.getType().equals(resourceWaypointId))
			{
				ResourceURLTourElement urlElement = new ResourceURLTourElement( ResourceWaypointUtils.getResource(waypoint) );
				urlElement.setNotes(waypoint.getText());
				elements.add(urlElement);
			}
			else if(waypoint.getType().equals(javaWaypointId))
			{
				ResourceURLTourElement urlElement = new ResourceURLTourElement(JavaWaypointUtils.getJavaElement(waypoint));
				urlElement.setNotes(waypoint.getText());
				elements.add(urlElement);
			}
			else
			{
				MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				box.setText("Unsupported drop operation");
				box.setMessage(waypoint.getClass().getName());
				box.open();
			}
		}
		return elements.toArray(new ITourElement[0]);
	}

	public Transfer getTransfer() 
	{
		return WaypointTransfer.getInstance();
	}
	
	/**
	 * Returns the file that the given waypoint references if it can be found.
	 * @param waypoint the waypoint.
	 * @return the file that the given waypoint references if it can be found.
	 */
	public static IFile getFile(IWaypoint waypoint) {
		String fileName;
		fileName = waypoint.getStringValue("javaElement", ""); //$NON-NLS-1$
		if (!"".equals(fileName)) { //$NON-NLS-1$
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
			return file;
		}
		return null;
	}
}
