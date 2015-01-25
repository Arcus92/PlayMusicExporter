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

package de.arcus.framework.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

/**
 * A media scanner which adds files to the android media system
 */
public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    /**
     * Connection to the media scanner
     */
    private MediaScannerConnection mMediaScanner;

    /**
     * File to scan
     */
    private String mFile;

    /**
     * Adds a file to the android media system
     * @param context The context of the app
     * @param string Path to the file
     */
    public MediaScanner(Context context, String string) {
        mFile = string;

        // Connect the media scanner
        mMediaScanner = new MediaScannerConnection(context, this);
        mMediaScanner.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        // Is connected
        mMediaScanner.scanFile(mFile, null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        // Done; close the connection
        mMediaScanner.disconnect();
    }

}