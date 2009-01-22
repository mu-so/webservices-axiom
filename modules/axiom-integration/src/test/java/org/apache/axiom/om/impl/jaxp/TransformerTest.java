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

package org.apache.axiom.om.impl.jaxp;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLIdentical;
import static org.custommonkey.xmlunit.XMLUnit.compareXML;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TransformerTest {
    private final TransformerFactory factory;
    
    @Parameters
    public static List<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                { org.apache.xalan.processor.TransformerFactoryImpl.class },
                { net.sf.saxon.TransformerFactoryImpl.class }
        });
    }
    
    public TransformerTest(Class<? extends TransformerFactory> factoryClass) throws Exception {
        this.factory = factoryClass.newInstance();
    }

    private InputStream getInput() {
        return TransformerTest.class.getResourceAsStream("test.xml");
    }
    
    @Test
    public void test() throws Exception {
        Transformer transformer = factory.newTransformer();
        
        OMSource omSource = new OMSource(new StAXOMBuilder(getInput()).getDocumentElement());
        OMResult omResult = new OMResult();
        transformer.transform(omSource, omResult);
        
        StreamSource streamSource = new StreamSource(getInput());
        StringWriter out = new StringWriter();
        StreamResult streamResult = new StreamResult(out);
        transformer.transform(streamSource, streamResult);
        
        assertXMLIdentical(compareXML(out.toString(), omResult.getRootElement().toString()), true);
    }
}
