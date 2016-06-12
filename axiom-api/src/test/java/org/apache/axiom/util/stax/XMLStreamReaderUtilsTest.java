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
package org.apache.axiom.util.stax;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Random;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.base64.Base64EncodingStringBufferOutputStream;
import org.apache.commons.io.IOUtils;

public class XMLStreamReaderUtilsTest extends TestCase {
    /**
     * Test that {@link XMLStreamReaderUtils#getDataHandlerFromElement(XMLStreamReader)}
     * returns an empty {@link DataHandler} when the element is empty. The test uses
     * an {@link XMLStreamReader} instance that doesn't implement the
     * {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader} extension.
     * 
     * @throws Exception
     */
    public void testGetDataHandlerFromElementWithZeroLengthNonDHR() throws Exception {
        testGetDataHandlerFromElementWithZeroLength(false);
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getDataHandlerFromElement(XMLStreamReader)}
     * returns an empty {@link DataHandler} when the element is empty. The test uses
     * an {@link XMLStreamReader} instance that implements the
     * {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader} extension.
     * 
     * @throws Exception
     */
    public void testGetDataHandlerFromElementWithZeroLengthDHR() throws Exception {
        testGetDataHandlerFromElementWithZeroLength(true);
    }
    
    private void testGetDataHandlerFromElementWithZeroLength(boolean useDHR) throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<test/>"));
        if (useDHR) {
            reader = new XMLStreamReaderWithDataHandlerReader(reader);
        }
        try {
            reader.next();
            
            // Check precondition
            assertTrue(reader.isStartElement());
            
            DataHandler dh = XMLStreamReaderUtils.getDataHandlerFromElement(reader);
            
            // Check postcondition
            assertTrue(reader.isEndElement());
            assertEquals(-1, dh.getInputStream().read());
        } finally {
            reader.close();
        }
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getDataHandlerFromElement(XMLStreamReader)}
     * throws an exception if the element has unexpected content. The test uses
     * an {@link XMLStreamReader} instance that doesn't implement the
     * {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader} extension.
     * 
     * @throws Exception
     */
    public void testGetDataHandlerFromElementWithUnexpectedContentNonDHR() throws Exception {
        testGetDataHandlerFromElementWithUnexpectedContent(false);
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getDataHandlerFromElement(XMLStreamReader)}
     * throws an exception if the element has unexpected content. The test uses
     * an {@link XMLStreamReader} instance that implements the
     * {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader} extension.
     * 
     * @throws Exception
     */
    public void testGetDataHandlerFromElementWithUnexpectedContentDHR() throws Exception {
        testGetDataHandlerFromElementWithUnexpectedContent(true);
    }
    
    private void testGetDataHandlerFromElementWithUnexpectedContent(boolean useDHR) throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<test>\n<child/>\n</test>"));
        if (useDHR) {
            reader = new XMLStreamReaderWithDataHandlerReader(reader);
        }
        try {
            reader.next();
            
            // Check precondition
            assertTrue(reader.isStartElement());
            
            try {
                XMLStreamReaderUtils.getDataHandlerFromElement(reader);
                fail("Expected XMLStreamException");
            } catch (XMLStreamException ex) {
                // Expected
            }
        } finally {
            reader.close();
        }
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getDataHandlerFromElement(XMLStreamReader)}
     * correctly decodes base64 data if the parser is non coalescing and produces the data
     * as multiple <tt>CHARACTER</tt> events. The test uses an {@link XMLStreamReader} instance
     * that doesn't implement the {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader}
     * extension.
     * 
     * @throws Exception
     */
    public void testGetDataHandlerFromElementNonCoalescingNonDHR() throws Exception {
        testGetDataHandlerFromElementNonCoalescing(false);
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getDataHandlerFromElement(XMLStreamReader)}
     * correctly decodes base64 data if the parser is non coalescing and produces the data
     * as multiple <tt>CHARACTER</tt> events. The test uses an {@link XMLStreamReader} instance
     * that implements the {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader}
     * extension.
     * 
     * @throws Exception
     */
    public void testGetDataHandlerFromElementNonCoalescingDHR() throws Exception {
        testGetDataHandlerFromElementNonCoalescing(true);
    }
    
    private void testGetDataHandlerFromElementNonCoalescing(boolean useDHR) throws Exception {
        // We generate base64 that is sufficiently large to force the parser to generate
        // multiple CHARACTER events
        StringBuffer buffer = new StringBuffer("<test>");
        Base64EncodingStringBufferOutputStream out = new Base64EncodingStringBufferOutputStream(buffer);
        byte[] data = new byte[65536];
        new Random().nextBytes(data);
        out.write(data);
        out.complete();
        buffer.append("</test>");
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(StAXParserConfiguration.NON_COALESCING,
                new StringReader(buffer.toString()));
        if (useDHR) {
            reader = new XMLStreamReaderWithDataHandlerReader(reader);
        }
        try {
            reader.next();
            
            // Check precondition
            assertTrue(reader.isStartElement());
            
            DataHandler dh = XMLStreamReaderUtils.getDataHandlerFromElement(reader);
            
            // Check postcondition
            assertTrue(reader.isEndElement());
            assertTrue(Arrays.equals(data, IOUtils.toByteArray(dh.getInputStream())));
        } finally {
            reader.close();
        }
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getDataHandlerFromElement(XMLStreamReader)} correctly
     * decodes base64 encoded content that contains whitespace. This is a regression test for <a
     * href="https://issues.apache.org/jira/browse/AXIOM-380">AXIOM-380</a>.
     * 
     * @throws Exception
     */
    public void testGetDataHandlerFromElementWithWhitespace() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader(
                "<data>MS4wMToxNDIdMS4wMjowMzAwHTEuMDM6MR8wMx4yHzAwHjQfMDEeNB8wMh0xLjA0OlBOUx0xLjA1\r\n" + 
                "OjIwMTEwODAyHTEuMDY6Mh0xLjA3OkZMRkRMRUNWWh0xLjA4OkZMMDM3ODhXMB0xLjA5OjExMDgw\r\n" + 
                "MjAwMDcdMS4xMToxOS42OR0xLjEyOjE5LjY5HDIuMDAxOjE4HTIuMDAyOjAwHAAA</data>"));
        try {
            reader.next();
            DataHandler dh = XMLStreamReaderUtils.getDataHandlerFromElement(reader);
            assertEquals("1.01:1421.02:03001.03:1032004014021.04:PNS1.05:201108021.06:21.07:FLFDLECVZ1.08:FL03788W01.09:11080200071.11:19.691.12:19.692.001:182.002:00  ",
                    IOUtils.toString(dh.getInputStream(), "ascii"));
        } finally {
            reader.close();
        }
    }
    
    public void testGetElementTextAsStream() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<a>test</a>"));
        reader.next();
        Reader in = XMLStreamReaderUtils.getElementTextAsStream(reader, false);
        assertEquals("test", IOUtils.toString(in));
        assertEquals(XMLStreamReader.END_ELEMENT, reader.getEventType());
    }
    
    public void testGetElementTextAsStreamWithAllowedNonTextChildren() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<a>xxx<b>yyy</b>zzz</a>"));
        reader.next();
        Reader in = XMLStreamReaderUtils.getElementTextAsStream(reader, true);
        assertEquals("xxxzzz", IOUtils.toString(in));
    }
    
    public void testGetElementTextAsStreamWithForbiddenNonTextChildren() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<a>xxx<b>yyy</b>zzz</a>"));
        reader.next();
        Reader in = XMLStreamReaderUtils.getElementTextAsStream(reader, false);
        try {
            IOUtils.toString(in);
            fail("Expected exception");
        } catch (IOException ex) {
            // Expected
        }
    }
}
