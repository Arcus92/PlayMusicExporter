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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import de.arcus.playmusicexporter2.R;
import de.arcus.playmusiclib.items.MusicTrack;

/**
 * Adapter for the music tracks
 */
public class MusicTrackAdapter implements ListAdapter {
    /**
     * The context of the app
     */
    private Context mContext;

    /**
     * The list
     */
    private List<MusicTrack> mList;

    /**
     * @param list Sets a new list
     */
    public void setList(List<MusicTrack> list) {
        mList = list;
    }

    /**
     * @return Gets the list
     */
    public List<MusicTrack> getList() {
        return mList;
    }

    /**
     * Create a new track adapter
     * @param context The app context
     */
    public MusicTrackAdapter(Context context) {
        mContext = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
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
        // The track
        MusicTrack musicTrack = mList.get(position);

        View view = convertView;

        // Inflates a view
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_music_track, parent, false);
        }

        TextView textView;

        // Set the track number
        textView = (TextView)view.findViewById(R.id.text_music_track_number);
        if (musicTrack.getTrackNumber() > 0)
            textView.setText("" + musicTrack.getTrackNumber());
        else
            textView.setText("");

        // Set the title
        textView = (TextView)view.findViewById(R.id.text_music_track_title);
        textView.setText(musicTrack.getTitle());

        // Set the artist
        textView = (TextView)view.findViewById(R.id.text_music_track_artist);
        textView.setText(musicTrack.getArtist());

        view.setEnabled(musicTrack.isOfflineAvailable());

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
}
