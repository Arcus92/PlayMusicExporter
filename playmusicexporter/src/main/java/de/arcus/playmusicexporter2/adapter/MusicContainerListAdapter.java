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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.arcus.playmusicexporter2.R;
import de.arcus.playmusicexporter2.utils.ArtworkViewLoader;
import de.arcus.playmusiclib.items.MusicTrackList;

/**
 * Adapter for the music track lists
 */
public class MusicContainerListAdapter extends ArrayAdapter<MusicTrackList> {
    /**
     * The context of the app
     */
    private Context mContext;

    /**
     * Create a new track list adapter
     * @param context The app context
     */
    public MusicContainerListAdapter(Context context) {
        super(context, R.layout.adapter_music_track);
        mContext = context;
    }

    public void setList(List<MusicTrackList> musicTrackLists) {
        // Clear all items
        clear();

        // Add the new items
        for(MusicTrackList musicTrackList : musicTrackLists) {
            add(musicTrackList);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // The track list
        MusicTrackList musicTrackList = getItem(position);

        View view = convertView;

        // Inflates a view
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_music_track_list, parent, false);
        }

        TextView textView;
        ImageView imageView;

        // Set the title
        textView = (TextView)view.findViewById(R.id.text_music_track_list_title);
        textView.setText(musicTrackList.getTitle());

        // Set the description
        textView = (TextView)view.findViewById(R.id.text_music_track_list_description);
        textView.setText(musicTrackList.getDescription());

        // Final for the callback
        imageView = (ImageView)view.findViewById(R.id.image_music_track_artwork);

        // Gets the artwork
        String artworkPath = musicTrackList.getArtworkPath();
        String artworkLocation = musicTrackList.getArtworkLocation();

        // Loads the artwork
        ArtworkViewLoader.loadImage(imageView, artworkPath, artworkLocation, R.drawable.cd_case);

        return view;
    }
}
