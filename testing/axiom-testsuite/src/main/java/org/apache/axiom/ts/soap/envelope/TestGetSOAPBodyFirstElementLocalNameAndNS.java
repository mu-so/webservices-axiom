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
package org.apache.axiom.ts.soap.envelope;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestGetSOAPBodyFirstElementLocalNameAndNS extends SOAPTestCase {
    private final QName qname;
    
    public TestGetSOAPBodyFirstElementLocalNameAndNS(OMMetaFactory metaFactory, SOAPSpec spec, QName qname) {
        super(metaFactory, spec);
        this.qname = qname;
        addTestParameter("prefix", qname.getPrefix());
        addTestParameter("uri", qname.getNamespaceURI());
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
        OMElement bodyElement = soapFactory.createOMElement(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix());
        envelope.getBody().addChild(bodyElement);
        assertEquals(qname.getLocalPart(), envelope.getSOAPBodyFirstElementLocalName());
        OMNamespace ns = envelope.getSOAPBodyFirstElementNS();
        if (qname.getNamespaceURI().length() == 0) {
            assertNull(ns);
        } else {
            assertEquals(qname.getNamespaceURI(), ns.getNamespaceURI());
            assertEquals(qname.getPrefix(), ns.getPrefix());
        }
    }
}
