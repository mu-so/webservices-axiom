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
package org.apache.axiom.om.impl.stream.stax.push;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.core.stream.stax.push.InternalXMLStreamWriter;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.impl.intf.TextContent;

public final class DataHandlerWriterImpl implements DataHandlerWriter {
    private final InternalXMLStreamWriter writer;

    public DataHandlerWriterImpl(InternalXMLStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize)
            throws IOException, XMLStreamException {
        writer.writeCharacterData(new TextContent(contentID, dataHandler, optimize));
    }

    @Override
    public void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID,
            boolean optimize) throws IOException, XMLStreamException {
        writer.writeCharacterData(new TextContent(contentID, dataHandlerProvider, optimize));
    }
}
