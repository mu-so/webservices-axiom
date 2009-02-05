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

package org.apache.axiom.soap;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

public class SOAPFactoryTest extends AbstractTestCase {

    protected static final String SOAP11_FILE_NAME = "soap/soap11/soap11message.xml";
    protected static final String SOAP12_FILE_NAME = "soap/soap12message.xml";
    private static Log log = LogFactory.getLog(SOAPFactoryTest.class);

    public void testSOAPFactory() {

        try {
            SOAPEnvelope soapEnvelope =
                    (SOAPEnvelope) new StAXSOAPModelBuilder(XMLInputFactory.newInstance().
                            createXMLStreamReader(getTestResource(SOAP11_FILE_NAME)), null)
                            .getDocumentElement();
            assertTrue(soapEnvelope != null);

            soapEnvelope = (SOAPEnvelope) new StAXSOAPModelBuilder(XMLInputFactory.newInstance().
                    createXMLStreamReader(getTestResource(SOAP12_FILE_NAME)), null)
                    .getDocumentElement();
            assertTrue(soapEnvelope != null);
        } catch (XMLStreamException e) {
            fail("Can not load soap envelope. Exception = " + e);
        }

    }

}
