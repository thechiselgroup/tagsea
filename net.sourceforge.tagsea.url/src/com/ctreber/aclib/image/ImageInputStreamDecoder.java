// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:28 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ImageInputStreamDecoder.java

package com.ctreber.aclib.image;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

import com.ctreber.aclib.codec.AbstractDecoder;

public class ImageInputStreamDecoder extends AbstractDecoder
{

    public ImageInputStreamDecoder(ImageInputStream imageinputstream)
    {
        _stream = imageinputstream;
    }

    public void seek(long l)
        throws IOException
    {
        _stream.seek(l);
    }

    public byte[] readBytes(long l, byte abyte0[])
        throws IOException
    {
        byte abyte1[] = abyte0;
        if(abyte1 == null)
            abyte1 = new byte[(int)l];
        else
        if((long)abyte1.length < l)
            throw new IllegalArgumentException("Insufficient space in buffer");
        int i = _stream.read(abyte0, 0, (int)l);
        if((long)i != l)
        {
            throw new IOException("Tried to read " + l + " bytes, but obtained " + i);
        } else
        {
            _pos += l;
            return abyte1;
        }
    }

    public void close()
        throws IOException
    {
        _stream.close();
    }

    private final ImageInputStream _stream;
}