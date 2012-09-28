// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:34 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ICOFile.java

package com.ctreber.aclib.image.ico;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ctreber.aclib.codec.AbstractDecoder;
import com.ctreber.aclib.codec.StreamDecoder;

// Referenced classes of package com.ctreber.aclib.image.ico:
//            BitmapDescriptor, BitmapHeader, BitmapIndexed1BPP, BitmapIndexed4BPP, 
//            BitmapIndexed8BPP, BitmapRGB24BPP, BitmapRGB32BPP, AbstractBitmap

public class ICOFile
    implements Comparable
{

    public ICOFile(String s)
        throws IOException
    {
        this(s, ((AbstractDecoder) (new StreamDecoder(new FileInputStream(s)))));
    }

    public ICOFile(InputStream inputstream)
        throws IOException
    {
        this("[from stream]", ((AbstractDecoder) (new StreamDecoder(inputstream))));
    }

    public ICOFile(URL url)
        throws IOException
    {
        this(url.toString(), ((AbstractDecoder) (new StreamDecoder(url.openStream()))));
    }

    public ICOFile(byte abyte0[])
        throws IOException
    {
        this("[from buffer]", ((AbstractDecoder) (new StreamDecoder(new ByteArrayInputStream(abyte0)))));
    }

    public ICOFile(String s, AbstractDecoder abstractdecoder)
        throws IOException
    {
        _descriptors = new ArrayList();
        _fileName = s;
        read(abstractdecoder);
    }

    public int compareTo(Object obj)
    {
        if(!(obj instanceof ICOFile))
            throw new IllegalArgumentException("Can't compare to " + obj.getClass());
        else
            return ((ICOFile)obj).getFileName().compareTo(getFileName());
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer(100);
        stringbuffer.append(_fileName + ", type: " + _type + ", image count: " + _imageCount);
        return stringbuffer.toString();
    }

    private void read(AbstractDecoder abstractdecoder)
        throws IOException
    {
//        LOG.info("Reading " + _fileName);
        abstractdecoder.setEndianess(1);
        readHeader(abstractdecoder);
        BitmapDescriptor abitmapdescriptor[] = readDescriptors(abstractdecoder);
        fillDescriptors(abstractdecoder, abitmapdescriptor);
        abstractdecoder.close();
//        LOG.info("Done reading icon file");
    }

    private void readHeader(AbstractDecoder abstractdecoder)
        throws IOException
    {
        _reserved = abstractdecoder.readUInt2();
        _type = abstractdecoder.readUInt2();
        _imageCount = abstractdecoder.readUInt2();
//        LOG.info("Image count in header: " + _imageCount);
        if(_type != 1)
        {
//            LOG.error("Unknown ICO type " + _type);
            throw new IllegalArgumentException("Unknown ICO type " + _type);
        }
        if(_imageCount == 0)
        {
            _imageCount = 1;
//            LOG.warn("Corrected image count from 0 to 1");
        }
    }

    private void fillDescriptors(AbstractDecoder abstractdecoder, BitmapDescriptor abitmapdescriptor[])
        throws IOException
    {
        for(int i = 0; i < abitmapdescriptor.length; i++)
        {
            BitmapDescriptor bitmapdescriptor = abitmapdescriptor[i];
            fillDescriptor(abstractdecoder, bitmapdescriptor);
            _descriptors.add(bitmapdescriptor);
        }

    }

    private void fillDescriptor(AbstractDecoder abstractdecoder, BitmapDescriptor bitmapdescriptor)
        throws IOException
    {
        if(abstractdecoder.getPos() != bitmapdescriptor.getOffset())
        {
//            LOG.info("FYI: Skipping some fluff, " + (bitmapdescriptor.getOffset() - abstractdecoder.getPos()));
            abstractdecoder.seek(bitmapdescriptor.getOffset());
        }
        bitmapdescriptor.setHeader(new BitmapHeader(abstractdecoder));
        bitmapdescriptor.setBitmap(readBitmap(abstractdecoder, bitmapdescriptor));
        doSomeChecks(bitmapdescriptor);
    }

    private BitmapDescriptor[] readDescriptors(AbstractDecoder abstractdecoder)
        throws IOException
    {
        BitmapDescriptor abitmapdescriptor[] = new BitmapDescriptor[_imageCount];
        for(int i = 0; i < _imageCount; i++)
            abitmapdescriptor[i] = readDescriptor(abstractdecoder);

        return abitmapdescriptor;
    }

    private BitmapDescriptor readDescriptor(AbstractDecoder abstractdecoder)
        throws IOException
    {
        return new BitmapDescriptor(abstractdecoder);
    }

    private void doSomeChecks(BitmapDescriptor bitmapdescriptor)
    {
        if(bitmapdescriptor.getHeader().getWidth() * 2L != bitmapdescriptor.getHeader().getHeight())
            System.out.println(this + ": In header, height is not twice the width");
    }

    private AbstractBitmap readBitmap(AbstractDecoder abstractdecoder, BitmapDescriptor bitmapdescriptor)
        throws IOException
    {
        int i = bitmapdescriptor.getHeader().getBPP();
        Object obj = null;
        switch(i)
        {
        case 1: // '\001'
            obj = new BitmapIndexed1BPP(bitmapdescriptor);
            break;

        case 4: // '\004'
            obj = new BitmapIndexed4BPP(bitmapdescriptor);
            break;

        case 8: // '\b'
            obj = new BitmapIndexed8BPP(bitmapdescriptor);
            break;

        case 16: // '\020'
//            LOG.error("16 BPP icons not supported - write to ct@ctreber.com if you need this");
            return null;

        case 24: // '\030'
            obj = new BitmapRGB24BPP(bitmapdescriptor);
            break;

        case 32: // ' '
            obj = new BitmapRGB32BPP(bitmapdescriptor);
            break;

        default:
            throw new IllegalArgumentException("Unsupported bit count " + i);
        }
        ((AbstractBitmap) (obj)).read(abstractdecoder);
        return ((AbstractBitmap) (obj));
    }

    public List getImages()
    {
        ArrayList arraylist = new ArrayList();
        BitmapDescriptor bitmapdescriptor;
        for(Iterator iterator = getDescriptors().iterator(); iterator.hasNext(); arraylist.add(bitmapdescriptor.getBitmap().createImageRGB()))
            bitmapdescriptor = (BitmapDescriptor)iterator.next();

        return arraylist;
    }

    public List getDescriptors()
    {
        return _descriptors;
    }

    public BitmapDescriptor getDescriptor(int i)
    {
        return (BitmapDescriptor)_descriptors.get(i);
    }

    public int getType()
    {
        return _type;
    }

    public int getImageCount()
    {
        return _imageCount;
    }

    public String getFileName()
    {
        return _fileName;
    }

    public int getReserved()
    {
        return _reserved;
    }

//    private static final Logger LOG;
    private String _fileName;
    private int _reserved;
    private int _type;
    private int _imageCount;
    private final List _descriptors;

//    static 
//    {
//        LOG = Logger.getLogger(com.ctreber.aclib.image.ico.ICOFile.class);
//    }
}