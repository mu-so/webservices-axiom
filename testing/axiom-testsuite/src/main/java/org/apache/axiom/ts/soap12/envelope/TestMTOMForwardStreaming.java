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
package org.apache.axiom.ts.soap12.envelope;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.testutils.activation.TestDataSource;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that attachments are streamed (i.e. not read entirely into memory) if the original message
 * was read from an input stream and
 * {@link OMElement#serializeAndConsume(java.io.OutputStream, OMOutputFormat)} is used to serialize
 * the message. This feature is important because it allows projects such as Synapse to forward MTOM
 * messages very efficiently.
 */
public class TestMTOMForwardStreaming extends AxiomTestCase {
    private final boolean buildSOAPPart;
    
    public TestMTOMForwardStreaming(OMMetaFactory metaFactory, boolean buildSOAPPart) {
        super(metaFactory);
        addTestParameter("buildSOAPPart", buildSOAPPart);
        this.buildSOAPPart = buildSOAPPart;
    }

    @Override
    protected void runTest() throws Throwable {
        DataSource ds1 = new TestDataSource('A', Runtime.getRuntime().maxMemory());
        DataSource ds2 = new TestDataSource('B', Runtime.getRuntime().maxMemory());
        
        // Programmatically create the original message
        SOAPFactory factory = metaFactory.getSOAP12Factory();
        final SOAPEnvelope orgEnvelope = factory.createSOAPEnvelope();
        SOAPBody orgBody = factory.createSOAPBody(orgEnvelope);
        OMElement orgBodyElement = factory.createOMElement("test", factory.createOMNamespace("urn:test", "p"), orgBody);
        OMElement orgData1 = factory.createOMElement("data", null, orgBodyElement);
        orgData1.addChild(factory.createOMText(new DataHandler(ds1), true));
        OMElement orgData2 = factory.createOMElement("data", null, orgBodyElement);
        orgData2.addChild(factory.createOMText(new DataHandler(ds2), true));
        
        final OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        format.setSOAP11(false);
        final String contentType = format.getContentType();
        
        final PipedOutputStream pipe1Out = new PipedOutputStream();
        final PipedInputStream pipe1In = new PipedInputStream(pipe1Out);
        
        // Create the producer thread (simulating the client sending the MTOM message)
        Thread producerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        orgEnvelope.serialize(pipe1Out, format);
                    } finally {
                        pipe1Out.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        producerThread.start();
        
        final PipedOutputStream pipe2Out = new PipedOutputStream();
        PipedInputStream pipe2In = new PipedInputStream(pipe2Out);
        
        // Create the forwarder thread (simulating the mediation engine that forwards the message)
        Thread forwarderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        Attachments attachments = new Attachments(pipe1In, contentType);
                        SOAPEnvelope envelope = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory, attachments).getSOAPEnvelope();
                        // The code path executed by serializeAndConsume is significantly different if
                        // the element is built. Therefore we need two different test executions.
                        if (buildSOAPPart) {
                            envelope.build();
                        }
                        // Usage of serializeAndConsume should enable streaming
                        envelope.serializeAndConsume(pipe2Out, format);
                    } finally {
                        pipe2Out.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        forwarderThread.start();
        
        try {
            Attachments attachments = new Attachments(pipe2In, contentType);
            SOAPEnvelope envelope = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory, attachments).getSOAPEnvelope();
            OMElement bodyElement = envelope.getBody().getFirstElement();
            Iterator<OMElement> it = bodyElement.getChildElements();
            OMElement data1 = it.next();
            OMElement data2 = it.next();
            
            IOTestUtils.compareStreams(ds1.getInputStream(),
                    ((DataHandlerExt)((OMText)data1.getFirstOMChild()).getDataHandler()).readOnce());
            IOTestUtils.compareStreams(ds2.getInputStream(),
                    ((DataHandlerExt)((OMText)data2.getFirstOMChild()).getDataHandler()).readOnce());
        } finally {
            pipe2In.close();
        }
    }
}
