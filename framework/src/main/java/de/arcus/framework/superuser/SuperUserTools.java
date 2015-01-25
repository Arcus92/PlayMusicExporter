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

/**
 * Tools for the superuser
 */
public class SuperUserTools {
    /**
     * Private constructor
     */
    private SuperUserTools() {}

    /**
     * Copy a file with root permissions
     * @param src Source file
     * @param dest Destination file
     * @return Returns whether the command was successful
     */
    public static boolean fileCopy(String src, String dest) {
        SuperUserCommand superUserCommand = new SuperUserCommand(new String[] {
                "rm -f '" + dest + "'", // Remove destination file
                "cat '" + src + "' >> '" + dest + "'", // Using cat to copy file instead of cp, because you can use it without busybox
                "chmod 0777 '" + dest + "'", // Change the access mode to all users (chown sdcard_r will fail on some devices)
                "echo 'done'" // Fix to prevent the 'no output' bug in SuperUserCommand
        });
        // Executes the command
        superUserCommand.execute();

        // Superuser permissions and command are successful
        return superUserCommand.commandWasSuccessful();
    }

    /**
     * Checks if the file exists
     * @param path The path to check
     * @return Returns whether the path exists
     */
    public static boolean fileExists(String path) {
        SuperUserCommand superUserCommand = new SuperUserCommand("ls '" + path + "'");

        // Executes the command
        superUserCommand.execute();

        // Superuser permissions and command are successful
        return superUserCommand.commandWasSuccessful();
    }


    /**
     * Gets all bytes from one file
     * @param path The path to the file
     * @return Returns the byte array or null if the file doesn't exists
     */
    public static byte[] fileReadToByteArray(String path) {
        SuperUserCommand superUserCommand = new SuperUserCommand("cat '" + path + "'");

        // Don't spam the log with binary code
        superUserCommand.setHideStandardOutput(true);
        superUserCommand.setBinaryStandardOutput(true);

        // Executes the command
        superUserCommand.execute();

        // Failed
        if (!superUserCommand.commandWasSuccessful())
            return null;

        return superUserCommand.getStandardOutputBinary();
    }
}
