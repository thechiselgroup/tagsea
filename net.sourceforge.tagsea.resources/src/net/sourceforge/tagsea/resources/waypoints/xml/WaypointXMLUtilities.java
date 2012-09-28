/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.resources.waypoints.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.TransformerConfigurationException;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointDescriptor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Utility class for reading and writing waypoints in XML.
 * @author Del Myers
 */
public class WaypointXMLUtilities {
	public static final String fileName = ".resourceWaypoints";
	public static final String dtd = "waypoint.dtd";
	
	public static final String DOC_ROOT = "waypoints";
	public static final String WAYPOINT_ELEMENT = "waypoint";
	public static final String TAG_ELEMENT = "tag";
	public static final String MESSAGE_ELEMENT = "message";
	public static final String RESOURCE_ATTR = "resource";
	public static final String LINE_ATTR = "lineNumber";
	public static final String AUTHOR_ATTR = "author";
	public static final String DATE_ATTR = "date";
	public static final String STAMP_ATTR = "stamp";
	public static final String REVISION_ATTR = "revisionStamp";
	
	public static final String CHAR_START = "charStart";
	public static final String CHAR_END = "charEnd";
	
	
	private static class XMLHandler implements ContentHandler, ErrorHandler {

		private List<ResourceWaypointDescriptor> waypointDescriptors;
		private ResourceWaypointDescriptor currentDescriptor;
		private String currentElement;
		private LinkedList<String> elementStack;
		private String currentString;
		
		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
		 */
		public void characters(char[] ch, int start, int length) throws SAXException {
			currentString += new String(ch, start, length);
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#endDocument()
		 */
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (WaypointXMLUtilities.MESSAGE_ELEMENT.equals(currentElement)) {
				if (currentDescriptor == null) throw new SAXException("No waypoint");
				currentDescriptor.setText(currentString);
			} else if (WaypointXMLUtilities.TAG_ELEMENT.equals(currentElement)){
				if (currentDescriptor == null) throw new SAXException("No waypoint");
				currentDescriptor.addTag(currentString);
			} else if (WaypointXMLUtilities.WAYPOINT_ELEMENT.equals(qName)) {	
				currentDescriptor = null;
			}
			if (elementStack.size() > 0) {
				currentElement = elementStack.removeLast();
			}
			currentString = "";
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
		 */
		public void endPrefixMapping(String prefix) throws SAXException {
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
		 */
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
		 */
		public void processingInstruction(String target, String data) throws SAXException {
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
		 */
		public void setDocumentLocator(Locator locator) {
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
		 */
		public void skippedEntity(String name) throws SAXException {
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startDocument()
		 */
		public void startDocument() throws SAXException {
			waypointDescriptors = new LinkedList<ResourceWaypointDescriptor>();
			elementStack = new LinkedList<String>();
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if (waypointDescriptors == null) {
				throw new SAXException("No document");
			}
			if (currentElement != null) {
				elementStack.addLast(currentElement);
			}
			currentElement = qName;
			if (WaypointXMLUtilities.WAYPOINT_ELEMENT.equals(currentElement)) {
				currentDescriptor = new ResourceWaypointDescriptor();
				waypointDescriptors.add(currentDescriptor);
				for (int i = 0; i < atts.getLength(); i++) {
					String name = atts.getQName(i);
					if (WaypointXMLUtilities.AUTHOR_ATTR.equals(name)) {
						currentDescriptor.setAuthor(atts.getValue(i));
					} else if (WaypointXMLUtilities.DATE_ATTR.equals(name)) {
						try {
							currentDescriptor.setDate(parseDate(atts.getValue(i)));
						} catch (ParseException e) {
							throw new SAXException(e);
						}
					} else if (WaypointXMLUtilities.RESOURCE_ATTR.equals(name)) {
						IPath p = new Path(atts.getValue(i));
						currentDescriptor.setResource(p.toPortableString());
					} 
					else if (WaypointXMLUtilities.LINE_ATTR.equals(name)) 
					{
						try {
							currentDescriptor.setLine(Integer.parseInt(atts.getValue(i)));
						} catch (NumberFormatException e) {
							throw new SAXException(e);
						}
					}
					else if (WaypointXMLUtilities.CHAR_START.equals(name)) 
					{
						try {
							currentDescriptor.setCharStart(Integer.parseInt(atts.getValue(i)));
						} catch (NumberFormatException e) {
							throw new SAXException(e);
						}
					}
					else if (WaypointXMLUtilities.CHAR_END.equals(name)) 
					{
						try {
							currentDescriptor.setCharEnd(Integer.parseInt(atts.getValue(i)));
						} catch (NumberFormatException e) {
							throw new SAXException(e);
						}
					}
					else if (WaypointXMLUtilities.STAMP_ATTR.equals(name)) {
						currentDescriptor.setStamp(atts.getValue(i));
					} else if (WaypointXMLUtilities.REVISION_ATTR.equals(name)) {
						currentDescriptor.setRevision(atts.getValue(i));
					}


				}
			}
			currentString = "";
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
		 */
		public void startPrefixMapping(String prefix, String uri) throws SAXException {
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
		 */
		public void error(SAXParseException exception) throws SAXException {
			ResourceWaypointPlugin.getDefault().log(exception);
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
		 */
		public void fatalError(SAXParseException exception) throws SAXException {
			ResourceWaypointPlugin.getDefault().log(exception);
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
		 */
		public void warning(SAXParseException exception) throws SAXException {
			ResourceWaypointPlugin.getDefault().log(exception);
		}

	}
	
	/**
	 * Gets the file that waypoints should be saved to. May be in the project or may be inside the 
	 * state location; depending on whether or not resource waypoints are shared.
	 * @return he file that waypoints should be saved to.
	 */
	//
	public static final File getWaypointFile(IProject project) {
//		if (ResourceWaypointPreferences.doesShareWaypoints(project)) {
//			IFile waypointFile = project.getFile(WaypointXMLUtilities.fileName);
//			return waypointFile.getLocation().toFile();
//		}
		IPath stateLocation = ResourceWaypointPlugin.getDefault().getStateLocation();
		File parent = stateLocation.toFile();
		File projectLocation = new File(parent, project.getName());
		if (!projectLocation.exists()) {
			projectLocation.mkdir();
		} else if (!projectLocation.isDirectory()) {
			//this should never happen, but, we'll cover all our bases.
			projectLocation.delete();
			projectLocation.mkdir();
		}
		File waypointFile = new File(projectLocation, WaypointXMLUtilities.fileName);
		return waypointFile;
	}
	
	
	/**
	 * @param waypointFile
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static IResourceWaypointDescriptor[] loadFile(File waypointFile) throws IOException, SAXException {
		FileInputStream stream = new FileInputStream(waypointFile);
		return load(stream);
	}
	
	public static IResourceWaypointDescriptor[] load(InputStream stream) throws SAXException, IOException {
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setEntityResolver(new EntityResolver(){
			//@tag tagsea.hack tour.TourTest2.1194628865421.0 : there needs to be a way to resolve the dtd to the plugin location. That should be the only referenced file, so this should work.
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				URL url = new URL(systemId);
				IPath path = new Path(url.getPath());
				if (WaypointXMLUtilities.dtd.equals(path.lastSegment())) {
					path = new Path(WaypointXMLUtilities.dtd);
					URL newURL = FileLocator.find(ResourceWaypointPlugin.getDefault().getBundle(), path, new HashMap());
					newURL = FileLocator.toFileURL(newURL);
					if (newURL != null) {
						InputSource newSource = new InputSource(new FileReader(newURL.getFile()));
						return newSource;
					}
				}
				return new InputSource(new FileReader(systemId));
			}});
		XMLHandler handler = new XMLHandler();
		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);
		InputSource source = new InputSource(stream);
		reader.parse(source);
		if (handler.waypointDescriptors != null) {
			return handler.waypointDescriptors.toArray(new IResourceWaypointDescriptor[handler.waypointDescriptors.size()]);
		}
		return new IResourceWaypointDescriptor[0];
	}
	
	public static void writeWaypoints(List<IWaypoint> waypoints, OutputStream stream, IProgressMonitor monitor) throws IOException, SAXException {
		try {
			WaypointXMLWriter.writeInternal(waypoints, stream, monitor);
		} catch (TransformerConfigurationException e) {
			throw new IOException(e.getMessage());
		}
	}

	public static void writeDescriptors(List<IResourceWaypointDescriptor> descriptors, OutputStream stream, IProgressMonitor monitor) throws IOException, SAXException {
		try {
			WaypointXMLWriter.writeInternal(descriptors, stream, monitor);
		} catch (TransformerConfigurationException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * Returns a formated string for saving dates.
	 * @param date
	 * @return
	 */
	@SuppressWarnings("nls")
	public static String getDateString(Date date) {
		Locale locale = Locale.getDefault();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		return locale.getLanguage().toLowerCase() + locale.getCountry().toUpperCase() + ":" + dateFormat.format(date);
	}

	/**
	 * Parses a date of the format returned from getDateString(Date), and returns it. Throws a ParseException
	 * if the date cannot be parsed.
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String date) throws ParseException {
		int colon = date.indexOf(':');
		if (colon != 4) throw new ParseException("Missing colon", -1);
		String lang = date.substring(0, 2);
		String country = date.substring(2, 4);
		String dateString = date.substring(5);
		Locale locale = new Locale(lang, country);
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		return dateFormat.parse(dateString);
	}
	
}
