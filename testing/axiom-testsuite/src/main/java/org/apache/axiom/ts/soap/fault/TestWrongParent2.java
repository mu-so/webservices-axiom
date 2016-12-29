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
package org.apache.axiom.ts.soap.fault;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests that {@link OMNode#insertSiblingAfter(OMNode)} throws an exception if an attempt is made to
 * add a {@link SOAPFault} as a child of a SOAP element other than {@link SOAPBody}.
 */
public class TestWrongParent2 extends SOAPTestCase {
    public TestWrongParent2(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope parent = soapFactory.createSOAPEnvelope();
        OMElement child1 = soapFactory.createSOAPHeader(parent);
        SOAPFault fault = soapFactory.createSOAPFault();
        try {
            child1.insertSiblingAfter(fault);
            fail("Expected SOAPProcessingException");
        } catch (SOAPProcessingException ex) {
            // Expected
        }
    }
}
