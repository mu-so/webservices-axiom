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
package org.apache.axiom.ts.saaj;

import java.io.InputStream;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.TestMessageSet;

public class TestExamineMustUnderstandHeaderElements extends SAAJTestCase {
    private final SOAPSpec spec;
    private final boolean dynamic;
    
    public TestExamineMustUnderstandHeaderElements(SAAJImplementation saajImplementation, SOAPSpec spec, boolean dynamic) {
        super(saajImplementation);
        this.spec = spec;
        this.dynamic = dynamic;
        addTestParameter("spec", spec.getName());
        addTestParameter("dynamic", dynamic);
    }

    @Override
    protected void runTest() throws Throwable {
        MessageFactory messageFactory = spec.getAdapter(FactorySelector.class).newMessageFactory(saajImplementation, dynamic);
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type", spec.getContentType());
        InputStream in = TestMessageSet.MUST_UNDERSTAND.getMessage(spec).getInputStream();
        try {
            SOAPMessage message = messageFactory.createMessage(mimeHeaders, in);
            SOAPHeader header = message.getSOAPHeader();
            Iterator it = header.examineMustUnderstandHeaderElements(null);
            assertTrue(it.hasNext());
            assertTrue(it.next() instanceof SOAPHeaderElement);
            assertFalse(it.hasNext());
        } finally {
            in.close();
        }
    }
}
