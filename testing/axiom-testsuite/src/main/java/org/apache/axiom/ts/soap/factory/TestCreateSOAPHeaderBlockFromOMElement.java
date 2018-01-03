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
package org.apache.axiom.ts.soap.factory;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestCreateSOAPHeaderBlockFromOMElement extends SOAPTestCase {
    public TestCreateSOAPHeaderBlockFromOMElement(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement original = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), new StringReader(
                "<wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">"
                + "http://fabrikam123.example/Purchasing</wsa:To>")).getDocumentElement();
        SOAPHeaderBlock headerBlock = soapFactory.createSOAPHeaderBlock(original);
        assertAbout(xml())
                .that(xml(OMElement.class, headerBlock))
                .hasSameContentAs(xml(OMElement.class, original));
    }
}
