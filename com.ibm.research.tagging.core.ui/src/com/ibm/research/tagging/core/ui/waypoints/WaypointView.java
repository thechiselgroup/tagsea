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
package com.ibm.research.tagging.core.ui.waypoints;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.IWaypointListener;
import com.ibm.research.tagging.core.IWaypointModelListener;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.ITagSelectionListener;
import com.ibm.research.tagging.core.ui.TagUIPlugin;
import com.ibm.research.tagging.core.ui.controls.ExpressionFilteredTable;
import com.ibm.research.tagging.core.ui.controls.ExpressionPatternFilter;
import com.ibm.research.tagging.core.ui.controls.FilteredTable;
import com.ibm.research.tagging.core.ui.dnd.WaypointTableDragListener;
import com.ibm.research.tagging.core.ui.dnd.WaypointTransfer;
import com.ibm.research.tagging.core.ui.fieldassist.ExpressionProposalProvider;

/**
 * 
 * @author mdesmond
 *
 */
public class WaypointView extends ViewPart
{
	public static final String ID = "com.ibm.research.tagging.core.ui.waypoints.WaypointView";
	private static final long REFRESH_DELAY = 500; //msecs		

	private class WaypointListener implements IWaypointListener
	{
		public void tagAdded(IWaypoint waypoint, ITag tag) 
		{
			Display.getDefault().asyncExec(new Runnable() {
			
				public void run() 
				{
					fWaypointViewer.setSelection(fWaypointViewer.getSelection());
				}
			
			});
		}

		public void tagRemoved(IWaypoint waypoint, ITag tag) 
		{
			Display.getDefault().asyncExec(new Runnable() {
				
				public void run() 
				{
					fWaypointViewer.setSelection(fWaypointViewer.getSelection());
				}
			
			});
		}

		public void waypointChanged(IWaypoint waypoint) 
		{
			Display.getDefault().asyncExec(new Runnable() {
				
				public void run() 
				{
					fWaypointViewer.setSelection(fWaypointViewer.getSelection());
					scheduleViewerRefresh();
				}
			
			});
		}
	}
	
	private class WaypointCollectionListener implements IWaypointModelListener
	{
		/*
		 * (non-Javadoc)
		 * @see com.ibm.research.tagging.core.IWaypointCollectionListener#waypointAdded(com.ibm.research.tagging.core.IWaypoint)
		 */
		public void waypointAdded(IWaypoint waypoint) 
		{
			waypoint.addWaypointListener(waypointListener);
			scheduleViewerRefresh();
		}

		/*
		 * (non-Javadoc)
		 * @see com.ibm.research.tagging.core.IWaypointCollectionListener#waypointRemoved(com.ibm.research.tagging.core.IWaypoint)
		 */
		public void waypointRemoved(IWaypoint waypoint) 
		{
			waypoint.removeWaypointListener(waypointListener);
			scheduleViewerRefresh();
		}
	}

	private class TagSelectionListener implements ITagSelectionListener
	{
		/*
		 * (non-Javadoc)
		 * @see com.ibm.research.tagging.core.ui.ITagSelectionListener#tagsSelected(com.ibm.research.tagging.core.ITag[])
		 */
		public void tagsSelected(ITag[] tags) 
		{
			Display.getDefault().asyncExec(new Runnable() {
				
				public void run() 
				{
					fWaypointViewer.refresh();
				}
			
			});
		}
	}
	
	private FilteredTable fFilteredTable;
	private TableViewer fWaypointViewer;
	private Composite fRoot;
	private List<IWaypointViewListener> fListeners;
	private FormToolkit fFormToolkit;
	private TagHyperLinkListener fTagHyperLinkListener;
	private Section fPropertiesSection;
	private Section fTagsSection;
	private Button fEditPropertiesButton;
	private Composite fFormTextComposite;
	private Composite fPropertiesComposite;
	private Timer fRefreshTimer;
	private boolean fScheduleRefresh;
	private Composite fTagsSectionComposite;
	private FormText fTagsFormText;
	private Action deleteAction;
	
	IWaypointListener waypointListener = new WaypointListener();
	private TableColumn fNameColumn;
	private TableColumn fDescriptionColumn;


	@Override
	public void createPartControl(Composite parent) 
	{
		fFormToolkit = new FormToolkit(Display.getCurrent());

		// create the root with a 1 col gridlayout
		createRootComposite(parent);
		//create the waypoint viewer
		createWaypointTableViewer(fRoot);
		//create the collapsable tags box
		createWaypointTagsSection(fRoot);
		//create the collapsable properties box
		createWaypointPropertiesSection(fRoot);

		makeActions();
		hookContextMenu();
		contributeToActionBars();

		// refresh the waypoint table whenever a waypoint is added
		TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypointModelListener(new WaypointCollectionListener()); 

		// refresh the waypoint table whenever a tag is selected in the tag view
		TagUIPlugin.getDefault().getTagUI().addTagSelectionListener(new TagSelectionListener());

		addWaypointViewListener(new WaypointViewListener());

		fRefreshTimer = new Timer();
		
		fRefreshTimer.schedule(new TimerTask() 
		{
			@Override
			public void run() 
			{
				if(fScheduleRefresh)
				{
					fScheduleRefresh = false;

					Display.getDefault().asyncExec(new Runnable() 
					{
						public void run() 
						{
							if(!fWaypointViewer.getTable().isDisposed())
								fWaypointViewer.refresh();
						}

					});
				}
			}

		},0, REFRESH_DELAY);
	}

	private void createWaypointPropertiesSection(Composite parent) 
	{
		fPropertiesSection = fFormToolkit.createSection(parent,Section.SHORT_TITLE_BAR |Section.TWISTIE);
		fPropertiesSection.setText("Properties");
		fPropertiesSection.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		fPropertiesComposite = fFormToolkit.createComposite(fPropertiesSection);
		GridLayout propertiesLayout = new GridLayout(2,false);
		propertiesLayout.marginWidth = 0;
		propertiesLayout.marginHeight = 0;
		propertiesLayout.verticalSpacing = 0;
		propertiesLayout.marginTop = 0;
		propertiesLayout.marginBottom = 3;
		propertiesLayout.marginLeft = 0;
		propertiesLayout.marginRight = 0;
		propertiesLayout.horizontalSpacing = 0;
		fPropertiesComposite.setLayout(propertiesLayout);
		fPropertiesComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		fFormTextComposite = fFormToolkit.createComposite(fPropertiesComposite);
		fFormTextComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fFormTextComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		GridLayout textCompositeLayout = new GridLayout();
		textCompositeLayout.marginWidth = 0;
		textCompositeLayout.marginTop = 0;
		textCompositeLayout.marginBottom = 0;
		fFormTextComposite.setLayout(textCompositeLayout);
		
		fEditPropertiesButton = fFormToolkit.createButton(fPropertiesComposite, "Edit...", SWT.PUSH);
		fEditPropertiesButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		fEditPropertiesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) fWaypointViewer.getSelection();
				if ( selection!=null && selection.size()==1 )
				{
					final IWaypoint waypoint = (IWaypoint) selection.getFirstElement();
					if ( TagUIPlugin.getDefault().getTagUI().editProperties(waypoint) )
					{
						setSelectedWaypoint(waypoint);
						Display.getDefault().asyncExec(new Runnable() {
							public void run() 
							{
								fWaypointViewer.refresh();
							}
						});					
					}
				}
			}
		});
		
		fPropertiesSection.setClient(fPropertiesComposite);
		fPropertiesSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fPropertiesSection.setExpanded(true);
		
		clearSelectedWaypoint();
	}

	private void createWaypointTagsSection(Composite parent) 
	{
		fTagsSection = fFormToolkit.createSection(parent,Section.SHORT_TITLE_BAR |Section.TWISTIE);
		fTagsSection.setText("Tags");	
		fTagsSection.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		fTagsSectionComposite = fFormToolkit.createComposite(fTagsSection,SWT.NONE);
		fTagsSectionComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fTagsSectionComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		GridLayout tagsCompositeLayout = new GridLayout();
		tagsCompositeLayout.marginWidth = 0;
		tagsCompositeLayout.marginHeight = 0;
		tagsCompositeLayout.verticalSpacing = 0;
		tagsCompositeLayout.marginTop = 0;
		tagsCompositeLayout.marginBottom = 3;
		tagsCompositeLayout.marginLeft = 0;
		tagsCompositeLayout.marginRight = 0;
		tagsCompositeLayout.horizontalSpacing = 0;
		
		fTagsSectionComposite.setLayout(tagsCompositeLayout);

		fTagsFormText = fFormToolkit.createFormText(fTagsSectionComposite, true);
		fTagsFormText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fTagHyperLinkListener = new TagHyperLinkListener();
		fTagsFormText.addHyperlinkListener(fTagHyperLinkListener);
		fTagsFormText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
	
		fTagsSection.setClient(fTagsSectionComposite);
		GridData tagSectionData = new GridData(GridData.FILL_HORIZONTAL);
		fTagsSection.setLayoutData(tagSectionData);
		fTagsSection.setExpanded(true);
		
		clearTags();
	}

	private void createRootComposite(Composite parent) 
	{
		fRoot = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.horizontalSpacing = 0;
		fRoot.setLayout(layout);
		fRoot.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
	}

	private void createWaypointTableViewer(Composite parent) 
	{			
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 3;
		layout.marginHeight = 3;
		layout.verticalSpacing = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		
		fFilteredTable = new ExpressionFilteredTable(composite,SWT.MULTI | SWT.V_SCROLL, new ExpressionPatternFilter());
		fFilteredTable.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		fWaypointViewer = fFilteredTable.getViewer();
		fWaypointViewer.setContentProvider(new WaypointTableContentProvider());
		fWaypointViewer.setLabelProvider(new WaypointTableLabelProvider());
		fWaypointViewer.setSorter(new WaypointTableSorter());
		fWaypointViewer.addDragSupport(DND.DROP_MOVE|DND.DROP_COPY, new Transfer[]{WaypointTransfer.getInstance()/*, PluginTransfer.getInstance()*/}, new WaypointTableDragListener(fWaypointViewer));
		fWaypointViewer.addSelectionChangedListener(new WaypointTableSelectionChangedListener());
		fWaypointViewer.addDoubleClickListener(new WaypointTableDoubleClickListener());
		
		Table table = fWaypointViewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		
		fNameColumn = new TableColumn(table,SWT.LEFT,0);
		fNameColumn.setMoveable(false);
		fNameColumn.setWidth(240);
		
		fDescriptionColumn = new TableColumn(table,SWT.LEFT,1);
		fDescriptionColumn.setMoveable(false);
		fDescriptionColumn.setWidth(640);
		
		// auto-resize columns on any change to the table
		table.addPaintListener(new PaintListener() 
		{
			public void paintControl(PaintEvent e) 
			{
				Table table = (Table) e.widget;
				Rectangle clientSize = table.getClientArea();
				int newWidth = clientSize.width - fNameColumn.getWidth();
				
				// this check prevents recursion
				if ( fDescriptionColumn.getWidth()!=newWidth )
					fDescriptionColumn.setWidth(newWidth);
			}
		});
		
		fWaypointViewer.setInput(new Object());
	}

	private void makeActions() 
	{		
		deleteAction = new Action() 
		{
			@Override
			public void run()
			{
				TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
				{
					public void run() 
					{
						for(IWaypointViewListener listener : getViewListeners())
							listener.deleteWaypoint(WaypointView.this);
					}
				});
			}
		};
		deleteAction.setText("Delete");
		deleteAction.setToolTipText("Delete selected waypoints(s)");
		deleteAction.setImageDescriptor(TagUIPlugin.getDefault().getImageRegistry().getDescriptor(TagUIPlugin.IMG_DELETE));
	}

	private void hookContextMenu() 
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() 
		{
			public void menuAboutToShow(IMenuManager manager) 
			{
				WaypointView.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(fWaypointViewer.getControl());
		fWaypointViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fWaypointViewer);
	}

	private void contributeToActionBars() 
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) 
	{
		manager.add(deleteAction);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) 
	{
		manager.add(deleteAction);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) 
	{
		manager.add(deleteAction);
		manager.add(new Separator());
	}

	@Override
	public void setFocus() 
	{

	}

	/**
	 * Get the tag table viewer
	 * @return the tag table viewer
	 */
	public TableViewer getWaypointTableViewer()
	{
		return fWaypointViewer;
	}

	/**
	 * @param waypoint
	 */
	public void setSelectedWaypoint(IWaypoint waypoint) 
	{
		if(fPropertiesSection.isExpanded())
		{
			Control[] children = fFormTextComposite.getChildren();
			for(Control child : children)
				child.dispose();

			FormText text = TagUIPlugin.getDefault().getTagUI().getProperties(fFormTextComposite, waypoint);
			fFormToolkit.adapt(text);
			text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			
			GridData wrapAround = new GridData(GridData.FILL_HORIZONTAL);
			wrapAround.widthHint = fFormTextComposite.getSize().x;
			text.setLayoutData(wrapAround);
			
			fEditPropertiesButton.setEnabled(true);
		}
	}

	/**
	 *
	 */
	public void clearSelectedWaypoint() 
	{
		if(fPropertiesSection.isExpanded())
		{
			Control[] children = fFormTextComposite.getChildren();

			for(Control child : children)
				child.dispose();

			Label label = fFormToolkit.createLabel(fFormTextComposite, "Select a single waypoint to see its properties.");
			label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			
			fEditPropertiesButton.setEnabled(false);
		}
	}

	protected void setTags(ITag[] tags)
	{
		if(fTagsSection.isExpanded())
		{
			StringBuffer buffer = new StringBuffer();

			buffer.append("<form>");

			buffer.append("<p>");

			if(tags.length > 0)
			{
				boolean bold = true;
				
				for(ITag tag : tags)
				{
					// @tag hack tagsea urlencoding : currently using URLEncoder as stopgap to deal with tags with illegal chars that would bomb HTML formatting - this will render ok, but the hyperlinking may not work
					buffer.append("<a href=\"" + URLEncoder.encode(tag.getName()) + "\">" + tag.getName() + "</a>  ");
					bold = !bold;
				}
			}
			else
				buffer.append("This waypoint contains no tags. ");

			buffer.append("</p>");

			buffer.append("</form>");

			GridData wrapAround = new GridData(GridData.FILL_HORIZONTAL);
			wrapAround.widthHint = fTagsSectionComposite.getSize().x;

			fTagsFormText.setLayoutData(wrapAround);
			
			fTagsFormText.setText(buffer.toString(), true, true);
		}
	}
	
	public void clearTags() 
	{
		if(fTagsSection.isExpanded())
			fTagsFormText.setText("Select a waypoint to view its tags.", false, false);
		
	}

//	protected void setTags(ITag[] tags)
//	{
//		if(fTagsSection.isExpanded())
//		{
//			Control[] children = fDisposabeComposite.getChildren();
//			Boolean labelShowing = false;
//
//			// remove the existing controls
//			for(Control child : children)
//			{
//				if(child instanceof Hyperlink)
//				{
//					Hyperlink link = (Hyperlink)child;
//					link.removeHyperlinkListener(fTagHyperLinkListener);
//					link.dispose();
//				}
//				else if(child instanceof Label)
//					child.dispose();
//			}
//
//			if(tags.length != 0)
//			{
//				// add the new hyperlinks
//				for(ITag tag : tags)
//				{
//					Hyperlink link = fFormToolkit.createHyperlink(fDisposabeComposite, tag.getName(), SWT.NONE);
//					link.addHyperlinkListener(fTagHyperLinkListener);
//					link.setUnderlined(true);
//				}
//			}
//			else
//			{
//				fFormToolkit.createLabel(fDisposabeComposite, "Select a waypoint to see its tags.");
//				labelShowing = true;
//			}
//
//			int numberOfChildren = fDisposabeComposite.getChildren().length;
//
//			if(numberOfChildren > 0 && !labelShowing)
//			{
//				fDisposabeComposite.layout(true, true);
//				children = fDisposabeComposite.getChildren();
//				Hyperlink firstlink = (Hyperlink)children[0];
//				Hyperlink lastLink = (Hyperlink)children[numberOfChildren - 1];
//
//				int top = firstlink.getBounds().y;
//				int bottom = lastLink.getBounds().y + lastLink.getBounds().height;
//				int height = bottom - top;
//
//				GridData data = new GridData();
//				data.heightHint = height;
//				fExpandButton.setLayoutData(data);
//			}
//			else
//			{
//				GridData data = new GridData();
//				data.heightHint = fExpandButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
//				fExpandButton.setLayoutData(data);
//			}
//
//			fRoot.layout(true, true);
//			//fRoot.redraw();
//		}
//	}
	
	
	/**
	 * Add a TagViewListener
	 * @param listener
	 */
	public synchronized void addWaypointViewListener(IWaypointViewListener listener)
	{
		if(!getViewListeners().contains(listener))
			getViewListeners().add(listener);
	}

	/**
	 * Remove a TagViewListener
	 * @param listener
	 */
	public synchronized void removeWaypointViewListener(IWaypointViewListener listener)
	{
		getViewListeners().remove(listener);
	}

	/**
	 * Get the listeners
	 * @return
	 */
	private List<IWaypointViewListener> getViewListeners() 
	{
		if(fListeners == null)
			fListeners = new ArrayList<IWaypointViewListener>();

		return fListeners;
	}

	public void scheduleViewerRefresh()
	{
		fScheduleRefresh = true;
	}
	
	protected void refreshSections()
	{
		fRoot.layout(true,true);
		fRoot.redraw();
	}

	@Override
	public void dispose() 
	{
		fRefreshTimer.cancel();
		super.dispose();
	}
}