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
package org.apache.axiom.core.impl.mixin;

import org.apache.axiom.core.Builder;
import org.apache.axiom.core.DeferringParentNode;
import org.apache.axiom.core.InputContext;

public aspect DeferringParentNodeSupport {
    private InputContext DeferringParentNode.context;

    public final Builder DeferringParentNode.coreGetBuilder() {
        forceExpand();
        return context == null ? null : context.getBuilder();
    }

    public final InputContext DeferringParentNode.coreGetInputContext() {
        return context;
    }

    public final void DeferringParentNode.coreSetInputContext(InputContext context) {
        this.context = context;
        coreSetState(context == null ? COMPLETE : INCOMPLETE);
    }
}
