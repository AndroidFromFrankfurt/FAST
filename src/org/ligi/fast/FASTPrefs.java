package org.ligi.fast;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * Class to handle the Preferences 
 *
 * @author Marcus -ligi- Büschleb
 * 
 * License GPLv3
 *
 */
public class FASTPrefs {

	private SharedPreferences mSharedPreferences;
	
	public final static String KEY_LAUNCHSINGLE="launch_single";
	public static final String KEY_SEARCHPKG = "search_pkg";
	
	public FASTPrefs(Context ctx) {
		mSharedPreferences=PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public boolean isLaunchSingleActivated() {
		return mSharedPreferences.getBoolean(KEY_LAUNCHSINGLE,false);
	}
	
	public boolean isSearchPackageActivated() {
		return mSharedPreferences.getBoolean(KEY_SEARCHPKG,false);
	}
}
