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

import org.apache.axiom.om.OMXMLParserWrapper;

/**
 * Interface for parent nodes.
 */
public interface CoreParentNode extends CoreNode {
    int COMPLETE = 0;
    int INCOMPLETE = 1;
    int DISCARDED = 2;
    int COMPACT = 3;
    
    OMXMLParserWrapper getBuilder();
    void coreSetBuilder(OMXMLParserWrapper builder);
    int getState();
    void coreSetState(int state);
    void build();

    <T extends CoreElement> NodeIterator<T> coreGetElements(Axis axis, Class<T> type, ElementMatcher<? super T> matcher, String namespaceURI, String name, ExceptionTranslator exceptionTranslator, Semantics semantics);
}
