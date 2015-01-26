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

package de.arcus.playmusicexporter2.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import de.arcus.framework.superuser.SuperUserCommand;
import de.arcus.framework.superuser.SuperUserCommandCallback;
import de.arcus.framework.superuser.SuperUserTools;
import de.arcus.playmusicexporter2.R;
import de.arcus.playmusiclib.items.MusicTrackList;

/**
 * Adapter for the music track lists
 */
public class MusicTrackListAdapter implements ListAdapter {
    /**
     * The context of the app
     */
    private Context mContext;

    /**
     * The list
     */
    private List<MusicTrackList> mList;

    /**
     * @param list Sets a new list
     */
    public void setList(List<MusicTrackList> list) {
        mList = list;
    }

    /**
     * @return Gets the list
     */
    public List<MusicTrackList> getList() {
        return mList;
    }

    /**
     * Create a new track list adapter
     * @param context The app context
     */
    public MusicTrackListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // We don't have ids
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        // We don't have ids
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // The track list
        MusicTrackList musicTrackList = mList.get(position);

        View view = convertView;

        // Inflates a view
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_music_track_list, parent, false);
        }

        TextView textView;

        // Set the title
        textView = (TextView)view.findViewById(R.id.text_music_track_list_title);
        textView.setText(musicTrackList.getTitle());

        // Set the description
        textView = (TextView)view.findViewById(R.id.text_music_track_list_description);
        textView.setText(musicTrackList.getDescription());

        // Final for the callback
        final ImageView imageViewArtwork = (ImageView)view.findViewById(R.id.image_music_track_artwork);

        // Default icon
        imageViewArtwork.setImageResource(R.drawable.cd_case);

        // Gets the artwork
        String artworkPath = musicTrackList.getArtworkPath();

        if (artworkPath != null) {
            // Be careful! Don't scroll to fast! This will spam the superuser to do list!
            SuperUserTools.fileReadToByteArrayAsync(artworkPath, new SuperUserCommandCallback() {
                @Override
                public void onFinished(SuperUserCommand command) {
                    // Success
                    if (command.commandWasSuccessful()) {
                        // Binary data
                        byte[] bitmapData = command.getStandardOutputBinary();

                        // Load the bitmap
                        try {
                            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);

                            // The the bitmap in the UI thread
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    imageViewArtwork.setImageBitmap(bitmap);
                                }
                            };
                            imageViewArtwork.post(runnable);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // File not found
                        imageViewArtwork.setImageResource(R.drawable.cd_case);
                    }
                }
            });

        }



        return view;
    }

    @Override
    public int getItemViewType(int position) {
        // We don't have view types
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        // We don't have view types
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return (mList == null || mList.isEmpty());
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
