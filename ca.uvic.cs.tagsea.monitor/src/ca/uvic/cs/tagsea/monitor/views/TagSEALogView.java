package ca.uvic.cs.tagsea.monitor.views;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import ca.uvic.cs.tagsea.monitor.ISimpleLogListener;
import ca.uvic.cs.tagsea.monitor.MonitorPreferences;
import ca.uvic.cs.tagsea.monitor.SimpleDayLog;
import ca.uvic.cs.tagsea.monitor.TagSEAMonitorPlugin;
import ca.uvic.cs.tagsea.monitor.jobs.UploadRunnable;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class TagSEALogView extends ViewPart {
	private TableViewer viewer;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class ViewContentProvider implements IStructuredContentProvider, ISimpleLogListener {
		private Viewer viewer;
		private SimpleDayLog input;
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			this.viewer = v;
			if (oldInput instanceof SimpleDayLog) {
				((SimpleDayLog)oldInput).removeListener(this);
			}
			if (newInput instanceof SimpleDayLog) {
				((SimpleDayLog)newInput).addListener(this);
				this.input = (SimpleDayLog) newInput;
				DateFormat f = DateFormat.getDateInstance(DateFormat.MEDIUM);
				setPartName("TagSEA Log: " + f.format(input.getDate()));
			}
		}
		public void dispose() {
			if (input != null) {
				input.removeListener(this);
			}
		}
		public Object[] getElements(Object parent) {
			if (parent instanceof SimpleDayLog) {
				SimpleDayLog log = (SimpleDayLog)parent;
				String eol = System.getProperty("line.separator");
				String[] split = log.getText().split(eol);
				String[] elements = new String[split.length-1];
				System.arraycopy(split, 1, elements, 0, elements.length);
				return elements;
			}
			return new Object[0];
		}
		/* (non-Javadoc)
		 * @see ca.uvic.cs.tagsea.monitor.ISimpleLogListener#lineAdded(java.lang.String)
		 */
		public void lineAdded(final String line) {
			if (viewer instanceof TableViewer) {
				getViewSite().getShell().getDisplay().asyncExec(new Runnable(){
					public void run() {
						if (!((TableViewer)viewer).getControl().isDisposed()){
						((TableViewer)viewer).add(line);
						((TableViewer)viewer).refresh(line, true);
						}
					}
				});
			}
		}
	}
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			switch (index) {
				case 0:
					return getTimeText(getText(obj));
				case 1:
					return getDataText(getText(obj));

			}
			return "";
		}
		
		
		/**
		 * @param text
		 * @return
		 */
		private String getDataText(String text) {
			int index = text.indexOf('>');
			if (index > 0) {
				String t = text.substring(0, index);
				if (t.startsWith("<Time:")) {
					text = text.substring(index+1, text.length()).trim();
				}
			}
			return text;
		}
				
		/**
		 * @param text
		 * @return
		 */
		private String getTimeText(String text) {
			String prefix = "<Time:";
			if (text.startsWith(prefix)) {
				int index = text.indexOf('>');
				if (index > 0) {
					text = text.substring(prefix.length(), index);
				}
			}
			return text;
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return null;
		}
	}

	/**
	 * The constructor.
	 */
	public TagSEALogView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.getTable().setHeaderVisible(true);
		TableColumn column = new TableColumn(viewer.getTable(), SWT.NONE);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(10, true));
		layout.addColumnData(new ColumnWeightData(90, true));
		column.setText("Time");
		column.setResizable(true);
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText("Data");
		viewer.getTable().setLayout(layout);
		SimpleDayLog log = null;
		try {
			log = TagSEAMonitorPlugin.getDefault().getMonitorLog();
		} catch (IOException e) {
		}
		viewer.setInput(log);
		makeActions();
		contributeToActionBars();
	}


	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		final MenuManager subMenu = new MenuManager("View Date");
		manager.addMenuListener(new IMenuListener(){
			public void menuAboutToShow(IMenuManager manager) {
				try {
					subMenu.removeAll();
					Date[] dates = TagSEAMonitorPlugin.getDefault().getLogDates();
					Arrays.sort(dates);
					DateFormat f = DateFormat.getDateInstance(DateFormat.MEDIUM);
					for (final Date date : dates) {
						Action a = new Action(f.format(date)){
							public void run() {
								try {
									viewer.setInput(TagSEAMonitorPlugin.getDefault().getMonitorLogForDate(date));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						};
						subMenu.add(a);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		manager.add(subMenu);
	}

	
	private void fillLocalToolBar(IToolBarManager manager) {
		ImageDescriptor d = TagSEAMonitorPlugin.getImageDescriptor("icons/upload.gif");
		Action a = new Action() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(getSite().getShell());
				try {
					//force an open.
					boolean asked = MonitorPreferences.hasAskedToRegister();
					MonitorPreferences.setAskedToRegister(false);
					dialog.run(true, false, new UploadRunnable());
					if (asked) {
						MonitorPreferences.setAskedToRegister(asked);
					}
				} catch (InvocationTargetException e) {
					MessageDialog.openError(getSite().getShell(), "Error", "An error occurred. See error log for details.");
					TagSEAMonitorPlugin.getDefault().log(e);
				} catch (InterruptedException e) {
				}
			}
		};
		a.setImageDescriptor(d);
		a.setToolTipText("Upload Logs");
		manager.add(a);
	}

	private void makeActions() {
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}