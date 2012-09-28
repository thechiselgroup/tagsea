

package net.sourceforge.tagsea.resources.synchronize.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.team.ui.IConfigurationWizard;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.synchronize.ISynchronizeManager;
import org.eclipse.team.ui.synchronize.ISynchronizeParticipant;
import org.eclipse.team.ui.synchronize.ISynchronizeView;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for showing information about synchronizing waypoints.
 * @author Del Myers
 *
 */
public class SyncrhonizedConfigurationWizard extends Wizard implements
		IConfigurationWizard {

	public SyncrhonizedConfigurationWizard() {
	}

	@Override
	public boolean performFinish() {
		WaypointSynchronizeParticipant participant = WaypointSynchronizeParticipant.createParticipant();
		ISynchronizeManager manager = TeamUI.getSynchronizeManager();
		manager.addSynchronizeParticipants(new ISynchronizeParticipant[]{participant});
		ISynchronizeView view = manager.showSynchronizeViewInActivePage();
		view.display(participant);
		return true;
	}

	public void init(IWorkbench workbench, IProject project) {
	}

}
