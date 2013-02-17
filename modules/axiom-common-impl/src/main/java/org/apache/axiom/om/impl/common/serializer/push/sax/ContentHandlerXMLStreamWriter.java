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
package org.apache.axiom.om.impl.common.serializer.push.sax;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

final class ContentHandlerXMLStreamWriter implements XMLStreamWriter {
    private final SAXHelper helper;
    private final ContentHandler contentHandler;
    private final LexicalHandler lexicalHandler;
    
    /**
     * The namespace context of the {@link XMLStreamWriter}. This namespace context is inherited
     * from the {@link SAXSerializer}.
     */
    private final ScopedNamespaceContext writerNsContext;

    /**
     * Tracks the namespace declarations actually written using
     * {@link XMLStreamWriter#writeNamespace(String, String)} and
     * {@link XMLStreamWriter#writeDefaultNamespace(String)}. Note that the
     * {@link ScopedNamespaceContext} is actually not used as a {@link NamespaceContext}, but merely
     * to remember the namespace declarations. This is necessary to generate the necessary
     * {@link ContentHandler#endPrefixMapping(String)} events.
     */
    private final ScopedNamespaceContext outputNsContext = new ScopedNamespaceContext();
    
    private final AttributesImpl attributes = new AttributesImpl();

    ContentHandlerXMLStreamWriter(SAXHelper helper, ContentHandler contentHandler, LexicalHandler lexicalHandler,
            ScopedNamespaceContext nsContext) {
        this.helper = helper;
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
        writerNsContext = nsContext;
    }

    private static String normalize(String s) {
        return s == null ? "" : s;
    }
    
    public NamespaceContext getNamespaceContext() {
        return writerNsContext;
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        writerNsContext.setPrefix("", normalize(uri));
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return writerNsContext.getPrefix(uri);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        finishStartElementIfNecessary();
        helper.beginStartElement(normalize(prefix), normalize(namespaceURI), localName);
        writerNsContext.startScope();
        outputNsContext.startScope();
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        namespaceURI = normalize(namespaceURI);
        outputNsContext.setPrefix("", namespaceURI);
        try {
            contentHandler.startPrefixMapping("", namespaceURI);
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        helper.addAttribute(normalize(prefix), normalize(namespaceURI), localName, "CDATA", value);
    }

    private void finishStartElementIfNecessary() throws XMLStreamException {
        if (helper.isInStartElement()) {
            try {
                helper.finishStartElement(contentHandler);
            } catch (SAXException ex) {
                throw new SAXExceptionWrapper(ex);
            }
        }
    }
    
    public void writeEndElement() throws XMLStreamException {
        finishStartElementIfNecessary();
        try {
            helper.writeEndElement(contentHandler, outputNsContext);
            writerNsContext.endScope();
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void writeCharacters(String text) throws XMLStreamException {
        finishStartElementIfNecessary();
        try {
            char[] ch = text.toCharArray();
            contentHandler.characters(ch, 0, ch.length);
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void flush() throws XMLStreamException {
    }

    public void close() throws XMLStreamException {
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeAttribute(String namespaceURI, String localName, String value)
            throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeCData(String data) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeComment(String data) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeEndDocument() throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeStartDocument() throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }
}
