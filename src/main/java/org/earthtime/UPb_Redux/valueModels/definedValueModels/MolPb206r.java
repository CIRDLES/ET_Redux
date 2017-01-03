/*
 * MolPb206r.java
 *
 * Created on Dec 12, 2008
 *
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
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolPb206r extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = 6249520230202158563L;
    private final static String NAME = "molPb206r";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
//    private ValueModel r206_204m;
    private ValueModel r206_205m;
//    private ValueModel r204_205m;
    private ValueModel r206_205t;
//    private ValueModel r204_205t;
    private ValueModel molPb205t;
    private ValueModel alphaPb;
    private ValueModel molPb206b;
    private ValueModel molPb206c;
//    private ValueModel molPb204b;

    /**
     * Creates a new instance of MolPb206r
     */
    public MolPb206r() {
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

        r206_205m = inputValueModels[0];
        r206_205t = inputValueModels[1];
        molPb205t = inputValueModels[2];
        alphaPb = inputValueModels[3];
        molPb206b = inputValueModels[4];
        molPb206c = inputValueModels[5];

        try {
            setValue(//
                    molPb205t.getValue().
                    multiply(r206_205m.getValue().
                            multiply(alphaPb.getValue().add(BigDecimal.ONE)).
                            subtract(r206_205t.getValue())).
                    subtract(molPb206b.getValue()).
                    subtract(molPb206c.getValue()));

            setValueTree(//
                    molPb205t.getValueTree().
                    multiply(r206_205m.getValueTree().
                            multiply(alphaPb.getValueTree().add(ExpTreeII.ONE)).
                            subtract(r206_205t.getValueTree())).
                    subtract(molPb206b.getValueTree()).
                    subtract(molPb206c.getValueTree()));
        } catch (Exception e) {
            value = BigDecimal.ZERO;
            setValueTree(ExpTreeII.ZERO);
        }

        try {
            BigDecimal dMolPb206r__dMolPb205t = //
                    r206_205m.getValue()//
                    .multiply(BigDecimal.ONE.add(alphaPb.getValue()))//
                    .subtract(r206_205t.getValue());
            parDerivTerms.put("dMolPb206r__dMolPb205t", dMolPb206r__dMolPb205t);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb206r__dMolPb205t", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolPb206r__dR206_205m = //
                    molPb205t.getValue().//
                    multiply(BigDecimal.ONE.add(alphaPb.getValue()));
            parDerivTerms.put("dMolPb206r__dR206_205m", dMolPb206r__dR206_205m);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb206r__dR206_205m", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolPb206r__dAlphaPb = //
                    molPb205t.getValue().multiply(r206_205m.getValue());
            parDerivTerms.put("dMolPb206r__dAlphaPb", dMolPb206r__dAlphaPb);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb206r__dAlphaPb", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolPb206r__dR206_205t = //
                    molPb205t.getValue().negate();
            parDerivTerms.put("dMolPb206r__dR206_205t", dMolPb206r__dR206_205t);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb206r__dR206_205t", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolPb206r__dMolPb206b = //
                    BigDecimal.ONE.negate();
            parDerivTerms.put("dMolPb206r__dMolPb206b", dMolPb206r__dMolPb206b);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb206r__dMolPb206b", BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolPb206r__dMolPb206c = //
                    BigDecimal.ONE.negate();
            parDerivTerms.put("dMolPb206r__dMolPb206c", dMolPb206r__dMolPb206c);
        } catch (Exception e) {
            parDerivTerms.put("dMolPb206r__dMolPb206c", BigDecimal.ZERO);
        }

    }
}
