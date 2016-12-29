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
package org.apache.axiom.ts.soap12.faultcode;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFaultClassifier;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests the behavior of {@link SOAPFaultClassifier#setValue(QName)} when invoked on an empty SOAP
 * 1.2 {@link SOAPFaultCode}.
 */
public class TestSetValueFromQName extends SOAPTestCase {
    public TestSetValueFromQName(OMMetaFactory metaFactory) {
        super(metaFactory, SOAPSpec.SOAP12);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPFaultCode code = soapFactory.createSOAPFaultCode();
        code.setValue(new QName("urn:test", "MyFaultCode", "p"));
        SOAPFaultValue value = code.getValue();
        assertNotNull(value);
        assertEquals("p:MyFaultCode", value.getText());
        Iterator<OMNamespace> it = value.getAllDeclaredNamespaces();
        assertTrue(it.hasNext());
        OMNamespace ns = it.next();
        assertEquals("p", ns.getPrefix());
        assertEquals("urn:test", ns.getNamespaceURI());
    }
}
