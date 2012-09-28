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
 * 

    [
      odl,
      uuid(91493456-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x0007c830),
      dual,
      nonextensible,
      oleautomation
    ]

 return auto.invoke(id,new Variant[]{new Variant(path)});
 
 * 
 * @author Li-Te Cheng
 * IBM Research, 2006
 */
public class SlideShowWindows 
{

	private OleAutomation auto;
	
	public SlideShowWindows (OleAutomation automation)
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
		[id(0x000007d1), propget, helpcontext(0x0007c831)]
	        HRESULT Application([out, retval] Application** Application)
	*/
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
		[id(0x000007d2), propget, helpcontext(0x0007c832)]
	        HRESULT Parent([out, retval] IDispatch** Parent)
	*/
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
		[id(00000000), helpcontext(0x0007c833)]
	        HRESULT Item(
	                        [in] int Index, 
	                        [out, retval] SlideShowWindow** Item)
	*/
	public Variant Item(int index)
	{
	  int id = 00000000;
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
