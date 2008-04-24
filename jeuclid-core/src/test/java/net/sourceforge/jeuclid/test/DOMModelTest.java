/*
 * Copyright 2007 - 2008 JEuclid, http://jeuclid.sf.net
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package net.sourceforge.jeuclid.test;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jeuclid.DOMBuilder;
import net.sourceforge.jeuclid.MathMLParserSupport;
import net.sourceforge.jeuclid.MathMLSerializer;
import net.sourceforge.jeuclid.elements.JEuclidElementFactory;
import net.sourceforge.jeuclid.elements.generic.DocumentElement;
import net.sourceforge.jeuclid.elements.presentation.general.Mfrac;
import net.sourceforge.jeuclid.elements.presentation.general.Mrow;
import net.sourceforge.jeuclid.elements.presentation.script.Msub;
import net.sourceforge.jeuclid.elements.presentation.script.Msubsup;
import net.sourceforge.jeuclid.elements.presentation.script.Msup;
import net.sourceforge.jeuclid.elements.presentation.token.Mi;
import net.sourceforge.jeuclid.elements.presentation.token.Mo;
import net.sourceforge.jeuclid.elements.support.attributes.AbstractAttributeMap;
import net.sourceforge.jeuclid.elements.support.attributes.AttributeMap;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.mathml.MathMLActionElement;
import org.w3c.dom.mathml.MathMLAlignGroupElement;
import org.w3c.dom.mathml.MathMLAlignMarkElement;
import org.w3c.dom.mathml.MathMLAnnotationElement;
import org.w3c.dom.mathml.MathMLDocument;
import org.w3c.dom.mathml.MathMLElement;
import org.w3c.dom.mathml.MathMLEncloseElement;
import org.w3c.dom.mathml.MathMLFencedElement;
import org.w3c.dom.mathml.MathMLFractionElement;
import org.w3c.dom.mathml.MathMLGlyphElement;
import org.w3c.dom.mathml.MathMLLabeledRowElement;
import org.w3c.dom.mathml.MathMLMathElement;
import org.w3c.dom.mathml.MathMLMultiScriptsElement;
import org.w3c.dom.mathml.MathMLOperatorElement;
import org.w3c.dom.mathml.MathMLPaddedElement;
import org.w3c.dom.mathml.MathMLPresentationContainer;
import org.w3c.dom.mathml.MathMLPresentationToken;
import org.w3c.dom.mathml.MathMLRadicalElement;
import org.w3c.dom.mathml.MathMLScriptElement;
import org.w3c.dom.mathml.MathMLSemanticsElement;
import org.w3c.dom.mathml.MathMLSpaceElement;
import org.w3c.dom.mathml.MathMLStringLitElement;
import org.w3c.dom.mathml.MathMLStyleElement;
import org.w3c.dom.mathml.MathMLTableCellElement;
import org.w3c.dom.mathml.MathMLTableElement;
import org.w3c.dom.mathml.MathMLTableRowElement;
import org.w3c.dom.mathml.MathMLUnderOverElement;

/**
 * Various tests for the DOM model.
 * 
 * @version $Revision$
 */
public class DOMModelTest {

    final static AttributeMap aMap = new AbstractAttributeMap() {

        @Override
        protected String getAttribute(String attrName) {
            return null;
        }

        @Override
        protected String getAttributeNS(String namespace, String attrName) {
            return this.getAttribute(attrName);
        }

        public Map<String, String> getAsMap() {
            return new HashMap<String, String>();
        }
    };

    /**
     * Tests is the "id" attribute works.
     * 
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testID() throws Exception {
        final Document docWithID = MathMLParserSupport
                .parseString("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><math mode=\"display\">"
                        + "<mrow id='abc'><mn>1</mn></mrow></math>");

        final MathMLDocument docElement = DOMBuilder.getDOMBuilder()
                .createJeuclidDom(docWithID);

        final MathMLMathElement mathElement = (MathMLMathElement) docElement
                .getFirstChild();

        // // TODO: enable this test
        // Assert.assertEquals(mathElement.getDisplay(), "block");
        final MathMLPresentationContainer row = (MathMLPresentationContainer) mathElement
                .getFirstChild();
        Assert.assertNotNull(row);
        Assert.assertEquals(row.getId(), "abc");
    }

    /**
     * Tests if serialization works.
     * 
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testSerialization() throws Exception {
        final Document origDoc = MathMLParserSupport
                .parseString("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><math mode=\"display\">"
                        + "<mrow id='abc'><mn>1</mn></mrow></math>");
        final MathMLDocument mathMLDoc = DOMBuilder.getDOMBuilder()
                .createJeuclidDom(origDoc);
        final String reserialStr = MathMLSerializer.serializeDocument(
                mathMLDoc, false, false);

        final Document reserial = MathMLParserSupport
                .parseString(reserialStr);
        Assert.assertTrue(reserial.isEqualNode(origDoc));
    }

    /**
     * Tests if serialization with malignmark works.
     * 
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testSerialization2() throws Exception {
        final Document origDoc = MathMLParserSupport
                .parseString("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><math mode=\"display\">"
                        + "<mtext>Alignment<malignmark/>Test</mtext></math>");
        final MathMLDocument mathMLDoc = DOMBuilder.getDOMBuilder()
                .createJeuclidDom(origDoc);
        final String reserialStr = MathMLSerializer.serializeDocument(
                mathMLDoc, false, false);

        final Document reserial = MathMLParserSupport
                .parseString(reserialStr);
        Assert.assertTrue(reserial.isEqualNode(origDoc));
    }

    /**
     * Tests is all attributes on mathOperator work.
     * 
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testMOAttrs() throws Exception {
        final Document doc = MathMLParserSupport
                .parseString("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><math mode=\"display\">"
                        + "<mo stretchy='true'>X</mo>"
                        + "<mo stretchy='false'>Y</mo>"
                        + "<mo>&#x0007d;</mo>"
                        + "<mo>&#x02254;</mo>"
                        + "<mo>&#x0201d;</mo>" + "</math>");
        final MathMLDocument docElement = DOMBuilder.getDOMBuilder()
                .createJeuclidDom(doc);

        final MathMLMathElement mathElement = (MathMLMathElement) docElement
                .getFirstChild();

        final MathMLOperatorElement mo = (MathMLOperatorElement) mathElement
                .getChildNodes().item(0);
        Assert.assertNotNull(mo);
        Assert.assertTrue(Boolean.parseBoolean(mo.getStretchy()));
        final Mo mo2 = (Mo) mathElement.getChildNodes().item(1);
        Assert.assertNotNull(mo2);
        Assert.assertFalse(Boolean.parseBoolean(mo2.getStretchy()));
        final Mo mo3 = (Mo) mathElement.getChildNodes().item(2);
        // Should be strechty, since it is fence
        Assert.assertTrue(Boolean.parseBoolean(mo3.getStretchy()));
        final Mo mo4 = (Mo) mathElement.getChildNodes().item(3);
        Assert.assertFalse(Boolean.parseBoolean(mo4.getStretchy()));
        final Mo mo5 = (Mo) mathElement.getChildNodes().item(2);
        Assert.assertTrue(Boolean.parseBoolean(mo5.getStretchy()));
    }

    /**
     * Tests of objects created from MathElementFactory implement the proper
     * interfaces from W3C Dom.
     * 
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testInterfaces() throws Exception {

        // This mapping is taken straight from Table D.2.2, MathML 2.0 spec
        // TODO: Someday none of these should be commented out.

        // TODO: Use DOM instead;
        final Document ownerDocument = new DocumentElement();

        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("math",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLMathElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mi",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLPresentationToken);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mn",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLPresentationToken);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mo",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLOperatorElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mtext",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLPresentationToken);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mspace",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLSpaceElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("ms",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLStringLitElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mglyph",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLGlyphElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mrow",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLPresentationContainer);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mfrac",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLFractionElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("msqrt",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLRadicalElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mroot",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLRadicalElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mstyle",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLStyleElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("merror",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLPresentationContainer);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mpadded",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLPaddedElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mphantom",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLPresentationContainer);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mfenced",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLFencedElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("menclose",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLEncloseElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("msub",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLScriptElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("msup",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLScriptElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("msubsup",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLScriptElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("munder",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLUnderOverElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mover",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLUnderOverElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName(
                        "munderover", DOMModelTest.aMap, ownerDocument) instanceof MathMLUnderOverElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName(
                        "mmultiscripts", DOMModelTest.aMap, ownerDocument) instanceof MathMLMultiScriptsElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mtable",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLTableElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName(
                        "mlabeledtr", DOMModelTest.aMap, ownerDocument) instanceof MathMLLabeledRowElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mtr",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLTableRowElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("mtd",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLTableCellElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName(
                        "maligngroup", DOMModelTest.aMap, ownerDocument) instanceof MathMLAlignGroupElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName(
                        "malignmark", DOMModelTest.aMap, ownerDocument) instanceof MathMLAlignMarkElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName("maction",
                        DOMModelTest.aMap, ownerDocument) instanceof MathMLActionElement);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName("cn", aMap,
        // base) instanceof MathMLCnElement);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName("ci", aMap,
        // base) instanceof MathMLCiElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("csymbol",
        // aMap,
        // base) instanceof MathMLCsymbolElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("apply", aMap,
        // base) instanceof MathMLApplyElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("reln", aMap,
        // base) instanceof MathMLContentContainer);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName("fn", aMap,
        // base) instanceof MathMLFnElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("interval",
        // aMap) instanceof MathMLIntervalElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("inverse",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("condition",
        // aMap) instanceof MathMLConditionElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("declare",
        // aMap,
        // base) instanceof MathMLDeclareElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("lambda",
        // aMap,
        // base) instanceof MathMLLambdaElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("compose",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("ident", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("domain",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("codomain",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("image", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName(
        // "domainofapplication", aMap) instanceof
        // MathMLContentContainer);
        // Assert.assertTrue(MathElementFactory.elementFromName("piecewise",
        // aMap) instanceof MathMLPiecewiseElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("piece", aMap,
        // base) instanceof MathMLCaseElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("otherwise",
        // aMap) instanceof MathMLContentContainer);
        // Assert.assertTrue(MathElementFactory.elementFromName("quotient",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("exp", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("factorial",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("divide",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("max", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("min", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("minus", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("plus", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("power", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("rem", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("times", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("root", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("gcd", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("and", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName("or", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("xor", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("not", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("implies",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("forall",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("exists",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("abs", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("conjugate",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arg", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("real", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("imaginary",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("lcm", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("floor", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("ceiling",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName("eq", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("neq", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName("gt", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName("lt", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("geq", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("leq", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("equivalent",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("approx",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("factorof",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("int", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("diff", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("partialdiff",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("lowlimit",
        // aMap) instanceof MathMLContentContainer);
        // Assert.assertTrue(MathElementFactory.elementFromName("uplimit",
        // aMap,
        // base) instanceof MathMLContentContainer);
        // Assert.assertTrue(MathElementFactory.elementFromName("bvar", aMap,
        // base) instanceof MathMLBvarElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("degree",
        // aMap,
        // base) instanceof MathMLContentContainer);
        // Assert.assertTrue(MathElementFactory.elementFromName("divergence",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("grad", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("curl", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("laplacian",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("set", aMap,
        // base) instanceof MathMLSetElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("list", aMap,
        // base) instanceof MathMLListElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("union", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("intersect",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName("in", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("notin", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("subset",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("prsubset",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("notsubset",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("notprsubset",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("setdiff",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("card", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName(
        // "cartesianproduct", aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("sum", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("product",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("limit", aMap,
        // base) instanceof MathMLPredefinedSymbol);

        // This interface does not exist.
        // Assert.assertTrue(MathElementFactory.elementFromName("tendsto",
        // aMap,
        // base) instanceof MathMLTendsToElement);

        // Assert.assertTrue(MathElementFactory.elementFromName("exp", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName("ln", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("log", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("sin", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("cos", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("tan", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("sec", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("csc", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("cot", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("sinh", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("cosh", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("tanh", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("sech", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("csch", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("coth", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arcsin",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arccos",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arctan",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arccosh",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arccot",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arccoth",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arccsc",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arccsch",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arcsec",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arcsech",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arcsinh",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("arctanh",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("mean", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("sdev", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("variance",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("median",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("mode", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("moment",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("momentabout",
        // aMap) instanceof MathMLContentContainer);
        // Assert.assertTrue(MathElementFactory.elementFromName("vector",
        // aMap,
        // base) instanceof MathMLVectorElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("matrix",
        // aMap,
        // base) instanceof MathMLMatrixElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("matrixrow",
        // aMap) instanceof MathMLMatrixrowElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("determinant",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("transpose",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("selector",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("vectorproduct",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("scalarproduct",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("outerproduct",
        // aMap) instanceof MathMLPredefinedSymbol);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName(
                        "annotation", DOMModelTest.aMap, ownerDocument) instanceof MathMLAnnotationElement);
        Assert
                .assertTrue(JEuclidElementFactory.elementFromName(
                        "semantics", DOMModelTest.aMap, ownerDocument) instanceof MathMLSemanticsElement);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName(
        // "annotation-xml", aMap) instanceof
        // MathMLXMLAnnotationElement);
        // Assert.assertTrue(MathElementFactory.elementFromName("integers",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("reals", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("rationals",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName(
        // "naturalnumbers", aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("complexes",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("primes",
        // aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("exponentiale",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("imaginaryi",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("notanumber",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("true", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("false", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("emptyset",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert
        // .assertTrue(MathElementFactory.elementFromName("pi", aMap,
        // base) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("eulergamma",
        // aMap) instanceof MathMLPredefinedSymbol);
        // Assert.assertTrue(MathElementFactory.elementFromName("infinity",
        // aMap) instanceof MathMLPredefinedSymbol);

    }

    @Test
    public void testFrac() throws Exception {
        final Document d = new DocumentElement();
        final MathMLFractionElement mfrac = (MathMLFractionElement) d
                .createElement(Mfrac.ELEMENT);
        final Mi mi = (Mi) d.createElement(Mi.ELEMENT);
        final Mrow mrow = (Mrow) d.createElement(Mrow.ELEMENT);
        final Mi mi2 = (Mi) d.createElement(Mi.ELEMENT);
        mfrac.setDenominator(mi);
        mfrac.setNumerator(mrow);
        Assert.assertEquals(mi, mfrac.getDenominator());
        Assert.assertEquals(mrow, mfrac.getNumerator());
        Assert.assertEquals(mfrac.getChildNodes().getLength(), 2);
        mfrac.setNumerator(mi2);
        Assert.assertEquals(mi, mfrac.getDenominator());
        Assert.assertEquals(mi2, mfrac.getNumerator());
        Assert.assertEquals(mfrac.getChildNodes().getLength(), 2);
    }

    @Test
    public void testMMultiScripts() throws Exception {
        final Document doc = MathMLParserSupport
                .parseString("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><math mode=\"display\">"
                        + "<mmultiscripts>"
                        + "<mo>x</mo>"
                        + "<mi>a</mi>"
                        + "<mi>b</mi>"
                        + "<mprescripts/>"
                        + "<mi>c</mi>"
                        + "<mi>d</mi>" + "</mmultiscripts>" + "</math>");
        final MathMLDocument docElement = DOMBuilder.getDOMBuilder()
                .createJeuclidDom(doc);
        final MathMLMathElement mathElement = (MathMLMathElement) docElement
                .getFirstChild();

        final MathMLMultiScriptsElement multi = (MathMLMultiScriptsElement) mathElement
                .getChildNodes().item(0);

        Assert.assertEquals(multi.getBase().getTextContent(), "x");
        Assert.assertEquals(multi.getSubScript(1).getTextContent(), "a");
        Assert.assertEquals(multi.getSuperScript(1).getTextContent(), "b");
        Assert.assertEquals(multi.getPreSubScript(1).getTextContent(), "c");
        Assert.assertEquals(multi.getPreSuperScript(1).getTextContent(), "d");
        Assert.assertEquals(multi.getNumprescriptcolumns(), 1);
        Assert.assertEquals(multi.getNumscriptcolumns(), 1);
        final Mi mi = (Mi) docElement.createElement(Mi.ELEMENT);
        multi.insertPreSubScriptBefore(0, mi);
        Assert.assertEquals(multi.getNumprescriptcolumns(), 2);
        Assert.assertEquals(multi.getChildNodes().getLength(), 8);
    }

    @Test
    public void testMMultiScripts2() throws Exception {
        final Document doc = MathMLParserSupport
                .parseString("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><math mode=\"display\">"
                        + "<mmultiscripts>" + "</mmultiscripts>" + "</math>");
        final MathMLDocument docElement = DOMBuilder.getDOMBuilder()
                .createJeuclidDom(doc);
        final MathMLMathElement mathElement = (MathMLMathElement) docElement
                .getFirstChild();
        final MathMLMultiScriptsElement multi = (MathMLMultiScriptsElement) mathElement
                .getChildNodes().item(0);
        multi.setSubScriptAt(1, (MathMLElement) docElement
                .createElement(Mi.ELEMENT));
        Assert.assertEquals(multi.getChildNodes().getLength(), 3);
        multi.setSuperScriptAt(1, (MathMLElement) docElement
                .createElement(Mi.ELEMENT));
        Assert.assertEquals(multi.getChildNodes().getLength(), 3);
        multi.insertPreSuperScriptBefore(0, (MathMLElement) docElement
                .createElement(Mi.ELEMENT));
        Assert.assertEquals(multi.getChildNodes().getLength(), 6);
        multi.insertPreSubScriptBefore(0, (Mi) docElement
                .createElement(Mi.ELEMENT));
        Assert.assertEquals(multi.getChildNodes().getLength(), 8);

        Assert.assertNull(multi.getSubScript(0));
        Assert.assertNull(multi.getSuperScript(0));
        Assert.assertNull(multi.getSubScript(20));
        Assert.assertNull(multi.getSuperScript(20));
    }

    private int docCount;

    private int mathCount;

    private int miCount;

    @Test
    public void testEvents() throws Exception {
        final Document doc = MathMLParserSupport
                .parseString("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><math mode=\"display\">"
                        + "<mi>x</mi>" + "</math>");
        final MathMLDocument docElement = DOMBuilder.getDOMBuilder()
                .createJeuclidDom(doc);
        final MathMLMathElement mathElement = (MathMLMathElement) docElement
                .getFirstChild();

        final MathMLElement mi = (MathMLElement) mathElement.getChildNodes()
                .item(0);

        this.docCount = 0;
        this.mathCount = 0;
        this.miCount = 0;

        ((EventTarget) mathElement).addEventListener("DOMSubtreeModified",
                new EventListener() {

                    public void handleEvent(final Event evt) {
                        DOMModelTest.this.mathCount++;
                    }
                }, false);

        ((EventTarget) mi).addEventListener("DOMSubtreeModified",
                new EventListener() {

                    public void handleEvent(final Event evt) {
                        DOMModelTest.this.miCount++;

                    }
                }, false);
        ((EventTarget) docElement).addEventListener("DOMSubtreeModified",
                new EventListener() {

                    public void handleEvent(final Event evt) {
                        DOMModelTest.this.docCount++;
                    }
                }, false);
        mathElement.appendChild(docElement.createElement(Mi.ELEMENT));
        Assert
                .assertTrue(this.miCount == 0,
                        "Event must not be called on Mi");
        Assert.assertTrue(this.mathCount > 0, "Event must be called on Math");
        Assert.assertTrue(this.docCount > 0,
                "Event must be called on Document");
    }

    @Test
    public void testBadSupSuper() throws Exception {
        final Document ownerDocument = new DocumentElement();

        final MathMLScriptElement msup = (MathMLScriptElement) ownerDocument
                .createElement(Msup.ELEMENT);
        final MathMLScriptElement msubsup = (MathMLScriptElement) ownerDocument
                .createElement(Msubsup.ELEMENT);
        final MathMLScriptElement msub = (MathMLScriptElement) ownerDocument
                .createElement(Msub.ELEMENT);
        msup.getSuperscript();
        msup.getSubscript();
        msub.getSuperscript();
        msub.getSubscript();
        msubsup.getSuperscript();
        msubsup.getSubscript();
    }

}
