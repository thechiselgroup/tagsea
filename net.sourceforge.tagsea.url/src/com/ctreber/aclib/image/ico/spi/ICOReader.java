// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/26/2007 6:53:36 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ICOReader.java

package com.ctreber.aclib.image.ico.spi;

import java.awt.Container;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.ctreber.aclib.image.ImageInputStreamDecoder;
import com.ctreber.aclib.image.ico.BitmapDescriptor;
import com.ctreber.aclib.image.ico.ICOFile;

// Referenced classes of package com.ctreber.aclib.image.ico.spi:
//            ICOMetaData, ICOImageReaderSPI

public class ICOReader extends ImageReader
{

    public ICOReader(ImageReaderSpi imagereaderspi)
    {
        super(imagereaderspi);
    }

    public int getHeight(int i)
    {
        return getICOEntry(i).getHeight();
    }

    public IIOMetadata getImageMetadata(int i)
    {
        return new ICOMetaData(getICOEntry(i));
    }

    public Iterator getImageTypes(int i)
    {
        ArrayList arraylist = new ArrayList();
        for(int j = 0; j < getNumImages(false); j++)
        {
            ImageTypeSpecifier imagetypespecifier = ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1000), ONE, 0, false, false);
            arraylist.add(imagetypespecifier);
        }

        return arraylist.iterator();
    }

    public int getNumImages(boolean flag)
    {
        return getICOFile().getImageCount();
    }

    public IIOMetadata getStreamMetadata()
    {
        return null;
    }

    public int getWidth(int i)
    {
        return getICOEntry(i).getWidth();
    }

    public BufferedImage read(int i, ImageReadParam imagereadparam)
    {
        return getICOEntry(i).getBitmap().createImageRGB();
    }

    public void setInput(Object obj, boolean flag, boolean flag1)
    {
        if(!(obj instanceof ImageInputStream))
        {
            throw new IllegalArgumentException("Only ImageInputStream supported as input source");
        } else
        {
            _stream = (ImageInputStream)obj;
            return;
        }
    }

    private ICOFile getICOFile()
    {
        if(_icoFile == null)
            try
            {
                _icoFile = new ICOFile("[ImageInputStream]", new ImageInputStreamDecoder(_stream));
            }
            catch(IOException ioexception)
            {
                System.err.println("Can't create ICOFile: " + ioexception.getMessage());
            }
        return _icoFile;
    }

    private BitmapDescriptor getICOEntry(int i)
    {
        return (BitmapDescriptor)getICOFile().getDescriptors().get(i);
    }

    public static void main(String args[])
        throws IOException
    {
        if(args.length == 0)
        {
            System.err.println("Please specify the icon file name");
            System.exit(1);
        }
        IIORegistry.getDefaultInstance().registerServiceProvider(new ICOImageReaderSPI());
        listServiceProviders();
        File file = getICOFile(args);
        ImageReader imagereader = getICOReader();
        imagereader.setInput(ImageIO.createImageInputStream(file));
        String s = file.getName();
        JFrame jframe = createWindow(s);
        int i = imagereader.getNumImages(true);
        for(int j = 0; j < i; j++)
            addImage(jframe.getContentPane(), imagereader, j);

        jframe.pack();
        jframe.setVisible(true);
    }

    private static File getICOFile(String as[])
    {
        String s = as[0];
        File file = new File(s);
        if(!file.isFile())
        {
            System.err.println(s + " not found, or is no file");
            System.exit(1);
        }
        return file;
    }

    private static ImageReader getICOReader()
    {
        Iterator iterator = ImageIO.getImageReadersByFormatName("ico");
        if(iterator == null || !iterator.hasNext())
        {
            System.err.println("No reader for format 'ICO' found");
            System.exit(1);
        }
        return (ImageReader)iterator.next();
    }

    private static JFrame createWindow(String s)
    {
        JFrame jframe = new JFrame(s);
        jframe.setDefaultCloseOperation(2);
        jframe.addWindowListener(new WindowAdapter() {

            public void windowClosed(WindowEvent windowevent)
            {
                System.exit(0);
            }

        });
        BoxLayout boxlayout = new BoxLayout(jframe.getContentPane(), 1);
        jframe.getContentPane().setLayout(boxlayout);
        return jframe;
    }

    private static void addImage(Container container, ImageReader imagereader, int i)
        throws IOException
    {
        JButton jbutton = new JButton();
        jbutton.setIcon(new ImageIcon(imagereader.read(i)));
        jbutton.setText(imagereader.getWidth(i) + "x" + imagereader.getHeight(i));
        container.add(jbutton);
    }

    public static void listServiceProviders()
    {
        System.out.println("Registered image formats and their providers");
        String as[] = ImageIO.getReaderFormatNames();
        for(int i = 0; i < as.length; i++)
        {
            String s = as[i];
            ImageReaderSpi imagereaderspi;
            for(Iterator iterator = ImageIO.getImageReadersBySuffix(s); iterator.hasNext(); System.out.println(" o " + s + " (" + imagereaderspi.getDescription(Locale.getDefault()) + ") by " + imagereaderspi.getVendorName()))
            {
                ImageReader imagereader = (ImageReader)iterator.next();
                imagereaderspi = imagereader.getOriginatingProvider();
            }

        }

    }

    private static final int ONE[] = new int[1];
    protected ICOFile _icoFile;
    protected ImageInputStream _stream;

}