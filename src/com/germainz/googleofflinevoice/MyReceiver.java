package com.germainz.googleofflinevoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SettingsHelper settingsHelper = new SettingsHelper(context);
        String action = intent.getAction();
        if (action.equals("com.germainz.googleofflinevoice.enable_voice_ime"))
            settingsHelper.setVoiceTyping(true);
        else if (action.equals("com.germainz.googleofflinevoice.disable_voice_ime"))
            settingsHelper.setVoiceTyping(false);
        else {
            if (action.equals("com.germainz.googleofflinevoice.enable"))
                settingsHelper.setModDisabled(false);
            else if (action.equals("com.germainz.googleofflinevoice.disable"))
                settingsHelper.setModDisabled(true);
            else if (action.equals("com.germainz.googleofflinevoice.toggle"))
                settingsHelper.setModDisabled(!settingsHelper.isModDisabled());
            new ToggleAppWidgetProvider().updateWidgets(context, settingsHelper.isModDisabled());
        }
    }
}
