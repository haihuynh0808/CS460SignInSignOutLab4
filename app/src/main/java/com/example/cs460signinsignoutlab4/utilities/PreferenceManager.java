package com.example.cs460signinsignoutlab4.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Read and write SharedPreferences values
 */
public class PreferenceManager {

    private final SharedPreferences sharedPreferences;

    /**
     * Opens SharedPreferences
     *
     * @param context used to access the app preferences
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);

    }

    /**
     * Saves a boolean setting
     *
     * @param key identifier for the setting
     * @param value boolean value to store
     */
    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    /**
     * Reads a boolean setting
     *
     * @param key identifier for the setting
     * @return stored value or false if it does not exist
     */
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Saves a string setting
     *
     * @param key identifier for the setting
     * @param value text to store
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    /**
     * Reads a string setting
     *
     * @param key identifier for the setting
     * @return stored text, or null if it does not exist
     */
    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    /**
     * Removes all values from the SharedPreferences file
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
