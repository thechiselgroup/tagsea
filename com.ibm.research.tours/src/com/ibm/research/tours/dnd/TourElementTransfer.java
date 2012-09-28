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
import java.util.Vector;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ToursPlugin;

/**
 * @author mdesmond
 */
public class TourElementTransfer extends ByteArrayTransfer 
{
    /**
     * Singleton instance.
     */
    private static final TourElementTransfer instance = new TourElementTransfer();
    private static final String TYPE_NAME = "tour-element-transfer-format" + System.currentTimeMillis() + ":" + instance.hashCode();//$NON-NLS-2$//$NON-NLS-1$
    private static final int TYPEID = registerType(TYPE_NAME);

    /**
     * Creates a new transfer object.
     */
    private TourElementTransfer() 
    {
    }

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static TourElementTransfer getInstance() {
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
       byte[] bytes = toByteArray((ITourElement[])object);
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
    
    public byte[] toByteArray(ITourElement[] elements)
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);

        byte[] bytes = null;
        
        try 
        {
            /* write number of elements */
            out.writeInt(elements.length);

            for (int i = 0; i < elements.length; i++) 
            {
            	// Put element to the clipboard
            	String id = ToursPlugin.getDefault().getClipBoard().putTourElement(elements[i]);
                // Write the id
            	write(id, out);
            }
            out.close();
            bytes = byteOut.toByteArray();
        } 
        catch (IOException e) 
        {
            //when in doubt send nothing
        }

        return bytes;
    }

    public ITourElement[] fromByteArray(byte[] bytes) 
    {
    	DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        try 
        {
            Vector<ITourElement> elements = new Vector<ITourElement>();
            int n = in.readInt();
            
            for (int i = 0; i < n; i++)
            {
            	// Read the id
            	String id = read(in);
            	
                ITourElement element = ToursPlugin.getDefault().getClipBoard().getTourElement(id);
                
                if(element != null)
                	elements.add(element);
            }
            
            // Clean up the clip board after us
            ToursPlugin.getDefault().getClipBoard().clear();
            
            return elements.toArray(new ITourElement[0]);
            
        }
        catch (IOException e) 
        {
            return null;
        }
    }

    private String read(DataInputStream dataIn) throws IOException 
    {
        String id = dataIn.readUTF();
        return id;
    }

    private void write(String id, DataOutputStream dataOut)
            throws IOException 
    {
        dataOut.writeUTF(id);
    }
}
