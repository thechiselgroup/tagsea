// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:31 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BitmapIndexed1BPP.java

package com.ctreber.aclib.image.ico;

import java.io.IOException;

import com.ctreber.aclib.codec.AbstractDecoder;

// Referenced classes of package com.ctreber.aclib.image.ico:
//            AbstractBitmapIndexed, BitmapDescriptor

public class BitmapIndexed1BPP extends AbstractBitmapIndexed
{

    public BitmapIndexed1BPP(BitmapDescriptor bitmapdescriptor)
    {
        super(bitmapdescriptor);
    }

    void readBitmap(AbstractDecoder abstractdecoder)
        throws IOException
    {
        int i = getBytesPerScanLine(getWidth(), 1);
        for(int j = 0; j < getHeight(); j++)
        {
            byte abyte0[] = abstractdecoder.readBytes(i, null);
            int k = 0;
            int l = 128;
            int i1 = (getHeight() - 1 - j) * getWidth();
            for(int j1 = 0; j1 < getWidth(); j1++)
            {
                _pixels[i1++] = (abyte0[k] & l) / l & 0xff;
                if(l == '\001')
                {
                    l = 128;
                    k++;
                } else
                {
                    l >>= 1;
                }
            }

        }

    }
}