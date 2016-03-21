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
package org.apache.axiom.om.impl.dom.factory;

import org.apache.axiom.dom.DOMNodeFactory;
import org.apache.axiom.om.impl.common.factory.AxiomNodeFactoryImpl;

public final class DOOMNodeFactory extends AxiomNodeFactoryImpl implements DOMNodeFactory {
    public static final DOOMNodeFactory INSTANCE = new DOOMNodeFactory();
    
    private DOOMNodeFactory() {
        super(DOOMNodeFactory.class.getClassLoader(),
                "org.apache.axiom.om.impl.dom",
                "org.apache.axiom.soap.impl.dom",
                "org.apache.axiom.soap.impl.dom.soap11",
                "org.apache.axiom.soap.impl.dom.soap12");
    }
}
