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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.arcus.playmusicexporter2.R;
import de.arcus.playmusicexporter2.items.SelectedTrack;
import de.arcus.playmusicexporter2.utils.ImageViewLoader;
import de.arcus.playmusiclib.items.MusicTrack;

/**
 * Adapter for the music tracks
 */
public class MusicTrackAdapter extends ArrayAdapter<MusicTrack> {
    /**
     * The context of the app
     */
    private Context mContext;

    /**
     * If this is set the music track shows it artwork instead of the track number.
     * Used for playlists.
     */
    private boolean mShowArtworks = true;

    public boolean getShowArtwok() {
        return mShowArtworks;
    }

    public void setShowArtworks(boolean showArtworks) {
        mShowArtworks = showArtworks;
    }

    /**
     * Create a new track adapter
     * @param context The app context
     */
    public MusicTrackAdapter(Context context) {
        super(context, R.layout.adapter_music_track);
        mContext = context;
    }

    public void setList(List<MusicTrack> musicTracks) {
        // Clear all items
        clear();

        // Add the new items
        for(MusicTrack musicTrack : musicTracks) {
            add(musicTrack);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // The track
        MusicTrack musicTrack = getItem(position);

        View view = convertView;

        // Inflates a view
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_music_track, parent, false);
        }

        TextView textView;

        // Sets the track number
        textView = (TextView)view.findViewById(R.id.text_music_track_number);
        long trackPosition = musicTrack.getTrackNumber();

        if (musicTrack.getContainerName() != null)
            trackPosition = musicTrack.getContainerPosition();

        if (trackPosition > 0)
            textView.setText("" + trackPosition);
        else
            textView.setText("");

        textView.setTextColor(mContext.getResources().getColor(musicTrack.isOfflineAvailable() ? R.color.text_music_number : R.color.text_music_disable_number));

        // Sets the disc number
        textView = (TextView)view.findViewById(R.id.text_music_track_disc_number);
        textView.setText("CD " + musicTrack.getDiscNumber());
        // Don't show the disc number if this is a playlist or artist list
        if (musicTrack.getDiscNumber() > 0 && TextUtils.isEmpty(musicTrack.getContainerName()))
            textView.setVisibility(View.VISIBLE);
        else
            textView.setVisibility(View.GONE);

        textView.setTextColor(mContext.getResources().getColor(musicTrack.isOfflineAvailable() ? R.color.text_music_disc_number : R.color.text_music_disable_disc_number));


        if (mShowArtworks) {
            view.findViewById(R.id.relative_layout_number).setVisibility(View.GONE);
            view.findViewById(R.id.relative_layout_artwork).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.relative_layout_number).setVisibility(View.VISIBLE);
            view.findViewById(R.id.relative_layout_artwork).setVisibility(View.GONE);
        }

        // Shows the artwork
        if (mShowArtworks) {
            ImageView imageView = (ImageView) view.findViewById(R.id.image_music_track_artwork);

            String artworkPath = musicTrack.getArtworkPath();

            ImageViewLoader.loadImage(imageView, artworkPath, R.drawable.cd_case);
        }

        // Sets the title
        textView = (TextView)view.findViewById(R.id.text_music_track_title);
        textView.setText(musicTrack.getTitle());
        textView.setTextColor(mContext.getResources().getColor(musicTrack.isOfflineAvailable() ? R.color.text_music_title : R.color.text_music_disable_title));

        // Sets the artist
        textView = (TextView)view.findViewById(R.id.text_music_track_artist);
        textView.setText(musicTrack.getArtist());
        textView.setTextColor(mContext.getResources().getColor(musicTrack.isOfflineAvailable() ? R.color.text_music_description : R.color.text_music_disable_description));

        // Track is available?
        view.setEnabled(musicTrack.isOfflineAvailable());

        // Selected state
        SelectedTrack.getSelectionList().initView(new SelectedTrack(musicTrack.getId()), view);

        return view;
    }


}
