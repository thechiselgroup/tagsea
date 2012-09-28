package ca.uvic.chisel.videoplayer.test.views;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import ca.uvic.chisel.video.widget.AbstractVideoPlayer;
import ca.uvic.chisel.video.widget.AnnotatedProgress;
import ca.uvic.chisel.video.widget.ProgressAnnotation;
import ca.uvic.chisel.video.widget.VideoEvent;
import ca.uvic.chisel.video.widget.VideoListener;
import ca.uvic.chisel.video.widget.internal.BrowserVideoPlayer;
import ca.uvic.chisel.videoplayer.test.IVideoPlayerPart;
import ca.uvic.chisel.videoplayer.test.TestPlayer;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class VideoPlayerView extends ViewPart implements IVideoPlayerPart {
	
	private class StreamGobbler extends Thread {
		private InputStream in;
		private PrintWriter writer;

		StreamGobbler(InputStream in, OutputStream out) {
			super("Reading Input");
			this.in = in;
			this.writer = (out != null) ? new PrintWriter(out) : null;
		}
		
		StreamGobbler(InputStream in, PrintWriter out) {
			super("Reading Input");
			this.in = in;
			this.writer = out;
		}
		
		@Override
		public void run() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = reader.readLine())!=null) {
					if (writer != null) {
						writer.println(line);
					}
				}
			} catch (IOException e) {}
		}
	}

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ca.uvic.chisel.videoplayer.test.views.VideoPlayerView";


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

	private BrowserVideoPlayer videoPlayer;
	private AnnotatedProgress playerBar;
	private ProgressAnnotation loadedProgress;
	private Button play;

	/**
	 * The constructor.
	 */
	public VideoPlayerView() {
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

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
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
		
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				VideoPlayerView.this.fillContextMenu(manager);
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
	}

	private void fillContextMenu(IMenuManager manager) {
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		Action openAction = new Action() {
			@Override
			public void run() {
				FileDialog dialog = new FileDialog(getSite().getShell(), SWT.OPEN);
				Runtime rt = Runtime.getRuntime();
				String[] extensions = new String[] {"*.flv"};
				try {
					Process process = rt.exec("ffmpeg -version");
					new StreamGobbler(process.getInputStream(), System.out).start();
					new StreamGobbler(process.getErrorStream(), System.err).start();
					int err = process.waitFor();
					if (err == 0) {
						extensions = new String[] {"*.flv", "*.avi", "*.wmv", "*.m4v", "*.mp4"};
					}
				} catch (IOException e) {
				} catch (InterruptedException e) {
					Thread.interrupted();
				}
				dialog.setFilterExtensions(extensions);
				String filename = dialog.open();
				if (filename != null && !filename.isEmpty()) {
					File file = new File(filename);
					final File[] transcoder = new File[] {file};
					if (file.isFile()) {
						int dot = file.getName().lastIndexOf('.');
						if (dot > 0 && dot < file.getName().length()-1) {
							String extension = file.getName().substring(dot+1);
							if (!extension.toLowerCase().equals("flv")) {

								ProgressMonitorDialog progress = new ProgressMonitorDialog(getSite().getShell());
								try {
									progress.run(true, false, new IRunnableWithProgress() {

										@Override
										public void run(final IProgressMonitor monitor) throws InvocationTargetException,
										InterruptedException {
											try {
												IPath output = TestPlayer.getDefault().getStateLocation();
												File file = transcoder[0];
												int dot = file.getName().lastIndexOf('.');
												String fname = file.getName().substring(0, dot) + ".flv";
												output = output.append(fname);
												if (output.toFile().exists()) {
													output.toFile().delete();
												}
												String command = "ffmpeg -i \"" + file.getAbsolutePath() + "\" \"" + output.toOSString() + "\"";
												Process process = Runtime.getRuntime().exec(command);
												StringWriter errorString = new StringWriter();
												PrintWriter err = new PrintWriter(new StringWriter()) {
													public void println(String s) {
														super.print(s);
													};
												};
												PrintWriter out = new PrintWriter(errorString);
												new StreamGobbler(process.getInputStream(), out).start();
												new StreamGobbler(process.getErrorStream(), err).start();
												int error = 0;
												error = process.waitFor();

												if (error == 0) {
													transcoder[0] = output.toFile();
												} else {
													MessageDialog.openError(
															getSite().getShell(), 
															"Error Transcoding Video", 
															"ffmpeg error " + error + ". Unable to transcode video for playback.");
													System.err.println(errorString.toString());
												}
											} catch (Exception e) {
												throw new InvocationTargetException(e);
											}
										}

									});
								} catch (InvocationTargetException e1) {
								} catch (InterruptedException e1) {
								}

								
							}
						}
						file = transcoder[0];
						videoPlayer.load(file.getAbsolutePath());
					}
				}
			}
		};
		openAction.setText("Open file...");
		manager.add(openAction);
	}

	private void makeActions() {
		
	}

	private void hookDoubleClickAction() {
		
	}
	

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		
	}
	
	public AbstractVideoPlayer getVideoPlayer() {
		return videoPlayer;
	}
}