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

package de.arcus.framework.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import de.arcus.framework.R;

/**
 * Activity to browse for a directory
 */
public class DirectoryBrowserActivity extends AppCompatActivity {
    // The intent extra names
    public final static String EXTRA_PATH = "path";
    public final static String EXTRA_TITLE = "title";

    /**
     * The title
     */
    private String mTitle = "";

    /**
     * The current path
     */
    private File mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout
        setContentView(R.layout.activity_directory_browser);

        // Gets the bundle data
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Reads the title
            if (bundle.containsKey(EXTRA_TITLE))
                mTitle = bundle.getString(EXTRA_TITLE);

            // Reads the start path
            if (bundle.containsKey(EXTRA_PATH)) {
                String path = bundle.getString(EXTRA_PATH);
                if (path != null) {
                    mPath = new File(path);

                    // Default directory
                    if (!mPath.exists()) {
                        mPath = Environment.getExternalStorageDirectory();
                    }
                }
            }
        }

        // Setup the actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Set the title
            actionBar.setTitle(mTitle);
            actionBar.setSubtitle(mPath.getAbsolutePath());
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_directory_browser, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_accept) {
            // Result the path
            getIntent().setData(Uri.fromFile(mPath));
            setResult(RESULT_OK, getIntent());
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens the directory browser (use the Lollipop API if it's available)
     * @param context The Context
     * @param defaultPath The default path
     * @param title The title
     */
    public static Intent openDirectoryBrowser(Context context, Uri defaultPath, String title) {
        return openDirectoryBrowser(context, defaultPath, title, false);
    }

    /**
     * Opens the directory browser (use the Lollipop API if it's available)
     * @param context The Context
     * @param defaultPath The default path
     * @param title The title
     * @param forceBuildInBrowser Set this to true to use the build-in browser even for Lollipop
     */
    public static Intent openDirectoryBrowser(Context context, Uri defaultPath, String title, boolean forceBuildInBrowser) {
        Intent intent;

        // Check for Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !forceBuildInBrowser) {
            // Opens the Lollipop directory browser

            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        } else {
            // Opens the build-in directory browser
            intent = new Intent(context, DirectoryBrowserActivity.class);

            // The current path
            intent.putExtra(DirectoryBrowserActivity.EXTRA_PATH, defaultPath.getPath());
            intent.putExtra(DirectoryBrowserActivity.EXTRA_TITLE, title);
        }

        return intent;
    }
}
