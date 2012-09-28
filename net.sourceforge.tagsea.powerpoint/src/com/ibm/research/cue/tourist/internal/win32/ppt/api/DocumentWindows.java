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
      uuid(91493455-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x0007c448),
      dual,
      nonextensible,
      oleautomation
    ]

 * @author Li-Te Cheng
 * IBM Research, 2006
 */
public class DocumentWindows 
{

	private OleAutomation auto;
	
	public DocumentWindows (OleAutomation automation)
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
		[id(0x000007d1), propget, helpcontext(0x0007c449)]
	        HRESULT Application([out, retval] Application** Application)
	*/
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
		[id(0x000007d2), propget, helpcontext(0x0007c44a)]
	        HRESULT Parent([out, retval] IDispatch** Parent)
	*/
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
		[id(00000000), helpcontext(0x0007c44b)]
	        HRESULT Item(
	                        [in] int Index, 
	                        [out, retval] DocumentWindow** Item)
	*/
	public Variant Item(int index)
	{
	  int id = 00000000;
	  return auto.invoke(id,new Variant[]{new Variant(index)});
	}
	
	/*
		[id(0x000007d3), helpcontext(0x0007c44c)]
	        HRESULT Arrange([in, optional, defaultvalue(1)] PpArrangeStyle arrangeStyle)
	*/
	public Variant Arrange()
	{
	  int id = 0x000007d3;
	  return auto.invoke(id);
	}

}
