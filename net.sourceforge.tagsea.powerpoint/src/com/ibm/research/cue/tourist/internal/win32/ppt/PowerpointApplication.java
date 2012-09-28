/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/

package com.ibm.research.cue.tourist.internal.win32.ppt;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.ibm.research.cue.tourist.internal.win32.ppt.api.DocumentWindow;
import com.ibm.research.cue.tourist.internal.win32.ppt.api.MsoTriState;
import com.ibm.research.cue.tourist.internal.win32.ppt.api.PpSlideShowRangeType;
import com.ibm.research.cue.tourist.internal.win32.ppt.api.PpSlideShowType;
import com.ibm.research.cue.tourist.internal.win32.ppt.api.PpWindowState;
import com.ibm.research.cue.tourist.internal.win32.ppt.api.Presentations;
import com.ibm.research.cue.tourist.internal.win32.ppt.api.SlideShowSettings;
import com.ibm.research.cue.tourist.internal.win32.ppt.api.SlideShowWindows;
import com.ibm.research.cue.tourist.internal.win32.ppt.api.Slides;
import com.ibm.research.cue.tourist.internal.win32.ppt.api.View;
import com.ibm.research.cue.tourist.internal.win32.ppt.api._Application;
import com.ibm.research.cue.tourist.internal.win32.ppt.api._Presentation;
import com.ibm.research.cue.tourist.internal.win32.ppt.api._Slide;

/**
 * front-end to manipulating powerpoint from java.  note that powerpoint is minimized by default.  call show() to reveal it
 * 
 * @tag to-fix powerpoint : currently, powerpoint can only be floating, not embedded if we want to access all of the functionality
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research, 2006
 */
public class PowerpointApplication {

	private OleFrame 		frame;
	private OleClientSite 	clientSite;
	private _Application 	application;
	
	private Menu 			menubar;

	/**
	 * internal initialization of OLE stuff, UI, and dispose handlers
	 * @param parent
	 * @param style
	 */
	private void init(Composite parent, int style)
	{
		frame 		= new OleFrame(parent,style);
		
		//@tag to-fix powerpoint : using "Powerpoint.Slide" will let us embed, but DOES NOT give access to _Application API
		clientSite  = new OleClientSite(frame,SWT.NONE,"Powerpoint.Application");  

		OleAutomation auto = new OleAutomation(clientSite);
		
		application = new _Application(auto);
		
		frame.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				// clean up all OLE automation stuff
				if ( application!=null )
					application.dispose();
			}
		});
		
		// @tag powerpoint menu : SWT.BAR style bit enables access to powerpoint menu bar
		if ( (style & SWT.BAR)!=0 )
		{
			menubar = frame.getShell().getMenuBar(); 
			if ( menubar==null )
			{
				menubar = new Menu(frame.getShell(),SWT.BAR); 
				frame.getShell().setMenuBar(menubar); 
			}
		}
		
		// minimize powerpoint by default
		application.setWindowState(new Variant(PpWindowState.ppWindowMinimized));
		
		// powerpoint must be visible in order to manipulate it 
		application.setVisible(new Variant(MsoTriState.msoTrue));
	}
	
	/**
	 * constructor - sets up powerpoint, associated with given composite
	 * 
	 * @param parent
	 * @param style   : SWT.NONE or SWT.BAR (enables access to powerpoint menu bar)
	 */
	public PowerpointApplication(Composite parent, int style)
	{
		init(parent,style);
	}
	
	/**
	 * reveal powerpoint UI in normal state
	 */
	public void show()
	{
		application.setWindowState(new Variant(PpWindowState.ppWindowNormal));
	}
	
	/**
	 * kill powerpoint application
	 */
	public void quit()
	{
		application.Quit();
	}

	/**
	 * maxmimize powerpoint UI
	 */
	public void maximize()
	{
		application.setWindowState(new Variant(PpWindowState.ppWindowMaximized));
	}

	/**
	 * minimize powerpoint UI
	 */
	public void minimize()
	{
		application.setWindowState(new Variant(PpWindowState.ppWindowMinimized));
	}

	/**
	 * retrieves powerpoint menu bar if SWT.BAR was set as style bit in constructor
	 * @return Menu
	 */
	public Menu getMenuBar()
	{
		return menubar;
	}

	/**
	 * retrieves OleFrame - need this to place menu bar changes back into powerpoint window (see main() for example)
	 * @return OleFrame
	 */
	private OleFrame getOleFrame() {
		return frame;
	}
	
	
	/**
	 * checks if the active presentation as specified.  useful for preventing multiple "read-only" copies of the same presentation from opening.
	 * 
	 * @tag powerpoint tours todo : limitation - only works for active presentation.  need to make this work for multiple different presentations
	 * 
	 * @param filename
	 * @return boolean
	 */
	public boolean isActive(String filename)
	{
		if ( application.getActivePresentation()==null )
			return false;
		
		_Presentation activePresentation = new _Presentation(application.getActivePresentation().getAutomation());
		Variant fullName = activePresentation.getFullName();
		boolean result = (fullName==null)?false:fullName.getString().equals(filename);
		activePresentation.dispose();
		
		return result;
	}	
	
	/**
	 * loads a powerpoint presentation
	 * @param filename
	 */
	public void open(String filename)
	{
		Presentations presentations = new Presentations(application.getPresentations().getAutomation());
		presentations.Open(filename);
		presentations.dispose();
	}
	
	/**
	 * saves currently active powerpoint presentation
	 * @param filename
	 */
	public void save(String filename)
	{
		_Presentation activePresentation = new _Presentation(application.getActivePresentation().getAutomation());
		activePresentation.SaveAs(filename);
		activePresentation.dispose();
	}
	
	/**
	 * navigates powerpoint to specified slide in slide editor (not in slide show mode)
	 * @param n - slide #
	 */
	public void gotoEditorSlide(int n)
	{
		DocumentWindow 		activeWindow = new DocumentWindow(application.getActiveWindow().getAutomation());
		View				view		 = new View(activeWindow.getView().getAutomation());
		
		view.GotoSlide(n);
		
		view.dispose();
		activeWindow.dispose();
	}
	
	/**
	 * runs a powerpoint slide show - this method will block until the show is finished
	 * 
	 * @param startingSlide
	 * @param endingSlide
	 */
	public void runSlideShow(int startingSlide, int endingSlide)
	{
		_Presentation	  activePresentation = new _Presentation(application.getActivePresentation().getAutomation());
		SlideShowSettings settings		     = new SlideShowSettings(activePresentation.getSlideShowSettings().getAutomation());
		SlideShowWindows  slideShowWindows   = new SlideShowWindows(application.getSlideShowWindows().getAutomation());
		
		settings.setShowType(new Variant(PpSlideShowType.ppShowTypeSpeaker));
		settings.setRangeType(new Variant(PpSlideShowRangeType.ppShowSlideRange));
		settings.setStartingSlide(new Variant(startingSlide));
		settings.setEndingSlide(new Variant(endingSlide));
		
		settings.Run();

		// wait until the show is over
		int count = 0;
		do
		{
			Variant vCount = slideShowWindows.getCount();
			
			if ( vCount==null )
				break;
			
			count = vCount.getInt();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		} while ( count>=1 );

		slideShowWindows.dispose();
		settings.dispose();
		activePresentation.dispose();
	}
	
	/**
	 * runs a powerpoint slide show - this method will block until the show is finished
	 */
	public void runSlideShow()
	{
		_Presentation	  activePresentation = new _Presentation(application.getActivePresentation().getAutomation());
		SlideShowSettings settings		     = new SlideShowSettings(activePresentation.getSlideShowSettings().getAutomation());
		SlideShowWindows  slideShowWindows   = new SlideShowWindows(application.getSlideShowWindows().getAutomation());
		
		settings.setShowType(new Variant(PpSlideShowType.ppShowTypeSpeaker));
		settings.setRangeType(new Variant(PpSlideShowRangeType.ppShowAll));
		
		settings.Run();

		// wait until the show is over
		int count = 0;
		do
		{
			Variant vCount = slideShowWindows.getCount();
			
			if ( vCount==null )
				break;
			
			count = vCount.getInt();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		} while ( count>=1 );

		slideShowWindows.dispose();
		settings.dispose();
		activePresentation.dispose();
	}

	/**
	 * returns slide # of the slide currently in the powerpoint editor
	 * @return slide number or -1 on failure
	 */
	public int getCurrentEditorSlide() 
	{
		int n = -1;
		DocumentWindow 		activeWindow = new DocumentWindow(application.getActiveWindow().getAutomation());
		View				view		 = new View(activeWindow.getView().getAutomation());
		
		Variant vSlide = view.getSlide();
		if ( vSlide!=null )
		{
			_Slide slide = new _Slide(vSlide.getAutomation());
			
			Variant vSlideNum = slide.getSlideNumber();
			n = vSlideNum.getInt();
			
			slide.dispose();
		}
		
		view.dispose();
		activeWindow.dispose();
		return n;
	}

	/**
	 * returns number of slides in the active presentation loaded in the powerpoint editor
	 * @return slide number of -1 on failure
	 */
	public int getCount() 
	{
		int n = -1;
		_Presentation activePresentation = new _Presentation(application.getActivePresentation().getAutomation());
		Slides slides = new Slides(activePresentation.getSlides().getAutomation());
		
		Variant vCount = slides.getCount();
		n = vCount.getInt();
		
		slides.dispose();
		activePresentation.dispose();
		return n;
	}
	
	/**
	 * test program
	 * @param args
	 */
	public static void main(String[] args) {
		// example of PowerpointFrame in action
		
		final Display	 			display = new Display();
		final Shell   				shell   = new Shell(display);
		final PowerpointApplication ppt 	= new PowerpointApplication(shell,SWT.BAR);

		shell.setText("powerpoint application test program");
		
		Menu	 bar   = ppt.getMenuBar();
		
		MenuItem fileBarItem = new MenuItem(bar,SWT.CASCADE);
		fileBarItem.setText("Test");
		
		Menu fileMenu 	  = new Menu(fileBarItem);
		fileBarItem.setMenu(fileMenu);
		
		MenuItem openItem = new MenuItem(fileMenu,SWT.CASCADE);
		openItem.setText("&Open");
		openItem.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						FileDialog openDialog = new FileDialog(shell,SWT.OPEN);
						String filename = openDialog.open();
						if ( filename!=null )
						{
							ppt.open(filename);
							ppt.show();
						}
					}
				}
		);
		
		MenuItem saveItem = new MenuItem(fileMenu,SWT.CASCADE);
		saveItem.setText("&Save");
		saveItem.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					FileDialog saveDialog = new FileDialog(shell,SWT.SAVE);
					String filename = saveDialog.open();
					if ( filename!=null )
						ppt.save(filename);
				}
			}
		);
		
		MenuItem allItem = new MenuItem(fileMenu,SWT.CASCADE);
		allItem.setText("Show all slides");
		allItem.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) 
				{
					ppt.runSlideShow();
				}
			}
		);
		
		MenuItem gotoItem = new MenuItem(fileMenu,SWT.CASCADE);
		gotoItem.setText("Show slide x");
		gotoItem.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) 
				{
					InputDialog dialog = new InputDialog(shell,"show slide","enter a slide #","1",null);
					if ( dialog.open() == Window.OK )
					{
						String value = dialog.getValue();
						int slideNum = Integer.parseInt(value);
						ppt.runSlideShow(slideNum, slideNum);
					}
				}
			}
		);

		MenuItem editorItem = new MenuItem(fileMenu,SWT.CASCADE);
		editorItem.setText("Goto slide x in editor");
		editorItem.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) 
				{
					InputDialog dialog = new InputDialog(shell,"goto slide in editor","enter a slide #","1",null);
					if ( dialog.open() == Window.OK )
					{
						String value = dialog.getValue();
						int slideNum = Integer.parseInt(value);
						ppt.gotoEditorSlide(slideNum);
					}
				}
			}
		);

		MenuItem slidenumItem = new MenuItem(fileMenu,SWT.CASCADE);
		slidenumItem.setText("Get current slide # in editor");
		slidenumItem.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) 
				{
					int n = ppt.getCurrentEditorSlide();
					MessageDialog.openInformation(shell, "current slide #", "current slide in editor = " + n);
				}
			}
		);

		MenuItem quitItem = new MenuItem(fileMenu,SWT.CASCADE);
		quitItem.setText("kill powerpoint");
		quitItem.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) 
				{
					ppt.quit();
				}
			}
		);
		
		OleFrame frame = ppt.getOleFrame();
		frame.setFileMenus(new MenuItem[] {fileBarItem} );
		
		shell.setLayout( new FillLayout() );
		shell.open();
		
		while ( !shell.isDisposed() )
		{
			if ( !display.readAndDispatch() ) display.sleep();
		}
	}

}
