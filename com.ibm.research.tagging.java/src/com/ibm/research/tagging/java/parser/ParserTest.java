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

package com.ibm.research.tagging.java.parser;

public class ParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		new ParserTest();
	}
	
	public ParserTest() 
	{
		IJavaWaypointParser parser = JavaWaypointParserFactory.createParser();
		String waypointDefinition = "";
		
		try 
		{
			IJavaWaypointInfo info = parser.parse(waypointDefinition);
			System.out.println(info);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}