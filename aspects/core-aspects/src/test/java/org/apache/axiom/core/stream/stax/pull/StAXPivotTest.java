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
package org.apache.axiom.core.stream.stax.pull;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.core.stream.dom.DOMInput;
import org.apache.axiom.ts.jaxp.DOMImplementation;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StAXPivotTest {
    private StAXPivot pivot;

    @Before
    public void setUp() throws Exception {
        Document document = DOMImplementation.XERCES.newDocument();
        Element element = document.createElementNS("urn:test", "p:test");
        document.appendChild(element);
        element.appendChild(document.createEntityReference("ent"));
        pivot = new StAXPivot(null);
        pivot.setReader(new DOMInput(document, false).createReader(pivot));
    }
    
    @Test
    public void testSuccess() throws Exception {
        pivot.require(XMLStreamConstants.START_DOCUMENT, null, null);
        pivot.next();
        pivot.require(XMLStreamConstants.START_ELEMENT, "urn:test", "test");
        pivot.next();
        pivot.require(XMLStreamConstants.ENTITY_REFERENCE, null, "ent");
        pivot.next();
        pivot.require(XMLStreamConstants.END_ELEMENT, "urn:test", "test");
        pivot.next();
        pivot.require(XMLStreamConstants.END_DOCUMENT, null, null);
    }
    
    @Test(expected=XMLStreamException.class)
    public void testEventTypeMismatch() throws Exception {
        pivot.require(XMLStreamConstants.CHARACTERS, null, null);
    }
    
    @Test(expected=XMLStreamException.class)
    public void testLocalNameOnStartDocument() throws Exception {
        pivot.require(XMLStreamConstants.START_DOCUMENT, null, "test");
    }
    
    @Test(expected=XMLStreamException.class)
    public void testLocalNameMismatchOnStartElement() throws Exception {
        pivot.next();
        pivot.require(XMLStreamConstants.START_ELEMENT, "urn:test", "wrong_name");
    }
    
    @Test(expected=XMLStreamException.class)
    public void testNamespaceURIOnStartDocument() throws Exception {
        pivot.require(XMLStreamConstants.START_DOCUMENT, "http://example.org", null);
    }
    
    @Test(expected=XMLStreamException.class)
    public void testNamespaceURIMismatchOnStartElement() throws Exception {
        pivot.next();
        pivot.require(XMLStreamConstants.START_ELEMENT, "urn:wrong_uri", "test");
    }
}
