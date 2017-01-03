/*
 * R207_206r_ThPa.java
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
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class R207_206r_ThPa extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -5838560929000160442L;
    private final static String NAME = "r207_206r_ThPa";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb206r_Th;
    private ValueModel molPb207r_Pa;

    /** Creates a new instance of R207_206r_ThPa */
    public R207_206r_ThPa () {
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

        molPb206r_Th = inputValueModels[0];
        molPb207r_Pa = inputValueModels[1];

        // Jan 2011
        try {
            setValue(molPb207r_Pa.getValue().//
                    divide(molPb206r_Th.getValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
        }

        try {
            BigDecimal dR207_206r_ThPa__dMolPb207r_Pa =BigDecimal.ONE.//
                    divide(molPb206r_Th.getValue(), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR207_206r_ThPa__dMolPb207r_Pa", dR207_206r_ThPa__dMolPb207r_Pa );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r_ThPa__dMolPb207r_Pa", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r_ThPa__dMolPb206r_Th = molPb207r_Pa.getValue().negate().//
                    divide(molPb206r_Th.getValue().pow( 2 ), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR207_206r_ThPa__dMolPb206r_Th", dR207_206r_ThPa__dMolPb206r_Th );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r_ThPa__dMolPb206r_Th", BigDecimal.ZERO );
        }

    }
}
