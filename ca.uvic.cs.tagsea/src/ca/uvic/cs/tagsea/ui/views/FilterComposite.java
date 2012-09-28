/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.ui.views;

import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.progress.WorkbenchJob;

import ca.uvic.cs.tagsea.ITagseaImages;
import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.monitoring.TagSEAActionEvent;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;
import ca.uvic.cs.tagsea.navigation.TreeFilter;


/**
 * This composite contains a filter label, textbox, and a clear button.
 * To use this composite cal the constructor and then the createTextFilterControl(TreeViewer) method.
 * This will add a ViewerFilter to the TreeViewer 
 * 
 * @author Chris Callendar
 */
public class FilterComposite extends Composite implements KeyListener, FocusListener, ModifyListener {

	private final String INITIAL_TEXT = "type filter text";
    
	private TreeFilter treeFilter;
	private TreeViewer treeViewer;
	private boolean filterRegexError = false;
	private Color originalTextColor;
	private RefreshTreeViewerJob refreshJob;
	private Text text;
	private Action clearTextAction;
	private Color errorColor;
	
	/**
	 * Initializes this composite and sets it to a grid layout with 3 columns
	 * and uses the default TreeFilter class. 
	 */
	public FilterComposite(Composite parent, int style) {
		this(parent, style, new TreeFilter());
	}
	
	/**
	 * Initializes this composite and sets it to a grid layout with 3 columns. 
	 */
	public FilterComposite(Composite parent, int style, TreeFilter treeFilter) {
		super(parent, style);
		this.treeFilter = treeFilter;
		this.errorColor = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
		this.refreshJob = new RefreshTreeViewerJob();
		
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		this.setLayoutData(data);
		
		GridLayout grid = new GridLayout(3, false);
		grid.marginHeight = 0;
		grid.marginWidth = 0;
		this.setLayout(grid);
	}
	
	/**
	 * Returns the treeFilter
	 * @return FilterComposite
	 */
	public TreeFilter getTreeFilter() {
		return treeFilter;
	}
	
	/**
	 * Sets the tree filter to use.
	 * @param treeFilter The treeFilter to set.
	 */
	public void setTreeFilter(TreeFilter treeFilter) {
		if (treeFilter != null) {
			this.treeFilter = treeFilter;
		}
	}	
	
	/**
	 * Returns the text control
	 * @return Text 
	 */
	public Text getTextControl() {
		return text;
	}
	
	private void scheduleRefreshJob() {
		// if it has already been scheduled (but not run) then we don't need to schedule again
		if (refreshJob.isScheduled()) {
			return;
		}
		refreshJob.scheduleJob();
		Monitoring.getDefault().fireEvent(new TagSEAActionEvent(TagSEAActionEvent.Type.Filter, text.getText()));
	}
		
	private IStatus refreshTreeViewer() {
		if (treeViewer.getControl().isDisposed()) {
			return Status.CANCEL_STATUS;
		}
		
		treeViewer.getControl().setRedraw(false);
		treeViewer.refresh(true);
		
		TreeItem[] items = treeViewer.getTree().getItems();
		if (items.length > 0) {
			treeViewer.getTree().showItem(items[0]); // to prevent scrolling
		}

		// only expand if a filter pattern exists
		if (!"".equals(treeFilter.getPattern())) {
			treeViewer.expandAll();
		}
		treeViewer.getControl().setRedraw(true);

		return Status.OK_STATUS;
		
	}
	
	/**
	 * Creates the textbox and clear button which is tied to the given
	 * TreeViewer.  Also adds listeners to the textbox so that when the 
	 * enter or down arrow key is pressed the tree view gets focus.
	 * The tree filter is also added to the tree viewer.
	 * @param treeViewer
	 */
	public void createTextFilterControl(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
		
		treeViewer.addFilter(treeFilter);
		this.text = new Text(this, SWT.SINGLE | SWT.BORDER);
		text.setText(INITIAL_TEXT);
		text.setToolTipText("Tags Filter");
		GridData data = new GridData(SWT.LEFT, SWT.FILL, false, false);
		data.widthHint = 80;
		text.setLayoutData(data);
		text.pack();
		originalTextColor = text.getForeground();

		// create the clear text button
		this.clearTextAction = new Action("", IAction.AS_PUSH_BUTTON) {
			public void run() {
				text.setText("");
				text.setFocus();
			}
		};
		clearTextAction.setToolTipText("Clear the filter text");
		final ImageDescriptor clearDescriptor = TagSEAPlugin.getDefault().getTagseaImages().getDescriptor(ITagseaImages.IMG_CLEAR);
		final ImageDescriptor clearDescriptorDisabled = TagSEAPlugin.getDefault().getTagseaImages().getDescriptor(ITagseaImages.IMG_CLEAR_DISABLED);
		clearTextAction.setImageDescriptor(clearDescriptor);
		clearTextAction.setDisabledImageDescriptor(clearDescriptorDisabled);
		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.HORIZONTAL);
		ToolBarManager manager = new ToolBarManager(toolBar);
		manager.add(clearTextAction);
		manager.update(false);

		Label spacer = new Label(this, SWT.NONE);
		data = new GridData(SWT.LEFT, SWT.FILL, false, false);
		data.widthHint = 8;
		spacer.setLayoutData(data);
		
		// add the textbox listeners
		text.addModifyListener(this);
		text.addKeyListener(this);
		text.addFocusListener(this);
	}	
	

	/** Clears the initial text on focus. */
	public void focusGained(FocusEvent e) {
		 if (INITIAL_TEXT.equals(text.getText())) {
			 text.setText("");
		 }
	}
	/** Does nothing. */
	public void focusLost(FocusEvent e) {}
	
	/** Updates the TreeFilter with the new text. */
	public void modifyText(ModifyEvent e) {
		clearTextAction.setEnabled(!"".equals(text.getText()));
		updateFilter(text);
	}

	public void keyPressed(KeyEvent e) {}
	
	/** Checks for the Enter or Down Arrow keys to give focus to the TreeViewer. */
	public void keyReleased(KeyEvent e) {
		boolean hasItems = (treeViewer.getTree().getItemCount() > 0);
		if (hasItems && ((e.keyCode == SWT.ARROW_DOWN) || (e.character == SWT.CR))) {
			treeViewer.getTree().setFocus();
		}
	}
	
	/**
	 * Updates the filter (if changed) and schedules the filter job.
	 * Also ensures that the right color is used - either an error color
	 * if the pattern contains a syntax error, or the original color otherwise.
	 * @param pattern
	 */
	private void updateFilter(Text text) {
		String pattern = text.getText();
		if (filterRegexError) {
			filterRegexError = false;
			text.setForeground(originalTextColor);
		}
		if (!treeFilter.getPattern().equals(pattern)) {
			try {
				treeFilter.setPattern(pattern);
				// start the job which will do the filtering
				scheduleRefreshJob();
			} catch (PatternSyntaxException e) {
				if (!filterRegexError) {
					filterRegexError = true;
					if (errorColor != text.getForeground()) {
					originalTextColor = text.getForeground();
					}
				}
				text.setForeground(errorColor);
			}
		}
	}
	

	/**
	 * Refreshes the tree viewer.
	 * @author Chris Callendar
	 */
	class RefreshTreeViewerJob extends WorkbenchJob {

		private boolean scheduled = false;
		
		public RefreshTreeViewerJob() {
			super("Refresh Filter");
			setSystem(true);
		}
		
		/**
		 * Schedules this job to run in 250ms.
		 */
		public void scheduleJob() {
			scheduleJob(250);
		}
		
		/**
		 * Schedules this job to run after the given delay.
		 * @param delay
		 */
		public void scheduleJob(long delay) {
			scheduled = true;
			schedule(delay);
		}
		
		/**
		 * Returns true if this job has been scheduled but hasn't run yet.
		 * @return boolean
		 */
		public synchronized boolean isScheduled() {
			return scheduled;
		}
		
		public IStatus runInUIThread(IProgressMonitor monitor) {
			scheduled = false;
			IStatus result = refreshTreeViewer();
			return result;
		}
		
	}

}
