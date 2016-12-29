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

package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

class Woodstox3Dialect extends AbstractStAXDialect {
    public static final Woodstox3Dialect INSTANCE = new Woodstox3Dialect();

    @Override
    public String getName() {
        return "Woodstox 3.x";
    }

    @Override
    public XMLInputFactory enableCDataReporting(XMLInputFactory factory) {
        // For Woodstox, this is sufficient
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        return factory;
    }

    @Override
    public XMLInputFactory disallowDoctypeDecl(XMLInputFactory factory) {
        return StAXDialectUtils.disallowDoctypeDecl(factory);
    }

    @Override
    public XMLInputFactory makeThreadSafe(XMLInputFactory factory) {
        // Woodstox' factories are designed to be thread safe
        return factory;
    }

    @Override
    public XMLOutputFactory makeThreadSafe(XMLOutputFactory factory) {
        // Woodstox' factories are designed to be thread safe
        return factory;
    }

    @Override
    public XMLStreamReader normalize(XMLStreamReader reader) {
        return new Woodstox3StreamReaderWrapper(reader);
    }

    @Override
    public XMLStreamWriter normalize(XMLStreamWriter writer) {
        return new Woodstox3StreamWriterWrapper(writer);
    }

    @Override
    public XMLInputFactory normalize(XMLInputFactory factory) {
        return new NormalizingXMLInputFactoryWrapper(factory, this);
    }

    @Override
    public XMLOutputFactory normalize(XMLOutputFactory factory) {
        return new Woodstox3OutputFactoryWrapper(factory, this);
    }
}
