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
 * generated from Powerpoint Object Library

    [
      odl,
      uuid(9149346A-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x00081a38),
      dual,
      nonextensible,
      oleautomation
    ]
    
 * @author Li-Te Cheng 
 * CUE, IBM Research 2006
 */

public class _Slide 
{

	private OleAutomation auto;
	
	public _Slide (OleAutomation automation)
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
		[id(0x000007d1), propget, helpcontext(0x00081a39)]
	        HRESULT Application([out, retval] Application** Application)
	*/
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
		[id(0x000007d2), propget, helpcontext(0x00081a3a)]
	        HRESULT Parent([out, retval] IDispatch** Parent)
	*/
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
		[id(0x000007d3), propget, helpcontext(0x00081a3b)]
	        HRESULT Shapes([out, retval] Shapes** Shapes)
	*/
	public Variant getShapes()
	{
	  return auto.getProperty(0x000007d3);
	}
	
	/*
		[id(0x000007d4), propget, helpcontext(0x00081a3c)]
	        HRESULT HeadersFooters([out, retval] HeadersFooters** HeadersFooters)
	*/
	public Variant getHeadersFooters()
	{
	  return auto.getProperty(0x000007d4);
	}
	
	/*
		[id(0x000007d5), propget, helpcontext(0x00081a3d)]
	        HRESULT SlideShowTransition([out, retval] SlideShowTransition** SlideShowTransition)
	*/
	public Variant getSlideShowTransition()
	{
	  return auto.getProperty(0x000007d5);
	}
	
	/*
		[id(0x000007d6), propget, helpcontext(0x00081a3e)]
	        HRESULT ColorScheme([out, retval] ColorScheme** ColorScheme)
	*/
	public Variant getColorScheme()
	{
	  return auto.getProperty(0x000007d6);
	}
	
	/*
		[id(0x000007d6), propput, helpcontext(0x00081a3e)]
	        HRESULT ColorScheme([in] ColorScheme* ColorScheme)
	*/
	public boolean setColorScheme(Variant val)
	{
	  return auto.setProperty(0x000007d6,val);
	}
	
	/*
		[id(0x000007d7), propget, helpcontext(0x00081a3f)]
	        HRESULT Background([out, retval] ShapeRange** Background)
	*/
	public Variant getBackground()
	{
	  return auto.getProperty(0x000007d7);
	}
	
	/*
		[id(0x000007d8), propget, helpcontext(0x00081a40)]
	        HRESULT Name([out, retval] BSTR* Name)
	*/
	public Variant getName()
	{
	  return auto.getProperty(0x000007d8);
	}
	
	/*
		[id(0x000007d8), propput, helpcontext(0x00081a40)]
	        HRESULT Name([in] BSTR Name)
	*/
	public boolean setName(Variant val)
	{
	  return auto.setProperty(0x000007d8,val);
	}
	
	/*
		[id(0x000007d9), propget, helpcontext(0x00081a41)]
	        HRESULT SlideID([out, retval] long* SlideID)
	*/
	public Variant getSlideID()
	{
	  return auto.getProperty(0x000007d9);
	}
	
	/*
		[id(0x000007da), propget, helpcontext(0x00081a42)]
	        HRESULT PrintSteps([out, retval] int* PrintSteps)
	*/
	public Variant getPrintSteps()
	{
	  return auto.getProperty(0x000007da);
	}
	
	/*
		[id(0x000007db), helpcontext(0x00081a43)]
	        HRESULT Select()
	*/
	public Variant Select()
	{
	  int id = 0x000007db;
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007dc), helpcontext(0x00081a44)]
	        HRESULT Cut()
	*/
	public Variant Cut()
	{
	  int id = 0x000007dc;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007dd), helpcontext(0x00081a45)]
	        HRESULT Copy()
	*/
	public Variant Copy()
	{
	  int id = 0x000007dd;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007de), propget, helpcontext(0x00081a46)]
	        HRESULT Layout([out, retval] PpSlideLayout* Layout)
	*/
	public Variant getLayout()
	{
	  return auto.getProperty(0x000007de);
	}
	
	/*
		[id(0x000007de), propput, helpcontext(0x00081a46)]
	        HRESULT Layout([in] PpSlideLayout Layout)
	*/
	public boolean setLayout(Variant val)
	{
	  return auto.setProperty(0x000007de,val);
	}
	
	/*
		[id(0x000007df), helpcontext(0x00081a47)]
	        HRESULT Duplicate([out, retval] SlideRange** Duplicate)
	*/
	public Variant Duplicate()
	{
	  int id = 0x000007df;
	  /* args=
	[out, retval] SlideRange** Duplicate
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e0), helpcontext(0x00081a48)]
	        HRESULT Delete()
	*/
	public Variant Delete()
	{
	  int id = 0x000007e0;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e1), propget, helpcontext(0x00081a49)]
	        HRESULT Tags([out, retval] Tags** Tags)
	*/
	public Variant getTags()
	{
	  return auto.getProperty(0x000007e1);
	}
	
	/*
		[id(0x000007e2), propget, helpcontext(0x00081a4a)]
	        HRESULT SlideIndex([out, retval] int* SlideIndex)
	*/
	public Variant getSlideIndex()
	{
	  return auto.getProperty(0x000007e2);
	}
	
	/*
		[id(0x000007e3), propget, helpcontext(0x00081a4b)]
	        HRESULT SlideNumber([out, retval] int* SlideNumber)
	*/
	public Variant getSlideNumber()
	{
	  return auto.getProperty(0x000007e3);
	}
	
	/*
		[id(0x000007e4), propget, helpcontext(0x00081a4c)]
	        HRESULT DisplayMasterShapes([out, retval] MsoTriState* DisplayMasterShapes)
	*/
	public Variant getDisplayMasterShapes()
	{
	  return auto.getProperty(0x000007e4);
	}
	
	/*
		[id(0x000007e4), propput, helpcontext(0x00081a4c)]
	        HRESULT DisplayMasterShapes([in] MsoTriState DisplayMasterShapes)
	*/
	public boolean setDisplayMasterShapes(Variant val)
	{
	  return auto.setProperty(0x000007e4,val);
	}
	
	/*
		[id(0x000007e5), propget, helpcontext(0x00081a4d)]
	        HRESULT FollowMasterBackground([out, retval] MsoTriState* FollowMasterBackground)
	*/
	public Variant getFollowMasterBackground()
	{
	  return auto.getProperty(0x000007e5);
	}
	
	/*
		[id(0x000007e5), propput, helpcontext(0x00081a4d)]
	        HRESULT FollowMasterBackground([in] MsoTriState FollowMasterBackground)
	*/
	public boolean setFollowMasterBackground(Variant val)
	{
	  return auto.setProperty(0x000007e5,val);
	}
	
	/*
		[id(0x000007e6), propget, helpcontext(0x00081a4e)]
	        HRESULT NotesPage([out, retval] SlideRange** NotesPage)
	*/
	public Variant getNotesPage()
	{
	  return auto.getProperty(0x000007e6);
	}
	
	/*
		[id(0x000007e7), propget, helpcontext(0x00081a4f)]
	        HRESULT Master([out, retval] _Master** Master)
	*/
	public Variant getMaster()
	{
	  return auto.getProperty(0x000007e7);
	}
	
	/*
		[id(0x000007e8), propget, helpcontext(0x00081a50)]
	        HRESULT Hyperlinks([out, retval] Hyperlinks** Hyperlinks)
	*/
	public Variant getHyperlinks()
	{
	  return auto.getProperty(0x000007e8);
	}
	
	/*
		[id(0x000007e9), helpcontext(0x00081a51)]
	        HRESULT Export(
	                        [in] BSTR FileName, 
	                        [in] BSTR FilterName, 
	                        [in, optional, defaultvalue(0)] int ScaleWidth, 
	                        [in, optional, defaultvalue(0)] int ScaleHeight)
	*/
	// not supported
	//	public Variant Export()
	//	{
	//	  int id = 0x000007e9;
	//	  /* args=
	//	
	//	                        [in] BSTR FileName, 
	//	                        [in] BSTR FilterName, 
	//	                        [in, optional, defaultvalue(0
	//	*/
	//	  return auto.invoke(id,new Variant[]{new Variant(arg)});
	//	}
	
	/*
		[id(0x000007ea), propget, helpcontext(0x00081a52)]
	        HRESULT Scripts([out, retval] Scripts** Scripts)
	*/
	public Variant getScripts()
	{
	  return auto.getProperty(0x000007ea);
	}
	
	/*
		[id(0x000007ec), propget, helpcontext(0x00081a54)]
	        HRESULT Comments([out, retval] Comments** Comments)
	*/
	public Variant getComments()
	{
	  return auto.getProperty(0x000007ec);
	}
	
	/*
		[id(0x000007ed), propget, helpcontext(0x00081a55)]
	        HRESULT Design([out, retval] Design** Design)
	*/
	public Variant getDesign()
	{
	  return auto.getProperty(0x000007ed);
	}
	
	/*
		[id(0x000007ed), propput, helpcontext(0x00081a55)]
	        HRESULT Design([in] Design* Design)
	*/
	public boolean setDesign(Variant val)
	{
	  return auto.setProperty(0x000007ed,val);
	}
	
	/*
		[id(0x000007ee), helpcontext(0x00081a56)]
	        HRESULT MoveTo([in] int toPos)
	*/
	public Variant MoveTo(int toPos)
	{
	  int id = 0x000007ee;
	  /* args=
	[in] int toPos
	*/
	  return auto.invoke(id,new Variant[]{new Variant(toPos)});
	}
	
	/*
		[id(0x000007ef), propget, helpcontext(0x00081a57)]
	        HRESULT TimeLine([out, retval] TimeLine** TimeLine)
	*/
	public Variant getTimeLine()
	{
	  return auto.getProperty(0x000007ef);
	}
	
	/*
		[id(0x000007f0), helpcontext(0x00081a58)]
	        HRESULT ApplyTemplate([in] BSTR FileName)
	*/
	public Variant ApplyTemplate(String filename)
	{
	  int id = 0x000007f0;
	  /* args=
	[in] BSTR FileName
	*/
	  return auto.invoke(id,new Variant[]{new Variant(filename)});
	}


}
