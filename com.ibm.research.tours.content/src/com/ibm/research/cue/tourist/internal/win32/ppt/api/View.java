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
package com.ibm.research.cue.tourist.internal.win32.ppt.api;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;

/**
 * generated from Powerpoint Object Library
 * 
 
     [
      odl,
      uuid(91493458-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x0007d000),
      dual,
      nonextensible,
      oleautomation
    ]

 
 * @author Li-Te Cheng
 * CUE, IBM Research, 2006
 */
public class View 
{

	private OleAutomation auto;
	
	public View (OleAutomation automation)
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
		[id(0x000007d1), propget, helpcontext(0x0007d001)]
	        HRESULT Application([out, retval] Application** Application)
	*/
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
		[id(0x000007d2), propget, helpcontext(0x0007d002)]
	        HRESULT Parent([out, retval] IDispatch** Parent)
	*/
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
		[id(0x000007d3), propget, helpcontext(0x0007d003)]
	        HRESULT Type([out, retval] PpViewType* Type)
	*/
	public Variant getType()
	{
	  return auto.getProperty(0x000007d3);
	}
	
	/*
		[id(0x000007d4), propget, helpcontext(0x0007d004)]
	        HRESULT Zoom([out, retval] int* Zoom)
	*/
	public Variant getZoom()
	{
	  return auto.getProperty(0x000007d4);
	}
	
	/*
		[id(0x000007d4), propput, helpcontext(0x0007d004)]
	        HRESULT Zoom([in] int Zoom)
	*/
	public boolean setZoom(Variant val)
	{
	  return auto.setProperty(0x000007d4,val);
	}
	
	/*
		[id(0x000007d5), helpcontext(0x0007d005)]
	        HRESULT Paste()
	*/
	public Variant Paste()
	{
	  int id = 0x000007d5;
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007d6), propget, helpcontext(0x0007d006)]
	        HRESULT Slide([out, retval] IDispatch** Slide)
	*/
	public Variant getSlide()
	{
	  return auto.getProperty(0x000007d6);
	}
	
	/*
		[id(0x000007d6), propput, helpcontext(0x0007d006)]
	        HRESULT Slide([in] IDispatch* Slide)
	*/
	public boolean setSlide(Variant val)
	{
	  return auto.setProperty(0x000007d6,val);
	}
	
	/*
		[id(0x000007d7), helpcontext(0x0007d007)]
	        HRESULT GotoSlide([in] int Index)
	*/
	public Variant GotoSlide(int index)
	{
	  int id = 0x000007d7;
	  return auto.invoke(id,new Variant[]{new Variant(index)});
	}
	
	/*
		[id(0x000007d8), propget, helpcontext(0x0007d008)]
	        HRESULT DisplaySlideMiniature([out, retval] MsoTriState* DisplaySlideMiniature)
	*/
	public Variant getDisplaySlideMiniature()
	{
	  return auto.getProperty(0x000007d8);
	}
	
	/*
		[id(0x000007d8), propput, helpcontext(0x0007d008)]
	        HRESULT DisplaySlideMiniature([in] MsoTriState DisplaySlideMiniature)
	*/
	public boolean setDisplaySlideMiniature(Variant val)
	{
	  return auto.setProperty(0x000007d8,val);
	}
	
	/*
		[id(0x000007d9), propget, helpcontext(0x0007d009)]
	        HRESULT ZoomToFit([out, retval] MsoTriState* ZoomToFit)
	*/
	public Variant getZoomToFit()
	{
	  return auto.getProperty(0x000007d9);
	}
	
	/*
		[id(0x000007d9), propput, helpcontext(0x0007d009)]
	        HRESULT ZoomToFit([in] MsoTriState ZoomToFit)
	*/
	public boolean setZoomToFit(Variant val)
	{
	  return auto.setProperty(0x000007d9,val);
	}
	
	/*
		[id(0x000007da), helpcontext(0x0007d00a)]
	        HRESULT PasteSpecial(
	                        [in, optional, defaultvalue(0)] PpPasteDataType DataType, 
	                        [in, optional, defaultvalue(0)] MsoTriState DisplayAsIcon, 
	                        [in, optional, defaultvalue("")] BSTR IconFileName, 
	                        [in, optional, defaultvalue(0)] int IconIndex, 
	                        [in, optional, defaultvalue("")] BSTR IconLabel, 
	                        [in, optional, defaultvalue(0)] MsoTriState Link)
	*/
	// not supported
	//	---- *** OLE 2 JAVA NEEDS YOUR HELP TO PARSE THIS METHOD *** ---
	//	public Variant PasteSpecial()
	//	{
	//	  int id = 0x000007da;
	//	  /* args=
	//	
	//	                        [in, optional, defaultvalue(0
	//	*/
	//	  return auto.invoke(id,new Variant[]{new Variant(arg)});
	//  }
	
	/*
		[id(0x000007db), propget, helpcontext(0x0007d00b)]
	        HRESULT PrintOptions([out, retval] PrintOptions** PrintOptions)
	*/
	public Variant getPrintOptions()
	{
	  return auto.getProperty(0x000007db);
	}
	
	/*
		[id(0x000007dc), helpcontext(0x0007d00c)]
	        HRESULT PrintOut(
	                        [in, optional, defaultvalue(-1)] int From, 
	                        [in, optional, defaultvalue(-1)] int To, 
	                        [in, optional, defaultvalue("")] BSTR PrintToFile, 
	                        [in, optional, defaultvalue(0)] int Copies, 
	                        [in, optional, defaultvalue(-99)] MsoTriState Collate)
	*/
	public Variant PrintOut()
	{
	  int id = 0x000007dc;
	  return auto.invoke(id);
	}

}
