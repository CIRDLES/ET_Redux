/*
 * ExpTree.java
 *
 * Created on 18 October 2010
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.ReduxConstants;

/**
 * 
 * @author samuelbowring
 */
public class ExpTree {

    /**
     * 
     */
    static public ExpTree ZERO = new ExpTree( BigDecimal.ZERO );
    /**
     * 
     */
    static public ExpTree ONE = new ExpTree( BigDecimal.ONE );
    /**
     * 
     */
    static public ExpTree TWO = new ExpTree( new BigDecimal( 2.0, ReduxConstants.mathContext15 ) );
    /**
     * 
     */
    static public ExpTree THREE = new ExpTree( new BigDecimal( 3.0, ReduxConstants.mathContext15 ) );
    /**
     * 
     */
    static public ExpTree FOUR = new ExpTree( new BigDecimal( 4.0, ReduxConstants.mathContext15 ) );
    /**
     * 
     */
    static public ExpTree FIVE = new ExpTree( new BigDecimal( 5.0, ReduxConstants.mathContext15 ) );
    /**
     * 
     */
    static public ExpTree SIX = new ExpTree( new BigDecimal( 6.0, ReduxConstants.mathContext15 ) );
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
    protected ExpTree leftChild;
    /**
     * 
     */
    protected ExpTree rightChild;//
    //
    /**
     *
     */
    private static final Map<String, String> PresentationMathMLConversions = new HashMap<String, String>();

    static {
        PresentationMathMLConversions.put( "+", "<mo> + </mo>\n" );
        PresentationMathMLConversions.put( "-", "<mo> - </mo>\n" );
        PresentationMathMLConversions.put( "*", "<mo> * </mo>\n" );
        PresentationMathMLConversions.put( "/", "" );
        PresentationMathMLConversions.put( "SQRT", "" );
        PresentationMathMLConversions.put( "^", "" );
        PresentationMathMLConversions.put( "e", "" );
        PresentationMathMLConversions.put( "log", "" );
    }

    /**
     *
     * Creates a new Expression Tree with default values.
     * `
     */
    public ExpTree () {

        nodeName = "ANON";
        nodeRole = "V";
        nodeValue = BigDecimal.ZERO;
        leftChild = null;
        rightChild = null;

    }

    /**
     *
     * Creates a new Expression Tree with all default values except the node name, which becomes the name passed in.
     *
     * @param name the name of the node
     *
     */
    public ExpTree ( String name ) {

        this();
        this.nodeName = name;
    }

    /**
     *
     * Creates a new Expression Tree with all default values except the node value, which becomes the value passed in.
     *
     * @param arg the BigDecimal value of the node
     *
     */
    public ExpTree ( BigDecimal arg ) {

        this();
        nodeValue = arg;
    }

    /**
     *
     * Creates a new Expression Tree with all default values except the node value.Creates a BigDecimal from the double passed
     * in.
     *
     * @param arg the double value of the node
     */
    public ExpTree ( double arg ) {

        this();
        nodeValue = new BigDecimal( arg, ReduxConstants.mathContext15 );
    }

    /**
     * 
     * @param nodeRole
     * @param nodeValue
     * @param leftChild
     * @param rightChild
     */
    public ExpTree ( String nodeRole, BigDecimal nodeValue, ExpTree leftChild, ExpTree rightChild ) {

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
    public boolean isValueNode () {

        return this.nodeRole.equalsIgnoreCase( "V" );
    }

    /**
     * 
     * @return
     */
    public String getNodeName () {

        return nodeName;
    }

    /**
     * 
     * @return
     */
    public String getNodeNameLatex () {

        return nodeName.replaceAll( "_", "\\\\\\_" );
    }

    /**
     * 
     * @return
     */
    public String getNodeNameMathML () {
        String nameMML = nodeName;

        if ( nodeName.startsWith( "r" ) ) {
            nameMML = "<msub><mrow><mo>(</mo><mfrac>"//
                    + "<mi>" + getIsotopeMathML( nodeName.substring( 1, 4 ) ) + "</mi>" //
                    + "<mi>" + getIsotopeMathML( nodeName.substring( 5, 8 ) ) + "</mi>" //
                    + "</mfrac><mo>)</mo></mrow>";
            if ( nodeName.endsWith( "t" ) ) {
                nameMML += "<mi>tr</mi></msub>";
            } else if ( nodeName.endsWith( "oc" ) ) {
                nameMML += "<mi>oc</mi></msub>";
            } else if ( nodeName.endsWith( "s" ) ) {
                nameMML += "<mi>s</mi></msub>";
            } else if ( nodeName.endsWith( "m" ) ) {
                nameMML += "<mi>m</mi></msub>";
            } else if ( nodeName.endsWith( "b" ) ) {
                nameMML += "<mi>b</mi></msub>";
            } else {
                nameMML = nodeName;
            }
        }

        return nameMML;
    }

    private String getIsotopeMathML ( String atomicWeight ) {
        String isotopeMathML = "";
        if ( atomicWeight.contains( "0" ) ) {
            isotopeMathML = "<mover><mtext> &#160;&#160;&#160;&#160;&#160;&#160;&#160;Pb </mtext><mi>" + atomicWeight + "</mi></mover>";//<mtext> &nbsp;&nbsp;vs&nbsp;&nbsp; </mtext>

        } else {
            isotopeMathML = "<mover><mtext> &#160;&#160;&#160;&#160;&#160;&#160;&#160;U </mtext><mi>" + atomicWeight + "</mi></mover>";

        }

        return isotopeMathML;
    }

    /**
     * 
     * @param nodeName
     */
    public void setNodeName ( String nodeName ) {
        this.nodeName = nodeName;
    }

    /**
     * 
     * @return
     */
    public BigDecimal getNodeValue () {

        return nodeValue;
    }

    /**
     * 
     * @param nodeValue
     */
    public void setNodeValue ( BigDecimal nodeValue ) {

        this.nodeValue = nodeValue;
    }

    /**
     * 
     * @return
     */
    public String getNodeRole () {

        return nodeRole;
    }

    /**
     * 
     * @param rightChild
     * @return
     */
    public ExpTree add ( ExpTree rightChild ) {

        return new ExpTree( "+", this.getNodeValue().add(rightChild.getNodeValue(), ReduxConstants.mathContext15 ), this, rightChild );
    }

    /**
     * 
     * @param rightChild
     * @return
     */
    public ExpTree subtract ( ExpTree rightChild ) {

        return new ExpTree( "-", this.getNodeValue().subtract(rightChild.getNodeValue(), ReduxConstants.mathContext15 ), this, rightChild );
    }

    /**
     * 
     * @param rightChild
     * @return
     */
    public ExpTree divide ( ExpTree rightChild ) {

        if ( rightChild.getNodeValue().compareTo( BigDecimal.ZERO ) == 0 ) {
            return new ExpTree( "/", BigDecimal.ZERO, this, rightChild );

        } else {

            return new ExpTree( "/", this.getNodeValue().divide(rightChild.getNodeValue(), ReduxConstants.mathContext15 ), this, rightChild );
        }

    }

    /**
     * 
     * @param rightChild
     * @return
     */
    public ExpTree multiply ( ExpTree rightChild ) {

        return new ExpTree( "*", this.getNodeValue().multiply(rightChild.getNodeValue(), ReduxConstants.mathContext15 ), this, rightChild );
    }

    /**
     * 
     * @return
     */
    public ExpTree sqrt () {

        BigDecimal mySqrt = BigDecimal.ZERO;

        if ( this.getNodeValue().doubleValue() >= 0 ) {
            mySqrt = new BigDecimal( Math.sqrt( this.getNodeValue().doubleValue() ), ReduxConstants.mathContext15 );
        }

        return new ExpTree( "SQRT", mySqrt, this, null );

    }

    /**
     * 
     * @param rightChild
     * @return
     */
    public ExpTree pow ( ExpTree rightChild ) {

        double myPow = //
                Math.pow( this.getNodeValue().doubleValue(), rightChild.getNodeValue().doubleValue() );

        if ( !Double.isFinite( myPow ) ) {
            myPow = 0;
        }

        return new ExpTree( "^", new BigDecimal( myPow ), this, rightChild );
    }

    /**
     * 
     * @return
     */
    public ExpTree exp () {

        double myExpm1 = //
                Math.expm1( this.getNodeValue().doubleValue() );

        if ( !Double.isFinite( myExpm1 ) ) {
            myExpm1 = 0;
        }

        if ( Double.isInfinite( myExpm1 ) ) {
            myExpm1 = 0;
        }

        return new ExpTree( "e", new BigDecimal( myExpm1 + 1.0 ), this, null );
    }

    /**
     * 
     * @return
     */
    public ExpTree log () {

        double myLog1p = //
                Math.log1p( this.getNodeValue().doubleValue() );

        if ( !Double.isFinite( myLog1p ) ) {
            myLog1p = 0;
        }

        if ( Double.isInfinite( myLog1p ) ) {
            myLog1p = 0;
        }

        return new ExpTree( "log", new BigDecimal( myLog1p - 1.0 ), this, null );
    }

    /**
     * 
     * @return
     */
    public ExpTree sqr () {
        return pow( new ExpTree( 2 ) );
    }

    /**
     * 
     * @return
     */
    public String getNodeInfo () {

        String info = "";

        if (  ! this.isValueNode() ) {

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
     */
    public String treeToPresentation ( int depth, boolean showValue ) {

        return presentationNodeProcessor( this, depth, showValue, false );
    }

    /**
     * 
     * @param node
     * @param depth
     * @param showValue
     * @param suppressParens
     * @return
     */
    public static String presentationNodeProcessor ( ExpTree node, int depth, boolean showValue, boolean suppressParens ) {

        String retVal = "";

        String htmlLinkControl = //
                " style=\"text-decoration:none\"  link=\"text-decoration:none\" visited=\"text-decoration:none\" href=\"";

        if ( node != null ) {
            if ( (depth == 0) || (node.leftChild == null) ) {
                if ( showValue ) {
                    retVal += "<mn>" + MathMachine.formattedValue( node.getNodeValue() ) + "</mn>";
                } else {  //expression mode
                    if ( node.nodeName.equals( "ANON" ) ) {
                        retVal += "<mn>" + MathMachine.formattedValue( node.getNodeValue() ) + "</mn>";
                    } else {
                        retVal += "<mi><html:a" + (String) ((node.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachine.createPresentationFile( node, "" ) + "\"")//
                                + " title=\"" //
                                + MathMachine.formattedValue( node.nodeValue ) //
                                + "\">" + node.getNodeNameMathML() //
                                + "</html:a></mi>";
                    }
                }
            } else {

                boolean leftStop = false;
                int leftDepth = depth;
                if (  ! node.leftChild.nodeName.equalsIgnoreCase( "ANON" ) ) {
                    if ( leftDepth == 1 ) {
                        leftStop = true;
                    } else {
                        leftDepth --;
                    }
                }

                boolean rightStop = false;
                int rightDepth = depth;
                // escapes if right is null as in unary operations
                try {
                    if (  ! node.rightChild.nodeName.equalsIgnoreCase( "ANON" ) ) {
                        if ( rightDepth == 1 ) {
                            rightStop = true;
                        } else {
                            rightDepth --;
                        }
                    }
                } catch (Exception e) {
                }

                if ( node.nodeRole.equals( "/" ) ) {

                    if ( leftStop &&  ! showValue ) {
                        retVal += "<mfrac>\n"//
                                + "<mi><html:a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachine.createPresentationFile( node.leftChild, "" ) + "\"")//
                                + " title=\"" //
                                + MathMachine.formattedValue( node.leftChild.nodeValue ) //
                                + "\">" + node.leftChild.getNodeNameMathML() //
                                + "</html:a></mi>\n";
                    } else if ( leftStop ) {
                        retVal += "<mfrac>\n" + "<mn>" + MathMachine.formattedValue( node.leftChild.getNodeValue() ) + "</mn>\n";
                    } else {
                        retVal += "<mfrac>\n" + "<mrow>\n" + presentationNodeProcessor( node.leftChild, leftDepth, showValue, true ) + "</mrow>\n";
                    }

                    if ( rightStop &&  ! showValue ) {
                        retVal += "<mi><html:a" + (String) ((node.rightChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachine.createPresentationFile( node.rightChild, "" ) + "\"")//
                                + " title=\"" //
                                + MathMachine.formattedValue( node.rightChild.nodeValue ) //
                                + "\">" //
                                + node.rightChild.getNodeNameMathML()//
                                + "</html:a></mi>\n</mfrac>\n";//close of mfrac added june 2011
                    } else if ( rightStop ) {
                        retVal += "<mn>" + MathMachine.formattedValue( node.rightChild.getNodeValue() ) + "</mn>\n</mfrac>\n"; //close of mfrac added june 2011
                    } else {
                        retVal += "<mrow>\n" + presentationNodeProcessor( node.rightChild, rightDepth, showValue, true ) + "</mrow>\n" + "</mfrac>\n";
                    }

                } else if ( node.nodeRole.equals( "^" ) ) {

                    if ( leftStop &&  ! showValue ) {
                        retVal += "<msup>\n"//
                                + "<mi><html:a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachine.createPresentationFile( node.leftChild, "" ) + "\"")//
                                + " title=\"" //
                                + MathMachine.formattedValue( node.leftChild.nodeValue ) //
                                + "\">" + node.leftChild.getNodeNameMathML() //
                                + "</html:a></mi>\n";
                    } else if ( leftStop ) {
                        retVal += "<msup>\n" + "<mn>" + MathMachine.formattedValue( node.leftChild.getNodeValue() ) + "</mn>\n";
                    } else {
                        retVal += "<msup>\n" + "<mrow>\n" + presentationNodeProcessor( node.leftChild, leftDepth, showValue, false ) + "</mrow>\n";
                    }

                    if ( rightStop &&  ! showValue ) {
                        retVal += "<mi><html:a" + (String) ((node.rightChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachine.createPresentationFile( node.rightChild, "" ) + "\"")//
                                + " title=\"" //
                                + MathMachine.formattedValue( node.rightChild.nodeValue ) //
                                + "\">" //
                                + node.rightChild.getNodeNameMathML()//
                                + "</html:a></mi></msup>\n";
                    } else if ( rightStop ) {
                        retVal += "<mn>" + MathMachine.formattedValue( node.rightChild.getNodeValue() ) + "</mn></msup>\n";
                    } else {
                        retVal += "<mrow>\n" + presentationNodeProcessor( node.rightChild, rightDepth, showValue, false ) + "</mrow>\n" + "</msup>\n";
                    }

                } else if ( node.nodeRole.equals( "SQRT" ) ) {

                    if ( leftStop &&  ! showValue ) {
                        retVal += "<msqrt>\n"//
                                + "<mi><html:a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachine.createPresentationFile( node.leftChild, "" ) + "\"")//
                                + " title=\"" //
                                + MathMachine.formattedValue( node.leftChild.nodeValue ) //
                                + "\">" + node.leftChild.getNodeNameMathML() //
                                + "</html:a></mi></msqrt>\n";
                    } else if ( leftStop ) {
                        retVal += "<msqrt>\n" + "<mn>" + MathMachine.formattedValue( node.leftChild.getNodeValue() ) + "</mn></msqrt>\n";
                    } else {
                        retVal += "<msqrt>\n" + "<mrow>\n" + presentationNodeProcessor( node.leftChild, leftDepth, showValue, true ) + "</mrow></msqrt>\n";
                    }

                } else if ( node.nodeRole.equals( "e" ) ) {

                    String specialELink = //
                            "<msup><mi><html:a" + htmlLinkControl + "http://en.wikipedia.org/wiki/E_%28mathematical_constant%29\" title=\"2.71828182845904523536...\"><em>e</em></html:a></mi>\n";
                    if ( leftStop &&  ! showValue ) {
                        retVal += specialELink //
                                + "<mi><html:a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachine.createPresentationFile( node.leftChild, "" ) + "\"")//
                                + " title=\"" //
                                + MathMachine.formattedValue( node.leftChild.nodeValue ) //
                                + "\">" + node.leftChild.getNodeNameMathML() //
                                + "</html:a></mi></msup>\n";
                    } else if ( leftStop ) {
                        retVal += specialELink + "<mn>" + MathMachine.formattedValue( node.leftChild.getNodeValue() ) + "</mn></msup>\n";
                    } else {
                        retVal += specialELink + "<mrow>\n" + presentationNodeProcessor( node.leftChild, leftDepth, showValue, false ) + "</mrow></msup>\n";
                    }

                } else if ( node.nodeRole.equals( "log" ) ) {

                    if ( leftStop &&  ! showValue ) {
                        retVal += "<mi>log</mi>\n"//
                                + "<mi><html:a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachine.createPresentationFile( node.leftChild, "" ) + "\"")//
                                + " title=\"" //
                                + MathMachine.formattedValue( node.leftChild.nodeValue ) //
                                + "\">" + node.leftChild.getNodeNameMathML() //
                                + "</html:a></mi>\n";
                    } else if ( leftStop ) {
                        retVal += "<mi>log</mi>\n" + "<mn>" + MathMachine.formattedValue( node.leftChild.getNodeValue() ) + "</mn>\n";
                    } else {
                        retVal += "<mi>log</mi>\n" + "<mrow>\n" + presentationNodeProcessor( node.leftChild, leftDepth, showValue, false ) + "</mrow>\n";
                    }

                } else {

                    String leftParen = "";
                    String rightParen = "";

                    if (  ! suppressParens &&  ! node.nodeRole.equals( "*" ) ) {
                        leftParen = "<mo> ( </mo>";
                        rightParen = "<mo> ) </mo>";
                    }

                    if ( leftStop &&  ! showValue ) {
                        retVal += leftParen//
                                + "<mi><html:a" + (String) ((node.leftChild.leftChild == null) ? "" : htmlLinkControl//
                                + MathMachine.createPresentationFile( node.leftChild, "" ) + "\"")//
                                + " title=\"" //
                                + MathMachine.formattedValue( node.leftChild.nodeValue ) //
                                + "\">" + node.leftChild.getNodeNameMathML()//
                                + "</html:a></mi>\n";
                    } else if ( leftStop ) {
                        retVal += leftParen + "<mn>" + MathMachine.formattedValue( node.leftChild.getNodeValue() ) + "</mn>\n";
                    } else {
                        retVal += leftParen + presentationNodeProcessor( node.leftChild, leftDepth, showValue, false );
                    }

                    retVal += PresentationMathMLConversions.get( node.nodeRole );


                    if ( rightStop &&  ! showValue ) {
                        retVal += "<mi><html:a" + (String) ((node.rightChild.leftChild == null) ? "" : htmlLinkControl //
                                + MathMachine.createPresentationFile( node.rightChild, "" ) + "\"")//
                                + " title=\"" //
                                + MathMachine.formattedValue( node.rightChild.nodeValue ) //
                                + "\">" + node.rightChild.getNodeNameMathML() //
                                + "</html:a></mi>\n" + rightParen;
                    } else if ( rightStop ) {
                        retVal += "<mn>" + MathMachine.formattedValue( node.rightChild.getNodeValue() ) + "</mn>\n" + rightParen;
                    } else {
                        retVal += presentationNodeProcessor( node.rightChild, rightDepth, showValue, false ) + rightParen;
                    }

                }
            }
        }
        return retVal;
    }
}
