package com.smartspend.ai.utils;

import android.content.Context;
import androidx.preference.PreferenceManager;

public final class SecurityUtils {
    private static final String KEY_BIOMETRIC = "biometric_enabled";
    private SecurityUtils() {}

    public static boolean isBiometricEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_BIOMETRIC, false);
    }

    public static void setBiometricEnabled(Context context, boolean enabled) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_BIOMETRIC, enabled).apply();
    }
}