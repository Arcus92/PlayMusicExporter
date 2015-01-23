/*
 * Copyright (c) 2015 David Schulte
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.arcus.framework.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class to read and write app settings without to care about to open and close an editor
 */
public class AppSettings {
    /**
     * The default settings file
     */
    private static final String DEFAULT_SETTINGS_FILENAME = "app_settings";

    /**
     * The preferences
     */
    private SharedPreferences mSharedPreferences;

    /**
     * Creates a new instance of AppSettings that access to the default settings file
     * @param context Context of the app
     */
    public AppSettings(Context context) {
        this(context, DEFAULT_SETTINGS_FILENAME);
    }

    /**
     * Creates a new instance of AppSettings that access to a specific settings file
     * @param context Context of the app
     */
    public AppSettings(Context context, String settingsFilename) {
        mSharedPreferences = context.getSharedPreferences(settingsFilename, Context.MODE_PRIVATE);
    }

    /**
     * Gets a string from the settings
     * @param key Key of the setting
     * @param defValue Default value which is returned if the key doesn't exists
     * @return Value
     */
    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    /**
     * Gets a boolean from the settings
     * @param key Key of the setting
     * @param defValue Default value which is returned if the key doesn't exists
     * @return Value
     */
    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * Gets a float from the settings
     * @param key Key of the setting
     * @param defValue Default value which is returned if the key doesn't exists
     * @return Value
     */
    public float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    /**
     * Gets an int from the settings
     * @param key Key of the setting
     * @param defValue Default value which is returned if the key doesn't exists
     * @return Value
     */
    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    /**
     * Gets a long from the settings
     * @param key Key of the setting
     * @param defValue Default value which is returned if the key doesn't exists
     * @return Value
     */
    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    /**
     * Returns whether the settings contains a specific key
     * @param key Key of the setting
     * @return Returns whether the settings contains a specific key
     */
    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    /**
     * Removes an setting from the settings
     * @param key Key of the setting
     */
    public void remove(String key) {
        // Opens the editor
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        // Removes the key
        editor.remove(key);

        // Commits the change
        editor.apply();
    }

    /**
     * Saves a string to the settings
     * @param key Key of the setting
     * @param value Value
     */
    public void setString(String key, String value) {
        // Opens the editor
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(key, value);

        // Commits the change
        editor.apply();
    }

    /**
     * Saves a boolean to the settings
     * @param key Key of the setting
     * @param value Value
     */
    public void setBoolean(String key, boolean value) {
        // Opens the editor
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putBoolean(key, value);

        // Commits the change
        editor.apply();
    }

    /**
     * Saves a float to the settings
     * @param key Key of the setting
     * @param value Value
     */
    public void setFloat(String key, float value) {
        // Opens the editor
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putFloat(key, value);

        // Commits the change
        editor.apply();
    }

    /**
     * Saves an int to the settings
     * @param key Key of the setting
     * @param value Value
     */
    public void setInt(String key, int value) {
        // Opens the editor
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putInt(key, value);

        // Commits the change
        editor.apply();
    }

    /**
     * Saves a long to the settings
     * @param key Key of the setting
     * @param value Value
     */
    public void setLong(String key, long value) {
        // Opens the editor
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putLong(key, value);

        // Commits the change
        editor.apply();
    }



}
