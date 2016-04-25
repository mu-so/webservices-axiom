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
package org.apache.axiom.om.impl.common.factory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.impl.builder.BuilderImpl;
import org.apache.axiom.core.impl.builder.BuilderListener;
import org.apache.axiom.core.impl.builder.PlainXMLModel;
import org.apache.axiom.core.stream.FilteredXmlInput;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.core.stream.dom.DOMInput;
import org.apache.axiom.core.stream.sax.SAXInput;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.impl.common.builder.OMXMLParserWrapperImpl;
import org.apache.axiom.om.impl.stream.stax.StAXPullInput;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.common.builder.SOAPFilter;
import org.apache.axiom.soap.impl.common.builder.SOAPModel;
import org.apache.axiom.soap.impl.common.builder.SOAPModelBuilderImpl;
import org.apache.axiom.soap.impl.intf.AxiomSOAPEnvelope;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.util.stax.XMLFragmentStreamReader;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.apache.axiom.util.stax.xop.XOPDecodingStreamReader;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Base class for {@link OMMetaFactory} implementations that make use of the standard builders
 * ({@link org.apache.axiom.core.impl.builder.BuilderImpl} and its subclasses).
 */
public abstract class AbstractOMMetaFactory implements OMMetaFactory {
    private final static class SourceInfo {
        private final XMLStreamReader reader;
        private final Detachable detachable;
        private final Closeable closeable;
        
        SourceInfo(XMLStreamReader reader, Detachable detachable, Closeable closeable) {
            this.reader = reader;
            this.detachable = detachable;
            this.closeable = closeable;
        }

        XMLStreamReader getReader() {
            return reader;
        }

        Detachable getDetachable() {
            return detachable;
        }

        Closeable getCloseable() {
            return closeable;
        }
    }
    
    private final NodeFactory nodeFactory;
    
    public AbstractOMMetaFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }
    
    private static SourceInfo createXMLStreamReader(StAXParserConfiguration configuration,
            InputSource is, boolean makeDetachable) {
        XMLStreamReader reader;
        Detachable detachable;
        Closeable closeable;
        try {
            if (is.getByteStream() != null) {
                String systemId = is.getSystemId();
                String encoding = is.getEncoding();
                InputStream in = is.getByteStream();
                if (makeDetachable) {
                    DetachableInputStream detachableInputStream = new DetachableInputStream(in, false);
                    in = detachableInputStream;
                    detachable = detachableInputStream;
                } else {
                    detachable = null;
                }
                if (systemId != null) {
                    if (encoding == null) {
                        reader = StAXUtils.createXMLStreamReader(configuration, systemId, in);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                } else {
                    if (encoding == null) {
                        reader = StAXUtils.createXMLStreamReader(configuration, in);
                    } else {
                        reader = StAXUtils.createXMLStreamReader(configuration, in, encoding);
                    }
                }
                closeable = null;
            } else if (is.getCharacterStream() != null) {
                Reader in = is.getCharacterStream();
                if (makeDetachable) {
                    DetachableReader detachableReader = new DetachableReader(in);
                    in = detachableReader;
                    detachable = detachableReader;
                } else {
                    detachable = null;
                }
                reader = StAXUtils.createXMLStreamReader(configuration, in);
                closeable = null;
            } else {
                String systemId = is.getSystemId();
                InputStream in = new URL(systemId).openConnection().getInputStream();
                if (makeDetachable) {
                    DetachableInputStream detachableInputStream = new DetachableInputStream(in, true);
                    in = detachableInputStream;
                    detachable = detachableInputStream;
                } else {
                    detachable = null;
                }
                reader = StAXUtils.createXMLStreamReader(configuration, systemId, in);
                closeable = in;
            }
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        } catch (IOException ex) {
            throw new OMException(ex);
        }
        return new SourceInfo(reader, detachable, closeable);
    }
    
    private static XMLStreamReader getXMLStreamReader(XMLStreamReader originalReader) {
        int eventType = originalReader.getEventType();
        switch (eventType) {
            case XMLStreamReader.START_DOCUMENT:
                return originalReader;
            case XMLStreamReader.START_ELEMENT:
                return new XMLFragmentStreamReader(originalReader);
            default:
                throw new OMException("The supplied XMLStreamReader is in an unexpected state ("
                        + XMLEventUtils.getEventTypeString(eventType) + ")");
        }
    }
    
    private OMXMLParserWrapper createOMBuilder(XmlInput input, boolean repairNamespaces, Detachable detachable) {
        return new OMXMLParserWrapperImpl(new BuilderImpl(input, nodeFactory, PlainXMLModel.INSTANCE, null, repairNamespaces), detachable);
    }
    
    private SOAPModelBuilder createSOAPModelBuilder(XmlInput input,
            boolean repairNamespaces, Detachable detachable) {
        BuilderImpl builder = new BuilderImpl(new FilteredXmlInput(input, SOAPFilter.INSTANCE), nodeFactory, new SOAPModel(), null, true);
        // The SOAPFactory instance linked to the SOAPMessage is unknown until we reach the
        // SOAPEnvelope. Register a post-processor that does the necessary updates on the
        // SOAPMessage.
        builder.addListener(new BuilderListener() {
            private AxiomSOAPMessage message;
            
            @Override
            public Runnable nodeAdded(CoreNode node, int depth) {
                if (node instanceof AxiomSOAPMessage) {
                    message = (AxiomSOAPMessage)node;
                } else if (message != null && node instanceof AxiomSOAPEnvelope) {
                    message.initSOAPFactory((SOAPFactory)((AxiomSOAPEnvelope)node).getOMFactory());
                }
                return null;
            }
        });
        return new SOAPModelBuilderImpl(builder, detachable);
    }
    
    public OMXMLParserWrapper createStAXOMBuilder(XMLStreamReader parser) {
        return createOMBuilder(
                new StAXPullInput(getXMLStreamReader(parser), false, null),
                true, null);
    }

    public OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, InputSource is) {
        SourceInfo sourceInfo = createXMLStreamReader(configuration, is, true);
        return createOMBuilder(
                new StAXPullInput(sourceInfo.getReader(), true, sourceInfo.getCloseable()),
                false, sourceInfo.getDetachable());
    }
    
    private static InputSource toInputSource(StreamSource source) {
        InputSource is = new InputSource();
        is.setByteStream(source.getInputStream());
        is.setCharacterStream(source.getReader());
        is.setPublicId(source.getPublicId());
        is.setSystemId(source.getSystemId());
        return is;
    }
    
    public OMXMLParserWrapper createOMBuilder(Source source) {
        if (source instanceof SAXSource) {
            return createOMBuilder((SAXSource)source, true);
        } else if (source instanceof DOMSource) {
            return createOMBuilder(((DOMSource)source).getNode(), true);
        } else if (source instanceof StreamSource) {
            return createOMBuilder(StAXParserConfiguration.DEFAULT, toInputSource((StreamSource)source));
        } else {
            try {
                return createOMBuilder(
                        new StAXPullInput(StAXUtils.getXMLInputFactory().createXMLStreamReader(source), true, null),
                        true, null);
            } catch (XMLStreamException ex) {
                throw new OMException(ex);
            }
        }
    }

    public OMXMLParserWrapper createOMBuilder(Node node, boolean expandEntityReferences) {
        return createOMBuilder(
                new DOMInput(node, expandEntityReferences),
                true, null);
    }

    public OMXMLParserWrapper createOMBuilder(SAXSource source, boolean expandEntityReferences) {
        return createOMBuilder(new SAXInput(source, expandEntityReferences), true, null);
    }

    public OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration,
            InputSource rootPart, MimePartProvider mimePartProvider) {
        SourceInfo sourceInfo = createXMLStreamReader(configuration, rootPart, false);
        return createOMBuilder(
                new StAXPullInput(new XOPDecodingStreamReader(sourceInfo.getReader(), mimePartProvider), true, sourceInfo.getCloseable()), 
                false, mimePartProvider instanceof Detachable ? (Detachable)mimePartProvider : null);
    }

    public SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader parser) {
        return createSOAPModelBuilder(new StAXPullInput(getXMLStreamReader(parser), false, null), true, null);
    }

    public SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration configuration, InputSource is) {
        SourceInfo sourceInfo = createXMLStreamReader(configuration, is, true);
        return createSOAPModelBuilder(
                new StAXPullInput(sourceInfo.getReader(), true, sourceInfo.getCloseable()),
                false, sourceInfo.getDetachable());
    }

    public SOAPModelBuilder createSOAPModelBuilder(Source source) {
        if (source instanceof SAXSource) {
            // TODO: supporting this will require some refactoring of the builders
            throw new UnsupportedOperationException();
        } else if (source instanceof DOMSource) {
            return createSOAPModelBuilder(new DOMInput(((DOMSource)source).getNode(), true), true, null);
        } else if (source instanceof StreamSource) {
            return createSOAPModelBuilder(StAXParserConfiguration.SOAP,
                    toInputSource((StreamSource)source));
        } else {
            try {
                return createSOAPModelBuilder(new StAXPullInput(StAXUtils.getXMLInputFactory().createXMLStreamReader(source), true, null), true, null);
            } catch (XMLStreamException ex) {
                throw new OMException(ex);
            }
        }
    }

    public SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration configuration,
            SOAPFactory soapFactory, InputSource rootPart, MimePartProvider mimePartProvider) {
        SourceInfo sourceInfo = createXMLStreamReader(configuration, rootPart, false);
        SOAPModelBuilder builder = createSOAPModelBuilder(
                new StAXPullInput(new XOPDecodingStreamReader(sourceInfo.getReader(), mimePartProvider), true, sourceInfo.getCloseable()),
                false,
                mimePartProvider instanceof Detachable ? (Detachable)mimePartProvider : null);
        if (builder.getSOAPMessage().getOMFactory() != soapFactory) {
            throw new SOAPProcessingException("Invalid SOAP namespace URI. " +
                    "Expected " + soapFactory.getSoapVersionURI(), SOAP12Constants.FAULT_CODE_SENDER);
        }
        return builder;
    }
}
