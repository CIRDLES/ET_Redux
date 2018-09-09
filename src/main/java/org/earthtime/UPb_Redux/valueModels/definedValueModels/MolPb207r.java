/*
 * MolPb207r.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolPb207r extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID =  6226056899892122405L;
    private final static String NAME = "molPb207r";
    private final static String UNCT_TYPE = "ABS";

    // Instance variables
    private ValueModel r207_205m;
    private ValueModel r207_205t;
    private ValueModel molPb205t;
    private ValueModel alphaPb;
    private ValueModel molPb207b;
    private ValueModel molPb207c;

    /** Creates a new instance of MolPb207r */
    public MolPb207r() {
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

        r207_205m = inputValueModels[0];
        r207_205t = inputValueModels[1];
        molPb205t = inputValueModels[2];
        alphaPb = inputValueModels[3];
        molPb207b = inputValueModels[4];
        molPb207c = inputValueModels[5];

        try {
            setValue(//
                    molPb205t.getValue().
                    multiply( r207_205m.getValue().
                    multiply( alphaPb.getValue().multiply( new BigDecimal( "2.0" ) ).add( BigDecimal.ONE ) ).
                    subtract( r207_205t.getValue() ) ).
                    subtract( molPb207b.getValue() ).
                    subtract( molPb207c.getValue() ) );
        } catch (Exception e) {
        }
        try {
            BigDecimal dMolPb207r__dMolPb205t = //
                    r207_205m.getValue()//
                    .multiply(BigDecimal.ONE.add(alphaPb.getValue().multiply(new BigDecimal("2.0"))))//
                    .subtract(r207_205t.getValue());
            parDerivTerms.put("dMolPb207r__dMolPb205t", dMolPb207r__dMolPb205t);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb207r__dMolPb205t", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolPb207r__dR207_205m = //
                    molPb205t.getValue().//
                    multiply(BigDecimal.ONE.add(alphaPb.getValue().multiply(new BigDecimal("2.0"))));
            parDerivTerms.put("dMolPb207r__dR207_205m", dMolPb207r__dR207_205m);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb207r__dR207_205m", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolPb207r__dAlphaPb = //
                    molPb205t.getValue().//
                    multiply(r207_205m.getValue()).//
                    multiply(new BigDecimal("2.0"));
            parDerivTerms.put("dMolPb207r__dAlphaPb", dMolPb207r__dAlphaPb);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb207r__dAlphaPb", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolPb207r__dR207_205t = //
                    molPb205t.getValue().negate();
            parDerivTerms.put("dMolPb207r__dR207_205t", dMolPb207r__dR207_205t);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb207r__dR207_205t", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolPb207r__dMolPb207b = //
                    BigDecimal.ONE.negate();
            parDerivTerms.put("dMolPb207r__dMolPb207b", dMolPb207r__dMolPb207b);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb207r__dMolPb207b", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolPb207r__dMolPb207c = //
                    BigDecimal.ONE.negate();
            parDerivTerms.put("dMolPb207r__dMolPb207c", dMolPb207r__dMolPb207c);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb207r__dMolPb207c", BigDecimal.ZERO);
        }

    }

}