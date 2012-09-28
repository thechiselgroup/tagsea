package net.sourceforge.tagsea.cloudsee.views;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.TagSEAUtils;
import net.sourceforge.tagsea.clouds.widgets.Cloud;
import net.sourceforge.tagsea.clouds.widgets.CloudItem;
import net.sourceforge.tagsea.cloudsee.CloudSeePlugin;
import net.sourceforge.tagsea.cloudsee.ITagseaImages;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITagChangeListener;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

public class CloudSeeView extends ViewPart {

	private Cloud cloud;
	private String rootTag;
	
	private ITagChangeListener tagsListener;
	private Timer refreshTimer;
	private TimerTask refreshTask;
	private Action backAction;
	private Action forwardAction;
	private Action hierarchyAction;
	protected boolean hierarchy;
	private String basePartName;

	public CloudSeeView() {
		tagsListener = new ITagChangeListener() {
			public void tagsChanged(TagDelta delta) {
				scheduleViewerRefresh();
			}
		};
		refreshTimer = new Timer();
		hierarchy = true;
	}

	@Override
	public void createPartControl(Composite parent) {
		this.basePartName = getPartName();
		Composite page = new Composite(parent, SWT.NONE);
		page.setBackground(page.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		page.setLayout(layout);
		this.cloud = new Cloud(page, SWT.DOUBLE_BUFFERED | SWT.MULTI);
		cloud.setBackground(page.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		cloud.setLayout(new FillLayout(SWT.HORIZONTAL));
		this.rootTag = null;
		makeActions();
		cloud.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button != 1) return;
				CloudItem item = cloud.findItemAt(e.x, e.y);
				if (item != null) {
					if (item.getData("children") != null) {
						rootTag = (String) item.getData();
						refreshTags();
					}
				}
				super.mouseDoubleClick(e);
			}
		});
		cloud.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				CloudItem[] selection = cloud.getSelection();
				if (selection.length > 0) {
					forwardAction.setEnabled(selection[0].getData("children") != null);
				} else {
					forwardAction.setEnabled(false);
				}
				
			}
		});
		cloud.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.CR:
					if (forwardAction.isEnabled()) {
						forwardAction.run();
					}
					break;
				case SWT.BS:
					if (backAction.isEnabled()) {
						backAction.run();
					}
					break;
				}
			}
			public void keyReleased(KeyEvent e) {
			}
		});
		
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() 
		{
			public void menuAboutToShow(IMenuManager manager) 
			{
				manager.add(backAction);
				manager.add(forwardAction);
				manager.add(new Separator());
				manager.add(hierarchyAction);
			}
		});

		Menu menu = menuMgr.createContextMenu(cloud);
		cloud.setMenu(menu);
		//getSite().registerContextMenu(menuMgr, cloud);
		
		new ToolTip(cloud) {
			private Composite page;
			@Override
			protected Composite createToolTipContentArea(Event event,
					Composite parent) {
				if (page != null && !page.isDisposed()) {
					page.dispose();
				}
				page = new Composite(parent, SWT.NONE);
				page.setLayout(new FillLayout());
				page.setBackground(page.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				Label l = new Label(page, SWT.NONE);
				CloudItem item = cloud.findItemAt(event.x, event.y);
				if (item != null) {
					l.setText(item.getData().toString() + ": " + item.getPriority() + " waypoints");
				} else {
					l.setText("Tag Cloud");
				}
				l.setBackground(page.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				page.pack();
				Point size = page.computeSize(-1, -1);
				Rectangle clientArea = event.display.getClientArea();
				Point displayCoordinates = ((Composite)event.widget).toDisplay(event.x, event.y);
				Point cursorSize = page.getDisplay().getCursorSizes()[0];
				int xShift = cursorSize.x/2;
				if (size.x + xShift + displayCoordinates.x > clientArea.width) {
					xShift = -size.x;
				}
				int yShift = cursorSize.y/2;
				if (size.y + yShift + displayCoordinates.y > clientArea.height) {
					yShift = -size.y;
				}
				setShift(new Point(xShift, yShift));
				return page;
			}
			
		};
		fillToolbar();
		refreshTags();
		page.layout();
		TagSEAPlugin.addTagChangeListener(tagsListener);
	}

	/**
	 * 
	 */
	private void fillToolbar() {
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.add(backAction);
		manager.add(forwardAction);
		manager.add(new Separator());
		manager.add(hierarchyAction);
	}

	/**
	 * 
	 */
	private void makeActions() {
		ITagseaImages images = CloudSeePlugin.getDefault().getImages();
		ImageDescriptor descriptorBack = images.getDescriptor(ITagseaImages.IMG_UP_ARROW);
		ImageDescriptor descriptorBackDisabled = images.getDescriptor(ITagseaImages.IMG_UP_ARROW_DISABLED);
		ImageDescriptor hierarchyDescriptor = TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_TAG_HIERARCHY);
		backAction = new Action("Move Out", descriptorBack) {
			@Override
			public void run() {	
				if (rootTag != null) {
					int dot = rootTag.lastIndexOf('.');
					if (dot != -1) {
						rootTag = rootTag.substring(0, dot);
						refreshTags();
					} else {
						rootTag = null;
						refreshTags();
					}
				}
			}

		};
		backAction.setDisabledImageDescriptor(descriptorBackDisabled);
		
		ImageDescriptor descriptorForward = images.getDescriptor(ITagseaImages.IMG_DOWN_ARROW);
		ImageDescriptor descriptorForwardDisabled = images.getDescriptor(ITagseaImages.IMG_DOWN_ARROW_DISABLED);

		forwardAction = new Action("Move In", descriptorForward) {
			@Override
			public void run() {	
				CloudItem[] selection = cloud.getSelection();
				if (selection.length > 0) {
					CloudItem item = selection[0];
					if (item.getData("children") != null) {
						rootTag = (String) item.getData();
						refreshTags();
					}
				}
			}
		};
		forwardAction.setDisabledImageDescriptor(descriptorForwardDisabled);
		
		hierarchyAction = new Action("View as Hierarchy", SWT.TOGGLE) {
			@Override
			public void run() {
				CloudSeeView.this.hierarchy = !CloudSeeView.this.hierarchy;
				refreshTags();
			}
		};
		hierarchyAction.setChecked(hierarchy);
		hierarchyAction.setImageDescriptor(hierarchyDescriptor);
		
	}

	/**
	 * 
	 */
	private void refreshTags() {
		//dispose items
		if (hierarchy) {
			String partName = basePartName;
			if (rootTag != null) {
				partName += " (" + rootTag + ")";
			}
			setPartName(partName);
			refreshTagsAsHierarchy();
		} else {
			setPartName(basePartName);
			forwardAction.setEnabled(false);
			backAction.setEnabled(false);
			ITag[] tags = TagSEAPlugin.getTagsModel().getAllTags();
			cloud.setRedraw(false);
			for (CloudItem item : cloud.getItems()) {
				item.dispose();
			}
			for (ITag tag : tags) {
				CloudItem item = new CloudItem(cloud);
				item.setText(tag.getName());
				item.setData(tag.getName());
				item.setPriority(tag.getWaypointCount());
			}
			cloud.setRedraw(true);
			cloud.redraw();
		}
		
	}

	/**
	 * 
	 */
	private void refreshTagsAsHierarchy() {
		cloud.setRedraw(false);
		for (CloudItem item : cloud.getItems()) {
			if (item != null && !item.isDisposed()) {
				item.dispose();
			}
		}
		String[] childNames;
		TreeSet<String> hasChildSet = new TreeSet<String>();
		HashMap<String, Integer> waypointCounts = new HashMap<String, Integer>();
		if (rootTag == null) {
			backAction.setEnabled(false);
			childNames = TagSEAUtils.getRootTagNames();
			for (String name : childNames) {
				ITag tag = TagSEAPlugin.getTagsModel().getTag(name);
				if (tag != null) {
					ITag[] tags = TagSEAUtils.getAllChildTags(tag);
					if (tags.length > 0) {
						hasChildSet.add(name);
					}
					Integer count = waypointCounts.get(name);
					if (count == null) {
						count = 0;
					}
					count = count + tag.getWaypointCount();
					for (ITag child : tags) {
						count = count + child.getWaypointCount();
					}
					waypointCounts.put(name, count);
				} else {
					ITag[] parents = TagSEAUtils.getChildTagsForName(name);
					if (parents.length > 0) {
						hasChildSet.add(name);
					}
					for (ITag parent : parents) {
						Integer count = waypointCounts.get(name);
						if (count == null) {
							count = 0;
						}
						count = count + parent.getWaypointCount();
						for (ITag child : TagSEAUtils.getAllChildTags(parent)) {
							count = count + child.getWaypointCount();
						}
						waypointCounts.put(name, count);
					}
				}
			}
		} else {
			backAction.setEnabled(true);
			ITag[] tags = TagSEAUtils.getChildTagsForName(rootTag);
			TreeSet<String> childSet = new TreeSet<String>();
			for (ITag tag : tags) {
				String name = tag.getName();
				name = name.substring(rootTag.length()+1);
				int dot = name.indexOf('.');
				if (dot != -1) {
					name = name.substring(0, dot);
					hasChildSet.add(rootTag + '.' + name);
				}
				name = rootTag + '.' + name;
				Integer count = waypointCounts.get(name);
				if (count == null) {
					count = 0;
				}
				count = count + tag.getWaypointCount();
				for (ITag child : TagSEAUtils.getAllChildTags(tag)) {
					count = count + child.getWaypointCount();
				}
				childSet.add(name);
				waypointCounts.put(name, count);
			}
			childNames = childSet.toArray(new String[childSet.size()]);
		}
		for (String name : childNames) {
			CloudItem item = new CloudItem(cloud);
			item.setData(name);
			Integer priority = waypointCounts.get(name);
			if (priority == null) {
				priority = 0;
			}
			item.setPriority(priority);
			if (hasChildSet.contains(name)) {
				name += "+";
				item.setForeground(cloud.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
				item.setData("children", Boolean.TRUE);
			}
			if (rootTag != null) {
				name = name.substring(rootTag.length()+1);
			}
			item.setText(name);
			
		}
		cloud.setRedraw(true);
		//cloud.layout();
		cloud.redraw();
	}

	@Override
	public void setFocus() {
		cloud.setFocus();
	}
	
	private void scheduleViewerRefresh() {
		if (refreshTask != null) {
			refreshTask.cancel();
		}
		
		if (!cloud.isDisposed()) {
			refreshTask = new TimerTask() {
				@Override
				public void run() {
					if (!cloud.isDisposed()) {
						cloud.getDisplay().syncExec(new Runnable(){
							public void run() {
								refreshTags();
							}
						});
					}
				}
			};
			refreshTimer.schedule(refreshTask, 200);
		}
	}
	
	@Override
	public void dispose() {
		refreshTimer.cancel();
		TagSEAPlugin.removeTagChangeListener(tagsListener);
		super.dispose();
	}

}
