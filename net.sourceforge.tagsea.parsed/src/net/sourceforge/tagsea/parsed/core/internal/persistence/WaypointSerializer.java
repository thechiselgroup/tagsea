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
package net.sourceforge.tagsea.parsed.core.internal.persistence;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * A utility class for serializing waypoints using XML mementos to compressed files.
 * @author Del Myers
 *
 */
public class WaypointSerializer {
	/**
	 * @tag todo.comment : Comment this type.
	 * @author Del Myers
	 *
	 */
	private final class NonClosingStream extends BufferedOutputStream {
		/**
		 * @param out
		 */
		private NonClosingStream(OutputStream out) {
			super(out);
		}

		@Override
		public void close() throws IOException {
			flush();
		}
	}

	/**
	 * The file that will be used to serialize.
	 */
	private File file;
	/**
	 * A file that is used to copy old values to the new file.
	 */
	private File backupFile;
	
	private final String EXTENSION = ".xml";

	/**
	 * Creates a serializer that will write to the file at the given path. The given path will be used as the
	 * file to write the serialized waypoints to. The last segment of the path is assumed to be the filename
	 * of the file. For the given file name <i>filename</i>, this serializer must
	 * also be able to have access to write to a file called ~<i>filename</i>, for backup purposes.
	 * @param serializedFile
	 * @throws IOException if the given path exists, but points to a directory.
	 */
	public WaypointSerializer(IPath serializedFile) throws IOException {
		this.file = serializedFile.toFile();
		IPath backupPath = (IPath)serializedFile.clone();
		String backupFileName = "~" + backupPath.lastSegment();
		backupPath = backupPath.removeLastSegments(1).append(backupFileName);
		this.backupFile = backupPath.toFile();
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("Cannot write to a directory.");
			}
		}
		if (backupFile.exists()) {
			if (backupFile.isDirectory()) {
				throw new IOException("Cannot write to a directory.");
			}
		}
	}
	
	/**
	 * Returns the file that will be used to save the data.
	 * @return the file that will be used to save the data.
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * @return the file that will be used to backup the data during copying.
	 */
	public File getBackupFile() {
		return backupFile;
	}
	
	/**
	 * Shortcut for write(waypoints, true).
	 * @param waypoints the waypoints to write.
	 * @throws IOException 
	 */
	public void write(IWaypoint[] waypoints) throws IOException {
		write(waypoints, true);
	}
	
	/**
	 * Writes the given waypoints to the file for this serializer. If <code>backup</code> is true, then the
	 * waypoints for files that are not in the the given list of waypoints will still remain in the file. Otherwise,
	 * they will be removed. The granularity for backing up is the file level. If there is a single waypoint existent
	 * for a file in the list of passed waypoints, then all old waypoints in that file will be overwritten.
	 * @param waypoints the waypoints to save
	 * @param backup true if the old waypoints should remain in the new file.
	 * @throws IOException if an error occurs during the write.
	 */
	public void write(IWaypoint[] waypoints, boolean backup)  throws IOException {
		//map all of the waypoints by file name so that we can get consistent Zip entries.
		HashMap<String, List<IWaypoint>> fileWaypointMap = new HashMap<String, List<IWaypoint>>(); 
		for (IWaypoint wp : waypoints) {
			String filename = wp.getStringValue(IParsedWaypointAttributes.ATTR_RESOURCE, null);
			if (filename != null) {
				List<IWaypoint> fileWaypointList = fileWaypointMap.get(filename);
				if (fileWaypointList == null) {
					fileWaypointList = new ArrayList<IWaypoint>(10);
					fileWaypointMap.put(filename, fileWaypointList);
				}
				fileWaypointList.add(wp);
			}
		}
		FileOutputStream fileStream = new FileOutputStream(file);
		ZipOutputStream output = new ZipOutputStream(fileStream);
		boolean wrote = false;
		try {
			ZipFile backupZip = null;
			if (backup) {
				backupWaypointState();
				boolean doBackup = true;
				try {
					backupZip = new ZipFile(backupFile);
				} catch (ZipException e) {
					//the backup file couldn't be loaded for some reason. Don't run the 
					//backup
					doBackup = false;
				}
				if (doBackup) {
					try {
						Enumeration<? extends ZipEntry> entries = backupZip.entries();
						while (entries.hasMoreElements()) {
							ZipEntry entry = entries.nextElement();
							String fileName = entry.getName().substring(0, entry.getName().length()-EXTENSION.length());
							Path filePath = new Path(fileName);
							if (!filePath.isAbsolute()) {
								filePath.makeAbsolute();
								fileName = filePath.toPortableString();
							}
							List<IWaypoint> waypointsList = fileWaypointMap.get(fileName);
							output.putNextEntry(new ZipEntry(entry.getName()));
							wrote = true;
							if (waypointsList != null) {
								//save this state.
								saveWaypoints(waypointsList, fileName, output);
								fileWaypointMap.remove(fileName);
							} else {
								InputStream input = backupZip.getInputStream(entry);
								byte[] buffer = new byte[512];
								int read = -1;
								while ((read = input.read(buffer)) !=-1) {
									output.write(buffer, 0, read);
								}
								input.close();
							}
							output.closeEntry();
						}
					} catch (IOException e) {
						throw e;
					} finally {
						if (backupZip != null) {
							backupZip.close();
						}
					}
				}
			}
			for (String fileName : fileWaypointMap.keySet()) {
				List<IWaypoint> fileWaypointsList = fileWaypointMap.get(fileName);
				String entryName = new Path(fileName).makeRelative().toPortableString() + EXTENSION;
				output.putNextEntry(new ZipEntry(entryName));
				wrote = true;
				saveWaypoints(fileWaypointsList, fileName, output);
				output.closeEntry();
			}
			if (wrote) {
				output.finish();
			}
		} catch (IOException e) {
			//delete the files. They will be corrupted.
			file.delete();
			backupFile.delete();
			throw e;
		} finally {
			fileStream.close();
			if (!wrote) {
				file.delete();
			}
		}
	}
	
	/**
	 * Loads the memento that was used to save the file with the given name in this serializer, if it is available.
	 * Returns null otherwise.
	 * @param fileName the file to look for.
	 * @return the memento that was used to save the waypoint data for the given file name.
	 */
	public IMemento loadFileInfo(String fileName) throws IOException {
		String entryName = new Path(fileName).makeRelative().toPortableString() + EXTENSION;
		ZipFile zipFile = null;
		IMemento memento = null;
		try {
			zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().equals(entryName)) {
					InputStream input = zipFile.getInputStream(entry);
					InputStreamReader reader = new InputStreamReader(input);
					memento = XMLMemento.createReadRoot(reader);
					break;
				}
			}
		} catch (IOException e) {
			throw e;
		} catch (WorkbenchException e) {
			throw new IOException(e);
		} finally {
			if (zipFile != null) {
				zipFile.close();
			}
		}
		return memento;
	}
	
	/**
	 * Loads the file information for the given filename (if it exists), deletes it from the saved state,
	 * and returns the memento that was loaded.
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public IMemento removeFileInfo(String fileName) throws IOException {
		String entryName = new Path(fileName).makeRelative().toPortableString() + EXTENSION;
		backupWaypointState();
		ZipFile zipFile = null;
		IMemento memento = null;
		ZipOutputStream outputStream = null;
		FileOutputStream fileStream = new FileOutputStream(getFile());
		boolean wrote = false;
		try {
			zipFile = new ZipFile(getBackupFile());
		} catch (IOException e) {
			//there is nothing to load from the saved data. Just return null.
			return null;
		}
		try {
			
			outputStream = new ZipOutputStream(fileStream);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().equals(entryName)) {
					InputStream input = zipFile.getInputStream(entry);
					InputStreamReader reader = new InputStreamReader(input);
					memento = XMLMemento.createReadRoot(reader);
					break;
				} else {
					outputStream.putNextEntry(new ZipEntry(entry.getName()));
					wrote = true;
					byte[] buffer = new byte[512];
					int read = -1;
					InputStream inStream = zipFile.getInputStream(entry);
					while((read = inStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, read);
					}
					outputStream.closeEntry();
				}
			}
			if (wrote) {
				outputStream.finish();
			}
		} catch (IOException e) {
			throw e;
		} catch (WorkbenchException e) {
			throw new IOException(e);
		} finally {
			if (zipFile != null) {
				zipFile.close();
			}
			fileStream.close();
			if (!wrote) {
				file.delete();
			}
		}
		return memento;
	}
	/**
	 * Loads all of the mementos for all the files saved in the file used by this serializer.
	 * @return the mementos loaded.
	 * @throws IOException if there was a problem reading the file.
	 */
	public IMemento[] loadFileMementos() throws IOException {
		ZipFile zipFile = null;
		List<IMemento> result = new LinkedList<IMemento>();
		IMemento[] mementos = null;
		try {
			zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				InputStream input = zipFile.getInputStream(entry);
				InputStreamReader reader = new InputStreamReader(input);
				IMemento memento = XMLMemento.createReadRoot(reader);
				result.add(memento);
			}
		} catch (IOException e) {
			throw e;
		} catch (WorkbenchException e) {
			throw new IOException(e);
		} finally {
			if (zipFile != null) {
				zipFile.close();
			}
		}
		mementos = result.toArray(new IMemento[result.size()]);
		return mementos;
	}
	
	/**
	 * @param fileName
	 * @throws IOException 
	 */
	private void saveWaypoints(List<IWaypoint> waypoints, String fileName, OutputStream output) throws IOException {
		XMLMemento fileMemento = XMLMemento.createWriteRoot("file");
		fileMemento.putString("fileName", fileName);
		for (IWaypoint wp : waypoints) {
			if (wp.exists()) {
				IMemento waypointMemento = fileMemento.createChild("waypoint");
				populateMemento(waypointMemento, wp);
			}
		}
		//create an output stream that won't close the underlying output stream.
		OutputStream nonClosingStream = new NonClosingStream(output);
		OutputStreamWriter writer = new OutputStreamWriter(nonClosingStream);
		//can't just use an OutputStreamWriter because the 
		fileMemento.save(writer);	
	}
	
	private void populateMemento(IMemento waypointMemento, IWaypoint wp) {
		for (String attr : wp.getAttributes()) {
			Object value = wp.getValue(attr);
			if (value != null) {
				if (value instanceof Date) {
					waypointMemento.putString(attr, getDateString((Date)value));
				} else {
					waypointMemento.putString(attr, value.toString());
				}
			}
		}
		for (ITag tag : wp.getTags()) {
			IMemento tagMemento = waypointMemento.createChild("tag");
			tagMemento.putTextData(tag.getName());
		}
	}
	
	
	/**
	 * Copies the waypoint state file from the current state to the old state.
	 */
	private void backupWaypointState() throws IOException {
		if (file.exists()) {
			FileInputStream inStream = new FileInputStream(file);
			FileOutputStream outStream = new FileOutputStream(backupFile);
			byte[] buffer = new byte[512];
			int read = -1;
			while ((read = inStream.read(buffer, 0, buffer.length)) != -1) {
				outStream.write(buffer, 0, read);
			}
			inStream.close();
			outStream.close();
		}
	}
	
	/**
	 * Returns a string for dates suitable for serialization in XML.
	 * @param value
	 * @return
	 */
	public static String getDateString(Date value) {
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
		return format.format(value);
	}
	
	/**
	 * Returns a Date from a serialized XML string.
	 * @param value the serialized value.
	 * @return
	 */
	public static Date getDateFromString(String value) {
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
		try {
			return format.parse(value);
		} catch (ParseException e) {
			return new Date();
		}
	}

}
