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
package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;

/**
 * Interface that is used internally by Axiom and that should not be considered being part of the
 * public API.
 */
public interface OMFactoryEx extends OMFactory {

    <T extends AxiomElement> T createAxiomElement(Class<T> type, String localName,
            OMContainer parent, OMXMLParserWrapper builder);

    OMText createOMText(OMContainer parent, Object dataHandler, boolean optimize, boolean fromBuilder);
    
    OMText createOMText(OMContainer parent, String text, int type, boolean fromBuilder);
    
    OMDocType createOMDocType(OMContainer parent, String rootName, String publicId, String systemId,
            String internalSubset, boolean fromBuilder);
    
    OMProcessingInstruction createOMProcessingInstruction(OMContainer parent,
            String piTarget, String piData, boolean fromBuilder);
    
    OMEntityReference createOMEntityReference(OMContainer parent, String name, String replacementText, boolean fromBuilder);
    
    /**
     * This method is intended only to be used by Axiom intenrals when merging nodes from different
     * Axiom implementations.
     *
     * @param child
     */
    OMNode importNode(OMNode child);
}
