package com.germainz.googleofflinevoice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class Preferences extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

    }

    public class PrefsFragment extends PreferenceFragment {
        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.prefs);

            Preference prefDisabled = findPreference(Common.PREF_DISABLED);
            prefDisabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    new ToggleAppWidgetProvider().updateWidgets(Preferences.this, (Boolean) newValue);
                    return true;
                }
            });

            Preference prefShowAppIcon = findPreference(Common.PREF_SHOW_APP_ICON);
            prefShowAppIcon.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Activity act = getActivity();
                    PackageManager p = act.getPackageManager();
                    int state = (Boolean) newValue ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                    final ComponentName alias = new ComponentName(getActivity(), "com.germainz.googleofflinevoice.Preferences-Alias");
                    p.setComponentEnabledSetting(alias, state, PackageManager.DONT_KILL_APP);
                    return true;
                }
            });

            Preference prefBlacklist = findPreference(Common.PREF_BLACKLIST);
            prefBlacklist.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    Intent i = new Intent(Preferences.this, Blacklist.class);
                    startActivity(i);
                    return true;
                }
            });
        }
    }
}
