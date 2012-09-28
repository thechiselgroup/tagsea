/**
 * 
 */
package ca.uvic.chisel.videoplayer.test.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import ca.uvic.chisel.video.widget.AbstractVideoPlayer;
import ca.uvic.chisel.video.widget.AnnotatedProgress;
import ca.uvic.chisel.video.widget.ProgressAnnotation;
import ca.uvic.chisel.video.widget.VideoEvent;
import ca.uvic.chisel.video.widget.VideoListener;
import ca.uvic.chisel.video.widget.internal.BrowserVideoPlayer;
import ca.uvic.chisel.videoplayer.test.IVideoPlayerPart;
import ca.uvic.chisel.videoplayer.test.TestPlayer;

/**
 * @author Del Myers
 *
 */
public class VideoPlayerEditor extends EditorPart implements IVideoPlayerPart {

	private IFile file;
	private BrowserVideoPlayer videoPlayer;
	private Button play;
	private AnnotatedProgress playerBar;
	private ProgressAnnotation loadedProgress;
	private SeekRunnable seeker;
	
	private class SeekRunnable implements Runnable {
		long time = 0;
		long seek = 0;
		@Override
		public synchronized void run() {
			if (videoPlayer != null && !videoPlayer.isDisposed() && time != 0 && time < System.currentTimeMillis()) {
				videoPlayer.seek(seek);
			}
		}
		
		/**
		 * Requests a seek operation at <code>future</code> milliseconds
		 * in the future.
		 * @param future
		 */
		public synchronized void deferredSeek(int future, long seek) {
			if (videoPlayer != null && !videoPlayer.isDisposed()) {
				this.seek = seek;
				time = System.currentTimeMillis() + future;
				videoPlayer.getDisplay().timerExec(future, this);
			}
		}
	}
	
	public static final String ID = "ca.uvic.chisel.videoplayer.test.videoPlayerEditor";
	
	class BrowserVideoListener implements VideoListener {

		@Override
		public void videoUpdate(VideoEvent event) {
			long loaded = 0;
			switch (event.type) {
			case VideoEvent.VIDEO_LOAD_PROGRESS:
				loadedProgress.setOffset(0);
				loadedProgress.setLength(event.getSource().getLoadProgress());
				break;
			case VideoEvent.VIDEO_LOADED:
				loadedProgress.setOffset(0);
				loadedProgress.setLength(event.getSource().getLength());
				break;
			case VideoEvent.VIDEO_READY:
				loaded = event.getSource().getLoadProgress();
				if (loaded != loadedProgress.getLength()) {
					loadedProgress.setLength(loaded);
				}
				playerBar.setMaximum(event.getSource().getLength());
				break;
			case VideoEvent.VIDEO_PLAYHEAD:
				loaded = event.getSource().getLoadProgress();
				if (loaded != loadedProgress.getLength()) {
					loadedProgress.setLength(loaded);
				}
				playerBar.setSelectedMaximum(event.getSource().getTime());
				break;
			case VideoEvent.VIDEO_STARTED:
			case VideoEvent.VIDEO_PAUSED:
			case VideoEvent.VIDEO_STOPPED:
				updateButtons();
				break;
			}
			
		}
		
	}

	/**
	 * 
	 */
	public VideoPlayerEditor() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		if (input instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput)input).getFile();
			if (file.getName().endsWith(".flv")) {
				setInput(input);
				this.file = file;
				if (videoPlayer != null) {
					videoPlayer.load(file.getLocation().toPortableString());
				}
				return;
			}
		}
		throw new PartInitException("Invalid video file");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.seeker = new SeekRunnable();
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(4, false));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 4;
		this.videoPlayer = new BrowserVideoPlayer(page, SWT.NONE);
		videoPlayer.setLayoutData(data);
		play = new Button(page, SWT.TOGGLE);
		play.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.widget;
				ImageRegistry reg = TestPlayer.getDefault().getImageRegistry();
				if (b.getSelection()) {
					videoPlayer.play();
				} else {
					videoPlayer.pause();
				}
			}
		});
		ImageRegistry reg = TestPlayer.getDefault().getImageRegistry();
		play.setImage(reg.get(TestPlayer.ICON_PLAY));
		play.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
				
		Button stop = new Button(page, SWT.PUSH);
		stop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ImageRegistry reg = TestPlayer.getDefault().getImageRegistry();
				videoPlayer.stop();
				play.setSelection(false);
				play.setImage(reg.get(TestPlayer.ICON_PLAY));
				play.setToolTipText("Play");
			}
		});
		stop.setToolTipText("Stop");
		stop.setImage(reg.get(TestPlayer.ICON_STOP));
		
		stop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		videoPlayer.addVideoListener(new BrowserVideoListener());
		
		playerBar = new AnnotatedProgress(page, SWT.NONE);
		playerBar.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		loadedProgress = new ProgressAnnotation(playerBar);
		loadedProgress.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_YELLOW));
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.heightHint = 10;
		playerBar.setLayoutData(data);
		playerBar.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				seeker.deferredSeek(300, playerBar.toRangeValue(e.width));
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);			
			}
		});
		if (file != null) {
			videoPlayer.load(file.getLocation().toPortableString());
		}
	}
	
	private void updateButtons() {
		ImageRegistry reg = TestPlayer.getDefault().getImageRegistry();
		if (getVideoPlayer().isPlaying()) {
			play.setImage(reg.get(TestPlayer.ICON_PAUSE));
			play.setToolTipText("Pause");
			play.setSelection(true);
		} else if (getVideoPlayer().isPaused() || getVideoPlayer().isStopped()){
			play.setImage(reg.get(TestPlayer.ICON_PLAY));
			play.setToolTipText("Play");
		}
	}
	
	
	

	public AbstractVideoPlayer getVideoPlayer() {
		return videoPlayer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

}
