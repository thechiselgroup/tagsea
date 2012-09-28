package net.sourceforge.tagsea.java.waypoints;

import net.sourceforge.tagsea.core.ui.IWaypointFilter;
import net.sourceforge.tagsea.core.ui.IWaypointFilterUI;
import net.sourceforge.tagsea.java.JavaTagsPlugin;
import net.sourceforge.tagsea.java.JavaWaypointPreferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class WaypointFilterUI implements IWaypointFilterUI {

	private Button anyResourceButton;
	private Button anyResourceInSameProjectButton;
	private Button selectedResourceButton;
	private Button selectedResourceAndChildrenButton;
	private Button linkToResourceButton;

	public void applyToFilter() {
		IPreferenceStore store = JavaTagsPlugin.getDefault().getPreferenceStore();
		if (anyResourceButton.getSelection()) {
			store.setValue(JavaWaypointPreferences.FILTER_PREFERENCE_KEY, JavaWaypointPreferences.FILTER_ANY);
		} else if (anyResourceInSameProjectButton.getSelection()){
			store.setValue(JavaWaypointPreferences.FILTER_PREFERENCE_KEY, JavaWaypointPreferences.FILTER_PROJECT);
		} else if (selectedResourceButton.getSelection()) {
			store.setValue(JavaWaypointPreferences.FILTER_PREFERENCE_KEY, JavaWaypointPreferences.FILTER_SELECTED);
		} else if (selectedResourceAndChildrenButton.getSelection()) {
			store.setValue(JavaWaypointPreferences.FILTER_PREFERENCE_KEY, JavaWaypointPreferences.FILTER_CHILDREN);
		}
		store.setValue(JavaWaypointPreferences.LINK_TO_RESOURCES_KEY, linkToResourceButton.getSelection());
	}

	public Control createControl(Composite parent) {

		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout());
		page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		linkToResourceButton = createButton(page, "Link To Resource Waypoint Filter", SWT.CHECK);
		Composite group = new Composite(page, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout();
		layout.marginLeft = 15;
		group.setLayout(layout);
		group.setFont(parent.getFont());
		anyResourceButton = createButton(group, "On any element", SWT.RADIO);
		anyResourceInSameProjectButton = createButton(group,
				"On any element in selected project", SWT.RADIO);// added by cagatayk@acm.org
		selectedResourceButton = createButton(group, "On selected element", SWT.RADIO); 
		selectedResourceAndChildrenButton = createButton(group,
				"On selected element and its children", SWT.RADIO);
		String filterType = JavaWaypointPreferences.getCurrentFilterType();
		if (filterType.equals(JavaWaypointPreferences.FILTER_ANY)) {
			anyResourceButton.setSelection(true);
		} else if (filterType.equals(JavaWaypointPreferences.FILTER_CHILDREN)) {
			selectedResourceAndChildrenButton.setSelection(true);
		} else if (filterType.equals(JavaWaypointPreferences.FILTER_PROJECT)) {
			anyResourceInSameProjectButton.setSelection(true);
		} else if (filterType.equals(JavaWaypointPreferences.FILTER_SELECTED)) {
			selectedResourceButton.setSelection(true);
		}
		if (JavaWaypointPreferences.isFilterLinkedToResources()) {
			linkToResourceButton.setSelection(true);
			setRadioEnablment(false);
		}
		linkToResourceButton.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setRadioEnablment(!linkToResourceButton.getSelection());
			}
		});
		return page;
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
     * @param parent the parent composite
     * @param text the text for the check box
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
