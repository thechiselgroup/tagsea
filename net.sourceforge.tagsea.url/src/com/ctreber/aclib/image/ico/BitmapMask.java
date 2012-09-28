// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:32 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BitmapMask.java

package com.ctreber.aclib.image.ico;

import java.io.IOException;

import com.ctreber.aclib.codec.AbstractDecoder;

// Referenced classes of package com.ctreber.aclib.image.ico:
//            BitmapIndexed1BPP, BitmapDescriptor

public class BitmapMask
{

    public BitmapMask(BitmapDescriptor bitmapdescriptor)
    {
        _mask = new BitmapIndexed1BPP(bitmapdescriptor);
    }

    void read(AbstractDecoder abstractdecoder)
        throws IOException
    {
        _mask.readBitmap(abstractdecoder);
    }

    public int getPaletteIndex(int i, int j)
    {
        return _mask.getPaletteIndex(i, j);
    }

    void setDescriptor(BitmapDescriptor bitmapdescriptor)
    {
        _mask.setDescriptor(bitmapdescriptor);
    }

    public boolean isOpaque(int i, int j)
    {
        return _mask.getPaletteIndex(i, j) == 0;
    }

    private final BitmapIndexed1BPP _mask;
}