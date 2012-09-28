/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation
 *******************************************************************************/
package ca.uvic.cs.tagsea.ui.views.overview;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaSourceViewer;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * @author mdesmond
 */
public class DocumentVisualizationView2 extends ViewPart implements SelectionListener,
																	IViewportListener,
																	ControlListener,
																	IPartListener
																	
{
	private IResourceChangeListener fResourceChangeListener;
	private ScrolledComposite fscrolledComposite;
	private JavaSourceViewer fJavaSourceViewer;
	private JavaSourceViewer fMiniJavaSourceViewer; 
	private JavaEditor fEditor;
	private Viewport fViewport;
	private Font fFont; 
	private IFile fFile;
	
	/** The color used for painting lines which contain a tag */
	private Color fTagColor = new Color(Display.getCurrent(),new RGB(95,238,255));

	/** The color used for painting lines which contain a tag */
	private Color fViewportColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);

	/**
	 * The constructor.
	 */
	public DocumentVisualizationView2(){}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) 
	{
		fscrolledComposite = new ScrolledComposite(parent,SWT.V_SCROLL);
		IPreferenceStore store = JavaPlugin.getDefault().getCombinedPreferenceStore();
		fMiniJavaSourceViewer = new JavaSourceViewer(fscrolledComposite, null, null, false, SWT.NONE,store);
		JavaSourceViewerConfiguration config = new JavaSourceViewerConfiguration(JavaPlugin.getDefault().getJavaTextTools().getColorManager(),store, null,IJavaPartitions.JAVA_PARTITIONING);
		fMiniJavaSourceViewer.configure(config);
		
		fscrolledComposite.setContent(fMiniJavaSourceViewer.getTextWidget());
		fscrolledComposite.setExpandVertical(true);
		fscrolledComposite.setExpandHorizontal(true);
		
		Font defaultFont = Display.getCurrent().getSystemFont();
		
		// now adjust the size and style
		FontData[] fds = defaultFont.getFontData();
		
		if ((fds != null) && (fds.length >= 1)) 
		{
			for (FontData data : fds) 
				data.setHeight(1);
		}
		
		fFont = new Font(Display.getCurrent(),fds);
		
		fMiniJavaSourceViewer.getTextWidget().setFont(fFont);
		fMiniJavaSourceViewer.getTextWidget().setEditable(false);
		fMiniJavaSourceViewer.getTextWidget().setCaret(null);
		fMiniJavaSourceViewer.getTextWidget().setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
		
		fMiniJavaSourceViewer.getTextWidget().addMouseListener(new MouseListener() 
		{
			public void mouseUp(MouseEvent e) {}
		
			public void mouseDown(MouseEvent e) 
			{
				DocumentVisualizationView2.this.mouseDown(e);
			}
			public void mouseDoubleClick(MouseEvent e) {}
		});
		
		fResourceChangeListener = new IResourceChangeListener() 
		{
			public void resourceChanged(IResourceChangeEvent event) 
			{
				DocumentVisualizationView2.this.resourceChanged(event);
			}
		};
		
		IWorkbenchWindow workbenchWindow = TagSEAPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		
		if(page == null)
			workbenchWindow.addPageListener(new IPageListener() 
			{
				public void pageOpened(IWorkbenchPage page)
				{
					if(page.getActiveEditor() instanceof JavaEditor)
						connect((JavaEditor)page.getActiveEditor());
				
					page.addPartListener(DocumentVisualizationView2.this);		
				}
			
				public void pageClosed(IWorkbenchPage page) {}
				public void pageActivated(IWorkbenchPage page){}
			});
		else
		{
			if(page.getActiveEditor() instanceof JavaEditor)
				connect((JavaEditor)page.getActiveEditor());
		
			page.addPartListener(this);	
		}
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partActivated(IWorkbenchPart part) 
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partDeactivated(IWorkbenchPart part)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partClosed(IWorkbenchPart part) 
	{
		//	disconnect
		if(part instanceof JavaEditor)
			if(fEditor == part)
				disconnect();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partBroughtToTop(IWorkbenchPart part)
	{	
		// reconnect
		if(part instanceof JavaEditor)
			if(fEditor != part)
			{	
				disconnect();
				connect((JavaEditor)part);
			}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partOpened(IWorkbenchPart part)
	{
		// connect
		if(part instanceof JavaEditor)
		{
			disconnect();
			connect((JavaEditor)part);
		}
	}
	
	public void widgetDefaultSelected(SelectionEvent e){}
	
	public void widgetSelected(SelectionEvent e) 
	{
		// The user has scrolled the target editor
		refreshViewport();
		// center the view port for scroll events
		correctScroll(true);
	}
	
	/**
	 * Correct the scroll position
	 */
	private void correctScroll(boolean centerViewPort) 
	{
		// Corrrect the scroll bar
		int height = fMiniJavaSourceViewer.getTextWidget().getSize().y;
		Rectangle area = fscrolledComposite.getClientArea();
		int topY = fMiniJavaSourceViewer.getTextWidget().getLinePixel(fViewport.topLineIndex);
		int bottomY = fMiniJavaSourceViewer.getTextWidget().getLinePixel(fViewport.bottomLineIndex);
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
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(MouseEvent e)
	{
		if (fEditor == null) return;
		try 
		{
			int line = fMiniJavaSourceViewer.getTextWidget().getLineIndex(e.y);
			//translate this widget line back to the model line
			int modelLine = fMiniJavaSourceViewer.widgetLine2ModelLine(line);
			// Get the lines region
			IRegion region = fMiniJavaSourceViewer.getDocument().getLineInformation(modelLine);
			fEditor.selectAndReveal(region.getOffset(), region.getLength());
		}
		catch (BadLocationException ble) 
		{
			ble.printStackTrace();
		}
	}
	
	/**
	 * Connect to an editor instance
	 * @param editor
	 */
	private void connect(JavaEditor editor)
	{
		fEditor = editor;
		fJavaSourceViewer = (JavaSourceViewer)fEditor.getViewer();
		fJavaSourceViewer.addViewportListener(this);

		fJavaSourceViewer.getTextWidget().getVerticalBar().addSelectionListener(this);
		fJavaSourceViewer.getTextWidget().addControlListener(this);
		fMiniJavaSourceViewer.setInput(fJavaSourceViewer.getDocument());
		fscrolledComposite.setMinHeight(computeHeight());
		fViewport = new Viewport();
		
		IEditorInput input = editor.getEditorInput();
		
		if(input instanceof IFileEditorInput)
			fFile = ((IFileEditorInput)input).getFile();
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceChangeListener);		
		refreshViewport();
	}
	
	protected void resourceChanged(IResourceChangeEvent event) 
	{
		if(event.getType() == IResourceChangeEvent.POST_CHANGE)
		{
			IResourceDelta delta = event.getDelta();
			
			try 
			{
				delta.accept(new IResourceDeltaVisitor() 
				{
					public boolean visit(IResourceDelta delta) throws CoreException 
					{
						if(delta.getResource().equals(fFile))
						{
							DocumentVisualizationView2.this.refreshMarkers();
							return false;
						}
						return true;
					}
				});
			} 
			catch (CoreException e) 
			{
			}
		}
	}
	
	protected void refreshMarkers() 
	{
		try 
		{
			IMarker[] markers = fFile.findMarkers(TagSEAPlugin.MARKER_ID, false,IResource.DEPTH_ZERO);
		
			for(IMarker marker : markers)
			{
				
				
			}
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Refresh the viewport
	 * 
	 * TODO : Seperate the viewport and the expansion refresh for more control
	 */
	private void refreshViewport()
	{
		if(fEditor == null)
			return;
		
		clearViewport();
		
		int topWidgetLine = fJavaSourceViewer.getTextWidget().getTopIndex();
		int bottomWidgetLine = getBottomIndex(fJavaSourceViewer.getTextWidget());

		fViewport.topLineIndex = fJavaSourceViewer.widgetLine2ModelLine(topWidgetLine);
		fViewport.bottomLineIndex = fJavaSourceViewer.widgetLine2ModelLine(bottomWidgetLine);

		fMiniJavaSourceViewer.getTextWidget().setLineBackground(fViewport.topLineIndex, 1, fViewportColor);
		fMiniJavaSourceViewer.getTextWidget().setLineBackground(fViewport.bottomLineIndex, 1, fViewportColor);
		
	}
	
	/**
	 * Refresh the viewport
	 */
	private void clearViewport()
	{
		if(fEditor == null)
			return;
		
		int numLines = fMiniJavaSourceViewer.getTextWidget().getLineCount();
		
		// Exceptionally simple yet horribly innefficient way of clearing the viewport
		for(int i = 0; i < numLines; i++)
		{
			if(fMiniJavaSourceViewer.getTextWidget().getLineBackground(i) == fViewportColor)
				fMiniJavaSourceViewer.getTextWidget().setLineBackground(i,1,null);
		}
	}
	
	/**
	 * 	Clear the currently connected editor instance
	 */
	private void disconnect() 
	{
		if(fEditor == null)
			return;

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceChangeListener);
		fFile = null;		
		fEditor = null;
		fJavaSourceViewer.removeViewportListener(this);
		fJavaSourceViewer.getTextWidget().getVerticalBar().removeSelectionListener(this);
		fJavaSourceViewer.getTextWidget().removeControlListener(this);
		fMiniJavaSourceViewer.setInput(new Document());
		fJavaSourceViewer = null;
		fViewport = null;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus(){}
	
	/* Compute the height of the canvas */
	private int computeHeight() 
	{
		return fMiniJavaSourceViewer.getTextWidget().getLineCount() * fMiniJavaSourceViewer.getTextWidget().getLineHeight();
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
	private static int getBottomIndex(StyledText widget)
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
	

	@Override
	public void dispose() 
	{
		super.dispose();

		if(fFont!=null && !fFont.isDisposed())
			fFont.dispose();
		
		fFont = null;
		
		if(fTagColor!=null && !fTagColor.isDisposed())
		{
			fTagColor.dispose();
		}
		
		fTagColor = null;
		IWorkbenchWindow workbenchWindow = TagSEAPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		if (page != null) {
			page.removePartListener(this);
		} else if (fEditor != null) {
			fEditor.getSite().getPage().removePartListener(this);
		}
		disconnect();
	}
}