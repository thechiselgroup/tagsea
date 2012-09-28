package ca.uvic.chisel.video.widget.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import ca.uvic.chisel.video.VideoPlayerPlugin;
import ca.uvic.chisel.video.widget.AbstractVideoPlayer;
import ca.uvic.chisel.video.widget.VideoEvent;

public class BrowserVideoPlayer extends AbstractVideoPlayer {

	
	private static final String VIDEO_PROGRESS = "VideoProgress";
	private static final String VIDEO_LOADED = "VideoLoaded";
	private static final String VIDEO_PLAY_HEAD = "VideoPlayHead";
	private static final String VIDEO_READY = "VideoReady";
	protected static final String VIDEO_EVENT = "VideoEvent";
	protected static final String FLEX_LOADED = "FlexLoaded";
	private boolean loaded;
	private static final int STOPPED = 0;
	private static final int PLAYING = 1;
	private static final int PAUSED = 2;
	private int playState = STOPPED;
	private String deferredLoad;
	private boolean deferredPlay;
	private long deferredLocation;

	public BrowserVideoPlayer(Composite parent, int style) {
		super(parent, style);
		loaded = false;
	}

	@Override
	protected Control createContents(Composite parent, int style) {
		Browser browser = new Browser(parent, style);
		browser.addProgressListener(new ProgressListener() {
			@Override
			public void changed(ProgressEvent event) {
			}

			@Override
			public void completed(ProgressEvent event) {
			}});
		browser.addTitleListener(new TitleListener() {
			
			@Override
			public void changed(TitleEvent event) {
				String[] eventText = event.title.split("\\:");
				if (eventText.length > 1 && VIDEO_EVENT.equals(eventText[0])) {
					handleVideoEvent(eventText);
				}
			}
		});
		browser.addStatusTextListener(new StatusTextListener(){

			@Override
			public void changed(StatusTextEvent event) {
				String[] eventText = event.text.split("\\:");
				if (eventText.length > 1 && VIDEO_EVENT.equals(eventText[0])) {
					handleVideoEvent(eventText);
				}
			}});
		browser.setJavascriptEnabled(true);
		browser.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				synchronizeBrowserSize();
			}
			
			@Override
			public void controlMoved(ControlEvent e) {}
		});
		URL url = VideoPlayerPlugin.getDefault().getBundle().getResource("web/main.html");
		try {
			URL fileURL = FileLocator.toFileURL(url);
			browser.setUrl(fileURL.toString());
			waitForLoad();
		} catch (IOException e) {
		}
		
		return browser;
	}
	
	protected void handleVideoEvent(String[] eventText) {
		//the second string contains the event type
		String type = eventText[1];
		if (FLEX_LOADED.equals(type)) {
			loaded = true;
			synchronizeBrowserSize();
			if (deferredLoad != null) {
				load(deferredLoad);
				deferredLoad = null;
			}
			if (deferredLocation > 0) {
				seek(deferredLocation);
				deferredLocation = 0;
			}
			if (deferredPlay) {
				play();
			}
		} else if (VIDEO_READY.equals(type)) {
			loaded = true;
			asyncFireVideoEvent(new VideoEvent(VideoEvent.VIDEO_READY,this));
		} else if (VIDEO_PLAY_HEAD.equals(type)) {
			asyncFireVideoEvent(new VideoEvent(VideoEvent.VIDEO_PLAYHEAD, this));
		} else if (VIDEO_LOADED.equals(type)) {
			asyncFireVideoEvent(new VideoEvent(VideoEvent.VIDEO_LOADED, this));
		} else if (VIDEO_PROGRESS.equals(type)) {
			asyncFireVideoEvent(new VideoEvent(VideoEvent.VIDEO_LOAD_PROGRESS, this));
		}
	}

	protected Browser getBrowser() {
		return (Browser)getControl();
	}
	
	protected void synchronizeBrowserSize() {
		try {
			Browser b = getBrowser();
			Rectangle r = b.getClientArea();
			setVideoWidth(r.width);
			setVideoHeight(r.height);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void setVideoWidth(int width) {
		if (!isLoaded()) {
			return;
		}
		Browser b = getBrowser();
		String script = "return setVideoWidth(" + width + ")";
		b.evaluate(script);
	}
	
	protected void setVideoHeight(int height) {
		if (!isLoaded()) {
			return;
		}
		Browser b = getBrowser();
		b.evaluate("return setVideoHeight('" + height + "')");
	}

	@Override
	public long getTime() {
		if (!isLoaded()) {
			return -1;
		}
		Object result = getBrowser().evaluate("return getVideoPlayLocation()");
		if (result instanceof Number) {
			return ((Number)result).longValue();
		}
		return 0;
	}
	
	public long getLength() {
		if (!isLoaded()) {
			return -1;
		}
		Object result = getBrowser().evaluate("return getVideoLength()");
		if (result instanceof Number) {
			return ((Number)result).longValue();
		}
		return 0;
	}
	
	public long getLoadProgress() {
		if (!isLoaded()) {
			return -1;
		}
		Object result = getBrowser().evaluate("return getVideoProgress()");
		if (result instanceof Number) {
			return ((Number)result).longValue();
		}
		return 0;
	}

	@Override
	public void load(String location) {
		if (!isLoaded()) {
			deferredLoad = location;
			return;
		}
		try {
			location = location.replace('\\', '/');
			getBrowser().evaluate("return load('" + location + "')");
			Object error = getBrowser().evaluate("return getError()");
			if (error != null) {
				System.out.println(error);
			}
		} catch (SWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void pause() {
		if (!isLoaded()) {
			deferredPlay = false;
			return;
		}
		Object result = getBrowser().evaluate("return pause()");
		if (result != null) {
			System.out.println(result);
		} else {
			playState = PAUSED;
			fireVideoEvent(new VideoEvent(VideoEvent.VIDEO_PAUSED, this));
		}
	}

	@Override
	public void play() {
		if (!isLoaded()) {
			deferredPlay = true;
			return;
		}
		Object result = getBrowser().evaluate("return play()");
		if (result != null) {
			System.out.println(result);
		} else {
			playState = PLAYING;
			fireVideoEvent(new VideoEvent(VideoEvent.VIDEO_STARTED, this));
		}
	}

	@Override
	public void seek(long offset) {
		if (!isLoaded()) {
			deferredLocation = offset;
			return;
		}
		Object result = getBrowser().evaluate("return setVideoPlayLocation(" + offset + ")");
		if (result != null) {
			System.out.println(result);
		} 
	}

	@Override
	public void stop() {
		if (!isLoaded()) {
			deferredPlay = false;
			return;
		}
		Object result = getBrowser().evaluate("return stop()");
		if (result != null) {
			System.out.println(result);
		} else {
			fireVideoEvent(new VideoEvent(VideoEvent.VIDEO_STOPPED, this));
			playState = STOPPED;
		}
	}
	
	private void waitForLoad() {
//		while (!isLoaded()) {
//			getDisplay().readAndDispatch();
//		}
	}
	
	private boolean isLoaded() {
		return loaded;
	}

	@Override
	public boolean isPaused() {
		return playState == PAUSED;
	}

	@Override
	public boolean isPlaying() {
		return playState == PLAYING;
	}
	
	@Override
	public boolean isStopped() {
		return playState == STOPPED;
	}

}
