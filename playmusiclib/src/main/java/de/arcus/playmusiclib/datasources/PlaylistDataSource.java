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
import de.arcus.playmusiclib.items.Playlist;

/**
 * Data source for playlists
 */
public class PlaylistDataSource extends DataSource<Playlist> {
    // Tables
    private final static String TABLE_LIST = "LISTS";

    // All fields
    private final static String COLUMN_ID = "LISTS.Id";
    private final static String COLUMN_NAME = "LISTS.Name";
    private final static String COLUMN_LIST_TYPE = "LISTS.ListType";
    private final static String COLUMN_OWNER_NAME = "LISTS.OwnerName";
    private final static String COLUMN_ARTWORK_FILE = "(SELECT ARTWORK_CACHE.LocalLocation FROM LISTITEMS LEFT JOIN MUSIC ON MUSIC.Id = LISTITEMS.MusicId LEFT JOIN ARTWORK_CACHE ON ARTWORK_CACHE.RemoteLocation = MUSIC.AlbumArtLocation WHERE LISTITEMS.ListId = LISTS.Id AND ARTWORK_CACHE.LocalLocation IS NOT NULL LIMIT 1) AS ArtworkFile";

    // All columns
    private final static String[] COLUMNS_ALL = { COLUMN_ID, COLUMN_NAME,
            COLUMN_LIST_TYPE, COLUMN_OWNER_NAME, COLUMN_ARTWORK_FILE};


    /**
     * If this is set the data source will only load offline tracks
     */
    private boolean mOfflineOnly; // TODO: Offline only has no effects on the playlist data sources

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
    public PlaylistDataSource(PlayMusicManager playMusicManager) {
        super(playMusicManager);

        // Load global settings
        //setOfflineOnly(playMusicManager.getOfflineOnly());
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

            where = combineWhere(where, "(" + COLUMN_NAME + " LIKE " + searchKey + ")");
        }

        return where;
    }

    @Override
    /**
     * Gets the data object from a data row
     * @param cursor Data row
     * @return Data object
     */
    protected Playlist getDataObject(Cursor cursor) {
        Playlist instance = new Playlist(mPlayMusicManager);

        // Read all properties from the data row
        instance.setId(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_ID)));
        instance.setName(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_NAME)));
        instance.setListType(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_LIST_TYPE)));
        instance.setOwnerName(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_OWNER_NAME)));
        instance.setArtworkFile(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ARTWORK_FILE)));

        return instance;
    }

    /**
     * Loads a playlist by Id
     * @param id The playlist id
     * @return Returns the playlist or null
     */
    public Playlist getById(long id) {
        return getItem(TABLE_LIST, COLUMNS_ALL, prepareWhere(COLUMN_ID + " = " + id));
    }

    /**
     * Gets a list of all playlists
     * @return Returns all playlists
     */
    public List<Playlist> getAll() {
        return getItems(TABLE_LIST, COLUMNS_ALL, prepareWhere(COLUMN_LIST_TYPE + " != " + Playlist.TYPE_QUEUE), COLUMN_LIST_TYPE + " DESC, " + COLUMN_NAME);
    }
}
