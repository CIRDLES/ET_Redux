/*
 * R207_206r_Pa.java
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
public class R207_206r_Pa extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 1736423873087689778L;
    private final static String NAME = "r207_206r_Pa";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb207r_Pa;
    private ValueModel molPb206r;

    /** Creates a new instance of R207_206r_Pa */
    public R207_206r_Pa () {
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

        molPb207r_Pa = inputValueModels[0];
        molPb206r = inputValueModels[1];

        // jan 2011
        try {
            setValue(molPb207r_Pa.getValue().//
                    divide(molPb206r.getValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r_Pa__dMolPb207r_Pa = BigDecimal.ONE.//
                    divide(molPb206r.getValue(), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR207_206r_Pa__dMolPb207r_Pa", dR207_206r_Pa__dMolPb207r_Pa );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r_Pa__dMolPb207r_Pa", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR207_206r_Pa__dMolPb206r = molPb207r_Pa.getValue().negate().//
                    divide(molPb206r.getValue().pow( 2 ), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR207_206r_Pa__dMolPb206r", dR207_206r_Pa__dMolPb206r );
        } catch (Exception e) {
            parDerivTerms.put( "dR207_206r_Pa__dMolPb206r", BigDecimal.ZERO );
        }


    }

}
