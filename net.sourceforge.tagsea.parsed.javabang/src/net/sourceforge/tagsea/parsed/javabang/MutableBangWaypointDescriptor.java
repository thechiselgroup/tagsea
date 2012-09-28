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
package net.sourceforge.tagsea.parsed.javabang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.parsed.parser.DefaultMutableParsedWaypointDescripor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

/**
 * Creates a mutable waypoint descriptor for bang waypoints.
 * @author Del Myers
 *
 */
public class MutableBangWaypointDescriptor extends DefaultMutableParsedWaypointDescripor {
	
	private int offset;
	private HashMap<String, Set<IRegion>> tagRegionMap;
	private Document document;
	private boolean dirty;
	private String message;
	private int line;

	/**
	 * Uses the given text to generate a bang waypoint descriptor. The regions are the regions in
	 * which the tags exist, starting from the beginning of the text. offset is the offset in the original
	 * document.
	 * @param text
	 * @param tags
	 */
	public MutableBangWaypointDescriptor(int offset, String text, List<IRegion> tags, int line) {
		this.offset = offset;
		this.line = line;
		this.document = new Document(text);
		//read the regions and generate a tag region map.
		this.tagRegionMap = new HashMap<String, Set<IRegion>>();
		for (IRegion tagRegion : tags) {
			String tagWord = text.substring(tagRegion.getOffset(), tagRegion.getOffset()+tagRegion.getLength());
			String realTag = tagWord.substring(1);
			Set<IRegion> regions = tagRegionMap.get(realTag);
			if (regions == null) {
				regions = new HashSet<IRegion>();
				tagRegionMap.put(realTag, regions);
			}
			regions.add(tagRegion);
		}
		this.dirty = true;
	}
	
	/**
	 * Returns the most up-to-date regions, relative to the beginning of the waypoint, where tags exist.
	 * No order is guaranteed.
	 * @return
	 */
	public Collection<IRegion> getAllTagLocations() {
		HashSet<IRegion> regionSet = new HashSet<IRegion>();
		for (String tag : tagRegionMap.keySet()) {
			regionSet.addAll(tagRegionMap.get(tag));
		}
		return regionSet;
	}


	public String getText() {
		return document.get();
	}

	public void removeTag(String tag) throws UnsupportedOperationException {
		Set<IRegion> tagRegions = tagRegionMap.get(tag);
		if (tagRegions != null) {
			MultiTextEdit edit = new MultiTextEdit();
			for(IRegion region : tagRegions) {
				edit.addChild(new ReplaceEdit(region.getOffset(), region.getLength(), tag));
			}
			try {
				edit.apply(this.document);
			} catch (MalformedTreeException e) {
			} catch (BadLocationException e) {
			}
			tagRegions.remove(tag);
			dirty = true;
		}
		
	}

	public void replaceTag(String oldTag, String newTag)
			throws UnsupportedOperationException {
		Set<IRegion> oldRegions = tagRegionMap.get(oldTag);
		if (oldRegions == null) return;
		List<IRegion> regionList = new ArrayList<IRegion>(oldRegions);
		Collections.sort(regionList, new Comparator<IRegion>(){
			public int compare(IRegion o1, IRegion o2) {
				return o1.getOffset() - o2.getOffset();
			}
		});
		int sizeDiff = oldTag.length() - newTag.length();
		int adjust = 0;
		Set<IRegion> newRegions = new HashSet<IRegion>();
		for (IRegion region : regionList) {
			try {
				int offset = region.getOffset() - adjust;
				this.document.replace(offset, region.getLength(), "`" + newTag);
				int length = newTag.length() + 1;
				newRegions.add(new Region(offset, length));
				adjust += sizeDiff;
			} catch (BadLocationException e) {
			}
		}
		Set<IRegion> newTagSet = tagRegionMap.get(newTag);
		if (newTagSet == null) {
			newTagSet = new HashSet<IRegion>();
			tagRegionMap.put(newTag, newTagSet);
		}
		newTagSet.addAll(newRegions);
		this.dirty = true;
	}


	public String getAuthor() {
		return "";
	}

	public int getCharEnd() {
		return offset+document.getLength();
	}

	public int getCharStart() {
		return offset;
	}
	
	@Override
	public int getLine() {
		return line;
	}

	public Date getDate() {
		return null;
	}

	public String getDetail() {
		return "";
	}

	public String getMessage() {
		if (dirty) {
			this.message = recalculateMessage();
		}
		return this.message;
	}

	private boolean isMultiLine() {
		String documentText = document.get();
		return (documentText.charAt(0) == '/' && documentText.charAt(1) == '*');
	}
	
	/**
	 * @return
	 */
	private String recalculateMessage() {
		int start = 2;
		int end = this.document.getLength();
		if (isMultiLine()) {
			//read back from the end.
			end -= 3;
			while (end > start) {
				try {
					char c = this.document.getChar(end);
					if (c != '*' && c != '/' && !(Character.isWhitespace(c))) {
						break;
					}
					end--;
				} catch (BadLocationException e) {
					break;
				}
			}
			end++;
		}
		char c;
		String word = "";
		StringBuilder message = new StringBuilder();
		if (start < end) {
			try {
				String text = this.document.get(start, end - start);
				String[] lines = text.split("(\\r+)|(\\n+)");
				for (String line : lines) {
					line = line.replaceAll("\\s+", " ");
					line = line.replaceAll("^(\\s*)\\*+(\\s*)", " ");
					line = line.trim();
					if ("".equals(line)) {
						continue;
					}
					for (int i = 0; i < line.length(); i++) {
						c = line.charAt(i);
						if (Character.isWhitespace(c)) {
							if (!"".equals(word)) {
								message.append(' ');
								if (BangCommentParser.isTag(word)) {
									word = word.substring(1);
								}
								message.append(word);
								word = "";
							}
						} else {
							word += c;
						}
					}
					if (!"".equals(word)) {
						if (BangCommentParser.isTag(word)) {
							word = word.substring(1);
						}
						message.append(" " + word);
						word = "";
					}
					message.append(' ');
				}
			} catch (BadLocationException e) {
				return "";
			}
		}
		return message.toString();
	}

	public String[] getTags() {
		return tagRegionMap.keySet().toArray(new String[tagRegionMap.size()]);
	}
	
	@Override
	public void addTag(String tag) throws UnsupportedOperationException {
		//never add the default tag.
		if (ITag.DEFAULT.equals(tag)) return;
		//don't do anything if it isn't a valid tag or it already exists
		if (tag == null || !BangCommentParser.isTag('`' + tag) || tagRegionMap.keySet().contains(tag)) return;
		int end = document.getLength()-1;
		if (isMultiLine()) {
			try {
				while (Character.isWhitespace(document.getChar(end)) && end > 0) {
					end--;
				}
			} catch (BadLocationException e) {
				JavaBangPlugin.getDefault().log(e);
			}
			end-=2;
		}
		
		try {
			while (Character.isWhitespace(document.getChar(end)) && end > 0) {
				end--;
			}
		} catch (BadLocationException e) {
			JavaBangPlugin.getDefault().log(e);
		}
		
		end++;
		try {
			document.replace(end, 0, " `" + tag);
		} catch (BadLocationException e) {
			JavaBangPlugin.getDefault().log(e);
		}
		Set<IRegion> tagRegionSet = new HashSet<IRegion>();
		tagRegionSet.add(new Region(end, tag.length()+2));
		tagRegionMap.put(tag, tagRegionSet);
	}

}
