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
package org.apache.axiom.om.impl.common.builder;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11HeaderBlock;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12HeaderBlock;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeaderBlock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class CustomBuilderManager implements BuilderListener {
    private static final Log log = LogFactory.getLog(CustomBuilderManager.class);
    
    private ArrayList<CustomBuilderRegistration> registrations;
    private AxiomElement lastCandidateElement;
    private int lastCandidateDepth = -1;
    
    void register(CustomBuilder.Selector selector, CustomBuilder customBuilder) {
        if (registrations == null) {
            registrations = new ArrayList<CustomBuilderRegistration>();
        }
        registrations.add(new CustomBuilderRegistration(selector, customBuilder));
        // Try to apply the new custom builder to the element currently being built (unless it has
        // already been processed by another custom builder). This is important for custom builders
        // used to process the payload of a SOAP message: by the time the custom builder is
        // registered the payload root element may already have been created (e.g. because code
        // executed before the custom builder registration may have checked if the payload is a
        // SOAP fault).
        if (lastCandidateElement != null) {
            Runnable action = getAction(lastCandidateElement, lastCandidateDepth, registrations.size()-1);
            if (action != null) {
                action.run();
            }
        }
    }
    
    @Override
    public Runnable nodeAdded(CoreNode node, int depth) {
        return getAction(node, depth, 0);
    }
    
    private Runnable getAction(CoreNode node, int depth, int firstCustomBuilder) {
        lastCandidateElement = null;
        lastCandidateDepth = -1;
        if (node instanceof AxiomElement && (node instanceof AxiomSOAPHeaderBlock || !(node instanceof AxiomSOAPElement))) {
            final AxiomElement element = (AxiomElement)node;
            if (registrations != null) {
                for (int i=firstCustomBuilder; i<registrations.size(); i++) {
                    CustomBuilderRegistration registration = registrations.get(i);
                    final String namespaceURI = element.coreGetNamespaceURI();
                    final String localName = element.coreGetLocalName();
                    if (registration.getSelector().accepts(element.getParent(), depth, namespaceURI, localName)) {
                        final CustomBuilder customBuilder = registration.getCustomBuilder();
                        if (log.isDebugEnabled()) {
                            log.debug("Custom builder " + customBuilder + " accepted element {" + namespaceURI + "}" + localName + " at depth " + depth);
                        }
                        return new Runnable() {
                            @Override
                            public void run() {
                                if (log.isDebugEnabled()) {
                                    log.debug("Invoking custom builder " + customBuilder);
                                }
                                XMLStreamReader reader = element.getXMLStreamReader(false);
                                // Advance the reader to the START_ELEMENT event of the root element
                                try {
                                    reader.next();
                                } catch (XMLStreamException ex) {
                                    // We should never get here
                                    throw new OMException(ex);
                                }
                                OMDataSource dataSource = customBuilder.create(reader);
                                try {
                                    reader.close();
                                } catch (XMLStreamException ex) {
                                    // We should never get here
                                    throw new OMException(ex);
                                }
                                Class<? extends AxiomSourcedElement> type;
                                if (element instanceof AxiomSOAP11HeaderBlock) {
                                    type = AxiomSOAP11HeaderBlock.class;
                                } else if (element instanceof AxiomSOAP12HeaderBlock) {
                                    type = AxiomSOAP12HeaderBlock.class;
                                } else {
                                    type = AxiomSourcedElement.class;
                                }
                                if (log.isDebugEnabled()) {
                                    log.debug("Replacing element with new sourced element of type " + type);
                                }
                                AxiomSourcedElement newElement = element.coreCreateNode(type);
                                newElement.init(localName, new OMNamespaceImpl(namespaceURI, null), dataSource);
                                element.coreReplaceWith(newElement, AxiomSemantics.INSTANCE);
                            }
                        };
                    }
                }
            }
            // Save a reference to the element so that we can process it when another custom builder is registered
            lastCandidateElement = element;
            lastCandidateDepth = depth;
        }
        return null;
    }
}
