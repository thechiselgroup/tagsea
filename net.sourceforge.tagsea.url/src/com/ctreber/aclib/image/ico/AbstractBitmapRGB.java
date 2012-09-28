// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:30 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AbstractBitmapRGB.java

package com.ctreber.aclib.image.ico;


// Referenced classes of package com.ctreber.aclib.image.ico:
//            AbstractBitmap, BitmapDescriptor

public abstract class AbstractBitmapRGB extends AbstractBitmap
{

    public AbstractBitmapRGB(BitmapDescriptor bitmapdescriptor)
    {
        super(bitmapdescriptor);
        _samples = new int[getWidth() * getHeight()];
    }

    protected int _samples[];
}