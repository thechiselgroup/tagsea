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
package net.sourceforge.tagsea.core.ui.internal.clouds;

import net.sourceforge.tagsea.clouds.viewers.CloudViewer;
import net.sourceforge.tagsea.clouds.viewers.ICloudLabelProvider;
import net.sourceforge.tagsea.clouds.widgets.CloudItem;
import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItem;
import net.sourceforge.tagsea.core.ui.internal.tags.TagsTree;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * A composite that contains a tag cloud that can be organised as a tree.
 * @author Del Myers
 *
 */
public class CloudTreeViewer extends Composite {
	private Composite breadcrumbComposite;
	private CloudViewer viewer;
	private TagsTree input;

	private boolean flat;
	private FormToolkit toolkit;
	private FormText formText;
	
	private class CloudListener implements KeyListener, MouseListener {
		public void keyPressed(KeyEvent e) {
			IStructuredSelection s = (IStructuredSelection) getSelection();
			
			switch (e.keyCode) {
			case SWT.CR:
				if (isHierarchical()) {
					if (s.size() == 1) {
						TagTreeItem selected = (TagTreeItem) s.getFirstElement();
						if (selected.hasChildren()) {
							viewer.setInput(selected);
							refresh();
						}
					}
				}
				break;
			case SWT.BS:
				if (isHierarchical()) {
					TagTreeItem current = (TagTreeItem) viewer.getInput();
					if (current.getParent() != null) {
						viewer.setInput(current.getParent());
						refresh();
					}
				}
				break;
			}
		}

		public void keyReleased(KeyEvent e) {
			
		}
		public void mouseDoubleClick(MouseEvent e) {
			if (e.button == 1) {
				CloudItem item = viewer.getCloud().findItemAt(e.x, e.y);
				TagTreeItem treeItem = (TagTreeItem) item.getData();
				if (treeItem.hasChildren()) {
					viewer.setInput(treeItem);
					refresh();
				}
			}
		}
		public void mouseDown(MouseEvent e) {
			
		}
		public void mouseUp(MouseEvent e) {
			
		}
		
	}
	
	private class BreadCrumbListener implements IHyperlinkListener {

		public void linkActivated(HyperlinkEvent e) {
			String ref = (String) e.getHref();
			int number = Integer.parseInt(ref);
			if (number == 0) {
				// Clear the selection if root is selected
				if(viewer.getInput() == input.getRoot())
					viewer.setSelection(new StructuredSelection(new Object[0]),true);
				
				return;
			}
			//get the parent numbered "number"
			TagTreeItem current = (TagTreeItem) viewer.getInput();
			while (current != null && number > 0) {
				current = current.getParent();
				number--;
			}
			if (current == null) {
				viewer.setInput(input.getRoot());
			} else {
				viewer.setInput(current);
			}
			refresh();
		}

		public void linkEntered(HyperlinkEvent e) {}

		public void linkExited(HyperlinkEvent e) {}
		
	}
	
	private class CloudContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof TagTreeItem) {
				return ((TagTreeItem)inputElement).getChildren();
			}
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
	}
	
	private class CloudLabelProvider implements ICloudLabelProvider, IColorProvider {

		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			String text = "";
			if (element instanceof TagTreeItem) {
				TagTreeItem tti = (TagTreeItem) element;
				text = tti.getName();
				if (isHierarchical()) {
					int dot = text.lastIndexOf('.');
					if (dot > 0 && dot < text.length()-1) {
						text = text.substring(dot+1);
					}
				}
				
				text += "(" + tti.getWaypointCount() + ")";
				
				if (tti.hasChildren()) {
					text += "+";
				}
			}
			return text;
		}

		public void addListener(ILabelProviderListener listener) {}

		public void dispose() {}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		public void removeListener(ILabelProviderListener listener) {}

		public Color getBackground(Object element) {
			return null;
		}

		public Color getForeground(Object element) {
			if (element instanceof TagTreeItem) {
				TagTreeItem tti = (TagTreeItem) element;
				if (tti.hasChildren()) {
					return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN);
				}
			}
			return null;
		}

		public int getPriority(Object element) {
			if (element instanceof TagTreeItem) {
				return ((TagTreeItem)element).getWaypointCount();
			}
			return 0;
		}
		
	}

	/**
	 * @param parent
	 * @param style
	 */
	public CloudTreeViewer(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, true));
		this.flat = false;
		this.toolkit = new FormToolkit(Display.getCurrent());
		breadcrumbComposite = new Composite(this, SWT.NONE);
		breadcrumbComposite.setLayout(new FillLayout());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		breadcrumbComposite.setLayoutData(data);
		createBreadCrumb(breadcrumbComposite);
		createViewer();
		addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				CloudTreeViewer.this.widgetDisposed(e);
			}
		});
	}
	
	/**
	 * @param cloudTreeViewer
	 */
	private void createViewer() {
		viewer = new CloudViewer(this, SWT.FLAT | SWT.MULTI);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setContentProvider(new CloudContentProvider());
		viewer.setLabelProvider(new CloudLabelProvider());
		CloudListener cloudListener = new CloudListener();
		viewer.getControl().addKeyListener(cloudListener);
		viewer.getControl().addMouseListener(cloudListener);
		viewer.getControl().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	}

	/**
	 * @param e
	 */
	void widgetDisposed(DisposeEvent e) {
		if (toolkit != null) {
			toolkit.dispose();
		}
	}

	/**
	 * @param breadcrumbComposite2
	 */
	private void createBreadCrumb(Composite parent) {
		this.formText = toolkit.createFormText(parent, false);
		formText.addHyperlinkListener(new BreadCrumbListener());
		formText.setBackground(parent.getBackground());
	}

	public void setInput(TagsTree input) {
		if (this.input == input) {
			return;
		}
		this.flat = input.isFlat();
		this.input = input;
		viewer.setInput(input.getRoot());
		refresh();
	}
	
	public void refresh() {
		this.flat = input.isFlat();
		if (flat) {
			viewer.setInput(input.getRoot());
		}
		refreshBreadCrumb();
		viewer.refresh();
		
		if(viewer.getInput() == input.getRoot())
			viewer.setSelection(new StructuredSelection(new Object[]{input.getRoot()}), true);
		
//		if (viewer.getCloud().getItems().length > 0) {
//			viewer.setSelection(new StructuredSelection(viewer.getCloud().getItems()[0].getData()), true);
//		}
	}
	
	/**
	 * 
	 */
	private void refreshBreadCrumb() {
		String linkText = "";
		TagTreeItem current = (TagTreeItem) viewer.getInput();
		int parentNum = 0;
		while (current != null) {
			String text = current.getName();
			int dot = text.lastIndexOf('.');
			if (dot > 0 && dot < text.length()-1) {
				text = text.substring(dot+1);
			}
			linkText = "<a href=\""+parentNum+"\">" + text + "</a>" + linkText;
			if (current.getParent() != null && current.getParent() != input.getRoot()) {
				linkText = "." + linkText;
			} else {
				linkText = " " + linkText;
			}
			current = current.getParent();
			parentNum++;
		}
		formText.setText("<form><p>"+linkText+"</p></form>", true, false);
	}

	public ISelection getSelection() {
		return viewer.getSelection();
	}
	
	public boolean isHierarchical() {
		return !flat;
	}

	/**
	 * @return
	 */
	public CloudViewer getViewer() {
		return viewer;
	}

}
