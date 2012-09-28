/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.model.internal.Waypoint;
import net.sourceforge.tagsea.model.internal.WaypointsModel;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Transfer for waypoints within the Eclipse workbench. Waypoints can only be transfered in one
 * workbench instance, not accross multiple workbenches.
 * @author mdesmond
 */
public class WaypointTransfer extends ByteArrayTransfer 
{
    /**
     * 
     * Singleton instance.
     */
    private static final WaypointTransfer instance = new WaypointTransfer();
    //make the type id unique so that waypoints can only be transfered within one Eclipse instance.
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

    /*
     * Method declared on Transfer.
     */
    protected void javaToNative(Object object, TransferData transferData) {
       byte[] bytes = toByteArray((IWaypoint[])object);
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
    
    public byte[] toByteArray(IWaypoint[] waypoints)
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);

        byte[] bytes = null;
        
        try {
            /* write number of markers */
            out.writeInt(waypoints.length);

            /* write waypoints */
            for (int i = 0; i < waypoints.length; i++) 
            {
                writeWaypoint((IWaypoint) waypoints[i], out);
            }
            out.close();
            bytes = byteOut.toByteArray();
        } catch (IOException e) {
            //when in doubt send nothing
        }

        return bytes;
    }

    public IWaypoint[] fromByteArray(byte[] bytes) 
    {
    	DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        try 
        {
            /* read number of waypoints */
            int n = in.readInt();

            /* read waypoints */
            IWaypoint[] waypoints = new IWaypoint[n];
            
            for (int i = 0; i < n; i++)
            {
            	String waypointId = readWaypoint(in);
            	int colon = waypointId.lastIndexOf(':');
            	IWaypoint waypoint = null;
            	try {
            		String type = waypointId.substring(0, colon);
            		String idString = waypointId.substring(colon+1);
            		waypoint = ((WaypointsModel)TagSEAPlugin.getWaypointsModel()).getWaypointById(type, Long.parseLong(idString));
            	} catch (Exception e) {
            		//do nothing, just skip to the next one.
            	}
                if(waypoint != null)
                	waypoints[i] = waypoint;
            }
            
            return waypoints;
            
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
        String id = dataIn.readUTF();
        return id;
    }

    /**
     * Writes the given marker to the given stream.
     *
     * @param marker the marker
     * @param dataOut the output stream
     * @exception IOException if there is a problem writing to the stream
     */
    private void writeWaypoint(IWaypoint waypoint, DataOutputStream dataOut)
            throws IOException 
    {
    	dataOut.writeUTF(waypoint.getType() + ":" + ((Waypoint)waypoint).getWorkbenchId());
        dataOut.flush();
    }
}
