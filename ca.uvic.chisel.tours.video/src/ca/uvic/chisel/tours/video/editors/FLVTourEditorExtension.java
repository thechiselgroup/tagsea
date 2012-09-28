package ca.uvic.chisel.tours.video.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;

import ca.uvic.chisel.videoplayer.test.IVideoPlayerPart;
import ca.uvic.chisel.videoplayer.test.views.VideoPlayerView;

import com.ibm.research.tours.content.url.delegates.IResourceTourEditorExtension;

public class FLVTourEditorExtension implements IResourceTourEditorExtension {
	
	long startTime = 0;
	long endTime = -1;

	public FLVTourEditorExtension() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void editorOpened(IEditorPart part, IFile file) {
		if (!(part instanceof IVideoPlayerPart) || !file.getFileExtension().equalsIgnoreCase("flv")) {
			return;
		}
		IVideoPlayerPart editor = (IVideoPlayerPart) part;
		editor.getVideoPlayer().play();
	}
	
	public void finish(IEditorPart part, IFile file) {
		if (!(part instanceof IVideoPlayerPart) || !file.getFileExtension().equalsIgnoreCase("flv")) {
			return;
		}
		IVideoPlayerPart editor = (IVideoPlayerPart) part;
		editor.getVideoPlayer().stop();
	}


	@Override
	public void load(IMemento memento) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void save(IMemento memento) {
		// TODO Auto-generated method stub
		
	}

}
