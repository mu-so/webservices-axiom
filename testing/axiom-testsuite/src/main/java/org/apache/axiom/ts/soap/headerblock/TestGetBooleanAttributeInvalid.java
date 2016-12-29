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
package org.apache.axiom.ts.soap.headerblock;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.ts.soap.HeaderBlockAttribute;
import org.apache.axiom.ts.soap.BooleanAttributeAccessor;
import org.apache.axiom.ts.soap.SOAPSpec;

/**
 * Tests that {@link SOAPHeaderBlock#getMustUnderstand()} (resp. {@link SOAPHeaderBlock#getRelay()})
 * throws {@link SOAPProcessingException} if a <tt>mustUnderstand</tt> (resp. <tt>relay</tt>)
 * attribute is present but has an invalid value.
 */
public class TestGetBooleanAttributeInvalid extends BooleanAttributeTestCase {
    private final String value;
    
    public TestGetBooleanAttributeInvalid(OMMetaFactory metaFactory, SOAPSpec spec, HeaderBlockAttribute attribute, String value) {
        super(metaFactory, spec, attribute);
        this.value = value;
        addTestParameter("value", value);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPHeader header = soapFactory.getDefaultEnvelope().getOrCreateHeader();
        SOAPHeaderBlock headerBlock = header.addHeaderBlock(new QName("urn:test", "test", "p"));
        headerBlock.addAttribute(attribute.getName(spec), value, header.getNamespace());
        try {
            attribute.getAdapter(BooleanAttributeAccessor.class).getValue(headerBlock);
            fail("Expected SOAPProcessingException");
        } catch (SOAPProcessingException ex) {
            // Expected
        }
    }
}
