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

package de.arcus.playmusicexporter2.items;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;

import de.arcus.framework.utils.SelectionList;
import de.arcus.playmusicexporter2.R;
import de.arcus.playmusicexporter2.actionmode.ActionModeTitle;

/**
 * The selection manager for music tracks
 */
public class SelectedTrackList extends SelectionList<SelectedTrack> {
    /**
     * The instance of the selection
     */
    private static SelectedTrackList instance;

    /**
     * Gets the latest instance of the track selection.
     * Creates a new one if it doesn't exist.
     * @return The instance
     */
    @SuppressWarnings("ResourceAsColor")
    public static SelectedTrackList getInstance() {
        // Create a new instance
        if (instance == null) {
            instance = new SelectedTrackList();

            // Sets the color resources
            instance.setColor(R.color.button_navigation_drawer_normal, R.color.button_navigation_drawer_selected);
        }

        return instance;
    }

    /**
     * Creates the action mode callback
     * @param activity The activity
     * @return The new action mode callback
     */
    @Override
    protected ActionMode.Callback createActionMode(AppCompatActivity activity) {
        return new ActionModeTitle(activity, this);
    }
}
