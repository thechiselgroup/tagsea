package net.sourceforge.tagsea.tours.waypoints;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITagChangeEvent;
import net.sourceforge.tagsea.core.ITagChangeListener;
import net.sourceforge.tagsea.core.ITagSEAStartParticipant;
import net.sourceforge.tagsea.core.TagDelta;

public class TagSEAToursStartParticipant implements ITagSEAStartParticipant, ITagChangeListener {
	
	public void tagSEAStarting() {
		TagSEAPlugin.addTagChangeListener(this);
	}

	public void tagSEAStarted() {
		
	}

	public void tagsChanged(TagDelta delta) {
		for (ITagChangeEvent event : delta.getEvents()) {
			if (event.getType() == ITagChangeEvent.NEW) {
				WaypointTourElement.reloadForTag(event.getTag().getName());
			}
		}
	}

}
