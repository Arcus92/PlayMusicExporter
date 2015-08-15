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

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.arcus.playmusicexporter2.R;
import de.arcus.playmusicexporter2.activities.MusicContainerListActivity;
import de.arcus.playmusicexporter2.activities.MusicTrackListActivity;
import de.arcus.playmusicexporter2.adapter.MusicTrackListAdapter;
import de.arcus.playmusicexporter2.items.SelectedTrack;
import de.arcus.playmusicexporter2.items.SelectedTrackList;
import de.arcus.playmusicexporter2.settings.PlayMusicExporterSettings;
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

    private FloatingActionButton mFloatingButtonExport;

    private MusicTrackListAdapter mMusicTrackAdapter;

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

        updateFloatingButton();
    }

    /**
     * Update the floating button
     */
    public void updateFloatingButton() {
        if (SelectedTrackList.getInstance().getSelectedItems().size() > 0) {
            mFloatingButtonExport.show();
        } else {
            mFloatingButtonExport.hide();
        }
    }

    /**
     * Select all items
     */
    public void selectAll() {
        // Select all tracks
        for(int i = 0; i < mMusicTrackAdapter.getCount(); i++ ) {
            MusicTrack musicTrack = mMusicTrackAdapter.getItem(i);

            selectTrack(musicTrack, null, TrackSelectionState.Select);
        }

        updateListView();
    }

    /**
     * Deselect all items
     */
    public void deselectAll() {
        // Deselect all tracks
        for(int i = 0; i < mMusicTrackAdapter.getCount(); i++ ) {
            MusicTrack musicTrack = mMusicTrackAdapter.getItem(i);

            selectTrack(musicTrack, null, TrackSelectionState.Deselect);
        }

        updateListView();
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
            mMusicTrackAdapter = new MusicTrackListAdapter(getActivity());

            mMusicTrackAdapter.setShowArtworks(mMusicTrackList.getShowArtworkInTrack());

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

            mMusicTrackAdapter.setList(mMusicTrackList.getMusicTrackList());

            mListView.setAdapter(mMusicTrackAdapter);

            // Click on one list item
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // The header is not clicked
                    if (position > 0) {
                        // We need to subtract the header view
                        position -= 1;

                        // Gets the selected track
                        MusicTrack musicTrack = mMusicTrackAdapter.getItem(position);

                        // Toggle the track
                        selectTrack(musicTrack, view, TrackSelectionState.Toggle);
                    }
                }
            });

            // The floating action button
            mFloatingButtonExport = (FloatingActionButton)rootView.findViewById(R.id.floating_button_export);
            mFloatingButtonExport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Export all selected tracks
                    for(SelectedTrack selectedTrack : SelectedTrackList.getInstance().getSelectedItems()) {
                        selectedTrack.export(getActivity());
                    }

                    // Clear the selection
                    SelectedTrackList.getInstance().clear(true);
                }
            });
            updateFloatingButton();
        }

        return rootView;
    }

    private enum TrackSelectionState { Deselect, Select, Toggle }

    /**
     * Select a track
     * @param musicTrack The track
     * @param view The view
     * @param state Selection state
     */
    private void selectTrack(MusicTrack musicTrack, View view, TrackSelectionState state) {
        // Track is available
        if (musicTrack.isOfflineAvailable()) {

            // Gets the settings
            PlayMusicExporterSettings settings = new PlayMusicExporterSettings(getActivity());

            // Default structure
            String pathStructure = "{album-artist}/{album}/{disc=CD $}/{no=$$.} {title}.mp3";

            // Track is exported from a group (playlist or artist)
            if (!TextUtils.isEmpty(musicTrack.getContainerName()))
            {
                pathStructure = "{group}/{group-no=$$.} {title}.mp3";
            }

            // Build the path
            String path = MusicPathBuilder.Build(musicTrack, pathStructure);

            // Gets the root uri
            Uri uri = settings.getUri(PlayMusicExporterSettings.PREF_EXPORT_URI, Uri.EMPTY);


            // Prevent the closing
            SelectedTrackList.getInstance().setDoNotCloseActionMode(true);

            switch (state) {
                case Select:
                    SelectedTrackList.getInstance().setSelected(new SelectedTrack(musicTrack.getId(), uri, path), true, view);
                    break;
                case Deselect:
                    SelectedTrackList.getInstance().setSelected(new SelectedTrack(musicTrack.getId(), uri, path), false, view);
                    break;
                case Toggle:
                    SelectedTrackList.getInstance().toggle(new SelectedTrack(musicTrack.getId(), uri, path), view);
                    break;
            }

            SelectedTrackList.getInstance().setDoNotCloseActionMode(false);
        } else {
            if (state == TrackSelectionState.Toggle) {
                // Show an info message for offline tracks
                Toast toast = Toast.makeText(getActivity(), R.string.toast_error_track_not_offline, Toast.LENGTH_LONG);
                toast.show();
            }
        }

        updateFloatingButton();
    }
}
