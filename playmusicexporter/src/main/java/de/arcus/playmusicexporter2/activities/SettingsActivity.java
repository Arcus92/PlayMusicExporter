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
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.io.File;

import de.arcus.framework.activities.DirectoryBrowserActivity;
import de.arcus.framework.logger.Logger;
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
    private Preference mPrefExportPath;

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
        mPrefExportPath = findPreference("preference_export_path");
        mPrefExportPath.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Current path
                Uri currentPath = mSettings.getUri(PlayMusicExporterSettings.PREF_EXPORT_URI, Uri.EMPTY);

                // Starts the directory browser activity
                Intent intent = DirectoryBrowserActivity.openDirectoryBrowser(getApplicationContext(), currentPath, getString(R.string.settings_export_path));
                startActivityForResult(intent, REQUEST_EXPORT_PATH);

                // We wait for the activity result
                return true;
            }
        });

        updatePrefExportPath();
    }

    /**
     * Updates the entry for the export path
     */
    private void updatePrefExportPath() {
        // Get the path from the settings
        Uri selectedPath = mSettings.getUri(PlayMusicExporterSettings.PREF_EXPORT_URI, Uri.EMPTY);

        // Get all storage
        String[] storage = FileTools.getStorages();
        String[] storageValues = new String[storage.length + 1];
        for(int i=0; i<storage.length; i++) {
            String path = storage[i] + "/Music";
            storageValues[i] = path;
        }

        // If the path is not set, we use the first default value.
        // This should not happen, because we set the default value in the
        // PlayMusicExporterSettings constructor, but i want to be sure.
        if (selectedPath == Uri.EMPTY && storageValues.length > 0) {
            selectedPath = Uri.fromFile(new File(storageValues[0]));
        }

        String label;

        if (selectedPath.toString().startsWith("file://")) {
            // Simple path
            label = selectedPath.getPath();
        } else {
            // Uri TODO: Add a nice readable label
            label = selectedPath.getPath();
        }

        mPrefExportPath.setSummary(label);
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
                Uri uri = data.getData();

                Logger.getInstance().logInfo("Uri", uri.toString());

                mSettings.setUri(PlayMusicExporterSettings.PREF_EXPORT_URI, uri);

                // Update the label
                updatePrefExportPath();
            }
        }
    }
}