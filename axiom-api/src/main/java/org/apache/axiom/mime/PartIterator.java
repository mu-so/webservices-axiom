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
package org.apache.axiom.mime;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class PartIterator implements Iterator<Part> {
    private final MultipartBody message;
    private PartImpl part;
    private boolean hasNextCalled;

    PartIterator(MultipartBody message) {
        this.message = message;
    }

    @Override
    public boolean hasNext() {
        if (!hasNextCalled) {
            if (part == null) {
                part = message.getFirstPart();
            } else {
                part = part.getNextPart();
            }
            hasNextCalled = true;
        }
        return part != null;
    }

    @Override
    public Part next() {
        if (hasNext()) {
            hasNextCalled = false;
            return part;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
