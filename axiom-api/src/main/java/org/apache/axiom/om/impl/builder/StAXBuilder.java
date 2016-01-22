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

package org.apache.axiom.om.impl.builder;

import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMAttributeEx;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.Closeable;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Internal implementation class.
 */
public abstract class StAXBuilder implements OMXMLParserWrapper, CustomBuilderSupport {

    private static final Log log = LogFactory.getLog(StAXBuilder.class);
    
    /** Field parser */
    protected XMLStreamReader parser;

    /** Field omfactory */
    protected OMFactoryEx omfactory;
    
    private final Detachable detachable;
    private final Closeable closeable;

    /** Field lastNode */
    protected OMContainerEx target;

    // returns the state of completion

    /** Field done */
    protected boolean done = false;

    // keeps the state of the cache

    /** Field cache */
    protected boolean cache = true;

    // keeps the state of the parser access. if the parser is
    // accessed atleast once,this flag will be set

    /** Field parserAccessed */
    protected boolean parserAccessed = false;
    protected OMDocument document;

    protected String charEncoding = null;
    
    /**
     * Specifies whether the builder/parser should be automatically closed when the
     * {@link XMLStreamConstants#END_DOCUMENT} event is reached.
     */
    boolean autoClose;
    
    protected boolean _isClosed = false;              // Indicate if parser is closed

    // Fields for Custom Builder implementation
    protected CustomBuilder customBuilderForPayload = null;
    protected Map<QName,CustomBuilder> customBuilders = null;
    protected int maxDepthForCustomBuilders = -1;
    
    /**
     * Reference to the {@link DataHandlerReader} extension of the parser, or <code>null</code> if
     * the parser doesn't support this extension.
     */
    protected DataHandlerReader dataHandlerReader;
    
    /**
     * Tracks the depth of the node identified by {@link #target}. By definition, the level of the
     * root element is defined as 1. Note that if caching is disabled, then this depth may be
     * different from the actual depth reached by the underlying parser.
     */
    protected int elementLevel = 0;
    
    /**
     * Stores exceptions thrown by the parser. Used to avoid accessing the parser
     * again after is has thrown a parse exception.
     */
    protected Exception parserException;
    
    /**
     * Stores the stack trace of the code that caused a node to be discarded or consumed. This is
     * only used if debug logging was enabled when builder was created.
     */
    private final Map<OMContainer,Throwable> discardTracker = log.isDebugEnabled() ? new LinkedHashMap<OMContainer,Throwable>() : null;
    
    /**
     * For internal use only.
     */
    protected StAXBuilder(OMFactory omFactory, XMLStreamReader parser, String encoding,
            Detachable detachable, Closeable closeable) {
        omfactory = (OMFactoryEx)omFactory;
        this.detachable = detachable;
        this.closeable = closeable;
        charEncoding = encoding;
        initParser(parser);
    }
    
    /**
     * For internal use only.
     */
    protected StAXBuilder(OMFactory omFactory, XMLStreamReader parser, Detachable detachable,
            Closeable closeable) {
        // The getEncoding information is only available at the START_DOCUMENT event.
        this(omFactory, parser, parser.getEncoding(), detachable, closeable);
    }
    
    private void initParser(XMLStreamReader parser) {
        if (parser instanceof BuilderAwareReader) {
            ((BuilderAwareReader) parser).setBuilder(this);
        }
        dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(parser);
        this.parser = parser;
    }

    /**
     * @deprecated Not used anywhere
     */
    public void init(InputStream inputStream, String charSetEncoding, String url,
                     String contentType) throws OMException {
        try {
            this.parser = StAXUtils.createXMLStreamReader(inputStream);
        } catch (XMLStreamException e1) {
            throw new OMException(e1);
        }
        omfactory = (OMFactoryEx)OMAbstractFactory.getOMFactory();
    }

    /**
     * Method setOMBuilderFactory.
     *
     * @param ombuilderFactory
     */
    public void setOMBuilderFactory(OMFactory ombuilderFactory) {
        this.omfactory = (OMFactoryEx)ombuilderFactory;
    }

    /**
     * Method processNamespaceData.
     *
     * @param node
     */
    protected abstract void processNamespaceData(OMElement node);

    // since the behaviors are different when it comes to namespaces
    // this must be implemented differently

    /**
     * Method processAttributes.
     *
     * @param node
     */
    protected void processAttributes(OMElement node) {
        int attribCount = parser.getAttributeCount();
        for (int i = 0; i < attribCount; i++) {
            String uri = parser.getAttributeNamespace(i);
            String prefix = parser.getAttributePrefix(i);


            OMNamespace namespace = null;
            if (uri != null && uri.length() > 0) {

                // prefix being null means this elements has a default namespace or it has inherited
                // a default namespace from its parent
                namespace = node.findNamespace(uri, prefix);
                if (namespace == null) {
                    namespace = node.declareNamespace(uri, prefix);
                }
            }

            // todo if the attributes are supposed to namespace qualified all the time
            // todo then this should throw an exception here

            OMAttribute attr = node.addAttribute(parser.getAttributeLocalName(i),
                              parser.getAttributeValue(i), namespace);
            attr.setAttributeType(parser.getAttributeType(i));
            if (attr instanceof OMAttributeEx) {
                ((OMAttributeEx)attr).setSpecified(parser.isAttributeSpecified(i));
            }
        }
    }

    /**
     * This method will check whether the text can be optimizable using IS_BINARY flag. If that is
     * set then we try to get the data handler.
     *
     * @param textType
     * @return omNode
     */
    protected OMNode createOMText(int textType) {
        if (dataHandlerReader != null && dataHandlerReader.isBinary()) {
            Object dataHandlerObject;
            if (dataHandlerReader.isDeferred()) {
                dataHandlerObject = dataHandlerReader.getDataHandlerProvider();
            } else {
                try {
                    dataHandlerObject = dataHandlerReader.getDataHandler();
                } catch (XMLStreamException ex) {
                    throw new OMException(ex);
                }
            }
            OMText text = omfactory.createOMText(target, dataHandlerObject, dataHandlerReader.isOptimized(), true);
            String contentID = dataHandlerReader.getContentID();
            if (contentID != null) {
                text.setContentID(contentID);
            }
            return text;
        } else {
            // Some parsers (like Woodstox) parse text nodes lazily and may throw a
            // RuntimeException in getText()
            String text;
            try {
                text = parser.getText();
            } catch (RuntimeException ex) {
                parserException = ex;
                throw ex;
            }
            return omfactory.createOMText(target, text, textType, true);
        }
    }

    private void discarded(OMContainerEx container) {
        container.discarded();
        if (discardTracker != null) {
            discardTracker.put(container, new Throwable());
        }
    }
    
    /**
     * For internal use only.
     * 
     * @param container
     */
    public void debugDiscarded(Object container) {
        if (log.isDebugEnabled() && discardTracker != null) {
            Throwable t = (Throwable)discardTracker.get(container);
            if (t != null) {
                log.debug("About to throw NodeUnavailableException. Location of the code that caused the node to be discarded/consumed:", t);
            }
        }
    }
    
    // For compatibility only
    public void discard(OMElement element) throws OMException {
        discard((OMContainer)element);
        element.discard();
    }
    
    public void discard(OMContainer container) throws OMException {
        int targetElementLevel = elementLevel;
        OMContainerEx current = target;
        while (current != container) {
            targetElementLevel--;
            current = (OMContainerEx)((OMElement)current).getParent();
        }
        if (targetElementLevel == 0 || targetElementLevel == 1 && document == null) {
            close();
            current = target;
            while (true) {
                discarded(current);
                if (current == container) {
                    break;
                }
                current = (OMContainerEx)((OMElement)current).getParent();
            }
            return;
        }
        int skipDepth = 0;
        loop: while (true) {
            switch (parserNext()) {
                case XMLStreamReader.START_ELEMENT:
                    skipDepth++;
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (skipDepth > 0) {
                        skipDepth--;
                    } else {
                        discarded(target);
                        boolean found = container == target;
                        target = (OMContainerEx)((OMElement)target).getParent();
                        elementLevel--;
                        if (found) {
                            break loop;
                        }
                    }
                    break;
                case XMLStreamReader.END_DOCUMENT:
                    if (skipDepth != 0 || elementLevel != 0) {
                        throw new OMException("Unexpected END_DOCUMENT");
                    }
                    if (target != document) {
                        throw new OMException("Called discard for an element that is not being built by this builder");
                    }
                    discarded(target);
                    target = null;
                    done = true;
                    break loop;
            }
        }
    }

    /**
     * Method getText.
     *
     * @return Returns String.
     * @throws OMException
     */
    public String getText() throws OMException {
        return parser.getText();
    }

    /**
     * Method getNamespace.
     *
     * @return Returns String.
     * @throws OMException
     */
    public String getNamespace() throws OMException {
        return parser.getNamespaceURI();
    }

    /**
     * Method setCache.
     *
     * @param b
     */
    public void setCache(boolean b) {
        if (parserAccessed && b) {
            throw new UnsupportedOperationException(
                    "parser accessed. cannot set cache");
        }
        cache = b;
    }
    
    /**
     * @return true if caching
     */
    public boolean isCache() {
        return cache;
    }

    /**
     * Method getName.
     *
     * @return Returns String.
     * @throws OMException
     */
    public String getName() throws OMException {
        return parser.getLocalName();
    }

    /**
     * Method getPrefix.
     *
     * @return Returns String.
     * @throws OMException
     */
    public String getPrefix() throws OMException {
        return parser.getPrefix();
    }

    /**
     * Get the underlying {@link XMLStreamReader} used by this builder. Note that for this type of
     * builder, accessing the underlying parser implies that can no longer be used, and any attempt
     * to call {@link #next()} will result in an exception.
     * 
     * @return The {@link XMLStreamReader} object used by this builder. Note that the constraints
     *         described in the Javadoc of the <code>reader</code> parameter of the
     *         {@link CustomBuilder#create(String, String, OMContainer, XMLStreamReader, OMFactory)}
     *         method also apply to the stream reader returned by this method, i.e.:
     *         <ul>
     *         <li>The caller should use
     *         {@link org.apache.axiom.util.stax.xop.XOPUtils#getXOPEncodedStream(XMLStreamReader)}
     *         to get an XOP encoded stream from the return value.
     *         <li>To get access to the bare StAX parser implementation, the caller should use
     *         {@link org.apache.axiom.util.stax.XMLStreamReaderUtils#getOriginalXMLStreamReader(XMLStreamReader)}.
     *         </ul>
     * @throws IllegalStateException
     *             if the parser has already been accessed
     */
    public Object getParser() {
        if (parserAccessed) {
            throw new IllegalStateException(
                    "Parser already accessed!");
        }
        if (!cache) {
            parserAccessed = true;
            // Mark all containers in the hierarchy as discarded because they can no longer be built
            OMContainerEx current = target;
            while (elementLevel > 0) {
                discarded(current);
                current = (OMContainerEx)((OMElement)current).getParent();
                elementLevel--;
            }
            if (current != null && current == document) {
                discarded(current);
            }
            target = null;
            return parser;
        } else {
            throw new IllegalStateException(
                    "cache must be switched off to access the parser");
        }
    }

    /**
     * For internal use only.
     */
    public XMLStreamReader disableCaching() {
        cache = false;
        // Always advance to the event right after the current node; this also takes
        // care of lookahead
        parserNext();
        if (log.isDebugEnabled()) {
            log.debug("Caching disabled; current element level is " + elementLevel);
        }
        return parser;
    }
    
    /**
     * For internal use only.
     */
    // This method expects that the parser is currently positioned on the
    // end event corresponding to the container passed as parameter
    public void reenableCaching(OMContainer container) {
        OMContainerEx current = target;
        while (true) {
            discarded(current);
            if (elementLevel == 0) {
                if (current != container || current != document) {
                    throw new IllegalStateException();
                }
                break;
            }
            elementLevel--;
            if (current == container) {
                break;
            }
            current = (OMContainerEx)((OMElement)current).getParent();
        }
        // Note that at this point current == container
        if (container == document) {
            target = null;
            done = true;
        } else if (elementLevel == 0 && document == null) {
            // Consume the remaining event; for the rationale, see StAXOMBuilder#next()
            while (parserNext() != XMLStreamConstants.END_DOCUMENT) {
                // Just loop
            }
            target = null;
            done = true;
        } else {
            target = (OMContainerEx)((OMElement)container).getParent();
        }
        if (log.isDebugEnabled()) {
            log.debug("Caching re-enabled; new element level: " + elementLevel + "; done=" + done);
        }
        if (done && autoClose) {
            close();
        }
        cache = true;
    }

    /**
     * Method isCompleted.
     *
     * @return Returns boolean.
     */
    public boolean isCompleted() {
        return done;
    }

    /**
     * This method is called with the XMLStreamConstants.START_ELEMENT event.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected abstract OMNode createOMElement() throws OMException;

    abstract int parserNext();
    
    /**
     * Forwards the parser one step further, if parser is not completed yet. If this is called after
     * parser is done, then throw an OMException. If the cache is set to false, then returns the
     * event, *without* building the OM tree. If the cache is set to true, then handles all the
     * events within this, and builds the object structure appropriately and returns the event.
     *
     * @return Returns int.
     * @throws OMException
     */
    public abstract int next() throws OMException;
    
    public CustomBuilder registerCustomBuilder(QName qName, int maxDepth, CustomBuilder customBuilder) {
        CustomBuilder old = null;
        if (customBuilders == null) {
            customBuilders = new HashMap<QName,CustomBuilder>();
        } else {
            old = customBuilders.get(qName);
        }
        maxDepthForCustomBuilders = 
                (maxDepthForCustomBuilders > maxDepth) ?
                        maxDepthForCustomBuilders: maxDepth;
        customBuilders.put(qName, customBuilder);
        return old;
    }
    
    
    public CustomBuilder registerCustomBuilderForPayload(CustomBuilder customBuilder) {
        CustomBuilder old = null;
        this.customBuilderForPayload = customBuilder;
        return old;
    }
    
    /**
     * Return CustomBuilder associated with the namespace/localPart
     * @param namespace
     * @param localPart
     * @return CustomBuilder or null
     */ 
    protected CustomBuilder getCustomBuilder(String namespace, String localPart) {
        if (customBuilders == null) {
            return null;
        }
        QName qName = new QName(namespace, localPart);
        return customBuilders.get(qName);
    }

    /** @return Returns short. */
    public short getBuilderType() {
        return OMConstants.PULL_TYPE_BUILDER;
    }

    /**
     * Method registerExternalContentHandler.
     *
     * @param obj
     */
    public void registerExternalContentHandler(Object obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * Method getRegisteredContentHandler.
     *
     * @return Returns Object.
     */
    public Object getRegisteredContentHandler() {
        throw new UnsupportedOperationException();
    }

    protected abstract OMDocument createDocument();
    
    protected void createDocumentIfNecessary() {
        if (document == null && parser.getEventType() == XMLStreamReader.START_DOCUMENT) {
            document = createDocument();
            if (charEncoding != null) {
                document.setCharsetEncoding(charEncoding);
            }
            document.setXMLVersion(parser.getVersion());
            document.setXMLEncoding(parser.getCharacterEncodingScheme());
            document.setStandalone(parser.isStandalone() ? "yes" : "no");
            target = (OMContainerEx)document;
        }
    }
    
    public OMDocument getDocument() {
        createDocumentIfNecessary();
        if (document == null) {
            throw new UnsupportedOperationException("There is no document linked to this builder");
        }
        return document;
    }

    public String getCharsetEncoding() {
        return document.getCharsetEncoding();
    }

    public void close() {
        try {
            if (!isClosed()) {
                parser.close();
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (Throwable e) {
            // Can't see a reason why we would want to surface an exception
            // while closing the parser.
            if (log.isDebugEnabled()) {
                log.debug("Exception occurred during parser close.  " +
                                "Processing continues. " + e);
            }
        } finally {
            _isClosed = true;
            done = true;
            // Release the parser so that it can be GC'd or reused. This is important because the
            // object model keeps a reference to the builder even after the builder is complete.
            parser = null;
        }
    }

    /**
     * Get the value of a feature/property from the underlying XMLStreamReader implementation
     * without accessing the XMLStreamReader. https://issues.apache.org/jira/browse/AXIOM-348
     *
     * @param name
     * @return TODO
     */
    public Object getReaderProperty(String name) throws IllegalArgumentException {
        if (!isClosed()) {
            return parser.getProperty(name);
        } 
        return null;
    }

    /**
     * Returns the encoding style of the XML data
     * @return the character encoding, defaults to "UTF-8"
     */
    public String getCharacterEncoding() {
        if(this.charEncoding == null){
            return "UTF-8";
        }
        return this.charEncoding;
    }
    
    /**
     * For internal use only.
     * 
     * @param autoClose
     */
    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }
    
    /**
     * @return if parser is closed
     */
    public boolean isClosed() {
        return _isClosed;
    }
    
    /**
     * @deprecated As of Axiom 1.2.15, the builder always releases the parser.
     */
    public void releaseParserOnClose(boolean value) {
    }

    public void detach() throws OMException {
        if (detachable != null) {
            detachable.detach();
        } else {
            while (!done) {
                next();
            }
        }
    }
}
