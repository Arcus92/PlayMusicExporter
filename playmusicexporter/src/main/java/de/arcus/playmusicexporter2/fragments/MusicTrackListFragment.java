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

package de.arcus.playmusicexporter2.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.arcus.playmusicexporter2.R;
import de.arcus.playmusicexporter2.activities.MusicContainerListActivity;
import de.arcus.playmusicexporter2.activities.MusicTrackListActivity;
import de.arcus.playmusicexporter2.adapter.MusicTrackListAdapter;
import de.arcus.playmusicexporter2.items.SelectedTrack;
import de.arcus.playmusicexporter2.items.SelectedTrackList;
import de.arcus.playmusicexporter2.utils.ArtworkViewLoader;
import de.arcus.playmusicexporter2.utils.MusicPathBuilder;
import de.arcus.playmusiclib.PlayMusicManager;
import de.arcus.playmusiclib.items.MusicTrack;
import de.arcus.playmusiclib.items.MusicTrackList;


/**
 * A fragment representing a single Track detail screen.
 * This fragment is either contained in a {@link MusicContainerListActivity}
 * in two-pane mode (on tablets) or a {@link MusicTrackListActivity}
 * on handsets.
 */
public class MusicTrackListFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MUSIC_TRACK_LIST_ID = "music_track_list_id";
    public static final String ARG_MUSIC_TRACK_LIST_TYPE = "music_track_list_type";

    /**
     * The track list
     */
    private MusicTrackList mMusicTrackList;

    /**
     * The list view
     */
    private ListView mListView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MusicTrackListFragment() {
    }

    /**
     * Update the list view
     */
    public void updateListView() {
        if (mListView != null)
            mListView.invalidateViews();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MUSIC_TRACK_LIST_ID)
         && getArguments().containsKey(ARG_MUSIC_TRACK_LIST_TYPE)) {

            // Loads the track list
            long id = getArguments().getLong(ARG_MUSIC_TRACK_LIST_ID);
            String type = getArguments().getString(ARG_MUSIC_TRACK_LIST_TYPE);

            PlayMusicManager playMusicManager = PlayMusicManager.getInstance();

            if (playMusicManager != null) {
                mMusicTrackList = MusicTrackList.deserialize(playMusicManager, id, type);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mMusicTrackList != null) {
            mListView = (ListView)rootView.findViewById(R.id.list_music_track);
            final MusicTrackListAdapter musicTrackAdapter = new MusicTrackListAdapter(getActivity());

            musicTrackAdapter.setShowArtworks(mMusicTrackList.getShowArtworkInTrack());

            View headerView = inflater.inflate(R.layout.header_music_track_list, mListView, false);
            headerView.setEnabled(false);

            TextView textView;
            ImageView imageView;

            // Sets the artwork image
            imageView = (ImageView)headerView.findViewById(R.id.image_music_track_artwork);

            // Loads the artwork
            ArtworkViewLoader.loadImage(mMusicTrackList, imageView, R.drawable.cd_case);

            // Sets the title
            textView = (TextView)headerView.findViewById(R.id.text_music_track_list_title);
            textView.setText(mMusicTrackList.getTitle());

            // Sets the description
            textView = (TextView)headerView.findViewById(R.id.text_music_track_list_description);
            textView.setText(mMusicTrackList.getDescription());

            mListView.addHeaderView(headerView);

            musicTrackAdapter.setList(mMusicTrackList.getMusicTrackList());

            mListView.setAdapter(musicTrackAdapter);

            // Click on one list item
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // The header is not clicked
                    if (position > 0) {
                        // We need to subtract the header view
                        position -= 1;

                        // Gets the selected track
                        MusicTrack musicTrack = musicTrackAdapter.getItem(position);

                        // Track is available
                        if (musicTrack.isOfflineAvailable()) {

                            // Default structure
                            String pathStructure = "{album-artist}/{album}/{disc=CD $}/{no=$$.} {title}.mp3";

                            // Track is exported from a group (playlist or artist)
                            if (!TextUtils.isEmpty(musicTrack.getContainerName()))
                            {
                                pathStructure = "{group}/{group-no=$$.} {title}.mp3";
                            }

                            // Build the path
                            String path = MusicPathBuilder.Build(musicTrack, pathStructure);

                            // Path to the public music folder
                            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/" + path;

                            // Toggles the music track
                            SelectedTrackList.getInstance().toggle(new SelectedTrack(musicTrack.getId(), path), view);
                        }
                    }
                }
            });
        }

        return rootView;
    }
}
