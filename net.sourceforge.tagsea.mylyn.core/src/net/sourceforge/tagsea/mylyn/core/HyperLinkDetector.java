package net.sourceforge.tagsea.mylyn.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

public abstract class HyperLinkDetector extends AbstractHyperlinkDetector {

	protected abstract IHyperlink extractHyperlink(ITextViewer viewer,
			int regionOffset, Matcher m);

	protected abstract Pattern getPattern();

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {

		if (region == null || textViewer == null)
			return null;

		IDocument document = textViewer.getDocument();
		if (document == null) {
			return null;
		}

		IRegion lineInfo;
		String line;
		try {
			lineInfo = document.getLineInformationOfOffset(region.getOffset());
			line = document.get(lineInfo.getOffset(), lineInfo.getLength());
		} catch (BadLocationException ex) {
			return null;
		}

		
		List<IHyperlink> links = findHyperlinks(textViewer, region, lineInfo,
				line);

		if (!links.isEmpty()) {
			return links.toArray(new IHyperlink[1]);
		}

		return null;
	}

	private List<IHyperlink> findHyperlinks(ITextViewer viewer, IRegion region,
			IRegion lineInfo, String line) {
		int lineOffset = region.getOffset() - lineInfo.getOffset();
		int regionOffset = lineInfo.getOffset();
		List<IHyperlink> links = new ArrayList<IHyperlink>();
		Matcher m = getPattern().matcher(line);
		while (m.find()) {
			
				IHyperlink link = extractHyperlink(viewer, regionOffset, m);
				if (link != null)
					links.add(link);
			
		}
		return links;
	}

	protected void strikeoutText(ITextViewer viewer, IRegion sregion) {
		// Strike through text
		StyledText styledText = viewer.getTextWidget();
		StyleRange[] ranges = styledText.getStyleRanges(sregion.getOffset(),
				sregion.getLength());
		for (StyleRange style : ranges) {
			style.strikeout = true;
			styledText.setStyleRange(style);
		}
	}
}
