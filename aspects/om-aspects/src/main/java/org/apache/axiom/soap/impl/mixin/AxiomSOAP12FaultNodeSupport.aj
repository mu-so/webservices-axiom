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
package org.apache.axiom.soap.impl.mixin;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultNode;

public aspect AxiomSOAP12FaultNodeSupport {
    public final Class<? extends CoreNode> AxiomSOAP12FaultNode.coreGetNodeClass() {
        return AxiomSOAP12FaultNode.class;
    }

    public final boolean AxiomSOAP12FaultNode.isChildElementAllowed(OMElement child) {
        return false;
    }

    public final void AxiomSOAP12FaultNode.setFaultNodeValue(String uri) {
        setText(uri);
    }

    public final String AxiomSOAP12FaultNode.getFaultNodeValue() {
        return getText();
    }
}
