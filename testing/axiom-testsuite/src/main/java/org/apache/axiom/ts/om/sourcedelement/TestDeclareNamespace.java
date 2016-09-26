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
package org.apache.axiom.ts.om.sourcedelement;

import static com.google.common.truth.Truth.assertThat;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;

/**
 * Tests that declaring a namespace on an {@link OMSourcedElement} overrides a corresponding
 * namespace declaration that may be produced during expansion.
 */
public class TestDeclareNamespace extends AxiomTestCase {
    public TestDeclareNamespace(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement element = factory.createOMElement(
                new PullOMDataSource("<root xmlns:p='urn:ns1'><child/></root>"), "root", null);
        // Declare a namespace before expansion
        element.declareNamespace("urn:ns2", "p");
        // Force expansion; this should not overwrite the namespace declaration we just added
        assertThat(element.getFirstOMChild()).isNotNull();
        OMNamespace ns = element.findNamespaceURI("p");
        assertThat(ns).isNotNull();
        assertThat(ns.getPrefix()).isEqualTo("p");
        assertThat(ns.getNamespaceURI()).isEqualTo("urn:ns2");
        Iterator it = element.getAllDeclaredNamespaces();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameAs(ns);
        assertThat(it.hasNext()).isFalse();
    }
}
