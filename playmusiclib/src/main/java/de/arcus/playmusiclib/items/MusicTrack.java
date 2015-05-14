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

import android.text.TextUtils;

import de.arcus.playmusiclib.PlayMusicManager;

/**
 * A single music track from Play Music
 */
public class MusicTrack extends ArtworkEntry {
    // Variables
    private long mId, mSize, mTrackNumber, mDiscNumber, mAlbumId, mArtistId, mLocalCopyType, mLocalCopyStorageType, mDuration, mRating;
    private String mTitle, mArtist, mAlbum, mAlbumArtist, mLocalCopyPath, mGenre, mYear, mClientId, mSourceId;
    private byte[] mCpData;

    private String mSourceFile;

    /**
     * Creates a data item
     * @param playMusicManager The manager
     */
    public MusicTrack(PlayMusicManager playMusicManager) {
        super(playMusicManager);
    }

    /**
     * @return Gets the track is
     */
    public long getId() {
        return mId;
    }

    /**
     * @param id Sets the track id
     */
    public void setId(long id) {
        this.mId = id;
    }

    /**
     * @return Gets the file size
     */
    public long getSize() {
        return mSize;
    }

    /**
     * @param size Sets the file size
     */
    public void setSize(long size) {
        this.mSize = size;
    }

    /**
     * @return Get the track number in the album
     */
    public long getTrackNumber() {
        return mTrackNumber;
    }

    /**
     * @param trackNumber Sets the track number in the album
     */
    public void setTrackNumber(long trackNumber) {
        this.mTrackNumber = trackNumber;
    }

    /**
     * @return Gets the disc number in the album
     */
    public long getDiscNumber() {
        return mDiscNumber;
    }

    /**
     * @param discNumber Sets the disc number in the album
     */
    public void setDiscNumber(long discNumber) {
        this.mDiscNumber = discNumber;
    }

    /**
     * @return Gets the duration of the track
     */
    public long getDuration() {
        return mDuration;
    }

    /**
     * @param duration Sets the duration of the track
     */
    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    /**
     * @return Gets the rating of the track
     */
    public long getRating() {
        return mRating;
    }

    /**
     * @param rating Sets the rating of the track
     */
    public void setRating(long rating) {
        this.mRating = rating;
    }

    /**
     * @return Gets the local copy type
     */
    public long getLocalCopyType() {
        return mLocalCopyType;
    }

    /**
     * @param localCopyType Sets the local copy type
     */
    public void setLocalCopyType(long localCopyType) {
        this.mLocalCopyType = localCopyType;
    }

    /**
     * @return Gets the local copy storage type
     */
    public long getLocalCopyStorageType() {
        return mLocalCopyStorageType;
    }

    /**
     * @param localCopyStorageType Sets the local copy storage type
     */
    public void setLocalCopyStorageType(long localCopyStorageType) {
        this.mLocalCopyStorageType = localCopyStorageType;
    }

    /**
     * @return Gets the release year of the song
     */
    public String getYear() {
        return mYear;
    }

    /**
     * @param year Sets the release year of the song
     */
    public void setYear(String year) {
        this.mYear = year;
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
     * @return Gets the track title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @param title Sets the track title
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * @return Gets the artist is
     */
    public long getArtistId() {
        return mArtistId;
    }

    /**
     * @param artistId Sets the artist id
     */
    public void setArtistId(long artistId) {
        this.mArtistId = artistId;
    }

    /**
     * @return Gets the artist
     */
    public String getArtist() {
        return mArtist;
    }

    /**
     * @param artist Sets the artist
     */
    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    /**
     * @return Gets the album artist
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
     * @return Gets the album title
     */
    public String getAlbum() {
        return mAlbum;
    }

    /**
     * @param album Sets the album title
     */
    public void setAlbum(String album) {
        this.mAlbum = album;
    }

    /**
     * @return Gets the local copy path
     */
    public String getLocalCopyPath() {
        return mLocalCopyPath;
    }

    /**
     * @param localCopyPath Sets the local copy path
     */
    public void setLocalCopyPath(String localCopyPath) {
        this.mLocalCopyPath = localCopyPath;
    }

    /**
     * @return Gets the genre
     */
    public String getGenre() {
        return mGenre;
    }

    /**
     * @param genre Sets the genre
     */
    public void setGenre(String genre) {
        this.mGenre = genre;
    }

    /**
     * @return Gets the client id
     */
    public String getClientId() {
        return mClientId;
    }

    /**
     * @param clientId Sets the client id
     */
    public void setClientId(String clientId) {
        this.mClientId = clientId;
    }

    /**
     * @return Gets the source id
     */
    public String getSourceId() {
        return mSourceId;
    }

    /**
     * @param sourceId Sets the source id
     */
    public void setSourceId(String sourceId) {
        this.mSourceId = sourceId;
    }

    /**
     * @return Gets the AllAccess decryption key
     */
    public byte[] getCpData() {
        return mCpData;
    }

    /**
     * @param cpData Sets the AllAccess decryption key
     */
    public void setCpData(byte[] cpData) {
        this.mCpData = cpData;
    }
    /**
     * The name of the container (eg. a playlist or an artist)
     */
    private String mContainerName;

    /**
     * @return Gets the name of the container
     */
    public String getContainerName() {
        return mContainerName;
    }

    /**
     * @param containerName Sets the name of the container
     */
    public void setContainerName(String containerName) {
        mContainerName = containerName;
    }

    /**
     * The position of the track in the container
     */
    private long mContainerPosition;

    /**
     * @return Gets the position in the container
     */
    public long getContainerPosition() {
        return mContainerPosition;
    }

    /**
     * @param containerPosition Sets the position in the container
     */
    public void setContainerPosition(long containerPosition) {
        mContainerPosition = containerPosition;
    }

    /**
     * @return Returns the source file path
     */
    public String getSourceFile() {
        // Search for the source file
        if (mSourceFile == null)
            mSourceFile = mPlayMusicManager.getMusicFile(mLocalCopyPath);
        return mSourceFile;
    }

    /**
     * @return Returns if this file is encoded from AllAccess
     */
    public boolean isEncoded() {
        return (mCpData != null);
    }

    /**
     * @return Returns true if this track is offline available
     */
    public boolean isOfflineAvailable() {
        return !TextUtils.isEmpty(mLocalCopyPath);
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
