package org.ligi.fast;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
/**
 * Activity to Edit the Preferences 
 *
 * @author Marcus -ligi- Büschleb
 * 
 * License GPLv3
 *
 */
public class FASTPrefsActivity extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
				this);

		CheckBoxPreference doLaunchSingleCheckBox = new CheckBoxPreference(this);
		doLaunchSingleCheckBox.setKey(FASTPrefs.KEY_LAUNCHSINGLE);
		doLaunchSingleCheckBox.setTitle(R.string.launch_single);
		doLaunchSingleCheckBox.setSummary("Auto-Launch if only one App left ");
		doLaunchSingleCheckBox.setDefaultValue(false);

		root.addPreference(doLaunchSingleCheckBox);
		return root;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		}
		return false;
	}
}
