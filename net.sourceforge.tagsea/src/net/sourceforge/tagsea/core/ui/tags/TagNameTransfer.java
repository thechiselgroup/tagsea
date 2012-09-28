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
package net.sourceforge.tagsea.core.ui.tags;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.sourceforge.tagsea.TagSEAPlugin;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Transfer for tag names.
 * @author Del Myers
 */

public class TagNameTransfer extends ByteArrayTransfer {
	private static final TagNameTransfer _instance = new TagNameTransfer();
	private static final String ID = "tagsea-tagname-transfer" + System.currentTimeMillis() + _instance.hashCode();
	private static final int TYPEID = registerType(ID);
	
	
	public static TagNameTransfer getInstance() {
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
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		String[] result = new String[0];
		try {
			ObjectInputStream is = new ObjectInputStream(byteStream);
			result = (String[]) is.readObject();
		} catch (IOException e) {
			TagSEAPlugin.getDefault().log(e);
		} catch (ClassNotFoundException e) {
			TagSEAPlugin.getDefault().log(e);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.ByteArrayTransfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		if (object instanceof String[]) {
			super.javaToNative(toByteArray((String[])object), transferData);
			return;
		}
		super.javaToNative(object, transferData);
	}

	/**
	 * Simply translates the tag names to a byte array.
	 * @param tag
	 * @return
	 */
	private Object toByteArray(String[] tags) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream os = new ObjectOutputStream(byteStream);
			os.writeObject(tags);
			os.flush();
			byte[] bytes = byteStream.toByteArray();
			os.close();
			return bytes;
		} catch (IOException e) {
			TagSEAPlugin.getDefault().log(e);
		}
		return new byte[0];
	}

}
