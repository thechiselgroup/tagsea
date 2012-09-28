package net.sourceforge.tagsea.parsed.ui.internal.preferences;

import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ParsedWaypointPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	private CheckboxTableViewer definitionSelector;
	
	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		page.setLayout(layout);
		definitionSelector = CheckboxTableViewer.newCheckList(page, SWT.BORDER);
		definitionSelector.setContentProvider(new ArrayContentProvider());
		definitionSelector.setLabelProvider(new WaypointDefinitionLabelProvider());
		definitionSelector.setInput(ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getContributedDefinitions());
		definitionSelector.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		loadSelection();
		return page;
	}

	@Override
	public boolean performOk() {
		Object[] checked = definitionSelector.getCheckedElements();
		String[] definitions = new String[checked.length];
		for (int i = 0; i < checked.length; i++) {
			definitions[i] = ((IParsedWaypointDefinition)checked[i]).getKind();
		}
		ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().setRegistered(definitions);
		return super.performOk();
	}
	
	/**
	 * 
	 */
	private void loadSelection() {
		IParsedWaypointDefinition[] registered  = 
			ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getRegisteredDefinitions();
		definitionSelector.setCheckedElements(registered);
	}

	public void init(IWorkbench workbench) {
	}
	


	
}