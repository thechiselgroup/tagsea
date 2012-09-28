// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:29 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AbstractBitmap.java

package com.ctreber.aclib.image.ico;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.ctreber.aclib.codec.AbstractDecoder;

// Referenced classes of package com.ctreber.aclib.image.ico:
//            BitmapDescriptor, BitmapHeader

public abstract class AbstractBitmap
{

    public AbstractBitmap(BitmapDescriptor bitmapdescriptor)
    {
        _descriptor = bitmapdescriptor;
    }

    public abstract BufferedImage createImageRGB();

    abstract void read(AbstractDecoder abstractdecoder)
        throws IOException;

    protected int getHeight()
    {
        if(_descriptor.getWidth() == _descriptor.getHeight() / 2)
            return _descriptor.getWidth();
        else
            return _descriptor.getHeight();
    }

    protected int getWidth()
    {
        return _descriptor.getWidth();
    }

    protected int getColorCount()
    {
        return _descriptor.getHeader().getColorCount();
    }

    public BitmapDescriptor getDescriptor()
    {
        return _descriptor;
    }

    void setDescriptor(BitmapDescriptor bitmapdescriptor)
    {
        _descriptor = bitmapdescriptor;
    }

    public String toString()
    {
        return getClass().toString();
    }

    protected BitmapDescriptor _descriptor;
}