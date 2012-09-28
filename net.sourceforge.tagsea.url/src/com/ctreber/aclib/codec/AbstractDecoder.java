// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:28 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AbstractDecoder.java

package com.ctreber.aclib.codec;

import java.io.IOException;

public abstract class AbstractDecoder
{

    public AbstractDecoder()
    {
        _endianness = 0;
    }

    public short readUInt1()
        throws IOException
    {
        return (short)(int)readValue(1);
    }

    public int readUInt2()
        throws IOException
    {
        return (int)readValue(2);
    }

    public long readUInt4()
        throws IOException
    {
        return readValue(4);
    }

    public void setEndianess(int i)
    {
        _endianness = i;
    }

    public long getPos()
    {
        return _pos;
    }

    public abstract void seek(long l)
        throws IOException;

    public abstract byte[] readBytes(long l, byte abyte0[])
        throws IOException;

    protected long readValue(int i)
        throws IOException
    {
        readBytes(i, _readBuf);
        if(i == 1)
            return (long)(_readBuf[0] & 0xff);
        long l = 0L;
        if(_endianness == 0)
        {
            for(int j = 0; j < i; j++)
            {
                l <<= 8;
                l += _readBuf[j] & 0xff;
            }

        } else
        {
            for(int k = i - 1; k >= 0; k--)
            {
                l <<= 8;
                l += _readBuf[k] & 0xff;
            }

        }
        return l;
    }

    public abstract void close()
        throws IOException;

    public static final int BIG_ENDIAN = 0;
    public static final int LITTLE_ENDIAN = 1;
    private int _endianness;
    protected long _pos;
    private final byte _readBuf[] = new byte[4];
}