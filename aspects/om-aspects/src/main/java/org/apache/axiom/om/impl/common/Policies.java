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
package org.apache.axiom.om.impl.common;

import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.DetachPolicy;
import org.apache.axiom.core.NSAwareAttributeMatcher;
import org.apache.axiom.core.NamespaceDeclarationMatcher;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

public final class Policies {
    private Policies() {}
    
    public static final DetachPolicy DETACH_POLICY = DetachPolicy.NEW_DOCUMENT;
    
    public static final AttributeMatcher ATTRIBUTE_MATCHER = new NSAwareAttributeMatcher(
            DETACH_POLICY,
            false,  // Axiom doesn't support namespace unaware attributes
            false);

    public static final AttributeMatcher NAMESPACE_DECLARATION_MATCHER = new NamespaceDeclarationMatcher(DETACH_POLICY);
    
    public static final ClonePolicy<OMCloneOptions> CLONE_POLICY = new ClonePolicy<OMCloneOptions>() {
        public Class<? extends CoreNode> getTargetNodeClass(OMCloneOptions options, CoreNode node) {
            if (options != null && options.isPreserveModel()) {
                return node.coreGetNodeClass();
            } else if (options != null && options.isCopyOMDataSources() && node instanceof AxiomSourcedElement) {
                return AxiomSourcedElement.class;
            } else {
                return node.coreGetNodeType().getInterface();
            }
        }

        public boolean repairNamespaces(OMCloneOptions options) {
            return true;
        }

        public boolean cloneAttributes(OMCloneOptions options) {
            return true;
        }

        public boolean cloneChildren(OMCloneOptions options, NodeType nodeType) {
            return true;
        }

        public void postProcess(OMCloneOptions options, CoreNode clone) {
            if (clone instanceof AxiomElement && ((AxiomElement)clone).isExpanded()) {
                // Repair namespaces
                AxiomElement element = (AxiomElement)clone;
                NSUtil.handleNamespace(element, element.getNamespace(), false, true);
                CoreAttribute attr = element.coreGetFirstAttribute();
                while (attr != null) {
                    if (attr instanceof AxiomAttribute) {
                        NSUtil.handleNamespace(element, ((AxiomAttribute)attr).getNamespace(), true, true);
                    }
                    attr = attr.coreGetNextAttribute();
                }
            }
        }
    };
}
