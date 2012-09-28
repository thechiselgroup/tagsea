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
package net.sourceforge.tagsea.resources.waypoints.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.SortedSet;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.IWaypointType;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;



/**
 * Utility class to write waypoints, or waypoint descriptors, as XML out to a stream.
 * @author Del Myers
 *
 */
class WaypointXMLWriter {
	/**
	 * @param name
	 * @param stream
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws TransformerConfigurationException 
	 */
	static void writeInternal(List waypointsOrDescriptors, OutputStream stream, IProgressMonitor monitor) throws IOException, SAXException, TransformerConfigurationException {
		monitor.beginTask("Writing waypoints", waypointsOrDescriptors.size());
		StreamResult streamResult = new StreamResult(stream);
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		TransformerHandler handler = tf.newTransformerHandler();
		Transformer serializer = handler.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, WaypointXMLUtilities.dtd);
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		handler.setResult(streamResult);
		handler.startDocument();
		handler.startElement("", "", WaypointXMLUtilities.DOC_ROOT, new AttributesImpl());
		for (Object wp : waypointsOrDescriptors) {
			if (wp instanceof IWaypoint)
				writeWaypoint(handler, (IWaypoint)wp);
			else if (wp instanceof IResourceWaypointDescriptor)
				writeDescriptor(handler, (IResourceWaypointDescriptor)wp);
			monitor.worked(1);
		}
		handler.endElement("", "", WaypointXMLUtilities.DOC_ROOT);
		handler.endDocument();
		stream.close();
		monitor.done();
		
	}
	
	
	/**
	 * @param handler
	 * @param wp
	 * @throws SAXException 
	 */
	@SuppressWarnings("nls")
	private static void writeWaypoint(TransformerHandler handler, IWaypoint wp) throws SAXException {
		int line = ResourceWaypointUtils.getLine(wp);
		//IResource resource = ResourceWaypointUtils.getResource(wp);
		String resource = wp.getStringValue(IResourceWaypointAttributes.ATTR_RESOURCE, "");
		AttributesImpl attrs = new AttributesImpl();
		IWaypointType type = TagSEAPlugin.getDefault().getWaypointType(ResourceWaypointPlugin.WAYPOINT_ID);
		int charStart = wp.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_START, -1);
		int charEnd = wp.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_END, -1);
		
		attrs.addAttribute("", "", WaypointXMLUtilities.CHAR_START, "PCDATA", charStart+"");
		attrs.addAttribute("", "", WaypointXMLUtilities.CHAR_END, "PCDATA", charEnd+"");
		String revision = wp.getStringValue(IResourceWaypointAttributes.ATTR_REVISION, (String)type.getDefaultValue(IResourceWaypointAttributes.ATTR_REVISION));
		attrs.addAttribute("", "", WaypointXMLUtilities.REVISION_ATTR, "PCDATA", revision);
		attrs.addAttribute("", "", WaypointXMLUtilities.RESOURCE_ATTR, "PCDATA", resource);
		attrs.addAttribute("", "", WaypointXMLUtilities.LINE_ATTR, "PCDATA", line+"");
		String stamp = wp.getStringValue(IResourceWaypointAttributes.ATTR_STAMP, (String)type.getDefaultValue(IResourceWaypointAttributes.ATTR_STAMP));
		attrs.addAttribute("", "", WaypointXMLUtilities.STAMP_ATTR, "PCDATA", stamp);
		AbstractWaypointDelegate delegate = 
			ResourceWaypointPlugin.getDefault().getDelgate();
	
		if (!delegate.getDefaultValue(IWaypoint.ATTR_DATE).equals(wp.getDate()))
			attrs.addAttribute("", "", WaypointXMLUtilities.DATE_ATTR, "CDATA", WaypointXMLUtilities.getDateString(wp.getDate()));
		if (!delegate.getDefaultValue(IWaypoint.ATTR_AUTHOR).equals(wp.getAuthor()))
			attrs.addAttribute("", "", WaypointXMLUtilities.AUTHOR_ATTR, "PCDATA", wp.getAuthor());
		handler.startElement("", "", WaypointXMLUtilities.WAYPOINT_ELEMENT, attrs);
		ITag[] tags = wp.getTags();
		String message = wp.getText();
		for (ITag tag : tags) {
			handler.startElement("", "", WaypointXMLUtilities.TAG_ELEMENT, new AttributesImpl());
			handler.characters(tag.getName().toCharArray(), 0, tag.getName().length());
			handler.endElement("", "", WaypointXMLUtilities.TAG_ELEMENT);
		}
		if (!delegate.getDefaultValue(IWaypoint.ATTR_MESSAGE).equals(message)) {
			handler.startElement("", "", WaypointXMLUtilities.MESSAGE_ELEMENT, new AttributesImpl());
			handler.characters(message.toCharArray(), 0, message.length());
			handler.endElement("", "", WaypointXMLUtilities.MESSAGE_ELEMENT);
		}
		handler.endElement("", "", WaypointXMLUtilities.WAYPOINT_ELEMENT);
		
	}

	
	/**
	 * @param handler
	 * @param wp
	 * @throws SAXException 
	 */
	@SuppressWarnings("nls")
	private static void writeDescriptor(TransformerHandler handler, IResourceWaypointDescriptor wp) throws SAXException {
		int line = wp.getLine();
		String resource = wp.getResource();
		AttributesImpl attrs = new AttributesImpl();
		
		int charStart = wp.getCharStart();
		int charEnd = wp.getCharEnd();
		
		attrs.addAttribute("", "", WaypointXMLUtilities.CHAR_START, "PCDATA", charStart+""); 
		attrs.addAttribute("", "", WaypointXMLUtilities.CHAR_END, "PCDATA", charEnd+"");
		String revision = wp.getRevision();
		attrs.addAttribute("", "", WaypointXMLUtilities.REVISION_ATTR, "PCDATA", revision);
		attrs.addAttribute("", "", WaypointXMLUtilities.RESOURCE_ATTR, "PCDATA", resource);
		attrs.addAttribute("", "", WaypointXMLUtilities.LINE_ATTR, "PCDATA", line+"");
		String stamp = wp.getStamp();
		attrs.addAttribute("", "", WaypointXMLUtilities.STAMP_ATTR, "PCDATA", stamp);
		AbstractWaypointDelegate delegate = 
			ResourceWaypointPlugin.getDefault().getDelgate();
	
		if (!delegate.getDefaultValue(IWaypoint.ATTR_DATE).equals(wp.getDate()))
			attrs.addAttribute("", "", WaypointXMLUtilities.DATE_ATTR, "CDATA", WaypointXMLUtilities.getDateString(wp.getDate()));
		if (!delegate.getDefaultValue(IWaypoint.ATTR_AUTHOR).equals(wp.getAuthor()))
			attrs.addAttribute("", "", WaypointXMLUtilities.AUTHOR_ATTR, "PCDATA", wp.getAuthor());
		handler.startElement("", "", WaypointXMLUtilities.WAYPOINT_ELEMENT, attrs);
		SortedSet<String> tags = wp.getTags();
		String message = wp.getText();
		for (String tag : tags) {
			handler.startElement("", "", WaypointXMLUtilities.TAG_ELEMENT, new AttributesImpl());
			handler.characters(tag.toCharArray(), 0, tag.length());
			handler.endElement("", "", WaypointXMLUtilities.TAG_ELEMENT);
		}
		if (!delegate.getDefaultValue(IWaypoint.ATTR_MESSAGE).equals(message)) {
			handler.startElement("", "", WaypointXMLUtilities.MESSAGE_ELEMENT, new AttributesImpl());
			handler.characters(message.toCharArray(), 0, message.length());
			handler.endElement("", "", WaypointXMLUtilities.MESSAGE_ELEMENT);
		}
		handler.endElement("", "", WaypointXMLUtilities.WAYPOINT_ELEMENT);
		
	}

}
