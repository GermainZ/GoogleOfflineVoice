package com.germainz.googleofflinevoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String action = intent.getAction();
        if (action.equals("com.germainz.googleofflinevoice.enable"))
            prefs.edit().putBoolean("pref_disabled", false).commit();
        else if (action.equals("com.germainz.googleofflinevoice.disable"))
            prefs.edit().putBoolean("pref_disabled", true).commit();
        else if (action.equals("com.germainz.googleofflinevoice.enable_voice_ime"))
            prefs.edit().putBoolean("pref_voice_ime", true).commit();
        else if (action.equals("com.germainz.googleofflinevoice.disable_voice_ime"))
            prefs.edit().putBoolean("pref_voice_ime", false).commit();
    }
}
