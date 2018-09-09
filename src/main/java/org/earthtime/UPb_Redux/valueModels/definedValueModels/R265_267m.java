/*
 * R270_267m.java
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
public class R265_267m extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 6672361878564816125L;
    private final static String NAME = AnalysisMeasures.r265_267m.getName();
    private final static String UNCT_TYPE = "ABS";
    // instance variables
    private ValueModel r18O_16O;
    private ValueModel r233_235m;

    /** Creates a new instance of R265_267m */
    public R265_267m() {
        super(NAME, UNCT_TYPE);
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

        r18O_16O = inputValueModels[0];
        r233_235m = inputValueModels[1];

        try {
            setValue(r233_235m.getValue().//
                    divide(BigDecimal.ONE.//
                    add(new BigDecimal(2.0).//
                    multiply(r18O_16O.getValue().//
                    multiply(r233_235m.getValue()))), ReduxConstants.mathContext15));
        } catch (Exception e) {
        }

        try {

//            ExpTreeII term2 = r18O_16O.getValueTree().//
//                    multiply(r233_235m.getValueTree());
//            term2.setNodeName("term2");
//
//            ExpTreeII denominator = ExpTreeII.ONE.//
//                    add(new ExpTreeII(2.0).//
//                    multiply(term2));
//            denominator.setNodeName("denominator");
//
//            setValueTree(//
//                    r233_235m.getValueTree().//
//                    divide(denominator));


            setValueTree(//
                    r233_235m.getValueTree().//
                    divide(ExpTreeII.ONE.//
                    add(new ExpTreeII(2.0).//
                    multiply(r18O_16O.getValueTree().//
                    multiply(r233_235m.getValueTree())))));

        } catch (Exception e) {
        }

 //       System.out.println(getName() + "  " + getValue().toPlainString() + "\n" + getValueTree().treeToLaTeX(1, false));
        if (getValue().compareTo(getValueTree().getNodeValue()) != 0) {
//            System.out.println( differenceValueCalcs() );
        }

    }

    /**
     * 
     * @param tracerUncertaintiesOn
     * @param lambdaUncertaintiesOn
     * @param parDerivTerms
     * @param coVariances
     */
    public void calculateOneSigma(
            boolean tracerUncertaintiesOn, boolean lambdaUncertaintiesOn,
            ConcurrentMap<String, BigDecimal> parDerivTerms, ConcurrentMap<String, BigDecimal> coVariances) {

        try {
            setOneSigma(r233_235m.getOneSigmaAbs().//
                    divide(BigDecimal.ONE.//
                    add(new BigDecimal(2.0).//
                    multiply(r18O_16O.getValue().//
                    multiply(r233_235m.getValue()))), ReduxConstants.mathContext15));

        } catch (Exception e) {
            setOneSigma(BigDecimal.ZERO);
        }

        // align this calculated measured ratio with the others
        toggleUncertaintyType();

    }
}
