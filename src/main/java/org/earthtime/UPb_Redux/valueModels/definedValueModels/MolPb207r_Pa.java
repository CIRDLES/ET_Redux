/*
 * MolPb207r_Pa.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolPb207r_Pa extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 5005886211049100035L;
    private final static String NAME = "molPb207r_Pa";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    //private ValueModel ar231_235sample;
    private ValueModel age207_235r_Pa;
    private ValueModel lambda235;
    //private ValueModel lambda231;
    private ValueModel molU235s;

    /** Creates a new instance of MolPb207r_Pa */
    public MolPb207r_Pa () {
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

        age207_235r_Pa = inputValueModels[0];
        lambda235 = inputValueModels[1];
        molU235s = inputValueModels[2];

        BigDecimal expLambda235_Age207_235r_Pa_minus1 =
                new BigDecimal(//
                 //
                 //
                 //
                 + Math.expm1( lambda235.getValue().doubleValue()//
                * age207_235r_Pa.getValue().doubleValue() ), ReduxConstants.mathContext15 //
                );

        // jan 2011 this class was DeltaPb207_Pa
        try {
            setValue(//
                    molU235s.getValue(). //
                    multiply( expLambda235_Age207_235r_Pa_minus1 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            BigDecimal dMolPb207r_Pa__dMolU235s = //
                    expLambda235_Age207_235r_Pa_minus1;
            parDerivTerms.put( "dMolPb207r_Pa__dMolU235s", dMolPb207r_Pa__dMolU235s );
        } catch (Exception e) {
            parDerivTerms.put( "dMolPb207r_Pa__dMolU235s", BigDecimal.ZERO );
        }

        try {
            BigDecimal dMolPb207r_Pa__dLambda235 = //
                    expLambda235_Age207_235r_Pa_minus1.//
                    add( BigDecimal.ONE ).//
                    multiply( molU235s.getValue() ).//
                    multiply( age207_235r_Pa.getValue() );
            parDerivTerms.put( "dMolPb207r_Pa__dLambda235", dMolPb207r_Pa__dLambda235 );
        } catch (Exception e) {
            parDerivTerms.put( "dMolPb207r_Pa__dLambda235", BigDecimal.ZERO );
        }

        try {
            BigDecimal dMolPb207r_Pa__dAge207_235r_Pa = //
                    expLambda235_Age207_235r_Pa_minus1.//
                    add( BigDecimal.ONE ).//
                    multiply( molU235s.getValue() ).//
                    multiply( lambda235.getValue() );
            parDerivTerms.put( "dMolPb207r_Pa__dAge207_235r_Pa", dMolPb207r_Pa__dAge207_235r_Pa );
        } catch (Exception e) {
            parDerivTerms.put( "dMolPb207r_Pa__dAge207_235r_Pa", BigDecimal.ZERO );
        }

    }

}
