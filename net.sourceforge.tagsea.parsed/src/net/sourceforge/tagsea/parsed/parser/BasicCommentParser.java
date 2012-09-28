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
package net.sourceforge.tagsea.parsed.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.IFileBufferListener;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * A parser that can be used for source code or text files that have natural single and multi-line
 * comments.
 * @author Del Myers
 *
 */
public abstract class BasicCommentParser implements IWaypointParser, IReconcilingWaypointParser {
	public static final int MULTILINE = 2;
	public static final int SINGLELINE = 1;
	public static final int UNKNOWN = 0;
	//the scanner used to discover comments.
	private RuleBasedPartitionScanner commentScanner;
	private final String LINE_CONTENT = "__wpnt_single_line"; 
	private final IToken LINE_TOKEN  = new Token(LINE_CONTENT);
	private final String MULTI_LINE_CONTENT = "__wpnt_multi_line";
	private final IToken MULTILINE_TOKEN = new Token(MULTI_LINE_CONTENT);
	private static final String EXCLUSION_CONTENT = "__wpnt_exclusion";
	private static final IToken EXCLUSION_TOKEN = new Token(EXCLUSION_CONTENT);
	private final String PARTITION_SUFFIX = ".partition";
	private String partitioning;
	private HashMap<IDocument, IDocumentPartitioner> partionerMap;
	private String[] singleLineStarts;
	private String[] multiLineStarts;
	private String[] multiLineEnds;
	
	/**
	 * Constructs a new comment parser using the given strings to discover where comment regions are.
	 * Multi-line comments are indicated using start/end string pairs. multiLineStart and multiLineEnd
	 * represent those pairs where multiLineStart[x] is the start indicator for the multi-line comment
	 * terminated by multiLineEnd[x]. Hence multiLineStart and multiLineEnd must have equal length.
	 * The lists may not have length 0, cannot be null, nor may they have any null elements or empty
	 * strings.
	 * @param singleLine strings indicating the start of a single-line comment.
	 * @param multiLineStart strings indicating the start of a multi-line comment.
	 * @param multiLineEnd strings indicating the end of a multi-line comment.
	 * @param exclusionStart strings that indicate the beginnings of regions that should be ignored, such as strings.
	 * @param exclusionEnd strings taht indication the ends of regions that should be ignored, such as strings. Must be the same length as exclusionStart.
	 */
	public BasicCommentParser(String[] singleLine, String[] multiLineStart, String[] multiLineEnd, String[] exclusionStart, String[] exclusionEnd) {
		if (multiLineStart.length != multiLineEnd.length) {
			throw new IllegalArgumentException("Unmatched start/end pair");
		} else if (multiLineStart.length == 0 && singleLine.length == 0) {
			throw new IllegalArgumentException("No comment markers defined");	 
		}
		checkArray(singleLine);
		checkArray(multiLineStart);
		checkArray(multiLineEnd);
		if (exclusionStart != null || exclusionEnd != null) {
			//can both be null, but not just one can be null
			if (exclusionStart == null || exclusionEnd == null) {
				throw new IllegalArgumentException("Null reference");
			}
			if (exclusionStart.length != exclusionEnd.length) {
				throw new IllegalArgumentException("Unmatched start/end pair");
			}
			checkArray(exclusionStart);
			checkArray(exclusionEnd);
		}
		LinkedList<IPredicateRule> rules = new LinkedList<IPredicateRule>();
		for (String s : singleLine) {
			rules.add(new EndOfLineRule(s, LINE_TOKEN));
		}
		for (int i = 0; i < multiLineStart.length; i++) {
			rules.add(new MultiLineRule(multiLineStart[i], multiLineEnd[i], MULTILINE_TOKEN, (char)0, true));
		}
		//add the rules for exclusion
		if (exclusionStart != null && exclusionEnd != null) {
			for (int i = 0; i < exclusionStart.length; i++) {
				rules.add(new MultiLineRule(exclusionStart[i], exclusionEnd[i], EXCLUSION_TOKEN, (char)0, true));
			}
		}
		this.singleLineStarts = singleLine;
		this.multiLineStarts = multiLineStart;
		this.multiLineEnds = multiLineEnd;
		
		IPredicateRule[] rulesArray = rules.toArray(new IPredicateRule[rules.size()]);
		this.commentScanner = new RuleBasedPartitionScanner();
		this.commentScanner.setPredicateRules(rulesArray);
		this.partionerMap = new HashMap<IDocument, IDocumentPartitioner>();
		FileBuffers.getTextFileBufferManager().addFileBufferListener(new IFileBufferListener(){
			public void bufferContentAboutToBeReplaced(IFileBuffer buffer) {}
			public void bufferContentReplaced(IFileBuffer buffer) {}
			public void bufferCreated(IFileBuffer buffer) {
				connectBuffer((ITextFileBuffer)buffer);
			}
			public void bufferDisposed(IFileBuffer buffer) {
				if (Display.getCurrent() == null) {
					final ITextFileBuffer fBuffer = (ITextFileBuffer) buffer;
					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable(){public void run(){disconnectBuffer(fBuffer);}});
				} else {
					disconnectBuffer((ITextFileBuffer)buffer);
				}
				
			}
			public void dirtyStateChanged(IFileBuffer buffer, boolean isDirty) {}
			public void stateChangeFailed(IFileBuffer buffer) {}
			public void stateChanging(IFileBuffer buffer) {}
			public void stateValidationChanged(IFileBuffer buffer,
					boolean isStateValidated) {}
			public void underlyingFileDeleted(IFileBuffer buffer) {}
			public void underlyingFileMoved(IFileBuffer buffer, IPath path) {}
			
		});
	}
	
	protected boolean isManagedFile(IFile file, IContentType content){
		String kind = getParsedWaypointKind();
		IParsedWaypointDefinition def = 
			ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getDefinition(kind);
		IParsedWaypointDefinition[] defs = 
			ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getMatchingDefinitions(file);
		for (IParsedWaypointDefinition check : defs) {
			if (check == def) return true;
		}
		return false;
	}
	
	private void connectBuffer(ITextFileBuffer buffer) {
		IContentType type = null;
		try {
			type = buffer.getContentType();
		} catch (CoreException e) {
		}
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(buffer.getLocation());
		if (resource instanceof IFile) {
			if (!isManagedFile((IFile)resource, type)) {
				return;
			}
		}
		IDocumentPartitioner partitioner = 
			new FastPartitioner(((IPartitionTokenScanner)commentScanner), new String[] {LINE_CONTENT, MULTI_LINE_CONTENT});
		partionerMap.put(buffer.getDocument(), partitioner);
		if (Display.getCurrent() == null) {
			final ITextFileBuffer fBuffer = buffer;
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable(){public void run(){prepareDocument(fBuffer.getDocument());}});
		} else {
			prepareDocument(buffer.getDocument());
		}
	}
	
	private void disconnectBuffer(ITextFileBuffer buffer) {
		IDocument document = buffer.getDocument();
		IDocumentPartitioner partitioner = partionerMap.get(buffer.getDocument());
		
		if (partitioner != null) {
			if (document instanceof IDocumentExtension3) {
				IDocumentExtension3 d = (IDocumentExtension3) document;
				IDocumentPartitioner partitioner2 = d.getDocumentPartitioner(getPartitioning());
				if (partitioner == partitioner2) {
					partitioner.disconnect();
					d.setDocumentPartitioner(getPartitioning(), null);
				}
			}
		}
	}
	
	/**
	 * @param singleLine
	 */
	private void checkArray(String[] array) {
		if (array == null) {
			throw new IllegalArgumentException("Null argument");
		} else {
			for (String s : array) {
				if (s == null) {
					throw new IllegalArgumentException("Null element");
				} else if ("".equals(s)) {
					throw new IllegalArgumentException("Empty element");
				}
			}
		}
	}


	/**
	 * Parses the document for waypoint descriptors. The regions are expected to be either:
	 * <ol>
	 * <li>Regions representing valid comments in the document</li>
	 * <li>A one-element array with a region that spans the entire document</li>
	 * </ol>
	 * Clients should rarely need to call this method as it will be called automatically within
	 * the framework. If clients need to use it, however, it is recommended that they parse the
	 * entire document rather than trying to calculate the individual comment regions first as
	 * the latter is more error prone.
	 */
	public final IParsedWaypointDescriptor[] parse(final IDocument document, IRegion[] regions, IWaypointParseProblemCollector collector) {
		//see if we need to check the entire document.
		if (regions.length == 1) {
			if (regions[0].getOffset() == 0 && regions[0].getLength() == document.getLength()) {
				regions = gatherCommentRegions(document, 0, document.getLength());
			}
		}
		List<IParsedWaypointDescriptor> descriptors = new LinkedList<IParsedWaypointDescriptor>();
		for (IRegion region : regions) {
			//we can parse for comments because we know that according to the contract of
			//IReconcilingWaypointParser, the regions will always be either the entire document,
			//or the regions defined by calculatedirtyRegions() (unless a third-party uses the
			//parser, which he/she does at his/her own risk).
			descriptors.addAll(doParseInComment(document, region, collector));
		}
		return descriptors.toArray(new IParsedWaypointDescriptor[descriptors.size()]);
	}
	
	/**
	 * Parses the comment within the given region to look for a waypoint. It can be assumed that
	 * the text within the given region represents a valid comment block.
	 * @param document the document to parse.
	 * @param region the comment region.
	 * @return a list of ParsedWaypointDescriptors that represent waypoints or parse errors.
	 */
	protected abstract List<IParsedWaypointDescriptor> doParseInComment(IDocument document, IRegion region, IWaypointParseProblemCollector collector);
	
	/**
	 * Returns the unique id for the kind of waypoint that this parser is used to discover.
	 * @return the unique id for the kind of waypoint that this parser is used to discover.
	 */
	public abstract String getParsedWaypointKind();
	
	/**
	 * Prepares the document for parsing or reconciling.
	 * @param document
	 */
	private void prepareDocument(IDocument document) {
		//attempts to use a document partitioner to quickly and always keep the 
		//comment regions up-to-date.
		if (document instanceof IDocumentExtension3) {
			IDocumentPartitioner documentPartitioner = partionerMap.get(document);
			if (documentPartitioner == null) {
				ITextFileBuffer buffer = 
					ParsedWaypointPlugin.getDefault().getPlatformDocumentRegistry().getBufferForDocument(document);
				if (buffer != null) {
					connectBuffer(buffer);
					documentPartitioner = partionerMap.get(document);
					if (documentPartitioner == null)
						return;
				}
			}
			IDocumentExtension3 d = (IDocumentExtension3) document;
			IDocumentPartitioner partitioner = d.getDocumentPartitioner(getPartitioning());
			if (partitioner == null) {
				d.setDocumentPartitioner(getPartitioning(), documentPartitioner);
				documentPartitioner.connect(document);
			}
		}
	}
	
	public IRegion[] calculateDirtyRegions(final DocumentEvent event) {
		final IDocument doc = event.getDocument();
		final IRegion runnableResult[][] = new IRegion[1][];
		Runnable displayRunnable = new Runnable(){
			public void run() {
				//get the regions that intersect with the dirty portion of the event.
				if (doc instanceof IDocumentExtension3) {
					if (((IDocumentExtension3)doc).getDocumentPartitioner(partitioning) == null) {
						runnableResult[0] = gatherCommentRegions(event.getDocument(), 0, event.getDocument().getLength());
					}
					int textLength = (event.getText() != null) ? event.getText().length() : 0;
					Region eventRegion = new Region(event.getOffset(), textLength);
					ITypedRegion[] regions;
					try {

						regions = TextUtilities.computePartitioning(
								doc, 
								partitioning, 
								0, 
								event.getDocument().getLength(), 
								true);
						LinkedList<IRegion> result = new LinkedList<IRegion>();
						if (regions.length == 0) {
							runnableResult[0] = new IRegion[] {new Region(0, doc.getLength())};
							return;
						}
						for (IRegion region : regions) {
							if (!IDocument.DEFAULT_CONTENT_TYPE.equals(((TypedRegion)region).getType()) && 
									TextUtilities.overlaps(region, eventRegion)) {
								result.add(region);
							}
						}
						runnableResult[0] = result.toArray(new IRegion[result.size()]);
						return;
					} catch (BadLocationException e) {
						ParsedWaypointPlugin.getDefault().log(e);
					} catch (RuntimeException e) {
						ITextFileBuffer buffer = 
							ParsedWaypointPlugin.getDefault().getPlatformDocumentRegistry().getBufferForDocument(doc);
						if (buffer != null) {
							disconnectBuffer(buffer);
						}
					}
				}
				runnableResult[0] = gatherCommentRegions(event.getDocument(), 0, event.getDocument().getLength());
			}
		};
		if (Display.getCurrent() != null) {
			displayRunnable.run();
		} else {
			PlatformUI.getWorkbench().getDisplay().syncExec(displayRunnable);
		}
		if (runnableResult[0] == null) {
			
		}
		return runnableResult[0];
	}
	
	/**
	 * Used in cases where partitioning can't be done on the document. Scans the entire document for
	 * comment areas.
	 * @return
	 */
	public IRegion[] gatherCommentRegions(IDocument document, int offset, int length) {
		List<IRegion> commentRegions = new ArrayList<IRegion>();
		commentScanner.setRange(document,offset, length);

		while (true) 
		{
			IToken token = commentScanner.nextToken();
			if (token.isEOF()) {
				break;
			} else if (token.getData() == LINE_CONTENT || token.getData() == MULTI_LINE_CONTENT) {
				commentRegions.add(new Region(commentScanner.getTokenOffset(), commentScanner.getTokenLength()));
			}

		}

		IRegion[] result= new IRegion[commentRegions.size()];
		commentRegions.toArray(result);
		return result;
	}


	private String getPartitioning() {
		if (this.partitioning == null) {
			partitioning = getParsedWaypointKind() + PARTITION_SUFFIX;
		}
		return partitioning;
	}
	
	/**
	 * Returns the strings that indicate the beginning of a single-line comment. This is a handle-only method.
	 * @return the strings that indicate the beginning of a single-line comment.
	 */
	public String[] getSingleLineIndicators() {
		String[] result = new String[singleLineStarts.length];
		System.arraycopy(singleLineStarts, 0, result, 0, result.length);
		return result;
	}
	
	/**
	 * Returns the strings that indicate the beginning of a multi-line comment. This is a handle-only method.
	 * @return the strings that indicate the beginning of a multi-line comment.
	 */
	public String[] getMultilineStartIndicators() {
		String[] result = new String[multiLineStarts.length];
		System.arraycopy(multiLineStarts, 0, result, 0, result.length);
		return result;
	}
	
	/**
	 * Returns the strings that indicate the end of a multi-line comment. This is a handle-only method.
	 * @return the strings that indicate the end of a multi-line comment.
	 */
	public String[] getMultilineEndIndicators() {
		String[] result = new String[multiLineEnds.length];
		System.arraycopy(multiLineEnds, 0, result, 0, result.length);
		return result;
	}
	
	/**
	 * Returns the comment type for the given waypoint. One of MULTILINE, SINGLELINE, or UNKNOWN.
	 * @param wp the waypoint to check.
	 * @return the comment type for the given waypoint. One of MULTILINE, SINGLELINE, or UNKNOWN.
	 */
	public int getCommentTypeFor(final IWaypoint wp) {
		final int[] runnableResult = new int[1];
		Runnable displayRunnable = new Runnable() {
			public void run() {
				IRegion wpRegion = new Region(ParsedWaypointUtils.getCharStart(wp), ParsedWaypointUtils.getLength(wp));
				IDocument doc = ParsedWaypointUtils.getDocument(wp);
				if (doc instanceof IDocumentExtension3 && partionerMap.keySet().contains(doc)) {
					if (((IDocumentExtension3)doc).getDocumentPartitioner(partitioning) == null) {
						runnableResult[0] = UNKNOWN;
						return;
					}
					ITypedRegion[] regions;
					try {
						regions = TextUtilities.computePartitioning(
								doc, 
								partitioning, 
								0, 
								doc.getLength(), 
								true);
						LinkedList<IRegion> result = new LinkedList<IRegion>();
						for (ITypedRegion region : regions) {
							if (TextUtilities.overlaps(region, wpRegion)) {
								if (region.getType().equals(MULTI_LINE_CONTENT)) {
									runnableResult[0]=MULTILINE;
									return;
								} else if (region.getType().equals(LINE_CONTENT)) {
									runnableResult[0]=SINGLELINE;
									return;
								}
							}
							if (!IDocument.DEFAULT_CONTENT_TYPE.equals(((TypedRegion)region).getType()) && 
									TextUtilities.overlaps(region, wpRegion)) {
								result.add(region);
							}
						}
					} catch (BadLocationException e) {
					}
				}
				runnableResult[0] = UNKNOWN;
			}
		};
		if (Display.getCurrent() != null) {
			displayRunnable.run();
		} else {
			PlatformUI.getWorkbench().getDisplay().syncExec(displayRunnable);
		}
		
		return runnableResult[0];
	}
}
