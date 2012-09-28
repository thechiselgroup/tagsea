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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Creates a cloud that lays-out and sizes cloud widgets according to importance ratios.
 * @author Del Myers
 *
 */
public class Cloud extends Canvas {
	@SuppressWarnings("unchecked")
	private static class TypedListener implements Listener {
		private int type;
		private Class clazz;
		private Method method;
		private Object listener;
		public TypedListener(int type, Class eventClass, Method eventMethod, Object listener) {
			this.type = type;
			this.clazz = eventClass;
			this.method = eventMethod;
			this.listener = listener;
		}
		public void handleEvent(Event event) {
			if (event.type == type) {
				try {
					Constructor c = clazz.getConstructor(event.getClass());
					Object eventObject = c.newInstance(event);
					method.invoke(listener, eventObject);
				} catch (SecurityException e) {
				} catch (NoSuchMethodException e) {
				} catch (IllegalArgumentException e) {
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
			}
		}
		
	}
	
	private class ItemLine {
		private int top;
		private int bottom;
		private final CloudItem[] line;
		public ItemLine(int top, int bottom, CloudItem[] line) {
			this.top = top;
			this.bottom = bottom;
			this.line = line;
		}
		
	}
	
	private LinkedList<CloudItem> items;
	private ItemLine[] lines;
	private int numPriorities = 10;
	private int[] priorityListing;
	private int itemId;
	private static final int TEXT_PADDING = 10;
	private Font[] itemFonts;
	private boolean dirty;
	private CloudItem[] selection;
	private HashMap<EventListener, List<TypedListener>> listeners;
	
	/**
	 * @param parent
	 * @param style
	 */
	public Cloud(Composite parent, int style) {
		super(parent, style);
		listeners = new HashMap<EventListener, List<TypedListener>>();
		this.items = new LinkedList<CloudItem>();
		addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent e) {
				Cloud.this.doPaintControl(e);
			}
		});
		addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				Cloud.this.widgetDisposed(e);				
			}
		});
		addControlListener(new ControlListener(){
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				markDirty();
				
			}
		});
		addMouseListener(new MouseListener(){
			public void mouseDoubleClick(MouseEvent e) {
				
			}
			public void mouseDown(MouseEvent e) {
				if (e.button != 1) return;
				CloudItem item = findItemAt(e.x, e.y);
				if (item == null) return;
				if (selection == null || selection.length == 0) {
					internalSetSelection(new CloudItem[]{item});
				}
				if (item != null) {
					if ((getStyle() & SWT.MULTI) == 0) {
						internalSetSelection(new CloudItem[]{item});
					} else {
						if ((e.stateMask & SWT.SHIFT) != 0) {
							CloudItem first = selection[0];
							CloudItem last = item;
							if (last != null) {
								int firstIndex = items.indexOf(first);
								int lastIndex = items.indexOf(last);
								if (firstIndex == -1 || lastIndex == -1) return;
								if (firstIndex == lastIndex) return;
								int incr = (firstIndex < lastIndex) ? 1 : -1;
								int index = 0;
								CloudItem[] newSelection = new CloudItem[Math.abs(lastIndex-firstIndex)+1];
								for (int i = firstIndex; i != lastIndex; i+= incr, index++) {
									newSelection[index] = items.get(i);
								}
								newSelection[0] = first;
								newSelection[index] = last;
								internalSetSelection(newSelection);
							}
						} else if ((e.stateMask & SWT.CTRL) != 0) {
							for (CloudItem ci : selection) {
								if (ci.equals(item)) {
									return;
								}
							}
							CloudItem[] newSelection = new CloudItem[selection.length+1];
							System.arraycopy(selection, 0, newSelection, 1, selection.length);
							newSelection[0] = item;
							internalSetSelection(newSelection);
							
						} else {
							internalSetSelection(new CloudItem[]{item});
						}
					}
				}
				
			}		
			
			public void mouseUp(MouseEvent e) {
			}
		});
		addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				if (selection == null || selection.length == 0) return;
				CloudItem current = selection[0];
				CloudItem next = null;
				switch (e.keyCode) {
				case SWT.ARROW_LEFT:
					next = leftOf(current, true);
					break;
				case SWT.ARROW_RIGHT:
					next = rightOf(current, true);
					break;
				case SWT.ARROW_DOWN:
					next = bottomOf(current, true);
					break;
				case SWT.ARROW_UP:
					next = topOf(current, true);
					break;
				}
				if (next != null) {
					internalSetSelection(new CloudItem[]{next});
				}
			}

			public void keyReleased(KeyEvent e) {
			}
			
		});
		addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				redraw();
			}

			public void focusLost(FocusEvent e) {
				redraw();
			}
		});
		priorityListing = new int[0];
		this.itemId = 0;
		itemFonts = new Font[0];
		setBackground(parent.getBackground());
		setForeground(parent.getForeground());
		setFont(parent.getFont());
	}
	
	

	/**
	 * @param e
	 */
	protected void widgetDisposed(DisposeEvent e) {
		CloudItem[] items = getItems();
		this.items.clear();
		for (CloudItem item : items) {
			if (!item.isDisposed()) {
				item.dispose();
			}
		}
		disposeFonts();
		
	}
	
	
	public CloudItem[] getItems() {
		CloudItem[] items = this.items.toArray(new CloudItem[this.items.size()]);
		return items;
	}

	private void disposeFonts() {
		for (Font font : itemFonts) {
			if (font != null && !font.isDisposed()) {
				font.dispose();
			}
		}
	}

	public CloudItem findItemAt(int x, int y) {
		int line = lineOf(y);
		if (line >= 0) {
			for (CloudItem item : lines[line].line) {
				if (item.getBounds().contains(x, y)) {
					return item;
				}
			}
		}
		return null;
	}


	protected void doPaintControl(PaintEvent e) {
		Rectangle bounds = getClientArea();
		if (dirty) {
			internalLayout();
			dirty = false;
		}
		e.gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width-2, bounds.height-2, TEXT_PADDING*2, TEXT_PADDING*2);
		Rectangle damage = new Rectangle(e.x, e.y, e.width, e.height);
		for (CloudItem item : items) {
			if (item.getBounds().intersects(damage)) {
				item.draw(e.gc);
			}
		}
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
	}

	/**
	 * @param cloudItem
	 * @param index
	 * @return
	 */
	int createItem(CloudItem cloudItem, int index) {
		if (index < 0 || index == items.size()) {
			items.add(cloudItem);
		} else {
			items.add(index, cloudItem);
		}
		dirty = true;
		if (selection == null || selection.length == 0) {
			internalSetSelection(new CloudItem[]{cloudItem});
		}
		return ++itemId;
	}
	
	/**
	 * @param widget
	 */
	void deleteItem(CloudItem cloudItem) {
		items.remove(cloudItem);
		ArrayList<CloudItem> newSelection = new ArrayList<CloudItem>(selection.length);
		for (CloudItem selected : selection) {
			if (!selected.equals(cloudItem)) {
				newSelection.add(selected);
			}
		}
		dirty = true;
		internalSetSelection(newSelection.toArray(new CloudItem[newSelection.size()]));
	}
	
	void markDirty() {
		dirty = true;
		redraw();
	}
	
	private void internalLayout() {
		refreshPriorities();
		layoutCloudItems();
		dirty = false;
	}
	
	
	/**
	 * 
	 */
	private void refreshPriorities() {
		TreeSet<Integer> prioritySet = new TreeSet<Integer>(new Comparator<Integer>(){
			public int compare(Integer o1, Integer o2) {
				//integers less <= 0 are considered equal
				if (o1 <= 0 && o2 <= 0) {
					return 0;
				}
				return o1-o2;
			}});
		for (CloudItem item : items) {
			prioritySet.add(item.getPriority());
		}
		this.priorityListing = new int[numPriorities];
		double modulus = Math.pow(prioritySet.size(), .2);
//		modulus = (double)prioritySet.size()/priorityListing.length;
		int index = 0;
		for (int p : prioritySet) {
			if (p <= 0) {
				p=0;
			}
			int i = (int) Math.round(Math.log10(index)/Math.log10(modulus));
//			int i = (int)(index * modulus);
			if (i < priorityListing.length) {
				priorityListing[i] = p;
			}
			index++;
		}
		if (priorityListing[numPriorities-1] == 0 && prioritySet.size() > 0) {
			priorityListing[numPriorities-1]=prioritySet.last();
		}
	}
	

	

	private void layoutCloudItems() {
		disposeFonts();
		Font[] baselineFonts = new Font[priorityListing.length];
		FontData baseData = getFont().getFontData()[0];
		for (int i = 0; i < baselineFonts.length; i++) {
			baseData.setHeight(10*(i+1));
			baselineFonts[i] = new Font(getDisplay(), baseData);
		}
		itemFonts = baselineFonts;
		
		Rectangle bounds = getClientArea();
		float aspectRatio = (float)bounds.width/(float)bounds.height;
		int minimalArea = 0;
		int minimalWidth = 0;
		for (CloudItem item : items) {
			resetFont(item);
			minimalArea += item.getTextSize().x * item.getTextSize().y;
			if (item.getTextSize().x*2/3 > minimalWidth) {
				minimalWidth = item.getTextSize().x*2/3;
			}
		}
		
		float square = (float) Math.sqrt(minimalArea);
		int availableWidth = bounds.width - TEXT_PADDING*2;
		int availableHeight = bounds.height - TEXT_PADDING*2;
		int w = (int)(square*aspectRatio);
		if (w < minimalWidth) {
			w = minimalWidth;
		}
		int xExtent = (int)(bounds.x + TEXT_PADDING + w);
		int x = bounds.x + TEXT_PADDING;
		int y = bounds.y + TEXT_PADDING;
		int lineHeight = 0;
		int width = 0;
		GC tempGC = new GC(this);
		LinkedList<CloudItem> currentLine = new LinkedList<CloudItem>();
		LinkedList<ItemLine> lines = new LinkedList<ItemLine>();
		for (CloudItem item : items) {
			if (item.getText() == null) {
				continue;
			}
			tempGC.setFont(item.getFont());
			Point textSize = item.getTextSize();
			if (textSize.x + x > xExtent) {
				for (CloudItem lineItem : currentLine) {
					Rectangle itemBounds = lineItem.getBounds();
					lineItem.setBounds(new Rectangle(itemBounds.x, itemBounds.y, itemBounds.width, lineHeight));
				}
				if (currentLine.size() > 0) {
					lines.add(new ItemLine(y, y+lineHeight, currentLine.toArray(new CloudItem[currentLine.size()])));
				}
				currentLine.clear();
				//go to the next line
				x = bounds.x + TEXT_PADDING;
				y += lineHeight;
				lineHeight = 0;
			} 
			item.setBounds(new Rectangle(x, y, textSize.x,0));
			int localWidth = x + textSize.x - bounds.x;
			if (localWidth > width) {
				width = localWidth;
			}
			if (textSize.y > lineHeight) {
				lineHeight = textSize.y;
			}
			x+= textSize.x;
			currentLine.add(item);
		}
		for (CloudItem lineItem : currentLine) {
			Rectangle itemBounds = lineItem.getBounds();
			lineItem.setBounds(new Rectangle(itemBounds.x, itemBounds.y, itemBounds.width, lineHeight));
		}
		if (currentLine.size() > 0) {
			lines.add(new ItemLine(y, y+lineHeight, currentLine.toArray(new CloudItem[currentLine.size()])));
		}
		this.lines = lines.toArray(new ItemLine[lines.size()]);
		tempGC.dispose();
		this.itemFonts = baselineFonts;
		
		float xScale = ((float)availableWidth)/(width);
		float yScale = ((float)availableHeight)/(y+lineHeight-bounds.y-TEXT_PADDING);
		float scale = xScale;
		if (yScale < xScale) {
			scale = yScale;
		}
		scale(scale);
	}
	
	/**
	 * @param scale
	 */
	private void scale(float scale) {
		disposeFonts();
		Font[] baselineFonts = new Font[priorityListing.length];
		FontData baseData = getFont().getFontData()[0];
		for (int i = 0; i < baselineFonts.length; i++) {
			baseData.setHeight((int)(scale*10*(i+1)));
			baselineFonts[i] = new Font(getDisplay(), baseData);
		}
		if (lines == null || lines.length <= 0) return;
		
		itemFonts = baselineFonts;
		int top = getClientArea().y + TEXT_PADDING;
		Rectangle areaBounds = getClientArea();
		for(ItemLine line: lines) {
			int lineHeight = 0;
			int x = getBounds().x + TEXT_PADDING;
			for (CloudItem item : line.line) {
				resetFont(item);
				Point size = item.getTextSize();
				Rectangle bounds = new Rectangle(x, top, size.x, size.y);
				if (size.y > lineHeight) {
					lineHeight = size.y;
				}
				item.setBounds(bounds);
				x += size.x;
			}
			line.top = top;
			line.bottom = top + lineHeight;
			top = line.bottom;
			//re-adjust the bounds based on the amount of space available
			//in order to get a justified look.
			int space = (areaBounds.x+areaBounds.width-TEXT_PADDING) - x;
			int padding = space/line.line.length;
			int padded = 0;
			for (CloudItem item : line.line) {
				Rectangle bounds = item.getBounds();
				bounds.width+=padding;
				bounds.x+= padded;
				bounds.height = lineHeight;
				item.setBounds(bounds);
				padded+=padding;
			}
		}
		
		//re-adjust the line heights to get a justified look.
		int space = areaBounds.x+areaBounds.height-TEXT_PADDING*2-lines[lines.length-1].bottom;
		int padding = space/lines.length;
		int padded = 0;
		for (ItemLine  line : lines) {
			for (CloudItem item : line.line) {
				Rectangle bounds = item.getBounds();
				bounds.y += padded;
				bounds.height+=padding;
				item.setBounds(bounds);
			}
			line.top+=padded;
			line.bottom+=padded+padding;
			padded+=padding;
		}
	}



	/**
	 * @param item
	 */
	private void resetFont(CloudItem item) {
		int index = 0;
		if (item.getPriority() > 0) {
			for (; index < priorityListing.length; index++) {
				if (priorityListing[index] > item.getPriority()) {
					if (index > 0)
						index--;
					break;
				} else if (priorityListing[index] == item.getPriority()) {
					break;
				}
			}
			if (index >= priorityListing.length) {
				index--;
			}
		}
		item.setFont(itemFonts[index]);
	}



	private void internalSetSelection(CloudItem[] cloudItems) {
		
		if (this.selection != null) {
			HashSet<CloudItem> oldSelection = new HashSet<CloudItem>(Arrays.asList(selection));
			oldSelection.removeAll(Arrays.asList(cloudItems));
			for (CloudItem item : oldSelection) {
				item.setSelection(false);
			}
		}
		HashSet<CloudItem> oldSelection = new HashSet<CloudItem>();
		if (this.selection != null) {
			oldSelection.addAll(Arrays.asList(this.selection));
		}
		this.selection = cloudItems;
		if (this.selection != null) {
			HashSet<CloudItem> newSelection = new HashSet<CloudItem>(Arrays.asList(this.selection));
			newSelection.removeAll(oldSelection);
			for (CloudItem item : newSelection) {
				item.setSelection(true);
				Event event = new Event();
				event.widget = this;
				event.item = item;
				event.index = items.indexOf(item);
				notifyListeners(SWT.Selection, event);
			}
		}
	}
	
	public void setSelection(CloudItem[] cloudItems) {
		internalSetSelection(cloudItems);
	}
	
	/**
	 * Returns the line that the given item is in, or -1 if it is not in this cloud.
	 * @param item the item to check.
	 * @return the line that the item is in.
	 */
	public int lineOf(CloudItem item) {
		if (lines == null) return -1;
		Rectangle itemBounds = item.getBounds();
		for (int i = 0; i < lines.length; i++) {
			ItemLine line = lines[i];
			if (line.top <= itemBounds.y) {
				if (line.bottom >= itemBounds.y + itemBounds.height) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Returns the line at the given y location.
	 * @param y the y location.
	 * @return the line or -1 if no line exists for the location.
	 */
	public int lineOf(int y) {
		for (int i = 0; i < lines.length; i++) {
			ItemLine line = lines[i];
			if (line.top <= y) {
				if (line.bottom >= y) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Returns the top y coordinate of the given line number, or -1 if there is no such line.
	 * @param line the line to check.
	 * @return the y position of the given line.
	 */
	public int getLinePosition(int line) {
		if (lines == null || line < 0 || line >= lines.length) {
			return -1;
		}
		return lines[line].top;
	}
	
	/**
	 * Returns the height of the line of the given number, or -1 if there is no such line.
	 * @param line
	 * @return
	 */
	public int getLineHeight(int line) {
		if (lines == null || line < 0 || line >= lines.length) {
			return -1;
		}
		return lines[line].bottom - lines[line].top;
	}
	
	/**
	 * Returns a point with the line location of the given item, or null if it can't be found.
	 * @param item
	 * @return
	 */
	Point getLineLocation(CloudItem item) {
		int line = lineOf(item);
		if (line < 0) return null;
		int i;
		for (i = 0; i < lines[line].line.length; i++) {
			if (lines[line].line[i].equals(item)) {
				return new Point(i, line);
			}
		}
		return null;
	}
	
	/**
	 * Returns the item to the "left" of the given item, if one exists. If "wrap" is true,
	 * when the beginning of the line is reached, then the right-most item on the line above
	 * (or on the bottom line, if the item given is on the top line) is returned. Null
	 * is returned if the given item can't be found, or if there is no item to the left
	 * when "wrap" is false.
	 * @param item item to the right of the desired item.
	 * @param wrap whether or not wrapping should be done.
	 * @return the item to the left.
	 */
	CloudItem leftOf(CloudItem item, boolean wrap) {
		Point location = getLineLocation(item);
		if (location == null) return null;
		int y = location.y;
		int x = location.x;
		if (x <= 0) {
			if (wrap) {
				if (y <= 0) {
					y = lines.length-1;
				} else {
					y--;
				}
				x = lines[y].line.length;
			} else {
				return null;
			}
		}
		if (y < lines.length && y >= 0) {
			if (x <= lines[y].line.length && x > 0) {
				return lines[y].line[x-1];
			}
		}
		return null;
	}
	
	/**
	 * Returns the item to the "right" of the given item, if one exists. If "wrap" is true,
	 * when the end of the line is reached, then the left-most item on the line below
	 * (or on the top line, if the item given is on the bottom line) is returned. Null
	 * is returned if the given item can't be found, or if there is no item to the right
	 * when "wrap" is false.
	 * @param item item to the left of the desired item.
	 * @param wrap whether or not wrapping should be done.
	 * @return the item to the right.
	 */
	CloudItem rightOf(CloudItem item, boolean wrap) {
		Point location = getLineLocation(item);
		if (location == null) return null;
		int y = location.y;
		int x = location.x;
		if (x >= lines[y].line.length-1) {
			if (wrap) {
				if (y >= lines.length-1) {
					y = 0;
				} else {
					y++;
				}
				x = -1;
			} else {
				return null;
			}
		}
		if (y < lines.length && y >= 0) {
			if (x <= lines[y].line.length && x >= -1) {
				return lines[y].line[x+1];
			}
		}
		return null;
	}



	/**
	 * Returns the item to the "top" of the given item, if one exists. If "wrap" is true,
	 * when the top line is reached, then the position is wrapped to the bottom of the
	 * cloud. Null is returned if the given item can't be found, or if there is no item above
	 * when "wrap" is false.
	 * @param item item to the left of the desired item.
	 * @param wrap whether or not wrapping should be done.
	 * @return the item to the right.
	 */
	protected CloudItem topOf(CloudItem item, boolean wrap) {
		int line = lineOf(item);
		if (line < 0) return null;
		if (line == 0) {
			if (wrap) line = lines.length;
			else return null;
		}
		line--;
		Rectangle bounds = item.getBounds();
		int lineLocation = getLinePosition(line);
		int lineHeight = getLineHeight(line);
		Point center = new Point(bounds.x + bounds.width/2, lineLocation+lineHeight/2);
		return findItemAt(center.x, center.y);
	}



	/**
	 * Returns the item to the "bottom" of the given item, if one exists. If "wrap" is true,
	 * when the bottom line is reached, then the position is wrapped to the top of the
	 * cloud. Null is returned if the given item can't be found, or if there is no underneath
	 * when "wrap" is false.
	 * @param item item to the left of the desired item.
	 * @param wrap whether or not wrapping should be done.
	 * @return the item to the right.
	 */
	protected CloudItem bottomOf(CloudItem item, boolean wrap) {
		int line = lineOf(item);
		if (line < 0) return null;
		if (line == lines.length-1) {
			if (wrap) line = -1;
			else return null;
		}
		line++;
		Rectangle bounds = item.getBounds();
		int lineLocation = getLinePosition(line);
		int lineHeight = getLineHeight(line);
		Point center = new Point(bounds.x + bounds.width/2, lineLocation+lineHeight/2);
		return findItemAt(center.x, center.y);
	}



	/**
	 * Returns the selection in the cloud. Changing the returned array has no effect on the current selection.
	 * @return the selection in the cloud.
	 */
	public CloudItem[] getSelection() {
		if (selection == null || selection.length == 0) {
			return new CloudItem[0];
		}
		CloudItem[] result = new CloudItem[selection.length];
		System.arraycopy(selection, 0, result, 0, selection.length);
		return result;
	}



	/**
	 * @param selectionListener
	 */
	public void addSelectionListener(SelectionListener selectionListener) {
		List<TypedListener> typedListeners = listeners.get(selectionListener);
		if (typedListeners == null) {
			try {
				typedListeners = new ArrayList<TypedListener>(2);
				listeners.put(selectionListener, typedListeners);
				Method method = SelectionListener.class.getDeclaredMethod("widgetSelected", SelectionEvent.class);
				TypedListener listener = new TypedListener(SWT.Selection, SelectionEvent.class, method, selectionListener);
				typedListeners.add(listener);
				addListener(listener.type, listener);
				method = SelectionListener.class.getDeclaredMethod("widgetDefaultSelected", SelectionEvent.class);
				listener = new TypedListener(SWT.DefaultSelection, SelectionEvent.class, method, selectionListener);
				typedListeners.add(listener);
				addListener(listener.type, listener);
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			}
		}
	}
	
	public void removeSelectionListener(SelectionListener selectionListener) {
		List<TypedListener> typedListeners = listeners.get(selectionListener);
		if (typedListeners != null) {
			for (TypedListener listener : typedListeners) {
				removeListener(listener.type, listener);
			}
			typedListeners.clear();
			listeners.remove(selectionListener);
		}
	}
	
	/**
	 * Returns the highest priority value of all of the items in the current view.
	 * @return the highest priority value of all of the items in the current view.
	 */
	public int getHighestPriority() {
		if (priorityListing == null) {
			refreshPriorities();
		}
		if (priorityListing.length == 0) {
			return 0;
		}
		return priorityListing[priorityListing.length-1];
	}



	/**
	 * Returns the index of the given item, enumerating from left-to-right, top-to-bottom.
	 * @param item the item to find. Or -1 if the position couldn't be found.
	 */
	public int getIndex(CloudItem item) {
		int line = lineOf(item);
		if (line < 0 || line >= lines.length) {
			return -1;
		}
		int index = 0;
		for (int currLine = 0; currLine < line; currLine++) {
			index+=lines[currLine].line.length;
		}
		for (int i = 0; i < lines[line].line.length; i++) {
			if (lines[line].line[i] == item) {
				break;
			}
		}
		return index;
	}
	
	/**
	 * Sets the number of distinct font sizes that can be used in this cloud. The more
	 * sizes there are, the more visible the difference between items of different priority.
	 * The default number is 10.
	 * @param numSizes the number of distinct font sizes that can be used in this cloud.
	 */
	public void setNumSizes(int numSizes) {
		checkWidget();
		this.numPriorities = numSizes;
		layout();
	}
	
	/**
	 * Returns the number of distinct font sizes that can be used in this cloud. The more
	 * sizes there are, the more visible the difference between items of different priority.
	 * The default number is 10.
	 * @return the number of distinct font sizes that can be used in this cloud.
	 */
	public int getNumSizes() {
		return this.numPriorities;
	}

}
