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
package net.sourceforge.tagsea.instrumentation.network.ui;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.sourceforge.tagsea.instrumentation.InstrumentationPreferences;
import net.sourceforge.tagsea.instrumentation.TagSEAInstrumentationPlugin;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

/**
 * Wizard page that allows the user to select logs for upload.
 * @author Del Myers
 *
 */
public class UploadSelectionWizardPage extends WizardPage {
	private CheckboxTableViewer logsTable;
	private CheckboxTableViewer waypointsTable;
	private List<File> logFiles;
	private List<File> waypointFiles;
	private Button agreementButton;

	private class FileLabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof File) {
				if (columnIndex == 0) {
					return ((File)element).getName();
				} else if (columnIndex == 1) {
					Date date = getFileDate((File)element);
					if (date != null) {
						return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
					}
				}
				
			}
			return element.toString();
		}

		public void addListener(ILabelProviderListener listener) {}

		public void dispose() {}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		public void removeListener(ILabelProviderListener listener) {}
		
	}
	
	private class FileDateSorter extends ViewerComparator {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 instanceof File && e2 instanceof File) {
				File f1 = (File) e1;
				File f2 = (File) e2;
				Date d1 = getFileDate(f1);
				Date d2 = getFileDate(f2);
				if (d1 == null) {
					if (d2 == null) {
						return getFileExtension(f1).compareTo(getFileExtension(f2));
					}
					return -1;
				} else if (d2 == null) {
					return 1;
				}
				if (d1.equals(d2)) {
					return getFileExtension(f1).compareTo(getFileExtension(f2));
				}
				if (d1.after(d2)) {
					return 1;
				}
				return -1;
			}
			return super.compare(viewer, e1, e2);
		}
	}
	
	/**
	 * @param pageName
	 */
	protected UploadSelectionWizardPage() {
		super("Log Selection", "Log Selection", TagSEAInstrumentationPlugin.imageDescriptorFromPlugin(TagSEAInstrumentationPlugin.PLUGIN_ID, "/icons/upload.png"));
	}

	public void createControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(3, false));
		Label label = new Label(page, SWT.NONE);
		label.setText("Please Select Files to Upload");
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan=3;
		label.setLayoutData(data);
		Group logs = new Group(page, SWT.FLAT);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan=3;
		logs.setLayoutData(data);
		logs.setText("Logs");
		logs.setLayout(new GridLayout());
		logsTable = createTableViewer(logs);
		if (logFiles == null) {
			logFiles = new ArrayList<File>();
		}
		logsTable.setInput(logFiles);
		logsTable.setAllChecked(true);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		//data.widthHint = 200;
		//data.heightHint = 200;
		logsTable.getTable().setLayoutData(data);
		
		Group waypoints = new Group(page, SWT.FLAT);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan=3;
		waypoints.setLayoutData(data);
		waypoints.setText("Waypoints");
		waypoints.setLayout(new GridLayout());
		waypointsTable = createTableViewer(waypoints);
		if (waypointFiles == null) {
			waypointFiles = new ArrayList<File>();
		}
		waypointsTable.setInput(waypointFiles);
		waypointsTable.setAllChecked(true);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		//data.widthHint = 200;
		//data.heightHint = 200;
		waypointsTable.getTable().setLayoutData(data);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		Button button = new Button(page, SWT.PUSH);
		button.setText("Select All");
		button.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				waypointsTable.setAllChecked(true);
				logsTable.setAllChecked(true);
			}
		});
		button.setLayoutData(data);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		button = new Button(page, SWT.PUSH);
		button.setText("Select None");
		button.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				waypointsTable.setAllChecked(false);
				logsTable.setAllChecked(false);
			}
		});
		button.setLayoutData(data);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		button = new Button(page, SWT.PUSH);
		button.setText("Review Files...");
		button.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				LogTabDialog dialog = new LogTabDialog(getShell(), getSelectedFiles());
				dialog.open();
			}
		});
		button.setLayoutData(data);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), "net.sourceforge.tagsea.instrumentation.uploadnow");
		agreementButton = new Button(page, SWT.CHECK);
		agreementButton.setText("*I agree to the terms of the TagSEA Monitoring Consent Agreement");
		agreementButton.setForeground(agreementButton.getDisplay().getSystemColor(SWT.COLOR_RED));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(agreementButton, "net.sourceforge.tagsea.instrumentation.consent");
		agreementButton.setSelection(InstrumentationPreferences.isMonitoringEnabled());
		agreementButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				agreementButton.notifyListeners(SWT.Help, new Event());
				checkAgreement();
			}
		});
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = 3;
		agreementButton.setLayoutData(data);
		agreementButton.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
		checkAgreement();
		setControl(page);
		getShell().addShellListener(new ShellListener() {
			public void shellActivated(ShellEvent e) {
				getShell().notifyListeners(SWT.Help, new Event());
			}

			public void shellClosed(ShellEvent e) {
			}

			public void shellDeactivated(ShellEvent e) {
			}

			public void shellDeiconified(ShellEvent e) {
			}

			public void shellIconified(ShellEvent e) {
			}
			
		});
		
	}
	/**
	 * 
	 */
	protected void checkAgreement() {
		setPageComplete(agreementButton.getSelection());
		if (!isPageComplete()) {
			setErrorMessage("You must agree to the TagSEA Monitoring Consent Agreement (press F1 for help).");
		} else {
			setErrorMessage(null);
		}
	}

	/**
	 * @param logs
	 * @return
	 */
	private CheckboxTableViewer createTableViewer(Group logs) {
		CheckboxTableViewer table = CheckboxTableViewer.newCheckList(logs, SWT.FLAT | SWT.BORDER);
		TableColumn logs0 = new TableColumn(table.getTable(), SWT.NONE, 0);
		TableColumn logs11 = new TableColumn(table.getTable(), SWT.NONE, 1);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1));
		layout.addColumnData(new ColumnWeightData(1));
		table.getTable().setLayout(layout);
		table.setLabelProvider(new FileLabelProvider());
		table.setContentProvider(new ArrayContentProvider());
		table.setComparator(new FileDateSorter());
		return table;
	}

	
	public void setFiles(File[] files) {
		waypointFiles = new ArrayList<File>();
		logFiles = new ArrayList<File>();
		for (File file : files) {
			String extension = getFileExtension(file);
			if ("modellog".equals(extension) || "uilog".equals(extension)) {
				logFiles.add(file);
			} else if ("waypoints".equals(extension)) {
				waypointFiles.add(file);
			}
		}
		if (logsTable != null && !logsTable.getTable().isDisposed()) {
			logsTable.setInput(logFiles);
		}
		if (waypointsTable != null && !waypointsTable.getTable().isDisposed()) {
			waypointsTable.setInput(waypointFiles);
		}
		if (getControl() != null)
			((Composite)getControl()).layout();
		
	}
	
	public File[] getSelectedFiles() {
		Object[] checkedWaypoints = waypointsTable.getCheckedElements();
		Object[] checkedLogs = logsTable.getCheckedElements();
		File[] files = new File[checkedWaypoints.length + checkedLogs.length];
		int i = 0;
		for (; i < checkedWaypoints.length; i++) {
			files[i] = (File) checkedWaypoints[i];
		}
		for (int j = 0; j < checkedLogs.length; j++, i++) {
			files[i] = (File) checkedLogs[j];
		}
		return files;
	}
	
	private Date getFileDate(File file) {
		int dot = file.getName().lastIndexOf('.');
		if (dot != -1) {
			String dateString = file.getName().substring(0, dot).replace('-', '/');
			try {
				return DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).parse(dateString);
			} catch (ParseException e) {
			}
		}
		return null;
	}
	
	private String getFileExtension(File file) {
		int dot = file.getName().lastIndexOf('.');
		if (dot == -1 || dot >= file.getName().length()) {
			return "";
		}
		return file.getName().substring(dot+1);
	}

}
