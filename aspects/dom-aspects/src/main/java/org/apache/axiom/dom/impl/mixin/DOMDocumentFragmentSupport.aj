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

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.ElementAction;
import org.apache.axiom.dom.DOMDocumentFragment;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMSemantics;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public aspect DOMDocumentFragmentSupport {
    public final Document DOMDocumentFragment.getOwnerDocument() {
        return (Document)coreGetOwnerDocument(true);
    }

    public final short DOMDocumentFragment.getNodeType() {
        return Node.DOCUMENT_FRAGMENT_NODE;
    }

    public final String DOMDocumentFragment.getNodeName() {
        return "#document-fragment";
    }

    public final String DOMDocumentFragment.getNodeValue() {
        return null;
    }

    public final void DOMDocumentFragment.setNodeValue(String nodeValue) {
    }

    public final CoreElement DOMDocumentFragment.getNamespaceContext() {
        return null;
    }

    public final String DOMDocumentFragment.getPrefix() {
        return null;
    }

    public final void DOMDocumentFragment.setPrefix(String prefix) throws DOMException {
        throw newDOMException(DOMException.NAMESPACE_ERR);
    }

    public final String DOMDocumentFragment.getNamespaceURI() {
        return null;
    }

    public final String DOMDocumentFragment.getLocalName() {
        return null;
    }

    public final boolean DOMDocumentFragment.hasAttributes() {
        return false;
    }

    public final NamedNodeMap DOMDocumentFragment.getAttributes() {
        return null;
    }
    
    public final String DOMDocumentFragment.getTextContent() {
        try {
            return coreGetCharacterData(ElementAction.RECURSE).toString();
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    public final void DOMDocumentFragment.setTextContent(String textContent) {
        try {
            coreSetCharacterData(textContent, DOMSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }
}
