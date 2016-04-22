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

import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMNamespaceDeclaration;
import org.w3c.dom.DOMException;

public aspect DOMNamespaceDeclarationSupport {
    public final String DOMNamespaceDeclaration.getNamespaceURI() {
        return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
    }

    public final String DOMNamespaceDeclaration.getPrefix() {
        return coreGetDeclaredPrefix().length() == 0 ? null : XMLConstants.XMLNS_ATTRIBUTE;
    }

    public final void DOMNamespaceDeclaration.setPrefix(String prefix) throws DOMException {
        // Other DOM implementations allow changing the prefix, but this means that a namespace
        // declaration is transformed into a normal attribute. We don't support this.
        throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
    }

    public final String DOMNamespaceDeclaration.getLocalName() {
        String declaredPrefix = coreGetDeclaredPrefix();
        return declaredPrefix.length() == 0 ? XMLConstants.XMLNS_ATTRIBUTE : declaredPrefix;
    }

    public final String DOMNamespaceDeclaration.getName() {
        String declaredPrefix = coreGetDeclaredPrefix();
        if (declaredPrefix.length() == 0) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        } else {
            return XMLConstants.XMLNS_ATTRIBUTE + ":" + declaredPrefix;
        }
    }

    public final boolean DOMNamespaceDeclaration.isId() {
        return false;
    }
}
