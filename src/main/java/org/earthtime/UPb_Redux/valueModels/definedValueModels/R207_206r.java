/*
 * R207_206r.java
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
public class R207_206r extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -6648644903106722085L;
    private final static String NAME = "r207_206r";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb207r;
    private ValueModel molPb206r;
  
    /** Creates a new instance of R207_206r */
    public R207_206r () {
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
        molPb206r = inputValueModels[1];
        
        try {
            setValue(molPb207r.getValue(). //
                    divide(molPb206r.getValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r__dMolPb207r = BigDecimal.ONE.//
                    divide(molPb206r.getValue(), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR207_206r__dMolPb207r", dR207_206r__dMolPb207r );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r__dMolPb207r", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r__dMolPb206r = //
                    molPb207r.getValue().negate().//
                    multiply( parDerivTerms.get( "dR207_206r__dMolPb207r" ).pow( 2 ) );
            parDerivTerms.put( "dR207_206r__dMolPb206r", dR207_206r__dMolPb206r );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r__dMolPb206r", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r__dR206_205m = //
                    parDerivTerms.get( "dR207_206r__dMolPb206r" ).//
                    multiply( parDerivTerms.get( "dMolPb206r__dR206_205m" ) );
            parDerivTerms.put( "dR207_206r__dR206_205m", dR207_206r__dR206_205m );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r__dR206_205m", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r__dR207_205m = //
                    parDerivTerms.get( "dR207_206r__dMolPb207r" ).//
                    multiply( parDerivTerms.get( "dMolPb207r__dR207_205m" ) );
            parDerivTerms.put( "dR207_206r__dR207_205m", dR207_206r__dR207_205m );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r__dR207_205m", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r__dR206_205t = //
                    parDerivTerms.get( "dR207_206r__dMolPb206r" ).//
                    multiply( parDerivTerms.get( "dMolPb206r__dR206_205t" ) );
            parDerivTerms.put( "dR207_206r__dR206_205t", dR207_206r__dR206_205t );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r__dR206_205t", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r__dR207_205t = //
                    parDerivTerms.get( "dR207_206r__dMolPb207r" ).//
                    multiply( parDerivTerms.get( "dMolPb207r__dR207_205t" ) );
            parDerivTerms.put( "dR207_206r__dR207_205t", dR207_206r__dR207_205t );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r__dR207_205t", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r__dR206_204b = //
                    parDerivTerms.get( "dR207_206r__dMolPb206r" ).//
                    multiply( parDerivTerms.get( "dMolPb206r__dMolPb206b" ).//
                    multiply( parDerivTerms.get( "dMolPb206b__dR206_204b" ) ) );
            parDerivTerms.put( "dR207_206r__dR206_204b", dR207_206r__dR206_204b );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r__dR206_204b", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r__dR207_204b = //
                    parDerivTerms.get( "dR207_206r__dMolPb207r" ).//
                    multiply( parDerivTerms.get( "dMolPb207r__dMolPb207b" ).//
                    multiply( parDerivTerms.get( "dMolPb207b__dR207_204b" ) ) );
            parDerivTerms.put( "dR207_206r__dR207_204b", dR207_206r__dR207_204b );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r__dR207_204b", BigDecimal.ZERO );
        }
    }


}
