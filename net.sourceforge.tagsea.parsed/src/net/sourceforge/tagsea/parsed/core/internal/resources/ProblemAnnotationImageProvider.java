package net.sourceforge.tagsea.parsed.core.internal.resources;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.parser.IReplacementProposal;
import net.sourceforge.tagsea.parsed.parser.WaypointParseProblem;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class ProblemAnnotationImageProvider implements IAnnotationImageProvider {
	ImageRegistry localRegistry;

	public ProblemAnnotationImageProvider() {
		localRegistry = new ImageRegistry();
		localRegistry.put(ITagSEAImageConstants.IMG_WAYPOINT_FIX,
				ParsedWaypointPlugin.imageDescriptorFromPlugin(
						ParsedWaypointPlugin.PLUGIN_ID, "icons/bulb.png")
						.createImage());
	}

	public ImageDescriptor getImageDescriptor(String imageDescritporId) {
		ImageDescriptor desc = localRegistry.getDescriptor(imageDescritporId);
		if (desc == null) {
			return TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(
					imageDescritporId);
		}
		return desc;
	}

	public String getImageDescriptorId(Annotation annotation) {
		if (!(annotation instanceof MarkerAnnotation)) {
			return null;
		}
		MarkerAnnotation ppa = (MarkerAnnotation) annotation;
		WaypointParseProblem problem = DocumentRegistry.INSTANCE
				.getProblemForMarker(ppa.getMarker());
		if (problem == null)
			return null;

		switch (problem.getSeverity()) {
		case WaypointParseProblem.SEVERITY_ERROR:
			return ITagSEAImageConstants.IMG_WAYPOINT_ERROR;
		case WaypointParseProblem.SEVERITY_WARNING:
			return ITagSEAImageConstants.IMG_WAYPOINT_WARNING;
		}
		IReplacementProposal[] proposals = problem.getProposals();
		if (proposals.length > 0) {
			return ITagSEAImageConstants.IMG_WAYPOINT_FIX;
		}
		return null;
	}

	public Image getManagedImage(Annotation annotation) {
		String id = getImageDescriptorId(annotation);
		if (id != null) {
			Image i = localRegistry.get(id);
			if (i == null) {
				return TagSEAPlugin.getDefault().getImageRegistry().get(id);
			}
			return i;
		}
		return null;
	}

}
