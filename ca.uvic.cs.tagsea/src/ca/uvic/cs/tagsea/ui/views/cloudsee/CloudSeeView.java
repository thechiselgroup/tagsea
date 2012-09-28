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
package ca.uvic.cs.tagsea.ui.views.cloudsee;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import ca.uvic.cs.tagsea.ITagseaImages;
import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Tag;


/**
 * This class contains methods and functionality for displaying the CloudSee visualization.
 * The CloudSee visualization is a hierarchical tag cloud, which can be used to navigate the
 * tag hierarchy.  A CloudElement's text size is a representation of that tag's number of waypoints.
 * Also, the color is a representation of how many children that tag has.
 * 
 * @author Sean Falconer
 */
public class CloudSeeView extends ViewPart implements PaintListener, MouseListener, ControlListener {
	public static final String ID = "ca.uvic.cs.tagsea.views.CloudSeeView";
	
	// total number of different font sizes used in the tag cloud
	private static final int FONT_DIVISIONS = 25;
	
	// starting x, y values for tag cloud elements
	private static final int START_X = 17;
	private static final int START_Y = 17;
	
	// border arc width and height
	private static final int BORDER_ARC_WIDTH = 10;
	private static final int BORDER_ARC_HEIGHT = 10;
	
	// minimum border width
	private static final int BORDER_MIN_WIDTH = 300;
	
	// buffer for line wrapping
	private static final int BORDER_BUFFER = 20;
	
	// x and y spacing for tag cloud
	private static final int X_BUFFER = 30;
	private static final int Y_BUFFER = 10;
	
	// UI components
	private Composite composite;
	private Canvas canvas;
	private ScrolledComposite scrolledComposite;
	
	// back buffer used for double buffering
	private Image backBuffer;
	
	// currently visible cloud elements
	private CloudElement[] cloudElements;
	
	// the previously viewed tag
	private Tag prevTag;
	
	// the currently selected cloud element
	private CloudElement selectedElement;
	
	// toolbar actions
	private Action forwardAction;
	private Action backAction;
	private Action refreshAction;
	
	// flag to say whether we loaded the cloud elements yet
	private boolean loaded = false;

	/**
	 * The constructor.
	 */
	public CloudSeeView() {}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		parent.addControlListener(this);
		
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, true);
		composite.setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.heightHint = 24;
		
		makeActions();
		makeToolBar(gridData);
				
		GridData scrollGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledComposite = new ScrolledComposite(composite, SWT.V_SCROLL | SWT.BORDER);
		scrolledComposite.setLayoutData(scrollGridData);
		
		canvas = new Canvas(scrolledComposite, SWT.NO_BACKGROUND);
		canvas.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		canvas.addPaintListener(this);
		canvas.addMouseListener(this);
		scrolledComposite.setContent(canvas);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		
		getSite().getWorkbenchWindow().getPartService().addPartListener(new IPartListener() {
			public void partClosed(IWorkbenchPart part) {
				//savePreferences();
	
				// remove this listener, it will be added when the part is created
				getSite().getWorkbenchWindow().getPartService().removePartListener(this);
			}
			public void partActivated(IWorkbenchPart part) {}
			public void partBroughtToTop(IWorkbenchPart part) {}
			public void partDeactivated(IWorkbenchPart part) {}
			public void partOpened(IWorkbenchPart part) {}
		});
	}
	
	public void refresh() {
		loaded = false;
		canvas.redraw();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {}
	
	public void paintControl(PaintEvent e) {
		doubleBufferPaint(e.gc);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlMoved(ControlEvent e){}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlResized(ControlEvent e) {
		canvas.redraw();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e) {
		attemptExpansion(e.x, e.y);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(MouseEvent e) {
		attemptSelect(e.x, e.y);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(MouseEvent e) {}
	
	private void makeToolBar(GridData gridData) {
		ToolBar toolBar = new ToolBar(composite, SWT.HORIZONTAL);
		toolBar.setLayoutData(gridData);
		
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		toolBarManager.add(refreshAction);
		toolBarManager.add(backAction);
		toolBarManager.add(forwardAction);
		toolBarManager.update(true);
		
		forwardAction.setEnabled(false);
		backAction.setEnabled(false);
	}
	
	private void makeActions() {
//		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
//		ImageDescriptor descriptorBack = sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK);
//		ImageDescriptor descriptorBackDisabled = sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED);
		ITagseaImages images = TagSEAPlugin.getDefault().getTagseaImages();
		ImageDescriptor descriptorBack = images.getDescriptor(ITagseaImages.IMG_UP_ARROW);
		ImageDescriptor descriptorBackDisabled = images.getDescriptor(ITagseaImages.IMG_UP_ARROW_DISABLED);

		backAction = new Action("Back", descriptorBack) {
			@Override
			public void run() {	
				takeBackAction();
			}
		};
		backAction.setDisabledImageDescriptor(descriptorBackDisabled);
		
		ImageDescriptor descriptorForward = images.getDescriptor(ITagseaImages.IMG_DOWN_ARROW);
		ImageDescriptor descriptorForwardDisabled = images.getDescriptor(ITagseaImages.IMG_DOWN_ARROW_DISABLED);

		forwardAction = new Action("Forward", descriptorForward) {
			@Override
			public void run() {	
				takeForwardAction();
			}
		};
		forwardAction.setDisabledImageDescriptor(descriptorForwardDisabled);
		
		refreshAction = new Action("Refresh", TagSEAPlugin.getImageDescriptor("icons/refresh_nav.gif")) {
			@Override
			public void run() {			
				loaded = false;
				canvas.redraw();
			}
		};
	}
	
	/**
	 * Expands the selected element if one exists.
	 * @param x The x coordinate of where the user clicked.
	 * @param y The y coordinate of where the user clicked.
	 */
	private void attemptExpansion(int x, int y) {
		CloudElement selectedElement = getSelected(x, y);
		if(selectedElement != null && selectedElement.getTag().getChildren().length > 0) {
			prevTag = selectedElement.getTag();
			loadCloudElements(selectedElement.getTag().getChildren());
			backAction.setEnabled(true);
			forwardAction.setEnabled(false);
		}
	}
	
	/**
	 * Attempts to select a cloud element.
	 * @param x The x coordinate of where the user clicked.
	 * @param y The y coordinate of where the user clicked.
	 */
	private void attemptSelect(int x, int y) {
		forwardAction.setEnabled(false);
		for(int i = 0; i < cloudElements.length; i++) {
			cloudElements[i].setSelected(false);
		}
		
		selectedElement = getSelected(x, y);
		if(selectedElement != null) {
			selectedElement.setSelected(true);
			
			if(selectedElement.getTag().getChildren().length > 0)
				forwardAction.setEnabled(true);
		}
		
		canvas.redraw();
	}
	
	private CloudElement getSelected(int x, int y) {
		for(int i = 0; i < cloudElements.length; i++) {
			if(cloudElements[i].contains(x, y)) {
				return cloudElements[i];
			}
		}
		return null;
	}
	
	private void takeBackAction() {
		if(prevTag != null) {
			loadCloudElements(prevTag.getParent().getChildren());
			prevTag = prevTag.getParent();
			selectedElement = null;
			
			// turn off back action at root
			if(prevTag.getName().length() == 0) backAction.setEnabled(false);
			
			canvas.redraw();
		}
	}
	
	private void takeForwardAction() {
		if(selectedElement != null) {
			Tag[] tags = selectedElement.getTag().getChildren();
			if(tags.length > 0) {
				loadCloudElements(selectedElement.getTag().getChildren());
				forwardAction.setEnabled(false);
				prevTag = selectedElement.getTag();
				
				backAction.setEnabled(true);
				
				canvas.redraw();
			}
		}
	}
	
	/**
	 * Loads the cloud element collection based on the root level tags.
	 */
	private void loadCloudElements(Tag[] tags) {
		cloudElements = new CloudElement[tags.length];
		
		int maxFrequency = Integer.MIN_VALUE;
		int maxChildren = Integer.MIN_VALUE;
		for(int i = 0; i < cloudElements.length; i++) {
			loaded = true;
			int frequency = tags[i].getAllWaypoints().length;
			int children = tags[i].getChildrenCount();
			cloudElements[i] = new CloudElement(tags[i], frequency, children);
			
			maxFrequency = Math.max(maxFrequency, frequency);
			maxChildren = Math.max(maxChildren, children);
		}
		
		double factor = (CloudSeeView.FONT_DIVISIONS - 1) / Math.log(maxFrequency+1);
		for(int i = 0; i < cloudElements.length; i++) 
			cloudElements[i].calculateFont(factor, maxChildren);
	}
	
	/**
	 * Draw view to the canvas.
	 * @param dest
	 */
	private void doubleBufferPaint(GC dest) {
		Point size = canvas.getSize();

		if(size.x <= 0 || size.y <= 0) return;

		if(backBuffer != null) {
			Rectangle r = backBuffer.getBounds();
			if (r.width != size.x || r.height != size.y) {
				backBuffer.dispose();
				backBuffer = null;
			}
		}
		
		backBuffer = new Image(canvas.getDisplay(), size.x, size.y);
		GC gc = new GC(backBuffer);

		try {
			gc.setBackground(canvas.getBackground());
			gc.fillRectangle(0, 0, size.x, size.y);
			doPaint(gc);
		} 
		finally {
			gc.dispose();
		}

		dest.drawImage(backBuffer, 0, 0);
	}
	
	private void doPaint(GC gc) {
		if(!loaded) {
			loadCloudElements(TagSEAPlugin.getDefault().getTagCollection().getRootTags());
		}
		
		int width = BORDER_MIN_WIDTH, height = 0;
		int x = START_X, y = START_Y;
		int maxRowHeight = 0;
		for(int i = 0; i < cloudElements.length; i++) {
			cloudElements[i].draw(gc, x, y);
			x = cloudElements[i].getX() + cloudElements[i].getWidth() + X_BUFFER;
			y = cloudElements[i].getY();
			maxRowHeight = Math.max(maxRowHeight, cloudElements[i].getHeight());
			
			width = Math.max(width, x);
			height = Math.max(height, y + cloudElements[i].getHeight());
			
			// wrap line if we exceed the canvas x value
			if(x >= canvas.getSize().x - BORDER_BUFFER) {
				x = START_X;
				y += maxRowHeight + Y_BUFFER;
				maxRowHeight = 0;
			}
		}
		
		if(cloudElements.length > 0)
			drawTagCloudBorder(gc, width, height);
	}
	
	private void drawTagCloudBorder(GC gc, int width, int height) {
		gc.setFont(new Font(null, "Arial", 12, SWT.NORMAL));
		gc.setForeground(new Color(null, 0, 0, 0));		
		gc.drawRoundRectangle(10, 13, width, height+12, BORDER_ARC_WIDTH, BORDER_ARC_HEIGHT);
		if ((prevTag != null) && (prevTag.getName() != null) && (prevTag.getName().length() > 0)) {
			gc.drawString(" " + prevTag.getName() + " ", 20, 0);
		}
	}
}