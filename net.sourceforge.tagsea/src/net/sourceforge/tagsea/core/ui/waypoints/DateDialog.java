/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/

package net.sourceforge.tagsea.core.ui.waypoints;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.vafada.swtcalendar.SWTCalendar;

/**
 * A dialog that provides a date.
 * @author Del Myers
 */

public class DateDialog extends Dialog {

	private SWTCalendar calendar;
	private Date date;

	/**
	 * @param parentShell
	 */
	public DateDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = new Composite(parent, SWT.FLAT);
		page.setLayout(new FillLayout());
		this.calendar = new SWTCalendar(page, SWT.FLAT);
		if (this.date != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			this.calendar.setCalendar(c);
		}
		return page;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		this.date = calendar.getCalendar().getTime();
		super.okPressed();
	}
	
	public Date getDate() {
		return date;
	}

}
