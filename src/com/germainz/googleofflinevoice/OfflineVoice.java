package com.germainz.googleofflinevoice;

import java.util.HashMap;
import java.util.Map;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class OfflineVoice implements IXposedHookLoadPackage {

    private static final Map<String, EngineSelectorImpl> CLASS_ENGINE_SELECTOR_IMPL;
    private static final SettingsHelper mSettingsHelper = new SettingsHelper();

    // Adapted from GravityBox's implementation, by C3C0
    // https://github.com/GravityBox/GravityBox/commit/8b6a926876360c5b8b92542029a4b8dda9fe6506
    private static final class EngineSelectorImpl {
        Class<?> clazz;
        String mTriggerApplication;
        String mApplicationId;

        public EngineSelectorImpl(String mTriggerApplication, String mApplicationId) {
            this.mTriggerApplication = mTriggerApplication;
            this.mApplicationId = mApplicationId;
        }
    }

    static {
        CLASS_ENGINE_SELECTOR_IMPL = new HashMap<String, EngineSelectorImpl>();
        CLASS_ENGINE_SELECTOR_IMPL.put("com.google.android.speech.EngineSelectorImpl",
                new EngineSelectorImpl("mTriggerApplication", "mApplicationId"));
        CLASS_ENGINE_SELECTOR_IMPL.put("ehy", new EngineSelectorImpl("bZX", "anL"));
    }

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.google.android.googlequicksearchbox"))
            return;

        for (String className : CLASS_ENGINE_SELECTOR_IMPL.keySet()) {
            final EngineSelectorImpl engineSelectorImpl;
            try {
                Class<?> cls = findClass(className, lpparam.classLoader);
                engineSelectorImpl = CLASS_ENGINE_SELECTOR_IMPL.get(className);
                engineSelectorImpl.clazz = cls;
            } catch (Throwable t) {
                continue;
            }

            XC_MethodHook hook = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    mSettingsHelper.reload();
                    if (mSettingsHelper.isModDisabled())
                        return;
                    String mTriggerApplication = (String) getObjectField(param.args[0], engineSelectorImpl.mTriggerApplication);
                    String mApplicationId = (String) getObjectField(param.args[0], engineSelectorImpl.mApplicationId);
                    // Google Search initiated this if mApplicationId equals "voice-search"; if we continue,
                    // recognition will succeed but Google Search will show an error about not being able to
                    // connect. The user can then press "Resend audio" and it'll work, which is a hassle.
                    // I couldn't figure out how fix this, so I'm disabling it.
                    if (mApplicationId.equals("voice-search") ||
                            (mApplicationId.equals("voice-ime") && !mSettingsHelper.isVoiceTypingEnabled()) ||
                            (mSettingsHelper.isListed(mTriggerApplication)))
                        return;
                    param.args[2] = false;
                }
            };

            hookAllConstructors(engineSelectorImpl.clazz, hook);
        }

    }
}
