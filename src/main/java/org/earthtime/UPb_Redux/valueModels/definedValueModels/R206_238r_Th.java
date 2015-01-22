/*
 * R206_238r_Th.java
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
public class R206_238r_Th extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -5579642295723086948L;
    private final static String NAME = "r206_238r_Th";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb206r_Th;
    private ValueModel molU238s;

    /** Creates a new instance of R206_238r_Th */
    public R206_238r_Th () {
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
        molU238s = inputValueModels[1];

        try {
            setValue(molPb206r_Th.getValue().//
                    divide(molU238s.getValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            BigDecimal dR206_238r_Th__dMolPb206r_Th =BigDecimal.ONE.//
                    divide(molU238s.getValue(), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR206_238r_Th__dMolPb206r_Th", dR206_238r_Th__dMolPb206r_Th );
        } catch (Exception e) {
            parDerivTerms.put( "dR206_238r_Th__dMolPb206r_Th", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR206_238r_Th__dMolU238s =molPb206r_Th.getValue().//
                    divide(molU238s.getValue().pow( 2 ), ReduxConstants.mathContext15 ).//
                    negate();
            parDerivTerms.put( "dR206_238r_Th__dMolU238s", dR206_238r_Th__dMolU238s );
        } catch (Exception e) {
            parDerivTerms.put( "dR206_238r_Th__dMolU238s", BigDecimal.ZERO );
        }


    }
}
