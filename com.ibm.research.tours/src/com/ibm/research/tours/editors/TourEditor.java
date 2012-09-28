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
package com.ibm.research.tours.editors;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.ibm.research.tours.IDoubleClickActionContribution;
import com.ibm.research.tours.IDoubleClickActionDelegate;
import com.ibm.research.tours.IPaletteEntry;
import com.ibm.research.tours.IPaletteModelListener;
import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourElementDropAdapter;
import com.ibm.research.tours.ITourListener;
import com.ibm.research.tours.ToursPlugin;
import com.ibm.research.tours.controls.FilteredTable;
import com.ibm.research.tours.controls.HandleToggle;
import com.ibm.research.tours.controls.NQueryTablePatternFilter;
import com.ibm.research.tours.dnd.PaletteDragListener;
import com.ibm.research.tours.dnd.PaletteEntryTransfer;
import com.ibm.research.tours.dnd.TourDragNDropAdapter;
import com.ibm.research.tours.dnd.TourElementTransfer;
import com.ibm.research.tours.serializer.XMLTourFactory;
import com.ibm.research.tours.xml.XMLEditor;

/**
 * The Tour editor.
 */
public class TourEditor extends MultiPageEditorPart implements IResourceChangeListener
{
	private class TourListener implements ITourListener
	{
		public void elementsAdded(ITour tour, ITourElement[] elements) 
		{
			Display.getDefault().asyncExec(new Runnable() 
			{
				public void run() 
				{
					if(fTourTreeAdapter != null)
						getTourViewer().refresh(fTourTreeAdapter.getTourElements());
				}
			});

			setDirty(true);
		}

		public void elementsRemoved(ITour tour, ITourElement[] elements) 
		{
			Display.getDefault().asyncExec(new Runnable() 
			{
				public void run() 
				{
					if(fTourTreeAdapter != null)
						getTourViewer().refresh(fTourTreeAdapter.getTourElements());
				}
			});

			setDirty(true);
		}

		public void tourChanged(ITour tour) 
		{
			Display.getDefault().asyncExec(new Runnable() 
			{
				public void run() 
				{
					if(fTourTreeAdapter != null)
						getTourViewer().refresh();
				}
			});

			setDirty(true);
		}
	}

	private class PaletteModelListener implements IPaletteModelListener
	{
		public void entriesAdded(IPaletteEntry[] entries) 
		{
			Display.getDefault().asyncExec(new Runnable() 
			{
				public void run() 
				{
					if(fPaletteViewer != null)
					{
						fPaletteViewer.refresh();
					}
				}
			});
		}
	}

	private XMLEditor fXmlEditor;
	//private FilteredTree fFilteredTree;
	private FilteredTable fFilteredPaletteTable;
	private TableViewer fPaletteViewer;
	private TreeViewer fTourViewer;
	private Composite fPaletteComposite;
	private Composite fInnerPaletteComposite;
	private Section fPaletteSection;
	private FormToolkit fFormToolkit;
	private Composite fRoot;
	private Composite fTourComposite;
	private HandleToggle fToggle;
	private ITour fTour;
	private TourListener fTourListener = new TourListener();
	private PaletteModelListener fPaletteModelListener = new PaletteModelListener();
	private TourTreeAdapter fTourTreeAdapter;
	private boolean fIsDesignerDirty;
	private MenuManager fMenuMgr;
	private StyledText fNotesText;
	private TourTreeSelectionChangedListener fTourTreeSelectionChangedListener;
	private TourEditorActionDelegate fTourEditorActionDelegate = new TourEditorActionDelegate();
	private IAction deleteAction;
	private IAction deleteAllAction;
	private IAction previewAction;
	//private IAction timingAction;
	private IAction masterTimingAction;
	private IAction runAction;
	private IAction transitionOnClickAction;
	private IAction transitionWithPreviousAction;
	private IAction transitionAfterPreviousAction;

	private Text fTitleText;
	private Text fAuthorText;
	private Text fDescriptionText;
	private Section fMetaSection;

	public TourEditor() 
	{
		super();

		// Pick up changes from the input file
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	@Override
	public boolean isDirty() 
	{
		boolean editorDirty =  super.isDirty();
		return editorDirty || fIsDesignerDirty;
	}

	protected void setDirty(boolean b) 
	{
		fIsDesignerDirty = b;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	void createDesignPage() 
	{
		fFormToolkit = new FormToolkit(Display.getDefault());
		fRoot = createRootComposite(getContainer());

		createTourComposite(fRoot);
		createMetaInformationSection(fTourComposite);

		createTourTree(fTourComposite);
		createTourNotesArea(fTourComposite);

		createPaletteComposite(fRoot);
		createToggle(fPaletteComposite);
		createPaletteTable(fPaletteComposite);

		makeActions();
		hookContextMenu();

		int index = addPage(fRoot);
		setPageText(index, "Design");
	}

	private void createMetaInformationSection(Composite parent) 
	{
		fMetaSection = fFormToolkit.createSection(parent, Section.SHORT_TITLE_BAR|Section.TWISTIE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		fMetaSection.setLayoutData(data);
		fMetaSection.setText("Meta Information");

		Composite comp = fFormToolkit.createComposite(fMetaSection);
		GridLayout layout = new GridLayout(2,false);
		comp.setLayout(layout);
		comp.setBackground(JFaceColors.getBannerBackground(Display.getCurrent()));

		fFormToolkit.createLabel(comp, "Title:").setForeground(JFaceColors.getHyperlinkText(Display.getDefault()));
		fTitleText =  fFormToolkit.createText(comp,"");
		fTitleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fTitleText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				if(!getTour().getTitle().equals(fTitleText.getText()))
				{
					setDirty(true);
				}
			}
		});


		fFormToolkit.createLabel(comp, "Description:").setForeground(JFaceColors.getHyperlinkText(Display.getDefault()));
		fDescriptionText =  fFormToolkit.createText(comp,"",SWT.MULTI|SWT.WRAP);
		GridData descData = new GridData(GridData.FILL_HORIZONTAL);
		descData.heightHint = 36;
		fDescriptionText.setLayoutData(descData);
		fDescriptionText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				if(!getTour().getDescription().equals(fDescriptionText.getText()))
					setDirty(true);
			}
		});

		fFormToolkit.createLabel(comp, "Author:").setForeground(JFaceColors.getHyperlinkText(Display.getDefault()));
		fAuthorText =  fFormToolkit.createText(comp,"");
		fAuthorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fAuthorText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				if(!getTour().getAuthor().equals(fAuthorText.getText()))
					setDirty(true);
			}
		});

		comp.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		fFormToolkit.paintBordersFor(comp);
		fMetaSection.setClient(comp);
	}

	public Object[] getSelection()
	{
		IStructuredSelection structuredSelection = (IStructuredSelection)fTourViewer.getSelection();
		return structuredSelection.toArray();
	}

	private boolean containsAllTourElements(Object[] selection)
	{
		for(Object o : selection)
		{
			if(!(o instanceof ITourElement))
				return false;
		}

		return true;
	}

	private void makeActions() 
	{
		deleteAction = new Action() 
		{
			@Override
			public void run() 
			{
				fTourEditorActionDelegate.delete();
			}
		};

		deleteAction.setImageDescriptor(ToursPlugin.getDefault().getImageRegistry().getDescriptor(ToursPlugin.IMG_DELETE));
		deleteAction.setText("Delete");

		deleteAllAction = new Action() 
		{
			@Override
			public void run() 
			{
				fTourEditorActionDelegate.deleteAll();
			}
		};

		deleteAllAction.setImageDescriptor(ToursPlugin.getDefault().getImageRegistry().getDescriptor(ToursPlugin.IMG_DELETE));
		deleteAllAction.setText("Delete All");

		previewAction = new Action() 
		{
			@Override
			public void run() 
			{
				fTourEditorActionDelegate.preview();
			}
		};
		previewAction.setImageDescriptor(ToursPlugin.getDefault().getImageRegistry().getDescriptor(ToursPlugin.IMG_PREVIEW));
		previewAction.setText("Preview");
		previewAction.setEnabled(false); // @tag todo tours editor demo : preview action disabled, not implemented yet

//		timingAction = new Action() 
//		{
//			@Override
//			public void run() 
//			{
//				fTourEditorActionDelegate.timing();
//			}
//		};
//		timingAction.setImageDescriptor(ToursPlugin.getDefault().getImageRegistry().getDescriptor(ToursPlugin.IMG_TIME));
//		timingAction.setText("Timing...");

		masterTimingAction = new Action() 
		{
			@Override
			public void run() 
			{
				fTourEditorActionDelegate.masterTiming();
			}
		};
		masterTimingAction.setImageDescriptor(ToursPlugin.getDefault().getImageRegistry().getDescriptor(ToursPlugin.IMG_TIME));
		masterTimingAction.setText("Master Timing...");

		transitionOnClickAction = new Action() 
		{
			@Override
			public void run() 
			{
				fTourEditorActionDelegate.transitionOnClick();
			}
		};
		transitionOnClickAction.setText("Start on click");	

		transitionWithPreviousAction = new Action() 
		{
			@Override
			public void run() 
			{
				fTourEditorActionDelegate.transitionWithPrevious();
			}
		};
		transitionWithPreviousAction.setText("Start with previous");
		transitionWithPreviousAction.setEnabled(false); // @tag todo tours editor demo : transitionWithPreviousAction action disabled, not implemented yet

		transitionAfterPreviousAction = new Action() 
		{
			@Override
			public void run() 
			{
				fTourEditorActionDelegate.transitionAfterPrevious();
			}
		};
		transitionAfterPreviousAction.setText("Start after previous");	
		transitionAfterPreviousAction.setEnabled(false); // @tag todo tours editor demo : transitionAfterPreviousAction action disabled, not implemented yet

		runAction = new Action() 
		{
			@Override
			public void run() 
			{
				fTourEditorActionDelegate.run();
			}
		};
		runAction.setImageDescriptor(ToursPlugin.getDefault().getImageRegistry().getDescriptor(ToursPlugin.IMG_RUN));
		runAction.setText("Run");	
	}

	private void hookContextMenu() 
	{
		fMenuMgr = new MenuManager("#Popup");
		fMenuMgr.setRemoveAllWhenShown(true);

		fMenuMgr.addMenuListener(new IMenuListener() 
		{
			public void menuAboutToShow(IMenuManager manager) 
			{
				TourEditor.this.fillContextMenu(manager);
			}
		});

		Menu menu = fMenuMgr.createContextMenu(fTourViewer.getControl());
		fTourViewer.getControl().setMenu(menu);
		getEditorSite().registerContextMenu(fMenuMgr, fTourViewer, false);
	}

	private void fillContextMenu(IMenuManager manager) 
	{
		// Other plug-ins can contribute there actions here
		Object[] selection = getSelection();

		if(selection.length > 0)
		{
			if(selection.length == 1 && selection[0] instanceof TourElements)
			{
				manager.add(deleteAllAction);
				manager.add(new Separator());
			}
			else if(containsAllTourElements(selection))
			{
				manager.add(deleteAction);
				manager.add(new Separator());
			}

			manager.add(runAction);
			manager.add(new Separator());

			if(selection.length == 1 && selection[0] instanceof TourElements)
			{
				manager.add(masterTimingAction);
			}
			else if(selection.length == 1 && selection[0] instanceof ITourElement)
			{
				manager.add(previewAction);
				manager.add(new Separator());
				//manager.add(timingAction);
				//manager.add(new Separator());

				manager.add(transitionOnClickAction);
				manager.add(transitionWithPreviousAction);
				manager.add(transitionAfterPreviousAction);

				ITourElement element = (ITourElement)selection[0];
				int transition = element.getTransition();

				transitionOnClickAction.setChecked(false);
				transitionWithPreviousAction.setChecked(false);
				transitionAfterPreviousAction.setChecked(false);

				switch(transition)
				{
				case ITourElement.START_ON_CLICK:
					transitionOnClickAction.setChecked(true);
					break;

				case ITourElement.START_WITH_PREVIOUS:
					transitionWithPreviousAction.setChecked(true);
					break;

				case ITourElement.START_AFTER_PREVIOUS:
					transitionAfterPreviousAction.setChecked(true);
					break;

				default:
					break;
				}

				manager.add(new Separator());
			}
		}

		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void createToggle(Composite parent) 
	{
		fToggle = new HandleToggle(parent,SWT.VERTICAL);
		GridData data = new GridData(SWT.RIGHT, SWT.FILL, false, true);
		data.widthHint = 8;
		data.minimumWidth = 8;
		fToggle.setLayoutData(data);
		fToggle.setTooltips("Hide palette", "Show palette");

		fToggle.setCornerBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		fToggle.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));

		fToggle.addSelectionListener(new SelectionListener() 
		{	
			public void widgetSelected(SelectionEvent e) 
			{
				if(e.detail == SWT.RIGHT)
				{
					fInnerPaletteComposite.setVisible(false);
					GridData data = (GridData)fInnerPaletteComposite.getLayoutData();
					data.exclude = true;
					fInnerPaletteComposite.setLayoutData(data);
					fRoot.layout(true,true);
				}
				else if(e.detail == SWT.LEFT)
				{
					fInnerPaletteComposite.setVisible(true);
					GridData data = (GridData)fInnerPaletteComposite.getLayoutData();
					data.exclude = false;
					fInnerPaletteComposite.setLayoutData(data);
					fRoot.layout(true,true);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
	}

	private void createTourComposite(Composite root) 
	{
		fTourComposite = new Composite(root,SWT.NONE);
		GridLayout layout = new GridLayout(2,false);
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		fTourComposite.setLayout(layout);
		fTourComposite.setBackground(JFaceColors.getBannerBackground(Display.getCurrent()));
		fTourComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createPaletteComposite(Composite root) 
	{
		fPaletteComposite = new Composite(root,SWT.NONE);
		GridLayout layout = new GridLayout(2,false);
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		fPaletteComposite.setLayout(layout);
		fPaletteComposite.setBackground(JFaceColors.getBannerBackground(Display.getCurrent()));
		fPaletteComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	}

	private Composite createRootComposite(Composite parent) 
	{
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout(2,false);
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		return composite;
	}

	private void createTourTree(Composite parent) 
	{
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		composite.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
		GridLayout layout = new GridLayout();
		layout.marginTop = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 3;
		layout.marginRight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		//fFilteredTree = new FilteredTree(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL,new PatternFilter());
		//fFilteredTree.setBackground(JFaceColors.getBannerBackground(Display.getCurrent()));

		fTourViewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fTourViewer.getTree().setBackground(JFaceColors.getBannerBackground(Display.getCurrent()));
		//fTourViewer = fFilteredTree.getViewer();
		fTourViewer.setContentProvider(new TourContentProvider());
		fTourViewer.setLabelProvider(new TourLabelProvider());

		// Load the transfers 
		List<Transfer> transfers = new ArrayList<Transfer>();

		// Add the default tour element transfer for internal dnd
		transfers.add(TourElementTransfer.getInstance());
		transfers.add(PaletteEntryTransfer.getInstance());

		// Add the drop adapters
		for(ITourElementDropAdapter adapter : ToursPlugin.getDefault().getDropAdapters())
		{
			Transfer transfer = adapter.getTransfer();

			if(transfer!=null && !(transfers.contains(transfer)))
				transfers.add(transfer);
		}

		if(transfers.size() > 0)
			fTourViewer.addDropSupport(DND.DROP_COPY| DND.DROP_MOVE, transfers.toArray(new Transfer[0]), new TourDragNDropAdapter(this));

		fTourViewer.addDragSupport(DND.DROP_COPY| DND.DROP_MOVE, new Transfer[]{TourElementTransfer.getInstance()}, new TourDragNDropAdapter(this));
		fTourViewer.addDoubleClickListener(new TourTreeDoubleClickListener());
		fTourTreeSelectionChangedListener = new TourTreeSelectionChangedListener(this);
		fTourViewer.addSelectionChangedListener(fTourTreeSelectionChangedListener);

		fTourViewer.addDoubleClickListener(new IDoubleClickListener() 
		{
			public void doubleClick(DoubleClickEvent event) 
			{
				IStructuredSelection structuredSelection = (IStructuredSelection)event.getSelection();
				Object[] selection = structuredSelection.toArray();
				String objectType = selection[0].getClass().getName();

				IDoubleClickActionContribution[] contributions = ToursPlugin.getDefault().getDoubleClickActionContributions();

				if(contributions.length > 0)
				{
					for(IDoubleClickActionContribution contribution : contributions)
					{
						if(contribution.getObjectContribution().equals(objectType))
						{
							IDoubleClickActionDelegate delegate = contribution.getDoubleClickActionDelegate();
							delegate.setActivePart(TourEditor.this);
							delegate.selectionChanged(structuredSelection);
							delegate.run();
						}
					}
				}				
			}
		});

		fTourTreeAdapter = new TourTreeAdapter(getTour());
		fTourViewer.setInput(fTourTreeAdapter);
		fTourViewer.setExpandedState(fTourTreeAdapter.getTourElements(), true);
		fTourViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		fTour.addTourListener(fTourListener,false);
		
//		final Tree tree = fTourViewer.getTree();
//	    // Disable native tooltip
//		tree.setToolTipText("");
//
//	    // Implement a "fake" tooltip
//	    final Listener labelListener = new Listener() {
//	      public void handleEvent(Event event) {
//	        Label label = (Label) event.widget;
//	        Shell shell = label.getShell();
//	        switch (event.type) {
//	        case SWT.MouseDown:
//	          Event e = new Event();
//	          e.item = (TreeItem) label.getData("_TABLEITEM");
//	          // Assuming table is single select, set the selection as if
//	          // the mouse down event went through to the table
//	          tree.setSelection(new TreeItem[] { (TreeItem) e.item });
//	          tree.notifyListeners(SWT.Selection, e);
//	        // fall through
//	        case SWT.MouseExit:
//	          shell.dispose();
//	          break;
//	        }
//	      }
//	    };
//
//	    Listener tableListener = new Listener() 
//	    {
//	      Shell tip = null;
//	      Label label = null;
//
//	      public void handleEvent(Event event) {
//	        switch (event.type) {
//	        case SWT.Dispose:
//	        case SWT.KeyDown:
//	        case SWT.MouseMove: {
//	          if (tip == null)
//	            break;
//	          tip.dispose();
//	          tip = null;
//	          label = null;
//	          break;
//	        }
//	        case SWT.MouseHover: {
//	          TreeItem item = tree.getItem(new Point(event.x, event.y));
//	          
//	          if (item != null) 
//	          {
//	            if (tip != null && !tip.isDisposed())
//	              tip.dispose();
//	            tip = new Shell(Display.getDefault().getActiveShell(), SWT.ON_TOP | SWT.TOOL);
//	            tip.setLayout(new FillLayout());
//	            label = new Label(tip, SWT.NONE);
//	            label.setForeground(Display.getDefault()
//	                .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
//	            label.setBackground(Display.getDefault()
//	                .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
//	            label.setData("_TABLEITEM", item);
//	            label.setText("tooltip " + item.getText());
//	            
//	            label.addListener(SWT.MouseExit, labelListener);
//	            label.addListener(SWT.MouseDown, labelListener);
//	            Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//	            Rectangle rect = item.getBounds(0);
//	            Point pt = tree.toDisplay(rect.x, rect.y);
//	            tip.setBounds(pt.x, pt.y, size.x, size.y);
//	            tip.setVisible(true);
//	          }
//	        }
//	        }
//	      }
//	    };
//	    tree.addListener(SWT.Dispose, tableListener);
//	    tree.addListener(SWT.KeyDown, tableListener);
//	    tree.addListener(SWT.MouseMove, tableListener);
//	    tree.addListener(SWT.MouseHover, tableListener);
		
	}

	private void createTourNotesArea(Composite parent) 
	{
		Section section = fFormToolkit.createSection(parent, Section.TWISTIE | Section.EXPANDED | Section.SHORT_TITLE_BAR);
		section.setText("Notes");
		GridData sectionData = new GridData(GridData.FILL_HORIZONTAL);
		sectionData.horizontalSpan = 2;
		sectionData.heightHint = 48;
		section.setLayoutData(sectionData);
		section.setLayout(new GridLayout());

		Composite composite = fFormToolkit.createComposite(section,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.marginBottom = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		composite.setLayout(layout);

		fNotesText = new StyledText(composite,SWT.WRAP|SWT.BORDER);
		GridData textData = new GridData(GridData.FILL_BOTH);
		textData.heightHint = 48;
		fNotesText.setLayoutData(textData);
		fNotesText.setEditable(false);

		// We want to pick up on human edit events 
		fNotesText.addKeyListener(new KeyListener() {

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			public void keyPressed(KeyEvent e) 
			{
				// Only when a valid element is selected and the control is editable
				if(fNotesText.getEditable() && !isDirty())
					setDirty(true);
			}

		});

		section.setClient(composite);
	}

	private void createPaletteTable(Composite parent) 
	{
		fInnerPaletteComposite = fFormToolkit.createComposite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = 2;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		fInnerPaletteComposite.setLayout(layout);
		fInnerPaletteComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite titleComposite = fFormToolkit.createComposite(fInnerPaletteComposite,SWT.NONE);
		GridLayout titleLayout = new GridLayout(2,true);
		titleLayout.marginHeight = 0;
		titleLayout.marginWidth = 0;
		titleComposite.setLayout(titleLayout);
		titleComposite.setLayoutData(new GridData(GridData.CENTER,GridData.CENTER,true,false));

		Label title = fFormToolkit.createLabel(titleComposite,"Tour Palette");
		title.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		Label imageLabel = fFormToolkit.createLabel(titleComposite,"");		
		imageLabel.setImage(ToursPlugin.getDefault().getImageRegistry().get(ToursPlugin.IMG_PALETTE));

		fPaletteSection = fFormToolkit.createSection(fInnerPaletteComposite,Section.NO_TITLE);
		fPaletteSection.setText("Palette");
		GridData paletteData = new GridData(GridData.FILL_BOTH);
		fPaletteSection.setLayoutData(paletteData);

		fFilteredPaletteTable = new FilteredTable(fPaletteSection,SWT.MULTI | SWT.V_SCROLL,new NQueryTablePatternFilter());
		fFilteredPaletteTable.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));

		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		fFilteredPaletteTable.setLayoutData(data);

		fPaletteViewer = fFilteredPaletteTable.getViewer();
		fPaletteViewer.setContentProvider(new PaletteContentProvider());
		fPaletteViewer.addDoubleClickListener(new PaletteDoubleClickListener());
		fPaletteViewer.setSorter(new PaletteSorter());
		fPaletteViewer.setLabelProvider(new PaletteLabelProvider());
		fPaletteViewer.addDragSupport(DND.DROP_COPY| DND.DROP_MOVE, new Transfer[]{PaletteEntryTransfer.getInstance()}, new PaletteDragListener(fPaletteViewer));
		fPaletteViewer.setInput(new Object());
		fPaletteSection.setClient(fFilteredPaletteTable);
		fPaletteSection.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		ToursPlugin.getDefault().getPaletteModel().addPaletteModelListener(fPaletteModelListener);
	}

	void createFilePage() 
	{
		try 
		{
			fXmlEditor = new XMLEditor();
			int index = addPage(fXmlEditor, getEditorInput());
			setPageText(index, fXmlEditor.getTitle());
		} 
		catch (PartInitException e) 
		{
			ErrorDialog.openError(
					getSite().getShell(),
					"Error creating nested text editor",
					null,
					e.getStatus());
		}
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() 
	{
		createDesignPage();
		createFilePage();
		setMetaInformation();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		ToursPlugin.getDefault().getPaletteModel().removePaletteModelListener(fPaletteModelListener);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) 
	{
		IFileEditorInput input = (IFileEditorInput)getEditorInput();
		IFile file = input.getFile();

		if(fIsDesignerDirty || (fIsDesignerDirty && isDirty()))
		{
			fTourTreeSelectionChangedListener.performSave();
			getEditor(1).doSave(monitor);
			// Write tour to file and clear the dirty flag
			syncMetaInformation();

			try 
			{
				getTour().write(file);
			}
			catch (Exception e) 
			{

				e.printStackTrace();
			}
			setDirty(false);
		}
		// The text editor is dirty
		else if(isDirty())
		{
			// Read the tour from the file
			getEditor(1).doSave(monitor);
			try 
			{
				getTour().read(file);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			setMetaInformation();
			setDirty(false);
		}
	}

	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() 
	{
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(1, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) 
	{
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException 
	{
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");

		super.init(site, editorInput);

		setPartName(getEditorInput().getName());

		// Process the input
		IFileEditorInput input = (IFileEditorInput)editorInput;
		IFile file = input.getFile();

		if(!file.isSynchronized(0))
		{
			try 
			{
				file.refreshLocal(0, null);
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}

		// Load the tour from the file input
		try 
		{
			fTour = XMLTourFactory.createTour(file);
		}
		catch (CoreException e) 
		{
			ToursPlugin.getDefault().getLog().log(e.getStatus());
		}

		fTourEditorActionDelegate.init(this);
	}

	private void setMetaInformation() 
	{
		fTitleText.setText(fTour.getTitle());
		fDescriptionText.setText(fTour.getDescription());
		fAuthorText.setText(fTour.getAuthor());

		if ( fMetaSection!=null && !fMetaSection.isDisposed() )
		{
			String titleStr = "";
			if ( fTour.getTitle()!=null )
				titleStr += " (" + fTour.getTitle() + ")";
			fMetaSection.setText("Meta Information" + titleStr);
		}
	}

	private void syncMetaInformation() 
	{
		fTour.setTitle(fTitleText.getText());
		fTour.setDescription(fDescriptionText.getText());
		fTour.setAuthor(fAuthorText.getText());
		
		if ( fMetaSection!=null && !fMetaSection.isDisposed() )
		{
			String titleStr = "";
			if ( fTour.getTitle()!=null )
				titleStr += " (" + fTour.getTitle() + ")";
			fMetaSection.setText("Meta Information" + titleStr);
		}
	}

	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() 
	{
		return true;
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++){
						if(((FileEditorInput)fXmlEditor.getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(fXmlEditor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}

	public TableViewer getPaletteViewer() 
	{
		return fPaletteViewer;
	}

	public ITour getTour() 
	{
		return fTour;
	}

	public TreeViewer getTourViewer() 
	{
		return fTourViewer;
	}

	public TourTreeAdapter getTourTreeAdapter() 
	{
		return fTourTreeAdapter;
	}

	public StyledText getNotesText() 
	{
		return fNotesText;
	}
}
