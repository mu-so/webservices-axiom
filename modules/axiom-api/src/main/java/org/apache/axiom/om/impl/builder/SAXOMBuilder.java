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

package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMElementEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;

public class SAXOMBuilder extends DefaultHandler implements LexicalHandler, OMXMLParserWrapper {
    private final boolean expandEntityReferences;
    
    private OMDocument document;
    
    OMElement root = null;

    OMNode lastNode = null;

    OMElement nextElem = null;

    private final OMFactoryEx factory;

    List prefixMappings = new ArrayList();
    
    int textNodeType = OMNode.TEXT_NODE;

    public SAXOMBuilder(OMFactory factory, boolean expandEntityReferences) {
        this.factory = (OMFactoryEx)factory;
        this.expandEntityReferences = expandEntityReferences;
    }
    
    public SAXOMBuilder(OMFactory factory) {
        this(factory, true);
    }
    
    public SAXOMBuilder() {
        this(OMAbstractFactory.getOMFactory());
    }
    
    private OMContainer getContainer() {
        if (lastNode != null) {
            return lastNode.isComplete() ? lastNode.getParent() : (OMContainer)lastNode;
        } else if (document != null) {
            return document;
        } else {
            throw new OMException("Unexpected event. There is no container to add the node to.");
        }
    }
    
    private void addNode(OMNode node) {
        if (root == null && node instanceof OMElement) {
            root = (OMElement)node;
        }
        lastNode = node;
    }
    
    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
        document = factory.createOMDocument(this);
    }

    public void endDocument() throws SAXException {
        ((OMContainerEx)document).setComplete(true);
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
//        addNode(factory.createOMDocType(getContainer(), ""));
    }

    public void endDTD() throws SAXException {
    }

    protected OMElement createNextElement(String localName) throws OMException {
        OMElement element = factory.createOMElement(localName, getContainer(), this);
        addNode(element);
        return element;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     *      java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        if (nextElem == null) {
            nextElem = createNextElement("DUMMY");
        }
        ((OMElementEx)nextElem).addNamespaceDeclaration(uri, prefix);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
        if (localName == null || localName.trim().equals(""))
            localName = qName.substring(qName.indexOf(':') + 1);
        if (nextElem == null)
            nextElem = createNextElement(localName);
        else
            nextElem.setLocalName(localName);

        int idx = qName.indexOf(':');
        String prefix = idx == -1 ? "" : qName.substring(0, idx);
        BuilderUtil.setNamespace(nextElem, namespaceURI, prefix, false);
        
        int j = atts.getLength();
        for (int i = 0; i < j; i++) {
            // Note that some SAX parsers report namespace declarations as attributes in addition
            // to calling start/endPrefixMapping.
            // NOTE: This filter was introduced to make SAXOMBuilder work with some versions of
            //       XMLBeans (2.3.0). It is not clear whether this is a bug in XMLBeans or not.
            //       See http://forum.springframework.org/showthread.php?t=43958 for a discussion.
            //       If this test causes problems with other parsers, don't hesitate to remove it.
            if (!atts.getQName(i).startsWith("xmlns")) {
                String attrNamespaceURI = atts.getURI(i);
                OMNamespace ns;
                if (attrNamespaceURI.length() > 0) {
                    ns = nextElem.findNamespace(atts.getURI(i), null);
                    if (ns == null) {
                        // The "xml" prefix is not necessarily declared explicitly; in this case,
                        // create a new OMNamespace instance.
                        if (attrNamespaceURI.equals(XMLConstants.XML_NS_URI)) {
                            ns = factory.createOMNamespace(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
                        } else {
                            throw new SAXException("Unbound namespace " + attrNamespaceURI);
                        }
                    }
                } else {
                    ns = null;
                }
                OMAttribute attr = nextElem.addAttribute(atts.getLocalName(i), atts.getValue(i), ns);
                attr.setAttributeType(atts.getType(i));
            }
        }
        
        lastNode = nextElem;
        nextElem = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (lastNode.isComplete()) {
            OMContainer parent = lastNode.getParent();
            ((OMNodeEx) parent).setComplete(true);
            lastNode = (OMNode) parent;
        } else {
            OMElement e = (OMElement) lastNode;
            ((OMNodeEx) e).setComplete(true);
        }
    }

    public void startCDATA() throws SAXException {
        textNodeType = OMNode.CDATA_SECTION_NODE;
    }

    public void endCDATA() throws SAXException {
        textNodeType = OMNode.TEXT_NODE;
    }

    public void characterData(char[] ch, int start, int length, int nodeType)
            throws SAXException {
        addNode(factory.createOMText(getContainer(), new String(ch, start, length), nodeType, true));
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        characterData(ch, start, length, textNodeType);
    }
    
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        characterData(ch, start, length, OMNode.SPACE_NODE);
    }

    public void processingInstruction(String target, String data)
            throws SAXException {
        addNode(factory.createOMProcessingInstruction(getContainer(), target, data, true));
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (lastNode == null) {
            // Do nothing: the comment appears before the root element.
            return;
        } 
        addNode(factory.createOMComment(getContainer(), new String(ch, start, length), true));
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void startEntity(String name) throws SAXException {
    }

    public void endEntity(String name) throws SAXException {
    }

    public OMDocument getDocument() {
        if (document != null && document.isComplete()) {
            return document;
        } else {
            throw new OMException("Tree not complete");
        }
    }
    
    /**
     * Get the root element of the Axiom tree built by this content handler.
     * 
     * @return the root element of the tree
     * @throws OMException if the tree is not complete
     */
    public OMElement getRootElement() {
        if (root != null && root.isComplete()) {
            return root;
        } else {
            throw new OMException("Tree not complete");
        }
    }

    public int next() throws OMException {
        throw new UnsupportedOperationException();
    }

    public void discard(OMElement el) throws OMException {
        throw new UnsupportedOperationException();
    }

    public void setCache(boolean b) throws OMException {
        throw new UnsupportedOperationException();
    }

    public boolean isCache() {
        throw new UnsupportedOperationException();
    }

    public Object getParser() {
        throw new UnsupportedOperationException();
    }

    public boolean isCompleted() {
        throw new UnsupportedOperationException();
    }

    public OMElement getDocumentElement() {
        throw new UnsupportedOperationException();
    }

    public OMElement getDocumentElement(boolean discardDocument) {
        throw new UnsupportedOperationException();
    }

    public short getBuilderType() {
        throw new UnsupportedOperationException();
    }

    public void registerExternalContentHandler(Object obj) {
        throw new UnsupportedOperationException();
    }

    public Object getRegisteredContentHandler() {
        throw new UnsupportedOperationException();
    }

    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    public void close() {
        throw new UnsupportedOperationException();
    }
}
