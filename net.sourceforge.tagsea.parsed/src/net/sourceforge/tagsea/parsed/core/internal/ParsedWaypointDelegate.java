package net.sourceforge.tagsea.parsed.core.internal;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeSet;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITagSEAOperationStateListener;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.IWaypointLocator;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointRegistry;
import net.sourceforge.tagsea.parsed.core.internal.operations.AbstractWaypointUpdateOperation;
import net.sourceforge.tagsea.parsed.core.internal.resources.DocumentRegistry;
import net.sourceforge.tagsea.parsed.core.internal.resources.ResourceListener;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

public class ParsedWaypointDelegate extends AbstractWaypointDelegate {
	public static final QualifiedName WAYPOINT_DEFINITIONS = new QualifiedName("net.sourceforge.tagsea.parsed", "projectWaypointDefinitions");
	
	/**
	 * Listens for TagSEA operations to ensure that the delegate only allows changes to
	 * occurr within certain operation kinds.
	 * @author Del Myers
	 *
	 */
	private class OperationWatcher implements ITagSEAOperationStateListener {
		private LinkedList<AbstractWaypointUpdateOperation> updateOperations = 
			new LinkedList<AbstractWaypointUpdateOperation>();
		public synchronized void stateChanged(TagSEAOperation operation) {
			if (operation instanceof AbstractWaypointUpdateOperation) {
				switch (operation.getState()) {
				case RUNNING:
					updateOperations.add((AbstractWaypointUpdateOperation) operation);
					break;
				case DONE:
					updateOperations.remove(operation);
					break;
				}
			}
		}
		
		public synchronized int getUpdateCount() {
			return updateOperations.size();
		}
		
		public synchronized boolean isUpdating() {
			return updateOperations.size() > 0;
		}
		
	}
	

	OperationWatcher updateStateWatcher;
	ParsedWaypointRegistry waypointRegistry;
	
	private IWaypointLocator locator;
	
	public ParsedWaypointDelegate() throws IOException, CoreException {
		waypointRegistry = new ParsedWaypointRegistry();
	}

	public IParsedWaypointRegistry getParsedWaypointRegistry() {
		return waypointRegistry;
		
	}

	@Override
	protected void load() {
		updateStateWatcher = new OperationWatcher();
		TagSEAPlugin.addOperationStateListener(updateStateWatcher);
		//load the contributed waypoint definitions. Will force a clean.
		try {
			waypointRegistry.initializeRegistry(!start());
		} catch (CoreException e) {
			ParsedWaypointPlugin.getDefault().log(e);
		}
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceListener());
		DocumentRegistry.INSTANCE.connect();
	}

	@Override
	protected void navigate(final IWaypoint waypoint) {
		if (!waypoint.exists()) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable(){
			public void run() {
				IMarker marker = ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getMarkerForWaypoint(waypoint);
				
				if (marker != null) {
					IWorkbenchPage page = 
						ParsedWaypointPlugin.
							getDefault().
							getWorkbench().
							getActiveWorkbenchWindow().
							getActivePage();
					try {
						if (page == null) return;
						IDE.openEditor(page, marker);
					} catch (PartInitException e) {
					}
				}
			}
		});

	}

	@Override
	protected void tagsChanged(TagDelta delta) {
	}

	@Override
	protected void waypointsChanged(WaypointDelta delta) {
		if (!updateStateWatcher.isUpdating()) {
			RefactoringSupport.applyWaypointChanges(delta);
		}
	}
	
	@Override
	public TagSEAChangeStatus processChange(IWaypointChangeEvent event) {
		if (updateStateWatcher.isUpdating()) {
			return TagSEAChangeStatus.SUCCESS_STATUS;
		}
		String kind = event.getWaypoint().getStringValue(IParsedWaypointAttributes.ATTR_KIND, null);
		IParsedWaypointDefinition def = ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getDefinition(kind);
		if (def != null) {
			switch (event.getType()) {
			case IWaypointChangeEvent.CHANGE:
				TreeSet<String> changeableAttributes = new TreeSet<String>();
				if (def.getRefactoringMethod().canSetAuthor(event.getWaypoint())) {
					changeableAttributes.add(IParsedWaypointAttributes.ATTR_AUTHOR);
				} if (def.getRefactoringMethod().canSetDate(event.getWaypoint())) {
					changeableAttributes.add(IParsedWaypointAttributes.ATTR_DATE);
				} if (def.getRefactoringMethod().canSetMessage(event.getWaypoint())) {
					changeableAttributes.add(IParsedWaypointAttributes.ATTR_MESSAGE);
				}
				TreeSet<String> changedAttributes = new TreeSet<String>(Arrays.asList(event.getChangedAttributes()));
				changedAttributes.removeAll(changeableAttributes);
				if (changedAttributes.size() > 0) {
					return new TagSEAChangeStatus(ParsedWaypointPlugin.PLUGIN_ID, false, 0, "attempt to change an immutable attribute");
				}
				return TagSEAChangeStatus.SUCCESS_STATUS;
			case IWaypointChangeEvent.TAGS_CHANGED:
				boolean okay = (def.getRefactoringMethod().canAddTags(event.getWaypoint()) && 
						def.getRefactoringMethod().canRemoveTags(event.getWaypoint()));
				if (!okay) {
					//see if only an add or remove is being done see if one of them is allowed.
					okay = (def.getRefactoringMethod().canAddTags(event.getWaypoint()) || 
							def.getRefactoringMethod().canRemoveTags(event.getWaypoint()));
					if (okay) {
						TreeSet<String> oldTags = new TreeSet<String>(Arrays.asList(event.getOldTags()));
						TreeSet<String> newTags = new TreeSet<String>(Arrays.asList(event.getNewTags()));
						TreeSet<String> intersection = new TreeSet<String>(newTags);
						intersection.removeAll(oldTags);

						if (intersection.size() > 0) {
							//tags are being added
							okay = def.getRefactoringMethod().canAddTags(event.getWaypoint());
						}
						if (okay) {
							intersection.clear();
							intersection.addAll(oldTags);
							intersection.removeAll(newTags);
							if (intersection.size() > 0) {
								okay &= def.getRefactoringMethod().canRemoveTags(event.getWaypoint());
							}
						}
						if (!okay) {
							return new TagSEAChangeStatus(ParsedWaypointPlugin.PLUGIN_ID, false, 0, "unsuccessful attempt to add or remove a tag from a waypoint.");
						}
					}
				}
				return TagSEAChangeStatus.SUCCESS_STATUS;
			case IWaypointChangeEvent.TAG_NAME_CHANGED:
				if (def.getRefactoringMethod().canReplaceTags(event.getWaypoint())) {
					return TagSEAChangeStatus.SUCCESS_STATUS;
				}
				return new TagSEAChangeStatus(ParsedWaypointPlugin.PLUGIN_ID, false, 0, "cannot change tag names");

			case IWaypointChangeEvent.DELETE:
				if (def.getRefactoringMethod().canDelete(event.getWaypoint())) {
					return TagSEAChangeStatus.SUCCESS_STATUS;
				}
				return new TagSEAChangeStatus(ParsedWaypointPlugin.PLUGIN_ID, false, 0, "cannot delete waypoint");

			}
		}
		return new TagSEAChangeStatus(ParsedWaypointPlugin.PLUGIN_ID, false, 0, "Operation not implemented");
	}

	/**
	 * Notified of start and stop to ensure that crashes are logged.
	 */
	public void stop() {
		File stateLocation = ParsedWaypointPlugin.getDefault().getStateLocation().toFile();
		File crashFile = new File(stateLocation, ".crash");
		try {
			if (crashFile.exists()) {
				crashFile.delete();
			}
		} catch (Exception e) {
			//do nothing, but make sure that the stop process can continue;
		}
	}

	/**
	 * @returns false if something went wrong with the last shut-down
	 */
	public boolean start() {
		File stateLocation = ParsedWaypointPlugin.getDefault().getStateLocation().toFile();
		File crashFile = new File(stateLocation, ".crash");
		try {
			if (crashFile.exists()) {
				return false;
			} else {
				crashFile.createNewFile();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public IWaypointLocator getLocator() {
		if (this.locator == null) {
			this.locator = new ParsedWaypointLocator();
		}
		return this.locator;
	}

	
}
