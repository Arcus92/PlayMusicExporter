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

import android.content.Context;

import java.util.List;

import de.arcus.playmusiclib.PlayMusicManager;

/**
 * List of {@link de.arcus.playmusiclib.items.MusicTrack MusicTracks}.
 * Eg. albums, playlists, etc...
 */
public abstract class MusicList {
    /**
     * The manager
     */
    protected PlayMusicManager mPlayMusicManager;

    /**
     * Creates a data item
     * @param playMusicManager The manager
     */
    public MusicList(PlayMusicManager playMusicManager) {
        mPlayMusicManager = playMusicManager;
    }

    /**
     * List of all loaded tracks in this list.
     * This list will only be loaded if {@link #getMusicTrackList()} was called
     */
    protected List<MusicTrack> mMusicTrackList;

    public List<MusicTrack> getMusicTrackList() {
        // List is requested for the fist time
        if (mMusicTrackList == null)
            fetchTrackList();

        // Return list
        return mMusicTrackList;
    }

    /**
     * Loads all tracks from this list.
     * Must be overwritten in all extended classes
     */
    protected abstract void fetchTrackList();

    /**
     * Gets the artwork path
     * @return Path to the artwork
     */
    public abstract String getArtworkPath();

    /**
     * Gets the title of the list
     * @return Title
     */
    public abstract String getTitle();

    /**
     * Gets the description of the list.
     * Eg. the artist of the album
     * @return Description
     */
    public abstract String getDescription();

    @Override
    public String toString() {
        return getTitle();
    }

}
