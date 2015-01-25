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

package de.arcus.playmusiclib;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Exports encrypted music files from Google Music All Access
 */
public class AllAccessExporter {
    private Cipher mCipher;
    private SecretKeySpec mKeySpec;
    private InputStream mInput;
    private OutputStream mOutput;
    private byte[] mMagicNumber;

    static final int BUFFER_SIZE = 1024;
    static final byte[] MAGIC_NUMBER = { 18, -45, 21, 39 };

    /**
     * Creates a new exporter for AllAccess files
     *
     * @param input The encrypted music file (Copy the file into a readable directory before you use them.)
     * @param cpData The 16 byte long key (CpData-Blob in MUSIC table)
     * @throws NoSuchAlgorithmException Encryption method is not supported on this device
     * @throws NoSuchPaddingException Padding mode is not supported on this device
     * @throws IOException File could not be read (This class does not support SU commands; first copy your file into a readable directory)
     */
    public AllAccessExporter(String input, byte[] cpData) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException
    {
        // Select encryption mode
        mCipher = Cipher.getInstance("AES/CTR/NoPadding");
        mKeySpec = new SecretKeySpec(cpData, "AES");

        // Opens the source file
        mInput = new FileInputStream(input);

        // Reads the first 4 bytes = MagicNumber
        mMagicNumber = new byte[4];
        if (mInput.read(mMagicNumber) != 4)
            mMagicNumber = null;
    }

    /**
     * Checks whether the magic number of the file is correct
     *
     * @return Returns whether the Magic Number is valid
     */
    public boolean hasValidMagicNumber()
    {
        if (mMagicNumber == null)
            return false;

        if (mMagicNumber.length != 4)
            return false;

        for(int i=0; i<4; i++)
            if (mMagicNumber[i] != MAGIC_NUMBER[i])
                return false;

        return true;
    }

    /**
     * Saves an unencrypted copy of the music file as an Mp3
     * @param filename The path to the target file
     * @return Returns whether the file was successfully saved
     */
    public boolean save(String filename)
    {
        try {
            // Opens the target file
            mOutput = new FileOutputStream(filename);

            byte[] output = new byte[BUFFER_SIZE];

            // Reads all blocks of the file
            while(mInput.available() > 0) {
                int size = read(output);
                if (size > 0) {
                    mOutput.write(output, 0, size);
                } else
                    break;
            }

            // Close the files
            mInput.close();
            mOutput.close();

            // Everything went according to plan
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Try to close the files if an error occurs
        try {
            if (mInput != null)
                mInput.close();
            if (mOutput != null)
                mOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // An error has Occurred
        return false;
    }

    /**
     * Reads a block and decrypts it
     * @param output output buffer
     * @return Returns the length of the output buffer
     * @throws Exception
     */
    private int read(byte[] output) throws Exception
    {
        byte[] mBuffer = new byte[BUFFER_SIZE];
        byte[] mIvBuffer = new byte[16];

        int pos = 0;

        // Fills the buffer
        while (pos < BUFFER_SIZE)
        {
            int size = mInput.read(mBuffer, pos, BUFFER_SIZE - pos);
            // There is nothing more to read
            if (size == -1)
            {
                // Error, there was nothing more to read
                if (pos == 0)
                    return -1;

                // Buffer is filled
                if (pos > 16)
                    break;

                // Error, block is smaller than 16 bytes
                return -1;
            }
            pos += size;
        }

        // The first 16 bytes of the block are the initialization vector
        System.arraycopy(mBuffer, 0, mIvBuffer, 0, 16);
        IvParameterSpec ivParameter = new IvParameterSpec(mIvBuffer);

        // The remaining bytes are the encrypted data
        int decodeSize = pos - 16;

        try
        {
            // Initializes the encryption method
            mCipher.init(Cipher.DECRYPT_MODE, mKeySpec, ivParameter);

            // Decrypts the block
            if (mCipher.doFinal(mBuffer, 16, decodeSize, output, 0) != decodeSize)
                throw new IllegalStateException("Wrong block size");
        }
        catch (Exception ex)
        {
            // Unexpected error
            throw new Exception("Unexpected error while decrypting", ex);
        }

        // Returns the block size
        return decodeSize;
    }
}