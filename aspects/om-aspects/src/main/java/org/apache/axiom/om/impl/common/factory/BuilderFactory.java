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
package org.apache.axiom.om.impl.common.factory;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.impl.builder.BuilderImpl;
import org.apache.axiom.core.impl.builder.BuilderListener;
import org.apache.axiom.core.impl.builder.PlainXMLModel;
import org.apache.axiom.core.stream.FilteredXmlInput;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.builder.OMXMLParserWrapperImpl;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.impl.common.builder.SOAPFilter;
import org.apache.axiom.soap.impl.common.builder.SOAPModel;
import org.apache.axiom.soap.impl.common.builder.SOAPModelBuilderImpl;
import org.apache.axiom.soap.impl.intf.AxiomSOAPEnvelope;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;

abstract class BuilderFactory<T extends OMXMLParserWrapper> {
    final static BuilderFactory<OMXMLParserWrapper> OM = new BuilderFactory<OMXMLParserWrapper>() {
        @Override
        OMXMLParserWrapper createBuilder(NodeFactory nodeFactory, BuilderSpec spec) {
            return new OMXMLParserWrapperImpl(new BuilderImpl(spec.getInput(), nodeFactory,
                    PlainXMLModel.INSTANCE, null), spec.getDetachable());
        }
    };

    final static BuilderFactory<SOAPModelBuilder> SOAP = new BuilderFactory<SOAPModelBuilder>() {
        @Override
        SOAPModelBuilder createBuilder(NodeFactory nodeFactory, BuilderSpec spec) {
            BuilderImpl builder = new BuilderImpl(new FilteredXmlInput(spec.getInput(), SOAPFilter.INSTANCE), nodeFactory, new SOAPModel(), null);
            // The SOAPFactory instance linked to the SOAPMessage is unknown until we reach the
            // SOAPEnvelope. Register a post-processor that does the necessary updates on the
            // SOAPMessage.
            builder.addListener(new BuilderListener() {
                private AxiomSOAPMessage message;
                
                @Override
                public Runnable nodeAdded(CoreNode node, int depth) {
                    if (node instanceof AxiomSOAPMessage) {
                        message = (AxiomSOAPMessage)node;
                    } else if (message != null && node instanceof AxiomSOAPEnvelope) {
                        message.initSOAPFactory((SOAPFactory)((AxiomSOAPEnvelope)node).getOMFactory());
                    }
                    return null;
                }
            });
            return new SOAPModelBuilderImpl(builder, spec.getDetachable());
        }
    };

    abstract T createBuilder(NodeFactory nodeFactory, BuilderSpec spec);
}
