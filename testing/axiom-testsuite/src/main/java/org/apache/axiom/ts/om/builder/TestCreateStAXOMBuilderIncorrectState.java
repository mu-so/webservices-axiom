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
package org.apache.axiom.ts.om.builder;

import java.io.StringReader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMXMLBuilderFactory#createStAXOMBuilder(OMFactory, XMLStreamReader)} throws an
 * exception if the supplied {@link XMLStreamReader} is not positioned on a
 * {@link XMLStreamConstants#START_DOCUMENT} or {@link XMLStreamConstants#END_DOCUMENT} event.
 */
public class TestCreateStAXOMBuilderIncorrectState extends AxiomTestCase {
    public TestCreateStAXOMBuilderIncorrectState(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<root>text</root>"));
        // Position the reader on a CHARACTERS event
        while (reader.getEventType() != XMLStreamReader.CHARACTERS) {
            reader.next();
        }
        try {
            OMXMLBuilderFactory.createStAXOMBuilder(metaFactory.getOMFactory(), reader);
            fail("Expected OMException");
        } catch (OMException ex) {
            // Expected
        }
    }
}
