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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Class for general image functions
 */
public class ImageTools {
    /**
     * Loads a bitmap and scale it down to a maximum size
     * @param bytes The image data
     * @param width The maximal width
     * @param height The maximal height
     * @return The loaded bitmap
     */
    public static Bitmap decodeByteArraySubsampled(byte[] bytes, int width, int height) {
        // Loads only the image dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        // If one side is 0 we load the original bitmap size
        if (width > 0 && height > 0) {
            // Calculate inSampleSize
            options.inSampleSize = calculateSampleSize(options, width, height);
        }

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * Given the bitmap size and View size calculate a subsampling size (powers of 2)
     * @param options The image optione with the originial size
     * @param width The maximal width
     * @param height The maximal height
     * @return The subsampling size
     */
    private static int calculateSampleSize( BitmapFactory.Options options, int width, int height) {
        int sampleSize = 1;	//Default subsampling size
        // The image is larger than required, we need to scale it down
        if (options.outHeight > height || options.outWidth > width) {
            // Gets the half resolutions
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / sampleSize) > height
                    && (halfWidth / sampleSize) > width) {
                sampleSize *= 2;
            }
        }
        return sampleSize;
    }
}
