/*
 * R207_206r_Th.java
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
public class R207_206r_Th extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -8086860131648650161L;
    private final static String NAME = "r207_206r_Th";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb207r;
    private ValueModel molPb206r_Th;

    /** Creates a new instance of R207_206r_Th */
    public R207_206r_Th () {
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
        molPb206r_Th = inputValueModels[1];

        try {
            setValue(molPb207r.getValue().//
                    divide(molPb206r_Th.getValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r_Th__dMolPb207r = BigDecimal.ONE.//
                    divide(molPb206r_Th.getValue(), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR207_206r_Th__dMolPb207r", dR207_206r_Th__dMolPb207r );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r_Th__dMolPb207r", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r_Th__dMolPb206r_Th = molPb207r.getValue().//
                    divide(molPb206r_Th.getValue().pow( 2 ), ReduxConstants.mathContext15 ).//
                    negate();
            parDerivTerms.put( "dR207_206r_Th__dMolPb206r_Th", dR207_206r_Th__dMolPb206r_Th );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r_Th__dMolPb206r_Th", BigDecimal.ZERO );
        }

    }
}
