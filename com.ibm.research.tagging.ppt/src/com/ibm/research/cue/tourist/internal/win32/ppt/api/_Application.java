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

package com.ibm.research.cue.tourist.internal.win32.ppt.api;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;

/**
 * generated from Powerpoint.Application._Application IDL
 * 
    [
      odl,
      uuid(91493442-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x0007a8f0),
      dual,
      nonextensible,
      oleautomation
    ]
    
 * @author Li-Te Cheng
 * IBM Research, 2006
 */


public class _Application 
{
	private OleAutomation auto;
	
	public _Application (OleAutomation automation)
	{
	  auto = automation;
	}
	
	public OleAutomation getAutomation()
	{
	  return auto;
	}
	
	public void dispose()
	{
	  if ( auto!=null ) auto.dispose();
	}
	
	/*
	 * [id(0x000007d1), propget, helpcontext(0x0007a8f1)] HRESULT
	 * Presentations([out, retval] Presentations** Presentations)
	 */
	public Variant getPresentations()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
	 * [id(0x000007d2), propget, helpcontext(0x0007a8f2)] HRESULT Windows([out,
	 * retval] DocumentWindows** Windows)
	 */
	public Variant getWindows()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
	 * [id(0x000007d3), propget, hidden, helpcontext(0x0007a8f3)] HRESULT
	 * Dialogs([out, retval] IUnknown** Dialogs)
	 */
	public Variant getDialogs()
	{
	  return auto.getProperty(0x000007d3);
	}
	
	/*
	 * [id(0x000007d4), propget, helpcontext(0x0007a8f4)] HRESULT ActiveWindow([out,
	 * retval] DocumentWindow** ActiveWindow)
	 */
	public Variant getActiveWindow()
	{
	  return auto.getProperty(0x000007d4);
	}
	
	/*
	 * [id(0x000007d5), propget, helpcontext(0x0007a8f5)] HRESULT
	 * ActivePresentation([out, retval] Presentation** ActivePresentation)
	 */
	public Variant getActivePresentation()
	{
	  return auto.getProperty(0x000007d5);
	}
	
	/*
	 * [id(0x000007d6), propget, helpcontext(0x0007a8f6)] HRESULT
	 * SlideShowWindows([out, retval] SlideShowWindows** SlideShowWindows)
	 */
	public Variant getSlideShowWindows()
	{
	  return auto.getProperty(0x000007d6);
	}
	
	/*
	 * [id(0x000007d7), propget, helpcontext(0x0007a8f7)] HRESULT CommandBars([out,
	 * retval] CommandBars** CommandBars)
	 */
	public Variant getCommandBars()
	{
	  return auto.getProperty(0x000007d7);
	}
	
	/*
	 * [id(0x000007d8), propget, helpcontext(0x0007a8f8)] HRESULT Path([out, retval]
	 * BSTR* Path)
	 */
	public Variant getPath()
	{
	  return auto.getProperty(0x000007d8);
	}
	
	/*
	 * [id(00000000), propget, helpcontext(0x0007a8f9)] HRESULT Name([out, retval]
	 * BSTR* Name)
	 */
	public Variant getName()
	{
	  return auto.getProperty(00000000);
	}
	
	/*
	 * [id(0x000007d9), propget, helpcontext(0x0007a8fa)] HRESULT Caption([out,
	 * retval] BSTR* Caption)
	 */
	public Variant getCaption()
	{
	  return auto.getProperty(0x000007d9);
	}
	
	/*
	 * [id(0x000007d9), propput, helpcontext(0x0007a8fa)] HRESULT Caption([in] BSTR
	 * Caption)
	 */
	public boolean setCaption(Variant val)
	{
	  return auto.setProperty(0x000007d9,val);
	}
	
	/*
	 * [id(0x000007da), propget, helpcontext(0x0007a8fb)] HRESULT Assistant([out,
	 * retval] Assistant** Assistant)
	 */
	public Variant getAssistant()
	{
	  return auto.getProperty(0x000007da);
	}
	
	/*
	 * [id(0x000007db), propget, helpcontext(0x0007a8fc)] HRESULT FileSearch([out,
	 * retval] FileSearch** FileSearch)
	 */
	public Variant getFileSearch()
	{
	  return auto.getProperty(0x000007db);
	}
	
	/*
	 * [id(0x000007dc), propget, helpcontext(0x0007a8fd)] HRESULT FileFind([out,
	 * retval] IFind** FileFind)
	 */
	public Variant getFileFind()
	{
	  return auto.getProperty(0x000007dc);
	}
	
	/*
	 * [id(0x000007dd), propget, helpcontext(0x0007a8fe)] HRESULT Build([out,
	 * retval] BSTR* Build)
	 */
	public Variant getBuild()
	{
	  return auto.getProperty(0x000007dd);
	}
	
	/*
	 * [id(0x000007de), propget, helpcontext(0x0007a8ff)] HRESULT Version([out,
	 * retval] BSTR* Version)
	 */
	public Variant getVersion()
	{
	  return auto.getProperty(0x000007de);
	}
	
	/*
	 * [id(0x000007df), propget, helpcontext(0x0007a900)] HRESULT
	 * OperatingSystem([out, retval] BSTR* OperatingSystem)
	 */
	public Variant getOperatingSystem()
	{
	  return auto.getProperty(0x000007df);
	}
	
	/*
	 * [id(0x000007e0), propget, helpcontext(0x0007a901)] HRESULT
	 * ActivePrinter([out, retval] BSTR* ActivePrinter)
	 */
	public Variant getActivePrinter()
	{
	  return auto.getProperty(0x000007e0);
	}
	
	/*
	 * [id(0x000007e1), propget, helpcontext(0x0007a902)] HRESULT Creator([out,
	 * retval] long* Creator)
	 */
	public Variant getCreator()
	{
	  return auto.getProperty(0x000007e1);
	}
	
	/*
	 * [id(0x000007e2), propget, helpcontext(0x0007a903)] HRESULT AddIns([out,
	 * retval] AddIns** AddIns)
	 */
	public Variant getAddIns()
	{
	  return auto.getProperty(0x000007e2);
	}
	
	/*
	 * [id(0x000007e3), propget, helpcontext(0x0007a904)] HRESULT VBE([out, retval]
	 * VBE** VBE)
	 */
	public Variant getVBE()
	{
	  return auto.getProperty(0x000007e3);
	}
	
	/*
	 * [id(0x000007e4), helpcontext(0x0007a905)] HRESULT Help( [in, optional,
	 * defaultvalue("vbapp10.chm")] BSTR HelpFile, [in, optional, defaultvalue(0)]
	 * int ContextID)
	 */
	public Variant Help()
	{
	  int id = 0x000007e4;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x000007e5), helpcontext(0x0007a906)] HRESULT Quit()
	 */
	public Variant Quit()
	{
	  int id = 0x000007e5;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x000007e6), vararg, helpcontext(0x0007a907)] HRESULT Run( [in] BSTR
	 * MacroName, [in] SAFEARRAY(VARIANT)* safeArrayOfParams, [out, retval] VARIANT*
	 * Run)
	 */
	
	// unsupported
	
	//	public int Run()
	//	{
	//	  int id = 0x000007e6;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in] BSTR MacroName, [in] SAFEARRAY(VARIANT
	//		 */
	//	}
	
	/*
	 * [id(0x000007e7), hidden, helpcontext(0x0007a908)] HRESULT PPFileDialog( [in]
	 * PpFileDialogType Type, [out, retval] IUnknown** PPFileDialog)
	 */
	
	// unsupported
	
	//	public int PPFileDialog()
	//	{
	//	  int id = 0x000007e7;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in] PpFileDialogType Type, [out, retval] IUnknown** PPFileDialog
	//		 */
	//	}
	
	/*
	 * [id(0x000007e8), restricted, helpcontext(0x0007a909)] HRESULT
	 * LaunchSpelling([in] DocumentWindow* pWindow)
	 */
	// LaunchSpelling is restricted access. no java code access will be provided
	
	/*
	 * [id(0x000007e9), propget, helpcontext(0x0007a90a)] HRESULT Left([out, retval]
	 * single* Left)
	 */
	public Variant getLeft()
	{
	  return auto.getProperty(0x000007e9);
	}
	
	/*
	 * [id(0x000007e9), propput, helpcontext(0x0007a90a)] HRESULT Left([in] single
	 * Left)
	 */
	public boolean setLeft(Variant val)
	{
	  return auto.setProperty(0x000007e9,val);
	}
	
	/*
	 * [id(0x000007ea), propget, helpcontext(0x0007a90b)] HRESULT Top([out, retval]
	 * single* Top)
	 */
	public Variant getTop()
	{
	  return auto.getProperty(0x000007ea);
	}
	
	/*
	 * [id(0x000007ea), propput, helpcontext(0x0007a90b)] HRESULT Top([in] single
	 * Top)
	 */
	public boolean setTop(Variant val)
	{
	  return auto.setProperty(0x000007ea,val);
	}
	
	/*
	 * [id(0x000007eb), propget, helpcontext(0x0007a90c)] HRESULT Width([out,
	 * retval] single* Width)
	 */
	public Variant getWidth()
	{
	  return auto.getProperty(0x000007eb);
	}
	
	/*
	 * [id(0x000007eb), propput, helpcontext(0x0007a90c)] HRESULT Width([in] single
	 * Width)
	 */
	public boolean setWidth(Variant val)
	{
	  return auto.setProperty(0x000007eb,val);
	}
	
	/*
	 * [id(0x000007ec), propget, helpcontext(0x0007a90d)] HRESULT Height([out,
	 * retval] single* Height)
	 */
	public Variant getHeight()
	{
	  return auto.getProperty(0x000007ec);
	}
	
	/*
	 * [id(0x000007ec), propput, helpcontext(0x0007a90d)] HRESULT Height([in] single
	 * Height)
	 */
	public boolean setHeight(Variant val)
	{
	  return auto.setProperty(0x000007ec,val);
	}
	
	/*
	 * [id(0x000007ed), propget, helpcontext(0x0007a90e)] HRESULT WindowState([out,
	 * retval] PpWindowState* WindowState)
	 */
	public Variant getWindowState()
	{
	  return auto.getProperty(0x000007ed);
	}
	
	/*
	 * [id(0x000007ed), propput, helpcontext(0x0007a90e)] HRESULT WindowState([in]
	 * PpWindowState WindowState)
	 */
	public boolean setWindowState(Variant val)
	{
	  return auto.setProperty(0x000007ed,val);
	}
	
	/*
	 * [id(0x000007ee), propget, helpcontext(0x0007a90f)] HRESULT Visible([out,
	 * retval] MsoTriState* Visible)
	 */
	public Variant getVisible()
	{
	  return auto.getProperty(0x000007ee);
	}
	
	/*
	 * [id(0x000007ee), propput, helpcontext(0x0007a90f)] HRESULT Visible([in]
	 * MsoTriState Visible)
	 */
	public boolean setVisible(Variant val)
	{
	  return auto.setProperty(0x000007ee,val);
	}
	
	/*
	 * [id(0x000007ef), propget, restricted, helpcontext(0x0007a910)] HRESULT
	 * HWND([out, retval] long* HWND)
	 */
	// HWND is restricted access. no java code access will be provided
	
	/*
	 * [id(0x000007f0), propget, helpcontext(0x0007a911)] HRESULT Active([out,
	 * retval] MsoTriState* Active)
	 */
	public Variant getActive()
	{
	  return auto.getProperty(0x000007f0);
	}
	
	/*
	 * [id(0x000007f1), helpcontext(0x0007a912)] HRESULT Activate()
	 */
	
	// unsupported

	//	public int Activate()
	//	{
	//	  int id = 0x000007f1;
	//	  /*
	//		 * args=
	//		 * 
	//		 */
	//	}
	
	/*
	 * [id(0x000007f2), propget, helpcontext(0x0007a913)] HRESULT AnswerWizard([out,
	 * retval] AnswerWizard** AnswerWizard)
	 */
	public Variant getAnswerWizard()
	{
	  return auto.getProperty(0x000007f2);
	}
	
	/*
	 * [id(0x000007f3), propget, helpcontext(0x0007a914)] HRESULT COMAddIns([out,
	 * retval] COMAddIns** COMAddIns)
	 */
	public Variant getCOMAddIns()
	{
	  return auto.getProperty(0x000007f3);
	}
	
	/*
	 * [id(0x000007f4), propget, helpcontext(0x0007a915)] HRESULT ProductCode([out,
	 * retval] BSTR* ProductCode)
	 */
	public Variant getProductCode()
	{
	  return auto.getProperty(0x000007f4);
	}
	
	/*
	 * [id(0x000007f5), propget, helpcontext(0x0007a916)] HRESULT
	 * DefaultWebOptions([out, retval] DefaultWebOptions** DefaultWebOptions)
	 */
	public Variant getDefaultWebOptions()
	{
	  return auto.getProperty(0x000007f5);
	}
	
	/*
	 * [id(0x000007f6), propget, helpcontext(0x0007a917)] HRESULT
	 * LanguageSettings([out, retval] LanguageSettings** LanguageSettings)
	 */
	public Variant getLanguageSettings()
	{
	  return auto.getProperty(0x000007f6);
	}
	
	/*
	 * [id(0x000007f7), propget, hidden, helpcontext(0x0007a918)] HRESULT
	 * MsoDebugOptions([out, retval] MsoDebugOptions** MsoDebugOptions)
	 */
	public Variant getMsoDebugOptions()
	{
	  return auto.getProperty(0x000007f7);
	}
	
	/*
	 * [id(0x000007f8), propget, helpcontext(0x0007a919)] HRESULT
	 * ShowWindowsInTaskbar([out, retval] MsoTriState* ShowWindowsInTaskbar)
	 */
	public Variant getShowWindowsInTaskbar()
	{
	  return auto.getProperty(0x000007f8);
	}
	
	/*
	 * [id(0x000007f8), propput, helpcontext(0x0007a919)] HRESULT
	 * ShowWindowsInTaskbar([in] MsoTriState ShowWindowsInTaskbar)
	 */
	public boolean setShowWindowsInTaskbar(Variant val)
	{
	  return auto.setProperty(0x000007f8,val);
	}
	
	/*
	 * [id(0x000007f9), propget, hidden, helpcontext(0x0007a91a)] HRESULT
	 * Marker([out, retval] IUnknown** Marker)
	 */
	public Variant getMarker()
	{
	  return auto.getProperty(0x000007f9);
	}
	
	/*
	 * [id(0x000007fa), propget, helpcontext(0x0007a91b)] HRESULT
	 * FeatureInstall([out, retval] MsoFeatureInstall* FeatureInstall)
	 */
	public Variant getFeatureInstall()
	{
	  return auto.getProperty(0x000007fa);
	}
	
	/*
	 * [id(0x000007fa), propput, helpcontext(0x0007a91b)] HRESULT
	 * FeatureInstall([in] MsoFeatureInstall FeatureInstall)
	 */
	public boolean setFeatureInstall(Variant val)
	{
	  return auto.setProperty(0x000007fa,val);
	}
	
	/*
	 * [id(0x000007fb), hidden, helpcontext(0x0007a91c)] HRESULT GetOptionFlag( [in]
	 * long Option, [in, optional, defaultvalue(0)] VARIANT_BOOL Persist, [out,
	 * retval] VARIANT_BOOL* GetOptionFlag)
	 */
	
	// unsupported
	
	//	public int GetOptionFlag()
	//	{
	//	  int id = 0x000007fb;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in] long Option, [in, optional, defaultvalue(0
	//		 */
	//	}
	
	/*
	 * [id(0x000007fc), hidden, helpcontext(0x0007a91d)] HRESULT SetOptionFlag( [in]
	 * long Option, [in] VARIANT_BOOL State, [in, optional, defaultvalue(0)]
	 * VARIANT_BOOL Persist)
	 */
	
	// unsupported
	
	//	public int SetOptionFlag()
	//	{
	//	  int id = 0x000007fc;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in] long Option, [in] VARIANT_BOOL State, [in, optional, defaultvalue(0
	//		 */
	//	}
	
	/*
	 * [id(0x000007fd), propget, helpcontext(0x0007a91e)] HRESULT FileDialog( [in]
	 * MsoFileDialogType Type, [out, retval] FileDialog** FileDialog)
	 */
	public Variant getFileDialog()
	{
	  return auto.getProperty(0x000007fd);
	}
	
	/*
	 * [id(0x000007fe), propget, helpcontext(0x0007a91f)] HRESULT
	 * DisplayGridLines([out, retval] MsoTriState* DisplayGridLines)
	 */
	public Variant getDisplayGridLines()
	{
	  return auto.getProperty(0x000007fe);
	}
	
	/*
	 * [id(0x000007fe), propput, helpcontext(0x0007a91f)] HRESULT
	 * DisplayGridLines([in] MsoTriState DisplayGridLines)
	 */
	public boolean setDisplayGridLines(Variant val)
	{
	  return auto.setProperty(0x000007fe,val);
	}
	
	/*
	 * [id(0x000007ff), propget, helpcontext(0x0007a920)] HRESULT
	 * AutomationSecurity([out, retval] MsoAutomationSecurity* AutomationSecurity)
	 */
	public Variant getAutomationSecurity()
	{
	  return auto.getProperty(0x000007ff);
	}
	
	/*
	 * [id(0x000007ff), propput, helpcontext(0x0007a920)] HRESULT
	 * AutomationSecurity([in] MsoAutomationSecurity AutomationSecurity)
	 */
	public boolean setAutomationSecurity(Variant val)
	{
	  return auto.setProperty(0x000007ff,val);
	}
	
	/*
	 * [id(0x00000800), propget, helpcontext(0x0007a921)] HRESULT
	 * NewPresentation([out, retval] NewFile** NewPresentation)
	 */
	public Variant getNewPresentation()
	{
	  return auto.getProperty(0x00000800);
	}
	
	/*
	 * [id(0x00000801), propget, helpcontext(0x0007a922)] HRESULT
	 * DisplayAlerts([out, retval] PpAlertLevel* DisplayAlerts)
	 */
	public Variant getDisplayAlerts()
	{
	  return auto.getProperty(0x00000801);
	}
	
	/*
	 * [id(0x00000801), propput, helpcontext(0x0007a922)] HRESULT DisplayAlerts([in]
	 * PpAlertLevel DisplayAlerts)
	 */
	public boolean setDisplayAlerts(Variant val)
	{
	  return auto.setProperty(0x00000801,val);
	}
	
	/*
	 * [id(0x00000802), propget, helpcontext(0x0007a923)] HRESULT
	 * ShowStartupDialog([out, retval] MsoTriState* ShowStartupDialog)
	 */
	public Variant getShowStartupDialog()
	{
	  return auto.getProperty(0x00000802);
	}
	
	/*
	 * [id(0x00000802), propput, helpcontext(0x0007a923)] HRESULT
	 * ShowStartupDialog([in] MsoTriState ShowStartupDialog)
	 */
	public boolean setShowStartupDialog(Variant val)
	{
	  return auto.setProperty(0x00000802,val);
	}
	
	/*
	 * [id(0x00000803), hidden, helpcontext(0x0007a924)] HRESULT SetPerfMarker([in]
	 * int Marker)
	 */
	
	// unsupported
	
	//	public int SetPerfMarker()
	//	{
	//	  int id = 0x00000803;
	//	  /*
	//		 * args= [in] int Marker
	//		 */
	//	}
	
	/*
	 * [id(0x00000804), propget, helpcontext(0x0007a925)] HRESULT AutoCorrect([out,
	 * retval] AutoCorrect** AutoCorrect)
	 */
	public Variant getAutoCorrect()
	{
	  return auto.getProperty(0x00000804);
	}
	
	/*
	 * [id(0x00000805), propget, helpcontext(0x0007a926)] HRESULT Options([out,
	 * retval] Options** Options)
	 */
	public Variant getOptions()
	{
	  return auto.getProperty(0x00000805);
	}


}
