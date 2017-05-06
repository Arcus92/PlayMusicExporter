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

package de.arcus.playmusiclib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;

import java.net.URL;

import de.arcus.framework.logger.Logger;
import de.arcus.framework.superuser.SuperUserTools;
import de.arcus.framework.utils.ImageTools;
import de.arcus.playmusiclib.items.ArtworkEntry;

/**
 * This class contains methods to load the artworks from the Play Music cache or from the internet
 */
public class ArtworkLoader {
    /**
     * Loads an artwork
     * @param artworkEntry The artwork entry
     * @param artworkSize The size
     * @return The loaded bitmap or null if it failed
     */
    public static Bitmap loadArtwork(ArtworkEntry artworkEntry, int artworkSize) {
        Bitmap bitmap = null;
        String artworkPath = artworkEntry.getArtworkPath();
        String artworkUrl = artworkEntry.getArtworkLocation();

        // The local path is set
        if (!TextUtils.isEmpty(artworkPath)) {
            // Tries to load the bitmap
            try {
                // Load the local file
                byte[] bitmapData = SuperUserTools.fileReadToByteArray(artworkPath);

                // DS 2017-05-06: Added null check
                if (bitmapData != null) {
                    bitmap = ImageTools.decodeByteArraySubsampled(bitmapData, artworkSize, artworkSize);
                }
            } catch (Exception e) {
                // Error
                Logger.getInstance().logError("LoadArtwork", e.toString());
            }
        }

        // The bitmap is not loaded
        if (bitmap == null) {
            // Url is set
            if (!TextUtils.isEmpty(artworkUrl)) {
                // Tries to load the artwork via internet
                try {
                    URL url = new URL(artworkUrl);
                    bitmap = BitmapFactory.decodeStream(url.openStream());
                } catch (Exception e) {
                    // Error
                    Logger.getInstance().logError("LoadArtwork", e.toString());
                }
            }
        }

        return bitmap;
    }


    /**
     * Loads an artwork
     * @param artworkEntry The artwork entry
     * @param artworkSize The size
     * @param callback The callback
     */
    public static void loadArtworkAsync(final ArtworkEntry artworkEntry, final int artworkSize, final ArtworkLoaderCallback callback) {
        // The main handler
        final Handler handler = new Handler();

        // Create a thread to run the artwork loader
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = loadArtwork(artworkEntry, artworkSize);

                // Call the callback event in the main thread
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(bitmap);
                    }
                });
            }
        }).start();
    }
}
