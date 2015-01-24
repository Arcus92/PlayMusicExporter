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

package de.arcus.playmusiclib.datasources;

import android.database.Cursor;
import android.text.TextUtils;

import java.util.List;

import de.arcus.playmusiclib.PlayMusicManager;
import de.arcus.playmusiclib.items.Album;
import de.arcus.playmusiclib.items.MusicTrack;
import de.arcus.playmusiclib.items.Playlist;

/**
 * Data source for music tracks
 */
public class MusicTrackDataSource extends DataSource<MusicTrack> {
    // Tables
    private final static String TABLE_MUSIC = "MUSIC";
    private final static String TABLE_MUSIC_PLAYLIST = "MUSIC LEFT JOIN LISTITEMS ON MUSIC.ID = LISTITEMS.MusicID";

    // All fields
    private final static String COLUMN_ID = "Id";
    private final static String COLUMN_SIZE = "Size";
    private final static String COLUMN_LOCALCOPYPATH = "LocalCopyPath";
    private final static String COLUMN_LOCALCOPYTYPE = "LocalCopyType";
    private final static String COLUMN_LOCALCOPYSTORAGETYPE = "LocalCopyStorageType";
    private final static String COLUMN_TITLE = "Title";
    private final static String COLUMN_ARTIST = "Artist";
    private final static String COLUMN_ALBUMARTIST = "AlbumArtist";
    private final static String COLUMN_ALBUM = "Album";
    private final static String COLUMN_GENRE = "Genre";
    private final static String COLUMN_YEAR = "Year";
    private final static String COLUMN_TRACKNUMBER = "TrackNumber";
    private final static String COLUMN_DISCNUMBER = "DiscNumber";
    private final static String COLUMN_DURATION = "Duration";
    private final static String COLUMN_ALBUMID = "AlbumId";
    private final static String COLUMN_CLIENTID = "ClientId";
    private final static String COLUMN_SOURCEID = "SourceId";
    private final static String COLUMN_CPDATA = "CpData";
    private final static String COLUMN_ARTWORKFILE = "(SELECT LocalLocation FROM artwork_cache WHERE artwork_cache.RemoteLocation = AlbumArtLocation) AS ArtworkFile";

    // All columns
    private final static String[] COLUMNS_ALL = { COLUMN_ID, COLUMN_SIZE,
            COLUMN_LOCALCOPYPATH, COLUMN_LOCALCOPYTYPE, COLUMN_LOCALCOPYSTORAGETYPE, COLUMN_TITLE, COLUMN_ARTIST, COLUMN_ALBUMARTIST,
            COLUMN_ALBUM, COLUMN_GENRE, COLUMN_YEAR, COLUMN_TRACKNUMBER, COLUMN_DISCNUMBER, COLUMN_DURATION,
            COLUMN_ALBUMID, COLUMN_CLIENTID, COLUMN_SOURCEID, COLUMN_ARTWORKFILE, COLUMN_CPDATA };

    /**
     * If this is set the data source will only load offline tracks
     */
    private boolean mOfflineOnly;

    /**
     * If the search key is set, this data source will only load items which contains this text
     */
    private String mSearchKey;

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
     * @return Gets the search key
     */
    public String getSearchKey() {
        return mSearchKey;
    }

    /**
     * @param searchKey Sets the search key
     */
    public void setSerchKey(String searchKey) {
        mSearchKey = searchKey;
    }

    /**
     * Creates a new data source
     * @param playMusicManager The manager
     */
    public MusicTrackDataSource(PlayMusicManager playMusicManager) {
        super(playMusicManager);

        // Load global settings
        setOfflineOnly(playMusicManager.getOfflineOnly());
    }

    /**
     * Prepare the where command and adds the global settings
     * @param where The where command
     * @return The new where command
     */
    private String prepareWhere(String where) {
        // The new where
        String newWhere = "LocalCopyType != 300";

        // Loads only offline tracks
        if (mOfflineOnly)
            newWhere += " AND LocalCopyPath IS NOT NULL";

        // Search only items which contains the key
        if (!TextUtils.isEmpty(mSearchKey)) {
            String searchKey = mSearchKey.replace("'", "''");

            newWhere += " AND (" + COLUMN_ALBUM + " LIKE '%" + searchKey + "%'";
            newWhere += " OR " + COLUMN_TITLE + " LIKE '%" + searchKey + "%'";
            newWhere += " OR " + COLUMN_ALBUMARTIST + " LIKE '%" + searchKey + "%'";
            newWhere += " OR " + COLUMN_ARTIST + " LIKE '%" + searchKey + "%')";
        }

        // Adds an 'and' if needed
        if (!TextUtils.isEmpty(where)) where = "(" + where + ") AND ";

        where += newWhere;

        return where;
    }


    @Override
    /**
     * Gets the data object from a data row
     * @param cursor Data row
     * @return Data object
     */
    protected MusicTrack getDataObject(Cursor cursor) {
        MusicTrack instance = new MusicTrack(mPlayMusicManager);

        // Read all properties from the data row
        instance.setId(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_ID)));
        instance.setSize(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_SIZE)));
        instance.setLocalCopyPath(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_LOCALCOPYPATH)));
        instance.setLocalCopyType(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_LOCALCOPYTYPE)));
        instance.setLocalCopyStorageType(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_LOCALCOPYSTORAGETYPE)));
        instance.setTitle(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_TITLE)));
        instance.setArtist(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ARTIST)));
        instance.setAlbumArtist(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ALBUMARTIST)));
        instance.setAlbum(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ALBUM)));
        instance.setGenre(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_GENRE)));
        instance.setYear(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_YEAR)));
        instance.setTrackNumber(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_TRACKNUMBER)));
        instance.setDiscNumber(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_DISCNUMBER)));
        instance.setDuration(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_DURATION)));
        instance.setAlbumId(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_ALBUMID)));
        instance.setClientId(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_CLIENTID)));
        instance.setSourceId(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_SOURCEID)));
        instance.setCpData(cursor.getBlob(getColumnsIndex(COLUMNS_ALL, COLUMN_CPDATA)));
        instance.setArtworkFile(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ARTWORKFILE)));

        return instance;
    }

    /**
     * Loads a track by Id
     * @param id The track id
     * @return Returns the music track or null
     */
    public MusicTrack getById(long id) {
        return getItem(TABLE_MUSIC, COLUMNS_ALL, prepareWhere("Id = " + id));
    }

    /**
     * Gets a list of tracks by an album
     * @param album The album
     * @return Returns the track list
     */
    public List<MusicTrack> getByAlbum(Album album) {
        return getItems(TABLE_MUSIC, COLUMNS_ALL, prepareWhere("AlbumId = " + album.getAlbumId()), COLUMN_DISCNUMBER + ", " + COLUMN_TRACKNUMBER);
    }

    /**
     * Gets a list of tracks by an playlist
     * @param playlist The playlist
     * @return Returns the track list
     */
    public List<MusicTrack> getByPlaylist(Playlist playlist) {
        return getItems(TABLE_MUSIC, COLUMNS_ALL, prepareWhere("ListId = " + playlist.getId()), "LISTITEMS.ID");
    }
}
