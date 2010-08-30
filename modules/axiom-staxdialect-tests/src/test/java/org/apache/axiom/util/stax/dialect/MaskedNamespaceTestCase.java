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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class MaskedNamespaceTestCase extends DialectTestCase {
    protected void runTest() throws Throwable {
        XMLInputFactory factory = newNormalizedXMLInputFactory();
        XMLStreamReader reader = factory.createXMLStreamReader(
                MaskedNamespaceTestCase.class.getResourceAsStream("masked_namespace.xml"));
        reader.nextTag();
        reader.nextTag();
        assertEquals(XMLStreamReader.START_ELEMENT, reader.getEventType());
        NamespaceContext nc = reader.getNamespaceContext();
        assertEquals("urn:ns2", nc.getNamespaceURI("p"));
        assertNull(nc.getPrefix("urn:ns1"));
        assertFalse(nc.getPrefixes("urn:ns1").hasNext());
    }
}
