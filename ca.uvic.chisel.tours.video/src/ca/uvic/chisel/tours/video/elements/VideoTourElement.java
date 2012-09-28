package ca.uvic.chisel.tours.video.elements;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import ca.uvic.chisel.video.widget.AbstractVideoPlayer;
import ca.uvic.chisel.videoplayer.test.IVideoPlayerPart;
import ca.uvic.chisel.videoplayer.test.views.VideoPlayerView;

import com.ibm.research.tours.AbstractTourElement;
import com.ibm.research.tours.ITimeLimit;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourElementListener;

public class VideoTourElement extends AbstractTourElement {
	private String videoName = "Video";
	private String videoPath = null;
	
	private static final String VIDEO_ELEMENT = "VIDEO_ELEMENT";
	private static final String VIDEO_NAME = "name";
	private static final String VIDEO_FILE = "file";
	
	public VideoTourElement() {
		
	}

	@Override
	public ITourElement createClone() {
		VideoTourElement element = new VideoTourElement();
		element.videoName = videoName;
		element.videoPath = videoPath;
		return element;
		
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getShortText() {
		return videoName;
	}

	@Override
	public String getText() {
		return videoName;
	}
	
	public void setVideoName(String text) {
		this.videoName = text;
		fireElementChangedEvent();
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transition() {
		if (videoPath == null) {
			return;
		}
		try {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VideoPlayerView.ID);
			if (view instanceof IVideoPlayerPart) {
				AbstractVideoPlayer player = ((IVideoPlayerPart)view).getVideoPlayer();
				player.load(videoPath);
				player.play();
			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void save(IMemento memento) {
		super.save(memento);
		IMemento videoMemento = memento.getChild(VIDEO_ELEMENT);
		if (videoMemento == null) {
			videoMemento = memento.createChild(VIDEO_ELEMENT);
		}
		videoMemento.putString(VIDEO_NAME, videoName);
		if (videoPath != null) {
			videoMemento.putString(VIDEO_FILE, videoPath);
		}
	}
	
	@Override
	public void load(IMemento memento) {
		super.load(memento);
		IMemento videoMemento = memento.getChild(VIDEO_ELEMENT);
		if (videoMemento == null) {
			videoName = "Video";
			videoPath = null;
		} else {
			videoPath = videoMemento.getString(VIDEO_FILE);
			videoName = videoMemento.getString(VIDEO_NAME);
		}
	}

	public void setVideoLocation(String s) {
		this.videoPath = s;
		fireElementChangedEvent();
	}
	
	public String getVideoLocation() {
		return videoPath;
	}

	

}
