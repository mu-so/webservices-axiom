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

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Test that calling {@link OMElement#addAttribute(OMAttribute)} with an attribute that is already
 * owned by the element is a no-op.
 */
public class TestAddAttributeAlreadyOwnedByElement extends AxiomTestCase {
    public TestAddAttributeAlreadyOwnedByElement(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = factory.createOMElement(new QName("test"));
        OMAttribute att = element.addAttribute("test", "test", null);
        OMAttribute result = element.addAttribute(att);
        assertSame(result, att);
        assertSame(element, att.getOwner());
        Iterator<OMAttribute> it = element.getAllAttributes();
        assertTrue(it.hasNext());
        assertSame(att, it.next());
        assertFalse(it.hasNext());
    }
}
