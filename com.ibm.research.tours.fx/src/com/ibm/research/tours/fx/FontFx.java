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
package com.ibm.research.tours.fx;

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.ccvs.ui.ICVSUIConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * Special font effects within the Eclipse IDE for tours.
 * 
 * Note: font effects on editors will persist after closing Eclipse.   If you don't want this to happen,
 * use getTextEditorsFont() to save the original font, and then later call setTextEditorsFont
 * to restore the original font after you are finished with the effects.
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */

public class FontFx {

	public static final String
			JAVA_EDITORS_PREF = "org.eclipse.jdt.ui.editors.textfont",
			TEXT_EDITORS_PREF = "org.eclipse.jface.textfont",
			CONSOLE_PREF = "org.eclipse.debug.ui.consoleFont";
	
	private static FontData[] fCachedConsoleFont;
	private static FontData[] fCachedTextEditorFont;
	private static FontData[] fCachedJavaEditorFont;
	private static Font		  fCachedPackageExplorerFont;
	
	// @tag todo tagsea font : replace specific setJava, setConsole, etc with setFont, getFont
	
	/**
	 * get font setting via preferences
	 * @param prefId
	 */
	public static FontData getFont(String prefId)
	{
		IPreferenceStore workbenchStore = WorkbenchPlugin.getDefault().getPreferenceStore();
		FontData[] fontData = PreferenceConverter.getFontDataArray(workbenchStore, prefId);
		
		if(fCachedJavaEditorFont == null)
			fCachedJavaEditorFont = fontData;
		
		return fontData[0];	// assume 1st element in the area is what we care about
	}
	
	/**
	 * apply font setting via preferences
	 * @param prefId
	 * @param font
	 * @return previous font setting
	 */
	public static FontData setFont(String prefId, FontData font)
	{
		FontData oldFont = getFont(prefId);
		IPreferenceStore workbenchStore = WorkbenchPlugin.getDefault().getPreferenceStore();
		FontData[] newFontData = new FontData[1];
		newFontData[0] = font;
		String newFontPrefsStr = PreferenceConverter.getStoredRepresentation(newFontData);
		workbenchStore.setValue(prefId, newFontPrefsStr);
		return oldFont;
	}
	
	/**
	 * reset font to defaults via preferences
	 * @param prefId
	 * @return FontData
	 */
	public static FontData resetFont(String prefId)
	{
		FontData oldFont = getFont(prefId);
		IPreferenceStore workbenchStore = WorkbenchPlugin.getDefault().getPreferenceStore();
		workbenchStore.setToDefault(prefId);
		return oldFont;
	}
	
	/**
	 * apply given font to all text editors in Eclipse
	 * @param font
	 * @return font prior to the font change
	 */
	public static FontData setTextEditorsFont(FontData font)
	{
		return setFont(TEXT_EDITORS_PREF,font);
	}
	
	public static FontData setJavaEditorsFont(FontData font)
	{
		return setFont(JAVA_EDITORS_PREF,font);
	}
	
	/**
	 * apply given font to console in Eclipse
	 * @param font
	 * @return font prior to the font change
	 */
	public static FontData setConsoleFont(FontData font)
	{
		return setFont(CONSOLE_PREF,font);
	}
	
	public static void resetJavaEditorsFont()
	{
		resetFont(JAVA_EDITORS_PREF);
	}
	
	public static void resetTextEditorsFont()
	{
		resetFont(TEXT_EDITORS_PREF);
	}
	
	
	public static void resetConsoleFont()
	{
		resetFont(CONSOLE_PREF);
	}
	
	/**
	 * sets current font size for all text editors in Eclise
	 * @param size
	 * @return font prior to the size change
	 */
	public static FontData setTextEditorsFontHeight(int size)
	{
		FontData oldFont = getTextEditorsFont();
		FontData newFont = new FontData(oldFont.getName(),size,oldFont.getStyle());
		newFont.setLocale(oldFont.getLocale());
		setTextEditorsFont(newFont);
		return oldFont;
	}

	/**
	 * retrieve current font setting for all text editors in Eclipse
	 * @return FontData
	 */
	public static FontData getTextEditorsFont()
	{
		return getFont(TEXT_EDITORS_PREF);
	}
	
	public static FontData getJavaEditorsFont()
	{
		return getFont(JAVA_EDITORS_PREF);
	}
	
	/**
	 * sets current font size to console in Eclipse
	 * @param size
	 * @return font prior to size change
	 */
	public static FontData setConsoleFontHeight(int size)
	{
		FontData oldFont = getConsoleFont();
		FontData newFont = new FontData(oldFont.getName(),size,oldFont.getStyle());
		newFont.setLocale(oldFont.getLocale());
		setConsoleFont(newFont);
		return oldFont;
	}

	/**
	 * retrieve current font setting for console in Eclipse
	 * @return FontData
	 */
	public static FontData getConsoleFont()
	{
		return getFont(CONSOLE_PREF);
	}

	/**
	 * change the font of the package explorer
	 * 
	 * @param Font
	 * @return previous font of package explorer
	 */
	public static Font setPackageExplorerFont(Font font)
	{
		PackageExplorerPart view = PackageExplorerPart.getFromActivePerspective();
		if ( view==null )
			throw new RuntimeException("package explorer not visible in current perspective");
		
		Tree tree = view.getTreeViewer().getTree();
		Font oldFont = tree.getFont();
		tree.setFont(font);
		tree.redraw();
		
		if ( fCachedPackageExplorerFont==null )
			oldFont = fCachedPackageExplorerFont;
		
		return oldFont;
	}
	
	/**
	 * retrieve the font of the package explorer
	 * @return Font
	 */
	public static Font getPackageExplorerFont()
	{
		PackageExplorerPart view = PackageExplorerPart.getFromActivePerspective();
		if ( view==null )
			throw new RuntimeException("package explorer not visible in current perspective");
		
		Tree tree = view.getTreeViewer().getTree();
		return tree.getFont();
	}
	
	
	/**
	 * change the font size of the package explorer
	 * @param size
	 * @return newly created Font with new size
	 */
	public static Font setPackageExplorerFontHeight(int size)
	{
		Font oldFont = getPackageExplorerFont();
		Font newFont = new Font(oldFont.getDevice(),setFontDataHeight(oldFont,size));
		setPackageExplorerFont(newFont);
		return newFont;
	}
	
	/**
	 * reset package explorer font to cached font
	 */
	public static void resetPackageExplorerFont()
	{
		if ( fCachedPackageExplorerFont!=null )
			setPackageExplorerFont(fCachedPackageExplorerFont);
	}

	/**
	 * utility routine that creates a new FontData based on an oldFont, but changes the height
	 * @param oldFont
	 * @param newHeight
	 * @return FontData
	 */
	public static FontData setFontDataHeight(Font oldFont, int newHeight)
	{
		FontData oldFontData = oldFont.getFontData()[0];
		FontData newFontData = new FontData(oldFontData.getName(),newHeight,oldFontData.getStyle());
		newFontData.setLocale(oldFontData.getLocale());
		return newFontData;
	}
	
	/**
	 * utility routine that creates a new FontData based on an oldFont, but changes the height
	 * @param oldFont
	 * @param newStyle
	 * @return FontData
	 */
	public static FontData setFontDataStyle(Font oldFont, int newStyle)
	{
		FontData oldFontData = oldFont.getFontData()[0];
		FontData newFontData = new FontData(oldFontData.getName(),oldFontData.getHeight(),newStyle);
		newFontData.setLocale(oldFontData.getLocale());
		return newFontData;
	}
	
	/**
	 * utility routine that creates a new Font based on an oldFont, but changes the name
	 * @param oldFont
	 * @param newName
	 * @return Font - need to dispose this resource when done with it
	 */
	public static FontData setFontDataName(Font oldFont, String newName)
	{
		FontData oldFontData = oldFont.getFontData()[0];
		FontData newFontData = new FontData(newName,oldFontData.getHeight(),oldFontData.getStyle());
		newFontData.setLocale(oldFontData.getLocale());
		return newFontData;
	}
	
	/**
	 * sets CVS font decorator preference - this interferes with package explorer and resource font effects, so usually turn this off
	 * @param enabled
	 * @return previous preference setting
	 */
	protected static void setCVSFontPreference(boolean enabled) {
		// @tag hack cvs tagsea font resource fx : this is a hack - disable CVS font decorator - because it overrides our decorator! this won't prevent any other coloring decorator from stopping us...
		IPreferenceStore cvsUiPrefStore = CVSUIPlugin.getPlugin().getPreferenceStore();
		cvsUiPrefStore.setValue(ICVSUIConstants.PREF_USE_FONT_DECORATORS, enabled);
	}

	/**
	 * returns current CVS font decorator preference
	 * @return boolean
	 */
	protected static boolean getCVSFontPreference() {
		IPreferenceStore cvsUiPrefStore = CVSUIPlugin.getPlugin().getPreferenceStore();
		return cvsUiPrefStore.getBoolean(ICVSUIConstants.PREF_USE_FONT_DECORATORS); 
		
	}
}