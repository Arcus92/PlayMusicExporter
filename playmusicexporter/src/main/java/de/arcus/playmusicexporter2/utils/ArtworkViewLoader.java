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

package de.arcus.playmusicexporter2.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import de.arcus.framework.logger.Logger;
import de.arcus.playmusicexporter2.R;
import de.arcus.playmusiclib.ArtworkLoader;
import de.arcus.playmusiclib.ArtworkLoaderCallback;
import de.arcus.playmusiclib.items.ArtworkEntry;

/**
 * Class to load artworks
 */
public class ArtworkViewLoader {
    /**
     * A weak reference to the image view
     */
    private WeakReference<ImageView> mImageView;

    /**
     * Is the artwork loaded?
     */
    private boolean mIsLoading = false;

    /**
     * @return Gets whether the loader is loading an artwork
     */
    public boolean isLoading() {
        return mIsLoading;
    }

    /**
     * The artwork entry
     */
    private ArtworkEntry mArtworkEntry;

    /**
     * Path of the new image which will be loaded after the current loading is completed
     */
    private ArtworkEntry mNewArtworkEntry;

    /**
     * The default image of the image view
     */
    private int mDefaultImage;

    /**
     * Starts an image loader
     * @param artworkEntry The artwork entry
     * @param imageView The image view
     * @param defaultImage The default image in case the image could not be loaded
     */
    public static void loadImage(ArtworkEntry artworkEntry, ImageView imageView, int defaultImage) {
        // No artwork
        if (artworkEntry.getArtworkLocation() == null) {
            return;
        }

        // Checks for an old artwork loader on this image view
        ArtworkViewLoader imageViewLoader = (ArtworkViewLoader)imageView.getTag();

        if (imageViewLoader == null) {
            imageViewLoader = new ArtworkViewLoader(artworkEntry, imageView, defaultImage);

            // Save the loader in the tag
            // If someone wants to load another artwork to this view
            imageView.setTag(imageViewLoader);

            imageViewLoader.loadImage();
        } else {
            // Updates the old loader
            imageViewLoader.updateImage(artworkEntry);
        }
    }

    /**
     * Loads a image to an image view
     * @param artworkEntry The artwork entry
     * @param imageView The image view
     * @param defaultImage The default image in case the image could not be loaded
     */
    private ArtworkViewLoader(ArtworkEntry artworkEntry, ImageView imageView, int defaultImage) {
        mImageView = new WeakReference<>(imageView);
        mArtworkEntry = artworkEntry;
        mDefaultImage = defaultImage;
    }

    /**
     * Loads the image asynchronously
     */
    private void loadImage() {
        // Show the default icon while loading
        final ImageView imageViewDefault = mImageView.get();
        int maximalArtworkSize = 0;
        if (imageViewDefault != null) {
            // The maximum artwork size
            maximalArtworkSize = imageViewDefault.getContext().getResources().getDimensionPixelSize(R.dimen.music_track_artwork_loading_size);

            // Sets the bitmap in the UI thread
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // Default icon
                    imageViewDefault.setImageResource(mDefaultImage);

                }
            };
            imageViewDefault.post(runnable);
        }



        // Start loading
        mIsLoading = true;

        // Load the artwork
        ArtworkLoader.loadArtworkAsync(mArtworkEntry, maximalArtworkSize, new ArtworkLoaderCallback() {
            @Override
            public void onFinished(final Bitmap bitmap) {
                final ImageView imageView = mImageView.get();

                if (imageViewDefault != null) {
                    // Sets the bitmap in the UI thread
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            // Bitmap is valid
                            if (bitmap != null)
                                imageView.setImageBitmap(bitmap);
                            else
                                imageView.setImageResource(mDefaultImage);
                        }
                    };
                    imageView.post(runnable);
                }

                // Loading is done
                mIsLoading = false;

                // Loads the next image
                if (mNewArtworkEntry != null) {
                    mArtworkEntry = mNewArtworkEntry;
                    mNewArtworkEntry = null;

                    loadImage();
                }
            }
        });
    }

    /**
     * Loads a new artwork
     * @param artworkEntry New artwork entry
     */
    private void updateImage(ArtworkEntry artworkEntry) {
        // The same artwork; nothing to do
        if (artworkEntry.getArtworkLocation().equals(mArtworkEntry.getArtworkLocation())) {
            return;
        }

        if (mIsLoading) {
            mNewArtworkEntry = artworkEntry;
        } else {
            mArtworkEntry = artworkEntry;
            loadImage();
        }
    }
}
