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

package de.arcus.playmusicexporter2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import de.arcus.framework.activities.DirectoryBrowserActivity;
import de.arcus.framework.utils.FileTools;
import de.arcus.playmusicexporter2.R;
import de.arcus.playmusicexporter2.settings.PlayMusicExporterSettings;

/**
 * The preference activity
 */
public class SettingsActivity extends PreferenceActivity {
    private final static int REQUEST_EXPORT_PATH = 1;

    // App settings
    private PlayMusicExporterSettings mSettings;

    // Preferences
    private ListPreference mPrefExportPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the settings
        mSettings = new PlayMusicExporterSettings(this);

        // Setup the default shared preference
        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(PlayMusicExporterSettings.DEFAULT_SETTINGS_FILENAME);
        prefMgr.setSharedPreferencesMode(MODE_WORLD_READABLE);

        // Loads the preference xml
        addPreferencesFromResource(R.xml.preferences);

        // The export path preference
        mPrefExportPath = (ListPreference)findPreference("preference_export_path");
        mPrefExportPath.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // The new value
                String selectedPath = newValue.toString();

                boolean ret = true;

                // Empty = custom
                if (TextUtils.isEmpty(selectedPath)) {
                    // Opens the directory browser
                    Intent intent = new Intent(getApplicationContext(), DirectoryBrowserActivity.class);

                    // The current path
                    intent.putExtra(DirectoryBrowserActivity.EXTRA_PATH, mSettings.getString(PlayMusicExporterSettings.PREF_EXPORT_PATH, ""));
                    intent.putExtra(DirectoryBrowserActivity.EXTRA_TITLE, getString(R.string.settings_export_path));

                    // Starts the activity
                    startActivityForResult(intent, REQUEST_EXPORT_PATH);

                    // Do not apply the empty value.
                    // We wait vor the activity result instead.
                    ret = false;
                } else {
                    // Saves the path
                    mSettings.setString(PlayMusicExporterSettings.PREF_EXPORT_PATH, selectedPath);
                }

                // Update the entry
                updatePrefExportPath();

                return ret;
            }
        });
        updatePrefExportPath();
    }

    /**
     * Updates the entry for the export path
     */
    private void updatePrefExportPath() {
        // Get the path from the settings
        String selectedPath = mSettings.getString(PlayMusicExporterSettings.PREF_EXPORT_PATH, "");

        // Get all storage
        String[] storage = FileTools.getStorages();
        String[] storageValues = new String[storage.length + 1];
        String[] storageNames = new String[storage.length + 1];
        int storageSelected = storage.length;

        for(int i=0; i<storage.length; i++) {
            String path = storage[i] + "/Music";
            storageValues[i] = path;
            storageNames[i] = path;

            // Is storage selected?
            if (selectedPath.equals(path)) {
                storageSelected = i;
            }
        }

        // If the path is not set, we use the first default value.
        // This should not happen, because we set the default value in the
        // PlayMusicExporterSettings constructor, but i want to be sure.
        if (selectedPath.equals("") && storageValues.length > 0) {
            selectedPath = storageValues[0];
            storageSelected = 0;
        }

        // Custom entry
        storageValues[storage.length] = "";
        storageNames[storage.length] = getString(R.string.settings_export_path_custom);

        // Custom is selected
        if (storageSelected == storage.length) {
            // Adds the custom path to the label
            storageNames[storage.length] += "\n" + selectedPath;
        }

        mPrefExportPath.setEntries(storageNames);
        mPrefExportPath.setEntryValues(storageValues);
        mPrefExportPath.setValueIndex(storageSelected);
        mPrefExportPath.setSummary(selectedPath);
    }

    /**
     * Returns from a result activity
     * @param requestCode The request code
     * @param resultCode The result code
     * @param data The data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result is ok
        if (resultCode == RESULT_OK) {
            // Export path was changed
            if (requestCode == REQUEST_EXPORT_PATH) {
                // TODO
            }
        }
    }
}