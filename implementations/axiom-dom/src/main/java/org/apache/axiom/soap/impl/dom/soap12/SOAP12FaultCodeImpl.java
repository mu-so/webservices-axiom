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

package org.apache.axiom.soap.impl.dom.soap12;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.ElementHelper;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.dom.SOAPFaultCodeImpl;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultCode;

public class SOAP12FaultCodeImpl extends SOAPFaultCodeImpl implements AxiomSOAP12FaultCode {
    public void setSubCode(SOAPFaultSubCode subCode)
            throws SOAPProcessingException {
        if (!(subCode instanceof SOAP12FaultSubCodeImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.2 implementation of SOAP Fault " +
                            "Sub Code. But received some other implementation");
        }
        ElementHelper.setNewElement(this, getSubCode(), subCode);
    }

    public void setValue(SOAPFaultValue value) throws SOAPProcessingException {
        if (!(value instanceof SOAP12FaultValueImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.2 implementation of SOAP Fault Value. " +
                            "But received some other implementation");
        }
        ElementHelper.setNewElement(this, getValue(), value);
    }

    // TODO: For compatibility with Axiom 1.2.x; remove in Axiom 1.3
    public QName getTextAsQName() {
        return getValueAsQName();
    }

    public void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.2 implementation of SOAP Fault as " +
                            "the parent. But received some other implementation");
        }
    }

    public SOAPFaultValue getValue() {
        return (SOAPFaultValue)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_VALUE);
    }

    public SOAPFaultSubCode getSubCode() {
        return (SOAPFaultSubCode)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_SUBCODE);
    }
}
