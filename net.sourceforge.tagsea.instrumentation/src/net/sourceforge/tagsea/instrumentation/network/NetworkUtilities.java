/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.instrumentation.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sourceforge.tagsea.instrumentation.TagSEAInstrumentationPlugin;


/**
 * Utilities for communicating with the network scripts.
 * @author Del Myers
 *
 */
public class NetworkUtilities {
	public static final String SCRIPT_ROOT = "http://stgild.cs.uvic.ca/cgi-bin";
	public static final String REGISTER_SCRIPT = SCRIPT_ROOT + "/tagseauid-january2008.cgi";
	public static final String UPLOAD_SCRIPT = SCRIPT_ROOT + "/tagSEAUpload-january2008.cgi";
	
		
	public static String readInputAsString(InputStream stream) {
		String s = "";
		String data = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		try {
			while ((s = br.readLine()) != null)
				data += s;
		} catch (IOException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
		return data;
	}
}
