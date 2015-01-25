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

import com.mpatric.mp3agic.ID3v1Genres;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v22Tag;
import com.mpatric.mp3agic.ID3v23Tag;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;

import java.util.ArrayList;
import java.util.List;

import de.arcus.framework.logger.Logger;
import de.arcus.framework.superuser.SuperUser;
import de.arcus.framework.superuser.SuperUserTools;
import de.arcus.framework.utils.FileTools;
import de.arcus.framework.utils.MediaScanner;
import de.arcus.playmusiclib.enums.ID3v2Version;
import de.arcus.playmusiclib.exceptions.CouldNotOpenDatabaseException;
import de.arcus.playmusiclib.exceptions.NoSuperUserException;
import de.arcus.playmusiclib.exceptions.PlayMusicNotFoundException;
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
     * If this is set the exporter will add ID3 tag to the mp3 files
     */
    private boolean mID3Enable = true;

    /**
     * @return Gets whether the exporter adds ID3 tags to the mp3 files
     */
    public boolean getID3Enable() {
        return mID3Enable;
    }

    /**
     * @param id3Enable Sets whether the exporter adds ID3 tags to the mp3 files
     */
    public void setID3Enable(boolean id3Enable) {
        mID3Enable = id3Enable;
    }

    /**
     * If this is set the exporter will add the artwork to the ID2v2 tag
     */
    private boolean mID3EnableArtwork = true;

    /**
     * @return Gets whether the exporter adds the artwork image
     */
    public boolean getID3EnableArtwork() {
        return mID3EnableArtwork;
    }

    /**
     * @param id3EnableArtwork Sets whether the exporter adds the artwork image
     */
    public void setID3EnableArtwork(boolean id3EnableArtwork) {
        mID3EnableArtwork = id3EnableArtwork;
    }

    /**
     * If this is set the exporter will also adds ID3v1 tags
     */
    private boolean mID3EnableFallback = true;

    /**
     * @return Gets whether the exporter adds ID3v1 tags as fallback
     */
    public boolean getID3EnableFallback() {
        return mID3EnableFallback;
    }

    /**
     * @param id3EnableFallback Sets whether the exporter adds ID3v1 tags as fallback
     */
    public void setID3EnableFallback(boolean id3EnableFallback) {
        mID3EnableFallback = id3EnableFallback;
    }

    /**
     * The sub version of ID3v2
     * Use 2.3 for default to fix issues with the Windows Windows Media Player
     */
    private ID3v2Version mID3v2Version = ID3v2Version.ID3v23;

    /**
     * @return Gets the sub version of ID3v2
     */
    public ID3v2Version getID3v2Version() {
        return mID3v2Version;
    }

    /**
     * @param id3v2Version Sets the sub version of ID3v2
     */
    public void setID3v2Version(ID3v2Version id3v2Version) {
        mID3v2Version = id3v2Version;
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
     * @throws de.arcus.playmusiclib.exceptions.PlayMusicNotFoundException PlayMusic is not installed
     * @throws NoSuperUserException No super user permissions
     * @throws de.arcus.playmusiclib.exceptions.CouldNotOpenDatabaseException Could not open the database
     */
    public void startUp() throws PlayMusicNotFoundException, NoSuperUserException, CouldNotOpenDatabaseException {
        // Gets the package manager
        PackageManager packageManager = mContext.getPackageManager();

        try {
            // Loads the application info
            mPlayMusicApplicationInfo = packageManager.getApplicationInfo(PLAYMUSIC_PACKAGE_ID, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // No PlayMusic
            throw new PlayMusicNotFoundException();
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
     * @throws de.arcus.playmusiclib.exceptions.CouldNotOpenDatabaseException Could not open the database
     */
    private void loadDatabase() throws NoSuperUserException, CouldNotOpenDatabaseException {
        // Ask for super user
        if (!SuperUser.askForPermissions())
            throw new NoSuperUserException();

        // Close the database
        closeDatabase();

        // Copy the database to the temp folder
        if (!SuperUserTools.fileCopy(getDatabasePath(), getTempDatabasePath()))
            throw new CouldNotOpenDatabaseException();

        // Opens the database
        try {
            mDatabase = SQLiteDatabase.openDatabase(getTempDatabasePath(), null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            throw new CouldNotOpenDatabaseException();
        }
    }

    /**
     * Reloads the database from PlayMusic
     * @throws NoSuperUserException No super user permissions
     * @throws de.arcus.playmusiclib.exceptions.CouldNotOpenDatabaseException Could not open the database
     */
    public void realoadDatabase() throws NoSuperUserException, CouldNotOpenDatabaseException {
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

        // Encrypt the file
        if (musicTrack.isEncoded()) {
            String fileTmpCrypt = getTempPath() + "/crypt.mp3";

            // Encrypts the file
            if (trackEncrypt(musicTrack, fileTmp, fileTmpCrypt)) {
                // Remove the old tmp file
                FileTools.fileDelete(fileTmp);

                // New tmp file
                fileTmp = fileTmpCrypt;
            } else {
                Logger.getInstance().logWarning("ExportMusicTrack", "Encrypting failed! Continue with decrypted file.");
            }
        }

        // We want to export the ID3 tags
        if (mID3Enable) {
            // Adds the meta data
            if (!trackWriteID3(musicTrack, fileTmp, dest)) {
                Logger.getInstance().logWarning("ExportMusicTrack", "ID3 writer failed! Continue without ID3 tags.");

                // Failed, moving without meta data
                if (!FileTools.fileMove(fileTmp, dest)) {
                    Logger.getInstance().logError("ExportMusicTrack", "Moving the raw file failed!");

                    // Could not copy the file
                    return false;
                }
            }
        } else {
            // Moving the file
            if (!FileTools.fileMove(fileTmp, dest)) {
                Logger.getInstance().logError("ExportMusicTrack", "Moving the raw file failed!");

                // Could not copy the file
                return false;
            }
        }

        cleanUp();

        // Adds the file to the media system
        new MediaScanner(mContext, dest);

        // Done
        return true;
    }

    /**
     * Copies the music file to a new path and adds the mp3 meta data
     * @param musicTrack Track information
     * @param src The source mp3 file
     * @param dest The destination path
     * return Return if the operation was successful
     */
    private boolean trackWriteID3(MusicTrack musicTrack, String src, String dest) {
        try {
            // Opens the mp3
            Mp3File mp3File = new Mp3File(src);

            // Removes all existing tags
            mp3File.removeId3v1Tag();
            mp3File.removeId3v2Tag();
            mp3File.removeCustomTag();

            // We want to add a fallback ID3v1 tag
            if (mID3EnableFallback) {
                // Create a new tag with ID3v1
                ID3v1Tag tagID3v1 = new ID3v1Tag();

                // Set all tag values
                tagID3v1.setTrack(musicTrack.getTitle());
                tagID3v1.setArtist(musicTrack.getArtist());
                tagID3v1.setAlbum(musicTrack.getAlbum());
                tagID3v1.setYear(musicTrack.getYear());

                // Search the genre
                for(int n=0; n<ID3v1Genres.GENRES.length; n++) {
                    // Genre found
                    if (ID3v1Genres.GENRES[n].equals(musicTrack.getGenre())) {
                        tagID3v1.setGenre(n);
                        break;
                    }
                }

                mp3File.setId3v1Tag(tagID3v1);
            }

            // It can't be null
            ID3v2 tagID3v2 = null;

            // Creates the requested version
            switch(mID3v2Version) {
                case ID3v22:
                    tagID3v2 = new ID3v22Tag();
                    break;
                case ID3v23:
                    tagID3v2 = new ID3v23Tag();
                    break;
                case ID3v24:
                    tagID3v2 = new ID3v24Tag();
                    break;
            }


            // Set all tag values
            tagID3v2.setTitle(musicTrack.getTitle());
            tagID3v2.setArtist(musicTrack.getArtist());
            tagID3v2.setAlbum(musicTrack.getAlbum());
            tagID3v2.setAlbumArtist(musicTrack.getAlbumArtist());
            tagID3v2.setTrack("" + musicTrack.getTrackNumber());
            tagID3v2.setPartOfSet("" + musicTrack.getDiscNumber());
            tagID3v2.setYear(musicTrack.getYear());

            try {
                // Maybe the genre is not supported
                tagID3v2.setGenreDescription(musicTrack.getGenre());
            } catch (IllegalArgumentException e) {
                Logger.getInstance().logWarning("TrackWriteID3", e.getMessage());
            }

            // Add the artwork to the meta data
            if (mID3EnableArtwork) {
                String artworkPath = musicTrack.getArtworkPath();

                if (artworkPath != null) {

                    // Reads the artwork with root permissions (maybe it is in /data)
                    byte[] artworkData = SuperUserTools.fileReadToByteArray(artworkPath);
                    if (artworkData != null) {
                        // The file extension is always .jpg even if the image is a PNG file,
                        // so we need to check the magic number

                        // JPEG is default
                        String mimeType = "image/jpeg";

                        // Check for other image formats
                        if (artworkData.length > 4) {
                            // PNG-Header
                            if (artworkData[0] == -119 && artworkData[1] == 80 && artworkData[2] == 78 && artworkData[3] == 71) {
                                mimeType = "image/png";
                            }
                        }

                        // Adds the artwork to the meta data
                        tagID3v2.setAlbumImage(artworkData, mimeType);
                    }
                }
            }

            mp3File.setId3v2Tag(tagID3v2);


            // Save the file
            mp3File.save(dest);

            // Done
            return true;
        } catch (Exception e) {
            Logger.getInstance().logError("TrackWriteId3", e.toString());
        }

        // Failed
        return false;
    }

    /**
     * Encrypts a track and save it to a new path
     * @param musicTrack The music track
     * @param src The source mp3 file
     * @param dest The destination path
     * @return Return if the operation was successful
     */
    private boolean trackEncrypt(MusicTrack musicTrack, String src, String dest) {

        try {
            AllAccessExporter allAccessExporter = new AllAccessExporter(src, musicTrack.getCpData());

            // Checks the magic number
            if (!allAccessExporter.hasValidMagicNumber()) {
                Logger.getInstance().logError("TrackEncrypt", "Invalid magic number! This is not an AllAccess file");
                return false;
            }

            // Saves the file
            return allAccessExporter.save(dest);
        } catch (Exception e) {
            Logger.getInstance().logError("TrackEncrypt", e.toString());
        }

        // Failed
        return false;

    }

    /**
     * Deletes all cache files
     */
    private void cleanUp() {
        FileTools.fileDelete(getTempPath() + "/tmp.mp3");
        FileTools.fileDelete(getTempPath() + "/crypt.mp3");
    }
}
