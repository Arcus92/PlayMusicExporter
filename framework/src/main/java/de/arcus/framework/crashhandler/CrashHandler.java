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

package de.arcus.framework.crashhandler;

import android.app.Activity;
import android.content.Intent;

import de.arcus.framework.logger.Logger;

/**
 * Handles crashes of activities and shows a nice dialog with
 * options to send the developer an email with the crash log
 *
 * Use in onCreate in every activity:
 * CrashHandler.addCrashHandler(this);
 *
 * Created by ds on 22.01.2015.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    /**
     * Activity of the app
     */
    private Activity mActivity;

    /**
     * The default crash handler
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * Addes a crash handler to the app context
     * @param activity The activity of the app
     */
    public static void addCrashHandler(Activity activity)
    {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(activity));
    }

    public CrashHandler(Activity activity)
    {
        mActivity = activity;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // Log crash
        Logger.getInstance().logError("CrashHandler", ex.toString());

        StringBuilder logBuilder = new StringBuilder();

        // Information
        logBuilder.append("---------- Information -----------\n");
        logBuilder.append("PackageName: " + mActivity.getPackageName() + "\n");
        logBuilder.append("Crashed activity: " + mActivity.getLocalClassName() + "\n");

        logBuilder.append("----------- Exception ------------\n");
        logBuilder.append(ex.getMessage() + "\n");

        // Log stack trace
        for (StackTraceElement stackTraceElement : ex.getStackTrace())
        {
            logBuilder.append("\t" + stackTraceElement.toString() + "\n");
        }

        // Log Caused by
        if (ex.getCause() != null) {
            logBuilder.append("----------- Caused by ------------\n");
            logBuilder.append(ex.getCause().getMessage() + "\n");

            // Log stack trace
            for (StackTraceElement stackTraceElement : ex.getCause().getStackTrace())
            {
                logBuilder.append("\t" + stackTraceElement.toString() + "\n");
            }
        }

        // Opens a crash window
        Intent intendCrash = new Intent(mActivity, CrashActivity.class);
        intendCrash.putExtra(CrashActivity.EXTRA_FLAG_CRASH_MESSAGE, ex.getMessage());
        intendCrash.putExtra(CrashActivity.EXTRA_FLAG_CRASH_LOG, logBuilder.toString());
        intendCrash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(intendCrash);

        // Close this app
        System.exit(0);
    }
}
