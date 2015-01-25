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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.arcus.framework.logger.Logger;

/**
 * Help function for files
 */
public class FileTools {
    /**
     * Private constructor
     */
    private FileTools() {}

    /**
     * Creates a directory if it not exists
     * @param dir Directory path
     * @return Returns true if the directory was created or already exists
     */
    public static boolean directoryCreate(String dir) {
        File fileDirectory = new File(dir);
        try {
            if (!fileDirectory.exists()) {
                Logger.getInstance().logVerbose("DirectoryCreate", "Create directory: " + dir);

                // Creates the directory
                if (fileDirectory.mkdirs())
                    return true;
                else
                    Logger.getInstance().logWarning("DirectoryCreate", "MkDir failed");
            } else {
                // Directory exists
                Logger.getInstance().logDebug("DirectoryCreate", "Directory already exists");

                return true;
            }
        } catch (Exception e) {
            // Failed
            Logger.getInstance().logError("DirectoryCreate", "Failed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if the directory exists
     * @param dir Path of the file
     * @return Return whether the directory exists
     */
    public static boolean directoryExists(String dir) {
        File tmp = new File(dir);

        // Checks whether the directory exists and whether it is a directory
        return (tmp.isDirectory() && tmp.exists());
    }

    /**
     * Creates an empty file
     * @param file File path
     * @return Returns true if the file was successfully created
     */
    public static boolean fileCreate(String file) {
        Logger.getInstance().logVerbose("FileCreate", "File: " + file);

        try {
            // Create the file
            return (new File(file)).createNewFile();
        } catch (IOException e) {
            // Failed
            Logger.getInstance().logError("FileCreate",  "Could not create file: " + e.getMessage());

            return false;
        }
    }

    /**
     * Moves a file
     * @param src Soruce path
     * @param dest Destination path
     * @return Return whether the moving was successful
     */
    public static boolean fileMove(String src, String dest) {
        Logger.getInstance().logVerbose("FileMove", "From " + src + " to " + dest);

        File fileSrc = new File(src);
        File fileDest = new File(dest);

        // Move the file
        return fileSrc.renameTo(fileDest);
    }

    /**
     * Deletes a file
     * @param file Path of the file
     * @return Returns whether the deleting was successful
     */
    public static boolean fileDelete(String file) {
        // Delete the file
        return (new File(file)).delete();
    }

    /**
     * Checks if the file exists
     * @param file Path of the file
     * @return Return whether the file exists
     */
    public static boolean fileExists(String file) {
        File tmp = new File(file);

        // Checks whether the file exists and whether it is a file
        return (tmp.isFile() && tmp.exists());
    }

    /**
     * Checks whether the file or directory is a link
     * @param path Path of the file / directory
     * @return Returns whether the file or directory is a link
     */
    public static boolean pathIsSymbolicLink(String path) {
        File file = new File(path);

        try {
            // Checks whether the file / directory is a symbolic link
            return (file.getAbsolutePath() == file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Gets the root canonical file of a symbolic link
     * @param path The path
     * @return The root file
     */
    public static File getRootCanonicalFile(String path) {
        return getRootCanonicalFile(new File(path));
    }

    /**
     * Gets the root canonical file of a symbolic link
     * @param file The file
     * @return The root file
     */
    public static File getRootCanonicalFile(File file) {
        try {
            // Gets the canonical file
            File canonicalFile = file.getCanonicalFile();

            // Differences between the canonical and the absolute file
            while (!file.getAbsolutePath().equals(canonicalFile.getAbsolutePath())) {
                file = canonicalFile;

                // Go deeper
                canonicalFile = file.getCanonicalFile();
            }
        } catch (IOException e) {
            // Failed
            e.printStackTrace();
        }

        return file;
    }

    /**
     * Gets all storages; eg. all sdcards
     * @return List of all storages
     */
    public static String[] getStorages() {
        List<String> storages = new ArrayList<>();

        // Hard coded mount points
        final String[] mountPointBlacklist = new String[] { "/mnt/tmp", "/mnt/factory", "/mnt/obb", "/mnt/asec", "/mnt/secure", "/mnt/media_rw", "/mnt/shell" };
        final String[] mountPointDirectories = new String[] { "/mnt", "/storage" };
        final String[] mountPoints = new String[] { "/sdcard", "/external_sd" };


        // Adds all mount point directories
        for(String mountPointDirectory : mountPointDirectories) {
            // Checks all subdirectories
            File dir = getRootCanonicalFile(mountPointDirectory);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File subDir : files) {
                        subDir = getRootCanonicalFile(subDir);
                        // Is directory
                        if (subDir.isDirectory()) {
                            // Add mount point to list
                            if (!storages.contains(subDir.getAbsolutePath()))
                                storages.add(subDir.getAbsolutePath());
                        }
                    }
                }
            }
        }

        // Adds all direct moint points
        for(String mointPoint : mountPoints) {
            File file = getRootCanonicalFile(mointPoint);
            if (file.isDirectory()) {
                if (!storages.contains(file.getAbsolutePath()))
                    storages.add(file.getAbsolutePath());
            }
        }

        // Remove all blacklisted paths
        for (String blacklistPath : mountPointBlacklist) {
            storages.remove(blacklistPath);
        }

        // Returns the array
        return storages.toArray(new String[storages.size()]);
    }
}
