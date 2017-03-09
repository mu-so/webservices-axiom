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
package org.apache.axiom.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.axiom.mime.Header;
import org.apache.axiom.util.base64.Base64EncodingOutputStream;

public final class MultipartBodyWriter {
    class PartOutputStream extends OutputStream {
        private final OutputStream parent;

        public PartOutputStream(OutputStream parent) {
            this.parent = parent;
        }

        @Override
        public void write(int b) throws IOException {
            parent.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            parent.write(b, off, len);
        }

        @Override
        public void write(byte[] b) throws IOException {
            parent.write(b);
        }
        
        @Override
        public void close() throws IOException {
            if (parent instanceof Base64EncodingOutputStream) {
                ((Base64EncodingOutputStream)parent).complete();
            }
            writeAscii("\r\n");
        }
    }
    
    private final OutputStream out;
    private final String boundary;
    private final byte[] buffer = new byte[256];

    public MultipartBodyWriter(OutputStream out, String boundary) {
        this.out = out;
        this.boundary = boundary;
    }

    void writeAscii(String s) throws IOException {
        int count = 0;
        for (int i=0, len=s.length(); i<len; i++) {
            char c = s.charAt(i);
            if (c >= 128) {
                throw new IOException("Illegal character '" + c + "'");
            }
            buffer[count++] = (byte)c;
            if (count == buffer.length) {
                out.write(buffer);
                count = 0;
            }
        }
        if (count > 0) {
            out.write(buffer, 0, count);
        }
    }
    
    /**
     * Start writing a MIME part. The methods returns an {@link OutputStream} that the caller can
     * use to write the content of the MIME part. After writing the content,
     * {@link OutputStream#close()} must be called to complete the writing of the MIME part.
     * 
     * @param contentType
     *            the value of the <tt>Content-Type</tt> header of the MIME part
     * @param contentTransferEncoding
     *            the content transfer encoding to be used (see above); must not be
     *            <code>null</code>
     * @param contentID
     *            the content ID of the MIME part (see above)
     * @param extraHeaders
     *            a list of {@link Header} objects with additional headers to write to the MIME part
     * @return an output stream to write the content of the MIME part
     * @throws IOException
     *             if an I/O error occurs when writing to the underlying stream
     */
    public OutputStream writePart(String contentType, String contentTransferEncoding,
            String contentID, List<Header> extraHeaders) throws IOException {
        OutputStream transferEncoder;
        if (contentTransferEncoding.equals("8bit") || contentTransferEncoding.equals("binary")) {
            transferEncoder = out;
        } else {
            // We support no content transfer encodings other than 8bit, binary and base64.
            transferEncoder = new Base64EncodingOutputStream(out);
            contentTransferEncoding = "base64";
        }
        writeAscii("--");
        writeAscii(boundary);
        // TODO: specify if contentType == null is legal and check what to do
        if (contentType != null) {
            writeAscii("\r\nContent-Type: ");
            writeAscii(contentType);
        }
        writeAscii("\r\nContent-Transfer-Encoding: ");
        writeAscii(contentTransferEncoding);
        // TODO: specify that the content ID may be null
        if (contentID != null) {
            writeAscii("\r\nContent-ID: <");
            writeAscii(contentID);
            out.write('>');
        }
        if (extraHeaders != null) {
            for (Header header : extraHeaders) {
                writeAscii("\r\n");
                writeAscii(header.getName());
                writeAscii(": ");
                writeAscii(header.getValue());
            }
        }
        writeAscii("\r\n\r\n");
        return new PartOutputStream(transferEncoder);
    }
    
    /**
     * Write a MIME part. The content is provided by a {@link DataHandler} object, which also
     * specifies the content type of the part.
     * 
     * @param dataHandler
     *            the content of the MIME part to write
     * @param contentTransferEncoding
     *            the content transfer encoding to be used (see above); must not be
     *            <code>null</code>
     * @param contentID
     *            the content ID of the MIME part (see above)
     * @param extraHeaders
     *            a list of {@link Header} objects with additional headers to write to the MIME part
     * @throws IOException
     *             if an I/O error occurs when writing the part to the underlying stream
     */
    public void writePart(DataHandler dataHandler, String contentTransferEncoding, String contentID, List<Header> extraHeaders)
            throws IOException {
        OutputStream partOutputStream = writePart(dataHandler.getContentType(), contentTransferEncoding, contentID, extraHeaders);
        dataHandler.writeTo(partOutputStream);
        partOutputStream.close();
    }
    
    /**
     * Complete writing of the MIME multipart package. This method does <b>not</b> close the
     * underlying stream.
     * 
     * @throws IOException
     *             if an I/O error occurs when writing to the underlying stream
     */
    public void complete() throws IOException {
        writeAscii("--");
        writeAscii(boundary);
        writeAscii("--\r\n");
    }
}
