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
import de.arcus.playmusiclib.R;
import de.arcus.playmusiclib.datasources.MusicTrackDataSource;

/**
 * A playlist
 */
public class Playlist extends MusicTrackList {
    // Variables
    private long mId, mListType;
    private String mName;
    private String mOwnerName;

    public final static long TYPE_USER = 0;
    public final static long TYPE_QUEUE = 10;
    public final static long TYPE_RADIO = 50;
    public final static long TYPE_PUBLIC = 71;

    /**
     * Creates a data item
     * @param playMusicManager The manager
     */
    public Playlist(PlayMusicManager playMusicManager) {
        super(playMusicManager);
    }

    /**
     * @return Gets the playlist id
     */
    public long getId() {
        return mId;
    }

    /**
     * @param id Sets the playlist id
     */
    public void setId(long id) {
        this.mId = id;
    }

    /**
     * @return Gets the playlist type
     */
    long getListType() {
        return mListType;
    }

    /**
     * @param listType Sets the playlist type
     */
    public void setListType(long listType) {
        this.mListType = listType;
    }

    /**
     * @return Gets the playlist name
     */
    public String getName() {
        return mName;
    }

    /**
     * @param name Sets the name of the playlist
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * @return Gets the name of the playlist owner
     */
    public String getOwnerName() {
        return mOwnerName;
    }

    /**
     * @param ownerName Sets the name of the playlist owner
     */
    public void setOwnerName(String ownerName) {
        this.mOwnerName = ownerName;
    }


    @Override
    /**
     * Gets the playlist name
     */
    public String getTitle() {
        // Play queue
        if (getListType() == TYPE_QUEUE) {
            return mPlayMusicManager.getContext().getString(R.string.music_playlist_queue);
        }

        return getName();
    }

    @Override
    /**
     * Gets the list owner name
     */
    public String getDescription() {
        // Play queue
        if (getListType() == TYPE_QUEUE) {
            return mPlayMusicManager.getContext().getString(R.string.music_playlist_queue_description);
        }

        // Radio
        if (getListType() == TYPE_RADIO) {
            return mPlayMusicManager.getContext().getString(R.string.music_playlist_radio);
        }

        return getOwnerName();
    }

    @Override
    /**
     * Loads all tracks from this playlist
     */
    protected void fetchTrackList() {
        // Music track data source
        MusicTrackDataSource musicTrackDataSource = new MusicTrackDataSource(mPlayMusicManager);

        // Load the track list
        mMusicTrackList = musicTrackDataSource.getByPlaylist(this);
    }
}
