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
package org.apache.axiom.ts.jaxp;

import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

final class CrimsonXMLReaderWrapper implements XMLReader {
    private final XMLReader parent;
    
    public CrimsonXMLReaderWrapper(XMLReader parent) {
        this.parent = parent;
    }

    public boolean getFeature(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        return parent.getFeature(name);
    }

    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        parent.setFeature(name, value);
    }

    public Object getProperty(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        return parent.getProperty(name);
    }

    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            value = new CrimsonLexicalHandlerWrapper((LexicalHandler)value);
        }
        parent.setProperty(name, value);
    }

    public void setEntityResolver(EntityResolver resolver) {
        parent.setEntityResolver(resolver);
    }

    public EntityResolver getEntityResolver() {
        return parent.getEntityResolver();
    }

    public void setDTDHandler(DTDHandler handler) {
        parent.setDTDHandler(handler);
    }

    public DTDHandler getDTDHandler() {
        return parent.getDTDHandler();
    }

    public void setContentHandler(ContentHandler handler) {
        parent.setContentHandler(handler);
    }

    public ContentHandler getContentHandler() {
        return parent.getContentHandler();
    }

    public void setErrorHandler(ErrorHandler handler) {
        parent.setErrorHandler(handler);
    }

    public ErrorHandler getErrorHandler() {
        return parent.getErrorHandler();
    }

    public void parse(InputSource input) throws IOException, SAXException {
        parent.parse(input);
    }

    public void parse(String systemId) throws IOException, SAXException {
        parent.parse(systemId);
    }
}
