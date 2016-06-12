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

package org.apache.axiom.util.stax.xop;

import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.impl.builder.AttachmentsMimePartProvider;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.testutils.stax.XMLStreamReaderComparator;
import org.apache.axiom.ts.soap.MTOMSample;

public class XOPEncodingStreamReaderTest extends TestCase {
    private static ContentIDGenerator contentIDGenerator = new ContentIDGenerator() {
        public String generateContentID(String existingContentID) {
            if (existingContentID == null) {
                fail();
            }
            return existingContentID;
        }
    };
    
    public void test() throws Exception {
        Attachments[] attachments = new Attachments[2];
        XMLStreamReader[] soapPartReader = new XMLStreamReader[2];
        for (int i=0; i<2; i++) {
            attachments[i] = new Attachments(MTOMSample.SAMPLE1.getInputStream(),
                    MTOMSample.SAMPLE1.getContentType());
            soapPartReader[i] = StAXUtils.createXMLStreamReader(attachments[i].getRootPartInputStream());
        }
        XMLStreamReader actual = new XOPEncodingStreamReader(new XOPDecodingStreamReader(soapPartReader[1], new AttachmentsMimePartProvider(attachments[1])), contentIDGenerator, OptimizationPolicy.DEFAULT);
        new XMLStreamReaderComparator(soapPartReader[0], actual).compare();
        for (int i=0; i<2; i++) {
            soapPartReader[i].close();
        }
    }

}
