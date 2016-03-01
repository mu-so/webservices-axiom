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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.axiom.om.OMElement;

public final class Sequence {
    private List<Class<? extends OMElement>> types;
    
    public Sequence(Class<?>... types) {
        this.types = new ArrayList<Class<? extends OMElement>>(types.length);
        for (Class<?> type : types) {
            this.types.add(type.asSubclass(OMElement.class));
        }
    }
    
    public Class<? extends OMElement> item(int index) {
        return types.get(index);
    }
    
    public int index(Class<? extends OMElement> type) {
        int index = types.indexOf(type);
        if (index == -1) {
            throw new NoSuchElementException();
        }
        return index;
    }
}
