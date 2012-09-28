package ca.uvic.chisel.video.widget;

/**
 * Simple event handler for when video events occur.
 * @author Del Myers
 *
 */
public class VideoEvent {
	/**
	 * Indicates that enough of the video has been loaded to begin
	 * playback.
	 */
	public static final int VIDEO_READY = 0;
	/**
	 * Indicates that some more of the video has been loaded.
	 */
	public static final int VIDEO_LOAD_PROGRESS = 1;
	
	public static final int VIDEO_LOADED = 2;
	
	/**
	 * Indicates that the video play-back location has changed.
	 */
	public static final int VIDEO_PLAYHEAD = 3;
	public static final int VIDEO_STARTED = 4;
	public static final int VIDEO_PAUSED = 5;
	public static final int VIDEO_STOPPED = 6;

	public final int type;
	public final AbstractVideoPlayer source;
	public final long time;
	
	
	public VideoEvent(int type, AbstractVideoPlayer source) {
		this.time = System.currentTimeMillis();
		this.type = type;
		this.source = source;
	}
	
	public int getType() {
		return type;
	}
	
	public AbstractVideoPlayer getSource() {
		return source;
	}
	
	public long getTime() {
		return time;
	}

}
