package net.sourceforge.tagsea.url.favicon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.ctreber.aclib.image.ico.ICOFile;

/**
 * Singleton class which is capable of downloading a favicon for a given
 * url object. 
 * 
 * @author Mike
 */
public class FaviconDownloader 
{
	private static FaviconDownloader fInstance;
	
	private FaviconDownloader(){}

	public static FaviconDownloader getInstance()
	{
		if(fInstance == null)
			fInstance = new FaviconDownloader();

		return fInstance;
	}

	/**
	 * Downloads a favicon for a given url object. This
	 * is a blocking method.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public BufferedImage downloadFavicon(URL url) throws IOException
	{
		if(url == null)
			return null;
		
		String host = url.getHost();
		String protocol = url.getProtocol();
		
		// Bluid the favicon url
		String favicon = protocol + "://" + host + "/favicon.ico";
		URL faviconUrl = new URL(favicon);
		
		ICOFile file = null;
		
		try 
		{
			file = new ICOFile(faviconUrl);
		} 
		catch (RuntimeException e) 
		{
			e.printStackTrace();
		}

		if(file != null)
		{
			List<BufferedImage> images = file.getImages();

			if(images != null && !images.isEmpty())
			{
				// We only support one static image
				BufferedImage bufferedImage = (BufferedImage)file.getImages().get(0);
				return bufferedImage;
			}
		}
		
		
		return null;
	}
}
