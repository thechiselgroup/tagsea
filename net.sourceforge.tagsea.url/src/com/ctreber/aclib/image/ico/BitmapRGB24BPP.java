// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:33 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BitmapRGB24BPP.java

package com.ctreber.aclib.image.ico;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.ctreber.aclib.codec.AbstractDecoder;

// Referenced classes of package com.ctreber.aclib.image.ico:
//            AbstractBitmapRGB, BitmapDescriptor

public class BitmapRGB24BPP extends AbstractBitmapRGB
{

    public BitmapRGB24BPP(BitmapDescriptor bitmapdescriptor)
    {
        super(bitmapdescriptor);
    }

    void read(AbstractDecoder abstractdecoder)
        throws IOException
    {
        for(int i = 0; i < getHeight(); i++)
        {
            byte abyte0[] = abstractdecoder.readBytes(getWidth() * 3, null);
            int j = 0;
            int k = (getHeight() - i - 1) * getWidth();
            for(int l = 0; l < getWidth(); l++)
                _samples[k++] = abyte0[j++] + (abyte0[j++] << 8) + (abyte0[j++] << 16);

        }

    }

    public BufferedImage createImageRGB()
    {
        BufferedImage bufferedimage = new BufferedImage(getWidth(), getHeight(), 1);
        bufferedimage.setRGB(0, 0, getWidth(), getHeight(), _samples, 0, getWidth());
        return bufferedimage;
    }
}