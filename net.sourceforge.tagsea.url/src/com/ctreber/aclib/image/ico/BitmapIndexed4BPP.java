// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:31 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BitmapIndexed4BPP.java

package com.ctreber.aclib.image.ico;

import java.io.IOException;

import com.ctreber.aclib.codec.AbstractDecoder;

// Referenced classes of package com.ctreber.aclib.image.ico:
//            AbstractBitmapIndexed, BitmapDescriptor

public class BitmapIndexed4BPP extends AbstractBitmapIndexed
{

    public BitmapIndexed4BPP(BitmapDescriptor bitmapdescriptor)
    {
        super(bitmapdescriptor);
    }

    void readBitmap(AbstractDecoder abstractdecoder)
        throws IOException
    {
        int i = getBytesPerScanLine(getWidth(), 4);
        for(int j = 0; j < getHeight(); j++)
        {
            byte abyte0[] = abstractdecoder.readBytes(i, null);
            int k = 0;
            boolean flag = true;
            int l = (getHeight() - j - 1) * getWidth();
            for(int i1 = 0; i1 < getWidth(); i1++)
            {
                int j1;
                if(flag)
                {
                    j1 = (abyte0[k] & 0xf0) >> 4;
                } else
                {
                    j1 = abyte0[k] & 0xf;
                    k++;
                }
                _pixels[l++] = j1 & 0xff;
                flag = !flag;
            }

        }

    }
}