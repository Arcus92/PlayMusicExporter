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

package de.arcus.framework.utils;

import android.support.annotation.ColorRes;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Selection list
 */
public abstract class SelectionList<T> {
    /**
     * The selected items
     */
    private List<T> mItems = new ArrayList<>();

    /**
     * The activity
     * We need the activity to show the action mode when some items are selected
     */
    private ActionBarActivity mActivity;

    /**
     * The action mode
     */
    private ActionMode mActionMode;

    /**
     * The callback for the action mode
     */
    private ActionMode.Callback mActionModeCallback;

    /**
     * The colors
     */
    private @ColorRes int mResColorNormal;
    private @ColorRes int mResColorSelected;

    /**
     * Gets the activity
     * @return Returns the activity
     */
    public ActionBarActivity getActivity() {
        return mActivity;
    }

    /**
     * Sets the colors of the view.
     * Use @SuppressWarnings("ResourceAsColor") to prevent Lint errors
     * @param colorNormal Normal state
     * @param colorSelected Selected state
     */
    public void setColor(@ColorRes int colorNormal, @ColorRes int colorSelected) {
        mResColorNormal = colorNormal;
        mResColorSelected = colorSelected;
    }

    /**
     * Sets up the action mode for this selection list
     * @param activity The activity
     */
    public void setupActionMode(ActionBarActivity activity) {
        mActivity = activity;
        mActionModeCallback = createActionMode(activity);

        // Updates the action mode
        updateActionModeMenu();
    }

    /**
     * Set the selection state of the item
     * @param item The item
     * @param state The selection state
     */
    public void setSelected(T item, boolean state) {
        // Adds the item to the selection list
        if (state && !mItems.contains(item))
            mItems.add(item);

        // Removes the item from the selection list
        if (!state && mItems.contains(item))
            mItems.remove(item);

        // Updates the action mode
        updateActionModeMenu();
    }

    /**
     * Set the selection state of the item and change the item background
     * @param item The item
     * @param state The selection state
     * @param view The view
     */
    public void setSelected(T item, boolean state, View view) {
        // Set the selection state
        setSelected(item, state);

        // Change the background
        if (state)
            view.setBackgroundColor(view.getResources().getColor(mResColorSelected));
        else
            view.setBackgroundColor(view.getResources().getColor(mResColorNormal));
    }

    /**
     * Toggles the item
     * @param item The selected item
     * @return Returns whether the item is now selected
     */
    public boolean toggle(T item) {
        boolean state = isSelected(item);

        setSelected(item, !state);

        return !state;
    }

    /**
     * Toggles the item
     * @param item The selected item
     * @param view The view
     * @return Returns whether the item is now selected
     */
    public boolean toggle(T item, View view) {
        boolean state = isSelected(item);

        setSelected(item, !state, view);

        return !state;
    }

    /**
     * Gets all selected items
     * @return Returns a list with all selected items
     */
    public List<T> getSelectedItems() {
        return mItems;
    }

    /**
     * Clears the selection
     */
    public void clear() {
        mItems.clear();

        // Updates the action mode
        updateActionModeMenu();
    }

    /**
     * Call this after the view was created.
     * This will change the background color.
     * @param item The selected item
     * @param view The view
     */
    public void initView(T item, View view) {
        // Change the background
        if (isSelected(item))
            view.setBackgroundColor(view.getResources().getColor(mResColorSelected));
        else
            view.setBackgroundColor(view.getResources().getColor(mResColorNormal));
    }

    /**
     * Updates the action mode menu
     */
    private void updateActionModeMenu() {
        // Null check
        if (mActionModeCallback != null && mActivity != null && !mActivity.isFinishing()) {

            // Some items are selected, shows the action mode
            if (mItems.size() > 0 && mActionMode == null) {
                mActionMode = mActivity.startSupportActionMode(mActionModeCallback);
            }

            // Close the action mode
            if (mItems.size() == 0 && mActionMode != null) {
                // Set mActionMode to null before call finish to prevent recursion
                ActionMode actionMode = mActionMode;
                mActionMode = null;

                // Close the action mode
                actionMode.finish();
            }

            // Set the text
            if (mActionMode != null) {
                // Update the action mode
                mActionMode.invalidate();
            }
        }
    }

    /**
     * This is called every time a new activity opens.
     * This method has to create a action mode callback
     * @param activity The new activity
     * @return Returns the action mode callback
     */
    protected abstract ActionMode.Callback createActionMode(ActionBarActivity activity);

    /**
     * Gets whether the item is selected
     * @param item The selected item
     * @return Returns true if the item is selected
     */
    public boolean isSelected(T item) {
        return mItems.contains(item);
    }

}
