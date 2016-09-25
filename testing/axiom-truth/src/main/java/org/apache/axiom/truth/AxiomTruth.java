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
package org.apache.axiom.truth;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;

import com.google.common.truth.Truth;

public final class AxiomTruth {
    private AxiomTruth() {}
    
    public static OMContainerSubject assertThat(OMContainer target) {
        return new OMContainerSubject(Truth.THROW_ASSERTION_ERROR, target);
    }
    
    public static OMElementSubject assertThat(OMElement target) {
        return new OMElementSubject(Truth.THROW_ASSERTION_ERROR, target);
    }
    
    public static OMAttributeSubject assertThat(OMAttribute target) {
        return new OMAttributeSubject(Truth.THROW_ASSERTION_ERROR, target);
    }
}
