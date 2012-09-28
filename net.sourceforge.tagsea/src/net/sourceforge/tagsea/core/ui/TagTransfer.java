/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Transfers tags.
 * @author Del Myers
 */

public class TagTransfer extends ByteArrayTransfer {

	private static final TagTransfer _instance = new TagTransfer();
	private static final String ID = "tagsea-tag-transfer" + System.currentTimeMillis() + _instance.hashCode();
	private static final int TYPEID = registerType(ID);
	
	
	public static TagTransfer getInstance() {
		return _instance;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	@Override
	protected int[] getTypeIds() {
		return new int[] {TYPEID};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	@Override
	protected String[] getTypeNames() {
		return new String[] {ID};
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.ByteArrayTransfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[])super.nativeToJava(transferData);
		return fromByteArray(bytes);
	}
	
	/**
	 * @param bytes
	 * @return
	 */
	private Object fromByteArray(byte[] bytes) {
		String s = new String(bytes);
		StringTokenizer tokenizer = new StringTokenizer(s, "\0");
		List<ITag> tags = new LinkedList<ITag>();
		while (tokenizer.hasMoreTokens()) {
			ITag tag = TagSEAPlugin.getTagsModel().getTag(tokenizer.nextToken());
			if (tag != null) {
				tags.add(tag);
			}
		}
		return tags.toArray(new ITag[tags.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.ByteArrayTransfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		if (object instanceof ITag[]) {
			super.javaToNative(toByteArray((ITag[])object), transferData);
			return;
		}
		super.javaToNative(object, transferData);
	}

	/**
	 * Simply translates the tag names to a byte array.
	 * @param tag
	 * @return
	 */
	private Object toByteArray(ITag[] tags) {
		StringWriter writer = new StringWriter();
		for (ITag tag : tags) {
			writer.append(tag.getName());
			writer.append('\0');
		}
		return writer.toString().getBytes();
	}

}
