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

package org.apache.axiom.soap.impl.common.builder;

import java.io.Closeable;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.CustomBuilder;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.impl.common.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.builder.OMMetaFactoryEx;
import org.apache.axiom.soap.impl.intf.SOAPFactoryEx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamReader;

/**
 * Internal implementation class.
 */
public class StAXSOAPModelBuilder extends StAXOMBuilder implements SOAPModelBuilder {
    /**
     * The meta factory used to get the SOAPFactory implementation when SOAP version detection
     * is enabled. This is only used if <code>soapFactory</code> is <code>null</code>.
     */
    private OMMetaFactory metaFactory;

    private SOAPFactoryEx soapFactory;

    /** Field headerPresent */
    private boolean headerPresent = false;

    /** Field bodyPresent */
    private boolean bodyPresent = false;

    /** Field log */
    private static final Log log = LogFactory.getLog(StAXSOAPModelBuilder.class);

    private boolean processingFault = false;

    private SOAPBuilderHelper builderHelper;

    public StAXSOAPModelBuilder(OMMetaFactory metaFactory, XMLStreamReader parser,
            boolean autoClose, Detachable detachable, Closeable closeable) {
        super(metaFactory.getOMFactory(), parser, autoClose, detachable, closeable);
        this.metaFactory = metaFactory;
    }
    
    public SOAPEnvelope getSOAPEnvelope() throws OMException {
        return (SOAPEnvelope)getDocumentElement();
    }

    protected OMNode createNextOMElement() {
        OMNode newElement = null;
        
        
        if (elementLevel == 3 && 
            customBuilderForPayload != null) {
            
            if (target instanceof SOAPBody) {
                newElement = createWithCustomBuilder(customBuilderForPayload,  soapFactory);
            }
        } 
        if (newElement == null && customBuilders != null && 
                elementLevel <= maxDepthForCustomBuilders) {
            String namespace = parser.getNamespaceURI();
            String localPart = parser.getLocalName();
            CustomBuilder customBuilder = getCustomBuilder(namespace, localPart);
            if (customBuilder != null) {
                newElement = createWithCustomBuilder(customBuilder, soapFactory);
            }
        }
        if (newElement == null) {
            newElement = createOMElement();
        } else {
            elementLevel--; // Decrease level since custom builder read the end element event
        }
        return newElement;
    }
    
    @Override
    protected Class<? extends AxiomElement> determineElementType(OMContainer parent, String elementName) {
        Class<? extends AxiomElement> elementType;
        if (elementLevel == 1) {

            // Now I've found a SOAP Envelope, now create SOAPEnvelope here.

            if (!elementName.equals(SOAPConstants.SOAPENVELOPE_LOCAL_NAME)) {
                throw new SOAPProcessingException("First Element must contain the local name, "
                        + SOAPConstants.SOAPENVELOPE_LOCAL_NAME + " , but found " + elementName,
                        SOAPConstants.FAULT_CODE_SENDER);
            }

            // determine SOAP version and from that determine a proper factory here.
            String namespaceURI = this.parser.getNamespaceURI();
            if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(namespaceURI)) {
                soapFactory = (SOAPFactoryEx)metaFactory.getSOAP12Factory();
                log.debug("Starting to process SOAP 1.2 message");
            } else if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(namespaceURI)) {
                soapFactory = (SOAPFactoryEx)metaFactory.getSOAP11Factory();
                log.debug("Starting to process SOAP 1.1 message");
            } else {
                throw new SOAPProcessingException(
                        "Only SOAP 1.1 or SOAP 1.2 messages are supported in the" +
                                " system", SOAPConstants.FAULT_CODE_VERSION_MISMATCH);
            }

            elementType = soapFactory.getSOAPHelper().getEnvelopeClass();
        } else if (elementLevel == 2) {
            String elementNS = parser.getNamespaceURI();

            if (soapFactory.getSoapVersionURI().equals(elementNS)) {
                // this is either a header or a body
                if (elementName.equals(SOAPConstants.HEADER_LOCAL_NAME)) {
                    if (headerPresent) {
                        throw new SOAPProcessingException("Multiple headers encountered!",
                                                          getSenderFaultCode());
                    }
                    if (bodyPresent) {
                        throw new SOAPProcessingException("Header Body wrong order!",
                                                          getSenderFaultCode());
                    }
                    headerPresent = true;
                    elementType = soapFactory.getSOAPHelper().getHeaderClass();
                } else if (elementName.equals(SOAPConstants.BODY_LOCAL_NAME)) {
                    if (bodyPresent) {
                        throw new SOAPProcessingException("Multiple body elements encountered",
                                                          getSenderFaultCode());
                    }
                    bodyPresent = true;
                    elementType = soapFactory.getSOAPHelper().getBodyClass();
                } else {
                    throw new SOAPProcessingException(elementName + " is not supported here.",
                                                      getSenderFaultCode());
                }
            } else if (soapFactory.getSOAPVersion() == SOAP11Version.getSingleton() && bodyPresent) {
                elementType = AxiomElement.class;
            } else {
                throw new SOAPProcessingException("Disallowed element found inside Envelope : {"
                        + elementNS + "}" + elementName);
            }
        } else if ((elementLevel == 3)
                &&
                ((OMElement)parent).getLocalName().equals(SOAPConstants.HEADER_LOCAL_NAME)) {

            // this is a headerblock
            try {
                elementType = soapFactory.getSOAPHelper().getHeaderBlockClass();
            } catch (SOAPProcessingException e) {
                throw new SOAPProcessingException("Can not create SOAPHeader block",
                                                  getReceiverFaultCode(), e);
            }
        } else if ((elementLevel == 3) &&
                ((OMElement)parent).getLocalName().equals(SOAPConstants.BODY_LOCAL_NAME) &&
                elementName.equals(SOAPConstants.BODY_FAULT_LOCAL_NAME) &&
                soapFactory.getSoapVersionURI().equals(parser.getNamespaceURI())) {
            // this is a SOAP fault
            elementType = soapFactory.getSOAPHelper().getFaultClass();
            processingFault = true;
            if (soapFactory.getSOAPVersion() == SOAP12Version.getSingleton()) {
                builderHelper = new SOAP12BuilderHelper();
            } else if (soapFactory.getSOAPVersion() == SOAP11Version.getSingleton()) {
                builderHelper = new SOAP11BuilderHelper();
            }

        } else if (elementLevel > 3 && processingFault) {
            elementType = builderHelper.handleEvent(parser, (OMElement)parent, elementLevel);
        } else {
            // this is neither of above. Just create an element
            elementType = AxiomElement.class;
        }
        return elementType;
    }

    private String getSenderFaultCode() {
        return getSOAPEnvelope().getVersion().getSenderFaultCode().getLocalPart();
    }

    private String getReceiverFaultCode() {
        return getSOAPEnvelope().getVersion().getReceiverFaultCode().getLocalPart();
    }

    protected OMDocument createDocument() {
        if (soapFactory != null) {
            return soapFactory.createSOAPMessage(this);
        } else {
            return ((OMMetaFactoryEx)metaFactory).createSOAPMessage(this);
        }
    }

    /** Method createDTD. Overriding the default behaviour as a SOAPMessage should not have a DTD. */
    protected OMNode createDTD() throws OMException {
        throw new SOAPProcessingException("SOAP message MUST NOT contain a Document Type Declaration(DTD)");
    }

    /** Method createPI. Overriding the default behaviour as a SOAP Message should not have a PI. */
    protected OMNode createPI() throws OMException {
        throw new SOAPProcessingException("SOAP message MUST NOT contain Processing Instructions(PI)");
    }

    protected OMNode createEntityReference() {
        throw new SOAPProcessingException("A SOAP message cannot contain entity references because it must not have a DTD");
    }

    public SOAPMessage getSOAPMessage() {
        return (SOAPMessage)getDocument();
    }

    public SOAPFactory getSOAPFactory() {
        if (soapFactory == null) {
            getSOAPEnvelope();
        }
        return soapFactory;
    }
}
