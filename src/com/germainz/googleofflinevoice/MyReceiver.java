package com.germainz.googleofflinevoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SettingsHelper settingsHelper = new SettingsHelper(context);
        String action = intent.getAction();
        if (action.equals(Common.ACTION_ENABLE_IME))
            settingsHelper.setVoiceTyping(true);
        else if (action.equals(Common.ACTION_DISABLE_IME))
            settingsHelper.setVoiceTyping(false);
        else {
            if (action.equals(Common.ACTION_ENABLE))
                settingsHelper.setModDisabled(false);
            else if (action.equals(Common.ACTION_DISABLE))
                settingsHelper.setModDisabled(true);
            else if (action.equals(Common.ACTION_TOGGLE))
                settingsHelper.setModDisabled(!settingsHelper.isModDisabled());
            new ToggleAppWidgetProvider().updateWidgets(context, settingsHelper.isModDisabled());
        }
    }
}
