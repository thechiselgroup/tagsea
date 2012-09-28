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
package com.ibm.research.tours.fx.elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.fx.ToursFxPlugin;
import com.ibm.research.tours.fx.location.Bottom;
import com.ibm.research.tours.fx.location.BottomLeft;
import com.ibm.research.tours.fx.location.BottomRight;
import com.ibm.research.tours.fx.location.Center;
import com.ibm.research.tours.fx.location.ILocation;
import com.ibm.research.tours.fx.location.ILocationProvider;
import com.ibm.research.tours.fx.location.Left;
import com.ibm.research.tours.fx.location.Right;
import com.ibm.research.tours.fx.location.Top;
import com.ibm.research.tours.fx.location.TopLeft;
import com.ibm.research.tours.fx.location.TopRight;


// @tag todo tours text : need curved corners and transparency support

public class TextTourElement extends FontTourElement implements ILocationProvider
{
	public static final String LOCATION_ELEMENT = "location";
	public static final String FOREGROUND_COLOR_ELEMENT = "foreground";
	public static final String BACKGROUND_COLOR_ELEMENT = "background";
	public static final String TEXT_ELEMENT = "text";

	public static final String TEXT = "Text Area";

	private String fText = TEXT;
	private RGB fForeground;
	private RGB fBackground;
	private ILocation fLocation;
	private ILocation[] fSupportedLocations;

	public ILocation[] getSupportedLocations()
	{
		if(fSupportedLocations == null)
		{
			fSupportedLocations = new ILocation[]
			                                    {
					Center.getInstance(),
					Top.getInstance(),
					Bottom.getInstance(),
					Left.getInstance(),
					Right.getInstance(),
					TopLeft.getInstance(),
					TopRight.getInstance(),
					BottomLeft.getInstance(),
					BottomRight.getInstance(),
			                                    };                             
		}

		return fSupportedLocations;
	}

	public ILocation getLocation() 
	{
		if(fLocation == null)
			fLocation = Center.getInstance();
		return fLocation;
	}

	public void setLocation(ILocation location)
	{
		if(fLocation!=location)
		{
			fLocation = location;
			fireElementChangedEvent();
		}
	}

	public String getDisplayText() 
	{
		if(fText == null)
			fText = new String();

		return fText;
	}

	public void setDisplayText(String text) 
	{
		if(!getDisplayText().equals(text))
		{
			fText = text;
			fireElementChangedEvent();
		}
	}

	public RGB getForegroundColor() 
	{
		if(fForeground == null)
			fForeground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK).getRGB();
		return fForeground;
	}

	public void setForegroundColor(RGB foreground) 
	{
		if(!getForegroundColor().equals(foreground))
		{
			fForeground = foreground;
			fireElementChangedEvent();
		}
	}

	public RGB getBackgroundColor() 
	{
		if(fBackground == null)
			fBackground = Display.getDefault().getSystemColor(SWT.COLOR_WHITE).getRGB();
		
		return fBackground;
	}

	public void setBackgroundColor(RGB background) 
	{
		if(!getBackgroundColor().equals(background))
		{
			fBackground = background;
			fireElementChangedEvent();
		}
	}

	@Override
	public void save(IMemento memento) 
	{
		super.save(memento);

		IMemento textMemento = memento.createChild(TEXT_ELEMENT);
		textMemento.putTextData(getDisplayText());

		if(getLocation() != null)
		{
			IMemento locationMemento = memento.createChild(LOCATION_ELEMENT);
			locationMemento.putTextData(getLocation().getText());
		}

		IMemento foregroundMemento = memento.createChild(FOREGROUND_COLOR_ELEMENT);
		RGB foreground = getForegroundColor();
		foregroundMemento.putTextData(foreground.red + "," + foreground.green + "," + foreground.blue);

		IMemento backgroundMemento = memento.createChild(BACKGROUND_COLOR_ELEMENT);
		RGB background = getBackgroundColor();
		backgroundMemento.putTextData(background.red + "," + background.green + "," + background.blue);
	}


	@Override
	public void load(IMemento memento) 
	{
		super.load(memento);	

		IMemento textMemento = memento.getChild(TEXT_ELEMENT);

		if(textMemento!=null)
		{
			String text = textMemento.getTextData();

			if(text!=null)
				setDisplayText(text);
		}

		IMemento locationMemento = memento.getChild(LOCATION_ELEMENT);

		if(locationMemento != null)
		{
			String text = locationMemento.getTextData();

			if(text!=null)
			{
				for(ILocation location : getSupportedLocations())
					if(location.getText().equals(text))
						setLocation(location);
			}
		}

		IMemento foregroundMemento = memento.getChild(FOREGROUND_COLOR_ELEMENT);

		if(foregroundMemento != null)
		{
			String text = foregroundMemento.getTextData();

			if(text!=null)
			{
				String[] rgb = text.split(",");

				if(rgb.length == 3)
				{
					try 
					{
						int red = Integer.parseInt(rgb[0]);
						int green = Integer.parseInt(rgb[1]);
						int blue = Integer.parseInt(rgb[2]);

						setForegroundColor(new RGB(red,green,blue));

					}
					catch (NumberFormatException e) 
					{
						e.printStackTrace();
					}

				}
			}
		}

		IMemento backgroundMemento = memento.getChild(BACKGROUND_COLOR_ELEMENT);

		if(backgroundMemento != null)
		{
			String text = backgroundMemento.getTextData();

			if(text!=null)
			{
				String[] rgb = text.split(",");

				if(rgb.length == 3)
				{
					try 
					{
						int red = Integer.parseInt(rgb[0]);
						int green = Integer.parseInt(rgb[1]);
						int blue = Integer.parseInt(rgb[2]);

						setBackgroundColor(new RGB(red,green,blue));

					}
					catch (NumberFormatException e) 
					{
						e.printStackTrace();
					}

				}
			}
		}

	}

	private Shell fShell;
	private Composite fComposite;
	private CLabel fLabel;
	private Color fForegroundColor;
	private Color fBackgroundColor;
	private Font fFont;
	
	public void start() 
	{
		
	}

	public void stop() 
	{
		if(fShell != null)
		{
			if(!fShell.isDisposed())
				fShell.dispose();
			
			fShell = null;
		}
		
		if(fForegroundColor != null)
		{
			if(!fForegroundColor.isDisposed())
				fForegroundColor.dispose();
			
			fForegroundColor = null;
		}
		
		if(fBackgroundColor != null)
		{
			if(!fBackgroundColor.isDisposed())
				fBackgroundColor.dispose();
			
			fBackgroundColor = null;
		}
		
		if(fFont != null)
		{
			if(!fFont.isDisposed())
				fFont.dispose();
			
			fFont = null;
		}
	}

	public void transition() 
	{
		fShell = new Shell(SWT.ON_TOP|SWT.MODELESS);	
		fShell.setLayout(new FillLayout());
		fComposite = new Composite(fShell,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		fComposite.setLayout(layout);
		fLabel = new CLabel(fComposite,SWT.CENTER);
		fLabel.setText(getDisplayText());

		GridData data = new GridData(GridData.FILL_BOTH);
		fLabel.setLayoutData(data);
		
		fForegroundColor = new Color(Display.getDefault(),getForegroundColor());
		fLabel.setForeground(fForegroundColor);
		fBackgroundColor = new Color(Display.getDefault(),getBackgroundColor());
		fLabel.setBackground(fBackgroundColor);
	
		fFont = new Font(Display.getDefault(),getFontData());
		fLabel.setFont(fFont);

		fShell.pack();
		
		int x = 0;
		int y = 0;
		Shell wbShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		int monitorHeight = wbShell.getBounds().height;
		int monitorWidth = wbShell.getBounds().width;
		int width = fShell.getBounds().width;
		int height = fShell.getBounds().height;
		int centerX = monitorWidth/2;
		int centerY = monitorHeight/2;
		
		int PADDING = monitorHeight/10;
		
		ILocation location = getLocation();
		
		if(location instanceof Center)
		{
			x = centerX - width /2;
			y = centerY - height /2;
		}
		else if(location instanceof Top)
		{
			x = centerX - width /2;
			y = PADDING;
		}
		else if(location instanceof Bottom)
		{
			x = centerX - width /2;
			y = monitorHeight - PADDING;
		}
		else if(location instanceof Left)
		{
			x = PADDING;
			y = centerY - height/2;
		}
		else if(location instanceof Right)
		{
			x = (monitorWidth - width) - PADDING;
			y = centerY - height/2;
		}
		else if(location instanceof TopLeft)
		{
			x = PADDING;
			y = PADDING;
		}
		else if(location instanceof BottomLeft)
		{
			x = PADDING;
			y = (monitorHeight - height) - PADDING;
		}
		else if(location instanceof TopRight)
		{
			x = (monitorWidth - width) - PADDING;
			y = PADDING;
		}
		else if(location instanceof BottomRight)
		{
			x = (monitorWidth - width) - PADDING;
			y = (monitorHeight - height) - PADDING;
		}
		Point globalLocation = wbShell.toDisplay(x, y);
		fShell.setLocation(globalLocation.x, globalLocation.y);
		fShell.open();
	}

	public Image getImage() 
	{
		return ToursFxPlugin.getDefault().getImageRegistry().get(ToursFxPlugin.IMG_TEXT);
	}

	public String getShortText()
	{
		String text = getDisplayText();
		
		if(text.length() > 50)
			return text.substring(0, 47) + "...";
		else
			return text;
	}
	
	public String getText() 
	{
		String displayText = getDisplayText();
		BufferedReader reader = new BufferedReader(new StringReader(displayText));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.isEmpty()) break;
			}
			if (line == null) line = "";
			displayText = line + "..."; 
		} catch (IOException e) {
		}
		if (displayText.length() > 30) {
			displayText = displayText.subSequence(0, 26) + "...";
		}
		return displayText + super.getTextAnnotations();
	}

	public ITourElement createClone() 
	{
		return new TextTourElement();
	}
}
