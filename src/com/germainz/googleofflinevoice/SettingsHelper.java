package com.germainz.googleofflinevoice;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;

public class SettingsHelper {
    private XSharedPreferences mXSharedPreferences = null;
    private SharedPreferences mSharedPreferences = null;
    private Context mContext = null;
    private Set<String> mListItems;

    private static final String PACKAGE_NAME = "com.germainz.googleofflinevoice";
    private static final String PREFS = PACKAGE_NAME + "_preferences";
    private static final String PREF_DISABLED = "pref_disabled";
    private static final String PREF_VOICE_IME = "pref_voice_ime";
    private static final String BLACKLIST = "blacklist";

    // Called from module's classes.
    public SettingsHelper() {
        mXSharedPreferences = new XSharedPreferences(PACKAGE_NAME, PREFS);
        mXSharedPreferences.makeWorldReadable();
    }

    // Called from activities.
    public SettingsHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_WORLD_READABLE);
        mContext = context;
    }

    // The methods below are only called from the module's class (XSharedPreferences)
    public boolean isVoiceTypingEnabled() {
        return mXSharedPreferences.getBoolean(PREF_VOICE_IME, true);
    }

    public void reload() {
        mXSharedPreferences.reload();
        mListItems = getListItems();
    }

    // The methods below are only called from activities (SharedPreferences)
    public boolean addListItem(String listItem) {
        mListItems.add(listItem);
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        prefEditor.putStringSet(BLACKLIST, mListItems);
        prefEditor.apply();
        return true;
    }

    public void removeListItem(String listItem) {
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        mListItems.remove(listItem);
        prefEditor.putStringSet(BLACKLIST, mListItems);
        prefEditor.apply();
    }

    public void setModDisabled(boolean disabled) {
        mSharedPreferences.edit().putBoolean(PREF_DISABLED, disabled).apply();
    }

    public void setVoiceTyping(boolean enabled) {
        mSharedPreferences.edit().putBoolean(PREF_VOICE_IME, enabled).apply();
    }

    // These methods can be called from both
    public boolean isModDisabled() {
        if (mSharedPreferences != null)
            return mSharedPreferences.getBoolean(PREF_DISABLED, false);
        else if (mXSharedPreferences != null)
            return mXSharedPreferences.getBoolean(PREF_DISABLED, false);
        return false;
    }

    public boolean isListed(String s) {
        if (mListItems == null)
            mListItems = getListItems();
        return mListItems.contains(s);
    }

    public Set<String> getListItems() {
        Set<String> set = new HashSet<String>();
        if (mSharedPreferences != null)
            return mSharedPreferences.getStringSet(BLACKLIST, set);
        else if (mXSharedPreferences != null)
            return mXSharedPreferences.getStringSet(BLACKLIST, set);
        return set;
    }
}
