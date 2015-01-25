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
import de.arcus.playmusiclib.items.Artist;

/**
 * Data source for artists
 */
public class ArtistDataSource extends DataSource<Artist> {
    // Tables
    private final static String TABLE_MUSIC = "MUSIC";

    // All fields
    private final static String COLUMN_ARTIST_ID = "ArtistId";
    private final static String COLUMN_ARTIST = "Artist";
    private final static String COLUMN_ARTWORK_FILE = "(SELECT ARTWORK_CACHE.LocalLocation FROM MUSIC AS MUSIC2 LEFT JOIN ARTWORK_CACHE ON ARTWORK_CACHE.RemoteLocation = MUSIC.AlbumArtLocation WHERE MUSIC2.ArtistId = MUSIC.ArtistId AND ARTWORK_CACHE.LocalLocation IS NOT NULL LIMIT 1) AS ArtworkFile";

    // All columns
    private final static String[] COLUMNS_ALL = { COLUMN_ARTIST_ID, COLUMN_ARTIST, COLUMN_ARTWORK_FILE };


    /**
     * If this is set the data source will only load offline tracks
     */
    private boolean mOfflineOnly; // TODO: Offline only has no effects on the artists data sources

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
    public ArtistDataSource(PlayMusicManager playMusicManager) {
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
        // Search only items which contains the key
        if (!TextUtils.isEmpty(mSearchKey)) {
            String searchKey = DatabaseUtils.sqlEscapeString("%" + mSearchKey + "%");

            where = combineWhere(where, "(" + COLUMN_ARTIST + " LIKE " + searchKey + ")");
        }

        return where;
    }

    @Override
    /**
     * Gets the data object from a data row
     * @param cursor Data row
     * @return Data object
     */
    protected Artist getDataObject(Cursor cursor) {
        Artist instance = new Artist(mPlayMusicManager);

        // Read all properties from the data row
        instance.setArtistId(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_ARTIST_ID)));
        instance.setArtist(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ARTIST)));
        instance.setArtworkFile(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ARTWORK_FILE)));

        return instance;
    }

    /**
     * Loads a artist by Id
     * @param id The artist id
     * @return Returns the artist or null
     */
    public Artist getById(long id) {
        return getItem(TABLE_MUSIC, COLUMNS_ALL, prepareWhere(COLUMN_ARTIST_ID + " = " + id), null, COLUMN_ARTIST_ID);
    }

    /**
     * Gets a list of all artists
     * @return Returns all artists
     */
    public List<Artist> getAll() {
        return getItems(TABLE_MUSIC, COLUMNS_ALL, prepareWhere(""), COLUMN_ARTIST, COLUMN_ARTIST_ID);
    }
}
