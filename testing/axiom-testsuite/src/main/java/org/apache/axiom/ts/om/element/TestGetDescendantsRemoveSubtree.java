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

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;
import java.util.Iterator;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Test that {@link Iterator#remove()} behaves correctly on the iterator returned by
 * {@link OMContainer#getDescendants(boolean)} when used to remove an element with child nodes.
 */
public class TestGetDescendantsRemoveSubtree extends AxiomTestCase {
    public TestGetDescendantsRemoveSubtree(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement root = OMXMLBuilderFactory.createOMBuilder(
                metaFactory.getOMFactory(),
                new StringReader("<root><a><b/></a><c/></root>")).getDocumentElement();
        Iterator<OMNode> it = root.getDescendants(false);
        assertThat(((OMElement)it.next()).getLocalName()).isEqualTo("a");
        it.remove();
        assertThat(((OMElement)it.next()).getLocalName()).isEqualTo("c");
    }
}
