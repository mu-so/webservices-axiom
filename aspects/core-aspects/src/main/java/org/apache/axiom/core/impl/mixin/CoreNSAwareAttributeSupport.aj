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
package org.apache.axiom.core.impl.mixin;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;

public aspect CoreNSAwareAttributeSupport {
    public final NodeType CoreNSAwareAttribute.coreGetNodeType() {
        return NodeType.NS_AWARE_ATTRIBUTE;
    }

    public final void CoreNSAwareAttribute.coreSerialize(XmlHandler handler) throws CoreModelException, StreamException {
        handler.processAttribute(coreGetNamespaceURI(), coreGetLocalName(), coreGetPrefix(), coreGetCharacterData().toString(), coreGetType(), coreGetSpecified());
    }
}
