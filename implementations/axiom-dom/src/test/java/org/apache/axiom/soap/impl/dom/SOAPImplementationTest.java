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
package org.apache.axiom.soap.impl.dom;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axiom.om.impl.dom.factory.OMDOMMetaFactory;
import org.apache.axiom.ts.soap.SOAPTestSuiteBuilder;

public class SOAPImplementationTest extends TestCase {
    public static TestSuite suite() {
        SOAPTestSuiteBuilder builder = new SOAPTestSuiteBuilder(OMDOMMetaFactory.INSTANCE, false);
        
        // TODO: currently broken; need a better solution for parent checks
        builder.exclude(org.apache.axiom.ts.soap.fault.TestWrongParent1.class);
        builder.exclude(org.apache.axiom.ts.soap.fault.TestWrongParent2.class);
        builder.exclude(org.apache.axiom.ts.soap.fault.TestWrongParent3.class);
        builder.exclude(org.apache.axiom.ts.soap.headerblock.TestWrongParent1.class);
        builder.exclude(org.apache.axiom.ts.soap.headerblock.TestWrongParent2.class);
        builder.exclude(org.apache.axiom.ts.soap.headerblock.TestWrongParent3.class);
        
        return builder.build();
    }
}
