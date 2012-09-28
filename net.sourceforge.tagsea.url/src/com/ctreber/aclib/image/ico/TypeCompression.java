// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:34 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TypeCompression.java

package com.ctreber.aclib.image.ico;

import java.util.HashMap;
import java.util.Map;

public final class TypeCompression
{

    private TypeCompression(String s, int i, String s1)
    {
        _name = s;
        _value = i;
        _comment = s1;
    }

    private static void register(TypeCompression typecompression)
    {
        TYPES.put(new Long(typecompression.getValue()), typecompression);
    }

    public String toString()
    {
        return _name + " (" + _comment + ")";
    }

    public String getName()
    {
        return _name;
    }

    public int getValue()
    {
        return _value;
    }

    public static TypeCompression getType(long l)
    {
        TypeCompression typecompression = (TypeCompression)TYPES.get(new Long(l));
        if(typecompression == null)
            throw new IllegalArgumentException("Compression type " + l + " unknown");
        else
            return typecompression;
    }

    private static final Map TYPES = new HashMap();
    public static final TypeCompression BI_RGB;
    public static final TypeCompression BI_RLE8;
    public static final TypeCompression BI_RLE4;
    public static final TypeCompression BI_BITFIELDS;
    private final int _value;
    private final String _name;
    private final String _comment;

    static 
    {
        BI_RGB = new TypeCompression("BI_RGB", 0, "Uncompressed (any BPP)");
        BI_RLE8 = new TypeCompression("BI_RLE8", 1, "8 Bit RLE Compression (8 BPP only)");
        BI_RLE4 = new TypeCompression("BI_RLE4", 2, "4 Bit RLE Compression (4 BPP only)");
        BI_BITFIELDS = new TypeCompression("BI_BITFIELDS", 3, "Uncompressed (16 & 32 BPP only)");
        register(BI_RGB);
        register(BI_RLE8);
        register(BI_RLE4);
        register(BI_BITFIELDS);
    }
}