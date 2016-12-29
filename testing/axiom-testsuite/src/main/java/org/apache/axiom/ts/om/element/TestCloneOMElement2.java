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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.InputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.ConformanceTestCase;
import org.apache.axiom.ts.xml.XMLSample;

public class TestCloneOMElement2 extends ConformanceTestCase {
    public TestCloneOMElement2(OMMetaFactory metaFactory, XMLSample file) {
        super(metaFactory, file);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        InputStream in = file.getInputStream();
        try {
            OMElement original = OMXMLBuilderFactory.createOMBuilder(factory, TEST_PARSER_CONFIGURATION, in).getDocumentElement();
            OMElement clone = original.cloneOMElement();
            assertAbout(xml())
                    .that(xml(OMElement.class, clone))
                    .hasSameContentAs(xml(OMElement.class, original));
        } finally {
            in.close();
        }
    }
}
