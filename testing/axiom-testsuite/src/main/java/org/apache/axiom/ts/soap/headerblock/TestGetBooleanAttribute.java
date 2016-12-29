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
import org.apache.axiom.ts.soap.HeaderBlockAttribute;
import org.apache.axiom.ts.soap.BooleanAttributeAccessor;
import org.apache.axiom.ts.soap.BooleanLiteral;
import org.apache.axiom.ts.soap.SOAPSpec;

public class TestGetBooleanAttribute extends BooleanAttributeTestCase {
    private final BooleanLiteral literal;
    
    public TestGetBooleanAttribute(OMMetaFactory metaFactory, SOAPSpec spec, HeaderBlockAttribute attribute, BooleanLiteral literal) {
        super(metaFactory, spec, attribute);
        this.literal = literal;
        addTestParameter("literal", literal.getLexicalRepresentation());
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPHeader header = soapFactory.getDefaultEnvelope().getOrCreateHeader();
        SOAPHeaderBlock headerBlock = header.addHeaderBlock(new QName("http://example.org", "test", "h"));
        headerBlock.addAttribute(attribute.getName(spec), literal.getLexicalRepresentation(), header.getNamespace());
        assertEquals(literal.getValue(), attribute.getAdapter(BooleanAttributeAccessor.class).getValue(headerBlock));
    }
}
