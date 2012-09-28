// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:32 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BitmapIndexed8BPP.java

package com.ctreber.aclib.image.ico;

import java.io.IOException;

import com.ctreber.aclib.codec.AbstractDecoder;

// Referenced classes of package com.ctreber.aclib.image.ico:
//            AbstractBitmapIndexed, BitmapDescriptor

public class BitmapIndexed8BPP extends AbstractBitmapIndexed
{

    public BitmapIndexed8BPP(BitmapDescriptor bitmapdescriptor)
    {
        super(bitmapdescriptor);
    }

    void readBitmap(AbstractDecoder abstractdecoder)
        throws IOException
    {
        int i = getBytesPerScanLine(getWidth(), 8);
        for(int j = 0; j < getHeight(); j++)
        {
            byte abyte0[] = abstractdecoder.readBytes(i, null);
            int k = 0;
            int l = (getHeight() - j - 1) * getWidth();
            for(int i1 = 0; i1 < getWidth(); i1++)
                _pixels[l++] = abyte0[k++] & 0xff;

        }

    }
}