// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:28 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   StreamDecoder.java

package com.ctreber.aclib.codec;

import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.ctreber.aclib.codec:
//            AbstractDecoder

public class StreamDecoder extends AbstractDecoder
{

    public StreamDecoder(InputStream inputstream)
    {
        _stream = inputstream;
    }

    public void seek(long l)
        throws IOException
    {
        long l1 = l - getPos();
        if(l1 >= 0L)
        {
            long l2 = _stream.skip(l1);
            if(l2 != l1)
                throw new IOException("Tried to skip " + l1 + ", but skipped " + l2);
            _pos += l1;
        } else
        {
            throw new IllegalArgumentException("Can't seek a position already passed (skip " + l1 + ")");
        }
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
        int i = _stream.read(abyte1, 0, (int)l);
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

    private final InputStream _stream;
}