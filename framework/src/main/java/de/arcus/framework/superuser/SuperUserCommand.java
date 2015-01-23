/*
 * Copyright (c) 2015.
 */

package de.arcus.framework.superuser;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    private boolean mSuperUserFailed;

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
     * @return Get the standard output
     */
    public String[] getStandardOutput() {
        return mOutputStandard;
    }

    /**
     * @return Get the error output
     */
    public String[] getErrorOutput() {
        return mOutputError;
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
     * Execute the command and return whether the command was executed.
     * It will only return false if the app wasn't granted superuser permissions, like {@link #superuserWasSuccessful()}.
     * It will also return true if the command itself returns error outputs. To check this case you should use {@link #commandWasSuccessful()} instead.
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

        try {
            // Gets the streams
            DataOutputStream dataOutputStream = new DataOutputStream(SuperUser.getProcess().getOutputStream());
            BufferedReader bufferedInputReader = new BufferedReader(new InputStreamReader(SuperUser.getProcess().getInputStream()));
            BufferedReader bufferedErrorReader = new BufferedReader(new InputStreamReader(SuperUser.getProcess().getErrorStream()));

            // Sends the command
            for (String command : mCommands)
                dataOutputStream.writeBytes(command + "\n");
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

            // Reads the standard output
            tmpList.clear();
            while (bufferedInputReader.ready()) {
                tmpLine = bufferedInputReader.readLine();

                // End of data
                if (tmpLine == null) break;

                Log.i("SuperUser", "> " + tmpLine);
                tmpList.add(tmpLine);
            }
            // Convert list to array
            mOutputStandard = tmpList.toArray(new String[tmpList.size()]);


            // Reads the error output
            tmpList.clear();
            while (bufferedErrorReader.ready()) {
                tmpLine = bufferedErrorReader.readLine();

                // End of data
                if (tmpLine == null) break;
                Log.e("SuperUser", "> " + tmpLine);

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
