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

package org.apache.axiom.ts.om.xpath;

import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.om.xpath.DocumentNavigator;
import org.apache.axiom.test.jaxen.JaxenXPathTestBase;
import org.apache.axiom.ts.om.container.RootWhitespaceFilter;
import org.jaxen.Navigator;

public class AXIOMXPathTestCase extends JaxenXPathTestBase {
    private final OMMetaFactory metaFactory;
    
    public AXIOMXPathTestCase(String name, OMMetaFactory metaFactory) {
        super(name);
        this.metaFactory = metaFactory;
    }

    @Override
    protected Navigator createNavigator() {
        return new DocumentNavigator();
    }

    @Override
    protected Object loadDocument(InputStream in) throws Exception {
        // Jaxen's unit tests assume that whitespace in the prolog/epilog is not
        // represented in the tree (as in DOM), so we need to filter these events.
        XMLStreamReader reader = new RootWhitespaceFilter(
                StAXUtils.createXMLStreamReader(in));
        return OMXMLBuilderFactory.createStAXOMBuilder(metaFactory.getOMFactory(), reader).getDocument();
    }

    @Override
    protected void releaseDocument(Object document) {
        ((OMDocument)document).close(false);
    }
}
