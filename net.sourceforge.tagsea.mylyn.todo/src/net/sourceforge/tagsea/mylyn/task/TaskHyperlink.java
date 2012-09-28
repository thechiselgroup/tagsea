package net.sourceforge.tagsea.mylyn.task;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;



public class TaskHyperlink implements IHyperlink {

	// @tag tagsea.mylyn.hyperlink.refactor : Refactor with WaypointHyperlink
	public static final String LINK_TAG = "<task>";
	private final IRegion region;
	private final IMarker todo;

	public TaskHyperlink(IRegion region, IMarker todo) {
		this.region = region;
		this.todo = todo;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return this.region;
	}

	@Override
	public String getHyperlinkText() {
		return "Go to task";
	}

	@Override
	public String getTypeLabel() {
		return "Task Hyperlink";
	}

	@Override
	public void open() {		
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, todo);
			return;
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
	}

	

}
