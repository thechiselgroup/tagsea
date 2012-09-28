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
package ca.uvic.cs.tagsea.ui.views.overview;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.corext.util.Strings;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaSourceViewer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * This view is very raw ans should be refactored into a more stable and generic
 * document view framework.
 * 
 * @author Mike
 *
 */
public class DocumentVisualizationView extends ViewPart implements PaintListener, 
																		IPartListener, 
																		IPageListener,
																		SelectionListener, 
																		IViewportListener, 
																		ControlListener, 
																		MouseListener, 
																		ITextListener
{
	private static int RENDER_OFFSET   = 2;
	private static int LINE_WIDTH   = 2;
	private static int LINE_GAP = 1;
	
	private Canvas fCanvas;
	private ScrolledComposite fscrolledComposite;
	private JavaSourceViewer fSourceViewer;
	private JavaEditor fEditor;
	//private IJavaElement fJavaElement;
	private IDocument fDocument;
	private StyledText fTextWidget;
	private IWorkbenchPage fWorkbenchPage;
	private Image fBuffer;
	private LineInfo[] fLineInfo;
	private MarkerInfo[] fMarkerInfo;
	private Viewport fViewport;

	/** The color used for painting lines which contain a tag */
	private Color fTagColor = new Color(Display.getCurrent(),new RGB(95,238,255));

	/**
	 * The constructor.
	 */
	public DocumentVisualizationView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) 
	{
		fscrolledComposite = new ScrolledComposite(parent,SWT.V_SCROLL);
		/* we will double buffer paint */
		fCanvas = new Canvas(fscrolledComposite,SWT.NO_BACKGROUND);
		fCanvas.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		fCanvas.addPaintListener(this);
		fCanvas.addMouseListener(this);
		fscrolledComposite.setContent(fCanvas);
		fscrolledComposite.setExpandVertical(true);
		fscrolledComposite.setExpandHorizontal(true);
		
		IWorkbenchWindow workbenchWindow = TagSEAPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		
		/* Activate the view */
		if(page == null)
			workbenchWindow.addPageListener(this);
		else
			pageActivated(page);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPageListener#pageActivated(org.eclipse.ui.IWorkbenchPage)
	 */
	public void pageActivated(IWorkbenchPage page) 
	{
		IEditorPart editorPart = page.getActiveEditor();
		
		if(editorPart instanceof JavaEditor)
		{
			connect((JavaEditor)editorPart);
			fWorkbenchPage = page;
			fWorkbenchPage.addPartListener(this);
			refreshContent();
			refreshViewport();
			fCanvas.redraw();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPageListener#pageClosed(org.eclipse.ui.IWorkbenchPage)
	 */
	public void pageClosed(IWorkbenchPage page) 
	{
		if(page == fWorkbenchPage)
		{
			fWorkbenchPage = null;
			fWorkbenchPage.removePartListener(this);
			disconnect();
			fCanvas.redraw();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPageListener#pageOpened(org.eclipse.ui.IWorkbenchPage)
	 */
	public void pageOpened(IWorkbenchPage page){}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partActivated(IWorkbenchPart part) 
	{
		if(part instanceof IEditorPart)
		{
			IEditorPart editorPart = (IEditorPart)part;
			
			if(editorPart instanceof JavaEditor)
			{
				connect((JavaEditor)editorPart);
				refreshContent();
				refreshViewport();
				fCanvas.redraw();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partClosed(IWorkbenchPart part) 
	{
		if(part == fEditor)
			disconnect();
		
		if(fCanvas != null && !fCanvas.isDisposed())
			fCanvas.redraw();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partBroughtToTop(IWorkbenchPart part){}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partDeactivated(IWorkbenchPart part){}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partOpened(IWorkbenchPart part){}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl(PaintEvent e)
	{
		doubleBufferPaint(e.gc);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextListener#textChanged(org.eclipse.jface.text.TextEvent)
	 */
	public void textChanged(TextEvent event)
	{
		refreshContent();
		refreshViewport();
		fCanvas.redraw();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e){}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) 
	{
		// The user has scrolled the target editor
		refreshViewport();
		fCanvas.redraw();
		// center the view port for scroll events
		correctScroll(true);
	}
	
	/**
	 * Correct the scroll position
	 */
	private void correctScroll(boolean centerViewPort) 
	{
		// Corrrect the scroll bar
		int height = fCanvas.getSize().y;
		Rectangle area = fscrolledComposite.getClientArea();
		int topY = RENDER_OFFSET + fViewport.topLineIndex*LINE_WIDTH + fViewport.topLineIndex*LINE_GAP;
		int bottomY = RENDER_OFFSET + fViewport.bottomLineIndex*LINE_WIDTH + fViewport.bottomLineIndex*LINE_GAP;
		int viewportHeight = bottomY - topY;
		int originY = topY - area.height/2 + viewportHeight/2; 
		
		Point origin = fscrolledComposite.getOrigin();
		Rectangle visibleAreaOfCanvas = new Rectangle(origin.x,origin.y,area.width,area.height);
		boolean resetOrigin = false;
		
		// if the height of the canvas is less than the client area dont scroll - ovbiously -
		// if the target scroll point isnt outside of the visible area of the canvas dont scroll
		// Giggidy!
		if(height > area.height)
			if(centerViewPort)
				resetOrigin = true;
			else
				if(!visibleAreaOfCanvas.contains(0,topY))
					resetOrigin = true;
		
		if(resetOrigin)
			fscrolledComposite.setOrigin(0, originY);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.IViewportListener#viewportChanged(int)
	 */
	public void viewportChanged(int verticalOffset)
	{
		refreshViewport();
		fCanvas.redraw();
		// dont center the view port for viewport change events
		correctScroll(false);
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
	public void controlResized(ControlEvent e) 
	{
		// Refresh the viewport when the control is resized
		refreshViewport();
		fCanvas.redraw();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e){}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(MouseEvent e)
	{
		if(fLineInfo == null)
			return;
		
		for(int i = 0; i < fLineInfo.length; i ++)
		{
			LineInfo info = fLineInfo[i];
			Rectangle bounds = info.bounds;
			
			if(bounds == null)
				continue;
						
			if(bounds.contains(e.x,e.y))
			{
				try 
				{
					//translate this widget line back to the model line
					int modelLine = fSourceViewer.widgetLine2ModelLine(i);
					// Get this lines region
					IRegion region = fDocument.getLineInformation(modelLine);
					fEditor.setHighlightRange(region.getOffset(), region.getLength(), true);
				} 
				catch (BadLocationException ble) 
				{
					ble.printStackTrace();
				}			
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(MouseEvent e){}
	
	/**
	 * Connect to an editor instance
	 * @param editor
	 */
	private void connect(JavaEditor editor)
	{
		fEditor = editor;
		//fJavaElement = fEditor.getEditorInput()
		
		fSourceViewer = (JavaSourceViewer)fEditor.getViewer();
		fSourceViewer.addViewportListener(this);
		fSourceViewer.addTextListener(this);

		fTextWidget = fSourceViewer.getTextWidget();
		fTextWidget.getVerticalBar().addSelectionListener(this);
		fTextWidget.addControlListener(this);
		
		fDocument = fSourceViewer.getDocument();
		fViewport = new Viewport();
	}
	
	/**
	 * 	Clear the currently connected editor instance
	 */
	private void disconnect() 
	{
		fEditor = null;
		
		fSourceViewer.removeViewportListener(this);
		fSourceViewer = null;
		
		fTextWidget.getVerticalBar().removeSelectionListener(this);
		fTextWidget.removeControlListener(this);		
		fTextWidget = null;

		fDocument =null;
		fLineInfo = null;
		fViewport = null;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus(){}
	
	/**
	 * Refresh the viewport
	 */
	private void refreshViewport()
	{
		if(fEditor == null)
			return;
		
		calculateViewPort();
	}

	/**
	 * Calculate the viewport
	 * 
	 */
	private void calculateViewPort() 
	{
		fViewport.topLineIndex = fTextWidget.getTopIndex();
		fViewport.bottomLineIndex = getBottomIndex(fTextWidget);
	}
	
	/**
	 * 
	 * Refresh the view content
	 */
	private void refreshContent() 
	{
		if(fEditor == null)
			return;
	
		// we will work from the text widget content
		StyledTextContent content = fSourceViewer.getTextWidget().getContent();
		int numberOfLines = fSourceViewer.getTextWidget().getLineCount();
		fLineInfo = new LineInfo[numberOfLines];

		for(int i = 0; i < fLineInfo.length; i++)
		{
			// get the line
			String line = content.getLine(i);

			/**
			 * @tag todo : use the indent width from the java project
			 */
			int indentWidth = 4;//CodeFormatterUtil.getIndentWidth(fJavaElement.getJavaProject());
			int indentLength = Strings.measureIndentLength(line, indentWidth);
			
			line = line.trim();

			fLineInfo[i] = new LineInfo();
			fLineInfo[i].start = indentLength;
			fLineInfo[i].length = line.length();
		}

		/*  Get the tag markers from the resource */
		IFileEditorInput input = (IFileEditorInput)fEditor.getEditorInput();
		IFile file = input.getFile();
		
		try 
		{
			IMarker[] markers = file.findMarkers(TagSEAPlugin.MARKER_ID,false,IResource.DEPTH_INFINITE);
			fMarkerInfo = new MarkerInfo[markers.length];
			
			for(int i = 0; i < fMarkerInfo.length; i ++)
			{
				if(markers[i].exists())
				{
					int startLine  = markers[i].getAttribute(IMarker.LINE_NUMBER, 0);
					fMarkerInfo[i] = new MarkerInfo();
					// translate the marker line to its widget line counterpart, -1 as the markers are 1 indexed, documents 0
					fMarkerInfo[i].startLine = fSourceViewer.modelLine2WidgetLine(startLine - 1);
				}
			}
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}

		// Set the height of the composite for the scroll bars
		fscrolledComposite.setMinHeight(computeHeight());
	}

	/* Compute the height of the canvas */
	private int computeHeight() 
	{
		LineInfo[] lineInfo = fLineInfo;
		int numberOfLines = lineInfo.length;
		return RENDER_OFFSET + numberOfLines*LINE_WIDTH + numberOfLines*LINE_GAP;
	}

	/* Paint the view */
	public void doPaint(GC gc) 
	{		
		Rectangle area = fCanvas.getClientArea(); 
		int y = RENDER_OFFSET;
		int width  = area.width;
		Color foregroundColor;
		gc.setLineWidth(LINE_WIDTH);
				
		if(fLineInfo != null && fLineInfo.length > 0)
		{
			int numberOfLines = fLineInfo.length;
			int longestLine = 0;
		
			/* compute the longest line */
			for(int line = 0; line < numberOfLines; line++)
			{
				LineInfo currentLine = fLineInfo[line];
				int length = currentLine.start + currentLine.length;
				
				if(length > longestLine)
					longestLine = length;
			}

			/* Compute the scale fraction based on the longest line, @tag todo : externalize the fraction [Author = Mike;Date = 5/12/06 10:58 PM;]*/
			double wScale = ((double)(width - (width/8))) / ((double)longestLine);
			
			// store the foreground color, we may change it
			foregroundColor = gc.getForeground();
			
			// render the lines
			for(int line = 0; line < numberOfLines; line++)
			{
				LineInfo currentLine = fLineInfo[line];
			
				if(currentLine.bounds == null)
					currentLine.bounds = new Rectangle(0,0,0,0);

				// change line color if we hit a marker
				for(MarkerInfo info : fMarkerInfo)
					if(line == info.startLine)
						gc.setForeground(fTagColor);
				
				// only draw the line if it has content
				if(currentLine.length > 0)
				{
					double x1 = ((double)currentLine.start) * wScale;
					double x2 = ((double)currentLine.start + currentLine.length)* wScale;
				
					gc.drawLine((int)x1,y,(int)x2,y);
				}
			
				// cache the line bounds for mouse hit testing
				currentLine.bounds.y = y;
				currentLine.bounds.height = LINE_WIDTH + LINE_GAP;
				currentLine.bounds.x = 0;
				currentLine.bounds.width = width;
			
				gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			
				if(line == fViewport.topLineIndex || line == fViewport.bottomLineIndex)
					gc.drawLine(0,y,width,y);
				
				// reset the foregroud color
				gc.setForeground(foregroundColor);
				
				// compute the new y pixel offset
				y += gc.getLineWidth() + LINE_GAP;
			}
		}
	}

	/**
	 * Returns the last fully visible line of the widget. The exact semantics of "last fully visible
	 * line" are:
	 * <ul>
	 * <li>the last line of which the last pixel is visible, if any
	 * <li>otherwise, the only line that is partially visible
	 * </ul>
	 * 
	 * @param widget the widget
	 * @return the last fully visible line
	 */
	public static int getBottomIndex(StyledText widget)
	{
		int caHeight= widget.getClientArea().height;
		int lastPixel= caHeight - 1;
		
		// bottom is in [0 .. lineCount - 1]
		int bottom= widget.getLineIndex(lastPixel);

		// bottom is the first line - no more checking
		if (bottom == 0)
			return bottom;
		
		int pixel= widget.getLinePixel(bottom);
		// bottom starts on or before the client area start - bottom is the only visible line
		if (pixel <= 0)
			return bottom;
		
		int offset= widget.getOffsetAtLine(bottom);
		int height= widget.getLineHeight(offset);
		
		// bottom is not showing entirely - use the previous line
		if (pixel + height - 1 > lastPixel)
			return bottom - 1;
		
		// bottom is fully visible and its last line is exactly the last pixel
		return bottom;
	}
	
	/**
	 * Double buffer drawing.
	 *
	 * @param dest the GC to draw into
	 */
	private void doubleBufferPaint(GC dest)
	{
		Point size= fCanvas.getSize();

		if (size.x <= 0 || size.y <= 0)
			return;

		if (fBuffer != null) {
			Rectangle r= fBuffer.getBounds();
			if (r.width != size.x || r.height != size.y) {
				fBuffer.dispose();
				fBuffer= null;
			}
		}
		if (fBuffer == null)
			fBuffer= new Image(fCanvas.getDisplay(), size.x, size.y);

		GC gc= new GC(fBuffer);

		try {
			gc.setBackground(fCanvas.getBackground());
			gc.fillRectangle(0, 0, size.x, size.y);
			doPaint(gc);
		} finally {
			gc.dispose();
		}

		dest.drawImage(fBuffer, 0, 0);
	}

	@Override
	public void dispose() 
	{
		super.dispose();
		
		if(fBuffer!=null && !fBuffer.isDisposed())
			fBuffer.dispose();
		
		fBuffer = null;	
		
		if(fTagColor!=null && !fTagColor.isDisposed())
		{
			fTagColor.dispose();
		}
		fTagColor = null;
	}
}