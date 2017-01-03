/*
 * AlphaPb.java
 *
 * Created on December 11, 2008
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
public class AlphaPb extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -5899185940656719957L;
    private final static String NAME = AnalysisMeasures.alphaPb.getName();
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private BigDecimal fractionMeanAlphaPb;
    private ValueModel r202_205m;
    private ValueModel r202_205t;

    /**
     * Creates a new instance of AlphaPb
     */
    public AlphaPb() {
        super(NAME, UNCT_TYPE);
        fractionMeanAlphaPb = BigDecimal.ZERO;
    }

    /**
     *
     * @param fractionMeanAlphaPb
     */
    public AlphaPb(BigDecimal fractionMeanAlphaPb) {
        this();
        this.fractionMeanAlphaPb = fractionMeanAlphaPb;
    }

    // July 2011 used at end of data reduction to reset AlphaPb per special case below where set to  0
    /**
     *
     */
    public void restoreValueFromFractionMeanAlphaPb() {
        if (getValue().compareTo(BigDecimal.ZERO) == 0) {
            setValue(fractionMeanAlphaPb);
            setValueTree(new ExpTreeII(fractionMeanAlphaPb));
        }
    }

    /**
     *
     * @param inputValueModels
     * @param parDerivTerms
     */
    @Override
    public void calculateValue(
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms) {

        r202_205m = inputValueModels[0];
        r202_205t = inputValueModels[1];

        // perform analysis to determine which of the four techniques applies
        if (fractionMeanAlphaPb.compareTo(BigDecimal.ZERO) == 0) {
            if (r202_205m.getValue().compareTo(BigDecimal.ZERO) == 1) {
                // case 2
                CalculateValueI(parDerivTerms);
            } else {
                // case 3
                // moved back to reducer to support Kwiki sensitivity  use copyValuesFrom(alphaPbModel);
            }
        } else {
            // case 1 value stays at 0
            // sept 19 2010 email Form Noah re: special case
            value = BigDecimal.ZERO;
            setValueTree(ExpTreeII.ZERO);
            // alphaPb needs to be restored after reduction
            setOneSigma(BigDecimal.ZERO);

            try {
                BigDecimal dAlphaPb__dR202_205t =BigDecimal.ONE.negate().//
                        divide(new BigDecimal("3.0", ReduxConstants.mathContext15).//
                                multiply(r202_205m.getValue()), ReduxConstants.mathContext15);
                parDerivTerms.put("dAlphaPb__dR202_205t", dAlphaPb__dR202_205t);
            } catch (Exception e) {
                parDerivTerms.put("dAlphaPb__dR202_205t", BigDecimal.ZERO);
            }
        }
    }

    private void CalculateValueI(ConcurrentMap<String, BigDecimal> parDerivTerms) {

        try {
            setValue(BigDecimal.ONE.//
                    divide(new BigDecimal("3.0", ReduxConstants.mathContext15), ReduxConstants.mathContext15).//
                    multiply(BigDecimal.ONE.//
                            subtract(r202_205t.getValue().//
                                    divide(r202_205m.getValue(), ReduxConstants.mathContext15))));
        } catch (Exception e) {
            value = BigDecimal.ZERO;
        }

        try {
            setValueTree(//
                    ExpTreeII.ONE.//
                    divide(ExpTreeII.THREE).//
                    multiply(ExpTreeII.ONE.//
                            subtract(r202_205t.getValueTree().//
                                    divide(r202_205m.getValueTree()))));
        } catch (Exception e) {
            setValueTree(ExpTreeII.ZERO);
        }
////              System.out.println( differenceValueCalcs() );

        try {
            BigDecimal dAlphaPb__dR202_205t =BigDecimal.ONE.negate().//
                    divide(new BigDecimal("3.0", ReduxConstants.mathContext15).//
                            multiply(r202_205m.getValue()), ReduxConstants.mathContext15);
            parDerivTerms.put("dAlphaPb__dR202_205t", dAlphaPb__dR202_205t);
        } catch (Exception e) {
            parDerivTerms.put("dAlphaPb__dR202_205t", BigDecimal.ZERO);
        }

        try {
            BigDecimal dAlphaPb__dR202_205m =r202_205t.getValue().//
                    divide(new BigDecimal("3.0", ReduxConstants.mathContext15).//
                            multiply(r202_205m.getValue().pow(2)), ReduxConstants.mathContext15);
            parDerivTerms.put("dAlphaPb__dR202_205m", dAlphaPb__dR202_205m);
        } catch (Exception e) {
            parDerivTerms.put("dAlphaPb__dR202_205m", BigDecimal.ZERO);
        }
    }

    /**
     * @return the fractionMeanAlphaPb
     */
    public BigDecimal getFractionMeanAlphaPb() {
        return fractionMeanAlphaPb;
    }

    /**
     * @param fractionMeanAlphaPb the fractionMeanAlphaPb to set
     */
    public void setFractionMeanAlphaPb(BigDecimal fractionMeanAlphaPb) {
        this.fractionMeanAlphaPb = fractionMeanAlphaPb;
    }

}
