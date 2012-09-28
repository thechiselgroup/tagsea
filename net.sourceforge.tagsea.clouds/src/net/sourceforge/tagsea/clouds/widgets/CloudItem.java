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
package net.sourceforge.tagsea.clouds.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;

/**
 * An item that goes inside of a cloud.
 * @author Del Myers
 *
 */
public class CloudItem extends Item {

	private int id;
	private Font font;
	private Color foreground;
	private Color background;
	private boolean selected;
	private Cloud cloud;
	private Point size;
	private int priority;
	private Rectangle bounds;

	/**
	 * Creates a cloud item in the given Cloud at the given index. May throw an {@link ArrayIndexOutOfBoundsException}
	 * if the index is out of range. If the index is negative, then the item will be added as the last
	 * item in the cloud.
	 * @param parent the cloud to create this item in.
	 * @param index the index at which to place the item in the cloud, or -1 if at the end of the list.
	 */
	public CloudItem(Cloud parent, int index) {
		super(parent, SWT.NONE, index);
		this.id = parent.createItem(this, index);
		this.cloud = parent;
		this.foreground = parent.getForeground();
		this.background = parent.getBackground();
		this.font = parent.getFont();
		this.priority = 0;
		bounds = new Rectangle(0,0,0,0);
		addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				cloud.deleteItem(CloudItem.this);
			}
		});
	}
	
	/**
	 * Creates a cloud item on the given cloud, and adds it to the end of the list.
	 * @param parent
	 */
	public CloudItem(Cloud parent) {
		this(parent, -1);
	}
	
	int itemId() {
		return this.id;
	}
	
	void setFont(Font f) {
		this.font = f;
		this.size = null;
	}
	
	/**
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}
	
//	void setLocation(int x, int y) {
//		this.bounds.x = x;
//		this.bounds.y = y;
//	}
	
	public void setForeground(Color foreground) {
		this.foreground = foreground;
		Point size = getTextSize();
		cloud.redraw(getLocation().x, getLocation().y, size.x, size.y, false);
	}
	
	/**
	 * @return the foreground
	 */
	public Color getForeground() {
		return foreground;
	}
	
	public void setBackground(Color background) {
		this.background = background;
		Point size = getTextSize();
		cloud.redraw(getLocation().x, getLocation().y, size.x, size.y, false);
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		size = null;
		cloud.markDirty();
	}
	
	Rectangle getBounds() {
		return bounds;
	}
	
	void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	Point getTextSize() {
		if (size == null) {
			String text = getText();
			if (text == null) {
				size = new Point(0,0);
			}
			GC gc = new GC(cloud);
			gc.setFont(getFont());
			size = gc.stringExtent(text);
			gc.dispose();
		}
		return size;
	}
	
	/**
	 * @return the background
	 */
	public Color getBackground() {
		return background;
	}
	
	void setSelection(boolean selected) {
		if (selected == this.selected) return;
		this.selected = selected;
		Rectangle bounds = getBounds();
		if (bounds != null && !bounds.isEmpty()) {
			cloud.redraw(bounds.x-1, bounds.y-1, bounds.width+2, bounds.height+2, false);
		}
	}
	
	public boolean getSelection() {
		return selected;
	}
	
	
	
	void draw(GC gc) {
		Rectangle bounds = getBounds();
		if (selected) {
			if (cloud.isFocusControl()) {
				gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION));
				gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
			} else {
				gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
				gc.setForeground(getForeground());
			}
		} else {
			gc.setBackground(getBackground());
			gc.setForeground(getForeground());
		}
		gc.fillRectangle(bounds);
		Point size = getTextSize();
		Point center = new Point(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
		int x = center.x - size.x/2;
		int y = center.y - size.y/2;
		gc.setFont(getFont());
		gc.drawString(getText(), x, y, false);
	}
	
	/**
	 * Sets the "priority" of this item. Will be used to calculate what size the cloud item should be.
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		if (priority == this.priority) return;
		this.priority = priority;
		cloud.layout(true);
	}
	
	/**
	 * Returns the priority used to determine the size of the cloud item.
	 * @return the priority used to determine the size of the cloud item.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @return
	 */
	Point getLocation() {
		return new Point(getBounds().x, getBounds().y);
	}

}
