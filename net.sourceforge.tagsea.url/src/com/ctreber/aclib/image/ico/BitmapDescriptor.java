// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:30 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BitmapDescriptor.java

package com.ctreber.aclib.image.ico;

import java.awt.Image;
import java.io.IOException;

import com.ctreber.aclib.codec.AbstractDecoder;

// Referenced classes of package com.ctreber.aclib.image.ico:
//            AbstractBitmapIndexed, BitmapHeader, AbstractBitmap, BitmapMask

public class BitmapDescriptor
{

    public BitmapDescriptor(AbstractDecoder abstractdecoder)
        throws IOException
    {
//        LOG.debug("Reading descriptor at " + abstractdecoder.getPos());
        _width = abstractdecoder.readUInt1();
        _height = abstractdecoder.readUInt1();
        _colorCount = abstractdecoder.readUInt1();
        _reserved = abstractdecoder.readUInt1();
        _planes = abstractdecoder.readUInt2();
        _bpp = abstractdecoder.readUInt2();
        _size = abstractdecoder.readUInt4();
        _offset = abstractdecoder.readUInt4();
//        LOG.debug(this);
    }

    public String toString()
    {
        return "width: " + _width + ", height: " + _height + ", colorCount: " + _colorCount + " (" + getColorCount() + ")" + ", planes: " + _planes + ", BPP: " + _bpp + ", size: " + _size + ", offset: " + _offset;
    }

    public Image getImageIndexed()
    {
        if(!(_bitmap instanceof AbstractBitmapIndexed))
            return null;
        else
            return ((AbstractBitmapIndexed)_bitmap).createImageIndexed();
    }

    public int getBPP()
    {
        if(_bpp != 0)
            return _bpp;
        else
            return _header.getBPP();
    }

    public int getBPPRaw()
    {
        return _bpp;
    }

    public Image getImageRGB()
    {
        return _bitmap.createImageRGB();
    }

    public int getColorCountRaw()
    {
        return _colorCount;
    }

    public int getColorCount()
    {
        return _colorCount != 0 ? _colorCount : 256;
    }

    public int getHeight()
    {
        return _height;
    }

    public long getOffset()
    {
        return _offset;
    }

    public int getPlanes()
    {
        return _planes;
    }

    public int getReserved()
    {
        return _reserved;
    }

    public long getSize()
    {
        return _size;
    }

    public int getWidth()
    {
        return _width;
    }

    public BitmapHeader getHeader()
    {
        return _header;
    }

    void setHeader(BitmapHeader bitmapheader)
    {
        _header = bitmapheader;
    }

    public BitmapMask getMask()
    {
        return _mask;
    }

    public AbstractBitmap getBitmap()
    {
        return _bitmap;
    }

    void setBitmap(AbstractBitmap abstractbitmap)
    {
        _bitmap = abstractbitmap;
    }

//    private static final Logger LOG;
    private final int _width;
    private final int _height;
    private final int _colorCount;
    private final int _reserved;
    private final int _planes;
    private final int _bpp;
    private final long _size;
    private final long _offset;
    private BitmapHeader _header;
    private AbstractBitmap _bitmap;
    private BitmapMask _mask;

//    static 
//    {
//        LOG = Logger.getLogger(com.ctreber.aclib.image.ico.BitmapDescriptor.class);
//    }
}