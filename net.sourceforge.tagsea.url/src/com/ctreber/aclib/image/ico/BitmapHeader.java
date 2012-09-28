// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:31 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BitmapHeader.java

package com.ctreber.aclib.image.ico;

import java.io.IOException;

import com.ctreber.aclib.codec.AbstractDecoder;

// Referenced classes of package com.ctreber.aclib.image.ico:
//            TypeCompression

public class BitmapHeader
{

    public BitmapHeader(AbstractDecoder abstractdecoder)
        throws IOException
    {
//        LOG.debug("Reading header at " + abstractdecoder.getPos());
        _headerSize = abstractdecoder.readUInt4();
        _width = abstractdecoder.readUInt4();
        _height = abstractdecoder.readUInt4();
        _planes = abstractdecoder.readUInt2();
        _bpp = abstractdecoder.readUInt2();
        _compression = TypeCompression.getType(abstractdecoder.readUInt4());
        _imageSize = abstractdecoder.readUInt4();
        _xPixelsPerM = abstractdecoder.readUInt4();
        _yPixelsPerM = abstractdecoder.readUInt4();
        _colorsUsed = abstractdecoder.readUInt4();
        _colorsImportant = abstractdecoder.readUInt4();
//        LOG.debug(this);
    }

    public String toString()
    {
        return "size: " + _headerSize + ", width: " + _width + ", height: " + _height + ", planes: " + _planes + ", BPP: " + _bpp + ", compression: " + _compression + ", imageSize: " + _imageSize + ", XPixelsPerM: " + _xPixelsPerM + ", YPixelsPerM: " + _yPixelsPerM + ", colorsUsed: " + _colorsUsed + ", colorsImportant: " + _colorsImportant + (_colorsImportant != 0L ? "" : " (all)");
    }

    public int getBPP()
    {
        return _bpp;
    }

    public long getColorsImportant()
    {
        return _colorsImportant;
    }

    public long getColorsUsed()
    {
        return _colorsUsed;
    }

    public TypeCompression getCompression()
    {
        return _compression;
    }

    public long getHeight()
    {
        return _height;
    }

    public long getBitmapSize()
    {
        return _imageSize;
    }

    public int getPlanes()
    {
        return _planes;
    }

    public long getHeaderSize()
    {
        return _headerSize;
    }

    public long getWidth()
    {
        return _width;
    }

    public long getXPixelsPerM()
    {
        return _xPixelsPerM;
    }

    public long getYPixelsPerM()
    {
        return _yPixelsPerM;
    }

    public int getColorCount()
    {
        return 1 << _bpp;
    }

//    private static final Logger LOG;
    private final long _headerSize;
    private final long _width;
    private final long _height;
    private final int _planes;
    private final int _bpp;
    private final TypeCompression _compression;
    private final long _imageSize;
    private final long _xPixelsPerM;
    private final long _yPixelsPerM;
    private final long _colorsUsed;
    private final long _colorsImportant;

//    static 
//    {
//        LOG = Logger.getLogger(com.ctreber.aclib.image.ico.BitmapHeader.class);
//    }
}