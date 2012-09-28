/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.cue.projector.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.ibm.research.tours.fx.ToursFxPlugin;

/**
 * uses win32 system hooks to intercept mouse and keyboard events.  singleton class.
 * use addListener and removeListener to apply custom listeners to intercept and override mouse and keyboard events.
 * 
 * warning: use this carefully.  this has the potential of locking all UI functionality in the computer.  in the worst case
 * scenario, you can use ctrl-alt-del to kill the application to restore control.
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */

@Deprecated
public class MouseKeyboardInterceptor {

	private static MouseKeyboardInterceptor instance = new MouseKeyboardInterceptor();
	private int 	 mouseHook = 0;
	private int 	 keyboardHook = 0;
	private List<InterceptorListener> listeners = new ArrayList<InterceptorListener>();
	
	/**
	 * internal constructor - only one instance available - use getInstance() to access it
	 */
	private MouseKeyboardInterceptor()
	{
		init();
	}
	
	/**
	 * utility routine to set up a system hook
	 * @param hookCode
	 * @param procName
	 * @return system hook code or 0 on failure
	 */
	private int createHook(int hookCode, String procName)
	{
		Callback callback = new Callback(this, procName, 3);
		int address = callback.getAddress();
		if ( address==0 )
			return 0;
		
		int hInstance = OS.GetModuleHandle (null);
		return OS.SetWindowsHookEx(hookCode, address, hInstance, 0);	
	}
	
	/**
	 * initializes interceptor.  you only need to call this if you called dispose() earlier and you want to restore the interceptor
	 */
	public void init() 
	{
		if ( mouseHook!=0 )
			return;
		
		mouseHook = createHook(14,"mouseFilterProc");	// 14 = WM_MOUSE_LL
		
		if ( keyboardHook!=0 )
			return;
		
		keyboardHook = createHook(13,"keyboardFilterProc");  // 13 = WM_KEYBOARD_LL
		
		if (mouseHook == 0) 
			throw new RuntimeException("unable to get mouse callback handle");
		if (keyboardHook == 0)
			throw new RuntimeException("unable to get keyboard callback handle");
	}
	
	/**
	 * disables interceptor.  must be called when you shutdown your application
	 */
	public void dispose()
	{
		if ( mouseHook != 0 )
			OS.UnhookWindowsHookEx(mouseHook);
		if ( keyboardHook != 0 )
			OS.UnhookWindowsHookEx(keyboardHook);
		
		mouseHook = 0;
		keyboardHook = 0;
	}
	
	/**
	 * returns true if dispose() was called
	 * @return boolean
	 */
	public boolean isDisposed()
	{
		return ( mouseHook==0 && keyboardHook==0 );
	}
	
	// keyboard system hook callback
	int keyboardFilterProc (int code, int wParam, int lParam) 
	{
		if ( code==0 )
		{
			int[] buffer = new int[4];
			OS.MoveMemory(buffer, lParam, 4*4);  // read first 4 words from KBDLLHOOKSTRUCT
			
			for (InterceptorListener listener : listeners)
			{
				// important to catch exceptions, because any failure can disrupt regular keyboard operation...
				try 
				{
					switch (wParam)
					{
						case OS.WM_KEYDOWN:
							if ( listener.KeyDown(buffer[0],buffer[1]) )
								return -1;
							break;
						case OS.WM_KEYUP:
							if ( listener.KeyUp(buffer[0],buffer[1]) )
								return -1;
							break;
						case OS.WM_SYSKEYDOWN:
							if ( listener.SystemKeyDown(buffer[0],buffer[1]) )
								return -1;
							break;
						case OS.WM_SYSKEYUP:
							if ( listener.SystemKeyUp(buffer[0],buffer[1]) )
								return -1;
							break;
					}
				} 
				catch (RuntimeException e) 
				{
					ToursFxPlugin.log("error while processing keyboard filter listener " + listener, e);
				}
			}
		}
		
		return OS.CallNextHookEx (keyboardHook, code, wParam, lParam);
	}
	
	// mouse system hook callback
	int mouseFilterProc(int code, int wParam, int lParam) 
	{
		if ( code==0 )
		{
			int[] buffer = new int[2];
			OS.MoveMemory(buffer, lParam, 4*2); // read first 2 words from MSLLHOOKSTRUCT
			
			for (InterceptorListener listener : listeners)
			{
				// important to catch exceptions, because any failure can disrupt regular mouse operation...
				
				try 
				{
					switch (wParam)
					{
						case OS.WM_LBUTTONDOWN:
							if ( listener.LeftButtonDown(buffer[0],buffer[1]) )
								return -1;
							break;
						case OS.WM_LBUTTONUP:
							if ( listener.LeftButtonUp(buffer[0],buffer[1]) )
								return -1;
							break;
						case OS.WM_RBUTTONDOWN:
							if ( listener.RightButtonDown(buffer[0],buffer[1]) )
								return -1;
							break;
						case OS.WM_RBUTTONUP:
							if ( listener.RightButtonUp(buffer[0],buffer[1]) )
								return -1;
							break;
						default:
							if ( listener.MouseMove(buffer[0],buffer[1]) )
								return -1;
							break;
					}
				} 
				catch (RuntimeException e) 
				{
					ToursFxPlugin.log("error while processing mouse filter listener " + listener, e);
				}
			}
		}
		
		return OS.CallNextHookEx (mouseHook, code, wParam, lParam);
	}

	/**
	 * retrieve instance of the interceptor
	 * @return MouseKeyboardInterceptor
	 */
	public static MouseKeyboardInterceptor getInstance()
	{
		return instance;
	}
	
	public void addListener(InterceptorListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeListener(InterceptorListener listener)
	{
		listeners.remove(listener);
	}
	
	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setText("MouseKeyboardInterceptor Test");
		shell.setLayout(new RowLayout(SWT.VERTICAL));
		
		Shell shell2 = new Shell(display);
		shell2.setSize(300, 300);
		
		final Label label = new Label(shell,SWT.LEFT);
		label.setText(" ---    current interceptor information will appear here  --- ");
		shell.getDisplay().addFilter(SWT.MouseMove, new Listener(){

			public void handleEvent(Event event) {
				Point displaypoint = ((Control)event.widget).toDisplay(new Point(event.x, event.y));
				label.setText("mouse move=" + displaypoint.x + "," + displaypoint.y + " on " + event.widget);
				event.doit = false;
				
				//return false;
			}});
		/*
		MouseKeyboardInterceptor interceptor = MouseKeyboardInterceptor.getInstance();
		
		InterceptorListener passiveListener = new InterceptorListener() {
			public boolean KeyDown(int keyCode, int scanCode) {
				label.setText("keydown keyCode=" + keyCode + " scanCode" + scanCode);
				return false;
			}

			public boolean KeyUp(int keyCode, int scanCode) {
				label.setText("keyup keyCode=" + keyCode + " scanCode" + scanCode);
				return false;
			}

			public boolean LeftButtonDown(int x, int y) {
				label.setText("left button down x,y=" + x + "," + y);
				return false;
			}

			public boolean LeftButtonUp(int x, int y) {
				label.setText("left button up x,y=" + x + "," + y);
				return false;
			}

			public boolean RightButtonDown(int x, int y) {
				label.setText("right button down x,y=" + x + "," + y);
				return false;
			}

			public boolean RightButtonUp(int x, int y) {
				label.setText("right button up x,y=" + x + "," + y);
				return false;
			}

			public boolean SystemKeyDown(int keyCode, int scanCode) {
				label.setText("systemkey down keyCode=" + keyCode + " scanCode" + scanCode);
				return false;
			}

			public boolean SystemKeyUp(int keyCode, int scanCode) {
				label.setText("systemkey up keyCode=" + keyCode + " scanCode" + scanCode);
				return false;
			}

			public boolean MouseMove(int x, int y) {
				label.setText("mouse move=" + x + "," + y);
				return false;
			}
		};
		interceptor.addListener(passiveListener);
		
		// kills keyboard until left mouse is clicked
		final InterceptorListener killKeyboardListener = new InterceptorAdapter() {

			boolean killKeyboard = true;
			
			public boolean KeyDown(int keyCode, int scanCode) {
				if ( killKeyboard )
					label.setText("keyboard control is locked until right mouse is clicked!");
				return killKeyboard;
			}

			public boolean KeyUp(int keyCode, int scanCode) {
				if ( killKeyboard )
					label.setText("keyboard control is locked until right mouse is clicked!");
				return killKeyboard;
			}

			public boolean RightButtonDown(int x, int y) {
				if ( killKeyboard )
					label.setText("restoring keyboard control");
				
				killKeyboard = false;
				return false;
			}
		};
		
		// kills left mouse until keyboard is touched 
		final InterceptorListener killMouseListener = new InterceptorAdapter() {
			
			boolean killMouse = true;

			public boolean KeyDown(int keyCode, int scanCode) {
				if ( killMouse )
					label.setText("restoring left button control");
				
				killMouse = false;
				return false;
			}

			public boolean LeftButtonDown(int x, int y) {
				if ( killMouse )
					label.setText("left mouse button control is locked until a key is pressed!");
				return killMouse;
			}

			public boolean LeftButtonUp(int x, int y) {
				if ( killMouse )
					label.setText("left mouse button control is locked until a key is pressed!");
				return killMouse;
			}
		};
		*/
		final String KILL_KEYBOARD_TEXT = "kill keyboard control",
		             KILL_MOUSE_TEXT    = "kill left button control",
		             DONE_TEXT          = "test done";
		
		Button demo = new Button(shell,SWT.PUSH);
		demo.setText(KILL_KEYBOARD_TEXT);
		demo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/*
				Button demo = (Button) e.widget;
				if ( demo.getText().equals(KILL_KEYBOARD_TEXT) )
				{
					MouseKeyboardInterceptor.getInstance().addListener(killKeyboardListener);
					demo.setText(KILL_MOUSE_TEXT);
				}
				else if ( demo.getText().equals(KILL_MOUSE_TEXT) )
				{
					MouseKeyboardInterceptor.getInstance().removeListener(killKeyboardListener);
					MouseKeyboardInterceptor.getInstance().addListener(killMouseListener);
					demo.setText(DONE_TEXT);
					demo.setEnabled(false);
				}
				*/
			}
		});

		
		shell.setSize(320,120);
		shell.open();
		shell2.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		//interceptor.dispose();
		display.dispose();
	}
}
