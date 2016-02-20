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
package org.apache.axiom.soap.impl.factory;

import javax.xml.namespace.QName;

import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.factory.OMFactoryImpl;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeaderBlock;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.apache.axiom.soap.impl.intf.SOAPHelper;

public abstract class SOAPFactoryImpl extends OMFactoryImpl implements SOAPFactory {
    public SOAPFactoryImpl(OMMetaFactory metaFactory, NodeFactory nodeFactory) {
        super(metaFactory, nodeFactory);
    }
    
    public abstract SOAPHelper getSOAPHelper();

    public final String getSoapVersionURI() {
        return getSOAPHelper().getEnvelopeURI();
    }

    public final SOAPVersion getSOAPVersion() {
        return getSOAPHelper().getVersion();
    }
    
    public final OMNamespace getNamespace() {
        return getSOAPHelper().getNamespace();
    }
    
    protected final <T extends AxiomSOAPElement> T createSOAPElement(Class<T> type, OMElement parent, QName qname) {
        T element = createNode(type);
        if (parent != null) {
            element.checkParent(parent);
            ((AxiomContainer)parent).addChild(element);
        }
        if (qname.getNamespaceURI().length() == 0) {
            element.initName(qname.getLocalPart(), null, true);
        } else if (parent != null) {
            element.initName(qname.getLocalPart(), parent.getNamespace(), false);
        } else {
            element.initName(qname.getLocalPart(), getNamespace(), true);
        }
        return element;
    }

    public final SOAPMessage createSOAPMessage() {
        AxiomSOAPMessage message = createNode(AxiomSOAPMessage.class);
        message.initSOAPFactory(this);
        return message;
    }

    public final SOAPEnvelope createSOAPEnvelope() {
        return createSOAPEnvelope(getNamespace());
    }
    
    public final SOAPEnvelope createSOAPEnvelope(OMNamespace ns) {
        return createAxiomElement(getSOAPHelper().getEnvelopeClass(), null, SOAPConstants.SOAPENVELOPE_LOCAL_NAME, ns);
    }

    public final SOAPHeader createSOAPHeader(SOAPEnvelope parent) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getHeaderClass(), parent, helper.getHeaderQName());
    }

    public final SOAPHeader createSOAPHeader() {
        return createSOAPHeader(null);
    }

    public final SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns, SOAPHeader parent) {
        return createAxiomElement(getSOAPHelper().getHeaderBlockClass(), parent, localName, ns);
    }

    public final SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns) {
        return createAxiomElement(getSOAPHelper().getHeaderBlockClass(), null, localName, ns);
    }

    public final SOAPHeaderBlock createSOAPHeaderBlock(OMDataSource source) {
        AxiomSOAPHeaderBlock element = createNode(getSOAPHelper().getHeaderBlockClass());
        element.init(source);
        return element;
    }

    public final SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns, OMDataSource ds) {
        AxiomSOAPHeaderBlock element = createNode(getSOAPHelper().getHeaderBlockClass());
        element.init(localName, ns, ds);
        return element;
    }

    public final SOAPBody createSOAPBody(SOAPEnvelope parent) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getBodyClass(), parent, helper.getBodyQName());
    }

    public final SOAPBody createSOAPBody() {
        return createSOAPBody(null);
    }

    public final SOAPFault createSOAPFault(SOAPBody parent) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getFaultClass(), parent, helper.getFaultQName());
    }

    public final SOAPFault createSOAPFault() {
        return createSOAPFault(null);
    }

    public final SOAPFault createSOAPFault(SOAPBody parent, Exception e) {
        SOAPFault fault = createSOAPFault(parent);
        fault.setException(e);
        return fault;
    }

    public final SOAPFaultCode createSOAPFaultCode(SOAPFault parent) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getFaultCodeClass(), parent, helper.getFaultCodeQName());
    }

    public final SOAPFaultCode createSOAPFaultCode() {
        return createSOAPFaultCode(null);
    }

    public final SOAPFaultReason createSOAPFaultReason(SOAPFault parent) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getFaultReasonClass(), parent, helper.getFaultReasonQName());
    }

    public final SOAPFaultReason createSOAPFaultReason() {
        return createSOAPFaultReason(null);
    }

    public final SOAPFaultRole createSOAPFaultRole(SOAPFault parent) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getFaultRoleClass(), parent, helper.getFaultRoleQName());
    }

    public final SOAPFaultRole createSOAPFaultRole() {
        return createSOAPFaultRole(null);
    }

    public final SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getFaultDetailClass(), parent, helper.getFaultDetailQName());
    }

    public final SOAPFaultDetail createSOAPFaultDetail() {
        return createSOAPFaultDetail(null);
    }

    public final SOAPMessage createDefaultSOAPMessage() {
        SOAPMessage message = createSOAPMessage();
        SOAPEnvelope env = createSOAPEnvelope();
        message.addChild(env);
        createSOAPBody(env);
        return message;
    }
    
    public final SOAPEnvelope getDefaultEnvelope() {
        SOAPEnvelope env = createSOAPEnvelope();
        createSOAPHeader(env);
        createSOAPBody(env);
        return env;
    }
}
