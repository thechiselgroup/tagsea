package net.sourceforge.tagsea.mylyn.waypoints;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ui.TagSEAView;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class TagsHyperlink implements IHyperlink {

	
	private String tagName;
	private final TagSEAView view;
	private final IRegion region;

	public TagsHyperlink(TagSEAView view, String tag, IRegion region) {
		this.view = view;
		this.tagName = tag;
		this.region = region;
	}
	
	@Override
	public IRegion getHyperlinkRegion() {
		return this.region;
	}

	@Override
	public String getHyperlinkText() {
		return "Show tag in TagSEA tag view";
	}

	@Override
	public String getTypeLabel() {
		return "Tag Hyperlink";
	}

	@Override
	public void open() {
		ITag tag = TagSEAPlugin.getTagsModel().getTag(getTag());
		if (tag != null) {
			view.setSelectedTags(new ITag[] {tag});
		}
	}

	public String getTag() {
		
		return tagName.replace(WaypointsUtils.TAG_MARKER, "").trim();
	}

}
