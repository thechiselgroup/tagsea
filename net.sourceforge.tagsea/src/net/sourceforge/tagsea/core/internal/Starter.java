package net.sourceforge.tagsea.core.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITagSEAStartParticipant;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUI;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IStartup;

public class Starter implements IStartup {
	public void earlyStartup() {
		//allow early participation to occur.
		IConfigurationElement[] elements = 
			Platform.getExtensionRegistry().getConfigurationElementsFor("net.sourceforge.tagsea.startParticipant"); //$NON-NLS-1$
		ArrayList<ITagSEAStartParticipant> starters = new ArrayList<ITagSEAStartParticipant>();
		for (IConfigurationElement el : elements) {
			if ("participant".equals(el.getName())) {
				try {
					ITagSEAStartParticipant starter = (ITagSEAStartParticipant)el.createExecutableExtension("class");
					starter.tagSEAStarting();
					starters.add(starter);
				} catch (Exception e) {
					TagSEAPlugin.getDefault().log(e);
				} catch (Throwable t) {
					TagSEAPlugin.getDefault().log(new InvocationTargetException(t, "Unable to run start participant " + el.getName()));
				}
			}
		}
		//load all the waypoints.
		for (AbstractWaypointDelegate delegate : TagSEAPlugin.getDefault().getWaypointDelegates()) {
			try {
				delegate.initialize();
			} catch (Throwable t) {
				TagSEAPlugin.getDefault().log(new InvocationTargetException(t, "Unable to initialize delegate " + delegate.getType()));
			}
		}
		((TagSEAUI)TagSEAPlugin.getDefault().getUI()).start();
		for (ITagSEAStartParticipant starter : starters) {
			starter.tagSEAStarted();
		}
	}
}
