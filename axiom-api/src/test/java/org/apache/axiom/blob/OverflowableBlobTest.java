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
package org.apache.axiom.blob;

import org.apache.axiom.blob.suite.WritableBlobTestSuiteBuilder;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class OverflowableBlobTest extends TestCase {
    public static TestSuite suite() {
        return new WritableBlobTestSuiteBuilder(new WritableBlobFactory<OverflowableBlob>() {
            public OverflowableBlob createBlob() {
                return Blobs.createOverflowableBlob(16*1024, "test", ".dat", null);
            }
        }, new int[] { 10000, 16*1024, 100000 }, true, false).build();
    }
}
