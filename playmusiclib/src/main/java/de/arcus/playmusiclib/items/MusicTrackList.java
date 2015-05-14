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

import java.util.List;

import de.arcus.playmusiclib.PlayMusicManager;
import de.arcus.playmusiclib.datasources.AlbumDataSource;
import de.arcus.playmusiclib.datasources.ArtistDataSource;
import de.arcus.playmusiclib.datasources.PlaylistDataSource;

/**
 * List of {@link de.arcus.playmusiclib.items.MusicTrack MusicTracks}.
 * Eg. albums, playlists, etc...
 */
public abstract class MusicTrackList extends ArtworkEntry {
    /**
     * Creates a data item
     * @param playMusicManager The manager
     */
    public MusicTrackList(PlayMusicManager playMusicManager) {
        super(playMusicManager);
    }

    /**
     * List of all loaded tracks in this list.
     * This list will only be loaded if {@link #getMusicTrackList()} was called
     */
    protected List<MusicTrack> mMusicTrackList;


    /**
     * @return Returns the music track list
     */
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

    /**
     * Gets the id of the track list
     */
    public abstract long getMusicTrackListID();

    /**
     * Gets the type of the track list
     */
    public String getMusicTrackListType() {
        return this.getClass().getSimpleName();
    }

    /**
     * @return Returns whether the music track should also show its album art in the list view
     * Needed for playlists and artists
     */
    public abstract boolean getShowArtworkInTrack();

    /**
     * Loads a music track list by type and id
     * @param playMusicManager The PlayMusicManager
     * @param id The id of the list
     * @param type The type of the list
     * @return Returns the music track list
     */
    public static MusicTrackList deserialize(PlayMusicManager playMusicManager, long id, String type) {
        // Loads the music track list
        switch (type) {
            case "Album":
                AlbumDataSource albumDataSource = new AlbumDataSource(playMusicManager);
                albumDataSource.setOfflineOnly(false);
                return albumDataSource.getById(id);
            case "Artist":
                ArtistDataSource artistDataSource = new ArtistDataSource(playMusicManager);
                artistDataSource.setOfflineOnly(false);
                return artistDataSource.getById(id);
            case "Playlist":
                PlaylistDataSource playlistDataSource = new PlaylistDataSource(playMusicManager);
                playlistDataSource.setOfflineOnly(false);
                return playlistDataSource.getById(id);
        }

        // Failed
        return null;
    }

    @Override
    public String toString() {
        return getTitle();
    }

}
