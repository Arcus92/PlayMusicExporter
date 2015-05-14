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

/**
 * Created by david on 14.05.15.
 */
public class ArtworkEntry {
    /**
     * The manager
     */
    protected PlayMusicManager mPlayMusicManager;

    /**
     * Creates a data item
     * @param playMusicManager The manager
     */
    public ArtworkEntry(PlayMusicManager playMusicManager) {
        mPlayMusicManager = playMusicManager;
    }

    /**
     * The filename of the artwork
     */
    protected String mArtworkFile;

    /**
     * The url of the artwork
     */
    protected String mArtworkLocation;

    /**
     * The complete path of the artwork
     */
    protected String mArtworkPath;

    /**
     * Gets the artwork filename
     */
    public String getArtworkFile() {
        return mArtworkFile;
    }

    /**
     * @param artworkFile Sets the artwork filename
     */
    public void setArtworkFile(String artworkFile) {
        mArtworkFile = artworkFile;
    }

    /**
     * Gets the artwork url
     */
    public String getArtworkLocation() {
        return mArtworkLocation;
    }

    /**
     * Sets the artwork url
     */
    public void setArtworkLocation(String artworkLocation) {
        mArtworkLocation = artworkLocation;
    }

    /**
     * Gets the artwork path
     * @return Path to the artwork
     */
    public String getArtworkPath() {
        // Search for the artwork path
        if (mArtworkPath == null)
            mArtworkPath = mPlayMusicManager.getArtworkPath(mArtworkFile);
        return mArtworkPath;
    }
}
