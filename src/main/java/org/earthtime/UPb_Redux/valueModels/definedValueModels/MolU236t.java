/*
 * MolU236t.java
 *
 * Created Jan 2011
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolU236t extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = 3315502700399987626L;
    private final static String NAME = "molU236t";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel concU236t;
    private ValueModel tracerMassInGrams;

    /** Creates a new instance of MolU235t */
    public MolU236t () {
        super( NAME, UNCT_TYPE );
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

        concU236t = inputValueModels[0];
        tracerMassInGrams = inputValueModels[1];

        try {
            setValue(//
                    concU236t.getValue()//
                    .multiply( tracerMassInGrams.getValue() ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO);
        }

        try {
            BigDecimal dMolU236t__dConcU236t = //
                    tracerMassInGrams.getValue();
            parDerivTerms.put( "dMolU236t__dConcU236t", dMolU236t__dConcU236t );
        } catch (Exception e) {
            parDerivTerms.put( "dMolU236t__dConcU236t", BigDecimal.ZERO );
        }

        try {
            BigDecimal dMolU236t__dTracerMassInGrams = //
                    concU236t.getValue();
            parDerivTerms.put( "dMolU236t__dTracerMassInGrams", dMolU236t__dTracerMassInGrams );
        } catch (Exception e) {
            parDerivTerms.put( "dMolU236t__dTracerMassInGrams", BigDecimal.ZERO );
        }
    }
}
