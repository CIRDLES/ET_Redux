/*
 * MolsU.java
 *
 * Created on Dec 12, 2008
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
package org.earthtime.UPb_Redux.valueModels.definedValueModels;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentMap;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolsU extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID =  -3850872903089836169L;
    private final static String NAME = "molsU";
    private final static String UNCT_TYPE = "ABS";

    // Instance variables
    private ValueModel molU235b;
    private ValueModel molU238b;
    private ValueModel molU235s;
    private ValueModel molU238s;
    private ValueModel molU233t;
    private ValueModel molU235t;
    private ValueModel molU238t;
    private ValueModel r238_233m;
    private ValueModel r238_235m;
    private ValueModel r238_235s;

    /** Creates a new instance of MolsU */
    public MolsU() {
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

        molU235b = inputValueModels[0];
        molU238b = inputValueModels[1];
        molU235s = inputValueModels[2];
        molU238s = inputValueModels[3];
        molU233t = inputValueModels[4];
        molU235t = inputValueModels[5];
        molU238t = inputValueModels[6];
        r238_233m = inputValueModels[7];
        r238_235m = inputValueModels[8];
        r238_235s = inputValueModels[9];

        setValue(molU238s.getValue().add(molU235s.getValue()));

        BigDecimal BD2 = new BigDecimal("2.0");
        BigDecimal BD3 = new BigDecimal("3.0");
        BigDecimal BD5 = new BigDecimal("5.0");


        try {
            BigDecimal dMolsU__dMolU235b = BD5.//
                    multiply(r238_235m.getValue()).//
                    multiply(BigDecimal.ONE.//
                    add(r238_235s.getValue())).//
                    divide(BD2.//
                    multiply(r238_235s.getValue()).//
                    subtract(BD5.//
                    multiply(r238_235m.getValue())), ReduxConstants.mathContext15);
            parDerivTerms.put("dMolsU__dMolU235b", dMolsU__dMolU235b);
            parDerivTerms.put("dMolsU__dMolU235t", dMolsU__dMolU235b);
        } catch (Exception e) {
            parDerivTerms.put("dMolsU__dMolU235b", BigDecimal.ZERO);
            parDerivTerms.put("dMolsU__dMolU235t", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolsU__dMolU238b = BD2.//
                    multiply(BigDecimal.ONE.//
                    add(r238_235s.getValue())).//
                    divide(BD5.//
                    multiply(r238_235m.getValue()).//
                    subtract(BD2.//
                    multiply(r238_235s.getValue())), ReduxConstants.mathContext15);
            parDerivTerms.put("dMolsU__dMolU238b", dMolsU__dMolU238b);
            parDerivTerms.put("dMolsU__dMolU238t", dMolsU__dMolU238b);
        } catch (Exception e) {
            parDerivTerms.put("dMolsU__dMolU238b", BigDecimal.ZERO);
            parDerivTerms.put("dMolsU__dMolU238t", BigDecimal.ZERO);
        }


        try {
            BigDecimal dMolsU__dMolU233t = BD3.//
                    multiply(r238_233m.getValue()).//
                    multiply(BigDecimal.ONE.//
                    add(r238_235s.getValue())).//
                    divide(BD5.//
                    multiply(r238_233m.getValue()).//
                    subtract(BD2.//
                    multiply(r238_235s.getValue())), ReduxConstants.mathContext15);
            parDerivTerms.put("dMolsU__dMolU233t", dMolsU__dMolU233t);
        } catch (Exception e) {
            parDerivTerms.put("dMolsU__dMolU233t", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolsU__dR238_233m = BD3.//
                    multiply(molU233t.getValue()).//
                    multiply(BigDecimal.ONE.//
                    add(r238_235s.getValue())).//
                    divide(BD5.//
                    multiply(r238_235m.getValue()).//
                    subtract(BD2.//
                    multiply(r238_235s.getValue())), ReduxConstants.mathContext15);
            parDerivTerms.put("dMolsU__dR238_233m", dMolsU__dR238_233m);
        } catch (Exception e) {
            parDerivTerms.put("dMolsU__dR238_233m", BigDecimal.ZERO);
        }


        try {
            BigDecimal dMolsU__dR238_235m = BD5.//
                    add(BD5.//
                    multiply(r238_235s.getValue())).//

                    multiply(BD2.//
                    multiply(r238_235s.getValue().//
                    multiply(molU235b.getValue().//
                    add(molU235t.getValue()))).//
                    subtract(BD2.//
                    multiply(molU238b.getValue().//
                    add(molU238t.getValue()))).//
                    subtract(BD3.//
                    multiply(molU233t.getValue().//
                    multiply(r238_233m.getValue())))).//

                    divide(BD5.//
                    multiply(r238_235m.getValue()).//
                    subtract(BD2.//
                    multiply(r238_235s.getValue())).pow(2), ReduxConstants.mathContext15);
            parDerivTerms.put("dMolsU__dR238_235m", dMolsU__dR238_235m);
        } catch (Exception e) {
            parDerivTerms.put("dMolsU__dR238_235m", BigDecimal.ZERO);
        }

        parDerivTerms.put("dMolsU__dMolU235s", BigDecimal.ONE);

        parDerivTerms.put("dMolsU__dMolU238s", BigDecimal.ONE);

    }

}