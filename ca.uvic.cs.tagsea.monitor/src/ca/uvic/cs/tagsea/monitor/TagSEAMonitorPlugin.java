package ca.uvic.cs.tagsea.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.httpclient.HttpClient;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.update.internal.core.UpdateCore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TagSEAMonitorPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.uvic.cs.tagsea.monitor";
	// The shared instance
	private static TagSEAMonitorPlugin plugin;
	
	/**
	 * The constructor
	 */
	public TagSEAMonitorPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TagSEAMonitorPlugin getDefault() {
		return plugin;
	}
	
	public SimpleDayLog getMonitorLog() throws IOException {
		return SimpleDayLog.loadLogForDay(new Date());
	}
	
	public Date[] getLogDates() throws IOException {
		File location = getStateLocation().toFile();
		ArrayList<Date> dates = new ArrayList<Date>();
		String[] files = location.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				File newFile = new File(dir, name);
				return !newFile.isDirectory() && name.endsWith(".monitor.log");
			}});
		for (String f : files) {
			File file = new File(location, f);
			FileReader r = new FileReader(file);
			BufferedReader in = new BufferedReader(r);
			String s = in.readLine();
			try {
				Date d = DateFormat.getDateInstance(DateFormat.SHORT).parse(s);
				dates.add(d);
			} catch (ParseException e) {
			}
		}
		return dates.toArray(new Date[dates.size()]);
	}
	
	public SimpleDayLog getMonitorLogForDate(Date day) throws IOException {
		return SimpleDayLog.loadLogForDay(day);
	}
	
	
	public IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}
	
	/**
	 * @param e
	 */
	public void log(Exception e) {
		Status s = new Status(Status.ERROR, PLUGIN_ID, 0, e.getLocalizedMessage(), e);
		getLog().log(s);
	}
	
	public void log(String message, int level) {
		Status s = new Status(level, PLUGIN_ID, 0, message, null);
		getLog().log(s);
	}
	
	public String getResourceString(String key) {
		ResourceBundle b;
		try {
			b = Platform.getResourceBundle(getBundle());
			String result = b.getString(key);
			if (result == null)
				return result = key;
			return result;
		} catch (MissingResourceException e) {
			log(e);
			return key;
		}
	}
	
	public void configureProxy(HttpClient httpClient) {
		if (UpdateCore.getPlugin().getPluginPreferences().getBoolean(UpdateCore.HTTP_PROXY_ENABLE)) {
			String proxyHost = UpdateCore.getPlugin().getPluginPreferences().getString(UpdateCore.HTTP_PROXY_HOST);
			int proxyPort = UpdateCore.getPlugin().getPluginPreferences().getInt(UpdateCore.HTTP_PROXY_PORT);
			httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);

			/*if (uploadAuthentication == null)
				uploadAuthentication = UserValidationDialog.getAuthentication(proxyHost,
						"(Leave fields blank if authentication is not required)");
			if (uploadAuthentication != null) {
				httpClient.getState().setProxyCredentials(
						new AuthScope(proxyHost, proxyPort),
						new UsernamePasswordCredentials(uploadAuthentication.getUser(), uploadAuthentication
								.getPassword()));
			}*/
		}
	}
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
