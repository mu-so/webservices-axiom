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
package org.apache.axiom.ts.om.factory;

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;

/**
 * Tests the behavior of the <code>createOMElement</code> methods in {@link OMFactory} when
 * requested to create an element without namespace and the specified namespace prefix is
 * <code>null</code>. Normally, a <code>null</code> prefix indicates that Axiom should generate a
 * prefix, but for an element without namespace this is not possible and an empty prefix must be
 * used instead.
 */
public class TestCreateOMElementWithoutNamespaceNullPrefix extends CreateOMElementTestCase {
    public TestCreateOMElementWithoutNamespaceNullPrefix(OMMetaFactory metaFactory, CreateOMElementVariant variant, CreateOMElementParentSupplier parentSupplier) {
        super(metaFactory, variant, parentSupplier);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = variant.createOMElement(factory, parentSupplier.createParent(factory), "test", "", null);
        assertEquals("test", element.getLocalName());
        assertNull(element.getNamespace());
        Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
        assertFalse(it.hasNext());
    }
}
