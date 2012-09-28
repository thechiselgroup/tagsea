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
 * generated from Microsoft Powerpoint Object Library
 * 
    [
      odl,
      uuid(91493462-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x0007f710),
      dual,
      nonextensible,
      oleautomation
    ]
    
 * @author Li-Te Cheng
 * IBM Research, 2006
 */
public class Presentations 
{

	private OleAutomation auto;
	
	public Presentations (OleAutomation automation)
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
	 * [id(0x000007d1), propget, helpcontext(0x0007f711)] HRESULT
	 * Application([out, retval] Application** Application)
	 */
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
	 * [id(0x000007d2), propget, helpcontext(0x0007f712)] HRESULT Parent([out,
	 * retval] IDispatch** Parent)
	 */
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
	 * [id(00000000), helpcontext(0x0007f713)] HRESULT Item( [in] VARIANT Index,
	 * [out, retval] Presentation** Item)
	 */
	public Variant Item(int index)
	{
	  int id = 00000000;
	  return auto.invoke(id,new Variant[]{new Variant(index)});
	}
	
	/*
	 * [id(0x000007d3), helpcontext(0x0007f714)] HRESULT Add( [in, optional,
	 * defaultvalue(-1)] MsoTriState WithWindow, [out, retval] Presentation**
	 * Add)
	 */
	
	// unsupported
	
	//	public int Add()
	//	{
	//	  int id = 0x000007d3;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in, optional, defaultvalue(-1
	//		 */
	//	}
	
	/*
	 * [id(0x000007d4), hidden, helpcontext(0x0007f715)] HRESULT OpenOld( [in]
	 * BSTR FileName, [in, optional, defaultvalue(0)] MsoTriState ReadOnly, [in,
	 * optional, defaultvalue(0)] MsoTriState Untitled, [in, optional,
	 * defaultvalue(-1)] MsoTriState WithWindow, [out, retval] Presentation**
	 * OpenOld)
	 */
	
	// unsupported
	
	//	public int OpenOld()
	//	{
	//	  int id = 0x000007d4;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in] BSTR FileName, [in, optional, defaultvalue(0
	//		 */
	//	}
	
	/*
	 * [id(0x000007d5), helpcontext(0x0007f716)] HRESULT Open( [in] BSTR
	 * FileName, [in, optional, defaultvalue(0)] MsoTriState ReadOnly, [in,
	 * optional, defaultvalue(0)] MsoTriState Untitled, [in, optional,
	 * defaultvalue(-1)] MsoTriState WithWindow, [out, retval] Presentation**
	 * Open)
	 */
	public Variant Open(String filename)
	{
	  int id = 0x000007d5;
	  return auto.invoke(id,new Variant[]{new Variant(filename)});
	}
	
	/*
	 * [id(0x000007d6), helpcontext(0x0007f717)] HRESULT CheckOut([in] BSTR
	 * FileName)
	 */
	
	// unsupported
	
	//	public int CheckOut()
	//	{
	//	  int id = 0x000007d6;
	//	  /*
	//		 * args= [in] BSTR FileName
	//		 */
	//	}
	
	/*
	 * [id(0x000007d7), helpcontext(0x0007f718)] HRESULT CanCheckOut( [in] BSTR
	 * FileName, [out, retval] VARIANT_BOOL* CanCheckOut)
	 */
	
	// unsupported
	
	//	public int CanCheckOut()
	//	{
	//	  int id = 0x000007d7;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in] BSTR FileName, [out, retval] VARIANT_BOOL* CanCheckOut
	//		 */
	//	}

}
