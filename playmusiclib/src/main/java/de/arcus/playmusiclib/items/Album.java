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

package de.arcus.playmusiclib.items;

import de.arcus.playmusiclib.PlayMusicManager;
import de.arcus.playmusiclib.datasources.MusicTrackDataSource;

/**
 * An Album
 */
public class Album extends MusicList {
    // Variables
    private long mAlbumId;
    private String mAlbum, mAlbumArtist, mArtworkFile;

    private String mArtworkPath;

    /**
     * Creates a data item
     * @param playMusicManager The manager
     */
    public Album(PlayMusicManager playMusicManager) {
        super(playMusicManager);
    }

    /**
     * @return Gets the album id
     */
    public long getAlbumId() {
        return mAlbumId;
    }

    /**
     * @param albumId Sets the album id
     */
    public void setAlbumId(long albumId) {
        this.mAlbumId = albumId;
    }

    /**
     * @return Gets the name of the album
     */
    public String getAlbum() {
        return mAlbum;
    }

    /**
     * @param album Sets the name of the album
     */
    public void setAlbum(String album) {
        this.mAlbum = album;
    }

    /**
     * @return gets the album artist
     */
    public String getAlbumArtist() {
        return mAlbumArtist;
    }

    /**
     * @param albumArtist Sets the album artist
     */
    public void setAlbumArtist(String albumArtist) {
        this.mAlbumArtist = albumArtist;
    }

    /**
     * Gets the artwork file name
     */
    public String getArtworkFile() {
        return mArtworkFile;
    }

    /**
     * @param artworkFile Sets the artwork file name
     */
    public void setArtworkFile(String artworkFile) {
        mArtworkFile = artworkFile;
    }

    @Override
    /**
     * Gets the album name
     */
    public String getTitle() {
        return getAlbum();
    }

    @Override
    /**
     * Gets the album artist
     */
    public String getDescription() {
        return getAlbumArtist();
    }

    @Override
    /**
     * Loads all tracks from this album
     */
    protected void fetchTrackList() {
        // Music track data source
        MusicTrackDataSource musicTrackDataSource = new MusicTrackDataSource(mPlayMusicManager);

        // Load the track list
        mMusicTrackList = musicTrackDataSource.getByAlbum(this);
    }

    @Override
    /**
     * return Gets the full path to the artwork
     */
    public String getArtworkPath() {
        // Search for the artwork path
        if (mArtworkPath == null)
            mArtworkPath = mPlayMusicManager.getArtworkPath(mArtworkFile);
        return mArtworkPath;
    }
}
