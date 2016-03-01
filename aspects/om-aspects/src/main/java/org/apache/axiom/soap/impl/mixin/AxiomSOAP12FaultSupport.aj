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
import org.apache.axiom.om.impl.intf.Sequence;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Fault;

public aspect AxiomSOAP12FaultSupport {
    private static final Sequence sequence = new Sequence(SOAPFaultCode.class, SOAPFaultReason.class,
            SOAPFaultNode.class, SOAPFaultRole.class, SOAPFaultDetail.class);

    public final Class<? extends CoreNode> AxiomSOAP12Fault.coreGetNodeClass() {
        return AxiomSOAP12Fault.class;
    }
    
    public final Sequence AxiomSOAP12Fault.getSequence() {
        return sequence;
    }
    
    public final void AxiomSOAP12Fault.setNode(SOAPFaultNode node) {
        insertChild(sequence, 2, node, true);
    }

    public final SOAPFaultCode AxiomSOAP12Fault.getCode() {
        return (SOAPFaultCode)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_CODE);
    }

    public final SOAPFaultReason AxiomSOAP12Fault.getReason() {
        return (SOAPFaultReason)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_REASON);
    }

    public final SOAPFaultNode AxiomSOAP12Fault.getNode() {
        return (SOAPFaultNode)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_NODE);
    }

    public final SOAPFaultRole AxiomSOAP12Fault.getRole() {
        return (SOAPFaultRole)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_ROLE);
    }

    public final SOAPFaultDetail AxiomSOAP12Fault.getDetail() {
        return (SOAPFaultDetail)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_DETAIL);
    }
}
