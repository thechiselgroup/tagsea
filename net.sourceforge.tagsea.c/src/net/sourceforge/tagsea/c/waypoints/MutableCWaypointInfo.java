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
package net.sourceforge.tagsea.c.waypoints;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.tagsea.c.ICWaypointAttributes;
import net.sourceforge.tagsea.c.waypoints.parser.CWaypointInfo;
import net.sourceforge.tagsea.c.waypoints.parser.IParsedCWaypointInfo;
import net.sourceforge.tagsea.c.waypoints.parser.WaypointParseProblem;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * Java waypoint information that can be changed dynamically in order to create text changes
 * on a document.
 * @author Del Myers
 */
public class MutableCWaypointInfo extends CWaypointInfo implements IParsedCWaypointInfo {
	public static enum TagStyle {
		/**
		 * new tags will be added using dot syntax.
		 */
		DOT,
		/**
		 * new tags will be added using parenthesis syntax
		 */
		PARENETHESIS,
	}
	
	/**
	 * A class that represents a change to the internal structure of a MutableJavaWaypointInfo.
	 * All offsets are relative to the internal text data of this info, not to the external document
	 * in which the waypoint can be found. In order to translate changes to the internal structure
	 * to changes in the external structure, add the waypoint info's offest (found by calling
	 * getOffset() on IJavaWaypointInfo) to the text replacement's offset.
	 * @author Del Myers
	 */
	public static class TextReplacement {
		/**
		 * The offset that the replacement takes place at.
		 */
		public final int offset;
		/**
		 * The length of text replaced.
		 */
		public final int length;
		/**
		 * The inserted text.
		 */
		public final String text;
		TextReplacement(int offset, int length, String text) {
			this.offset = offset;
			this.length = length;
			this.text = text;
		}
	}
	
	TreeMap<String, String> attributes;
	TreeMap<String, IRegion> attributeRegionMap;
	TreeMap<String, List<IRegion>> tagRegionMap;
	private LinkedList<TextReplacement> replacementStack;
	private int offset;
	private String text;
	private TagStyle preferredStyle;
	private boolean deleted;
	
	/**
	 * Creates a copy of the given prototype.
	 * @param prototype
	 */
	public MutableCWaypointInfo(IParsedCWaypointInfo prototype, TagStyle preferredStyle) {
		if (prototype.getProblems().length > 0) {
			throw new UnsupportedOperationException(Messages.getString("MutableJavaWaypointInfo.problemError")); //$NON-NLS-1$
		}
		this.replacementStack = new LinkedList<TextReplacement>();
		this.preferredStyle = preferredStyle;
		Map<String, String> attributes = prototype.getAttributes();
		this.attributes = new TreeMap<String, String>();
		this.attributeRegionMap = new TreeMap<String, IRegion>();
		for (String key : attributes.keySet()) {
			this.attributes.put(key, attributes.get(key));
			this.attributeRegionMap.put(key, prototype.getRegionForAttribute(key));
		}
		this.tagRegionMap = new TreeMap<String, List<IRegion>>();
		for (String tag : prototype.getTags()) {
			List<IRegion> regions = new LinkedList<IRegion>();
			regions.addAll(Arrays.asList(prototype.getRegionsForTag(tag)));
			tagRegionMap.put(tag, regions);
		}
		this.offset = prototype.getOffset();
		this.text = prototype.getWaypointData();
		//adjust the text so that all the new-linish characters are chomped
		//from the end.
		if (this.text.length() - 1 >= 0) {
			char c = this.text.charAt(this.text.length()-1);
			int end = this.text.length();
			while (c == '\n' || c == '\r') {
				end--;
			}
			this.text = this.text.substring(0, end);
		}
		this.deleted = false;
		
	}
	
	public MutableCWaypointInfo() {
		this.preferredStyle = TagStyle.PARENETHESIS;
		this.replacementStack = new LinkedList<TextReplacement>();
		this.attributeRegionMap = new TreeMap<String, IRegion>();
		this.tagRegionMap = new TreeMap<String, List<IRegion>>();
		this.offset = 0;
		this.text = "@tag "; //$NON-NLS-1$
		this.deleted = false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getOffset()
	 */
	public int getOffset() {
		return offset;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getProblems()
	 */
	public WaypointParseProblem[] getProblems() {
		return new WaypointParseProblem[0];
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getRegionForAttribute(java.lang.String)
	 */
	public IRegion getRegionForAttribute(String key) {
		return attributeRegionMap.get(key);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getRegionForDescription()
	 */
	public IRegion getRegionForDescription() {
		int end = 0;
		for (List<IRegion> regions : tagRegionMap.values()) {
			for (IRegion region : regions) {
				end = Math.max(region.getOffset() + region.getLength(), end);
			}
		}
		for (IRegion region : attributeRegionMap.values()) {
			end = Math.max(region.getOffset() + region.getLength(), end);
		}
		
		for (; end < text.length() && text.charAt(end) != ':'; end++) {}
		if (end >= text.length()) return null;
		return new Region(end, text.length()-end);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getRegionsForTag(java.lang.String)
	 */
	public IRegion[] getRegionsForTag(String tag) {
		List<IRegion> regions = tagRegionMap.get(tag);
		if (regions != null) {
			return (IRegion[])regions.toArray(new IRegion[regions.size()]);
		}
		return new IRegion[0];
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getWaypointData()
	 */
	public String getWaypointData() {
		return text;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getAttributes()
	 */
	public Map<String, String> getAttributes() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getDescription()
	 */
	public String getDescription() {
		IRegion region = getRegionForDescription();
		if (region.getLength() > 0) {
			return text.substring(region.getOffset());
		}
		return ""; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getLength()
	 */
	public int getLength() {
		return text.length();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getTags()
	 */
	public String[] getTags() {
		return tagRegionMap.keySet().toArray(new String[0]);
	}

	/**
	 * Deletes the entire text of the waypoint. All information will be deleted and no more
	 * incoming changes will be processed.
	 *
	 */
	public void delete() {
		attributeRegionMap.clear();
		tagRegionMap.clear();
		TextReplacement replacement = new TextReplacement(0, text.length(), ""); //$NON-NLS-1$
		applyReplacement(replacement);
		this.deleted = true;
	}
	
	public boolean isDeleted() {
		return this.deleted;
	}
	
	
	
	
	/**
	 * Adds the given tag to the waypoint information using the preferred tag style.
	 */
	public void addTag(String name) {
		if (isDeleted()) return;
		//do nothing if the tag already exists.
		if (tagRegionMap.containsKey(name)) return;
		
		switch (preferredStyle) {
		case DOT:
			addTagAsDot(name, findEndOfTags());
			break;
		case PARENETHESIS:
			addTagAsParenthesis(name, findEndOfTags());
			break;
		}
	}

	


	
	


	/**
	 * Finds the index after the last character for the largest tag region.
	 * @return
	 */
	private int findEndOfTags() {
		int end = 0;
		for (List<IRegion> regions : tagRegionMap.values()) {
			for (IRegion region : regions) {
				end = Math.max(region.getOffset() + region.getLength(), end);
			}
		}
		//find the beginning of the attributes.
		if (end == 0) {
			for (IRegion region : attributeRegionMap.values()) {
				end = Math.min(region.getOffset()-1, end);
			}
		}
		if (end == 0) {
			IRegion region = getRegionForDescription();
			if (region == null) {
				end = this.text.length();
			} else {
				//the end will be at the ':' we want to add before the colon.
				end = region.getOffset()-1;
			}
		}
		if (end == 0) {
			end = text.length();
		}
		return end;
	}
	
	private int findEndOfAttributes() {
		int end = 0;
		for (IRegion region : attributeRegionMap.values()) {
			end = Math.max(region.getOffset() + region.getLength(), end);
		}
		if (end == 0) {
			for (List<IRegion> regions : tagRegionMap.values()) {
				for (IRegion region : regions) {
					end = Math.max(region.getOffset() + region.getLength(), end);
				}
			}
		}
		if (end == 0) {
			IRegion region = getRegionForDescription();
			if (region == null) {
				end = this.text.length();
			} else {
				end = region.getOffset()-2;
			}
		}
		return end;
	}
	
	/**
	 * @param name
	 */
	private void addTagAsParenthesis(String name, int offset) {
		String dotName = name;
		name = getParenthesisTag(name);
		List<IRegion> regions = tagRegionMap.get(name);
		if (regions == null) {
			regions = new LinkedList<IRegion>();
			tagRegionMap.put(dotName, regions);
		}
		String prefix = ""; //$NON-NLS-1$
		if (offset == text.length() || !Character.isWhitespace(text.charAt(offset-1))) {
			prefix = " "; //$NON-NLS-1$
		}
		String suffix = ""; //$NON-NLS-1$
		if (offset < text.length()-1 && (!Character.isWhitespace(text.charAt(offset)))) {
			suffix = " "; //$NON-NLS-1$
		}
		applyReplacement(new TextReplacement(offset, 0, prefix+name+suffix));
		regions.add(new Region(offset+prefix.length(), name.length()));
	}
	
	private String getParenthesisTag(String tagName) {
		int dotCount = 0;
		for (int i = 0; i < tagName.length(); i++) {
			if (tagName.charAt(i) == '.') {
				dotCount++;
			}
		}
		String name = tagName.replace('.', '(');
		for (int i = 0; i < dotCount; i++) {
			name+=')';
		}
		return name;
	}

	/**
	 * @param name
	 */
	private void addTagAsDot(String name, int offset) {
		List<IRegion> regions = tagRegionMap.get(name);
		if (regions == null) {
			regions = new LinkedList<IRegion>();
			tagRegionMap.put(name, regions);
		}
		String prefix = ""; //$NON-NLS-1$
		if (offset == text.length() || !Character.isWhitespace(text.charAt(offset-1))) {
			prefix = " "; //$NON-NLS-1$
		}
		String suffix = ""; //$NON-NLS-1$
		if (offset < text.length()-1 && (!Character.isWhitespace(text.charAt(offset)))) {
			suffix = " "; //$NON-NLS-1$
		}
		applyReplacement(new TextReplacement(offset, 0, prefix + name + suffix));
		regions.add(new Region(offset+prefix.length(), name.length()));
	}
	
	public void changeTag(String oldName, String newName) {
		if (!tagRegionMap.containsKey(oldName)) return;
		List<IRegion> newRegions = tagRegionMap.get(newName);
		if (newRegions == null) {
			newRegions = new LinkedList<IRegion>();
			tagRegionMap.put(newName, newRegions);
		}
		//use the removal logic, but we want to make sure that
		//all the tags are replaced back at the same offset.
		List<IRegion> oldRegions = tagRegionMap.get(oldName);
		while (oldRegions.size() > 0) {
			IRegion region = oldRegions.remove(0);
			String oldText = text.substring(region.getOffset(), region.getOffset()+region.getLength());
			String name = newName;
			if (oldText.indexOf('(') != -1) {
				name = getParenthesisTag(newName);
			}
			applyReplacement(new TextReplacement(region.getOffset(), region.getLength(), name));
			newRegions.add(new Region(region.getOffset(), name.length()));
		}
		tagRegionMap.remove(oldName);
	}
	
	private void applyReplacement(TextReplacement replacement) {
		if (replacement.offset == text.length() + 1) {
			if (replacement.length != 0) {
				throw new IndexOutOfBoundsException();
			}
			this.text += replacement.text;
		} else if (replacement.offset > text.length()) {
			throw new IndexOutOfBoundsException();
		} else {
			this.text = 
				text.substring(0, replacement.offset)+ 
				replacement.text +
				text.substring(replacement.offset+replacement.length, text.length());
//			adjust all the regions that come after the replacement.
			for (List<IRegion> regions : tagRegionMap.values()) {
				List<IRegion> regionsToRemove = new LinkedList<IRegion>();
				List<IRegion> fixedRegions = new LinkedList<IRegion>();
				for (IRegion region : regions) {
					if (region.getOffset() > replacement.offset) {
						regionsToRemove.add(region);
						fixedRegions.add(new Region(region.getOffset()+(replacement.text.length()-replacement.length), region.getLength()));
					}
				}
				regions.removeAll(regionsToRemove);
				regions.addAll(fixedRegions);
			}
			for (String attr : attributeRegionMap.keySet()) {
				IRegion region = attributeRegionMap.get(attr);
				if (region.getOffset() > replacement.offset) {
					IRegion r = new Region(region.getOffset()+(replacement.text.length()-replacement.length), region.getLength());
					attributeRegionMap.put(attr, r);
				}
			}
		}
		
		replacementStack.add(replacement);
		
	}
	
	
	/**
	 * Sets the given attribute to the given value. The value cannot contain any double
	 * quotation marks (") or end-of-line characters (\n \r). If any of these exist, an
	 * IllegalArgumentException will be thrown.
	 * 
	 * "length", "offset", "file", and "javaElement" are illegal names and will cause an
	 * IllegalArgumentException to be thrown.
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, String value) throws IllegalArgumentException {
		if (ICWaypointAttributes.ATTR_RESOURCE.equals(name) 
				|| ICWaypointAttributes.ATTR_CHAR_END.equals(name)
				|| ICWaypointAttributes.ATTR_CHAR_START.equals(name)
		) {
			throw new IllegalArgumentException(Messages.getString("MutableJavaWaypointInfo.illegalAttribute") + name); //$NON-NLS-1$
		}
		if (isDeleted()) return;
		if (IWaypoint.ATTR_MESSAGE.equals(name)) {
			setDescription(value);
			return;
		}
		IRegion region = getRegionForAttribute(name);
		if (value == null || "".equals(value)) { //$NON-NLS-1$
			//simply remove the old one.
			if (region != null) {
				attributeRegionMap.remove(name);
				applyReplacement(new TextReplacement(region.getOffset()-1, region.getLength()+1, "")); //$NON-NLS-1$
				return;
			}
		}
		boolean needsQuotes = false;
		for (int i = 0 ; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '\r' || c == '\n' || c == '"') {
				throw new IllegalArgumentException(Messages.getString("MutableJavaWaypointInfo.illegalCharacter") + c); //$NON-NLS-1$
			}
			if (!needsQuotes && (Character.isWhitespace(c) || c == ':')) {
				needsQuotes = true;
			}
		}
		if (needsQuotes) {
			value = '"' + value + '"';
		}
		
		String insertString = "-"+name+"="+value; //$NON-NLS-1$ //$NON-NLS-2$
		if (region == null) {
			//just add the attribute to the end.
			
			int offset = findEndOfAttributes();
			String prefix = ""; //$NON-NLS-1$
			if (offset == text.length() || !Character.isWhitespace(text.charAt(offset-1))) {
				prefix = " "; //$NON-NLS-1$
			}
			String suffix = ""; //$NON-NLS-1$
			if (offset < text.length()-1 && (!Character.isWhitespace(text.charAt(offset)))) {
				suffix = " "; //$NON-NLS-1$
			}
			applyReplacement(new TextReplacement(offset, 0, prefix+insertString+suffix));
			attributeRegionMap.put(name, new Region(offset+prefix.length(), insertString.length()));
		} else {
			applyReplacement(new TextReplacement(region.getOffset(), region.getLength(), insertString));
			attributeRegionMap.put(name, new Region(region.getOffset(), insertString.length()));
		}
	}
	
	
	public void setDescription(String description) {
		if (isDeleted()) return;
		IRegion region = getRegionForDescription();
		if (region == null ) {
			int offset = text.length();
			String insertString = " :" + description; //$NON-NLS-1$
			applyReplacement(new TextReplacement(offset, 0, insertString));
		} else {
			applyReplacement(new TextReplacement(region.getOffset()+1, region.getLength()-1, description));
		}
	}
	
	public void setAuthor(String author) {
		if (isDeleted()) return;
		setAttribute(IWaypoint.ATTR_AUTHOR, author);
	}
	
	/**
	 * Sets the date using the default locale.
	 * @param date
	 */
	public void setDate(Date date) {
		if (isDeleted()) return;
		if (date == null) {
			setAttribute(IWaypoint.ATTR_DATE, null);
			return;
		}
		Locale loc = Locale.getDefault();
		String dateString = DateFormat.getDateInstance(DateFormat.SHORT, loc).format(date);
		dateString = loc.getLanguage()+loc.getCountry().toUpperCase()+':'+dateString;
		setAttribute(IWaypoint.ATTR_DATE, dateString);
	}
	
	
	public Iterator<TextReplacement> getReplacementIterator() {
		return replacementStack.iterator();
	}
	
	public void removeTag(String name) {
		if (isDeleted()) return;
		List<IRegion> regions = tagRegionMap.get(name);
		if (regions == null) return;
		while (regions.size() > 0) {
			IRegion region = regions.remove(0);
			//there will be whitespace before the tag.
			TextReplacement replacement = new TextReplacement(region.getOffset()-1, region.getLength()+1, ""); //$NON-NLS-1$
			applyReplacement(replacement);
			
		}
	}
	


	/**
	 * Sets the offset in the text at which the waypoint occurs.
	 * @param offset
	 */
	public void setOffsetInText(int offset) {
		this.offset = offset;		
	}
}
