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
package ca.uvic.cs.tagsea.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ca.uvic.cs.tagsea.TagSEAPlugin;


/**
 * Base composite class for the composites in the TagsView: tags, waypoints, routes.
 * Defines some useful methods, and has the default font and color.
 * 
 * @author Chris Callendar
 */
public class BaseTagsViewComposite extends Composite implements DisposeListener {

	//private Font headerFont = null;	 	// do need to dispose since we are making a new font
	private Color headerColor = null; 	// don't need to dispose, it will be when the display is disposed
	
	public BaseTagsViewComposite(Composite parent, int style) {
		super(parent, style);
		
		parent.addDisposeListener(this);
	}
	
	public void widgetDisposed(DisposeEvent e) {
		// disposes the header font.
//		if ((headerFont != null) && !headerFont.isDisposed()) {
//			headerFont.dispose();
//		}
	}

	/**
	 * @tag bug(177) : use regular system font for composite
	 * @tag font
	 * @return
	 */
	protected Font getHeaderLabelFont() 
	{		
		return  Display.getDefault().getSystemFont();
		
//		if (headerFont == null) 
//		{
//			headerFont = Display.getDefault().getSystemFont();
//			
//			// now adjust the size and style
//			FontData[] fds = headerFont.getFontData();
//			if ((fds != null) && (fds.length >= 1)) {
//				for (FontData data : fds) {
//					data.setHeight(11);
//					data.setStyle(SWT.BOLD);
//				}
//			}
//			headerFont = new Font(headerFont.getDevice(), fds);
//		}
//		return headerFont;
	}
	
	protected Color getHeaderLabelColor() {
		if (headerColor == null) {
			//headerColor = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
			headerColor = TagSEAPlugin.getDefault().getColorRegistry().get(TagSEAPlugin.COLOR_TEXT_KEY);
		}
		return headerColor;
	}
	

	/**
	 * Creates a down arrow toolbar button which shows a menu containing the given actions.
	 * @param toolbar the toolbar to add a ToolItem menu to
	 * @param text the text for the ToolItem (also the tooltip)
	 * @param img the image for the ToolItem (can be null);
	 * @param menu the menu to show when the ToolItem is clicked
	 * @param showMenuOnButtonClick if true, when the main button is clicked the menu will show
	 * @return ToolItem the toolbar item which when clicked shows the menu.
	 */
	protected ToolItem createDropDownToolBarMenu(final ToolBar toolbar, String text, Image img, final Menu menu, final boolean showMenuOnButtonClick) {
		toolbar.setMenu(menu);
		final ToolItem item = new ToolItem (toolbar, SWT.DROP_DOWN);
		if (text != null) {
			item.setText(text);
			item.setToolTipText(text);
		}
		if (img != null) {
			item.setImage(img);
		}
		item.addListener (SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// if the drop down arrow is clicked the detail will be SWT.ARROW
				// if showMenuOnButtonClick is true, then we always show the menu
				if (showMenuOnButtonClick || (event.detail == SWT.ARROW)) {
					Rectangle rect = item.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = toolbar.toDisplay(pt);
					menu.setLocation(pt.x, pt.y);
					menu.setVisible(true);
				}
			}
		});
		return item;
	}
}