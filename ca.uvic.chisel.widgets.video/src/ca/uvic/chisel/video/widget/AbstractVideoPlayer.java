package ca.uvic.chisel.video.widget;

import java.net.URI;
import java.util.LinkedList;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractVideoPlayer extends Composite {

	private Control control;
	private LinkedList<VideoListener> listeners;

	protected AbstractVideoPlayer(Composite parent, int style) {
		super(parent, style);
		listeners = new LinkedList<VideoListener>();
		setLayout(new FillLayout());
		this.control = createContents(this, style);
		addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent e) {
				AbstractVideoPlayer.this.widgetDisposed(e);
			}});
	}
	
	private void widgetDisposed(DisposeEvent e) {
		synchronized (listeners) {
			listeners.clear();
		}
	}

	protected Control getControl() {
		return control;
	}
	
	protected abstract Control createContents(Composite parent, int style);
	
	public abstract void play();
	
	public abstract void stop();
	
	public abstract void pause();
	
	public abstract void seek(long offset);
	
	public abstract long getTime();
	
	public abstract void load(String location);
	
	public void addVideoListener(VideoListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}
	
	public void removeVideoListener(VideoListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	private VideoListener[] getListeners() {
		synchronized (listeners) {
			return listeners.toArray(new VideoListener[listeners.size()]);
		}
	}
	
	
	/**
	 * Fires the given event in the current thread
	 * @param event
	 */
	protected void fireVideoEvent(VideoEvent event) {
		for (VideoListener listener : getListeners()) {
			listener.videoUpdate(event);
		}
	}
	
	/**
	 * Asynchronously fires the given event in the display thread.
	 * @param event
	 */
	protected void asyncFireVideoEvent(final VideoEvent event) {
		getDisplay().asyncExec(new Runnable(){

			@Override
			public void run() {
				fireVideoEvent(event);
			}
			
		});
	}

	/**
	 * Returns the length of the video in seconds.
	 * @return
	 */
	public abstract long getLength();
	
	/**
	 * Returns the number of seconds of video that have been loaded.
	 * @return
	 */
	public abstract long getLoadProgress();

	public abstract boolean isPaused();
	
	public abstract boolean isPlaying();

	public abstract boolean isStopped();

}
