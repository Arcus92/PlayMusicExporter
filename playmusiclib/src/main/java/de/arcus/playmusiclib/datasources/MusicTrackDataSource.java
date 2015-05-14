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
    private final static String COLUMN_ID = "MUSIC.Id";
    private final static String COLUMN_SIZE = "MUSIC.Size";
    private final static String COLUMN_LOCALCOPYPATH = "MUSIC.LocalCopyPath";
    private final static String COLUMN_LOCALCOPYTYPE = "MUSIC.LocalCopyType";
    private final static String COLUMN_LOCALCOPYSTORAGETYPE = "MUSIC.LocalCopyStorageType";
    private final static String COLUMN_TITLE = "MUSIC.Title";
    private final static String COLUMN_ARTIST_ID = "MUSIC.ArtistID";
    private final static String COLUMN_ARTIST = "MUSIC.Artist";
    private final static String COLUMN_ALBUM_ARTIST = "MUSIC.AlbumArtist";
    private final static String COLUMN_ALBUM = "MUSIC.Album";
    private final static String COLUMN_GENRE = "MUSIC.Genre";
    private final static String COLUMN_YEAR = "MUSIC.Year";
    private final static String COLUMN_TRACK_NUMBER = "MUSIC.TrackNumber";
    private final static String COLUMN_DISC_NUMBER = "MUSIC.DiscNumber";
    private final static String COLUMN_DURATION = "MUSIC.Duration";
    private final static String COLUMN_RATING = "MUSIC.Rating";
    private final static String COLUMN_ALBUM_ID = "MUSIC.AlbumId";
    private final static String COLUMN_CLIENT_ID = "MUSIC.ClientId";
    private final static String COLUMN_SOURCE_ID = "MUSIC.SourceId";
    private final static String COLUMN_CPDATA = "MUSIC.CpData";
    private final static String COLUMN_ARTWORK_LOCATION = "MUSIC.AlbumArtLocation";
    private final static String COLUMN_ARTWORK_FILE = "(SELECT LocalLocation FROM artwork_cache WHERE artwork_cache.RemoteLocation = AlbumArtLocation) AS ArtworkFile";

    // All columns
    private final static String[] COLUMNS_ALL = { COLUMN_ID, COLUMN_SIZE,
            COLUMN_LOCALCOPYPATH, COLUMN_LOCALCOPYTYPE, COLUMN_LOCALCOPYSTORAGETYPE, COLUMN_TITLE, COLUMN_ARTIST_ID, COLUMN_ARTIST, COLUMN_ALBUM_ARTIST,
            COLUMN_ALBUM, COLUMN_GENRE, COLUMN_YEAR, COLUMN_TRACK_NUMBER, COLUMN_DISC_NUMBER, COLUMN_DURATION, COLUMN_RATING,
            COLUMN_ALBUM_ID, COLUMN_CLIENT_ID, COLUMN_SOURCE_ID, COLUMN_ARTWORK_LOCATION, COLUMN_ARTWORK_FILE, COLUMN_CPDATA };

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
     * The name of the container (eg. a playlist or an artist)
     */
    private String mContainerName;

    /**
     * The position of the track in the container
     */
    private long mContainerPosition;

    /**
     * Creates a new data source
     * @param playMusicManager The manager
     */
    public MusicTrackDataSource(PlayMusicManager playMusicManager) {
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
    protected MusicTrack getDataObject(Cursor cursor) {
        MusicTrack instance = new MusicTrack(mPlayMusicManager);

        // Read all properties from the data row
        instance.setId(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_ID)));
        instance.setSize(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_SIZE)));
        instance.setLocalCopyPath(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_LOCALCOPYPATH)));
        instance.setLocalCopyType(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_LOCALCOPYTYPE)));
        instance.setLocalCopyStorageType(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_LOCALCOPYSTORAGETYPE)));
        instance.setTitle(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_TITLE)));
        instance.setArtistId(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_ARTIST_ID)));
        instance.setArtist(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ARTIST)));
        instance.setAlbumArtist(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ALBUM_ARTIST)));
        instance.setAlbum(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ALBUM)));
        instance.setGenre(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_GENRE)));
        instance.setYear(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_YEAR)));
        instance.setTrackNumber(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_TRACK_NUMBER)));
        instance.setDiscNumber(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_DISC_NUMBER)));
        instance.setDuration(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_DURATION)));
        instance.setRating(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_RATING)));
        instance.setAlbumId(cursor.getLong(getColumnsIndex(COLUMNS_ALL, COLUMN_ALBUM_ID)));
        instance.setClientId(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_CLIENT_ID)));
        instance.setSourceId(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_SOURCE_ID)));
        instance.setCpData(cursor.getBlob(getColumnsIndex(COLUMNS_ALL, COLUMN_CPDATA)));
        instance.setArtworkLocation(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ARTWORK_LOCATION)));
        instance.setArtworkFile(cursor.getString(getColumnsIndex(COLUMNS_ALL, COLUMN_ARTWORK_FILE)));

        // Sets the container information
        mContainerPosition++;
        instance.setContainerName(mContainerName);
        instance.setContainerPosition(mContainerPosition);

        return instance;
    }

    /**
     * Loads a track by Id
     * @param id The track id
     * @return Returns the music track or null
     */
    public MusicTrack getById(long id) {
        mContainerName = null;
        mContainerPosition = 0;

        return getItem(TABLE_MUSIC, COLUMNS_ALL, prepareWhere("Id = " + id));
    }

    /**
     * Gets a list of tracks by an album
     * @param album The album
     * @return Returns the track list
     */
    public List<MusicTrack> getByAlbum(Album album) {
        mContainerName = null;
        mContainerPosition = 0;

        return getItems(TABLE_MUSIC, COLUMNS_ALL, prepareWhere("AlbumId = " + album.getAlbumId()), COLUMN_DISC_NUMBER + ", " + COLUMN_TRACK_NUMBER);
    }

    /**
     * Gets a list of tracks by a playlist
     * @param playlist The playlist
     * @return Returns the track list
     */
    public List<MusicTrack> getByPlaylist(Playlist playlist) {
        mContainerName = playlist.getTitle();
        mContainerPosition = 0;

        return getItems(TABLE_MUSIC_PLAYLIST, COLUMNS_ALL, prepareWhere("ListId = " + playlist.getId()), "LISTITEMS.ID");
    }

    /**
     * Gets a list of tracks by an artist
     * @param artist The artist
     * @return Returns the track list
     */
    public List<MusicTrack> getByArtist(Artist artist) {
        mContainerName = artist.getTitle();
        mContainerPosition = 0;

        return getItems(TABLE_MUSIC, COLUMNS_ALL, prepareWhere(COLUMN_ARTIST_ID + " = " + artist.getArtistId()), COLUMN_ARTIST);
    }
}
