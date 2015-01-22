/*
 * ExpTreeII.java
 *
 * Created on 18 October 2010
 *
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.earthtime.UPb_Redux.expressions;

/**
 *
 * @author James F. Bowring and Brittany Johnson
 */
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.ReduxConstants;

/**
 *
 * @author James F. Bowring
 */
public class ExpTreeII {

    /**
     *
     */
    static public final ExpTreeII ZERO = new ExpTreeII(BigDecimal.ZERO);

    /**
     *
     */
    static public final ExpTreeII ONE = new ExpTreeII(BigDecimal.ONE);

    /**
     *
     */
    static public final ExpTreeII TWO = new ExpTreeII(new BigDecimal(2.0, ReduxConstants.mathContext15));

    /**
     *
     */
    static public final ExpTreeII THREE = new ExpTreeII(new BigDecimal(3.0, ReduxConstants.mathContext15));

    /**
     *
     */
    static public final ExpTreeII FOUR = new ExpTreeII(new BigDecimal(4.0, ReduxConstants.mathContext15));

    /**
     *
     */
    static public final ExpTreeII FIVE = new ExpTreeII(new BigDecimal(5.0, ReduxConstants.mathContext15));

    /**
     *
     */
    static public final ExpTreeII SIX = new ExpTreeII(new BigDecimal(6.0, ReduxConstants.mathContext15));

    /**
     *
     */
    protected String nodeName;

    /**
     *
     */
    protected String nodeRole;

    /**
     *
     */
    protected BigDecimal nodeValue;

    /**
     *
     */
    protected ExpTreeII leftChild;

    /**
     *
     */
    protected ExpTreeII rightChild;//
    //
    /**
     *
     */
    private static final Map<String, String> PresentationMathMLConversions = new HashMap<String, String>();

    static {
        PresentationMathMLConversions.put("+", "<mo> + </mo>\n");
        PresentationMathMLConversions.put("-", "<mo> - </mo>\n");
        PresentationMathMLConversions.put("*", "<mo> * </mo>\n");
        PresentationMathMLConversions.put("/", "");
        PresentationMathMLConversions.put("SQRT", "");
        PresentationMathMLConversions.put("^", "");
        PresentationMathMLConversions.put("e", "");
        PresentationMathMLConversions.put("log", "");
    }

    /**
     *
     * Creates a new Expression Tree with default values. `
     */
    public ExpTreeII() {

        nodeName = "ANON";
        nodeRole = "V";
        nodeValue = BigDecimal.ZERO;
        leftChild = null;
        rightChild = null;

    }

    /**
     *
     * Creates a new Expression Tree with all default values except the node
     * name, which becomes the name passed in.
     *
     * @param name the name of the node
     *
     */
    public ExpTreeII(String name) {

        this();
        this.nodeName = name;
    }

    /**
     *
     * Creates a new Expression Tree with all default values except the node
     * value, which becomes the value passed in.
     *
     * @param arg the BigDecimal value of the node
     *
     */
    public ExpTreeII(BigDecimal arg) {

        this();
        nodeValue = arg;
    }

    /**
     *
     * Creates a new Expression Tree with all default values except the node
     * value.Creates a BigDecimal from the double passed in.
     *
     * @param arg the double value of the node
     */
    public ExpTreeII(double arg) {

        this();
        nodeValue = new BigDecimal(arg, ReduxConstants.mathContext15);
    }

    /**
     *
     * @param nodeRole
     * @param nodeValue
     * @param leftChild
     * @param rightChild
     */
    public ExpTreeII(String nodeRole, BigDecimal nodeValue, ExpTreeII leftChild, ExpTreeII rightChild) {

        this();
        this.nodeRole = nodeRole;
        this.nodeValue = nodeValue;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    /**
     *
     * @return
     */
    public boolean isValueNode() {

        return this.nodeRole.equalsIgnoreCase("V");
    }

    /**
     *
     * @return
     */
    public String getNodeName() {

        return nodeName;
    }

    /**
     *
     * @return
     */
    public String getNodeNameLatex() {

        return nodeName.replaceAll("_", "\\\\\\_");
    }

    /**
     *
     * @return
     */
    public boolean isSubFraction() {
        boolean isFraction = false;

        if (nodeName.startsWith("r")) {
            isFraction = true;
            if (nodeName.endsWith("t")) {
                isFraction = true;
            } else if (nodeName.endsWith("oc")) {
                isFraction = true;
            } else if (nodeName.endsWith("s")) {
                isFraction = true;
            } else if (nodeName.endsWith("m")) {
                isFraction = true;
            } else if (nodeName.endsWith("b")) {
                isFraction = true;
            } else {
                isFraction = true;
            }
        }

        return isFraction;
    }

//    public String getNodeNameMathML() {
//        String nameMML = nodeName;
//
//        if (nodeName.startsWith("r")) {
//            nameMML = "<msub>\n"
//                    + "<mrow>\n"
//                    + "<mo>\n"
//                    + "(\n"
//                    + "</mo>\n"
//                    + "<mfrac>\n"//
//                    + "<msub>\n"
//                    + getIsotopeMathML(nodeName.substring(1, 4))
//                    + "\n</msub>\n"
//                    + "<msub>\n"
//                    + getIsotopeMathML(nodeName.substring(5, 8))
//                    + "\n</msub>\n" //
//                    + "</mfrac>\n"
//                    + "<mo>\n"
//                    + ")\n"
//                    + "</mo>"
//                    + "\n</mrow>\n";
//            if (nodeName.endsWith("t")) {
//                nameMML += "<mi>\n"
//                        + "tr\n"
//                        + "</mi>\n"
//                        + "</msub>\n";
//            } else if (nodeName.endsWith("oc")) {
//                nameMML += "<mi>\n"
//                        + "oc\n"
//                        + "</mi>\n"
//                        + "</msub>\n";
//            } else if (nodeName.endsWith("s")) {
//                nameMML += "<mi>\n"
//                        + "s\n"
//                        + "</mi>\n"
//                        + "</msub>\n"
//                        + "";
//            } else if (nodeName.endsWith("m")) {
//                nameMML += "<mi>\n"
//                        + "m\n"
//                        + "</mi>\n"
//                        + "</msub>\n";
//            } else if (nodeName.endsWith("b")) {
//                nameMML += "<mi>\n"
//                        + "b\n"
//                        + "</mi>\n"
//                        + "</msub>\n";
//            } else {
//                nameMML = nodeName;
//            }
//        }
//
//        return nameMML;
//    }

    /**
     *
     * @return
     */
        public String getNodeNameMathML() {
        String nameMML = nodeName;

        if (nodeName.startsWith("r")) {
            nameMML = "<msub>\n"
                    + "<mrow>\n"
                    + "<mo>\n"
                    + "(\n"
                    + "</mo>\n"
                    + "<mfrac>\n"//
                    + "<msubsup>\n"
                    + getIsotopeMathML(nodeName.substring(1, 4))
                    + "\n<mi></mi></msubsup>\n"
                    + "<msubsup>\n"
                    + getIsotopeMathML(nodeName.substring(5, 8))
                    + "\n<mi></mi></msubsup>\n" //
                    + "</mfrac>\n"
                    + "<mo>\n"
                    + ")\n"
                    + "</mo>"
                    + "\n</mrow>\n";
            if (nodeName.endsWith("t")) {
                nameMML += "<mi>\n"
                        + "tr\n"
                        + "</mi>\n"
                        + "</msub>\n";
            } else if (nodeName.endsWith("oc")) {
                nameMML += "<mi>\n"
                        + "oc\n"
                        + "</mi>\n"
                        + "</msub>\n";
            } else if (nodeName.endsWith("s")) {
                nameMML += "<mi>\n"
                        + "s\n"
                        + "</mi>\n"
                        + "</msub>\n"
                        + "";
            } else if (nodeName.endsWith("m")) {
                nameMML += "<mi>\n"
                        + "m\n"
                        + "</mi>\n"
                        + "</msub>\n";
            } else if (nodeName.endsWith("b")) {
                nameMML += "<mi>\n"
                        + "b\n"
                        + "</mi>\n"
                        + "</msub>\n";
            } else {
                nameMML = nodeName;
            }
        }

        return nameMML;
    }
    
//        private String getIsotopeMathML(String atomicWeight) {
//        String isotopeMathML = "";
//        if (atomicWeight.contains("0")) {
//            isotopeMathML = "<mtext>\n"
//                    + " Pb \n"
//                    + "</mtext>\n"
//                    +"<mn>\n"
//                    + atomicWeight
//                    + "\n</mn>\n"
//                    ;
//
//        } else {
//            isotopeMathML ="<mtext>\n"
//                    + " U \n"
//                    + "</mtext>\n"
//                    +"<mn>\n"
//                    + atomicWeight
//                    + "\n</mn>\n"
//                    ;
//
//        }
//
//        return isotopeMathML;
//    }

    private String getIsotopeMathML(String atomicWeight) {
        String isotopeMathML = "";
        if (atomicWeight.contains("0")) {
            isotopeMathML = "<mn>\n"
                    + atomicWeight
                    + "\n</mn>\n"
                    +"<mtext>\n"
                    + " Pb \n"
                    + "</mtext>\n"
                    ;

        } else {
            isotopeMathML ="<mn>\n"
                    + atomicWeight
                    + "\n</mn>\n"
                    +"<mtext>\n"
                    + " U \n"
                    + "</mtext>\n"
                    ;

        }

        return isotopeMathML;
    }

    //********************

    /**
     *
     * @param nodeName
     */
        public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     *
     * @return
     */
    public BigDecimal getNodeValue() {

        return nodeValue;
    }

    /**
     *
     * @param nodeValue
     */
    public void setNodeValue(BigDecimal nodeValue) {

        this.nodeValue = nodeValue;
    }

    /**
     *
     * @return
     */
    public String getNodeRole() {

        return nodeRole;
    }

    /**
     *
     * @param rightChild
     * @return
     */
    public ExpTreeII add(ExpTreeII rightChild) {

        return new ExpTreeII("+", this.getNodeValue().add(rightChild.getNodeValue(), ReduxConstants.mathContext15), this, rightChild);
    }

    /**
     *
     * @param rightChild
     * @return
     */
    public ExpTreeII subtract(ExpTreeII rightChild) {

        return new ExpTreeII("-", this.getNodeValue().subtract(rightChild.getNodeValue(), ReduxConstants.mathContext15), this, rightChild);
    }

    /**
     *
     * @param rightChild
     * @return
     */
    public ExpTreeII divide(ExpTreeII rightChild) {

        if (rightChild.getNodeValue().compareTo(BigDecimal.ZERO) == 0) {
            return new ExpTreeII("/", BigDecimal.ZERO, this, rightChild);

        } else {

            return new ExpTreeII("/", this.getNodeValue().divide(rightChild.getNodeValue(), ReduxConstants.mathContext15), this, rightChild);
        }

    }

    /**
     *
     * @param rightChild
     * @return
     */
    public ExpTreeII multiply(ExpTreeII rightChild) {

        return new ExpTreeII("*", this.getNodeValue().multiply(rightChild.getNodeValue(), ReduxConstants.mathContext15), this, rightChild);
    }

    /**
     *
     * @return
     */
    public ExpTreeII sqrt() {

        BigDecimal mySqrt = BigDecimal.ZERO;

        if (this.getNodeValue().doubleValue() >= 0) {
            mySqrt = new BigDecimal(Math.sqrt(this.getNodeValue().doubleValue()), ReduxConstants.mathContext15);
        }

        return new ExpTreeII("SQRT", mySqrt, this, null);

    }

    /**
     *
     * @param rightChild
     * @return
     */
    public ExpTreeII pow(ExpTreeII rightChild) {

        double myPow = //
                Math.pow(this.getNodeValue().doubleValue(), rightChild.getNodeValue().doubleValue());

        if (Double.isNaN(myPow)) {
            myPow = 0;
        }

        return new ExpTreeII("^", new BigDecimal(myPow), this, rightChild);
    }

    /**
     *
     * @return
     */
    public ExpTreeII exp() {

        double myExpm1 = //
                Math.expm1(this.getNodeValue().doubleValue());

        if (Double.isNaN(myExpm1)) {
            myExpm1 = 0;
        }

        if (Double.isInfinite(myExpm1)) {
            myExpm1 = 0;
        }

        return new ExpTreeII("e", new BigDecimal(myExpm1 + 1.0), this, null);
    }

    /**
     *
     * @return
     */
    public ExpTreeII log() {

        double myLog1p = //
                Math.log1p(this.getNodeValue().doubleValue());

        if (Double.isNaN(myLog1p)) {
            myLog1p = 0;
        }

        if (Double.isInfinite(myLog1p)) {
            myLog1p = 0;
        }

        return new ExpTreeII("log", new BigDecimal(myLog1p - 1.0), this, null);
    }

    /**
     *
     * @return
     */
    public ExpTreeII sqr() {
        return pow(new ExpTreeII(2));
    }

    /**
     *
     * @return
     */
    public String getNodeInfo() {

        String info = "";

        if (!this.isValueNode()) {

            info += "Node Role: " + this.nodeRole + "\n";
        } else {

            info += "Node Name: " + this.nodeName + "\n";
            info += "Node Value: " + this.getNodeValue() + "\n";
        }
        return info;
    }

    /**
     *
     * @param depth
     * @param showValue
     * @return
     * @throws IOException
     */
    public String treeToPresentation(int depth, boolean showValue) throws IOException {

        return presentationNodeProcessor(this, depth, showValue, false);
    }

    /**
     *
     * @param node
     * @param depth
     * @param showValue
     * @param suppressParens
     * @return
     * @throws IOException
     */
    public static String presentationNodeProcessor(ExpTreeII node, int depth, boolean showValue, boolean suppressParens) throws IOException {
        String retVal = "";

        String htmlLinkControl = //
                " style=\"text-decoration:none\"  link=\"text-decoration:none\" visited=\"text-decoration:none\" href=\"";
        if (node != null) {
            if ((depth == 0) || (node.leftChild == null)) {
                if (showValue) {
                    retVal += "<mn>\n"
                            + MathMachineII.formattedValue(node.getNodeValue())
                            + "\n</mn>\n";
                } else {  //expression mode
                    if (node.nodeName.equals("ANON")) {
                        retVal += "<mn>\n"
                                + MathMachineII.formattedValue(node.getNodeValue())
                                + "\n</mn>\n";
                    } else {
                        retVal += "<mi>\n"
                                + "<a " + (String) ((node.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachineII.createPresentationFileVMs(node, "") + "\"")//
                                + " title=\"" //
                                + MathMachineII.formattedValue(node.nodeValue) //
                                + "\"onMouseOver=\"openWindow('" + node.nodeName + ".html ','" + node.nodeName + "')\" onMouseOut=\"closeWindow()\"  >\n";
                        if (node.isSubFraction()) {
                            retVal += "<math label = \"" + node.nodeName + "\">\n"
                                    + node.getNodeNameMathML()
                                    + " \n</math>\n";
                        } else {
                            retVal += "<mtext label = \"" + node.nodeName + "\">\n"
                                    + node.getNodeNameMathML()
                                    + " \n</mtext>\n";
                        }
                        retVal += "</a>\n"
                                + "</mi>\n";
                    }
                }
            } else {

                boolean leftStop = false;
                int leftDepth = depth;
                if (!node.leftChild.nodeName.equalsIgnoreCase("ANON")) {
                    if (leftDepth == 1) {
                        leftStop = true;
                    } else {
                        leftDepth--;
                    }
                }

                boolean rightStop = false;
                int rightDepth = depth;
                // escapes if right is null as in unary operations
                try {
                    if (!node.rightChild.nodeName.equalsIgnoreCase("ANON")) {
                        if (rightDepth == 1) {
                            rightStop = true;
                        } else {
                            rightDepth--;
                        }
                    }
                } catch (Exception e) {
                }

                if (node.nodeRole.equals("/")) {

                    if (leftStop && !showValue) {
                        retVal += "<mfrac>\n"//
                                + "<mi>\n"
                                + "<a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachineII.createPresentationFileVMs(node.leftChild, "") + "\"")//
                                + " title=\"" //
                                + MathMachineII.formattedValue(node.leftChild.nodeValue) //
                                + "\"onMouseOver=\"openWindow('" + node.leftChild.getNodeName() + ".html ','" + node.leftChild.nodeName + "')\" onMouseOut=\"closeWindow()\">\n";
                        if (node.leftChild.isSubFraction()) {
                            retVal += "<math label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</math>\n";
                        } else {
                            retVal += "<mtext label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</mtext>\n";
                        }
                        retVal += "</a>\n"
                                + "</mi>\n";
                    } else if (leftStop) {
                        retVal += "<mfrac>\n"
                                + "<mn>\n"
                                + MathMachineII.formattedValue(node.leftChild.getNodeValue())
                                + "\n</mn>\n";
                    } else {
                        retVal += "<mfrac>\n"
                                + "<mrow>\n"
                                + presentationNodeProcessor(node.leftChild, leftDepth, showValue, true)
                                + "\n</mrow>\n";
                    }

                    if (rightStop && !showValue) {
                        retVal += "<mi>\n"
                                + "<a" + (String) ((node.rightChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachineII.createPresentationFileVMs(node.rightChild, "") + "\"")//
                                + " title=\"" //
                                + MathMachineII.formattedValue(node.rightChild.nodeValue) //
                                + "\"onMouseOver=\"openWindow('" + node.rightChild.getNodeName() + ".html','" + node.rightChild.nodeName + "')\" onMouseOut=\"closeWindow()\">\n";
                        if (node.rightChild.isSubFraction()) {
                            retVal += "<math label = \"" + node.rightChild.nodeName + "\">\n"
                                    + node.rightChild.getNodeNameMathML()
                                    + " \n</math>\n";
                        } else {
                            retVal += "<mtext label = \"" + node.rightChild.nodeName + "\">\n"
                                    + node.rightChild.getNodeNameMathML()
                                    + " \n</mtext>\n";
                        }
                        retVal += "</a>\n"
                                + "</mi>\n"
                                + "</mfrac>\n";//closing mfrac added june

                    } else if (rightStop) {
                        retVal += "<mn>\n"
                                + MathMachineII.formattedValue(node.rightChild.getNodeValue())
                                + "\n</mn>\n"
                                + "</mfrac>\n"; // this may be unreachable
                    } else {
                        retVal += "<mrow>\n"
                                + presentationNodeProcessor(node.rightChild, rightDepth, showValue, true)
                                + "\n</mrow>\n"
                                + "</mfrac>\n";
                    }

                } else if (node.nodeRole.equals("^")) {

                    if (leftStop && !showValue) {
                        retVal += "<msup>\n"//
                                + "<mi>\n"
                                + "<a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachineII.createPresentationFileVMs(node.leftChild, "") + "\"")//
                                + " title=\"" //
                                + MathMachineII.formattedValue(node.leftChild.nodeValue) //
                                + "\"onMouseOver=\"openWindow('" + node.leftChild.getNodeName() + ".html','" + node.leftChild.nodeName + "')\" onMouseOut=\"closeWindow()\">\n";
                        if (node.leftChild.isSubFraction()) {
                            retVal += "<math label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</math>\n";
                        } else {
                            retVal += "<mtext label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</mtext>\n";
                        }
                        retVal += "</a>\n"
                                + "</mi>\n";

                    } else if (leftStop) {
                        retVal += "<msup>\n"
                                + "<mn>\n"
                                + MathMachineII.formattedValue(node.leftChild.getNodeValue())
                                + "\n</mn>\n";
                    } else {
                        retVal += "<msup>\n"
                                + "<mrow>\n" + presentationNodeProcessor(node.leftChild, leftDepth, showValue, false)
                                + "\n</mrow>\n";
                    }

                    if (rightStop && !showValue) {
                        retVal += "<mi\n"
                                + "><a" + (String) ((node.rightChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachineII.createPresentationFileVMs(node.rightChild, "") + "\"")//
                                + " title=\"" //
                                + MathMachineII.formattedValue(node.rightChild.nodeValue) //
                                + "\"onMouseOver=\"openWindow('" + node.rightChild.getNodeName() + ".html','" + node.rightChild.nodeName + "')\" onMouseOut=\"closeWindow()\">\n";
                        if (node.rightChild.isSubFraction()) {
                            retVal += "<math label = \"" + node.rightChild.nodeName + "\">\n"
                                    + node.rightChild.getNodeNameMathML()
                                    + " \n</math>\n";
                        } else {
                            retVal += "<mtext label = \"" + node.rightChild.nodeName + "\">\n"
                                    + node.rightChild.getNodeNameMathML()
                                    + " \n</mtext>\n";
                        }
                        retVal += "</a>\n"
                                + "</mi>\n"
                                + "</msup>\n";
                    } else if (rightStop) {
                        retVal += "<mn>\n"
                                + MathMachineII.formattedValue(node.rightChild.getNodeValue())
                                + "\n</mn>\n"
                                + "</msup>\n";
                    } else {
                        retVal += "<mrow>\n"
                                + presentationNodeProcessor(node.rightChild, rightDepth, showValue, false)
                                + "\n</mrow>\n"
                                + "</msup>\n";
                    }

                } else if (node.nodeRole.equals("SQRT")) {

                    if (leftStop && !showValue) {
                        retVal += "<msqrt>\n"//
                                + "<mi>\n"
                                + "<a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachineII.createPresentationFileVMs(node.leftChild, "") + "\"")//
                                + " title=\"" //
                                + MathMachineII.formattedValue(node.leftChild.nodeValue) //
                                + "\"onMouseOver=\"openWindow('" + node.leftChild.getNodeName() + ".html','" + node.leftChild.nodeName + "')\" onMouseOut=\"closeWindow()\">\n";
                        if (node.leftChild.isSubFraction()) {
                            retVal += "<math label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</math>\n";
                        } else {
                            retVal += "<mtext label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</mtext>\n";
                        }
                        retVal += "</a>\n"
                                + "</mi>\n"
                                + "</msqrt>\n";

                    } else if (leftStop) {
                        retVal += "<msqrt>\n"
                                + "<mn>\n"
                                + MathMachineII.formattedValue(node.leftChild.getNodeValue()) + "\n</mn>\n"
                                + "</msqrt>\n";
                    } else {
                        retVal += "<msqrt>\n"
                                + "<mrow>\n"
                                + presentationNodeProcessor(node.leftChild, leftDepth, showValue, true) + "\n</mrow>\n"
                                + "</msqrt>\n";
                    }

                } else if (node.nodeRole.equals("e")) {

                    String specialELink = //
                            "<msup>\n"
                            + "<mi>\n"
                            + "<a" + htmlLinkControl + " title=\"2.71828182845904523536...\">\n"
                            + "<em>\n"
                            + "e\n"
                            + "</em>\n"
                            + "</a>\n"
                            + "</mi>\n";
                    if (leftStop && !showValue) {
                        retVal += specialELink //
                                + "<mi>\n"
                                + "<a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachineII.createPresentationFileVMs(node.leftChild, "") + "\"")//
                                + " title=\"" //
                                + MathMachineII.formattedValue(node.leftChild.nodeValue) //
                                + "\"onMouseOver=\"openWindow('" + node.leftChild.getNodeName() + ".html','" + node.leftChild.nodeName + "')\" onMouseOut=\"closeWindow()\">\n";
                        if (node.leftChild.isSubFraction()) {
                            retVal += "<math label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</math>\n";
                        } else {
                            retVal += "<mtext label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</mtext>\n";
                        }
                        retVal += "</a>\n"
                                + "</mi>\n"
                                + "</msup>\n";
                    } else if (leftStop) {
                        retVal += specialELink + "<mn>\n"
                                + MathMachineII.formattedValue(node.leftChild.getNodeValue()) + "\n</mn>\n"
                                + "</msup>\n";
                    } else {
                        retVal += specialELink + "<mrow>\n"
                                + presentationNodeProcessor(node.leftChild, leftDepth, showValue, false) + "\n</mrow>\n"
                                + "</msup>\n";
                    }

                } else if (node.nodeRole.equals("log")) {

                    if (leftStop && !showValue) {
                        retVal += "<mi>\n"
                                + "log\n"
                                + "</mi>\n"//
                                + "<mi>\n"
                                + "<a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachineII.createPresentationFileVMs(node.leftChild, "") + "\"")//
                                + " title=\"" //
                                + MathMachineII.formattedValue(node.leftChild.nodeValue) //
                                + "\"onMouseOver=\"openWindow('" + node.leftChild.getNodeName() + ".html','" + node.leftChild.nodeName + "')\" onMouseOut=\"closeWindow()\">\n";
                        if (node.leftChild.isSubFraction()) {
                            retVal += "<math label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</math>\n";
                        } else {
                            retVal += "<mtext label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</mtext>\n";
                        }
                        retVal += "</a>\n"
                                + "</mi>\n";
                    } else if (leftStop) {
                        retVal += "<mi>\n"
                                + "log\n"
                                + "</mi>\n"
                                + "<mn>\n"
                                + MathMachineII.formattedValue(node.leftChild.getNodeValue())
                                + "\n</mn>\n";
                    } else {
                        retVal += "<mi>\n"
                                + "log\n"
                                + "</mi>\n"
                                + "<mrow>\n"
                                + presentationNodeProcessor(node.leftChild, leftDepth, showValue, false)
                                + "\n</mrow>\n";
                    }

                } else {

                    String leftParen = "";
                    String rightParen = "";

                    if (!suppressParens && !node.nodeRole.equals("*")) {
                        leftParen = "<mo>\n"
                                + " ( \n"
                                + "</mo>\n";
                        rightParen = "<mo>\n"
                                + " ) \n"
                                + "</mo>\n";
                    }

                    if (leftStop && !showValue) {
                        retVal += leftParen//
                                + "<mi>\n"
                                + "<a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl//
                                + MathMachineII.createPresentationFileVMs(node.leftChild, "") + "\"")//
                                + " title=\"" //
                                + MathMachineII.formattedValue(node.leftChild.nodeValue) //
                                + "\"onMouseOver=\"openWindow('" + node.leftChild.getNodeName() + ".html','" + node.leftChild.nodeName + "')\" onMouseOut=\"closeWindow()\">\n";
                        if (node.leftChild.isSubFraction()) {
                            retVal += "<math label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</math>\n";
                        } else {
                            retVal += "<mtext label = \"" + node.leftChild.nodeName + "\">\n"
                                    + node.leftChild.getNodeNameMathML()
                                    + " \n</mtext>\n";
                        }
                        retVal += "</a>\n"
                                + "</mi>\n";
                    } else if (leftStop) {
                        retVal += leftParen + "<mn>\n"
                                + MathMachineII.formattedValue(node.leftChild.getNodeValue())
                                + "\n</mn>\n";
                    } else {
                        retVal += leftParen + presentationNodeProcessor(node.leftChild, leftDepth, showValue, false);
                    }

                    retVal += PresentationMathMLConversions.get(node.nodeRole);


                    if (rightStop && !showValue) {
                        retVal += "<mi>\n"
                                + "<a" + (String) ((node.rightChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachineII.createPresentationFileVMs(node.rightChild, "") + "\"")//
                                + " title=\"" //
                                + MathMachineII.formattedValue(node.rightChild.nodeValue) //
                                + "\"onMouseOver=\"openWindow('" + node.rightChild.getNodeName() + ".html','" + node.rightChild.nodeName + "')\" onMouseOut=\"closeWindow()\">\n";
                        if (node.rightChild.isSubFraction()) {
                            retVal += "<math label = \"" + node.rightChild.nodeName + "\">\n"
                                    + node.rightChild.getNodeNameMathML()
                                    + " \n</math>\n";
                        } else {
                            retVal += "<mtext label = \"" + node.rightChild.nodeName + "\">\n"
                                    + node.rightChild.getNodeNameMathML()
                                    + " \n</mtext>\n";
                        }
                        retVal += "</a>\n"
                                + "</mi>\n" + rightParen;
                    } else if (rightStop) {
                        retVal += "<mn>\n"
                                + MathMachineII.formattedValue(node.rightChild.getNodeValue()) + "</mn>\n" + rightParen;
                    } else {
                        retVal += presentationNodeProcessor(node.rightChild, rightDepth, showValue, false) + rightParen;
                    }

                }
            }
        }
        return retVal;
    }
}