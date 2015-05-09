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
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import de.arcus.framework.superuser.SuperUserCommand;
import de.arcus.framework.superuser.SuperUserCommandCallback;
import de.arcus.framework.superuser.SuperUserTools;
import de.arcus.playmusicexporter2.R;

/**
 * Class to load Artworks
 */
public class ImageViewLoader {
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
     * Path of the image
     */
    private String mImagePath;

    /**
     * Path of the new image which will be loaded after the current loading is completed
     */
    private String mNewImagePath;

    /**
     * The default image of the image view
     */
    private int mDefaultImage;

    /**
     * @return Gets the path of the image we want to load
     */
    public String getImagePath() {
        return mImagePath;
    }

    /**
     * Starts an image loader
     * @param imageView The image view
     * @param path The file path
     * @param defaultImage The default image in case the image could not be loaded
     */
    public static void loadImage(ImageView imageView, String path, int defaultImage) {
        // Checks for an old artwork loader on this image view
        ImageViewLoader imageViewLoader = (ImageViewLoader)imageView.getTag();

        if (path == null) path = "";

        if (imageViewLoader == null) {
            imageViewLoader = new ImageViewLoader(imageView, path, defaultImage);

            // Save the loader in the tag
            // If someone wants to load another artwork to this view
            imageView.setTag(imageViewLoader);

            imageViewLoader.loadImage();
        } else {
            // Updates the old loader
            imageViewLoader.updateImage(path);
        }
    }

    /**
     * Loads a image to an image view
     * @param imageView The image view we want to set
     * @param path The path to load
     */
    private ImageViewLoader(ImageView imageView, String path, int defaultImage) {
        mImageView = new WeakReference<>(imageView);
        mImagePath = path;
        mDefaultImage = defaultImage;
    }

    /**
     * Loads the image asynchronously
     */
    private void loadImage() {
        // Show the default icon while loading
        final ImageView imageViewDefault = mImageView.get();
        if (imageViewDefault != null) {
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

        if (!TextUtils.isEmpty(mImagePath)) {
            mIsLoading = true;

            // Be careful! Don't scroll to fast! This will spam the superuser to do list!
            SuperUserTools.fileReadToByteArrayAsync(mImagePath, new SuperUserCommandCallback() {
                @Override
                public void onFinished(SuperUserCommand command) {

                    final ImageView imageView = mImageView.get();

                    if (imageView != null) {

                        // Success
                        if (command.commandWasSuccessful()) {
                            // Binary data
                            byte[] bitmapData = command.getStandardOutputBinary();

                            // Loads the bitmap
                            try {
                                // We already want to load a new image, so we don't need to set this
                                if (mNewImagePath == null) {
                                    // Loads the bitmap
                                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);

                                    // Sets the bitmap in the UI thread
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            imageView.setImageBitmap(bitmap);
                                        }
                                    };
                                    imageView.post(runnable);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Sets the bitmap in the UI thread
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    // File not found
                                    imageView.setImageResource(R.drawable.cd_case);
                                }
                            };
                            imageView.post(runnable);
                        }
                    }

                    mIsLoading = false;

                    // Loads the next image
                    if (mNewImagePath != null) {
                        mImagePath = mNewImagePath;
                        mNewImagePath = null;

                        loadImage();
                    }
                }
            });
        }
    }

    /**
     * Loads a new artwork
     * @param path New artwork path
     */
    private void updateImage(String path) {
        // The same artwork; nothing to do
        if (path.equals(mImagePath)) {
            return;
        }

        if (mIsLoading) {
            mNewImagePath = path;
        } else {
            mImagePath = path;
            loadImage();
        }
    }
}
