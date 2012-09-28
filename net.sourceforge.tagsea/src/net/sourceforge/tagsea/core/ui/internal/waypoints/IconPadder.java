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
package net.sourceforge.tagsea.core.ui.internal.waypoints;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

/**
 * decorated resource icons, such as java and iresource elements create 22x16 icons.
 * most icons are typicaly 16x16.   this class create padded versions of 16x16
 * icons so the WaypointView table does not create distorted icons.
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class IconPadder {

	private static final int WIDTH  = 22, 
						     HEIGHT = 16;
	
	private Map<String, Image> images = new HashMap<String, Image>();
	
	public IconPadder()
	{
	}
	
	public Image getPadded(String key, Image image)
	{
		Rectangle bounds = image.getBounds();
		
		if ( bounds.width>=WIDTH && bounds.height>=HEIGHT )
			return image;  // no padding required

		Image padded = images.get(key);
		if ( padded!=null )
			return padded;
		
		padded = new Image(image.getDevice(),WIDTH,HEIGHT);
		GC  gc = new GC(padded);
		
		if ( bounds.width>0 && bounds.height>0 )
		{
			if ( HEIGHT<WIDTH )
				gc.drawImage(image, 0, 0, bounds.width, bounds.height, (WIDTH-bounds.width)/2, 0, bounds.width*HEIGHT/bounds.height, HEIGHT);
			else
				gc.drawImage(image, 0, 0, bounds.width, bounds.height, 0, (HEIGHT-bounds.height)/2, WIDTH, bounds.width*WIDTH/bounds.width);
		}
		
		gc.dispose();
		
		images.put(key,padded);
		return padded;
	}
	
	public void dispose()
	{
		for (Object key : images.keySet())
		{
			Image image = images.get(key);
			if ( image!=null && !image.isDisposed() )
				image.dispose();
		}
	}
}
