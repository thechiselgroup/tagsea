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
package net.sourceforge.tagsea.cloudsee.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Class for representing a cloud element that appears in the CloudSee visualization.
 * Each CloudElement is a visual representation of a tag.
 * 
 * @author Sean Falconer
 */
public class CloudElement {
	// buffer to making font size be atleast this height
	private static final int FONT_BUFFER = 5;
	
	// cloud element intensities
	//private static final int[] INTENSITY = {0, 51, 102, 153, 204, 255};
	
	// the tag reference this element represents
	private String tagName;
	
	private int frequency;
	private int weight;
	
	// the bounding box for this element's text
	private Rectangle boundingBox;
	
	// the font used to draw this cloud element's text
	private Font cloudElementFont;
	
	//private Color cloudElementColor;
	
	// flag for whether this element is selected in the UI
	private boolean selected;
	
	public CloudElement() {}
	
	public CloudElement(String tagName, int frequency, int weight) {
		this.tagName = tagName;
		this.frequency = frequency;
		this.weight = weight;
		this.boundingBox = new Rectangle(0, 0, 0, 0);
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String getTagName() {
		return tagName;
	}
	
	public int getX() {
		return boundingBox.x;
	}
	
	public int getY() {
		return boundingBox.y;
	}
	
	public int getWidth() {
		return boundingBox.width;
	}

	public int getHeight() {
		return boundingBox.height;
	}
	
	public boolean contains(int x, int y) {
		return boundingBox.contains(x, y);
	}
	
	public void calculateFont(double factor, double maxWeight) {
		// calculate the font size for this cloud element
		int scale = (int)(Math.log(frequency + 1) * factor) + FONT_BUFFER;
		cloudElementFont = new Font(null, "Arial", scale, SWT.NORMAL);
		
		// calculate the intensity index for this cloud element
//		double percent = weight / maxWeight;
//		int index = Math.max((int)(percent * INTENSITY.length + 0.5) - 1, 0);
//		
//		cloudElementColor = new Color(null, INTENSITY[index], 0, 0);
	}
	
	public void draw(GC gc, int x, int y) {		
		gc.setFont(cloudElementFont);
		//gc.setForeground(cloudElementColor);
		gc.drawString(getTagName(), x, y);
		
		Point p = gc.stringExtent(getTagName());
		boundingBox.x = x;
		boundingBox.y = y;
		boundingBox.width = p.x;
		boundingBox.height = p.y;
		
		if(weight > 0) {
			//gc.setFont(Display.getDefault().getSystemFont());
			gc.drawString("+", x + p.x, y-10);
		}
		
		if(selected) {
			gc.setLineStyle(SWT.LINE_DASH);
			gc.drawRectangle(boundingBox);
			gc.setLineStyle(SWT.LINE_SOLID);
		}
	}
}
