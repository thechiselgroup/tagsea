package ca.uvic.chisel.video.widget;

public interface VideoListener {
	
	/**
	 * Indicates something has changed with the video. Query the type of event
	 * for more information.
	 * @param event the event.
	 */
	void videoUpdate(VideoEvent event);

}
