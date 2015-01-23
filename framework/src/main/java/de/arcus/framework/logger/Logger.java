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

package de.arcus.framework.logger;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Helper class to write into a log file and to logcat
 */
public class Logger {
    /**
     * Type of the log entry
     */
    public enum LogEntryType { Verbose, Debug, Info, Warning, Error };

    /**
     * Instance of the logger
     */
    private static Logger instance;

    /**
     * Get the active instance of the Logger
     * Creates an instance if not exists
     * @return Gets the logger
     */
    public static Logger getInstance() {
        // Create new Instance
        if (instance == null)
            instance = new Logger();

        return instance;
    }

    /**
     * List of all entries
     */
    private Queue<LogEntry> mEntryList = new LinkedList<>();

    /**
     * @return Gets the entry list
     */
    public Queue<LogEntry> getEntryList() {
        return mEntryList;
    }

    /**
     * The minimum level to log messages
     */
    private LogEntryType mLogLevel = LogEntryType.Debug;

    /**
     * @return Gets the log level
     */
    public LogEntryType getLogLevel() {
        return mLogLevel;
    }

    /**
     * Sets the log level
     * @param type The minimal log level to log messages
     */
    public void setLogLevel(LogEntryType type) {
        mLogLevel = type;
    }

    /**
     * Adds a verbose entry to the log
     * @param tag Tag of the log entry (function or section)
     * @param message Message of the log entry
     */
    public void logVerbose(String tag, String message) {
        log(LogEntryType.Verbose, tag, message);
    }

    /**
     * Adds a debug entry to the log
     * @param tag Tag of the log entry (function or section)
     * @param message Message of the log entry
     */
    public void logDebug(String tag, String message) {
        log(LogEntryType.Debug, tag, message);
    }

    /**
     * Adds a info entry to the log
     * @param tag Tag of the log entry (function or section)
     * @param message Message of the log entry
     */
    public void logInfo(String tag, String message) {
        log(LogEntryType.Info, tag, message);
    }

    /**
     * Adds a warning entry to the log
     * @param tag Tag of the log entry (function or section)
     * @param message Message of the log entry
     */
    public void logWarning(String tag, String message) {
        log(LogEntryType.Warning, tag, message);
    }

    /**
     * Adds a error entry to the log
     * @param tag Tag of the log entry (function or section)
     * @param message Message of the log entry
     */
    public void logError(String tag, String message) {
        log(LogEntryType.Error, tag, message);
    }

    /**
     * Adds a entry to the log
     * @param type Entry type (Debug, Info, Warning, Error)
     * @param tag Tag of the log entry (function or section)
     * @param message Message of the log entry
     */
    private void log(LogEntryType type, String tag, String message) {
        // Entry type will be logged
        if (type.ordinal() >= mLogLevel.ordinal())
        {
            LogEntry logEntry = new LogEntry(type, tag, message);

            // LogCat
            logEntry.writeToLogCat();

            // Push entry to list
            mEntryList.add(logEntry);
        }
    }

    /**
     * A single log entry
     */
    public class LogEntry {
        /**
         * Type of the log entry
         */
        private LogEntryType mType;

        /**
         * Tag of the log entry
         */
        private String mTag;

        /**
         * Message of the log entry
         */
        private String mMessage;

        /**
         * @return Gets the Type of the log entry
         */
        public LogEntryType getType() {
            return mType;
        }

        /**
         * @return Gets the tag of the log entry
         */
        public String getTag() {
            return mTag;
        }

        /**
         * @return Gets the message of the log entry
         */
        public String getMessage() {
            return mMessage;
        }

        /**
         * Creates a log entry
         * @param type Entry type (Debug, Info, Warning, Error)
         * @param tag Tag of the log entry (function or section)
         * @param message Message of the log entry
         */
        public LogEntry(LogEntryType type, String tag, String message) {
            mType = type;
            mTag = tag;
            mMessage = message;
        }

        /**
         * Writes the entry to logcat
         */
        public void writeToLogCat() {
            switch (mType) {
                case Verbose:
                    Log.v(mTag, mMessage);
                    break;
                case Debug:
                    Log.d(mTag, mMessage);
                    break;
                case Info:
                    Log.i(mTag, mMessage);
                    break;
                case Warning:
                    Log.w(mTag, mMessage);
                    break;
                case Error:
                    Log.e(mTag, mMessage);
                    break;
            }
        }
    }
}
