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
package org.apache.axiom.soap.impl.mixin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.core.Axis;
import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.common.MURoleChecker;
import org.apache.axiom.soap.impl.common.RoleChecker;
import org.apache.axiom.soap.impl.common.RolePlayerChecker;
import org.apache.axiom.soap.impl.common.SOAPHeaderBlockMapper;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeader;

public aspect AxiomSOAPHeaderSupport {
    public final boolean AxiomSOAPHeader.isChildElementAllowed(OMElement child) {
        return child instanceof SOAPHeaderBlock;
    }

    public final SOAPHeaderBlock AxiomSOAPHeader.addHeaderBlock(String localName, OMNamespace ns)
            throws OMException {
        
        if (ns == null || ns.getNamespaceURI().length() == 0) {
            throw new OMException(
                    "All the SOAP Header blocks should be namespace qualified");
        }
        
        OMNamespace namespace = findNamespace(ns.getNamespaceURI(), ns.getPrefix());
        if (namespace != null) {
            ns = namespace;
        }
        
        SOAPHeaderBlock soapHeaderBlock;
        try {
            soapHeaderBlock = ((SOAPFactory)getOMFactory()).createSOAPHeaderBlock(localName, ns, this);
        } catch (SOAPProcessingException e) {
            throw new OMException(e);
        }
        return soapHeaderBlock;
    }

    public final SOAPHeaderBlock AxiomSOAPHeader.addHeaderBlock(QName qname) throws OMException {
        return addHeaderBlock(qname.getLocalPart(), getOMFactory().createOMNamespace(qname.getNamespaceURI(), qname.getPrefix()));
    }

    public final Iterator<SOAPHeaderBlock> AxiomSOAPHeader.examineAllHeaderBlocks() {
        return coreGetElements(Axis.CHILDREN, AxiomElement.class, ElementMatcher.ANY, null, null,
                SOAPHeaderBlockMapper.INSTANCE, AxiomSemantics.INSTANCE);
    }

    public final Iterator<SOAPHeaderBlock> AxiomSOAPHeader.examineHeaderBlocks(String role) {
        return coreGetElements(Axis.CHILDREN, AxiomElement.class, new RoleChecker(getSOAPHelper(), role), null, null,
                SOAPHeaderBlockMapper.INSTANCE, AxiomSemantics.INSTANCE);
    }

    public final Iterator<SOAPHeaderBlock> AxiomSOAPHeader.examineMustUnderstandHeaderBlocks(String role) {
        return coreGetElements(Axis.CHILDREN, AxiomElement.class, new MURoleChecker(getSOAPHelper(), role), null, null,
                SOAPHeaderBlockMapper.INSTANCE, AxiomSemantics.INSTANCE);
    }

    public final Iterator<SOAPHeaderBlock> AxiomSOAPHeader.getHeadersToProcess(RolePlayer rolePlayer) {
        return getHeadersToProcess(rolePlayer, null);
    }

    public final Iterator<SOAPHeaderBlock> AxiomSOAPHeader.getHeadersToProcess(RolePlayer rolePlayer, String namespace) {
        return coreGetElements(Axis.CHILDREN, AxiomElement.class, new RolePlayerChecker(getSOAPHelper(), rolePlayer, namespace), null, null,
                SOAPHeaderBlockMapper.INSTANCE, AxiomSemantics.INSTANCE);
    }

    public final Iterator<SOAPHeaderBlock> AxiomSOAPHeader.getHeaderBlocksWithNamespaceURI(String uri) {
        return coreGetElements(Axis.CHILDREN, AxiomElement.class, ElementMatcher.BY_NAMESPACE_URI, uri, null,
                SOAPHeaderBlockMapper.INSTANCE, AxiomSemantics.INSTANCE);
    }

    public final Iterator<SOAPHeaderBlock> AxiomSOAPHeader.getHeaderBlocksWithName(QName name) {
        return coreGetElements(Axis.CHILDREN, AxiomElement.class, ElementMatcher.BY_QNAME, name.getNamespaceURI(), name.getLocalPart(),
                SOAPHeaderBlockMapper.INSTANCE, AxiomSemantics.INSTANCE);
    }

    public final ArrayList<SOAPHeaderBlock> AxiomSOAPHeader.getHeaderBlocksWithNSURI(String nsURI) {
        ArrayList<SOAPHeaderBlock> result = new ArrayList<SOAPHeaderBlock>();
        for (Iterator<SOAPHeaderBlock> it = getHeaderBlocksWithNamespaceURI(nsURI); it.hasNext(); ) {
            result.add(it.next());
        }
        return result;
    }

    private Iterator<SOAPHeaderBlock> AxiomSOAPHeader.extract(Iterator<SOAPHeaderBlock> it) {
        List<SOAPHeaderBlock> result = new ArrayList<SOAPHeaderBlock>();
        while (it.hasNext()) {
            SOAPHeaderBlock headerBlock = it.next();
            it.remove();
            result.add(headerBlock);
        }
        return result.iterator();
    }

    public final Iterator<SOAPHeaderBlock> AxiomSOAPHeader.extractHeaderBlocks(String role) {
        return extract(examineHeaderBlocks(role));
    }

    public final Iterator<SOAPHeaderBlock> AxiomSOAPHeader.extractAllHeaderBlocks() {
        return extract(examineAllHeaderBlocks());
    }
}
