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
package org.apache.axiom.ts.xml;

import java.io.InputStream;
import java.net.URL;

import org.apache.axiom.testing.multiton.Multiton;

public abstract class MessageSample extends Multiton {
    private final MessageContent content;

    public MessageSample(MessageContent content) {
        this.content = content;
    }

    public abstract String getContentType();

    /**
     * Get the content of this message.
     * 
     * @return an input stream with the content of this message
     */
    public final InputStream getInputStream() {
        return content.getInputStream();
    }
    
    public final URL getUrl() {
        return content.getURL();
    }
}
