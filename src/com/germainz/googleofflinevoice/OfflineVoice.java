package com.germainz.googleofflinevoice;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class OfflineVoice implements IXposedHookLoadPackage {

    static XSharedPreferences prefs = new XSharedPreferences("com.germainz.googleofflinevoice");
    public static final String LISTVIEW_PREFERENCE_FILENAME = "listview_blacklist";
    static XSharedPreferences listviewPref = new XSharedPreferences("com.germainz.googleofflinevoice", LISTVIEW_PREFERENCE_FILENAME);

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        String pkg = lpparam.packageName;
        if (!pkg.equals("com.google.android.googlequicksearchbox"))
            return;
        final Class<?> EngineSelectorImpl = findClass("com.google.android.speech.EngineSelectorImpl", lpparam.classLoader);
        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                prefs.reload();
                if (prefs.getBoolean("pref_disabled", false))
                    return;
                String mTriggerApplication = (String) getObjectField(param.args[0], "mTriggerApplication");
                String mApplicationId = (String) getObjectField(param.args[0], "mApplicationId");
                // Google Search initiated this if mApplicationId equals "voice-search"; if we continue,
                // recognition will succeed but Google Search will show an error about not being able to
                // connect. The user can then press "Resend audio" and it'll work, which is a hassle.
                // I couldn't figure out how fix this, so I'm disabling it.
                if (mApplicationId.equals("voice-search") ||
                        (mApplicationId.equals("voice-ime") && !prefs.getBoolean("pref_voice_ime", true)) ||
                        (isBlacklisted(mTriggerApplication)))
                    return;
                param.args[2] = false;
            }
        };
        hookAllConstructors(EngineSelectorImpl, hook);
    }

    // Slightly modified from XuiMod by zst123
    private static boolean isBlacklisted(String triggerPkg) {
        listviewPref.reload();
        int size = listviewPref.getInt("items" + "_size", 0);
        if(size != 0) {
            for(int i = 0; i < size; i++){
                String pkg = listviewPref.getString("items" + "_" + i, "");
                if(pkg.equals(triggerPkg))
                    return true;
            }
        }
        return false;
    }
}
