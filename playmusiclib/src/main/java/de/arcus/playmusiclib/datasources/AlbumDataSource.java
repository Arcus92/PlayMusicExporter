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
import android.database.DatabaseUtils;
import android.text.TextUtils;

import java.util.List;

import de.arcus.playmusiclib.PlayMusicManager;
import de.arcus.playmusiclib.items.Album;
import de.arcus.playmusiclib.items.Artist;

/**
 * Data source for albums
 */
public class AlbumDataSource extends DataSource<Album> {
    // Tables
    private final static String TABLE_MUSIC = "MUSIC";

    // All fields
    private final static String COLUMN_ALBUM_ID = "MUSIC.AlbumId";
    private final static String COLUMN_ALBUM = "MUSIC.Album";
    private final static String COLUMN_ALBUM_ARTIST = "MUSIC.AlbumArtist";
    private final static String COLUMN_ALBUM_ARTWORK_FILE = "(SELECT ARTWORK_CACHE.LocalLocation FROM MUSIC AS MUSIC2 LEFT JOIN ARTWORK_CACHE ON MUSIC2.AlbumArtLocation = ARTWORK_CACHE.RemoteLocation WHERE MUSIC2.AlbumID = MUSIC.AlbumID AND ARTWORK_CACHE.RemoteLocation IS NOT NULL LIMIT 1) AS ArtistArtworkPath";

    private final static String COLUMN_TITLE = "MUSIC.Title";
    private final static String COLUMN_ARTIST = "MUSIC.Artist";

    // All columns
    private final static String[] COLUMNS_ALL = {COLUMN_ALBUM_ID, COLUMN_ALBUM,
            COLUMN_ALBUM_ARTIST, COLUMN_ALBUM_ARTWORK_FILE};

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
    public AlbumDataSource(PlayMusicManager playMusicManager) {
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
        // Ignore non-PlayMusic tracks
        where = combineWhere(where, "LocalCopyType != 300");

        // Loads only offline tracks
        if (mOfflineOnly)
            where = combineWhere(where, "LocalCopyPath IS NOT NULL");

        // Search only items which contains the key
        if (!TextUtils.isEmpty(mSearchKey)) {
            String searchKey = DatabaseUtils.sqlEscapeString("%" + mSearchKey + "%");

            String searchWhere = COLUMN_ALBUM + " LIKE " + searchKey;
            searchWhere += " OR " + COLUMN_TITLE + " LIKE " + searchKey;
            searchWhere += " OR " + COLUMN_ALBUM_ARTIST + " LIKE " + searchKey;
            searchWhere += " OR " + COLUMN_ARTIST + " LIKE " + searchKey;

            where = combineWhere(where, searchWhere);
        }

        return where;
    }

    @Override
    /**
     * Gets the data object from a data row
     * @param cursor Data row
     * @return Data object
     */
    protected Album getDataObject(Cursor cursor) {
        Album instance = new Album(mPlayMusicManager);

        // Read all properties from the data row
        instance.setAlbumId(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_ALBUM_ID)));
        instance.setAlbum(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ALBUM)));
        instance.setAlbumArtist(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ALBUM_ARTIST)));
        instance.setArtworkFile(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ALBUM_ARTWORK_FILE)));

        return instance;
    }

    /**
     * Loads an album by Id
     * @param id The album id
     * @return Returns the album or null
     */
    public Album getById(long id) {
        return getItem(TABLE_MUSIC, COLUMNS_ALL, prepareWhere(COLUMN_ALBUM_ID + " = " + id), null, COLUMN_ALBUM_ID);
    }

    /**
     * Gets a list of all albums by an artist
     * @return Returns albums
     */
    public List<Album> getByArtist(Artist artist) {
        return getItems(TABLE_MUSIC, COLUMNS_ALL, prepareWhere(COLUMN_ALBUM_ARTIST + " = " + DatabaseUtils.sqlEscapeString(artist.getArtist())), COLUMN_ALBUM, COLUMN_ALBUM_ID);
    }

    /**
     * Gets a list of all albums
     * @return Returns all albums
     */
    public List<Album> getAll() {
        return getItems(TABLE_MUSIC, COLUMNS_ALL, prepareWhere(""), COLUMN_ALBUM, COLUMN_ALBUM_ID);
    }
}
