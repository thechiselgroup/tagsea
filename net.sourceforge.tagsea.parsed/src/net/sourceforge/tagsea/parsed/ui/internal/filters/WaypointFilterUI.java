package net.sourceforge.tagsea.parsed.ui.internal.filters;

import java.util.Arrays;
import java.util.HashSet;

import net.sourceforge.tagsea.core.ui.IWaypointFilter;
import net.sourceforge.tagsea.core.ui.IWaypointFilterUI;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.ui.internal.preferences.PreferenceConstants;
import net.sourceforge.tagsea.parsed.ui.internal.preferences.WaypointDefinitionLabelProvider;
import net.sourceforge.tagsea.resources.ResourceWaypointPreferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class WaypointFilterUI implements IWaypointFilterUI {

	private Button anyResourceButton;
	private Button anyResourceInSameProjectButton;
	private Button selectedResourceButton;
	private Button selectedResourceAndChildrenButton;
	private Button linkToResourceButton;
	private CheckboxTableViewer checkbox;

	private class GrayedWaypointDefinitionLabelProvider extends
			WaypointDefinitionLabelProvider implements IColorProvider {

		public Color getBackground(Object element) {
			return null;
		}

		public Color getForeground(Object element) {
			if (!ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry()
					.isRegistered(
							((IParsedWaypointDefinition) element).getKind())) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
			}
			return null;
		}

	}

	public void applyToFilter() {
		IPreferenceStore store = ParsedWaypointPlugin.getDefault()
				.getPreferenceStore();
		if (anyResourceButton.getSelection()) {
			store.setValue(ResourceWaypointPreferences.FILTER_PREFERENCE_KEY,
					ResourceWaypointPreferences.FILTER_ANY);
		} else if (anyResourceInSameProjectButton.getSelection()) {
			store.setValue(ResourceWaypointPreferences.FILTER_PREFERENCE_KEY,
					ResourceWaypointPreferences.FILTER_PROJECT);
		} else if (selectedResourceButton.getSelection()) {
			store.setValue(ResourceWaypointPreferences.FILTER_PREFERENCE_KEY,
					ResourceWaypointPreferences.FILTER_SELECTED);
		} else if (selectedResourceAndChildrenButton.getSelection()) {
			store.setValue(ResourceWaypointPreferences.FILTER_PREFERENCE_KEY,
					ResourceWaypointPreferences.FILTER_CHILDREN);
		}
		store.setValue(PreferenceConstants.LINKED_TO_RESOURCE_WAYPOINT_FILTERS,
				linkToResourceButton.getSelection());
		String filteredString = "";
		IParsedWaypointDefinition[] defs = 
			ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getContributedDefinitions();
		HashSet<IParsedWaypointDefinition> filteredSet = new HashSet<IParsedWaypointDefinition>(Arrays.asList(defs));
		filteredSet.removeAll(Arrays.asList(checkbox.getCheckedElements()));
		for (IParsedWaypointDefinition def : filteredSet) {
			filteredString = filteredString + def.getKind() + " ";
		}
		filteredString = filteredString.trim();
		store.setValue(PreferenceConstants.FILTERED_KINDS, filteredString);
	}

	public Control createControl(Composite parent) {
		IPreferenceStore store = ParsedWaypointPlugin.getDefault()
				.getPreferenceStore();
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));
		Composite page = new Composite(control, SWT.NONE);
		page.setLayout(new GridLayout());
		page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		linkToResourceButton = createButton(page,
				"Link to Resources Filter", SWT.CHECK);
		Composite group = new Composite(page, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout();
		layout.marginLeft = 15;
		group.setLayout(layout);
		group.setFont(parent.getFont());
		anyResourceButton = createButton(group, "On any element", SWT.RADIO);
		anyResourceInSameProjectButton = createButton(group,
				"On any element in selected project", SWT.RADIO);// added by
																	// cagatayk@acm.org
		selectedResourceButton = createButton(group, "On selected element",
				SWT.RADIO);
		selectedResourceAndChildrenButton = createButton(group,
				"On selected element and its children", SWT.RADIO);
		String filterType = ResourceWaypointPreferences.getCurrentFilterType();
		if (filterType.equals(ResourceWaypointPreferences.FILTER_ANY)) {
			anyResourceButton.setSelection(true);
		} else if (filterType
				.equals(ResourceWaypointPreferences.FILTER_CHILDREN)) {
			selectedResourceAndChildrenButton.setSelection(true);
		} else if (filterType
				.equals(ResourceWaypointPreferences.FILTER_PROJECT)) {
			anyResourceInSameProjectButton.setSelection(true);
		} else if (filterType
				.equals(ResourceWaypointPreferences.FILTER_SELECTED)) {
			selectedResourceButton.setSelection(true);
		}
		if (store
				.getBoolean(PreferenceConstants.LINKED_TO_RESOURCE_WAYPOINT_FILTERS)) {
			linkToResourceButton.setSelection(true);
			setRadioEnablment(false);
		}
		linkToResourceButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setRadioEnablment(!linkToResourceButton.getSelection());
			}
		});

		checkbox = CheckboxTableViewer.newCheckList(control,
				SWT.BORDER);
		checkbox.setContentProvider(new ArrayContentProvider());
		checkbox.setLabelProvider(new GrayedWaypointDefinitionLabelProvider());
		checkbox.setInput(ParsedWaypointPlugin.getDefault()
				.getParsedWaypointRegistry().getContributedDefinitions());
		checkbox.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		initializeCheckedState();
		return control;
	}

	/**
	 * 
	 */
	private void initializeCheckedState() {
		for (IParsedWaypointDefinition def : ParsedWaypointPlugin.getDefault()
				.getParsedWaypointRegistry().getContributedDefinitions())	{
			checkbox.setChecked(def, isVisible(def));
		}
	}

	private boolean isVisible(IParsedWaypointDefinition def) {
		IPreferenceStore store = ParsedWaypointPlugin.getDefault().getPreferenceStore();
		String filtered = store.getString(PreferenceConstants.FILTERED_KINDS);
		String[] kinds = filtered.split("\\s+");
		for (String kind : kinds) {
			if (def.getKind().equals(kind)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param b
	 */
	private void setRadioEnablment(boolean b) {
		anyResourceButton.setEnabled(b);
		anyResourceInSameProjectButton.setEnabled(b);
		selectedResourceAndChildrenButton.setEnabled(b);
		selectedResourceButton.setEnabled(b);
	}

	/**
	 * Creates a radio button with the given parent and text.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param text
	 *            the text for the check box
	 * @return the radio box button
	 */
	Button createButton(Composite parent, String text, int type) {
		Button button = new Button(parent, type);
		button.setText(text);
		button.setFont(parent.getFont());
		return button;
	}

	public void initialize(IWaypointFilter filter) {
	}

}
