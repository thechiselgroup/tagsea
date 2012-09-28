/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tours.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import com.ibm.research.tours.IPaletteEntry;
import com.ibm.research.tours.ToursPlugin;

/**
 * @author mdesmond
 */
public class PaletteEntryTransfer extends ByteArrayTransfer 
{
    /**
     * Singleton instance.
     */
    private static final PaletteEntryTransfer instance = new PaletteEntryTransfer();
    private static final String TYPE_NAME = "palette-entry-transfer-format" + System.currentTimeMillis() + ":" + instance.hashCode();//$NON-NLS-2$//$NON-NLS-1$
    private static final int TYPEID = registerType(TYPE_NAME);

    /**
     * Creates a new transfer object.
     */
    private PaletteEntryTransfer() 
    {
    }

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static PaletteEntryTransfer getInstance() {
        return instance;
    }

    /* (non-Javadoc)
     * Method declared on Transfer.
     */
    protected int[] getTypeIds() {
        return new int[] { TYPEID };
    }

    /* (non-Javadoc)
     * Returns the type names.
     *
     * @return the list of type names
     */
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    /*
     * Method declared on Transfer.
     */
    protected void javaToNative(Object object, TransferData transferData) {
       byte[] bytes = toByteArray((IPaletteEntry[])object);
       if (bytes != null)
          super.javaToNative(bytes, transferData);
    }
    /*
     * Method declared on Transfer.
     */
    protected Object nativeToJava(TransferData transferData) {
       byte[] bytes = (byte[])super.nativeToJava(transferData);
       return fromByteArray(bytes);
    }
    
    public byte[] toByteArray(IPaletteEntry[] elements)
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);

        byte[] bytes = null;
        
        try {
            /* write number of elements */
            out.writeInt(elements.length);

            for (int i = 0; i < elements.length; i++) 
            {
                writeElement((IPaletteEntry) elements[i], out);
            }
            out.close();
            bytes = byteOut.toByteArray();
        } catch (IOException e) {
            //when in doubt send nothing
        }

        return bytes;
    }

    public IPaletteEntry[] fromByteArray(byte[] bytes) 
    {
    	DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        try 
        {
            int n = in.readInt();

            IPaletteEntry[] elements = new IPaletteEntry[n];
            
            for (int i = 0; i < n; i++)
            {
            	String id = readElement(in);
            	IPaletteEntry element = ToursPlugin.getDefault().getPaletteModel().getPaletteEntry(id);
                
                if(element != null)
                	elements[i] = element;
            }
            
            return elements;
            
        } catch (IOException e) {
            return null;
        }
    }

    private String readElement(DataInputStream dataIn) throws IOException 
    {
        String id = dataIn.readUTF();
        return id;
    }

    private void writeElement(IPaletteEntry element, DataOutputStream dataOut)
            throws IOException 
    {
        dataOut.writeUTF(element.getId());
    }
}
