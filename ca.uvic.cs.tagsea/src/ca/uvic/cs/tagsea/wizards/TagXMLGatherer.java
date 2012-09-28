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
package ca.uvic.cs.tagsea.wizards;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.core.TagCollection;
import ca.uvic.cs.tagsea.core.Waypoint;

/**
 * TODO comment
 * @author Del Myers
 *
 */
public class TagXMLGatherer implements IRunnableWithProgress {
	Document doc;
	String XML;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
		} catch (ParserConfigurationException e) {
			throw new InvocationTargetException(e);
		}
		TagCollection collection = TagSEAPlugin.getDefault().getTagCollection();
		Tag[] tags = collection.getAllTags();
		Waypoint[] waypoints = collection.getAllWaypoints();
		SubProgressMonitor tagMonitor = new SubProgressMonitor(monitor, tags.length);
		SubProgressMonitor waypointMonitor = new SubProgressMonitor(monitor, waypoints.length);
		monitor.beginTask("Creating XML", tags.length + waypoints.length);
		Element root = doc.createElement("tagsea-document");
		doc.appendChild(root);
		generateTags(tagMonitor, root.appendChild(doc.createElement("tags")), tags);
		generateWaypoints(waypointMonitor, root.appendChild(doc.createElement("waypoints")), waypoints);
		monitor.subTask("Building XML String");
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t;
		try {
			t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
			DOMSource source = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult stringResult = new StreamResult(writer);
			t.transform(source, stringResult);
			XML = writer.toString();
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
	
	}

	/**
	 * @param tagMonitor
	 * @param node
	 * @param doc
	 * @param tags
	 * @throws InterruptedException 
	 */
	private void generateTags(SubProgressMonitor monitor, Node node, Tag[] tags) throws InterruptedException {
		monitor.beginTask("Generating Tags", tags.length);
		for (Tag tag : tags) {
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
			//create the node for the tag.
			Element xmlTag = doc.createElement("tag");
			node.appendChild(xmlTag);
			Attr name = doc.createAttribute("name");
			name.setValue(filter(tag.getId()));
			xmlTag.getAttributes().setNamedItem(name);
			monitor.worked(1);
		}
		monitor.done();
	}

	/**
	 * @param waypointMonitor
	 * @param node
	 * @param doc
	 * @param waypoints
	 * @throws InterruptedException 
	 */
	private void generateWaypoints(SubProgressMonitor monitor, Node node, Waypoint[] waypoints) throws InterruptedException {
		monitor.beginTask("Generating Waypoints", waypoints.length);
		for (Waypoint waypoint : waypoints) {
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
			//@tag tagsea(todo) : add filtering to the tags.
			//create the node for the tag.
			Element xmlWaypoint = doc.createElement("waypoint");
			node.appendChild(xmlWaypoint);
			Attr tag = doc.createAttribute("tag");
			tag.setValue(filter(waypoint.getTag().getId()));
			xmlWaypoint.setAttributeNode(tag);
			Attr file = doc.createAttribute("file");
			file.setValue(waypoint.getMarker().getResource().getLocation().toPortableString());
			xmlWaypoint.setAttributeNodeNS(file);
			Attr line = doc.createAttribute("line");
			line.setValue(""+waypoint.getLineNumber()+"");
			xmlWaypoint.setAttributeNode(line);
			Attr comment = doc.createAttribute("comment");
			comment.setValue(filter(waypoint.getComment()));
			xmlWaypoint.setAttributeNode(comment);
			
			Element metaData = doc.createElement("metadata");
			xmlWaypoint.appendChild(metaData);
			
			String authorString = waypoint.getAuthor();
			if (authorString != null) {
				Element author = doc.createElement("author");
				author.appendChild(doc.createTextNode(authorString));
				metaData.appendChild(author);
			}
			String dateString = waypoint.getDate();
			if (dateString != null) {
				Element date = doc.createElement("date");
				date.appendChild(doc.createTextNode(dateString));
				metaData.appendChild(date);
			}
			String javaElementString = waypoint.getJavaElementName();
			if (javaElementString != null) {
				Element java = doc.createElement("java-element");
				java.appendChild(doc.createTextNode(javaElementString));
				metaData.appendChild(java);
			}
			monitor.worked(1);
			
		}
		monitor.done();
		
	}
	
	/**
	 * Filters the given string for illegal characters, and returns the result.
	 * @param string
	 * @return
	 */
	private String filter(String string) {
		if (string == null) return "";
		StringBuilder builder = new StringBuilder(string);
		for (int i = 0; i < builder.length(); i++) {
			char c = builder.charAt(i);
			String replacement = getReplacement(c);
			if (replacement.equals(""+c)) continue;
			builder.replace(i, i+1, replacement);
			i = i + replacement.length()-1;
		}
		return builder.toString();
	}

	/**
	 * @param c
	 * @return
	 */
	private String getReplacement(char c) {
		switch (c) {
			case '<':
				return "&lt;";
			case '>':
				return "&gt;";
			case '&':
				return "&amp;";
			case '\"':
				return "&quot;";
			case '\'':
				return "&apos;";
		}
		return c + "";
	}

	public Document getDocument() {
		return doc;
	}
	
	public String getXML() {
		return XML;
	}



}
