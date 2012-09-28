// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:35 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ICOImageReaderSPI.java

package com.ctreber.aclib.image.ico.spi;

import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;

// Referenced classes of package com.ctreber.aclib.image.ico.spi:
//            ICOReader

public class ICOImageReaderSPI extends ImageReaderSpi
{

    public ICOImageReaderSPI()
    {
        super("Christian Treber, www.ctreber.com, ct@ctreber.com", "1.0 December 2003", new String[] {
            "ico", "ICO"
        }, new String[] {
            "ico", "ICO"
        }, new String[] {
            "image/x-ico"
        }, "com.ctreber.aclib.image.ico.ICOReader", STANDARD_INPUT_TYPE, null, false, null, null, null, null, false, "com.ctreber.aclib.image.ico.ICOMetadata_1.0", "com.ctreber.aclib.image.ico.ICOMetadata", null, null);
    }

    public boolean canDecodeInput(Object obj)
    {
        return true;
    }

    public ImageReader createReaderInstance(Object obj)
    {
        return new ICOReader(this);
    }

    public String getDescription(Locale locale)
    {
        return "Microsoft Icon Format (ICO)";
    }
}