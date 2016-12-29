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
package org.apache.axiom.om.impl.stream.xop;

import java.io.IOException;

import javax.activation.DataHandler;

import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.xop.AbstractXOPDecodingFilterHandler;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.mime.MimePartProvider;
import org.apache.axiom.om.impl.intf.TextContent;

final class XOPDecodingFilterHandler extends AbstractXOPDecodingFilterHandler {
    private static class DataHandlerProviderImpl implements DataHandlerProvider {
        private final MimePartProvider mimePartProvider;
        private final String contentID;
        
        public DataHandlerProviderImpl(MimePartProvider mimePartProvider, String contentID) {
            this.mimePartProvider = mimePartProvider;
            this.contentID = contentID;
        }

        @Override
        public DataHandler getDataHandler() throws IOException {
            return mimePartProvider.getDataHandler(contentID);
        }
    }

    private enum State {
        AFTER_START_ELEMENT, CONTENT_SEEN, IN_XOP_INCLUDE, AFTER_XOP_INCLUDE
    }

    private final MimePartProvider mimePartProvider;

    XOPDecodingFilterHandler(XmlHandler parent, MimePartProvider mimePartProvider) {
        super(parent);
        this.mimePartProvider = mimePartProvider;
    }

    @Override
    protected Object buildCharacterData(String contentID) {
        return new TextContent(contentID, new DataHandlerProviderImpl(mimePartProvider, contentID), true);
    }
}
