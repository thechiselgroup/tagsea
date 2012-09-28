// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:29 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AbstractBitmapIndexed.java

package com.ctreber.aclib.image.ico;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;

import com.ctreber.aclib.codec.AbstractDecoder;

// Referenced classes of package com.ctreber.aclib.image.ico:
//            AbstractBitmap, BitmapMask, BitmapDescriptor

public abstract class AbstractBitmapIndexed extends AbstractBitmap
{

    public AbstractBitmapIndexed(BitmapDescriptor bitmapdescriptor)
    {
        super(bitmapdescriptor);
        _pixels = new int[getWidth() * getHeight()];
    }

    void read(AbstractDecoder abstractdecoder)
        throws IOException
    {
        readColorPalette(abstractdecoder);
        readBitmap(abstractdecoder);
        readMask(abstractdecoder);
    }

    private void readColorPalette(AbstractDecoder abstractdecoder)
        throws IOException
    {
        int i = getVerifiedColorCount();
        _colorPalette = new Color[i];
        for(int j = 0; j < i; j++)
            setColor(j, readColor(abstractdecoder));

    }

    private Color readColor(AbstractDecoder abstractdecoder)
        throws IOException
    {
        short word0 = abstractdecoder.readUInt1();
        short word1 = abstractdecoder.readUInt1();
        short word2 = abstractdecoder.readUInt1();
        abstractdecoder.readUInt1();
        return new Color(word2, word1, word0);
    }

    abstract void readBitmap(AbstractDecoder abstractdecoder)
        throws IOException;

    private void readMask(AbstractDecoder abstractdecoder)
        throws IOException
    {
        _transparencyMask = new BitmapMask(_descriptor);
        _transparencyMask.read(abstractdecoder);
    }

    private int getVerifiedColorCount()
    {
        int i = getColorCount();
        int j = 1 << _descriptor.getBPP();
        if(i < j)
        {
            //LOG.warn("Number of specified colors is smaller than color count calculated from bits per pixel (will trust the latter): " + i + "/" + j);
            i = j;
        }
        return i;
    }

    protected static int getBytesPerScanLine(int i, int j)
    {
        double d = (double)j / 8D;
        int k = (int)Math.ceil((double)i * d);
        if((k & 3) != 0)
            k = (k & -4) + 4;
        return k;
    }

    public BufferedImage createImageIndexed()
    {
        IndexColorModel indexcolormodel = createColorModel();
        BufferedImage bufferedimage = new BufferedImage(getWidth(), getHeight(), 13, indexcolormodel);
        bufferedimage.getRaster().setSamples(0, 0, getWidth(), getHeight(), 0, _pixels);
        return bufferedimage;
    }

    private IndexColorModel createColorModel()
    {
        int i = getVerifiedColorCount();
        byte abyte0[] = new byte[i];
        byte abyte1[] = new byte[i];
        byte abyte2[] = new byte[i];
        byte abyte3[] = new byte[i];
        for(int j = 0; j < i; j++)
        {
            Color color = getColor(j);
            abyte0[j] = (byte)color.getRed();
            abyte1[j] = (byte)color.getGreen();
            abyte2[j] = (byte)color.getBlue();
            abyte3[j] = -1;
        }

        IndexColorModel indexcolormodel = new IndexColorModel(8, i, abyte0, abyte1, abyte2, abyte3);
        return indexcolormodel;
    }

    public BufferedImage createImageRGB()
    {
        BufferedImage bufferedimage = new BufferedImage(getWidth(), getHeight(), 2);
        for(int i = 0; i < getHeight(); i++)
        {
            for(int j = 0; j < getWidth(); j++)
            {
                int k = getColor(j, i).getRGB();
                if(_transparencyMask.isOpaque(j, i))
                    k |= 0xff000000;
                else
                    k &= 0xffffff;
                bufferedimage.setRGB(j, i, k);
            }

        }

        return bufferedimage;
    }

    public Color getColor(int i, int j)
    {
        return getColor(getPaletteIndex(i, j));
    }

    public int getPaletteIndex(int i, int j)
    {
        return _pixels[j * getWidth() + i];
    }

    public Color getColor(int i)
    {
        if(i >= getVerifiedColorCount())
            throw new IllegalArgumentException("Color index out of range: is " + i + ", max. " + getVerifiedColorCount());
        else
            return _colorPalette[i];
    }

    private void setColor(int i, Color color)
    {
        _colorPalette[i] = color;
    }

//    private static final Logger LOG;
    private static final int OPAQUE = 255;
    private Color _colorPalette[];
    protected int _pixels[];
    private BitmapMask _transparencyMask;

//    static 
//    {
//        LOG = Logger.getLogger(com.ctreber.aclib.image.ico.AbstractBitmapIndexed.class);
//    }
}