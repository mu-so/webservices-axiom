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
package org.apache.axiom.dom.impl.mixin;

import javax.xml.XMLConstants;

import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.core.ElementAction;
import org.apache.axiom.dom.AttributesNamedNodeMap;
import org.apache.axiom.dom.DOMAttribute;
import org.apache.axiom.dom.DOMElement;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMSemantics;
import org.apache.axiom.dom.ElementsByTagName;
import org.apache.axiom.dom.ElementsByTagNameNS;
import org.apache.axiom.dom.NSUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

public aspect DOMElementSupport {
    public final Document DOMElement.getOwnerDocument() {
        return (Document)coreGetOwnerDocument(true);
    }

    public final short DOMElement.getNodeType() {
        return Node.ELEMENT_NODE;
    }
    
    public final String DOMElement.getNodeName() {
        return getTagName();
    }

    public final String DOMElement.getNodeValue() {
        return null;
    }

    public final void DOMElement.setNodeValue(String nodeValue) {
    }

    public final String DOMElement.getTagName() {
        return internalGetName();
    }
    
    public final TypeInfo DOMElement.getSchemaTypeInfo() {
        throw new UnsupportedOperationException();
    }

    public final CoreElement DOMElement.getNamespaceContext() {
        return this;
    }

    public final boolean DOMElement.hasAttributes() {
        return coreGetFirstAttribute() != null;
    }

    public final NamedNodeMap DOMElement.getAttributes() {
        return new AttributesNamedNodeMap(this);
    }
    
    public final Attr DOMElement.getAttributeNode(String name) {
        return (DOMAttribute)coreGetAttribute(DOMSemantics.DOM1_ATTRIBUTE_MATCHER, null, name);
    }

    public final Attr DOMElement.getAttributeNodeNS(String namespaceURI, String localName) {
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            return (DOMAttribute)coreGetAttribute(DOMSemantics.NAMESPACE_DECLARATION_MATCHER, null, localName.equals(XMLConstants.XMLNS_ATTRIBUTE) ? "" : localName);
        } else {
            return (DOMAttribute)coreGetAttribute(DOMSemantics.DOM2_ATTRIBUTE_MATCHER, namespaceURI == null ? "" : namespaceURI, localName);
        }
    }
    
    public final String DOMElement.getAttribute(String name) {
        Attr attr = getAttributeNode(name);
        return attr != null ? attr.getValue() : "";
    }

    public final String DOMElement.getAttributeNS(String namespaceURI, String localName) {
        Attr attr = getAttributeNodeNS(namespaceURI, localName);
        return attr != null ? attr.getValue() : "";
    }

    public final boolean DOMElement.hasAttribute(String name) {
        return getAttributeNode(name) != null;
    }

    public final boolean DOMElement.hasAttributeNS(String namespaceURI, String localName) {
        return getAttributeNodeNS(namespaceURI, localName) != null;
    }

    public final void DOMElement.setAttribute(String name, String value) {
        try {
            NSUtil.validateName(name);
            coreSetAttribute(DOMSemantics.DOM1_ATTRIBUTE_MATCHER, null, name, null, value);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    public final void DOMElement.setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        try {
            int i = NSUtil.validateQualifiedName(qualifiedName);
            String prefix;
            String localName;
            if (i == -1) {
                prefix = "";
                localName = qualifiedName;
            } else {
                prefix = qualifiedName.substring(0, i);
                localName = qualifiedName.substring(i+1);
            }
            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
                coreSetAttribute(DOMSemantics.NAMESPACE_DECLARATION_MATCHER, null, NSUtil.getDeclaredPrefix(localName, prefix), null, value);
            } else {
                namespaceURI = NSUtil.normalizeNamespaceURI(namespaceURI);
                NSUtil.validateAttributeName(namespaceURI, localName, prefix);
                coreSetAttribute(DOMSemantics.DOM2_ATTRIBUTE_MATCHER, namespaceURI, localName, prefix, value);
            }
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    public final Attr DOMElement.setAttributeNode(Attr newAttr) throws DOMException {
        return setAttributeNodeNS(newAttr);
    }
    
    public final Attr DOMElement.setAttributeNodeNS(Attr _newAttr) throws DOMException {
        if (!(_newAttr instanceof DOMAttribute)) {
            throw DOMExceptionUtil.newDOMException(DOMException.WRONG_DOCUMENT_ERR);
        }
        DOMAttribute newAttr = (DOMAttribute)_newAttr;
        CoreElement owner = newAttr.coreGetOwnerElement();
        if (owner == this) {
            // This means that the "new" attribute is already linked to the element
            // and replaces itself.
            return newAttr;
        } else if (owner != null) {
            throw DOMExceptionUtil.newDOMException(DOMException.INUSE_ATTRIBUTE_ERR);
        } else {
            if (!coreHasSameOwnerDocument(newAttr)) {
                throw DOMExceptionUtil.newDOMException(DOMException.WRONG_DOCUMENT_ERR);
            }
            AttributeMatcher matcher;
            if (newAttr instanceof CoreNSAwareAttribute) {
                matcher = DOMSemantics.DOM2_ATTRIBUTE_MATCHER;
            } else if (newAttr instanceof CoreNamespaceDeclaration) {
                matcher = DOMSemantics.NAMESPACE_DECLARATION_MATCHER;
            } else {
                // Must be a DOM1 (namespace unaware) attribute
                matcher = DOMSemantics.DOM1_ATTRIBUTE_MATCHER;
            }
            return (DOMAttribute)coreSetAttribute(matcher, newAttr, DOMSemantics.INSTANCE);
        }
    }

    public final Attr DOMElement.removeAttributeNode(Attr oldAttr) throws DOMException {
        if (oldAttr instanceof DOMAttribute) {
            DOMAttribute attr = (DOMAttribute)oldAttr;
            if (attr.coreGetOwnerElement() != this) {
                throw DOMExceptionUtil.newDOMException(DOMException.NOT_FOUND_ERR);
            } else {
                attr.coreRemove(DOMSemantics.INSTANCE);
            }
            return attr;
        } else {
            throw DOMExceptionUtil.newDOMException(DOMException.NOT_FOUND_ERR);
        }
    }

    public final void DOMElement.removeAttribute(String name) throws DOMException {
        // Specs: "If no attribute with this name is found, this method has no effect."
        coreRemoveAttribute(DOMSemantics.DOM1_ATTRIBUTE_MATCHER, null, name, DOMSemantics.INSTANCE);
    }

    public final void DOMElement.removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        // Specs: "If no attribute with this local name and namespace URI is found, this method has no effect."
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            coreRemoveAttribute(DOMSemantics.NAMESPACE_DECLARATION_MATCHER, null, localName.equals(XMLConstants.XMLNS_ATTRIBUTE) ? "" : localName, DOMSemantics.INSTANCE);
        } else {
            coreRemoveAttribute(DOMSemantics.DOM2_ATTRIBUTE_MATCHER, namespaceURI == null ? "" : namespaceURI, localName, DOMSemantics.INSTANCE);
        }
    }
    
    public final String DOMElement.getTextContent() {
        try {
            return coreGetCharacterData(ElementAction.RECURSE).toString();
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    public final void DOMElement.setTextContent(String textContent) {
        try {
            coreSetCharacterData(textContent, DOMSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    public final NodeList DOMElement.getElementsByTagName(String tagname) {
        return new ElementsByTagName(this, tagname);
    }

    public final NodeList DOMElement.getElementsByTagNameNS(String namespaceURI, String localName) {
        return new ElementsByTagNameNS(this, namespaceURI, localName);
    }
}
