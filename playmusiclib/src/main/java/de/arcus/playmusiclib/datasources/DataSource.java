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

package de.arcus.playmusiclib.datasources;

import android.database.Cursor;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

import de.arcus.playmusiclib.PlayMusicManager;

/**
 * Universal data source
 */
public abstract class DataSource<T> {
    /**
     * The manager
     */
    protected PlayMusicManager mPlayMusicManager;

    /**
     * Creates a new data source
     * @param playMusicManager The manager
     */
    public DataSource(PlayMusicManager playMusicManager) {
        mPlayMusicManager = playMusicManager;
    }

    /**
     * Gets the index of the column
     * @param columns Table header
     * @param column Column
     * @return Index
     */
    protected static int getColumnsIndex(String[] columns, String column) {
        for (int n=0; n<columns.length; n++) {
            // Found column
            if (columns[n].equals(column))
                return n;
        }

        // Not found
        return -1;
    }

    protected abstract T getDataObject(Cursor cursor);

    /**
     * Loads all items from the database
     * @param table The table
     * @param columns All columns
     * @param where The where-command
     * @return Returns a list with all items
     */
    protected List<T> getItems(String table, String[] columns, String where) {
        return getItems(table, columns, where, null);
    }

    /**
     * Loads all items from the database
     * @param table The table
     * @param columns All columns
     * @param where The where-command
     * @param orderBy Order
     * @return Returns a list with all items
     */
    protected List<T> getItems(String table, String[] columns, String where, String orderBy) {
        // No connection; abort
        if (!mPlayMusicManager.getDatabase().isOpen()) return null;

        // Creates the list
        List<T> items = new LinkedList<>();

        try {
            // Gets the first data row
            Cursor cursor = mPlayMusicManager.getDatabase().query(table, columns, where, null, null, null, orderBy);

            // SQL error
            if (cursor == null) return null;

            cursor.moveToFirst();

            int pos = 0;
            // Reads the table
            while (!cursor.isAfterLast()) {
                pos ++;

                // Adds the object
                items.add(getDataObject(cursor));

                // Go to next data row
                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return items;
    }

    /**
     * Loads one item from the database
     * @param table The table
     * @param columns All columns
     * @param where The where-command
     * @return Returns the item
     */
    protected T getItem(String table, String[] columns, String where) {
        return getItem(table, columns, where, null);
    }

    /**
     * Loads one item from the database
     * @param table The table
     * @param columns All columns
     * @param where The where-command
     * @param orderBy Order
     * @return Returns the item
     */
    protected T getItem(String table, String[] columns, String where, String orderBy) {
        // Loads the list
        List<T> items = getItems(table, columns, where, orderBy);

        // Gets the first item
        if (items.size() > 0)
            return items.get(0);
        else
            return null;
    }

    /**
     * Combines two SQL where commands with an AND operator
     * @param whereA Where command A
     * @param whereB Where command B
     * @return Returns a combined where command
     */
    protected static String combineWhere(String whereA, String whereB) {
        return combineWhere(whereA, whereB, "AND");
    }

    /**
     * Combines two SQL where commands
     * @param whereA Where command A
     * @param whereB Where command B
     * @param operator The operator word to use (AND or OR)
     * @return Returns a combined where command
     */
    protected static String combineWhere(String whereA, String whereB, String operator) {
        // Combine both
        if (!TextUtils.isEmpty(whereA) && !TextUtils.isEmpty(whereB))
            return "((" + whereA + ") " + operator + " (" + whereB + "))";

        // Use only whereA
        if (!TextUtils.isEmpty(whereA) && TextUtils.isEmpty(whereB))
            return whereA;

        // Use only whereB
        if (TextUtils.isEmpty(whereA) && !TextUtils.isEmpty(whereB))
            return whereB;

        // No where is set
        return "";
    }
}
