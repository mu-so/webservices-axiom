/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.axiom.core.stream.serializer;

import java.io.IOException;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.serializer.utils.MsgKey;
import org.apache.axiom.core.stream.serializer.utils.Utils;

/**
 * This class converts SAX or SAX-like calls to a 
 * serialized xml document.  The xsl:output method is "xml".
 * 
 * This class is used explicitly in code generated by XSLTC, 
 * so it is "public", but it should 
 * be viewed as internal or package private, this is not an API.
 * 
 * @xsl.usage internal
 */
public class ToXMLStream extends ToStream
{
    /**
     * Map that tells which XML characters should have special treatment, and it
     *  provides character to entity name lookup.
     */
    private CharInfo m_xmlcharInfo =
        CharInfo.getCharInfo(CharInfo.XML_ENTITIES_RESOURCE);

    /**
     * Default constructor.
     */
    public ToXMLStream()
    {
        m_charInfo = m_xmlcharInfo;

        initCDATA();
        // initialize namespaces
        m_prefixMap = new NamespaceMappings();

    }

    /**
     * Copy properties from another SerializerToXML.
     *
     * @param xmlListener non-null reference to a SerializerToXML object.
     */
    public void CopyFrom(ToXMLStream xmlListener)
    {

        setWriter(xmlListener.m_writer);


        // m_outputStream = xmlListener.m_outputStream;
        String encoding = xmlListener.getEncoding();
        setEncoding(encoding);

        setOmitXMLDeclaration(xmlListener.getOmitXMLDeclaration());

        m_ispreserve = xmlListener.m_ispreserve;
        m_preserves = xmlListener.m_preserves;
        m_isprevtext = xmlListener.m_isprevtext;
        m_doIndent = xmlListener.m_doIndent;
        setIndentAmount(xmlListener.getIndentAmount());
        m_startNewLine = xmlListener.m_startNewLine;
        m_needToOutputDocTypeDecl = xmlListener.m_needToOutputDocTypeDecl;
        setDoctypeSystem(xmlListener.getDoctypeSystem());
        setDoctypePublic(xmlListener.getDoctypePublic());        
        setStandalone(xmlListener.getStandalone());
        setMediaType(xmlListener.getMediaType());
        m_encodingInfo = xmlListener.m_encodingInfo;
        m_spaceBeforeClose = xmlListener.m_spaceBeforeClose;
        m_cdataStartCalled = xmlListener.m_cdataStartCalled;

    }

    /**
     * Receive notification of the beginning of a document.
     *
     * @throws StreamException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws StreamException
     */
    public void startDocumentInternal() throws StreamException
    {
        super.startDocumentInternal();

        if (m_inEntityRef)
            return;

        m_needToOutputDocTypeDecl = true;
        m_startNewLine = false;
        /* The call to getXMLVersion() might emit an error message
         * and we should emit this message regardless of if we are 
         * writing out an XML header or not.
         */ 
        final String version = getXMLVersion();
        if (getOmitXMLDeclaration() == false)
        {
            String encoding = Encodings.getMimeEncoding(getEncoding());
            String standalone;

            if (m_standaloneWasSpecified)
            {
                standalone = " standalone=\"" + getStandalone() + "\"";
            }
            else
            {
                standalone = "";
            }

            try
            {
                final java.io.Writer writer = m_writer;
                writer.write("<?xml version=\"");
                writer.write(version);
                writer.write("\" encoding=\"");
                writer.write(encoding);
                writer.write('\"');
                writer.write(standalone);
                writer.write("?>");
                if (m_doIndent) {
                    if (m_standaloneWasSpecified
                            || getDoctypePublic() != null
                            || getDoctypeSystem() != null) {
                        // We almost never put a newline after the XML
                        // header because this XML could be used as
                        // an extenal general parsed entity
                        // and we don't know the context into which it
                        // will be used in the future.  Only when
                        // standalone, or a doctype system or public is
                        // specified are we free to insert a new line
                        // after the header.  Is it even worth bothering
                        // in these rare cases?                           
                        writer.write(m_lineSep, 0, m_lineSepLen);
                    }
                }
            } 
            catch(IOException e)
            {
                throw new StreamException(e);
            }

        }
    }

    /**
     * Receive notification of the end of a document.
     *
     * @throws StreamException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws StreamException
     */
    public void endDocument() throws StreamException
    {
        flushPending();
        if (m_doIndent && !m_isprevtext)
        {
            try
            {
            outputLineSep();
            }
            catch(IOException e)
            {
                throw new StreamException(e);
            }
        }

        flushWriter();
    }

    /**
     * Starts a whitespace preserving section. All characters printed
     * within a preserving section are printed without indentation and
     * without consolidating multiple spaces. This is equivalent to
     * the <tt>xml:space=&quot;preserve&quot;</tt> attribute. Only XML
     * and HTML serializers need to support this method.
     * <p>
     * The contents of the whitespace preserving section will be delivered
     * through the regular <tt>characters</tt> event.
     *
     * @throws StreamException
     */
    public void startPreserving() throws StreamException
    {

        // Not sure this is really what we want.  -sb
        m_preserves.push(true);

        m_ispreserve = true;
    }

    /**
     * Ends a whitespace preserving section.
     *
     * @see #startPreserving
     *
     * @throws StreamException
     */
    public void endPreserving() throws StreamException
    {

        // Not sure this is really what we want.  -sb
        m_ispreserve = m_preserves.isEmpty() ? false : m_preserves.pop();
    }

    /**
     * Receive notification of a processing instruction.
     *
     * @param target The processing instruction target.
     * @param data The processing instruction data, or null if
     *        none was supplied.
     * @throws StreamException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws StreamException
     */
    public void processingInstruction(String target, String data)
        throws StreamException
    {
        if (m_inEntityRef)
            return;
        
        flushPending();   

        if (target.equals(Result.PI_DISABLE_OUTPUT_ESCAPING))
        {
            startNonEscaping();
        }
        else if (target.equals(Result.PI_ENABLE_OUTPUT_ESCAPING))
        {
            endNonEscaping();
        }
        else
        {
            try
            {
                if (shouldIndent())
                    indent();

                final java.io.Writer writer = m_writer;
                writer.write("<?");
                writer.write(target);

                if (data.length() > 0
                    && !Character.isSpaceChar(data.charAt(0)))
                    writer.write(' ');

                int indexOfQLT = data.indexOf("?>");

                if (indexOfQLT >= 0)
                {

                    // See XSLT spec on error recovery of "?>" in PIs.
                    if (indexOfQLT > 0)
                    {
                        writer.write(data.substring(0, indexOfQLT));
                    }

                    writer.write("? >"); // add space between.

                    if ((indexOfQLT + 2) < data.length())
                    {
                        writer.write(data.substring(indexOfQLT + 2));
                    }
                }
                else
                {
                    writer.write(data);
                }

                writer.write('?');
                writer.write('>');
                
                /*
                 * Don't write out any indentation whitespace now,
                 * because there may be non-whitespace text after this.
                 * 
                 * Simply mark that at this point if we do decide
                 * to indent that we should 
                 * add a newline on the end of the current line before
                 * the indentation at the start of the next line.
                 */ 
                m_startNewLine = true;
            }
            catch(IOException e)
            {
                throw new StreamException(e);
            }
        }
    }

    /**
     * Receive notivication of a entityReference.
     *
     * @param name The name of the entity.
     *
     * @throws StreamException
     */
    public void entityReference(String name) throws StreamException
    {
        try
        {
            if (shouldIndent())
                indent();

            final java.io.Writer writer = m_writer;
            writer.write('&');
            writer.write(name);
            writer.write(';');
        }
        catch(IOException e)
        {
            throw new StreamException(e);
        }
    }

    /**
     * @see ExtendedContentHandler#endElement(String)
     */
    public void endElement(String elemName) throws StreamException
    {
        endElement(null, null, elemName);
    }

    /**
     * Try's to reset the super class and reset this class for 
     * re-use, so that you don't need to create a new serializer 
     * (mostly for performance reasons).
     * 
     * @return true if the class was successfuly reset.
     */
    public boolean reset()
    {
        boolean wasReset = false;
        if (super.reset())
        {
            // Make this call when resetToXMLStream does
            // something.
            // resetToXMLStream();
            wasReset = true;
        }
        return wasReset;
    }
    
    /**
     * Reset all of the fields owned by ToStream class
     *
     */
    private void resetToXMLStream()
    {
        // This is an empty method, but is kept for future use
        // as a place holder for a location to reset fields
        // defined within this class
        return;
    }  

    /**
     * This method checks for the XML version of output document.
     * If XML version of output document is not specified, then output 
     * document is of version XML 1.0.
     * If XML version of output doucment is specified, but it is not either 
     * XML 1.0 or XML 1.1, a warning message is generated, the XML Version of
     * output document is set to XML 1.0 and processing continues.
     * @return string (XML version)
     */
    private String getXMLVersion()
    {
        String xmlVersion = getVersion();
        if(xmlVersion == null || xmlVersion.equals(XMLVERSION10))
        {
            xmlVersion = XMLVERSION10;
        }
        else if(xmlVersion.equals(XMLVERSION11))
        {
            xmlVersion = XMLVERSION11;
        }
        else
        {
            String msg = Utils.messages.createMessage(
                               MsgKey.ER_XML_VERSION_NOT_SUPPORTED,new Object[]{ xmlVersion });
            try 
            {
                // Prepare to issue the warning message
                Transformer tran = super.getTransformer();
                ErrorListener errHandler = tran.getErrorListener();
                // Issue the warning message
                if (null != errHandler && m_sourceLocator != null)
                    errHandler.warning(new TransformerException(msg, m_sourceLocator));
                else
                    System.out.println(msg);
            }
            catch (Exception e){}
            xmlVersion = XMLVERSION10;								
        }
        return xmlVersion;
    }
}
