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
package org.apache.axiom.ts.om.element;

import java.util.Iterator;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that an attempt to iterate over the child elements using
 * {@link OMElement#getChildElements()} results in the expected exception if the part of the
 * document has already been consumed by {@link OMContainer#getXMLStreamReaderWithoutCaching()}.
 */
public class TestGetChildElementsConsumed extends AxiomTestCase {
    public TestGetChildElementsConsumed(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(
                metaFactory.getOMFactory(),
                TestGetChildElementsConsumed.class.getResourceAsStream("purchase-order.xml"));

        OMElement documentElement = builder.getDocumentElement();
        XMLStreamReader reader = documentElement.getXMLStreamReaderWithoutCaching();

        //consume the parser. this should force the xml stream to be exhausted without
        //building the tree
        while (reader.hasNext()) {
            reader.next();
        }

        //try to find the children of the document element. This should produce an
        //error since the underlying stream is fully consumed without building the object tree
        try {
            Iterator<OMElement> childElements = documentElement.getChildElements();
            while (childElements.hasNext()) {
                childElements.next();
            }
            fail("Expected NodeUnavailableException");
        } catch (NodeUnavailableException ex) {
            // Expected
        }
        
        documentElement.close(false);
    }
}
