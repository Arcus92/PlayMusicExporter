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

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import de.arcus.framework.logger.Logger;
import de.arcus.framework.R;

public class CrashActivity extends AppCompatActivity {
    // Extra flags
    public static final String EXTRA_FLAG_CRASH_MESSAGE = "CRASH_TITLE";
    public static final String EXTRA_FLAG_CRASH_LOG = "CRASH_LOG";

    /**
     * StackTrace of the exception
     */
    private String mCrashMessage;

    /**
     * Log of the exception
     */
    private String mCrashLog;

    /**
     * The name of the app
     */
    private String mAppName;

    /**
     * The intent to start the app (for restart the app)
     */
    private Intent mLaunchIntent;
    /**
     * Email address to send the crash log to
     * Set this in AndroidManifest.xml: <meta-data android:name="crashhandler.email" android:value="..." />
     */
    private String mMetaDataEmail;

    /**
     * URL of a bugtracker or a support homepage
     * Set this in AndroidManifest.xml: <meta-data android:name="crashhandler.supporturl" android:value="..." />
     */
    private String mMetaDataSupportURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        // Reads the crash information
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_FLAG_CRASH_MESSAGE))
                mCrashMessage = bundle.getString(EXTRA_FLAG_CRASH_MESSAGE);
            if (bundle.containsKey(EXTRA_FLAG_CRASH_LOG))
                mCrashLog = bundle.getString(EXTRA_FLAG_CRASH_LOG);
        } else {
            // No information; close activity
            finish();
            return;
        }

        try {
            // Get the PackageManager to load information about the app
            PackageManager packageManager = getPackageManager();
            // Loads the ApplicationInfo with meta data
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

            if (applicationInfo.metaData != null) {
                // Reads the crash handler settings from meta data
                if (applicationInfo.metaData.containsKey("crashhandler.email"))
                    mMetaDataEmail = applicationInfo.metaData.getString("crashhandler.email");
                if (applicationInfo.metaData.containsKey("crashhandler.supporturl"))
                    mMetaDataSupportURL = applicationInfo.metaData.getString("crashhandler.supporturl");
            }

            // Gets the app name
            mAppName = packageManager.getApplicationLabel(applicationInfo).toString();
            // Gets the launch intent for the restart
            mLaunchIntent = packageManager.getLaunchIntentForPackage(getPackageName());

        } catch (PackageManager.NameNotFoundException ex) {
            // If this occurs then god must be already dead
            Logger.getInstance().logError("CrashHandler", ex.toString());
        }

        // Set the action bar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.crashhandler_app_has_stopped_working, mAppName));
        }

        // Set the message
        TextView textViewMessage = (TextView) findViewById(R.id.text_view_crash_message);
        if (textViewMessage != null) {
            textViewMessage.setText(mCrashLog);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crash, menu);

        // Hide the email button if there is no crashhandler.email in AndroidManifest.xml
        menu.findItem(R.id.action_email).setVisible(!TextUtils.isEmpty(mMetaDataEmail));

        // Hide the homepage if there is no crashhandler.supporturl in AndroidManifest.xml
        menu.findItem(R.id.action_support).setVisible(!TextUtils.isEmpty(mMetaDataSupportURL));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_restart) {
            // Close this window
            finish();

            // Restart the app
            startActivity(mLaunchIntent);
        } else if (id == R.id.action_email) {
            // Send email
            ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(this);
            builder.setType("message/rfc822");
            builder.addEmailTo(mMetaDataEmail);
            builder.setSubject("Crash log for " + mAppName);
            builder.setChooserTitle(R.string.crashhandler_choose_email_title);
            builder.setText(mCrashLog);
            builder.startChooser();
        } else if (id == R.id.action_support) {
            // Open Homepage
            Intent intentUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(mMetaDataSupportURL));
            startActivity(intentUrl);

        } else if (id == R.id.action_close_dialog) {
            // Close this window
            finish();
        } else { // Other
            return super.onOptionsItemSelected(item);
        }


        // One of our items was selected
        return true;
    }
}
