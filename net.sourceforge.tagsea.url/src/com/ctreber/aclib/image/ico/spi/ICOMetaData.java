// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:35 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ICOMetaData.java

package com.ctreber.aclib.image.ico.spi;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

import com.ctreber.aclib.image.ico.BitmapDescriptor;

public class ICOMetaData extends IIOMetadata
{

    public ICOMetaData(BitmapDescriptor bitmapdescriptor)
    {
        _entry = bitmapdescriptor;
    }

    public Node getAsTree(String s)
    {
        IIOMetadataNode iiometadatanode = new IIOMetadataNode("javax_imageio_ico_image_1.0");
        IIOMetadataNode iiometadatanode1 = new IIOMetadataNode("width");
        iiometadatanode1.setNodeValue(""+_entry.getWidth());
        iiometadatanode.appendChild(iiometadatanode1);
        iiometadatanode1 = new IIOMetadataNode("height");
        iiometadatanode1.setNodeValue(""+_entry.getHeight());
        iiometadatanode.appendChild(iiometadatanode1);
        return iiometadatanode;
    }

    public boolean isReadOnly()
    {
        return true;
    }

    public void mergeTree(String s, Node node)
    {
    }

    public void reset()
    {
    }

    private final BitmapDescriptor _entry;
}