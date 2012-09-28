/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation
 *******************************************************************************/

package ca.uvic.cs.tagsea.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import ca.uvic.cs.tagsea.core.Waypoint;

/**
 * @author mdesmond
 */
public class WaypointTransfer extends ByteArrayTransfer {

    /**
     * Singleton instance.
     */
    private static final WaypointTransfer instance = new WaypointTransfer();

    // Create a unique ID to make sure that different Eclipse
    // applications use different "types" of <code>WaypointTransfer</code>
    private static final String TYPE_NAME = "waypoint-transfer-format" + System.currentTimeMillis() + ":" + instance.hashCode();//$NON-NLS-2$//$NON-NLS-1$

    private static final int TYPEID = registerType(TYPE_NAME);

    /**
     * Creates a new transfer object.
     */
    private WaypointTransfer() 
    {
    }

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static WaypointTransfer getInstance() {
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

    /* (non-Javadoc)
     * Method declared on Transfer.
     * On a successful conversion, the transferData.result field will be set to
     * OLE.S_OK. If this transfer agent is unable to perform the conversion, the
     * transferData.result field will be set to the failure value of OLE.DV_E_TYMED.
     */
    protected void javaToNative(Object object, TransferData transferData) 
    {
        Object[] waypoints = (Object[]) object;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);

        byte[] bytes = null;

        try {
            /* write number of markers */
            out.writeInt(waypoints.length);

            /* write waypoints */
            for (int i = 0; i < waypoints.length; i++) 
            {
                writeWaypoint((Waypoint) waypoints[i], out);
            }
            out.close();
            bytes = byteOut.toByteArray();
        } catch (IOException e) {
            //when in doubt send nothing
        }

        if (bytes != null) {
            super.javaToNative(bytes, transferData);
        }
    }

    /* (non-Javadoc)
     * Method declared on Transfer.
     */
    protected Object nativeToJava(TransferData transferData) 
    {
        byte[] bytes = (byte[]) super.nativeToJava(transferData);
        DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(bytes));

        try {
            /* read number of waypoints */
            int n = in.readInt();

            /* read waypoints */
            String[] waypointIds = new String[n];
            
            for (int i = 0; i < n; i++)
            {
            	String waypointId = readWaypoint(in);
                
                if (waypointId == null) 
                {
                    return null;
                }
                waypointIds[i] = waypointId;
            }
            
            return waypointIds;
            
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Reads and returns a single marker from the given stream.
     *
     * @param dataIn the input stream
     * @return the marker
     * @exception IOException if there is a problem reading from the stream
     */
    private String readWaypoint(DataInputStream dataIn) throws IOException 
    {
        String keyword = dataIn.readUTF();
        int lineNumber = dataIn.readInt();
        return keyword + lineNumber;
    }

    /**
     * Writes the given marker to the given stream.
     *
     * @param marker the marker
     * @param dataOut the output stream
     * @exception IOException if there is a problem writing to the stream
     */
    private void writeWaypoint(Waypoint waypoint, DataOutputStream dataOut)
            throws IOException 
    {
        dataOut.writeUTF(waypoint.getKeyword());
        dataOut.writeInt(waypoint.getLineNumber());
    }
}
