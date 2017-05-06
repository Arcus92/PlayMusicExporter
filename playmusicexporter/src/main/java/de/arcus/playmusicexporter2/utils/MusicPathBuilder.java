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

package de.arcus.playmusicexporter2.utils;

import android.text.TextUtils;

import de.arcus.framework.logger.Logger;
import de.arcus.playmusiclib.items.MusicTrack;

/**
 * Helper class te create a path by a user defined structure
 */
public class MusicPathBuilder {
    /**
     * Hides the constructor
     */
    private MusicPathBuilder() {}

    /**
     * Generates a path from a user defined patter
     * @param musicTrack The music track data
     * @param patter The patter
     * @return Returns the file path
     */
    public static String Build(MusicTrack musicTrack, String patter)
    {
        String path = "";
        int pos = 0;

        // While there is an open tag
        while (patter.indexOf('{', pos) >= 0)
        {
            // Gets the start and the end of the tag
            int posStart = patter.indexOf('{', pos);
            int posEnd = patter.indexOf('}', posStart);

            // Gets the equal sign
            int posEqual = patter.indexOf('=', posStart);

            // Adds the part between this tag and the last one to the path
            path += patter.substring(pos, posStart);

            if (posEnd >= 0) {
                // Name of the tag
                String name;
                String value = "";

                // There is an equal sign
                if (posEqual >= 0 && posEqual < posEnd) {
                    name = patter.substring(posStart + 1, posEqual);
                } else {
                    name = patter.substring(posStart + 1, posEnd);
                }

                // Trim and lower
                name = name.trim().toLowerCase();

                // Gets the values
                switch (name) {
                    case "album-artist":
                        if (!TextUtils.isEmpty(musicTrack.getAlbumArtist()))
                            value = musicTrack.getAlbumArtist();
                        break;
                    case "album":
                        if (!TextUtils.isEmpty(musicTrack.getAlbum()))
                            value = musicTrack.getAlbum();
                        break;
                    case "group":
                        if (!TextUtils.isEmpty(musicTrack.getContainerName()))
                            value = musicTrack.getContainerName();
                        break;
                    case "artist":
                        if (!TextUtils.isEmpty(musicTrack.getArtist()))
                            value = musicTrack.getArtist();
                        break;
                    case "title":
                        if (!TextUtils.isEmpty(musicTrack.getTitle()))
                            value = musicTrack.getTitle();
                        break;
                    case "disc":
                        if (musicTrack.getDiscNumber() > 0)
                            value = String.valueOf(musicTrack.getDiscNumber());
                        break;
                    case "no":
                        if (musicTrack.getTrackNumber() > 0)
                            value = String.valueOf(musicTrack.getTrackNumber());
                        break;
                    case "group-no":
                        if (musicTrack.getContainerPosition() > 0)
                            value = String.valueOf(musicTrack.getContainerPosition());
                        break;
                    case "year":
                        if (!TextUtils.isEmpty(musicTrack.getYear()))
                            value = musicTrack.getYear();
                        break;
                    case "genre":
                        if (!TextUtils.isEmpty(musicTrack.getGenre()))
                            value = musicTrack.getGenre();
                        break;
                    default:
                        // Unknown tag
                        Logger.getInstance().logWarning("MusicPathBuilder", "Unknown tag '" + name + "'");
                        break;
                }

                // Equal sign exists
                if (posEqual >= 0 && posEqual < posEnd && !TextUtils.isEmpty(value)) {
                    String format = patter.substring(posEqual + 1, posEnd);

                    // Gets the insert sign
                    int posInsertStart = format.indexOf('$');
                    if (posInsertStart >= 0) {

                        int posInsertEnd = posInsertStart + 1;
                        // Search the end
                        while(posInsertEnd < format.length() && format.charAt(posInsertEnd) == '$') {
                            posInsertEnd ++;
                        }

                        // Fill Zeros
                        while(value.length() < posInsertEnd - posInsertStart) {
                            value = "0" + value;
                        }

                        // Adds the rest of the format to the value
                        value = format.substring(0, posInsertStart) + value + format.substring(posInsertEnd);

                    } else {
                        // Missing insert sign
                        Logger.getInstance().logWarning("MusicPathBuilder", "Cloud not find replace symbol ('$') of format attribute in tag '" + name + "'");
                    }
                }

                // Adds the value
                path += cleanFilename(value);

                pos = posEnd + 1;
            } else {
                path += "{";
                pos = posStart + 1;
                Logger.getInstance().logWarning("MusicPathBuilder", "Cloud not find end symbol ('}') of the tag in patter '" + patter + "'");
            }
        }

        // Insert end
        path += patter.substring(pos, patter.length());

        // Remove double slash
        while(path.contains("//"))
            path = path.replace("//", "/");

        // Return path
        return path;
    }

    /**
     * Removes forbidden chars in the filename
     * @param filename The filename
     * @return Returns the new clean filename
     */
    public static String cleanFilename(String filename)
    {
        // Forbidden chars
        filename = filename.replace('\\', '-');
        filename = filename.replace(':', '-');
        filename = filename.replace('*', '-');
        filename = filename.replace('?', '-');
        filename = filename.replace('"', '-');
        filename = filename.replace('<', '-');
        filename = filename.replace('>', '-');
        filename = filename.replace('|', '-');
        filename = filename.replace('/', '-');

        return filename;
    }
}
