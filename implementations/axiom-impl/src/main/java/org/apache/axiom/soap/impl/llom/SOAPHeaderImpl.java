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

package org.apache.axiom.soap.impl.llom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.common.Checker;
import org.apache.axiom.soap.impl.common.HeaderIterator;
import org.apache.axiom.soap.impl.common.MURoleChecker;
import org.apache.axiom.soap.impl.common.RoleChecker;
import org.apache.axiom.soap.impl.common.RolePlayerChecker;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** A class representing the SOAP Header, primarily allowing access to the contained HeaderBlocks. */
public abstract class SOAPHeaderImpl extends SOAPElement implements AxiomSOAPHeader {
    private static final Log log = LogFactory.getLog(SOAPHeaderImpl.class);
    
    public SOAPHeaderBlock addHeaderBlock(String localName, OMNamespace ns)
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

    public SOAPHeaderBlock addHeaderBlock(QName qname) throws OMException {
        return addHeaderBlock(qname.getLocalPart(), getOMFactory().createOMNamespace(qname.getNamespaceURI(), qname.getPrefix()));
    }

    public Iterator getHeadersToProcess(RolePlayer rolePlayer) {
        return new HeaderIterator(this, new RolePlayerChecker(rolePlayer));
    }

    public Iterator getHeadersToProcess(RolePlayer rolePlayer, String namespace) {
        return new HeaderIterator(this, new RolePlayerChecker(rolePlayer, namespace));
    }

    public Iterator examineHeaderBlocks(String role) {
        return new HeaderIterator(this, new RoleChecker(role));
    }

    public abstract Iterator extractHeaderBlocks(String role);

    public Iterator examineMustUnderstandHeaderBlocks(String actor) {
        return new HeaderIterator(this, new MURoleChecker(actor));
    }

    public Iterator extractAllHeaderBlocks() {
        List result = new ArrayList();
        for (Iterator iter = getChildElements(); iter.hasNext();) {
            OMElement headerBlock = (OMElement) iter.next();
            iter.remove();
            result.add(headerBlock);
        }
        return result.iterator();
    }

    public ArrayList getHeaderBlocksWithNSURI(String nsURI) {
        ArrayList headers = null;
        OMNode node;
        OMElement header = this.getFirstElement();

        if (header != null) {
            headers = new ArrayList();
        }

        node = header;

        while (node != null) {
            if (node.getType() == OMNode.ELEMENT_NODE) {
                header = (OMElement) node;
                OMNamespace namespace = header.getNamespace();
                if (nsURI == null) {
                    if (namespace == null) {
                        headers.add(header);
                    }
                } else {
                    if (namespace != null) {
                        if (nsURI.equals(namespace.getNamespaceURI())) {
                            headers.add(header);
                        }
                    }
                }
            }
            node = node.getNextOMSibling();

        }
        return headers;

    }

    public void addChild(OMNode child) {
        
        // Make sure a proper element is added.  The children of a SOAPHeader should be
        // SOAPHeaderBlock objects.
        // Due to legacy usages (AXIS2 has a lot of tests that violate this constraint)
        // I am only going to log an exception when debug is enabled. 
        if (log.isDebugEnabled()) {
            if (child instanceof OMElement &&
            !(child instanceof SOAPHeaderBlock)) {
                Exception e = new SOAPProcessingException(
                  "An attempt was made to add a normal OMElement as a child of a SOAPHeader." +
                  "  This is not supported.  The child should be a SOAPHeaderBlock.");
                log.debug(exceptionToString(e));
            }
        }
        super.addChild(child);
    }
    
    public static String exceptionToString(Throwable e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.BufferedWriter bw = new java.io.BufferedWriter(sw);
        java.io.PrintWriter pw = new java.io.PrintWriter(bw);
        e.printStackTrace(pw);
        pw.close();
        String text = sw.getBuffer().toString();
        return text;
    }
}
