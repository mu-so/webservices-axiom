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

package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultReason;
import org.apache.axiom.soap.impl.llom.SOAPFaultReasonImpl;

public class SOAP11FaultReasonImpl extends SOAPFaultReasonImpl implements AxiomSOAP11FaultReason {
    public void addSOAPText(SOAPFaultText soapFaultText)
            throws SOAPProcessingException {
        throw new UnsupportedOperationException("addSOAPText() not allowed for SOAP 1.1!");
    }

    public SOAPFaultText getFirstSOAPText() {
        throw new UnsupportedOperationException("getFirstSOAPText() not allowed for SOAP 1.1!");
    }
}
