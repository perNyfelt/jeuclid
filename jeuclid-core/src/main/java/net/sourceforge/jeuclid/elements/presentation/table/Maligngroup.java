/*
 * Copyright 2002 - 2007 JEuclid, http://jeuclid.sf.net
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

package net.sourceforge.jeuclid.elements.presentation.table;

import net.sourceforge.jeuclid.elements.AbstractInvisibleJEuclidElement;

import org.w3c.dom.mathml.MathMLAlignGroupElement;

/**
 * This class represents the maligngroup tag.
 * 
 * @version $Revision$
 */

public class Maligngroup extends AbstractInvisibleJEuclidElement implements
        MathMLAlignGroupElement {

    /**
     * The XML element from this class.
     */
    public static final String ELEMENT = "maligngroup";

    /** The groupalign attribute. */
    public static final String ATTR_GROUPALIGN = "groupalign";

    /** The width is calculated within MathTable. */
    protected float width;

    private Malignmark mathAlignMark;

    /**
     * Creates a math element.
     */
    public Maligngroup() {
        super();
    }

    /**
     * Return the current width of this element. Initially it's zero, but
     * MathTable after calculating will give it the right value.
     * 
     * @return Width of this element
     */

    public float getWidth() {
        return this.width;
    }

    // /** {@inheritDoc} */
    // @Override
    // public float calculateWidth(final Graphics2D g) {
    // return this.width;
    // }

    /**
     * @param mark
     *            MathAlignMark
     */
    protected void setMark(final Malignmark mark) {
        if (this.mathAlignMark == null) {
            this.mathAlignMark = mark;
        }
    }

    /**
     * @return mark
     */
    protected Malignmark getMark() {
        return this.mathAlignMark;
    }

    // /**
    // * @param elements
    // * Listof elements
    // * @return width of all elements
    // * @param g
    // * Graphics2D context to use.
    // */
    // protected static float getElementsWholeWidth(final Graphics2D g,
    // final List<JEuclidElement> elements) {
    // float result = 0;
    //
    // if (elements == null || elements.size() == 0) {
    // return result;
    // }
    //
    // for (int i = 0; i < elements.size(); i++) {
    // if (elements.get(i) != null) {
    // result += (elements.get(i)).getWidth(g);
    // }
    // }
    //
    // return result;
    // }

    // /**
    // * @param alignGroupElement
    // * maligngroup element
    // * @return list of elements of the maligngroup
    // */
    // protected static List<JEuclidElement> getElementsOfAlignGroup(
    // final JEuclidElement alignGroupElement) {
    // final List<JEuclidElement> result = new Vector<JEuclidElement>();
    //
    // JEuclidElement parent = alignGroupElement.getParent();
    // int index = parent.getIndexOfMathElement(alignGroupElement) + 1;
    // JEuclidElement current = parent.getMathElement(index);
    // final boolean searching = true;
    //
    // while (searching) {
    // if (parent.getMathElementCount() == index) {
    // // end of parent
    // if (parent instanceof Mtr || parent instanceof MathImpl) {
    // // parent is tablerow or root, exit
    // break;
    // }
    // index = parent.getParent().getIndexOfMathElement(parent) + 1;
    // parent = parent.getParent();
    // current = parent.getMathElement(index);
    // // going out from mrow or something...
    // continue;
    // } else {
    // // parent elements didn't over
    // if (current instanceof Mrow) {
    // // go inside mrow
    // parent = current;
    // current = parent.getMathElement(0);
    // index = 0;
    // continue;
    // } else {
    // if (current instanceof Maligngroup) {
    // // we've found next aligngroup element, stop search
    // break;
    // } else {
    // // adding element, continue loop
    // result.add(current);
    // }
    // // next element
    // current = parent.getMathElement(index + 1);
    // index++;
    // }
    // }
    // }
    //
    // return result;
    // }

    /** {@inheritDoc} */
    public String getTagName() {
        return Maligngroup.ELEMENT;
    }

    /** {@inheritDoc} */
    public String getGroupalign() {
        return this.getMathAttribute(Maligngroup.ATTR_GROUPALIGN);
    }

    /** {@inheritDoc} */
    public void setGroupalign(final String groupalign) {
        this.setAttribute(Maligngroup.ATTR_GROUPALIGN, groupalign);
    }
}
