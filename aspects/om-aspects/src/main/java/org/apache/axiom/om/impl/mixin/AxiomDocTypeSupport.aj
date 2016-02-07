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
package org.apache.axiom.om.impl.mixin;

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.impl.intf.AxiomDocType;
import org.apache.axiom.om.impl.stream.StreamException;

public aspect AxiomDocTypeSupport {
    public final int AxiomDocType.getType() {
        return DTD_NODE;
    }

    public final String AxiomDocType.getRootName() {
        return coreGetRootName();
    }

    public final void AxiomDocType.internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws StreamException {
        serializer.processDocumentTypeDeclaration(coreGetRootName(), coreGetPublicId(), coreGetSystemId(), coreGetInternalSubset());
    }
    
    public final void AxiomDocType.buildWithAttachments() {
    }
}
