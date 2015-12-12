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
package org.apache.axiom.ts.soap.header;

import static com.google.common.truth.Truth.assertThat;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

public class TestExamineHeaderBlocksWithParser extends SampleBasedSOAPTestCase {
    public TestExamineHeaderBlocksWithParser(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec, SOAPSampleSet.ROLES);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        String roleNextURI = spec.getNextRoleURI();
        Iterator it = envelope.getHeader().examineHeaderBlocks(roleNextURI);
        assertThat(it.hasNext()).isTrue();
        SOAPHeaderBlock headerBlock = (SOAPHeaderBlock)it.next();
        assertThat(headerBlock.getQName()).isEqualTo(new QName("http://example.org/RoleTest", "h2"));
        assertThat(headerBlock.getRole()).isEqualTo(roleNextURI);
        assertThat(it.hasNext()).isTrue();
        headerBlock = (SOAPHeaderBlock)it.next();
        assertThat(headerBlock.getQName()).isEqualTo(new QName("http://example.org/RoleTest", "h7"));
        assertThat(headerBlock.getRole()).isEqualTo(roleNextURI);
        assertThat(it.hasNext()).isFalse();
    }
}
