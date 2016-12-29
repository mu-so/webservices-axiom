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

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.BlobOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests functionality of BlobOMDataSource
 */
public class TestBlobOMDataSource extends AxiomTestCase {
    public TestBlobOMDataSource(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        
        String localName = "myPayload";
        String encoding = "utf-8";
        String payload = "<tns:myPayload xmlns:tns=\"urn://test\">Payload One</tns:myPayload>";
        OMNamespace ns = factory.createOMNamespace("urn://test", "tns");
        BlobOMDataSource ds = new BlobOMDataSource(Blobs.createBlob(payload.getBytes(encoding)), encoding);

        OMElement parent = factory.createOMElement("root", null);
        OMSourcedElement omse = factory.createOMElement(ds, localName, ns);
        parent.addChild(omse);
        OMNode firstChild = parent.getFirstOMChild();
        assertTrue("Expected OMSourcedElement child", firstChild instanceof OMSourcedElement);
        OMSourcedElement child = (OMSourcedElement) firstChild;
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        assertThat(child.getDataSource()).isSameAs(ds);
        
        // A BlobOMDataSource does not consume the backing object when read.
        // Thus getting the XMLStreamReader of the BlobOMDataSource should not 
        // cause expansion of the OMSourcedElement.
        XMLStreamReader reader = child.getXMLStreamReader();
        reader.next();
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        
        // Likewise, a BlobOMDataSource does not consume the backing object when 
        // written.  Thus serializing the OMSourcedElement should not cause the expansion
        // of the OMSourcedElement.
        assertTrue("The payload was not present in the output",
                   parent.toString().indexOf(payload) > 0);
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        
        // If a consumer calls build or buildWithAttachments on the tree, the 
        // tree should not be expanded.
        parent.build();
        assertTrue("OMSourcedElement is expanded after build().  This is unexpected", !child.isExpanded());
        parent.buildWithAttachments();
        assertTrue("OMSourcedElement is expanded after buildWithAttachments().  This is unexpected", !child.isExpanded());
        
        // Test getting the raw bytes from the BlobOMDataSource.
        assertThat(child.getDataSource()).isSameAs(ds);
    }
}
