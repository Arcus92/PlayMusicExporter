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

package de.arcus.playmusicexporter2.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import de.arcus.framework.logger.Logger;
import de.arcus.playmusicexporter2.R;
import de.arcus.playmusiclib.PlayMusicManager;
import de.arcus.playmusiclib.datasources.MusicTrackDataSource;
import de.arcus.playmusiclib.items.MusicTrack;

/**
 * The export service
 */
public class ExportService extends IntentService {
    /**
     * Constants for the server instance
     */
    public static final String ARG_EXPORT_TRACK_ID = "track_id";
    public static final String ARG_EXPORT_PATH = "path";

    /**
     * Notification id
     */
    public static final int NOTIFICATION_ID = 1;

    /**
     * The export notification
     */
    protected NotificationCompat.Builder mNotificationBuilder;

    /**
     * The number of tracks that needs to be exported
     */
    protected int mTracksTotal;

    /**
     * The number of tracks that were exported
     */
    protected int mTracksDone;

    /**
     * The number of tracks that were not exported
     */
    protected int mTracksFailed;

    /**
     * The current track we are exporting
     */
    protected MusicTrack mTrackCurrent;

    /**
     * The export finished
     */
    protected boolean mFinished;



    /**
     * Creates a new export service
     */
    public ExportService() {
        super("ExportService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.getInstance().logDebug("ExportService", "Start");

        // Creates a notification builder
        mNotificationBuilder = new NotificationCompat.Builder(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Finish
        Logger.getInstance().logDebug("ExportService", "End");
        mFinished = true;

        updateNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Count
        mTracksTotal++;

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Updates the notification.
     * Sets the data in {@link this.mNotificationBuilder}
     */
    protected void updateNotification() {
        // Gets the notification manager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (mFinished) {
            // Clear the progress
            mNotificationBuilder.setProgress(0, 0, false);

            mNotificationBuilder.setSmallIcon(R.drawable.ic_action_accept);
            mNotificationBuilder.setAutoCancel(false);

            mNotificationBuilder.setContentTitle(getString(R.string.notification_export_finished_title));

            if (mTracksTotal == 1) {
                mNotificationBuilder.setContentText(getString(R.string.notification_export_finished_single_summery, mTrackCurrent.getTitle()));
            } else {
                mNotificationBuilder.setContentText(getString(R.string.notification_export_finished_summery, mTracksDone, mTracksTotal));
            }
        } else {
            // Sets the progress
            mNotificationBuilder.setProgress(mTracksTotal, mTracksDone, false);

            mNotificationBuilder.setSmallIcon(R.drawable.ic_action_download);
            mNotificationBuilder.setAutoCancel(true);

            if (mTrackCurrent != null) {
                // Sets the title
                mNotificationBuilder.setContentTitle(mTrackCurrent.getTitle());

                if (mTracksTotal == 1) {
                    mNotificationBuilder.setContentText(getString(R.string.notification_export_working_single_summery));
                } else {
                    mNotificationBuilder.setContentText(getString(R.string.notification_export_working_summery, mTracksDone + 1, mTracksTotal));
                }
            }
        }

        // Build the notification
        Notification notification = mNotificationBuilder.build();

        // Show the notification and replace the older once with the same id
        notificationManager.notify(NOTIFICATION_ID, notification);

    }



    /**
     * New service request
     * @param intent Data for the exporter
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Get the parameter
        Bundle bundle = intent.getExtras();

        // Gets the track information
        long trackID = bundle.getLong(ARG_EXPORT_TRACK_ID);
        String path = bundle.getString(ARG_EXPORT_PATH);

        PlayMusicManager playMusicManager = PlayMusicManager.getInstance();

        if (playMusicManager != null) {
            // Creates a new data source to get the selected track
            MusicTrackDataSource musicTrackDataSource = new MusicTrackDataSource(playMusicManager);

            // Gets the track
            mTrackCurrent = musicTrackDataSource.getById(trackID);
            if (mTrackCurrent != null) {
                // Updates the notification
                updateNotification();

                // Exports the song
                if(!playMusicManager.exportMusicTrack(mTrackCurrent, path)) {
                    // Export failed
                    mTracksFailed ++;
                }
            } else {
                // Export failed
                mTracksFailed ++;
            }
        } else {
            // Export failed
            mTracksFailed ++;
        }

        mTracksDone ++;

        // Updates the notification
        updateNotification();
    }
}
