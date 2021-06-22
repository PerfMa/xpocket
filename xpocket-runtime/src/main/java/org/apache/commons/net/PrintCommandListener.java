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

package org.apache.commons.net;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This is a support class for some of the example programs.  It is
 * a sample implementation of the ProtocolCommandListener interface
 * which just prints out to a specified stream all command/reply traffic.
 *
 * @since 2.0
 */

public class PrintCommandListener implements ProtocolCommandListener
{
    private final PrintWriter writer;
    private final boolean nologin;
    private final char eolMarker;
    private final boolean directionMarker;

    /**
     * Create the default instance which prints everything.
     *
     * @param stream where to write the commands and responses
     * e.g. System.out
     * @since 3.0
     */
    public PrintCommandListener(final PrintStream stream)
    {
        this(new PrintWriter(stream));
    }

    /**
     * Create an instance which optionally suppresses login command text
     * and indicates where the EOL starts with the specified character.
     *
     * @param stream where to write the commands and responses
     * @param suppressLogin if {@code true}, only print command name for login
     *
     * @since 3.0
     */
    public PrintCommandListener(final PrintStream stream, final boolean suppressLogin) {
        this(new PrintWriter(stream), suppressLogin);
    }

    /**
     * Create an instance which optionally suppresses login command text
     * and indicates where the EOL starts with the specified character.
     *
     * @param stream where to write the commands and responses
     * @param suppressLogin if {@code true}, only print command name for login
     * @param eolMarker if non-zero, add a marker just before the EOL.
     *
     * @since 3.0
     */
    public PrintCommandListener(final PrintStream stream, final boolean suppressLogin, final char eolMarker) {
        this(new PrintWriter(stream), suppressLogin, eolMarker);
    }

    /**
     * Create an instance which optionally suppresses login command text
     * and indicates where the EOL starts with the specified character.
     *
     * @param stream where to write the commands and responses
     * @param suppressLogin if {@code true}, only print command name for login
     * @param eolMarker if non-zero, add a marker just before the EOL.
     * @param showDirection if {@code true}, add {@code "> "} or {@code "< "} as appropriate to the output
     *
     * @since 3.0
     */
    public PrintCommandListener(final PrintStream stream, final boolean suppressLogin, final char eolMarker,
            final boolean showDirection) {
        this(new PrintWriter(stream), suppressLogin, eolMarker, showDirection);
    }

    /**
     * Create the default instance which prints everything.
     *
     * @param writer where to write the commands and responses
     */
    public PrintCommandListener(final PrintWriter writer)
    {
        this(writer, false);
    }

    /**
     * Create an instance which optionally suppresses login command text.
     *
     * @param writer where to write the commands and responses
     * @param suppressLogin if {@code true}, only print command name for login
     *
     * @since 3.0
     */
    public PrintCommandListener(final PrintWriter writer, final boolean suppressLogin)
    {
        this(writer, suppressLogin, (char) 0);
    }

    /**
     * Create an instance which optionally suppresses login command text
     * and indicates where the EOL starts with the specified character.
     *
     * @param writer where to write the commands and responses
     * @param suppressLogin if {@code true}, only print command name for login
     * @param eolMarker if non-zero, add a marker just before the EOL.
     *
     * @since 3.0
     */
    public PrintCommandListener(final PrintWriter writer, final boolean suppressLogin, final char eolMarker)
    {
        this(writer, suppressLogin, eolMarker, false);
    }

    /**
     * Create an instance which optionally suppresses login command text
     * and indicates where the EOL starts with the specified character.
     *
     * @param writer where to write the commands and responses
     * @param suppressLogin if {@code true}, only print command name for login
     * @param eolMarker if non-zero, add a marker just before the EOL.
     * @param showDirection if {@code true}, add {@code ">} " or {@code "< "} as appropriate to the output
     *
     * @since 3.0
     */
    public PrintCommandListener(final PrintWriter writer, final boolean suppressLogin, final char eolMarker,
            final boolean showDirection) {
        this.writer = writer;
        this.nologin = suppressLogin;
        this.eolMarker = eolMarker;
        this.directionMarker = showDirection;
    }

    @Override
    public void protocolCommandSent(final ProtocolCommandEvent event)
    {
        if (directionMarker) {
            writer.print("> ");
        }
        if (nologin) {
            final String cmd = event.getCommand();
            if ("PASS".equalsIgnoreCase(cmd) || "USER".equalsIgnoreCase(cmd)) {
                writer.print(cmd);
                writer.println(" *******"); // Don't bother with EOL marker for this!
            } else {
                final String IMAP_LOGIN = "LOGIN";
                if (IMAP_LOGIN.equalsIgnoreCase(cmd)) { // IMAP
                    String msg = event.getMessage();
                    msg=msg.substring(0, msg.indexOf(IMAP_LOGIN)+IMAP_LOGIN.length());
                    writer.print(msg);
                    writer.println(" *******"); // Don't bother with EOL marker for this!
                } else {
                    writer.print(getPrintableString(event.getMessage()));
                }
            }
        } else {
            writer.print(getPrintableString(event.getMessage()));
        }
        writer.flush();
    }

    private String getPrintableString(final String msg){
        if (eolMarker == 0) {
            return msg;
        }
        final int pos = msg.indexOf(SocketClient.NETASCII_EOL);
        if (pos > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(msg.substring(0,pos));
            sb.append(eolMarker);
            sb.append(msg.substring(pos));
            return sb.toString();
        }
        return msg;
    }

    @Override
    public void protocolReplyReceived(final ProtocolCommandEvent event)
    {
        if (directionMarker) {
            writer.print("< ");
        }
        writer.print(event.getMessage());
        writer.flush();
    }
}

