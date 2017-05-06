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

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.arcus.playmusicexporter2.R;
import de.arcus.playmusicexporter2.activities.SettingsActivity;
import de.arcus.playmusicexporter2.settings.PlayMusicExporterSettings;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    private Button mButtonTypeAlbum;
    private Button mButtonTypeArtist;
    private Button mButtonTypePlaylist;
    private Button mButtonTypeRated;
    private Button mButtonSettings;

    public enum ViewType {
        Album, Artist, Playlist, Rated
    }

    private ViewType mViewType;

    /**
     * @return Gets the current view type
     */
    public ViewType getViewType() {
        return mViewType;
    }

    /**
     * @param viewType Sets the current view type
     */
    private void setViewType(ViewType viewType) {
        // Change the ui
        setButtonActive(mButtonTypeAlbum, viewType == ViewType.Album);
        setButtonActive(mButtonTypeArtist, viewType == ViewType.Artist);
        setButtonActive(mButtonTypePlaylist, viewType == ViewType.Playlist);
        setButtonActive(mButtonTypeRated, viewType == ViewType.Rated);

        // Callback the parent activity
        if (viewType != mViewType)
            if (mCallbacks != null) mCallbacks.onViewTypeChanged(viewType);

        mViewType = viewType;

        // Save the selection
        PlayMusicExporterSettings appSettings = new PlayMusicExporterSettings(getActivity());
        appSettings.setEnum(PlayMusicExporterSettings.PREF_DRAWER_SELECTED_TYPE, viewType);

        // Close the drawer
        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawers();
    }

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the settings
        PlayMusicExporterSettings appSettings = new PlayMusicExporterSettings(getActivity());
        mViewType = appSettings.getEnum(PlayMusicExporterSettings.PREF_DRAWER_SELECTED_TYPE, ViewType.Album);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        // Gets all buttons
        mButtonTypeAlbum = (Button)view.findViewById(R.id.button_type_album);
        mButtonTypeArtist = (Button)view.findViewById(R.id.button_type_artist);
        mButtonTypePlaylist = (Button)view.findViewById(R.id.button_type_playlist);
        mButtonTypeRated = (Button)view.findViewById(R.id.button_type_rated);

        mButtonSettings = (Button)view.findViewById(R.id.button_setting);

        // Set the default
        setViewType(mViewType);

        // Click on album
        mButtonTypeAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewType(ViewType.Album);
            }
        });

        // Click on artist
        mButtonTypeArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewType(ViewType.Artist);
            }
        });

        // Click on playlist
        mButtonTypePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewType(ViewType.Playlist);
            }
        });

        // Click on rated
        mButtonTypeRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewType(ViewType.Rated);
            }
        });

        // Click on settings
        mButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intentSettings);

                // Close the drawer
                mDrawerLayout.closeDrawers();
            }
        });

        // Color the settings button
        setButtonActive(mButtonSettings, false);

        return view;
    }

    /**
     * Format the button
     * @param button The button
     * @param active Active
     */
    public void setButtonActive(Button button, boolean active) {
        int colorText;
        int colorBackground;

        // Button is active
        if (active) {
            // Gets the active color

            colorText = ContextCompat.getColor(getContext(), R.color.button_navigation_drawer_text_active);
            colorBackground = ContextCompat.getColor(getContext(), R.color.button_navigation_drawer_active);
        } else {
            // Gets the normal color
            colorText = ContextCompat.getColor(getContext(), R.color.button_navigation_drawer_text);
            colorBackground = ContextCompat.getColor(getContext(), R.color.button_navigation_drawer_normal);
        }

        // Sets the color
        button.setBackgroundColor(colorBackground);
        button.setTextColor(colorText);
        button.getCompoundDrawables()[0].setColorFilter(colorText, PorterDuff.Mode.MULTIPLY);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setOnListViewChanged(NavigationDrawerCallbacks callback) {
        mCallbacks = callback;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };


        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            //inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        /*if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onViewTypeChanged(ViewType viewType);
    }
}
