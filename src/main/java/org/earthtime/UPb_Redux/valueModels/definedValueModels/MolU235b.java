/*
 * MolU235b.java
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
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolU235b extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 4701722104957910567L;
    private final static String NAME = "molU235b";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel r238_235b;
    private ValueModel gmol238;
    private ValueModel gmol235;
    private ValueModel uBlankMassInGrams;

    /** Creates a new instance of MolU235b */
    public MolU235b() {
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

        r238_235b = inputValueModels[0];
        gmol238 = inputValueModels[1];
        gmol235 = inputValueModels[2];
        uBlankMassInGrams = inputValueModels[3];

        setValue(BigDecimal.ONE. //
                divide(r238_235b.getValue().//
                multiply(gmol238.getValue()).//
                add(gmol235.getValue()), ReduxConstants.mathContext15).//
                multiply(uBlankMassInGrams.getValue()));

        setValueTree(//
                ExpTreeII.ONE. //
                divide(r238_235b.getValueTree().//
                multiply(gmol238.getValueTree()).//
                add(gmol235.getValueTree())).//
                multiply(uBlankMassInGrams.getValueTree()));


        //System.out.println(getValue().toPlainString() + "\n" + getValueTree().treeToString(1));
        if (getValue().compareTo(getValueTree().getNodeValue()) != 0) {
//            System.out.println(differenceValueCalcs());
        }



        try {
            BigDecimal dMolU235b__dUBlankMassInGrams = BigDecimal.ONE.//
                    divide(gmol235.getValue().//
                    add(gmol238.getValue().//
                    multiply(r238_235b.getValue())), ReduxConstants.mathContext15);
            parDerivTerms.put("dMolU235b__dUBlankMassInGrams", dMolU235b__dUBlankMassInGrams);
        } catch (Exception e) {
            parDerivTerms.put("dMolU235b__dUBlankMassInGrams", BigDecimal.ZERO);
        }
        try {
            BigDecimal dMolU235b__dR238_235b = gmol238.getValue().negate().//
                    multiply(uBlankMassInGrams.getValue()).//
                    divide(gmol235.getValue().//
                    add(gmol238.getValue().//
                    multiply(r238_235b.getValue())).pow(2), ReduxConstants.mathContext15);
            parDerivTerms.put("dMolU235b__dR238_235b", dMolU235b__dR238_235b);
        } catch (Exception e) {
            parDerivTerms.put("dMolU235b__dR238_235b", BigDecimal.ZERO);
        }

    }
}