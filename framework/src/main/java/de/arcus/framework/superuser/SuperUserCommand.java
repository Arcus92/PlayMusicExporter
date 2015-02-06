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

package de.arcus.framework.superuser;

import android.app.Activity;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.arcus.framework.logger.Logger;

/**
 * This class executes superuser commands.
 */
public class SuperUserCommand {
    /**
     * The default timeout for each command in milliseconds
     */
    private static final long DEFAULT_COMMAND_TIMEOUT = 30 * 1000; // 30 seconds

    private String[] mCommands = new String[] {};
    private String[] mOutputStandard = new String[] {};
    private String[] mOutputError = new String[] {};

    // If we want to get a binary return
    private byte[] mOutputStandardBinary = new byte[] {};

    /**
     * Command failed?
     */
    private boolean mSuperUserFailed;

    /**
     * If this value is set, the command will not store any input to the logger
     */
    private boolean mHideInput = false;
    /**
     * If this value is set, the command will not store any standard output to the logger
     */
    private boolean mHideStandardOutput = false;
    /**
     * If this value is set, the command will not store any error output to the logger
     */
    private boolean mHideErrorOutput = false;

    /**
     * @return Gets whether the command hides the input log
     */
    public boolean getHideInput() {
        return mHideInput;
    }

    /**
     * @param hideInput Set this to hide the input to the logger
     */
    public void setHideInput(boolean hideInput) {
        mHideInput = hideInput;
    }

    /**
     * @return Gets whether the command hides the standard output log
     */
    public boolean getHideStandardOutput() {
        return mHideStandardOutput;
    }

    /**
     * @param hideStandardOutput Set this to hide the standard output to the logger
     */
    public void setHideStandardOutput(boolean hideStandardOutput) {
        mHideStandardOutput = hideStandardOutput;
    }

    /**
     * @return Gets whether the command hides the error output log
     */
    public boolean getHideErrorOutput() {
        return mHideErrorOutput;
    }

    /**
     * @param hideErrorOutput Set this to hide the error output to the logger
     */
    public void setHideErrorOutput(boolean hideErrorOutput) {
        mHideErrorOutput = hideErrorOutput;
    }

    /**
     * If this value is set the command will read the standard output as binary
     */
    private boolean mBinaryStandardOutput = false;

    /**
     * @return Gets whether the output will be binary
     */
    public boolean getBinaryStandardOutput() {
        return mBinaryStandardOutput;
    }

    /**
     * @param binaryStandardOutput Set this if you want a binary output
     */
    public void setBinaryStandardOutput(boolean binaryStandardOutput) {
        mBinaryStandardOutput = binaryStandardOutput;
    }

    /**
     * The timeout for this command in milliseconds
     */
    private long mTimeout;

    /**
     * @return Gets the timeout for this command in milliseconds
     */
    public long getTimeout() {
        return mTimeout;
    }

    /**
     * Set the timeout for this command in milliseconds
     * @param timeout Timeout
     * @return Itself
     */
    public SuperUserCommand setTimeout(long timeout) {
        mTimeout = timeout;
        return this;
    }

    /**
     * @return Gets the executed commands
     */
    public String[] getCommands() {
        return mCommands;
    }

    /**
     * @return Gets the standard output
     */
    public String[] getStandardOutput() {
        return mOutputStandard;
    }

    /**
     * @return Gets the error output
     */
    public String[] getErrorOutput() {
        return mOutputError;
    }

    /**
     * @return Gets the standard output as binary
     */
    public byte[] getStandardOutputBinary() {
        return mOutputStandardBinary;
    }

    /**
     * @return Gets whether the command was executed without errors, even without error outputs from the command.
     */
    public boolean commandWasSuccessful() {
        return (!mSuperUserFailed && mOutputError.length == 0);
    }

    /**
     * @return Gets whether the command was granted superuser permissions, but maybe has some error outputs.
     */
    public boolean superuserWasSuccessful() {
        return (!mSuperUserFailed);
    }

    /**
     * The async execution thread
     */
    private SuperUserCommandThread mThread;

    /**
     * The async callback
     */
    private SuperUserCommandCallback mCallback;

    /**
     * Creates a command with one command line
     * @param command The command
     */
    public SuperUserCommand(String command) {
        this(new String[] {command});
    }

    /**
     * Creates a command with multiple command lines
     * @param commands The command lines
     */
    public SuperUserCommand(String[] commands) {
        mCommands = commands;

        // Default timeout
        mTimeout = DEFAULT_COMMAND_TIMEOUT;
    }

    /**
     * Execute the command asynchronously.
     * Please notice that the commands will only executed one after another.
     * The command will wait until the su process is free.
     * @param callback The callback instance
     */
    public void executeAsync(SuperUserCommandCallback callback) {
        mCallback = callback;

        // Thread is running
        if (mThread != null) return;

        // Create a new thread
        mThread = new SuperUserCommandThread();

        // Starts a thread
        mThread.start();
    }

    /**
     * Execute the command and return whether the command was executed.
     * It will only return false if the app wasn't granted superuser permissions, like {@link #superuserWasSuccessful()}.
     * It will also return true if the command itself returns error outputs. To check this case you should use {@link #commandWasSuccessful()} instead.
     * Please consider to use {@link #executeAsync} instead of this and execute the command asynchronously.
     * @return Gets whether the execution was successful.
     */
    public boolean execute() {
        String tmpLine;
        List<String> tmpList = new ArrayList<>();

        mSuperUserFailed = false;

        // Opps, we don't have superuser permissions
        // Did you run SuperUser.askForPermissions()?
        if (!SuperUser.hasPermissions()) {
            mSuperUserFailed = true;
            return false;
        }

        // Thread safe
        synchronized (SuperUser.getProcess()) {
            try {
                // Gets the streams
                DataOutputStream dataOutputStream = new DataOutputStream(SuperUser.getProcess().getOutputStream());
                BufferedReader bufferedInputReader = new BufferedReader(new InputStreamReader(SuperUser.getProcess().getInputStream()));
                BufferedReader bufferedErrorReader = new BufferedReader(new InputStreamReader(SuperUser.getProcess().getErrorStream()));

                // Sends the command
                for (String command : mCommands) {
                    if (!mHideInput) // Check if we want to hide this
                        Logger.getInstance().logInfo("SuperUser", "< " + command);
                    dataOutputStream.writeBytes(command + "\n");
                }
                dataOutputStream.flush();

                // TODO: This class cannot execute commands without any output (standard and error). These commands will run until the timeout will kill them!

                // Start waiting
                long timeStarted = System.currentTimeMillis();

                // Wait for first data
                while (!bufferedInputReader.ready() && !bufferedErrorReader.ready()) {
                    try {
                        // Waiting
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    long timeNow = System.currentTimeMillis();

                    // TimeOut
                    if (timeNow - timeStarted >= mTimeout) break;
                }

                // We want to read the data as binary
                if (mBinaryStandardOutput) {
                    int len;
                    byte[] buffer = new byte[1024];

                    // Byte buffer
                    ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(1024);

                    // Need the direct input stream
                    InputStream inputStream = SuperUser.getProcess().getInputStream();

                    do {
                        while (bufferedInputReader.ready()) {
                            // Read to buffer
                            len = inputStream.read(buffer);

                            // Write to buffer
                            byteArrayBuffer.append(buffer, 0, len);
                        }

                        // Fix: Wait for the buffer and try again
                        try {
                            // Sometimes cat is to slow.
                            // If there is no data anymore we will wait 100ms and check again.
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (bufferedInputReader.ready());

                    mOutputStandardBinary = byteArrayBuffer.toByteArray();
                } else {
                    // Reads the standard output as text
                    tmpList.clear();
                    while (bufferedInputReader.ready()) {
                        tmpLine = bufferedInputReader.readLine();

                        // End of data
                        if (tmpLine == null) break;

                        if (!mHideStandardOutput)
                            Logger.getInstance().logInfo("SuperUser", "> " + tmpLine);

                        tmpList.add(tmpLine);
                    }
                    // Convert list to array
                    mOutputStandard = tmpList.toArray(new String[tmpList.size()]);
                }

                // Reads the error output
                tmpList.clear();
                while (bufferedErrorReader.ready()) {
                    tmpLine = bufferedErrorReader.readLine();

                    // End of data
                    if (tmpLine == null) break;

                    if (!mHideErrorOutput)
                        Logger.getInstance().logError("SuperUser", "> " + tmpLine);

                    tmpList.add(tmpLine);
                }
                // Convert list to array
                mOutputError = tmpList.toArray(new String[tmpList.size()]);

                // Done
                return true;
            } catch (IOException e) {
                e.printStackTrace();

                mSuperUserFailed = true;

                // Command failed
                return false;
            }
        }
    }


    /**
     * Thread to executes the command asynchronously
     */
    private class SuperUserCommandThread extends Thread {
        @Override
        public void run() {

            super.run();

            // Executes the command
            execute();

            if (mCallback != null)
                mCallback.onFinished(SuperUserCommand.this);
        }
    }
}
