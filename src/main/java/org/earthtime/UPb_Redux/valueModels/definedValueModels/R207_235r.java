/*
 * R207_235r.java
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
public class R207_235r extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 8314509640370960894L;
    private final static String NAME = "r207_235r";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb207r;
    private ValueModel molU235s;

    /** Creates a new instance of R207_235r */
    public R207_235r () {
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

        molPb207r = inputValueModels[0];
        molU235s = inputValueModels[1];

        try {
            setValue(molPb207r.getValue().//
                    divide(molU235s.getValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_235r__dMolPb207r = BigDecimal.ONE.divide(molU235s.getValue(), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR207_235r__dMolPb207r", dR207_235r__dMolPb207r );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_235r__dMolPb207r", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_235r__dMolU235s = molPb207r.getValue().negate().//
                    divide(molU235s.getValue().pow( 2 ), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR207_235r__dMolU235s", dR207_235r__dMolU235s );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_235r__dMolU235s", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_235r__dConcU235t = //
                    parDerivTerms.get( "dR207_235r__dMolU235s" ).//
                    multiply( parDerivTerms.get( "dMolU235s__dMolU235t" ).//
                    multiply( parDerivTerms.get( "dMolU235t__dConcU235t" ) ).//
                    add( parDerivTerms.get( "dMolU235s__dMolU238t" ).//
                    multiply( parDerivTerms.get( "dMolU238t__dMolU235t" ).//
                    multiply( parDerivTerms.get( "dMolU235t__dConcU235t" ) ) ) ).//

                    add( parDerivTerms.get( "dMolU235s__dMolU233t" ).//
                    multiply( parDerivTerms.get( "dMolU233t__dMolU235t" ).//
                    multiply( parDerivTerms.get( "dMolU235t__dConcU235t" ) ) ) ) );
            parDerivTerms.put( "dR207_235r__dConcU235t", dR207_235r__dConcU235t );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_235r__dConcU235t", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_235r__dConcPb205t = //
                    parDerivTerms.get( "dR207_235r__dMolPb207r" ).//
                    multiply( parDerivTerms.get( "dMolPb207r__dMolPb205t" ).//
                    multiply( parDerivTerms.get( "dMolPb205t__dConcPb205t" ) ).//

                    add( parDerivTerms.get( "dMolPb207r__dMolPb207b" ).//
                    multiply( parDerivTerms.get( "dMolPb207b__dMolPb205t" ).//
                    multiply( parDerivTerms.get( "dMolPb205t__dConcPb205t" ) ) ) ).//
                    add( parDerivTerms.get( "dMolPb207r__dMolPb207c" ).//
                    multiply( parDerivTerms.get( "dMolPb207c__dMolPb204c" ).//)
                    multiply( parDerivTerms.get( "dMolPb204c__dMolPb204tc" ).//)
                    multiply( parDerivTerms.get( "dMolPb204tc__dMolPb205t" ).//)
                    multiply( parDerivTerms.get( "dMolPb205t__dConcPb205t" ) ) ) ) ) ) );
            parDerivTerms.put( "dR207_235r__dConcPb205t", dR207_235r__dConcPb205t );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_235r__dConcPb205t", BigDecimal.ZERO );
        }
    }


}
