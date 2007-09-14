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

package net.sourceforge.jeuclid.elements.presentation.script;

import java.awt.geom.Dimension2D;
import java.util.List;
import java.util.Vector;

import net.sourceforge.jeuclid.LayoutContext;
import net.sourceforge.jeuclid.elements.JEuclidElement;
import net.sourceforge.jeuclid.elements.support.Dimension2DImpl;
import net.sourceforge.jeuclid.elements.support.ElementListSupport;
import net.sourceforge.jeuclid.elements.support.MathMLNodeListImpl;
import net.sourceforge.jeuclid.layout.LayoutInfo;
import net.sourceforge.jeuclid.layout.LayoutStage;
import net.sourceforge.jeuclid.layout.LayoutView;

import org.w3c.dom.Node;
import org.w3c.dom.mathml.MathMLElement;
import org.w3c.dom.mathml.MathMLMultiScriptsElement;
import org.w3c.dom.mathml.MathMLNodeList;

/**
 * Prescripts and Tensor Indices.
 * 
 * @version $Revision$
 */

public class Mmultiscripts extends AbstractScriptElement implements
        MathMLMultiScriptsElement {
    /**
     * The XML element from this class.
     */
    public static final String ELEMENT = "mmultiscripts";

    // /**
    // * Logger for this class
    // */
    // currently unused
    // private static final Log LOGGER = LogFactory
    // .getLog(MathMultiScripts.class);
    private static final int STATE_POSTSUB = 0;

    private static final int STATE_POSTSUPER = 1;

    private static final int STATE_PRESUB = 2;

    private static final int STATE_PRESUPER = 3;

    private final List<JEuclidElement> postsubscripts = new Vector<JEuclidElement>();

    private final List<JEuclidElement> postsuperscripts = new Vector<JEuclidElement>();

    private final List<JEuclidElement> presubscripts = new Vector<JEuclidElement>();

    private final List<JEuclidElement> presuperscripts = new Vector<JEuclidElement>();

    private boolean inRewriteChildren;

    /**
     * Default constructor.
     */
    public Mmultiscripts() {
        super();
        this.inRewriteChildren = false;
    }

    /** {@inheritDoc} */
    @Override
    protected void changeHook() {
        super.changeHook();
        if (!this.inRewriteChildren) {
            this.parseChildren();
        }
    }

    private void parseChildren() {
        this.presubscripts.clear();
        this.presuperscripts.clear();
        this.postsubscripts.clear();
        this.postsuperscripts.clear();
        final org.w3c.dom.NodeList childList = this.getChildNodes();
        int state = Mmultiscripts.STATE_POSTSUB;
        final int len = childList.getLength();
        for (int i = 1; i < len; i++) {
            final Node child = childList.item(i);
            if (child instanceof Mprescripts) {
                state = Mmultiscripts.STATE_PRESUB;
            } else if (child instanceof JEuclidElement) {
                final JEuclidElement jchild = (JEuclidElement) child;
                if (state == Mmultiscripts.STATE_POSTSUB) {
                    this.postsubscripts.add(jchild);
                    state = Mmultiscripts.STATE_POSTSUPER;
                } else if (state == Mmultiscripts.STATE_POSTSUPER) {
                    this.postsuperscripts.add(jchild);
                    state = Mmultiscripts.STATE_POSTSUB;
                } else if (state == Mmultiscripts.STATE_PRESUB) {
                    this.presubscripts.add(jchild);
                    state = Mmultiscripts.STATE_PRESUPER;
                } else {
                    this.presuperscripts.add(jchild);
                    state = Mmultiscripts.STATE_PRESUB;
                }
            }
        }
        if (this.postsuperscripts.size() < this.postsubscripts.size()) {
            this.postsuperscripts.add(new None());
        }
        if (this.presuperscripts.size() < this.presubscripts.size()) {
            this.presuperscripts.add(new None());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void layoutStageInvariant(final LayoutView view,
            final LayoutInfo info, final LayoutStage stage,
            final LayoutContext context) {
        final JEuclidElement base = this.getBase();
        final LayoutInfo baseInfo = view.getInfo(base);
        final LayoutContext now = this.applyLocalAttributesToContext(context);

        final String subScriptshift = this.getSubscriptshift();
        final String superScriptshift = this.getSuperscriptshift();

        final ScriptSupport.ShiftInfo totalShiftInfo = this
                .calculateTotalShift(view, stage, baseInfo, now,
                        subScriptshift, superScriptshift);
        float posX = 0.0f;
        final float subBaselineShift = totalShiftInfo.getSubShift();
        final float superBaselineShift = totalShiftInfo.getSuperShift();
        for (int i = 0; i < this.presubscripts.size(); i++) {
            final LayoutInfo subInfo = view
                    .getInfo(this.presubscripts.get(i));
            final LayoutInfo superInfo = view.getInfo(this.presuperscripts
                    .get(i));
            subInfo.moveTo(posX, subBaselineShift, stage);
            superInfo.moveTo(posX, -superBaselineShift, stage);
            posX += Math.max(subInfo.getWidth(stage), superInfo
                    .getWidth(stage));
        }
        baseInfo.moveTo(posX, 0.0f, stage);
        posX += baseInfo.getWidth(stage);
        for (int i = 0; i < this.postsubscripts.size(); i++) {
            final LayoutInfo subInfo = view.getInfo(this.postsubscripts
                    .get(i));
            final LayoutInfo superInfo = view.getInfo(this.postsuperscripts
                    .get(i));
            subInfo.moveTo(posX, subBaselineShift, stage);
            superInfo.moveTo(posX, -superBaselineShift, stage);
            posX += Math.max(subInfo.getWidth(stage), superInfo
                    .getWidth(stage));
        }

        final Dimension2D noborder = new Dimension2DImpl(0.0f, 0.0f);
        ElementListSupport.fillInfoFromChildren(view, info, this, stage,
                noborder, noborder);
    }

    private ScriptSupport.ShiftInfo calculateTotalShift(
            final LayoutView view, final LayoutStage stage,
            final LayoutInfo baseInfo, final LayoutContext now,
            final String subScriptshift, final String superScriptshift) {
        final ScriptSupport.ShiftInfo totalShiftInfo = new ScriptSupport.ShiftInfo(
                0.0f, 0.0f);

        for (int i = 0; i < this.presubscripts.size(); i++) {
            final LayoutInfo subInfo = view
                    .getInfo(this.presubscripts.get(i));
            final LayoutInfo superInfo = view.getInfo(this.presuperscripts
                    .get(i));
            final ScriptSupport.ShiftInfo shiftInfo = ScriptSupport
                    .calculateScriptShfits(stage, now, subScriptshift,
                            superScriptshift, baseInfo, subInfo, superInfo);
            totalShiftInfo.max(shiftInfo);
        }
        for (int i = 0; i < this.postsubscripts.size(); i++) {
            final LayoutInfo subInfo = view.getInfo(this.postsubscripts
                    .get(i));
            final LayoutInfo superInfo = view.getInfo(this.postsuperscripts
                    .get(i));
            final ScriptSupport.ShiftInfo shiftInfo = ScriptSupport
                    .calculateScriptShfits(stage, now, subScriptshift,
                            superScriptshift, baseInfo, subInfo, superInfo);
            totalShiftInfo.max(shiftInfo);
        }
        return totalShiftInfo;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildPrescripts(final JEuclidElement child) {
        return child.isSameNode(this.getBase())
                && this.getNumprescriptcolumns() > 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildPostscripts(final JEuclidElement child) {
        return child.isSameNode(this.getBase())
                && this.getNumscriptcolumns() > 0;
    }

    /** {@inheritDoc} */
    public String getTagName() {
        return Mmultiscripts.ELEMENT;
    }

    /** {@inheritDoc} */
    public JEuclidElement getBase() {
        final JEuclidElement base = this.getMathElement(0);
        if (base == null) {
            return new None();
        } else {
            return base;
        }
    }

    /** {@inheritDoc} */
    public void setBase(final MathMLElement base) {
        this.setMathElement(0, base);
    }

    /** {@inheritDoc} */
    public int getNumprescriptcolumns() {
        return this.presubscripts.size();
    }

    /** {@inheritDoc} */
    public int getNumscriptcolumns() {
        return this.postsubscripts.size();
    }

    /** {@inheritDoc} */
    public MathMLElement getPreSubScript(final int colIndex) {
        return this.presubscripts.get(colIndex - 1);
    }

    /** {@inheritDoc} */
    public MathMLElement getPreSuperScript(final int colIndex) {
        return this.presuperscripts.get(colIndex - 1);
    }

    /** {@inheritDoc} */
    public MathMLNodeList getPrescripts() {
        final List<Node> list = new Vector<Node>();
        for (int i = 0; i < this.presubscripts.size(); i++) {
            list.add(this.presubscripts.get(i));
            list.add(this.presuperscripts.get(i));
        }
        return new MathMLNodeListImpl(list);
    }

    /** {@inheritDoc} */
    public MathMLNodeList getScripts() {
        final List<Node> list = new Vector<Node>();
        for (int i = 0; i < this.postsubscripts.size(); i++) {
            list.add(this.postsubscripts.get(i));
            list.add(this.postsuperscripts.get(i));
        }
        return new MathMLNodeListImpl(list);
    }

    /** {@inheritDoc} */
    public MathMLElement getSubScript(final int colIndex) {
        return this.postsubscripts.get(colIndex - 1);
    }

    /** {@inheritDoc} */
    public MathMLElement getSuperScript(final int colIndex) {
        return this.postsuperscripts.get(colIndex - 1);
    }

    private void rewriteChildren() {
        this.inRewriteChildren = true;

        final org.w3c.dom.NodeList childList = this.getChildNodes();
        final int len = childList.getLength();
        // start at 1 since 0 is the base!
        for (int i = 1; i < len; i++) {
            this.removeChild(childList.item(1));
        }
        if (len == 0) {
            this.addMathElement(new None());
        }
        for (int i = 0; i < this.postsubscripts.size(); i++) {
            this.addMathElement(this.postsubscripts.get(i));
            this.addMathElement(this.postsuperscripts.get(i));
        }
        final int numprescripts = this.presubscripts.size();
        if (numprescripts > 0) {
            this.addMathElement(new Mprescripts());
            for (int i = 0; i < numprescripts; i++) {
                this.addMathElement(this.presubscripts.get(i));
                this.addMathElement(this.presuperscripts.get(i));
            }
        }
        this.inRewriteChildren = false;
    }

    /** {@inheritDoc} */
    public MathMLElement insertPreSubScriptBefore(final int colIndex,
            final MathMLElement newScript) {
        final int targetIndex;
        if (colIndex == 0) {
            targetIndex = this.presubscripts.size();
        } else {
            targetIndex = colIndex - 1;
        }
        this.presubscripts.add(targetIndex, (JEuclidElement) newScript);
        this.presuperscripts.add(targetIndex, new None());
        this.rewriteChildren();
        return newScript;
    }

    /** {@inheritDoc} */
    public MathMLElement insertPreSuperScriptBefore(final int colIndex,
            final MathMLElement newScript) {
        final int targetIndex;
        if (colIndex == 0) {
            targetIndex = this.presubscripts.size();
        } else {
            targetIndex = colIndex - 1;
        }
        this.presubscripts.add(targetIndex, new None());
        this.presuperscripts.add(targetIndex, (JEuclidElement) newScript);
        this.rewriteChildren();
        return newScript;
    }

    /** {@inheritDoc} */
    public MathMLElement insertSubScriptBefore(final int colIndex,
            final MathMLElement newScript) {
        final int targetIndex;
        if (colIndex == 0) {
            targetIndex = this.postsubscripts.size();
        } else {
            targetIndex = colIndex - 1;
        }
        this.postsubscripts.add(targetIndex, (JEuclidElement) newScript);
        this.postsuperscripts.add(targetIndex, new None());
        this.rewriteChildren();
        return newScript;
    }

    /** {@inheritDoc} */
    public MathMLElement insertSuperScriptBefore(final int colIndex,
            final MathMLElement newScript) {
        final int targetIndex;
        if (colIndex == 0) {
            targetIndex = this.postsubscripts.size();
        } else {
            targetIndex = colIndex - 1;
        }
        this.postsubscripts.add(targetIndex, new None());
        this.postsuperscripts.add(targetIndex, (JEuclidElement) newScript);
        this.rewriteChildren();
        return newScript;
    }

    /** {@inheritDoc} */
    public MathMLElement setPreSubScriptAt(final int colIndex,
            final MathMLElement newScript) {
        final int targetCol = colIndex - 1;
        if (targetCol == this.presubscripts.size()) {
            return this.insertPreSubScriptBefore(0, newScript);
        } else {
            this.presubscripts.set(targetCol, (JEuclidElement) newScript);
            this.rewriteChildren();
            return newScript;
        }
    }

    /** {@inheritDoc} */
    public MathMLElement setPreSuperScriptAt(final int colIndex,
            final MathMLElement newScript) {
        final int targetCol = colIndex - 1;
        if (targetCol == this.presuperscripts.size()) {
            return this.insertPreSuperScriptBefore(0, newScript);
        } else {
            this.presuperscripts.set(targetCol, (JEuclidElement) newScript);
            this.rewriteChildren();
            return newScript;
        }
    }

    /** {@inheritDoc} */
    public MathMLElement setSubScriptAt(final int colIndex,
            final MathMLElement newScript) {
        final int targetCol = colIndex - 1;
        if (targetCol == this.postsubscripts.size()) {
            return this.insertSubScriptBefore(0, newScript);
        } else {
            this.postsubscripts.set(targetCol, (JEuclidElement) newScript);
            this.rewriteChildren();
            return newScript;
        }
    }

    /** {@inheritDoc} */
    public MathMLElement setSuperScriptAt(final int colIndex,
            final MathMLElement newScript) {
        final int targetCol = colIndex - 1;
        if (targetCol == this.postsuperscripts.size()) {
            return this.insertSuperScriptBefore(0, newScript);
        } else {
            this.postsuperscripts.set(targetCol, (JEuclidElement) newScript);
            this.rewriteChildren();
            return newScript;
        }
    }

}
