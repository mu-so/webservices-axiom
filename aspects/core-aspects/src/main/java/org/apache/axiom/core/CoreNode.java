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
package org.apache.axiom.core;

public interface CoreNode {
    void coreSetOwnerDocument(CoreDocument document);
    
    NodeFactory coreGetNodeFactory();
    
    <T extends CoreNode> T coreCreateNode(Class<T> type);
    
    /**
     * Get the node type.
     * 
     * @return the node type
     */
    NodeType coreGetNodeType();
    
    Class<? extends CoreNode> coreGetNodeClass();

    /**
     * Clone this node according to the provided policy.
     * 
     * @param policy
     *            the policy to use when cloning this node (and its children)
     * @return the clone of this node
     */
    <T> CoreNode coreClone(ClonePolicy<T> policy, T options) throws CoreModelException;
    
    <T> void init(ClonePolicy<T> policy, T options, CoreNode other) throws CoreModelException;
    <T> void cloneChildrenIfNecessary(ClonePolicy<T> policy, T options, CoreNode clone) throws CoreModelException;
}
