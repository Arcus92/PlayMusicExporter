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

package de.arcus.playmusicexporter2.actionmode;

import android.content.Context;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.arcus.playmusicexporter2.R;
import de.arcus.playmusicexporter2.activities.MusicTrackListActivity;
import de.arcus.playmusicexporter2.activities.MusicContainerListActivity;
import de.arcus.playmusicexporter2.items.SelectedTrack;
import de.arcus.playmusicexporter2.items.SelectedTrackList;

/**
 * Action mode for selected tracks
 */
public class ActionModeTitle implements ActionMode.Callback {
    /**
     * The context
     */
    private Context mContext;

    /**
     * The selection list
     */
    private SelectedTrackList mSelectionList;

    public ActionModeTitle(Context context, SelectedTrackList selectionList) {
        mContext = context;
        mSelectionList = selectionList;
    }

    // Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.action_mode_selection, menu);


        return true;
    }

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        updateViews();

        // Update the title
        mode.setTitle(mContext.getString(R.string.action_mode_track_selection, mSelectionList.getSelectedItems().size()));

        return false; // Return false if nothing is done
    }

    // Called when the user selects a contextual menu item
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:

                // Export all selected tracks
                for(SelectedTrack selectedTrack : SelectedTrackList.getInstance().getSelectedItems()) {
                    selectedTrack.export(mContext);
                }

                // Clear the selection
                SelectedTrackList.getInstance().clear();

                // Close the action mode
                //mode.finish();
                return true;
            case R.id.action_deselect_all:
                // Clear the selection
                SelectedTrackList.getInstance().clear();

                return true;
            default:
                return false;
        }
    }

    /**
     * Update all views
     */
    private void updateViews()
    {
        // We are in the album list
        if (mSelectionList.getActivity() instanceof MusicContainerListActivity) {
            MusicContainerListActivity trackListActivity = (MusicContainerListActivity)mSelectionList.getActivity();

            trackListActivity.updateLists();
        }

        // We are in the track list
        if (mSelectionList.getActivity() instanceof MusicTrackListActivity) {
            MusicTrackListActivity trackDetailActivity = (MusicTrackListActivity)mSelectionList.getActivity();

            trackDetailActivity.updateLists();
        }
    }

    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // We are in the music track list
        if (mSelectionList.getActivity() instanceof MusicTrackListActivity) {
            // Clear the action mode
            SelectedTrackList.getInstance().clearActionMode();

            // Close the activity
            MusicTrackListActivity trackDetailActivity = (MusicTrackListActivity)mSelectionList.getActivity();
            trackDetailActivity.finish();
        } else {
            // Clears the selection
            SelectedTrackList.getInstance().clear();

            // Update the views
            updateViews();
        }
    }
}
