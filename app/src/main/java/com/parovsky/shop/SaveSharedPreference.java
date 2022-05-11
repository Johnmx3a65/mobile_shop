package com.parovsky.shop;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    static final String PREF_EMAIL = "email";

    static final String PREF_USER_PASSWORD = "password";

    static SharedPreferences getSharedPreferences(android.content.Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setUserEmail(android.content.Context context, String email) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_EMAIL, email);
        editor.apply();
    }

    public static String getUserEmail(android.content.Context context) {
        return getSharedPreferences(context).getString(PREF_EMAIL, null);
    }

    public static void setUserPassword(android.content.Context context, String password) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USER_PASSWORD, password);
        editor.apply();
    }

    public static String getUserPassword(android.content.Context context) {
        return getSharedPreferences(context).getString(PREF_USER_PASSWORD, null);
    }
}
