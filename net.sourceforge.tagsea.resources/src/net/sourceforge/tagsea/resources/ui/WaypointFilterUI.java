package net.sourceforge.tagsea.resources.ui;

import net.sourceforge.tagsea.core.ui.IWaypointFilter;
import net.sourceforge.tagsea.core.ui.IWaypointFilterUI;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointPreferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
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

	public void applyToFilter() {
		IPreferenceStore store = ResourceWaypointPlugin.getDefault().getPreferenceStore();
		if (anyResourceButton.getSelection()) {
			store.setValue(ResourceWaypointPreferences.FILTER_PREFERENCE_KEY, ResourceWaypointPreferences.FILTER_ANY);
		} else if (anyResourceInSameProjectButton.getSelection()){
			store.setValue(ResourceWaypointPreferences.FILTER_PREFERENCE_KEY, ResourceWaypointPreferences.FILTER_PROJECT);
		} else if (selectedResourceButton.getSelection()) {
			store.setValue(ResourceWaypointPreferences.FILTER_PREFERENCE_KEY, ResourceWaypointPreferences.FILTER_SELECTED);
		} else if (selectedResourceAndChildrenButton.getSelection()) {
			store.setValue(ResourceWaypointPreferences.FILTER_PREFERENCE_KEY, ResourceWaypointPreferences.FILTER_CHILDREN);
		}
	}

	public Control createControl(Composite parent) {
		 Composite group = new Composite(parent, SWT.NONE);
	        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        group.setLayout(new GridLayout());
	        group.setFont(parent.getFont());
	        anyResourceButton = createRadioButton(group, "On any element");
	        anyResourceInSameProjectButton = createRadioButton(group,
	                "On any element in selected project");// added by cagatayk@acm.org
	        selectedResourceButton = createRadioButton(group, "On selected element"); 
	        selectedResourceAndChildrenButton = createRadioButton(group,
	                "On selected element and its children");
	        String filterType = ResourceWaypointPreferences.getCurrentFilterType();
	        if (filterType.equals(ResourceWaypointPreferences.FILTER_ANY)) {
	        	 anyResourceButton.setSelection(true);
	        } else if (filterType.equals(ResourceWaypointPreferences.FILTER_CHILDREN)) {
	        	selectedResourceAndChildrenButton.setSelection(true);
	        } else if (filterType.equals(ResourceWaypointPreferences.FILTER_PROJECT)) {
	        	anyResourceInSameProjectButton.setSelection(true);
	        } else if (filterType.equals(ResourceWaypointPreferences.FILTER_SELECTED)) {
	        	selectedResourceButton.setSelection(true);
	        }
	    return group;
	}
	
	 /**
     * Creates a radio button with the given parent and text.
     *
     * @param parent the parent composite
     * @param text the text for the check box
     * @return the radio box button
     */
    Button createRadioButton(Composite parent, String text) {
        Button button = new Button(parent, SWT.RADIO);
        button.setText(text);
        button.setFont(parent.getFont());
         return button;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointFilterUI#initialize(net.sourceforge.tagsea.core.ui.IWaypointFilter)
	 */
	public void initialize(IWaypointFilter filter) {
		//doesn't need to do anything.		
	}


}
