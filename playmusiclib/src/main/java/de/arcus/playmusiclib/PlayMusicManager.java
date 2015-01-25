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

package de.arcus.playmusiclib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import de.arcus.framework.superuser.SuperUser;
import de.arcus.framework.superuser.SuperUserTools;
import de.arcus.framework.utils.FileTools;
import de.arcus.playmusiclib.exceptions.CouldNotOpenDatabase;
import de.arcus.playmusiclib.exceptions.NoSuperUserException;
import de.arcus.playmusiclib.exceptions.PlayMusicNotFound;
import de.arcus.playmusiclib.items.MusicTrack;

/**
 * Connects to the PlayMusic data
 */
public class PlayMusicManager {
    /**
     * PlayMusic package id
     */
    public static final String PLAYMUSIC_PACKAGE_ID = "com.google.android.music";

    /**
     * Context of the app, needed to access to the package manager
     */
    private Context mContext;

    /**
     * @return Gets the app context
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Play Music database
     */
    private SQLiteDatabase mDatabase;

    /**
     * @return Gets the database
     */
    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    /**
     * Path to the private app data
     * Eg.: /data/data/com.google.android.music/
     */
    private String mPathPrivateData;

    /**
     * Paths to all possible public app data
     * Eg.: /sdcard/Android/data/com.google.android.music/
     */
    private String[] mPathPublicData;

    /**
     * Application info from PlayMusic
     */
    private ApplicationInfo mPlayMusicApplicationInfo;

    /**
     * @return Gets the path to the database
     */
    private String getDatabasePath() {
        return mPathPrivateData + "/databases/music.db";
    }

    /**
     * The database will be copied to a temp folder to access from the app
     * @return Gets the temp path to the database
     */
    private String getTempDatabasePath() {
        return getTempPath() + "/music.db";
    }

    /**
     * @return Gets the temp path to the exported music
     */
    private String getTempPath() {
        return mContext.getCacheDir().getAbsolutePath();
    }

    /**
     * If this is set the data source will only load offline tracks
     */
    private boolean mOfflineOnly;

    /**
     * @return Returns whether the data source should only load offline tracks
     */
    public boolean getOfflineOnly() {
        return mOfflineOnly;
    }

    /**
     * @param offlineOnly Sets whether the data source should only load offline tracks
     */
    public void setOfflineOnly(boolean offlineOnly) {
        mOfflineOnly = offlineOnly;
    }

    /**
     * Creates a new PlayMusic manager
     * @param context App context
     */
    public PlayMusicManager(Context context) {
        mContext = context;
    }


    /**
     * Loads all needed information and opens the database
     * @throws PlayMusicNotFound PlayMusic is not installed
     * @throws NoSuperUserException No super user permissions
     * @throws CouldNotOpenDatabase Could not open the database
     */
    public void startUp() throws PlayMusicNotFound, NoSuperUserException, CouldNotOpenDatabase {
        // Gets the package manager
        PackageManager packageManager = mContext.getPackageManager();

        try {
            // Loads the application info
            mPlayMusicApplicationInfo = packageManager.getApplicationInfo(PLAYMUSIC_PACKAGE_ID, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // No PlayMusic
            throw new PlayMusicNotFound();
        }


        // Path to the private data
        mPathPrivateData = mPlayMusicApplicationInfo.dataDir;

        List<String> publicDataList = new ArrayList<>();
        // Search on all sdcards
        for (String storage : FileTools.getStorages()) {
            String publicData = storage + "/Android/data/com.google.android.music";

            // Directory exists
            if (FileTools.directoryExists(publicData))
                publicDataList.add(publicData);
        }
        // Convert to array
        mPathPublicData = publicDataList.toArray(new String[publicDataList.size()]);

        // Loads the database
        loadDatabase();
    }

    /**
     * Copies the database to a temp directory and opens it
     * @throws NoSuperUserException No super user permissions
     * @throws CouldNotOpenDatabase Could not open the database
     */
    private void loadDatabase() throws NoSuperUserException, CouldNotOpenDatabase {
        // Ask for super user
        if (!SuperUser.askForPermissions())
            throw new NoSuperUserException();

        // Close the database
        closeDatabase();

        // Copy the database to the temp folder
        if (!SuperUserTools.fileCopy(getDatabasePath(), getTempDatabasePath()))
            throw new CouldNotOpenDatabase();

        // Opens the database
        try {
            mDatabase = SQLiteDatabase.openDatabase(getTempDatabasePath(), null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            throw new CouldNotOpenDatabase();
        }
    }

    /**
     * Reloads the database from playmusic
     * @throws NoSuperUserException No super user permissions
     * @throws CouldNotOpenDatabase Could not open the database
     */
    public void realoadDatabase() throws NoSuperUserException, CouldNotOpenDatabase {
        loadDatabase();
    }

    /**
     * Closes the database if it's open
     */
    private void closeDatabase() {
        if (mDatabase == null) return;

        mDatabase.close();
    }

    /**
     * @return Gets the path to the private music
     */
    public String getPrivateMusicPath() {
        return mPathPrivateData + "/files/music";
    }

    /**
     * Gets the path to the music track
     * @param localCopyPath The local copy path
     * @return The path to the music file
     */
    public String getMusicFile(String localCopyPath) {
        // LocalCopyPath is empty
        if (TextUtils.isEmpty(localCopyPath)) return null;

        // Private music path
        String path = getPrivateMusicPath() + "/" + localCopyPath;
        // Music file exists
        if (SuperUserTools.fileExists(path)) return path;

        // Search in the public data
        for (String publicData : mPathPublicData) {
            path = publicData + "/files/music/" + localCopyPath;

            if (FileTools.fileExists(path)) return path;
        }

        return null;
    }

    /**
     * @return Gets the path to the private files
     */
    public String getPrivateFilesPath() {
        return mPathPrivateData + "/files";
    }

    /**
     * Gets the full path to the artwork
     * @param artworkPath The artwork path
     * @return The full path to the artwork
     */
    public String getArtworkPath(String artworkPath) {
        // Artwork path is empty
        if (TextUtils.isEmpty(artworkPath)) return null;

        // Private artwork path
        String path = getPrivateFilesPath() + "/" + artworkPath;
        // Artwork file exists
        if (SuperUserTools.fileExists(path)) return path;

        // Search in the public data
        for (String publicData : mPathPublicData) {
            path = publicData + "/files/" + artworkPath;

            if (FileTools.fileExists(path)) return path;
        }

        return null;
    }

    /**
     * Exports a track to the sd card
     * @param musicTrack The music track you want to export
     * @param dest The destination path
     * @return Returns whether the export was successful
     */
    public boolean exportMusicTrack(MusicTrack musicTrack, String dest) {
        // Check for null
        if (musicTrack == null) return false;

        String srcFile = musicTrack.getSourceFile();

        // Could not find the source file
        if (srcFile == null) return false;

        String fileTmp = getTempPath() + "/tmp.mp3";

        // Copy to temp path failed
        if (!SuperUserTools.fileCopy(srcFile, fileTmp))
            return false;

        // TODO

        return true;
    }


}
