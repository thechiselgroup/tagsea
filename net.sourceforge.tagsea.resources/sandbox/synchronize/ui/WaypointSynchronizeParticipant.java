package net.sourceforge.tagsea.resources.synchronize.ui;

import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.synchronize.AbstractSynchronizeParticipant;
import org.eclipse.team.ui.synchronize.ISynchronizePageConfiguration;
import org.eclipse.team.ui.synchronize.ISynchronizeParticipant;
import org.eclipse.team.ui.synchronize.ISynchronizeParticipantDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPageBookViewPage;

public class WaypointSynchronizeParticipant extends AbstractSynchronizeParticipant {
	public final static String PARTICIPANT_ID = "net.sourceforge.tagsea.resources.synchronize.participant"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	public WaypointSynchronizeParticipant() {
		setSecondaryId("secondid");
	}
	
	public static WaypointSynchronizeParticipant createParticipant() {
		WaypointSynchronizeParticipant participant = new WaypointSynchronizeParticipant();
		ISynchronizeParticipantDescriptor descriptor = TeamUI.getSynchronizeManager().getParticipantDescriptor(PARTICIPANT_ID);
		try {
			participant.setInitializationData(descriptor);
			TeamUI.getSynchronizeManager().addSynchronizeParticipants(new ISynchronizeParticipant[] {participant});
			
		} catch (CoreException e) {
			ResourceWaypointPlugin.getDefault().log(e);
		}
			
		return participant;
	}
	
	
		
	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.synchronize.AbstractSynchronizeParticipant#initializeConfiguration(org.eclipse.team.ui.synchronize.ISynchronizePageConfiguration)
	 */
	@Override
	protected void initializeConfiguration(ISynchronizePageConfiguration configuration) {
				
	}
	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.synchronize.AbstractSynchronizeParticipant#init(java.lang.String, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(String secondaryId, IMemento memento) throws PartInitException {
		super.init(secondaryId, memento);
		try {
			ISynchronizeParticipantDescriptor descriptor = TeamUI.getSynchronizeManager().getParticipantDescriptor(PARTICIPANT_ID);
			setInitializationData(descriptor);
		} catch (CoreException e) {
			ResourceWaypointPlugin.getDefault().log(e);
		}
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.synchronize.ISynchronizeParticipant#createPage(org.eclipse.team.ui.synchronize.ISynchronizePageConfiguration)
	 */
	public IPageBookViewPage createPage(ISynchronizePageConfiguration configuration) {
		return new WaypointSynchronizePage();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.synchronize.ISynchronizeParticipant#dispose()
	 */
	public void dispose() {
			
	}

	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.synchronize.ISynchronizeParticipant#run(org.eclipse.ui.IWorkbenchPart)
	 */
	public void run(IWorkbenchPart part) {
		
	}

}
