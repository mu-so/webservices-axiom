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

import static com.google.common.truth.Truth.assertThat;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;

/**
 * Tests a scenario that gave incorrect results in previous Axiom versions because
 * {@link OMElement#findNamespace(String, String)} interpreted an empty prefix in the same way as
 * <code>null</code>.
 */
public class TestCreateOMElementWithNamespaceInScope4 extends CreateOMElementTestCase {
    public TestCreateOMElementWithNamespaceInScope4(OMMetaFactory metaFactory, CreateOMElementVariant variant) {
        super(metaFactory, variant, null);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement parent = factory.createOMElement("parent", "urn:test", "a");
        parent.declareDefaultNamespace("urn:test");
        OMElement child = variant.createOMElement(factory, parent, "child", "urn:test", "");
        assertThat(child.getAllDeclaredNamespaces().hasNext()).isFalse();
    }
}
