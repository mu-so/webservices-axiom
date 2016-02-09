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

import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreEntityReference;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeType;

public aspect CoreEntityReferenceSupport {
    private String CoreEntityReference.name;
    private String CoreEntityReference.replacementText;
    
    public final NodeType CoreEntityReference.coreGetNodeType() {
        return NodeType.ENTITY_REFERENCE;
    }
    
    public final String CoreEntityReference.coreGetName() {
        return name;
    }
    
    public final void CoreEntityReference.coreSetName(String name) {
        this.name = name;
    }
    
    public final String CoreEntityReference.coreGetReplacementText() {
        return replacementText;
    }
    
    public final void CoreEntityReference.coreSetReplacementText(String replacementText) {
        this.replacementText = replacementText;
    }
    
    public final <T> void CoreEntityReference.init(ClonePolicy<T> policy, T options, CoreNode other) {
        CoreEntityReference o = (CoreEntityReference)other;
        coreSetName(o.coreGetName());
        coreSetReplacementText(o.coreGetReplacementText());
    }
}
