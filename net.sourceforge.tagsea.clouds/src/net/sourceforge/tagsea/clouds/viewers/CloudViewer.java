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
package net.sourceforge.tagsea.clouds.viewers;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tagsea.clouds.widgets.Cloud;
import net.sourceforge.tagsea.clouds.widgets.CloudItem;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * Structured Viewer for tag clouds.
 * @author Del Myers
 *
 */
public class CloudViewer extends StructuredViewer {
	
	private Cloud cloud;

	public CloudViewer(Composite parent, int style) {
		this.cloud = new Cloud(parent, style);
		cloud.setForeground(parent.getForeground());
		cloud.setBackground(parent.getBackground());
		hookControl(cloud);
	}

	@Override
	protected Widget doFindInputItem(Object element) {
		if (getCloud().getData().equals(element)) {
			return getCloud();
		}
		return null;
	}

	@Override
	protected Widget doFindItem(Object element) {
		for (CloudItem item : getCloud().getItems()) {
			if (element.equals(item.getData())) {
				return item;
			}
		}
		return null;
	}

	@Override
	protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
		if (!(item instanceof CloudItem)) {
			return;
		}
		CloudItem ci = (CloudItem) item;
		if (getLabelProvider() instanceof IColorProvider) {
			IColorProvider colorProvider = (IColorProvider) getLabelProvider();
			Color color = colorProvider.getBackground(element);
			if (color != null) {
				ci.setBackground(color);
			} else {
				ci.setBackground(cloud.getBackground());
			}
			color = colorProvider.getForeground(element);
			if (color != null) {
				ci.setForeground(color);
			} else {
				ci.setForeground(cloud.getForeground());
			}
		}
		if (getLabelProvider() instanceof ILabelProvider) {
			ci.setText(((ILabelProvider)getLabelProvider()).getText(element));
		}
		if (getLabelProvider() instanceof ICloudLabelProvider) {
			ci.setPriority(((ICloudLabelProvider)getLabelProvider()).getPriority(element));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List getSelectionFromWidget() {
		ArrayList<Object> selected = new ArrayList<Object>();
		for (CloudItem item : getCloud().getSelection()) {
			selected.add(item.getData());
		}
		return selected;
	}

	@Override
	protected void internalRefresh(Object element) {
		if (getInput() == null || getInput().equals(element)) {
			rebuildChart();
		} else {
			update(element, null);
		}
		cloud.redraw();
	}

	/**
	 * 
	 */
	private void rebuildChart() {
		unmapAllElements();
		
		cloud.setData(getInput());
		mapElement(getInput(), cloud);
		for (CloudItem item : getCloud().getItems()) {
			item.dispose();
		}
		if (getContentProvider() instanceof IStructuredContentProvider) {
			for (Object element : getSortedChildren(getInput())) {
				CloudItem item = new CloudItem(getCloud());
				item.setData(element);
				mapElement(element, item);
				update(element, null);
			}
		}
	}

	@Override
	public void reveal(Object element) {
		Widget item = findItem(element);
		if (item instanceof CloudItem) {
			getCloud().setSelection(new CloudItem[] {(CloudItem)item});
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setSelectionToWidget(List l, boolean reveal) {
		ArrayList<CloudItem> itemSelection = new ArrayList<CloudItem>();
		for (Object o : l) {
			Widget item = findItem(o);
			if (item instanceof CloudItem) {
				itemSelection.add((CloudItem)item);
			}
		}
		getCloud().setSelection(itemSelection.toArray(new CloudItem[itemSelection.size()]));
	}

	@Override
	public Control getControl() {
		return cloud;
	}
	
	public Cloud getCloud() {
		return (Cloud)getControl();
	}

	@Override
	protected void inputChanged(Object input, Object oldInput) {
		getContentProvider().inputChanged(this, oldInput, input);
		refresh();
	}
	
}
