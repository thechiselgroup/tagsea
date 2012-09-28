/*******************************************************************************
 * 
 *   Copyright 2007, CHISEL Group, University of Victoria, Victoria, BC, Canada. 
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *       The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.mylyn.core.actions;

import net.sourceforge.tagsea.mylyn.core.actions.MylynTask;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public abstract class MylynRepositoryTask extends MylynTask {

	@Override
	protected AbstractTask createTask(String taskInfo){

		NewTaskWizard wizard = new NewTaskWizard();

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		if (shell != null && !shell.isDisposed()) {
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.setBlockOnOpen(true);
			if (dialog.open() == WizardDialog.CANCEL) {
				return null;
			}

			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			AbstractRepositoryTaskEditor editor = null;

			String summary = "";
			try {
				TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
				editor = (AbstractRepositoryTaskEditor) taskEditor
						.getActivePageInstance();
			} catch (ClassCastException e) {
				Clipboard clipboard = new Clipboard(page.getWorkbenchWindow()
						.getShell().getDisplay());
				clipboard.setContents(new Object[] { taskInfo.toString() },
						new Transfer[] { TextTransfer.getInstance() });

				MessageDialog
						.openInformation(
								page.getWorkbenchWindow().getShell(),
								ITasksUiConstants.TITLE_DIALOG,
								"This connector does not provide a rich task editor for creating tasks.\n\n"
										+ "The error contents have been placed in the clipboard so that you can paste them into the entry form.");
				return null;
			}

			editor.setSummaryText(summary);
			editor.setDescriptionText(taskInfo.toString());
			return editor.getRepositoryTask();
		}
		
		return null;
	}
}
