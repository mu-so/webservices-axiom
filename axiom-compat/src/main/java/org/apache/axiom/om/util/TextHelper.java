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

package org.apache.axiom.om.util;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;

import org.apache.axiom.om.OMText;
import org.apache.axiom.util.base64.Base64EncodingStringBufferOutputStream;
import org.apache.axiom.util.base64.Base64Utils;

/**
 * @deprecated Class containing only deprecated utility methods.
 */
public class TextHelper {
    /**
     * @deprecated This method was internally used by Axiom before version 1.2.9 but is no longer
     *             required.
     */
    public static String toString(InputStream inStream) throws IOException {
        StringBuffer buffer = new StringBuffer();
        toStringBuffer(inStream, buffer);
        return buffer.toString();
    }
    
    /**
     * @deprecated This method was internally used by Axiom before version 1.2.9 but is no longer
     *             required.
     */
    public static void toStringBuffer(InputStream inStream, StringBuffer buffer) throws IOException {
        int avail = inStream.available();
        
        // The Base64 will increase the size by 1.33 + some additional 
        // space at the data byte[] boundaries.  So a factor of 1.35 is used
        // to ensure capacity.
        if (avail > 0) {
            buffer.ensureCapacity((int) (avail* 1.35) + buffer.length());
        }
        
        // The size of the buffer must be a multiple of 3. Otherwise usage of the
        // stateless Base64 class would produce filler characters inside the Base64
        // encoded text.
        byte[] data = new byte[1023];
        boolean eos = false;
        do {
            int len = 0;
            do {
                // Always fill the buffer entirely (unless the end of the stream has
                // been reached); see above.
                int read = inStream.read(data, len, data.length-len);
                if (read == -1) {
                    eos = true;
                    break;
                }
                len += read;
            } while (len < data.length);
            Base64Utils.encode(data, 0, len, buffer);
        } while (!eos);
    }
    
    /**
     * @deprecated If you really need to write the base64 encoded content of an {@link OMText}
     *             instance to a {@link StringBuffer}, then request the {@link DataHandler} using
     *             {@link OMText#getDataHandler()} and use
     *             {@link Base64EncodingStringBufferOutputStream} to encode it.
     */
    public static void toStringBuffer(OMText omText, StringBuffer buffer) throws IOException {
        // If an InputStream is present, stream the BASE64 text to the StreamBuffer
        if (omText.isOptimized()) {
           DataHandler dh = omText.getDataHandler();
           if (dh != null) {
               InputStream is = dh.getInputStream();
               if (is != null) {
                   toStringBuffer(is, buffer);
                   return;
               }
           }
        }
        
        // Otherwise append the text
        buffer.append(omText.getText());
        return;
    }
    
}
