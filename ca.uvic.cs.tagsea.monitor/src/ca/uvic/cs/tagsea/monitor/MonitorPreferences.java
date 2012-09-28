package ca.uvic.cs.tagsea.monitor;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class MonitorPreferences extends AbstractPreferenceInitializer {

	public static final String LAST_DATE_PREF = "last.date";
	public static final String ASKED_TO_REGISTER_PREF = "asked.to.register";

	public MonitorPreferences() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore prefs = TagSEAMonitorPlugin.getDefault().getPreferenceStore();
		prefs.setDefault(LAST_DATE_PREF, "");
	}
	
	public static Date getLastSendDate() {
		String d = getPreferenceStore().getString(LAST_DATE_PREF);
		if ("".equals(d)) {
			return null;
		} else {
			try {
				return DateFormat.getDateInstance().parse(d);
			} catch (ParseException e) {
				return null;
			}
		}
	}
	
	public static void setLastSendDate(Date date) {
		String d = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
		getPreferenceStore().setValue(LAST_DATE_PREF, d);
	}
	
	public static boolean hasAskedToRegister() {
		return getPreferenceStore().getBoolean(ASKED_TO_REGISTER_PREF);
	}
	
	public static void setAskedToRegister(boolean b) {
		getPreferenceStore().setValue(ASKED_TO_REGISTER_PREF, b);
	}
	
	public static IPreferenceStore getPreferenceStore() {
		return TagSEAMonitorPlugin.getDefault().getPreferenceStore();
	}

}
