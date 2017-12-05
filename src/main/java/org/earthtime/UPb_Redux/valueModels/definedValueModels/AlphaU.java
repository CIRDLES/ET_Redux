/*
 * AlphaU.java
 *
 * Created on Dec 12, 2008
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
package org.earthtime.UPb_Redux.valueModels.definedValueModels;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentMap;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.AnalysisMeasures;

/**
 *
 * @author James F. Bowring
 */
public class AlphaU extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 4525118600008541091L;
    private final static String NAME = AnalysisMeasures.alphaU.getName();
    private final static String UNCT_TYPE = "ABS";
    // instance variables
    private BigDecimal fractionMeanAlphaU;
    private boolean doubleSpike;
    private boolean modelCopy;
    private ValueModel r233_235oc;
    private ValueModel r238_235oc;
    private ValueModel r238_235s;
    private ValueModel molU233t;
    private ValueModel molU235b;
    private ValueModel molU235t;
    private ValueModel molU238b;
    private ValueModel molU238t;
    private ValueModel molU235s;
    // not double spike i.e. metal ?????
    private ValueModel r233_236t;
    private ValueModel r233_236oc;

    /** Creates a new instance of AlphaU */
    public AlphaU () {
        super( NAME, UNCT_TYPE );
        doubleSpike = false;
        modelCopy = false;
    }

    /**
     * 
     * @param fractionMeanAlphaU
     */
    public AlphaU ( BigDecimal fractionMeanAlphaU ) {
        this();
        this.fractionMeanAlphaU = fractionMeanAlphaU;
    }

    /**
     * 
     * @param inputValueModels
     * @param parDerivTerms
     */
    @Override
    public void calculateValue (
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms ) {

        if (  ! isModelCopy() ) {
            // aug 2010 added in capturing imported piece-wise fractionation correction
            if ( fractionMeanAlphaU.compareTo( BigDecimal.ZERO ) == 0 ) {
                if ( isDoubleSpike() ) {
                    r233_235oc = inputValueModels[0];
                    r238_235oc = inputValueModels[1];
                    r238_235s = inputValueModels[2];
                    molU233t = inputValueModels[3];
                    molU235b = inputValueModels[4];
                    molU235t = inputValueModels[5];
                    molU238b = inputValueModels[6];
                    molU238t = inputValueModels[7];
                    molU235s = inputValueModels[8];

                    BigDecimal Term1 =
                            r238_235s.getValue().
                            multiply( molU233t.getValue().//
                            subtract( r233_235oc.getValue().//
                            multiply( molU235b.getValue().//
                            add( molU235t.getValue() ) ) ) );

                    BigDecimal Term2 =
                            r238_235oc.getValue().
                            multiply( molU233t.getValue() );

                    BigDecimal Term3 =
                            r233_235oc.getValue().//
                            multiply( molU238b.getValue().//
                            add( molU238t.getValue() ) );

                    BigDecimal Term4 =
                            new BigDecimal( "3.0", ReduxConstants.mathContext15 ).//
                            multiply( r238_235oc.getValue().
                            multiply( molU233t.getValue() ) );

                    BigDecimal Term5 =
                            new BigDecimal( "2.0", ReduxConstants.mathContext15 ).//
                            multiply( r233_235oc.getValue() ).
                            multiply( molU238b.getValue().
                            add( molU238t.getValue().
                            subtract( r238_235s.getValue().
                            multiply( molU235b.getValue().
                            add( molU235t.getValue() ) ) ) ) );

                    BigDecimal num = Term1.subtract( Term2 ).add( Term3 );
                    BigDecimal denominator = Term4.add( Term5 );

                    if ( denominator.compareTo( BigDecimal.ZERO ) == 0 ) {
                        setValue( BigDecimal.ZERO );
                    } else {
                        setValue(num.divide(denominator, ReduxConstants.mathContext15 ) );
                    }


                    // replication using ExpTreeII
                    ExpTreeII bdtTerm1 =
                            r238_235s.getValueTree().
                            multiply( molU233t.getValueTree().//
                            subtract( r233_235oc.getValueTree().//
                            multiply( molU235b.getValueTree().//
                            add( molU235t.getValueTree() ) ) ) );
                    bdtTerm1.setNodeName( "Term1" );

                    ExpTreeII bdtTerm2 =
                            r238_235oc.getValueTree().
                            multiply( molU233t.getValueTree() );
                    bdtTerm2.setNodeName( "Term2" );

                    ExpTreeII bdtTerm3 =
                            r233_235oc.getValueTree().//
                            multiply( molU238b.getValueTree().//
                            add( molU238t.getValueTree() ) );
                    bdtTerm3.setNodeName( "Term3" );

                    ExpTreeII bdtTerm4 =
                            ExpTreeII.THREE.//
                            multiply( r238_235oc.getValueTree().
                            multiply( molU233t.getValueTree() ) );
                    bdtTerm4.setNodeName( "Term4" );

                    ExpTreeII bdtTerm5 =
                            ExpTreeII.TWO.//
                            multiply( r233_235oc.getValueTree() ).
                            multiply( molU238b.getValueTree().
                            add( molU238t.getValueTree().
                            subtract( r238_235s.getValueTree().
                            multiply( molU235b.getValueTree().
                            add( molU235t.getValueTree() ) ) ) ) );
                    bdtTerm5.setNodeName( "Term5" );


                    try {
                        setValueTree(//
                                bdtTerm1.//
                                subtract( bdtTerm2 ).//
                                add( bdtTerm3 ).//
                                divide( bdtTerm4.//
                                add( bdtTerm5 ) ) );
                    } catch (Exception e) {
                        setValueTree(ExpTreeII.ZERO);
                    }



                    // added in March 2009
                    try {
                        BigDecimal dAlphaU__dMolU233t = BigDecimal.ONE.negate().//
                                divide(r233_235oc.getValue().//
                                multiply( molU235b.getValue().//
                                add( molU235s.getValue().//
                                add( molU235t.getValue() ) ) ), ReduxConstants.mathContext15 );
                        parDerivTerms.put( "dAlphaU__dMolU233t", dAlphaU__dMolU233t );
                    } catch (Exception e) {
                        parDerivTerms.put( "dAlphaU__dMolU233t", BigDecimal.ZERO );
                    }

                    try {
                        BigDecimal dAlphaU__dR233_235oc = molU233t.getValue().//
                                divide(new BigDecimal( 2., ReduxConstants.mathContext15 ).//
                                multiply( r233_235oc.getValue().pow( 2 ) ).//
                                multiply( molU235b.getValue().//
                                add( molU235s.getValue().//
                                add( molU235t.getValue() ) ) ), ReduxConstants.mathContext15 );
                        parDerivTerms.put( "dAlphaU__dR233_235oc", dAlphaU__dR233_235oc );
                    } catch (Exception e) {
                        parDerivTerms.put( "dAlphaU__dR233_235oc", BigDecimal.ZERO );
                    }

                    try {
                        BigDecimal dAlphaU__dMolU235s = molU233t.getValue().//
                                divide(new BigDecimal( 2.0, ReduxConstants.mathContext15 ).//
                                multiply( r233_235oc.getValue() ).//
                                multiply( molU235b.getValue().//
                                add( molU235s.getValue().//
                                add( molU235t.getValue() ) ) ), ReduxConstants.mathContext15 );
                        parDerivTerms.put( "dAlphaU__dMolU235s", dAlphaU__dMolU235s );
                        parDerivTerms.put( "dAlphaU__dMolU235t", dAlphaU__dMolU235s );
                        parDerivTerms.put( "dAlphaU__dMolU235b", dAlphaU__dMolU235s );
                    } catch (Exception e) {
                        parDerivTerms.put( "dAlphaU__dMolU235s", BigDecimal.ZERO );
                        parDerivTerms.put( "dAlphaU__dMolU235t", BigDecimal.ZERO );
                        parDerivTerms.put( "dAlphaU__dMolU235b", BigDecimal.ZERO );
                    }


                } else {
                    // is not a double spike
                    // Calculate AlphaU_233_236 Tracer As Metal

                    r233_236t = inputValueModels[0];
                    r233_236oc = inputValueModels[1];

                    try {
                        setValue(BigDecimal.ONE.//
                                divide(new BigDecimal( "3.0", ReduxConstants.mathContext15 ), ReduxConstants.mathContext15 ).
                                multiply(BigDecimal.ONE.subtract(r233_236t.getValue().//
                                divide(r233_236oc.getValue(), ReduxConstants.mathContext15 ) ) ) );
                    } catch (Exception e) {
                    }

                    try {
                        setValueTree(//
                                ExpTreeII.ONE.//
                                divide( ExpTreeII.THREE).
                                multiply( ExpTreeII.ONE.subtract( r233_236t.getValueTree().//
                                divide( r233_236oc.getValueTree()) ) ) );
                    } catch (Exception e) {
                    }

                    try {
                        BigDecimal dAlphaU__dR233_236t = BigDecimal.ONE.negate().//
                                divide(new BigDecimal( "3.0", ReduxConstants.mathContext15 ).//
                                multiply( r233_236oc.getValue() ), ReduxConstants.mathContext15 );
                        parDerivTerms.put( "dAlphaU__dR233_236t", dAlphaU__dR233_236t );
                    } catch (Exception e) {
                        parDerivTerms.put( "dAlphaU__dR233_236t", BigDecimal.ZERO );
                    }

                    try {
                        BigDecimal dAlphaU__dR233_236oc = r233_236t.getValue().//
                                divide(new BigDecimal( "3.0", ReduxConstants.mathContext15 ).//
                                multiply( r233_236oc.getValue().pow( 2 ) ), ReduxConstants.mathContext15 );
                        parDerivTerms.put( "dAlphaU__dR233_236oc", dAlphaU__dR233_236oc );
                    } catch (Exception e) {
                        parDerivTerms.put( "dAlphaU__dR233_236oc", BigDecimal.ZERO );
                    }
                }
            } else {
                // fractionation corrected from tripoli
                setValue( fractionMeanAlphaU );
                setValueTree( new ExpTreeII(fractionMeanAlphaU ));
                setOneSigma( BigDecimal.ZERO );
            }
        }// otherwise this was a copy so we do nothing

    }

    /**
     * @return the doubleSpike
     */
    public boolean isDoubleSpike () {
        return doubleSpike;
    }

    /**
     * @param zircon 
     */
    public void setDoubleSpike ( boolean zircon ) {
        this.doubleSpike = zircon;
    }

    /**
     * @return the modelCopy
     */
    public boolean isModelCopy () {
        return modelCopy;
    }

    /**
     * @param modelCopy the modelCopy to set
     */
    public void setModelCopy ( boolean modelCopy ) {
        this.modelCopy = modelCopy;
    }

    /**
     * @return the fractionMeanAlphaU
     */
    public BigDecimal getFractionMeanAlphaU () {
        return fractionMeanAlphaU;
    }
}
