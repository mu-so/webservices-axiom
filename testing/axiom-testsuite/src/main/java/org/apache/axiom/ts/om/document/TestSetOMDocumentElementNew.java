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
package org.apache.axiom.ts.om.document;

import java.util.Iterator;

import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMDocument#setOMDocumentElement(OMElement)} if there is no existing
 * document element.
 */
public class TestSetOMDocumentElementNew extends AxiomTestCase {
    public TestSetOMDocumentElementNew(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMDocument document = factory.createOMDocument();
        OMComment comment = factory.createOMComment(document, "some comment");
        OMElement documentElement = factory.createOMElement("root", null);
        document.setOMDocumentElement(documentElement);
        assertSame(documentElement, document.getOMDocumentElement());
        assertSame(document, documentElement.getParent());
        Iterator<OMNode> it = document.getChildren();
        assertTrue(it.hasNext());
        assertSame(comment, it.next());
        assertTrue(it.hasNext());
        assertSame(documentElement, it.next());
        assertFalse(it.hasNext());
    }
}
