/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.net.telnet;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps an output stream.
 * <p>
 * In binary mode, the only conversion is to double IAC.
 * <p>
 * In ASCII mode, if convertCRtoCRLF is true (currently always true), any CR is converted to CRLF.
 * IACs are doubled.
 * Also a bare LF is converted to CRLF and a bare CR is converted to CR\0
 * <p>
 */


final class TelnetOutputStream extends OutputStream
{
    private final TelnetClient client;
    // TODO there does not appear to be any way to change this value - should it be a ctor parameter?
    private final boolean convertCRtoCRLF = true;
    private boolean lastWasCR;

    TelnetOutputStream(final TelnetClient client)
    {
        this.client = client;
    }


    /**
     * Writes a byte to the stream.
     * <p>
     * @param ch The byte to write.
     * @throws IOException If an error occurs while writing to the underlying
     *            stream.
     */
    @Override
    public void write(int ch) throws IOException
    {

        synchronized (client)
        {
            ch &= 0xff;

            if (client.requestedWont(TelnetOption.BINARY)) // i.e. ASCII
            {
                if (lastWasCR)
                {
                    if (convertCRtoCRLF)
                    {
                        client.sendByte('\n');
                        if (ch == '\n') // i.e. was CRLF anyway
                        {
                            lastWasCR = false;
                            return ;
                        }
                    } // __convertCRtoCRLF
                    else if (ch != '\n')
                     {
                        client.sendByte('\0'); // RFC854 requires CR NUL for bare CR
                    }
                }

                switch (ch)
                {
                case '\r':
                    client.sendByte('\r');
                    lastWasCR = true;
                    break;
                case '\n':
                    if (!lastWasCR) { // convert LF to CRLF
                        client.sendByte('\r');
                    }
                    client.sendByte(ch);
                    lastWasCR = false;
                    break;
                case TelnetCommand.IAC:
                    client.sendByte(TelnetCommand.IAC);
                    client.sendByte(TelnetCommand.IAC);
                    lastWasCR = false;
                    break;
                default:
                    client.sendByte(ch);
                    lastWasCR = false;
                    break;
                }
            } // end ASCII
            else if (ch == TelnetCommand.IAC)
            {
                client.sendByte(ch);
                client.sendByte(TelnetCommand.IAC);
            } else {
                client.sendByte(ch);
            }
        }
    }


    /**
     * Writes a byte array to the stream.
     * <p>
     * @param buffer  The byte array to write.
     * @throws IOException If an error occurs while writing to the underlying
     *            stream.
     */
    @Override
    public void write(final byte buffer[]) throws IOException
    {
        write(buffer, 0, buffer.length);
    }


    /**
     * Writes a number of bytes from a byte array to the stream starting from
     * a given offset.
     * <p>
     * @param buffer  The byte array to write.
     * @param offset  The offset into the array at which to start copying data.
     * @param length  The number of bytes to write.
     * @throws IOException If an error occurs while writing to the underlying
     *            stream.
     */
    @Override
    public void write(final byte buffer[], int offset, int length) throws IOException
    {
        synchronized (client)
        {
            while (length-- > 0) {
                write(buffer[offset++]);
            }
        }
    }

    /** Flushes the stream. */
    @Override
    public void flush() throws IOException
    {
        client.flushOutputStream();
    }

    /** Closes the stream. */
    @Override
    public void close() throws IOException
    {
        client.closeOutputStream();
    }
}
