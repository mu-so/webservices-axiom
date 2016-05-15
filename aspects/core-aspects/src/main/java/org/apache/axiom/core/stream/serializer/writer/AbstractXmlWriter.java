/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axiom.core.stream.serializer.writer;

import java.io.IOException;
import java.io.OutputStream;

abstract class AbstractXmlWriter extends XmlWriter {
    private final OutputStream out;
    private final byte[] buffer = new byte[4096];
    private int len;
    private char highSurrogate;
    
    AbstractXmlWriter(OutputStream out) {
        this.out = out;
    }

    protected abstract void writeCharacter(int codePoint) throws IOException;

    protected final void writeByte(byte b) throws IOException {
        if (len == buffer.length) {
            flushBuffer();
        }
        buffer[len++] = b;
    }

    @Override
    public final void write(char c) throws IOException {
        if (highSurrogate != 0) {
            if (Character.isLowSurrogate(c)) {
                int codePoint = Character.toCodePoint(highSurrogate, c);
                // Need to reset highSurrogate before writing because the character
                // may be unmappable, resulting in a character reference being written
                // (which means that this method must be reentrant).
                highSurrogate = 0;
                writeCharacter(codePoint);
            } else {
                throw new IOException("Invalid surrogate pair");
            }
        } else if (Character.isHighSurrogate(c)) {
            highSurrogate = c;
        } else if (Character.isLowSurrogate(c)) {
            throw new IOException("Invalid surrogate pair");
        } else {
            writeCharacter(c);
        }
    }

    @Override
    public final void write(String s) throws IOException {
        for (int i=0, len=s.length(); i<len; i++) {
            write(s.charAt(i));
        }
    }

    @Override
    public final void write(char[] chars, int start, int length) throws IOException {
        for (int i=0; i<length; i++) {
            write(chars[start+i]);
        }
    }

    @Override
    public final void flushBuffer() throws IOException {
        out.write(buffer, 0, len);
        len = 0;
    }
}
