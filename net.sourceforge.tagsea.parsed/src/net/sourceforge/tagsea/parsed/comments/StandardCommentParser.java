/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.parsed.comments;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.sourceforge.tagsea.parsed.parser.BasicCommentParser;
import net.sourceforge.tagsea.parsed.parser.IParsedWaypointDescriptor;
import net.sourceforge.tagsea.parsed.parser.IReplacementProposal;
import net.sourceforge.tagsea.parsed.parser.IWaypointParseProblemCollector;
import net.sourceforge.tagsea.parsed.parser.ReplacementProposal;
import net.sourceforge.tagsea.parsed.parser.WaypointParseError;
import net.sourceforge.tagsea.parsed.parser.WaypointParseProblem;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * A parser that scans a document for comment regions and retrieves waypoint
 * descriptors based on a standard syntax. The standard syntax is as follows:
 * <code>&amp;tag <i>&lt;tags&gt;</i> <i>&lt;attributes&gt;</i> <i>: message</i></code>
 * Where <code>tags</code> is a list of white-space separated tags. Tag names
 * may be delimited with '.' characters to generate a tag hierarchy.
 * <code>attributes</code> is a list of attributes in the following format:
 * <code>-name=value</code> If the value must contain white space or a colon
 * (:), the value may be placed within double quotes ("). Valid names are
 * <code>author</code> and <code>date</code>. Date values may specify a
 * country and local. If a country and local are supplied, the following format
 * is used: <code>-date="languageCOUNTRY:dateValue"</code> where
 * <code>language</code> is the two-character ISO language code, and COUNTRY
 * is the two-character ISO country code. If no locale is specified, then the
 * default locale is used to try and parse the date. Date values are always
 * specified in Java DateFormat.SHORT format. For example:
 * <code>-date="enCA:01/01/01"</code> would represent January 1st, 2001 in
 * Canadian English.
 * 
 * @author Del Myers
 * 
 */
public class StandardCommentParser extends BasicCommentParser {

	private String waypointKind;
	private IDomainMethod domainMethod;
	protected static final String TAG_STRING = "@tag"; //$NON-NLS-1$
	protected static final Object TAG_KEY = new Object();
	private static RuleBasedScanner fWaypointScanner;
	static {
		fWaypointScanner = new RuleBasedScanner();
		EndOfLineRule waypointRule = new EndOfLineRule(TAG_STRING, new Token(
				TAG_KEY));
		fWaypointScanner.setRules(new IRule[] { waypointRule });
	}

	private class LocalWaypointParseProblem extends WaypointParseProblem {
		private IReplacementProposal[] proposals;
		private int severity;
		private String message;

		public LocalWaypointParseProblem(int start, int length,
				IDocument document, String message, int severity,
				IReplacementProposal proposal) {
			this(start, length, document, message, severity, Arrays
					.asList(new IReplacementProposal[] { proposal }));
		}

		public LocalWaypointParseProblem(int start, int length,
				IDocument document, String message, int severity,
				List<IReplacementProposal> proposals) {
			super(start, length, document);
			this.proposals = proposals
					.toArray(new IReplacementProposal[proposals.size()]);
			this.severity = severity;
			this.message = message;
		}

		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public IReplacementProposal[] getProposals() {
			return proposals;
		}

		@Override
		public int getSeverity() {
			return severity;
		}
	}

	private class AttributeProposal implements IReplacementProposal {
		private String displayString;
		private String[] attrs;
		private String[] values;
		private int offset;
		private int length;

		public AttributeProposal(String displayString, String[] attrs,
				String[] values, int offset, int length) {
			this.displayString = displayString;
			this.attrs = attrs;
			this.values = values;
			this.offset = offset;
			this.length = length;
		}

		public void apply(IDocument document) throws BadLocationException {
			String tagText = document.get(offset, length);
			// scan for the colon
			int location = 0;
			boolean done = location >= tagText.length();
			boolean inQuote = false;
			String replaceString = "";
			for (int i = 0; i < attrs.length; i++) {
				replaceString = replaceString + attrs[i] + "=\"" + values[i]
						+ "\" ";
			}
			while (!done) {
				char c = tagText.charAt(location);
				switch (c) {
				case ':':
					done = !inQuote;
					break;
				case '"':
					inQuote = !inQuote;
					break;
				}
				if (!done)
					location++;
				done = done || location >= tagText.length();
			}
			if (location < tagText.length()) {
				if (tagText.charAt(location) == ':') {
					// replace with the new text.
					if (!Character.isWhitespace(tagText.charAt(location-1))) {
						replaceString = " " + replaceString;
					}
					document.replace(offset + location, 0, replaceString);
				}
			} else if (location == tagText.length()) {
				if (!Character.isWhitespace(tagText.charAt(location-1))) {
					replaceString = " " + replaceString;
				}
				document.replace(offset+location, 0, replaceString);
			}
		}

		public String getDisplayString() {
			return displayString;
		}

	}

	/**
	 * @param singleLine
	 * @param multiLineStart
	 * @param multiLineEnd
	 */
	public StandardCommentParser(String[] singleLine, String[] multiLineStart,
			String[] multiLineEnd, String[] exclusionStart, String[] exclusionEnd, 
			String waypointKind) {
		super(singleLine, multiLineStart, multiLineEnd, exclusionStart, exclusionEnd);
		this.waypointKind = waypointKind;
	}

	@Override
	protected List<IParsedWaypointDescriptor> doParseInComment(
			IDocument document, IRegion region,
			IWaypointParseProblemCollector collector) {
		List<IRegion> waypointRegions = calculateWaypointRegions(document,
				region);
		LinkedList<IParsedWaypointDescriptor> descriptors = new LinkedList<IParsedWaypointDescriptor>();
		String author = System.getProperty("user.name");
		Locale locale = Locale.getDefault();
		DateFormat format = DateFormat
				.getDateInstance(DateFormat.SHORT, locale);
		Date date = new Date();
		String dateString = locale.getLanguage().toLowerCase()
				+ locale.getCountry().toUpperCase() + ":" + format.format(date);
		for (IRegion waypointRegion : waypointRegions) {
			try {
				int line = document.getLineOfOffset(waypointRegion.getOffset());
				StandardCommentWaypointDescriptor d = StandardCommentTextParser
						.parse(document.get(waypointRegion.getOffset(),
								waypointRegion.getLength()), waypointRegion
								.getOffset(), line);
				if (d != null) {
					d.setDetail(getDomainObject(document, region));
					descriptors.add(d);
				}
				if (d.getAuthor() == null && d.getDate() == null) {
					collector.accept(new LocalWaypointParseProblem(d
							.getCharStart(), d.getCharEnd() - d.getCharStart(),
							document, "Missing author and date",
							WaypointParseProblem.SEVERITY_INFO,
							new AttributeProposal("Add the author and date",
									new String[] { "-author", "-date" },
									new String[] { author, dateString }, d.getCharStart(),
									d.getLength())));
				}
				if (d.getAuthor() == null) {
					// add the author quick-fix
					collector.accept(new LocalWaypointParseProblem(d
							.getCharStart(), d.getCharEnd() - d.getCharStart(),
							document, "Missing author",
							WaypointParseProblem.SEVERITY_INFO,
							new AttributeProposal("Add the author",
									new String[] { "-author" },
									new String[] { author }, d.getCharStart(),
									d.getLength())));
				} if (d.getDate() == null) {
					// add the author quick-fix
					collector.accept(new LocalWaypointParseProblem(d
							.getCharStart(), d.getCharEnd() - d.getCharStart(),
							document, "Missing date",
							WaypointParseProblem.SEVERITY_INFO,
							new AttributeProposal("Add the date",
									new String[] { "-date" },
									new String[] { dateString }, d.getCharStart(),
									d.getLength())));
				}
			} catch (MalformedWaypointException e) {
				int offset = e.getOffset() + waypointRegion.getOffset();
				int length = e.getLength();
				LinkedList<IReplacementProposal> proposals = new LinkedList<IReplacementProposal>();
				;
				try {
					String text = document.get(offset, length);
					ReplacementProposal authorProposal = new ReplacementProposal(
							"Replace with author", offset, length, "-author=\""
									+ author + "\"");
					// add the date quick-fix
					ReplacementProposal dateProposal = new ReplacementProposal(
							"Replace with date", offset, length, "-date=\""
									+ dateString + "\"");
					switch (e.getType()) {
					case ExpectedRValue:
					case ExpectedEquals:
						if (text.length() > 1 && "-author=".startsWith(text)) {
							// add the author quick-fix
							proposals.add(authorProposal);
						} else if (text.length() > 1
								&& "-date=".startsWith(text)) {
							proposals.add(dateProposal);
						} else {
							proposals.add(authorProposal);
							proposals.add(dateProposal);
						}
					}
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (proposals.size() != 0) {
					collector.accept(new LocalWaypointParseProblem(offset,
							length, document, e.getMessage(),
							WaypointParseProblem.SEVERITY_ERROR, proposals));
				} else {
					collector.accept(new WaypointParseError(e.getMessage(), e
							.getOffset()
							+ waypointRegion.getOffset(), e.getLength(),
							document));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block and a really, really, really,
				// really, really, really, really, long message.
				e.printStackTrace();
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return descriptors;
	}

	/**
	 * @param document
	 * @param region
	 * @return
	 */
	private List<IRegion> calculateWaypointRegions(IDocument document,
			IRegion region) {
		List<IRegion> waypointRegions = new ArrayList<IRegion>();
		fWaypointScanner.setRange(document, region.getOffset(), region
				.getLength());

		while (true) {
			IToken token = fWaypointScanner.nextToken();
			if (token.getData() == TAG_KEY) {
				Region r = new Region(fWaypointScanner.getTokenOffset(),
						fWaypointScanner.getTokenLength());
				waypointRegions.add(r);
			} else if (token.equals(Token.EOF)) {
				break;
			}
		}

		return waypointRegions;
	}

	@Override
	public final String getParsedWaypointKind() {
		return waypointKind;
	}

	/**
	 * Sets the method for discovering what the domain-specific object is for
	 * this parser.
	 * 
	 * @param method
	 */
	public void setDomainMethod(IDomainMethod method) {
		this.domainMethod = method;
	}

	private String getDomainObject(IDocument document, IRegion region) {
		if (domainMethod != null) {
			return domainMethod.getDomainObject(document, region);
		}
		return null;
	}
}
