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
package ca.uvic.cs.tagsea.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Waypoint;

/**
 * Save the waypoints to an xml file.  Uses IMemento to facilitate this.
 * For each waypoint, the keyword, comment, author, and date are saved.
 * The xml file is zipped, and will eventually be uploaded to a server.
 * 
 * @author Chris Callendar
 */
public class TagsXMLUtil {
	
	public final static String WAYPOINTS = "Waypoints";
	public final static String WAYPOINT = "Waypoint";
	public final static String XML_FILE = "/Waypoints/waypoints.xml";
	public final static String ZIP_FILE = "waypoints.zip";
	
	public final static String NAME = "name";
	public final static String COMMENT = "comment";
	public final static String AUTHOR = "author";
	public final static String DATE = "date";
	
	/**
	 * Saves the waypoints to a file.
	 * @param waypoints the waypoints to save
	 */
	public static void saveWaypoints(Waypoint[] waypoints) {
		XMLMemento memento = XMLMemento.createWriteRoot(WAYPOINTS);
		saveRouteToMemento(memento, waypoints);
		saveToFile(memento, true);			
		
		// TODO upload zip file to server
	}

	/**
	 * Save the routes to an XML file (This is the first step,
	 * where we save the routes to a memento)
	 * @param memento
	 */
	private static void saveRouteToMemento(IMemento memento, Waypoint[] wps) {
		if (wps.length > 0) {
			//register all the waypoints
			for (int i= 0; i < wps.length; i++) {
				IMemento wpMem = memento.createChild(WAYPOINT);				
				Waypoint wp = wps[i];
				wpMem.putString(NAME, wp.getKeyword());
				wpMem.putString(COMMENT, wp.getComment());
				wpMem.putString(AUTHOR, wp.getAuthor());
				wpMem.putString(DATE, wp.getDate());
			}
		}		
	}

	/**
	 * Save the waypoints to an XML file (This is the second step, where we save the memento to a file)
	 * Then compresses it to a zip file.
	 * @return success
	 */
	private static boolean saveToFile(XMLMemento memento, boolean zip) {
		try {
			IPath path = TagSEAPlugin.getDefault().getStateLocation().append(XML_FILE);
			File file = path.toFile();	
			if (file == null) 
				return false;
			
			file.getParentFile().mkdirs();
			FileOutputStream stream = new FileOutputStream(file, false);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "utf-8")); //$NON-NLS-1$
			memento.save(writer);
			writer.close();
			
			if (zip) {
				File zipFile = new File(file.getParentFile(), ZIP_FILE);
				if (zipFile.exists()) {
					zipFile.delete();
				}
				ArrayList<File> files = new ArrayList<File>();
				files.add(file);
				ZipFileUtil.createZipFile(zipFile, files);
			}
		} catch (Exception e) {
			TagSEAPlugin.log("Error: writing to waypoints file failed in TagsXMLUtil.saveToFile()", e);
			return false;
		}
		// Success !
		return true;
	}
	
	

}
