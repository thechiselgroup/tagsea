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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Contains utility methods for working with zip files
 * 
 * @author Chris Callendar
 * @author Wesley Coelho
 * @author Shawn Minto (Wrote methods that were moved here)
 */
public class ZipFileUtil {

	/**
	 * All files are added with no prefix.  Can't contain duplicate filenames.
	 * @param zipFile Destination zipped file
	 * @param files List of files or directories to add to the zip file
	 */
	public static void createZipFile(File zipFile, List<File> files) throws FileNotFoundException, IOException {
		Map<File, String> map = new HashMap<File, String>(files.size());
		for (File file : files) {
			map.put(file, "");
		}
		createZipFile(zipFile, map);
	}
	
	/**
	 * Adds the files to a zip file with each file having the given prefix.
	 * @param zipFile Destination zipped file
	 * @param filesAndPrefixes Map of files or directories and the prefix for the zip entry
	 */
	public static void createZipFile(File zipFile, Map<File, String> filesAndPrefixes) throws FileNotFoundException, IOException {
        if (zipFile.exists()){
        	zipFile.delete();
        }
        
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
        
        for (File file: filesAndPrefixes.keySet()){
       		String prefix = filesAndPrefixes.get(file);
       		addZipEntry(zipOut, file, prefix);
        }
    
        // Complete the ZIP file
        zipOut.close();
	}	
	
	/**
	 * @author Shawn Minto
	 */
	private static void addZipEntry(ZipOutputStream zipOut, File file, String prefix) throws FileNotFoundException, IOException {

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];
        
        // Compress the files
        FileInputStream in = new FileInputStream(file);

        // Add ZIP entry to output stream.
        addZipEntryRecursive(zipOut, file, prefix);

        // Transfer bytes from the file to the ZIP file
        int len;
        while ((len = in.read(buf)) > 0) {
        	zipOut.write(buf, 0, len);
        }

        // Complete the entry
        zipOut.closeEntry();
        in.close();
	}
	
	/**
	 * If the file is a file, it is added to the output stream with the given prefix.
	 * If the file is a directory, it calls this method.
	 * @param file	 the file or directory to add to the output stream
	 * @param prefix the prefix - is either blank or ends with a forwardslash e.g. "dir/"
	 * @throws IOException
	 */
	private static void addZipEntryRecursive(ZipOutputStream zipOut, File file, String prefix) throws IOException {
        if (file.isDirectory()) {
        	String[] files = file.list();
        	for (String filename : files) {
        		addZipEntryRecursive(zipOut, new File(file, filename), prefix + file.getName() + "/");
        	}
        } else {
        	zipOut.putNextEntry(new ZipEntry(prefix + file.getName()));
        }		
	}
	
}
