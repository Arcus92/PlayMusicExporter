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

import java.util.ArrayList;
import java.util.List;

/**
 * A dynamic growing byte buffer
 */
public class ByteBuffer {
    private int mChunkSize = 4096;
    private int mSize = 0;
    private List<byte[]> mChunks = new ArrayList<>();

    /**
     * Creates a byte buffer
     */
    public ByteBuffer() {
    }

    /**
     * Creates a byte buffer
     * @param chunkSize Sets the chunks size
     */
    public ByteBuffer(int chunkSize) {
        mChunkSize = chunkSize;
    }
    /**
     * @return Returns the size of the buffer
     */
    public long size() {
        return mSize;
    }

    /**
     * Clears the buffer
     */
    public void clear() {
        mSize = 0;
        mChunks.clear();
    }

    /**
     * Adds bytes to the buffer
     * @param bytes The bytes to add
     */
    public void append(byte[] bytes, int offset, int length) {


        while(length > 0) {
            int freeSpaceInEndChunk = mChunks.size() * mChunkSize - mSize;
            int positionInEndChunk = mChunkSize - freeSpaceInEndChunk;

            // Adds a new chunk
            if (freeSpaceInEndChunk == 0) {
                mChunks.add(new byte[mChunkSize]);
                freeSpaceInEndChunk = mChunkSize;
                positionInEndChunk = 0;
            }
            byte[] endChunk = mChunks.get(mChunks.size() - 1);

            // We want to add more bytes the chunk can contain
            if (length > freeSpaceInEndChunk) {

                // Copies to end of chunk
                System.arraycopy(bytes, offset, endChunk, positionInEndChunk, freeSpaceInEndChunk);

                // Adds the size
                mSize += freeSpaceInEndChunk;
                offset += freeSpaceInEndChunk;
                length -= freeSpaceInEndChunk;
            } else {
                // Copies the rest of the data
                System.arraycopy(bytes, offset, endChunk, positionInEndChunk, length);

                // Adds the size
                mSize += length;

                offset += length;
                length = 0;
            }
        }
    }

    /**
     * @return Returns the bytes as array
     */
    public byte[] toByteArray() {
        byte[] bytes = new byte[mSize];

        int pos = 0;

        // Copies all chunks
        for(byte[] subBytes : mChunks) {
            int chunkSize = mChunkSize;
            // Last segment
            if (pos + chunkSize > mSize)
                chunkSize = mSize - pos;

            // Copies the bytes
            System.arraycopy(subBytes, 0, bytes, pos, chunkSize);

            pos += chunkSize;
        }

        return bytes;
    }
}
