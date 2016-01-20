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

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that when {@link OMElement#addAttribute(String, String, OMNamespace)} is called with an
 * {@link OMNamespace} with a <code>null</code> prefix and no namespace declaration for the given
 * namespace URI is in scope, the method generates a prefix.
 */
public class TestAddAttributeGeneratedPrefix extends AxiomTestCase {
    private final boolean defaultNamespaceInScope;
    
    public TestAddAttributeGeneratedPrefix(OMMetaFactory metaFactory, boolean defaultNamespaceInScope) {
        super(metaFactory);
        this.defaultNamespaceInScope = defaultNamespaceInScope;
        addTestParameter("defaultNamespaceInScope", defaultNamespaceInScope);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMNamespace otherNS = factory.createOMNamespace("urn:ns2", "p");
        OMElement parent = factory.createOMElement("parent", otherNS);
        if (defaultNamespaceInScope) {
            parent.declareDefaultNamespace("urn:test");
        }
        OMElement element = factory.createOMElement("test", otherNS, parent);
        OMAttribute attr = element.addAttribute("attr", "value", factory.createOMNamespace("urn:test", null));
        OMNamespace ns = attr.getNamespace();
        assertTrue(ns.getPrefix().length() > 0);
        Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
        assertTrue(it.hasNext());
        assertEquals(ns, it.next());
        assertFalse(it.hasNext());
    }
}
