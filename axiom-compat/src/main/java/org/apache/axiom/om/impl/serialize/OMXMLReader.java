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

package org.apache.axiom.om.impl.serialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.jaxp.OMSource;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;
import org.apache.axiom.util.sax.AbstractXMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * SAX {@link XMLReader} implementation that traverses a given OM tree and invokes the
 * callback methods on the configured {@link ContentHandler}. This can be used to
 * serialize an Axiom tree to SAX.
 * <p>
 * Note that this class only supports {@link ContentHandler} and {@link LexicalHandler}.
 * {@link DTDHandler} and {@link DeclHandler} are not supported.
 * <p>
 * This class can also generate SAX events for a subtree. This is the case if the
 * element passed to the constructor is not the root element of the document. In this
 * case, care is taken to properly generate <code>startPrefixMapping</code> and
 * <code>endPrefixMapping</code> events also for namespace mappings declared on the ancestors
 * of the element.
 * <p>
 * To understand why this is important, consider the following example:
 * <pre>&lt;root xmlns:ns="urn:ns"&gt;&lt;element attr="ns:someThing"/&gt;&lt;root&gt;</pre>
 * In that case, to correctly interpret the attribute value, the SAX content handler must be
 * aware of the namespace mapping for the <tt>ns</tt> prefix, even if the serialization starts
 * only at the child element.
 * 
 * @deprecated This class is used internally by {@link OMSource}. Starting with Axiom 1.2.13,
 * application code should use {@link OMContainer#getSAXSource(boolean)} to serialize an OM tree to
 * SAX. If there is a need to obtain an {@link XMLReader} instance, use
 * {@link SAXSource#getXMLReader()} on the {@link SAXSource} returned by
 * {@link OMContainer#getSAXSource(boolean)}.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class OMXMLReader extends AbstractXMLReader {
    private final OMContainer root;
    private final AttributesAdapter attributesAdapter = new AttributesAdapter();
    
    public OMXMLReader(OMContainer root) {
        this.root = root;
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        parse();
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        parse();
    }
    
    private void parse() throws SAXException {
        if (root instanceof OMDocument) {
            generateEvents((OMDocument)root);
        } else {
            OMElement element = (OMElement)root;
            contentHandler.startDocument();
            generateParentPrefixMappingEvents(element, true);
            generateEvents(element);
            generateParentPrefixMappingEvents(element, false);
            contentHandler.endDocument();
        }
    }
    
    private void generateEvents(OMDocument document) throws SAXException {
        contentHandler.startDocument();
        generateEventsForChildren(document);
        contentHandler.endDocument();
    }
    
    private void generatePrefixMappingEvents(OMNamespace ns, boolean start) throws SAXException {
        String prefix = ns.getPrefix();
        if (prefix != null) {
            if (start) {
                contentHandler.startPrefixMapping(prefix, ns.getNamespaceURI());
            } else {
                contentHandler.endPrefixMapping(prefix);
            }
        }
    }
    
    private void generatePrefixMappingEvents(OMElement omElement, boolean start)
            throws SAXException {
        
        for (Iterator it = omElement.getAllDeclaredNamespaces(); it.hasNext(); ) {
            generatePrefixMappingEvents((OMNamespace)it.next(), start);
        }
    }

    private void generateParentPrefixMappingEvents(OMElement omElement, boolean start)
            throws SAXException {
        
        if (!(omElement.getParent() instanceof OMElement)) {
            return;
        }
        // Maintain a set of the prefixes we have already seen. This is required to take into
        // account that a namespace mapping declared on an element can hide another one declared
        // for the same prefix on an ancestor of the element.
        Set/*<String>*/ seenPrefixes = new HashSet();
        for (Iterator it = omElement.getAllDeclaredNamespaces(); it.hasNext(); ) {
            seenPrefixes.add(((OMNamespace)it.next()).getPrefix());
        }
        OMElement current = omElement;
        while (true) {
            OMContainer parent = current.getParent();
            if (!(parent instanceof OMElement)) {
                return;
            }
            current = (OMElement)parent;
            for (Iterator it = current.getAllDeclaredNamespaces(); it.hasNext(); ) {
                OMNamespace ns = (OMNamespace)it.next();
                if (seenPrefixes.add(ns.getPrefix())) {
                    generatePrefixMappingEvents(ns, start);
                }
            }
        }
    }
    
    private void generateEvents(OMElement omElement) throws SAXException {
        generatePrefixMappingEvents(omElement, true);
        OMNamespace omNamespace = omElement.getNamespace();
        String uri;
        String prefix;
        if (omNamespace != null) {
            uri = omNamespace.getNamespaceURI();
            prefix = omNamespace.getPrefix();
        } else {
            uri = "";
            prefix = null;
        }
        String localName = omElement.getLocalName();
        String qName;
        if (prefix == null || prefix.length() == 0) {
            qName = localName;
        } else {
            qName = prefix + ":" + localName;
        }
        // For performance reasons, we always reuse the same instance of AttributesAdapter.
        // This is explicitely allowed by the specification of the startElement method.
        attributesAdapter.setAttributes(omElement);
        contentHandler.startElement(uri, localName, qName, attributesAdapter);
        generateEventsForChildren(omElement);
        contentHandler.endElement(uri, localName, qName);
        generatePrefixMappingEvents(omElement, false);
    }
    
    private void generateEventsForChildren(OMContainer parent) throws SAXException {
        for (Iterator it = parent.getChildren(); it.hasNext(); ) {
            OMNode node = (OMNode)it.next();
            switch (node.getType()) {
                case OMNode.DTD_NODE:
                    if (lexicalHandler != null) {
                        OMDocType doctype = (OMDocType)node;
                        lexicalHandler.startDTD(doctype.getRootName(), doctype.getPublicId(), doctype.getSystemId());
                        lexicalHandler.endDTD();
                    }
                    break;
                case OMNode.ELEMENT_NODE:
                    generateEvents((OMElement)node);
                    break;
                case OMNode.TEXT_NODE:
                    generateEvents((OMText)node, false);
                    break;
                case OMNode.SPACE_NODE:
                    generateEvents((OMText)node, true);
                    break;
                case OMNode.CDATA_SECTION_NODE:
                    if (lexicalHandler != null) {
                        lexicalHandler.startCDATA();
                    }
                    generateEvents((OMText)node, false);
                    if (lexicalHandler != null) {
                        lexicalHandler.endCDATA();
                    }
                    break;
                case OMNode.COMMENT_NODE:
                    if (lexicalHandler != null) {
                        char[] ch = ((OMComment)node).getValue().toCharArray();
                        lexicalHandler.comment(ch, 0, ch.length);
                    }
                    break;
                case OMNode.PI_NODE:
                    OMProcessingInstruction pi = (OMProcessingInstruction)node;
                    contentHandler.processingInstruction(pi.getTarget(), pi.getValue());
                    break;
                case OMNode.ENTITY_REFERENCE_NODE:
                    contentHandler.skippedEntity(((OMEntityReference)node).getName());
                    break;
                default:
                    throw new IllegalStateException("Unrecognized node type " + node.getType());
            }
        }
    }
    
    private void generateEvents(OMText omText, boolean space) throws SAXException {
        if (omText.isBinary()) {
            // Stream the binary content
            DataHandler dh = omText.getDataHandler();
            Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream(new ContentHandlerWriter(contentHandler));
            try {
                dh.writeTo(out);
                out.complete();
            } catch (IOException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof SAXException) {
                    throw (SAXException)ex.getCause();
                } else {
                    throw new SAXException(ex);
                }
            }
        } else {
            char[] ch = omText.getTextCharacters();
            if (space) {
                contentHandler.ignorableWhitespace(ch, 0, ch.length);
            } else {
                contentHandler.characters(ch, 0, ch.length);
            }
        }
    }

    protected static class AttributesAdapter implements Attributes {
        private List/*<OMAttribute>*/ attributes = new ArrayList(5);

        public void setAttributes(OMElement element) {
            attributes.clear();
            for (Iterator it = element.getAllAttributes(); it.hasNext(); ) {
                attributes.add(it.next());
            }
        }

        @Override
        public int getLength() {
            return attributes.size();
        }

        @Override
        public int getIndex(String qName) {
            for (int i=0, len=attributes.size(); i<len; i++) {
                if (getQName(i).equals(qName)) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getIndex(String uri, String localName) {
            for (int i=0, len=attributes.size(); i<len; i++) {
                if (getURI(i).equals(uri) && getLocalName(i).equals(localName)) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public String getLocalName(int index) {
            return ((OMAttribute)attributes.get(index)).getLocalName();
        }

        @Override
        public String getQName(int index) {
            OMAttribute attribute = ((OMAttribute)attributes.get(index));
            OMNamespace ns = attribute.getNamespace();
            if (ns == null) {
                return attribute.getLocalName();
            } else {
                String prefix = ns.getPrefix();
                if (prefix == null || prefix.length() == 0) {
                    return attribute.getLocalName();
                } else {
                    return ns.getPrefix() + ":" + attribute.getLocalName();
                }
            }
        }

        @Override
        public String getType(int index) {
            return ((OMAttribute)attributes.get(index)).getAttributeType();
        }

        @Override
        public String getType(String qName) {
            int index = getIndex(qName);
            return index == -1 ? null : getType(index);
        }

        @Override
        public String getType(String uri, String localName) {
            int index = getIndex(uri, localName);
            return index == -1 ? null : getType(index);
        }

        @Override
        public String getURI(int index) {
            OMNamespace ns = ((OMAttribute)attributes.get(index)).getNamespace();
            return ns == null ? "" : ns.getNamespaceURI();
        }

        @Override
        public String getValue(int index) {
            return ((OMAttribute)attributes.get(index)).getAttributeValue();
        }

        @Override
        public String getValue(String qName) {
            int index = getIndex(qName);
            return index == -1 ? null : getValue(index);
        }

        @Override
        public String getValue(String uri, String localName) {
            int index = getIndex(uri, localName);
            return index == -1 ? null : getValue(index);
        }
    }
}
