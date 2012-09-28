package net.sourceforge.tagsea.mylyn.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tagsea.mylyn.core.HyperLinkDetector;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class TaskHyperlinkDetector extends HyperLinkDetector {

	private static final String regexp = TaskHyperlink.LINK_TAG
			+ "\\s?((.|\\s)+)\\n?";
	private static final Pattern PATTERN = Pattern.compile(regexp,
			Pattern.CASE_INSENSITIVE);

	protected IHyperlink extractHyperlink(ITextViewer viewer, int regionOffset, Matcher m) {

		IRegion sregion = getRegion(m, regionOffset, TaskHyperlink.LINK_TAG);

		IMarker todo = TaskUtils.markerFromText(m.group(1));
		if (todo == null) {
			this.strikeoutText(viewer, sregion);
			return null;
		} else {
			return new TaskHyperlink(sregion, todo);
		}
	}

	@Override
	protected Pattern getPattern() {
		return PATTERN;
	}

	protected IRegion getRegion(Matcher m, int regionOffset, String linkTag) {
		int start = m.start();
		int end = m.end();
		 
		start += (regionOffset + linkTag.length()+1);
		end += regionOffset - (linkTag.length() + 1);
	
		return new Region(start, end - start);
	}
}
