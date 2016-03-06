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
package org.apache.axiom.om.impl.stream.stax;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.stream.stax.InternalXMLStreamReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.impl.intf.TextContent;

final class DataHandlerReaderImpl implements DataHandlerReader {
    private final InternalXMLStreamReader reader;

    DataHandlerReaderImpl(InternalXMLStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public boolean isBinary() {
        if (reader.getEventType() == XMLStreamReader.CHARACTERS) {
            Object data = reader.getCharacterData();
            return data instanceof TextContent && ((TextContent)data).isBinary();
        } else {
            return false;
        }
    }

    @Override
    public boolean isOptimized() {
        return ((TextContent)reader.getCharacterData()).isOptimize();
    }

    @Override
    public boolean isDeferred() {
        return ((TextContent)reader.getCharacterData()).getDataHandlerObject() instanceof DataHandlerProvider;
    }

    @Override
    public String getContentID() {
        return ((TextContent)reader.getCharacterData()).getContentID();
    }

    @Override
    public DataHandler getDataHandler() throws XMLStreamException {
        return ((TextContent)reader.getCharacterData()).getDataHandler();
    }

    @Override
    public DataHandlerProvider getDataHandlerProvider() {
        return (DataHandlerProvider)((TextContent)reader.getCharacterData()).getDataHandlerObject();
    }
}
