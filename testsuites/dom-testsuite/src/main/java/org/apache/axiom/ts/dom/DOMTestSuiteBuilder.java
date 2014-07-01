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
package org.apache.axiom.ts.dom;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.testutils.conformance.ConformanceTestFile;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.testutils.suite.XSLTImplementation;
import org.objectweb.asm.ClassReader;
import org.w3c.domts.DOMTestCase;
import org.w3c.domts.DOMTestDocumentBuilderFactory;
import org.w3c.domts.DOMTestIncompatibleException;
import org.w3c.domts.DOMTestSink;
import org.w3c.domts.DOMTestSuite;
import org.w3c.domts.DocumentBuilderSetting;

public final class DOMTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private static final QName[] validAttrQNames = new QName[] {
        new QName("urn:ns2", "attr", "q"),
        new QName("", "attr", ""),
        new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "ns", XMLConstants.XMLNS_ATTRIBUTE),
        new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE, ""),
    };
    
    private static final QName[] invalidAttrQNames = new QName[] {
        new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "attr", ""),
        new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "attr", "p"),
        new QName("urn:test", "p", XMLConstants.XMLNS_ATTRIBUTE),
        new QName("", XMLConstants.XMLNS_ATTRIBUTE, ""),
    };
    
    private final DocumentBuilderFactoryFactory dbff;
    private final Set<DOMFeature> unsupportedFeatures;
    
    public DOMTestSuiteBuilder(DocumentBuilderFactoryFactory dbff, DOMFeature... unsupportedFeatures) {
        this.dbff = dbff;
        this.unsupportedFeatures = new HashSet<DOMFeature>(Arrays.asList(unsupportedFeatures));
    }
    
    protected void addTests() {
        DocumentBuilderFactory dbf = dbff.newInstance();
        dbf.setNamespaceAware(true);
        ConformanceTestFile[] conformanceFiles = ConformanceTestFile.getConformanceTestFiles();
        addTest(new org.apache.axiom.ts.dom.attr.TestCloneNode(dbf, true));
        addTest(new org.apache.axiom.ts.dom.attr.TestCloneNode(dbf, false));
        addTest(new org.apache.axiom.ts.dom.attr.TestGetChildNodes(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestGetFirstChild(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestGetValueWithMultipleChildren(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestLookupNamespaceURI(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestLookupNamespaceURIWithoutOwnerElement(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestSetPrefixNotNullWithNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestSetPrefixNotNullWithoutNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestSetPrefixNullWithNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.builder.TestParseURI(dbf));
        addTest(new org.apache.axiom.ts.dom.builder.TestWhitespaceAroundDocumentElement(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestAdoptNode(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestAdoptNodeToSameDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestAdoptNodeWithParent(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestAllowedChildren(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestAppendChildWrongDocument(dbf));
        for (int i=0; i<conformanceFiles.length; i++) {
            addTest(new org.apache.axiom.ts.dom.document.TestCloneNode(dbf, conformanceFiles[i]));
        }
        addTest(new org.apache.axiom.ts.dom.document.TestCreateAttribute(dbf));
        for (int i=0; i<validAttrQNames.length; i++) {
            addTest(new org.apache.axiom.ts.dom.document.TestCreateAttributeNS(dbf, validAttrQNames[i]));
        }
        for (int i=0; i<invalidAttrQNames.length; i++) {
            addTest(new org.apache.axiom.ts.dom.document.TestCreateAttributeNSInvalid(dbf, invalidAttrQNames[i]));
        }
        addTest(new org.apache.axiom.ts.dom.document.TestCreateAttributeNSWithoutNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateCDATASection(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateElement(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateElementNS(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateElementNSWithInvalidName(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateElementNSWithoutNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateEntityReference(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateText(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestDocumentSiblings(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestGetDomConfigDefaults(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestGetOwnerDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestLookupNamespaceURI(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestLookupNamespaceURIWithEmptyDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestLookupPrefixWithEmptyDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestNormalizeDocumentNamespace(dbf));
        for (int i=0; i<XSLTImplementation.INSTANCES.length; i++) {
            XSLTImplementation xsltImplementation = XSLTImplementation.INSTANCES[i];
            addTest(new org.apache.axiom.ts.dom.document.TestTransformerWithIdentityStylesheet(dbf, xsltImplementation));
            addTest(new org.apache.axiom.ts.dom.document.TestTransformerWithStylesheet(dbf, xsltImplementation));
        }
        addTest(new org.apache.axiom.ts.dom.document.TestValidator(dbf));
        addTest(new org.apache.axiom.ts.dom.documentfragment.TestCloneNodeDeep(dbf));
        addTest(new org.apache.axiom.ts.dom.documentfragment.TestCloneNodeShallow(dbf));
        addTest(new org.apache.axiom.ts.dom.documentfragment.TestLookupNamespaceURI(dbf));
        addTest(new org.apache.axiom.ts.dom.documentfragment.TestLookupPrefix(dbf));
        addTest(new org.apache.axiom.ts.dom.documenttype.TestWithParser1(dbf));
        addTest(new org.apache.axiom.ts.dom.documenttype.TestWithParser2(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAppendChild(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAppendChildCyclic(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAppendChildSelf(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAppendChildWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAttributes(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAttributes2(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAttributes3(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAttributes4(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestCloneNode(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestCloneNodeWithAttributeHavingMultipleChildren(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestCloneNodeWithAttributes(dbf, true));
        addTest(new org.apache.axiom.ts.dom.element.TestCloneNodeWithAttributes(dbf, false));
        addTest(new org.apache.axiom.ts.dom.element.TestGetElementsByTagName(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetElementsByTagNameNS(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetElementsByTagNameRecursive(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetElementsByTagNameWithNamespaces(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetElementsByTagNameWithWildcard(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetNamespaceURIWithNoNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetPrefixWithDefaultNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetTextContent(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestInsertBefore(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestInsertBeforeWithDocumentFragment(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestLookupNamespaceURIExplicit(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestLookupNamespaceURIImplicit(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestLookupPrefixEmptyNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestLookupPrefixExplicitMasked(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestLookupPrefixImplicitMasked(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestRemoveAttributeNotOwner(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestRemoveFirstChild(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestRemoveLastChild(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestRemoveSingleChild(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildCyclic(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildFirst(dbf, false));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildFirst(dbf, true));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildFirstWithDocumentFragment(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildLast(dbf, false));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildLast(dbf, true));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildLastWithDocumentFragment(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildMiddle(dbf, false));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildMiddle(dbf, true));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildMiddleWithDocumentFragment(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildNotFound(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildNullNewChild(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildSingle(dbf, false));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildSingle(dbf, true));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNodeNSInUse(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNodeNSReplace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNodeNSWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNodeWrongDocument(dbf));
        for (int i=0; i<validAttrQNames.length; i++) {
            addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNS(dbf, validAttrQNames[i], "value"));
        }
        addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNSExisting(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNSExistingDefaultNamespaceDeclaration(dbf));
        for (int i=0; i<invalidAttrQNames.length; i++) {
            addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNSInvalid(dbf, invalidAttrQNames[i]));
        }
        addTest(new org.apache.axiom.ts.dom.element.TestSetPrefixNotNullWithNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetPrefixNotNullWithoutNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetPrefixNull(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetTextContent(dbf));
        addTest(new org.apache.axiom.ts.dom.element.attributes.TestSetNamedItemNSWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.element.attributes.TestSetNamedItemWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestAppendData(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestGetChildNodes(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestGetLength(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestGetWholeText(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestGetWholeTextWithCDATASection(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestGetWholeTextWithComment(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestLookupNamespaceURIWithNSDeclInScope(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestLookupNamespaceURIWithoutParent(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestSetPrefix(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestSplitText(dbf));
        
        DOMTestDocumentBuilderFactory factory;
        try {
            factory = new DOMTestDocumentBuilderFactoryImpl(dbff, new DocumentBuilderSetting[] {
                    DocumentBuilderSetting.notCoalescing,
                    DocumentBuilderSetting.notExpandEntityReferences,
                    DocumentBuilderSetting.notIgnoringElementContentWhitespace,
                    DocumentBuilderSetting.namespaceAware,
                    DocumentBuilderSetting.notValidating});
        } catch (DOMTestIncompatibleException ex) {
            // TODO
            throw new Error(ex);
        }
        try {
            addW3CTests(factory, new org.w3c.domts.level1.core.alltests(factory));
            addW3CTests(factory, new org.w3c.domts.level2.core.alltests(factory));
            addW3CTests(factory, new org.w3c.domts.level3.core.alltests(factory));
        } catch (Exception ex) {
            // TODO
            throw new Error(ex);
        }
    }
    
    private void addW3CTests(final DOMTestDocumentBuilderFactory factory, DOMTestSuite suite) {
        suite.build(new DOMTestSink() {
            public void addTest(Class testClass) {
                try {
                    if (!unsupportedFeatures.isEmpty()) {
                        Set<DOMFeature> usedFeatures = new HashSet<DOMFeature>();
                        DOMFeature.matchFeatures(testClass, usedFeatures);
                        ClassReader classReader = new ClassReader(testClass.getResourceAsStream(testClass.getSimpleName() + ".class"));
                        DOMTSClassVisitor cv = new DOMTSClassVisitor(usedFeatures);
                        classReader.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                        usedFeatures.retainAll(unsupportedFeatures);
                        if (!usedFeatures.isEmpty()) {
                            return;
                        }
                    }
                    Constructor<? extends DOMTestCase> testConstructor = ((Class<?>)testClass).asSubclass(DOMTestCase.class).getConstructor(DOMTestDocumentBuilderFactory.class);
                    DOMTestCase test;
                    try {
                        test = testConstructor.newInstance(new Object[] { factory });
                    } catch (InvocationTargetException ex) {
                        throw ex.getTargetException();
                    }
                    test.setFramework(JUnitTestFramework.INSTANCE);
                    DOMTestSuiteBuilder.this.addTest(new W3CTestCase(test));
                }
                catch (Throwable ex) {
                    // TODO
                    throw new Error(ex);
                }
            }
        });
    }
}