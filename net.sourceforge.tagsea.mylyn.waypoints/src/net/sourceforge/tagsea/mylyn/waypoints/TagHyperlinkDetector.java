package net.sourceforge.tagsea.mylyn.waypoints;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tagsea.core.ui.TagSEAView;
import net.sourceforge.tagsea.mylyn.core.HyperLinkDetector;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class TagHyperlinkDetector extends HyperLinkDetector {

	private static final String regexp = WaypointsUtils.TAG_MARKER
			+ "\\s?([\\w|\\.]+)";
	public static final Pattern PATTERN = Pattern.compile(regexp,
			Pattern.CASE_INSENSITIVE);

	protected IHyperlink extractHyperlink(ITextViewer viewer, int regionOffset,
			Matcher m) {

		IRegion sregion = getRegion(m, regionOffset, WaypointsUtils.TAG_MARKER);

		String tag = m.group();
		if (tag == null) {
			// Not sure that this will ever be called.
			this.strikeoutText(viewer, sregion);
			return null;
		} else {
			TagSEAView view = WaypointsUtils.getTagSEAView();
			return new TagsHyperlink(view, tag, sregion);
		}
	}

	protected Pattern getPattern() {
		return PATTERN;
	}

	public String[] getTagInfo(Matcher m) {
		List<String> tags = new ArrayList<String>();
		m.reset();
		while (m.find()) {
			String tagStr = m.group();
			tagStr = tagStr.replace(WaypointsUtils.TAG_MARKER, "");
			tags.add(tagStr.trim());
		}

		return tags.toArray(new String[0]);
	}

	protected IRegion getRegion(Matcher m, int regionOffset, String linkTag) {
		int start = m.start(1);
		int end = m.end(1);
		int length = end-start;
		
		start += regionOffset;
		
		return new Region(start, length);
	}
}
