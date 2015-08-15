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

package de.arcus.playmusicexporter2.items;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import de.arcus.playmusicexporter2.services.ExportService;

/**
 * The selected track
 */
public class SelectedTrack {
    /**
     * Type of the track
     */
    private long mId;

    /**
     * The uri of the track
     */
    private Uri mUri;

    /**
     * The path of the track
     */
    private String mPath;

    public SelectedTrack(long id) {
        mId = id;
    }

    public SelectedTrack(long id, Uri uri, String path) {
        mId = id;
        mUri = uri;
        mPath = path;
    }

    /**
     * Adds the track to the export list
     */
    public void export(Context context) {
        Intent intent = new Intent(context, ExportService.class);

        // Puts the export parameter
        intent.putExtra(ExportService.ARG_EXPORT_TRACK_ID, mId);
        intent.putExtra(ExportService.ARG_EXPORT_URI, mUri.toString());
        intent.putExtra(ExportService.ARG_EXPORT_PATH, mPath);

        // Starts the service
        context.startService(intent);
    }

    @Override
    public boolean equals(Object o) {
        // Compares two selected tracks
        if (o instanceof SelectedTrack) {
            SelectedTrack other = (SelectedTrack)o;

            return mId == other.mId;
        }

        return super.equals(o);
    }
}
