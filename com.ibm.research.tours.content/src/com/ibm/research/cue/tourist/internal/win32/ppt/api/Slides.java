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

    [
      odl,
      uuid(91493469-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x00081650),
      dual,
      nonextensible,
      oleautomation
    ]

 * @author Li-Te Cheng 
 * CUE, IBM Research, 2006
 */

public class Slides 
{
	
	private OleAutomation auto;
	
	public Slides (OleAutomation automation)
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
		[id(0x000007d1), propget, helpcontext(0x00081651)]
	        HRESULT Application([out, retval] Application** Application)
	*/
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
		[id(0x000007d2), propget, helpcontext(0x00081652)]
	        HRESULT Parent([out, retval] IDispatch** Parent)
	*/
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
		[id(00000000), helpcontext(0x00081653)]
	        HRESULT Item(
	                        [in] VARIANT Index, 
	                        [out, retval] Slide** Item)
	*/
	public Variant Item(int index)
	{
	  int id = 00000000;
	  /* args=
	
	                        [in] VARIANT Index, 
	                        [out, retval] Slide** Item
	*/
	  return auto.invoke(id,new Variant[]{new Variant(index)});
	}
	
	/*
		[id(0x000007d3), helpcontext(0x00081654)]
	        HRESULT FindBySlideID(
	                        [in] long SlideID, 
	                        [out, retval] Slide** FindBySlideID)
	*/
	public Variant FindBySlideID(int slideID)
	{
	  int id = 0x000007d3;
	  return auto.invoke(id,new Variant[]{new Variant(slideID)});
	}
	
	/*
		[id(0x000007d4), helpcontext(0x00081655)]
	        HRESULT Add(
	                        [in] int Index, 
	                        [in] PpSlideLayout Layout, 
	                        [out, retval] Slide** Add)
	*/
	public Variant Add(int index, int layout)
	{
	  int id = 0x000007d4;
	  /* args=
	
	                        [in] int Index, 
	                        [in] PpSlideLayout Layout, 
	                        [out, retval] Slide** Add
	*/
	  return auto.invoke(id,new Variant[]{new Variant(index),new Variant(layout)});
	}
	
	/*
		[id(0x000007d5), helpcontext(0x00081656)]
	        HRESULT InsertFromFile(
	                        [in] BSTR FileName, 
	                        [in] int Index, 
	                        [in, optional, defaultvalue(1)] int SlideStart, 
	                        [in, optional, defaultvalue(-1)] int SlideEnd, 
	                        [out, retval] int* FromFile)
	*/
	public Variant InsertFromFile(String filename, int index, int start, int end)
	{
	  int id = 0x000007d5;
	  /* args=
	
	                        [in] BSTR FileName, 
	                        [in] int Index, 
	                        [in, optional, defaultvalue(1
	*/
	  return auto.invoke(id,new Variant[]{new Variant(filename),new Variant(index),new Variant(start),new Variant(end)});
	}
	
	/*
		[id(0x000007d6), helpcontext(0x00081657)]
	        HRESULT Range(
	                        [in, optional] VARIANT Index, 
	                        [out, retval] SlideRange** Range)
	*/
	public Variant Range(int index)
	{
	  int id = 0x000007d6;
	  /* args=
	
	                        [in, optional] VARIANT Index, 
	                        [out, retval] SlideRange** Range
	*/
	  return auto.invoke(id,new Variant[]{new Variant(index)});
	}

	/*
	[id(0x000007d6), helpcontext(0x00081657)]
        HRESULT Range(
                        [in, optional] VARIANT Index, 
                        [out, retval] SlideRange** Range)
	*/
	public Variant Range()
	{
	  int id = 0x000007d6;
	  /* args=
	
	                        [in, optional] VARIANT Index, 
	                        [out, retval] SlideRange** Range
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007d7), helpcontext(0x00081658)]
	        HRESULT Paste(
	                        [in, optional, defaultvalue(-1)] int Index, 
	                        [out, retval] SlideRange** Paste)
	*/
	public Variant Paste(int index)
	{
	  int id = 0x000007d7;
	  /* args=
	
	                        [in, optional, defaultvalue(-1
	*/
	  return auto.invoke(id,new Variant[]{new Variant(index)});
	}

	/*
		[id(0x0000000b), propget, helpcontext(0x0007a50b)]
		long Count();
	 */
	public Variant getCount()
	{
	  int id = 0x0000000b;
	  return auto.getProperty(id);		
	}

}
