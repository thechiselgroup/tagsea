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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.LayoutPart;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSite;

/**
 * Eclipse IDE special effects utility routines for general manipulation of views, editors, and perspectives
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class EclipseFx {

	// several popular view ids, reproduced here for convenience
	public static final String

	// console view
	CONSOLEVIEW_ID     = "org.eclipse.ui.console.ConsoleView",

	// JDT views
	PACKAGEEXPLORER_ID = "org.eclipse.jdt.ui.PackageExplorer",
	TYPEHIERARCHY_ID   = "org.eclipse.jdt.ui.TypeHierarchy",
	CALLHIERARCHY_ID   = "org.eclipse.jdt.ui.callhierarchy.view",
	JAVADOCVIEW_ID     = "org.eclipse.jdt.ui.JavadocView",
	JUNITVIEW_ID	  = "org.eclipse.jdt.junit.ResultView",

	// navigator view
	NAVIGATOR_ID	   = "org.eclipse.ui.views.ResourceNavigator",

	// problems view
	PROBLEMSVIEW_ID    = "org.eclipse.ui.views.ProblemView",

	// project explorer
	PROJECTEXPLORER_ID = "org.eclipse.ui.navigator.ProjectExplorer",

	// properties view
	PROPERTIES_VIEW_ID = "org.eclipse.ui.views.PropertySheet",

	// outline view
	OUTLINE_VIEW_ID    = "org.eclipse.ui.views.ContentOutline",

	// PDE runtime views
	PLUGINREGISTRY_ID  = "org.eclipse.pde.runtime.RegistryBrowser",
	ERRORLOG_ID        = "org.eclipse.pde.runtime.LogView",

	// PDE views
	PLUGINSVIEW_ID     = "org.eclipse.pde.ui.PluginsView",
	DEPENDENCIESVIEW_ID = "org.eclipse.pde.ui.DependenciesView",

	// browser
	BROWSERVIEW_ID    = "org.eclipse.ui.browser.view",

	// cheatsheets
	CHEATSHEETSVIEW_ID = "org.eclipse.ui.cheatsheets.views.CheatSheetView",

	// search view
	SEARCHVIEW_ID     = "org.eclipse.search.ui.views.SearchView",

	// debugging views
	BREAKPOINTVIEW_ID = "org.eclipse.debug.ui.BreakpointView",
	DEBUGVIEW_ID      = "org.eclipse.debug.ui.DebugView",
	VARIABLEVIEW_ID   = "org.eclipse.debug.ui.VariableView",
	EXPRESSIONVIEW_ID = "org.eclipse.debug.ui.ExpressionView",
	REGISTERVIEW_ID   = "org.eclipse.debug.ui.RegisterView",
	MEMORYVIEW_ID     = "org.eclipse.debug.ui.MemoryView",

	// help view
	HELPVIEW_ID	      = "org.eclipse.help.ui.HelpView",

	// team views
	SYNCHRONIZEVIEW_ID = "org.eclipse.team.sync.views.SynchronizeView",
	GENERICHISTORYVIEW_ID = "org.eclipse.team.ui.GenericHistoryView",
	CVSREPOSITORIESVIEW_ID = "org.eclipse.team.ccvs.ui.RepositoriesView",
	CVSEDITORSVIEW_ID  = "org.eclipse.team.ccvs.ui.EditorsView",
	CVSANNOTATEVIEW_ID = "org.eclipse.team.ccvs.ui.AnnotateView"
		;

	// several popular perspective ids
	public static final String 
	DEBUG_PERSPECTIVE_ID = "org.eclipse.debug.ui.DebugPerspective",
	JAVA_PERSPECTIVE_ID  = "org.eclipse.jdt.ui.JavaPerspective",
	PDE_PERSPECTIVE_ID   = "org.eclipse.pde.ui.PDEPerspective",
	CVS_PERSPECTIVE_ID   = "org.eclipse.team.cvs.ui.cvsPerspective",
	SYNCHRONIZING_PERSPECTIVE_ID = "org.eclipse.team.ui.TeamSynchronizingPerspective",
	RESOURCE_PERSPECTIVE_ID = "org.eclipse.ui.resourcePerspective"
		;

	/**
	 * maximizes currently active Eclipse workbench
	 */
	public static void maximizeWorkbench()
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setMaximized(true);
	}

	/**
	 * minimizes currently active Eclipse workbench
	 */
	public static void minimizeWorkbench()
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setMinimized(true);
	}

	/**
	 * reveal a viewpart in the current perspective
	 * @param id
	 * @param maximize
	 * @return IViewPart or null on failure
	 */
	public static IViewPart showView(String id, boolean maximize)
	{
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart part = page.showView(id);
			
			// @tag tours fixme deprecated : in 3.3m6, isPageZoomed throws an internal classcastexception
			boolean isZoomed = false;
			try
			{ 
				isZoomed = page.isPageZoomed();
			}
			catch (Exception e)
			{
				ToursFxPlugin.log("page.isPageZoomed messed up - 3.3m6 issue",e);
			}
			
			if ( maximize )
			{
				if ( !isZoomed )
				{
					IViewReference ref = page.findViewReference(id);
					page.toggleZoom(ref);
				}
			}
			else 
			{
				if ( isZoomed )
				{
					IViewReference ref = page.findViewReference(id);
					page.toggleZoom(ref);  // unzooms 
				}
			}

			return part;
		} 
		catch (PartInitException e) {
			ToursFxPlugin.log("error while trying to showView id=" + id, e);
		}

		return null;
	}

	/**
	 * show editor associated with given marker in current perspective
	 * @param marker
	 * @param maximize
	 * @return IEditorPart or null on failure
	 */
	public static IEditorPart showEditor(IMarker marker, boolean maximize)
	{
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart part = IDE.openEditor(page, marker);
			
			// @tag tours fixme deprecated : in 3.3m6, isPageZoomed throws an internal classcastexception
			boolean isZoomed = false;
			try
			{ 
				isZoomed = page.isPageZoomed();
			}
			catch (Exception e)
			{
				ToursFxPlugin.log("page.isPageZoomed messed up - 3.3m6 issue",e);
			}
			
			
			if ( maximize )
			{
				if ( !isZoomed )
				{
					PartSite site = (PartSite) part.getSite();
					PartPane pane = site.getPane();
					pane.requestZoomIn();
				}
			}
			else
			{
				if ( isZoomed )
				{
					PartSite site = (PartSite) part.getSite();
					PartPane pane = site.getPane();
					pane.requestZoomOut();  
				}
			}

			return part;
		} 
		catch (PartInitException e) {
			ToursFxPlugin.log("error while trying to editor with marker=" + marker, e);
		}

		return null;
	}

	/**
	 * show editor associated with given file in current perspective
	 * @param file
	 * @param maximize
	 * @return IEditorPart or null on failure
	 */
	public static IEditorPart showEditor(IFile file, boolean maximize)
	{
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart part = IDE.openEditor(page, file);
			
			// @tag tours fixme deprecated : in 3.3m6, isPageZoomed throws an internal classcastexception
			boolean isZoomed = false;
			try
			{ 
				isZoomed = page.isPageZoomed();
			}
			catch (Exception e)
			{
				ToursFxPlugin.log("page.isPageZoomed messed up - 3.3m6 issue",e);
			}
			
			
			if ( maximize )
			{
				if ( !isZoomed )
				{
					PartSite site = (PartSite) part.getSite();
					PartPane pane = site.getPane();
					pane.requestZoomIn();
				}
			}

			return part;
		} 
		catch (PartInitException e) {
			ToursFxPlugin.log("error while trying to editor with file=" + file, e);
		}

		return null;
	}

	/**
	 * Maximize the currently active editor area
	 * @return
	 */
	public static IEditorPart maximizeActiveEditor()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart part = page.getActiveEditor();
		
		// @tag tours fixme deprecated : in 3.3m6, isPageZoomed throws an internal classcastexception
		boolean isZoomed = false;
		try
		{ 
			isZoomed = page.isPageZoomed();
		}
		catch (Exception e)
		{
			ToursFxPlugin.log("page.isPageZoomed messed up - 3.3m6 issue",e);
		}
		
		
		// @tag tours fixme deprecated : in 3.3m6, isPageZoomed throws an internal classcastexception
		if ( !isZoomed )
		{
			PartSite site = (PartSite) part.getSite();
			PartPane pane = site.getPane();
			pane.requestZoomIn();
		}

		return part;
	}

	/**
	 * generate bounding rectangles of all dialog-like shells derived from the workbench page
	 * @param exceptions : list of shells to ignore
	 * @return Rectangle[]
	 */
	public static Rectangle[] getShellBounds(IWorkbenchPage page, Shell[] exceptions)
	{
		Set<Shell> set = new HashSet<Shell>();
		Set<Rectangle> list = new HashSet<Rectangle>();
		
		for (Shell shell : exceptions)
			set.add(shell);
		
		Shell[] shells = page.getWorkbenchWindow().getShell().getDisplay().getShells();
		for (Shell shell : shells)
		{
			if ( !set.contains(shell) && shell.getChildren()!=null && shell.getChildren().length>0 && shell.getText()!=null && shell.getText().length()>0 )
			{
				list.add(shell.getBounds());
			}
		}
		
		return list.toArray(new Rectangle[list.size()]);
	}
	
	public static IViewPart maximizePackageExplorer()
	{
		return showView(PACKAGEEXPLORER_ID, true);
	}
	
	/**
	 * Maximize the currently active editor area
	 * @return
	 */
	public static IEditorPart getActiveEditor()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart part = page.getActiveEditor();
		return part;
	}

	/**
	 * switch to given perspective in current workbench window
	 * @param id
	 * @return IWorkbenchPage or null on failure
	 */
	public static IWorkbenchPage showPerspective(String id)
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			return PlatformUI.getWorkbench().showPerspective(id, window);
		} 
		catch (WorkbenchException e) {
			ToursFxPlugin.log("error showing perspective id=" + id, e);
		}

		return null;
	}

	/**
	 * unmaximize currently active viewpart or editor, restoring the previous layout of the current perspective.  this is useful if
	 * you have a series of calls to showEditor, showView with maximized=true, and you want to restore the original layout
	 */
	public static void restorePerspective()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		// @tag tours fixme deprecated : in 3.3m6, isPageZoomed throws an internal classcastexception
		boolean isZoomed = false;
		try
		{ 
			isZoomed = page.isPageZoomed();
		}
		catch (Exception e)
		{
			ToursFxPlugin.log("page.isPageZoomed messed up - 3.3m6 issue",e);
		}
		
		
		if ( isZoomed )
			page.zoomOut();
	}

	/**
	 * unmaximize currently active viewpart or editor, restoring the previous layout of the current perspective.  this is useful if
	 * you have a series of calls to showEditor, showView with maximized=true, and you want to restore the original layout
	 */
	public static void resetPerspective()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.resetPerspective();
	}
	
	
	/**
	 * returns true if given part is maximized
	 * @param part
	 * @return boolean
	 */
	public static boolean isMaximized(IWorkbenchPart part)
	{
		PartSite site           = (PartSite) part.getSite();
		PartPane pane 			= site.getPane();
		return pane.isZoomed();
	}
	
	/**
	 * makes sure the current workbench has focus
	 */
	public static void setFocus()
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
	}

	/**
	 * compute global display bounds of a viewpart (for use with AlphaFx.focus/unfocus)
	 * 
	 * @param part - IWorkbenchPart, IViewPart, IEditorPart
	 * @return Rectangle
	 */
	public static Rectangle getBounds(IWorkbenchPart part)
	{
		PartSite site           = (PartSite) part.getSite();
		if ( site==null )
			return null;
		PartPane pane 			= site.getPane();
		if ( pane==null )
			return null;
		LayoutPart layoutPart   = (LayoutPart) pane.getContainer();
		if ( layoutPart==null )
			return null;
		Control control         = layoutPart.getControl();
		if ( control==null )
			return null;
		
		Rectangle controlBounds = control.getBounds();
		Point     mapped = control.getDisplay().map(control, null, 0,0);

		return new Rectangle(mapped.x,mapped.y,controlBounds.width,controlBounds.height);
	}

	/**
	 * compute global display bounds of the perspective client area
	 * 
	 * @param page
	 * @return Rectangle
	 */
	public static Rectangle getBounds(IWorkbenchPage page)
	{
		Rectangle union = null;
		IViewReference[] references = page.getViewReferences();
		
		for(IViewReference reference : references)
		{
			IWorkbenchPart part = reference.getPart(false);
			
			if(part != null)
			{
				Rectangle bounds = getBounds(part);
				if(union == null)
					union = bounds;
				else if ( bounds!=null )
					union = union.union(bounds);
			}
		}
		Shell shell = page.getWorkbenchWindow().getShell();
		Display display = shell.getDisplay();
		Rectangle displayBounds = display.getBounds();
		//display.map(shell, display, union);
		//Rectangle bounds  = page.getWorkbenchWindow().getShell().getBounds();
		//return new Rectangle(bounds.x,bounds.y,bounds.width,bounds.height);
		return union;
	}
	
	/**
	 * compute the global display bounds of the upper trim, menu bar, and toolbar of a page
	 * 
	 * @param page
	 * @return Rectangle
	 */
	public static Rectangle getUpperAreaBounds(IWorkbenchPage page)
	{
		Rectangle all      = page.getWorkbenchWindow().getShell().getBounds();
		Rectangle pageArea = getBounds(page);
		return new Rectangle(all.x,all.y,all.width,pageArea.y-all.y);
	}
	
	/**
	 * compute the global display bounds of the lower status area of a page
	 * @param page
	 * @return Rectangle
	 */
	public static Rectangle getLowerAreaBounds(IWorkbenchPage page)
	{
		Rectangle all      = page.getWorkbenchWindow().getShell().getBounds();
		Rectangle pageArea = getBounds(page);
		return new Rectangle(all.x,pageArea.y+pageArea.height,all.width,all.y+all.height-(pageArea.y+pageArea.height));
	}
	
	/**
	 * generate all bounding rectangles of all different references on a page
	 * @param page
	 * @return Rectangle[]
	 */
	public static Rectangle[] getAllBounds(IWorkbenchPage page)
	{
		Set<Rectangle> list = new HashSet<Rectangle>();

		list.add(getUpperAreaBounds(page));
		list.add(getLowerAreaBounds(page));
		
		IViewReference[] viewRefs = page.getViewReferences();
		
		for(IViewReference reference : viewRefs)
		{
			IWorkbenchPart part = reference.getPart(false);
			if ( part!=null )
			{
				Rectangle bounds = getBounds(part);
				
				if(bounds!=null)
					list.add(bounds);
			}
		}
		
		IEditorReference[] editorRefs = page.getEditorReferences();
		
		for (IEditorReference reference : editorRefs)
		{
			IWorkbenchPart part = reference.getPart(false);
			
			if (part != null)
				list.add(getBounds(part));
		}
		
		return list.toArray(new Rectangle[list.size()]);
	}

	/**
	 * compute global display bounds of a viewpart (for use with AlphaFx.focus/unfocus).  does not include the "tab" area 
	 * 
	 * @param part - IWorkbenchPart, IViewPart, IEditorPart
	 * @return Rectangle
	 */
	public static Rectangle getClientBounds(IWorkbenchPart part)
	{
		PartSite site           = (PartSite) part.getSite();
		PartPane pane 			= site.getPane();
		Control control         = pane.getControl();
		Rectangle controlBounds = control.getBounds();
		Point     mapped = control.getDisplay().map(control, null, 0,0);

		return new Rectangle(mapped.x,mapped.y,controlBounds.width,controlBounds.height);
	}
	
	/**
	 * use reflection to find the "TreeViewer" associated with a part
	 * @param part
	 * @return Treeviewer or null
	 */
	public static TreeViewer findTreeViewer(IWorkbenchPart part)
	{
		Class clasz = part.getClass();
		
		while (clasz!=null)
		{
			Method[] methods = clasz.getDeclaredMethods();
			for (Method method : methods)
			{
				Class returnType   = method.getReturnType();
				String name        = method.getName();
				Class[] paramTypes = method.getParameterTypes();

				if ( returnType!=null && returnType.toString().equals(TreeViewer.class.toString()) && name.startsWith("get") && paramTypes.length==0 )
				{
					try {
						method.setAccessible(true);
						return (TreeViewer) method.invoke(part, new Object[]{});
					} catch (IllegalArgumentException e) {
						ToursFxPlugin.log("error while trying to invoke method=" + name + " on " + part,e);
					} catch (IllegalAccessException e) {
						ToursFxPlugin.log("error while trying to invoke method=" + name + " on " + part,e);
					} catch (InvocationTargetException e) {
						ToursFxPlugin.log("error while trying to invoke method=" + name + " on " + part,e);
					}
				}
			}
			clasz = clasz.getSuperclass();
		}
		
		return null;
	}
}
