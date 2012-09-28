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
package com.ibm.research.tagging.core.ui.tags;

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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.ITagModelListener;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.IWaypointListener;
import com.ibm.research.tagging.core.IWaypointModelListener;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.TagUIPlugin;
import com.ibm.research.tagging.core.ui.controls.FilteredTable;
import com.ibm.research.tagging.core.ui.controls.NQueryTablePatternFilter;

/**
 * @author mdesmond
 */
public class TagView extends ViewPart
{
	public static final String ID = "com.ibm.research.tagging.core.ui.tags.TagView";
	private static final long REFRESH_DELAY = 1000; //msecs
	
	private class TagCollectionListener implements ITagModelListener
	{
		/*
		 * (non-Javadoc)
		 * @see com.ibm.research.tagging.core.ITagCollectionListener#tagAdded(com.ibm.research.tagging.core.ITag)
		 */
		public void tagAdded(ITag tag) 
		{
			//fTagViewer.refresh();
			scheduleRefresh();
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.ibm.research.tagging.core.ITagCollectionListener#tagRemoved(com.ibm.research.tagging.core.ITag, com.ibm.research.tagging.core.IWaypoint[])
		 */
		public void tagRemoved(ITag tag, IWaypoint[] waypoints) 
		{
			//fTagViewer.refresh();
			scheduleRefresh();
		}

		/*
		 * (non-Javadoc)
		 * @see com.ibm.research.tagging.core.ITagCollectionListener#tagRenamed(com.ibm.research.tagging.core.ITag, java.lang.String)
		 */
		public void tagRenamed(ITag tag, String oldName) 
		{
			//fTagViewer.refresh();
			scheduleRefresh();
		}
	}
	
	private class WaypointListener implements IWaypointListener
	{
		public void tagAdded(IWaypoint waypoint, ITag tag) 
		{
			//fTagViewer.refresh();
			scheduleRefresh();
		}

		public void tagRemoved(IWaypoint waypoint, ITag tag) 
		{
			//fTagViewer.refresh();
			scheduleRefresh();
		}
		
		public void waypointChanged(IWaypoint waypoint) 
		{
			
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
			waypoint.addWaypointListener(fWaypointListener);
			scheduleRefresh();
			
			//for(ITag tag : waypoint.getTags())
			//	fTagViewer.refresh(tag, true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.ibm.research.tagging.core.IWaypointCollectionListener#waypointRemoved(com.ibm.research.tagging.core.IWaypoint)
		 */
		public void waypointRemoved(IWaypoint waypoint) 
		{
			waypoint.removeWaypointListener(fWaypointListener);
			scheduleRefresh();
			
			//for(ITag tag : waypoint.getTags())
			//	fTagViewer.refresh(tag, true);
		}
	}
	
	private boolean fScheduleRefresh = false;
	private Timer fRefreshTimer;
	private TableViewer fTagViewer;
	private List<ITagViewListener> fListeners;
	private Action fCreateTagAction;
	private Action fDeleteTagAction;
	private Action fRenameTagAction;
	private Action fLinkWithWaypointsViewAction;
	private Action fHideUnusedTags;
	private Action fDeleteUnusedTags;
	private Action fSelectAllTags;
	private UnusedTagsViewerFilter fHideUnusedTagsFilter;
	private Composite fRoot;
	private Composite fTagsSectionComposite;
	private Section fTagsSection;
	private FormToolkit fFormToolkit;
	private Label fTagsTotalsLabel;
	private IWaypointListener fWaypointListener = new WaypointListener();
	
	@Override
	public void createPartControl(Composite parent) 
	{
		fFormToolkit = new FormToolkit(Display.getDefault());
		createRootComposite(parent); 
		createTagTableViewer(fRoot);
		createStatisticsSection(fRoot); 
		
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		
		// Hook model listeners
		TagCorePlugin.getDefault().getTagCore().getTagModel().addTagModelListener(new TagCollectionListener());
		TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypointModelListener(new WaypointCollectionListener());
	
		// Hook view listener
		addTagViewListener(new TagViewListener());
		fRefreshTimer = new Timer();
		fRefreshTimer.schedule(new TimerTask() 
		{
			@Override
			public void run() 
			{
				if(fScheduleRefresh == true)
				{
					fScheduleRefresh = false;
					
					Display.getDefault().asyncExec(new Runnable() 
					{
						public void run() 
						{
							if(!fTagViewer.getTable().isDisposed())
								fTagViewer.refresh();
							
							refreshPropertiesSection();
						}
					});
				}
			}
		
		},0, REFRESH_DELAY);
	}
	
	private void createRootComposite(Composite parent) 
	{
		fRoot = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		fRoot.setLayout(layout);
		fRoot.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
	}
	
	private void createStatisticsSection(Composite parent) 
	{
		fTagsSection = fFormToolkit.createSection(parent,Section.SHORT_TITLE_BAR |Section.TWISTIE);
		fTagsSection.setText("Properties");	
		fTagsSection.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		fTagsSectionComposite = fFormToolkit.createComposite(fTagsSection,SWT.NONE);
		fTagsSectionComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fTagsSectionComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		GridLayout tagsCompositeLayout = new GridLayout(2,false);
		
		tagsCompositeLayout.marginHeight = 0;
		tagsCompositeLayout.marginWidth = 0;
		tagsCompositeLayout.marginTop = 0;
		tagsCompositeLayout.marginBottom = 2;
		tagsCompositeLayout.marginLeft = 2;
		tagsCompositeLayout.marginRight = 0;
		tagsCompositeLayout.verticalSpacing = 0;
		tagsCompositeLayout.horizontalSpacing = 4;
		
		fTagsSectionComposite.setLayout(tagsCompositeLayout);

		Label totalLabel = fFormToolkit.createLabel(fTagsSectionComposite,"");
		totalLabel.setImage(TagUIPlugin.getDefault().getImageRegistry().get(TagUIPlugin.IMG_TAG));
		totalLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		fTagsTotalsLabel = fFormToolkit.createLabel(fTagsSectionComposite, ""); 
		fTagsTotalsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fTagsTotalsLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		fTagsSection.setClient(fTagsSectionComposite);
		GridData tagSectionData = new GridData(GridData.FILL_HORIZONTAL);
		fTagsSection.setLayoutData(tagSectionData);
		fTagsSection.setExpanded(true);
		
		refreshPropertiesSection();

	}
	
	private void createTagTableViewer(Composite parent) 
	{	
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		composite.setLayout(layout);
		
		FilteredTable filteredTable = new FilteredTable(composite,
													    SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL,
													    new NQueryTablePatternFilter());
				
		filteredTable.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		fTagViewer = filteredTable.getViewer();
		fTagViewer.setContentProvider(new TagTableContentProvider());
		fTagViewer.setLabelProvider(new TagTableLabelProvider());
		fTagViewer.setSorter(new TagTableSorter());
		fHideUnusedTagsFilter = new UnusedTagsViewerFilter();
		fTagViewer.addFilter(fHideUnusedTagsFilter);
		fTagViewer.addDoubleClickListener(new TagTableDoubleClickListener());
		fTagViewer.addSelectionChangedListener(new TagTableSelectionChangedListener());
		Table table = fTagViewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(false);
		fTagViewer.setInput(new Object());
	}
	
	private void makeActions() 
	{
		fCreateTagAction = new Action() 
		{
			public void run() 
			{
				TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
				{
					public void run() 
					{
						for(ITagViewListener listener : getViewListeners())
							listener.createTag(TagView.this);
					}
				});
			}
		};
		fCreateTagAction.setText("New tag...");
		fCreateTagAction.setToolTipText("New tag...");
		fCreateTagAction.setImageDescriptor(TagUIPlugin.getDefault().getImageRegistry().getDescriptor(TagUIPlugin.IMG_ADD));
		
		fDeleteTagAction = new Action() 
		{
			public void run() 
			{
				TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
				{
					public void run() 
					{
						for(ITagViewListener listener : getViewListeners())
							listener.deleteTag(TagView.this);
					}
				});
			}
		};
		fDeleteTagAction.setText("Delete");
		fDeleteTagAction.setToolTipText("Delete selected tag(s)");
		fDeleteTagAction.setImageDescriptor(TagUIPlugin.getDefault().getImageRegistry().getDescriptor(TagUIPlugin.IMG_DELETE));
	
		fRenameTagAction = new Action() 
		{
			public void run() 
			{
				TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
				{
					public void run() 
					{
						for(ITagViewListener listener : getViewListeners())
							listener.renameTag(TagView.this);
					}
				});
			}
		};
		fRenameTagAction.setText("Rename");
		fRenameTagAction.setToolTipText("Rename selected tag");
		fRenameTagAction.setImageDescriptor(TagUIPlugin.getDefault().getImageRegistry().getDescriptor(TagUIPlugin.IMG_RENAME));

		fLinkWithWaypointsViewAction = new Action() 
		{
			public void run() 
			{
			}
		};
		fLinkWithWaypointsViewAction.setText("Link to waypoint view");
		fLinkWithWaypointsViewAction.setToolTipText("Link to waypoint view");
		fLinkWithWaypointsViewAction.setImageDescriptor(TagUIPlugin.getDefault().getImageRegistry().getDescriptor(TagUIPlugin.IMG_LINKWITH));
		fLinkWithWaypointsViewAction.setChecked(true);
		
		fHideUnusedTags= new Action() 
		{
			public void run() 
			{
				fHideUnusedTagsFilter.enable(isChecked());
				fTagViewer.refresh();
			}
		};
		fHideUnusedTags.setText("Filter unused tags");
		fHideUnusedTags.setToolTipText("Filter unused tags");
		fHideUnusedTags.setImageDescriptor(TagUIPlugin.getDefault().getImageRegistry().getDescriptor(TagUIPlugin.IMG_FILTER));
		fHideUnusedTags.setChecked(false);
		
		fDeleteUnusedTags= new Action() 
		{
			public void run() 
			{
				TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
				{
					public void run() 
					{
						for(ITagViewListener listener : getViewListeners())
							listener.deleteUnusedTags(TagView.this);
					}
				});
			}
		};
		fDeleteUnusedTags.setText("Delete unused tags");
		fDeleteUnusedTags.setToolTipText("Delete unused tags");
		fDeleteUnusedTags.setImageDescriptor(TagUIPlugin.getDefault().getImageRegistry().getDescriptor(TagUIPlugin.IMG_DELETE_UNUSED));

		// this is a pull-down menu item, not a toolbar action - no icon added
		fSelectAllTags = new Action() 
		{
			public void run() 
			{
				ITag[] tags = TagCorePlugin.getDefault().getTagCore().getTagModel().getTags();
				fTagViewer.setSelection(new StructuredSelection(tags));
			}
		};
		fSelectAllTags.setText("Select all tags");
		fSelectAllTags.setToolTipText("Select all tags");
	}

	private void hookContextMenu() 
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() 
		{
			public void menuAboutToShow(IMenuManager manager) 
			{
				TagView.this.fillContextMenu(manager);
			}
		});
		
		Menu menu = menuMgr.createContextMenu(fTagViewer.getControl());
		fTagViewer.getControl().setMenu(menu);
		
		// @tag context-menu : to add a context menu to the TagView, use ID's value for the targetID, not the actual plugin id, since there can be multiple views in the same plugin accepting context menu contributions
		getSite().registerContextMenu(ID, menuMgr, fTagViewer);
	}
	
	private void contributeToActionBars() 
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalPullDown(IMenuManager manager) 
	{
		manager.add(fCreateTagAction);
		manager.add(fDeleteTagAction);
		manager.add(new Separator());
		manager.add(fRenameTagAction);
		manager.add(new Separator());
		manager.add(fLinkWithWaypointsViewAction);
		manager.add(new Separator());
		manager.add(fHideUnusedTags);
		manager.add(new Separator());
		manager.add(fDeleteUnusedTags);
		manager.add(new Separator());
		manager.add(fSelectAllTags);
		manager.add(new Separator());
		
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillContextMenu(IMenuManager manager) 
	{
		manager.add(fCreateTagAction);
		manager.add(fDeleteTagAction);
		manager.add(new Separator());
		manager.add(fRenameTagAction);
		manager.add(new Separator());
		manager.add(fLinkWithWaypointsViewAction);
		manager.add(new Separator());
		manager.add(fHideUnusedTags);
		manager.add(new Separator());
		manager.add(fDeleteUnusedTags);
		manager.add(new Separator());
		manager.add(fSelectAllTags);
		manager.add(new Separator());
		
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) 
	{
		manager.add(fCreateTagAction);
		manager.add(fDeleteTagAction);
		manager.add(new Separator());
		manager.add(fRenameTagAction);
		manager.add(new Separator());
		manager.add(fLinkWithWaypointsViewAction);
		manager.add(new Separator());
		manager.add(fHideUnusedTags);
		manager.add(new Separator());
		manager.add(fDeleteUnusedTags);
		manager.add(new Separator());
		
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	@Override
	public void setFocus() 
	{
		
	}
	
	/**
	 * Get the tag table viewer
	 * @return the tag table viewer
	 */
	public TableViewer getTagTableViewer()
	{
		return fTagViewer;
	}
	
	/**
	 * Check if the tag view is linked with the waypoints view
	 * @return
	 */
	public boolean isLinkedWithWaypointView()
	{
		return fLinkWithWaypointsViewAction.isChecked();
	}

	/**
	 * Get the create tag action
	 * @return the create tag action
	 */
	public Action getCreateTagAction() 
	{
		return fCreateTagAction;
	}
	
	/**
	 * Get the Delete tag action
	 * @return the Delete tag action
	 */
	public Action getDeleteTagAction() 
	{
		return fDeleteTagAction;
	}

	/**
	 * Get the LinkWithWaypointsView tag action
	 * @return the LinkWithWaypointsView tag action
	 */
	public Action getLinkWithWaypointsViewAction() 
	{
		return fLinkWithWaypointsViewAction;
	}

	/**
	 * Get the Rename tag action
	 * @return the Rename tag action
	 */
	public Action getRenameTagAction() 
	{
		return fRenameTagAction;
	}
	
	/**
	 * Add a TagViewListener
	 * @param listener
	 */
	public synchronized void addTagViewListener(ITagViewListener listener)
	{
		if(!getViewListeners().contains(listener))
			getViewListeners().add(listener);
	}
	
	/**
	 * Remove a TagViewListener
	 * @param listener
	 */
	public synchronized void removeTagViewListener(ITagViewListener listener)
	{
		getViewListeners().remove(listener);
	}
	
	/**
	 * Get the listeners
	 * @return
	 */
	private List<ITagViewListener> getViewListeners() 
	{
		if(fListeners == null)
			fListeners = new ArrayList<ITagViewListener>();
		
		return fListeners;
	}
	
	private void scheduleRefresh()
	{
		fScheduleRefresh = true;
	}
	
	private void refreshPropertiesSection()
	{
		ITag[] allTags = TagCorePlugin.getDefault().getTagCore().getTagModel().getTags();
		
		int unused = 0;
		
		for(ITag tag : allTags)
			if(tag.getWaypointCount() == 0)
				unused ++;
		
		fTagsTotalsLabel.setText(allTags.length + " Tags (" + unused + " unused)");
	}
	
	@Override
	public void dispose() 
	{
		fRefreshTimer.cancel();
		super.dispose();
	}
}